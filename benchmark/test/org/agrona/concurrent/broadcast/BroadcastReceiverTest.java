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
package org.agrona.concurrent.broadcast;


import org.agrona.BitUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static BroadcastBufferDescriptor.LATEST_COUNTER_OFFSET;
import static BroadcastBufferDescriptor.TAIL_COUNTER_OFFSET;
import static BroadcastBufferDescriptor.TAIL_INTENT_COUNTER_OFFSET;
import static BroadcastBufferDescriptor.TRAILER_LENGTH;


public class BroadcastReceiverTest {
    private static final int MSG_TYPE_ID = 7;

    private static final int CAPACITY = 1024;

    private static final int TOTAL_BUFFER_LENGTH = (BroadcastReceiverTest.CAPACITY) + (TRAILER_LENGTH);

    private static final int TAIL_INTENT_COUNTER_OFFSET = (BroadcastReceiverTest.CAPACITY) + (TAIL_INTENT_COUNTER_OFFSET);

    private static final int TAIL_COUNTER_INDEX = (BroadcastReceiverTest.CAPACITY) + (TAIL_COUNTER_OFFSET);

    private static final int LATEST_COUNTER_INDEX = (BroadcastReceiverTest.CAPACITY) + (LATEST_COUNTER_OFFSET);

    private final UnsafeBuffer buffer = Mockito.mock(UnsafeBuffer.class);

    private BroadcastReceiver broadcastReceiver;

