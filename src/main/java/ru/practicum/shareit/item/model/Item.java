package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "description", length = 255, nullable = false)
    private String description;
    @EqualsAndHashCode.Exclude
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

}
