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
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.StreamCache;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.apache.camel.support.MessageHelper;
import org.apache.camel.support.dump.MessageDump;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link MessageHelper}
 */
public class MessageHelperTest extends Assert {
    private Message message;

    private CamelContext camelContext = new DefaultCamelContext();

    /* Tests the {@link MessageHelper#resetStreamCache(Message)} method */
    @Test
    public void testResetStreamCache() throws Exception {
        // should not throw exceptions when Message or message body is null
        MessageHelper.resetStreamCache(null);
        MessageHelper.resetStreamCache(message);
        // handle StreamCache
        final AtomicBoolean reset = new AtomicBoolean();
        message.setBody(new StreamCache() {
            public void reset() {
                reset.set(true);
            }

            public void writeTo(OutputStream os) throws IOException {
                // noop
            }

            public StreamCache copy(Exchange exchange) throws IOException {
                return null;
            }

            public boolean inMemory() {
                return true;
            }

            @Override
            public long length() {
                return 0;
            }
        });
        MessageHelper.resetStreamCache(message);
        Assert.assertTrue("Should have reset the stream cache", reset.get());
    }

    @Test
    public void testGetContentType() throws Exception {
        message.setHeader(CONTENT_TYPE, "text/xml");
        Assert.assertEquals("text/xml", MessageHelper.getContentType(message));
    }

    @Test
    public void testGetContentEncpding() throws Exception {
        message.setHeader(CONTENT_ENCODING, "iso-8859-1");
        Assert.assertEquals("iso-8859-1", MessageHelper.getContentEncoding(message));
    }

    @Test
    public void testCopyHeaders() throws Exception {
        Message source = message;
        Message target = new org.apache.camel.support.DefaultMessage(camelContext);
        source.setHeader("foo", 123);
        source.setHeader("bar", 456);
        target.setHeader("bar", "yes");
        MessageHelper.copyHeaders(source, target, false);
        Assert.assertEquals(123, target.getHeader("foo"));
        Assert.assertEquals("yes", target.getHeader("bar"));
    }

    @Test
    public void testCopyHeadersOverride() throws Exception {
        Message source = message;
        Message target = new org.apache.camel.support.DefaultMessage(camelContext);
        source.setHeader("foo", 123);
        source.setHeader("bar", 456);
        target.setHeader("bar", "yes");
        MessageHelper.copyHeaders(source, target, true);
        Assert.assertEquals(123, target.getHeader("foo"));
        Assert.assertEquals(456, target.getHeader("bar"));
    }

    @Test
    public void testCopyHeadersWithHeaderFilterStrategy() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        message = getIn();
        Message source = message;
        Message target = message.getExchange().getOut();
        DefaultHeaderFilterStrategy headerFilterStrategy = new DefaultHeaderFilterStrategy();
        headerFilterStrategy.setInFilterPattern("foo");
        source.setHeader("foo", 123);
        source.setHeader("bar", 456);
        target.setHeader("bar", "yes");
        MessageHelper.copyHeaders(source, target, headerFilterStrategy, true);
        Assert.assertEquals(null, target.getHeader("foo"));
        Assert.assertEquals(456, target.getHeader("bar"));
        context.stop();
    }

    @Test
    public void testDumpAsXmlPlainBody() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        message = getIn();
        // xml message body
        message.setBody("Hello World");
        message.setHeader("foo", 123);
        String out = MessageHelper.dumpAsXml(message);
        Assert.assertTrue("Should contain body", out.contains("<body type=\"java.lang.String\">Hello World</body>"));
        context.stop();
    }

    @Test
    public void testDumpAsXmlBody() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        message = getIn();
        // xml message body
        message.setBody("<?xml version=\"1.0\"?><hi>Hello World</hi>");
        message.setHeader("foo", 123);
        String out = MessageHelper.dumpAsXml(message);
        Assert.assertTrue("Should contain body", out.contains("<body type=\"java.lang.String\">&lt;?xml version=&quot;1.0&quot;?&gt;&lt;hi&gt;Hello World&lt;/hi&gt;</body>"));
        Assert.assertTrue("Should contain exchangeId", out.contains(message.getExchange().getExchangeId()));
        context.stop();
    }

    @Test
    public void testDumpAsXmlNoBody() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        message = getIn();
        // xml message body
        message.setBody("Hello World");
        message.setHeader("foo", 123);
        String out = MessageHelper.dumpAsXml(message, false);
        Assert.assertEquals(((("<message exchangeId=\"" + (message.getExchange().getExchangeId())) + "\">") + "\n  <headers>\n    <header key=\"foo\" type=\"java.lang.Integer\">123</header>\n  </headers>\n</message>"), out);
        context.stop();
    }

    @Test
    public void testDumpAsXmlNoBodyIndent() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        message = getIn();
        // xml message body
        message.setBody("Hello World");
        message.setHeader("foo", 123);
        String out = MessageHelper.dumpAsXml(message, false, 2);
        Assert.assertEquals(((("  <message exchangeId=\"" + (message.getExchange().getExchangeId())) + "\">") + "\n    <headers>\n      <header key=\"foo\" type=\"java.lang.Integer\">123</header>\n    </headers>\n  </message>"), out);
        context.stop();
    }

    @Test
    public void testMessageDump() throws Exception {
        JAXBContext jaxb = JAXBContext.newInstance(MessageDump.class);
        Unmarshaller unmarshaller = jaxb.createUnmarshaller();
        CamelContext context = new DefaultCamelContext();
        context.start();
        message = getIn();
        // xml message body
        message.setBody("Hello World");
        message.setHeader("foo", 123);
        String out = MessageHelper.dumpAsXml(message, true);
        MessageDump dump = ((MessageDump) (unmarshaller.unmarshal(new StringReader(out))));
        Assert.assertNotNull(dump);
        Assert.assertEquals("java.lang.String", dump.getBody().getType());
        Assert.assertEquals("Hello World", dump.getBody().getValue());
        Assert.assertEquals(1, dump.getHeaders().size());
        Assert.assertEquals("foo", dump.getHeaders().get(0).getKey());
        Assert.assertEquals("java.lang.Integer", dump.getHeaders().get(0).getType());
        Assert.assertEquals("123", dump.getHeaders().get(0).getValue());
    }
}

