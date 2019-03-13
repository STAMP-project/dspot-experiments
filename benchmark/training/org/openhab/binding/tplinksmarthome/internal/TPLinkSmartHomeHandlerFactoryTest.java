/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tplinksmarthome.internal;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.eclipse.smarthome.core.thing.Thing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.tplinksmarthome.internal.device.BulbDevice;
import org.openhab.binding.tplinksmarthome.internal.device.DimmerDevice;
import org.openhab.binding.tplinksmarthome.internal.device.EnergySwitchDevice;
import org.openhab.binding.tplinksmarthome.internal.device.RangeExtenderDevice;
import org.openhab.binding.tplinksmarthome.internal.device.SwitchDevice;
import org.openhab.binding.tplinksmarthome.internal.handler.SmartHomeHandler;

import static TPLinkSmartHomeBindingConstants.BINDING_ID;


/**
 * Test class for {@link TPLinkSmartHomeHandlerFactory}.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
@RunWith(Parameterized.class)
public class TPLinkSmartHomeHandlerFactoryTest {
    private static final String SMART_HOME_DEVICE_FIELD = "smartHomeDevice";

    private final TPLinkSmartHomeHandlerFactory factory = new TPLinkSmartHomeHandlerFactory();

    // @formatter:off
    private static final List<Object[]> TESTS = Arrays.asList(new Object[][]{ new Object[]{ "hs100", SwitchDevice.class }, new Object[]{ "hs110", EnergySwitchDevice.class }, new Object[]{ "hs200", SwitchDevice.class }, new Object[]{ "hs220", DimmerDevice.class }, new Object[]{ "lb100", BulbDevice.class }, new Object[]{ "lb120", BulbDevice.class }, new Object[]{ "lb130", BulbDevice.class }, new Object[]{ "lb230", BulbDevice.class }, new Object[]{ "kl110", BulbDevice.class }, new Object[]{ "kl120", BulbDevice.class }, new Object[]{ "kl130", BulbDevice.class }, new Object[]{ "re270", RangeExtenderDevice.class }, new Object[]{ "unknown", null } });

    // @formatter:on
    @Mock
    Thing thing;

    private final String name;

    private final Class<?> clazz;

    public TPLinkSmartHomeHandlerFactoryTest(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Test
    public void testCorrectClass() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Mockito.when(thing.getThingTypeUID()).thenReturn(new org.eclipse.smarthome.core.thing.ThingTypeUID(BINDING_ID, name));
        SmartHomeHandler handler = ((SmartHomeHandler) (factory.createHandler(thing)));
        if ((clazz) == null) {
            Assert.assertNull(((name) + " should not return any handler but null"), handler);
        } else {
            Assert.assertNotNull(((name) + " should no return null handler"), handler);
            Field smartHomeDeviceField = SmartHomeHandler.class.getDeclaredField(TPLinkSmartHomeHandlerFactoryTest.SMART_HOME_DEVICE_FIELD);
            smartHomeDeviceField.setAccessible(true);
            Assert.assertSame(((name) + " should return expected device class"), clazz, smartHomeDeviceField.get(handler).getClass());
        }
    }
}

