package com.querydsl.apt.domain;


import QGeneric15Test_MyContainable.myContainable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.junit.Test;


public class Generic15Test extends AbstractTest {
    @MappedSuperclass
    public abstract static class Compound<T extends Generic15Test.Containable> {
        private Set<T> containables = new HashSet<T>();
    }

    @MappedSuperclass
    public abstract static class Containable<T extends Generic15Test.Compound> {
        private T compound;
    }

    @Entity
    public static class MyCompound extends Generic15Test.Compound<Generic15Test.MyContainable> {}

    @Entity
    public static class MyContainable extends Generic15Test.Containable<Generic15Test.MyCompound> {
        private String additionalField;
    }

    @Test
    public void test() throws IllegalAccessException, NoSuchFieldException {
        start(QGeneric15Test_MyContainable.class, myContainable);
        match(QGeneric15Test_MyCompound.class, "compound");
        matchType(Generic15Test.MyCompound.class, "compound");
    }
}

