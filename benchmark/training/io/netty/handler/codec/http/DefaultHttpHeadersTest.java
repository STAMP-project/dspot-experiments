/**
 * Copyright 2015 The Netty Project
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


import HttpHeaderNames.CACHE_CONTROL;
import HttpHeaderNames.CONNECTION;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.TRANSFER_ENCODING;
import StringUtil.EMPTY_STRING;
import io.netty.util.AsciiString;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.THREE;


public class DefaultHttpHeadersTest {
    private static final CharSequence HEADER_NAME = "testHeader";

    @Test(expected = IllegalArgumentException.class)
    public void nullHeaderNameNotAllowed() {
        new DefaultHttpHeaders().add(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyHeaderNameNotAllowed() {
        new DefaultHttpHeaders().add(EMPTY_STRING, "foo");
    }

    @Test
    public void keysShouldBeCaseInsensitive() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeadersTestUtils.of("Name"), HttpHeadersTestUtils.of("value1"));
        headers.add(HttpHeadersTestUtils.of("name"), HttpHeadersTestUtils.of("value2"));
        headers.add(HttpHeadersTestUtils.of("NAME"), HttpHeadersTestUtils.of("value3"));
        Assert.assertEquals(3, headers.size());
        List<String> values = Arrays.asList("value1", "value2", "value3");
        Assert.assertEquals(values, headers.getAll(HttpHeadersTestUtils.of("NAME")));
        Assert.assertEquals(values, headers.getAll(HttpHeadersTestUtils.of("name")));
        Assert.assertEquals(values, headers.getAll(HttpHeadersTestUtils.of("Name")));
        Assert.assertEquals(values, headers.getAll(HttpHeadersTestUtils.of("nAmE")));
    }

    @Test
    public void keysShouldBeCaseInsensitiveInHeadersEquals() {
        DefaultHttpHeaders headers1 = new DefaultHttpHeaders();
        headers1.add(HttpHeadersTestUtils.of("name1"), Arrays.asList("value1", "value2", "value3"));
        headers1.add(HttpHeadersTestUtils.of("nAmE2"), HttpHeadersTestUtils.of("value4"));
        DefaultHttpHeaders headers2 = new DefaultHttpHeaders();
        headers2.add(HttpHeadersTestUtils.of("naMe1"), Arrays.asList("value1", "value2", "value3"));
        headers2.add(HttpHeadersTestUtils.of("NAME2"), HttpHeadersTestUtils.of("value4"));
        Assert.assertEquals(headers1, headers1);
        Assert.assertEquals(headers2, headers2);
        Assert.assertEquals(headers1, headers2);
        Assert.assertEquals(headers2, headers1);
        Assert.assertEquals(headers1.hashCode(), headers2.hashCode());
    }

    @Test
    public void testStringKeyRetrievedAsAsciiString() {
        final HttpHeaders headers = new DefaultHttpHeaders(false);
        // Test adding String key and retrieving it using a AsciiString key
        final String connection = "keep-alive";
        headers.add(HttpHeadersTestUtils.of("Connection"), connection);
        // Passes
        final String value = headers.getAsString(CONNECTION.toString());
        Assert.assertNotNull(value);
        Assert.assertEquals(connection, value);
        // Passes
        final String value2 = headers.getAsString(CONNECTION);
        Assert.assertNotNull(value2);
        Assert.assertEquals(connection, value2);
    }

    @Test
    public void testAsciiStringKeyRetrievedAsString() {
        final HttpHeaders headers = new DefaultHttpHeaders(false);
        // Test adding AsciiString key and retrieving it using a String key
        final String cacheControl = "no-cache";
        headers.add(CACHE_CONTROL, cacheControl);
        final String value = headers.getAsString(CACHE_CONTROL);
        Assert.assertNotNull(value);
        Assert.assertEquals(cacheControl, value);
        final String value2 = headers.getAsString(CACHE_CONTROL.toString());
        Assert.assertNotNull(value2);
        Assert.assertEquals(cacheControl, value2);
    }

    @Test
    public void testRemoveTransferEncodingIgnoreCase() {
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(TRANSFER_ENCODING, "Chunked");
        Assert.assertFalse(message.headers().isEmpty());
        HttpUtil.setTransferEncodingChunked(message, false);
        Assert.assertTrue(message.headers().isEmpty());
    }

    // Test for https://github.com/netty/netty/issues/1690
    @Test
    public void testGetOperations() {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeadersTestUtils.of("Foo"), HttpHeadersTestUtils.of("1"));
        headers.add(HttpHeadersTestUtils.of("Foo"), HttpHeadersTestUtils.of("2"));
        Assert.assertEquals("1", headers.get(HttpHeadersTestUtils.of("Foo")));
        List<String> values = headers.getAll(HttpHeadersTestUtils.of("Foo"));
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("1", values.get(0));
        Assert.assertEquals("2", values.get(1));
    }

    @Test
    public void testEqualsIgnoreCase() {
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(null, null), CoreMatchers.is(true));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(null, "foo"), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("bar", null), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("FoO", "fOo"), CoreMatchers.is(true));
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullHeaderValueValidate() {
        HttpHeaders headers = new DefaultHttpHeaders(true);
        headers.set(HttpHeadersTestUtils.of("test"), ((CharSequence) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullHeaderValueNotValidate() {
        HttpHeaders headers = new DefaultHttpHeaders(false);
        headers.set(HttpHeadersTestUtils.of("test"), ((CharSequence) (null)));
    }

    @Test
    public void addCharSequences() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.add(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void addIterable() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.add(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void addObjects() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.add(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void setCharSequences() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.set(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void setIterable() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.set(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void setObjectObjects() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.set(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void setObjectIterable() {
        final DefaultHttpHeaders headers = DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders();
        headers.set(DefaultHttpHeadersTest.HEADER_NAME, THREE.asList());
        DefaultHttpHeadersTest.assertDefaultValues(headers, THREE);
    }

    @Test
    public void toStringOnEmptyHeaders() {
        Assert.assertEquals("DefaultHttpHeaders[]", DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders().toString());
    }

    @Test
    public void toStringOnSingleHeader() {
        Assert.assertEquals("DefaultHttpHeaders[foo: bar]", DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders().add("foo", "bar").toString());
    }

    @Test
    public void toStringOnMultipleHeaders() {
        Assert.assertEquals("DefaultHttpHeaders[foo: bar, baz: qix]", DefaultHttpHeadersTest.newDefaultDefaultHttpHeaders().add("foo", "bar").add("baz", "qix").toString());
    }

    @Test
    public void providesHeaderNamesAsArray() throws Exception {
        Set<String> nettyHeaders = new DefaultHttpHeaders().add(CONTENT_LENGTH, 10).names();
        String[] namesArray = nettyHeaders.toArray(new String[0]);
        Assert.assertArrayEquals(namesArray, new String[]{ CONTENT_LENGTH.toString() });
    }
}

