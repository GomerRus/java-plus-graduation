package ru.practicum.main.service.user.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.user.dto.NewUserRequest;
import ru.practicum.main.service.user.dto.UserDto;
import ru.practicum.main.service.user.service.GetUserParam;
import ru.practicum.main.service.user.service.UserService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@Nullable @RequestParam List<Long> ids,
                                  @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                  @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET /admin/users с параметрами ids = {}, from = {}, size = {}", ids, from, size);
        Pageable page = PageRequest.of(from, size);
        GetUserParam param = GetUserParam.builder()
                .ids(ids)
                .pageable(page)
                .build();
        List<UserDto> users = userService.getUsers(param);

        log.info("Успешно выполнено получение пользователей с параметрами ids = {}, from = {}, size = {}. Найдено : {}",
                ids,
                from,
                size,
                users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Получен POST /admin/users");
        UserDto user = userService.createUser(newUserRequest);
        log.info("Пользователь успешно сохранён, eventId = {}", user.getId());
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Min(1) @PathVariable Long userId) {
        log.info("Получен DELETE /admin/users/{}", userId);
        userService.deleteUser(userId);
        log.info("Пользователь успешно удалён, eventId = {}", userId);
    }
}
