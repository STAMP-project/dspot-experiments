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
package org.openhab.binding.mqtt.generic.internal.values;


import OnOffType.OFF;
import OnOffType.ON;
import OpenClosedType.CLOSED;
import OpenClosedType.OPEN;
import PercentType.HUNDRED;
import PercentType.ZERO;
import UpDownType.DOWN;
import UpDownType.UP;
import java.math.BigDecimal;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the value classes. They should throw exceptions if the wrong command type is used
 * for an update. The percent value class should raise an exception if the value is out of range.
 *
 * The on/off value class should accept a multitude of values including the custom defined ones.
 *
 * The string value class states are tested.
 *
 * @author David Graeff - Initial contribution
 */
public class ValueTests {
    @Test(expected = IllegalArgumentException.class)
    public void illegalTextStateUpdate() {
        TextValue v = new TextValue("one,two".split(","));
        v.update(p(v, "three"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalColorUpdate() {
        ColorValue v = new ColorValue(true, null, null, 10);
        v.update(p(v, "255,255,abc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalNumberCommand() {
        NumberValue v = new NumberValue(null, null, null);
        v.update(OFF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPercentCommand() {
        PercentageValue v = new PercentageValue(null, null, null, null, null);
        v.update(OFF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalOnOffCommand() {
        OnOffValue v = new OnOffValue(null, null);
        v.update(new DecimalType(101.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPercentUpdate() {
        PercentageValue v = new PercentageValue(null, null, null, null, null);
        v.update(new DecimalType(101.0));
    }

    @Test
    public void onoffUpdate() {
        OnOffValue v = new OnOffValue("fancyON", "fancyOff");
        // Test with command
        v.update(OFF);
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyOff"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(OFF));
        v.update(ON);
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyON"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(ON));
        // Test with string, representing the command
        v.update(new StringType("OFF"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyOff"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(OFF));
        v.update(new StringType("ON"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyON"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(ON));
        // Test with custom string, setup in the constructor
        v.update(new StringType("fancyOff"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyOff"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(OFF));
        v.update(new StringType("fancyON"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyON"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(ON));
    }

    @Test
    public void openCloseUpdate() {
        OpenCloseValue v = new OpenCloseValue("fancyON", "fancyOff");
        // Test with command
        v.update(CLOSED);
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyOff"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(CLOSED));
        v.update(OPEN);
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyON"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(OPEN));
        // Test with string, representing the command
        v.update(new StringType("CLOSED"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyOff"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(CLOSED));
        v.update(new StringType("OPEN"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyON"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(OPEN));
        // Test with custom string, setup in the constructor
        v.update(new StringType("fancyOff"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyOff"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(CLOSED));
        v.update(new StringType("fancyON"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("fancyON"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(OPEN));
    }

    @Test
    public void rollershutterUpdate() {
        RollershutterValue v = new RollershutterValue("fancyON", "fancyOff", "fancyStop");
        // Test with command
        v.update(UP);
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("0"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(ZERO));
        v.update(DOWN);
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("100"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(HUNDRED));
        // Test with custom string
        v.update(new StringType("fancyON"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("0"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(ZERO));
        v.update(new StringType("fancyOff"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("100"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(HUNDRED));
        v.update(new StringType("27"));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("27"));
        Assert.assertThat(v.getChannelState(), CoreMatchers.is(new PercentType(27)));
    }

    @Test
    public void percentCalc() {
        PercentageValue v = new PercentageValue(new BigDecimal(10.0), new BigDecimal(110.0), new BigDecimal(1.0), null, null);
        v.update(new DecimalType(110.0));
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(100)));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("110"));
        v.update(new DecimalType(10.0));
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(0)));
        Assert.assertThat(v.getMQTTpublishValue(), CoreMatchers.is("10"));
        v.update(ON);
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(100)));
        v.update(OFF);
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(0)));
    }

    @Test
    public void percentCustomOnOff() {
        PercentageValue v = new PercentageValue(new BigDecimal(0.0), new BigDecimal(100.0), new BigDecimal(1.0), "on", "off");
        v.update(new StringType("on"));
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(100)));
        v.update(new StringType("off"));
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(0)));
    }

    @Test
    public void decimalCalc() {
        PercentageValue v = new PercentageValue(new BigDecimal(0.1), new BigDecimal(1.0), new BigDecimal(0.1), null, null);
        v.update(new DecimalType(1.0));
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(100)));
        v.update(new DecimalType(0.1));
        Assert.assertThat(((PercentType) (v.getChannelState())), CoreMatchers.is(new PercentType(0)));
        v.update(new DecimalType(0.2));
        Assert.assertEquals(floatValue(), 11.11F, 0.01F);
    }

    @Test(expected = IllegalArgumentException.class)
    public void percentCalcInvalid() {
        PercentageValue v = new PercentageValue(new BigDecimal(10.0), new BigDecimal(110.0), new BigDecimal(1.0), null, null);
        v.update(new DecimalType(9.0));
    }
}

