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
package org.openhab.io.transport.modbus.test;


import org.eclipse.smarthome.core.library.types.DecimalType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.io.transport.modbus.ModbusBitUtilities;
import org.openhab.io.transport.modbus.ModbusConstants.ValueType;
import org.openhab.io.transport.modbus.ModbusRegisterArray;


@RunWith(Parameterized.class)
public class BitUtilitiesExtractStateFromRegistersTest {
    final ModbusRegisterArray registers;

    final ValueType type;

    final int index;

    final Object expectedResult;

    @Rule
    public final ExpectedException shouldThrow = ExpectedException.none();

    public BitUtilitiesExtractStateFromRegistersTest(Object expectedResult, ValueType type, ModbusRegisterArray registers, int index) {
        this.registers = registers;
        this.index = index;
        this.type = type;
        this.expectedResult = expectedResult;// Exception or DecimalType

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCommandToRegisters() {
        if (((expectedResult) instanceof Class) && (Exception.class.isAssignableFrom(((Class) (expectedResult))))) {
            shouldThrow.expect(((Class) (expectedResult)));
        }
        DecimalType actualState = ModbusBitUtilities.extractStateFromRegisters(this.registers, this.index, this.type);
        Assert.assertThat(String.format("registers=%s, index=%d, type=%s", registers, index, type), actualState, CoreMatchers.is(CoreMatchers.equalTo(expectedResult)));
    }
}

