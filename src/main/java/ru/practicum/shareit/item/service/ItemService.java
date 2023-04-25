package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item getItem(Long itemId);

    Item updateItem(Item item);

    List<Item> getAllItems(long userId);

    List<Item> searchItems(String text);
}
