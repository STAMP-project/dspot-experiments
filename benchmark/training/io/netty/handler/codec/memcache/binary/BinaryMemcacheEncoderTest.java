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


import BinaryMemcacheOpcodes.GET;
import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.memcache.DefaultLastMemcacheContent;
import io.netty.handler.codec.memcache.DefaultMemcacheContent;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies the correct functionality of the {@link AbstractBinaryMemcacheEncoder}.
 */
public class BinaryMemcacheEncoderTest {
    public static final int DEFAULT_HEADER_SIZE = 24;

    private EmbeddedChannel channel;

    @Test
    public void shouldEncodeDefaultHeader() {
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest();
        boolean result = channel.writeOutbound(request);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE));
        Assert.assertThat(written.readByte(), CoreMatchers.is(((byte) (128))));
        Assert.assertThat(written.readByte(), CoreMatchers.is(((byte) (0))));
        written.release();
    }

    @Test
    public void shouldEncodeCustomHeader() {
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest();
        request.setMagic(((byte) (170)));
        request.setOpcode(GET);
        boolean result = channel.writeOutbound(request);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE));
        Assert.assertThat(written.readByte(), CoreMatchers.is(((byte) (170))));
        Assert.assertThat(written.readByte(), CoreMatchers.is(GET));
        written.release();
    }

    @Test
    public void shouldEncodeExtras() {
        String extrasContent = "netty<3memcache";
        ByteBuf extras = Unpooled.copiedBuffer(extrasContent, UTF_8);
        int extrasLength = extras.readableBytes();
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest(Unpooled.EMPTY_BUFFER, extras);
        boolean result = channel.writeOutbound(request);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(((BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE) + extrasLength)));
        written.skipBytes(BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE);
        Assert.assertThat(written.readSlice(extrasLength).toString(UTF_8), IsEqual.equalTo(extrasContent));
        written.release();
    }

    @Test
    public void shouldEncodeKey() {
        ByteBuf key = Unpooled.copiedBuffer("netty", UTF_8);
        int keyLength = key.readableBytes();
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest(key);
        boolean result = channel.writeOutbound(request);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(((BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE) + keyLength)));
        written.skipBytes(BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE);
        Assert.assertThat(written.readSlice(keyLength).toString(UTF_8), IsEqual.equalTo("netty"));
        written.release();
    }

    @Test
    public void shouldEncodeContent() {
        DefaultMemcacheContent content1 = new DefaultMemcacheContent(Unpooled.copiedBuffer("Netty", UTF_8));
        DefaultLastMemcacheContent content2 = new DefaultLastMemcacheContent(Unpooled.copiedBuffer(" Rocks!", UTF_8));
        int totalBodyLength = (content1.content().readableBytes()) + (content2.content().readableBytes());
        BinaryMemcacheRequest request = new DefaultBinaryMemcacheRequest();
        request.setTotalBodyLength(totalBodyLength);
        boolean result = channel.writeOutbound(request);
        Assert.assertThat(result, CoreMatchers.is(true));
        result = channel.writeOutbound(content1);
        Assert.assertThat(result, CoreMatchers.is(true));
        result = channel.writeOutbound(content2);
        Assert.assertThat(result, CoreMatchers.is(true));
        ByteBuf written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(BinaryMemcacheEncoderTest.DEFAULT_HEADER_SIZE));
        written.release();
        written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(content1.content().readableBytes()));
        Assert.assertThat(written.readSlice(content1.content().readableBytes()).toString(UTF_8), CoreMatchers.is("Netty"));
        written.release();
        written = channel.readOutbound();
        Assert.assertThat(written.readableBytes(), CoreMatchers.is(content2.content().readableBytes()));
        Assert.assertThat(written.readSlice(content2.content().readableBytes()).toString(UTF_8), CoreMatchers.is(" Rocks!"));
        written.release();
    }

    @Test(expected = EncoderException.class)
    public void shouldFailWithoutLastContent() {
        channel.writeOutbound(new DefaultMemcacheContent(Unpooled.EMPTY_BUFFER));
        channel.writeOutbound(new DefaultBinaryMemcacheRequest());
    }
}

