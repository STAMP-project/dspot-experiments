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


import Exchange.BEAN_METHOD_NAME;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BeanRouteTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BeanRouteTest.class);

    protected BeanRouteTest.MyBean myBean = new BeanRouteTest.MyBean();

    @Test
    public void testSendingMessageWithMethodNameHeader() throws Exception {
        String expectedBody = "Wobble";
        template.sendBodyAndHeader("direct:in", expectedBody, BEAN_METHOD_NAME, "read");
        Assert.assertEquals(("bean received correct value for: " + (myBean)), expectedBody, myBean.body);
    }

    @Test
    public void testSendingMessageWithMethodNameHeaderWithMoreVerboseCoe() throws Exception {
        final String expectedBody = "Wibble";
        template.send("direct:in", new Processor() {
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                in.setBody(expectedBody);
                in.setHeader(BEAN_METHOD_NAME, "read");
            }
        });
        Assert.assertEquals("bean received correct value", expectedBody, myBean.body);
    }

    public static class MyBean {
        private static AtomicInteger counter = new AtomicInteger(0);

        public String body;

        private int id;

        public MyBean() {
            id = BeanRouteTest.MyBean.counter.incrementAndGet();
        }

        @Override
        public String toString() {
            return "MyBean:" + (id);
        }

        public void read(String body) {
            this.body = body;
            BeanRouteTest.LOG.info("read() method on {} with body: {}", this, body);
        }

        public void wrongMethod(String body) {
            Assert.fail(("wrongMethod() called with: " + body));
        }
    }
}

