package com.querydsl.apt.domain;


import QManyToManyTest_Person.person.phones;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.junit.Assert;
import org.junit.Test;


public class ManyToManyTest {
    public interface PhoneNumber {}

    @Entity
    public static class PhoneNumberImpl {}

    @Entity
    public static class Person {
        @ManyToMany(targetEntity = ManyToManyTest.PhoneNumberImpl.class)
        Set<ManyToManyTest.PhoneNumber> phones;
    }

    @Test
    public void test() {
        Assert.assertEquals(ManyToManyTest.PhoneNumberImpl.class, phones.getElementType());
    }
}

