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
package io.netty.handler.codec.http2;


import Http2HeadersEncoder.NEVER_SENSITIVE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;


public class HpackEncoderTest {
    private HpackDecoder hpackDecoder;

    private HpackEncoder hpackEncoder;

    private Http2Headers mockHeaders;

    @Test
    public void testSetMaxHeaderTableSizeToMaxValue() throws Http2Exception {
        ByteBuf buf = Unpooled.buffer();
        hpackEncoder.setMaxHeaderTableSize(buf, Http2CodecUtil.MAX_HEADER_TABLE_SIZE);
        hpackDecoder.setMaxHeaderTableSize(Http2CodecUtil.MAX_HEADER_TABLE_SIZE);
        hpackDecoder.decode(0, buf, mockHeaders, true);
        Assert.assertEquals(Http2CodecUtil.MAX_HEADER_TABLE_SIZE, hpackDecoder.getMaxHeaderTableSize());
        buf.release();
    }

    @Test(expected = Http2Exception.class)
    public void testSetMaxHeaderTableSizeOverflow() throws Http2Exception {
        ByteBuf buf = Unpooled.buffer();
        try {
            hpackEncoder.setMaxHeaderTableSize(buf, ((Http2CodecUtil.MAX_HEADER_TABLE_SIZE) + 1));
        } finally {
            buf.release();
        }
    }

    /**
     * The encoder should not impose an arbitrary limit on the header size if
     * the server has not specified any limit.
     *
     * @throws Http2Exception
     * 		
     */
    @Test
    public void testWillEncode16MBHeaderByDefault() throws Http2Exception {
        ByteBuf buf = Unpooled.buffer();
        String bigHeaderName = "x-big-header";
        int bigHeaderSize = (1024 * 1024) * 16;
        String bigHeaderVal = new String(new char[bigHeaderSize]).replace('\u0000', 'X');
        Http2Headers headersIn = new DefaultHttp2Headers().add("x-big-header", bigHeaderVal);
        Http2Headers headersOut = new DefaultHttp2Headers();
        try {
            hpackEncoder.encodeHeaders(0, buf, headersIn, NEVER_SENSITIVE);
            hpackDecoder.setMaxHeaderListSize((bigHeaderSize + 1024));
            hpackDecoder.decode(0, buf, headersOut, false);
        } finally {
            buf.release();
        }
        Assert.assertEquals(headersOut.get(bigHeaderName).toString(), bigHeaderVal);
    }

    @Test(expected = Http2Exception.class)
    public void testSetMaxHeaderListSizeEnforcedAfterSet() throws Http2Exception {
        ByteBuf buf = Unpooled.buffer();
        Http2Headers headers = new DefaultHttp2Headers().add("x-big-header", new String(new char[1024 * 16]).replace('\u0000', 'X'));
        hpackEncoder.setMaxHeaderListSize(1000);
        try {
            hpackEncoder.encodeHeaders(0, buf, headers, NEVER_SENSITIVE);
        } finally {
            buf.release();
        }
    }
}

