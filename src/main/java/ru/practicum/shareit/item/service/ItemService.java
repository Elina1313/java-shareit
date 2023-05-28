package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> getAllItems(Long userId, int from, int size);

    List<Item> searchItems(String text, int from, int size);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);

}
