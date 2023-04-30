package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    @Override
    public Item createItem(ItemDto itemDto, Long ownerId) {
        itemDto.setOwnerId(ownerId);
        return itemStorage.createItem(itemMapper.dtoToItem(itemDto, ownerId));
    }

    @Override
    public Item getItem(Long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long userId) {
        return itemStorage.updateItem(itemMapper.dtoToItem(itemDto, userId));
    }

    @Override
    public List<Item> getAllItems(Long userId) {
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
