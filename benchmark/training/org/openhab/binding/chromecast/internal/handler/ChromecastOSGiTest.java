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
package org.openhab.binding.chromecast.internal.handler;


import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ManagedThingProvider;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.eclipse.smarthome.test.storage.VolatileStorageService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ChromecastHandler}.
 *
 * @author Fran?ois Pelsser, Wouter Born - Initial contribution
 */
public class ChromecastOSGiTest extends JavaOSGiTest {
    private ManagedThingProvider managedThingProvider;

    private VolatileStorageService volatileStorageService = new VolatileStorageService();

    @Test
    public void creationOfChromecastHandler() {
        ChromecastHandler handler = getService(ThingHandler.class, ChromecastHandler.class);
        Assert.assertThat(handler, Is.is(CoreMatchers.nullValue()));
        Configuration configuration = new Configuration();
        configuration.put(HOST, "hostname");
        Thing thing = ThingBuilder.create(THING_TYPE_CHROMECAST, "tv").withConfiguration(configuration).build();
        managedThingProvider.add(thing);
        waitForAssert(() -> assertThat(thing.getHandler(), notNullValue()));
        Assert.assertThat(thing.getConfiguration(), Is.is(CoreMatchers.notNullValue()));
        Assert.assertThat(thing.getHandler(), Is.is(CoreMatchers.instanceOf(ChromecastHandler.class)));
    }
}

