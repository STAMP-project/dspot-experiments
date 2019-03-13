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


import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3841Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ3841Test.class);

    private static final int maxFileLength = (1024 * 1024) * 32;

    private static final String destinationName = "TEST.QUEUE";

    BrokerService broker;

    @Test
    public void testRestartAfterQueueDelete() throws Exception {
        // Ensure we have an Admin View.
        Assert.assertTrue("Broker doesn't have an Admin View.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView()) != null;
            }
        }));
        broker.getAdminView().addQueue(AMQ3841Test.destinationName);
        Assert.assertNotNull(broker.getDestination(new ActiveMQQueue(AMQ3841Test.destinationName)));
        broker.getAdminView().removeQueue(AMQ3841Test.destinationName);
        broker.stop();
        broker.waitUntilStopped();
        prepareBrokerWithMultiStore(false);
        broker.start();
        broker.getAdminView().addQueue(AMQ3841Test.destinationName);
        Assert.assertNotNull(broker.getDestination(new ActiveMQQueue(AMQ3841Test.destinationName)));
    }
}

