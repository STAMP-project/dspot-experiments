/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  W1MasterTest.java
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
package com.pi4j.io.w1;


import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Peter Schuebl
 */
public class W1MasterTest {
    private W1Master master;

    @Test
    public void shouldAtLeastContainADummyDevice() {
        final Collection<W1DeviceType> deviceTypes = master.getDeviceTypes();
        Assert.assertTrue("there should be at least one device", ((deviceTypes.size()) >= 1));
    }

    @Test
    public void thereShouldBeThreeDevices() {
        final List<String> deviceIDs = master.getDeviceIDs();
        Assert.assertEquals(3, deviceIDs.size());
        Assert.assertTrue(deviceIDs.contains("28-00000698ebb1"));
        Assert.assertTrue(deviceIDs.contains("28-00000698ebb2"));
    }

    @Test
    public void thereShouldBeADummyDevice() {
        final List<W1Device> devices = master.getDevices();
        Assert.assertEquals(1, devices.size());
        final W1Device dummyDevice = devices.get(0);
        Assert.assertTrue((dummyDevice instanceof W1DummyDeviceType.W1DummyDevice));
        Assert.assertEquals("FE-00000698ebb3 Dummy Device", dummyDevice.getName());
    }

    @Test
    public void testCheckDeviceChanges() throws URISyntaxException {
        W1MasterTest.W1MasterDummy master = new W1MasterTest.W1MasterDummy();
        Assert.assertEquals(0, getDevices().size());
        final W1MasterTest.DummyDevice d1 = new W1MasterTest.DummyDevice("1");
        final W1MasterTest.DummyDevice d2 = new W1MasterTest.DummyDevice("2");
        master.setReadDevices(Arrays.<W1Device>asList(d1, d2));
        Assert.assertEquals(0, getDevices().size());
        checkDeviceChanges();
        Assert.assertEquals(2, getDevices().size());
        final W1MasterTest.DummyDevice d3 = new W1MasterTest.DummyDevice("3");
        final W1MasterTest.DummyDevice d4 = new W1MasterTest.DummyDevice("4");
        master.setReadDevices(Arrays.<W1Device>asList(d1, d3, d4));
        checkDeviceChanges();
        List<W1Device> devices = master.getDevices();
        Assert.assertEquals(3, devices.size());
        Assert.assertTrue(devices.contains(d1));
        Assert.assertTrue(devices.contains(d3));
        Assert.assertTrue(devices.contains(d4));
        checkDeviceChanges();
        Assert.assertEquals(3, getDevices().size());
        master.setReadDevices(Collections.<W1Device>emptyList());
        checkDeviceChanges();
        Assert.assertEquals(0, getDevices().size());
    }

    @Test
    public void thereShouldBeNoDevicesForFamily28() {
        Assert.assertEquals(0, master.getDevices(40).size());
    }

    @Test
    public void thereShouldBeOneDummyDevicesForFamilyFE() {
        Assert.assertEquals(1, master.getDevices(W1DummyDeviceType.FAMILY_ID).size());
    }

    static class DummyDevice extends W1BaseDevice {
        public DummyDevice(String id) {
            super(new File(("FD-" + id)));
        }

        @Override
        public int getFamilyId() {
            return 253;
        }

        @Override
        public String toString() {
            return getId();
        }
    }

    static class W1MasterDummy extends W1Master {
        List<W1Device> readDevices = new ArrayList<>();

        public W1MasterDummy() {
            super("");
        }

        @SuppressWarnings("unchecked")
        @Override
        <T extends W1Device> List<T> readDevices() {
            if ((readDevices) == null) {
                // during construction
                readDevices = new ArrayList();
            }
            return ((List<T>) (readDevices));
        }

        public void setReadDevices(List<W1Device> readDevices) {
            this.readDevices = new ArrayList();
            this.readDevices.addAll(readDevices);
        }
    }
}

