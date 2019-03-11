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
package io.netty.handler.codec;


import ChannelInputShutdownEvent.INSTANCE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.internal.PlatformDependent;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class ReplayingDecoderTest {
    @Test
    public void testLineProtocol() {
        EmbeddedChannel ch = new EmbeddedChannel(new ReplayingDecoderTest.LineDecoder());
        // Ordinary input
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'A' }));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'B' }));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'C' }));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ '\n' }));
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'A', 'B', 'C' });
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
        // Truncated input
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'A' }));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        Assert.assertNull(ch.readInbound());
    }

    private static final class LineDecoder extends ReplayingDecoder<Void> {
        LineDecoder() {
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
            ByteBuf msg = in.readBytes(in.bytesBefore(((byte) ('\n'))));
            out.add(msg);
            in.skipBytes(1);
        }
    }

    @Test
    public void testReplacement() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new ReplayingDecoderTest.BloatedLineDecoder());
        // "AB" should be forwarded to LineDecoder by BloatedLineDecoder.
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'A', 'B' }));
        Assert.assertNull(ch.readInbound());
        // "C\n" should be appended to "AB" so that LineDecoder decodes it correctly.
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'C', '\n' }));
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'A', 'B', 'C' });
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
        ch.finish();
        Assert.assertNull(ch.readInbound());
    }

    private static final class BloatedLineDecoder extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.pipeline().replace(this, "less-bloated", new ReplayingDecoderTest.LineDecoder());
            ctx.pipeline().fireChannelRead(msg);
        }
    }

    @Test
    public void testSingleDecode() throws Exception {
        ReplayingDecoderTest.LineDecoder decoder = new ReplayingDecoderTest.LineDecoder();
        setSingleDecode(true);
        EmbeddedChannel ch = new EmbeddedChannel(decoder);
        // "C\n" should be appended to "AB" so that LineDecoder decodes it correctly.
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'C', '\n', 'B', '\n' }));
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'C' });
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
        Assert.assertNull("Must be null as it must only decode one frame", ch.readInbound());
        ch.read();
        ch.finish();
        buf = Unpooled.wrappedBuffer(new byte[]{ 'B' });
        buf2 = ch.readInbound();
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
        Assert.assertNull(ch.readInbound());
    }

    @Test
    public void testRemoveItself() {
        EmbeddedChannel channel = new EmbeddedChannel(new ReplayingDecoder() {
            private boolean removed;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(removed);
                in.readByte();
                ctx.pipeline().remove(this);
                removed = true;
            }
        });
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'a', 'b', 'c' });
        channel.writeInbound(buf.copy());
        ByteBuf b = channel.readInbound();
        Assert.assertEquals(b, buf.skipBytes(1));
        b.release();
        buf.release();
    }

    @Test
    public void testRemoveItselfWithReplayError() {
        EmbeddedChannel channel = new EmbeddedChannel(new ReplayingDecoder() {
            private boolean removed;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(removed);
                ctx.pipeline().remove(this);
                in.readBytes(1000);
                removed = true;
            }
        });
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'a', 'b', 'c' });
        channel.writeInbound(buf.copy());
        ByteBuf b = channel.readInbound();
        Assert.assertEquals("Expect to have still all bytes in the buffer", b, buf);
        b.release();
        buf.release();
    }

    @Test
    public void testRemoveItselfWriteBuffer() {
        final ByteBuf buf = Unpooled.buffer().writeBytes(new byte[]{ 'a', 'b', 'c' });
        EmbeddedChannel channel = new EmbeddedChannel(new ReplayingDecoder() {
            private boolean removed;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(removed);
                in.readByte();
                ctx.pipeline().remove(this);
                // This should not let it keep call decode
                buf.writeByte('d');
                removed = true;
            }
        });
        channel.writeInbound(buf.copy());
        ByteBuf b = channel.readInbound();
        Assert.assertEquals(b, Unpooled.wrappedBuffer(new byte[]{ 'b', 'c' }));
        b.release();
        buf.release();
    }

    @Test
    public void testFireChannelReadCompleteOnInactive() throws InterruptedException {
        final BlockingQueue<Integer> queue = new LinkedBlockingDeque<Integer>();
        final ByteBuf buf = Unpooled.buffer().writeBytes(new byte[]{ 'a', 'b' });
        EmbeddedChannel channel = new EmbeddedChannel(new ReplayingDecoder<Integer>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                int readable = in.readableBytes();
                Assert.assertTrue((readable > 0));
                in.skipBytes(readable);
                out.add("data");
            }

            @Override
            protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(in.isReadable());
                out.add("data");
            }
        }, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                queue.add(3);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                queue.add(1);
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                if (!(ctx.channel().isActive())) {
                    queue.add(2);
                }
            }
        });
        Assert.assertFalse(channel.writeInbound(buf));
        channel.finish();
        Assert.assertEquals(1, ((int) (queue.take())));
        Assert.assertEquals(1, ((int) (queue.take())));
        Assert.assertEquals(2, ((int) (queue.take())));
        Assert.assertEquals(3, ((int) (queue.take())));
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testChannelInputShutdownEvent() {
        final AtomicReference<Error> error = new AtomicReference<Error>();
        EmbeddedChannel channel = new EmbeddedChannel(new ReplayingDecoder<Integer>(0) {
            private boolean decoded;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                if (!(in instanceof ReplayingDecoderByteBuf)) {
                    error.set(new AssertionError(((("in must be of type " + (ReplayingDecoderByteBuf.class)) + " but was ") + (in.getClass()))));
                    return;
                }
                if (!(decoded)) {
                    decoded = true;
                    in.readByte();
                    state(1);
                } else {
                    // This will throw an ReplayingError
                    in.skipBytes(Integer.MAX_VALUE);
                }
            }
        });
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 0, 1 })));
        channel.pipeline().fireUserEventTriggered(INSTANCE);
        Assert.assertFalse(channel.finishAndReleaseAll());
        Error err = error.get();
        if (err != null) {
            throw err;
        }
    }

    @Test
    public void handlerRemovedWillNotReleaseBufferIfDecodeInProgress() {
        EmbeddedChannel channel = new EmbeddedChannel(new ReplayingDecoder<Integer>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                ctx.pipeline().remove(this);
                Assert.assertTrue(((in.refCnt()) != 0));
            }

            @Override
            protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
                ReplayingDecoderTest.assertCumulationReleased(internalBuffer());
            }
        });
        byte[] bytes = new byte[1024];
        PlatformDependent.threadLocalRandom().nextBytes(bytes);
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(bytes)));
        Assert.assertTrue(channel.finishAndReleaseAll());
    }
}

