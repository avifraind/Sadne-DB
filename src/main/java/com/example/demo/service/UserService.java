package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class UserService {

    public Boolean createNewUser(User user) {

        return false;
    }

    public Boolean isUserExists(User user) {
        return false;
    }

    public User getUserInfo(long id) {
        if (id < 0) {
            return null;
        }

        String sqlSelectAllPersons = String.format("SELECT * FROM Users WHERE id=%s", String.valueOf(id));
        String connectionUrl = "jdbc:mysql://localhost:3306/bar-ilan";
        User user = null;
        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "123456789");
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                String name = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String password = rs.getString("last_name");
                user = new User(name, lastName, password);

                System.out.println(String.valueOf(id));
                System.out.println(name);
                System.out.println(lastName);
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

        String sqlSelectAllPersons = String.format("SELECT id FROM Users WHERE first_name=%s AND " +
                "last_name=%s AND password=%s", user.firstName, user.lastName, user.password);
        String connectionUrl = "jdbc:mysql://localhost:3306/bar-ilan";
        long id = -1;
        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "123456789");
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
