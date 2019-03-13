/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.maxcube.internal.message;


import Charge.LOW;
import Charge.OK;
import Charge.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Battery} of MAX! binding.
 *
 * @author Dominic Lerbs
 * @since 1.7.0
 */
public class BatteryTest {
    @Test
    public void testBatteryCharge() {
        Battery battery = new Battery();
        Assert.assertEquals("Battery Charge should be unknown", stringTypeFor(UNKNOWN), battery.getCharge());
        battery.setCharge(LOW);
        Assert.assertEquals("Battery Charge should be low", stringTypeFor(LOW), battery.getCharge());
        battery.setCharge(OK);
        Assert.assertEquals("Battery Charge should be ok", stringTypeFor(OK), battery.getCharge());
    }

    @Test
    public void testBatteryChargeUpdated() {
        Battery battery = new Battery();
        Assert.assertFalse("Battery charge should not be set to updated intitially", battery.isChargeUpdated());
        battery.setCharge(LOW);
        Assert.assertTrue(("Battery charge should be set to updated " + "after change to low"), battery.isChargeUpdated());
        battery.setCharge(LOW);
        Assert.assertFalse(("Battery charge should not be set to updated " + "after setting same charge again"), battery.isChargeUpdated());
        battery.setCharge(OK);
        Assert.assertTrue(("Battery charge should be set to updated " + "after change to ok"), battery.isChargeUpdated());
    }
}

