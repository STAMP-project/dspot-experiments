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


import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;


/**
 *
 *
 * @author James Furness
https://issues.apache.org/jira/browse/AMQ-3607
 */
public class ActiveMQSlowConsumerManualTest {
    private static final int PORT = 12345;

    private static final ActiveMQTopic TOPIC = new ActiveMQTopic("TOPIC");

    private static final String URL = ("nio://localhost:" + (ActiveMQSlowConsumerManualTest.PORT)) + "?socket.tcpNoDelay=true";

    @Test(timeout = 60000)
    public void testDefaultSettings() throws Exception {
        runTest("testDefaultSettings", 30, (-1), (-1), false, false, false, false);
    }

    @Test(timeout = 60000)
    public void testDefaultSettingsWithOptimiseAcknowledge() throws Exception {
        runTest("testDefaultSettingsWithOptimiseAcknowledge", 30, (-1), (-1), false, false, true, false);
    }

    @Test(timeout = 60000)
    public void testBounded() throws Exception {
        runTest("testBounded", 30, 5, 25, false, false, false, false);
    }

    @Test(timeout = 60000)
    public void testBoundedWithOptimiseAcknowledge() throws Exception {
        runTest("testBoundedWithOptimiseAcknowledge", 30, 5, 25, false, false, true, false);
    }
}

