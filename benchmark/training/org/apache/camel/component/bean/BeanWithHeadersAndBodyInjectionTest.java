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
package org.apache.camel.component.bean;


import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.processor.BeanRouteTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BeanWithHeadersAndBodyInjectionTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BeanRouteTest.class);

    protected BeanWithHeadersAndBodyInjectionTest.MyBean myBean = new BeanWithHeadersAndBodyInjectionTest.MyBean();

    @Test
    public void testSendMessage() throws Exception {
        template.send("direct:in", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setProperty("p1", "abc");
                exchange.setProperty("p2", 123);
                Message in = exchange.getIn();
                in.setHeader("h1", "xyz");
                in.setHeader("h2", 456);
                in.setBody("TheBody");
            }
        });
        Map<String, Object> foo = myBean.headers;
        Assert.assertNotNull("myBean.foo", foo);
        Assert.assertEquals("foo.h1", "xyz", foo.get("h1"));
        Assert.assertEquals("foo.h2", 456, foo.get("h2"));
        Assert.assertEquals("body", "TheBody", myBean.body);
    }

    public static class MyBean {
        public Map<String, Object> headers;

        public Object body;

        @Override
        public String toString() {
            return ((("MyBean[foo: " + (headers)) + " body: ") + (body)) + "]";
        }

        public void myMethod(@Headers
        Map<String, Object> headers, Object body) {
            this.headers = headers;
            this.body = body;
            BeanWithHeadersAndBodyInjectionTest.LOG.info(("myMethod() method called on " + (this)));
        }

        public void anotherMethod(@Headers
        Map<String, Object> headers, Object body) {
            Assert.fail("Should not have called this method!");
        }
    }
}

