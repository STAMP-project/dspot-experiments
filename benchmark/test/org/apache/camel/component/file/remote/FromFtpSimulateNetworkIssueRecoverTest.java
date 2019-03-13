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
package org.apache.camel.component.file.remote;


import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Simulate network issues by using a custom poll strategy to force exceptions
 * occurring during poll.
 */
public class FromFtpSimulateNetworkIssueRecoverTest extends FtpServerTestSupport {
    private static int counter;

    private static int rollback;

    @Test
    public void testFtpRecover() throws Exception {
        // should be able to download the file after recovering
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMinimumMessageCount(3);
        template.sendBody(getFtpUrl(), "Hello World");
        resultEndpoint.assertIsSatisfied();
        Thread.sleep(2000);
        assertTrue(("Should have tried at least 3 times was " + (FromFtpSimulateNetworkIssueRecoverTest.counter)), ((FromFtpSimulateNetworkIssueRecoverTest.counter) >= 3));
        assertEquals(2, FromFtpSimulateNetworkIssueRecoverTest.rollback);
    }

    public class MyPollStrategy extends RemoteFilePollingConsumerPollStrategy {
        @Override
        public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
            (FromFtpSimulateNetworkIssueRecoverTest.counter)++;
            if ((FromFtpSimulateNetworkIssueRecoverTest.counter) < 3) {
                throw new IllegalArgumentException("Forced by unit test");
            }
        }

        @Override
        public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) throws Exception {
            (FromFtpSimulateNetworkIssueRecoverTest.rollback)++;
            return super.rollback(consumer, endpoint, retryCounter, e);
        }
    }
}

