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
package io.netty.handler.codec.redis;


import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static ArrayRedisMessage.EMPTY_INSTANCE;
import static ArrayRedisMessage.NULL_INSTANCE;


/**
 * Verifies the correct functionality of the {@link RedisEncoder}.
 */
public class RedisEncoderTest {
    private EmbeddedChannel channel;

    @Test
    public void shouldEncodeInlineCommand() {
        RedisMessage msg = new InlineCommandRedisMessage("ping");
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(RedisCodecTestUtil.bytesOf("ping\r\n")));
        written.release();
    }

    @Test
    public void shouldEncodeSimpleString() {
        RedisMessage msg = new SimpleStringRedisMessage("simple");
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(RedisCodecTestUtil.bytesOf("+simple\r\n")));
        written.release();
    }

    @Test
    public void shouldEncodeError() {
        RedisMessage msg = new ErrorRedisMessage("error1");
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf("-error1\r\n"))));
        written.release();
    }

    @Test
    public void shouldEncodeInteger() {
        RedisMessage msg = new IntegerRedisMessage(1234L);
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf(":1234\r\n"))));
        written.release();
    }

    @Test
    public void shouldEncodeBulkStringContent() {
        RedisMessage header = new BulkStringHeaderRedisMessage(16);
        RedisMessage body1 = new DefaultBulkStringRedisContent(RedisCodecTestUtil.byteBufOf("bulk\nstr").retain());
        RedisMessage body2 = new DefaultLastBulkStringRedisContent(RedisCodecTestUtil.byteBufOf("ing\ntest").retain());
        Assert.assertThat(channel.writeOutbound(header), CoreMatchers.is(true));
        Assert.assertThat(channel.writeOutbound(body1), CoreMatchers.is(true));
        Assert.assertThat(channel.writeOutbound(body2), CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf("$16\r\nbulk\nstring\ntest\r\n"))));
        written.release();
    }

    @Test
    public void shouldEncodeFullBulkString() {
        ByteBuf bulkString = RedisCodecTestUtil.byteBufOf("bulk\nstring\ntest").retain();
        int length = bulkString.readableBytes();
        RedisMessage msg = new FullBulkStringRedisMessage(bulkString);
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf((("$" + length) + "\r\nbulk\nstring\ntest\r\n")))));
        written.release();
    }

    @Test
    public void shouldEncodeSimpleArray() {
        List<RedisMessage> children = new ArrayList<RedisMessage>();
        children.add(new FullBulkStringRedisMessage(RedisCodecTestUtil.byteBufOf("foo").retain()));
        children.add(new FullBulkStringRedisMessage(RedisCodecTestUtil.byteBufOf("bar").retain()));
        RedisMessage msg = new ArrayRedisMessage(children);
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf("*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n"))));
        written.release();
    }

    @Test
    public void shouldEncodeNullArray() {
        RedisMessage msg = NULL_INSTANCE;
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf("*-1\r\n"))));
        written.release();
    }

    @Test
    public void shouldEncodeEmptyArray() {
        RedisMessage msg = EMPTY_INSTANCE;
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf("*0\r\n"))));
        written.release();
    }

    @Test
    public void shouldEncodeNestedArray() {
        List<RedisMessage> grandChildren = new ArrayList<RedisMessage>();
        grandChildren.add(new FullBulkStringRedisMessage(RedisCodecTestUtil.byteBufOf("bar")));
        grandChildren.add(new IntegerRedisMessage((-1234L)));
        List<RedisMessage> children = new ArrayList<RedisMessage>();
        children.add(new SimpleStringRedisMessage("foo"));
        children.add(new ArrayRedisMessage(grandChildren));
        RedisMessage msg = new ArrayRedisMessage(children);
        boolean result = channel.writeOutbound(msg);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = RedisEncoderTest.readAll(channel);
        Assert.assertThat(RedisCodecTestUtil.bytesOf(written), CoreMatchers.is(CoreMatchers.equalTo(RedisCodecTestUtil.bytesOf("*2\r\n+foo\r\n*2\r\n$3\r\nbar\r\n:-1234\r\n"))));
        written.release();
    }
}

