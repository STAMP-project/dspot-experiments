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


import java.io.IOException;
import java.net.ConnectException;
import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTestSupport;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.junit.Assert;
import org.junit.Test;


public class DefaultExchangeTest extends ExchangeTestSupport {
    @Test
    public void testBody() throws Exception {
        Assert.assertNotNull(exchange.getIn().getBody());
        Assert.assertEquals("<hello id='m123'>world!</hello>", exchange.getIn().getBody());
        Assert.assertEquals("<hello id='m123'>world!</hello>", exchange.getIn().getBody(String.class));
        Assert.assertEquals("<hello id='m123'>world!</hello>", exchange.getIn().getMandatoryBody());
        Assert.assertEquals("<hello id='m123'>world!</hello>", exchange.getIn().getMandatoryBody(String.class));
    }

    @Test
    public void testMandatoryBody() throws Exception {
        Assert.assertNotNull(exchange.getIn().getBody());
        Assert.assertEquals("<hello id='m123'>world!</hello>", exchange.getIn().getBody());
        try {
            Assert.assertEquals(null, exchange.getIn().getBody(Integer.class));
            Assert.fail("Should have thrown a TypeConversionException");
        } catch (TypeConversionException e) {
            // expected
        }
        Assert.assertEquals("<hello id='m123'>world!</hello>", exchange.getIn().getMandatoryBody());
        try {
            exchange.getIn().getMandatoryBody(Integer.class);
            Assert.fail("Should have thrown an InvalidPayloadException");
        } catch (InvalidPayloadException e) {
            // expected
        }
    }

    @Test
    public void testExceptionAsType() throws Exception {
        exchange.setException(RuntimeCamelException.wrapRuntimeCamelException(new ConnectException("Cannot connect to remote server")));
        ConnectException ce = exchange.getException(ConnectException.class);
        Assert.assertNotNull(ce);
        Assert.assertEquals("Cannot connect to remote server", ce.getMessage());
        IOException ie = exchange.getException(IOException.class);
        Assert.assertNotNull(ie);
        Assert.assertEquals("Cannot connect to remote server", ie.getMessage());
        Exception e = exchange.getException(Exception.class);
        Assert.assertNotNull(e);
        Assert.assertEquals("Cannot connect to remote server", e.getMessage());
        RuntimeCamelException rce = exchange.getException(RuntimeCamelException.class);
        Assert.assertNotNull(rce);
        Assert.assertNotSame("Cannot connect to remote server", rce.getMessage());
        Assert.assertEquals("Cannot connect to remote server", rce.getCause().getMessage());
    }

    @Test
    public void testHeader() throws Exception {
        Assert.assertNotNull(exchange.getIn().getHeaders());
        Assert.assertEquals(123, exchange.getIn().getHeader("bar"));
        Assert.assertEquals(new Integer(123), exchange.getIn().getHeader("bar", Integer.class));
        Assert.assertEquals("123", exchange.getIn().getHeader("bar", String.class));
        Assert.assertEquals(123, exchange.getIn().getHeader("bar", 234));
        Assert.assertEquals(123, exchange.getIn().getHeader("bar", () -> 456));
        Assert.assertEquals(456, exchange.getIn().getHeader("baz", () -> 456));
        Assert.assertEquals(123, exchange.getIn().getHeader("bar", 234));
        Assert.assertEquals(new Integer(123), exchange.getIn().getHeader("bar", 234, Integer.class));
        Assert.assertEquals("123", exchange.getIn().getHeader("bar", "234", String.class));
        Assert.assertEquals("123", exchange.getIn().getHeader("bar", () -> "456", String.class));
        Assert.assertEquals("456", exchange.getIn().getHeader("baz", () -> "456", String.class));
        Assert.assertEquals(234, exchange.getIn().getHeader("cheese", 234));
        Assert.assertEquals("234", exchange.getIn().getHeader("cheese", 234, String.class));
        Assert.assertEquals("456", exchange.getIn().getHeader("cheese", () -> 456, String.class));
    }

    @Test
    public void testProperty() throws Exception {
        exchange.removeProperty("foobar");
        Assert.assertFalse(exchange.hasProperties());
        exchange.setProperty("fruit", "apple");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals("apple", exchange.getProperty("fruit"));
        Assert.assertEquals(null, exchange.getProperty("beer"));
        Assert.assertEquals(null, exchange.getProperty("beer", String.class));
        // Current TypeConverter support to turn the null value to false of boolean,
        // as assertEquals needs the Object as the parameter, we have to use Boolean.FALSE value in this case
        Assert.assertEquals(Boolean.FALSE, exchange.getProperty("beer", boolean.class));
        Assert.assertEquals(null, exchange.getProperty("beer", Boolean.class));
        Assert.assertEquals("apple", exchange.getProperty("fruit", String.class));
        Assert.assertEquals("apple", exchange.getProperty("fruit", "banana", String.class));
        Assert.assertEquals("banana", exchange.getProperty("beer", "banana"));
        Assert.assertEquals("banana", exchange.getProperty("beer", "banana", String.class));
    }

