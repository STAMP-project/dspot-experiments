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
package org.openhab.binding.mqtt.generic.internal.convention.homeassistant;


import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HaIDTests {
    @Test
    public void testWithoutNode() {
        HaID subject = new HaID("homeassistant/switch/name/config");
        Assert.assertThat(subject.getThingID(), CoreMatchers.is("name"));
        Assert.assertThat(subject.getChannelGroupTypeID(), CoreMatchers.is("name_switch"));
        Assert.assertThat(subject.getChannelTypeID("channel"), CoreMatchers.is(new ChannelTypeUID("mqtt:name_switch_channel")));
        Assert.assertThat(subject.getChannelGroupID(), CoreMatchers.is("switch_"));
    }

    @Test
    public void testWithNode() {
        HaID subject = new HaID("homeassistant/switch/node/name/config");
        Assert.assertThat(subject.getThingID(), CoreMatchers.is("name"));
        Assert.assertThat(subject.getChannelGroupTypeID(), CoreMatchers.is("name_switchnode"));
        Assert.assertThat(subject.getChannelTypeID("channel"), CoreMatchers.is(new ChannelTypeUID("mqtt:name_switchnode_channel")));
        Assert.assertThat(subject.getChannelGroupID(), CoreMatchers.is("switch_node"));
    }
}

