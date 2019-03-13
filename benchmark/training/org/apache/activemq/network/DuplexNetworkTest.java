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
package org.apache.activemq.network;


import java.util.concurrent.TimeUnit;
import javax.jms.MessageProducer;
import javax.jms.TemporaryQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DuplexNetworkTest extends SimpleNetworkTest {
    private static final Logger LOG = LoggerFactory.getLogger(DuplexNetworkTest.class);

    @Test
    public void testTempQueues() throws Exception {
        TemporaryQueue temp = localSession.createTemporaryQueue();
        MessageProducer producer = localSession.createProducer(temp);
        producer.send(localSession.createTextMessage("test"));
        Thread.sleep(100);
        Assert.assertEquals("Destination not created", 1, remoteBroker.getAdminView().getTemporaryQueues().length);
        temp.delete();
        Assert.assertTrue("Destination not deleted", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (remoteBroker.getAdminView().getTemporaryQueues().length);
            }
        }));
    }

    @Test
    public void testStaysUp() throws Exception {
        int bridgeIdentity = getBridgeId();
        DuplexNetworkTest.LOG.info(("Bridges: " + bridgeIdentity));
        TimeUnit.SECONDS.sleep(5);
        Assert.assertEquals("Same bridges", bridgeIdentity, getBridgeId());
    }
}

