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
package org.openhab.binding.dmx.multiverse;


import BaseDmxChannel.MAX_CHANNEL_ID;
import BaseDmxChannel.MIN_CHANNEL_ID;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.dmx.internal.multiverse.BaseDmxChannel;


/**
 * Tests cases for BaseChannel
 *
 * @author Jan N. Klug - Initial contribution
 */
public class BaseChannelTest {
    @Test
    public void creatingBaseChannelFromIntegers() {
        // overrange
        BaseDmxChannel channel = new BaseDmxChannel(0, 600);
        Assert.assertThat(channel.getChannelId(), CoreMatchers.is(MAX_CHANNEL_ID));
        // underrange
        channel = new BaseDmxChannel(0, (-1));
        Assert.assertThat(channel.getChannelId(), CoreMatchers.is(MIN_CHANNEL_ID));
        // inrange & universe
        channel = new BaseDmxChannel(5, 100);
        Assert.assertThat(channel.getChannelId(), CoreMatchers.is(100));
        Assert.assertThat(channel.getUniverseId(), CoreMatchers.is(5));
        // set universe
        channel.setUniverseId(1);
        Assert.assertThat(channel.getUniverseId(), CoreMatchers.is(1));
    }

    @Test
    public void creatingBaseChannelfromBaseChannel() {
        BaseDmxChannel baseChannel = new BaseDmxChannel(5, 100);
        BaseDmxChannel copyChannel = new BaseDmxChannel(baseChannel);
        Assert.assertThat(copyChannel.getChannelId(), CoreMatchers.is(100));
        Assert.assertThat(copyChannel.getUniverseId(), CoreMatchers.is(5));
    }

    @Test
    public void comparingChannels() {
        BaseDmxChannel channel1 = new BaseDmxChannel(5, 100);
        BaseDmxChannel channel2 = new BaseDmxChannel(7, 140);
        Assert.assertThat(channel1.compareTo(channel2), CoreMatchers.is((-1)));
        Assert.assertThat(channel2.compareTo(channel1), CoreMatchers.is(1));
        Assert.assertThat(channel1.compareTo(channel1), CoreMatchers.is(0));
    }

    @Test
    public void stringConversion() {
        // to string
        BaseDmxChannel baseChannel = new BaseDmxChannel(5, 100);
        Assert.assertThat(baseChannel.toString(), CoreMatchers.is(CoreMatchers.equalTo("5:100")));
        // single channel from string with universe
        String parseString = new String("2:100");
        List<BaseDmxChannel> channelList = BaseDmxChannel.fromString(parseString, 0);
        Assert.assertThat(channelList.size(), CoreMatchers.is(1));
        Assert.assertThat(channelList.get(0).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:100")));
        // single channel from string without universe
        parseString = new String("100");
        channelList = BaseDmxChannel.fromString(parseString, 2);
        Assert.assertThat(channelList.size(), CoreMatchers.is(1));
        Assert.assertThat(channelList.get(0).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:100")));
        // two channels with channel width
        parseString = new String("100/2");
        channelList = BaseDmxChannel.fromString(parseString, 2);
        Assert.assertThat(channelList.size(), CoreMatchers.is(2));
        Assert.assertThat(channelList.get(0).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:100")));
        Assert.assertThat(channelList.get(1).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:101")));
        // to channels with comma
        parseString = new String("100,102");
        channelList = BaseDmxChannel.fromString(parseString, 2);
        Assert.assertThat(channelList.size(), CoreMatchers.is(2));
        Assert.assertThat(channelList.get(0).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:100")));
        Assert.assertThat(channelList.get(1).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:102")));
        // complex string
        parseString = new String("257,100/3,426");
        channelList = BaseDmxChannel.fromString(parseString, 2);
        Assert.assertThat(channelList.size(), CoreMatchers.is(5));
        Assert.assertThat(channelList.get(0).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:257")));
        Assert.assertThat(channelList.get(1).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:100")));
        Assert.assertThat(channelList.get(2).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:101")));
        Assert.assertThat(channelList.get(3).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:102")));
        Assert.assertThat(channelList.get(4).toString(), CoreMatchers.is(CoreMatchers.equalTo("2:426")));
    }
}

