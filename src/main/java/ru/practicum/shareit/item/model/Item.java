package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

}
