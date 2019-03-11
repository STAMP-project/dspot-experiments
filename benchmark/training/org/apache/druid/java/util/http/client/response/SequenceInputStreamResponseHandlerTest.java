/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.java.util.http.client.response;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Assert;
import org.junit.Test;


public class SequenceInputStreamResponseHandlerTest {
    private static final int TOTAL_BYTES = 1 << 10;

    private static final ArrayList<byte[]> BYTE_LIST = new ArrayList<>();

    private static final Random RANDOM = new Random(378134789L);

    private static byte[] allBytes = new byte[SequenceInputStreamResponseHandlerTest.TOTAL_BYTES];

    @Test(expected = SequenceInputStreamResponseHandlerTest.TesterException.class)
    public void testExceptionalChunkedStream() throws IOException {
        Iterator<byte[]> it = SequenceInputStreamResponseHandlerTest.BYTE_LIST.iterator();
        SequenceInputStreamResponseHandler responseHandler = new SequenceInputStreamResponseHandler();
        final HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setChunked(true);
        ClientResponse<InputStream> clientResponse = responseHandler.handleResponse(response, null);
        final int failAt = SequenceInputStreamResponseHandlerTest.RANDOM.nextInt(SequenceInputStreamResponseHandlerTest.allBytes.length);
        long chunkNum = 0;
        while (it.hasNext()) {
            final DefaultHttpChunk chunk = new DefaultHttpChunk(new BigEndianHeapChannelBuffer(it.next()) {
                @Override
                public void getBytes(int index, byte[] dst, int dstIndex, int length) {
                    if ((dstIndex + length) >= failAt) {
                        throw new SequenceInputStreamResponseHandlerTest.TesterException();
                    }
                    super.getBytes(index, dst, dstIndex, length);
                }
            });
            clientResponse = responseHandler.handleChunk(clientResponse, chunk, (++chunkNum));
        } 
        clientResponse = responseHandler.done(clientResponse);
        final InputStream stream = clientResponse.getObj();
        final byte[] buff = new byte[SequenceInputStreamResponseHandlerTest.allBytes.length];
        SequenceInputStreamResponseHandlerTest.fillBuff(stream, buff);
    }

    public static class TesterException extends RuntimeException {}

    @Test(expected = SequenceInputStreamResponseHandlerTest.TesterException.class)
    public void testExceptionalSingleStream() throws IOException {
        SequenceInputStreamResponseHandler responseHandler = new SequenceInputStreamResponseHandler();
        final HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setChunked(false);
        response.setContent(new BigEndianHeapChannelBuffer(SequenceInputStreamResponseHandlerTest.allBytes) {
            @Override
            public void getBytes(int index, byte[] dst, int dstIndex, int length) {
                if ((dstIndex + length) >= (SequenceInputStreamResponseHandlerTest.allBytes.length)) {
                    throw new SequenceInputStreamResponseHandlerTest.TesterException();
                }
                super.getBytes(index, dst, dstIndex, length);
            }
        });
        ClientResponse<InputStream> clientResponse = responseHandler.handleResponse(response, null);
        clientResponse = responseHandler.done(clientResponse);
        final InputStream stream = clientResponse.getObj();
        final byte[] buff = new byte[SequenceInputStreamResponseHandlerTest.allBytes.length];
        SequenceInputStreamResponseHandlerTest.fillBuff(stream, buff);
    }

