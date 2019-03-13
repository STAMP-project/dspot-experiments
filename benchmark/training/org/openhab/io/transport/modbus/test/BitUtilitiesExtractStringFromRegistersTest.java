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


import java.nio.charset.Charset;
import org.eclipse.smarthome.core.library.types.StringType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.io.transport.modbus.ModbusBitUtilities;
import org.openhab.io.transport.modbus.ModbusRegisterArray;


@RunWith(Parameterized.class)
public class BitUtilitiesExtractStringFromRegistersTest {
    final ModbusRegisterArray registers;

    final int index;

    final int length;

    final Object expectedResult;

    final Charset charset;

    @Rule
    public final ExpectedException shouldThrow = ExpectedException.none();

    public BitUtilitiesExtractStringFromRegistersTest(Object expectedResult, ModbusRegisterArray registers, int index, int length, Charset charset) {
        this.registers = registers;
        this.index = index;
        this.length = length;
        this.charset = charset;
        this.expectedResult = expectedResult;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testExtractStringFromRegisters() {
        if (((expectedResult) instanceof Class) && (Exception.class.isAssignableFrom(((Class) (expectedResult))))) {
            shouldThrow.expect(((Class) (expectedResult)));
        }
        StringType actualState = ModbusBitUtilities.extractStringFromRegisters(this.registers, this.index, this.length, this.charset);
        Assert.assertThat(String.format("registers=%s, index=%d, length=%d", registers, index, length), actualState, CoreMatchers.is(CoreMatchers.equalTo(expectedResult)));
    }
}

