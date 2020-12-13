package main.service;

import java.util.List;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.dao.SeatDAO;
import main.dto.Seat;

@Component
public class SeatService {

    @Inject
    private SeatDAO seatDao;
    
    public List<Seat> getAllByOrderId(int orderid) {
       return this.seatDao.getAllByOrderId(orderid);
    }

    public List<Seat> getAllByTimetableId(int timetableid) {
        return this.seatDao.getAllByTimetableId(timetableid);
    }

    //get all seat bookings by order
    //get all seat bookings by a list of orders
    
}
