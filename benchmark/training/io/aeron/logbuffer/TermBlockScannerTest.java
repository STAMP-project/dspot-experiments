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


import org.agrona.BitUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TermBlockScannerTest {
    private final UnsafeBuffer termBuffer = Mockito.mock(UnsafeBuffer.class);

    @Test
    public void shouldScanEmptyBuffer() {
        final int offset = 0;
        final int limit = termBuffer.capacity();
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(newOffset, Matchers.is(offset));
    }

    @Test
    public void shouldReadFirstMessage() {
        final int offset = 0;
        final int limit = termBuffer.capacity();
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(newOffset, Matchers.is(alignedMessageLength));
    }

    @Test
    public void shouldReadBlockOfTwoMessages() {
        final int offset = 0;
        final int limit = termBuffer.capacity();
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(alignedMessageLength))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(alignedMessageLength))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(newOffset, Matchers.is((alignedMessageLength * 2)));
    }

    @Test
    public void shouldReadBlockOfThreeMessagesThatFillBuffer() {
        final int offset = 0;
        final int limit = termBuffer.capacity();
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        final int thirdMessageLength = limit - (alignedMessageLength * 2);
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(alignedMessageLength))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(alignedMessageLength))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset((alignedMessageLength * 2)))).thenReturn(thirdMessageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset((alignedMessageLength * 2)))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(newOffset, Matchers.is(limit));
    }

    @Test
    public void shouldReadBlockOfTwoMessagesBecauseOfLimit() {
        final int offset = 0;
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        final int limit = (alignedMessageLength * 2) + 1;
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(alignedMessageLength))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(alignedMessageLength))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset((alignedMessageLength * 2)))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset((alignedMessageLength * 2)))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(newOffset, Matchers.is((alignedMessageLength * 2)));
    }

    @Test
    public void shouldFailToReadFirstMessageBecauseOfLimit() {
        final int offset = 0;
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        final int limit = alignedMessageLength - 1;
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(newOffset, Matchers.is(offset));
    }

    @Test
    public void shouldReadOneMessageOnLimit() {
        final int offset = 0;
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        final int newOffset = TermBlockScanner.scan(termBuffer, offset, alignedMessageLength);
        Assert.assertThat(newOffset, Matchers.is(alignedMessageLength));
    }

    @Test
    public void shouldReadBlockOfOneMessageThenPadding() {
        final int offset = 0;
        final int limit = termBuffer.capacity();
        final int messageLength = 50;
        final int alignedMessageLength = BitUtil.align(messageLength, FrameDescriptor.FRAME_ALIGNMENT);
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(offset))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(offset))).thenReturn(((short) (HDR_TYPE_DATA)));
        Mockito.when(termBuffer.getIntVolatile(FrameDescriptor.lengthOffset(alignedMessageLength))).thenReturn(messageLength);
        Mockito.when(termBuffer.getShort(FrameDescriptor.typeOffset(alignedMessageLength))).thenReturn(((short) (HDR_TYPE_PAD)));
        final int firstOffset = TermBlockScanner.scan(termBuffer, offset, limit);
        Assert.assertThat(firstOffset, Matchers.is(alignedMessageLength));
        final int secondOffset = TermBlockScanner.scan(termBuffer, firstOffset, limit);
        Assert.assertThat(secondOffset, Matchers.is((alignedMessageLength * 2)));
    }
}

