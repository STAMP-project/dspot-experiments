package com.baeldung.objects;


import org.junit.Assert;
import org.junit.Test;


public class CarUnitTest {
    private Car car;

    @Test
    public final void when_speedIncreased_then_verifySpeed() {
        car.increaseSpeed(30);
        Assert.assertEquals(30, car.getSpeed());
        car.increaseSpeed(20);
        Assert.assertEquals(50, car.getSpeed());
    }

    @Test
    public final void when_speedDecreased_then_verifySpeed() {
        car.increaseSpeed(50);
        Assert.assertEquals(50, car.getSpeed());
        car.decreaseSpeed(30);
        Assert.assertEquals(20, car.getSpeed());
        car.decreaseSpeed(20);
        Assert.assertEquals(0, car.getSpeed());
    }
}

