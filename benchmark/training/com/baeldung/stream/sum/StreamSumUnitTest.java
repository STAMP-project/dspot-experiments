package com.baeldung.stream.sum;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StreamSumUnitTest {
    @Test
    public void givenListOfIntegersWhenSummingUsingCustomizedAccumulatorThenCorrectValueReturned() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = StreamSumCalculator.getSumUsingCustomizedAccumulator(integers);
        Assertions.assertEquals(15, sum.intValue());
    }

    @Test
    public void givenListOfIntegersWhenSummingUsingJavaAccumulatorThenCorrectValueReturned() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = StreamSumCalculator.getSumUsingJavaAccumulator(integers);
        Assertions.assertEquals(15, sum.intValue());
    }

    @Test
    public void givenListOfIntegersWhenSummingUsingReduceThenCorrectValueReturned() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = StreamSumCalculator.getSumUsingReduce(integers);
        Assertions.assertEquals(15, sum.intValue());
    }

    @Test
    public void givenListOfIntegersWhenSummingUsingCollectThenCorrectValueReturned() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = StreamSumCalculator.getSumUsingCollect(integers);
        Assertions.assertEquals(15, sum.intValue());
    }

    @Test
    public void givenListOfIntegersWhenSummingUsingSumThenCorrectValueReturned() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = StreamSumCalculator.getSumUsingSum(integers);
        Assertions.assertEquals(15, sum.intValue());
    }

    @Test
    public void givenListOfItemsWhenSummingUsingCustomizedAccumulatorThenCorrectValueReturned() {
        Item item1 = new Item(1, 10);
        Item item2 = new Item(2, 15);
        Item item3 = new Item(3, 25);
        Item item4 = new Item(4, 40);
        List<Item> items = Arrays.asList(item1, item2, item3, item4);
        Integer sum = StreamSumCalculatorWithObject.getSumUsingCustomizedAccumulator(items);
        Assertions.assertEquals(90, sum.intValue());
    }

    @Test
    public void givenListOfItemsWhenSummingUsingJavaAccumulatorThenCorrectValueReturned() {
        Item item1 = new Item(1, 10);
        Item item2 = new Item(2, 15);
        Item item3 = new Item(3, 25);
        Item item4 = new Item(4, 40);
        List<Item> items = Arrays.asList(item1, item2, item3, item4);
        Integer sum = StreamSumCalculatorWithObject.getSumUsingJavaAccumulator(items);
        Assertions.assertEquals(90, sum.intValue());
    }

    @Test
    public void givenListOfItemsWhenSummingUsingReduceThenCorrectValueReturned() {
        Item item1 = new Item(1, 10);
        Item item2 = new Item(2, 15);
        Item item3 = new Item(3, 25);
        Item item4 = new Item(4, 40);
        List<Item> items = Arrays.asList(item1, item2, item3, item4);
        Integer sum = StreamSumCalculatorWithObject.getSumUsingReduce(items);
        Assertions.assertEquals(90, sum.intValue());
    }

    @Test
    public void givenListOfItemsWhenSummingUsingCollectThenCorrectValueReturned() {
        Item item1 = new Item(1, 10);
        Item item2 = new Item(2, 15);
        Item item3 = new Item(3, 25);
        Item item4 = new Item(4, 40);
        List<Item> items = Arrays.asList(item1, item2, item3, item4);
        Integer sum = StreamSumCalculatorWithObject.getSumUsingCollect(items);
        Assertions.assertEquals(90, sum.intValue());
    }

    @Test
    public void givenListOfItemsWhenSummingUsingSumThenCorrectValueReturned() {
        Item item1 = new Item(1, 10);
        Item item2 = new Item(2, 15);
        Item item3 = new Item(3, 25);
        Item item4 = new Item(4, 40);
        List<Item> items = Arrays.asList(item1, item2, item3, item4);
        Integer sum = StreamSumCalculatorWithObject.getSumUsingSum(items);
        Assertions.assertEquals(90, sum.intValue());
    }

    @Test
    public void givenMapWhenSummingThenCorrectValueReturned() {
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        map.put(1, 10);
        map.put(2, 15);
        map.put(3, 25);
        map.put(4, 40);
        Integer sum = StreamSumCalculator.getSumOfMapValues(map);
        Assertions.assertEquals(90, sum.intValue());
    }

    @Test
    public void givenStringWhenSummingThenCorrectValueReturned() {
        String string = "Item1 10 Item2 25 Item3 30 Item4 45";
        Integer sum = StreamSumCalculator.getSumIntegersFromString(string);
        Assertions.assertEquals(110, sum.intValue());
    }
}

