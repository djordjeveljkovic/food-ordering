package org.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import org.example.usermanagement.entity.Order;
import org.example.usermanagement.entity.Dish;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private String status;
    private UserDTO createdBy;
    private List<DishDTO> items;
    private String scheduledFor;
    private String createdDate;

public OrderDTO() {}
public OrderDTO(Order order) {
    this.id = order.getId();
    this.status = order.getStatus().name(); // fix enum to String
    this.createdBy = new UserDTO(order.getCreatedBy());
    this.items = order.getItems().stream()
                      .map(DishDTO::new)
                      .toList(); // make sure DishDTO has constructor accepting Dish
    this.scheduledFor = order.getScheduledFor().toString(); // format if needed
    this.createdDate = order.getCreatedDate().toString();
}
    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                ", items=" + items +
                ", scheduledFor='" + scheduledFor + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}

