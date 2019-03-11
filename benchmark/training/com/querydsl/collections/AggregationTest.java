package com.querydsl.collections;


import org.junit.Assert;
import org.junit.Test;

import static QCat.cat;


public class AggregationTest extends AbstractQueryTest {
    private static final QCat cat = cat;

    private CollQuery<?> query;

    @Test
    public void avg() {
        Assert.assertEquals(3.5, query.select(AggregationTest.cat.weight.avg()).fetchOne(), 0.0);
    }

    @Test
    public void count() {
        Assert.assertEquals(Long.valueOf(4L), query.select(AggregationTest.cat.count()).fetchOne());
    }

    @Test
    public void countDistinct() {
        Assert.assertEquals(Long.valueOf(4L), query.select(AggregationTest.cat.countDistinct()).fetchOne());
    }

    @Test
    public void max() {
        Assert.assertEquals(Integer.valueOf(5), query.select(AggregationTest.cat.weight.max()).fetchOne());
    }

    @Test
    public void min() {
        Assert.assertEquals(Integer.valueOf(2), query.select(AggregationTest.cat.weight.min()).fetchOne());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = UnsupportedOperationException.class)
    public void min_and_max() {
        query.select(AggregationTest.cat.weight.min(), AggregationTest.cat.weight.max()).fetchOne();
    }

    @Test
    public void sum() {
        Assert.assertEquals(Integer.valueOf(14), query.select(AggregationTest.cat.weight.sum()).fetchOne());
    }
}

