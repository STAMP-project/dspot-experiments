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
package io.netty.handler.codec.http;


import CharsetUtil.US_ASCII;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderValues.CHUNKED;
import HttpResponseStatus.NOT_MODIFIED;
import HttpResponseStatus.NO_CONTENT;
import LastHttpContent.EMPTY_LAST_CONTENT;
import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.FileRegion;
import io.netty.channel.embedded.EmbeddedChannel;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import org.junit.Assert;
import org.junit.Test;

import static HttpHeaderNames.TRANSFER_ENCODING;
import static HttpHeaderValues.CHUNKED;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


public class HttpResponseEncoderTest {
    private static final long INTEGER_OVERFLOW = ((long) (Integer.MAX_VALUE)) + 1;

    private static final FileRegion FILE_REGION = new HttpResponseEncoderTest.DummyLongFileRegion();

    @Test
    public void testLargeFileRegionChunked() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(TRANSFER_ENCODING, CHUNKED);
        Assert.assertTrue(channel.writeOutbound(response));
        ByteBuf buffer = channel.readOutbound();
        Assert.assertEquals((((("HTTP/1.1 200 OK\r\n" + (TRANSFER_ENCODING)) + ": ") + (CHUNKED)) + "\r\n\r\n"), buffer.toString(US_ASCII));
        buffer.release();
        Assert.assertTrue(channel.writeOutbound(HttpResponseEncoderTest.FILE_REGION));
        buffer = channel.readOutbound();
        Assert.assertEquals("80000000\r\n", buffer.toString(US_ASCII));
        buffer.release();
        FileRegion region = channel.readOutbound();
        Assert.assertSame(HttpResponseEncoderTest.FILE_REGION, region);
        region.release();
        buffer = channel.readOutbound();
        Assert.assertEquals("\r\n", buffer.toString(US_ASCII));
        buffer.release();
        Assert.assertTrue(channel.writeOutbound(EMPTY_LAST_CONTENT));
        buffer = channel.readOutbound();
        Assert.assertEquals("0\r\n\r\n", buffer.toString(US_ASCII));
        buffer.release();
        Assert.assertFalse(channel.finish());
    }

    private static class DummyLongFileRegion implements FileRegion {
        @Override
        public long position() {
            return 0;
        }

        @Override
        public long transfered() {
            return 0;
        }

        @Override
        public long transferred() {
            return 0;
        }

        @Override
        public long count() {
            return HttpResponseEncoderTest.INTEGER_OVERFLOW;
        }

        @Override
        public long transferTo(WritableByteChannel target, long position) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileRegion touch(Object hint) {
            return this;
        }

        @Override
        public FileRegion touch() {
            return this;
        }

        @Override
        public FileRegion retain() {
            return this;
        }

        @Override
        public FileRegion retain(int increment) {
            return this;
        }

        @Override
        public int refCnt() {
            return 1;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int decrement) {
            return false;
        }
    }

    @Test
    public void testEmptyBufferBypass() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        // Test writing an empty buffer works when the encoder is at ST_INIT.
        channel.writeOutbound(EMPTY_BUFFER);
        ByteBuf buffer = channel.readOutbound();
        Assert.assertThat(buffer, is(sameInstance(EMPTY_BUFFER)));
        // Leave the ST_INIT state.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        Assert.assertTrue(channel.writeOutbound(response));
        buffer = channel.readOutbound();
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n", buffer.toString(US_ASCII));
        buffer.release();
        // Test writing an empty buffer works when the encoder is not at ST_INIT.
        channel.writeOutbound(EMPTY_BUFFER);
        buffer = channel.readOutbound();
        Assert.assertThat(buffer, is(sameInstance(EMPTY_BUFFER)));
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testEmptyContentChunked() throws Exception {
        HttpResponseEncoderTest.testEmptyContent(true);
    }

    @Test
    public void testEmptyContentNotChunked() throws Exception {
        HttpResponseEncoderTest.testEmptyContent(false);
    }

    @Test
    public void testStatusNoContent() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponseEncoderTest.assertEmptyResponse(channel, NO_CONTENT, null, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusNoContentContentLength() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponseEncoderTest.assertEmptyResponse(channel, NO_CONTENT, CONTENT_LENGTH, true);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusNoContentTransferEncoding() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponseEncoderTest.assertEmptyResponse(channel, NO_CONTENT, TRANSFER_ENCODING, true);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusNotModified() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponseEncoderTest.assertEmptyResponse(channel, NOT_MODIFIED, null, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusNotModifiedContentLength() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponseEncoderTest.assertEmptyResponse(channel, NOT_MODIFIED, CONTENT_LENGTH, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusNotModifiedTransferEncoding() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        HttpResponseEncoderTest.assertEmptyResponse(channel, NOT_MODIFIED, TRANSFER_ENCODING, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusInformational() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        for (int code = 100; code < 200; code++) {
            HttpResponseStatus status = HttpResponseStatus.valueOf(code);
            HttpResponseEncoderTest.assertEmptyResponse(channel, status, null, false);
        }
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusInformationalContentLength() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        for (int code = 100; code < 200; code++) {
            HttpResponseStatus status = HttpResponseStatus.valueOf(code);
            HttpResponseEncoderTest.assertEmptyResponse(channel, status, CONTENT_LENGTH, (code != 101));
        }
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testStatusInformationalTransferEncoding() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseEncoder());
        for (int code = 100; code < 200; code++) {
            HttpResponseStatus status = HttpResponseStatus.valueOf(code);
            HttpResponseEncoderTest.assertEmptyResponse(channel, status, TRANSFER_ENCODING, (code != 101));
        }
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testEmptyContentsChunked() throws Exception {
        testEmptyContents(true, false);
    }

    @Test
    public void testEmptyContentsChunkedWithTrailers() throws Exception {
        testEmptyContents(true, true);
    }

    @Test
    public void testEmptyContentsNotChunked() throws Exception {
        testEmptyContents(false, false);
    }

    @Test
    public void testEmptyContentNotsChunkedWithTrailers() throws Exception {
        testEmptyContents(false, true);
    }

    @Test
    public void testStatusResetContentTransferContentLength() {
        HttpResponseEncoderTest.testStatusResetContentTransferContentLength0(CONTENT_LENGTH, Unpooled.buffer().writeLong(8));
    }

    @Test
    public void testStatusResetContentTransferEncoding() {
        HttpResponseEncoderTest.testStatusResetContentTransferContentLength0(TRANSFER_ENCODING, Unpooled.buffer().writeLong(8));
    }
}

