package com.querydsl.collections;


import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


public class PathComparatorTest {
    private Comparator<Car> comparator;

    @Test
    public void equalReference() {
        Car car = new Car();
        Assert.assertEquals(0, comparator.compare(car, car));
    }

    @Test
    public void semanticallyEqual() {
        Car car = new Car();
        car.setModel("car");
        car.setHorsePower(50);
        Car similarCar = new Car();
        similarCar.setModel("car");
        similarCar.setHorsePower(50);
        Assert.assertEquals(0, comparator.compare(car, similarCar));
    }

    @Test
    public void leftIsNull() {
        Assert.assertEquals((-1), comparator.compare(null, new Car()));
    }

    @Test
    public void rightIsNull() {
        Assert.assertEquals(1, comparator.compare(new Car(), null));
    }

    @Test
    public void compareOnValue() {
        Car car = new Car();
        car.setHorsePower(50);
        Car betterCar = new Car();
        betterCar.setHorsePower(150);
        Assert.assertEquals((-1), comparator.compare(car, betterCar));
    }
}

