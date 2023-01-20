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
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Object> getMovies(@RequestBody MovieService.MoviesReqData moviesRange,
                                            @CookieValue(name = "user-id", defaultValue = "-1") String userId) {
        if (userId.equals("-1"))
        {
            return new ResponseEntity<>("Please login", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(movieService.getMovies(moviesRange, userId), HttpStatus.OK);
    }


    //to be continued
    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public ResponseEntity<?> recommend(@RequestBody MovieService.MoviesRecommendData moviesRecommendData,
                                            @CookieValue(name = "user-id", defaultValue = "-1") String userId) {
        if (userId.equals("-1"))
        {
            return new ResponseEntity<>("Please login", HttpStatus.BAD_REQUEST);
        }
        Boolean res = movieService.recommendMovie(moviesRecommendData, userId);
        if (!res) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }



}
