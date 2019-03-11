/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import ModbusBindingProvider.VALUE_TYPE_BIT;
import ModbusBindingProvider.VALUE_TYPE_FLOAT32;
import ModbusBindingProvider.VALUE_TYPE_INT16;
import ModbusBindingProvider.VALUE_TYPE_INT32;
import ModbusBindingProvider.VALUE_TYPE_INT32_SWAP;
import ModbusBindingProvider.VALUE_TYPE_INT8;
import ModbusBindingProvider.VALUE_TYPE_UINT32;
import ModbusBindingProvider.VALUE_TYPE_UINT8;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Dictionary;
import net.wimpi.modbus.procimg.Register;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.openhab.binding.modbus.ModbusBindingProvider;
import org.openhab.core.library.types.DecimalType;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.service.cm.ConfigurationException;


/**
 * Parameterized test case that tests reading of both input and holding
 * registers
 */
@RunWith(Parameterized.class)
public class ReadRegistersTestCase extends TestCaseSupport {
    private boolean nonZeroOffset;

    private Constructor<Register> constructRegisterInt;

    private Constructor<Register> constructRegister2Byte;

    private String type;

    private Method addRegisterMethod;

    private String spiAddRegisterMethodName;

    private Class<?> addRegisterArgClass;

    /**
     *
     *
     * @param serverType
     * 		
     * @param nonZeroOffset
     * 		whether to test non-zero start address in modbus binding
     * @param type
     * 		type of the slave (e.g. "holding")
     * @param registerClass
     * 		register class to instantiate when configuring SPI of the
     * 		server
     * @param spiAddRegisterMethodName
     * 		method to call when adding register to SPI
     * @param addRegisterArgClass
     * 		argument type of the method corresponding to
     * 		spiAddRegisterMethodName
     */
    public ReadRegistersTestCase(TestCaseSupport.ServerType serverType, boolean nonZeroOffset, String type, Class<Register> registerClass, String spiAddRegisterMethodName, Class<?> addRegisterArgClass) throws NoSuchMethodException, SecurityException {
        this.serverType = serverType;
        this.nonZeroOffset = nonZeroOffset;
        this.type = type;
        this.spiAddRegisterMethodName = spiAddRegisterMethodName;
        this.addRegisterArgClass = addRegisterArgClass;
        constructRegisterInt = registerClass.getDeclaredConstructor(new Class[]{ int.class });
        constructRegister2Byte = registerClass.getDeclaredConstructor(new Class[]{ byte.class, byte.class });
    }

