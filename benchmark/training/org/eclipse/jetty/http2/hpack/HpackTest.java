/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http2.hpack;


import HpackContext.STATIC_TABLE;
import HpackException.SessionException;
import HttpHeader.CONTENT_ENCODING;
import HttpHeader.CONTENT_LENGTH;
import HttpHeader.CONTENT_TYPE;
import HttpHeader.SET_COOKIE;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.DateGenerator;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.MetaData.Response;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HpackTest {
    static final HttpField ServerJetty = new org.eclipse.jetty.http.PreEncodedHttpField(HttpHeader.SERVER, "jetty");

    static final HttpField XPowerJetty = new org.eclipse.jetty.http.PreEncodedHttpField(HttpHeader.X_POWERED_BY, "jetty");

    static final HttpField Date = new org.eclipse.jetty.http.PreEncodedHttpField(HttpHeader.DATE, DateGenerator.formatDate(TimeUnit.NANOSECONDS.toMillis(System.nanoTime())));

    @Test
    public void encodeDecodeResponseTest() throws Exception {
        HpackEncoder encoder = new HpackEncoder();
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        ByteBuffer buffer = BufferUtil.allocateDirect((16 * 1024));
        HttpFields fields0 = new HttpFields();
        fields0.add(CONTENT_TYPE, "text/html");
        fields0.add(CONTENT_LENGTH, "1024");
        fields0.add(new HttpField(HttpHeader.CONTENT_ENCODING, ((String) (null))));
        fields0.add(HpackTest.ServerJetty);
        fields0.add(HpackTest.XPowerJetty);
        fields0.add(HpackTest.Date);
        fields0.add(SET_COOKIE, "abcdefghijklmnopqrstuvwxyz");
        fields0.add("custom-key", "custom-value");
        Response original0 = new MetaData.Response(HttpVersion.HTTP_2, 200, fields0);
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original0);
        BufferUtil.flipToFlush(buffer, 0);
        Response decoded0 = ((Response) (decoder.decode(buffer)));
        original0.getFields().put(new HttpField(HttpHeader.CONTENT_ENCODING, ""));
        assertMetadataSame(original0, decoded0);
        // Same again?
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original0);
        BufferUtil.flipToFlush(buffer, 0);
        Response decoded0b = ((Response) (decoder.decode(buffer)));
        assertMetadataSame(original0, decoded0b);
        HttpFields fields1 = new HttpFields();
        fields1.add(CONTENT_TYPE, "text/plain");
        fields1.add(CONTENT_LENGTH, "1234");
        fields1.add(CONTENT_ENCODING, " ");
        fields1.add(HpackTest.ServerJetty);
        fields1.add(HpackTest.XPowerJetty);
        fields1.add(HpackTest.Date);
        fields1.add("Custom-Key", "Other-Value");
        Response original1 = new MetaData.Response(HttpVersion.HTTP_2, 200, fields1);
        // Same again?
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original1);
        BufferUtil.flipToFlush(buffer, 0);
        Response decoded1 = ((Response) (decoder.decode(buffer)));
        assertMetadataSame(original1, decoded1);
        Assertions.assertEquals("custom-key", decoded1.getFields().getField("Custom-Key").getName());
    }

    @Test
    public void encodeDecodeTooLargeTest() throws Exception {
        HpackEncoder encoder = new HpackEncoder();
        HpackDecoder decoder = new HpackDecoder(4096, 164);
        ByteBuffer buffer = BufferUtil.allocateDirect((16 * 1024));
        HttpFields fields0 = new HttpFields();
        fields0.add("1234567890", "1234567890123456789012345678901234567890");
        fields0.add("Cookie", "abcdeffhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR");
        MetaData original0 = new MetaData(HttpVersion.HTTP_2, fields0);
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original0);
        BufferUtil.flipToFlush(buffer, 0);
        MetaData decoded0 = ((MetaData) (decoder.decode(buffer)));
        assertMetadataSame(original0, decoded0);
        HttpFields fields1 = new HttpFields();
        fields1.add("1234567890", "1234567890123456789012345678901234567890");
        fields1.add("Cookie", "abcdeffhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR");
        fields1.add("x", "y");
        MetaData original1 = new MetaData(HttpVersion.HTTP_2, fields1);
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original1);
        BufferUtil.flipToFlush(buffer, 0);
        try {
            decoder.decode(buffer);
            Assertions.fail();
        } catch (HpackException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Header too large"));
        }
    }

    @Test
    public void evictReferencedFieldTest() throws Exception {
        HpackEncoder encoder = new HpackEncoder(200, 200);
        HpackDecoder decoder = new HpackDecoder(200, 1024);
        ByteBuffer buffer = BufferUtil.allocateDirect((16 * 1024));
        HttpFields fields0 = new HttpFields();
        fields0.add("123456789012345678901234567890123456788901234567890", "value");
        fields0.add("foo", "abcdeffhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR");
        MetaData original0 = new MetaData(HttpVersion.HTTP_2, fields0);
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original0);
        BufferUtil.flipToFlush(buffer, 0);
        MetaData decoded0 = ((MetaData) (decoder.decode(buffer)));
        Assertions.assertEquals(2, encoder.getHpackContext().size());
        Assertions.assertEquals(2, decoder.getHpackContext().size());
        Assertions.assertEquals("123456789012345678901234567890123456788901234567890", encoder.getHpackContext().get(((STATIC_TABLE.length) + 1)).getHttpField().getName());
        Assertions.assertEquals("foo", encoder.getHpackContext().get(((STATIC_TABLE.length) + 0)).getHttpField().getName());
        assertMetadataSame(original0, decoded0);
        HttpFields fields1 = new HttpFields();
        fields1.add("123456789012345678901234567890123456788901234567890", "other_value");
        fields1.add("x", "y");
        MetaData original1 = new MetaData(HttpVersion.HTTP_2, fields1);
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, original1);
        BufferUtil.flipToFlush(buffer, 0);
        MetaData decoded1 = ((MetaData) (decoder.decode(buffer)));
        assertMetadataSame(original1, decoded1);
        Assertions.assertEquals(2, encoder.getHpackContext().size());
        Assertions.assertEquals(2, decoder.getHpackContext().size());
        Assertions.assertEquals("x", encoder.getHpackContext().get(((STATIC_TABLE.length) + 0)).getHttpField().getName());
        Assertions.assertEquals("foo", encoder.getHpackContext().get(((STATIC_TABLE.length) + 1)).getHttpField().getName());
    }
}

