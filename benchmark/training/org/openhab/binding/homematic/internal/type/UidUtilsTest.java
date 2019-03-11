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
package org.openhab.binding.homematic.internal.type;


import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.type.ChannelGroupTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openhab.binding.homematic.internal.model.HmChannel;
import org.openhab.binding.homematic.internal.model.HmDatapoint;
import org.openhab.binding.homematic.internal.model.HmDatapointInfo;
import org.openhab.binding.homematic.internal.model.HmDevice;
import org.openhab.binding.homematic.test.util.BridgeHelper;
import org.openhab.binding.homematic.test.util.DimmerHelper;


/**
 * Tests for {@link UidUtilsTest}.
 *
 * @author Florian Stolte - Initial Contribution
 */
public class UidUtilsTest {
    @Test
    public void testGeneratedThingTypeUIDHasExpectedFormat() {
        HmDevice hmDevice = DimmerHelper.createDimmerHmDevice();
        ThingTypeUID generatedThingTypeUID = UidUtils.generateThingTypeUID(hmDevice);
        MatcherAssert.assertThat(generatedThingTypeUID.getAsString(), CoreMatchers.is("homematic:HM-LC-Dim1-Pl3"));
    }

    @Test
    public void testGeneratedThingTypeUIDHasExpectedFormatForHomegear() {
        HmDevice hmDevice = DimmerHelper.createDimmerHmDevice("HOMEGEAR");
        ThingTypeUID generatedThingTypeUID = UidUtils.generateThingTypeUID(hmDevice);
        MatcherAssert.assertThat(generatedThingTypeUID.getAsString(), CoreMatchers.is("homematic:HG-HM-LC-Dim1-Pl3"));
    }

    @Test
    public void testGeneratedChannelTypeUIDHasExpectedFormat() {
        HmDatapoint hmDatapoint = DimmerHelper.createDimmerHmDatapoint();
        ChannelTypeUID channelTypeUID = UidUtils.generateChannelTypeUID(hmDatapoint);
        MatcherAssert.assertThat(channelTypeUID.getAsString(), CoreMatchers.is("homematic:HM-LC-Dim1-Pl3_1_DIMMER"));
    }

    @Test
    public void testGeneratedChannelUIDHasExpectedFormat() {
        HmDatapoint hmDatapoint = DimmerHelper.createDimmerHmDatapoint();
        ThingUID thingUID = DimmerHelper.createDimmerThingUID();
        ChannelUID channelUID = UidUtils.generateChannelUID(hmDatapoint, thingUID);
        MatcherAssert.assertThat(channelUID.getAsString(), CoreMatchers.is("homematic:HM-LC-Dim1-Pl3:ABC12345678:1#DIMMER"));
    }

    @Test
    public void testGeneratedChannelGroupTypeUIDHasExpectedFormat() {
        HmChannel hmChannel = DimmerHelper.createDimmerHmChannel();
        ChannelGroupTypeUID channelGroupTypeUID = UidUtils.generateChannelGroupTypeUID(hmChannel);
        MatcherAssert.assertThat(channelGroupTypeUID.getAsString(), CoreMatchers.is("homematic:HM-LC-Dim1-Pl3_1"));
    }

    @Test
    public void testGeneratedThingUIDHasExpectedFormat() {
        HmDevice hmDevice = DimmerHelper.createDimmerHmDevice();
        Bridge bridge = BridgeHelper.createHomematicBridge();
        ThingUID thingUID = UidUtils.generateThingUID(hmDevice, bridge);
        MatcherAssert.assertThat(thingUID.getAsString(), CoreMatchers.is("homematic:HM-LC-Dim1-Pl3:myBridge:ABC12345678"));
    }

    @Test
    public void testCreateHmDatapointInfoParsesChannelUIDCorrectly() {
        ChannelUID channelUid = new ChannelUID("homematic:HM-LC-Dim1-Pl3:myBridge:ABC12345678:1#DIMMER");
        HmDatapointInfo hmDatapointInfo = UidUtils.createHmDatapointInfo(channelUid);
        MatcherAssert.assertThat(hmDatapointInfo.getAddress(), CoreMatchers.is("ABC12345678"));
        MatcherAssert.assertThat(hmDatapointInfo.getChannel(), CoreMatchers.is(1));
        MatcherAssert.assertThat(hmDatapointInfo.getName(), CoreMatchers.is("DIMMER"));
    }

    @Test
    public void testHomematicAddressIsGeneratedFromThingID() {
        Thing thing = DimmerHelper.createDimmerThing();
        String generatedAddress = UidUtils.getHomematicAddress(thing);
        MatcherAssert.assertThat(generatedAddress, CoreMatchers.is("ABC12345678"));
    }
}

