/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.logbuffer;


import io.aeron.DirectBufferVector;
import io.aeron.ReservedValueSupplier;
import io.aeron.exceptions.AeronException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.agrona.BitUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static LogBufferDescriptor.LOG_META_DATA_LENGTH;
import static LogBufferDescriptor.TERM_MIN_LENGTH;


public class TermAppenderTest {
    private static final int TERM_BUFFER_LENGTH = TERM_MIN_LENGTH;

    private static final int META_DATA_BUFFER_LENGTH = LOG_META_DATA_LENGTH;

    private static final int MAX_FRAME_LENGTH = 1024;

    private static final int MAX_PAYLOAD_LENGTH = (TermAppenderTest.MAX_FRAME_LENGTH) - (HEADER_LENGTH);

    private static final int PARTITION_INDEX = 0;

    private static final int TERM_TAIL_COUNTER_OFFSET = (LogBufferDescriptor.TERM_TAIL_COUNTERS_OFFSET) + ((TermAppenderTest.PARTITION_INDEX) * (SIZE_OF_LONG));

    private static final int TERM_ID = 7;

    private static final long RV = 7777L;

    private static final ReservedValueSupplier RVS = ( termBuffer, termOffset, frameLength) -> RV;

    private static final UnsafeBuffer DEFAULT_HEADER = new UnsafeBuffer(ByteBuffer.allocateDirect(HEADER_LENGTH));

    private final UnsafeBuffer termBuffer = Mockito.spy(new UnsafeBuffer(ByteBuffer.allocateDirect(TermAppenderTest.TERM_BUFFER_LENGTH)));

