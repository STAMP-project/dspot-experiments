/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  GpioPinDigitalOutputTests.java
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


import PinDirection.OUT;
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
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import com.pi4j.io.gpio.exception.InvalidPinException;
import com.pi4j.io.gpio.exception.UnsupportedPinModeException;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class GpioPinDigitalOutputTests {
    private static GpioController gpio;

    private static GpioPinDigitalOutput pin;

    @Test
    public void testPinProvisioned() {
        // make sure that pin is provisioned
        Collection<GpioPin> pins = GpioPinDigitalOutputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinDigitalOutputTests.pin));
    }

    @Test(expected = GpioPinExistsException.class)
    public void testPinDuplicatePovisioning() {
        // make sure that pin cannot be provisioned a second time
        GpioPinDigitalOutputTests.gpio.provisionDigitalOutputPin(MockPin.DIGITAL_OUTPUT_PIN, "digitalOutputPin", LOW);
    }

    @Test(expected = UnsupportedPinModeException.class)
    public void testPinInvalidModePovisioning() {
        // make sure that pin cannot be provisioned that does not support DIGITAL OUTPUT
        GpioPinDigitalOutputTests.gpio.provisionDigitalOutputPin(MockPin.DIGITAL_INPUT_PIN, "analogOutputPin");
    }

    @Test(expected = InvalidPinException.class)
    public void testInvalidPin() {
        // attempt to export a pin that is not supported by the GPIO provider
        GpioPinDigitalOutputTests.pin.getProvider().export(GPIO_01, DIGITAL_OUTPUT);
    }

    @Test
    public void testPinProvider() {
        // verify pin provider
        Assert.assertTrue(((GpioPinDigitalOutputTests.pin.getProvider()) instanceof MockGpioProvider));
    }

    @Test
    public void testPinExport() {
        // verify is exported
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isExported());
    }

    @Test
    public void testPinInstance() {
        // verify pin instance
        Assert.assertEquals(MockPin.DIGITAL_OUTPUT_PIN, GpioPinDigitalOutputTests.pin.getPin());
    }

    @Test
    public void testPinAddress() {
        // verify pin address
        Assert.assertEquals(MockPin.DIGITAL_OUTPUT_PIN.getAddress(), GpioPinDigitalOutputTests.pin.getPin().getAddress());
    }

    @Test
    public void testPinName() {
        // verify pin name
        Assert.assertEquals("digitalOutputPin", GpioPinDigitalOutputTests.pin.getName());
    }

    @Test
    public void testPinMode() {
        // verify pin mode
        Assert.assertEquals(DIGITAL_OUTPUT, GpioPinDigitalOutputTests.pin.getMode());
    }

    @Test
    public void testPinValidSupportedMode() {
        // verify valid pin mode
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_OUTPUT));
    }

    @Test
    public void testPinInvalidSupportedMode() {
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalOutputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_INPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalOutputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_OUTPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalOutputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_INPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinDigitalOutputTests.pin.getPin().getSupportedPinModes().contains(PWM_OUTPUT));
    }

    @Test
    public void testPinDirection() {
        // verify pin direction
        Assert.assertEquals(OUT, GpioPinDigitalOutputTests.pin.getMode().getDirection());
    }

    @Test
    public void testPinInitialState() {
        // verify pin initial state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
        Assert.assertEquals(LOW, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinHiState() {
        GpioPinDigitalOutputTests.pin.setState(HIGH);
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        Assert.assertEquals(HIGH, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinLowState() {
        GpioPinDigitalOutputTests.pin.setState(LOW);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
        Assert.assertEquals(LOW, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinHiStateBoolean() {
        GpioPinDigitalOutputTests.pin.setState(true);
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        Assert.assertEquals(HIGH, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinLowStateBoolean() {
        GpioPinDigitalOutputTests.pin.setState(false);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
        Assert.assertEquals(LOW, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinHi() {
        GpioPinDigitalOutputTests.pin.high();
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        Assert.assertEquals(HIGH, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinLow() {
        GpioPinDigitalOutputTests.pin.low();
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
        Assert.assertEquals(LOW, GpioPinDigitalOutputTests.pin.getState());
    }

    @Test
    public void testPinToggle() {
        // set known start state
        GpioPinDigitalOutputTests.pin.low();
        // toggle hi
        GpioPinDigitalOutputTests.pin.toggle();
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        // toggle low
        GpioPinDigitalOutputTests.pin.toggle();
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
    }

    @Test
    public void testPinPulse() throws InterruptedException {
        // set known start state
        GpioPinDigitalOutputTests.pin.low();
        // pulse pin hi for 1/5 second
        GpioPinDigitalOutputTests.pin.pulse(200, HIGH);
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        // wait 1/2 second before continuing test
        Thread.sleep(500);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
    }

    @Test
    public void testPinBlink() throws InterruptedException {
        // set known start state
        GpioPinDigitalOutputTests.pin.low();
        // blink pin for 1 seconds with a blink rate of 1/5 second
        GpioPinDigitalOutputTests.pin.blink(200, 1000, HIGH);
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        // wait before continuing test
        Thread.sleep(250);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
        // wait before continuing test
        Thread.sleep(250);
        // verify pin hi state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isHigh());
        // wait before continuing test
        Thread.sleep(250);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
        // wait before continuing test
        Thread.sleep(500);
        // verify pin low state
        Assert.assertTrue(GpioPinDigitalOutputTests.pin.isLow());
    }

    @Test
    public void testPinUnexport() {
        // unexport pin
        GpioPinDigitalOutputTests.pin.unexport();
        // verify is not exported
        Assert.assertFalse(GpioPinDigitalOutputTests.pin.isExported());
    }

    @Test
    public void testPinUnprovision() {
        // make sure that pin is provisioned before we start
        Collection<GpioPin> pins = GpioPinDigitalOutputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinDigitalOutputTests.pin));
        // un-provision pin
        GpioPinDigitalOutputTests.gpio.unprovisionPin(GpioPinDigitalOutputTests.pin);
        // make sure that pin is no longer provisioned
        pins = GpioPinDigitalOutputTests.gpio.getProvisionedPins();
        Assert.assertFalse(pins.contains(GpioPinDigitalOutputTests.pin));
    }
}

