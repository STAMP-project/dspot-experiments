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
package org.apache.activemq.transport.amqp.interop;


import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.junit.Test;


/**
 * Tests various behaviors of broker side drain support.
 */
public class AmqpReceiverDrainTest extends AmqpClientTestSupport {
    @Test(timeout = 60000)
    public void testReceiverCanDrainMessagesQueue() throws Exception {
        doTestReceiverCanDrainMessages(false);
    }

    @Test(timeout = 60000)
    public void testReceiverCanDrainMessagesTopic() throws Exception {
        doTestReceiverCanDrainMessages(true);
    }

    @Test(timeout = 60000)
    public void testPullWithNoMessageGetDrainedQueue() throws Exception {
        doTestPullWithNoMessageGetDrained(false);
    }

    @Test(timeout = 60000)
    public void testPullWithNoMessageGetDrainedTopic() throws Exception {
        doTestPullWithNoMessageGetDrained(true);
    }

    @Test(timeout = 60000)
    public void testPullOneFromRemoteQueue() throws Exception {
        doTestPullOneFromRemote(false);
    }

    @Test(timeout = 60000)
    public void testPullOneFromRemoteTopic() throws Exception {
        doTestPullOneFromRemote(true);
    }

    @Test(timeout = 60000)
    public void testMultipleZeroResultPullsQueue() throws Exception {
        doTestMultipleZeroResultPulls(false);
    }

    @Test(timeout = 60000)
    public void testMultipleZeroResultPullsTopic() throws Exception {
        doTestMultipleZeroResultPulls(true);
    }
}

