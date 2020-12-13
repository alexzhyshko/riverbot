package main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Boat {

    private int id;
    private String name;
    private int regularPlacesCount;
    private int vipPlacesCount;
    private Captain captain;
    
    
    public Boat(String name, int regularPlacesCount, int vipPlacesCount, Captain captain) {
        this.name = name;
        this.regularPlacesCount = regularPlacesCount;
        this.vipPlacesCount = vipPlacesCount;
        this.captain = captain;
    }
    
    
    
}
