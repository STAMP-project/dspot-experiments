/**
 * Copyright 2013 The Netty Project
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


import HttpHeaderNames.TRANSFER_ENCODING;
import io.netty.util.AsciiString;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


public class HttpHeadersTest {
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

    @Test(expected = IllegalArgumentException.class)
    public void testAddSelf() {
        HttpHeaders headers = new DefaultHttpHeaders(false);
        headers.add(headers);
    }

    @Test
    public void testSetSelfIsNoOp() {
        HttpHeaders headers = new DefaultHttpHeaders(false);
        headers.add("name", "value");
        headers.set(headers);
        Assert.assertEquals(1, headers.size());
    }
}

