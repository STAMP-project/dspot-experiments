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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.language.XPath;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BeanOgnMethodWithXPathInjectionTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BeanRouteTest.class);

    protected BeanOgnMethodWithXPathInjectionTest.MyBean myBean = new BeanOgnMethodWithXPathInjectionTest.MyBean();

    protected BeanOgnMethodWithXPathInjectionTest.MyOtherBean myOtherBean = new BeanOgnMethodWithXPathInjectionTest.MyOtherBean(myBean);

    @Test
    public void testSendMessage() throws Exception {
        String expectedBody = "<env:Envelope xmlns:env='http://www.w3.org/2003/05/soap-envelope'><env:Body>" + "<foo>bar</foo></env:Body></env:Envelope>";
        Object out = template.requestBodyAndHeader("direct:in", expectedBody, "foo", "bar");
        Assert.assertEquals("bar", out);
        Assert.assertEquals(("bean body: " + (myBean)), expectedBody, myBean.body);
        Assert.assertEquals(("bean foo: " + (myBean)), "bar", myBean.foo);
    }

    @Test
    public void testSendTwoMessages() throws Exception {
        // 1st message
        String expectedBody = "<env:Envelope xmlns:env='http://www.w3.org/2003/05/soap-envelope'><env:Body>" + "<foo>bar</foo></env:Body></env:Envelope>";
        Object out = template.requestBodyAndHeader("direct:in", expectedBody, "foo", "bar");
        Assert.assertEquals("bar", out);
        Assert.assertEquals(("bean body: " + (myBean)), expectedBody, myBean.body);
        Assert.assertEquals(("bean foo: " + (myBean)), "bar", myBean.foo);
        // 2nd message
        String expectedBody2 = "<env:Envelope xmlns:env='http://www.w3.org/2003/05/soap-envelope'><env:Body>" + "<foo>baz</foo></env:Body></env:Envelope>";
        Object out2 = template.requestBodyAndHeader("direct:in", expectedBody2, "foo", "bar");
        Assert.assertEquals("baz", out2);
        Assert.assertEquals(("bean body: " + (myBean)), expectedBody2, myBean.body);
        Assert.assertEquals(("bean foo: " + (myBean)), "baz", myBean.foo);
    }

    public static class MyOtherBean {
        private final BeanOgnMethodWithXPathInjectionTest.MyBean inner;

        public MyOtherBean(BeanOgnMethodWithXPathInjectionTest.MyBean inner) {
            this.inner = inner;
        }

        public BeanOgnMethodWithXPathInjectionTest.MyBean doSomething() {
            return inner;
        }
    }

    public static class MyBean {
        public String body;

        public String foo;

        @Override
        public String toString() {
            return ((("MyBean[foo: " + (foo)) + " body: ") + (body)) + "]";
        }

        public String read(String body, @XPath("/soap:Envelope/soap:Body/foo/text()")
        String foo) {
            this.foo = foo;
            this.body = body;
            BeanOgnMethodWithXPathInjectionTest.LOG.info(("read() method called on " + (this)));
            return foo;
        }
    }
}

