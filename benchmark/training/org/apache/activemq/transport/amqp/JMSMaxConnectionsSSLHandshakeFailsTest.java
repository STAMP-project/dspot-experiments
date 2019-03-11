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
package org.apache.activemq.transport.amqp;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test that failed SSL Handshakes don't leave the transport in a bad sate.
 */
@RunWith(Parameterized.class)
public class JMSMaxConnectionsSSLHandshakeFailsTest extends JMSClientTestSupport {
    private static final int MAX_CONNECTIONS = 10;

    private final String connectorScheme;

    public JMSMaxConnectionsSSLHandshakeFailsTest(String connectorScheme) {
        this.connectorScheme = connectorScheme;
    }

    @Test(timeout = 60000)
    public void testFailedSSLConnectionAttemptsDoesNotBreakTransport() throws Exception {
        for (int i = 0; i < (JMSMaxConnectionsSSLHandshakeFailsTest.MAX_CONNECTIONS); ++i) {
            try {
                createFailingConnection();
                Assert.fail("Should not be able to connect.");
            } catch (Exception ex) {
                AmqpTestSupport.LOG.debug("Connection failed as expected");
            }
        }
        for (int i = 0; i < (JMSMaxConnectionsSSLHandshakeFailsTest.MAX_CONNECTIONS); ++i) {
            try {
                createNonSslConnection().start();
                Assert.fail("Should not be able to connect.");
            } catch (Exception ex) {
                AmqpTestSupport.LOG.debug("Connection failed as expected");
            }
        }
        for (int i = 0; i < (JMSMaxConnectionsSSLHandshakeFailsTest.MAX_CONNECTIONS); ++i) {
            try {
                createGoodConnection();
                AmqpTestSupport.LOG.debug("Connection created as expected");
            } catch (Exception ex) {
                Assert.fail(("Should be able to connect: " + (ex.getMessage())));
            }
        }
        Assert.assertEquals(0, getProxyToBroker().getCurrentConnectionsCount());
    }
}

