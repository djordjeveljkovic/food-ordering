package org.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class OrderCreationRequest {
    private List<Long> itemIds;
    private String scheduleDate;

}