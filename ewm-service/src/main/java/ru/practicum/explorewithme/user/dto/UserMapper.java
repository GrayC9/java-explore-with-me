package ru.practicum.explorewithme.user.dto;

import ru.practicum.explorewithme.user.model.User;

public class UserMapper {

    public static User toUser(UserInDto inDto) {
        User user = new User();
        user.setEmail(inDto.getEmail());
        user.setId(user.getId());
        return user;
    }

    public static UserOutDto toUserOutDto(User user) {
        return UserOutDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
