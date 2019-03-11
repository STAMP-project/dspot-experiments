package com.querydsl.apt.domain;


import QGeneric3Test_MyOrder.myOrder;
import com.querydsl.core.types.dsl.StringPath;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.junit.Test;


public class Generic3Test extends AbstractTest {
    @MappedSuperclass
    public abstract static class BaseEntity<E extends Generic3Test.BaseEntity<E>> {}

    @MappedSuperclass
    public abstract static class Order<O extends Generic3Test.Order<O>> extends Generic3Test.BaseEntity<O> implements Cloneable {
        String property1;
    }

    @Entity
    public static class MyOrder<O extends Generic3Test.MyOrder<O>> extends Generic3Test.Order<O> {
        String property2;
    }

    @Test
    public void test() throws NoSuchFieldException {
        start(QGeneric3Test_MyOrder.class, myOrder);
        match(StringPath.class, "property1");
        match(StringPath.class, "property2");
    }
}

