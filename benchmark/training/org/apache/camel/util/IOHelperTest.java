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


import Exchange.CHARSET_NAME;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.ExchangeHelper;
import org.junit.Assert;
import org.junit.Test;


public class IOHelperTest extends Assert {
    @Test
    public void testIOException() {
        IOException io = new IOException("Damn", new IllegalArgumentException("Damn"));
        Assert.assertEquals("Damn", io.getMessage());
        Assert.assertTrue(((io.getCause()) instanceof IllegalArgumentException));
    }

    @Test
    public void testIOExceptionWithMessage() {
        IOException io = new IOException("Not again", new IllegalArgumentException("Damn"));
        Assert.assertEquals("Not again", io.getMessage());
        Assert.assertTrue(((io.getCause()) instanceof IllegalArgumentException));
    }

    @Test
    public void testNewStringFromBytes() {
        String s = IOHelper.newStringFromBytes("Hello".getBytes());
        Assert.assertEquals("Hello", s);
    }

    @Test
    public void testNewStringFromBytesWithStart() {
        String s = IOHelper.newStringFromBytes("Hello".getBytes(), 2, 3);
        Assert.assertEquals("llo", s);
    }

    @Test
    public void testCopyAndCloseInput() throws Exception {
        InputStream is = new ByteArrayInputStream("Hello".getBytes());
        OutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os, 256);
    }

    @Test
    public void testCharsetNormalize() throws Exception {
        Assert.assertEquals("UTF-8", IOHelper.normalizeCharset("'UTF-8'"));
        Assert.assertEquals("UTF-8", IOHelper.normalizeCharset("\"UTF-8\""));
        Assert.assertEquals("UTF-8", IOHelper.normalizeCharset("\"UTF-8 \""));
        Assert.assertEquals("UTF-8", IOHelper.normalizeCharset("\' UTF-8\'"));
    }

    @Test
    public void testLine1() throws Exception {
        assertReadAsWritten("line1", "line1", "line1\n");
    }

    @Test
    public void testLine1LF() throws Exception {
        assertReadAsWritten("line1LF", "line1\n", "line1\n");
    }

    @Test
    public void testLine2() throws Exception {
        assertReadAsWritten("line2", "line1\nline2", "line1\nline2\n");
    }

    @Test
    public void testLine2LF() throws Exception {
        assertReadAsWritten("line2LF", "line1\nline2\n", "line1\nline2\n");
    }

    @Test
    public void testCharsetName() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        Assert.assertNull(ExchangeHelper.getCharsetName(exchange, false));
        exchange.getIn().setHeader(CHARSET_NAME, "iso-8859-1");
        Assert.assertEquals("iso-8859-1", ExchangeHelper.getCharsetName(exchange, false));
        exchange.getIn().removeHeader(CHARSET_NAME);
        exchange.setProperty(CHARSET_NAME, "iso-8859-1");
        Assert.assertEquals("iso-8859-1", ExchangeHelper.getCharsetName(exchange, false));
    }

    @Test
    public void testGetCharsetNameFromContentType() throws Exception {
        String charsetName = IOHelper.getCharsetNameFromContentType("text/html; charset=iso-8859-1");
        Assert.assertEquals("iso-8859-1", charsetName);
        charsetName = IOHelper.getCharsetNameFromContentType("text/html");
        Assert.assertEquals("UTF-8", charsetName);
    }
}

