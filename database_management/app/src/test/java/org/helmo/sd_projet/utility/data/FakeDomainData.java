package org.helmo.sd_projet.utility.data;

import org.helmo.sd_projet.domain.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FakeDomainData {

    public static List<Person> persons = List.of(
            new Person("Joy", "Edgin", LocalDate.parse("2021-12-25")),
            new Person("Joy", "Edgin", LocalDate.parse("2021-12-25")),
            new Person("Jamey", "Pharro", LocalDate.now()),
            new Person("Kinnie", "Merryweather", LocalDate.now()),
            new Person("Barnett", "Domenichini", LocalDate.now()),
            new Person("Keebly", "Woolard", LocalDate.now()));

    public static List<Customer> customers = List.of(
            new Customer("tmarcoolyn0"),
            new Customer("tmarcoolyn0"),
            new Customer("msouthern2"),
            new Customer("gdenes3"),
            new Customer("acarlow5"));

    public static List<Movie> movies = List.of(
            new Movie("Airport '77", 2008, Duration.ofMinutes(145), List.of(
                    persons.get(0)),
                    List.of(
                            persons.get(0)), Set.of(
                    new Category("Sport"),
                    new Category("Drame"),
                    new Category("Comédie"))),
            new Movie("Airport '77", 2008, Duration.ofMinutes(145), List.of(
                    persons.get(0)),
                    List.of(
                            persons.get(0)), Set.of(
                    new Category("Sport"),
                    new Category("Drame"),
                    new Category("Comédie"))),
            new Movie("Tai Chi Master (Twin Warriors) (Tai ji: Zhang San Feng)", 1998,
                    Duration.ofMinutes(145), List.of(
                    persons.get(4),
                    persons.get(1),
                    persons.get(2),
                    persons.get(3)),
                    List.of(
                            persons.get(4),
                            persons.get(1),
                            persons.get(2),
                            persons.get(3)), Set.of(
                    new Category("Sport"))),
            new Movie("Veronika Voss (Sehnsucht der Veronika Voss, Die)", 2025, Duration.ofMinutes(145),
                    List.of(
                            persons.get(0),
                            persons.get(1),
                            persons.get(2),
                            persons.get(3)),
                    List.of(
                            persons.get(4),
                            persons.get(1),
                            persons.get(2),
                            persons.get(3)), Set.of(
                    new Category("Romantique"),
                    new Category("Action"),
                    new Category("Policier"))),
            new Movie("Friday Night (Vendredi Soir)", 2014, Duration.ofMinutes(145), List.of(
                    persons.get(0),
                    persons.get(1),
                    persons.get(2),
                    persons.get(3)),
                    List.of(
                            persons.get(4),
                            persons.get(1),
                            persons.get(2),
                            persons.get(3)), Set.of(
                    new Category("Action"),
                    new Category("Policier"))),
            new Movie("Street Fighter: Assassin's Fist", 2025, Duration.ofMinutes(145), List.of(
                    persons.get(0),
                    persons.get(1),
                    persons.get(2),
                    persons.get(3)),
                    List.of(
                            persons.get(4),
                            persons.get(1),
                            persons.get(2),
                            persons.get(3)), Set.of(
                    new Category("Romantique"),
                    new Category("Action"),
                    new Category("Policier"))));

    public static List<Review> reviews = List.of(
            new Review("Tim Roth appears impervious and shamelessly kindhearted as the jittery Atoner",
                    5, customers.get(0), movies.get(0), LocalDate.now()),
            new Review("Tim Roth appears impervious and shamelessly kindhearted as the jittery Atoner",
                    5, customers.get(0), movies.get(0), LocalDate.now()),
            new Review("Frances McDormand is facetious but vivacious as the fanatical Playful Hacker",
                    5, customers.get(2), movies.get(2), LocalDate.now()),
            new Review("Marie-Christine Barrault seems immaterial while managing to stay whimsical as the empty Sealed Evil In A Can",
                    5, customers.get(4), movies.get(4), LocalDate.now()),
            new Review("Merle Oberon comes across as insipid yet somehow abrasive as the phobic Granola Girl",
                    5, customers.get(3), movies.get(3), LocalDate.now()));

    public static List<Category> categories = List.of(
            new Category("Sport"),
            new Category("Sport"),
            new Category("Drame"),
            new Category("Comédie"),
            new Category("Romantique"),
            new Category("Action"),
            new Category("Policier")
    );
}