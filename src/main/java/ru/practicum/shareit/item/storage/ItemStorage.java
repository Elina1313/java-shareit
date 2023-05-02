package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item getItem(Long itemId);

    Item updateItem(Item item);

    List<Item> getAllItems(Long userId);

    List<Item> searchItems(String description);
}
