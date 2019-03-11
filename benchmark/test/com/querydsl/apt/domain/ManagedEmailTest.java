package com.querydsl.apt.domain;


import QManagedEmailTest_ManagedEmails.managedEmails.emails;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import org.junit.Assert;
import org.junit.Test;


public class ManagedEmailTest {
    public interface ManagedEmail {}

    public enum EmailType {

        WORK,
        HOME;}

    @Entity
    public static class ManagedEmailImpl implements ManagedEmailTest.ManagedEmail {}

    @Entity
    public static class ManagedEmails {
        @OneToMany(targetEntity = ManagedEmailTest.ManagedEmailImpl.class)
        @MapKey(name = "emailType")
        private Map<ManagedEmailTest.EmailType, ManagedEmailTest.ManagedEmail> emails;
    }

    @Test
    public void test() {
        Assert.assertEquals(ManagedEmailTest.EmailType.class, emails.getKeyType());
        Assert.assertEquals(ManagedEmailTest.ManagedEmailImpl.class, emails.getValueType());
    }
}

