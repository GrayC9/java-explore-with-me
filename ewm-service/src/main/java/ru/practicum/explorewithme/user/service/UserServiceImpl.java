package ru.practicum.explorewithme.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.exception.DataConflictException;
import ru.practicum.explorewithme.exception.DataValidationException;
import ru.practicum.explorewithme.exception.UserNotFoundException;
import ru.practicum.explorewithme.user.dto.UserInDto;
import ru.practicum.explorewithme.user.dto.UserMapper;
import ru.practicum.explorewithme.user.dto.UserOutDto;
import ru.practicum.explorewithme.user.dto.UserWithFollowersDto;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserOutDto> findUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        Pageable pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageRequest).getContent();
        } else {
            users = userRepository.findByIdIn(ids, pageRequest);
        }
        log.info("Выполняется запрос на поиск пользователей. Выбранные id: {}", ids);
        return UserMapper.toOutDtos(users);
    }

    @Override
    @Transactional
    public UserOutDto addUser(UserInDto inDto) {
        User user = UserMapper.toUser(inDto);
        log.info("Добавление нового пользователя");
        return UserMapper.toUserOutDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("Пользователя с заданным id не существует");
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удалён", userId);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
    }

    @Override
    @Transactional
    public UserWithFollowersDto addFollower(Long userId, Long followerId) {
        if (userId.equals(followerId)) {
            throw new DataConflictException("Пользователь не может подписаться на себя");
        }
        User user = findUserById(userId);
        User follower = findUserById(followerId);

        if (user.getFollowers().contains(follower)) {
            throw new DataConflictException("Пользователь с id " + followerId + " уже подписан на пользователя с id "
                    + userId);
        }
        user.getFollowers().add(follower);
        user = userRepository.save(user);
        log.info("Пользователь с id {} подписался на пользователя с id {}", followerId, userId);
        return UserMapper.toDtoWithFollowers(user);
    }

    @Transactional
    @Override
    public void deleteFollower(Long userId, Long followerId) {
        if (userId.equals(followerId)) {
            throw new DataConflictException("Пользователь не может быть подписан на себя");
        }
        User user = findUserById(userId);
        User follower = findUserById(followerId);

        if (!user.getFollowers().contains(follower)) {
            throw new DataConflictException("Пользователь с id " + followerId + " не подписан на пользователя с id "
                    + userId);
        }
        user.getFollowers().remove(follower);
        log.info("Пользователь с id {} отписан от пользователя с id {}", followerId, userId);
        userRepository.save(user);
    }
}
