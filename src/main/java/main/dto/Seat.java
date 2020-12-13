package main.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Seat {

    private int id;
    private Timetable timetable;
    private boolean isSeatVip;
    
}
