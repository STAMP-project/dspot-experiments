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


import ActionState.COMPLETED;
import ActionState.COMPLETEDFINAL;
import ActionState.RUNNING;
import ActionState.WAITING;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.dmx.internal.action.FadeAction;
import org.openhab.binding.dmx.internal.multiverse.DmxChannel;


/**
 * Tests cases FadeAction
 *
 * @author Jan N. Klug - Initial contribution
 */
public class FadeActionTest {
    private static final int testValue = 200;

    private static final int testFadeTime = 1000;

    private static final int testHoldTime = 1000;

    @Test
    public void checkWithFadingWithoutHold() {
        FadeAction fadeAction = new FadeAction(FadeActionTest.testFadeTime, FadeActionTest.testValue, 0);
        DmxChannel testChannel = new DmxChannel(0, 1, 0);
        testChannel.setValue(0);
        long startTime = System.currentTimeMillis();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, startTime), CoreMatchers.is(0));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + ((FadeActionTest.testFadeTime) / 2))), CoreMatchers.is(((256 * (FadeActionTest.testValue)) / 2)));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + 1000)), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(COMPLETED));
        fadeAction.reset();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
    }

    @Test
    public void checkWithFadingWithHold() {
        FadeAction fadeAction = new FadeAction(FadeActionTest.testFadeTime, FadeActionTest.testValue, FadeActionTest.testHoldTime);
        DmxChannel testChannel = new DmxChannel(0, 1, 0);
        testChannel.setValue(0);
        long startTime = System.currentTimeMillis();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, startTime), CoreMatchers.is(0));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + ((FadeActionTest.testFadeTime) / 2))), CoreMatchers.is(((256 * (FadeActionTest.testValue)) / 2)));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + (FadeActionTest.testFadeTime))), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, ((startTime + (FadeActionTest.testFadeTime)) + ((FadeActionTest.testHoldTime) / 2))), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, ((startTime + (FadeActionTest.testFadeTime)) + (FadeActionTest.testHoldTime))), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(COMPLETED));
        fadeAction.reset();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
    }

    @Test
    public void checkWithFadingWithInfiniteHold() {
        FadeAction fadeAction = new FadeAction(FadeActionTest.testFadeTime, FadeActionTest.testValue, (-1));
        DmxChannel testChannel = new DmxChannel(0, 1, 0);
        testChannel.setValue(0);
        long startTime = System.currentTimeMillis();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, startTime), CoreMatchers.is(0));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + ((FadeActionTest.testFadeTime) / 2))), CoreMatchers.is(((256 * (FadeActionTest.testValue)) / 2)));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + (FadeActionTest.testFadeTime))), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(COMPLETEDFINAL));
        fadeAction.reset();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
    }

    @Test
    public void checkWithoutFadingWithHold() {
        FadeAction fadeAction = new FadeAction(0, FadeActionTest.testValue, FadeActionTest.testHoldTime);
        DmxChannel testChannel = new DmxChannel(0, 1, 0);
        testChannel.setValue(0);
        long startTime = System.currentTimeMillis();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, startTime), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + ((FadeActionTest.testHoldTime) / 2))), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(RUNNING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, (startTime + (FadeActionTest.testHoldTime))), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(COMPLETED));
        fadeAction.reset();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
    }

    @Test
    public void checkWithoutFadingWithoutHold() {
        FadeAction fadeAction = new FadeAction(0, FadeActionTest.testValue, 0);
        DmxChannel testChannel = new DmxChannel(0, 1, 0);
        testChannel.setValue(0);
        long startTime = System.currentTimeMillis();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, startTime), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(COMPLETED));
        fadeAction.reset();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
    }

    @Test
    public void checkWithoutFadingWithInfiniteHold() {
        FadeAction fadeAction = new FadeAction(0, FadeActionTest.testValue, (-1));
        DmxChannel testChannel = new DmxChannel(0, 1, 0);
        testChannel.setValue(0);
        long startTime = System.currentTimeMillis();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
        Assert.assertThat(fadeAction.getNewValue(testChannel, startTime), CoreMatchers.is((256 * (FadeActionTest.testValue))));
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(COMPLETEDFINAL));
        fadeAction.reset();
        Assert.assertThat(fadeAction.getState(), CoreMatchers.is(WAITING));
    }
}

