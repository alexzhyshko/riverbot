package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import application.context.annotation.Component;
import main.connection.ConnectionManager;
import main.dto.User;

@Component
public class UserDAO {

    public Optional<User> findById(int id) {
        Connection connection = ConnectionManager.getConnection();
        String query = "SELECT id, username, phone_number FROM `Users` WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    return Optional.of(new User(id, rs.getString("username"), rs.getString("phone_number")));
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

    public boolean save(User user) {
        Connection connection = ConnectionManager.getConnection();
        String query = "INSERT INTO `Users`(id, username) VALUES(?,?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user.getId());
            ps.setString(2, user.getUsername());
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
