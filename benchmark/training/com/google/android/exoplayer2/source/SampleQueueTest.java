/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.source;


import C.BUFFER_FLAG_KEY_FRAME;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link SampleQueue}.
 */
@RunWith(RobolectricTestRunner.class)
public final class SampleQueueTest {
    private static final int ALLOCATION_SIZE = 16;

    private static final Format FORMAT_1 = Format.createSampleFormat("1", "mimeType", 0);

    private static final Format FORMAT_2 = Format.createSampleFormat("2", "mimeType", 0);

    private static final Format FORMAT_1_COPY = Format.createSampleFormat("1", "mimeType", 0);

    private static final Format FORMAT_SPLICED = Format.createSampleFormat("spliced", "mimeType", 0);

    private static final byte[] DATA = TestUtil.buildTestData(((SampleQueueTest.ALLOCATION_SIZE) * 10));

    /* SAMPLE_SIZES and SAMPLE_OFFSETS are intended to test various boundary cases (with
    respect to the allocation size). SAMPLE_OFFSETS values are defined as the backward offsets
    (as expected by SampleQueue.sampleMetadata) assuming that DATA has been written to the
    sampleQueue in full. The allocations are filled as follows, where | indicates a boundary
    between allocations and x indicates a byte that doesn't belong to a sample:

    x<s1>|x<s2>x|x<s3>|<s4>x|<s5>|<s6|s6>|x<s7|s7>x|<s8>
     */
    private static final int[] SAMPLE_SIZES = new int[]{ (SampleQueueTest.ALLOCATION_SIZE) - 1, (SampleQueueTest.ALLOCATION_SIZE) - 2, (SampleQueueTest.ALLOCATION_SIZE) - 1, (SampleQueueTest.ALLOCATION_SIZE) - 1, SampleQueueTest.ALLOCATION_SIZE, (SampleQueueTest.ALLOCATION_SIZE) * 2, ((SampleQueueTest.ALLOCATION_SIZE) * 2) - 2, SampleQueueTest.ALLOCATION_SIZE };

    private static final int[] SAMPLE_OFFSETS = new int[]{ (SampleQueueTest.ALLOCATION_SIZE) * 9, ((SampleQueueTest.ALLOCATION_SIZE) * 8) + 1, (SampleQueueTest.ALLOCATION_SIZE) * 7, ((SampleQueueTest.ALLOCATION_SIZE) * 6) + 1, (SampleQueueTest.ALLOCATION_SIZE) * 5, (SampleQueueTest.ALLOCATION_SIZE) * 3, (SampleQueueTest.ALLOCATION_SIZE) + 1, 0 };

    private static final long[] SAMPLE_TIMESTAMPS = new long[]{ 0, 1000, 2000, 3000, 4000, 5000, 6000, 7000 };

    private static final long LAST_SAMPLE_TIMESTAMP = SampleQueueTest.SAMPLE_TIMESTAMPS[((SampleQueueTest.SAMPLE_TIMESTAMPS.length) - 1)];

    private static final int[] SAMPLE_FLAGS = new int[]{ C.BUFFER_FLAG_KEY_FRAME, 0, 0, 0, C.BUFFER_FLAG_KEY_FRAME, 0, 0, 0 };

    private static final Format[] SAMPLE_FORMATS = new Format[]{ SampleQueueTest.FORMAT_1, SampleQueueTest.FORMAT_1, SampleQueueTest.FORMAT_1, SampleQueueTest.FORMAT_1, SampleQueueTest.FORMAT_2, SampleQueueTest.FORMAT_2, SampleQueueTest.FORMAT_2, SampleQueueTest.FORMAT_2 };

    private static final int DATA_SECOND_KEYFRAME_INDEX = 4;

    private Allocator allocator;

    private SampleQueue sampleQueue;

    private FormatHolder formatHolder;

    private DecoderInputBuffer inputBuffer;

    @Test
    public void testResetReleasesAllocations() {
        writeTestData();
        assertAllocationCount(10);
        sampleQueue.reset();
        assertAllocationCount(0);
    }

    @Test
    public void testReadWithoutWrite() {
        assertNoSamplesToRead(null);
    }

