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
package org.apache.camel.component.aws.sqs;


import java.util.concurrent.TimeUnit;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Test fails occationally on CI servers")
public class SqsFilterMessagesWithNoDeleteTest extends TestSupport {
    @Test
    public void testDoesNotGetThroughFilter() throws Exception {
        final String sqsURI = String.format(("aws-sqs://MyQueue?amazonSQSClient=#amazonSQSClient" + // note we will NOT delete if this message gets filtered out
        ("&deleteIfFiltered=false" + "&defaultVisibilityTimeout=1")));
        AmazonSQSClientMock clientMock = new AmazonSQSClientMock();
        populateMessages(clientMock);
        SimpleRegistry registry = new SimpleRegistry();
        DefaultCamelContext ctx = new DefaultCamelContext(registry);
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // try to filter using a non-existent header... should not go through
                from(sqsURI).filter(simple("${header.login} == true")).to("mock:result");
            }
        });
        MockEndpoint result = MockEndpoint.resolve(ctx, "mock:result");
        clientMock.setScheduler(ctx.getExecutorServiceManager().newScheduledThreadPool(clientMock, "ClientMock Scheduler", 1));
        registry.put("amazonSQSClient", clientMock);
        result.expectedMessageCount(0);
        ctx.start();
        // we shouldn't get
        assertIsSatisfied(2000, TimeUnit.MILLISECONDS);
        // however, the message should not be deleted, that is, it should be left on the queue
        String response = ctx.createConsumerTemplate().receiveBody(sqsURI, 5000, String.class);
        assertEquals(response, "Message: hello, world!");
        ctx.stop();
        shutdown();
    }

    @Test
    public void testGetThroughFilter() throws Exception {
        final String sqsURI = String.format(("aws-sqs://MyQueue?amazonSQSClient=#amazonSQSClient" + // note we will NOT delete if this message gets filtered out, but if it goes
        // through filter, it should be deleted!
        ("&deleteIfFiltered=false" + "&defaultVisibilityTimeout=1")));
        AmazonSQSClientMock clientMock = new AmazonSQSClientMock();
        populateMessages(clientMock);
        SimpleRegistry registry = new SimpleRegistry();
        DefaultCamelContext ctx = new DefaultCamelContext(registry);
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // this filter should allow the message to pass..
                from(sqsURI).setHeader("login", constant(true)).filter(simple("${header.login} == true")).to("mock:result");
            }
        });
        MockEndpoint result = MockEndpoint.resolve(ctx, "mock:result");
        registry.put("amazonSQSClient", clientMock);
        clientMock.setScheduler(ctx.getExecutorServiceManager().newScheduledThreadPool(clientMock, "ClientMock Scheduler", 1));
        result.expectedMessageCount(1);
        ctx.start();
        // the message should get through filter and mock should assert this
        assertIsSatisfied(2000, TimeUnit.MILLISECONDS);
        // however, the message should not be deleted, that is, it should be left on the queue
        String response = ctx.createConsumerTemplate().receiveBody(sqsURI, 5000, String.class);
        assertNull(response);
        ctx.stop();
        shutdown();
    }
}

