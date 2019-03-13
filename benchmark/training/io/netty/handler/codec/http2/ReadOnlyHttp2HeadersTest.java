/**
 * Copyright 2016 The Netty Project
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


import AsciiString.EMPTY_STRING;
import Http2Headers.PseudoHeaderName.PATH;
import Http2Headers.PseudoHeaderName.STATUS;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class ReadOnlyHttp2HeadersTest {
    @Test(expected = IllegalArgumentException.class)
    public void notKeyValuePairThrows() {
        ReadOnlyHttp2Headers.trailers(false, new AsciiString[]{ null });
    }

    @Test(expected = NullPointerException.class)
    public void nullTrailersNotAllowed() {
        ReadOnlyHttp2Headers.trailers(false, ((AsciiString[]) (null)));
    }

    @Test
    public void nullHeaderNameNotChecked() {
        ReadOnlyHttp2Headers.trailers(false, null, null);
    }

    @Test(expected = Http2Exception.class)
    public void nullHeaderNameValidated() {
        ReadOnlyHttp2Headers.trailers(true, null, new AsciiString("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pseudoHeaderNotAllowedAfterNonPseudoHeaders() {
        ReadOnlyHttp2Headers.trailers(true, new AsciiString(":name"), new AsciiString("foo"), new AsciiString("othername"), new AsciiString("goo"), new AsciiString(":pseudo"), new AsciiString("val"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValuesAreNotAllowed() {
        ReadOnlyHttp2Headers.trailers(true, new AsciiString("foo"), null);
    }

    @Test
    public void emptyHeaderNameAllowed() {
        ReadOnlyHttp2Headers.trailers(false, EMPTY_STRING, new AsciiString("foo"));
    }

    @Test
    public void testPseudoHeadersMustComeFirstWhenIteratingServer() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newServerHeaders();
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
    }

    @Test
    public void testPseudoHeadersMustComeFirstWhenIteratingClient() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newClientHeaders();
        DefaultHttp2HeadersTest.verifyPseudoHeadersFirst(headers);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorReadOnlyClient() {
        ReadOnlyHttp2HeadersTest.testIteratorReadOnly(ReadOnlyHttp2HeadersTest.newClientHeaders());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorReadOnlyServer() {
        ReadOnlyHttp2HeadersTest.testIteratorReadOnly(ReadOnlyHttp2HeadersTest.newServerHeaders());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorReadOnlyTrailers() {
        ReadOnlyHttp2HeadersTest.testIteratorReadOnly(ReadOnlyHttp2HeadersTest.newTrailers());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorEntryReadOnlyClient() {
        ReadOnlyHttp2HeadersTest.testIteratorEntryReadOnly(ReadOnlyHttp2HeadersTest.newClientHeaders());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorEntryReadOnlyServer() {
        ReadOnlyHttp2HeadersTest.testIteratorEntryReadOnly(ReadOnlyHttp2HeadersTest.newServerHeaders());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorEntryReadOnlyTrailers() {
        ReadOnlyHttp2HeadersTest.testIteratorEntryReadOnly(ReadOnlyHttp2HeadersTest.newTrailers());
    }

    @Test
    public void testSize() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newTrailers();
        Assert.assertEquals(((ReadOnlyHttp2HeadersTest.otherHeaders().length) / 2), headers.size());
    }

    @Test
    public void testIsNotEmpty() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newTrailers();
        Assert.assertFalse(headers.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        Http2Headers headers = ReadOnlyHttp2Headers.trailers(false);
        Assert.assertTrue(headers.isEmpty());
    }

    @Test
    public void testContainsName() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newClientHeaders();
        Assert.assertTrue(headers.contains("Name1"));
        Assert.assertTrue(headers.contains(PATH.value()));
        Assert.assertFalse(headers.contains(STATUS.value()));
        Assert.assertFalse(headers.contains("a missing header"));
    }

    @Test
    public void testContainsNameAndValue() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newClientHeaders();
        Assert.assertTrue(headers.contains("Name1", "value1"));
        Assert.assertFalse(headers.contains("Name1", "Value1"));
        Assert.assertTrue(headers.contains("name2", "Value2", true));
        Assert.assertFalse(headers.contains("name2", "Value2", false));
        Assert.assertTrue(headers.contains(PATH.value(), "/foo"));
        Assert.assertFalse(headers.contains(STATUS.value(), "200"));
        Assert.assertFalse(headers.contains("a missing header", "a missing value"));
    }

    @Test
    public void testGet() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newClientHeaders();
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("value1", headers.get("Name1")));
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("/foo", headers.get(PATH.value())));
        Assert.assertNull(headers.get(STATUS.value()));
        Assert.assertNull(headers.get("a missing header"));
    }

    @Test
    public void testClientOtherValueIterator() {
        ReadOnlyHttp2HeadersTest.testValueIteratorSingleValue(ReadOnlyHttp2HeadersTest.newClientHeaders(), "name2", "value2");
    }

    @Test
    public void testClientPsuedoValueIterator() {
        ReadOnlyHttp2HeadersTest.testValueIteratorSingleValue(ReadOnlyHttp2HeadersTest.newClientHeaders(), ":path", "/foo");
    }

    @Test
    public void testServerPsuedoValueIterator() {
        ReadOnlyHttp2HeadersTest.testValueIteratorSingleValue(ReadOnlyHttp2HeadersTest.newServerHeaders(), ":status", "200");
    }

    @Test
    public void testEmptyValueIterator() {
        Http2Headers headers = ReadOnlyHttp2HeadersTest.newServerHeaders();
        Iterator<CharSequence> itr = headers.valueIterator("foo");
        Assert.assertFalse(itr.hasNext());
        try {
            itr.next();
            Assert.fail();
        } catch (NoSuchElementException ignored) {
            // ignored
        }
    }

    @Test
    public void testIteratorMultipleValues() {
        Http2Headers headers = ReadOnlyHttp2Headers.serverHeaders(false, new AsciiString("200"), new AsciiString[]{ new AsciiString("name2"), new AsciiString("value1"), new AsciiString("name1"), new AsciiString("value2"), new AsciiString("name2"), new AsciiString("value3") });
        Iterator<CharSequence> itr = headers.valueIterator("name2");
        Assert.assertTrue(itr.hasNext());
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("value1", itr.next()));
        Assert.assertTrue(itr.hasNext());
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("value3", itr.next()));
        Assert.assertFalse(itr.hasNext());
    }
}

