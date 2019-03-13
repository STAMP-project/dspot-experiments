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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases ValueSet
 *
 * @author Jan N. Klug - Initial contribution
 */
public class ValueSetTest {
    @Test
    public void fadeAndHoldTime() {
        ValueSet valueSet = new ValueSet(100, 200);
        Assert.assertThat(valueSet.getFadeTime(), CoreMatchers.is(100));
        Assert.assertThat(valueSet.getHoldTime(), CoreMatchers.is(200));
    }

    @Test
    public void valueAndRepetition() {
        ValueSet valueSet = new ValueSet(0, 0);
        valueSet.addValue(100);
        valueSet.addValue(200);
        // stored values
        Assert.assertThat(valueSet.getValue(0), CoreMatchers.is(100));
        Assert.assertThat(valueSet.getValue(1), CoreMatchers.is(200));
        // repetitions
        Assert.assertThat(valueSet.getValue(2), CoreMatchers.is(100));
        Assert.assertThat(valueSet.getValue(5), CoreMatchers.is(200));
    }
}

