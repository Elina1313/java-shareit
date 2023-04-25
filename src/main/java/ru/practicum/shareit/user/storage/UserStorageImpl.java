package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {
    private int generatorId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();

    @Override
    public User createUser(User user) {
        validate(user);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUpdate(user);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public User getUser(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("user with id:" + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public void deleteUser(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("user with id:" + id + " not found error");
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void validate(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("Error, duplicate email");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Error, email no empty and contains @");
        }

        getNextId(user);

    }

    private void getNextId(User user) {
        if (user.getId() == 0) {
            user.setId(++generatorId);
        }
    }

    private void validateUpdate(User user) {
        if (user.getName() == null) {
            user.setName(getUser(user.getId()).getName());
        }
        if (user.getEmail() != null) {
            emails.remove(getUser(user.getId()).getEmail());
        } else {
            user.setEmail(getUser(user.getId()).getEmail());
            emails.remove(user.getEmail());
        }
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("Duplicate email");
        }
    }

}
