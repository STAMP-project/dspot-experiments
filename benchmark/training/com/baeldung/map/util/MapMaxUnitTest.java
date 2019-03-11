package com.baeldung.map.util;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MapMaxUnitTest {
    Map<Integer, Integer> map = null;

    MapMax mapMax = null;

    @Test
    public void givenMap_whenIterated_thenReturnMaxValue() {
        Assert.assertEquals(new Integer(38), mapMax.maxUsingIteration(map));
    }

    @Test
    public void givenMap_whenUsingCollectionsMax_thenReturnMaxValue() {
        Assert.assertEquals(new Integer(38), mapMax.maxUsingCollectionsMax(map));
    }

    @Test
    public void givenMap_whenUsingCollectionsMaxAndLambda_thenReturnMaxValue() {
        Assert.assertEquals(new Integer(38), mapMax.maxUsingCollectionsMaxAndLambda(map));
    }

    @Test
    public void givenMap_whenUsingCollectionsMaxAndMethodReference_thenReturnMaxValue() {
        Assert.assertEquals(new Integer(38), mapMax.maxUsingCollectionsMaxAndMethodReference(map));
    }

    @Test
    public void givenMap_whenUsingStreamAndLambda_thenReturnMaxValue() {
        Assert.assertEquals(new Integer(38), mapMax.maxUsingStreamAndLambda(map));
    }

    @Test
    public void givenMap_whenUsingStreamAndMethodReference_thenReturnMaxValue() {
        Assert.assertEquals(new Integer(38), mapMax.maxUsingStreamAndMethodReference(map));
    }
}

