package main.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Order {

    private int id;
    private User user;
    private List<Seat> seats;
    private LocalDateTime timestamp;
    
}
