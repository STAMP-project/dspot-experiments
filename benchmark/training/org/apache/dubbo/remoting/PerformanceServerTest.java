/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting;


import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.junit.jupiter.api.Test;


/**
 * PerformanceServer
 * <p>
 * mvn clean test -Dtest=*PerformanceServerTest -Dport=9911
 */
public class PerformanceServerTest {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceServerTest.class);

    private static ExchangeServer server = null;

    @Test
    public void testServer() throws Exception {
        // Read port from property
        if ((PerformanceUtils.getProperty("port", null)) == null) {
            PerformanceServerTest.logger.warn("Please set -Dport=9911");
            return;
        }
        final int port = PerformanceUtils.getIntProperty("port", 9911);
        final boolean telnet = PerformanceUtils.getBooleanProperty("telnet", true);
        if (telnet)
            PerformanceServerTest.statTelnetServer((port + 1));

        PerformanceServerTest.server = PerformanceServerTest.statServer();
        synchronized(PerformanceServerTest.class) {
            while (true) {
                try {
                    PerformanceServerTest.class.wait();
                } catch (InterruptedException e) {
                }
            } 
        }
    }
}

