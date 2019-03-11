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


import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class LogBufferUnblockerTest {
    private static final int TERM_LENGTH = TERM_MIN_LENGTH;

    private static final int TERM_ID_1 = 1;

    private static final int TERM_TAIL_COUNTER_OFFSET = TERM_TAIL_COUNTERS_OFFSET;

    private final UnsafeBuffer logMetaDataBuffer = Mockito.spy(new UnsafeBuffer(ByteBuffer.allocateDirect(LOG_META_DATA_LENGTH)));

    private final UnsafeBuffer[] termBuffers = new UnsafeBuffer[PARTITION_COUNT];

    private final int positionBitsToShift = LogBufferDescriptor.LogBufferDescriptor.positionBitsToShift(LogBufferUnblockerTest.TERM_LENGTH);

    @Test
    public void shouldNotUnblockWhenPositionHasCompleteMessage() {
        final int blockedOffset = (HEADER_LENGTH) * 4;
        final long blockedPosition = computePosition(LogBufferUnblockerTest.TERM_ID_1, blockedOffset, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1);
        final int activeIndex = indexByPosition(blockedPosition, positionBitsToShift);
        Mockito.when(termBuffers[activeIndex].getIntVolatile(blockedOffset)).thenReturn(HEADER_LENGTH);
        Assert.assertFalse(LogBufferUnblocker.unblock(termBuffers, logMetaDataBuffer, blockedPosition, LogBufferUnblockerTest.TERM_LENGTH));
        final long rawTail = rawTailVolatile(logMetaDataBuffer);
        Assert.assertThat(computePosition(termId(rawTail), blockedOffset, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1), Matchers.is(blockedPosition));
    }

    @Test
    public void shouldUnblockWhenPositionHasNonCommittedMessageAndTailWithinTerm() {
        final int blockedOffset = (HEADER_LENGTH) * 4;
        final int messageLength = (HEADER_LENGTH) * 4;
        final long blockedPosition = computePosition(LogBufferUnblockerTest.TERM_ID_1, blockedOffset, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1);
        final int activeIndex = indexByPosition(blockedPosition, positionBitsToShift);
        Mockito.when(termBuffers[activeIndex].getIntVolatile(blockedOffset)).thenReturn((-messageLength));
        Assert.assertTrue(LogBufferUnblocker.unblock(termBuffers, logMetaDataBuffer, blockedPosition, LogBufferUnblockerTest.TERM_LENGTH));
        final long rawTail = rawTailVolatile(logMetaDataBuffer);
        Assert.assertThat(computePosition(termId(rawTail), (blockedOffset + messageLength), positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1), Matchers.is((blockedPosition + messageLength)));
    }

    @Test
    public void shouldUnblockWhenPositionHasNonCommittedMessageAndTailAtEndOfTerm() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int blockedOffset = (LogBufferUnblockerTest.TERM_LENGTH) - messageLength;
        final long blockedPosition = computePosition(LogBufferUnblockerTest.TERM_ID_1, blockedOffset, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1);
        final int activeIndex = indexByPosition(blockedPosition, positionBitsToShift);
        Mockito.when(termBuffers[activeIndex].getIntVolatile(blockedOffset)).thenReturn(0);
        logMetaDataBuffer.getAndAddLong(LogBufferUnblockerTest.TERM_TAIL_COUNTER_OFFSET, LogBufferUnblockerTest.TERM_LENGTH);
        Assert.assertTrue(LogBufferUnblocker.unblock(termBuffers, logMetaDataBuffer, blockedPosition, LogBufferUnblockerTest.TERM_LENGTH));
        final long rawTail = rawTailVolatile(logMetaDataBuffer);
        final int termId = termId(rawTail);
        Assert.assertThat(computePosition(termId, 0, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1), Matchers.is((blockedPosition + messageLength)));
        Mockito.verify(logMetaDataBuffer).compareAndSetInt(LOG_ACTIVE_TERM_COUNT_OFFSET, 0, 1);
    }

    @Test
    public void shouldUnblockWhenPositionHasCommittedMessageAndTailAtEndOfTermButNotRotated() {
        final long blockedPosition = LogBufferUnblockerTest.TERM_LENGTH;
        final int termTailCounterTwoOffset = (LogBufferUnblockerTest.TERM_TAIL_COUNTER_OFFSET) + (SIZE_OF_LONG);
        logMetaDataBuffer.getAndAddLong(LogBufferUnblockerTest.TERM_TAIL_COUNTER_OFFSET, LogBufferUnblockerTest.TERM_LENGTH);
        Assert.assertTrue(LogBufferUnblocker.unblock(termBuffers, logMetaDataBuffer, blockedPosition, LogBufferUnblockerTest.TERM_LENGTH));
        final long rawTail = rawTailVolatile(logMetaDataBuffer);
        final int termId = termId(rawTail);
        Assert.assertThat(termId, Matchers.is(((LogBufferUnblockerTest.TERM_ID_1) + 1)));
        Assert.assertThat(computePosition(termId, 0, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1), Matchers.is(blockedPosition));
        final InOrder inOrder = Mockito.inOrder(logMetaDataBuffer);
        inOrder.verify(logMetaDataBuffer).compareAndSetLong(termTailCounterTwoOffset, LogBufferUnblockerTest.pack(((LogBufferUnblockerTest.TERM_ID_1) - 2), 0), LogBufferUnblockerTest.pack(((LogBufferUnblockerTest.TERM_ID_1) + 1), 0));
        inOrder.verify(logMetaDataBuffer).compareAndSetInt(LOG_ACTIVE_TERM_COUNT_OFFSET, 0, 1);
    }

    @Test
    public void shouldUnblockWhenPositionHasNonCommittedMessageAndTailPastEndOfTerm() {
        final int messageLength = (HEADER_LENGTH) * 4;
        final int blockedOffset = (LogBufferUnblockerTest.TERM_LENGTH) - messageLength;
        final long blockedPosition = computePosition(LogBufferUnblockerTest.TERM_ID_1, blockedOffset, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1);
        final int activeIndex = indexByPosition(blockedPosition, positionBitsToShift);
        Mockito.when(termBuffers[activeIndex].getIntVolatile(blockedOffset)).thenReturn(0);
        logMetaDataBuffer.putLong(LogBufferUnblockerTest.TERM_TAIL_COUNTER_OFFSET, LogBufferUnblockerTest.pack(LogBufferUnblockerTest.TERM_ID_1, ((LogBufferUnblockerTest.TERM_LENGTH) + (HEADER_LENGTH))));
        Assert.assertTrue(LogBufferUnblocker.unblock(termBuffers, logMetaDataBuffer, blockedPosition, LogBufferUnblockerTest.TERM_LENGTH));
        final long rawTail = rawTailVolatile(logMetaDataBuffer);
        final int termId = termId(rawTail);
        Assert.assertThat(computePosition(termId, 0, positionBitsToShift, LogBufferUnblockerTest.TERM_ID_1), Matchers.is((blockedPosition + messageLength)));
        Mockito.verify(logMetaDataBuffer).compareAndSetInt(LOG_ACTIVE_TERM_COUNT_OFFSET, 0, 1);
    }
}

