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
import OnOffType.OFF;
import OnOffType.ON;
import org.junit.Assert;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;


public class RockerSwitchInSwitchOnOffProfileTest extends BasicBindingTest {
    private static final String CHANNEL = "B";

    @Test
    public void switchOnForButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInSwitchOnOffProfileTest.CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", ON, publisher.popLastCommand());
    }

    @Test
    public void switchOffForButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), RockerSwitchInSwitchOnOffProfileTest.CHANNEL, Parameter.O);
        binding.valueChanged(valueParameterAddress, PRESSED);
        Assert.assertEquals("Update State", OFF, publisher.popLastCommand());
    }
}

