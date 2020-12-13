package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.connection.ConnectionManager;
import main.dto.Seat;
import main.service.TimetableService;


@Component
public class SeatDAO {

    @Inject
    private TimetableService timetableService;

    public List<Seat> getAllByOrderId(int orderid) {
        Connection connection = ConnectionManager.getConnection();
        List<Seat> result = new ArrayList<>();
        String query = "SELECT s.timetable_id as timetable_id, s.is_seat_vip as is_seat_vip FROM SeatBooking as s WHERE order_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, orderid);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    result.add(Seat.builder()
                            .isSeatVip(rs.getBoolean("is_seat_vip"))
                            .timetable(this.timetableService.getTimetableById(rs.getInt("timetable_id")))
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<Seat> getAllByTimetableId(int timetableid) {
        Connection connection = ConnectionManager.getConnection();
        List<Seat> result = new ArrayList<>();
        String query = "SELECT s.timetable_id as timetable_id, s.is_seat_vip as is_seat_vip FROM SeatBooking as s WHERE timetable_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, timetableid);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    result.add(Seat.builder()
                            .isSeatVip(rs.getBoolean("is_seat_vip"))
                            .timetable(this.timetableService.getTimetableById(rs.getInt("timetable_id")))
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