    private final UnsafeBuffer logMetaDataBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(TermAppenderTest.META_DATA_BUFFER_LENGTH));

    private final HeaderWriter headerWriter = Mockito.spy(new HeaderWriter(createDefaultHeader(0, 0, TermAppenderTest.TERM_ID)));

    private final TermAppender termAppender = new TermAppender(termBuffer, logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX);

    @Test
    public void shouldAppendFrameToEmptyLog() {
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int msgLength = 20;
        final int frameLength = msgLength + headerLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int tail = 0;
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        Assert.assertThat(termAppender.appendUnfragmentedMessage(headerWriter, buffer, 0, msgLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(alignedFrameLength));
        Assert.assertThat(LogBufferDescriptor.rawTailVolatile(logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX), Matchers.is(LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, (tail + alignedFrameLength))));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes(headerLength, buffer, 0, msgLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameLength);
    }

    @Test
    public void shouldAppendFrameTwiceToLog() {
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int msgLength = 20;
        final int frameLength = msgLength + headerLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        int tail = 0;
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        Assert.assertThat(termAppender.appendUnfragmentedMessage(headerWriter, buffer, 0, msgLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(alignedFrameLength));
        Assert.assertThat(termAppender.appendUnfragmentedMessage(headerWriter, buffer, 0, msgLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is((alignedFrameLength * 2)));
        Assert.assertThat(LogBufferDescriptor.rawTailVolatile(logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX), Matchers.is(LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, (tail + (alignedFrameLength * 2)))));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes(headerLength, buffer, 0, msgLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameLength);
        tail = alignedFrameLength;
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((tail + headerLength), buffer, 0, msgLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameLength);
    }

    @Test
    public void shouldPadLogWhenAppendingWithInsufficientRemainingCapacity() {
        final int msgLength = 120;
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final int requiredFrameSize = align((headerLength + msgLength), FRAME_ALIGNMENT);
        final int tailValue = (TermAppenderTest.TERM_BUFFER_LENGTH) - (align(msgLength, FRAME_ALIGNMENT));
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int frameLength = (TermAppenderTest.TERM_BUFFER_LENGTH) - tailValue;
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tailValue));
        Assert.assertThat(termAppender.appendUnfragmentedMessage(headerWriter, buffer, 0, msgLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(TermAppender.FAILED));
        Assert.assertThat(LogBufferDescriptor.rawTailVolatile(logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX), Matchers.is(LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, (tailValue + requiredFrameSize))));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tailValue, frameLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putShort(typeOffset(tailValue), ((short) (PADDING_FRAME_TYPE)), ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tailValue, frameLength);
    }

    @Test
    public void shouldFragmentMessageOverTwoFrames() {
        final int msgLength = (TermAppenderTest.MAX_PAYLOAD_LENGTH) + 1;
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final int frameLength = headerLength + 1;
        final int requiredCapacity = (align((headerLength + 1), FRAME_ALIGNMENT)) + (TermAppenderTest.MAX_FRAME_LENGTH);
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[msgLength]);
        int tail = 0;
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        Assert.assertThat(termAppender.appendFragmentedMessage(headerWriter, buffer, 0, msgLength, TermAppenderTest.MAX_PAYLOAD_LENGTH, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(requiredCapacity));
        Assert.assertThat(LogBufferDescriptor.rawTailVolatile(logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX), Matchers.is(LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, (tail + requiredCapacity))));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, TermAppenderTest.MAX_FRAME_LENGTH, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((tail + headerLength), buffer, 0, TermAppenderTest.MAX_PAYLOAD_LENGTH);
        inOrder.verify(termBuffer, Mockito.times(1)).putByte(flagsOffset(tail), BEGIN_FRAG_FLAG);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, TermAppenderTest.MAX_FRAME_LENGTH);
        tail = TermAppenderTest.MAX_FRAME_LENGTH;
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((tail + headerLength), buffer, TermAppenderTest.MAX_PAYLOAD_LENGTH, 1);
        inOrder.verify(termBuffer, Mockito.times(1)).putByte(flagsOffset(tail), END_FRAG_FLAG);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameLength);
    }

    @Test
    public void shouldClaimRegionForZeroCopyEncoding() {
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final int msgLength = 20;
        final int frameLength = msgLength + headerLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int tail = 0;
        final BufferClaim bufferClaim = new BufferClaim();
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        Assert.assertThat(termAppender.claim(headerWriter, msgLength, bufferClaim, TermAppenderTest.TERM_ID), Matchers.is(alignedFrameLength));
        Assert.assertThat(bufferClaim.offset(), Matchers.is((tail + headerLength)));
        Assert.assertThat(bufferClaim.length(), Matchers.is(msgLength));
        Assert.assertThat(LogBufferDescriptor.rawTailVolatile(logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX), Matchers.is(LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, (tail + alignedFrameLength))));
        // Map flyweight or encode to buffer directly then call commit() when done
        bufferClaim.commit();
        final InOrder inOrder = Mockito.inOrder(headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameLength, TermAppenderTest.TERM_ID);
    }

    @Test
    public void shouldAppendUnfragmentedFromVectorsToEmptyLog() {
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final UnsafeBuffer bufferOne = new UnsafeBuffer(new byte[64]);
        final UnsafeBuffer bufferTwo = new UnsafeBuffer(new byte[256]);
        bufferOne.setMemory(0, bufferOne.capacity(), ((byte) ('1')));
        bufferTwo.setMemory(0, bufferTwo.capacity(), ((byte) ('2')));
        final int msgLength = (bufferOne.capacity()) + 200;
        final int frameLength = msgLength + headerLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int tail = 0;
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        final DirectBufferVector[] vectors = new DirectBufferVector[]{ new DirectBufferVector(bufferOne, 0, bufferOne.capacity()), new DirectBufferVector(bufferTwo, 0, 200) };
        Assert.assertThat(termAppender.appendUnfragmentedMessage(headerWriter, vectors, msgLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(alignedFrameLength));
        Assert.assertThat(LogBufferDescriptor.rawTailVolatile(logMetaDataBuffer, TermAppenderTest.PARTITION_INDEX), Matchers.is(LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, (tail + alignedFrameLength))));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes(headerLength, bufferOne, 0, bufferOne.capacity());
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((headerLength + (bufferOne.capacity())), bufferTwo, 0, 200);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameLength);
    }

    @Test
    public void shouldAppendFragmentedFromVectorsToEmptyLog() {
        final int mtu = 2048;
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final int maxPayloadLength = mtu - headerLength;
        final int bufferOneLength = 64;
        final int bufferTwoLength = 3000;
        final UnsafeBuffer bufferOne = new UnsafeBuffer(new byte[bufferOneLength]);
        final UnsafeBuffer bufferTwo = new UnsafeBuffer(new byte[bufferTwoLength]);
        bufferOne.setMemory(0, bufferOne.capacity(), ((byte) ('1')));
        bufferTwo.setMemory(0, bufferTwo.capacity(), ((byte) ('2')));
        final int msgLength = bufferOneLength + bufferTwoLength;
        int tail = 0;
        final int frameOneLength = mtu;
        final int frameTwoLength = (msgLength - (mtu - headerLength)) + headerLength;
        final int resultingOffset = frameOneLength + (BitUtil.align(frameTwoLength, FRAME_ALIGNMENT));
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        final DirectBufferVector[] vectors = new DirectBufferVector[]{ new DirectBufferVector(bufferOne, 0, bufferOneLength), new DirectBufferVector(bufferTwo, 0, bufferTwoLength) };
        Assert.assertThat(termAppender.appendFragmentedMessage(headerWriter, vectors, msgLength, maxPayloadLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(resultingOffset));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameOneLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes(headerLength, bufferOne, 0, bufferOneLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((headerLength + bufferOneLength), bufferTwo, 0, (maxPayloadLength - bufferOneLength));
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameOneLength);
        tail += frameOneLength;
        final int bufferTwoOffset = maxPayloadLength - bufferOneLength;
        final int fragmentTwoPayloadLength = bufferTwoLength - (maxPayloadLength - bufferOneLength);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameTwoLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((tail + headerLength), bufferTwo, bufferTwoOffset, fragmentTwoPayloadLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameTwoLength);
    }

    @Test
    public void shouldAppendFragmentedFromVectorsWithNonZeroOffsetToEmptyLog() {
        final int mtu = 2048;
        final int headerLength = TermAppenderTest.DEFAULT_HEADER.capacity();
        final int maxPayloadLength = mtu - headerLength;
        final int bufferOneLength = 64;
        final int offset = 15;
        final int bufferTwoTotalLength = 3000;
        final int bufferTwoLength = bufferTwoTotalLength - offset;
        final UnsafeBuffer bufferOne = new UnsafeBuffer(new byte[bufferOneLength]);
        final UnsafeBuffer bufferTwo = new UnsafeBuffer(new byte[bufferTwoTotalLength]);
        bufferOne.setMemory(0, bufferOne.capacity(), ((byte) ('1')));
        bufferTwo.setMemory(0, bufferTwo.capacity(), ((byte) ('2')));
        final int msgLength = bufferOneLength + bufferTwoLength;
        int tail = 0;
        final int frameOneLength = mtu;
        final int frameTwoLength = (msgLength - (mtu - headerLength)) + headerLength;
        final int resultingOffset = frameOneLength + (BitUtil.align(frameTwoLength, FRAME_ALIGNMENT));
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(TermAppenderTest.TERM_ID, tail));
        final DirectBufferVector[] vectors = new DirectBufferVector[]{ new DirectBufferVector(bufferOne, 0, bufferOneLength), new DirectBufferVector(bufferTwo, offset, bufferTwoLength) };
        Assert.assertThat(termAppender.appendFragmentedMessage(headerWriter, vectors, msgLength, maxPayloadLength, TermAppenderTest.RVS, TermAppenderTest.TERM_ID), Matchers.is(resultingOffset));
        final InOrder inOrder = Mockito.inOrder(termBuffer, headerWriter);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameOneLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes(headerLength, bufferOne, 0, bufferOneLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((headerLength + bufferOneLength), bufferTwo, offset, (maxPayloadLength - bufferOneLength));
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameOneLength);
        tail += frameOneLength;
        final int bufferTwoOffset = (maxPayloadLength - bufferOneLength) + offset;
        final int fragmentTwoPayloadLength = bufferTwoLength - (maxPayloadLength - bufferOneLength);
        inOrder.verify(headerWriter, Mockito.times(1)).write(termBuffer, tail, frameTwoLength, TermAppenderTest.TERM_ID);
        inOrder.verify(termBuffer, Mockito.times(1)).putBytes((tail + headerLength), bufferTwo, bufferTwoOffset, fragmentTwoPayloadLength);
        inOrder.verify(termBuffer, Mockito.times(1)).putLong((tail + (RESERVED_VALUE_OFFSET)), TermAppenderTest.RV, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(termBuffer, Mockito.times(1)).putIntOrdered(tail, frameTwoLength);
    }

    @Test(expected = AeronException.class)
    public void shouldDetectInvalidTerm() {
        final int length = 128;
        final int srcOffset = 0;
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[length]);
        logMetaDataBuffer.putLong(TermAppenderTest.TERM_TAIL_COUNTER_OFFSET, LogBufferDescriptor.packTail(((TermAppenderTest.TERM_ID) + 1), 0));
        termAppender.appendUnfragmentedMessage(headerWriter, buffer, srcOffset, length, TermAppenderTest.RVS, TermAppenderTest.TERM_ID);
    }
}

