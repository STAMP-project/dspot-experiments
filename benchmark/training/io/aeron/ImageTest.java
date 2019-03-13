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
package io.aeron;


import Action.ABORT;
import Action.BREAK;
import Action.COMMIT;
import Action.CONTINUE;
import io.aeron.logbuffer.DataHeaderFlyweight;
import io.aeron.logbuffer.ErrorHandler;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.logbuffer.Position;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static io.aeron.logbuffer.LogBufferDescriptor.LogBufferDescriptor.TERM_MIN_LENGTH;


public class ImageTest {
    private static final int TERM_BUFFER_LENGTH = TERM_MIN_LENGTH;

    private static final int POSITION_BITS_TO_SHIFT = LogBufferDescriptor.LogBufferDescriptor.positionBitsToShift(ImageTest.TERM_BUFFER_LENGTH);

    private static final byte[] DATA = new byte[36];

    static {
        for (int i = 0; i < (ImageTest.DATA.length); i++) {
            ImageTest.DATA[i] = ((byte) (i));
        }
    }

    private static final long CORRELATION_ID = 201608730L;

    private static final int SESSION_ID = 1582632989;

    private static final int STREAM_ID = 802830;

    private static final String SOURCE_IDENTITY = "ipc";

    private static final int INITIAL_TERM_ID = 976925;

    private static final int MESSAGE_LENGTH = (HEADER_LENGTH) + (ImageTest.DATA.length);

    private static final int ALIGNED_FRAME_LENGTH = align(ImageTest.MESSAGE_LENGTH, FrameDescriptor.FRAME_ALIGNMENT);

