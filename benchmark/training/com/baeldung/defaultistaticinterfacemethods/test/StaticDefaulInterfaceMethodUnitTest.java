package com.baeldung.defaultistaticinterfacemethods.test;


import com.baeldung.defaultstaticinterfacemethods.model.Car;
import com.baeldung.defaultstaticinterfacemethods.model.Motorbike;
import com.baeldung.defaultstaticinterfacemethods.model.Vehicle;
import org.junit.Test;


public class StaticDefaulInterfaceMethodUnitTest {
    private static Car car;

    private static Motorbike motorbike;

    @Test
    public void givenCarInstace_whenBrandisBMW_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.car.getBrand()).isEqualTo("BMW");
    }

    @Test
    public void givenCarInstance_whenCallingSpeedUp_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.car.speedUp()).isEqualTo("The car is speeding up.");
    }

    @Test
    public void givenCarInstance_whenCallingSlowDown_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.car.slowDown()).isEqualTo("The car is slowing down.");
    }

    @Test
    public void givenCarInstance_whenCallingTurnAlarmOn_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.car.turnAlarmOn()).isEqualTo("Turning the vehice alarm on.");
    }

    @Test
    public void givenCarInstance_whenCallingTurnAlarmOff_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.car.turnAlarmOff()).isEqualTo("Turning the vehicle alarm off.");
    }

    @Test
    public void givenVehicleInterface_whenCallinggetHorsePower_thenOneAssertion() {
        assertThat(Vehicle.getHorsePower(2500, 480)).isEqualTo(228);
    }

    @Test
    public void givenMooorbikeInstace_whenBrandisYamaha_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.motorbike.getBrand()).isEqualTo("Yamaha");
    }

    @Test
    public void givenMotorbikeInstance_whenCallingSpeedUp_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.motorbike.speedUp()).isEqualTo("The motorbike is speeding up.");
    }

    @Test
    public void givenMotorbikeInstance_whenCallingSlowDown_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.motorbike.slowDown()).isEqualTo("The motorbike is slowing down.");
    }

    @Test
    public void givenMotorbikeInstance_whenCallingTurnAlarmOn_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.motorbike.turnAlarmOn()).isEqualTo("Turning the vehice alarm on.");
    }

    @Test
    public void givenMotorbikeInstance_whenCallingTurnAlarmOff_thenOneAssertion() {
        assertThat(StaticDefaulInterfaceMethodUnitTest.motorbike.turnAlarmOff()).isEqualTo("Turning the vehicle alarm off.");
    }
}

