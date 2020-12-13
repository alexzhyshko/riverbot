package main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private int id;
    private String username;
    private String phoneNumber;
    
    
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }
    
    
    
}
