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


import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.memcache.DefaultLastMemcacheContent;
import io.netty.handler.codec.memcache.DefaultMemcacheContent;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies the correct functionality of the {@link BinaryMemcacheObjectAggregator}.
 */
public class BinaryMemcacheObjectAggregatorTest {
    private static final byte[] SET_REQUEST_WITH_CONTENT = new byte[]{ ((byte) (128)), 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 111, 111, 1, 2, 3, 4, 5, 6, 7, 8 };

    public static final int MAX_CONTENT_SIZE = 2 << 10;

    private EmbeddedChannel channel;

    @Test
    public void shouldAggregateChunksOnDecode() {
        int smallBatchSize = 2;
        channel = new EmbeddedChannel(new BinaryMemcacheRequestDecoder(smallBatchSize), new BinaryMemcacheObjectAggregator(BinaryMemcacheObjectAggregatorTest.MAX_CONTENT_SIZE));
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(BinaryMemcacheObjectAggregatorTest.SET_REQUEST_WITH_CONTENT);
        channel.writeInbound(incoming);
        FullBinaryMemcacheRequest request = channel.readInbound();
        MatcherAssert.assertThat(request, CoreMatchers.instanceOf(FullBinaryMemcacheRequest.class));
        MatcherAssert.assertThat(request, IsNull.notNullValue());
        MatcherAssert.assertThat(request.key(), IsNull.notNullValue());
        MatcherAssert.assertThat(request.extras(), IsNull.nullValue());
        MatcherAssert.assertThat(request.content().readableBytes(), CoreMatchers.is(8));
        MatcherAssert.assertThat(request.content().readByte(), CoreMatchers.is(((byte) (1))));
        MatcherAssert.assertThat(request.content().readByte(), CoreMatchers.is(((byte) (2))));
        request.release();
        MatcherAssert.assertThat(channel.readInbound(), IsNull.nullValue());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void shouldRetainByteBufWhenAggregating() {
        channel = new EmbeddedChannel(new BinaryMemcacheRequestEncoder(), new BinaryMemcacheRequestDecoder(), new BinaryMemcacheObjectAggregator(BinaryMemcacheObjectAggregatorTest.MAX_CONTENT_SIZE));
        ByteBuf key = Unpooled.copiedBuffer("Netty", UTF_8);
        ByteBuf extras = Unpooled.copiedBuffer("extras", UTF_8);
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest(key, extras);
        DefaultMemcacheContent content1 = new DefaultMemcacheContent(Unpooled.copiedBuffer("Netty", UTF_8));
        DefaultLastMemcacheContent content2 = new DefaultLastMemcacheContent(Unpooled.copiedBuffer(" Rocks!", UTF_8));
        int totalBodyLength = (((key.readableBytes()) + (extras.readableBytes())) + (content1.content().readableBytes())) + (content2.content().readableBytes());
        request.setTotalBodyLength(totalBodyLength);
        Assert.assertTrue(channel.writeOutbound(request, content1, content2));
        MatcherAssert.assertThat(channel.outboundMessages().size(), CoreMatchers.is(3));
        Assert.assertTrue(channel.writeInbound(channel.readOutbound(), channel.readOutbound(), channel.readOutbound()));
        FullBinaryMemcacheRequest read = channel.readInbound();
        MatcherAssert.assertThat(read, IsNull.notNullValue());
        MatcherAssert.assertThat(read.key().toString(UTF_8), CoreMatchers.is("Netty"));
        MatcherAssert.assertThat(read.extras().toString(UTF_8), CoreMatchers.is("extras"));
        MatcherAssert.assertThat(read.content().toString(UTF_8), CoreMatchers.is("Netty Rocks!"));
        read.release();
        Assert.assertFalse(channel.finish());
    }
}

