package com.baeldung.arraydeque;


import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.Assert;
import org.junit.Test;


public class ArrayDequeUnitTest {
    @Test
    public void whenOffer_addsAtLast() {
        final Deque<String> deque = new ArrayDeque<>();
        deque.offer("first");
        deque.offer("second");
        Assert.assertEquals("second", deque.getLast());
    }

    @Test
    public void whenPoll_removesFirst() {
        final Deque<String> deque = new ArrayDeque<>();
        deque.offer("first");
        deque.offer("second");
        Assert.assertEquals("first", deque.poll());
    }

    @Test
    public void whenPush_addsAtFirst() {
        final Deque<String> deque = new ArrayDeque<>();
        deque.push("first");
        deque.push("second");
        Assert.assertEquals("second", deque.getFirst());
    }

    @Test
    public void whenPop_removesLast() {
        final Deque<String> deque = new ArrayDeque<>();
        deque.push("first");
        deque.push("second");
        Assert.assertEquals("second", deque.pop());
    }
}

