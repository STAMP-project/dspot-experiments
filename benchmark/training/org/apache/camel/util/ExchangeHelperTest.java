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
package org.apache.camel.util;


import Exchange.CONTENT_ENCODING;
import Exchange.CONTENT_TYPE;
import ExchangePattern.InOut;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchBeanException;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.NoSuchPropertyException;
import org.apache.camel.support.ExchangeHelper;
import org.junit.Assert;
import org.junit.Test;


public class ExchangeHelperTest extends ContextTestSupport {
    protected Exchange exchange;

    @Test
    public void testValidProperty() throws Exception {
        String value = ExchangeHelper.getMandatoryProperty(exchange, "foo", String.class);
        Assert.assertEquals("foo property", "123", value);
    }

    @Test
    public void testMissingProperty() throws Exception {
        try {
            String value = ExchangeHelper.getMandatoryProperty(exchange, "bar", String.class);
            Assert.fail(("Should have failed but got: " + value));
        } catch (NoSuchPropertyException e) {
            Assert.assertEquals("bar", e.getPropertyName());
        }
    }

    @Test
    public void testPropertyOfIncompatibleType() throws Exception {
        try {
            List<?> value = ExchangeHelper.getMandatoryProperty(exchange, "foo", List.class);
            Assert.fail(("Should have failed but got: " + value));
        } catch (NoSuchPropertyException e) {
            Assert.assertEquals("foo", e.getPropertyName());
        }
    }

    @Test
    public void testMissingHeader() throws Exception {
        try {
            String value = ExchangeHelper.getMandatoryHeader(exchange, "unknown", String.class);
            Assert.fail(("Should have failed but got: " + value));
        } catch (NoSuchHeaderException e) {
            Assert.assertEquals("unknown", e.getHeaderName());
        }
    }

    @Test
    public void testHeaderOfIncompatibleType() throws Exception {
        exchange.getIn().setHeader("foo", 123);
        try {
            List<?> value = ExchangeHelper.getMandatoryHeader(exchange, "foo", List.class);
            Assert.fail(("Should have failed but got: " + value));
        } catch (NoSuchHeaderException e) {
            Assert.assertEquals("foo", e.getHeaderName());
        }
    }

    @Test
    public void testNoSuchBean() throws Exception {
        try {
            ExchangeHelper.lookupMandatoryBean(exchange, "foo");
            Assert.fail("Should have thrown an exception");
        } catch (NoSuchBeanException e) {
            Assert.assertEquals("No bean could be found in the registry for: foo", e.getMessage());
            Assert.assertEquals("foo", e.getName());
        }
    }

    @Test
    public void testNoSuchBeanType() throws Exception {
        try {
            ExchangeHelper.lookupMandatoryBean(exchange, "foo", String.class);
            Assert.fail("Should have thrown an exception");
        } catch (NoSuchBeanException e) {
            Assert.assertEquals("No bean could be found in the registry for: foo", e.getMessage());
            Assert.assertEquals("foo", e.getName());
        }
    }

    @Test
    public void testGetExchangeById() throws Exception {
        List<Exchange> list = new ArrayList<>();
        Exchange e1 = context.getEndpoint("mock:foo").createExchange();
        Exchange e2 = context.getEndpoint("mock:foo").createExchange();
        list.add(e1);
        list.add(e2);
        Assert.assertNull(ExchangeHelper.getExchangeById(list, "unknown"));
        Assert.assertEquals(e1, ExchangeHelper.getExchangeById(list, e1.getExchangeId()));
        Assert.assertEquals(e2, ExchangeHelper.getExchangeById(list, e2.getExchangeId()));
    }

    @Test
    public void testPopulateVariableMap() throws Exception {
        exchange.setPattern(InOut);
        exchange.getOut().setBody("bar");
        exchange.getOut().setHeader("quote", "Camel rocks");
        Map<String, Object> map = new HashMap<>();
        ExchangeHelper.populateVariableMap(exchange, map);
        Assert.assertEquals(8, map.size());
        Assert.assertSame(exchange, map.get("exchange"));
        Assert.assertSame(exchange.getIn(), map.get("in"));
        Assert.assertSame(exchange.getIn(), map.get("request"));
        Assert.assertSame(exchange.getOut(), map.get("out"));
        Assert.assertSame(exchange.getOut(), map.get("response"));
        Assert.assertSame(exchange.getIn().getHeaders(), map.get("headers"));
        Assert.assertSame(exchange.getIn().getBody(), map.get("body"));
        Assert.assertSame(exchange.getContext(), map.get("camelContext"));
    }

    @Test
    public void testCreateVariableMap() throws Exception {
        exchange.setPattern(InOut);
        exchange.getOut().setBody("bar");
        exchange.getOut().setHeader("quote", "Camel rocks");
        Map<?, ?> map = ExchangeHelper.createVariableMap(exchange);
        Assert.assertEquals(8, map.size());
        Assert.assertSame(exchange, map.get("exchange"));
        Assert.assertSame(exchange.getIn(), map.get("in"));
        Assert.assertSame(exchange.getIn(), map.get("request"));
        Assert.assertSame(exchange.getOut(), map.get("out"));
        Assert.assertSame(exchange.getOut(), map.get("response"));
        Assert.assertSame(exchange.getIn().getHeaders(), map.get("headers"));
        Assert.assertSame(exchange.getIn().getBody(), map.get("body"));
        Assert.assertSame(exchange.getContext(), map.get("camelContext"));
    }

    @Test
    public void testCreateVariableMapNoExistingOut() throws Exception {
        exchange.setPattern(InOut);
        exchange.getIn().setBody("bar");
        exchange.getIn().setHeader("quote", "Camel rocks");
        Assert.assertFalse(exchange.hasOut());
        Map<?, ?> map = ExchangeHelper.createVariableMap(exchange);
        // there should still be 8 in the map
        Assert.assertEquals(8, map.size());
        Assert.assertSame(exchange, map.get("exchange"));
        Assert.assertSame(exchange.getIn(), map.get("in"));
        Assert.assertSame(exchange.getIn(), map.get("request"));
        Assert.assertSame(exchange.getIn(), map.get("out"));
        Assert.assertSame(exchange.getIn(), map.get("response"));
        Assert.assertSame(exchange.getIn().getHeaders(), map.get("headers"));
        Assert.assertSame(exchange.getIn().getBody(), map.get("body"));
        Assert.assertSame(exchange.getContext(), map.get("camelContext"));
        // but the Exchange does still not have an OUT message to avoid
        // causing side effects with the createVariableMap method
        Assert.assertFalse(exchange.hasOut());
    }

    @Test
    public void testGetContentType() throws Exception {
        exchange.getIn().setHeader(CONTENT_TYPE, "text/xml");
        Assert.assertEquals("text/xml", ExchangeHelper.getContentType(exchange));
    }

    @Test
    public void testGetContentEncoding() throws Exception {
        exchange.getIn().setHeader(CONTENT_ENCODING, "iso-8859-1");
        Assert.assertEquals("iso-8859-1", ExchangeHelper.getContentEncoding(exchange));
    }

    @Test
    public void testIsStreamCaching() throws Exception {
        Assert.assertFalse(ExchangeHelper.isStreamCachingEnabled(exchange));
        exchange.getContext().getStreamCachingStrategy().setEnabled(true);
        Assert.assertTrue(ExchangeHelper.isStreamCachingEnabled(exchange));
    }
}

