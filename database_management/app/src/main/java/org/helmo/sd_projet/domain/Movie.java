package org.helmo.sd_projet.domain;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Movie {
    private final String title;
    private final int release_year;
    private final Duration duration;
    private final List<Person> director;
    private final List<Person> casting;
    private final Set<Category> categories;

    public Movie(String title, int release_year, Duration duration, List<Person> director, List<Person> casting, Set<Category> categories) {
        this.title = title;
        this.release_year = release_year;
        this.duration = duration;
        this.director = director;
        this.casting = casting;
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public int getRelease_year() {
        return release_year;
    }

    public Duration getDuration() {
        return duration;
    }

    public Iterator<Person> castingIterator() {
        return casting.iterator();
    }

    public Iterator<Person> directorIterator() {
        return director.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Movie movie = (Movie) o;
        return release_year == movie.release_year && title.equals(movie.title) && duration.equals(movie.duration)
                && director.equals(movie.director) && casting.equals(movie.casting) && categories.equals(movie.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, release_year, duration, director, casting, categories);
    }

    @Override
    public String toString() {
        final String actors = casting.stream().map(Person::toString).collect(Collectors.joining(","));
        final String directors = director.stream().map(Person::toString).collect(Collectors.joining(","));
        final String cats = categories.stream().map(Category::toString).collect(Collectors.joining(","));
        return String.format("{" +
                "\"duration\":%s," +
                "\"castings\":[%s]," +
                "\"directors\":[%s]," +
                "\"release_year\":%d," +
                "\"categories\":%s," +
                "\"title\":\"%s\"}", duration.toMinutes(), actors, directors, release_year, cats, title);
    }

    public Iterator<Category> categoriesIterator() {
        return categories.stream().iterator();
    }
}
