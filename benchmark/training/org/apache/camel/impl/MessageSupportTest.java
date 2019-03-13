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
package org.apache.camel.impl;


import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.TestSupport;
import org.apache.camel.support.SimpleUuidGenerator;
import org.junit.Assert;
import org.junit.Test;


public class MessageSupportTest extends ContextTestSupport {
    @Test
    public void testSetBodyType() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = exchange.getIn();
        in.setBody("123", Integer.class);
        TestSupport.assertIsInstanceOf(Integer.class, in.getBody());
    }

    @Test
    public void testGetMandatoryBody() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = exchange.getIn();
        try {
            in.getMandatoryBody();
            Assert.fail("Should have thrown an exception");
        } catch (InvalidPayloadException e) {
            // expected
        }
        in.setBody("Hello World");
        Assert.assertEquals("Hello World", in.getMandatoryBody());
    }

    @Test
    public void testGetMessageId() {
        context.setUuidGenerator(new SimpleUuidGenerator());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = exchange.getIn();
        Assert.assertEquals("1", in.getMessageId());
    }

    @Test
    public void testGetMessageIdWithoutAnExchange() {
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Assert.assertNotNull(in.getMessageId());
    }

    @Test
    public void testCopyFromSameHeadersInstance() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = exchange.getIn();
        Map<String, Object> headers = in.getHeaders();
        headers.put("foo", 123);
        Message out = new org.apache.camel.support.DefaultMessage(context);
        out.setBody("Bye World");
        out.setHeaders(headers);
        out.copyFrom(in);
        Assert.assertEquals(123, headers.get("foo"));
        Assert.assertEquals(123, in.getHeader("foo"));
        Assert.assertEquals(123, out.getHeader("foo"));
    }

    @Test
    public void testCopyOverExchange() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = exchange.getIn();
        in.setBody("Bye World");
        Message two = in.copy();
        Assert.assertSame(exchange, two.getExchange());
        Message three = new org.apache.camel.support.DefaultMessage(context);
        three.copyFrom(two);
        Assert.assertSame(exchange, three.getExchange());
    }
}

