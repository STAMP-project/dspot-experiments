package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryInit;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class AnyPathTest {
    @Entity
    public static class Foo {
        @OneToMany(mappedBy = "key.foo")
        @QueryInit("key.student")
        private Set<AnyPathTest.Bar> bars = new HashSet<AnyPathTest.Bar>();
    }

    @Entity
    public static class Bar {
        @EmbeddedId
        @QueryInit("student")
        private AnyPathTest.BarId key = new AnyPathTest.BarId();
    }

    @Embeddable
    public static class BarId {
        @ManyToOne
        private AnyPathTest.Student student;

        @ManyToOne
        private AnyPathTest.Foo foo;
    }

    @Entity
    public static class Student {}

    @Test
    public void anyPath() {
        Assert.assertNotNull(authorFilter(new AnyPathTest.Student()));
    }
}

