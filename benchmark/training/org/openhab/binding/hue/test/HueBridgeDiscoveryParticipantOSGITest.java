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
package org.openhab.binding.hue.test;


import DiscoveryResultFlag.NEW;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.upnp.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.jupnp.model.meta.RemoteDevice;


/**
 * Tests for {@link HueBridgeDiscoveryParticipant}.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Thomas H?fer - Added representation
 * @author Markus Rathgeb - migrated to plain Java test
 */
public class HueBridgeDiscoveryParticipantOSGITest extends JavaOSGiTest {
    UpnpDiscoveryParticipant discoveryParticipant;

    RemoteDevice hueDevice;

    RemoteDevice otherDevice;

    @Test
    public void correctSupportedTypes() {
        Assert.assertThat(discoveryParticipant.getSupportedThingTypeUIDs().size(), CoreMatchers.is(1));
        Assert.assertThat(discoveryParticipant.getSupportedThingTypeUIDs().iterator().next(), CoreMatchers.is(THING_TYPE_BRIDGE));
    }

    @Test
    public void correctThingUID() {
        Assert.assertThat(discoveryParticipant.getThingUID(hueDevice), CoreMatchers.is(new ThingUID("hue:bridge:serial123")));
    }

    @Test
    public void validDiscoveryResult() {
        final DiscoveryResult result = discoveryParticipant.createResult(hueDevice);
        Assert.assertThat(result.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(result.getThingUID(), CoreMatchers.is(new ThingUID("hue:bridge:serial123")));
        Assert.assertThat(result.getThingTypeUID(), CoreMatchers.is(THING_TYPE_BRIDGE));
        Assert.assertThat(result.getBridgeUID(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(result.getProperties().get(HOST), CoreMatchers.is("1.2.3.4"));
        Assert.assertThat(result.getProperties().get(PROPERTY_SERIAL_NUMBER), CoreMatchers.is("serial123"));
        Assert.assertThat(result.getRepresentationProperty(), CoreMatchers.is(PROPERTY_SERIAL_NUMBER));
    }

    @Test
    public void noThingUIDForUnknownDevice() {
        Assert.assertThat(discoveryParticipant.getThingUID(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void noDiscoveryResultForUnknownDevice() {
        Assert.assertThat(discoveryParticipant.createResult(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

