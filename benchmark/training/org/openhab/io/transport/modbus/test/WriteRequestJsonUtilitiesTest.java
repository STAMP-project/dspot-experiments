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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.io.transport.modbus.ModbusWriteFunctionCode;
import org.openhab.io.transport.modbus.json.WriteRequestJsonUtilities;


public class WriteRequestJsonUtilitiesTest {
    @Test
    public void testEmptyArray() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(3, "[]").size(), CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFC6NoRegister() {
        WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 6,"// 
         + "\"address\": 5412,")// 
         + "\"value\": []")// 
         + "}]")));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC6SingleRegister() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 6,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3]")// 
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new RegisterMatcher(55, 5412, WriteRequestJsonUtilities.DEFAULT_MAX_TRIES, ModbusWriteFunctionCode.WRITE_SINGLE_REGISTER, 3)))));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC6SingleRegisterMaxTries99() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + (((("\"functionCode\": 6,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3],")// 
         + "\"maxTries\": 99")// 
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new RegisterMatcher(55, 5412, 99, ModbusWriteFunctionCode.WRITE_SINGLE_REGISTER, 3)))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFC6MultipleRegisters() {
        WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 6,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3, 4]")// 
         + "}]")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFC16NoRegister() {
        WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 16,"// 
         + "\"address\": 5412,")// 
         + "\"value\": []")// 
         + "}]")));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC16SingleRegister() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 16,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3]")// 
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new RegisterMatcher(55, 5412, WriteRequestJsonUtilities.DEFAULT_MAX_TRIES, ModbusWriteFunctionCode.WRITE_MULTIPLE_REGISTERS, 3)))));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC16MultipleRegisters() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 16,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3, 4, 2]")// 
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new RegisterMatcher(55, 5412, WriteRequestJsonUtilities.DEFAULT_MAX_TRIES, ModbusWriteFunctionCode.WRITE_MULTIPLE_REGISTERS, 3, 4, 2)))));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC5SingeCoil() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 5,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3]")// value 3 (!= 0) is converted to boolean true
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new CoilMatcher(55, 5412, WriteRequestJsonUtilities.DEFAULT_MAX_TRIES, ModbusWriteFunctionCode.WRITE_COIL, true)))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFC5MultipleCoils() {
        WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 5,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3, 4]")// 
         + "}]")));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC15SingleCoil() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 15,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [3]")// 
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new CoilMatcher(55, 5412, WriteRequestJsonUtilities.DEFAULT_MAX_TRIES, ModbusWriteFunctionCode.WRITE_MULTIPLE_COILS, true)))));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFC15MultipleCoils() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(55, ("[{"// 
         + ((("\"functionCode\": 15,"// 
         + "\"address\": 5412,")// 
         + "\"value\": [1, 0, 5]")// 
         + "}]"))).toArray(), arrayContaining(((org.hamcrest.Matcher) (new CoilMatcher(55, 5412, WriteRequestJsonUtilities.DEFAULT_MAX_TRIES, ModbusWriteFunctionCode.WRITE_MULTIPLE_COILS, true, false, true)))));
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyObject() {
        // we are expecting list, not object -> error
        WriteRequestJsonUtilities.fromJson(3, "{}");
    }

    @Test(expected = IllegalStateException.class)
    public void testNumber() {
        // we are expecting list, not primitive (number) -> error
        WriteRequestJsonUtilities.fromJson(3, "3");
    }

    @Test
    public void testEmptyList() {
        Assert.assertThat(WriteRequestJsonUtilities.fromJson(3, "[]").size(), CoreMatchers.is(CoreMatchers.equalTo(0)));
    }
}

