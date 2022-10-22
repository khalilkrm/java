package org.helmo.sd_projet.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTests {

    @Test
    public void constructorTest() {
        Customer customer = new Customer("LUDFR");
        assertEquals("LUDFR", customer.getNickname());
    }

    @Test
    public void equalsTest() {
        Customer customer = new Customer("LUDFR");
        Customer customer2 = new Customer("LUDFR");
        Customer customer3 = new Customer("HENNI");
        assertEquals(customer, customer2);
        assertNotEquals(customer, customer3);
    }

    @Test
    public void customersGetTest() {
        Customers customers = new Customers();
        customers.addCustomer(new Customer("LUDFR"), 1);
        customers.addCustomer(new Customer("HENNI"), 2);

        assertEquals("LUDFR", customers.get(1).getNickname());
        assertEquals("HENNI", customers.get(2).getNickname());
    }

    @Test
    public void customersContainsTest() {
        Customers customers = new Customers();
        customers.addCustomer(new Customer("LUDFR"), 1);
        customers.addCustomer(new Customer("HENNI"), 2);

        assertTrue(customers.contains(new Customer("LUDFR")));
        assertTrue(customers.contains(new Customer("HENNI")));
        assertFalse(customers.contains(new Customer("MATCH")));
    }
}
