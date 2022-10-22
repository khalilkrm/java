package org.helmo.sd_projet.domain;

import java.util.*;

public class Persons {
    private final Map<Integer, Person> persons;

    public Persons() {
        this.persons = new HashMap<>();
    }

    public void addPerson(Person person, int id) {
        persons.put(id, person);
    }

    public Iterator<Map.Entry<Integer, Person>> getIterator() {
        return persons.entrySet().iterator();
    }

    public boolean contains(final Person newPerson) {
        var values = persons.entrySet();
        for (var entry : values)
            if (entry.getValue().equals(newPerson))
                return true;
        return false;
    }

    public int getID(Person director) {
        var values = persons.entrySet();
        for (var entry : values)
            if (entry.getValue().equals(director))
                return entry.getKey();
        return -1;
    }

    public List<Person> searchPerson(String text) {
        text = text.toLowerCase();
        List<Person> result = new ArrayList<>();
        var values = persons.entrySet();
        for (var entry : values) {
            Person p = entry.getValue();
            String name = p.getFirstname() + " " + p.getLastname();
            if (name.toLowerCase().contains(text))
                result.add(p);
        }
        return result;
    }

    public Person getPerson(int id) {
        return persons.get(id);
    }
}
