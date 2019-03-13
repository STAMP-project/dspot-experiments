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
package org.apache.camel.component.ironmq.integrationtest;


import java.util.concurrent.TimeUnit;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Must be manually tested. Provide your own projectId and token!")
public class Queue2QueueExample extends CamelTestSupport {
    private static final String PAYLOAD = "{some:text, number:#}";

    // replace with your proejctid
    private String projectId = "myIronMQproject";

    // replace with your token
    private String token = "myIronMQToken";

    private final String ironQueue1 = ((("ironmq:queue1?projectId=" + (projectId)) + "&token=") + (token)) + "&ironMQCloud=https://mq-aws-eu-west-1-1.iron.io";

    private final String ironQueue2 = ((("ironmq:queue2?projectId=" + (projectId)) + "&token=") + (token)) + "&ironMQCloud=https://mq-aws-eu-west-1-1.iron.io";

    @Test
    public void testSendMessagesBetweenQueues() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(100);
        for (int i = 1; i <= 100; i++) {
            String payloadToSend = Queue2QueueExample.PAYLOAD.replace("#", ("" + i));
            template.sendBody("direct:start", payloadToSend);
        }
        assertMockEndpointsSatisfied(2, TimeUnit.MINUTES);
    }
}

