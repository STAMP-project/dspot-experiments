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
package org.openhab.binding.nibeheatpump.internal.models;


import PumpModel.F1X45;
import PumpModel.F1X55;
import PumpModel.F470;
import PumpModel.F750;
import VariableInformation.NibeDataType.S16;
import VariableInformation.Type.SENSOR;
import org.junit.Assert;
import org.junit.Test;


public class VariableInformationTest {
    @Test
    public void TestF1X45Variable() {
        final int coilAddress = 40004;
        final VariableInformation variableInfo = VariableInformation.getVariableInfo(F1X45, coilAddress);
        Assert.assertEquals(10, variableInfo.factor);
        Assert.assertEquals("BT1 Outdoor temp", variableInfo.variable);
        Assert.assertEquals(S16, variableInfo.dataType);
        Assert.assertEquals(SENSOR, variableInfo.type);
    }

    @Test
    public void TestF1X55Variable() {
        final int coilAddress = 40004;
        final VariableInformation variableInfo = VariableInformation.getVariableInfo(F1X55, coilAddress);
        Assert.assertEquals(10, variableInfo.factor);
        Assert.assertEquals("BT1 Outdoor Temperature", variableInfo.variable);
        Assert.assertEquals(S16, variableInfo.dataType);
        Assert.assertEquals(SENSOR, variableInfo.type);
    }

    @Test
    public void TestF750Variable() {
        final int coilAddress = 40004;
        final VariableInformation variableInfo = VariableInformation.getVariableInfo(F750, coilAddress);
        Assert.assertEquals(10, variableInfo.factor);
        Assert.assertEquals("BT1 Outdoor Temperature", variableInfo.variable);
        Assert.assertEquals(S16, variableInfo.dataType);
        Assert.assertEquals(SENSOR, variableInfo.type);
    }

    @Test
    public void TestF470Variable() {
        final int coilAddress = 40020;
        final VariableInformation variableInfo = VariableInformation.getVariableInfo(F470, coilAddress);
        Assert.assertEquals(10, variableInfo.factor);
        Assert.assertEquals("EB100-BT16 Evaporator temp", variableInfo.variable);
        Assert.assertEquals(S16, variableInfo.dataType);
        Assert.assertEquals(SENSOR, variableInfo.type);
    }
}

