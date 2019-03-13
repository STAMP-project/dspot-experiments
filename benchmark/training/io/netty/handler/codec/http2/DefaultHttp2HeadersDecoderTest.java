/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import Http2Error.PROTOCOL_ERROR;
import Http2Exception.HeaderListSizeException;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DefaultHttp2HeadersDecoder}.
 */
public class DefaultHttp2HeadersDecoderTest {
    private DefaultHttp2HeadersDecoder decoder;

    @Test
    public void decodeShouldSucceed() throws Exception {
        ByteBuf buf = DefaultHttp2HeadersDecoderTest.encode(DefaultHttp2HeadersDecoderTest.b(":method"), DefaultHttp2HeadersDecoderTest.b("GET"), DefaultHttp2HeadersDecoderTest.b("akey"), DefaultHttp2HeadersDecoderTest.b("avalue"), Http2TestUtil.randomBytes(), Http2TestUtil.randomBytes());
        try {
            Http2Headers headers = decoder.decodeHeaders(0, buf);
            Assert.assertEquals(3, headers.size());
            Assert.assertEquals("GET", headers.method().toString());
            Assert.assertEquals("avalue", headers.get(new AsciiString("akey")).toString());
        } finally {
            buf.release();
        }
    }

    @Test(expected = Http2Exception.class)
    public void testExceedHeaderSize() throws Exception {
        final int maxListSize = 100;
        decoder.configuration().maxHeaderListSize(maxListSize, maxListSize);
        ByteBuf buf = DefaultHttp2HeadersDecoderTest.encode(Http2TestUtil.randomBytes(maxListSize), Http2TestUtil.randomBytes(1));
        try {
            decoder.decodeHeaders(0, buf);
            Assert.fail();
        } finally {
            buf.release();
        }
    }

    @Test
    public void decodeLargerThanHeaderListSizeButLessThanGoAway() throws Exception {
        decoder.maxHeaderListSize(Http2CodecUtil.MIN_HEADER_LIST_SIZE, Http2CodecUtil.MAX_HEADER_LIST_SIZE);
        ByteBuf buf = DefaultHttp2HeadersDecoderTest.encode(DefaultHttp2HeadersDecoderTest.b(":method"), DefaultHttp2HeadersDecoderTest.b("GET"));
        final int streamId = 1;
        try {
            decoder.decodeHeaders(streamId, buf);
            Assert.fail();
        } catch (Http2Exception e) {
            Assert.assertEquals(streamId, e.streamId());
        } finally {
            buf.release();
        }
    }

    @Test
    public void decodeLargerThanHeaderListSizeButLessThanGoAwayWithInitialDecoderSettings() throws Exception {
        ByteBuf buf = DefaultHttp2HeadersDecoderTest.encode(DefaultHttp2HeadersDecoderTest.b(":method"), DefaultHttp2HeadersDecoderTest.b("GET"), DefaultHttp2HeadersDecoderTest.b("test_header"), DefaultHttp2HeadersDecoderTest.b(String.format("%09000d", 0).replace('0', 'A')));
        final int streamId = 1;
        try {
            decoder.decodeHeaders(streamId, buf);
            Assert.fail();
        } catch (Http2Exception e) {
            Assert.assertEquals(streamId, e.streamId());
        } finally {
            buf.release();
        }
    }

    @Test
    public void decodeLargerThanHeaderListSizeGoAway() throws Exception {
        decoder.maxHeaderListSize(Http2CodecUtil.MIN_HEADER_LIST_SIZE, Http2CodecUtil.MIN_HEADER_LIST_SIZE);
        ByteBuf buf = DefaultHttp2HeadersDecoderTest.encode(DefaultHttp2HeadersDecoderTest.b(":method"), DefaultHttp2HeadersDecoderTest.b("GET"));
        final int streamId = 1;
        try {
            decoder.decodeHeaders(streamId, buf);
            Assert.fail();
        } catch (Http2Exception e) {
            Assert.assertEquals(PROTOCOL_ERROR, e.error());
        } finally {
            buf.release();
        }
    }
}

