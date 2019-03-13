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
package org.openhab.binding.tradfri.discovery;


import DiscoveryResultFlag.NEW;
import Thing.PROPERTY_FIRMWARE_VERSION;
import Thing.PROPERTY_VENDOR;
import javax.jmdns.ServiceInfo;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link TradfriDiscoveryParticipant}.
 *
 * @author Kai Kreuzer - Initial contribution
 */
public class TradfriDiscoveryParticipantOSGITest extends JavaOSGiTest {
    private MDNSDiscoveryParticipant discoveryParticipant;

    @Mock
    private ServiceInfo tradfriGateway;

    @Mock
    private ServiceInfo otherDevice;

    @Test
    public void correctSupportedTypes() {
        Assert.assertThat(discoveryParticipant.getSupportedThingTypeUIDs().size(), CoreMatchers.is(1));
        Assert.assertThat(discoveryParticipant.getSupportedThingTypeUIDs().iterator().next(), CoreMatchers.is(GATEWAY_TYPE_UID));
    }

    @Test
    public void correctThingUID() {
        Mockito.when(tradfriGateway.getName()).thenReturn("gw:12-34-56-78-90-ab");
        Assert.assertThat(discoveryParticipant.getThingUID(tradfriGateway), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        Mockito.when(tradfriGateway.getName()).thenReturn("gw:1234567890ab");
        Assert.assertThat(discoveryParticipant.getThingUID(tradfriGateway), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        Mockito.when(tradfriGateway.getName()).thenReturn("gw-12-34-56-78-90-ab");
        Assert.assertThat(discoveryParticipant.getThingUID(tradfriGateway), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        Mockito.when(tradfriGateway.getName()).thenReturn("gw:1234567890ab");
        Assert.assertThat(discoveryParticipant.getThingUID(tradfriGateway), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        Mockito.when(tradfriGateway.getName()).thenReturn("gw:1234567890abServiceInfo");
        Assert.assertThat(discoveryParticipant.getThingUID(tradfriGateway), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        Mockito.when(tradfriGateway.getName()).thenReturn("gw:12-34-56-78-90-ab-service-info");
        Assert.assertThat(discoveryParticipant.getThingUID(tradfriGateway), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        // restore original value
        Mockito.when(tradfriGateway.getName()).thenReturn("gw:12-34-56-78-90-ab");
    }

    @Test
    public void validDiscoveryResult() {
        DiscoveryResult result = discoveryParticipant.createResult(tradfriGateway);
        Assert.assertNotNull(result);
        Assert.assertThat(result.getProperties().get(PROPERTY_FIRMWARE_VERSION), CoreMatchers.is("1.1"));
        Assert.assertThat(result.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(result.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:gateway:gw1234567890ab")));
        Assert.assertThat(result.getThingTypeUID(), CoreMatchers.is(GATEWAY_TYPE_UID));
        Assert.assertThat(result.getBridgeUID(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(result.getProperties().get(PROPERTY_VENDOR), CoreMatchers.is("IKEA of Sweden"));
        Assert.assertThat(result.getProperties().get(GATEWAY_CONFIG_HOST), CoreMatchers.is("192.168.0.5"));
        Assert.assertThat(result.getProperties().get(GATEWAY_CONFIG_PORT), CoreMatchers.is(1234));
        Assert.assertThat(result.getRepresentationProperty(), CoreMatchers.is(GATEWAY_CONFIG_HOST));
    }

    @Test
    public void noThingUIDForUnknownDevice() {
        Mockito.when(otherDevice.getName()).thenReturn("something");
        Assert.assertThat(discoveryParticipant.getThingUID(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
        Mockito.when(otherDevice.getName()).thenReturn("gw_1234567890ab");
        Assert.assertThat(discoveryParticipant.getThingUID(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
        Mockito.when(otherDevice.getName()).thenReturn("gw:12-3456--7890-ab");
        Assert.assertThat(discoveryParticipant.getThingUID(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
        Mockito.when(otherDevice.getName()).thenReturn("gw1234567890ab");
        Assert.assertThat(discoveryParticipant.getThingUID(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
        // restore original value
        Mockito.when(otherDevice.getName()).thenReturn("something");
    }

    @Test
    public void noDiscoveryResultForUnknownDevice() {
        Assert.assertThat(discoveryParticipant.createResult(otherDevice), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

