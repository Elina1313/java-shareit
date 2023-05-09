package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime end);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(
            User booker, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(
            User owner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Sort sort);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
            Long itemId, LocalDateTime end, BookingStatus status, Sort sort);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
            Long itemId, LocalDateTime end, BookingStatus status, Sort sort);
}
