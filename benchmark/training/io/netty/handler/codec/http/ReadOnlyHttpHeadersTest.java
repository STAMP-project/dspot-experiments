/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ReadOnlyHttpHeadersTest {
    @Test
    public void getValue() {
        ReadOnlyHttpHeaders headers = new ReadOnlyHttpHeaders(true, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON);
        Assert.assertFalse(headers.isEmpty());
        Assert.assertEquals(1, headers.size());
        Assert.assertTrue(HttpHeaderValues.APPLICATION_JSON.contentEquals(headers.get(HttpHeaderNames.ACCEPT)));
        Assert.assertTrue(headers.contains(HttpHeaderNames.ACCEPT));
        Assert.assertNull(headers.get(HttpHeaderNames.CONTENT_LENGTH));
        Assert.assertFalse(headers.contains(HttpHeaderNames.CONTENT_LENGTH));
    }

    @Test
    public void charSequenceIterator() {
        ReadOnlyHttpHeaders headers = new ReadOnlyHttpHeaders(true, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON, HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO, HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        Assert.assertFalse(headers.isEmpty());
        Assert.assertEquals(3, headers.size());
        Iterator<Map.Entry<CharSequence, CharSequence>> itr = headers.iteratorCharSequence();
        Assert.assertTrue(itr.hasNext());
        Map.Entry<CharSequence, CharSequence> next = itr.next();
        Assert.assertTrue(HttpHeaderNames.ACCEPT.contentEqualsIgnoreCase(next.getKey()));
        Assert.assertTrue(HttpHeaderValues.APPLICATION_JSON.contentEqualsIgnoreCase(next.getValue()));
        Assert.assertTrue(itr.hasNext());
        next = itr.next();
        Assert.assertTrue(HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(next.getKey()));
        Assert.assertTrue(HttpHeaderValues.ZERO.contentEqualsIgnoreCase(next.getValue()));
        Assert.assertTrue(itr.hasNext());
        next = itr.next();
        Assert.assertTrue(HttpHeaderNames.CONNECTION.contentEqualsIgnoreCase(next.getKey()));
        Assert.assertTrue(HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(next.getValue()));
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void stringIterator() {
        ReadOnlyHttpHeaders headers = new ReadOnlyHttpHeaders(true, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON, HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO, HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        Assert.assertFalse(headers.isEmpty());
        Assert.assertEquals(3, headers.size());
        ReadOnlyHttpHeadersTest.assert3ParisEquals(headers.iterator());
    }

    @Test
    public void entries() {
        ReadOnlyHttpHeaders headers = new ReadOnlyHttpHeaders(true, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON, HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO, HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        Assert.assertFalse(headers.isEmpty());
        Assert.assertEquals(3, headers.size());
        ReadOnlyHttpHeadersTest.assert3ParisEquals(headers.entries().iterator());
    }

    @Test
    public void names() {
        ReadOnlyHttpHeaders headers = new ReadOnlyHttpHeaders(true, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON, HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO, HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        Assert.assertFalse(headers.isEmpty());
        Assert.assertEquals(3, headers.size());
        Set<String> names = headers.names();
        Assert.assertEquals(3, names.size());
        Assert.assertTrue(names.contains(HttpHeaderNames.ACCEPT.toString()));
        Assert.assertTrue(names.contains(HttpHeaderNames.CONTENT_LENGTH.toString()));
        Assert.assertTrue(names.contains(HttpHeaderNames.CONNECTION.toString()));
    }

    @Test
    public void getAll() {
        ReadOnlyHttpHeaders headers = new ReadOnlyHttpHeaders(false, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON, HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_OCTET_STREAM);
        Assert.assertFalse(headers.isEmpty());
        Assert.assertEquals(3, headers.size());
        List<String> names = headers.getAll(HttpHeaderNames.ACCEPT);
        Assert.assertEquals(2, names.size());
        Assert.assertTrue(HttpHeaderValues.APPLICATION_JSON.contentEqualsIgnoreCase(names.get(0)));
        Assert.assertTrue(HttpHeaderValues.APPLICATION_OCTET_STREAM.contentEqualsIgnoreCase(names.get(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateNamesFail() {
        new ReadOnlyHttpHeaders(true, HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON, AsciiString.cached(" "));
    }
}

