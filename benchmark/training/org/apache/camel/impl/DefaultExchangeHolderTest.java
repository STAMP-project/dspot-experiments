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


import Exchange.EXCEPTION_CAUGHT;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeExchangeException;
import org.apache.camel.support.DefaultExchangeHolder;
import org.junit.Assert;
import org.junit.Test;


public class DefaultExchangeHolderTest extends ContextTestSupport {
    private String id;

    @Test
    public void testMarshal() throws Exception {
        DefaultExchangeHolder holder = createHolder(true);
        Assert.assertNotNull(holder);
        Assert.assertNotNull(holder.toString());
    }

    @Test
    public void testNoProperties() throws Exception {
        DefaultExchangeHolder holder = createHolder(false);
        Assert.assertNotNull(holder);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        DefaultExchangeHolder.unmarshal(exchange, holder);
        Assert.assertEquals("Hello World", exchange.getIn().getBody());
        Assert.assertEquals("Bye World", exchange.getOut().getBody());
        Assert.assertEquals(123, exchange.getIn().getHeader("foo"));
        Assert.assertNull(exchange.getProperty("bar"));
    }

    @Test
    public void testUnmarshal() throws Exception {
        id = null;
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        DefaultExchangeHolder.unmarshal(exchange, createHolder(true));
        Assert.assertEquals("Hello World", exchange.getIn().getBody());
        Assert.assertEquals("Bye World", exchange.getOut().getBody());
        Assert.assertEquals(123, exchange.getIn().getHeader("foo"));
        Assert.assertEquals("Hi Camel", exchange.getIn().getHeader("CamelFoo"));
        Assert.assertEquals(444, exchange.getProperty("bar"));
        Assert.assertEquals(555, exchange.getProperty("CamelBar"));
        Assert.assertEquals(id, exchange.getExchangeId());
    }

    @Test
    public void testSkipNonSerializableData() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setBody("Hello World");
        exchange.getIn().setHeader("Foo", new DefaultExchangeHolderTest.MyFoo("Tiger"));
        exchange.getIn().setHeader("Bar", 123);
        DefaultExchangeHolder holder = DefaultExchangeHolder.marshal(exchange);
        exchange = new org.apache.camel.support.DefaultExchange(context);
        DefaultExchangeHolder.unmarshal(exchange, holder);
        // the non serializable header should be skipped
        Assert.assertEquals("Hello World", exchange.getIn().getBody());
        Assert.assertEquals(123, exchange.getIn().getHeader("Bar"));
        Assert.assertNull(exchange.getIn().getHeader("Foo"));
    }

    @Test
    public void testSkipNonSerializableDataFromList() throws Exception {
        // use a mixed list, the MyFoo is not serializable so the entire list should be skipped
        List<Object> list = new ArrayList<>();
        list.add("I am okay");
        list.add(new DefaultExchangeHolderTest.MyFoo("Tiger"));
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setBody("Hello World");
        exchange.getIn().setHeader("Foo", list);
        exchange.getIn().setHeader("Bar", 123);
        DefaultExchangeHolder holder = DefaultExchangeHolder.marshal(exchange);
        exchange = new org.apache.camel.support.DefaultExchange(context);
        DefaultExchangeHolder.unmarshal(exchange, holder);
        // the non serializable header should be skipped
        Assert.assertEquals("Hello World", exchange.getIn().getBody());
        Assert.assertEquals(123, exchange.getIn().getHeader("Bar"));
        Assert.assertNull(exchange.getIn().getHeader("Foo"));
    }

    @Test
    public void testSkipNonSerializableDataFromMap() throws Exception {
        // use a mixed Map, the MyFoo is not serializable so the entire map should be skipped
        Map<String, Object> map = new HashMap<>();
        map.put("A", "I am okay");
        map.put("B", new DefaultExchangeHolderTest.MyFoo("Tiger"));
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setBody("Hello World");
        exchange.getIn().setHeader("Foo", map);
        exchange.getIn().setHeader("Bar", 123);
        DefaultExchangeHolder holder = DefaultExchangeHolder.marshal(exchange);
        exchange = new org.apache.camel.support.DefaultExchange(context);
        DefaultExchangeHolder.unmarshal(exchange, holder);
        // the non serializable header should be skipped
        Assert.assertEquals("Hello World", exchange.getIn().getBody());
        Assert.assertEquals(123, exchange.getIn().getHeader("Bar"));
        Assert.assertNull(exchange.getIn().getHeader("Foo"));
    }

    @Test
    public void testCaughtException() throws Exception {
        // use a mixed list, the MyFoo is not serializable so the entire list should be skipped
        List<Object> list = new ArrayList<>();
        list.add("I am okay");
        list.add(new DefaultExchangeHolderTest.MyFoo("Tiger"));
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setBody("Hello World");
        exchange.getIn().setHeader("Foo", list);
        exchange.getIn().setHeader("Bar", 123);
        exchange.setProperty(EXCEPTION_CAUGHT, new IllegalArgumentException("Forced"));
        DefaultExchangeHolder holder = DefaultExchangeHolder.marshal(exchange);
        exchange = new org.apache.camel.support.DefaultExchange(context);
        DefaultExchangeHolder.unmarshal(exchange, holder);
        // the caught exception should be included
        Assert.assertEquals("Hello World", exchange.getIn().getBody());
        Assert.assertEquals(123, exchange.getIn().getHeader("Bar"));
        Assert.assertNull(exchange.getIn().getHeader("Foo"));
        Assert.assertNotNull(exchange.getProperty(EXCEPTION_CAUGHT));
        Assert.assertEquals("Forced", exchange.getProperty(EXCEPTION_CAUGHT, Exception.class).getMessage());
    }

    @Test
    public void testFileNotSupported() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setBody(new File("src/test/resources/log4j2.properties"));
        try {
            DefaultExchangeHolder.marshal(exchange);
            Assert.fail("Should have thrown exception");
        } catch (RuntimeExchangeException e) {
            // expected
        }
    }

    private static final class MyFoo {
        private String foo;

        private MyFoo(String foo) {
            this.foo = foo;
        }

        @SuppressWarnings("unused")
        public String getFoo() {
            return foo;
        }
    }
}

