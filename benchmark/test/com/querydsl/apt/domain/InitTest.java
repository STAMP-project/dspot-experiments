package com.querydsl.apt.domain;


import QInitTest_User.user.address.city;
import org.junit.Assert;
import org.junit.Test;

import static FetchType.EAGER;


public class InitTest {
    @Entity
    public static class User {
        @ManyToOne(fetch = EAGER)
        private InitTest.Address address;
    }

    @Entity
    public static class Address extends InitTest.AddressBase {}

    @MappedSuperclass
    public abstract static class AddressBase {
        @Id
        private long idAddress;

        @Id
        private int numVersion;

        @ManyToOne
        private InitTest.City city;
    }

    @Entity
    public static class City {}

    @Test
    public void test() {
        Assert.assertNotNull(city);
    }
}

