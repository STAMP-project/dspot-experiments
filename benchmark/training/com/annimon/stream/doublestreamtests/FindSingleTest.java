package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import com.annimon.stream.OptionalDouble;
import org.junit.Assert;
import org.junit.Test;


public final class FindSingleTest {
    @Test
    public void testFindSingleOnEmptyStream() {
        Assert.assertThat(DoubleStream.empty().findSingle(), isEmpty());
    }

    @Test
    public void testFindSingleOnOneElementStream() {
        Assert.assertThat(DoubleStream.of(42.0).findSingle(), hasValue(42.0));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindSingleOnMoreElementsStream() {
        DoubleStream.of(1, 2).findSingle();
    }

    @Test
    public void testFindSingleAfterFilteringToEmptyStream() {
        OptionalDouble result = DoubleStream.of(0, 1, 2).filter(Functions.greaterThan(Math.PI)).findSingle();
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFindSingleAfterFilteringToOneElementStream() {
        OptionalDouble result = DoubleStream.of(1.0, 10.12, (-3.01)).filter(Functions.greaterThan(Math.PI)).findSingle();
        Assert.assertThat(result, hasValue(10.12));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindSingleAfterFilteringToMoreElementStream() {
        DoubleStream.of(1.0, 10.12, (-3.01), 6.45).filter(Functions.greaterThan(Math.PI)).findSingle();
    }
}

