package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("add item request");
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findALLItemRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("get all item requests of user and item requests answers");
        return itemRequestService.findALLItemRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestByUser(@RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("incorrect parameters");
        }
        return itemRequestService.getAllItemRequestByUser(userId, PageRequest.of(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

}
