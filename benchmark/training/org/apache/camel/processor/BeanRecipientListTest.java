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
package org.apache.camel.processor;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.RecipientList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BeanRecipientListTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BeanRecipientListTest.class);

    protected BeanRecipientListTest.MyBean myBean = new BeanRecipientListTest.MyBean();

    @Test
    public void testSendMessage() throws Exception {
        final String expectedBody = "Wibble";
        getMockEndpoint("mock:a").expectedBodiesReceived(expectedBody);
        getMockEndpoint("mock:b").expectedBodiesReceived(expectedBody);
        template.sendBody("direct:in", expectedBody);
        assertMockEndpointsSatisfied();
    }

    public static class MyBean {
        private static AtomicInteger counter = new AtomicInteger(0);

        private int id;

        public MyBean() {
            id = BeanRecipientListTest.MyBean.counter.incrementAndGet();
        }

        @Override
        public String toString() {
            return "MyBean:" + (id);
        }

        @RecipientList
        public String[] route(String body) {
            BeanRecipientListTest.LOG.debug("Called {} with body: {}", this, body);
            return new String[]{ "mock:a", "mock:b" };
        }
    }
}

