package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        validate(itemDto);
        Item item = ItemMapper.dtoToItem(itemDto, userService.getUser(ownerId));
        User owner = findUser(ownerId);
        item.setOwner(owner);

        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    private User findUser(long userId) {
        if (userId == 0) {
            throw new ValidationException("userId is null");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("user not found");
        }
        return user.get();
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("parameter name is empty");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("parameter description is empty");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("parameter available is empty");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItem(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item with id:" + itemId + " not found error"));
        ItemDto itemDto = ItemMapper.itemToDto(item);

        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }

        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

        itemDto.setLastBooking(lastBooking.isEmpty() ? null : BookingMapper.toBookingShortDto(lastBooking.get(0)));

        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        itemDto.setNextBooking(nextBooking.isEmpty() ? null : BookingMapper.toBookingShortDto(nextBooking.get(0)));

        if (itemDto.getLastBooking() == null && itemDto.getNextBooking() != null) {
            itemDto.setLastBooking(itemDto.getNextBooking());
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item with id:" + itemId + " not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new WrongUserException("wrong user:" + item.getOwner().getId() + " is not an owner of " + itemDto);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.itemToDto(item);

    }

    @Transactional
    @Override
    public List<ItemDto> getAllItems(Long userId) {

        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemDto> itemDtoList = items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        for (ItemDto itemDto : itemDtoList) {
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

            List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

            itemDto.setLastBooking(lastBooking.isEmpty() ? new BookingShortDto() : BookingMapper.toBookingShortDto(lastBooking.get(0)));

            List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

            itemDto.setNextBooking(nextBooking.isEmpty() ? new BookingShortDto() : BookingMapper.toBookingShortDto(nextBooking.get(0)));
        }

        itemDtoList.sort(Comparator.comparing(o -> o.getLastBooking().getStart(), Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getLastBooking().getBookerId() == null) {
                itemDto.setLastBooking(null);
            }
            if (itemDto.getNextBooking().getBookerId() == null) {
                itemDto.setNextBooking(null);
            }
        }

        return itemDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> result = new ArrayList<>();
        text = text.toLowerCase();

        for (Item item : itemRepository.findAll()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();

            if (item.getAvailable().equals(true) && (name.contains(text) || description.contains(text))) {
                result.add(item);
            }
        }

        return result;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id:" + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item with id:" + itemId + " not found"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Wrong item for comment");
        }
        Comment comment = CommentMapper.toCommentModel(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }
}
