package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("add user request");
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull @PathVariable long userId, @RequestBody UserDto userDto) {
        log.debug("request to update user id: {}", userId);
        User user = UserMapper.dtoToUser(userDto);
        user.setId(userId);
        return UserMapper.userToDto(userService.updateUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@NotNull @PathVariable long userId) {
        log.debug("request to get user id: {}", userId);
        return UserMapper.userToDto(userService.getUser(userId));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.debug("request to get all users");
        return UserMapper.userToDtoList(userService.findAllUsers());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@NotNull @PathVariable long userId) {
        log.info("request to delete user id: {}", userId);
        userService.deleteUser(userId);
    }
}
