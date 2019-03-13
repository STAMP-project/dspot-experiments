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
package org.jboss.netty.handler.codec.http.multipart;


import ChannelBuffers.EMPTY_BUFFER;
import CharsetUtil.UTF_8;
import HttpHeaders.Names.CONTENT_TYPE;
import HttpHeaders.Names.TRANSFER_ENCODING;
import HttpHeaders.Values.CHUNKED;
import java.util.Arrays;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link HttpPostRequestDecoder} test case.
 */
public class HttpPostRequestDecoderTest {
    @Test
    public void testBinaryStreamUpload() throws Exception {
        final String boundary = "dLV9Wyq26L_-JQxk6ferf-RT153LhOO";
        final DefaultHttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost");
        req.setContent(EMPTY_BUFFER);
        req.setHeader(CONTENT_TYPE, ("multipart/form-data; boundary=" + boundary));
        req.setHeader(TRANSFER_ENCODING, CHUNKED);
        req.setChunked(true);
        // Force to use memory-based data.
        final DefaultHttpDataFactory inMemoryFactory = new DefaultHttpDataFactory(false);
        for (String data : Arrays.asList("", "\r", "\r\r", "\r\r\r")) {
            final String body = ((((((((("--" + boundary) + "\r\n") + "Content-Disposition: form-data; name=\"file\"; filename=\"tmp-0.txt\"\r\n") + "Content-Type: image/gif\r\n") + "\r\n") + data) + "\r\n") + "--") + boundary) + "--\r\n";
            // Create decoder instance to test.
            final HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(inMemoryFactory, req);
            decoder.offer(new org.jboss.netty.handler.codec.http.DefaultHttpChunk(ChannelBuffers.copiedBuffer(body, UTF_8)));
            decoder.offer(new org.jboss.netty.handler.codec.http.DefaultHttpChunk(ChannelBuffers.EMPTY_BUFFER));
            // Validate it's enough chunks to decode upload.
            Assert.assertTrue(decoder.hasNext());
            // Decode binary upload.
            MemoryFileUpload upload = ((MemoryFileUpload) (decoder.next()));
            // Validate data has been parsed correctly as it was passed into request.
            Assert.assertArrayEquals((((("Invalid decoded data [data=" + data) + "upload=") + upload) + ']'), data.getBytes(), upload.get());
        }
    }
}

