package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }

    public String hello() {
        String sqlSelectAllPersons = "SELECT * FROM Users";
        String connectionUrl = "jdbc:mysql://localhost:3306/bar-ilan";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "123456789");
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("frist_name");
                String lastName = rs.getString("last_name");
                System.out.println(String.valueOf(id));
                System.out.println(name);
                System.out.println(lastName);


            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        return "hello worls";
    }

}
