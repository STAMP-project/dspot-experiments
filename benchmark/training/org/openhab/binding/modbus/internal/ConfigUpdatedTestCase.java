/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import ModbusBindingProvider.TYPE_DISCRETE;
import OnOffType.OFF;
import OnOffType.ON;
import java.net.UnknownHostException;
import java.util.Dictionary;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openhab.binding.modbus.internal.TestCaseSupport.ServerType.TCP;
import static org.openhab.binding.modbus.internal.TestCaseSupport.ServerType.UDP;


/**
 * Testing how configuration update is handled
 */
@RunWith(Parameterized.class)
public class ConfigUpdatedTestCase extends TestCaseSupport {
    @SuppressWarnings("serial")
    public static class ExpectedFailure extends AssertionError {
        public ExpectedFailure(Throwable cause) {
            initCause(cause);
        }
    }

    public ConfigUpdatedTestCase(TestCaseSupport.ServerType serverType) {
        super();
        this.serverType = serverType;
        // Server is a bit slower to respond than normally
        // this for the testConfigUpdatedWhilePolling
        this.artificialServerWait = 1000;// Also remember default timeout for tcp Modbus.DEFAULT_TIMEOUT

    }

    @Test
    public void testConfigUpdated() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        // Modbus server ("modbus slave") has two digital inputs
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalIn(new SimpleDigitalIn(false));
        binding = new ModbusBinding();
        // simulate configuration changes
        for (int i = 0; i < 2; i++) {
            binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, TYPE_DISCRETE, null, 0, 2));
        }
        configureSwitchItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        binding.execute();
        // Give the system some time to make the expected connections & requests
        waitForRequests(1);
        if (!(serverType.equals(UDP))) {
            waitForConnectionsReceived(1);
        }
        verifyEvents();
    }

    /**
     * To verify fix for https://github.com/openhab/openhab1-addons/issues/5078
     *
     * @throws UnknownHostException
     * 		
     * @throws org.osgi.service.cm.ConfigurationException
     * 		
     * @throws BindingConfigParseException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testConfigUpdatedWhilePolling() throws InterruptedException, UnknownHostException, BindingConfigParseException, ConfigurationException {
        final Logger logger = LoggerFactory.getLogger(ConfigUpdatedTestCase.class);
        // run this test only for tcp server due to the customized connection string
        Assume.assumeTrue(serverType.equals(TCP));
        MAX_WAIT_REQUESTS_MILLIS = 10000;
        spi.addDigitalIn(new SimpleDigitalIn(true));
        spi.addDigitalIn(new SimpleDigitalIn(false));
        binding = new ModbusBinding();
        // Customized connection settings, keep the connection open for 2000s, no connection retries, 300ms connection
        // timeout
        String connection = String.format("%s:%d:30:2000000:0:1:300", TestCaseSupport.localAddress().getHostAddress(), tcpModbusPort);
        Dictionary<String, Object> cfg = TestCaseSupport.addSlave(TestCaseSupport.newLongPollBindingConfig(), serverType, connection, TestCaseSupport.SLAVE_NAME, TYPE_DISCRETE, null, 1, 0, 2);
        TestCaseSupport.putSlaveConfigParameter(cfg, serverType, TestCaseSupport.SLAVE_NAME, "updateunchangeditems", "true");
        binding.updated(cfg);
        configureSwitchItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        Thread executeOnBackground = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("First execution started");
                binding.execute();
                logger.info("First execution finished");
            }
        });
        executeOnBackground.start();
        Thread.sleep(100);
        // Connection should be now open (since ~100ms passed since connection)
        // But the first query is still on the way (since server is so slow)
        // Simulate config update
        // any returned connections to the old connection pool will be closed immediately on return
        binding.updated(cfg);
        // Let the previous polling round end before entering the next round
        // We are not really interested what would happen in the "transient period"
        // where the old connections are ongoing at the same time as the new ones
        executeOnBackground.join();
        // Polling should work after config update
        logger.info("Second execution started");
        binding.execute();
        logger.info("Second execution finished");
        // three requests, two of those due to execute() commands in this test,
        // one due to initial automatic execute() (when updated the first time)
        waitForRequests(3);
        // two connections, connection is closed on second updated(), and thus connection needs to re-initated
        waitForConnectionsReceived(2);
        Mockito.verify(eventPublisher, Mockito.times(3)).postUpdate("Item1", ON);
        Mockito.verify(eventPublisher, Mockito.times(3)).postUpdate("Item2", OFF);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }
}

