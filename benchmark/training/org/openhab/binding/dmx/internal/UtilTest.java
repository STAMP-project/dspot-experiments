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
package org.openhab.binding.dmx.internal;


import DmxChannel.MAX_VALUE;
import DmxChannel.MIN_VALUE;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases Util
 *
 * @author Jan N. Klug - Initial contribution
 */
public class UtilTest {
    @Test
    public void coercingOfDmxValues() {
        // overrange
        int value = Util.toDmxValue(300);
        Assert.assertThat(value, CoreMatchers.is(MAX_VALUE));
        // underrange
        value = Util.toDmxValue((-1));
        Assert.assertThat(value, CoreMatchers.is(MIN_VALUE));
        // inrange
        value = Util.toDmxValue(100);
        Assert.assertThat(value, CoreMatchers.is(100));
    }

    @Test
    public void conversionString() {
        int value = Util.toDmxValue("100");
        Assert.assertThat(value, CoreMatchers.is(100));
    }

    @Test
    public void conversionFromPercentType() {
        // borders
        int value = Util.toDmxValue(new PercentType(100));
        Assert.assertThat(value, CoreMatchers.is(255));
        value = Util.toDmxValue(new PercentType(0));
        Assert.assertThat(value, CoreMatchers.is(0));
        // middle
        value = Util.toDmxValue(new PercentType(50));
        Assert.assertThat(value, CoreMatchers.is(127));
    }

    @Test
    public void conversionToPercentType() {
        // borders
        PercentType value = Util.toPercentValue(255);
        Assert.assertThat(value.intValue(), CoreMatchers.is(100));
        value = Util.toPercentValue(0);
        Assert.assertThat(value.intValue(), CoreMatchers.is(0));
        // middle
        value = Util.toPercentValue(127);
        Assert.assertThat(value.intValue(), CoreMatchers.is(49));
    }

    @Test
    public void fadeTimeFraction() {
        // target already reached
        int value = Util.fadeTimeFraction(123, 123, 1000);
        Assert.assertThat(value, CoreMatchers.is(0));
        // full fade
        value = Util.fadeTimeFraction(0, 255, 1000);
        Assert.assertThat(value, CoreMatchers.is(1000));
        // fraction
        value = Util.fadeTimeFraction(100, 155, 2550);
        Assert.assertThat(value, CoreMatchers.is(550));
    }
}

