package com.willowtreeapps.hyperion.timber.model;


import org.junit.Assert;
import org.junit.Test;


public class CircularBufferTest {
    @Test
    public void size() {
        CircularBuffer<Integer> queue = new CircularBuffer(2);
        Assert.assertEquals(0, queue.size());
        queue.enqueue(0);
        queue.enqueue(1);
        Assert.assertEquals(2, queue.size());
        queue.enqueue(3);
        Assert.assertEquals(2, queue.size());
    }

    @Test
    public void getItem() {
        CircularBuffer<Integer> queue = new CircularBuffer(3);
        queue.enqueue(0);
        Assert.assertEquals(((Integer) (0)), queue.getItem(0));
        queue.enqueue(1);
        Assert.assertEquals(((Integer) (1)), queue.getItem(0));
        Assert.assertEquals(((Integer) (0)), queue.getItem(1));
        queue.enqueue(2);
        Assert.assertEquals(((Integer) (2)), queue.getItem(0));
        Assert.assertEquals(((Integer) (1)), queue.getItem(1));
        Assert.assertEquals(((Integer) (0)), queue.getItem(2));
        queue.enqueue(3);
        Assert.assertEquals(((Integer) (3)), queue.getItem(0));
        Assert.assertEquals(((Integer) (2)), queue.getItem(1));
        Assert.assertEquals(((Integer) (1)), queue.getItem(2));
    }
}

