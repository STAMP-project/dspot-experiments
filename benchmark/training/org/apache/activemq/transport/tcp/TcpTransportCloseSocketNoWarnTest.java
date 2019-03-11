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
package org.apache.activemq.transport.tcp;


import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TcpTransportCloseSocketNoWarnTest {
    private static final Logger LOG = LoggerFactory.getLogger(TcpTransportCloseSocketNoWarnTest.class);

    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private BrokerService brokerService;

    static {
        System.setProperty("javax.net.ssl.trustStore", TcpTransportCloseSocketNoWarnTest.TRUST_KEYSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", TcpTransportCloseSocketNoWarnTest.PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", TcpTransportCloseSocketNoWarnTest.KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStore", TcpTransportCloseSocketNoWarnTest.SERVER_KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", TcpTransportCloseSocketNoWarnTest.PASSWORD);
        System.setProperty("javax.net.ssl.keyStoreType", TcpTransportCloseSocketNoWarnTest.KEYSTORE_TYPE);
    }

    final AtomicBoolean gotExceptionInLog = new AtomicBoolean();

    Appender appender = new DefaultTestAppender() {
        @Override
        public void doAppend(LoggingEvent event) {
            if ((event.getLevel().equals(Level.WARN)) && (event.getRenderedMessage().contains("failed:"))) {
                gotExceptionInLog.set(Boolean.TRUE);
                TcpTransportCloseSocketNoWarnTest.LOG.error(((("got event: " + event) + ", ex:") + (event.getRenderedMessage())));
                TcpTransportCloseSocketNoWarnTest.LOG.error("Event source: ", new Throwable("Here"));
            }
            return;
        }
    };

    @Test(timeout = 60000)
    public void testNoWarn() throws Exception {
        doTest(false);
    }

    @Test(timeout = 60000)
    public void testWarn() throws Exception {
        doTest(true);
    }
}