    private final UnsafeBuffer rcvBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ImageTest.ALIGNED_FRAME_LENGTH));

    private final DataHeaderFlyweight dataHeader = new DataHeaderFlyweight();

    private final FragmentHandler mockFragmentHandler = Mockito.mock(FragmentHandler.class);

    private final ControlledFragmentHandler mockControlledFragmentHandler = Mockito.mock(ControlledFragmentHandler.class);

    private final Position position = Mockito.spy(new AtomicLongPosition());

    private final LogBuffers logBuffers = Mockito.mock(LogBuffers.class);

    private final ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);

    private final Subscription subscription = Mockito.mock(Subscription.class);

    private final UnsafeBuffer[] termBuffers = new UnsafeBuffer[PARTITION_COUNT];

    @Test
    public void shouldHandleClosedImage() {
        final Image image = createImage();
        image.close();
        Assert.assertTrue(image.isClosed());
        MatcherAssert.assertThat(image.poll(mockFragmentHandler, Integer.MAX_VALUE), Matchers.is(0));
        MatcherAssert.assertThat(image.position(), Matchers.is(0L));
    }

    @Test
    public void shouldAllowValidPosition() {
        final Image image = createImage();
        final long expectedPosition = (ImageTest.TERM_BUFFER_LENGTH) - 32;
        position.setOrdered(expectedPosition);
        MatcherAssert.assertThat(image.position(), Matchers.is(expectedPosition));
        image.position(ImageTest.TERM_BUFFER_LENGTH);
        MatcherAssert.assertThat(image.position(), Matchers.is(((long) (ImageTest.TERM_BUFFER_LENGTH))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAdvancePastEndOfTerm() {
        final Image image = createImage();
        final long expectedPosition = (ImageTest.TERM_BUFFER_LENGTH) - 32;
        position.setOrdered(expectedPosition);
        MatcherAssert.assertThat(image.position(), Matchers.is(expectedPosition));
        image.position(((ImageTest.TERM_BUFFER_LENGTH) + 32));
    }

    @Test
    public void shouldReportCorrectPositionOnReception() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        final int messages = image.poll(mockFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(messages, Matchers.is(1));
        Mockito.verify(mockFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        final InOrder inOrder = Mockito.inOrder(position);
        inOrder.verify(position).setOrdered(initialPosition);
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
    }

    @Test
    public void shouldReportCorrectPositionOnReceptionWithNonZeroPositionInInitialTermId() {
        final int initialMessageIndex = 5;
        final int initialTermOffset = ImageTest.offsetForFrame(initialMessageIndex);
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, initialTermOffset, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(initialMessageIndex));
        final int messages = image.poll(mockFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(messages, Matchers.is(1));
        Mockito.verify(mockFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq((initialTermOffset + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        final InOrder inOrder = Mockito.inOrder(position);
        inOrder.verify(position).setOrdered(initialPosition);
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
    }

    @Test
    public void shouldReportCorrectPositionOnReceptionWithNonZeroPositionInNonInitialTermId() {
        final int activeTermId = (ImageTest.INITIAL_TERM_ID) + 1;
        final int initialMessageIndex = 5;
        final int initialTermOffset = ImageTest.offsetForFrame(initialMessageIndex);
        final long initialPosition = computePosition(activeTermId, initialTermOffset, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(activeTermId, ImageTest.offsetForFrame(initialMessageIndex));
        final int messages = image.poll(mockFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(messages, Matchers.is(1));
        Mockito.verify(mockFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq((initialTermOffset + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        final InOrder inOrder = Mockito.inOrder(position);
        inOrder.verify(position).setOrdered(initialPosition);
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
    }

    @Test
    public void shouldPollNoFragmentsToControlledFragmentHandler() {
        final Image image = createImage();
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(0));
        Mockito.verify(position, Mockito.never()).setOrdered(ArgumentMatchers.anyLong());
        Mockito.verify(mockControlledFragmentHandler, Mockito.never()).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldPollOneFragmentToControlledFragmentHandlerOnContinue() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(1));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
    }

    @Test
    public void shouldUpdatePositionOnRethrownExceptionInControlledPoll() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenThrow(new RuntimeException());
        Mockito.doThrow(new RuntimeException()).when(errorHandler).onError(ArgumentMatchers.any());
        boolean thrown = false;
        try {
            image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        } catch (final Exception ignore) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        MatcherAssert.assertThat(image.position(), Matchers.is((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH))));
        Mockito.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldUpdatePositionOnRethrownExceptionInPoll() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        Mockito.doThrow(new RuntimeException()).when(mockFragmentHandler).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class));
        Mockito.doThrow(new RuntimeException()).when(errorHandler).onError(ArgumentMatchers.any());
        boolean thrown = false;
        try {
            image.poll(mockFragmentHandler, Integer.MAX_VALUE);
        } catch (final Exception ignore) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        MatcherAssert.assertThat(image.position(), Matchers.is((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH))));
        Mockito.verify(mockFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldNotPollOneFragmentToControlledFragmentHandlerOnAbort() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(ABORT);
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(0));
        MatcherAssert.assertThat(image.position(), Matchers.is(initialPosition));
        Mockito.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldPollOneFragmentToControlledFragmentHandlerOnBreak() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(BREAK);
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(1));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
    }

    @Test
    public void shouldPollFragmentsToControlledFragmentHandlerOnCommit() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(COMMIT);
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(2));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(((ImageTest.ALIGNED_FRAME_LENGTH) + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + ((ImageTest.ALIGNED_FRAME_LENGTH) * 2)));
    }

    @Test
    public void shouldUpdatePositionToEndOfCommittedFragmentOnCommit() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(2));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE, COMMIT, CONTINUE);
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(3));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        // first fragment, continue
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        // second fragment, commit
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(((ImageTest.ALIGNED_FRAME_LENGTH) + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + ((ImageTest.ALIGNED_FRAME_LENGTH) * 2)));
        // third fragment, continue, but position is updated because last
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(((2 * (ImageTest.ALIGNED_FRAME_LENGTH)) + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + ((ImageTest.ALIGNED_FRAME_LENGTH) * 3)));
    }

    @Test
    public void shouldPollFragmentsToControlledFragmentHandlerOnContinue() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.controlledPoll(mockControlledFragmentHandler, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(2));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(((ImageTest.ALIGNED_FRAME_LENGTH) + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + ((ImageTest.ALIGNED_FRAME_LENGTH) * 2)));
    }

    @Test
    public void shouldPollNoFragmentsToBoundedControlledFragmentHandlerWithMaxPositionBeforeInitialPosition() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        final long maxPosition = initialPosition - (HEADER_LENGTH);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.boundedControlledPoll(mockControlledFragmentHandler, maxPosition, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(0));
        MatcherAssert.assertThat(position.get(), Matchers.is(initialPosition));
        Mockito.verify(mockControlledFragmentHandler, Mockito.never()).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldPollNoFragmentsToBoundedControlledFragmentHandlerWithInitialOffsetNotZero() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1), ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        final long maxPosition = initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(2));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.boundedControlledPoll(mockControlledFragmentHandler, maxPosition, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(1));
        MatcherAssert.assertThat(position.get(), Matchers.is(maxPosition));
        Mockito.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldPollFragmentsToBoundedControlledFragmentHandlerWithMaxPositionBeforeNextMessage() {
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, 0, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        final long maxPosition = initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(0));
        insertDataFrame(ImageTest.INITIAL_TERM_ID, ImageTest.offsetForFrame(1));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.boundedControlledPoll(mockControlledFragmentHandler, maxPosition, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(1));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered((initialPosition + (ImageTest.ALIGNED_FRAME_LENGTH)));
    }

    @Test
    public void shouldPollFragmentsToBoundedControlledFragmentHandlerWithMaxPositionAfterEndOfTerm() {
        final int initialOffset = (ImageTest.TERM_BUFFER_LENGTH) - ((ImageTest.ALIGNED_FRAME_LENGTH) * 2);
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, initialOffset, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        final long maxPosition = initialPosition + (ImageTest.TERM_BUFFER_LENGTH);
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, initialOffset);
        insertPaddingFrame(ImageTest.INITIAL_TERM_ID, (initialOffset + (ImageTest.ALIGNED_FRAME_LENGTH)));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.boundedControlledPoll(mockControlledFragmentHandler, maxPosition, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(1));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq((initialOffset + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered(ImageTest.TERM_BUFFER_LENGTH);
    }

    @Test
    public void shouldPollFragmentsToBoundedControlledFragmentHandlerWithMaxPositionAboveIntMaxValue() {
        final int initialOffset = (ImageTest.TERM_BUFFER_LENGTH) - ((ImageTest.ALIGNED_FRAME_LENGTH) * 2);
        final long initialPosition = computePosition(ImageTest.INITIAL_TERM_ID, initialOffset, ImageTest.POSITION_BITS_TO_SHIFT, ImageTest.INITIAL_TERM_ID);
        final long maxPosition = ((long) (Integer.MAX_VALUE)) + 1000;
        position.setOrdered(initialPosition);
        final Image image = createImage();
        insertDataFrame(ImageTest.INITIAL_TERM_ID, initialOffset);
        insertPaddingFrame(ImageTest.INITIAL_TERM_ID, (initialOffset + (ImageTest.ALIGNED_FRAME_LENGTH)));
        Mockito.when(mockControlledFragmentHandler.onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Header.class))).thenReturn(CONTINUE);
        final int fragmentsRead = image.boundedControlledPoll(mockControlledFragmentHandler, maxPosition, Integer.MAX_VALUE);
        MatcherAssert.assertThat(fragmentsRead, Matchers.is(1));
        final InOrder inOrder = Mockito.inOrder(position, mockControlledFragmentHandler);
        inOrder.verify(mockControlledFragmentHandler).onFragment(ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.eq((initialOffset + (HEADER_LENGTH))), ArgumentMatchers.eq(ImageTest.DATA.length), ArgumentMatchers.any(Header.class));
        inOrder.verify(position).setOrdered(ImageTest.TERM_BUFFER_LENGTH);
    }
}

