package com.querydsl.apt.domain;


import QGeneric13Test_GenericBase.genericBase;
import QGeneric13Test_GenericBaseSubclass.genericBaseSubclass;
import QGeneric13Test_Subclass.subclass;
import com.querydsl.core.annotations.QueryEntity;
import org.junit.Test;


public class Generic13Test extends AbstractTest {
    @QueryEntity
    public static class GenericBase<T extends Generic13Test.AnotherClass> {
        T t;
    }

    @QueryEntity
    public static class GenericBaseSubclass<P> extends Generic13Test.GenericBase<Generic13Test.AnotherClass> {
        P p;
    }

    @QueryEntity
    public static class Subclass extends Generic13Test.GenericBaseSubclass<Number> {}

    public static class AnotherClass {}

    @Test
    public void test() throws IllegalAccessException, NoSuchFieldException {
        start(QGeneric13Test_GenericBase.class, genericBase);
        matchType(Generic13Test.AnotherClass.class, "t");
        start(QGeneric13Test_GenericBaseSubclass.class, genericBaseSubclass);
        matchType(Object.class, "p");
        start(QGeneric13Test_Subclass.class, subclass);
        matchType(Number.class, "p");
    }
}

