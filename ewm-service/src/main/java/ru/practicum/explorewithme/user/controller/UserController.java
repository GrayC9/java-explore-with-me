package ru.practicum.explorewithme.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.user.dto.UserInDto;
import ru.practicum.explorewithme.user.dto.UserOutDto;
import ru.practicum.explorewithme.user.dto.UserWithFollowersDto;
import ru.practicum.explorewithme.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //Admin endpoints
    @GetMapping("/admin/users")
    public List<UserOutDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                      @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                      @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.findUsers(ids, from, size);
    }

    @DeleteMapping(value = "/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@NotNull @PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping(value = "/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserOutDto addUser(@Valid @RequestBody UserInDto inDto) {
        return userService.addUser(inDto);
    }

    //Private endpoints
    @PostMapping(value = "/users/{userId}/followers/{followerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserWithFollowersDto addFollower(@PathVariable Long userId, @PathVariable Long followerId) {
        return userService.addFollower(userId, followerId);
    }

    @DeleteMapping(value = "/users/{userId}/followers/{followerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFollower(@PathVariable Long userId, @PathVariable Long followerId) {
        userService.deleteFollower(userId, followerId);
    }
}
