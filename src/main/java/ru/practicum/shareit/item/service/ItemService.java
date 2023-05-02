package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(ItemDto itemDto, Long userId);

    Item getItem(Long itemId);

    Item updateItem(ItemDto itemDto, Long userId);

    List<Item> getAllItems(Long userId);

    List<Item> searchItems(String text);
}
