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
package org.apache.activemq.usecases;


import java.util.concurrent.TimeUnit;
import javax.jms.Message;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DurableSubscriptionHangTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(DurableSubscriptionHangTestCase.class);

    static final String brokerName = "DurableSubscriptionHangTestCase";

    static final String clientID = "myId";

    private static final String topicName = "myTopic";

    private static final String durableSubName = "mySub";

    BrokerService brokerService;

    @Test
    public void testHanging() throws Exception {
        registerDurableSubscription();
        produceExpiredAndOneNonExpiredMessages();
        TimeUnit.SECONDS.sleep(10);// make sure messages are expired

        Message message = collectMessagesFromDurableSubscriptionForOneMinute();
        DurableSubscriptionHangTestCase.LOG.info(("got message:" + message));
        Assert.assertNotNull("Unable to read unexpired message", message);
    }
}

