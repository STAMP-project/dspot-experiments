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


import DmxChannel.MAX_VALUE;
import DmxChannel.MIN_VALUE;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openhab.binding.dmx.internal.action.FadeAction;
import org.openhab.binding.dmx.internal.action.ResumeAction;
import org.openhab.binding.dmx.internal.handler.DimmerThingHandler;
import org.openhab.binding.dmx.internal.multiverse.DmxChannel;


/**
 * Tests cases for Channel
 *
 * @author Jan N. Klug - Initial contribution
 */
public class DmxChannelTest {
    private final ChannelUID valueChannelUID = new ChannelUID("dmx:testBridge:testThing:valueChannel");

    DmxChannel dmxChannel;

    DimmerThingHandler dimmerThingHandler;

    long currentTime;

    @Test
    public void checkValueSettingAndReporting() {
        dmxChannel.setValue(125);
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.getValue(), CoreMatchers.is(125));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 125);
    }

    @Test
    public void checkLimitsAreObserved() {
        dmxChannel.setValue(300);
        Assert.assertThat(dmxChannel.getValue(), CoreMatchers.is(MAX_VALUE));
        dmxChannel.setValue((-1));
        Assert.assertThat(dmxChannel.getValue(), CoreMatchers.is(MIN_VALUE));
    }

    @Test
    public void setAndClearAction() {
        // has action
        dmxChannel.setChannelAction(new FadeAction(0, 100, (-1)));
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        // clear action
        dmxChannel.clearAction();
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(false));
    }

    @Test
    public void checkSingleFadeAction() {
        dmxChannel.addChannelAction(new FadeAction(1000, 243, (-1)));
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 0);
        dmxChannel.getNewValue(((currentTime) + 1000));
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(false));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 243);
    }

    @Test
    public void checkMultipleInfiniteFadeAction() {
        dmxChannel.addChannelAction(new FadeAction(1000, 243, 0));
        dmxChannel.addChannelAction(new FadeAction(1000, 127, 0));
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 0);
        // check first action completes
        dmxChannel.getNewValue(currentTime);
        currentTime += 1000;
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 243);
        // check second action completes
        dmxChannel.getNewValue(currentTime);
        currentTime += 1000;
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 127);
        // check first action completes again
        currentTime += 1000;
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 243);
    }

    @Test
    public void checkFadeActionWithResume() {
        dmxChannel.setValue(127);
        dmxChannel.suspendAction();
        dmxChannel.addChannelAction(new FadeAction(1000, 243, 0));
        dmxChannel.addChannelAction(new ResumeAction());
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 127);
        // check action completes
        dmxChannel.getNewValue(currentTime);
        currentTime += 1000;
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(true));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 243);
        // check state is restored
        dmxChannel.getNewValue(currentTime);
        Assert.assertThat(dmxChannel.hasRunningActions(), CoreMatchers.is(false));
        Mockito.verify(dimmerThingHandler).updateChannelValue(valueChannelUID, 127);
    }
}

