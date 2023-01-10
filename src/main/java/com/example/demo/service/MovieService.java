package com.example.demo.service;

import com.example.demo.model.Movie;
import com.example.demo.model.filters.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    public List<Movie> getAllMovies(int numMovies) {
        List<Movie> movies = new ArrayList<>();
        String sqlSelectAllMovies = String.format("WITH directors AS (\n" +
                "\tSELECT\n" +
                "\t\tMovie_ID,\n" +
                "\t\tGROUP_CONCAT(Director SEPARATOR ',') AS directors_concat\n" +
                "\tFROM\n" +
                "\t\tDirectors\n" +
                "\tGROUP BY\n" +
                "\t\tMovie_ID\n" +
                "),\n" +
                "actors AS (\n" +
                "\tSELECT\n" +
                "\t\tMovie_ID,\n" +
                "\t\tGROUP_CONCAT(Actor_Name SEPARATOR ',') AS actor_concat\n" +
                "\tFROM\n" +
                "\t\tActors\n" +
                "\tGROUP BY\n" +
                "\t\tMovie_ID\n" +
                ")\n" +
                "\n" +
                "\n" +
                "SELECT\n" +
                "\t*\n" +
                "FROM\n" +
                "\tMovies_Data md\n" +
                "\tLEFT JOIN directors ON md.Movie_ID = directors.Movie_ID\n" +
                "\tLEFT JOIN actors ON md.Movie_ID = actors.Movie_ID;");
        String connectionUrl = "jdbc:mysql://localhost:3306/movies";
        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "Raphael13@");
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllMovies);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                String movieId = rs.getString("Movie_ID");

                String budget = rs.getString("budget");
                String adult = rs.getString("Adult");
                String revenue = rs.getString("Revenue");
                String language = rs.getString("Language");
                String overview = rs.getString("Overview");
                String popularity = rs.getString("Popularity");
                String directors_concat = rs.getString("directors_concat");
                List<String> directors;
                if (directors_concat != null) {
                    directors = List.of(directors_concat.split(","));

                } else {
                    directors = new ArrayList<>();
                }
                String actor_concat = rs.getString("actor_concat");
                List<String> actors;
                if (actor_concat != null) {
                    actors = List.of(actor_concat.split(","));

                } else {
                    actors = new ArrayList<>();
                }
                String title = rs.getString("Title");
                Movie movie = new Movie(movieId, budget, adult, revenue, language, overview, popularity,title,
                        directors, actors, null, null );
                movies.add(movie);

