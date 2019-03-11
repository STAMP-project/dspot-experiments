package io.aeron.logbuffer;


import io.aeron.protocol.DataHeaderFlyweight;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static LogBufferDescriptor.TERM_MIN_LENGTH;


public class TermScannerTest {
    private static final int TERM_BUFFER_CAPACITY = TERM_MIN_LENGTH;

    private static final int MTU_LENGTH = 1024;

    private static final int HEADER_LENGTH = DataHeaderFlyweight.HEADER_LENGTH;

    private final UnsafeBuffer termBuffer = Mockito.mock(UnsafeBuffer.class);

    @Test
    public void shouldPackPaddingAndOffsetIntoResultingStatus() {
        final int padding = 77;
        final int available = 65000;
        final long scanOutcome = TermScanner.pack(padding, available);
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(padding));
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(available));
    }

    @Test
    public void shouldReturnZeroOnEmptyLog() {
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, 0, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(0));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
    }

    @Test
    public void shouldScanSingleMessage() {
        final int msgLength = 1;
        final int frameLength = (TermScannerTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int frameOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(alignedFrameLength));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
        final InOrder inOrder = Mockito.inOrder(termBuffer);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
    }

    @Test
    public void shouldFailToScanMessageLargerThanMaxLength() {
        final int msgLength = 1;
        final int frameLength = (TermScannerTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int maxLength = alignedFrameLength - 1;
        final int frameOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, maxLength);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(0));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
        final InOrder inOrder = Mockito.inOrder(termBuffer);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
    }

    @Test
    public void shouldScanTwoMessagesThatFitInSingleMtu() {
        final int msgLength = 100;
        final int frameLength = (TermScannerTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        int frameOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile((frameOffset + alignedFrameLength))).thenReturn(alignedFrameLength);
        Mockito.when(termBuffer.getShort(typeOffset((frameOffset + alignedFrameLength)))).thenReturn(((short) (HDR_TYPE_DATA)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is((alignedFrameLength * 2)));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
        final InOrder inOrder = Mockito.inOrder(termBuffer);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
        frameOffset += alignedFrameLength;
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
    }

    @Test
    public void shouldScanTwoMessagesAndStopAtMtuBoundary() {
        final int frameTwoLength = align(((TermScannerTest.HEADER_LENGTH) + 1), FRAME_ALIGNMENT);
        final int frameOneLength = (TermScannerTest.MTU_LENGTH) - frameTwoLength;
        int frameOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameOneLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile((frameOffset + frameOneLength))).thenReturn(frameTwoLength);
        Mockito.when(termBuffer.getShort(typeOffset((frameOffset + frameOneLength)))).thenReturn(((short) (HDR_TYPE_DATA)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is((frameOneLength + frameTwoLength)));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
        final InOrder inOrder = Mockito.inOrder(termBuffer);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
        frameOffset += frameOneLength;
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
    }

    @Test
    public void shouldScanTwoMessagesAndStopAtSecondThatSpansMtu() {
        final int frameTwoLength = align(((TermScannerTest.HEADER_LENGTH) * 2), FRAME_ALIGNMENT);
        final int frameOneLength = (TermScannerTest.MTU_LENGTH) - (frameTwoLength / 2);
        int frameOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameOneLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile((frameOffset + frameOneLength))).thenReturn(frameTwoLength);
        Mockito.when(termBuffer.getShort(typeOffset((frameOffset + frameOneLength)))).thenReturn(((short) (HDR_TYPE_DATA)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(frameOneLength));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
        final InOrder inOrder = Mockito.inOrder(termBuffer);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
        frameOffset += frameOneLength;
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(termBuffer).getShort(typeOffset(frameOffset));
    }

    @Test
    public void shouldScanLastFrameInBuffer() {
        final int alignedFrameLength = align(((TermScannerTest.HEADER_LENGTH) * 2), FRAME_ALIGNMENT);
        final int frameOffset = (TermScannerTest.TERM_BUFFER_CAPACITY) - alignedFrameLength;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(alignedFrameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(alignedFrameLength));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
    }

    @Test
    public void shouldScanLastMessageInBufferPlusPadding() {
        final int alignedFrameLength = align(((TermScannerTest.HEADER_LENGTH) * 2), FRAME_ALIGNMENT);
        final int paddingFrameLength = align(((TermScannerTest.HEADER_LENGTH) * 3), FRAME_ALIGNMENT);
        final int frameOffset = (TermScannerTest.TERM_BUFFER_CAPACITY) - (alignedFrameLength + paddingFrameLength);
        Mockito.when(Integer.valueOf(termBuffer.getIntVolatile(frameOffset))).thenReturn(alignedFrameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile((frameOffset + alignedFrameLength))).thenReturn(paddingFrameLength);
        Mockito.when(termBuffer.getShort(typeOffset((frameOffset + alignedFrameLength)))).thenReturn(((short) (PADDING_FRAME_TYPE)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, TermScannerTest.MTU_LENGTH);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is((alignedFrameLength + (TermScannerTest.HEADER_LENGTH))));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is((paddingFrameLength - (TermScannerTest.HEADER_LENGTH))));
    }

    @Test
    public void shouldScanLastMessageInBufferMinusPaddingLimitedByMtu() {
        final int alignedFrameLength = align(TermScannerTest.HEADER_LENGTH, FRAME_ALIGNMENT);
        final int frameOffset = (TermScannerTest.TERM_BUFFER_CAPACITY) - (align(((TermScannerTest.HEADER_LENGTH) * 3), FRAME_ALIGNMENT));
        final int mtu = alignedFrameLength + 8;
        Mockito.when(Integer.valueOf(termBuffer.getIntVolatile(frameOffset))).thenReturn(alignedFrameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile((frameOffset + alignedFrameLength))).thenReturn((alignedFrameLength * 2));
        Mockito.when(termBuffer.getShort(typeOffset((frameOffset + alignedFrameLength)))).thenReturn(((short) (PADDING_FRAME_TYPE)));
        final long scanOutcome = TermScanner.scanForAvailability(termBuffer, frameOffset, mtu);
        Assert.assertThat(TermScanner.available(scanOutcome), CoreMatchers.is(alignedFrameLength));
        Assert.assertThat(TermScanner.padding(scanOutcome), CoreMatchers.is(0));
    }
}

