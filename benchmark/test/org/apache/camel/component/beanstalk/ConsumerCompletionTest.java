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
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static BeanstalkComponent.DEFAULT_DELAY;
import static BeanstalkComponent.DEFAULT_PRIORITY;


public class ConsumerCompletionTest extends BeanstalkMockTestSupport {
    private String testMessage = "hello, world";

    private boolean shouldIdie;

    private Processor processor = new Processor() {
        @Override
        public void process(Exchange exchange) throws InterruptedException {
            if (shouldIdie) {
                throw new InterruptedException("die");
            }
        }
    };

    @Test
    public void testDeleteOnComplete() throws Exception {
        if (!(canTest())) {
            return;
        }
        final long jobId = 111;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final Job jobMock = Mockito.mock(Job.class);
        Mockito.when(jobMock.getJobId()).thenReturn(jobId);
        Mockito.when(jobMock.getData()).thenReturn(payload);
        Mockito.when(client.reserve(ArgumentMatchers.anyInt())).thenReturn(jobMock).thenReturn(null);
        Mockito.when(client.statsJob(ArgumentMatchers.anyLong())).thenReturn(null);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMinimumMessageCount(1);
        result.expectedBodiesReceived(testMessage);
        result.expectedHeaderReceived(JOB_ID, jobId);
        result.message(0).header(JOB_ID).isEqualTo(jobId);
        context.getRouteController().startRoute("foo");
        result.assertIsSatisfied();
        Mockito.verify(client, Mockito.atLeastOnce()).reserve(ArgumentMatchers.anyInt());
        Mockito.verify(client, Mockito.atLeastOnce()).statsJob(ArgumentMatchers.anyLong());
        Mockito.verify(client).delete(jobId);
    }

    @Test
    public void testReleaseOnFailure() throws Exception {
        shouldIdie = true;
        final long jobId = 111;
        final long priority = DEFAULT_PRIORITY;
        final int delay = DEFAULT_DELAY;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final Job jobMock = Mockito.mock(Job.class);
        Mockito.when(jobMock.getJobId()).thenReturn(jobId);
        Mockito.when(jobMock.getData()).thenReturn(payload);
        Mockito.when(client.reserve(ArgumentMatchers.anyInt())).thenReturn(jobMock).thenReturn(null);
        Mockito.when(client.statsJob(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(client.release(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(true);
        NotifyBuilder notify = whenFailed(1).create();
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(0);
        context.getRouteController().startRoute("foo");
        assertTrue(notify.matches(5, TimeUnit.SECONDS));
        Mockito.verify(client, Mockito.atLeastOnce()).reserve(ArgumentMatchers.anyInt());
        Mockito.verify(client, Mockito.atLeastOnce()).statsJob(ArgumentMatchers.anyLong());
        Mockito.verify(client).release(jobId, priority, delay);
    }

    @Test
    public void testBeanstalkException() throws Exception {
        if (!(canTest())) {
            return;
        }
        shouldIdie = false;
        final Job jobMock = Mockito.mock(Job.class);
        final long jobId = 111;
        final byte[] payload = Helper.stringToBytes(testMessage);
        Mockito.when(jobMock.getJobId()).thenReturn(jobId);
        Mockito.when(jobMock.getData()).thenReturn(payload);
        Mockito.when(client.reserve(ArgumentMatchers.anyInt())).thenThrow(new BeanstalkException("test")).thenReturn(jobMock);
        Mockito.when(client.statsJob(ArgumentMatchers.anyInt())).thenReturn(null);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(1);
        result.expectedBodiesReceived(testMessage);
        result.expectedHeaderReceived(JOB_ID, jobId);
        result.message(0).header(JOB_ID).isEqualTo(jobId);
        context.getRouteController().startRoute("foo");
        result.assertIsSatisfied();
        Mockito.verify(client, Mockito.atLeast(1)).reserve(ArgumentMatchers.anyInt());
        Mockito.verify(client, Mockito.times(1)).close();
    }
}

