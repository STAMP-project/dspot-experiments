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
package io.aeron.driver;


import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class LossDetectorTest {
    private static final int TERM_BUFFER_LENGTH = TERM_MIN_LENGTH;

    private static final int POSITION_BITS_TO_SHIFT = LogBufferDescriptor.positionBitsToShift(LossDetectorTest.TERM_BUFFER_LENGTH);

    private static final int MASK = (LossDetectorTest.TERM_BUFFER_LENGTH) - 1;

    private static final byte[] DATA = new byte[36];

    static {
        for (int i = 0; i < (LossDetectorTest.DATA.length); i++) {
            LossDetectorTest.DATA[i] = ((byte) (i));
        }
    }

    private static final int MESSAGE_LENGTH = (DataHeaderFlyweight.HEADER_LENGTH) + (LossDetectorTest.DATA.length);

    private static final int ALIGNED_FRAME_LENGTH = align(LossDetectorTest.MESSAGE_LENGTH, FrameDescriptor.FRAME_ALIGNMENT);

    private static final int SESSION_ID = 1582632989;

    private static final int STREAM_ID = 802830;

    private static final int TERM_ID = 976925;

    private static final long ACTIVE_TERM_POSITION = computePosition(LossDetectorTest.TERM_ID, 0, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);

    private static final StaticDelayGenerator DELAY_GENERATOR = new StaticDelayGenerator(TimeUnit.MILLISECONDS.toNanos(20), false);

    private static final StaticDelayGenerator DELAY_GENERATOR_WITH_IMMEDIATE = new StaticDelayGenerator(TimeUnit.MILLISECONDS.toNanos(20), true);

    private final UnsafeBuffer termBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(LossDetectorTest.TERM_BUFFER_LENGTH));

    private final UnsafeBuffer rcvBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(LossDetectorTest.MESSAGE_LENGTH));

    private final DataHeaderFlyweight dataHeader = new DataHeaderFlyweight();

    private LossDetector lossDetector;

    private LossHandler lossHandler;

    private long currentTime = 0;

    public LossDetectorTest() {
        lossHandler = Mockito.mock(LossHandler.class);
        lossDetector = new LossDetector(LossDetectorTest.DELAY_GENERATOR, lossHandler);
        dataHeader.wrap(rcvBuffer);
    }

    @Test
    public void shouldNotSendNakWhenBufferIsEmpty() {
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(100);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verifyZeroInteractions(lossHandler);
    }

    @Test
    public void shouldNotNakIfNoMissingData() {
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(1));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(40);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verifyZeroInteractions(lossHandler);
    }

    @Test
    public void shouldNakMissingData() {
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(40);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(1), gapLength());
    }

    @Test
    public void shouldRetransmitNakForMissingData() {
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(30);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(60);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler, Mockito.atLeast(2)).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(1), gapLength());
    }

    @Test
    public void shouldStopNakOnReceivingData() {
        long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(20);
        insertDataFrame(offsetOfMessage(1));
        rebuildPosition += (LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3;
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(100);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verifyZeroInteractions(lossHandler);
    }

    @Test
    public void shouldHandleMoreThan2Gaps() {
        long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 7);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        insertDataFrame(offsetOfMessage(4));
        insertDataFrame(offsetOfMessage(6));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(40);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        insertDataFrame(offsetOfMessage(1));
        rebuildPosition += 3 * (LossDetectorTest.ALIGNED_FRAME_LENGTH);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(80);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        final InOrder inOrder = Mockito.inOrder(lossHandler);
        inOrder.verify(lossHandler, Mockito.atLeast(1)).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(1), gapLength());
        inOrder.verify(lossHandler, Mockito.atLeast(1)).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(3), gapLength());
        inOrder.verify(lossHandler, Mockito.never()).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(5), gapLength());
    }

    @Test
    public void shouldReplaceOldNakWithNewNak() {
        long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(20);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        insertDataFrame(offsetOfMessage(4));
        insertDataFrame(offsetOfMessage(1));
        rebuildPosition += (LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3;
        hwmPosition = (LossDetectorTest.ALIGNED_FRAME_LENGTH) * 5;
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        currentTime = TimeUnit.MILLISECONDS.toNanos(100);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler, Mockito.atLeast(1)).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(3), gapLength());
    }

    @Test
    public void shouldHandleImmediateNak() {
        lossDetector = getLossHandlerWithImmediate();
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(1), gapLength());
    }

    @Test
    public void shouldNotNakImmediatelyByDefault() {
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verifyZeroInteractions(lossHandler);
    }

    @Test
    public void shouldOnlySendNaksOnceOnMultipleScans() {
        lossDetector = getLossHandlerWithImmediate();
        final long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        insertDataFrame(offsetOfMessage(0));
        insertDataFrame(offsetOfMessage(2));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(1), gapLength());
    }

    @Test
    public void shouldHandleHwmGreaterThanCompletedBuffer() {
        lossDetector = getLossHandlerWithImmediate();
        long rebuildPosition = LossDetectorTest.ACTIVE_TERM_POSITION;
        final long hwmPosition = ((LossDetectorTest.ACTIVE_TERM_POSITION) + (LossDetectorTest.TERM_BUFFER_LENGTH)) + (LossDetectorTest.ALIGNED_FRAME_LENGTH);
        insertDataFrame(offsetOfMessage(0));
        rebuildPosition += LossDetectorTest.ALIGNED_FRAME_LENGTH;
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(1), ((LossDetectorTest.TERM_BUFFER_LENGTH) - ((int) (rebuildPosition))));
    }

    @Test
    public void shouldHandleNonZeroInitialTermOffset() {
        lossDetector = getLossHandlerWithImmediate();
        final long rebuildPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 3);
        final long hwmPosition = (LossDetectorTest.ACTIVE_TERM_POSITION) + ((LossDetectorTest.ALIGNED_FRAME_LENGTH) * 5);
        insertDataFrame(offsetOfMessage(2));
        insertDataFrame(offsetOfMessage(4));
        lossDetector.scan(termBuffer, rebuildPosition, hwmPosition, currentTime, LossDetectorTest.MASK, LossDetectorTest.POSITION_BITS_TO_SHIFT, LossDetectorTest.TERM_ID);
        Mockito.verify(lossHandler).onGapDetected(LossDetectorTest.TERM_ID, offsetOfMessage(3), gapLength());
        Mockito.verifyNoMoreInteractions(lossHandler);
    }
}

