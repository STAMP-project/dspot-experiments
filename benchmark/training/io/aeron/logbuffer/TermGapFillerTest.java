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
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TermGapFillerTest {
    private static final int INITIAL_TERM_ID = 11;

    private static final int TERM_ID = 22;

    private static final int SESSION_ID = 333;

    private static final int STREAM_ID = 7;

    private final UnsafeBuffer metaDataBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(LOG_META_DATA_LENGTH));

    private final UnsafeBuffer termBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(TERM_MIN_LENGTH));

    private final DataHeaderFlyweight dataFlyweight = new DataHeaderFlyweight(termBuffer);

    @Test
    public void shouldFillGapAtBeginningOfTerm() {
        final int gapOffset = 0;
        final int gapLength = 64;
        Assert.assertTrue(TermGapFiller.tryFillGap(metaDataBuffer, termBuffer, TermGapFillerTest.TERM_ID, gapOffset, gapLength));
        Assert.assertThat(dataFlyweight.frameLength(), CoreMatchers.is(gapLength));
        Assert.assertThat(dataFlyweight.termOffset(), CoreMatchers.is(gapOffset));
        Assert.assertThat(dataFlyweight.sessionId(), CoreMatchers.is(TermGapFillerTest.SESSION_ID));
        Assert.assertThat(dataFlyweight.termId(), CoreMatchers.is(TermGapFillerTest.TERM_ID));
        Assert.assertThat(dataFlyweight.headerType(), CoreMatchers.is(FrameDescriptor.PADDING_FRAME_TYPE));
        Assert.assertThat(((byte) (dataFlyweight.flags())), CoreMatchers.is(FrameDescriptor.UNFRAGMENTED));
    }

    @Test
    public void shouldNotOverwriteExistingFrame() {
        final int gapOffset = 0;
        final int gapLength = 64;
        dataFlyweight.frameLength(32);
        Assert.assertFalse(TermGapFiller.tryFillGap(metaDataBuffer, termBuffer, TermGapFillerTest.TERM_ID, gapOffset, gapLength));
    }

    @Test
    public void shouldFillGapAfterExistingFrame() {
        final int gapOffset = 128;
        final int gapLength = 64;
        dataFlyweight.sessionId(TermGapFillerTest.SESSION_ID).termId(TermGapFillerTest.TERM_ID).streamId(TermGapFillerTest.STREAM_ID).flags(FrameDescriptor.UNFRAGMENTED).frameLength(gapOffset);
        dataFlyweight.setMemory(0, (gapOffset - (DataHeaderFlyweight.HEADER_LENGTH)), ((byte) ('x')));
        Assert.assertTrue(TermGapFiller.tryFillGap(metaDataBuffer, termBuffer, TermGapFillerTest.TERM_ID, gapOffset, gapLength));
        dataFlyweight.wrap(termBuffer, gapOffset, ((termBuffer.capacity()) - gapOffset));
        Assert.assertThat(dataFlyweight.frameLength(), CoreMatchers.is(gapLength));
        Assert.assertThat(dataFlyweight.termOffset(), CoreMatchers.is(gapOffset));
        Assert.assertThat(dataFlyweight.sessionId(), CoreMatchers.is(TermGapFillerTest.SESSION_ID));
        Assert.assertThat(dataFlyweight.termId(), CoreMatchers.is(TermGapFillerTest.TERM_ID));
        Assert.assertThat(dataFlyweight.headerType(), CoreMatchers.is(FrameDescriptor.PADDING_FRAME_TYPE));
        Assert.assertThat(((byte) (dataFlyweight.flags())), CoreMatchers.is(FrameDescriptor.UNFRAGMENTED));
    }

    @Test
    public void shouldFillGapBetweenExistingFrames() {
        final int gapOffset = 128;
        final int gapLength = 64;
        dataFlyweight.sessionId(TermGapFillerTest.SESSION_ID).termId(TermGapFillerTest.TERM_ID).termOffset(0).streamId(TermGapFillerTest.STREAM_ID).flags(FrameDescriptor.UNFRAGMENTED).frameLength(gapOffset).setMemory(0, (gapOffset - (DataHeaderFlyweight.HEADER_LENGTH)), ((byte) ('x')));
        final int secondExistingFrameOffset = gapOffset + gapLength;
        dataFlyweight.wrap(termBuffer, secondExistingFrameOffset, ((termBuffer.capacity()) - secondExistingFrameOffset));
        dataFlyweight.sessionId(TermGapFillerTest.SESSION_ID).termId(TermGapFillerTest.TERM_ID).termOffset(secondExistingFrameOffset).streamId(TermGapFillerTest.STREAM_ID).flags(FrameDescriptor.UNFRAGMENTED).frameLength(64);
        Assert.assertTrue(TermGapFiller.tryFillGap(metaDataBuffer, termBuffer, TermGapFillerTest.TERM_ID, gapOffset, gapLength));
        dataFlyweight.wrap(termBuffer, gapOffset, ((termBuffer.capacity()) - gapOffset));
        Assert.assertThat(dataFlyweight.frameLength(), CoreMatchers.is(gapLength));
        Assert.assertThat(dataFlyweight.termOffset(), CoreMatchers.is(gapOffset));
        Assert.assertThat(dataFlyweight.sessionId(), CoreMatchers.is(TermGapFillerTest.SESSION_ID));
        Assert.assertThat(dataFlyweight.termId(), CoreMatchers.is(TermGapFillerTest.TERM_ID));
        Assert.assertThat(dataFlyweight.headerType(), CoreMatchers.is(FrameDescriptor.PADDING_FRAME_TYPE));
        Assert.assertThat(((byte) (dataFlyweight.flags())), CoreMatchers.is(FrameDescriptor.UNFRAGMENTED));
    }

    @Test
    public void shouldFillGapAtEndOfTerm() {
        final int gapOffset = (termBuffer.capacity()) - 64;
        final int gapLength = 64;
        dataFlyweight.sessionId(TermGapFillerTest.SESSION_ID).termId(TermGapFillerTest.TERM_ID).streamId(TermGapFillerTest.STREAM_ID).flags(FrameDescriptor.UNFRAGMENTED).frameLength(((termBuffer.capacity()) - gapOffset));
        dataFlyweight.setMemory(0, (gapOffset - (DataHeaderFlyweight.HEADER_LENGTH)), ((byte) ('x')));
        Assert.assertTrue(TermGapFiller.tryFillGap(metaDataBuffer, termBuffer, TermGapFillerTest.TERM_ID, gapOffset, gapLength));
        dataFlyweight.wrap(termBuffer, gapOffset, ((termBuffer.capacity()) - gapOffset));
        Assert.assertThat(dataFlyweight.frameLength(), CoreMatchers.is(gapLength));
        Assert.assertThat(dataFlyweight.termOffset(), CoreMatchers.is(gapOffset));
        Assert.assertThat(dataFlyweight.sessionId(), CoreMatchers.is(TermGapFillerTest.SESSION_ID));
        Assert.assertThat(dataFlyweight.termId(), CoreMatchers.is(TermGapFillerTest.TERM_ID));
        Assert.assertThat(dataFlyweight.headerType(), CoreMatchers.is(FrameDescriptor.PADDING_FRAME_TYPE));
        Assert.assertThat(((byte) (dataFlyweight.flags())), CoreMatchers.is(FrameDescriptor.UNFRAGMENTED));
    }
}

