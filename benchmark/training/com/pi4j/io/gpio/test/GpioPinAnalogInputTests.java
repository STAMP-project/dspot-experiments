/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  GpioPinAnalogInputTests.java
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
import RaspiPin.GPIO_01;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import com.pi4j.io.gpio.exception.InvalidPinException;
import com.pi4j.io.gpio.exception.InvalidPinModeException;
import com.pi4j.io.gpio.exception.UnsupportedPinModeException;
import java.util.Collection;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class GpioPinAnalogInputTests {
    private static MockGpioProvider provider;

    private static GpioController gpio;

    private static GpioPinAnalogInput pin;

    private static Double pinMonitoredValue;

    private static int eventCounter;

    @Test
    public void testPinProvisioned() {
        // make sure that pin is provisioned
        Collection<GpioPin> pins = GpioPinAnalogInputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinAnalogInputTests.pin));
    }

    @Test(expected = GpioPinExistsException.class)
    public void testPinDuplicatePovisioning() {
        // make sure that pin cannot be provisioned a second time
        GpioPinAnalogInputTests.gpio.provisionAnalogOutputPin(MockPin.ANALOG_INPUT_PIN, "analogInputPin");
    }

    @Test(expected = UnsupportedPinModeException.class)
    public void testPinInvalidModePovisioning() {
        // make sure that pin cannot be provisioned that does not support ANALOG INPUT
        GpioPinAnalogInputTests.gpio.provisionAnalogInputPin(MockPin.DIGITAL_INPUT_PIN, "digitalInputPin");
    }

    @Test(expected = InvalidPinException.class)
    public void testInvalidPin() {
        // attempt to export a pin that is not supported by the GPIO provider
        GpioPinAnalogInputTests.provider.export(GPIO_01, ANALOG_INPUT);
    }

    @Test
    public void testPinProvider() {
        // verify pin mode
        Assert.assertEquals(GpioPinAnalogInputTests.provider, GpioPinAnalogInputTests.pin.getProvider());
    }

    @Test
    public void testPinExport() {
        // verify is exported
        Assert.assertTrue(GpioPinAnalogInputTests.pin.isExported());
    }

    @Test
    public void testPinInstance() {
        // verify pin instance
        Assert.assertEquals(MockPin.ANALOG_INPUT_PIN, GpioPinAnalogInputTests.pin.getPin());
    }

    @Test
    public void testPinAddress() {
        // verify pin address
        Assert.assertEquals(MockPin.ANALOG_INPUT_PIN.getAddress(), GpioPinAnalogInputTests.pin.getPin().getAddress());
    }

    @Test
    public void testPinName() {
        // verify pin name
        Assert.assertEquals("analogInputPin", GpioPinAnalogInputTests.pin.getName());
    }

    @Test
    public void testPinMode() {
        // verify pin mode
        Assert.assertEquals(ANALOG_INPUT, GpioPinAnalogInputTests.pin.getMode());
    }

    @Test
    public void testPinValidSupportedMode() {
        // verify valid pin mode
        Assert.assertTrue(GpioPinAnalogInputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_INPUT));
    }

    @Test
    public void testPinInvalidSupportedMode() {
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogInputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_OUTPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogInputTests.pin.getPin().getSupportedPinModes().contains(ANALOG_OUTPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogInputTests.pin.getPin().getSupportedPinModes().contains(DIGITAL_INPUT));
        // verify invalid pin mode
        Assert.assertFalse(GpioPinAnalogInputTests.pin.getPin().getSupportedPinModes().contains(PWM_OUTPUT));
    }

    @Test
    public void testPinDirection() {
        // verify pin direction
        Assert.assertEquals(IN, GpioPinAnalogInputTests.pin.getMode().getDirection());
    }

    @Test(expected = InvalidPinModeException.class)
    public void testPinInvalidSetState() {
        // explicit mock set on the mock provider
        GpioPinAnalogInputTests.provider.setState(MockPin.ANALOG_INPUT_PIN, HIGH);
    }

    @Test
    public void testPinUnexport() {
        // unexport pin
        GpioPinAnalogInputTests.pin.unexport();
        // verify is not exported
        Assert.assertFalse(GpioPinAnalogInputTests.pin.isExported());
    }

    @Test
    public void testPinValueEvent() throws InterruptedException {
        Random generator = new Random();
        // reset event counter
        GpioPinAnalogInputTests.eventCounter = 0;
        // test five events
        for (int index = 0; index < 5; index++) {
            double newValue = generator.nextDouble();
            // reset pin monitoring variable
            GpioPinAnalogInputTests.pinMonitoredValue = null;
            // explicit mock set on the mock provider
            GpioPinAnalogInputTests.provider.setMockAnalogValue(MockPin.ANALOG_INPUT_PIN, newValue);
            // wait 1/10 second before continuing test
            Thread.sleep(100);
            // verify pin value
            Assert.assertTrue(((GpioPinAnalogInputTests.pin.getValue()) == (GpioPinAnalogInputTests.pinMonitoredValue)));
        }
        // make sure we received the proper number of events
        Assert.assertEquals(5, GpioPinAnalogInputTests.eventCounter);
    }

    @Test
    public void testPinUnprovision() {
        // make sure that pin is provisioned before we start
        Collection<GpioPin> pins = GpioPinAnalogInputTests.gpio.getProvisionedPins();
        Assert.assertTrue(pins.contains(GpioPinAnalogInputTests.pin));
        // un-provision pin
        GpioPinAnalogInputTests.gpio.unprovisionPin(GpioPinAnalogInputTests.pin);
        // make sure that pin is no longer provisioned
        pins = GpioPinAnalogInputTests.gpio.getProvisionedPins();
        Assert.assertFalse(pins.contains(GpioPinAnalogInputTests.pin));
    }
}

