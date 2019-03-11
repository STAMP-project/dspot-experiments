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
package org.openhab.binding.tradfri;


import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ManagedThingProvider;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.eclipse.smarthome.test.storage.VolatileStorageService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases for {@link TradfriGatewayHandler}.
 *
 * @author Kai Kreuzer - Initial contribution
 */
public class TradfriHandlerTest extends JavaOSGiTest {
    private ManagedThingProvider managedThingProvider;

    private VolatileStorageService volatileStorageService = new VolatileStorageService();

    private Bridge bridge;

    private Thing thing;

    @Test
    public void creationOfTradfriGatewayHandler() {
        Assert.assertThat(bridge.getHandler(), Is.is(CoreMatchers.nullValue()));
        managedThingProvider.add(bridge);
        waitForAssert(() -> assertThat(bridge.getHandler(), notNullValue()));
        configurationOfTradfriGatewayHandler();
    }

    @Test
    public void creationOfTradfriLightHandler() {
        Assert.assertThat(thing.getHandler(), Is.is(CoreMatchers.nullValue()));
        managedThingProvider.add(bridge);
        managedThingProvider.add(thing);
        waitForAssert(() -> assertThat(thing.getHandler(), notNullValue()));
        configurationOfTradfriLightHandler();
    }
}

