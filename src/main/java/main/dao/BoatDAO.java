package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.connection.ConnectionManager;
import main.dto.Boat;
import main.dto.Timetable;
import main.service.CaptainService;

@Component
public class BoatDAO {

    @Inject
    private CaptainService captainService;
    
    public Optional<Boat> findById(int id) {
        Connection connection = ConnectionManager.getConnection();
        String query = "SELECT name, regular_places_count, vip_places_count, captain_id FROM Boats WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    return Optional.of(Boat.builder()
                            .name(rs.getString("name"))
                            .regularPlacesCount(rs.getInt("regular_places_count"))
                            .vipPlacesCount(rs.getInt("vip_places_count"))
                            .captain(this.captainService.getCaptainById(rs.getInt("captain_id")))
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
    
    public List<Boat> findAllByCaptainId(int captainId) {
        Connection connection = ConnectionManager.getConnection();
        List<Boat> result = new ArrayList<>();
        String query = "SELECT * FROM Boats WHERE captain_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, captainId);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    result.add(Boat.builder()
                            .name(rs.getString("name"))
                            .regularPlacesCount(rs.getInt("regular_places_count"))
                            .vipPlacesCount(rs.getInt("vip_places_count"))
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

    public boolean save(Boat boat) {
        Connection connection = ConnectionManager.getConnection();
        String query = "INSERT INTO Boats(name, regular_places_count, vip_places_count, captain_id) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, boat.getName());
            ps.setInt(2, boat.getRegularPlacesCount());
            ps.setInt(3, boat.getVipPlacesCount());
            ps.setInt(4, boat.getCaptain().getId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
