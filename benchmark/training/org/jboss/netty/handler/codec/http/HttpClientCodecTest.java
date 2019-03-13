/**
 * Copyright 2012 The Netty Project
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
package org.jboss.netty.handler.codec.http;


import CharsetUtil.ISO_8859_1;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.PrematureChannelClosureException;
import org.jboss.netty.handler.codec.embedder.CodecEmbedderException;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpVersion.HTTP_1_1;


public class HttpClientCodecTest {
    private static final String RESPONSE = "HTTP/1.0 200 OK\r\n" + (((("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n" + "Content-Type: text/html\r\n") + "Content-Length: 28\r\n") + "\r\n") + "<html><body></body></html>\r\n");

    private static final String INCOMPLETE_CHUNKED_RESPONSE = "HTTP/1.1 200 OK\r\n" + ((((((("Content-Type: text/plain\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "5\r\n") + "first\r\n") + "6\r\n") + "second\r\n") + "0\r\n");

    private static final String CHUNKED_RESPONSE = (HttpClientCodecTest.INCOMPLETE_CHUNKED_RESPONSE) + "\r\n";

    @Test
    public void testFailsNotOnRequestResponse() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        DecoderEmbedder<ChannelBuffer> decoder = new DecoderEmbedder<ChannelBuffer>(codec);
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(codec);
        encoder.offer(new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost/"));
        decoder.offer(ChannelBuffers.copiedBuffer(HttpClientCodecTest.RESPONSE, ISO_8859_1));
        encoder.finish();
        decoder.finish();
    }

    @Test
    public void testFailsNotOnRequestResponseChunked() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        DecoderEmbedder<ChannelBuffer> decoder = new DecoderEmbedder<ChannelBuffer>(codec);
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(codec);
        encoder.offer(new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost/"));
        decoder.offer(ChannelBuffers.copiedBuffer(HttpClientCodecTest.CHUNKED_RESPONSE, ISO_8859_1));
        encoder.finish();
        decoder.finish();
    }

    @Test
    public void testFailsOnMissingResponse() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(codec);
        encoder.offer(new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost/"));
        try {
            encoder.finish();
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertTrue(((e.getCause()) instanceof PrematureChannelClosureException));
        }
    }

    @Test
    public void testFailsOnIncompleteChunkedResponse() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        DecoderEmbedder<ChannelBuffer> decoder = new DecoderEmbedder<ChannelBuffer>(codec);
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(codec);
        encoder.offer(new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost/"));
        decoder.offer(ChannelBuffers.copiedBuffer(HttpClientCodecTest.INCOMPLETE_CHUNKED_RESPONSE, ISO_8859_1));
        try {
            encoder.finish();
            decoder.finish();
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertTrue(((e.getCause()) instanceof PrematureChannelClosureException));
        }
    }
}

