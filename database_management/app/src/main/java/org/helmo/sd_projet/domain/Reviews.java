package org.helmo.sd_projet.domain;

import java.util.*;

public class Reviews {

    private final List<Review> reviews;

    public Reviews() {
        this.reviews = new ArrayList<>();
    }

    public void addReview(final Review review) {
        if (!this.contains(review))
            reviews.add(review);
    }

    public boolean contains(final Review newMReview) {
        for (var value : reviews)
            if (value.getAuthor().equals(newMReview.getAuthor()) && value.getMovie().equals(newMReview.getMovie())) {
                return true;
            }
        return false;
    }

    public List<Review> concernBy(final Movie movie) {
        List<Review> results = new ArrayList<>();

        for (var value : reviews) {
            if (value.getMovie().equals(movie)) {
                results.add(value);
            }
        }

        return results;
    }

    public Iterator<Review> getIterator() {
        return reviews.iterator();
    }
}
