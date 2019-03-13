package com.baeldung.queueInterface;


import org.junit.Assert;
import org.junit.Test;


public class CustomBaeldungQueueUnitTest {
    private CustomBaeldungQueue<Integer> customQueue;

    @Test
    public void givenQueueWithTwoElements_whenElementsRetrieved_checkRetrievalCorrect() {
        customQueue.add(7);
        customQueue.add(5);
        int first = customQueue.poll();
        int second = customQueue.poll();
        Assert.assertEquals(7, first);
        Assert.assertEquals(5, second);
    }
}

