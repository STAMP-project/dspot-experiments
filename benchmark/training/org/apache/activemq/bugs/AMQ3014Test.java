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
package org.apache.activemq.bugs;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.Connection;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.BrokerInfo;
import org.junit.Test;


/**
 * This test involves the creation of a local and remote broker, both of which
 * communicate over VM and TCP. The local broker establishes a bridge to the
 * remote broker for the purposes of verifying that broker info is only
 * transfered once the local broker's ID is known to the bridge support.
 */
public class AMQ3014Test {
    // Change this URL to be an unused port.
    private static final String BROKER_URL = "tcp://localhost:0";

    private List<BrokerInfo> remoteBrokerInfos = Collections.synchronizedList(new ArrayList<BrokerInfo>());

    private BrokerService localBroker = new BrokerService();

    // Override the "remote" broker so that it records all (remote) BrokerInfos
    // that it receives.
    private BrokerService remoteBroker = new BrokerService() {
        @Override
        protected TransportConnector createTransportConnector(URI brokerURI) throws Exception {
            TransportServer transport = TransportFactorySupport.bind(this, brokerURI);
            return new TransportConnector(transport) {
                @Override
                protected Connection createConnection(Transport transport) throws IOException {
                    Connection connection = super.createConnection(transport);
                    final TransportListener proxiedListener = transport.getTransportListener();
                    transport.setTransportListener(new TransportListener() {
                        @Override
                        public void onCommand(Object command) {
                            if (command instanceof BrokerInfo) {
                                remoteBrokerInfos.add(((BrokerInfo) (command)));
                            }
                            proxiedListener.onCommand(command);
                        }

                        @Override
                        public void onException(IOException error) {
                            proxiedListener.onException(error);
                        }

                        @Override
                        public void transportInterupted() {
                            proxiedListener.transportInterupted();
                        }

                        @Override
                        public void transportResumed() {
                            proxiedListener.transportResumed();
                        }
                    });
                    return connection;
                }
            };
        }
    };

    /**
     * This test verifies that the local broker's ID is typically known by the
     * bridge support before the local broker's BrokerInfo is sent to the remote
     * broker.
     */
    @Test
    public void NormalCaseTest() throws Exception {
        runTest(0, 3000);
    }

    /**
     * This test verifies that timing can arise under which the local broker's
     * ID is not known by the bridge support before the local broker's
     * BrokerInfo is sent to the remote broker.
     */
    @Test
    public void DelayedCaseTest() throws Exception {
        runTest(500, 3000);
    }
}

