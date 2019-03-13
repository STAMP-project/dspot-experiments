/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal;


import LightwaveRfItemDirection.IN_AND_OUT;
import LightwaveRfItemDirection.IN_ONLY;
import LightwaveRfItemDirection.OUT_ONLY;
import LightwaveRfType.DIMMER;
import LightwaveRfType.HEATING_BATTERY;
import LightwaveRfType.HEATING_CURRENT_TEMP;
import LightwaveRfType.SWITCH;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.SwitchItem;


public class LightwaveRfGenericBindingProviderTest {
    private static final String context = "";

    @Test
    public void testProcessBindingConfigurationForDimmer() throws Exception {
        LightwaveRfGenericBindingProvider bingindProvider = new LightwaveRfGenericBindingProvider();
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new DimmerItem("MyDimmer"), "room=1,device=2,type=DIMMER");
        Assert.assertEquals(Arrays.asList("MyDimmer"), bingindProvider.getBindingItemsForRoomDevice("1", "2"));
        Assert.assertEquals(Arrays.asList("MyDimmer"), bingindProvider.getItemNames());
        Assert.assertEquals("1", bingindProvider.getRoomId("MyDimmer"));
        Assert.assertEquals("2", bingindProvider.getDeviceId("MyDimmer"));
        Assert.assertEquals(DIMMER, bingindProvider.getTypeForItemName("MyDimmer"));
        Assert.assertEquals(IN_AND_OUT, bingindProvider.getDirection("MySwitch"));
    }

    @Test
    public void testProcessBindingConfiguratiLionForInOnly() throws Exception {
        LightwaveRfGenericBindingProvider bingindProvider = new LightwaveRfGenericBindingProvider();
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new SwitchItem("MySwitch"), "<room=3,device=4,type=SWITCH");
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getBindingItemsForRoomDevice("3", "4"));
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getItemNames());
        Assert.assertEquals("3", bingindProvider.getRoomId("MySwitch"));
        Assert.assertEquals("4", bingindProvider.getDeviceId("MySwitch"));
        Assert.assertEquals(SWITCH, bingindProvider.getTypeForItemName("MySwitch"));
        Assert.assertEquals(IN_ONLY, bingindProvider.getDirection("MySwitch"));
    }

    @Test
    public void testProcessBindingConfiguratiLionForOutOnly() throws Exception {
        LightwaveRfGenericBindingProvider bingindProvider = new LightwaveRfGenericBindingProvider();
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new SwitchItem("MySwitch"), ">room=3,device=4,type=SWITCH");
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getBindingItemsForRoomDevice("3", "4"));
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getItemNames());
        Assert.assertEquals("3", bingindProvider.getRoomId("MySwitch"));
        Assert.assertEquals("4", bingindProvider.getDeviceId("MySwitch"));
        Assert.assertEquals(SWITCH, bingindProvider.getTypeForItemName("MySwitch"));
        Assert.assertEquals(OUT_ONLY, bingindProvider.getDirection("MySwitch"));
    }

    @Test
    public void testProcessBindingConfigurationForSwitch() throws Exception {
        LightwaveRfGenericBindingProvider bingindProvider = new LightwaveRfGenericBindingProvider();
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new SwitchItem("MySwitch"), "room=3,device=4,type=SWITCH");
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getBindingItemsForRoomDevice("3", "4"));
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getItemNames());
        Assert.assertEquals("3", bingindProvider.getRoomId("MySwitch"));
        Assert.assertEquals("4", bingindProvider.getDeviceId("MySwitch"));
        Assert.assertEquals(SWITCH, bingindProvider.getTypeForItemName("MySwitch"));
    }

    @Test
    public void testProcessBindingConfigurationForHeatingBattery() throws Exception {
        LightwaveRfGenericBindingProvider bingindProvider = new LightwaveRfGenericBindingProvider();
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new NumberItem("MyBattery"), "room=3,device=4,type=HEATING_BATTERY");
        Assert.assertEquals(Arrays.asList("MyBattery"), bingindProvider.getBindingItemsForRoomDevice("3", "4"));
        Assert.assertEquals(Arrays.asList("MyBattery"), bingindProvider.getItemNames());
        Assert.assertEquals("3", bingindProvider.getRoomId("MyBattery"));
        Assert.assertEquals("4", bingindProvider.getDeviceId("MyBattery"));
        Assert.assertEquals(HEATING_BATTERY, bingindProvider.getTypeForItemName("MyBattery"));
    }

    @Test
    public void testRealLifeConfigurationForHeatingBattery() throws Exception {
        LightwaveRfGenericBindingProvider bingindProvider = new LightwaveRfGenericBindingProvider();
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new NumberItem("MyBattery"), "room=3,device=4,type=HEATING_BATTERY");
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new NumberItem("MyCurrentTemp"), "room=3,device=4,type=HEATING_CURRENT_TEMP");
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new DimmerItem("MyDimmer"), "room=1,device=2,type=DIMMER");
        bingindProvider.processBindingConfiguration(LightwaveRfGenericBindingProviderTest.context, new SwitchItem("MySwitch"), "room=3,device=3,type=SWITCH");
        List<String> expectedNames = Arrays.asList("MyBattery", "MyDimmer", "MySwitch", "MyCurrentTemp");
        Collection<String> itemNames = bingindProvider.getItemNames();
        Assert.assertTrue((((expectedNames.size()) == (itemNames.size())) && (expectedNames.containsAll(itemNames))));
        Assert.assertEquals(Arrays.asList("MySwitch"), bingindProvider.getBindingItemsForRoomDevice("3", "3"));
        Assert.assertEquals("3", bingindProvider.getRoomId("MySwitch"));
        Assert.assertEquals("3", bingindProvider.getDeviceId("MySwitch"));
        Assert.assertEquals(SWITCH, bingindProvider.getTypeForItemName("MySwitch"));
        Assert.assertEquals(Arrays.asList("MyDimmer"), bingindProvider.getBindingItemsForRoomDevice("1", "2"));
        Assert.assertEquals("1", bingindProvider.getRoomId("MyDimmer"));
        Assert.assertEquals("2", bingindProvider.getDeviceId("MyDimmer"));
        Assert.assertEquals(DIMMER, bingindProvider.getTypeForItemName("MyDimmer"));
        Assert.assertEquals(Arrays.asList("MyBattery", "MyCurrentTemp"), bingindProvider.getBindingItemsForRoomDevice("3", "4"));
        Assert.assertEquals("3", bingindProvider.getRoomId("MyBattery"));
        Assert.assertEquals("4", bingindProvider.getDeviceId("MyBattery"));
        Assert.assertEquals(HEATING_BATTERY, bingindProvider.getTypeForItemName("MyBattery"));
        Assert.assertEquals("3", bingindProvider.getRoomId("MyCurrentTemp"));
        Assert.assertEquals("4", bingindProvider.getDeviceId("MyCurrentTemp"));
        Assert.assertEquals(HEATING_CURRENT_TEMP, bingindProvider.getTypeForItemName("MyCurrentTemp"));
    }
}

