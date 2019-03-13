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
package io.netty.handler.codec.memcache.binary;


import BinaryMemcacheResponseStatus.KEY_ENOENT;
import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies the correct functionality of the {@link AbstractBinaryMemcacheDecoder}.
 * <p/>
 * While technically there are both a {@link BinaryMemcacheRequestDecoder} and a {@link BinaryMemcacheResponseDecoder}
 * they implement the same basics and just differ in the type of headers returned.
 */
public class BinaryMemcacheDecoderTest {
    /**
     * Represents a GET request header with a key size of three.
     */
    private static final byte[] GET_REQUEST = new byte[]{ ((byte) (128)), 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 111, 111 };

    private static final byte[] SET_REQUEST_WITH_CONTENT = new byte[]{ ((byte) (128)), 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 111, 111, 1, 2, 3, 4, 5, 6, 7, 8 };

    private static final byte[] GET_RESPONSE_CHUNK_1 = new byte[]{ ((byte) (129)), 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 78, 111, 116, 32, 102, 111, 117, 110, 100, ((byte) (129)), 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 78, 111, 116, 32, 102, 111, 117 };

    private static final byte[] GET_RESPONSE_CHUNK_2 = new byte[]{ 110, 100, ((byte) (129)), 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 78, 111, 116, 32, 102, 111, 117, 110, 100 };

    private EmbeddedChannel channel;

    /**
     * This tests a simple GET request with a key as the value.
     */
    @Test
    public void shouldDecodeRequestWithSimpleValue() {
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(BinaryMemcacheDecoderTest.GET_REQUEST);
        channel.writeInbound(incoming);
        BinaryMemcacheRequest request = channel.readInbound();
        MatcherAssert.assertThat(request, IsNull.notNullValue());
        MatcherAssert.assertThat(request.key(), IsNull.notNullValue());
        MatcherAssert.assertThat(request.extras(), IsNull.nullValue());
        MatcherAssert.assertThat(request.keyLength(), CoreMatchers.is(((short) (3))));
        MatcherAssert.assertThat(request.extrasLength(), CoreMatchers.is(((byte) (0))));
        MatcherAssert.assertThat(request.totalBodyLength(), CoreMatchers.is(3));
        request.release();
        MatcherAssert.assertThat(channel.readInbound(), CoreMatchers.instanceOf(LastMemcacheContent.class));
    }

    /**
     * This test makes sure that large content is emitted in chunks.
     */
    @Test
    public void shouldDecodeRequestWithChunkedContent() {
        int smallBatchSize = 2;
        channel = new EmbeddedChannel(new BinaryMemcacheRequestDecoder(smallBatchSize));
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(BinaryMemcacheDecoderTest.SET_REQUEST_WITH_CONTENT);
        channel.writeInbound(incoming);
        BinaryMemcacheRequest request = channel.readInbound();
        MatcherAssert.assertThat(request, IsNull.notNullValue());
        MatcherAssert.assertThat(request.key(), IsNull.notNullValue());
        MatcherAssert.assertThat(request.extras(), IsNull.nullValue());
        MatcherAssert.assertThat(request.keyLength(), CoreMatchers.is(((short) (3))));
        MatcherAssert.assertThat(request.extrasLength(), CoreMatchers.is(((byte) (0))));
        MatcherAssert.assertThat(request.totalBodyLength(), CoreMatchers.is(11));
        request.release();
        int expectedContentChunks = 4;
        for (int i = 1; i <= expectedContentChunks; i++) {
            MemcacheContent content = channel.readInbound();
            if (i < expectedContentChunks) {
                MatcherAssert.assertThat(content, CoreMatchers.instanceOf(MemcacheContent.class));
            } else {
                MatcherAssert.assertThat(content, CoreMatchers.instanceOf(LastMemcacheContent.class));
            }
            MatcherAssert.assertThat(content.content().readableBytes(), CoreMatchers.is(2));
            content.release();
        }
        MatcherAssert.assertThat(channel.readInbound(), IsNull.nullValue());
    }

    /**
     * This test makes sure that even when the decoder is confronted with various chunk
     * sizes in the middle of decoding, it can recover and decode all the time eventually.
     */
    @Test
    public void shouldHandleNonUniformNetworkBatches() {
        ByteBuf incoming = Unpooled.copiedBuffer(BinaryMemcacheDecoderTest.SET_REQUEST_WITH_CONTENT);
        while (incoming.isReadable()) {
            channel.writeInbound(incoming.readBytes(5));
        } 
        incoming.release();
        BinaryMemcacheRequest request = channel.readInbound();
        MatcherAssert.assertThat(request, IsNull.notNullValue());
        MatcherAssert.assertThat(request.key(), IsNull.notNullValue());
        MatcherAssert.assertThat(request.extras(), IsNull.nullValue());
        request.release();
        MemcacheContent content1 = channel.readInbound();
        MemcacheContent content2 = channel.readInbound();
        MatcherAssert.assertThat(content1, CoreMatchers.instanceOf(MemcacheContent.class));
        MatcherAssert.assertThat(content2, CoreMatchers.instanceOf(LastMemcacheContent.class));
        MatcherAssert.assertThat(content1.content().readableBytes(), CoreMatchers.is(3));
        MatcherAssert.assertThat(content2.content().readableBytes(), CoreMatchers.is(5));
        content1.release();
        content2.release();
    }

