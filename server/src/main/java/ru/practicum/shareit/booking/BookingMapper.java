package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto result = new BookingDto();
        result.setId(booking.getId());
        result.setStart(booking.getStart());
        result.setEnd(booking.getEnd());
        result.setStatus(booking.getStatus());
        result.setBooker(booking.getBooker());
        result.setItem(booking.getItem());
        return result;
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        BookingShortDto result = new BookingShortDto();
        result.setId(booking.getId());
        result.setStart(booking.getStart());
        result.setEnd(booking.getEnd());
        result.setItemId(booking.getItem().getId());
        result.setBookerId(booking.getBooker().getId());
        return result;
    }

    public static Booking toBooking(BookingShortDto bookingShortDto) {
        Booking result = new Booking();
        result.setStart(bookingShortDto.getStart());
        result.setEnd(bookingShortDto.getEnd());

        return result;
    }
}
