package com.baeldung.methodoverloadingoverriding.test;


import com.baeldung.methodoverloadingoverriding.model.Car;
import com.baeldung.methodoverloadingoverriding.model.Vehicle;
import org.junit.Test;


public class MethodOverridingUnitTest {
    private static Vehicle vehicle;

    private static Car car;

    @Test
    public void givenVehicleInstance_whenCalledAccelerate_thenOneAssertion() {
        assertThat(MethodOverridingUnitTest.vehicle.accelerate(100)).isEqualTo("The vehicle accelerates at : 100 MPH.");
    }

    @Test
    public void givenVehicleInstance_whenCalledRun_thenOneAssertion() {
        assertThat(MethodOverridingUnitTest.vehicle.run()).isEqualTo("The vehicle is running.");
    }

    @Test
    public void givenVehicleInstance_whenCalledStop_thenOneAssertion() {
        assertThat(MethodOverridingUnitTest.vehicle.stop()).isEqualTo("The vehicle has stopped.");
    }

    @Test
    public void givenCarInstance_whenCalledAccelerate_thenOneAssertion() {
        assertThat(MethodOverridingUnitTest.car.accelerate(80)).isEqualTo("The car accelerates at : 80 MPH.");
    }

    @Test
    public void givenCarInstance_whenCalledRun_thenOneAssertion() {
        assertThat(MethodOverridingUnitTest.car.run()).isEqualTo("The vehicle is running.");
    }

    @Test
    public void givenCarInstance_whenCalledStop_thenOneAssertion() {
        assertThat(MethodOverridingUnitTest.car.stop()).isEqualTo("The vehicle has stopped.");
    }

    @Test
    public void givenVehicleCarInstances_whenCalledAccelerateWithSameArgument_thenNotEqual() {
        assertThat(MethodOverridingUnitTest.vehicle.accelerate(100)).isNotEqualTo(MethodOverridingUnitTest.car.accelerate(100));
    }

    @Test
    public void givenVehicleCarInstances_whenCalledRun_thenEqual() {
        assertThat(MethodOverridingUnitTest.vehicle.run()).isEqualTo(MethodOverridingUnitTest.car.run());
    }

    @Test
    public void givenVehicleCarInstances_whenCalledStop_thenEqual() {
        assertThat(MethodOverridingUnitTest.vehicle.stop()).isEqualTo(MethodOverridingUnitTest.car.stop());
    }
}

