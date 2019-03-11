/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.bus;


import WindowHandleState.DOWN;
import WindowHandleState.MIDDLE;
import WindowHandleState.UP;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.types.StringType;


/**
 * Testcases to test the transformation of window handle enocean-messages to window states
 *
 * @author Holger Ploch (contact@holger-ploch.de)
 */
public class WindowHandleTest extends BasicBindingTest {
    @Test
    public void testReceiveWindowClosed() {
        binding.valueChanged(parameterAddress, DOWN);
        Assert.assertEquals("Update Window State", new StringType("CLOSED").toString(), publisher.popLastCommand().toString());
    }

    @Test
    public void testReceiveWindowOpened() {
        binding.valueChanged(parameterAddress, MIDDLE);
        Assert.assertEquals("Update Window State", new StringType("OPEN").toString(), publisher.popLastCommand().toString());
    }

    @Test
    public void testReceiveWindowAjar() {
        binding.valueChanged(parameterAddress, UP);
        Assert.assertEquals("Update Window State", new StringType("AJAR").toString(), publisher.popLastCommand().toString());
    }
}

