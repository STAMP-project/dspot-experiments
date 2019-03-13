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
package org.apache.camel.component.mail;


import Exchange.BATCH_SIZE;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test for batch consumer.
 */
public class MailMaxMessagesPerPollTest extends CamelTestSupport {
    @Test
    public void testBatchConsumer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.setResultWaitTime(2000);
        mock.expectedMessageCount(3);
        mock.message(0).body().isEqualTo("Message 0");
        mock.message(1).body().isEqualTo("Message 1");
        mock.message(2).body().isEqualTo("Message 2");
        mock.expectedPropertyReceived(BATCH_SIZE, 3);
        assertMockEndpointsSatisfied();
        mock.reset();
        mock.expectedMessageCount(2);
        mock.expectedPropertyReceived(BATCH_SIZE, 2);
        mock.message(0).body().isEqualTo("Message 3");
        mock.message(1).body().isEqualTo("Message 4");
        assertMockEndpointsSatisfied();
    }
}

