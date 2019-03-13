/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import MessageFramer.Sink;
import io.grpc.Codec;
import io.grpc.internal.testing.TestStreamTracer.TestBaseStreamTracer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Tests for {@link MessageFramer}.
 */
@RunWith(JUnit4.class)
public class MessageFramerTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private Sink sink;

    private final TestBaseStreamTracer tracer = new TestBaseStreamTracer();

    private MessageFramer framer;

    @Captor
    private ArgumentCaptor<MessageFramerTest.ByteWritableBuffer> frameCaptor;

    @Captor
    private ArgumentCaptor<Long> wireSizeCaptor;

    @Captor
    private ArgumentCaptor<Long> uncompressedSizeCaptor;

    private MessageFramerTest.BytesWritableBufferAllocator allocator = new MessageFramerTest.BytesWritableBufferAllocator(1000, 1000);

    private StatsTraceContext statsTraceCtx;

    @Test
    public void simplePayload() {
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3, 14 });
        Mockito.verifyNoMoreInteractions(sink);
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 2, 3, 14 }), false, true, 1);
        Assert.assertEquals(1, allocator.allocCount);
        Mockito.verifyNoMoreInteractions(sink);
        checkStats(2, 2);
    }

    @Test
    public void simpleUnknownLengthPayload() {
        MessageFramerTest.writeUnknownLength(framer, new byte[]{ 3, 14 });
        framer.flush();
        // Header is written first, then payload
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 2 }), false, false, 0);
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 3, 14 }), false, true, 1);
        Assert.assertEquals(2, allocator.allocCount);
        Mockito.verifyNoMoreInteractions(sink);
        checkStats(2, 2);
    }

    @Test
    public void smallPayloadsShouldBeCombined() {
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3 });
        Mockito.verifyNoMoreInteractions(sink);
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 14 });
        Mockito.verifyNoMoreInteractions(sink);
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 1, 14 }), false, true, 2);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(1, allocator.allocCount);
        checkStats(1, 1, 1, 1);
    }

    @Test
    public void closeCombinedWithFullSink() {
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3, 14, 1, 5, 9, 2, 6 });
        Mockito.verifyNoMoreInteractions(sink);
        framer.close();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 7, 3, 14, 1, 5, 9, 2, 6 }), true, true, 1);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(1, allocator.allocCount);
        checkStats(7, 7);
    }

    @Test
    public void closeWithoutBufferedFrameGivesNullBuffer() {
        framer.close();
        Mockito.verify(sink).deliverFrame(null, true, true, 0);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(0, allocator.allocCount);
        checkStats();
    }

    @Test
    public void payloadSplitBetweenSinks() {
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(12, 12);
        framer = new MessageFramer(sink, allocator, statsTraceCtx);
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3, 14, 1, 5, 9, 2, 6, 5 });
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 8, 3, 14, 1, 5, 9, 2, 6 }), false, false, 1);
        Mockito.verifyNoMoreInteractions(sink);
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 5 }), false, true, 0);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(2, allocator.allocCount);
        checkStats(8, 8);
    }

    @Test
    public void frameHeaderSplitBetweenSinks() {
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(12, 12);
        framer = new MessageFramer(sink, allocator, statsTraceCtx);
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3, 14, 1 });
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3 });
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 3, 3, 14, 1, 0, 0, 0, 0 }), false, false, 2);
        Mockito.verifyNoMoreInteractions(sink);
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBufferWithMinSize(new byte[]{ 1, 3 }, 12), false, true, 0);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(2, allocator.allocCount);
        checkStats(3, 3, 1, 1);
    }

    @Test
    public void emptyPayloadYieldsFrame() throws Exception {
        MessageFramerTest.writeKnownLength(framer, new byte[0]);
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 0 }), false, true, 1);
        Assert.assertEquals(1, allocator.allocCount);
        checkStats(0, 0);
    }

    @Test
    public void emptyUnknownLengthPayloadYieldsFrame() throws Exception {
        MessageFramerTest.writeUnknownLength(framer, new byte[0]);
        Mockito.verifyZeroInteractions(sink);
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 0 }), false, true, 1);
        // One alloc for the header
        Assert.assertEquals(1, allocator.allocCount);
        checkStats(0, 0);
    }

    @Test
    public void flushIsIdempotent() {
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3, 14 });
        framer.flush();
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 2, 3, 14 }), false, true, 1);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(1, allocator.allocCount);
        checkStats(2, 2);
    }

    @Test
    public void largerFrameSize() throws Exception {
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(0, 10000);
        framer = new MessageFramer(sink, allocator, statsTraceCtx);
        MessageFramerTest.writeKnownLength(framer, new byte[1000]);
        framer.flush();
        Mockito.verify(sink).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
        MessageFramerTest.ByteWritableBuffer buffer = frameCaptor.getValue();
        Assert.assertEquals(1005, buffer.size());
        byte[] data = new byte[1005];
        data[3] = 3;
        data[4] = ((byte) (232));
        Assert.assertEquals(MessageFramerTest.toWriteBuffer(data), buffer);
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(1, allocator.allocCount);
        checkStats(1000, 1000);
    }

    @Test
    public void largerFrameSizeUnknownLength() throws Exception {
        // Force payload to be split into two chunks
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(500, 500);
        framer = new MessageFramer(sink, allocator, statsTraceCtx);
        MessageFramerTest.writeUnknownLength(framer, new byte[1000]);
        framer.flush();
        // Header and first chunk written with flush = false
        Mockito.verify(sink, Mockito.times(2)).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0));
        // On flush third buffer written with flish = true
        // The message count is only bumped when a message is completely written.
        Mockito.verify(sink).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
        // header has fixed length of 5 and specifies correct length
        Assert.assertEquals(5, frameCaptor.getAllValues().get(0).readableBytes());
        byte[] data = new byte[5];
        data[3] = 3;
        data[4] = ((byte) (232));
        Assert.assertEquals(MessageFramerTest.toWriteBuffer(data), frameCaptor.getAllValues().get(0));
        Assert.assertEquals(500, frameCaptor.getAllValues().get(1).readableBytes());
        Assert.assertEquals(500, frameCaptor.getAllValues().get(2).readableBytes());
        Mockito.verifyNoMoreInteractions(sink);
        Assert.assertEquals(3, allocator.allocCount);
        checkStats(1000, 1000);
    }

    @Test
    public void compressed() throws Exception {
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(100, Integer.MAX_VALUE);
        // setMessageCompression should default to true
        framer = new MessageFramer(sink, allocator, statsTraceCtx).setCompressor(new Codec.Gzip());
        MessageFramerTest.writeKnownLength(framer, new byte[1000]);
        framer.flush();
        // The GRPC header is written first as a separate frame.
        // The message count is only bumped when a message is completely written.
        Mockito.verify(sink).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0));
        Mockito.verify(sink).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
        // Check the header
        MessageFramerTest.ByteWritableBuffer buffer = frameCaptor.getAllValues().get(0);
        Assert.assertEquals(1, buffer.data[0]);
        ByteBuffer byteBuf = ByteBuffer.wrap(buffer.data, 1, 4);
        byteBuf.order(ByteOrder.BIG_ENDIAN);
        int length = byteBuf.getInt();
        // compressed data should be smaller than uncompressed data.
        Assert.assertTrue((length < 1000));
        Assert.assertEquals(frameCaptor.getAllValues().get(1).size(), length);
        checkStats(length, 1000);
    }

    @Test
    public void dontCompressIfNoEncoding() throws Exception {
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(100, Integer.MAX_VALUE);
        framer = setMessageCompression(true);
        MessageFramerTest.writeKnownLength(framer, new byte[1000]);
        framer.flush();
        // The GRPC header is written first as a separate frame
        Mockito.verify(sink).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
        // Check the header
        MessageFramerTest.ByteWritableBuffer buffer = frameCaptor.getAllValues().get(0);
        // We purposefully don't check the last byte of length, since that depends on how exactly it
        // compressed.
        Assert.assertEquals(0, buffer.data[0]);
        ByteBuffer byteBuf = ByteBuffer.wrap(buffer.data, 1, 4);
        byteBuf.order(ByteOrder.BIG_ENDIAN);
        int length = byteBuf.getInt();
        Assert.assertEquals(1000, length);
        Assert.assertEquals(((buffer.data.length) - 5), length);
        checkStats(1000, 1000);
    }

    @Test
    public void dontCompressIfNotRequested() throws Exception {
        allocator = new MessageFramerTest.BytesWritableBufferAllocator(100, Integer.MAX_VALUE);
        framer = setMessageCompression(false);
        MessageFramerTest.writeKnownLength(framer, new byte[1000]);
        framer.flush();
        // The GRPC header is written first as a separate frame
        Mockito.verify(sink).deliverFrame(frameCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
        // Check the header
        MessageFramerTest.ByteWritableBuffer buffer = frameCaptor.getAllValues().get(0);
        // We purposefully don't check the last byte of length, since that depends on how exactly it
        // compressed.
        Assert.assertEquals(0, buffer.data[0]);
        ByteBuffer byteBuf = ByteBuffer.wrap(buffer.data, 1, 4);
        byteBuf.order(ByteOrder.BIG_ENDIAN);
        int length = byteBuf.getInt();
        Assert.assertEquals(1000, length);
        Assert.assertEquals(((buffer.data.length) - 5), length);
        checkStats(1000, 1000);
    }

    @Test
    public void closeIsRentrantSafe() throws Exception {
        MessageFramer.Sink reentrant = new MessageFramer.Sink() {
            int count = 0;

            @Override
            public void deliverFrame(WritableBuffer frame, boolean endOfStream, boolean flush, int numMessages) {
                if ((count) == 0) {
                    framer.close();
                    (count)++;
                } else {
                    Assert.fail("received event from reentrant call to close");
                }
            }
        };
        framer = new MessageFramer(reentrant, allocator, statsTraceCtx);
        MessageFramerTest.writeKnownLength(framer, new byte[]{ 3, 14 });
        framer.close();
    }

    @Test
    public void zeroLengthCompressibleMessageIsNotCompressed() {
        framer.setCompressor(new Codec.Gzip());
        framer.setMessageCompression(true);
        MessageFramerTest.writeKnownLength(framer, new byte[]{  });
        framer.flush();
        Mockito.verify(sink).deliverFrame(MessageFramerTest.toWriteBuffer(new byte[]{ 0, 0, 0, 0, 0 }), false, true, 1);
        checkStats(0, 0);
    }

    static class ByteWritableBuffer implements WritableBuffer {
        byte[] data;

        private int writeIdx;

        ByteWritableBuffer(int maxFrameSize) {
            data = new byte[maxFrameSize];
        }

        @Override
        public void write(byte[] bytes, int srcIndex, int length) {
            System.arraycopy(bytes, srcIndex, data, writeIdx, length);
            writeIdx += length;
        }

        @Override
        public void write(byte b) {
            data[((writeIdx)++)] = b;
        }

        @Override
        public int writableBytes() {
            return (data.length) - (writeIdx);
        }

        @Override
        public int readableBytes() {
            return writeIdx;
        }

        @Override
        public void release() {
            data = null;
        }

        int size() {
            return writeIdx;
        }

        @Override
        public boolean equals(Object buffer) {
            if (!(buffer instanceof MessageFramerTest.ByteWritableBuffer)) {
                return false;
            }
            MessageFramerTest.ByteWritableBuffer other = ((MessageFramerTest.ByteWritableBuffer) (buffer));
            return ((readableBytes()) == (other.readableBytes())) && (Arrays.equals(Arrays.copyOf(data, readableBytes()), Arrays.copyOf(other.data, readableBytes())));
        }

        @Override
        public int hashCode() {
            return ((Arrays.hashCode(data)) + (writableBytes())) + (readableBytes());
        }
    }

    static class BytesWritableBufferAllocator implements WritableBufferAllocator {
        public int minSize;

        public int maxSize;

        public int allocCount = 0;

        BytesWritableBufferAllocator(int minSize, int maxSize) {
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        @Override
        public WritableBuffer allocate(int capacityHint) {
            (allocCount)++;
            return new MessageFramerTest.ByteWritableBuffer(Math.min(maxSize, Math.max(capacityHint, minSize)));
        }
    }
}

