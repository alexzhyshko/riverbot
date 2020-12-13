package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import application.context.annotation.Component;
import application.context.annotation.Inject;
import main.connection.ConnectionManager;
import main.dto.Order;
import main.dto.Seat;
import main.service.SeatService;

@Component
public class OrderDAO {

    @Inject
    private SeatService seatService;

    public boolean save(Order order) {
        String saveOrderQuery = "INSERT INTO Orders(user_id, timestamp) VALUES(?,?)";
        String getAddedOrderId = "SELECT LAST_INSERT_ID();";
        String saveSeatQuery = "INSERT INTO Seatbooking(timetable_id, order_id, is_seat_vip) VALUES(?,?,?)";
        try (Connection connection = ConnectionManager.getConnection()) {
            
            
            boolean orderInserted = insertOrder(connection, saveOrderQuery, order);
            int orderId = getInsertedOrderId(connection, getAddedOrderId);
            boolean orderIdValid = orderId>0;
            order.setId(orderId);
            boolean seatsInserted = insertSeats(connection, saveSeatQuery, order);
            
            if(orderInserted && orderIdValid && seatsInserted) {
                connection.commit();
                return true;
            }
            connection.rollback();
            return false;
           
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertSeats(Connection connection, String saveSeatQuery, Order order) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(saveSeatQuery)){
            ps.setInt(1, order.getSeats().get(0).getTimetable().getId());
            ps.setInt(2,  order.getId());
            for(Seat seat : order.getSeats()) {
                ps.setBoolean(3, seat.isSeatVip());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getInsertedOrderId(Connection connection, String getAddedOrderId) throws SQLException {
        int orderId = -1;
        try(PreparedStatement ps = connection.prepareStatement(getAddedOrderId)){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) {
                    orderId = rs.getInt(1);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return orderId;
    }

    private boolean insertOrder(Connection connection, String saveOrderQuery, Order order) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(saveOrderQuery)){
            ps.setInt(1, order.getUser().getId());
            ps.setTimestamp(2, Timestamp.valueOf(order.getTimestamp()));
            ps.executeUpdate();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> findAllByUserId(int userid) {
        Connection connection = ConnectionManager.getConnection();
        List<Order> result = new ArrayList<>();
        String query = "SELECT o.id as order_id, o.timestamp as order_timestamp FROM Orders as o WHERE o.user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userid);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Order order = Order.builder()
                            .timestamp(rs.getTimestamp("order_timestamp").toLocalDateTime())
                            .seats(this.seatService.getAllByOrderId(rs.getInt("order_id")))
                            .build();
                    result.add(order);
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