    /**
     * This test makes sure that even when more requests arrive in the same batch, they
     * get emitted as separate messages.
     */
    @Test
    public void shouldHandleTwoMessagesInOneBatch() {
        channel.writeInbound(Unpooled.buffer().writeBytes(BinaryMemcacheDecoderTest.GET_REQUEST).writeBytes(BinaryMemcacheDecoderTest.GET_REQUEST));
        BinaryMemcacheRequest request = channel.readInbound();
        MatcherAssert.assertThat(request, CoreMatchers.instanceOf(BinaryMemcacheRequest.class));
        MatcherAssert.assertThat(request, IsNull.notNullValue());
        request.release();
        Object lastContent = channel.readInbound();
        MatcherAssert.assertThat(lastContent, CoreMatchers.instanceOf(LastMemcacheContent.class));
        release();
        request = channel.readInbound();
        MatcherAssert.assertThat(request, CoreMatchers.instanceOf(BinaryMemcacheRequest.class));
        MatcherAssert.assertThat(request, IsNull.notNullValue());
        request.release();
        lastContent = channel.readInbound();
        MatcherAssert.assertThat(lastContent, CoreMatchers.instanceOf(LastMemcacheContent.class));
        release();
    }

    @Test
    public void shouldDecodeSeparatedValues() {
        String msgBody = "Not found";
        channel = new EmbeddedChannel(new BinaryMemcacheResponseDecoder());
        channel.writeInbound(Unpooled.buffer().writeBytes(BinaryMemcacheDecoderTest.GET_RESPONSE_CHUNK_1));
        channel.writeInbound(Unpooled.buffer().writeBytes(BinaryMemcacheDecoderTest.GET_RESPONSE_CHUNK_2));
        // First message
        BinaryMemcacheResponse response = channel.readInbound();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(KEY_ENOENT));
        MatcherAssert.assertThat(response.totalBodyLength(), CoreMatchers.is(msgBody.length()));
        response.release();
        // First message first content chunk
        MemcacheContent content = channel.readInbound();
        MatcherAssert.assertThat(content, CoreMatchers.instanceOf(LastMemcacheContent.class));
        MatcherAssert.assertThat(content.content().toString(UTF_8), CoreMatchers.is(msgBody));
        content.release();
        // Second message
        response = channel.readInbound();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(KEY_ENOENT));
        MatcherAssert.assertThat(response.totalBodyLength(), CoreMatchers.is(msgBody.length()));
        response.release();
        // Second message first content chunk
        content = channel.readInbound();
        MatcherAssert.assertThat(content, CoreMatchers.instanceOf(MemcacheContent.class));
        MatcherAssert.assertThat(content.content().toString(UTF_8), CoreMatchers.is(msgBody.substring(0, 7)));
        content.release();
        // Second message second content chunk
        content = channel.readInbound();
        MatcherAssert.assertThat(content, CoreMatchers.instanceOf(LastMemcacheContent.class));
        MatcherAssert.assertThat(content.content().toString(UTF_8), CoreMatchers.is(msgBody.substring(7, 9)));
        content.release();
        // Third message
        response = channel.readInbound();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(KEY_ENOENT));
        MatcherAssert.assertThat(response.totalBodyLength(), CoreMatchers.is(msgBody.length()));
        response.release();
        // Third message first content chunk
        content = channel.readInbound();
        MatcherAssert.assertThat(content, CoreMatchers.instanceOf(LastMemcacheContent.class));
        MatcherAssert.assertThat(content.content().toString(UTF_8), CoreMatchers.is(msgBody));
        content.release();
    }

    @Test
    public void shouldRetainCurrentMessageWhenSendingItOut() {
        channel = new EmbeddedChannel(new BinaryMemcacheRequestEncoder(), new BinaryMemcacheRequestDecoder());
        ByteBuf key = Unpooled.copiedBuffer("Netty", UTF_8);
        ByteBuf extras = Unpooled.copiedBuffer("extras", UTF_8);
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest(key, extras);
        Assert.assertTrue(channel.writeOutbound(request));
        for (; ;) {
            ByteBuf buffer = channel.readOutbound();
            if (buffer == null) {
                break;
            }
            channel.writeInbound(buffer);
        }
        BinaryMemcacheRequest read = channel.readInbound();
        read.release();
        // tearDown will call "channel.finish()"
    }
}

