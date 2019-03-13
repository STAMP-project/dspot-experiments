package com.baeldung.counter;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class CounterUnitTest {
    @Test
    public void whenMapWithWrapperAsCounter_runsSuccessfully() {
        Map<String, Integer> counterMap = new HashMap<>();
        CounterUtil.counterWithWrapperObject(counterMap);
        Assert.assertEquals(3, counterMap.get("China").intValue());
        Assert.assertEquals(2, counterMap.get("India").intValue());
    }

    @Test
    public void whenMapWithLambdaAndWrapperCounter_runsSuccessfully() {
        Map<String, Long> counterMap = new HashMap<>();
        CounterUtil.counterWithLambdaAndWrapper(counterMap);
        Assert.assertEquals(3L, counterMap.get("China").longValue());
        Assert.assertEquals(2L, counterMap.get("India").longValue());
    }

    @Test
    public void whenMapWithMutableIntegerCounter_runsSuccessfully() {
        Map<String, CounterUtil.MutableInteger> counterMap = new HashMap<>();
        CounterUtil.counterWithMutableInteger(counterMap);
        Assert.assertEquals(3, counterMap.get("China").getCount());
        Assert.assertEquals(2, counterMap.get("India").getCount());
    }

    @Test
    public void whenMapWithPrimitiveArray_runsSuccessfully() {
        Map<String, int[]> counterMap = new HashMap<>();
        CounterUtil.counterWithPrimitiveArray(counterMap);
        Assert.assertEquals(3, counterMap.get("China")[0]);
        Assert.assertEquals(2, counterMap.get("India")[0]);
    }
}

