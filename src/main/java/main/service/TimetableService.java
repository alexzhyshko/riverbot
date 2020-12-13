package main.service;

import java.time.LocalDateTime;
import java.util.List;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.dao.TimetableDAO;
import main.dto.Seat;
import main.dto.Timetable;

@Component
public class TimetableService {

    @Inject
    private TimetableDAO timetableDao;
    
    @Inject
    private SeatService seatService;
    
    public Timetable getTimetableById(int timetableid) {
        return this.timetableDao.findById(timetableid).orElseThrow(()->new NullPointerException("No timetanle found by id"));
    }

    public List<Timetable> getAllAvailable(LocalDateTime now) {
        return this.timetableDao.findAllAfterDatetime(now);
    }

    public int getAvailableRegularPlacesCountById(int timetableid) {
       Timetable timetable = this.timetableDao.findById(timetableid).orElseThrow(()->new NullPointerException("Could not find timetable by id"));
       List<Seat> bookedSeats = this.seatService.getAllByTimetableId(timetableid);
       timetable.setBookedSeats(bookedSeats);
       int totalRegularPlacesCount = timetable.getBoat().getRegularPlacesCount();
       int bookedRegularPlacesCount = (int) timetable.getBookedSeats().stream().filter(seat->!seat.isSeatVip()).count();
       return totalRegularPlacesCount-bookedRegularPlacesCount;
    }
    
    public int getAvailableVipPlacesCountById(int timetableid) {
        Timetable timetable = this.timetableDao.findById(timetableid).orElseThrow(()->new NullPointerException("Could not find timetable by id"));
        List<Seat> bookedSeats = this.seatService.getAllByTimetableId(timetableid);
        timetable.setBookedSeats(bookedSeats);
        int totalVipPlacesCount = timetable.getBoat().getVipPlacesCount();
        int bookedVipPlacesCount = (int) timetable.getBookedSeats().stream().filter(seat->seat.isSeatVip()).count();
        return totalVipPlacesCount-bookedVipPlacesCount;
     }
    
}
