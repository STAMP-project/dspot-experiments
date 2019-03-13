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


import gnu.io.SerialPort;
import net.wimpi.modbus.Modbus;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.io.transport.modbus.endpoint.ModbusSerialSlaveEndpoint;
import org.openhab.io.transport.modbus.endpoint.ModbusTCPSlaveEndpoint;
import org.openhab.io.transport.modbus.endpoint.ModbusUDPSlaveEndpoint;


/**
 *
 *
 * @author Sami Salonen
 */
public class ModbusSlaveEndpointTestCase {
    @Test
    public void testEqualsSameTcp() {
        ModbusTCPSlaveEndpoint e1 = new ModbusTCPSlaveEndpoint("127.0.0.1", 500);
        ModbusTCPSlaveEndpoint e2 = new ModbusTCPSlaveEndpoint("127.0.0.1", 500);
        Assert.assertEquals(e1, e2);
    }

    @Test
    public void testEqualsSameSerial2() {
        ModbusSerialSlaveEndpoint e1 = new ModbusSerialSlaveEndpoint("port1", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, true, 500);
        ModbusSerialSlaveEndpoint e2 = new ModbusSerialSlaveEndpoint("port1", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, true, 500);
        Assert.assertEquals(e1, e2);
    }

    /**
     * even though different echo parameter & baud rate, the endpoints are considered the same due to same port
     */
    @Test
    public void testEqualsSameSerial3() {
        ModbusSerialSlaveEndpoint e1 = new ModbusSerialSlaveEndpoint("port1", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, true, 500);
        ModbusSerialSlaveEndpoint e2 = new ModbusSerialSlaveEndpoint("port1", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, false, 500);
        Assert.assertEquals(e1, e2);
        Assert.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void testEqualsDifferentSerial() {
        ModbusSerialSlaveEndpoint e1 = new ModbusSerialSlaveEndpoint("port1", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, true, 500);
        ModbusSerialSlaveEndpoint e2 = new ModbusSerialSlaveEndpoint("port2", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, true, 500);
        Assert.assertNotEquals(e1, e2);
        Assert.assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void testEqualsDifferentTCPPort() {
        ModbusTCPSlaveEndpoint e1 = new ModbusTCPSlaveEndpoint("127.0.0.1", 500);
        ModbusTCPSlaveEndpoint e2 = new ModbusTCPSlaveEndpoint("127.0.0.1", 501);
        Assert.assertNotEquals(e1, e2);
        Assert.assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void testEqualsDifferentTCPHost() {
        ModbusTCPSlaveEndpoint e1 = new ModbusTCPSlaveEndpoint("127.0.0.1", 500);
        ModbusTCPSlaveEndpoint e2 = new ModbusTCPSlaveEndpoint("127.0.0.2", 501);
        Assert.assertNotEquals(e1, e2);
        Assert.assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void testEqualsDifferentProtocol() {
        ModbusTCPSlaveEndpoint e1 = new ModbusTCPSlaveEndpoint("127.0.0.1", 500);
        ModbusUDPSlaveEndpoint e2 = new ModbusUDPSlaveEndpoint("127.0.0.1", 500);
        Assert.assertNotEquals(e1, e2);
        Assert.assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void testEqualsDifferentProtocol2() {
        ModbusTCPSlaveEndpoint e1 = new ModbusTCPSlaveEndpoint("127.0.0.1", 500);
        ModbusSerialSlaveEndpoint e2 = new ModbusSerialSlaveEndpoint("port2", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, true, 500);
        Assert.assertNotEquals(e1, e2);
        Assert.assertNotEquals(e1.hashCode(), e2.hashCode());
    }
}

