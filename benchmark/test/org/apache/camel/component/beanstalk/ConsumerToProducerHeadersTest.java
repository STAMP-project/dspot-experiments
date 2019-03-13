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
package org.apache.camel.component.beanstalk;


import BeanstalkComponent.DEFAULT_DELAY;
import BeanstalkComponent.DEFAULT_PRIORITY;
import BeanstalkComponent.DEFAULT_TIME_TO_RUN;
import Headers.JOB_ID;
import com.surftools.BeanstalkClient.Job;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConsumerToProducerHeadersTest extends BeanstalkMockTestSupport {
    @EndpointInject(uri = "beanstalk:tube=A")
    protected BeanstalkEndpoint endpoint;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    private String testMessage = "hello, world";

    private Processor a;

    private Processor b;

    @Test
    public void testBeanstalkConsumerToProducer() throws Exception {
        final long jobId = 111;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final Job jobMock = Mockito.mock(Job.class);
        // stats that may be set in the consumer:
        // mock stats : "tube", "state", "age", "time-left", "timeouts", "releases", "buries", "kicks"
        Map<String, String> stats = new HashMap<>();
        stats.put("tube", "A");
        stats.put("state", "Test");
        stats.put("age", "0");
        stats.put("time-left", "0");
        stats.put("timeouts", "0");
        stats.put("releases", "0");
        stats.put("buries", "0");
        stats.put("kicks", "0");
        Mockito.when(jobMock.getJobId()).thenReturn(jobId);
        Mockito.when(jobMock.getData()).thenReturn(payload);
        Mockito.when(client.reserve(ArgumentMatchers.anyInt())).thenReturn(jobMock).thenReturn(null);
        Mockito.when(client.statsJob(ArgumentMatchers.anyLong())).thenReturn(stats);
        Mockito.when(client.put(DEFAULT_PRIORITY, DEFAULT_DELAY, DEFAULT_TIME_TO_RUN, payload)).thenReturn(jobId);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMinimumMessageCount(1);
        result.expectedBodiesReceived(testMessage);
        result.expectedHeaderReceived(JOB_ID, jobId);
        result.message(0).header(JOB_ID).isEqualTo(jobId);
        context.getRouteController().startRoute("foo");
        result.assertIsSatisfied();
        Mockito.verify(client, Mockito.atLeastOnce()).reserve(ArgumentMatchers.anyInt());
        Mockito.verify(client, Mockito.atLeastOnce()).statsJob(ArgumentMatchers.anyLong());
        assertEquals(((TestExchangeCopyProcessor) (a)).getExchangeCopy().getIn().getHeaders(), ((TestExchangeCopyProcessor) (b)).getExchangeCopy().getIn().getHeaders());
    }
}

