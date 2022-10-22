package org.helmo.sd_projet.domain;

import java.util.*;

public class Categories {

    private final Set<Category> categories;

    public Categories() {
        this.categories = new HashSet<>();
    }

    public void addCategory(final Category category) {
        categories.add(category);
    }

    public Iterator<Category> getIterator() {
        return categories.iterator();
    }

    public boolean contains(final Category newCategory) {
        return categories.contains(newCategory);
    }

    public Category get(final String categoryName) {
        for (var category : categories)
            if (category.getName().equals(categoryName))
                return category;
        return null;
    }

    public List<Category> searchCategory(String text) {
        if (text.isEmpty()) return new ArrayList<>(categories);
        text = text.toLowerCase();
        final List<Category> found = new ArrayList<>();
        for (var category : categories) {
            if (category.getName().toLowerCase().contains(text))
                found.add(category);
        }
        return found;
    }
}
