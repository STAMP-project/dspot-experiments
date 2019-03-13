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


import BeanstalkCommand.bury;
import BeanstalkCommand.delete;
import BeanstalkCommand.release;
import BeanstalkCommand.touch;
import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import Headers.DELAY;
import Headers.JOB_ID;
import Headers.PRIORITY;
import Headers.RESULT;
import Headers.TIME_TO_RUN;
import com.surftools.BeanstalkClient.BeanstalkException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.beanstalk.processors.BuryCommand;
import org.apache.camel.component.beanstalk.processors.DeleteCommand;
import org.apache.camel.component.beanstalk.processors.PutCommand;
import org.apache.camel.component.beanstalk.processors.ReleaseCommand;
import org.apache.camel.component.beanstalk.processors.TouchCommand;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static BeanstalkComponent.DEFAULT_DELAY;
import static BeanstalkComponent.DEFAULT_PRIORITY;
import static BeanstalkComponent.DEFAULT_TIME_TO_RUN;


public class ProducerTest extends BeanstalkMockTestSupport {
    @EndpointInject(uri = "beanstalk:tube")
    protected BeanstalkEndpoint endpoint;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate direct;

    private String testMessage = "hello, world";

    @Test
    public void testPut() throws Exception {
        final long priority = DEFAULT_PRIORITY;
        final int delay = DEFAULT_DELAY;
        final int timeToRun = DEFAULT_TIME_TO_RUN;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final long jobId = 111;
        Mockito.when(client.put(priority, delay, timeToRun, payload)).thenReturn(jobId);
        final Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(PutCommand.class));
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            // TODO: SetBodyProcessor(?)
            public void process(Exchange exchange) {
                exchange.getIn().setBody(testMessage);
            }
        });
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).put(priority, delay, timeToRun, payload);
    }

    @Test
    public void testPutOut() throws Exception {
        final long priority = DEFAULT_PRIORITY;
        final int delay = DEFAULT_DELAY;
        final int timeToRun = DEFAULT_TIME_TO_RUN;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final long jobId = 111;
        Mockito.when(client.put(priority, delay, timeToRun, payload)).thenReturn(jobId);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(PutCommand.class));
        final Exchange exchange = template.send(endpoint, InOut, new Processor() {
            // TODO: SetBodyProcessor(?)
            public void process(Exchange exchange) {
                exchange.getIn().setBody(testMessage);
            }
        });
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getOut().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).put(priority, delay, timeToRun, payload);
    }

    @Test
    public void testPutWithHeaders() throws Exception {
        final long priority = 111;
        final int delay = 5;
        final int timeToRun = 65;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final long jobId = 111;
        Mockito.when(client.put(priority, delay, timeToRun, payload)).thenReturn(jobId);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(PutCommand.class));
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            // TODO: SetBodyProcessor(?)
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(PRIORITY, priority);
                exchange.getIn().setHeader(DELAY, delay);
                exchange.getIn().setHeader(TIME_TO_RUN, timeToRun);
                exchange.getIn().setBody(testMessage);
            }
        });
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).put(priority, delay, timeToRun, payload);
    }

    @Test
    public void testBury() throws Exception {
        final long priority = DEFAULT_PRIORITY;
        final long jobId = 111;
        endpoint.setCommand(bury);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(BuryCommand.class));
        Mockito.when(client.bury(jobId, priority)).thenReturn(true);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(JOB_ID, jobId);
            }
        });
        assertEquals("Op result", Boolean.TRUE, exchange.getIn().getHeader(RESULT, Boolean.class));
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).bury(jobId, priority);
    }

    @Test
    public void testBuryNoJobId() throws Exception {
        endpoint.setCommand(bury);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(BuryCommand.class));
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
            }
        });
        assertTrue("Exchange failed", exchange.isFailed());
        Mockito.verify(client, Mockito.never()).bury(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void testBuryWithHeaders() throws Exception {
        final long priority = 1000;
        final long jobId = 111;
        endpoint.setCommand(bury);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(BuryCommand.class));
        Mockito.when(client.bury(jobId, priority)).thenReturn(true);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(PRIORITY, priority);
                exchange.getIn().setHeader(JOB_ID, jobId);
            }
        });
        assertEquals("Op result", Boolean.TRUE, exchange.getIn().getHeader(RESULT, Boolean.class));
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).bury(jobId, priority);
    }

    @Test
    public void testDelete() throws Exception {
        final long jobId = 111;
        endpoint.setCommand(delete);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(DeleteCommand.class));
        Mockito.when(client.delete(jobId)).thenReturn(true);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(JOB_ID, jobId);
            }
        });
        assertEquals("Op result", Boolean.TRUE, exchange.getIn().getHeader(RESULT, Boolean.class));
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).delete(jobId);
    }

    @Test
    public void testDeleteNoJobId() throws Exception {
        endpoint.setCommand(delete);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(DeleteCommand.class));
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
            }
        });
        assertTrue("Exchange failed", exchange.isFailed());
        Mockito.verify(client, Mockito.never()).delete(ArgumentMatchers.anyLong());
    }

    @Test
    public void testRelease() throws Exception {
        final long priority = DEFAULT_PRIORITY;
        final int delay = DEFAULT_DELAY;
        final long jobId = 111;
        endpoint.setCommand(release);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(ReleaseCommand.class));
        Mockito.when(client.release(jobId, priority, delay)).thenReturn(true);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(JOB_ID, jobId);
            }
        });
        assertEquals("Op result", Boolean.TRUE, exchange.getIn().getHeader(RESULT, Boolean.class));
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).release(jobId, priority, delay);
    }

    @Test
    public void testReleaseNoJobId() throws Exception {
        endpoint.setCommand(release);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(ReleaseCommand.class));
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
            }
        });
        assertTrue("Exchange failed", exchange.isFailed());
        Mockito.verify(client, Mockito.never()).release(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testReleaseWithHeaders() throws Exception {
        final long priority = 1001;
        final int delay = 124;
        final long jobId = 111;
        endpoint.setCommand(release);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(ReleaseCommand.class));
        Mockito.when(client.release(jobId, priority, delay)).thenReturn(true);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(JOB_ID, jobId);
                exchange.getIn().setHeader(PRIORITY, priority);
                exchange.getIn().setHeader(DELAY, delay);
            }
        });
        assertEquals("Op result", Boolean.TRUE, exchange.getIn().getHeader(RESULT, Boolean.class));
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).release(jobId, priority, delay);
    }

    @Test
    public void testTouch() throws Exception {
        final long jobId = 111;
        endpoint.setCommand(touch);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(TouchCommand.class));
        Mockito.when(client.touch(jobId)).thenReturn(true);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(JOB_ID, jobId);
            }
        });
        assertEquals("Op result", Boolean.TRUE, exchange.getIn().getHeader(RESULT, Boolean.class));
        assertEquals("Job ID in exchange", Long.valueOf(jobId), exchange.getIn().getHeader(JOB_ID, Long.class));
        Mockito.verify(client).touch(jobId);
    }

    @Test
    public void testTouchNoJobId() throws Exception {
        endpoint.setCommand(touch);
        Producer producer = endpoint.createProducer();
        assertNotNull("Producer", producer);
        assertThat("Producer class", producer, CoreMatchers.instanceOf(BeanstalkProducer.class));
        assertThat("Processor class", getCommand(), CoreMatchers.instanceOf(TouchCommand.class));
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
            }
        });
        assertTrue("Exchange failed", exchange.isFailed());
        Mockito.verify(client, Mockito.never()).touch(ArgumentMatchers.anyLong());
    }

    @Test
    public void testHeaderOverride() throws Exception {
        final long priority = 1020;
        final int delay = 50;
        final int timeToRun = 75;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final long jobId = 113;
        Mockito.when(client.put(priority, delay, timeToRun, payload)).thenReturn(jobId);
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.allMessages().body().isEqualTo(testMessage);
        resultEndpoint.allMessages().header(JOB_ID).isEqualTo(jobId);
        direct.sendBodyAndHeader(testMessage, TIME_TO_RUN, timeToRun);
        resultEndpoint.assertIsSatisfied();
        final Long jobIdIn = resultEndpoint.getReceivedExchanges().get(0).getIn().getHeader(JOB_ID, Long.class);
        assertNotNull("Job ID in 'In' message", jobIdIn);
        Mockito.verify(client).put(priority, delay, timeToRun, payload);
    }

    @Test
    public void test1BeanstalkException() throws Exception {
        final long priority = 1020;
        final int delay = 50;
        final int timeToRun = 75;
        final byte[] payload = Helper.stringToBytes(testMessage);
        final long jobId = 113;
        Mockito.when(client.put(priority, delay, timeToRun, payload)).thenThrow(new BeanstalkException("test")).thenReturn(jobId);
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.allMessages().body().isEqualTo(testMessage);
        resultEndpoint.allMessages().header(JOB_ID).isEqualTo(jobId);
        direct.sendBodyAndHeader(testMessage, TIME_TO_RUN, timeToRun);
        resultEndpoint.assertIsSatisfied();
        final Long jobIdIn = resultEndpoint.getReceivedExchanges().get(0).getIn().getHeader(JOB_ID, Long.class);
        assertNotNull("Job ID in 'In' message", jobIdIn);
        Mockito.verify(client, Mockito.times(1)).close();
        Mockito.verify(client, Mockito.times(2)).put(priority, delay, timeToRun, payload);
    }

    @Test
    public void test2BeanstalkException() throws Exception {
        final long jobId = 111;
        Mockito.when(client.touch(jobId)).thenThrow(new BeanstalkException("test"));
        endpoint.setCommand(touch);
        final Exchange exchange = template.send(endpoint, InOnly, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(JOB_ID, jobId);
            }
        });
        assertTrue("Exchange failed", exchange.isFailed());
        Mockito.verify(client, Mockito.times(2)).touch(jobId);
        Mockito.verify(client, Mockito.times(1)).close();
    }
}

