package com.baeldung.queueInterface;


import java.util.PriorityQueue;
import org.junit.Assert;
import org.junit.Test;


public class PriorityQueueUnitTest {
    @Test
    public void givenIntegerQueue_whenIntegersOutOfOrder_checkRetrievalOrderIsNatural() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<>();
        integerQueue.add(9);
        integerQueue.add(2);
        integerQueue.add(4);
        int first = integerQueue.poll();
        int second = integerQueue.poll();
        int third = integerQueue.poll();
        Assert.assertEquals(2, first);
        Assert.assertEquals(4, second);
        Assert.assertEquals(9, third);
    }

    @Test
    public void givenStringQueue_whenStringsAddedOutOfNaturalOrder_checkRetrievalOrderNatural() {
        PriorityQueue<String> stringQueue = new PriorityQueue<>();
        stringQueue.add("banana");
        stringQueue.add("apple");
        stringQueue.add("cherry");
        String first = stringQueue.poll();
        String second = stringQueue.poll();
        String third = stringQueue.poll();
        Assert.assertEquals("apple", first);
        Assert.assertEquals("banana", second);
        Assert.assertEquals("cherry", third);
    }
}

