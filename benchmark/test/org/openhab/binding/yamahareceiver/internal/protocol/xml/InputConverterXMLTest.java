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
package org.openhab.binding.yamahareceiver.internal.protocol.xml;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link InputConverterXML}.
 *
 * @author Tomasz Maruszak - Initial contribution
 */
public class InputConverterXMLTest extends AbstractXMLProtocolTest {
    private InputConverterXML subject;

    @Test
    public void when_noMapping_fromStateName_returnsCanonicalNames() {
        // arrange
        subject = new InputConverterXML(con, "");
        // act
        String hdmi1 = subject.fromStateName("HDMI1");
        String hdmi2 = subject.fromStateName("HDMI2");
        String av1 = subject.fromStateName("AV1");
        String av2 = subject.fromStateName("AV2");
        String audio1 = subject.fromStateName("AUDIO1");
        String audio2 = subject.fromStateName("AUDIO2");
        String bluetooth = subject.fromStateName(INPUT_BLUETOOTH);
        String usb = subject.fromStateName(INPUT_USB);
        String tuner = subject.fromStateName(INPUT_TUNER);
        String netRadio = subject.fromStateName(INPUT_NET_RADIO);
        String server = subject.fromStateName(INPUT_SERVER);
        String multiCastLink = subject.fromStateName(INPUT_MUSIC_CAST_LINK);
        String spotify = subject.fromStateName(INPUT_SPOTIFY);
        // assert
        Assert.assertEquals("HDMI1", hdmi1);
        Assert.assertEquals("HDMI2", hdmi2);
        Assert.assertEquals("AV1", av1);
        Assert.assertEquals("AV2", av2);
        Assert.assertEquals("AUDIO1", audio1);
        Assert.assertEquals("AUDIO2", audio2);
        Assert.assertEquals("Bluetooth", bluetooth);
        Assert.assertEquals("USB", usb);
        Assert.assertEquals("TUNER", tuner);
        Assert.assertEquals("NET RADIO", netRadio);
        Assert.assertEquals("SERVER", server);
        Assert.assertEquals("MusicCast Link", multiCastLink);
        Assert.assertEquals("Spotify", spotify);
    }

    @Test
    public void when_mapping_fromStateName_takesUserMappingAboveAll() {
        // arrange
        subject = new InputConverterXML(con, "HDMI1=HDMI 1,Bluetooth=BLUETOOTH");
        // act
        String hdmi1 = subject.fromStateName("HDMI1");
        String bluetooth = subject.fromStateName(INPUT_BLUETOOTH);
        // assert
        Assert.assertEquals("HDMI 1", hdmi1);
        Assert.assertEquals("BLUETOOTH", bluetooth);
    }
}