    @Test
    public void testRemoveProperties() throws Exception {
        exchange.removeProperty("foobar");
        Assert.assertFalse(exchange.hasProperties());
        exchange.setProperty("fruit", "apple");
        exchange.setProperty("fruit1", "banana");
        exchange.setProperty("zone", "Africa");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals("apple", exchange.getProperty("fruit"));
        Assert.assertEquals("banana", exchange.getProperty("fruit1"));
        Assert.assertEquals("Africa", exchange.getProperty("zone"));
        exchange.removeProperties("fr*");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals(exchange.getProperties().size(), 1);
        Assert.assertEquals(null, exchange.getProperty("fruit", String.class));
        Assert.assertEquals(null, exchange.getProperty("fruit1", String.class));
        Assert.assertEquals("Africa", exchange.getProperty("zone", String.class));
    }

    @Test
    public void testRemoveAllProperties() throws Exception {
        exchange.removeProperty("foobar");
        Assert.assertFalse(exchange.hasProperties());
        exchange.setProperty("fruit", "apple");
        exchange.setProperty("fruit1", "banana");
        exchange.setProperty("zone", "Africa");
        Assert.assertTrue(exchange.hasProperties());
        exchange.removeProperties("*");
        Assert.assertFalse(exchange.hasProperties());
        Assert.assertEquals(exchange.getProperties().size(), 0);
    }

    @Test
    public void testRemovePropertiesWithExclusion() throws Exception {
        exchange.removeProperty("foobar");
        Assert.assertFalse(exchange.hasProperties());
        exchange.setProperty("fruit", "apple");
        exchange.setProperty("fruit1", "banana");
        exchange.setProperty("fruit2", "peach");
        exchange.setProperty("zone", "Africa");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals("apple", exchange.getProperty("fruit"));
        Assert.assertEquals("banana", exchange.getProperty("fruit1"));
        Assert.assertEquals("peach", exchange.getProperty("fruit2"));
        Assert.assertEquals("Africa", exchange.getProperty("zone"));
        exchange.removeProperties("fr*", "fruit1", "fruit2");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals(exchange.getProperties().size(), 3);
        Assert.assertEquals(null, exchange.getProperty("fruit", String.class));
        Assert.assertEquals("banana", exchange.getProperty("fruit1", String.class));
        Assert.assertEquals("peach", exchange.getProperty("fruit2", String.class));
        Assert.assertEquals("Africa", exchange.getProperty("zone", String.class));
    }

    @Test
    public void testRemovePropertiesPatternWithAllExcluded() throws Exception {
        exchange.removeProperty("foobar");
        Assert.assertFalse(exchange.hasProperties());
        exchange.setProperty("fruit", "apple");
        exchange.setProperty("fruit1", "banana");
        exchange.setProperty("fruit2", "peach");
        exchange.setProperty("zone", "Africa");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals("apple", exchange.getProperty("fruit"));
        Assert.assertEquals("banana", exchange.getProperty("fruit1"));
        Assert.assertEquals("peach", exchange.getProperty("fruit2"));
        Assert.assertEquals("Africa", exchange.getProperty("zone"));
        exchange.removeProperties("fr*", "fruit", "fruit1", "fruit2", "zone");
        Assert.assertTrue(exchange.hasProperties());
        Assert.assertEquals(exchange.getProperties().size(), 4);
        Assert.assertEquals("apple", exchange.getProperty("fruit", String.class));
        Assert.assertEquals("banana", exchange.getProperty("fruit1", String.class));
        Assert.assertEquals("peach", exchange.getProperty("fruit2", String.class));
        Assert.assertEquals("Africa", exchange.getProperty("zone", String.class));
    }

    @Test
    public void testInType() throws Exception {
        exchange.setIn(new DefaultExchangeTest.MyMessage(context));
        DefaultExchangeTest.MyMessage my = exchange.getIn(DefaultExchangeTest.MyMessage.class);
        Assert.assertNotNull(my);
    }

    @Test
    public void testOutType() throws Exception {
        exchange.setOut(new DefaultExchangeTest.MyMessage(context));
        DefaultExchangeTest.MyMessage my = exchange.getOut(DefaultExchangeTest.MyMessage.class);
        Assert.assertNotNull(my);
    }

    @Test
    public void testCopy() {
        DefaultExchange sourceExchange = new DefaultExchange(context);
        DefaultExchangeTest.MyMessage sourceIn = new DefaultExchangeTest.MyMessage(context);
        sourceExchange.setIn(sourceIn);
        Exchange destExchange = sourceExchange.copy();
        Message destIn = destExchange.getIn();
        Assert.assertEquals("Dest message should be of the same type as source message", sourceIn.getClass(), destIn.getClass());
    }

    @Test
    public void testFaultSafeCopy() {
        testFaultCopy();
    }

    public static class MyMessage extends DefaultMessage {
        public MyMessage(CamelContext camelContext) {
            super(camelContext);
        }

        @Override
        public DefaultExchangeTest.MyMessage newInstance() {
            return new DefaultExchangeTest.MyMessage(getCamelContext());
        }
    }
}

