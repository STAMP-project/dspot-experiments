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


import org.eclipse.smarthome.core.types.Command;
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
public class BitUtilitiesCommandToRegistersTest {
    private final Command command;

    private final ValueType type;

    private final Object expectedResult;

    @Rule
    public final ExpectedException shouldThrow = ExpectedException.none();

    public BitUtilitiesCommandToRegistersTest(Command command, ValueType type, Object expectedResult) {
        this.command = command;
        this.type = type;
        this.expectedResult = expectedResult;// Exception or array of 16bit integers

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCommandToRegisters() {
        if (((expectedResult) instanceof Class) && (Exception.class.isAssignableFrom(((Class) (expectedResult))))) {
            shouldThrow.expect(((Class) (expectedResult)));
        }
        ModbusRegisterArray registers = ModbusBitUtilities.commandToRegisters(this.command, this.type);
        short[] expectedRegisters = ((short[]) (expectedResult));
        Assert.assertThat(String.format("register index command=%s, type=%s", command, type), registers.size(), CoreMatchers.is(CoreMatchers.equalTo(expectedRegisters.length)));
        for (int i = 0; i < (expectedRegisters.length); i++) {
            int expectedRegisterDataUnsigned = (expectedRegisters[i]) & 65535;
            int actual = registers.getRegister(i).getValue();
            Assert.assertThat(String.format("register index i=%d, command=%s, type=%s", i, command, type), actual, CoreMatchers.is(CoreMatchers.equalTo(expectedRegisterDataUnsigned)));
        }
    }
}

