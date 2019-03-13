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


import HpackException.SessionException;
import HttpHeader.AUTHORIZATION;
import HttpHeader.COOKIE;
import HttpHeader.HOST;
import HttpScheme.HTTP;
import HttpScheme.HTTPS;
import MetaData.Request;
import MetaData.Response;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.hpack.HpackException.CompressionException;
import org.eclipse.jetty.http2.hpack.HpackException.StreamException;
import org.eclipse.jetty.util.TypeUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class HpackDecoderTest {
    @Test
    public void testDecodeD_3() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        // First request
        String encoded = "828684410f7777772e6578616d706c652e636f6d";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        MetaData.Request request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        Assertions.assertFalse(request.iterator().hasNext());
        // Second request
        encoded = "828684be58086e6f2d6361636865";
        buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        Iterator<HttpField> iterator = request.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(new HttpField("cache-control", "no-cache"), iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        // Third request
        encoded = "828785bf400a637573746f6d2d6b65790c637573746f6d2d76616c7565";
        buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTPS.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/index.html", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        iterator = request.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(new HttpField("custom-key", "custom-value"), iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    public void testDecodeD_4() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        // First request
        String encoded = "828684418cf1e3c2e5f23a6ba0ab90f4ff";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        MetaData.Request request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        Assertions.assertFalse(request.iterator().hasNext());
        // Second request
        encoded = "828684be5886a8eb10649cbf";
        buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        Iterator<HttpField> iterator = request.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(new HttpField("cache-control", "no-cache"), iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    public void testDecodeWithArrayOffset() throws Exception {
        String value = "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "8682418cF1E3C2E5F23a6bA0Ab90F4Ff841f0822426173696320515778685a475270626a70766347567549484e6c633246745a513d3d";
        byte[] bytes = TypeUtil.fromHexString(encoded);
        byte[] array = new byte[(bytes.length) + 1];
        System.arraycopy(bytes, 0, array, 1, bytes.length);
        ByteBuffer buffer = ByteBuffer.wrap(array, 1, bytes.length).slice();
        MetaData.Request request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        Assertions.assertEquals(1, request.getFields().size());
        HttpField field = request.iterator().next();
        Assertions.assertEquals(AUTHORIZATION, field.getHeader());
        Assertions.assertEquals(value, field.getValue());
    }

    @Test
    public void testDecodeHuffmanWithArrayOffset() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "8286418cf1e3c2e5f23a6ba0ab90f4ff84";
        byte[] bytes = TypeUtil.fromHexString(encoded);
        byte[] array = new byte[(bytes.length) + 1];
        System.arraycopy(bytes, 0, array, 1, bytes.length);
        ByteBuffer buffer = ByteBuffer.wrap(array, 1, bytes.length).slice();
        MetaData.Request request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("www.example.com", request.getURI().getHost());
        Assertions.assertFalse(request.iterator().hasNext());
    }

    @Test
    public void testNghttpx() throws Exception {
        // Response encoded by nghttpx
        String encoded = "886196C361Be940b6a65B6850400B8A00571972e080a62D1Bf5f87497cA589D34d1f9a0f0d0234327690Aa69D29aFcA954D3A5358980Ae112e0f7c880aE152A9A74a6bF3";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        MetaData.Response response = ((MetaData.Response) (decoder.decode(buffer)));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(response.getFields().size(), Matchers.is(6));
        MatcherAssert.assertThat(response.getFields(), containsHeaderValue(HttpHeader.DATE, "Fri, 15 Jul 2016 02:36:20 GMT"));
        MatcherAssert.assertThat(response.getFields(), containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/html"));
        MatcherAssert.assertThat(response.getFields(), containsHeaderValue(HttpHeader.CONTENT_ENCODING, ""));
        MatcherAssert.assertThat(response.getFields(), containsHeaderValue(HttpHeader.CONTENT_LENGTH, "42"));
        MatcherAssert.assertThat(response.getFields(), containsHeaderValue(HttpHeader.SERVER, "nghttpx nghttp2/1.12.0"));
        MatcherAssert.assertThat(response.getFields(), containsHeaderValue(HttpHeader.VIA, "1.1 nghttpx"));
    }

    @Test
    public void testResize() throws Exception {
        String encoded = "203f136687A0E41d139d090760881c6490B2Cd39Ba7f";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        MetaData metaData = decoder.decode(buffer);
        MatcherAssert.assertThat(metaData.getFields().get(HOST), Matchers.is("localhost0"));
        MatcherAssert.assertThat(metaData.getFields().get(COOKIE), Matchers.is("abcdefghij"));
        MatcherAssert.assertThat(decoder.getHpackContext().getMaxDynamicTableSize(), Matchers.is(50));
        MatcherAssert.assertThat(decoder.getHpackContext().size(), Matchers.is(1));
    }

    @Test
    public void testBadResize() throws Exception {
        /* 4. Dynamic Table Management
        4.2. Maximum Table Size
        ? 1: Sends a dynamic table size update at the end of header block
        -> The endpoint MUST treat this as a decoding error.
        Expected: GOAWAY Frame (Error Code: COMPRESSION_ERROR)
        Connection closed
         */
        String encoded = "203f136687A0E41d139d090760881c6490B2Cd39Ba7f20";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        try {
            decoder.decode(buffer);
            Assertions.fail();
        } catch (CompressionException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Dynamic table resize after fields"));
        }
    }

    @Test
    public void testTooBigToIndex() throws Exception {
        String encoded = "3f610f17FfEc02Df3990A190A0D4Ee5b3d2940Ec98Aa4a62D127D29e273a0aA20dEcAa190a503b262d8a2671D4A2672a927aA874988a2471D05510750c951139EdA2452a3a548cAa1aA90bE4B228342864A9E0D450A5474a92992a1aA513395448E3A0Aa17B96cFe3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f3f14E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F3E7Cf9f3e7cF9F353F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F7F54f";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        HpackDecoder decoder = new HpackDecoder(128, 8192);
        MetaData metaData = decoder.decode(buffer);
        MatcherAssert.assertThat(decoder.getHpackContext().getDynamicTableSize(), Matchers.is(0));
        MatcherAssert.assertThat(metaData.getFields().get("host"), Matchers.startsWith("This is a very large field"));
    }

    @Test
    public void testUnknownIndex() throws Exception {
        String encoded = "BE";
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        HpackDecoder decoder = new HpackDecoder(128, 8192);
        try {
            decoder.decode(buffer);
            Assertions.fail();
        } catch (HpackException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.startsWith("Unknown index"));
        }
    }

    /* 8.1.2.1. Pseudo-Header Fields */
    @Test
    public void test8_1_2_1_PsuedoHeaderFields() throws Exception {
        // 1:Sends a HEADERS frame that contains a unknown pseudo-header field
        MetaDataBuilder mdb = new MetaDataBuilder(4096);
        mdb.emit(new HttpField(":unknown", "value"));
        try {
            mdb.build();
            Assertions.fail();
        } catch (StreamException ex) {
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Unknown pseudo header"));
        }
        // 2: Sends a HEADERS frame that contains the pseudo-header field defined for response
        mdb = new MetaDataBuilder(4096);
        mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
        mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
        mdb.emit(new HttpField(HttpHeader.C_PATH, "/path"));
        mdb.emit(new HttpField(HttpHeader.C_STATUS, "100"));
        try {
            mdb.build();
            Assertions.fail();
        } catch (StreamException ex) {
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Request and Response headers"));
        }
        // 3: Sends a HEADERS frame that contains a pseudo-header field as trailers
        // 4: Sends a HEADERS frame that contains a pseudo-header field that appears in a header block after a regular header field
        mdb = new MetaDataBuilder(4096);
        mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
        mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
        mdb.emit(new HttpField(HttpHeader.C_PATH, "/path"));
        mdb.emit(new HttpField("Accept", "No Compromise"));
        mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost"));
        try {
            mdb.build();
            Assertions.fail();
        } catch (StreamException ex) {
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Pseudo header :authority after fields"));
        }
    }

    @Test
    public void test8_1_2_2_ConnectionSpecificHeaderFields() throws Exception {
        MetaDataBuilder mdb;
        // 1: Sends a HEADERS frame that contains the connection-specific header field
        mdb = new MetaDataBuilder(4096);
        mdb.emit(new HttpField(HttpHeader.CONNECTION, "value"));
        try {
            mdb.build();
            Assertions.fail();
        } catch (StreamException ex) {
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Connection specific field 'Connection'"));
        }
        // 2: Sends a HEADERS frame that contains the TE header field with any value other than "trailers"
        mdb = new MetaDataBuilder(4096);
        mdb.emit(new HttpField(HttpHeader.TE, "not_trailers"));
        try {
            mdb.build();
            Assertions.fail();
        } catch (StreamException ex) {
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Unsupported TE value 'not_trailers'"));
        }
        mdb = new MetaDataBuilder(4096);
        mdb.emit(new HttpField(HttpHeader.CONNECTION, "TE"));
        mdb.emit(new HttpField(HttpHeader.TE, "trailers"));
        Assertions.assertNotNull(mdb.build());
    }

    @Test
    public void test8_1_2_3_RequestPseudoHeaderFields() throws Exception {
        {
            MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            mdb.emit(new HttpField(HttpHeader.C_PATH, "/"));
            MatcherAssert.assertThat(mdb.build(), Matchers.instanceOf(Request.class));
        }
        {
            // 1: Sends a HEADERS frame with empty ":path" pseudo-header field
            final MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            mdb.emit(new HttpField(HttpHeader.C_PATH, ""));
            StreamException ex = Assertions.assertThrows(StreamException.class, () -> mdb.build());
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("No Path"));
        }
        {
            // 2: Sends a HEADERS frame that omits ":method" pseudo-header field
            final MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            mdb.emit(new HttpField(HttpHeader.C_PATH, "/"));
            StreamException ex = Assertions.assertThrows(StreamException.class, () -> mdb.build());
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("No Method"));
        }
        {
            // 3: Sends a HEADERS frame that omits ":scheme" pseudo-header field
            final MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            mdb.emit(new HttpField(HttpHeader.C_PATH, "/"));
            StreamException ex = Assertions.assertThrows(StreamException.class, () -> mdb.build());
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("No Scheme"));
        }
        {
            // 4: Sends a HEADERS frame that omits ":path" pseudo-header field
            final MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            StreamException ex = Assertions.assertThrows(StreamException.class, () -> mdb.build());
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("No Path"));
        }
        {
            // 5: Sends a HEADERS frame with duplicated ":method" pseudo-header field
            final MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            mdb.emit(new HttpField(HttpHeader.C_PATH, "/"));
            StreamException ex = Assertions.assertThrows(StreamException.class, () -> mdb.build());
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Duplicate"));
        }
        {
            // 6: Sends a HEADERS frame with duplicated ":scheme" pseudo-header field
            final MetaDataBuilder mdb = new MetaDataBuilder(4096);
            mdb.emit(new HttpField(HttpHeader.C_METHOD, "GET"));
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_SCHEME, "http"));
            mdb.emit(new HttpField(HttpHeader.C_AUTHORITY, "localhost:8080"));
            mdb.emit(new HttpField(HttpHeader.C_PATH, "/"));
            StreamException ex = Assertions.assertThrows(StreamException.class, () -> mdb.build());
            MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Duplicate"));
        }
    }

    @Test
    public void testHuffmanEncodedStandard() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "82868441" + ("83" + "49509F");
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        MetaData.Request request = ((MetaData.Request) (decoder.decode(buffer)));
        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals(HTTP.asString(), request.getURI().getScheme());
        Assertions.assertEquals("/", request.getURI().getPath());
        Assertions.assertEquals("test", request.getURI().getHost());
        Assertions.assertFalse(request.iterator().hasNext());
    }

    /* 5.2.1: Sends a Huffman-encoded string literal representation with padding longer than 7 bits */
    @Test
    public void testHuffmanEncodedExtraPadding() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "82868441" + ("84" + "49509FFF");
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        CompressionException ex = Assertions.assertThrows(CompressionException.class, () -> decoder.decode(buffer));
        MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Bad termination"));
    }

    /* 5.2.2: Sends a Huffman-encoded string literal representation padded by zero */
    @Test
    public void testHuffmanEncodedZeroPadding() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "82868441" + ("83" + "495090");
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        CompressionException ex = Assertions.assertThrows(CompressionException.class, () -> decoder.decode(buffer));
        MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Incorrect padding"));
    }

    /* 5.2.3: Sends a Huffman-encoded string literal representation containing the EOS symbol */
    @Test
    public void testHuffmanEncodedWithEOS() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "82868441" + ("87" + "497FFFFFFF427F");
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        CompressionException ex = Assertions.assertThrows(CompressionException.class, () -> decoder.decode(buffer));
        MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("EOS in content"));
    }

    @Test
    public void testHuffmanEncodedOneIncompleteOctet() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "82868441" + ("81" + "FE");
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        CompressionException ex = Assertions.assertThrows(CompressionException.class, () -> decoder.decode(buffer));
        MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Bad termination"));
    }

    @Test
    public void testHuffmanEncodedTwoIncompleteOctet() throws Exception {
        HpackDecoder decoder = new HpackDecoder(4096, 8192);
        String encoded = "82868441" + ("82" + "FFFE");
        ByteBuffer buffer = ByteBuffer.wrap(TypeUtil.fromHexString(encoded));
        CompressionException ex = Assertions.assertThrows(CompressionException.class, () -> decoder.decode(buffer));
        MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Bad termination"));
    }
}

