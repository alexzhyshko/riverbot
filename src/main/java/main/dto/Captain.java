package main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Captain extends User{

    private String name;
    private String surname;
    
    
    public Captain(int id, String username, String name, String surname) {
        super(id, username);
        this.name = name;
        this.surname = surname;
    }


    public Captain(int id, String username, String phoneNumber, String name, String surname) {
        super(id, username,phoneNumber);
        this.name = name;
        this.surname = surname;
    }
    
    
    
    
    
}
