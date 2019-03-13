package com.orm.query;


import com.orm.util.QueryBuilder;
import junit.framework.Assert;
import org.junit.Test;


public final class QueryBuilderTests {
    @Test(expected = RuntimeException.class)
    public void noArgumentsTest() {
        QueryBuilder.generatePlaceholders(0);
    }

    @Test
    public void oneArgumentsTest() {
        Assert.assertEquals("?", QueryBuilder.generatePlaceholders(1));
    }

    @Test
    public void twoArgumentsTest() {
        Assert.assertEquals("?,?", QueryBuilder.generatePlaceholders(2));
    }

    @Test
    public void manyArgumentsTest() {
        Assert.assertEquals("?,?,?,?,?,?,?,?,?,?", QueryBuilder.generatePlaceholders(10));
    }
}

