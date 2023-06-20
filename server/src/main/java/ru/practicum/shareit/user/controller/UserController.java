package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.debug("add user request");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("request to update user id: {}", userId);
        User user = UserMapper.dtoToUser(userDto);
        user.setId(userId);
        return UserMapper.userToDto(userService.updateUser(user, userDto));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("request to get user id: {}", userId);
        return UserMapper.userToDto(userService.getUser(userId));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.debug("request to get all users");
        return UserMapper.userToDtoList(userService.findAllUsers());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("request to delete user id: {}", userId);
        userService.deleteUser(userId);
    }
}
