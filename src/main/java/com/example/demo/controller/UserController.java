package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public ResponseEntity<?> isUserAuth(@CookieValue(name = "user-id", defaultValue = "-1") String userId) {
        if (userId.equals("-1"))
        {
            return new ResponseEntity<>("false", HttpStatus.OK);
        }
        return new ResponseEntity<>("true", HttpStatus.OK);
    }

    //need to check if there is already a user with the same name and password.
    //not to use yet
    @RequestMapping(value = "/createUser", method = RequestMethod.PUT)
    public ResponseEntity<?> createNewUser(@CookieValue(name = "user-id", defaultValue = "-1") String userId,
                                           @RequestBody User user) {
        if (!userId.equals("-1"))
        {
            return new ResponseEntity<>("User already exist", HttpStatus.BAD_REQUEST);
        }

        boolean res = userService.createNewUser(user);
        if (!res) {
            return new ResponseEntity<>("Name or password already in use", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User created", HttpStatus.OK);
    }

    //front end should check if user logged in already.
    @RequestMapping(value = "/login", method = RequestMethod.PUT)
    public ResponseEntity<?> login(@RequestBody User user) {
        long id = userService.getUserId(user);
        if (id == -1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(id))
                .path("/")
                .maxAge(60000)
                .domain("localhost")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .build();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(@CookieValue(name = "user-id", defaultValue = "-1") String userId) {
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("user-id", "-1")
                .path("/")
                .maxAge(0)
                .domain("localhost")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();
    }


}
