package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("received a request to add Item");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @NotNull @PathVariable Long itemId,
                              @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("request to update Item with id: {}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@NotNull @PathVariable Long itemId,
                           @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("request to get info for itemId: {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getListOfThings(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("request to get list of things for userId: {}", userId);
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchThing(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                     @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("request to search a thing by description: {}", text);
        List<Item> foundItems = itemService.searchItems(text, PageRequest.of(from, size));
        return ItemMapper.toItemDtoList(foundItems);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(itemId, userId, commentDto);
    }

}
