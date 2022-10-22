package org.helmo.sd_projet.domain;

import java.util.Objects;

public class Customer {
    private final String nickname;

    public Customer(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Customer customer = (Customer) o;
        return nickname.equalsIgnoreCase(customer.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

    @Override
    public String toString() {
        return nickname;
    }
}
