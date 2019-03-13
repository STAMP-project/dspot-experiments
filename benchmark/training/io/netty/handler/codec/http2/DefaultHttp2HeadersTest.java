/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.codec.http2;


import StringUtil.EMPTY_STRING;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DefaultHttp2HeadersTest {
    @Test(expected = Http2Exception.class)
    public void nullHeaderNameNotAllowed() {
        new DefaultHttp2Headers().add(null, "foo");
    }

    @Test(expected = Http2Exception.class)
    public void emptyHeaderNameNotAllowed() {
        new DefaultHttp2Headers().add(EMPTY_STRING, "foo");
    }

    @Test
    public void testPseudoHeadersMustComeFirstWhenIterating() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
        DefaultHttp2HeadersTest.verifyAllPseudoHeadersPresent(headers);
    }

    @Test
    public void testPseudoHeadersWithRemovePreservesPseudoIterationOrder() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        Http2Headers nonPseudoHeaders = new DefaultHttp2Headers();
        for (Map.Entry<CharSequence, CharSequence> entry : headers) {
            if (((entry.getKey().length()) == 0) || (((entry.getKey().charAt(0)) != ':') && (!(nonPseudoHeaders.contains(entry.getKey()))))) {
                nonPseudoHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        Assert.assertFalse(nonPseudoHeaders.isEmpty());
        // Remove all the non-pseudo headers and verify
        for (Map.Entry<CharSequence, CharSequence> nonPseudoHeaderEntry : nonPseudoHeaders) {
            Assert.assertTrue(headers.remove(nonPseudoHeaderEntry.getKey()));
            DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
            DefaultHttp2HeadersTest.verifyAllPseudoHeadersPresent(headers);
        }
        // Add back all non-pseudo headers
        for (Map.Entry<CharSequence, CharSequence> nonPseudoHeaderEntry : nonPseudoHeaders) {
            headers.add(nonPseudoHeaderEntry.getKey(), of("goo"));
            DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
            DefaultHttp2HeadersTest.verifyAllPseudoHeadersPresent(headers);
        }
    }

    @Test
    public void testPseudoHeadersWithClearDoesNotLeak() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        Assert.assertFalse(headers.isEmpty());
        headers.clear();
        Assert.assertTrue(headers.isEmpty());
        // Combine 2 headers together, make sure pseudo headers stay up front.
        headers.add("name1", "value1").scheme("nothing");
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
        Http2Headers other = new DefaultHttp2Headers().add("name2", "value2").authority("foo");
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(other);
        headers.add(other);
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
        // Make sure the headers are what we expect them to be, and no leaking behind the scenes.
        Assert.assertEquals(4, headers.size());
        Assert.assertEquals("value1", headers.get("name1"));
        Assert.assertEquals("value2", headers.get("name2"));
        Assert.assertEquals("nothing", headers.scheme());
        Assert.assertEquals("foo", headers.authority());
    }

    @Test
    public void testSetHeadersOrdersPseudoHeadersCorrectly() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        Http2Headers other = new DefaultHttp2Headers().add("name2", "value2").authority("foo");
        headers.set(other);
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
        Assert.assertEquals(other.size(), headers.size());
        Assert.assertEquals("foo", headers.authority());
        Assert.assertEquals("value2", headers.get("name2"));
    }

    @Test
    public void testSetAllOrdersPseudoHeadersCorrectly() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        Http2Headers other = new DefaultHttp2Headers().add("name2", "value2").authority("foo");
        int headersSizeBefore = headers.size();
        headers.setAll(other);
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
        DefaultHttp2HeadersTest.verifyAllPseudoHeadersPresent(headers);
        Assert.assertEquals((headersSizeBefore + 1), headers.size());
        Assert.assertEquals("foo", headers.authority());
        Assert.assertEquals("value2", headers.get("name2"));
    }

    @Test(expected = Http2Exception.class)
    public void testHeaderNameValidation() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        headers.add(of("Foo"), of("foo"));
    }

    @Test
    public void testClearResetsPseudoHeaderDivision() {
        DefaultHttp2Headers http2Headers = new DefaultHttp2Headers();
        http2Headers.method("POST");
        http2Headers.set("some", "value");
        http2Headers.clear();
        http2Headers.method("GET");
        Assert.assertEquals(1, http2Headers.names().size());
    }

    @Test
    public void testContainsNameAndValue() {
        Http2Headers headers = DefaultHttp2HeadersTest.newHeaders();
        Assert.assertTrue(headers.contains("name1", "value2"));
        Assert.assertFalse(headers.contains("name1", "Value2"));
        Assert.assertTrue(headers.contains("2name", "Value3", true));
        Assert.assertFalse(headers.contains("2name", "Value3", false));
    }
}

