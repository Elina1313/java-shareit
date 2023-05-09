package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingShortDto bookingShortDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    List<BookingDto> getAllByOwner(Long userId, BookingState state);

    List<BookingDto> getAllByUser(Long userId, BookingState state);

    BookingDto getById(Long itemId, Long userId);
}
