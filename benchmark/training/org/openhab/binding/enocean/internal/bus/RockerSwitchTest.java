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
import OnOffType.ON;
import org.junit.Assert;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.common.Parameter;
import org.openhab.core.library.items.SwitchItem;


public class RockerSwitchTest extends BasicBindingTest {
    @Test
    public void testReceiveButtonPress() {
        parameterAddress = new org.opencean.core.address.EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), Parameter.I);
        provider.setParameterAddress(parameterAddress);
        binding.addBindingProvider(provider);
        provider.setItem(new SwitchItem("dummie"));
        binding.valueChanged(parameterAddress, PRESSED);
        Assert.assertEquals("Update State", ON, publisher.getUpdateState());
    }
}

