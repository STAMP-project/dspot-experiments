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
package io.netty.handler.codec.memcache.binary;


import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class FullMemcacheMessageResponseTest {
    private EmbeddedChannel channel;

    @Test
    public void testEncodeDecode() throws Exception {
        ByteBuf key = Unpooled.wrappedBuffer("key".getBytes(UTF_8));
        ByteBuf content = Unpooled.wrappedBuffer("content".getBytes(UTF_8));
        ByteBuf extras = Unpooled.wrappedBuffer("extras".getBytes(UTF_8));
        FullBinaryMemcacheResponse resp = new DefaultFullBinaryMemcacheResponse(key, extras, content);
        Assert.assertTrue(channel.writeOutbound(resp));
        // header + content
        Assert.assertEquals(2, channel.outboundMessages().size());
        Assert.assertTrue(channel.writeInbound(channel.readOutbound(), channel.readOutbound()));
        FullBinaryMemcacheResponse read = channel.readInbound();
        Assert.assertEquals("key", read.key().toString(UTF_8));
        Assert.assertEquals("content", read.content().toString(UTF_8));
        Assert.assertEquals("extras", read.extras().toString(UTF_8));
        read.release();
    }
}

