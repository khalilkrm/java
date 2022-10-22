package org.helmo.sd_projet.domain;

import java.util.*;

public class Customers {
    private final Map<Integer, Customer> customers;

    public Customers() {
        this.customers = new HashMap<>();
    }

    public void addCustomer(Customer customer, int id) {
        customers.put(id, customer);
    }

    public Iterator<Map.Entry<Integer, Customer>> getIterator() {
        return customers.entrySet().iterator();
    }

    public boolean contains(Customer newCustomer) {
        var values = customers.entrySet();
        for (var entry : values)
            if (entry.getValue().getNickname().equalsIgnoreCase(newCustomer.getNickname()))
                return true;
        return false;
    }

    public int getID(Customer author) {
        var values = customers.entrySet();
        for (var entry : values)
            if (entry.getValue().equals(author))
                return entry.getKey();
        return -1;
    }

    public Customer get(int id) {
        return customers.get(id);
    }

    public List<Customer> searchCustomer(String text) {
        text = text.toLowerCase();
        List<Customer> result = new ArrayList<>();
        var values = customers.entrySet();
        for (var entry : values) {
            Customer p = entry.getValue();
            String name = p.getNickname();
            if (name.toLowerCase().contains(text))
                result.add(p);
        }
        return result;
    }
}
