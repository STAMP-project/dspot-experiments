package com.querydsl.apt.domain;


import QQueryInit5Test_Parent.parent;
import QQueryInit5Test_Parent.parent.z.entity;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import org.junit.Assert;
import org.junit.Test;


public class QueryInit5Test {
    @QueryEntity
    public static class OtherClass {
        QueryInit5Test.OtherClass entity;
    }

    @QueryEntity
    public static class OtherClassTwo {
        QueryInit5Test.OtherClassTwo entity;
    }

    @QueryEntity
    public static class Parent {
        int x;

        @QueryInit("*")
        QueryInit5Test.OtherClass z;
    }

    @QueryEntity
    public static class Child extends QueryInit5Test.Parent {
        @QueryInit("*")
        QueryInit5Test.OtherClassTwo y;
    }

    @Test
    public void test() {
        // QChild c = QParent.parent.as(QChild.class)
        Assert.assertNotNull(entity);
        QQueryInit5Test_Child child = parent.as(QQueryInit5Test_Child.class);
        Assert.assertNotNull(child.z.entity);
        Assert.assertNotNull(child.y.entity);
    }
}

