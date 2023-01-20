package com.example.demo.service;

import com.example.demo.model.Movie;
import com.example.demo.model.User;
import com.example.demo.model.filters.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    Logger logger = LoggerFactory.getLogger(MovieService.class);
    @Autowired
    UserService userService;


    public List<Movie> getMovies(MovieService.MoviesReqData moviesRange, String user_id1) {
        if (moviesRange == null) {
            return new ArrayList<>();
        }
        if (moviesRange.filters == null) {
            return new ArrayList<>();
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
        String userRecommendedToOtherQ = buildSqlQueryUserRecommendedToOther(moviesRange.filters.userRecommendedToOtherFilter, user_id1);
        String recommendedToUser = buildSqlQueryRecommendedToUser(moviesRange.filters.recommendedToUserFilter, user_id1);
//        buildSqlQueryGenres
        String userRecommendedToOtherJoin = "\n";
        String recommendedToUserJoin = "\n";
        if (moviesRange.filters.userRecommendedToOtherFilter.isActive) {
            userRecommendedToOtherJoin = "\tINNER JOIN user_recommended_to_other ON md.movie_id = user_recommended_to_other.movie_id\n";
        }

        if (moviesRange.filters.recommendedToUserFilter.isActive) {
            recommendedToUserJoin = "\tINNER JOIN recommended_to_user ON md.movie_id = recommended_to_user.movie_id\n";
        }
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
                "user_recommended_to_other AS (\n" +
                "\tSELECT\n" +
                "\t\tmovie_id,\n" +
                "\t\tGROUP_CONCAT(user_id2 SEPARATOR ',') AS user_id2_concat\n" +
                "\tFROM\n" +
                "\t\trecommendation\n" +
                "\tWHERE 1=1 "+
                userRecommendedToOtherQ +
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                "),\n" +
                "recommended_to_user AS (\n" +
                "\tSELECT\n" +
                "\t\tmovie_id,\n" +
                "\t\tGROUP_CONCAT(user_id1 SEPARATOR ',') AS user_id1_concat\n" +
                "\tFROM\n" +
                "\t\trecommendation\n" +
                "\tWHERE 1=1 "+
                recommendedToUser +
                "\tGROUP BY\n" +
                "\t\tmovie_id\n" +
                "),\n" +
                "\n" +
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
                        userRecommendedToOtherJoin +
                        recommendedToUserJoin +
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

                String user_recommended_to_other_concat = null;
                String recommended_to_user_concat = null;

                if (moviesRange.filters.userRecommendedToOtherFilter.isActive) {
                    user_recommended_to_other_concat = rs.getString("user_id2_concat");
                }

                if (moviesRange.filters.recommendedToUserFilter.isActive) {
                    recommended_to_user_concat = rs.getString("user_id1_concat");
                }
                List<String> user_recommended_to_other;
                if (user_recommended_to_other_concat != null) {
                    user_recommended_to_other = List.of(user_recommended_to_other_concat.split(",")).stream().map(u -> userService.getUserName(u)).collect(Collectors.toList());


                } else {
                    user_recommended_to_other = new ArrayList<>();
                }

                List<String> recommended_to_user;
                if (recommended_to_user_concat != null) {
                    recommended_to_user = List.of(recommended_to_user_concat.split(",")).stream().map(u -> userService.getUserName(u)).collect(Collectors.toList());

                } else {
                    recommended_to_user = new ArrayList<>();
                }
                Movie movie = new Movie(movieId, budget, adult, revenue, language, overview, popularity,title,
                        directors, actors, genres, null, user_recommended_to_other, recommended_to_user );
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

    private String buildSqlQueryUserRecommendedToOther(UserRecommendedToOtherFilter userRecommendedToOtherFilter, String userId1) {
        if (!userRecommendedToOtherFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND user_id1=%s", userId1);

        return query;

    }

    private String buildSqlQueryRecommendedToUser(RecommendedToUserFilter  recommendedToUserFilter, String userId1) {
        if (!recommendedToUserFilter.isActive) {
            return "";
        }
        String query = String.format("\nAND user_id2=%s", userId1);

        return query;

    }


    public Boolean recommendMovie(MoviesRecommendData recommendData, String user_id1) {
        if (recommendData.movieId == null || recommendData.userName2 == null || user_id1 == null) {
            return false;
        }
        String user_id2 = Long.toString(userService.getUserIdOnlyByName(recommendData.userName2));
        if (user_id2.equals("-1")) {
            return false;
        }
        if (isAlreadyRecommeded( user_id1, user_id2, recommendData.movieId)) {
            return true;
        }

        Boolean res = false;
        String sqlSelectAllPersons = String.format("INSERT INTO recommendation (user_id1, user_id2, movie_id)\n values (%s, %s, %s)", user_id1, user_id2, recommendData.movieId);
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

    public Boolean isAlreadyRecommeded(String user_id1, String user_id2, String movieId) {
        if (movieId == null || user_id2 == null || user_id1 == null) {
            return false;
        }

        String sqlSelectAllPersons = String.format("SELECT COUNT(*) FROM recommendation WHERE user_id1=%s AND user_id2=%s AND movie_id=%s", user_id1, user_id2, movieId);
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
        return false;
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

    @Data
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class MoviesRecommendData {
        public String movieId;
        public String userName2;
    }

    @Data
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Recommendation {
        public String movieId;
        public String userId1;
        public String userId2;
    }

}
