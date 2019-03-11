/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.bus;


import ButtonState.PRESSED;
import ButtonState.RELEASED;
import StopMoveType.STOP;
import UpDownType.DOWN;
import UpDownType.UP;
import org.junit.Assert;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;


public class RockerSwitchInRollershutterProfileTest extends BasicBindingTest {
    private static final int LONG_PRESS_DELAY = 300 + 10;

    @Test
    public void openShutterOnShortButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", UP, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void closeShutterOnShortButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", DOWN, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void openShutterDuringLongButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", UP, publisher.popLastCommand());
        waitFor(RockerSwitchInRollershutterProfileTest.LONG_PRESS_DELAY);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void closeShutterDuringLongButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", DOWN, publisher.popLastCommand());
        waitFor(RockerSwitchInRollershutterProfileTest.LONG_PRESS_DELAY);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void stopShutterMovingUpOnShortPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        publisher.popLastCommand();
        waitFor(100);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void stopShutterMovingDownOnShortPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        publisher.popLastCommand();
        waitFor(100);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void stopShutterMovingDownOnShortPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        publisher.popLastCommand();
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
        waitFor(100);
        valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void stopShutterMovingUpOnShortPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        publisher.popLastCommand();
        waitFor(100);
        valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }

    @Test
    public void stopShutterMovingAndStartAgain() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", UP, publisher.popLastCommand());
        waitFor(100);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        waitFor(100);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", UP, publisher.popLastCommand());
        waitFor(100);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", STOP, publisher.popLastCommand());
        waitFor(100);
        binding.valueChanged(valueParameterAddress, PRESSED);
        binding.valueChanged(valueParameterAddress, RELEASED);
        Assert.assertEquals("Update State", UP, publisher.popLastCommand());
        Assert.assertEquals("No new state expected", null, publisher.popLastCommand());
    }
}

