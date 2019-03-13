/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  GpioPinDigitalInputTests.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package com.pi4j.io.gpio.test;


import PinDirection.IN;
import PinMode.ANALOG_INPUT;
import PinMode.ANALOG_OUTPUT;
import PinMode.DIGITAL_INPUT;
import PinMode.DIGITAL_OUTPUT;
import PinMode.PWM_OUTPUT;
import PinState.HIGH;
import PinState.LOW;
import RaspiPin.GPIO_01;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import com.pi4j.io.gpio.exception.InvalidPinException;
import com.pi4j.io.gpio.exception.InvalidPinModeException;
import com.pi4j.io.gpio.exception.UnsupportedPinModeException;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class GpioPinDigitalInputTests {
    private static MockGpioProvider provider;

    private static GpioController gpio;

    private static GpioPinDigitalInput pin;

    private static PinState pinMonitoredState;

    @Test
    public void testPinProvisioned() {
        // make sure that pin is provisioned
        Collection<GpioPin> pins = GpioPinDigitalInputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinDigitalInputTests.pin));
    }

    @Test(expected = GpioPinExistsException.class)
    public void testPinDuplicatePovisioning() {
        // make sure that pin cannot be provisioned a second time
        GpioPinDigitalInputTests.gpio.provisionDigitalOutputPin(MockPin.DIGITAL_INPUT_PIN, "digitalInputPin", LOW);
    }

    @Test(expected = UnsupportedPinModeException.class)
    public void testPinInvalidModePovisioning() {
        // make sure that pin cannot be provisioned that does not support DIGITAL INPUT
        GpioPinDigitalInputTests.gpio.provisionDigitalInputPin(MockPin.ANALOG_INPUT_PIN, "digitalInputPin");
    }

    @Test(expected = InvalidPinException.class)
    public void testInvalidPin() {
        // attempt to export a pin that is not supported by the GPIO provider
        GpioPinDigitalInputTests.provider.export(GPIO_01, DIGITAL_INPUT);
    }

    @Test
    public void testPinProvider() {
        // verify pin mode
        Assert.assertEquals(GpioPinDigitalInputTests.provider, GpioPinDigitalInputTests.pin.getProvider());
    }

    @Test
    public void testPinExport() {
        // verify is exported
        Assert.assertTrue(GpioPinDigitalInputTests.pin.isExported());
    }

    @Test
    public void testPinInstance() {
        // verify pin instance
        Assert.assertEquals(MockPin.DIGITAL_INPUT_PIN, GpioPinDigitalInputTests.pin.getPin());
    }

    @Test
    public void testPinAddress() {
        // verify pin address
        Assert.assertEquals(MockPin.DIGITAL_INPUT_PIN.getAddress(), GpioPinDigitalInputTests.pin.getPin().getAddress());
    }

    @Test
    public void testPinName() {
        // verify pin name
        Assert.assertEquals("digitalInputPin", GpioPinDigitalInputTests.pin.getName());
    }

    @Test
    public void testPinMode() {
        // verify pin mode
        Assert.assertEquals(DIGITAL_INPUT, GpioPinDigitalInputTests.pin.getMode());
    }

    @Test
    public void testPinValidSupportedMode() {
        // verify valid pin mode
        Assert.assertTrue(GpioPinDigitalInputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_INPUT));
    }

    @Test
    public void testPinInvalidSupportedMode() {
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalInputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_OUTPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalInputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_OUTPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalInputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_INPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalInputTests.pin.getPin().getSupportedPinModes().contains(PWM_OUTPUT));
    }

    @Test
    public void testPinDirection() {
        // verify pin direction
        Assert.assertEquals(IN, GpioPinDigitalInputTests.pin.getMode().getDirection());
    }

    @Test
    public void testPinLowState() {
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, LOW);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalInputTests.pin.isLow());
        Assert.assertEquals(LOW, GpioPinDigitalInputTests.pin.getState());
    }

    @Test(expected = InvalidPinModeException.class)
    public void testPinInvalidSetHiState() {
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setState(MockPin.DIGITAL_INPUT_PIN, HIGH);
    }

    @Test(expected = InvalidPinModeException.class)
    public void testPinInvalidSetLowState() {
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setState(MockPin.DIGITAL_INPUT_PIN, LOW);
    }

    @Test
    public void testPinUnexport() {
        // unexport pin
        GpioPinDigitalInputTests.pin.unexport();
        // verify is not exported
        Assert.assertFalse(GpioPinDigitalInputTests.pin.isExported());
    }

    @Test
    public void testPinLowEvent() throws InterruptedException {
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, HIGH);
        // wait 1/100 second before continuing test
        Thread.sleep(10);
        // reset pin monitoring variable
        GpioPinDigitalInputTests.pinMonitoredState = null;
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, LOW);
        // wait 1/100 second before continuing test
        Thread.sleep(10);
        // verify pin low state
        Assert.assertEquals(LOW, GpioPinDigitalInputTests.pinMonitoredState);
    }

    @Test
    public void testPinHiEvent() throws InterruptedException {
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, LOW);
        // wait 1/100 second before continuing test
        Thread.sleep(10);
        // reset pin monitoring variable
        GpioPinDigitalInputTests.pinMonitoredState = null;
        // explicit mock set on the mock provider
        GpioPinDigitalInputTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, HIGH);
        // wait 1/100 second before continuing test
        Thread.sleep(10);
        // verify pin hi state
        Assert.assertEquals(HIGH, GpioPinDigitalInputTests.pinMonitoredState);
    }

    @Test
    public void testPinUnprovision() {
        // make sure that pin is provisioned before we start
        Collection<GpioPin> pins = GpioPinDigitalInputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinDigitalInputTests.pin));
        // un-provision pin
        GpioPinDigitalInputTests.gpio.unprovisionPin(GpioPinDigitalInputTests.pin);
        // make sure that pin is no longer provisioned
        pins = GpioPinDigitalInputTests.gpio.getProvisionedPins();
        Assert.assertFalse(pins.contains(GpioPinDigitalInputTests.pin));
    }
}