    /**
     * Test reading of input/holding registers, uses valuetype=int8
     */
    @Test
    public void testReadRegistersInt8() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        // first register has following bytes (hi byte, lo byte)
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(((byte) (1)), ((byte) (2))));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(((byte) (3)), ((byte) (-4))));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(((byte) (5)), ((byte) (6))));
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(99));
        binding = new ModbusBinding();
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_INT8, (nonZeroOffset ? 1 : 0), 2));
        configureNumberItemBinding(4, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            // 2nd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType((-4)));
            // 2nd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(3));
            // 3rd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item3", new DecimalType(6));
            // 3rd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item4", new DecimalType(5));
        } else {
            // 1st register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(2));
            // 1st register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(1));
            // 2nd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item3", new DecimalType((-4)));
            // 2nd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item4", new DecimalType(3));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses valuetype=uint8
     */
    @Test
    public void testReadRegistersUint8() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        // first register has following bytes (hi byte, lo byte)
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(((byte) (1)), ((byte) (2))));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(((byte) (3)), ((byte) (-4))));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(((byte) (5)), ((byte) (6))));
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(99));
        binding = new ModbusBinding();
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_UINT8, (nonZeroOffset ? 1 : 0), 2));
        Assert.assertEquals(TestCaseSupport.REFRESH_INTERVAL, binding.getRefreshInterval());
        configureNumberItemBinding(4, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            // 2nd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType((256 - 4)));
            // 2nd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(3));
            // 3rd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item3", new DecimalType(6));
            // 3rd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item4", new DecimalType(5));
        } else {
            // 1st register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(2));
            // 1st register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(1));
            // 2nd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("Item3", new DecimalType((256 - 4)));
            // 2nd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("Item4", new DecimalType(3));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses default valuetype
     */
    @Test
    public void testReadRegistersUint16() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(2));
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance((-4)));
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(99));
        binding = new ModbusBinding();
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, null, (nonZeroOffset ? 1 : 0), 2));
        configureNumberItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(65532));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(99));
        } else {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(2));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(65532));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses valuetype=int16
     */
    @Test
    public void testReadRegistersInt16() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(2));
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance((-4)));
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(99));
        binding = new ModbusBinding();
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_INT16, (nonZeroOffset ? 1 : 0), 2));
        configureNumberItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType((-4)));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(99));
        } else {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(2));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType((-4)));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses valuetype=bit
     *
     * Items refer to individual bits (lowest significant bit = index 0) of the
     * 16bits registers.
     *
     * In this test, we have items referring to the all 32bits (two registers)
     */
    @Test
    public void testReadRegistersBit() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        // 0x0002 = 00000000 00000010
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(2));
        // 0xFFFC = 11111111 11111100
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance((-4)));
        // 0x0063 = 00000000 01100011
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(99));
        binding = new ModbusBinding();
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_BIT, (nonZeroOffset ? 1 : 0), 2));
        configureSwitchItemBinding(32, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        // Bits should correspond to bits of the register, in LSB order.
        if (nonZeroOffset) {
            verifyBitItems(new StringBuffer("1111111111111100").reverse().toString());
            verifyBitItems(new StringBuffer("0000000001100011").reverse().toString(), 16);
        } else {
            // 1st register bits
            verifyBitItems(new StringBuffer("0000000000000010").reverse().toString());
            // 2nd register bits
            verifyBitItems(new StringBuffer("1111111111111100").reverse().toString(), 16);
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses valuetype=uint32
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadRegistersUint32() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        byte[] registerData = int32AsRegisters(123456789);// 0x075BCD15

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = int32AsRegisters((-123456789));// 0xF8A432EB

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = int32AsRegisters(123456788);// 0x075BCD14

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        binding = new ModbusBinding();
        // read 4 registers = 2 uint32 numbers
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_UINT32, (nonZeroOffset ? 1 : 0), 4));
        configureNumberItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            // 3440769188 = 0xCD15 F8A4 = (1st register lo byte, 2nd register hi
            // byte)
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(3440769188L));
            // 854263643 = 0x32EB075B = (2nd register lo byte, 3rd register hi
            // byte)
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(854263643));
        } else {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(123456789));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType((4294967296L - 123456789L)));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses valuetype=int32
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadRegistersInt32() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        byte[] registerData = int32AsRegisters(123456789);// 0x075BCD15

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = int32AsRegisters((-123456789));// 0xF8A432EB

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = int32AsRegisters(123456788);// 0x075BCD14

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        binding = new ModbusBinding();
        // read 4 registers = 2 uint32 numbers
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_INT32, (nonZeroOffset ? 1 : 0), 4));
        configureNumberItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            // -854198108 = 0xCD15F8A4 = (1st register lo byte, 2nd register hi
            // byte)
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType((-854198108)));
            // 854263643 = 0x32EB 075B = (2nd register lo byte, 3rd register hi
            // byte)
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType(854263643));
        } else {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(123456789));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType((-123456789)));
        }
    }

    /**
     * Test reading of input/holding registers, uses valuetype=int32_swap
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadRegistersInt32Swap() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        byte[] registerData = int32AsRegistersSwapped(123456789);// 0x075BCD15

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = int32AsRegistersSwapped((-123456789));// 0xF8A432EB

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = int32AsRegistersSwapped(123456788);// 0x075BCD14

        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        binding = new ModbusBinding();
        // read 4 registers = 2 uint32 numbers
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_INT32_SWAP, (nonZeroOffset ? 1 : 0), 4));
        configureNumberItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        // register data:
        // CD15 075B
        // 32EB F8A4
        // CD14 075B
        if (nonZeroOffset) {
            // 075B32EB interpreted as CDAB int32 (1st register lo byte, 2nd register hi
            // byte)
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(854263643));
            // F8A4CD14 interpreted as CDAB int32 (2nd register lo byte, 3rd register hi
            // byte)
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType((-854263644)));
        } else {
            Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(123456789));
            Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType((-123456789)));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading of input/holding registers, uses valuetype=float32
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadRegistersFloat32() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has input registers
        byte[] registerData = float32AsRegisters(1.23456792E8F);
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        registerData = float32AsRegisters((-1.23456792E8F));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[0], registerData[1]));
        addRegisterMethod.invoke(spi, constructRegister2Byte.newInstance(registerData[2], registerData[3]));
        binding = new ModbusBinding();
        // read 4 registers = 2 uint32 numbers
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, VALUE_TYPE_FLOAT32, 0, 4));
        configureNumberItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(1.23456792E8F));
        Mockito.verify(eventPublisher).postUpdate("Item2", new DecimalType((-1.23456792E8F)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test reading same registers using different value types.
     */
    @Test
    public void testReadRegistersMultipleWays() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // 0x0002 = 00000000 00000010
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(2));
        // 0xFFFC = 11111111 11111100
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance((-4)));
        // 0x0063 = 00000000 01100011
        addRegisterMethod.invoke(spi, constructRegisterInt.newInstance(99));
        binding = new ModbusBinding();
        Dictionary<String, Object> cfg = TestCaseSupport.newLongPollBindingConfig();
        for (String valueType : new String[]{ ModbusBindingProvider.VALUE_TYPE_BIT, ModbusBindingProvider.VALUE_TYPE_UINT8, ModbusBindingProvider.VALUE_TYPE_INT16 }) {
            addSlave(cfg, ((TestCaseSupport.SLAVE_NAME) + valueType), type, valueType, (nonZeroOffset ? 1 : 0), 2);
        }
        binding.updated(cfg);
        // Here we test only some of the read values (int16 read but not tested)
        configureSwitchItemBinding(32, ((TestCaseSupport.SLAVE_NAME) + (ModbusBindingProvider.VALUE_TYPE_BIT)), 0, "B", null);
        configureNumberItemBinding(4, ((TestCaseSupport.SLAVE_NAME) + (ModbusBindingProvider.VALUE_TYPE_UINT8)), 0, "UI8", null);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        // We expect as many requests and connections as there are slaves conifigured for the binding.
        // Note: same registers are read many times, there is currently no optimization implemented for this use case
        waitForConnectionsReceived(3);
        waitForRequests(3);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        // verify bit items
        if (nonZeroOffset) {
            verifyBitItems(new StringBuffer("1111111111111100").reverse().toString(), 0, "B");
            verifyBitItems(new StringBuffer("0000000001100011").reverse().toString(), 16, "B");
        } else {
            // 1st register bits
            verifyBitItems(new StringBuffer("0000000000000010").reverse().toString(), 0, "B");
            // 2nd register bits
            verifyBitItems(new StringBuffer("1111111111111100").reverse().toString(), 16, "B");
        }
        // verify int8 items
        if (nonZeroOffset) {
            // 2nd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item1", new DecimalType(252));
            // 2nd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item2", new DecimalType(255));
            // 3rd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item3", new DecimalType(99));
            // 3rd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item4", new DecimalType(0));
        } else {
            // 1st register, lo byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item1", new DecimalType(2));
            // 1st register, hi byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item2", new DecimalType(0));
            // 2nd register, lo byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item3", new DecimalType(252));
            // 2nd register, hi byte
            Mockito.verify(eventPublisher).postUpdate("UI8Item4", new DecimalType(255));
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }
}

