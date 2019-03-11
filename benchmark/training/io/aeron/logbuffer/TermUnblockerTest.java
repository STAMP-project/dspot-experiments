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


import java.nio.ByteOrder;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TermUnblockerTest {
    private static final int TERM_BUFFER_CAPACITY = 64 * 1014;

    private static final int TERM_ID = 7;

    private final UnsafeBuffer mockTermBuffer = Mockito.mock(UnsafeBuffer.class);

    private final UnsafeBuffer mockLogMetaDataBuffer = Mockito.mock(UnsafeBuffer.class);

    @Test
    public void shouldTakeNoActionWhenMessageIsComplete() {
        final int termOffset = 0;
        final int tailOffset = TermUnblockerTest.TERM_BUFFER_CAPACITY;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn(HEADER_LENGTH);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldTakeNoActionWhenNoUnblockedMessage() {
        final int termOffset = 0;
        final int tailOffset = (TermUnblockerTest.TERM_BUFFER_CAPACITY) / 2;
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldPatchNonCommittedMessage() {
        final int termOffset = 0;
        final int messageLength = (HEADER_LENGTH) * 4;
        final int tailOffset = messageLength;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn((-messageLength));
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.UNBLOCKED));
        final InOrder inOrder = Mockito.inOrder(mockTermBuffer);
        inOrder.verify(mockTermBuffer).putShort(FrameDescriptor.typeOffset(termOffset), ((short) (HDR_TYPE_PAD)), ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putInt(FrameDescriptor.termOffsetOffset(termOffset), termOffset, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putIntOrdered(termOffset, messageLength);
    }

    @Test
    public void shouldPatchToEndOfPartition() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = (TermUnblockerTest.TERM_BUFFER_CAPACITY) - messageLength;
        final int tailOffset = TermUnblockerTest.TERM_BUFFER_CAPACITY;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn(0);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.UNBLOCKED_TO_END));
        final InOrder inOrder = Mockito.inOrder(mockTermBuffer);
        inOrder.verify(mockTermBuffer).putShort(FrameDescriptor.typeOffset(termOffset), ((short) (HDR_TYPE_PAD)), ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putInt(FrameDescriptor.termOffsetOffset(termOffset), termOffset, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putIntOrdered(termOffset, messageLength);
    }

    @Test
    public void shouldScanForwardForNextCompleteMessage() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = 0;
        final int tailOffset = messageLength * 2;
        Mockito.when(mockTermBuffer.getIntVolatile(messageLength)).thenReturn(messageLength);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.UNBLOCKED));
        final InOrder inOrder = Mockito.inOrder(mockTermBuffer);
        inOrder.verify(mockTermBuffer).putShort(FrameDescriptor.typeOffset(termOffset), ((short) (HDR_TYPE_PAD)), ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putInt(FrameDescriptor.termOffsetOffset(termOffset), termOffset, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putIntOrdered(termOffset, messageLength);
    }

    @Test
    public void shouldScanForwardForNextNonCommittedMessage() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = 0;
        final int tailOffset = messageLength * 2;
        Mockito.when(mockTermBuffer.getIntVolatile(messageLength)).thenReturn((-messageLength));
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.UNBLOCKED));
        final InOrder inOrder = Mockito.inOrder(mockTermBuffer);
        inOrder.verify(mockTermBuffer).putShort(FrameDescriptor.typeOffset(termOffset), ((short) (HDR_TYPE_PAD)), ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putInt(FrameDescriptor.termOffsetOffset(termOffset), termOffset, ByteOrder.LITTLE_ENDIAN);
        inOrder.verify(mockTermBuffer).putIntOrdered(termOffset, messageLength);
    }

    @Test
    public void shouldTakeNoActionIfMessageCompleteAfterScan() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = 0;
        final int tailOffset = messageLength * 2;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn(0).thenReturn(messageLength);
        Mockito.when(mockTermBuffer.getIntVolatile(messageLength)).thenReturn(messageLength);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldTakeNoActionIfMessageNonCommittedAfterScan() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = 0;
        final int tailOffset = messageLength * 2;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn(0).thenReturn((-messageLength));
        Mockito.when(mockTermBuffer.getIntVolatile(messageLength)).thenReturn(messageLength);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldTakeNoActionToEndOfPartitionIfMessageCompleteAfterScan() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = (TermUnblockerTest.TERM_BUFFER_CAPACITY) - messageLength;
        final int tailOffset = TermUnblockerTest.TERM_BUFFER_CAPACITY;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn(0).thenReturn(messageLength);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldTakeNoActionToEndOfPartitionIfMessageNonCommittedAfterScan() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = (TermUnblockerTest.TERM_BUFFER_CAPACITY) - messageLength;
        final int tailOffset = TermUnblockerTest.TERM_BUFFER_CAPACITY;
        Mockito.when(mockTermBuffer.getIntVolatile(termOffset)).thenReturn(0).thenReturn((-messageLength));
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldNotUnblockGapWithMessageRaceOnSecondMessageIncreasingTailThenInterrupting() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = 0;
        final int tailOffset = messageLength * 3;
        Mockito.when(mockTermBuffer.getIntVolatile(messageLength)).thenReturn(0).thenReturn(messageLength);
        Mockito.when(mockTermBuffer.getIntVolatile((messageLength * 2))).thenReturn(messageLength);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }

    @Test
    public void shouldNotUnblockGapWithMessageRaceWhenScanForwardTakesAnInterrupt() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int termOffset = 0;
        final int tailOffset = messageLength * 3;
        Mockito.when(mockTermBuffer.getIntVolatile(messageLength)).thenReturn(0).thenReturn(messageLength);
        Mockito.when(mockTermBuffer.getIntVolatile((messageLength + (HEADER_LENGTH)))).thenReturn(7);
        Assert.assertThat(TermUnblocker.unblock(mockLogMetaDataBuffer, mockTermBuffer, termOffset, tailOffset, TermUnblockerTest.TERM_ID), Matchers.is(Status.NO_ACTION));
    }
}