    @Test
    public void testReadFormatDeduplicated() {
        sampleQueue.format(SampleQueueTest.FORMAT_1);
        assertReadFormat(false, SampleQueueTest.FORMAT_1);
        // If the same format is input then it should be de-duplicated (i.e. not output again).
        sampleQueue.format(SampleQueueTest.FORMAT_1);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_1);
        // The same applies for a format that's equal (but a different object).
        sampleQueue.format(SampleQueueTest.FORMAT_1_COPY);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_1);
    }

    @Test
    public void testReadSingleSamples() {
        sampleQueue.sampleData(new ParsableByteArray(SampleQueueTest.DATA), SampleQueueTest.ALLOCATION_SIZE);
        assertAllocationCount(1);
        // Nothing to read.
        assertNoSamplesToRead(null);
        sampleQueue.format(SampleQueueTest.FORMAT_1);
        // Read the format.
        assertReadFormat(false, SampleQueueTest.FORMAT_1);
        // Nothing to read.
        assertNoSamplesToRead(SampleQueueTest.FORMAT_1);
        sampleQueue.sampleMetadata(1000, BUFFER_FLAG_KEY_FRAME, SampleQueueTest.ALLOCATION_SIZE, 0, null);
        // If formatRequired, should read the format rather than the sample.
        assertReadFormat(true, SampleQueueTest.FORMAT_1);
        // Otherwise should read the sample.
        assertReadSample(1000, true, SampleQueueTest.DATA, 0, SampleQueueTest.ALLOCATION_SIZE);
        // Allocation should still be held.
        assertAllocationCount(1);
        sampleQueue.discardToRead();
        // The allocation should have been released.
        assertAllocationCount(0);
        // Nothing to read.
        assertNoSamplesToRead(SampleQueueTest.FORMAT_1);
        // Write a second sample followed by one byte that does not belong to it.
        sampleQueue.sampleData(new ParsableByteArray(SampleQueueTest.DATA), SampleQueueTest.ALLOCATION_SIZE);
        sampleQueue.sampleMetadata(2000, 0, ((SampleQueueTest.ALLOCATION_SIZE) - 1), 1, null);
        // If formatRequired, should read the format rather than the sample.
        assertReadFormat(true, SampleQueueTest.FORMAT_1);
        // Read the sample.
        assertReadSample(2000, false, SampleQueueTest.DATA, 0, ((SampleQueueTest.ALLOCATION_SIZE) - 1));
        // Allocation should still be held.
        assertAllocationCount(1);
        sampleQueue.discardToRead();
        // The last byte written to the sample queue may belong to a sample whose metadata has yet to be
        // written, so an allocation should still be held.
        assertAllocationCount(1);
        // Write metadata for a third sample containing the remaining byte.
        sampleQueue.sampleMetadata(3000, 0, 1, 0, null);
        // If formatRequired, should read the format rather than the sample.
        assertReadFormat(true, SampleQueueTest.FORMAT_1);
        // Read the sample.
        assertReadSample(3000, false, SampleQueueTest.DATA, ((SampleQueueTest.ALLOCATION_SIZE) - 1), 1);
        // Allocation should still be held.
        assertAllocationCount(1);
        sampleQueue.discardToRead();
        // The allocation should have been released.
        assertAllocationCount(0);
    }

    @Test
    public void testReadMultiSamples() {
        writeTestData();
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP);
        assertAllocationCount(10);
        assertReadTestData();
        assertAllocationCount(10);
        sampleQueue.discardToRead();
        assertAllocationCount(0);
    }

    @Test
    public void testReadMultiSamplesTwice() {
        writeTestData();
        writeTestData();
        assertAllocationCount(20);
        assertReadTestData(SampleQueueTest.FORMAT_2);
        assertReadTestData(SampleQueueTest.FORMAT_2);
        assertAllocationCount(20);
        sampleQueue.discardToRead();
        assertAllocationCount(0);
    }

    @Test
    public void testReadMultiWithRewind() {
        writeTestData();
        assertReadTestData();
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(0);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        assertAllocationCount(10);
        // Rewind.
        sampleQueue.rewind();
        assertAllocationCount(10);
        // Read again.
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(0);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(0);
        assertReadTestData();
    }

    @Test
    public void testRewindAfterDiscard() {
        writeTestData();
        assertReadTestData();
        sampleQueue.discardToRead();
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(8);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        assertAllocationCount(0);
        // Rewind.
        sampleQueue.rewind();
        assertAllocationCount(0);
        // Can't read again.
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(8);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        assertReadEndOfStream(false);
    }

    @Test
    public void testAdvanceToEnd() {
        writeTestData();
        sampleQueue.advanceToEnd();
        assertAllocationCount(10);
        sampleQueue.discardToRead();
        assertAllocationCount(0);
        // Despite skipping all samples, we should still read the last format, since this is the
        // expected format for a subsequent sample.
        assertReadFormat(false, SampleQueueTest.FORMAT_2);
        // Once the format has been read, there's nothing else to read.
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testAdvanceToEndRetainsUnassignedData() {
        sampleQueue.format(SampleQueueTest.FORMAT_1);
        sampleQueue.sampleData(new ParsableByteArray(SampleQueueTest.DATA), SampleQueueTest.ALLOCATION_SIZE);
        sampleQueue.advanceToEnd();
        assertAllocationCount(1);
        sampleQueue.discardToRead();
        // Skipping shouldn't discard data that may belong to a sample whose metadata has yet to be
        // written.
        assertAllocationCount(1);
        // We should be able to read the format.
        assertReadFormat(false, SampleQueueTest.FORMAT_1);
        // Once the format has been read, there's nothing else to read.
        assertNoSamplesToRead(SampleQueueTest.FORMAT_1);
        sampleQueue.sampleMetadata(0, BUFFER_FLAG_KEY_FRAME, SampleQueueTest.ALLOCATION_SIZE, 0, null);
        // Once the metadata has been written, check the sample can be read as expected.
        assertReadSample(0, true, SampleQueueTest.DATA, 0, SampleQueueTest.ALLOCATION_SIZE);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_1);
        assertAllocationCount(1);
        sampleQueue.discardToRead();
        assertAllocationCount(0);
    }

    @Test
    public void testAdvanceToBeforeBuffer() {
        writeTestData();
        int skipCount = sampleQueue.advanceTo(((SampleQueueTest.SAMPLE_TIMESTAMPS[0]) - 1), true, false);
        // Should fail and have no effect.
        assertThat(skipCount).isEqualTo(SampleQueue.ADVANCE_FAILED);
        assertReadTestData();
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testAdvanceToStartOfBuffer() {
        writeTestData();
        int skipCount = sampleQueue.advanceTo(SampleQueueTest.SAMPLE_TIMESTAMPS[0], true, false);
        // Should succeed but have no effect (we're already at the first frame).
        assertThat(skipCount).isEqualTo(0);
        assertReadTestData();
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testAdvanceToEndOfBuffer() {
        writeTestData();
        int skipCount = sampleQueue.advanceTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP, true, false);
        // Should succeed and skip to 2nd keyframe (the 4th frame).
        assertThat(skipCount).isEqualTo(4);
        assertReadTestData(null, SampleQueueTest.DATA_SECOND_KEYFRAME_INDEX);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testAdvanceToAfterBuffer() {
        writeTestData();
        int skipCount = sampleQueue.advanceTo(((SampleQueueTest.LAST_SAMPLE_TIMESTAMP) + 1), true, false);
        // Should fail and have no effect.
        assertThat(skipCount).isEqualTo(SampleQueue.ADVANCE_FAILED);
        assertReadTestData();
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testAdvanceToAfterBufferAllowed() {
        writeTestData();
        int skipCount = sampleQueue.advanceTo(((SampleQueueTest.LAST_SAMPLE_TIMESTAMP) + 1), true, true);
        // Should succeed and skip to 2nd keyframe (the 4th frame).
        assertThat(skipCount).isEqualTo(4);
        assertReadTestData(null, SampleQueueTest.DATA_SECOND_KEYFRAME_INDEX);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testDiscardToEnd() {
        writeTestData();
        // Should discard everything.
        sampleQueue.discardToEnd();
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(8);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        assertAllocationCount(0);
        // We should still be able to read the upstream format.
        assertReadFormat(false, SampleQueueTest.FORMAT_2);
        // We should be able to write and read subsequent samples.
        writeTestData();
        assertReadTestData(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testDiscardToStopAtReadPosition() {
        writeTestData();
        // Shouldn't discard anything.
        sampleQueue.discardTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP, false, true);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(0);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(0);
        assertAllocationCount(10);
        // Read the first sample.
        assertReadTestData(null, 0, 1);
        // Shouldn't discard anything.
        sampleQueue.discardTo(((SampleQueueTest.SAMPLE_TIMESTAMPS[1]) - 1), false, true);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(0);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(1);
        assertAllocationCount(10);
        // Should discard the read sample.
        sampleQueue.discardTo(SampleQueueTest.SAMPLE_TIMESTAMPS[1], false, true);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(1);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(1);
        assertAllocationCount(9);
        // Shouldn't discard anything.
        sampleQueue.discardTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP, false, true);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(1);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(1);
        assertAllocationCount(9);
        // Should be able to read the remaining samples.
        assertReadTestData(SampleQueueTest.FORMAT_1, 1, 7);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(1);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        // Should discard up to the second last sample
        sampleQueue.discardTo(((SampleQueueTest.LAST_SAMPLE_TIMESTAMP) - 1), false, true);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(6);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        assertAllocationCount(3);
        // Should discard up the last sample
        sampleQueue.discardTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP, false, true);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(7);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(8);
        assertAllocationCount(1);
    }

    @Test
    public void testDiscardToDontStopAtReadPosition() {
        writeTestData();
        // Shouldn't discard anything.
        sampleQueue.discardTo(((SampleQueueTest.SAMPLE_TIMESTAMPS[1]) - 1), false, false);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(0);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(0);
        assertAllocationCount(10);
        // Should discard the first sample.
        sampleQueue.discardTo(SampleQueueTest.SAMPLE_TIMESTAMPS[1], false, false);
        assertThat(sampleQueue.getFirstIndex()).isEqualTo(1);
        assertThat(sampleQueue.getReadIndex()).isEqualTo(1);
        assertAllocationCount(9);
        // Should be able to read the remaining samples.
        assertReadTestData(SampleQueueTest.FORMAT_1, 1, 7);
    }

    @Test
    public void testDiscardUpstream() {
        writeTestData();
        sampleQueue.discardUpstreamSamples(8);
        assertAllocationCount(10);
        sampleQueue.discardUpstreamSamples(7);
        assertAllocationCount(9);
        sampleQueue.discardUpstreamSamples(6);
        assertAllocationCount(7);
        sampleQueue.discardUpstreamSamples(5);
        assertAllocationCount(5);
        sampleQueue.discardUpstreamSamples(4);
        assertAllocationCount(4);
        sampleQueue.discardUpstreamSamples(3);
        assertAllocationCount(3);
        sampleQueue.discardUpstreamSamples(2);
        assertAllocationCount(2);
        sampleQueue.discardUpstreamSamples(1);
        assertAllocationCount(1);
        sampleQueue.discardUpstreamSamples(0);
        assertAllocationCount(0);
        assertReadFormat(false, SampleQueueTest.FORMAT_2);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testDiscardUpstreamMulti() {
        writeTestData();
        sampleQueue.discardUpstreamSamples(4);
        assertAllocationCount(4);
        sampleQueue.discardUpstreamSamples(0);
        assertAllocationCount(0);
        assertReadFormat(false, SampleQueueTest.FORMAT_2);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testDiscardUpstreamBeforeRead() {
        writeTestData();
        sampleQueue.discardUpstreamSamples(4);
        assertAllocationCount(4);
        assertReadTestData(null, 0, 4);
        assertReadFormat(false, SampleQueueTest.FORMAT_2);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testDiscardUpstreamAfterRead() {
        writeTestData();
        assertReadTestData(null, 0, 3);
        sampleQueue.discardUpstreamSamples(8);
        assertAllocationCount(10);
        sampleQueue.discardToRead();
        assertAllocationCount(7);
        sampleQueue.discardUpstreamSamples(7);
        assertAllocationCount(6);
        sampleQueue.discardUpstreamSamples(6);
        assertAllocationCount(4);
        sampleQueue.discardUpstreamSamples(5);
        assertAllocationCount(2);
        sampleQueue.discardUpstreamSamples(4);
        assertAllocationCount(1);
        sampleQueue.discardUpstreamSamples(3);
        assertAllocationCount(0);
        assertReadFormat(false, SampleQueueTest.FORMAT_2);
        assertNoSamplesToRead(SampleQueueTest.FORMAT_2);
    }

    @Test
    public void testLargestQueuedTimestampWithDiscardUpstream() {
        writeTestData();
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP);
        sampleQueue.discardUpstreamSamples(((SampleQueueTest.SAMPLE_TIMESTAMPS.length) - 1));
        // Discarding from upstream should reduce the largest timestamp.
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(SampleQueueTest.SAMPLE_TIMESTAMPS[((SampleQueueTest.SAMPLE_TIMESTAMPS.length) - 2)]);
        sampleQueue.discardUpstreamSamples(0);
        // Discarding everything from upstream without reading should unset the largest timestamp.
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testLargestQueuedTimestampWithDiscardUpstreamDecodeOrder() {
        long[] decodeOrderTimestamps = new long[]{ 0, 3000, 2000, 1000, 4000, 7000, 6000, 5000 };
        writeTestData(SampleQueueTest.DATA, SampleQueueTest.SAMPLE_SIZES, SampleQueueTest.SAMPLE_OFFSETS, decodeOrderTimestamps, SampleQueueTest.SAMPLE_FORMATS, SampleQueueTest.SAMPLE_FLAGS);
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(7000);
        sampleQueue.discardUpstreamSamples(((SampleQueueTest.SAMPLE_TIMESTAMPS.length) - 2));
        // Discarding the last two samples should not change the largest timestamp, due to the decode
        // ordering of the timestamps.
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(7000);
        sampleQueue.discardUpstreamSamples(((SampleQueueTest.SAMPLE_TIMESTAMPS.length) - 3));
        // Once a third sample is discarded, the largest timestamp should have changed.
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(4000);
        sampleQueue.discardUpstreamSamples(0);
        // Discarding everything from upstream without reading should unset the largest timestamp.
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testLargestQueuedTimestampWithRead() {
        writeTestData();
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP);
        assertReadTestData();
        // Reading everything should not reduce the largest timestamp.
        assertThat(sampleQueue.getLargestQueuedTimestampUs()).isEqualTo(SampleQueueTest.LAST_SAMPLE_TIMESTAMP);
    }

    @Test
    public void testSetSampleOffset() {
        long sampleOffsetUs = 1000;
        sampleQueue.setSampleOffsetUs(sampleOffsetUs);
        writeTestData();
        assertReadTestData(null, 0, 8, sampleOffsetUs);
        assertReadEndOfStream(false);
    }

    @Test
    public void testSplice() {
        writeTestData();
        sampleQueue.splice();
        // Splice should succeed, replacing the last 4 samples with the sample being written.
        long spliceSampleTimeUs = SampleQueueTest.SAMPLE_TIMESTAMPS[4];
        writeSample(SampleQueueTest.DATA, spliceSampleTimeUs, SampleQueueTest.FORMAT_SPLICED, BUFFER_FLAG_KEY_FRAME);
        assertReadTestData(null, 0, 4);
        assertReadFormat(false, SampleQueueTest.FORMAT_SPLICED);
        assertReadSample(spliceSampleTimeUs, true, SampleQueueTest.DATA, 0, SampleQueueTest.DATA.length);
        assertReadEndOfStream(false);
    }

    @Test
    public void testSpliceAfterRead() {
        writeTestData();
        assertReadTestData(null, 0, 4);
        sampleQueue.splice();
        // Splice should fail, leaving the last 4 samples unchanged.
        long spliceSampleTimeUs = SampleQueueTest.SAMPLE_TIMESTAMPS[3];
        writeSample(SampleQueueTest.DATA, spliceSampleTimeUs, SampleQueueTest.FORMAT_SPLICED, BUFFER_FLAG_KEY_FRAME);
        assertReadTestData(SampleQueueTest.SAMPLE_FORMATS[3], 4, 4);
        assertReadEndOfStream(false);
        sampleQueue.rewind();
        assertReadTestData(null, 0, 4);
        sampleQueue.splice();
        // Splice should succeed, replacing the last 4 samples with the sample being written
        spliceSampleTimeUs = (SampleQueueTest.SAMPLE_TIMESTAMPS[3]) + 1;
        writeSample(SampleQueueTest.DATA, spliceSampleTimeUs, SampleQueueTest.FORMAT_SPLICED, BUFFER_FLAG_KEY_FRAME);
        assertReadFormat(false, SampleQueueTest.FORMAT_SPLICED);
        assertReadSample(spliceSampleTimeUs, true, SampleQueueTest.DATA, 0, SampleQueueTest.DATA.length);
        assertReadEndOfStream(false);
    }

    @Test
    public void testSpliceWithSampleOffset() {
        long sampleOffsetUs = 30000;
        sampleQueue.setSampleOffsetUs(sampleOffsetUs);
        writeTestData();
        sampleQueue.splice();
        // Splice should succeed, replacing the last 4 samples with the sample being written.
        long spliceSampleTimeUs = SampleQueueTest.SAMPLE_TIMESTAMPS[4];
        writeSample(SampleQueueTest.DATA, spliceSampleTimeUs, SampleQueueTest.FORMAT_SPLICED, BUFFER_FLAG_KEY_FRAME);
        assertReadTestData(null, 0, 4, sampleOffsetUs);
        assertReadFormat(false, SampleQueueTest.FORMAT_SPLICED.copyWithSubsampleOffsetUs(sampleOffsetUs));
        assertReadSample((spliceSampleTimeUs + sampleOffsetUs), true, SampleQueueTest.DATA, 0, SampleQueueTest.DATA.length);
        assertReadEndOfStream(false);
    }
}

