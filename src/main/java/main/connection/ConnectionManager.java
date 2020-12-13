package main.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import application.context.reader.PropertyReader;

public class ConnectionManager {

    private ConnectionManager() {}
    
    private static PropertyReader reader;
    
    private static String USERNAME;
    private static String PASSWORD;
    private static String PORT;
    private static String HOST;
    
    private static String CONNECTION_URL;
    
    static {
        USERNAME = reader.getProperty("database.user");
        PASSWORD = reader.getProperty("database.password");
        PORT = reader.getProperty("database.port");
        HOST = reader.getProperty("database.host");
        
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://");
        sb.append(HOST);
        sb.append(":");
        sb.append(PORT);
        sb.append("/riverbot?serverTimezone=GMT%2B2&useAffectedRows=true&useUnicode=true&serverEncoding=utf8&autoReconnect=true");
        CONNECTION_URL = sb.toString();
    }
    
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);;
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
