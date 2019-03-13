/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  TmpDS18S20Test.java
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
package com.pi4j.component.temperature.impl;


import TmpDS18S20DeviceType.FAMILY_CODE;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Peter Schuebl
 */
public class TmpDS18S20Test {
    private W1Master master;

    @Test
    public void testDevices() {
        // System.out.println(master.toString());
        final List<W1Device> devices = master.getDevices(FAMILY_CODE);
        Assert.assertEquals(2, devices.size());
        for (W1Device device : devices) {
            // System.out.println(((TemperatureSensor) device).getTemperature());
            Assert.assertEquals((-1.3), getTemperature(), 0.01);
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() throws Exception {
        final W1Device w1Devicea1 = createDevice("10-000801d54852");
        final W1Device w1Devicea2 = createDevice("10-000801d54852");
        Assert.assertTrue(w1Devicea1.equals(w1Devicea2));
        final W1Device w1Deviceb = createDevice("10-000801d54853");
        Assert.assertFalse(w1Devicea1.equals(w1Deviceb));
        Assert.assertFalse(w1Devicea1.equals(null));
        Assert.assertFalse(w1Devicea1.equals("123"));
    }

    @Test
    public void testHashCode() throws Exception {
        final W1Device w1Devicea1 = createDevice("10-000801d54852");
        final W1Device w1Devicea2 = createDevice("10-000801d54852");
        Assert.assertEquals(w1Devicea1.hashCode(), w1Devicea2.hashCode());
        final W1Device w1Deviceb = createDevice("10-000801d54853");
        Assert.assertNotEquals(w1Devicea1.hashCode(), w1Deviceb.hashCode());
    }
}

