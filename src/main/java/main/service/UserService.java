package main.service;

import java.util.List;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.dao.UserDAO;
import main.dto.Order;
import main.dto.User;

@Component
public class UserService {

    @Inject
    private UserDAO userDao;
    
    public boolean registerUser(int userid, String username) {
        User user = new User(userid, username);
        return this.userDao.save(user);
    }

    public User getUserById(int userid) {
       return userDao.findById(userid).orElseThrow(()->new NullPointerException("User not found by id"));
    }
    
}
