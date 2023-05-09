package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto itemToDto(Item item) {
        ItemDto result = new ItemDto();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.getAvailable());
        return result;
    }

    public static Item dtoToItem(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                0

        );
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = itemToDto(item);
            result.add(itemDto);
        }
        return result;
    }

}
