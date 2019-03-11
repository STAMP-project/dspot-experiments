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
package io.netty.handler.stream;


import CharsetUtil.ISO_8859_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ChunkedWriteHandlerTest {
    private static final byte[] BYTES = new byte[1024 * 64];

    private static final File TMP;

    static {
        for (int i = 0; i < (ChunkedWriteHandlerTest.BYTES.length); i++) {
            ChunkedWriteHandlerTest.BYTES[i] = ((byte) (i));
        }
        FileOutputStream out = null;
        try {
            TMP = File.createTempFile("netty-chunk-", ".tmp");
            ChunkedWriteHandlerTest.TMP.deleteOnExit();
            out = new FileOutputStream(ChunkedWriteHandlerTest.TMP);
            out.write(ChunkedWriteHandlerTest.BYTES);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    // See #310
    @Test
    public void testChunkedStream() {
        ChunkedWriteHandlerTest.check(new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)));
        ChunkedWriteHandlerTest.check(new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)), new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)), new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)));
    }

    @Test
    public void testChunkedNioStream() {
        ChunkedWriteHandlerTest.check(new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))));
        ChunkedWriteHandlerTest.check(new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))), new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))), new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))));
    }

    @Test
    public void testChunkedFile() throws IOException {
        ChunkedWriteHandlerTest.check(new ChunkedFile(ChunkedWriteHandlerTest.TMP));
        ChunkedWriteHandlerTest.check(new ChunkedFile(ChunkedWriteHandlerTest.TMP), new ChunkedFile(ChunkedWriteHandlerTest.TMP), new ChunkedFile(ChunkedWriteHandlerTest.TMP));
    }

    @Test
    public void testChunkedNioFile() throws IOException {
        ChunkedWriteHandlerTest.check(new ChunkedNioFile(ChunkedWriteHandlerTest.TMP));
        ChunkedWriteHandlerTest.check(new ChunkedNioFile(ChunkedWriteHandlerTest.TMP), new ChunkedNioFile(ChunkedWriteHandlerTest.TMP), new ChunkedNioFile(ChunkedWriteHandlerTest.TMP));
    }

    @Test
    public void testUnchunkedData() throws IOException {
        ChunkedWriteHandlerTest.check(Unpooled.wrappedBuffer(ChunkedWriteHandlerTest.BYTES));
        ChunkedWriteHandlerTest.check(Unpooled.wrappedBuffer(ChunkedWriteHandlerTest.BYTES), Unpooled.wrappedBuffer(ChunkedWriteHandlerTest.BYTES), Unpooled.wrappedBuffer(ChunkedWriteHandlerTest.BYTES));
    }

    // Test case which shows that there is not a bug like stated here:
    // http://stackoverflow.com/a/10426305
    @Test
    public void testListenerNotifiedWhenIsEnd() {
        ByteBuf buffer = Unpooled.copiedBuffer("Test", ISO_8859_1);
        ChunkedInput<ByteBuf> input = new ChunkedInput<ByteBuf>() {
            private boolean done;

            private final ByteBuf buffer = Unpooled.copiedBuffer("Test", ISO_8859_1);

            @Override
            public boolean isEndOfInput() throws Exception {
                return done;
            }

            @Override
            public void close() throws Exception {
                buffer.release();
            }

            @Deprecated
            @Override
            public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
                return readChunk(ctx.alloc());
            }

            @Override
            public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
                if (done) {
                    return null;
                }
                done = true;
                return buffer.retainedDuplicate();
            }

            @Override
            public long length() {
                return -1;
            }

            @Override
            public long progress() {
                return 1;
            }
        };
        final AtomicBoolean listenerNotified = new AtomicBoolean(false);
        final ChannelFutureListener listener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                listenerNotified.set(true);
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        ch.writeAndFlush(input).addListener(listener).syncUninterruptibly();
        Assert.assertTrue(ch.finish());
        // the listener should have been notified
        Assert.assertTrue(listenerNotified.get());
        ByteBuf buffer2 = ch.readOutbound();
        Assert.assertEquals(buffer, buffer2);
        Assert.assertNull(ch.readOutbound());
        buffer.release();
        buffer2.release();
    }

    @Test
    public void testChunkedMessageInput() {
        ChunkedInput<Object> input = new ChunkedInput<Object>() {
            private boolean done;

            @Override
            public boolean isEndOfInput() throws Exception {
                return done;
            }

            @Override
            public void close() throws Exception {
                // NOOP
            }

            @Deprecated
            @Override
            public Object readChunk(ChannelHandlerContext ctx) throws Exception {
                return readChunk(ctx.alloc());
            }

            @Override
            public Object readChunk(ByteBufAllocator ctx) throws Exception {
                if (done) {
                    return false;
                }
                done = true;
                return 0;
            }

            @Override
            public long length() {
                return -1;
            }

            @Override
            public long progress() {
                return 1;
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        ch.writeAndFlush(input).syncUninterruptibly();
        Assert.assertTrue(ch.finish());
        Assert.assertEquals(0, ch.readOutbound());
        Assert.assertNull(ch.readOutbound());
    }

    @Test
    public void testWriteFailureChunkedStream() throws IOException {
        ChunkedWriteHandlerTest.checkFirstFailed(new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)));
    }

    @Test
    public void testWriteFailureChunkedNioStream() throws IOException {
        ChunkedWriteHandlerTest.checkFirstFailed(new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))));
    }

    @Test
    public void testWriteFailureChunkedFile() throws IOException {
        ChunkedWriteHandlerTest.checkFirstFailed(new ChunkedFile(ChunkedWriteHandlerTest.TMP));
    }

    @Test
    public void testWriteFailureChunkedNioFile() throws IOException {
        ChunkedWriteHandlerTest.checkFirstFailed(new ChunkedNioFile(ChunkedWriteHandlerTest.TMP));
    }

    @Test
    public void testWriteFailureUnchunkedData() throws IOException {
        ChunkedWriteHandlerTest.checkFirstFailed(Unpooled.wrappedBuffer(ChunkedWriteHandlerTest.BYTES));
    }

    @Test
    public void testSkipAfterFailedChunkedStream() throws IOException {
        ChunkedWriteHandlerTest.checkSkipFailed(new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)), new ChunkedStream(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES)));
    }

    @Test
    public void testSkipAfterFailedChunkedNioStream() throws IOException {
        ChunkedWriteHandlerTest.checkSkipFailed(new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))), new ChunkedNioStream(Channels.newChannel(new ByteArrayInputStream(ChunkedWriteHandlerTest.BYTES))));
    }

    @Test
    public void testSkipAfterFailedChunkedFile() throws IOException {
        ChunkedWriteHandlerTest.checkSkipFailed(new ChunkedFile(ChunkedWriteHandlerTest.TMP), new ChunkedFile(ChunkedWriteHandlerTest.TMP));
    }

    @Test
    public void testSkipAfterFailedChunkedNioFile() throws IOException {
        ChunkedWriteHandlerTest.checkSkipFailed(new ChunkedNioFile(ChunkedWriteHandlerTest.TMP), new ChunkedFile(ChunkedWriteHandlerTest.TMP));
    }

    // See https://github.com/netty/netty/issues/8700.
    @Test
    public void testFailureWhenLastChunkFailed() throws IOException {
        ChannelOutboundHandlerAdapter failLast = new ChannelOutboundHandlerAdapter() {
            private int passedWrites;

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                if ((++(this.passedWrites)) < 4) {
                    ctx.write(msg, promise);
                } else {
                    ReferenceCountUtil.release(msg);
                    promise.tryFailure(new RuntimeException());
                }
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(failLast, new ChunkedWriteHandler());
        ChannelFuture r = ch.writeAndFlush(new ChunkedFile(ChunkedWriteHandlerTest.TMP, (1024 * 16)));// 4 chunks

        Assert.assertTrue(ch.finish());
        Assert.assertFalse(r.isSuccess());
        Assert.assertTrue(((r.cause()) instanceof RuntimeException));
        // 3 out of 4 chunks were already written
        int read = 0;
        for (; ;) {
            ByteBuf buffer = ch.readOutbound();
            if (buffer == null) {
                break;
            }
            read += buffer.readableBytes();
            buffer.release();
        }
        Assert.assertEquals(((1024 * 16) * 3), read);
    }

    @Test
    public void testDiscardPendingWritesOnInactive() throws IOException {
        final AtomicBoolean closeWasCalled = new AtomicBoolean(false);
        ChunkedInput<ByteBuf> notifiableInput = new ChunkedInput<ByteBuf>() {
            private boolean done;

            private final ByteBuf buffer = Unpooled.copiedBuffer("Test", ISO_8859_1);

            @Override
            public boolean isEndOfInput() throws Exception {
                return done;
            }

            @Override
            public void close() throws Exception {
                buffer.release();
                closeWasCalled.set(true);
            }

            @Deprecated
            @Override
            public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
                return readChunk(ctx.alloc());
            }

            @Override
            public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
                if (done) {
                    return null;
                }
                done = true;
                return buffer.retainedDuplicate();
            }

            @Override
            public long length() {
                return -1;
            }

            @Override
            public long progress() {
                return 1;
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        // Write 3 messages and close channel before flushing
        ChannelFuture r1 = ch.write(new ChunkedFile(ChunkedWriteHandlerTest.TMP));
        ChannelFuture r2 = ch.write(new ChunkedNioFile(ChunkedWriteHandlerTest.TMP));
        ch.write(notifiableInput);
        // Should be `false` as we do not expect any messages to be written
        Assert.assertFalse(ch.finish());
        Assert.assertFalse(r1.isSuccess());
        Assert.assertFalse(r2.isSuccess());
        Assert.assertTrue(closeWasCalled.get());
    }

    // See https://github.com/netty/netty/issues/8700.
    @Test
    public void testStopConsumingChunksWhenFailed() {
        final ByteBuf buffer = Unpooled.copiedBuffer("Test", ISO_8859_1);
        final AtomicInteger chunks = new AtomicInteger(0);
        ChunkedInput<ByteBuf> nonClosableInput = new ChunkedInput<ByteBuf>() {
            @Override
            public boolean isEndOfInput() throws Exception {
                return (chunks.get()) >= 5;
            }

            @Override
            public void close() throws Exception {
                // no-op
            }

            @Deprecated
            @Override
            public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
                return readChunk(ctx.alloc());
            }

            @Override
            public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
                chunks.incrementAndGet();
                return buffer.retainedDuplicate();
            }

            @Override
            public long length() {
                return -1;
            }

            @Override
            public long progress() {
                return 1;
            }
        };
        ChannelOutboundHandlerAdapter noOpWrites = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                ReferenceCountUtil.release(msg);
                promise.tryFailure(new RuntimeException());
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(noOpWrites, new ChunkedWriteHandler());
        ch.writeAndFlush(nonClosableInput).awaitUninterruptibly();
        // Should be `false` as we do not expect any messages to be written
        Assert.assertFalse(ch.finish());
        buffer.release();
        // We should expect only single chunked being read from the input.
        // It's possible to get a race condition here between resolving a promise and
        // allocating a new chunk, but should be fine when working with embedded channels.
        Assert.assertEquals(1, chunks.get());
    }

    @Test
    public void testCloseSuccessfulChunkedInput() {
        int chunks = 10;
        ChunkedWriteHandlerTest.TestChunkedInput input = new ChunkedWriteHandlerTest.TestChunkedInput(chunks);
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        Assert.assertTrue(ch.writeOutbound(input));
        for (int i = 0; i < chunks; i++) {
            ByteBuf buf = ch.readOutbound();
            Assert.assertEquals(i, buf.readInt());
            buf.release();
        }
        Assert.assertTrue(input.isClosed());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testCloseFailedChunkedInput() {
        Exception error = new Exception("Unable to produce a chunk");
        ChunkedWriteHandlerTest.ThrowingChunkedInput input = new ChunkedWriteHandlerTest.ThrowingChunkedInput(error);
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        try {
            ch.writeOutbound(input);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(error, e);
        }
        Assert.assertTrue(input.isClosed());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testWriteListenerInvokedAfterSuccessfulChunkedInputClosed() throws Exception {
        final ChunkedWriteHandlerTest.TestChunkedInput input = new ChunkedWriteHandlerTest.TestChunkedInput(2);
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        final AtomicBoolean inputClosedWhenListenerInvoked = new AtomicBoolean();
        final CountDownLatch listenerInvoked = new CountDownLatch(1);
        ChannelFuture writeFuture = ch.write(input);
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                inputClosedWhenListenerInvoked.set(input.isClosed());
                listenerInvoked.countDown();
            }
        });
        ch.flush();
        Assert.assertTrue(listenerInvoked.await(10, TimeUnit.SECONDS));
        Assert.assertTrue(writeFuture.isSuccess());
        Assert.assertTrue(inputClosedWhenListenerInvoked.get());
        Assert.assertTrue(ch.finishAndReleaseAll());
    }

    @Test
    public void testWriteListenerInvokedAfterFailedChunkedInputClosed() throws Exception {
        final ChunkedWriteHandlerTest.ThrowingChunkedInput input = new ChunkedWriteHandlerTest.ThrowingChunkedInput(new RuntimeException());
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        final AtomicBoolean inputClosedWhenListenerInvoked = new AtomicBoolean();
        final CountDownLatch listenerInvoked = new CountDownLatch(1);
        ChannelFuture writeFuture = ch.write(input);
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                inputClosedWhenListenerInvoked.set(input.isClosed());
                listenerInvoked.countDown();
            }
        });
        ch.flush();
        Assert.assertTrue(listenerInvoked.await(10, TimeUnit.SECONDS));
        Assert.assertFalse(writeFuture.isSuccess());
        Assert.assertTrue(inputClosedWhenListenerInvoked.get());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testWriteListenerInvokedAfterChannelClosedAndInputFullyConsumed() throws Exception {
        // use empty input which has endOfInput = true
        final ChunkedWriteHandlerTest.TestChunkedInput input = new ChunkedWriteHandlerTest.TestChunkedInput(0);
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        final AtomicBoolean inputClosedWhenListenerInvoked = new AtomicBoolean();
        final CountDownLatch listenerInvoked = new CountDownLatch(1);
        ChannelFuture writeFuture = ch.write(input);
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                inputClosedWhenListenerInvoked.set(input.isClosed());
                listenerInvoked.countDown();
            }
        });
        ch.close();// close channel to make handler discard the input on subsequent flush

        ch.flush();
        Assert.assertTrue(listenerInvoked.await(10, TimeUnit.SECONDS));
        Assert.assertTrue(writeFuture.isSuccess());
        Assert.assertTrue(inputClosedWhenListenerInvoked.get());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testWriteListenerInvokedAfterChannelClosedAndInputNotFullyConsumed() throws Exception {
        // use non-empty input which has endOfInput = false
        final ChunkedWriteHandlerTest.TestChunkedInput input = new ChunkedWriteHandlerTest.TestChunkedInput(42);
        EmbeddedChannel ch = new EmbeddedChannel(new ChunkedWriteHandler());
        final AtomicBoolean inputClosedWhenListenerInvoked = new AtomicBoolean();
        final CountDownLatch listenerInvoked = new CountDownLatch(1);
        ChannelFuture writeFuture = ch.write(input);
        writeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                inputClosedWhenListenerInvoked.set(input.isClosed());
                listenerInvoked.countDown();
            }
        });
        ch.close();// close channel to make handler discard the input on subsequent flush

        ch.flush();
        Assert.assertTrue(listenerInvoked.await(10, TimeUnit.SECONDS));
        Assert.assertFalse(writeFuture.isSuccess());
        Assert.assertTrue(inputClosedWhenListenerInvoked.get());
        Assert.assertFalse(ch.finish());
    }

    private static final class TestChunkedInput implements ChunkedInput<ByteBuf> {
        private final int chunksToProduce;

        private int chunksProduced;

        private volatile boolean closed;

        TestChunkedInput(int chunksToProduce) {
            this.chunksToProduce = chunksToProduce;
        }

        @Override
        public boolean isEndOfInput() {
            return (chunksProduced) >= (chunksToProduce);
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public ByteBuf readChunk(ChannelHandlerContext ctx) {
            return readChunk(ctx.alloc());
        }

        @Override
        public ByteBuf readChunk(ByteBufAllocator allocator) {
            ByteBuf buf = allocator.buffer();
            buf.writeInt(chunksProduced);
            (chunksProduced)++;
            return buf;
        }

        @Override
        public long length() {
            return chunksToProduce;
        }

        @Override
        public long progress() {
            return chunksProduced;
        }

        boolean isClosed() {
            return closed;
        }
    }

    private static final class ThrowingChunkedInput implements ChunkedInput<ByteBuf> {
        private final Exception error;

        private volatile boolean closed;

        ThrowingChunkedInput(Exception error) {
            this.error = error;
        }

        @Override
        public boolean isEndOfInput() {
            return false;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
            return readChunk(ctx.alloc());
        }

        @Override
        public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
            throw error;
        }

        @Override
        public long length() {
            return -1;
        }

        @Override
        public long progress() {
            return -1;
        }

        boolean isClosed() {
            return closed;
        }
    }
}

