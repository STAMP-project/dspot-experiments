/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.receiver.thrift.tcp;


import ManagedAgentLifeCycle.CLOSED_BY_CLIENT;
import ManagedAgentLifeCycle.CLOSED_BY_SERVER;
import ManagedAgentLifeCycle.RUNNING;
import ManagedAgentLifeCycle.UNEXPECTED_CLOSE_BY_CLIENT;
import ManagedAgentLifeCycle.UNEXPECTED_CLOSE_BY_SERVER;
import com.navercorp.pinpoint.collector.handler.DirectExecutor;
import com.navercorp.pinpoint.collector.service.AgentLifeCycleService;
import com.navercorp.pinpoint.collector.service.async.AgentLifeCycleAsyncTaskService;
import com.navercorp.pinpoint.rpc.server.PinpointServer;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(MockitoJUnitRunner.class)
public class AgentLifeCycleAsyncTaskServiceTest {
    // FIX guava 19.0.0 update error. MoreExecutors.sameThreadExecutor(); change final class
    @Spy
    private Executor executor = new DirectExecutor();

    @Mock
    private PinpointServer pinpointServer;

    @Mock
    private AgentLifeCycleService agentLifeCycleService;

    @InjectMocks
    private AgentLifeCycleAsyncTaskService agentLifeCycleAsyncTaskService = new AgentLifeCycleAsyncTaskService();

    private static final String TEST_AGENT_ID = "TEST_AGENT";

    private static final long TEST_START_TIMESTAMP = System.currentTimeMillis();

    private static final long TEST_EVENT_TIMESTAMP = (AgentLifeCycleAsyncTaskServiceTest.TEST_START_TIMESTAMP) + 10;

    private static final int TEST_SOCKET_ID = 999;

    private static final Map<Object, Object> TEST_CHANNEL_PROPERTIES = AgentLifeCycleAsyncTaskServiceTest.createTestChannelProperties();

    @Test
    public void runningStateShouldBeInserted() {
        runAndVerifyAgentLifeCycle(RUNNING);
    }

    @Test
    public void closedByClientStateShouldBeInserted() {
        runAndVerifyAgentLifeCycle(CLOSED_BY_CLIENT);
    }

    @Test
    public void unexpectedCloseByClientStateShouldBeInserted() {
        runAndVerifyAgentLifeCycle(UNEXPECTED_CLOSE_BY_CLIENT);
    }

    @Test
    public void closedByServerStateShouldBeInserted() {
        runAndVerifyAgentLifeCycle(CLOSED_BY_SERVER);
    }

    @Test
    public void unexpectedCloseByServerStateShouldBeInserted() {
        runAndVerifyAgentLifeCycle(UNEXPECTED_CLOSE_BY_SERVER);
    }

    @Test
    public void testValidTimestampCreation() {
        // socketId = 1, eventCounter = 1 -> timestamp = 0x100000001
        int givenSocketId = 1;
        int givenEventCounter = 1;
        long expectedTimestamp = new BigInteger("100000001", 16).longValue();
        long timestamp = this.agentLifeCycleAsyncTaskService.createEventIdentifier(givenSocketId, givenEventCounter);
        Assert.assertEquals(expectedTimestamp, timestamp);
        // socketId = 0, eventCounter = 0 -> timestamp = 0x0
        givenSocketId = 0;
        givenEventCounter = 0;
        expectedTimestamp = new BigInteger("0000000000000000", 16).longValue();
        timestamp = this.agentLifeCycleAsyncTaskService.createEventIdentifier(givenSocketId, givenEventCounter);
        Assert.assertEquals(expectedTimestamp, timestamp);
        // socketId = Integer.MAX_VALUE, eventCounter = 0 -> timestamp = 0x7fffffff00000000
        givenSocketId = Integer.MAX_VALUE;
        givenEventCounter = 0;
        expectedTimestamp = new BigInteger("7fffffff00000000", 16).longValue();
        timestamp = this.agentLifeCycleAsyncTaskService.createEventIdentifier(givenSocketId, givenEventCounter);
        Assert.assertEquals(expectedTimestamp, timestamp);
        // socketId = Integer.MAX_VALUE, eventCounter = Integer.MAX_VALUE -> timestamp = 0x7fffffff7fffffff
        givenSocketId = Integer.MAX_VALUE;
        givenEventCounter = Integer.MAX_VALUE;
        expectedTimestamp = new BigInteger("7fffffff7fffffff", 16).longValue();
        timestamp = this.agentLifeCycleAsyncTaskService.createEventIdentifier(givenSocketId, givenEventCounter);
        Assert.assertEquals(expectedTimestamp, timestamp);
    }

    @Test
    public void testTimestampOrdering() {
        // 0x7fffffff < 0x100000000
        long smallerTimestamp = this.agentLifeCycleAsyncTaskService.createEventIdentifier(0, Integer.MAX_VALUE);
        long largerTimestamp = this.agentLifeCycleAsyncTaskService.createEventIdentifier(1, 0);
        Assert.assertTrue((smallerTimestamp < largerTimestamp));
    }

    @Test
    public void testInvalidTimestampCreation() {
        final int negativeSocketId = new BigInteger("ffffffff", 16).intValue();
        final int eventCounter = 0;
        try {
            this.agentLifeCycleAsyncTaskService.createEventIdentifier(negativeSocketId, eventCounter);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
        final int socketId = 0;
        final int negativeEventCounter = new BigInteger("ffffffff", 16).intValue();
        try {
            this.agentLifeCycleAsyncTaskService.createEventIdentifier(socketId, negativeEventCounter);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}