    @Test
    public void simpleMultiStreamTest() throws IOException {
        Iterator<byte[]> it = SequenceInputStreamResponseHandlerTest.BYTE_LIST.iterator();
        SequenceInputStreamResponseHandler responseHandler = new SequenceInputStreamResponseHandler();
        final HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setChunked(true);
        ClientResponse<InputStream> clientResponse = responseHandler.handleResponse(response, null);
        long chunkNum = 0;
        while (it.hasNext()) {
            final DefaultHttpChunk chunk = new DefaultHttpChunk(new BigEndianHeapChannelBuffer(it.next()));
            clientResponse = responseHandler.handleChunk(clientResponse, chunk, (++chunkNum));
        } 
        clientResponse = responseHandler.done(clientResponse);
        final InputStream stream = clientResponse.getObj();
        final InputStream expectedStream = new ByteArrayInputStream(SequenceInputStreamResponseHandlerTest.allBytes);
        int read = 0;
        while (read < (SequenceInputStreamResponseHandlerTest.allBytes.length)) {
            final byte[] expectedBytes = new byte[Math.min(SequenceInputStreamResponseHandlerTest.RANDOM.nextInt(128), ((SequenceInputStreamResponseHandlerTest.allBytes.length) - read))];
            final byte[] actualBytes = new byte[expectedBytes.length];
            SequenceInputStreamResponseHandlerTest.fillBuff(stream, actualBytes);
            SequenceInputStreamResponseHandlerTest.fillBuff(expectedStream, expectedBytes);
            Assert.assertArrayEquals(expectedBytes, actualBytes);
            read += expectedBytes.length;
        } 
        Assert.assertEquals(SequenceInputStreamResponseHandlerTest.allBytes.length, responseHandler.getByteCount());
    }

    @Test
    public void alignedMultiStreamTest() throws IOException {
        Iterator<byte[]> it = SequenceInputStreamResponseHandlerTest.BYTE_LIST.iterator();
        SequenceInputStreamResponseHandler responseHandler = new SequenceInputStreamResponseHandler();
        final HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setChunked(true);
        ClientResponse<InputStream> clientResponse = responseHandler.handleResponse(response, null);
        long chunkNum = 0;
        while (it.hasNext()) {
            final DefaultHttpChunk chunk = new DefaultHttpChunk(new BigEndianHeapChannelBuffer(it.next()));
            clientResponse = responseHandler.handleChunk(clientResponse, chunk, (++chunkNum));
        } 
        clientResponse = responseHandler.done(clientResponse);
        final InputStream stream = clientResponse.getObj();
        final InputStream expectedStream = new ByteArrayInputStream(SequenceInputStreamResponseHandlerTest.allBytes);
        for (byte[] bytes : SequenceInputStreamResponseHandlerTest.BYTE_LIST) {
            final byte[] expectedBytes = new byte[bytes.length];
            final byte[] actualBytes = new byte[expectedBytes.length];
            SequenceInputStreamResponseHandlerTest.fillBuff(stream, actualBytes);
            SequenceInputStreamResponseHandlerTest.fillBuff(expectedStream, expectedBytes);
            Assert.assertArrayEquals(expectedBytes, actualBytes);
            Assert.assertArrayEquals(expectedBytes, bytes);
        }
        Assert.assertEquals(SequenceInputStreamResponseHandlerTest.allBytes.length, responseHandler.getByteCount());
    }

    @Test
    public void simpleSingleStreamTest() throws IOException {
        SequenceInputStreamResponseHandler responseHandler = new SequenceInputStreamResponseHandler();
        final HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setChunked(false);
        response.setContent(new BigEndianHeapChannelBuffer(SequenceInputStreamResponseHandlerTest.allBytes));
        ClientResponse<InputStream> clientResponse = responseHandler.handleResponse(response, null);
        clientResponse = responseHandler.done(clientResponse);
        final InputStream stream = clientResponse.getObj();
        final InputStream expectedStream = new ByteArrayInputStream(SequenceInputStreamResponseHandlerTest.allBytes);
        int read = 0;
        while (read < (SequenceInputStreamResponseHandlerTest.allBytes.length)) {
            final byte[] expectedBytes = new byte[Math.min(SequenceInputStreamResponseHandlerTest.RANDOM.nextInt(128), ((SequenceInputStreamResponseHandlerTest.allBytes.length) - read))];
            final byte[] actualBytes = new byte[expectedBytes.length];
            SequenceInputStreamResponseHandlerTest.fillBuff(stream, actualBytes);
            SequenceInputStreamResponseHandlerTest.fillBuff(expectedStream, expectedBytes);
            Assert.assertArrayEquals(expectedBytes, actualBytes);
            read += expectedBytes.length;
        } 
        Assert.assertEquals(SequenceInputStreamResponseHandlerTest.allBytes.length, responseHandler.getByteCount());
    }
}

