package com.example.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class Movie {
    public final String movieId;
    public final String budget;
    public final String adult;
    public final String revenue;
    public final String language;
    public final String overview;
    public final String popularity;
    public final String title;
    public final List<String> directors;
    public final List<String> actors;
    public final List<String> genres;
    public final List<String> keywords;
//    public final List<User> recommendedToMe;
}
