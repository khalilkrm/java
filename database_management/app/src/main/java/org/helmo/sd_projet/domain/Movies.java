package org.helmo.sd_projet.domain;

import java.util.*;

public class Movies {

    private final Map<Integer, Movie> movies;

    public Movies() {
        this.movies = new HashMap<>();
    }

    public void addMovie(Movie movie, int id) {
        movies.put(id, movie);
    }

    public boolean contains(Movie newMovie) {
        var values = movies.entrySet();
        for (var entry : values)
            if (entry.getValue().equals(newMovie))
                return true;
        return false;
    }

    public List<Movie> searchMovie(String text) {
        text = text.toLowerCase();
        List<Movie> result = new ArrayList<>();
        var values = movies.entrySet();
        for (var entry : values) {
            var m = entry.getValue();
            if (m.getTitle().toLowerCase().contains(text))
                result.add(m);
        }
        return result;
    }

    public int getID(Movie movie) {
        var values = movies.entrySet();
        for (var entry : values)
            if (entry.getValue().equals(movie))
                return entry.getKey();
        return -1;

    }

    public Movie get(int id) {
        return movies.get(id);
    }

    public Iterator<Map.Entry<Integer, Movie>> getIterator() {
        return movies.entrySet().iterator();
    }
}
