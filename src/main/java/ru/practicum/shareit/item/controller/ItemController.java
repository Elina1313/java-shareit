package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("received a request to add Item");
        return ItemMapper.itemToDto(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable("itemId") Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("request to update Item with id: {}", itemId);
        itemDto.setId(itemId);
        return ItemMapper.itemToDto(itemService.updateItem(itemDto, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") Long itemId) {
        log.debug("request to get info for itemId: {}", itemId);
        return ItemMapper.itemToDto(itemService.getItem(itemId));
    }

    @GetMapping
    public List<ItemDto> getListOfThings(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("request to get list of things for userId: {}", userId);
        List<Item> userItems = itemService.getAllItems(userId);
        return ItemMapper.toItemDtoList(userItems);
    }

    @GetMapping("/search")
    public List<ItemDto> searchThing(@RequestParam String text) {
        log.debug("request to search a thing by description: {}", text);
        List<Item> foundItems = itemService.searchItems(text);
        return ItemMapper.toItemDtoList(foundItems);

    }
}
