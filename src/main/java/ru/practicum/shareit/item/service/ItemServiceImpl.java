package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item createItem(Item item) {
        userStorage.getUser(item.getOwner());
        return itemStorage.createItem(item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        return itemStorage.updateItem(item);
    }

    @Override
    public List<Item> getAllItems(long userId) {
        return itemStorage.getAllItems(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItems(text);
    }
}
