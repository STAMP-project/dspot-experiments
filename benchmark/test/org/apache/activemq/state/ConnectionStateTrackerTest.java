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
package org.apache.activemq.state;


import java.io.IOException;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.SessionId;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionStateTrackerTest {
    private final ActiveMQQueue queue = new ActiveMQQueue("Test");

    private ConnectionId testConnectionId;

    private SessionId testSessionId;

    private int connectionId = 0;

    private int sessionId = 0;

    private int consumerId = 0;

    @Test
    public void testCacheSizeWithMessagePulls() throws IOException {
        final ConsumerId consumer1 = createConsumerId(testSessionId);
        ConnectionStateTracker tracker = new ConnectionStateTracker();
        Assert.assertEquals(0, tracker.getCurrentCacheSize());
        MessagePull pullCommand = createPullCommand(consumer1);
        tracker.track(pullCommand);
        Assert.assertEquals(0, tracker.getCurrentCacheSize());
        tracker.trackBack(pullCommand);
        long currentSize = tracker.getCurrentCacheSize();
        Assert.assertTrue((currentSize > 0));
        pullCommand = createPullCommand(consumer1);
        tracker.track(pullCommand);
        tracker.trackBack(pullCommand);
        Assert.assertEquals(currentSize, tracker.getCurrentCacheSize());
    }
}

