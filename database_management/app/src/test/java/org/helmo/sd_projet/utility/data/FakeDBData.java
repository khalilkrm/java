package org.helmo.sd_projet.utility.data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeDBData {

    /* --- USERS --- */

    private static final List<String> SafeCustomerData = List.of(
            "insert into CUSTOMER (NICKNAME) values ('vshitliff0')",
            "insert into CUSTOMER (NICKNAME) values ('pisakson1')",
            "insert into CUSTOMER (NICKNAME) values ('wlintill2')",
            "insert into CUSTOMER (NICKNAME) values ('okipling3')",
            "insert into CUSTOMER (NICKNAME) values ('lzorzoni4')");

    private static final List<String> CustomersWithDouble = List.of(
            "insert into CUSTOMER (NICKNAME) values ('vshitliff0')",
            "insert into CUSTOMER (NICKNAME) values ('pisakson1')",
            "insert into CUSTOMER (NICKNAME) values ('wlintill2')",
            "insert into CUSTOMER (NICKNAME) values ('wlintill2')",
            "insert into CUSTOMER (NICKNAME) values ('wlintill2')",
            "insert into CUSTOMER (NICKNAME) values ('wlintill2')",
            "insert into CUSTOMER (NICKNAME) values ('pisakson1')",
            "insert into CUSTOMER (NICKNAME) values ('pisakson1')",
            "insert into CUSTOMER (NICKNAME) values ('okipling3')",
            "insert into CUSTOMER (NICKNAME) values ('lzorzoni4')");

    /* --- PERSONS --- */

    private static final List<String> SafePersonData = List.of(
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Gerner', 'Yurik', '2014-05-17')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Lamers', 'Wandis', '2010-09-30')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('De Witt', 'Maureen', '2013-03-06')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Geeves', 'Brianna', '1996-11-20')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('De Laci', 'Pepi', '2014-11-29')");

    /* Persons with double */
    private static final List<String> PersonsWithDouble = List.of(
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Gerner', 'Yurik', '2014-05-17')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Gerner', 'Yurik', '2014-05-17')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Gerner', 'Yurik', '2014-05-17')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Lamers', 'Wandis', '2010-09-30')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('De Witt', 'Maureen', '2013-03-06')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('De Witt', 'Maureen', '2013-03-06')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('De Witt', 'Maureen', '2013-03-06')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('Geeves', 'Brianna', '1996-11-20')",
            "insert into PERSON (LASTNAME, FIRSTNAME, BIRTHDATE) values ('De Laci', 'Pepi', '2014-11-29')");

    /* --- REVIEWS --- */

    private static final List<String> SafeReviewData = List.of(
            "insert into REVIEW (COMMENT, EVALUATION, REVIEW_DATE , MOVIE_ID, CUSTOMER_ID) values ('Quisque id justo sit amet sapien dignissim vestibulum.', 10, '2020-10-14', 1, 1)",
            "insert into REVIEW (COMMENT, EVALUATION, REVIEW_DATE , MOVIE_ID, CUSTOMER_ID) values ('Duis aliquam convallis nunc.', 9, '2020-07-20', 2, 2)",
            "insert into REVIEW (COMMENT, EVALUATION, REVIEW_DATE , MOVIE_ID, CUSTOMER_ID) values ('Nulla neque libero convallis eget eleifend luctus ultricies eu nibh.', 1, '2021-03-21', 3, 3)",
            "insert into REVIEW (COMMENT, EVALUATION, REVIEW_DATE , MOVIE_ID, CUSTOMER_ID) values ('Morbi vestibulum velit id pretium iaculis diam erat fermentum justo.', 3, '2021-06-19', 4, 4)",
            "insert into REVIEW (COMMENT, EVALUATION, REVIEW_DATE , MOVIE_ID, CUSTOMER_ID) values ('Nullam orci pede venenatis non sodales sed tincidunt eu felis.', 8, '2020-03-13', 5, 5)");

    /* --- MOVIES --- */

    private static final List<String> SafeMovieData = List.of(
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Preston Sturges: The Rise and Fall of an American Dreamer', 2002, '3:07:05')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Princesas', 1987, '3:52:54')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Mercredi folle journée!', 1990, '2:04:44')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Julie', 1984, '2:50:20')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Hand of Peace: Pope Pius XII and the Holocaust A', 2008, '2:03:07')");

    private static final List<String> MoviesWithDouble = List.of(
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Preston Sturges: The Rise and Fall of an American Dreamer', 2002, '3:07:05')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Princesas', 1987, '3:52:54')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Preston Sturges: The Rise and Fall of an American Dreamer', 2002, '3:07:05')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Preston Sturges: The Rise and Fall of an American Dreamer', 2002, '3:07:05')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Mercredi folle journée!', 1990, '2:04:44')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Julie', 1984, '2:50:20')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Julie', 1984, '2:50:20')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Julie', 1984, '2:50:20')",
            "insert into MOVIE (NAME, RELEASE_YEAR, RUNTIME) values ('Hand of Peace: Pope Pius XII and the Holocaust A', 2008, '2:03:07')");

    /* --- DIRECTED_BY --- */

    private static final List<String> SafeDirectedByData = List.of(
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (1, 1)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (2, 2)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (3, 1)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (4, 1)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (5, 5)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (2, 5)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (4, 2)",
            "insert into DIRECTED_BY (MOVIE_ID, PERSON_ID) values (5, 1)");

    /* --- CASTINGS --- */

    /**
     * Each position is greater than 0 and less than the number of movie's actors
     */
    private static final List<String> SafeCastingData = List.of(
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (1, 1, 1)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (1, 2, 2)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (1, 3, 3)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (3, 2, 1)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (3, 3, 2)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (3, 4, 3)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (4, 5, 1)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (4, 1, 2)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (4, 2, 3)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (5, 3, 1)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (5, 4, 2)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (5, 5, 3)");

    private static final List<String> CastingsDataWithIncorrectPositions = List.of(
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (1, 1, 41)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (1, 2, 584)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (1, 3, 44)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (2, 4, 0)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (2, 5, 254)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (2, 1, -25)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (3, 2, 1457)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (3, 3, 25)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (3, 4, 7)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (4, 5, 1)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (4, 1, 2)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (4, 2, 254)",

            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (5, 3, 36)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (5, 4, 147)",
            "insert into CASTING (MOVIE_ID, ACTOR_ID, POSITION) values (5, 5, -114)");

    private static final List<String> SafeCategoriesData = List.of(
            "insert into CATEGORY (NAME) values ('Sport')",
            "insert into CATEGORY (NAME) values ('Drame')",
            "insert into CATEGORY (NAME) values ('Comédie')",
            "insert into CATEGORY (NAME) values ('Romantique')",
            "insert into CATEGORY (NAME) values ('Action')",
            "insert into CATEGORY (NAME) values ('Policier')"
    );

    private static final List<String> SafeMovieCategoryData = List.of(
            "insert into MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) values (1, 'Sport')",
            "insert into MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) values (1, 'Comédie')",
            "insert into MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) values (2, 'Drame')",
            "insert into MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) values (3, 'Comédie')",
            "insert into MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) values (3, 'Romantique')",
            "insert into MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) values (4, 'Drame')"
    );

    /* --- DATA --- */

    public static List<String> SafeData = Stream
            .of(SafePersonData, SafeMovieData, SafeDirectedByData, SafeCastingData, SafeCustomerData,
                    SafeReviewData, SafeCategoriesData, SafeMovieCategoryData)
            .flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());

    public static List<String> IncorrectData = Stream
            .of(PersonsWithDouble, MoviesWithDouble, SafeDirectedByData, CastingsDataWithIncorrectPositions,
                    CustomersWithDouble, SafeReviewData, SafeCategoriesData, SafeMovieCategoryData)
            .flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
}
