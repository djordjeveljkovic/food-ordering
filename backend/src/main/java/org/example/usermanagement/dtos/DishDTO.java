package org.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.usermanagement.entity.Dish;

@Getter
@Setter
public class DishDTO {
    private Long id;
    private String name;

    public DishDTO(){}
    public DishDTO(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
    }
}
