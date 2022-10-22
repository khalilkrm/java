package org.helmo.sd_projet.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Review {
    private final String comment;
    private final int evaluation;
    private final Customer author;
    private final Movie movie;
    private final LocalDate creationDate;

    public Review(String comment, int evaluation, Customer author, Movie movie, LocalDate creationDate) {
        this.comment = comment;
        this.evaluation = evaluation;
        this.author = author;
        this.movie = movie;
        this.creationDate = creationDate;
    }

    public String getComment() {
        return comment;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public Customer getAuthor() {
        return author;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Review review = (Review) o;
        return evaluation == review.evaluation && comment.equals(review.comment) && author.equals(review.author)
                && movie.equals(review.movie) && creationDate.equals(review.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, evaluation, author, movie, creationDate);
    }

    @Override
    public String toString() {
        return String.format(
                "{\"evaluation\":%s," +
                        "\"author\":\"%s\"," +
                        "\"comment\":\"%s\"," +
                        "\"creationDate\":\"%s\"}", evaluation, author.getNickname(), comment, creationDate);
    }
}
