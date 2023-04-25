package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private long generateId;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {

        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null) {
            throw new ValidationException("available name or description");
        }
        if (item.getName().isBlank() || item.getDescription().isBlank()) {
            throw new ValidationException("empty name or description");
        }

        Long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;//
    }

    @Override
    public Item getItem(Long itemId) {
        validateItemInStorage(itemId);
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {

        validateItemInStorage(item.getId());
        Item newItem = items.get(item.getId());

        if (newItem.getOwner() != (item.getOwner())) {
            throw new NotFoundException("wrong user:" + item.getOwner() + " is not an owner of " + item);
        }
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        return newItem;
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return items.values()
                .stream()
                .filter(x -> x.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        }
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(x -> x.getDescription().toLowerCase().contains(query.toLowerCase())
                        || x.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return ++generateId;
    }

    private void validateItemInStorage(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("item with id:" + itemId + " not found");
        }
    }

}
