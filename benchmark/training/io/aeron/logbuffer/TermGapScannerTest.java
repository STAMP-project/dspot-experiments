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


import TermGapScanner.GapHandler;
import io.aeron.protocol.DataHeaderFlyweight;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static LogBufferDescriptor.TERM_MIN_LENGTH;


public class TermGapScannerTest {
    private static final int LOG_BUFFER_CAPACITY = TERM_MIN_LENGTH;

    private static final int TERM_ID = 1;

    private static final int HEADER_LENGTH = DataHeaderFlyweight.HEADER_LENGTH;

    private final UnsafeBuffer termBuffer = Mockito.mock(UnsafeBuffer.class);

    private final GapHandler gapHandler = Mockito.mock(GapHandler.class);

    @Test
    public void shouldReportGapAtBeginningOfBuffer() {
        final int frameOffset = align(((TermGapScannerTest.HEADER_LENGTH) * 3), FrameDescriptor.FRAME_ALIGNMENT);
        final int highWaterMark = frameOffset + (align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT));
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(TermGapScannerTest.HEADER_LENGTH);
        Assert.assertThat(TermGapScanner.scanForGap(termBuffer, TermGapScannerTest.TERM_ID, 0, highWaterMark, gapHandler), Matchers.is(0));
        Mockito.verify(gapHandler).onGap(TermGapScannerTest.TERM_ID, 0, frameOffset);
    }

    @Test
    public void shouldReportSingleGapWhenBufferNotFull() {
        final int tail = align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT);
        final int highWaterMark = (FrameDescriptor.FRAME_ALIGNMENT) * 3;
        Mockito.when(termBuffer.getIntVolatile((tail - (align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT))))).thenReturn(TermGapScannerTest.HEADER_LENGTH);
        Mockito.when(termBuffer.getIntVolatile(tail)).thenReturn(0);
        Mockito.when(termBuffer.getIntVolatile((highWaterMark - (align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT))))).thenReturn(TermGapScannerTest.HEADER_LENGTH);
        Assert.assertThat(TermGapScanner.scanForGap(termBuffer, TermGapScannerTest.TERM_ID, tail, highWaterMark, gapHandler), Matchers.is(tail));
        Mockito.verify(gapHandler).onGap(TermGapScannerTest.TERM_ID, tail, align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT));
    }

    @Test
    public void shouldReportSingleGapWhenBufferIsFull() {
        final int tail = (TermGapScannerTest.LOG_BUFFER_CAPACITY) - ((align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT)) * 2);
        final int highWaterMark = TermGapScannerTest.LOG_BUFFER_CAPACITY;
        Mockito.when(termBuffer.getIntVolatile((tail - (align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT))))).thenReturn(TermGapScannerTest.HEADER_LENGTH);
        Mockito.when(termBuffer.getIntVolatile(tail)).thenReturn(0);
        Mockito.when(termBuffer.getIntVolatile((highWaterMark - (align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT))))).thenReturn(TermGapScannerTest.HEADER_LENGTH);
        Assert.assertThat(TermGapScanner.scanForGap(termBuffer, TermGapScannerTest.TERM_ID, tail, highWaterMark, gapHandler), Matchers.is(tail));
        Mockito.verify(gapHandler).onGap(TermGapScannerTest.TERM_ID, tail, align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT));
    }

    @Test
    public void shouldReportNoGapWhenHwmIsInPadding() {
        final int paddingLength = (align(TermGapScannerTest.HEADER_LENGTH, FrameDescriptor.FRAME_ALIGNMENT)) * 2;
        final int tail = (TermGapScannerTest.LOG_BUFFER_CAPACITY) - paddingLength;
        final int highWaterMark = ((TermGapScannerTest.LOG_BUFFER_CAPACITY) - paddingLength) + (TermGapScannerTest.HEADER_LENGTH);
        Mockito.when(termBuffer.getIntVolatile(tail)).thenReturn(paddingLength);
        Mockito.when(termBuffer.getIntVolatile((tail + (TermGapScannerTest.HEADER_LENGTH)))).thenReturn(0);
        Assert.assertThat(TermGapScanner.scanForGap(termBuffer, TermGapScannerTest.TERM_ID, tail, highWaterMark, gapHandler), Matchers.is(TermGapScannerTest.LOG_BUFFER_CAPACITY));
        Mockito.verifyZeroInteractions(gapHandler);
    }
}

