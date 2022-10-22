package org.helmo.sd_projet.storage;

import org.helmo.sd_projet.domain.*;

public interface FilmographyStorage extends AutoCloseable {

    void addUser(final Customer customer);

    void loadUsers();

    void addPerson(final Person person);

    void loadPersons();

    void addMovie(final Movie movie);

    void loadMovies();

    void addReview(final Review review);

    void loadReviews();

    void load();

    void loadCategories();

    void addCategory(final Category category);
}
