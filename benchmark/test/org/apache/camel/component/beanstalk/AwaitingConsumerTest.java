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


import Headers.JOB_ID;
import com.surftools.BeanstalkClient.BeanstalkException;
import com.surftools.BeanstalkClient.Job;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AwaitingConsumerTest extends BeanstalkMockTestSupport {
    @EndpointInject(uri = "beanstalk:tube")
    protected BeanstalkEndpoint endpoint;

    private String testMessage = "hello, world";

    @Test
    public void testReceive() throws Exception {
        if (!(canTest())) {
            return;
        }
        final Job jobMock = Mockito.mock(Job.class);
        final long jobId = 111;
        final byte[] payload = Helper.stringToBytes(testMessage);
        Mockito.when(jobMock.getJobId()).thenReturn(jobId);
        Mockito.when(jobMock.getData()).thenReturn(payload);
        Mockito.when(client.reserve(ArgumentMatchers.anyInt())).thenReturn(jobMock).thenReturn(null);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(1);
        result.expectedBodiesReceived(testMessage);
        result.expectedHeaderReceived(JOB_ID, jobId);
        result.message(0).header(JOB_ID).isEqualTo(jobId);
        result.assertIsSatisfied(100);
        Mockito.verify(client, Mockito.atLeast(1)).reserve(0);
        Mockito.verify(client, Mockito.atLeast(1)).delete(jobId);
    }

    @Test
    public void testBeanstalkException() throws Exception {
        if (!(canTest())) {
            return;
        }
        final Job jobMock = Mockito.mock(Job.class);
        final long jobId = 111;
        final byte[] payload = Helper.stringToBytes(testMessage);
        Mockito.when(jobMock.getJobId()).thenReturn(jobId);
        Mockito.when(jobMock.getData()).thenReturn(payload);
        Mockito.when(client.reserve(ArgumentMatchers.anyInt())).thenThrow(new BeanstalkException("test")).thenReturn(jobMock);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(1);
        result.expectedBodiesReceived(testMessage);
        result.expectedHeaderReceived(JOB_ID, jobId);
        result.message(0).header(JOB_ID).isEqualTo(jobId);
        result.assertIsSatisfied(100);
        Mockito.verify(client, Mockito.atLeast(1)).reserve(ArgumentMatchers.anyInt());
        Mockito.verify(client, Mockito.times(1)).close();
    }
}

