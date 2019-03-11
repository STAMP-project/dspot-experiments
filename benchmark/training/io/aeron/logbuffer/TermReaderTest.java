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


import io.aeron.protocol.DataHeaderFlyweight;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.Position;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static LogBufferDescriptor.TERM_MIN_LENGTH;


public class TermReaderTest {
    private static final int TERM_BUFFER_CAPACITY = TERM_MIN_LENGTH;

    private static final int HEADER_LENGTH = DataHeaderFlyweight.HEADER_LENGTH;

    private static final int INITIAL_TERM_ID = 7;

    private final Header header = new Header(TermReaderTest.INITIAL_TERM_ID, TermReaderTest.TERM_BUFFER_CAPACITY);

    private final UnsafeBuffer termBuffer = Mockito.mock(UnsafeBuffer.class);

    private final ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);

    private final FragmentHandler handler = Mockito.mock(FragmentHandler.class);

    private final Position subscriberPosition = Mockito.mock(Position.class);

    @Test
    public void shouldReadFirstMessage() {
        final int msgLength = 1;
        final int frameLength = (TermReaderTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int termOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(0)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(typeOffset(0))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int readOutcome = TermReader.read(termBuffer, termOffset, handler, Integer.MAX_VALUE, header, errorHandler, 0, subscriberPosition);
        Assert.assertThat(readOutcome, Is.is(1));
        final InOrder inOrder = Mockito.inOrder(termBuffer, handler, subscriberPosition);
        inOrder.verify(termBuffer).getIntVolatile(0);
        inOrder.verify(handler).onFragment(ArgumentMatchers.eq(termBuffer), ArgumentMatchers.eq(TermReaderTest.HEADER_LENGTH), ArgumentMatchers.eq(msgLength), ArgumentMatchers.any(Header.class));
        inOrder.verify(subscriberPosition).setOrdered(alignedFrameLength);
    }

    @Test
    public void shouldNotReadPastTail() {
        final int termOffset = 0;
        final int readOutcome = TermReader.read(termBuffer, termOffset, handler, Integer.MAX_VALUE, header, errorHandler, 0, subscriberPosition);
        Assert.assertThat(readOutcome, Is.is(0));
        Mockito.verify(subscriberPosition, Mockito.never()).setOrdered(ArgumentMatchers.anyLong());
        Mockito.verify(termBuffer).getIntVolatile(0);
        Mockito.verify(handler, Mockito.never()).onFragment(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }

    @Test
    public void shouldReadOneLimitedMessage() {
        final int msgLength = 1;
        final int frameLength = (TermReaderTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int termOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(ArgumentMatchers.anyInt())).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(ArgumentMatchers.anyInt())).thenReturn(((short) (HDR_TYPE_DATA)));
        final int readOutcome = TermReader.read(termBuffer, termOffset, handler, 1, header, errorHandler, 0, subscriberPosition);
        Assert.assertThat(readOutcome, Is.is(1));
        final InOrder inOrder = Mockito.inOrder(termBuffer, handler, subscriberPosition);
        inOrder.verify(termBuffer).getIntVolatile(0);
        inOrder.verify(handler).onFragment(ArgumentMatchers.eq(termBuffer), ArgumentMatchers.eq(TermReaderTest.HEADER_LENGTH), ArgumentMatchers.eq(msgLength), ArgumentMatchers.any(Header.class));
        inOrder.verify(subscriberPosition).setOrdered(alignedFrameLength);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldReadMultipleMessages() {
        final int msgLength = 1;
        final int frameLength = (TermReaderTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int termOffset = 0;
        Mockito.when(termBuffer.getIntVolatile(0)).thenReturn(frameLength);
        Mockito.when(termBuffer.getIntVolatile(alignedFrameLength)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(ArgumentMatchers.anyInt())).thenReturn(((short) (HDR_TYPE_DATA)));
        final int readOutcome = TermReader.read(termBuffer, termOffset, handler, Integer.MAX_VALUE, header, errorHandler, 0, subscriberPosition);
        Assert.assertThat(readOutcome, Is.is(2));
        final InOrder inOrder = Mockito.inOrder(termBuffer, handler, subscriberPosition);
        inOrder.verify(termBuffer).getIntVolatile(0);
        inOrder.verify(handler).onFragment(ArgumentMatchers.eq(termBuffer), ArgumentMatchers.eq(TermReaderTest.HEADER_LENGTH), ArgumentMatchers.eq(msgLength), ArgumentMatchers.any(Header.class));
        inOrder.verify(termBuffer).getIntVolatile(alignedFrameLength);
        inOrder.verify(handler).onFragment(ArgumentMatchers.eq(termBuffer), ArgumentMatchers.eq((alignedFrameLength + (TermReaderTest.HEADER_LENGTH))), ArgumentMatchers.eq(msgLength), ArgumentMatchers.any(Header.class));
        inOrder.verify(subscriberPosition).setOrdered((alignedFrameLength * 2));
    }

    @Test
    public void shouldReadLastMessage() {
        final int msgLength = 1;
        final int frameLength = (TermReaderTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int frameOffset = (TermReaderTest.TERM_BUFFER_CAPACITY) - alignedFrameLength;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int readOutcome = TermReader.read(termBuffer, frameOffset, handler, Integer.MAX_VALUE, header, errorHandler, 0, subscriberPosition);
        Assert.assertThat(readOutcome, Is.is(1));
        final InOrder inOrder = Mockito.inOrder(termBuffer, handler, subscriberPosition);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        inOrder.verify(handler).onFragment(ArgumentMatchers.eq(termBuffer), ArgumentMatchers.eq((frameOffset + (TermReaderTest.HEADER_LENGTH))), ArgumentMatchers.eq(msgLength), ArgumentMatchers.any(Header.class));
        inOrder.verify(subscriberPosition).setOrdered(alignedFrameLength);
    }

    @Test
    public void shouldNotReadLastMessageWhenPadding() {
        final int msgLength = 1;
        final int frameLength = (TermReaderTest.HEADER_LENGTH) + msgLength;
        final int alignedFrameLength = align(frameLength, FRAME_ALIGNMENT);
        final int frameOffset = (TermReaderTest.TERM_BUFFER_CAPACITY) - alignedFrameLength;
        Mockito.when(termBuffer.getIntVolatile(frameOffset)).thenReturn(frameLength);
        Mockito.when(termBuffer.getShort(typeOffset(frameOffset))).thenReturn(((short) (PADDING_FRAME_TYPE)));
        final int readOutcome = TermReader.read(termBuffer, frameOffset, handler, Integer.MAX_VALUE, header, errorHandler, 0, subscriberPosition);
        Assert.assertThat(readOutcome, Is.is(0));
        final InOrder inOrder = Mockito.inOrder(termBuffer, subscriberPosition);
        inOrder.verify(termBuffer).getIntVolatile(frameOffset);
        Mockito.verify(handler, Mockito.never()).onFragment(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        inOrder.verify(subscriberPosition).setOrdered(alignedFrameLength);
    }
}

