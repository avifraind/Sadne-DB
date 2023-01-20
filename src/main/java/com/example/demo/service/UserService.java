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
        Boolean res = false;
        String sqlSelectAllPersons = String.format("INSERT INTO users (name, password)\n values ('%s', '%s')", user.name, user.password);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ) {
            System.out.println("Database connected!");
            stmt.executeUpdate(sqlSelectAllPersons);
            res = true;

        } catch (SQLException e) {
            System.out.println("Database not connected!");
            res = false;
        }

        return res;
    }

    public Boolean isUserExists(User user) {
        if (user.name == null || user.password == null) {
            return false;
        }
        if (isUserNameExists(user)) {
            return true;
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

    public Boolean isUserNameExists(User user) {
        if (user.name == null) {
            return true;
        }
        String sqlSelectAllPersons = String.format("SELECT COUNT(*) FROM users WHERE name='%s'", user.name);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return true;
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
                id = rs.getLong("user_id");
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return id;
    }

    public String getUserName(String userId) {
        if (userId == null) {
            return "";
        }

        String sqlSelectAllPersons = String.format("SELECT name FROM users WHERE user_id=%s", userId);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        String name = "";
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");

            while (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return name;
    }

    public long getUserIdOnlyByName(String userName) {
        if (userName == null) {
            return -1;
        }

        String sqlSelectAllPersons = String.format("SELECT user_id FROM users WHERE name='%s'" , userName);
        String connectionUrl = DbService.DB_CONNECTION_URL;
        long id = -1;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");

            while (rs.next()) {
                id = rs.getLong("user_id");
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return id;
    }
}
