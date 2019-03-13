package com.querydsl.apt.domain;


import QGenericStackOverflowTest_AbstractEntity.abstractEntity;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QuerySupertype;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class GenericStackOverflowTest extends AbstractTest {
    public interface Identifiable<ID extends Comparable<ID> & Serializable> {}

    @QuerySupertype
    public abstract static class AbstractEntity<ID extends Comparable<ID> & Serializable> implements GenericStackOverflowTest.Identifiable<ID> {}

    @QueryEntity
    public static class TestEntity extends GenericStackOverflowTest.AbstractEntity<Long> {}

    @Test
    public void test() {
        Assert.assertNotNull(abstractEntity);
    }
}

