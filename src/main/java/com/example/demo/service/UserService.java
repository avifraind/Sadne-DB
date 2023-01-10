package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class UserService {

    public Boolean createNewUser(User user) {
        if (isUserExists(user)) {
            return false;
        }
        String sqlSelectAllPersons = String.format("SELECT COUNT(*) FROM users WHERE name='%s' AND password='%s'", user.name, user.password);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                int count = rs.getInt(1);
                if (count != 1) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return false;


//        return false;
    }

    public Boolean isUserExists(User user) {
        if (user.name == null || user.password == null) {
            return false;
        }
        String sqlSelectAllPersons = String.format("SELECT COUNT(*) FROM users WHERE name='%s' AND password='%s'", user.name, user.password);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                 int count = rs.getInt(1);
                 if (count != 1) {
                     return false;
                 }
                 return true;
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return false;
    }

    public User getUserInfo(long id) {
        if (id < 0) {
            return null;
        }

        String sqlSelectAllPersons = String.format("SELECT * FROM users WHERE user_id=%s", String.valueOf(id));
        String connectionUrl = DbService.DB_CONNECTION_URL;
        User user = null;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                String user_id = rs.getString("user_id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                user = new User(user_id, name, password);


            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return user;
    }

    public long getUserId(User user) {
        if (user == null) {
            return -1;
        }

        String sqlSelectAllPersons = String.format("SELECT user_id FROM users WHERE name='%s' AND " +
                " password='%s'", user.name, user.password);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        long id = -1;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");

            while (rs.next()) {
                id = rs.getLong("id");
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return id;
    }
}
