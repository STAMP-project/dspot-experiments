package com.baeldung.staticdemo;


import Car.numberOfCars;
import org.junit.Assert;
import org.junit.Test;


public class CarIntegrationTest {
    @Test
    public void whenNumberOfCarObjectsInitialized_thenStaticCounterIncreases() {
        new Car("Jaguar", "V8");
        new Car("Bugatti", "W16");
        Assert.assertEquals(2, numberOfCars);
    }
}

