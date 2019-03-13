package com.querydsl.apt.domain;


import QOneToOneTest_Person.person.phone;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import org.junit.Assert;
import org.junit.Test;


public class OneToOneTest {
    public interface PhoneNumber {}

    @Entity
    public static class PhoneNumberImpl {}

    @Entity
    public static class Person {
        @OneToOne(targetEntity = OneToOneTest.PhoneNumberImpl.class)
        OneToOneTest.PhoneNumber phone;
    }

    @Test
    public void test() {
        Assert.assertEquals(OneToOneTest.PhoneNumberImpl.class, phone.getType());
    }
}

