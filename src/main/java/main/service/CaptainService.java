package main.service;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.dao.CaptainDAO;
import main.dto.Captain;

@Component
public class CaptainService {
    //get captain
    //get all captains for timetable
    //set name(optional)
    //set surname(optional)
    
    @Inject
    private CaptainDAO captainDao;
    
    public void registerCaptain(int userid, String username, String name, String surname) {
        Captain captain = new Captain(userid, username, name, surname);
        captainDao.save(captain);
    }

    public Captain getCaptainById(int userid) {
        return captainDao.findById(userid).orElseThrow(()->new NullPointerException("Captain not found by id"));
    }
    
}
