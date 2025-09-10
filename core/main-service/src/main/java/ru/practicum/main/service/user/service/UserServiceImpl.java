package ru.practicum.main.service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.Constants;
import ru.practicum.main.service.exception.DuplicateException;
import ru.practicum.main.service.exception.NotFoundException;
import ru.practicum.main.service.user.MapperUser;
import ru.practicum.main.service.user.UserRepository;
import ru.practicum.main.service.user.dto.NewUserRequest;
import ru.practicum.main.service.user.dto.UserDto;
import ru.practicum.main.service.user.model.User;

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
}
