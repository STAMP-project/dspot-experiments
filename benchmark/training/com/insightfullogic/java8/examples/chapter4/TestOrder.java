package com.insightfullogic.java8.examples.chapter4;


import com.insightfullogic.java8.examples.chapter1.Album;
import java.util.List;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestOrder {
    private final TestOrder.OrderFactory factory;

    private Order order;

    private static interface OrderFactory extends Function<List<Album>, Order> {}

    public TestOrder(TestOrder.OrderFactory factory) {
        this.factory = factory;
    }

    @Test
    public void countsRunningTime() {
        Assert.assertEquals(909, order.countRunningTime());
    }

    @Test
    public void countsArtists() {
        Assert.assertEquals(1, order.countMusicians());
    }

    @Test
    public void countsTracks() {
        Assert.assertEquals(2, order.countTracks());
    }
}

