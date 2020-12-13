package main.service;

import java.util.List;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.dao.BoatDAO;
import main.dto.Boat;
import main.dto.Captain;

@Component
public class BoatService {

    @Inject
    private BoatDAO boatDao;
    
    @Inject
    private CaptainService captainService;
    
    public void createBoat(int userid, String boatName, int boatRegularPlaces, int boatVipPlaces) {
        Captain captain = this.captainService.getCaptainById(userid);
        Boat boat = new Boat(boatName, boatRegularPlaces, boatVipPlaces, captain);
        this.boatDao.save(boat);
    }

    public List<Boat> getAllByCaptainId(int captainId) {
        return this.boatDao.findAllByCaptainId(captainId);
    }

    public Boat getById(int boatid) {
        return this.boatDao.findById(boatid).orElseThrow(()->new NullPointerException("Boat not found by id"));
    }

    
    //get boat by id
    //get boats by captain
}
