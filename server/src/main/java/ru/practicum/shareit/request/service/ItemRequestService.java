package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findALLItemRequestByUser(Long userId);

    List<ItemRequestDto> getAllItemRequestByUser(Long userId, Pageable pageable);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);
}
