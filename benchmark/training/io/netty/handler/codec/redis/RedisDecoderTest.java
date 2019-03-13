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
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies the correct functionality of the {@link RedisDecoder} and {@link RedisArrayAggregator}.
 */
public class RedisDecoderTest {
    private EmbeddedChannel channel;

    @Test
    public void splitEOLDoesNotInfiniteLoop() throws Exception {
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("$6\r\nfoobar\r")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\n")));
        RedisMessage msg = channel.readInbound();
        Assert.assertTrue((msg instanceof FullBulkStringRedisMessage));
        ReferenceCountUtil.release(msg);
    }

    @Test(expected = DecoderException.class)
    public void shouldNotDecodeInlineCommandByDefault() {
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("P")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("I")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("N")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("G")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        channel.readInbound();
    }

    @Test
    public void shouldDecodeInlineCommand() {
        channel = RedisDecoderTest.newChannel(true);
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("P")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("I")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("N")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("G")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        InlineCommandRedisMessage msg = channel.readInbound();
        Assert.assertThat(msg.content(), CoreMatchers.is("PING"));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeSimpleString() {
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("+")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("O")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("K")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        SimpleStringRedisMessage msg = channel.readInbound();
        Assert.assertThat(msg.content(), CoreMatchers.is("OK"));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeTwoSimpleStrings() {
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("+")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("O")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("K")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n+SEC")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("OND\r\n")));
        SimpleStringRedisMessage msg1 = channel.readInbound();
        Assert.assertThat(msg1.content(), CoreMatchers.is("OK"));
        ReferenceCountUtil.release(msg1);
        SimpleStringRedisMessage msg2 = channel.readInbound();
        Assert.assertThat(msg2.content(), CoreMatchers.is("SECOND"));
        ReferenceCountUtil.release(msg2);
    }

    @Test
    public void shouldDecodeError() {
        String content = "ERROR sample message";
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("-")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(content)));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\n")));
        ErrorRedisMessage msg = channel.readInbound();
        Assert.assertThat(msg.content(), CoreMatchers.is(content));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeInteger() {
        long value = 1234L;
        byte[] content = RedisCodecTestUtil.bytesOf(value);
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(":")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(content)));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        IntegerRedisMessage msg = channel.readInbound();
        Assert.assertThat(msg.value(), CoreMatchers.is(value));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeBulkString() {
        String buf1 = "bulk\nst";
        String buf2 = "ring\ntest\n1234";
        byte[] content = RedisCodecTestUtil.bytesOf((buf1 + buf2));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("$")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(Integer.toString(content.length))));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(buf1)));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(buf2)));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        FullBulkStringRedisMessage msg = channel.readInbound();
        Assert.assertThat(RedisCodecTestUtil.bytesOf(msg.content()), CoreMatchers.is(content));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeEmptyBulkString() {
        byte[] content = RedisCodecTestUtil.bytesOf("");
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("$")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(Integer.toString(content.length))));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(content)));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        FullBulkStringRedisMessage msg = channel.readInbound();
        Assert.assertThat(RedisCodecTestUtil.bytesOf(msg.content()), CoreMatchers.is(content));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeNullBulkString() {
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("$")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(Integer.toString((-1)))));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("$")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf(Integer.toString((-1)))));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("\r\n")));
        FullBulkStringRedisMessage msg1 = channel.readInbound();
        Assert.assertThat(msg1.isNull(), CoreMatchers.is(true));
        ReferenceCountUtil.release(msg1);
        FullBulkStringRedisMessage msg2 = channel.readInbound();
        Assert.assertThat(msg2.isNull(), CoreMatchers.is(true));
        ReferenceCountUtil.release(msg2);
        FullBulkStringRedisMessage msg3 = channel.readInbound();
        Assert.assertThat(msg3, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldDecodeSimpleArray() throws Exception {
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("*3\r\n")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf(":1234\r\n")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("+sim")));
        Assert.assertFalse(channel.writeInbound(RedisCodecTestUtil.byteBufOf("ple\r\n-err")));
        Assert.assertTrue(channel.writeInbound(RedisCodecTestUtil.byteBufOf("or\r\n")));
        ArrayRedisMessage msg = channel.readInbound();
        List<RedisMessage> children = msg.children();
        Assert.assertThat(msg.children().size(), CoreMatchers.is(CoreMatchers.equalTo(3)));
        Assert.assertThat(children.get(0), CoreMatchers.instanceOf(IntegerRedisMessage.class));
        Assert.assertThat(value(), CoreMatchers.is(1234L));
        Assert.assertThat(children.get(1), CoreMatchers.instanceOf(SimpleStringRedisMessage.class));
        Assert.assertThat(content(), CoreMatchers.is("simple"));
        Assert.assertThat(children.get(2), CoreMatchers.instanceOf(ErrorRedisMessage.class));
        Assert.assertThat(content(), CoreMatchers.is("error"));
        ReferenceCountUtil.release(msg);
    }

    @Test
    public void shouldDecodeNestedArray() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*2\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*3\r\n:1\r\n:2\r\n:3\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*2\r\n+Foo\r\n-Bar\r\n"));
        Assert.assertTrue(channel.writeInbound(buf));
        ArrayRedisMessage msg = channel.readInbound();
        List<RedisMessage> children = msg.children();
        Assert.assertThat(msg.children().size(), CoreMatchers.is(2));
        ArrayRedisMessage intArray = ((ArrayRedisMessage) (children.get(0)));
        ArrayRedisMessage strArray = ((ArrayRedisMessage) (children.get(1)));
        Assert.assertThat(intArray.children().size(), CoreMatchers.is(3));
        Assert.assertThat(value(), CoreMatchers.is(1L));
        Assert.assertThat(value(), CoreMatchers.is(2L));
        Assert.assertThat(value(), CoreMatchers.is(3L));
        Assert.assertThat(strArray.children().size(), CoreMatchers.is(2));
        Assert.assertThat(content(), CoreMatchers.is("Foo"));
        Assert.assertThat(content(), CoreMatchers.is("Bar"));
        ReferenceCountUtil.release(msg);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void shouldErrorOnDoubleReleaseArrayReferenceCounted() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*2\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*3\r\n:1\r\n:2\r\n:3\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*2\r\n+Foo\r\n-Bar\r\n"));
        Assert.assertTrue(channel.writeInbound(buf));
        ArrayRedisMessage msg = channel.readInbound();
        ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(msg);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void shouldErrorOnReleaseArrayChildReferenceCounted() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*2\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*3\r\n:1\r\n:2\r\n:3\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("$3\r\nFoo\r\n"));
        Assert.assertTrue(channel.writeInbound(buf));
        ArrayRedisMessage msg = channel.readInbound();
        List<RedisMessage> children = msg.children();
        ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(children.get(1));
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void shouldErrorOnReleasecontentOfArrayChildReferenceCounted() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("*2\r\n"));
        buf.writeBytes(RedisCodecTestUtil.byteBufOf("$3\r\nFoo\r\n$3\r\nBar\r\n"));
        Assert.assertTrue(channel.writeInbound(buf));
        ArrayRedisMessage msg = channel.readInbound();
        List<RedisMessage> children = msg.children();
        ByteBuf childBuf = ((FullBulkStringRedisMessage) (children.get(0))).content();
        ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(childBuf);
    }
}

