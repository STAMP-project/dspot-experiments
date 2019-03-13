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
package org.apache.camel.component.jmx;


import java.util.LinkedHashSet;
import javax.management.Notification;
import org.apache.camel.Exchange;
import org.apache.camel.component.jmx.beans.ISimpleMXBean;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that the NotificationFilter is applied if configured
 */
public class JMXNotificationFilterTest extends SimpleBeanFixture {
    /**
     * we'll track the rejected messages so we know what got filtered
     */
    private LinkedHashSet<Notification> mRejected = new LinkedHashSet<>();

    @Test
    public void testNotificationFilter() throws Exception {
        ISimpleMXBean bean = getSimpleMXBean();
        Assert.assertEquals("no notifications should have been filtered at this point", 0, mRejected.size());
        // we should only get 5 messages, which is 1/2 the number of times we touched the object.
        // The 1/2 is due to the behavior of the test NotificationFilter implemented below
        getMockFixture().getMockEndpoint().setExpectedMessageCount(5);
        for (int i = 0; i < 10; i++) {
            bean.touch();
        }
        getMockFixture().waitForMessages();
        Assert.assertEquals("5 notifications should have been filtered", 5, mRejected.size());
        // assert that all of the rejected ones are odd and accepted ones even
        for (Notification rejected : mRejected) {
            Assert.assertEquals(1, ((rejected.getSequenceNumber()) % 2));
        }
        for (Exchange received : getMockFixture().getMockEndpoint().getReceivedExchanges()) {
            Notification n = ((Notification) (received.getIn().getBody()));
            Assert.assertEquals(0, ((n.getSequenceNumber()) % 2));
        }
    }
}