    @Test
    public void shouldCalculateCapacityForBuffer() {
        Assert.assertThat(broadcastReceiver.capacity(), Matchers.is(BroadcastReceiverTest.CAPACITY));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForCapacityThatIsNotPowerOfTwo() {
        final int capacity = 777;
        final int totalBufferLength = capacity + (TRAILER_LENGTH);
        Mockito.when(buffer.capacity()).thenReturn(totalBufferLength);
        new BroadcastReceiver(buffer);
    }

    @Test
    public void shouldNotBeLappedBeforeReception() {
        Assert.assertThat(broadcastReceiver.lappedCount(), Matchers.is(0L));
    }

    @Test
    public void shouldNotReceiveFromEmptyBuffer() {
        Assert.assertFalse(broadcastReceiver.receiveNext());
    }

    @Test
    public void shouldReceiveFirstMessageFromBuffer() {
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final long tail = recordLengthAligned;
        final long latestRecord = tail - recordLengthAligned;
        final int recordOffset = ((int) (latestRecord));
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET)).thenReturn(tail);
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        Mockito.when(buffer.getInt(lengthOffset(recordOffset))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(recordOffset))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        Assert.assertTrue(broadcastReceiver.receiveNext());
        Assert.assertThat(broadcastReceiver.typeId(), Matchers.is(BroadcastReceiverTest.MSG_TYPE_ID));
        Assert.assertThat(broadcastReceiver.buffer(), Matchers.is(buffer));
        Assert.assertThat(broadcastReceiver.offset(), Matchers.is(msgOffset(recordOffset)));
        Assert.assertThat(broadcastReceiver.length(), Matchers.is(length));
        Assert.assertTrue(broadcastReceiver.validate());
        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET);
    }

    @Test
    public void shouldReceiveTwoMessagesFromBuffer() {
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final long tail = recordLengthAligned * 2;
        final long latestRecord = tail - recordLengthAligned;
        final int recordOffsetOne = 0;
        final int recordOffsetTwo = ((int) (latestRecord));
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET)).thenReturn(tail);
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        Mockito.when(buffer.getInt(lengthOffset(recordOffsetOne))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(recordOffsetOne))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        Mockito.when(buffer.getInt(lengthOffset(recordOffsetTwo))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(recordOffsetTwo))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        Assert.assertTrue(broadcastReceiver.receiveNext());
        Assert.assertThat(broadcastReceiver.typeId(), Matchers.is(BroadcastReceiverTest.MSG_TYPE_ID));
        Assert.assertThat(broadcastReceiver.buffer(), Matchers.is(buffer));
        Assert.assertThat(broadcastReceiver.offset(), Matchers.is(msgOffset(recordOffsetOne)));
        Assert.assertThat(broadcastReceiver.length(), Matchers.is(length));
        Assert.assertTrue(broadcastReceiver.validate());
        Assert.assertTrue(broadcastReceiver.receiveNext());
        Assert.assertThat(broadcastReceiver.typeId(), Matchers.is(BroadcastReceiverTest.MSG_TYPE_ID));
        Assert.assertThat(broadcastReceiver.buffer(), Matchers.is(buffer));
        Assert.assertThat(broadcastReceiver.offset(), Matchers.is(msgOffset(recordOffsetTwo)));
        Assert.assertThat(broadcastReceiver.length(), Matchers.is(length));
        Assert.assertTrue(broadcastReceiver.validate());
        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET);
    }

    @Test
    public void shouldLateJoinTransmission() {
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final long tail = (((BroadcastReceiverTest.CAPACITY) * 3L) + (HEADER_LENGTH)) + recordLengthAligned;
        final long latestRecord = tail - recordLengthAligned;
        final int recordOffset = ((int) (latestRecord)) & ((BroadcastReceiverTest.CAPACITY) - 1);
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET)).thenReturn(tail);
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        Mockito.when(buffer.getLong(BroadcastReceiverTest.LATEST_COUNTER_INDEX)).thenReturn(latestRecord);
        Mockito.when(buffer.getInt(lengthOffset(recordOffset))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(recordOffset))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        Assert.assertTrue(broadcastReceiver.receiveNext());
        Assert.assertThat(broadcastReceiver.typeId(), Matchers.is(BroadcastReceiverTest.MSG_TYPE_ID));
        Assert.assertThat(broadcastReceiver.buffer(), Matchers.is(buffer));
        Assert.assertThat(broadcastReceiver.offset(), Matchers.is(msgOffset(recordOffset)));
        Assert.assertThat(broadcastReceiver.length(), Matchers.is(length));
        Assert.assertTrue(broadcastReceiver.validate());
        Assert.assertThat(broadcastReceiver.lappedCount(), Matchers.is(Matchers.greaterThan(0L)));
    }

    @Test
    public void shouldCopeWithPaddingRecordAndWrapOfBufferForNextRecord() {
        final int length = 120;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final long catchupTail = ((BroadcastReceiverTest.CAPACITY) * 2L) - (HEADER_LENGTH);
        final long postPaddingTail = (catchupTail + (HEADER_LENGTH)) + recordLengthAligned;
        final long latestRecord = catchupTail - recordLengthAligned;
        final int catchupOffset = ((int) (latestRecord)) & ((BroadcastReceiverTest.CAPACITY) - 1);
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET)).thenReturn(catchupTail).thenReturn(postPaddingTail);
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX)).thenReturn(catchupTail).thenReturn(postPaddingTail);
        Mockito.when(buffer.getLong(BroadcastReceiverTest.LATEST_COUNTER_INDEX)).thenReturn(latestRecord);
        Mockito.when(buffer.getInt(lengthOffset(catchupOffset))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(catchupOffset))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        final int paddingOffset = ((int) (catchupTail)) & ((BroadcastReceiverTest.CAPACITY) - 1);
        final int recordOffset = ((int) (postPaddingTail - recordLengthAligned)) & ((BroadcastReceiverTest.CAPACITY) - 1);
        Mockito.when(buffer.getInt(typeOffset(paddingOffset))).thenReturn(PADDING_MSG_TYPE_ID);
        Mockito.when(buffer.getInt(lengthOffset(recordOffset))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(recordOffset))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        Assert.assertTrue(broadcastReceiver.receiveNext());// To catch up to record before padding.

        Assert.assertTrue(broadcastReceiver.receiveNext());// no skip over the padding and read next record.

        Assert.assertThat(broadcastReceiver.typeId(), Matchers.is(BroadcastReceiverTest.MSG_TYPE_ID));
        Assert.assertThat(broadcastReceiver.buffer(), Matchers.is(buffer));
        Assert.assertThat(broadcastReceiver.offset(), Matchers.is(msgOffset(recordOffset)));
        Assert.assertThat(broadcastReceiver.length(), Matchers.is(length));
        Assert.assertTrue(broadcastReceiver.validate());
    }

    @Test
    public void shouldDealWithRecordBecomingInvalidDueToOverwrite() {
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final long tail = recordLengthAligned;
        final long latestRecord = tail - recordLengthAligned;
        final int recordOffset = ((int) (latestRecord));
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_INTENT_COUNTER_OFFSET)).thenReturn(tail).thenReturn((tail + ((BroadcastReceiverTest.CAPACITY) - recordLengthAligned)));
        Mockito.when(buffer.getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        Mockito.when(buffer.getInt(lengthOffset(recordOffset))).thenReturn(recordLength);
        Mockito.when(buffer.getInt(typeOffset(recordOffset))).thenReturn(BroadcastReceiverTest.MSG_TYPE_ID);
        Assert.assertTrue(broadcastReceiver.receiveNext());
        Assert.assertThat(broadcastReceiver.typeId(), Matchers.is(BroadcastReceiverTest.MSG_TYPE_ID));
        Assert.assertThat(broadcastReceiver.buffer(), Matchers.is(buffer));
        Assert.assertThat(broadcastReceiver.offset(), Matchers.is(msgOffset(recordOffset)));
        Assert.assertThat(broadcastReceiver.length(), Matchers.is(length));
        Assert.assertFalse(broadcastReceiver.validate());// Need to receiveNext() to catch up with transmission again.

        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLongVolatile(BroadcastReceiverTest.TAIL_COUNTER_INDEX);
    }
}

