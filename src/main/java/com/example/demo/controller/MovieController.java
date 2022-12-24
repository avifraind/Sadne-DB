package com.example.demo.controller;

import com.example.demo.service.MovieService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/movies")
public class MovieController {

//    @Autowired
//    MovieService movieService;
//
//    @Autowired
//    UserService userService;
//
//    //get movies in range
//    @RequestMapping(value = "/")
//    public ResponseEntity<Object> getMovie(@RequestBody Product product) {
//        return new ResponseEntity<>(movieService.getProducts(), HttpStatus.OK);
//    }
//
//    //update movie rating for user
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    public ResponseEntity<Object> updateMovieRating(@PathVariable("id") String id, @RequestBody Product product) {
//
//        movieService.updateProduct(id, product);
//        return new ResponseEntity<>("Movie is updated successfully", HttpStatus.OK);
//    }
//
//    //
//    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
//        productService.deleteProduct(id);
//        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
//    }
//    @RequestMapping(value = "/products", method = RequestMethod.POST)
//    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
//        productService.createProduct(product);
//        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
//    }
}
