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


import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultMessage;
import org.junit.Assert;
import org.junit.Test;


public class DefaultMessageHeaderTest extends Assert {
    private CamelContext camelContext = new DefaultCamelContext();

    @Test
    public void testLookupCaseAgnostic() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        Assert.assertEquals("cheese", msg.getHeader("foo"));
        Assert.assertEquals("cheese", msg.getHeader("Foo"));
        Assert.assertEquals("cheese", msg.getHeader("FOO"));
    }

    @Test
    public void testLookupCaseAgnosticAddHeader() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        Assert.assertEquals("cheese", msg.getHeader("foo"));
        Assert.assertEquals("cheese", msg.getHeader("Foo"));
        Assert.assertEquals("cheese", msg.getHeader("FOO"));
        Assert.assertNull(msg.getHeader("unknown"));
        msg.setHeader("bar", "beer");
        Assert.assertEquals("beer", msg.getHeader("bar"));
        Assert.assertEquals("beer", msg.getHeader("Bar"));
        Assert.assertEquals("beer", msg.getHeader("BAR"));
        Assert.assertNull(msg.getHeader("unknown"));
    }

    @Test
    public void testLookupCaseAgnosticAddHeader2() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        Assert.assertEquals("cheese", msg.getHeader("FOO"));
        Assert.assertEquals("cheese", msg.getHeader("foo"));
        Assert.assertEquals("cheese", msg.getHeader("Foo"));
        Assert.assertNull(msg.getHeader("unknown"));
        msg.setHeader("bar", "beer");
        Assert.assertEquals("beer", msg.getHeader("BAR"));
        Assert.assertEquals("beer", msg.getHeader("bar"));
        Assert.assertEquals("beer", msg.getHeader("Bar"));
        Assert.assertNull(msg.getHeader("unknown"));
    }

    @Test
    public void testLookupCaseAgnosticAddHeaderRemoveHeader() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        Assert.assertEquals("cheese", msg.getHeader("foo"));
        Assert.assertEquals("cheese", msg.getHeader("Foo"));
        Assert.assertEquals("cheese", msg.getHeader("FOO"));
        Assert.assertNull(msg.getHeader("unknown"));
        msg.setHeader("bar", "beer");
        Assert.assertEquals("beer", msg.getHeader("bar"));
        Assert.assertEquals("beer", msg.getHeader("Bar"));
        Assert.assertEquals("beer", msg.getHeader("BAR"));
        Assert.assertNull(msg.getHeader("unknown"));
        msg.removeHeader("bar");
        Assert.assertNull(msg.getHeader("bar"));
        Assert.assertNull(msg.getHeader("unknown"));
    }

    @Test
    public void testSetWithDifferentCase() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        msg.setHeader("Foo", "bar");
        Assert.assertEquals("bar", msg.getHeader("FOO"));
        Assert.assertEquals("bar", msg.getHeader("foo"));
        Assert.assertEquals("bar", msg.getHeader("Foo"));
    }

    @Test
    public void testRemoveWithDifferentCase() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        msg.setHeader("Foo", "bar");
        Assert.assertEquals("bar", msg.getHeader("FOO"));
        Assert.assertEquals("bar", msg.getHeader("foo"));
        Assert.assertEquals("bar", msg.getHeader("Foo"));
        msg.removeHeader("FOO");
        Assert.assertEquals(null, msg.getHeader("foo"));
        Assert.assertEquals(null, msg.getHeader("Foo"));
        Assert.assertEquals(null, msg.getHeader("FOO"));
        Assert.assertTrue(msg.getHeaders().isEmpty());
    }

    @Test
    public void testRemoveHeaderWithNullValue() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", null);
        msg.removeHeader("tick");
        Assert.assertTrue(msg.getHeaders().isEmpty());
    }

    @Test
    public void testRemoveHeadersWithWildcard() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tack", "blaa");
        msg.setHeader("tock", "blaaa");
        Assert.assertEquals("bla", msg.getHeader("tick"));
        Assert.assertEquals("blaa", msg.getHeader("tack"));
        Assert.assertEquals("blaaa", msg.getHeader("tock"));
        msg.removeHeaders("t*");
        Assert.assertTrue(msg.getHeaders().isEmpty());
    }

    @Test
    public void testRemoveHeadersAllWithWildcard() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tack", "blaa");
        msg.setHeader("tock", "blaaa");
        Assert.assertEquals("bla", msg.getHeader("tick"));
        Assert.assertEquals("blaa", msg.getHeader("tack"));
        Assert.assertEquals("blaaa", msg.getHeader("tock"));
        msg.removeHeaders("*");
        Assert.assertTrue(msg.getHeaders().isEmpty());
    }

    @Test
    public void testRemoveHeadersWithExclude() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tiack", "blaa");
        msg.setHeader("tiock", "blaaa");
        msg.setHeader("tiuck", "blaaaa");
        msg.removeHeaders("ti*", "tiuck", "tiack");
        Assert.assertEquals(2, msg.getHeaders().size());
        Assert.assertEquals("blaa", msg.getHeader("tiack"));
        Assert.assertEquals("blaaaa", msg.getHeader("tiuck"));
    }

    @Test
    public void testRemoveHeadersAllWithExclude() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tack", "blaa");
        msg.setHeader("tock", "blaaa");
        Assert.assertEquals("bla", msg.getHeader("tick"));
        Assert.assertEquals("blaa", msg.getHeader("tack"));
        Assert.assertEquals("blaaa", msg.getHeader("tock"));
        msg.removeHeaders("*", "tick", "tock", "toe");
        // new message headers
        Assert.assertEquals("bla", msg.getHeader("tick"));
        Assert.assertEquals(null, msg.getHeader("tack"));
        Assert.assertEquals("blaaa", msg.getHeader("tock"));
    }

    @Test
    public void testRemoveHeadersWithWildcardInExclude() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tack", "blaa");
        msg.setHeader("taick", "blaa");
        msg.setHeader("tock", "blaaa");
        msg.removeHeaders("*", "ta*");
        Assert.assertEquals(2, msg.getHeaders().size());
        Assert.assertEquals("blaa", msg.getHeader("tack"));
        Assert.assertEquals("blaa", msg.getHeader("taick"));
    }

    @Test
    public void testRemoveHeadersWithNulls() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tack", "blaa");
        msg.setHeader("tock", "blaaa");
        msg.setHeader("taack", "blaaaa");
        Assert.assertEquals("bla", msg.getHeader("tick"));
        Assert.assertEquals("blaa", msg.getHeader("tack"));
        Assert.assertEquals("blaaa", msg.getHeader("tock"));
        Assert.assertEquals("blaaaa", msg.getHeader("taack"));
        msg.removeHeaders(null, null, null, null);
        Assert.assertFalse(msg.getHeaders().isEmpty());
    }

    @Test
    public void testRemoveHeadersWithNonExcludeHeaders() {
        Message msg = new DefaultMessage(camelContext);
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("tick", "bla");
        msg.setHeader("tack", "blaa");
        msg.setHeader("tock", "blaaa");
        msg.removeHeaders("*", "camels", "are", "fun");
        Assert.assertTrue(msg.getHeaders().isEmpty());
    }

    @Test
    public void testWithDefaults() {
        DefaultMessage msg = new DefaultMessage(camelContext);
        // must have exchange so to leverage the type converters
        msg.setExchange(new org.apache.camel.support.DefaultExchange(new DefaultCamelContext()));
        Assert.assertNull(msg.getHeader("foo"));
        msg.setHeader("foo", "cheese");
        Assert.assertEquals("cheese", msg.getHeader("foo"));
        Assert.assertEquals("cheese", msg.getHeader("foo", "foo"));
        Assert.assertEquals("cheese", msg.getHeader("foo", "foo", String.class));
        Assert.assertEquals(null, msg.getHeader("beer"));
        Assert.assertEquals("foo", msg.getHeader("beer", "foo"));
        Assert.assertEquals(Integer.valueOf(123), msg.getHeader("beer", "123", Integer.class));
    }
}

