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
package org.apache.activemq.proxy;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSSecurityException;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4889Test {
    protected static final Logger LOG = LoggerFactory.getLogger(AMQ4889Test.class);

    public static final String USER = "user";

    public static final String GOOD_USER_PASSWORD = "password";

    public static final String WRONG_PASSWORD = "wrongPassword";

    public static final String PROXY_URI_PREFIX = "tcp://localhost:";

    public static final String LOCAL_URI_PREFIX = "tcp://localhost:";

    private String proxyURI;

    private String localURI;

    private BrokerService brokerService;

    private ProxyConnector proxyConnector;

    private ConnectionFactory connectionFactory;

    private static final Integer ITERATIONS = 100;

    @Test(timeout = 60000)
    public void testForConnectionLeak() throws Exception {
        Integer expectedConnectionCount = 0;
        for (int i = 0; i < (AMQ4889Test.ITERATIONS); i++) {
            try {
                if ((i % 2) == 0) {
                    AMQ4889Test.LOG.debug("Iteration {} adding bad connection", i);
                    Connection connection = connectionFactory.createConnection(AMQ4889Test.USER, AMQ4889Test.WRONG_PASSWORD);
                    connection.createSession(false, AUTO_ACKNOWLEDGE);
                    Assert.fail("createSession should fail");
                } else {
                    AMQ4889Test.LOG.debug("Iteration {} adding good connection", i);
                    Connection connection = connectionFactory.createConnection(AMQ4889Test.USER, AMQ4889Test.GOOD_USER_PASSWORD);
                    connection.createSession(false, AUTO_ACKNOWLEDGE);
                    expectedConnectionCount++;
                }
            } catch (JMSSecurityException e) {
            }
            AMQ4889Test.LOG.debug("Iteration {} Connections? {}", i, proxyConnector.getConnectionCount());
        }
        final Integer val = expectedConnectionCount;
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return val.equals(proxyConnector.getConnectionCount());
            }
        }, 20);
        Assert.assertEquals(val, proxyConnector.getConnectionCount());
    }
}

