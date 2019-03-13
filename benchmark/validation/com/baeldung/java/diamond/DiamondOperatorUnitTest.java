package com.baeldung.java.diamond;


import org.junit.Assert;
import org.junit.Test;


public class DiamondOperatorUnitTest {
    @Test
    public void whenCreateCarUsingDiamondOperator_thenSuccess() {
        Car<Diesel> myCar = new Car<>();
        Assert.assertNotNull(myCar);
    }
}

