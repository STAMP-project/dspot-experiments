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
package org.apache.camel.processor.jpa;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.examples.SendEmail;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class JpaRouteSharedEntityManagerTest extends AbstractJpaTest {
    protected static final String SELECT_ALL_STRING = ("select x from " + (SendEmail.class.getName())) + " x";

    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testRouteJpaShared() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        int countStart = getBrokerCount();
        assertThat("brokerCount", countStart, CoreMatchers.equalTo(1));
        template.sendBody("direct:startShared", new SendEmail("one@somewhere.org"));
        // start route
        context.getRouteController().startRoute("jpaShared");
        // not the cleanest way to check the number of open connections
        int countEnd = getBrokerCount();
        assertThat("brokerCount", countEnd, CoreMatchers.equalTo(1));
        latch.countDown();
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRouteJpaNotShared() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:startNotshared", new SendEmail("one@somewhere.org"));
        int countStart = getBrokerCount();
        assertThat("brokerCount", countStart, CoreMatchers.equalTo(1));
        // start route
        context.getRouteController().startRoute("jpaOwn");
        // not the cleanest way to check the number of open connections
        int countEnd = getBrokerCount();
        assertThat("brokerCount", countEnd, CoreMatchers.equalTo(2));
        latch.countDown();
        assertMockEndpointsSatisfied();
    }

    private class LatchProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            latch.await(2, TimeUnit.SECONDS);
        }
    }
}

