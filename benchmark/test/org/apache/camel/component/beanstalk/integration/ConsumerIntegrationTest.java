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
package org.apache.camel.component.beanstalk.integration;


import Exchange.CREATED_TIMESTAMP;
import Headers.AGE;
import Headers.BURIES;
import Headers.JOB_ID;
import Headers.KICKS;
import Headers.PRIORITY;
import Headers.RELEASES;
import Headers.STATE;
import Headers.TIMEOUTS;
import Headers.TIME_LEFT;
import Headers.TUBE;
import java.io.IOException;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.beanstalk.Helper;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ConsumerIntegrationTest extends BeanstalkCamelTestSupport {
    final String testMessage = "Hello, world!";

    @EndpointInject(uri = "mock:result")
    MockEndpoint result;

    @Test
    public void testReceive() throws IOException, InterruptedException {
        long prio = 0;
        int ttr = 10;
        final long jobId = writer.put(prio, 0, ttr, Helper.stringToBytes(testMessage));
        result.expectedMessageCount(1);
        result.expectedHeaderReceived(JOB_ID, jobId);
        result.message(0).header(CREATED_TIMESTAMP).isNotNull();
        result.message(0).header(JOB_ID).isEqualTo(jobId);
        result.message(0).header(PRIORITY).isEqualTo(prio);
        result.message(0).header(TUBE).isEqualTo(tubeName);
        result.message(0).header(STATE).isEqualTo("reserved");
        result.message(0).header(AGE).isGreaterThan(0);
        result.message(0).header(TIME_LEFT).isGreaterThan(0);
        result.message(0).header(TIMEOUTS).isNotNull();
        result.message(0).header(RELEASES).isNotNull();
        result.message(0).header(BURIES).isNotNull();
        result.message(0).header(KICKS).isNotNull();
        result.message(0).body().isEqualTo(testMessage);
        result.assertIsSatisfied(500);
    }
}

