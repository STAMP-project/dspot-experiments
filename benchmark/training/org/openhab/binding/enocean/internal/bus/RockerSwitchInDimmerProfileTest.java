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
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;


@Ignore
public class RockerSwitchInDimmerProfileTest extends BasicBindingTest {
    @Test
    public void increaseLightOnShortButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void decreaseLightOnShortButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        Assert.assertEquals("Update State", DECREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void lightenUpDuringLongButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        waitFor(300);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void lightenUpDuringVeryLongButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        waitFor(300);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        waitFor(300);
        Assert.assertEquals("Update State", INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void dimmLightDuringLongButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        waitFor(10);
        Assert.assertEquals("Update State", DECREASE, publisher.popLastCommand());
        waitFor(300);
        Assert.assertEquals("Update State", DECREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, RELEASED);
        waitFor(10);
        Assert.assertNull("Update State", publisher.popLastCommand());
    }
}