//                System.out.println(String.valueOf(id));
//                System.out.println(name);
//                System.out.println(lastName);
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        System.out.println(movies.size());
        if (numMovies > -1) {
            return movies.stream().limit(numMovies).collect(Collectors.toList());
        }

        return movies;
    }
    public List<Movie> getMovies(MovieService.MoviesReqData moviesRange) {
        if (moviesRange == null) {
            return new ArrayList<>();
        }
        if (moviesRange.filters == null) {
            return getAllMovies(Integer.parseInt(moviesRange.numMovies));
        }
        String directorsQ = buildSqlQueryDirectors(moviesRange.filters.directorFilter);
        String actorsQ = buildSqlQueryActors(moviesRange.filters.actorFilter);
        String adultQ = buildSqlQueryAdult(moviesRange.filters.adultFilter);
        String revenueQ = buildSqlQueryRevenue(moviesRange.filters.revenueFilter);
        String budgetQ = buildSqlQueryBudget(moviesRange.filters.budgetFilter);
        String languageQ = buildSqlQueryLanguage(moviesRange.filters.languageFilter);
//        String popularityQ = buildSqlQuery(moviesRange.filters.directorFilter);
        String titleQ = buildSqlQueryTitle(moviesRange.filters.titleFilter);
        String genreQ = buildSqlQueryGenres(moviesRange.filters.genreFilter);
//        buildSqlQueryGenres
        List<Movie> movies = new ArrayList<>();
        String sqlSelectAllMovies = String.format("WITH directorss AS (\n" +
                "\tSELECT\n" +
                "\t\tmovie_id,\n" +
                "\t\tGROUP_CONCAT(director SEPARATOR ',') AS directors_concat\n" +
                "\tFROM\n" +
                "\t\tdirectors\n" +
                "\tWHERE 1=1 "+
                        directorsQ +
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                "),\n" +
                "actorss AS (\n" +
                "\tSELECT\n" +
                "\t\tmovie_id,\n" +
                "\t\tGROUP_CONCAT(actor_name SEPARATOR ',') AS actor_concat\n" +
                "\tFROM\n" +
                "\t\tactors\n" +
                "\tWHERE 1=1 "+
                        actorsQ +
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                "),\n" +
                "\n" +
                "genress AS (\n" +
                "\tSELECT\n" +
                "\t\tmovie_id,\n" +
                "\t\tGROUP_CONCAT(genre SEPARATOR ',') AS genre_concat\n" +
                "\tFROM\n" +
                "\t\tgenres\n" +
                "\tWHERE 1=1 "+
                genreQ +
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                ")\n" +
                "\n" +
                "SELECT\n" +
                "\t*\n" +
                "FROM\n" +
                "\tmovies_data md\n" +
                "\tINNER JOIN directorss ON md.movie_id = directorss.movie_id\n" +
                "\tINNER JOIN actorss ON md.movie_id = actorss.movie_id\n" +
                "\tINNER JOIN genress ON md.movie_id = genress.movie_id\n" +
                "\tWHERE 1=1 " +
                        adultQ+
                        revenueQ+
                        budgetQ+
                        languageQ+
                        titleQ+
                "\n\t ORDER BY md.movie_id;");

        System.out.println("QUERY: " + sqlSelectAllMovies);

        String connectionUrl = DbService.DB_CONNECTION_URL;
        try (Connection conn = DriverManager.getConnection(connectionUrl, DbService.DB_USER, DbService.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllMovies);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Database connected!");


            while (rs.next()) {
                String movieId = rs.getString("movie_id");

                String budget = rs.getString("budget");
                String adult = rs.getString("adult");
                String title = rs.getString("title");
                String language = rs.getString("lang");
                String overview = rs.getString("overview");
                String popularity = rs.getString("popularity");
                String revenue = rs.getString("revenue");
                String directors_concat = rs.getString("directors_concat");

                List<String> directors;
                if (directors_concat != null) {
                    directors = List.of(directors_concat.split(","));

                } else {
                    directors = new ArrayList<>();
                }

                String actor_concat = rs.getString("actor_concat");
                List<String> actors;
                if (actor_concat != null) {
                    actors = List.of(actor_concat.split(","));

                } else {
                    actors = new ArrayList<>();
                }

                String genre_concat = rs.getString("genre_concat");
                List<String> genres;
                if (genre_concat != null) {
                    genres = List.of(genre_concat.split(","));

                } else {
                    genres = new ArrayList<>();
                }
                Movie movie = new Movie(movieId, budget, adult, revenue, language, overview, popularity,title,
                        directors, actors, genres, null );
                movies.add(movie);

//                System.out.println(String.valueOf(id));
//                System.out.println(name);
//                System.out.println(lastName);
            }
        } catch (SQLException e) {
            System.out.println("Database not connected!");
        }
        System.out.println(movies.size());
        int numMovies = Integer.parseInt(moviesRange.numMovies);
        if (numMovies > -1) {
            return movies.stream().limit(numMovies).collect(Collectors.toList());
        }

        return movies;
    }

    private String buildSqlQueryBudget(BudgetFilter budgetFilter) {
        if (!budgetFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND budget BETWEEN %s AND %s\n", budgetFilter.from, budgetFilter.to);

        return query;
    }
//TOO MANY WHERES
    private String buildSqlQueryAdult(AdultFilter adultFilter) {
        if (!adultFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND adult = '%s'\n", adultFilter.isAdult ? "TRUE" : "FALSE");

        return query;
    }

    private String buildSqlQueryRevenue(RevenueFilter revenueFilter) {
        if (!revenueFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND revenue BETWEEN %s AND %S\n", revenueFilter.from, revenueFilter.to);

        return query;
    }

    private String buildSqlQueryLanguage(LanguageFilter languageFilter) {
        if (!languageFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND lang = '%s'\n", languageFilter.language);

        return query;
    }

    private String buildSqlQueryDirectors(DirectorFilter directorFilter) {
        if (!directorFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND movie_id IN (" +
                "\tSELECT\n" +
                "\t\tmovie_id\n" +
                "\tFROM\n" +
                "\t\tdirectors\n" +
                "\tWHERE director ='%s' "+
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                ") \n", directorFilter.director);
        return query;
    }

    private String buildSqlQueryActors(ActorFilter actorFilter) {
        if (!actorFilter.isActive) {
            return "";
        }

        String query = String.format("\nAND movie_id IN (" +
                "\tSELECT\n" +
                "\t\tmovie_id\n" +
                "\tFROM\n" +
                "\t\tactors\n" +
                "\tWHERE actor_name ='%s' "+
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                ") \n", actorFilter.actor);


        return query;

    }

    private String buildSqlQueryTitle(TitleFilter titleFilter) {
        if (!titleFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND title = '%s'\n", titleFilter.title);

        return query;

    }

    private String buildSqlQueryGenres(GenreFilter genreFilter) {
        if (!genreFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND movie_id IN (" +
                "\tSELECT\n" +
                "\t\tmovie_id\n" +
                "\tFROM\n" +
                "\t\tgenres\n" +
                "\tWHERE genre ='%s' "+
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                ") \n", genreFilter.genre);

        return query;

    }

    @Data
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class MoviesReqData {
        public final String idToStartFrom;
        public final boolean isBackwards;
        public final String numMovies;
        public final Filters filters;
    }

    public static class MoviesRecommendData {
        public String movieId;
        public String recommenderUserId;
        public String recommendedUserId;
    }

}
