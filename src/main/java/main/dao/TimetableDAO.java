package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.connection.ConnectionManager;
import main.dto.Order;
import main.dto.Seat;
import main.dto.Timetable;
import main.service.BoatService;

@Component
public class TimetableDAO {

    @Inject
    private BoatService boatService;
    
    public Optional<Timetable> findById(int id) {
        Connection connection = ConnectionManager.getConnection();
        
        String query = "SELECT boat_id, departure_datetime, duration, departure_place, destination_place FROM Timetables WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    return Optional.of(Timetable.builder()
                            .id(id)
                            .departureDatetime(rs.getTimestamp("departure_datetime").toLocalDateTime())
                            .duration(Duration.ofMinutes(rs.getInt("duration")))
                            .departurePlace(rs.getString("departure_place"))
                            .destinationPlace(rs.getString("destination_place"))
                            .boat(this.boatService.getById(rs.getInt("boat_id")))
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public List<Timetable> findAllAfterDatetime(LocalDateTime now) {
        Connection connection = ConnectionManager.getConnection();
        List<Timetable> result = new ArrayList<>();
        String query = "SELECT id, boat_id, departure_datetime, duration, departure_place, destination_place FROM Timetables WHERE departure_datetime>?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(now));
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    result.add(Timetable.builder()
                            .id(rs.getInt("id"))
                            .departureDatetime(rs.getTimestamp("departure_datetime").toLocalDateTime())
                            .duration(Duration.ofMinutes(rs.getInt("duration")))
                            .departurePlace(rs.getString("departure_place"))
                            .destinationPlace(rs.getString("destination_place"))
                            .boat(this.boatService.getById(rs.getInt("boat_id")))
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
