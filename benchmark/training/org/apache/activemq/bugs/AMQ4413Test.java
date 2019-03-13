/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class AMQ4413Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ4413Test.class);

    final String brokerUrl = "tcp://localhost:0";

    private String connectionUri;

    final int numMsgsTriggeringReconnection = 2;

    final int numMsgs = 30;

    final int numTests = 75;

    final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Test
    public void testDurableSubMessageLoss() throws Exception {
        // start embedded broker
        BrokerService brokerService = new BrokerService();
        connectionUri = brokerService.addConnector(brokerUrl).getPublishableConnectString();
        brokerService.setPersistent(false);
        brokerService.setUseJmx(false);
        brokerService.setKeepDurableSubsActive(true);
        brokerService.setAdvisorySupport(false);
        brokerService.start();
        AMQ4413Test.LOG.info("##### broker started");
        // repeat test 50 times
        try {
            for (int i = 0; i < (numTests); ++i) {
                AMQ4413Test.LOG.info((("##### test " + i) + " started"));
                test();
            }
            AMQ4413Test.LOG.info("##### tests are done");
        } catch (Exception e) {
            e.printStackTrace();
            AMQ4413Test.LOG.info("##### tests failed!");
        } finally {
            threadPool.shutdown();
            brokerService.stop();
            AMQ4413Test.LOG.info("##### broker stopped");
        }
    }
}

