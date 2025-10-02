package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.user.mapper.MapperUser;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import ru.yandex.practicum.interaction.utility.Constants;
import ru.yandex.practicum.interaction.exception.DuplicateException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.interaction.dto.user.NewUserRequest;
import ru.yandex.practicum.interaction.dto.user.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUser mapperUser;

    @Override
    public List<UserDto> getUsers(GetUserParam getUserParam) {
        Page<User> users = userRepository.findUsersByIds(getUserParam.getIds(), getUserParam.getPageable());
        return users.map(mapperUser::toUserDto).getContent();
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.findByEmailIgnoreCase(newUserRequest.getEmail()).isPresent()) {
            throw new DuplicateException(Constants.DUPLICATE_USER);
        }

        User user = mapperUser.toUser(newUserRequest);

        user = userRepository.save(user);

        return mapperUser.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(Constants.USER_NOT_FOUND);
        }

        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        return mapperUser.toUserDto(userFromDb);
    }
}
