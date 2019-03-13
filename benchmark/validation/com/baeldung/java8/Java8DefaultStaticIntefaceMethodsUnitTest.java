package com.baeldung.java8;


import com.baeldung.java_8_features.Vehicle;
import com.baeldung.java_8_features.VehicleImpl;
import org.junit.Assert;
import org.junit.Test;


public class Java8DefaultStaticIntefaceMethodsUnitTest {
    @Test
    public void callStaticInterfaceMethdosMethods_whenExpectedResults_thenCorrect() {
        Vehicle vehicle = new VehicleImpl();
        String overview = vehicle.getOverview();
        long[] startPosition = vehicle.startPosition();
        Assert.assertEquals(overview, "ATV made by N&F Vehicles");
        Assert.assertEquals(startPosition[0], 23);
        Assert.assertEquals(startPosition[1], 15);
    }

    @Test
    public void callDefaultInterfaceMethods_whenExpectedResults_thenCorrect() {
        String producer = Vehicle.producer();
        Assert.assertEquals(producer, "N&F Vehicles");
    }
}

