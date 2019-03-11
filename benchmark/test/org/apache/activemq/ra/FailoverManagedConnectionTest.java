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
package org.apache.activemq.ra;


import java.util.HashSet;
import javax.resource.spi.ManagedConnection;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;


public class FailoverManagedConnectionTest {
    private static final String BROKER_TRANSPORT = "tcp://localhost:61616";

    private static final String BROKER_URL = "failover://" + (FailoverManagedConnectionTest.BROKER_TRANSPORT);

    private static final String KAHADB_DIRECTORY = "target/activemq-data/";

    private ActiveMQManagedConnectionFactory managedConnectionFactory;

    private ManagedConnection managedConnection;

    private ManagedConnectionProxy proxy;

    private BrokerService broker;

    private HashSet<ManagedConnection> connections;

    private ActiveMQConnectionRequestInfo connectionInfo;

    @Test(timeout = 60000)
    public void testFailoverBeforeClose() throws Exception {
        createConnectionAndProxyAndSession();
        stopBroker();
        cleanupConnectionAndProxyAndSession();
        createAndStartBroker();
        for (int i = 0; i < 2; i++) {
            createConnectionAndProxyAndSession();
            cleanupConnectionAndProxyAndSession();
        }
    }
}

