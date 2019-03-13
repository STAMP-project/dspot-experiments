/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import OnOffType.OFF;
import OnOffType.ON;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import net.wimpi.modbus.procimg.Register;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.service.cm.ConfigurationException;


/**
 * Parameterized test case that tests reading of both coils (i.e. boolean 0/1
 * output signals) and discrete inputs (i.e. boolean 0/1 inputs signals)
 * registers
 */
@RunWith(Parameterized.class)
public class ReadCoilsAndDiscreteTestCase extends TestCaseSupport {
    private boolean nonZeroOffset;

    private Constructor<Register> constructBoolStore;

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
     * @param storeClass
     * 		"register" class to instantiate when configuring SPI of the
     * 		server
     * @param spiAddRegisterMethodName
     * 		method to call when adding register to SPI
     * @param addRegisterArgClass
     * 		argument type of the method corresponding to
     * 		spiAddRegisterMethodName
     */
    public ReadCoilsAndDiscreteTestCase(TestCaseSupport.ServerType serverType, boolean nonZeroOffset, String type, Class<Register> storeClass, String spiAddRegisterMethodName, Class<?> addRegisterArgClass) throws NoSuchMethodException, SecurityException {
        this.serverType = serverType;
        this.nonZeroOffset = nonZeroOffset;
        this.type = type;
        this.spiAddRegisterMethodName = spiAddRegisterMethodName;
        this.addRegisterArgClass = addRegisterArgClass;
        constructBoolStore = storeClass.getDeclaredConstructor(new Class[]{ boolean.class });
    }

    /**
     * Test reading of discrete inputs/outputs, uses default valuetype
     */
    @Test
    public void testReadDigitals() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has two digital inputs/outputs
        addRegisterMethod.invoke(spi, constructBoolStore.newInstance(false));
        addRegisterMethod.invoke(spi, constructBoolStore.newInstance(true));
        addRegisterMethod.invoke(spi, constructBoolStore.newInstance(false));
        binding = new ModbusBinding();
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, null, (nonZeroOffset ? 1 : 0), 2));
        configureSwitchItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        if (nonZeroOffset) {
            Mockito.verify(eventPublisher).postUpdate("Item1", ON);
            Mockito.verify(eventPublisher).postUpdate("Item2", OFF);
        } else {
            Mockito.verify(eventPublisher).postUpdate("Item1", OFF);
            Mockito.verify(eventPublisher).postUpdate("Item2", ON);
        }
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }
}

