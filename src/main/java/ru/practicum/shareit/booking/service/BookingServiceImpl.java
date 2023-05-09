package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingDto create(BookingShortDto bookingShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id:" + userId + " not found"));
        Item item = itemRepository.findById(bookingShortDto.getItemId())
                .orElseThrow(() -> new NotFoundException("item with id:" + bookingShortDto.getItemId() + " not found"));

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("user:" + userId + " can't booking");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("item:" + item.getId() + " is not available");
        }
        Booking booking = BookingMapper.toBooking(bookingShortDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new BadRequestException("wrong booking start or end date");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id:" + bookingId + " not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("user id:" + userId + " is not an owner of item:" + booking.getItem().getId());
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Booking is already approved or rejected");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id:" + userId + " not found error"));
        List<Booking> bookingsList = new ArrayList<>();
        switch (state) {
            case ALL:
                bookingsList.addAll(bookingRepository.findAllByItemOwner(user, sort));
                break;

            case FUTURE:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort));
                break;

            case CURRENT:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;

            case PAST:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user,
                        LocalDateTime.now(), sort));
                break;

            case WAITING:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, sort));
                break;

            case REJECTED:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, sort));
                break;

            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingsList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByUser(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Booking not found - " +
                        "not found user with id: " + userId));
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case ALL:
                bookingDtoList.addAll(bookingRepository.findAllByBooker(user, sort));
                break;

            case REJECTED:
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, sort));
                break;

            case CURRENT:
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;

            case PAST:
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now(), sort));
                break;

            case WAITING:
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, sort));
                break;

            case FUTURE:
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort));
                break;

            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("No booking with id:" + bookingId));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("access denied");
        }

        return BookingMapper.toBookingDto(booking);
    }
}
