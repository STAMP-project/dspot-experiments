/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  GpioPinAnalogOutputTests.java
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
import RaspiPin.GPIO_00;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinAnalogOutput;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import com.pi4j.io.gpio.exception.InvalidPinException;
import com.pi4j.io.gpio.exception.UnsupportedPinModeException;
import java.util.Collection;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class GpioPinAnalogOutputTests {
    private static GpioController gpio;

    private static GpioPinAnalogOutput pin;

    @Test
    public void testPinProvisioned() {
        // make sure that pin is provisioned
        Collection<GpioPin> pins = GpioPinAnalogOutputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinAnalogOutputTests.pin));
    }

    @Test(expected = GpioPinExistsException.class)
    public void testPinDuplicatePovisioning() {
        // make sure that pin cannot be provisioned a second time
        GpioPinAnalogOutputTests.gpio.provisionAnalogOutputPin(MockPin.ANALOG_OUTPUT_PIN, "analogOutputPin");
    }

    @Test(expected = UnsupportedPinModeException.class)
    public void testPinInvalidModePovisioning() {
        // make sure that pin cannot be provisioned that does not support ANALOG OUTPUT
        GpioPinAnalogOutputTests.gpio.provisionAnalogOutputPin(MockPin.DIGITAL_OUTPUT_PIN, "digitalOutputPin");
    }

    @Test(expected = InvalidPinException.class)
    public void testInvalidPin() {
        // attempt to export a pin that is not supported by the GPIO provider
        GpioPinAnalogOutputTests.pin.getProvider().export(GPIO_00, ANALOG_OUTPUT);
    }

    @Test
    public void testPinProvider() {
        // verify pin provider
        Assert.assertTrue(((GpioPinAnalogOutputTests.pin.getProvider()) instanceof MockGpioProvider));
    }

    @Test
    public void testPinExport() {
        // verify is exported
        Assert.assertTrue(GpioPinAnalogOutputTests.pin.isExported());
    }

    @Test
    public void testPinInstance() {
        // verify pin instance
        Assert.assertEquals(MockPin.ANALOG_OUTPUT_PIN, GpioPinAnalogOutputTests.pin.getPin());
    }

    @Test
    public void testPinAddress() {
        // verify pin address
        Assert.assertEquals(MockPin.ANALOG_OUTPUT_PIN.getAddress(), GpioPinAnalogOutputTests.pin.getPin().getAddress());
    }

    @Test
    public void testPinName() {
        // verify pin name
        Assert.assertEquals("analogOutputPin", GpioPinAnalogOutputTests.pin.getName());
    }

    @Test
    public void testPinMode() {
        // verify pin mode
        Assert.assertEquals(GpioPinAnalogOutputTests.pin.getMode(), ANALOG_OUTPUT);
    }

    @Test
    public void testPinValidSupportedMode() {
        // verify valid pin mode
        Assert.assertTrue(GpioPinAnalogOutputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_OUTPUT));
    }

    @Test
    public void testPinInvalidSupportedMode() {
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogOutputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_INPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogOutputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_OUTPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogOutputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_INPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogOutputTests.pin.getPin().getSupportedPinModes().contains(PWM_OUTPUT));
    }

    @Test
    public void testPinDirection() {
        // verify pin direction
        Assert.assertEquals(OUT, GpioPinAnalogOutputTests.pin.getMode().getDirection());
    }

    @Test
    public void testPinInitialValue() {
        // verify pin initial state
        Assert.assertTrue(((GpioPinAnalogOutputTests.pin.getValue()) == 3.1416));
    }

    @Test
    public void testPinSetValue() {
        Random generator = new Random();
        // test ten random numbers
        for (int index = 0; index < 10; index++) {
            double newValue = generator.nextDouble();
            // explicit mock set on the mock provider
            GpioPinAnalogOutputTests.pin.setValue(newValue);
            // verify pin value
            Assert.assertTrue(((GpioPinAnalogOutputTests.pin.getValue()) == newValue));
        }
    }

    @Test
    public void testPinUnexport() {
        // unexport pin
        GpioPinAnalogOutputTests.pin.unexport();
        // verify is not exported
        Assert.assertFalse(GpioPinAnalogOutputTests.pin.isExported());
    }

    @Test
    public void testPinUnprovision() {
        // make sure that pin is provisioned before we start
        Collection<GpioPin> pins = GpioPinAnalogOutputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinAnalogOutputTests.pin));
        // un-provision pin
        GpioPinAnalogOutputTests.gpio.unprovisionPin(GpioPinAnalogOutputTests.pin);
        // make sure that pin is no longer provisioned
        pins = GpioPinAnalogOutputTests.gpio.getProvisionedPins();
        Assert.assertFalse(pins.contains(GpioPinAnalogOutputTests.pin));
    }
}

