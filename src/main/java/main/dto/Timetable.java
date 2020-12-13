package main.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Timetable {

    private int id;
    private Boat boat;
    private List<Seat> bookedSeats;
    private LocalDateTime departureDatetime;
    private Duration duration;
    private String departurePlace;
    private String destinationPlace;
    
}
