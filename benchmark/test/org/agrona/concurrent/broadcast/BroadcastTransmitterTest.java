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


public class BroadcastTransmitterTest {
    private static final int MSG_TYPE_ID = 7;

    private static final int CAPACITY = 1024;

    private static final int TOTAL_BUFFER_LENGTH = (BroadcastTransmitterTest.CAPACITY) + (TRAILER_LENGTH);

    private static final int TAIL_INTENT_COUNTER_OFFSET = (BroadcastTransmitterTest.CAPACITY) + (TAIL_INTENT_COUNTER_OFFSET);

    private static final int TAIL_COUNTER_INDEX = (BroadcastTransmitterTest.CAPACITY) + (TAIL_COUNTER_OFFSET);

    private static final int LATEST_COUNTER_INDEX = (BroadcastTransmitterTest.CAPACITY) + (LATEST_COUNTER_OFFSET);

    private final UnsafeBuffer buffer = Mockito.mock(UnsafeBuffer.class);

    private BroadcastTransmitter broadcastTransmitter;

    @Test
    public void shouldCalculateCapacityForBuffer() {
        Assert.assertThat(broadcastTransmitter.capacity(), Matchers.is(BroadcastTransmitterTest.CAPACITY));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForCapacityThatIsNotPowerOfTwo() {
        final int capacity = 777;
        final int totalBufferLength = capacity + (TRAILER_LENGTH);
        Mockito.when(buffer.capacity()).thenReturn(totalBufferLength);
        new BroadcastTransmitter(buffer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMaxMessageLengthExceeded() {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        broadcastTransmitter.transmit(BroadcastTransmitterTest.MSG_TYPE_ID, srcBuffer, 0, ((broadcastTransmitter.maxMsgLength()) + 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMessageTypeIdInvalid() {
        final int invalidMsgId = -1;
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        broadcastTransmitter.transmit(invalidMsgId, srcBuffer, 0, 32);
    }

    @Test
    public void shouldTransmitIntoEmptyBuffer() {
        final long tail = 0L;
        final int recordOffset = ((int) (tail));
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int srcIndex = 0;
        broadcastTransmitter.transmit(BroadcastTransmitterTest.MSG_TYPE_ID, srcBuffer, srcIndex, length);
        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_INTENT_COUNTER_OFFSET, (tail + recordLengthAligned));
        inOrder.verify(buffer).putInt(lengthOffset(recordOffset), recordLength);
        inOrder.verify(buffer).putInt(typeOffset(recordOffset), BroadcastTransmitterTest.MSG_TYPE_ID);
        inOrder.verify(buffer).putBytes(msgOffset(recordOffset), srcBuffer, srcIndex, length);
        inOrder.verify(buffer).putLong(BroadcastTransmitterTest.LATEST_COUNTER_INDEX, tail);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_COUNTER_INDEX, (tail + recordLengthAligned));
    }

    @Test
    public void shouldTransmitIntoUsedBuffer() {
        final long tail = (RECORD_ALIGNMENT) * 3;
        final int recordOffset = ((int) (tail));
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        Mockito.when(buffer.getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int srcIndex = 0;
        broadcastTransmitter.transmit(BroadcastTransmitterTest.MSG_TYPE_ID, srcBuffer, srcIndex, length);
        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_INTENT_COUNTER_OFFSET, (tail + recordLengthAligned));
        inOrder.verify(buffer).putInt(lengthOffset(recordOffset), recordLength);
        inOrder.verify(buffer).putInt(typeOffset(recordOffset), BroadcastTransmitterTest.MSG_TYPE_ID);
        inOrder.verify(buffer).putBytes(msgOffset(recordOffset), srcBuffer, srcIndex, length);
        inOrder.verify(buffer).putLong(BroadcastTransmitterTest.LATEST_COUNTER_INDEX, tail);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_COUNTER_INDEX, (tail + recordLengthAligned));
    }

    @Test
    public void shouldTransmitIntoEndOfBuffer() {
        final int length = 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final long tail = (BroadcastTransmitterTest.CAPACITY) - recordLengthAligned;
        final int recordOffset = ((int) (tail));
        Mockito.when(buffer.getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int srcIndex = 0;
        broadcastTransmitter.transmit(BroadcastTransmitterTest.MSG_TYPE_ID, srcBuffer, srcIndex, length);
        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_INTENT_COUNTER_OFFSET, (tail + recordLengthAligned));
        inOrder.verify(buffer).putInt(lengthOffset(recordOffset), recordLength);
        inOrder.verify(buffer).putInt(typeOffset(recordOffset), BroadcastTransmitterTest.MSG_TYPE_ID);
        inOrder.verify(buffer).putBytes(msgOffset(recordOffset), srcBuffer, srcIndex, length);
        inOrder.verify(buffer).putLong(BroadcastTransmitterTest.LATEST_COUNTER_INDEX, tail);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_COUNTER_INDEX, (tail + recordLengthAligned));
    }

    @Test
    public void shouldApplyPaddingWhenInsufficientSpaceAtEndOfBuffer() {
        long tail = (BroadcastTransmitterTest.CAPACITY) - (RECORD_ALIGNMENT);
        int recordOffset = ((int) (tail));
        final int length = (RECORD_ALIGNMENT) + 8;
        final int recordLength = length + (HEADER_LENGTH);
        final int recordLengthAligned = BitUtil.align(recordLength, RECORD_ALIGNMENT);
        final int toEndOfBuffer = (BroadcastTransmitterTest.CAPACITY) - recordOffset;
        Mockito.when(buffer.getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX)).thenReturn(tail);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int srcIndex = 0;
        broadcastTransmitter.transmit(BroadcastTransmitterTest.MSG_TYPE_ID, srcBuffer, srcIndex, length);
        final InOrder inOrder = Mockito.inOrder(buffer);
        inOrder.verify(buffer).getLong(BroadcastTransmitterTest.TAIL_COUNTER_INDEX);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_INTENT_COUNTER_OFFSET, ((tail + recordLengthAligned) + toEndOfBuffer));
        inOrder.verify(buffer).putInt(lengthOffset(recordOffset), toEndOfBuffer);
        inOrder.verify(buffer).putInt(typeOffset(recordOffset), PADDING_MSG_TYPE_ID);
        tail += toEndOfBuffer;
        recordOffset = 0;
        inOrder.verify(buffer).putInt(lengthOffset(recordOffset), recordLength);
        inOrder.verify(buffer).putInt(typeOffset(recordOffset), BroadcastTransmitterTest.MSG_TYPE_ID);
        inOrder.verify(buffer).putBytes(msgOffset(recordOffset), srcBuffer, srcIndex, length);
        inOrder.verify(buffer).putLong(BroadcastTransmitterTest.LATEST_COUNTER_INDEX, tail);
        inOrder.verify(buffer).putLongOrdered(BroadcastTransmitterTest.TAIL_COUNTER_INDEX, (tail + recordLengthAligned));
    }
}

