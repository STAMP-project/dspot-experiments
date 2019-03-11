/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import OnOffType.OFF;
import OnOffType.ON;
import UnDefType.UNDEF;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.modbus.internal.ItemIOConnection.IOType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;

import static ItemIOConnection.POLL_STATE_CHANGE_TRIGGER;
import static ItemIOConnection.TRIGGER_DEFAULT;


public class ItemIOConnectionTestCase {
    @Test
    public void testSupportsStateShouldReturnFalseWithCommandType() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, TRIGGER_DEFAULT);
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, true));
        Assert.assertFalse(connection.supportsState(new DecimalType(), true, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(), true, true));
    }

    @Test
    public void testSupportsStateShouldReturnFalseWithCommandType2() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, "*");
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, true));
        Assert.assertFalse(connection.supportsState(new DecimalType(), true, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(), true, true));
    }

    @Test
    public void testSupportsStateWithDefaultTriggerUnchangedValue() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, TRIGGER_DEFAULT);
        // value not changed, slave setting updateunchanged=false, -> False
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, false));
        // value not changed, slave setting updateunchanged=true, -> False
        Assert.assertTrue(connection.supportsState(new DecimalType(), false, true));
    }

    @Test
    public void testSupportsStateWithDefaultTriggerChangedValue() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, TRIGGER_DEFAULT);
        // should always update changed values with default trigger
        Assert.assertTrue(connection.supportsState(new DecimalType(), true, false));
        Assert.assertTrue(connection.supportsState(new DecimalType(), true, true));
    }

    @Test
    public void testSupportsStateWithChangedTrigger() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, POLL_STATE_CHANGE_TRIGGER);
        Assert.assertTrue(connection.supportsState(new DecimalType(), true, false));
        Assert.assertTrue(connection.supportsState(new DecimalType(), true, true));
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(), false, true));
    }

    @Test
    public void testSupportsStateWithSpecificMatchingTrigger() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, "5");
        Assert.assertTrue(connection.supportsState(new DecimalType(5), false, false));
        Assert.assertTrue(connection.supportsState(new DecimalType(5), false, true));
        Assert.assertTrue(connection.supportsState(new DecimalType(5), true, false));
        Assert.assertTrue(connection.supportsState(new DecimalType(5), true, true));
        Assert.assertTrue(connection.supportsState(new StringType("5"), false, false));
        Assert.assertTrue(connection.supportsState(new StringType("5"), false, true));
        Assert.assertTrue(connection.supportsState(new StringType("5"), true, false));
        Assert.assertTrue(connection.supportsState(new StringType("5"), true, true));
    }

    @Test
    public void testSupportsStateWithSpecificMatchingTrigger2() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, "ON");
        Assert.assertTrue(connection.supportsState(ON, false, false));
        Assert.assertTrue(connection.supportsState(ON, false, true));
        Assert.assertTrue(connection.supportsState(ON, true, false));
        Assert.assertTrue(connection.supportsState(new StringType("oN"), true, true));
    }

    @Test
    public void testSupportsStateWithWildcardTrigger() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, "*");
        Assert.assertTrue(connection.supportsState(ON, false, false));
        Assert.assertTrue(connection.supportsState(new DecimalType(3.3), false, true));
        Assert.assertTrue(connection.supportsState(ON, true, false));
        Assert.assertTrue(connection.supportsState(new StringType("xxx"), true, true));
    }

    @Test
    public void testSupportsStateWithSpecificNonMatchingTrigger() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, "5");
        Assert.assertFalse(connection.supportsState(new DecimalType(5.2), false, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(5.4), false, true));
        Assert.assertFalse(connection.supportsState(new DecimalType((-5)), true, false));
        Assert.assertFalse(connection.supportsState(new DecimalType(5.1), true, true));
        Assert.assertFalse(connection.supportsState(new StringType("5.1"), false, false));
        Assert.assertFalse(connection.supportsState(new StringType("5x"), false, true));
        Assert.assertFalse(connection.supportsState(new StringType("5a"), true, false));
        Assert.assertFalse(connection.supportsState(UNDEF, true, true));
    }

    @Test
    public void testSupportsStateWithSpecificNonMatchingTrigger2() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, "ON");
        Assert.assertFalse(connection.supportsState(OFF, false, false));
        Assert.assertFalse(connection.supportsState(OFF, false, true));
        Assert.assertFalse(connection.supportsState(OFF, true, false));
        Assert.assertFalse(connection.supportsState(new StringType("OFF"), true, true));
    }

    @Test
    public void testSupportsCommandShouldReturnFalseWithStateType() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, TRIGGER_DEFAULT);
        Assert.assertFalse(connection.supportsCommand(new DecimalType()));
    }

    @Test
    public void testSupportsCommandShouldReturnFalseWithStateType2() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.STATE, "*");
        Assert.assertFalse(connection.supportsCommand(new DecimalType()));
    }

    @Test
    public void testSupportsCommandWithDefaultTriggerAlwaysTrue() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, TRIGGER_DEFAULT);
        Assert.assertTrue(connection.supportsCommand(new DecimalType()));
        Assert.assertTrue(connection.supportsCommand(new StringType("ff")));
        Assert.assertTrue(connection.supportsCommand(OFF));
    }

    @Test
    public void testSupportsCommandWithMatchingTrigger() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, "5");
        Assert.assertTrue(connection.supportsCommand(new DecimalType(5)));
        Assert.assertTrue(connection.supportsCommand(new DecimalType(5)));
        Assert.assertTrue(connection.supportsCommand(new DecimalType(5)));
        Assert.assertTrue(connection.supportsCommand(new DecimalType(5)));
        Assert.assertTrue(connection.supportsCommand(new StringType("5")));
        Assert.assertTrue(connection.supportsCommand(new StringType("5")));
        Assert.assertTrue(connection.supportsCommand(new StringType("5")));
        Assert.assertTrue(connection.supportsCommand(new StringType("5")));
    }

    @Test
    public void testSupportsCommandWithMatchingTrigger2() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, "ON");
        Assert.assertTrue(connection.supportsCommand(ON));
        Assert.assertTrue(connection.supportsCommand(new StringType("oN")));
    }

    @Test
    public void testSupportsCommandWithSpecificNonMatchingTrigger() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, "5");
        Assert.assertFalse(connection.supportsCommand(new DecimalType(5.2)));
        Assert.assertFalse(connection.supportsCommand(new DecimalType(5.4)));
        Assert.assertFalse(connection.supportsCommand(new DecimalType((-5))));
        Assert.assertFalse(connection.supportsCommand(new DecimalType(5.1)));
        Assert.assertFalse(connection.supportsCommand(new StringType("5.1")));
        Assert.assertFalse(connection.supportsCommand(new StringType("5x")));
        Assert.assertFalse(connection.supportsCommand(new StringType("5a")));
    }

    @Test
    public void testSupportsCommandWithSpecificNonMatchingTrigger2() {
        ItemIOConnection connection = new ItemIOConnection("", 0, IOType.COMMAND, "ON");
        Assert.assertFalse(connection.supportsCommand(OFF));
        Assert.assertFalse(connection.supportsCommand(OFF));
        Assert.assertFalse(connection.supportsCommand(OFF));
        Assert.assertFalse(connection.supportsCommand(new StringType("OFF")));
    }
}

