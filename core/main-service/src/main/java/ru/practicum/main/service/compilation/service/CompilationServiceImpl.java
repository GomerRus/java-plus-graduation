package ru.practicum.main.service.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.compilation.CompilationRepository;
import ru.practicum.main.service.compilation.MapperCompilation;
import ru.practicum.main.service.compilation.dto.CompilationDto;
import ru.practicum.main.service.compilation.dto.NewCompilationDto;
import ru.practicum.main.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.service.compilation.model.Compilation;
import ru.practicum.main.service.compilation.model.QCompilation;
import ru.practicum.main.service.event.EventRepository;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.event.model.QEvent;
import ru.practicum.main.service.exception.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final MapperCompilation mapperCompilation;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(GetCompilationsParam param) {
        log.debug("Попытка получить подборку событий с параметрами {}", param);
        QCompilation qCompilation = QCompilation.compilation;
        BooleanExpression pinnedExpression = qCompilation.pinned.eq(param.getPinned());

        List<Compilation> compilations =
                compilationRepository.findAll(pinnedExpression, param.getPageable()).getContent();
        log.debug("Успешно получено {} подборок с параметрами {}", compilations.size(), param);
        return compilations.stream()
                .map(mapperCompilation::toCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.trace("Попытка получить подборку по eventId = {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с eventId = " + compId + " не найдена"));
        log.trace("Подборка с eventId = {} найдена", compId);
        return mapperCompilation.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.trace("Попытка создать новую подборку");
        Compilation compilation = mapperCompilation.toCompilation(newCompilationDto);
        Set<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.trace("Успешно сохранена подборка, eventId = {}", compilation.getId());
        return mapperCompilation.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        log.trace("Попытка обновить подборку");
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с eventId = " + compId + " не найдена"));

        if (updateCompilationRequest.hasEvents()) {
            log.trace("Необходимо обновить events");
            QEvent qEvent = QEvent.event;
            BooleanExpression idsExpression = qEvent.id.in(updateCompilationRequest.getEvents());
            Iterable<Event> eventsInDb = eventRepository.findAll(idsExpression);

            long sizeEventsInDb = Stream.of(eventsInDb).count();
            log.debug("Количество events в запросе = {} найдено в БД = {}",
                    updateCompilationRequest.getEvents().size(),
                    sizeEventsInDb);

            if (updateCompilationRequest.getEvents().size() != sizeEventsInDb) {
                log.trace("Одно или более событий включенных в подборку не существует");
                throw new NotFoundException("Одно или более событий включенных в подборку не существует");
            }
            log.trace("Количество events совпало с количеством в базе, обновляется база");

            compilation.getEvents().clear();
            eventsInDb.forEach(compilation.getEvents()::add);
        }

        if (updateCompilationRequest.hasTitle()
            && !compilation.getTitle().equals(updateCompilationRequest.getTitle())) {
            log.trace("Необходимо обновить title");
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.hasPinned()
            && !compilation.getPinned().equals(updateCompilationRequest.getPinned())) {
            log.trace("Необходимо обновить pinned");
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        log.trace("Compilation успешно обновлен");
        return mapperCompilation.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        log.trace("Попытка удалить подборку с eventId = {}", compId);
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с eventId = " + compId + " не найдена"));

        compilationRepository.deleteById(compId);
        log.trace("Подборка с eventId = {} успешно удалена", compId);
    }
}
