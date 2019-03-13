/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.rest;


import GridClientProtocol.TCP;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.internal.client.GridClientConfiguration;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 *
 */
public class RestProcessorStartSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String HOST = "127.0.0.1";

    /**
     *
     */
    public static final int TCP_PORT = 11222;

    /**
     *
     */
    private CountDownLatch gridReady;

    /**
     *
     */
    private CountDownLatch proceed;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTcpStart() throws Exception {
        GridClientConfiguration clCfg = new GridClientConfiguration();
        clCfg.setProtocol(TCP);
        clCfg.setServers(Collections.singleton((((RestProcessorStartSelfTest.HOST) + ":") + (RestProcessorStartSelfTest.TCP_PORT))));
        doTest(clCfg);
    }

    /**
     * Test SPI.
     */
    private class TestDiscoverySpi extends TcpDiscoverySpi {
        /**
         * {@inheritDoc }
         */
        @Override
        public void spiStart(@Nullable
        String igniteInstanceName) throws IgniteSpiException {
            gridReady.countDown();
            try {
                proceed.await();
            } catch (InterruptedException e) {
                throw new IgniteSpiException("Failed to await start signal.", e);
            }
            super.spiStart(igniteInstanceName);
        }
    }
}

