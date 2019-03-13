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
import IncreaseDecreaseType.DECREASE;
import IncreaseDecreaseType.INCREASE;
import OnOffType.OFF;
import OnOffType.ON;
import org.junit.Assert;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;


public class RockerSwitchInDimmerOnOffProfileTest extends BasicBindingTest {
    private static final String CHANNEL = "B";

    @Test
    public void switchOnLightOnShortButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInDimmerOnOffProfileTest.CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertEquals("Update State", ON, publisher.popLastCommand());
    }

    @Test
    public void doNothingOnWrongChannel() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), "A", Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void switchOffLightOnShortButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInDimmerOnOffProfileTest.CHANNEL, Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertEquals("Update State", OFF, publisher.popLastCommand());
    }

    @Test
    public void lightenUpDuringLongButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInDimmerOnOffProfileTest.CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(400);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void lightenUpDuringVeryLongButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInDimmerOnOffProfileTest.CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(400);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        waitFor(300);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void dimmLightDuringLongButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInDimmerOnOffProfileTest.CHANNEL, Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(400);
        Assert.assertEquals("Update State", DECREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }
}

