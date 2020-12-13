package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import application.context.annotation.Component;
import main.connection.ConnectionManager;
import main.dto.Captain;

@Component
public class CaptainDAO {

    public Optional<Captain> findById(int id) {
        Connection connection = ConnectionManager.getConnection();
        String query = "SELECT id, username, phone_number, name, surname FROM Captains WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    return Optional.of(new Captain(rs.getInt("id"), rs.getString("username"), rs.getString("phone_number"), rs.getString("name"), rs.getString("surname")));
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

    public boolean save(Captain captain) {
        Connection connection = ConnectionManager.getConnection();
        String query = "INSERT INTO Captains(id, username, name, surname) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, captain.getId());
            ps.setString(2, captain.getUsername());
            ps.setString(3, captain.getName());
            ps.setString(4, captain.getSurname());
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
