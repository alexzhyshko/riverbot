package main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.dao.OrderDAO;
import main.dto.Order;
import main.dto.Seat;
import main.dto.Timetable;
import main.dto.User;

@Component
public class OrderService {

    @Inject
    private OrderDAO orderDao;
    
    @Inject
    private UserService userService;
    
    @Inject
    private TimetableService timetableService;
    
    public List<Order> getAllByUserId(int userid) {
        return this.orderDao.findAllByUserId(userid);
    }

    public boolean createOrder(int userid, int timetableid, int regularPlaces, int vipPlaces) {
        User user = this.userService.getUserById(userid);
        Timetable timetable = this.timetableService.getTimetableById(timetableid);
        
        List<Seat> seats = new ArrayList<>();
        for(int i=0; i<regularPlaces; i++) {
            seats.add(Seat.builder().isSeatVip(false).timetable(timetable).build());
        }
        for(int i=0; i<vipPlaces; i++) {
            seats.add(Seat.builder().isSeatVip(true).timetable(timetable).build());
        }
        
        Order order = Order.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .seats(seats)
                .build();
        return this.orderDao.save(order);
    }
}
