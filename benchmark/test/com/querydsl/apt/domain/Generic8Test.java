package com.querydsl.apt.domain;


import QGeneric8Test_Entity.entity.values;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QuerySupertype;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class Generic8Test {
    @QuerySupertype
    public static class Superclass<T> {
        Long id;

        List<T> values;

        List<? extends T> values2;
    }

    @QueryEntity
    public static class IntermediateEntity<E> extends Generic8Test.Superclass<E> {}

    @QueryEntity
    public static class Entity extends Generic8Test.Superclass<String> {}

    @QueryEntity
    public static class Entity2 extends Generic8Test.Superclass<Integer> {}

    @QueryEntity
    public static class Entity3 extends Generic8Test.IntermediateEntity<String> {}

    @Test
    public void test() {
        Assert.assertEquals(String.class, values.getElementType());
        Assert.assertEquals(Integer.class, QGeneric8Test_Entity2.entity2.values.getElementType());
        Assert.assertEquals(String.class, QGeneric8Test_Entity3.entity3.values.getElementType());
    }
}

