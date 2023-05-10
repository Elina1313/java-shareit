package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.NotFoundException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Email can't be empty and must contains @");
        }
        User user = userRepository.save(UserMapper.dtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @Transactional
    @Override
    public User updateUser(User user, UserDto userDto) {
        User firstUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("user " + user.getId() + " not found"));

        if (userDto.getName() != null) {
            firstUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail()
                .equals(firstUser.getEmail())) {
            firstUser.setEmail(userDto.getEmail());
        }

        return firstUser;
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user " + id + " not found")
        );
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
