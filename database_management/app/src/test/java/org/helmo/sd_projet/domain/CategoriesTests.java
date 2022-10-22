package org.helmo.sd_projet.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoriesTests {

    @Test
    public void categoriesContainsTest() {
        Categories categories = new Categories();
        Category a = new Category("François");
        Category b = new Category("Stéphanie");
        Category c = new Category("Judith");

        categories.addCategory(a);
        categories.addCategory(b);

        assertTrue(categories.contains(a));
        assertTrue(categories.contains(b));
        assertFalse(categories.contains(c));
    }

}
