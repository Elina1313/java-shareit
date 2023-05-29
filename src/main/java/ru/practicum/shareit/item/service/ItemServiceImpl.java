package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        validate(itemDto);
        findUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id:" + userId + " not found"));

        Item item = ItemMapper.dtoToItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("request with id:" + itemDto.getRequestId() + " not found error"));
            item.setRequest(itemRequest);
        }

        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    private void findUser(long userId) {
        if (userId == 0) {
            throw new ValidationException("userId is null");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("user not found");
        }
        user.get();
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

        return ItemMapper.itemToDto(itemRepository.save(item));

    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItems(Long userId, int from, int size) {

        List<ItemDto> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size)).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        for (ItemDto itemDto : items) {
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

            List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

            itemDto.setLastBooking(lastBooking.isEmpty() ? new BookingShortDto() : BookingMapper.toBookingShortDto(lastBooking.get(0)));

            List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

            itemDto.setNextBooking(nextBooking.isEmpty() ? new BookingShortDto() : BookingMapper.toBookingShortDto(nextBooking.get(0)));
        }

        items.sort(Comparator.comparing(o -> o.getLastBooking().getStart(), Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : items) {
            if (itemDto.getLastBooking().getBookerId() == null) {
                itemDto.setLastBooking(null);
            }
            if (itemDto.getNextBooking().getBookerId() == null) {
                itemDto.setNextBooking(null);
            }
        }

        return items;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> result = new ArrayList<>();
        text = text.toLowerCase();

        List<Item> itemList = itemRepository.findAll(PageRequest.of(from, size)).toList();

        for (Item item : itemList) {
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
