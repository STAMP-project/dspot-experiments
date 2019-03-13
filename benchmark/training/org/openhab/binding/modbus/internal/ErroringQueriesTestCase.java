/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import ModbusBindingProvider.TYPE_COIL;
import ModbusBindingProvider.TYPE_DISCRETE;
import OnOffType.ON;
import UnDefType.UNDEF;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import org.junit.Test;
import org.mockito.Mockito;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.service.cm.ConfigurationException;

import static org.openhab.binding.modbus.internal.TestCaseSupport.ServerType.TCP;


/**
 * Tests for erroring queries. Run only against TCP server.
 */
public class ErroringQueriesTestCase extends TestCaseSupport {
    @SuppressWarnings("serial")
    public static class ExpectedFailure extends AssertionError {}

    /**
     * Test case for situation where we try to poll too much data.
     *
     * In this test server has
     * - single discrete input
     * - single coil inputs
     *
     * Binding is configured to read
     * - two coils
     *
     * Items are follows
     * - first discrete input (Item1) -> no output since query
     * should fail
     */
    @Test
    public void testReadingTooMuch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalOut(new SimpleDigitalOut(true));
        spi.addDigitalOut(new SimpleDigitalOut(false));
        binding = new ModbusBinding();
        Dictionary<String, Object> config = TestCaseSupport.newLongPollBindingConfig();
        addSlave(config, TestCaseSupport.SLAVE_NAME, TYPE_DISCRETE, null, 0, 2);
        binding.updated(config);
        // Configure items
        final ModbusGenericBindingProvider provider = new ModbusGenericBindingProvider();
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("%s:%d", TestCaseSupport.SLAVE_NAME, 0));
        binding.setEventPublisher(eventPublisher);
        binding.addBindingProvider(provider);
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testConnectionTimeout() throws InterruptedException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        /**
         * Test that connection timeout is handled properly
         *
         * In the test we have non-responding slave (see http://stackoverflow.com/a/904609), and we use connection
         * timeout of 300ms
         *
         * We assert that after 100ms the binding still have not given up on the connection (no UNDEF posted to the
         * event bus)
         * but after 400ms from the connection, the UNDEF is there.
         */
        binding = new ModbusBinding();
        Dictionary<String, Object> config = TestCaseSupport.newLongPollBindingConfig();
        TestCaseSupport.addSlave(config, TCP, "10.255.255.1:9999:0:0:0:1:300", TestCaseSupport.SLAVE_NAME, TYPE_DISCRETE, null, 0, 0, 2);
        TestCaseSupport.putSlaveConfigParameter(config, serverType, TestCaseSupport.SLAVE_NAME, "postundefinedonreaderror", "true");
        binding.updated(config);
        // Configure items
        final ModbusGenericBindingProvider provider = new ModbusGenericBindingProvider();
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("%s:%d", TestCaseSupport.SLAVE_NAME, 0));
        binding.setEventPublisher(eventPublisher);
        binding.addBindingProvider(provider);
        final CountDownLatch lock = new CountDownLatch(1);
        Thread thread = new Thread() {
            @Override
            public void run() {
                binding.execute();
                lock.countDown();
            }
        };
        try {
            thread.start();
            lock.await(100, TimeUnit.MILLISECONDS);
            // After 100ms the binding has not yet given up, i.e. no UNDEF posted to event bus
            Mockito.verifyNoMoreInteractions(eventPublisher);
            // After 100ms+300ms+margin 500ms the timeout of 300ms has passed and UNDEF should have been posted
            lock.await(((100 + 300) + 500), TimeUnit.MILLISECONDS);
            Mockito.verify(eventPublisher).postUpdate("Item1", UNDEF);
            Mockito.verifyNoMoreInteractions(eventPublisher);
        } finally {
            thread.interrupt();
        }
    }

    @Test
    public void testReadingTooMuchWithPostUndef() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalOut(new SimpleDigitalOut(true));
        spi.addDigitalOut(new SimpleDigitalOut(false));
        binding = new ModbusBinding();
        Dictionary<String, Object> config = TestCaseSupport.newLongPollBindingConfig();
        addSlave(config, TestCaseSupport.SLAVE_NAME, TYPE_DISCRETE, null, 0, 2);
        TestCaseSupport.putSlaveConfigParameter(config, serverType, TestCaseSupport.SLAVE_NAME, "postundefinedonreaderror", "true");
        binding.updated(config);
        // Configure items
        final ModbusGenericBindingProvider provider = new ModbusGenericBindingProvider();
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("%s:%d", TestCaseSupport.SLAVE_NAME, 0));
        binding.setEventPublisher(eventPublisher);
        binding.addBindingProvider(provider);
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", UNDEF);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    /**
     * Test case for situation where we try to poll too much data.
     *
     * In this test server has
     * - single coil
     * - two discrete inputs
     *
     * Binding is configured to read
     * - two coils
     * - two discrete inputs
     *
     * Items are follows
     * - first (index=0) coil (Item1) -> no output since coil query should fail
     * - index=1 discrete (Item2) should be OK
     * - index=2 discrete (Item3) no event transmitted, item readIndex out of bounds. WARN logged
     */
    @Test
    public void testReadingTooMuchTwoSlaves() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        spi.addDigitalOut(new SimpleDigitalOut(true));
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalIn(new SimpleDigitalIn(true));
        binding = new ModbusBinding();
        Dictionary<String, Object> config = TestCaseSupport.newLongPollBindingConfig();
        addSlave(config, TestCaseSupport.SLAVE_NAME, TYPE_COIL, null, 0, 2);
        addSlave(config, TestCaseSupport.SLAVE2_NAME, TYPE_DISCRETE, null, 0, 2);
        binding.updated(config);
        // Configure items
        final ModbusGenericBindingProvider provider = new ModbusGenericBindingProvider();
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("%s:%d", TestCaseSupport.SLAVE_NAME, 0));
        provider.processBindingConfiguration("test.items", new SwitchItem("Item2"), String.format("%s:%d", TestCaseSupport.SLAVE2_NAME, 1));
        provider.processBindingConfiguration("test.items", new SwitchItem("Item3"), String.format("%s:%d", TestCaseSupport.SLAVE2_NAME, 2));
        binding.setEventPublisher(eventPublisher);
        binding.addBindingProvider(provider);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForConnectionsReceived(2);
        waitForRequests(2);
        Mockito.verify(eventPublisher, Mockito.never()).postCommand(null, null);
        Mockito.verify(eventPublisher, Mockito.never()).sendCommand(null, null);
        Mockito.verify(eventPublisher).postUpdate("Item2", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }
}

