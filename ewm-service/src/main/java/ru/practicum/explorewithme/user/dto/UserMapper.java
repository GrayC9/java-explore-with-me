package ru.practicum.explorewithme.user.dto;

import ru.practicum.explorewithme.user.model.User;

import java.util.ArrayList;
import java.util.List;

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

    public static List<UserOutDto> toOutDtos(List<User> users) {
        List<UserOutDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toUserOutDto(user));
        }
        return dtos;
    }
}
