/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.protocols.http2;


import io.undertow.testutils.category.UnitTest;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static Hpack.DEFAULT_TABLE_SIZE;


/**
 * HPACK unit test case, based on examples from the spec
 *
 * @author Stuart Douglas
 */
@Category(UnitTest.class)
public class HpackSpecExamplesUnitTestCase {
    @Test
    public void testExample_D_2_1() throws HpackException {
        // custom-key: custom-header
        byte[] data = new byte[]{ 64, 10, 99, 117, 115, 116, 111, 109, 45, 107, 101, 121, 13, 99, 117, 115, 116, 111, 109, 45, 104, 101, 97, 100, 101, 114 };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(1, emitter.map.size());
        Assert.assertEquals("custom-header", emitter.map.getFirst(new HttpString("custom-key")));
        Assert.assertEquals(1, decoder.getFilledTableSlots());
        Assert.assertEquals(55, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "custom-key", "custom-header");
    }

    @Test
    public void testExample_D_2_2() throws HpackException {
        // :path: /sample/path
        byte[] data = new byte[]{ 4, 12, 47, 115, 97, 109, 112, 108, 101, 47, 112, 97, 116, 104 };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(1, emitter.map.size());
        Assert.assertEquals("/sample/path", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals(0, decoder.getFilledTableSlots());
        Assert.assertEquals(0, decoder.getCurrentMemorySize());
    }

    @Test
    public void testExample_D_2_3() throws HpackException {
        // password: secret
        byte[] data = new byte[]{ 16, 8, 112, 97, 115, 115, 119, 111, 114, 100, 6, 115, 101, 99, 114, 101, 116 };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(1, emitter.map.size());
        Assert.assertEquals("secret", emitter.map.getFirst(new HttpString("password")));
        Assert.assertEquals(0, decoder.getFilledTableSlots());
        Assert.assertEquals(0, decoder.getCurrentMemorySize());
    }

    @Test
    public void testExample_D_2_4() throws HpackException {
        // :method: GET
        byte[] data = new byte[]{ ((byte) (130)) };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(1, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals(0, decoder.getFilledTableSlots());
        Assert.assertEquals(0, decoder.getCurrentMemorySize());
    }

    @Test
    public void testExample_D_3() throws HpackException {
        // d 3.1
        byte[] data = new byte[]{ ((byte) (130)), ((byte) (134)), ((byte) (132)), 65, 15, 119, 119, 119, 46, 101, 120, 97, 109, 112, 108, 101, 46, 99, 111, 109 };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(4, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals("http", emitter.map.getFirst(new HttpString(":scheme")));
        Assert.assertEquals("/", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals("www.example.com", emitter.map.getFirst(new HttpString(":authority")));
        Assert.assertEquals(1, decoder.getFilledTableSlots());
        Assert.assertEquals(57, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, ":authority", "www.example.com");
        // d 3.2
        data = new byte[]{ ((byte) (130)), ((byte) (134)), ((byte) (132)), ((byte) (190)), 88, 8, 110, 111, 45, 99, 97, 99, 104, 101 };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(5, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals("http", emitter.map.getFirst(new HttpString(":scheme")));
        Assert.assertEquals("/", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals("www.example.com", emitter.map.getFirst(new HttpString(":authority")));
        Assert.assertEquals("no-cache", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals(2, decoder.getFilledTableSlots());
        Assert.assertEquals(110, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "cache-control", "no-cache");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, ":authority", "www.example.com");
        // d 3.3
        data = new byte[]{ ((byte) (130)), ((byte) (135)), ((byte) (133)), ((byte) (191)), 64, 10, 99, 117, 115, 116, 111, 109, 45, 107, 101, 121, 12, 99, 117, 115, 116, 111, 109, 45, 118, 97, 108, 117, 101 };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(5, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals("https", emitter.map.getFirst(new HttpString(":scheme")));
        Assert.assertEquals("/index.html", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals("www.example.com", emitter.map.getFirst(new HttpString(":authority")));
        Assert.assertEquals("custom-value", emitter.map.getFirst(new HttpString("custom-key")));
        Assert.assertEquals(3, decoder.getFilledTableSlots());
        Assert.assertEquals(164, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "custom-key", "custom-value");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "cache-control", "no-cache");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, ":authority", "www.example.com");
    }

    @Test
    public void testExample_D_4() throws HpackException {
        // d 4.1
        byte[] data = new byte[]{ ((byte) (130)), ((byte) (134)), ((byte) (132)), 65, ((byte) (140)), ((byte) (241)), ((byte) (227)), ((byte) (194)), ((byte) (229)), ((byte) (242)), 58, 107, ((byte) (160)), ((byte) (171)), ((byte) (144)), ((byte) (244)), ((byte) (255)) };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(4, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals("http", emitter.map.getFirst(new HttpString(":scheme")));
        Assert.assertEquals("/", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals("www.example.com", emitter.map.getFirst(new HttpString(":authority")));
        Assert.assertEquals(1, decoder.getFilledTableSlots());
        Assert.assertEquals(57, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, ":authority", "www.example.com");
        // d 4.2
        data = new byte[]{ ((byte) (130)), ((byte) (134)), ((byte) (132)), ((byte) (190)), 88, ((byte) (134)), ((byte) (168)), ((byte) (235)), 16, 100, ((byte) (156)), ((byte) (191)) };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(5, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals("http", emitter.map.getFirst(new HttpString(":scheme")));
        Assert.assertEquals("/", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals("www.example.com", emitter.map.getFirst(new HttpString(":authority")));
        Assert.assertEquals("no-cache", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals(2, decoder.getFilledTableSlots());
        Assert.assertEquals(110, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "cache-control", "no-cache");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, ":authority", "www.example.com");
        // d 4.3
        data = new byte[]{ ((byte) (130)), ((byte) (135)), ((byte) (133)), ((byte) (191)), 64, ((byte) (136)), 37, ((byte) (168)), 73, ((byte) (233)), 91, ((byte) (169)), 125, 127, ((byte) (137)), 37, ((byte) (168)), 73, ((byte) (233)), 91, ((byte) (184)), ((byte) (232)), ((byte) (180)), ((byte) (191)) };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(5, emitter.map.size());
        Assert.assertEquals("GET", emitter.map.getFirst(new HttpString(":method")));
        Assert.assertEquals("https", emitter.map.getFirst(new HttpString(":scheme")));
        Assert.assertEquals("/index.html", emitter.map.getFirst(new HttpString(":path")));
        Assert.assertEquals("www.example.com", emitter.map.getFirst(new HttpString(":authority")));
        Assert.assertEquals("custom-value", emitter.map.getFirst(new HttpString("custom-key")));
        Assert.assertEquals(3, decoder.getFilledTableSlots());
        Assert.assertEquals(164, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "custom-key", "custom-value");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "cache-control", "no-cache");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, ":authority", "www.example.com");
    }

    @Test
    public void testExample_D_5() throws HpackException {
        byte[] data = new byte[]{ 72, 3, 51, 48, 50, 88, 7, 112, 114, 105, 118, 97, 116, 101, 97, 29, 77, 111, 110, 44, 32, 50, 49, 32, 79, 99, 116, 32, 50, 48, 49, 51, 32, 50, 48, 58, 49, 51, 58, 50, 49, 32, 71, 77, 84, 110, 23, 104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 101, 120, 97, 109, 112, 108, 101, 46, 99, 111, 109 };
        HpackDecoder decoder = new HpackDecoder(256);
        // d 5.1
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(4, emitter.map.size());
        Assert.assertEquals("302", emitter.map.getFirst(new HttpString(":status")));
        Assert.assertEquals("private", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals("Mon, 21 Oct 2013 20:13:21 GMT", emitter.map.getFirst(new HttpString("date")));
        Assert.assertEquals("https://www.example.com", emitter.map.getFirst(new HttpString("location")));
        Assert.assertEquals(4, decoder.getFilledTableSlots());
        Assert.assertEquals(222, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "location", "https://www.example.com");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "date", "Mon, 21 Oct 2013 20:13:21 GMT");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, "cache-control", "private");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 4, ":status", "302");
        // d 5.2
        data = new byte[]{ ((byte) (72)), 3, 51, 48, 55, ((byte) (193)), ((byte) (192)), ((byte) (191)) };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(4, emitter.map.size());
        Assert.assertEquals("307", emitter.map.getFirst(new HttpString(":status")));
        Assert.assertEquals("private", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals("Mon, 21 Oct 2013 20:13:21 GMT", emitter.map.getFirst(new HttpString("date")));
        Assert.assertEquals("https://www.example.com", emitter.map.getFirst(new HttpString("location")));
        Assert.assertEquals(4, decoder.getFilledTableSlots());
        Assert.assertEquals(222, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, ":status", "307");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "location", "https://www.example.com");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, "date", "Mon, 21 Oct 2013 20:13:21 GMT");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 4, "cache-control", "private");
        data = new byte[]{ ((byte) (136)), ((byte) (193)), 97, 29, 77, 111, 110, 44, 32, 50, 49, 32, 79, 99, 116, 32, 50, 48, 49, 51, 32, 50, 48, 58, 49, 51, 58, 50, 50, 32, 71, 77, 84, ((byte) (192)), 90, 4, 103, 122, 105, 112, 119, 56, 102, 111, 111, 61, 65, 83, 68, 74, 75, 72, 81, 75, 66, 90, 88, 79, 81, 87, 69, 79, 80, 73, 85, 65, 88, 81, 87, 69, 79, 73, 85, 59, 32, 109, 97, 120, 45, 97, 103, 101, 61, 51, 54, 48, 48, 59, 32, 118, 101, 114, 115, 105, 111, 110, 61, 49 };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(6, emitter.map.size());
        Assert.assertEquals("200", emitter.map.getFirst(new HttpString(":status")));
        Assert.assertEquals("private", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals("Mon, 21 Oct 2013 20:13:22 GMT", emitter.map.getFirst(new HttpString("date")));
        Assert.assertEquals("https://www.example.com", emitter.map.getFirst(new HttpString("location")));
        Assert.assertEquals("foo=ASDJKHQKBZXOQWEOPIUAXQWEOIU; max-age=3600; version=1", emitter.map.getFirst(new HttpString("set-cookie")));
        Assert.assertEquals("gzip", emitter.map.getFirst(new HttpString("content-encoding")));
        Assert.assertEquals(3, decoder.getFilledTableSlots());
        Assert.assertEquals(215, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "set-cookie", "foo=ASDJKHQKBZXOQWEOPIUAXQWEOIU; max-age=3600; version=1");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "content-encoding", "gzip");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, "date", "Mon, 21 Oct 2013 20:13:22 GMT");
    }

    @Test
    public void testExample_D_6() throws HpackException {
        byte[] data = new byte[]{ 72, ((byte) (130)), 100, 2, 88, ((byte) (133)), ((byte) (174)), ((byte) (195)), 119, 26, 75, 97, ((byte) (150)), ((byte) (208)), 122, ((byte) (190)), ((byte) (148)), 16, 84, ((byte) (212)), 68, ((byte) (168)), 32, 5, ((byte) (149)), 4, 11, ((byte) (129)), 102, ((byte) (224)), ((byte) (130)), ((byte) (166)), 45, 27, ((byte) (255)), 110, ((byte) (145)), ((byte) (157)), 41, ((byte) (173)), 23, 24, 99, ((byte) (199)), ((byte) (143)), 11, ((byte) (151)), ((byte) (200)), ((byte) (233)), ((byte) (174)), ((byte) (130)), ((byte) (174)), 67, ((byte) (211)) };
        HpackDecoder decoder = new HpackDecoder(256);
        // d 5.1
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(4, emitter.map.size());
        Assert.assertEquals("302", emitter.map.getFirst(new HttpString(":status")));
        Assert.assertEquals("private", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals("Mon, 21 Oct 2013 20:13:21 GMT", emitter.map.getFirst(new HttpString("date")));
        Assert.assertEquals("https://www.example.com", emitter.map.getFirst(new HttpString("location")));
        Assert.assertEquals(4, decoder.getFilledTableSlots());
        Assert.assertEquals(222, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "location", "https://www.example.com");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "date", "Mon, 21 Oct 2013 20:13:21 GMT");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, "cache-control", "private");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 4, ":status", "302");
        // d 5.2
        data = new byte[]{ ((byte) (72)), ((byte) (131)), 100, 14, ((byte) (255)), ((byte) (193)), ((byte) (192)), ((byte) (191)) };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(4, emitter.map.size());
        Assert.assertEquals("307", emitter.map.getFirst(new HttpString(":status")));
        Assert.assertEquals("private", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals("Mon, 21 Oct 2013 20:13:21 GMT", emitter.map.getFirst(new HttpString("date")));
        Assert.assertEquals("https://www.example.com", emitter.map.getFirst(new HttpString("location")));
        Assert.assertEquals(4, decoder.getFilledTableSlots());
        Assert.assertEquals(222, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, ":status", "307");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "location", "https://www.example.com");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, "date", "Mon, 21 Oct 2013 20:13:21 GMT");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 4, "cache-control", "private");
        data = new byte[]{ ((byte) (136)), ((byte) (193)), 97, ((byte) (150)), ((byte) (208)), 122, ((byte) (190)), ((byte) (148)), 16, 84, ((byte) (212)), 68, ((byte) (168)), 32, 5, ((byte) (149)), 4, 11, ((byte) (129)), 102, ((byte) (224)), ((byte) (132)), ((byte) (166)), 45, 27, ((byte) (255)), ((byte) (192)), 90, ((byte) (131)), ((byte) (155)), ((byte) (217)), ((byte) (171)), 119, ((byte) (173)), ((byte) (148)), ((byte) (231)), ((byte) (130)), 29, ((byte) (215)), ((byte) (242)), ((byte) (230)), ((byte) (199)), ((byte) (179)), 53, ((byte) (223)), ((byte) (223)), ((byte) (205)), 91, 57, 96, ((byte) (213)), ((byte) (175)), 39, 8, 127, 54, 114, ((byte) (193)), ((byte) (171)), 39, 15, ((byte) (181)), 41, 31, ((byte) (149)), ((byte) (135)), 49, 96, 101, ((byte) (192)), 3, ((byte) (237)), 78, ((byte) (229)), ((byte) (177)), 6, 61, 80, 7 };
        emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        decoder.decode(ByteBuffer.wrap(data), false);
        Assert.assertEquals(6, emitter.map.size());
        Assert.assertEquals("200", emitter.map.getFirst(new HttpString(":status")));
        Assert.assertEquals("private", emitter.map.getFirst(new HttpString("cache-control")));
        Assert.assertEquals("Mon, 21 Oct 2013 20:13:22 GMT", emitter.map.getFirst(new HttpString("date")));
        Assert.assertEquals("https://www.example.com", emitter.map.getFirst(new HttpString("location")));
        Assert.assertEquals("foo=ASDJKHQKBZXOQWEOPIUAXQWEOIU; max-age=3600; version=1", emitter.map.getFirst(new HttpString("set-cookie")));
        Assert.assertEquals("gzip", emitter.map.getFirst(new HttpString("content-encoding")));
        Assert.assertEquals(3, decoder.getFilledTableSlots());
        Assert.assertEquals(215, decoder.getCurrentMemorySize());
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 1, "set-cookie", "foo=ASDJKHQKBZXOQWEOPIUAXQWEOIU; max-age=3600; version=1");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 2, "content-encoding", "gzip");
        HpackSpecExamplesUnitTestCase.assertTableState(decoder, 3, "date", "Mon, 21 Oct 2013 20:13:22 GMT");
    }

    @Test
    public void testExample_D_2_110() throws HpackException {
        // my, wrong header field without indexing data test
        byte[] data = new byte[]{ 0 };
        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        try {
            decoder.decode(ByteBuffer.wrap(data), false);
        } catch (HpackException e) {
            // This exception is expected
            return;
        }
        Assert.fail("Didn't get expected HPackException!");
    }

    @Test
    public void testExample_D_2_111() throws HpackException {
        // my, wrong header field with incremental indexing data test (last byte is not passed)
        byte[] data = new byte[]{ 96, 1, 120, 1 };// , 0x79};

        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        try {
            decoder.decode(ByteBuffer.wrap(data), false);
        } catch (HpackException e) {
            // This exception is expected
            return;
        }
        Assert.fail("Didn't get expected HPackException!");
    }

    @Test
    public void testExample_D_2_112() throws HpackException {
        // my, wrong header field with incremental indexing data test (last byte is not passed)
        byte[] data = new byte[]{ 96, 2, 120 };// , 0x79};

        HpackDecoder decoder = new HpackDecoder(DEFAULT_TABLE_SIZE);
        HpackSpecExamplesUnitTestCase.HeaderMapEmitter emitter = new HpackSpecExamplesUnitTestCase.HeaderMapEmitter();
        decoder.setHeaderEmitter(emitter);
        try {
            decoder.decode(ByteBuffer.wrap(data), false);
        } catch (HpackException e) {
            // This exception is expected
            return;
        }
        Assert.fail("Didn't get expected HPackException!");
    }

    private static class HeaderMapEmitter implements HpackDecoder.HeaderEmitter {
        HeaderMap map = new HeaderMap();

        @Override
        public void emitHeader(HttpString name, String value, boolean neverIndex) {
            map.add(name, value);
        }
    }
}

