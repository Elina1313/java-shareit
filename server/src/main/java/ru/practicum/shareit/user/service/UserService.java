package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    User updateUser(User user, UserDto userDto);

    User getUser(long id);

    void deleteUser(long id);

    List<User> findAllUsers();

}
