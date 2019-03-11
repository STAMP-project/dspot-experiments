package com.querydsl.apt.domain;


import QExampleEntity.exampleEntity;
import com.querydsl.core.types.Predicate;
import org.junit.Assert;
import org.junit.Test;


public class QueryByExampleTest {
    @Test
    public void name_not_set() {
        ExampleEntity entity = new ExampleEntity();
        Predicate qbe = exampleEntity.like(entity);
        Assert.assertNull(qbe);
    }

    @Test
    public void name_set() {
        ExampleEntity entity = new ExampleEntity();
        entity.name = "XXX";
        Predicate qbe = exampleEntity.like(entity);
        Assert.assertEquals("exampleEntity.name = XXX", qbe.toString());
    }
}

