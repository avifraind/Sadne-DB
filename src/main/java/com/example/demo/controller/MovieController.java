package com.example.demo.controller;

import com.example.demo.service.MovieService;
import com.example.demo.service.UserService;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    MovieService movieService;

    @Autowired
    UserService userService;

    //get movies in range
    @RequestMapping(value = "/")
    public ResponseEntity<Object> getMovies(@RequestBody MovieService.MoviesReqData moviesRange) {
        return new ResponseEntity<>(movieService.getMovies(moviesRange), HttpStatus.OK);
    }

    @RequestMapping(value = "/all")
    public ResponseEntity<Object> getMovies() {
        return new ResponseEntity<>(movieService.getAllMovies(-1), HttpStatus.OK);
    }

    //to be continued
    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public ResponseEntity<Object> recommend(@RequestBody MovieService.MoviesRecommendData moviesRecommendData) {
        return new ResponseEntity<>(movieService.getAllMovies(-1), HttpStatus.OK);
    }



}
