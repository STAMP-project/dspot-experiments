/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker;


import TestUtils.INTS;
import TestUtils.NO_INTS;
import com.google.api.services.dataflow.model.ApproximateReportedProgress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.beam.runners.dataflow.worker.AvroByteReader.AvroByteFileIterator;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for AvroByteReader.
 */
// TODO: sharded filenames
// TODO: reading from GCS
@RunWith(JUnit4.class)
public class AvroByteReaderTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Class representing information about an Avro file generated from a list of elements.
     */
    private static class AvroFileInfo<T> {
        String filename;

        List<Integer> elementSizes = new ArrayList<>();

        List<Long> syncPoints = new ArrayList<>();

        long totalElementEncodedSize = 0;
    }

    @Test
    public void testRead() throws Exception {
        /* require exact match */
        runTestRead(Collections.singletonList(INTS), BigEndianIntegerCoder.of(), true);
    }

    @Test
    public void testReadEmpty() throws Exception {
        /* require exact match */
        runTestRead(Collections.singletonList(NO_INTS), BigEndianIntegerCoder.of(), true);
    }

    @Test
    public void testReadSmallRanges() throws Exception {
        /* require exact match */
        runTestRead(generateInputBlocks(3, 50, 5), StringUtf8Coder.of(), true);
    }

    @Test
    public void testReadBigRanges() throws Exception {
        /* don't require exact match */
        runTestRead(generateInputBlocks(10, (128 * 1024), 100), StringUtf8Coder.of(), false);
    }

    // Verification behavior for split requests. Used for testRequestDynamicSplitInternal.
    // Perform no verification.
    private static enum SplitVerificationBehavior {

        VERIFY_SUCCESS,
        // Split request must succeed.
        VERIFY_FAILURE,
        // Split request must fail.
        DO_NOT_VERIFY;}

    @Test
    public void testRequestDynamicSplit() throws Exception {
        // Note that exhaustive tests for AvroSource's split behavior exist in {@link AvroSourceTest}.
        List<List<String>> elements = generateInputBlocks(10, (100 * 100), 100);
        Coder<String> coder = StringUtf8Coder.of();
        AvroByteReaderTest.AvroFileInfo<String> fileInfo = initInputFile(elements, coder);
        AvroByteReader<String> reader = new AvroByteReader<String>(fileInfo.filename, 0L, Long.MAX_VALUE, coder, PipelineOptionsFactory.create());
        // Read most of the records before the proposed split point.
        testRequestDynamicSplitInternal(reader, 0.5F, 490, AvroByteReaderTest.SplitVerificationBehavior.VERIFY_SUCCESS);
        // Read a single record.
        testRequestDynamicSplitInternal(reader, 0.5F, 1, AvroByteReaderTest.SplitVerificationBehavior.VERIFY_SUCCESS);
        // Read zero records.
        testRequestDynamicSplitInternal(reader, 0.5F, 0, AvroByteReaderTest.SplitVerificationBehavior.VERIFY_FAILURE);
        // Read almost the entire input.
        testRequestDynamicSplitInternal(reader, 0.5F, 900, AvroByteReaderTest.SplitVerificationBehavior.VERIFY_FAILURE);
        // Read the entire input.
        testRequestDynamicSplitInternal(reader, 0.5F, 2000, AvroByteReaderTest.SplitVerificationBehavior.VERIFY_FAILURE);
    }

    @Test
    public void testRequestDynamicSplitExhaustive() throws Exception {
        List<List<String>> elements = generateInputBlocks(5, (10 * 10), 10);
        Coder<String> coder = StringUtf8Coder.of();
        AvroByteReaderTest.AvroFileInfo<String> fileInfo = initInputFile(elements, coder);
        AvroByteReader<String> reader = new AvroByteReader<String>(fileInfo.filename, 0L, Long.MAX_VALUE, coder, PipelineOptionsFactory.create());
        for (float splitFraction = 0.0F; splitFraction < 1.0F; splitFraction += 0.02F) {
            for (int recordsToRead = 0; recordsToRead <= 500; recordsToRead += 5) {
                testRequestDynamicSplitInternal(reader, splitFraction, recordsToRead, AvroByteReaderTest.SplitVerificationBehavior.DO_NOT_VERIFY);
            }
        }
    }

    @Test
    public void testGetProgress() throws Exception {
        // Ensure that AvroByteReader reports progress from the underlying AvroSource.
        // 4 blocks with 4 split points.
        List<List<String>> elements = generateInputBlocks(4, 10, 10);
        Coder<String> coder = StringUtf8Coder.of();
        AvroByteReaderTest.AvroFileInfo<String> fileInfo = initInputFile(elements, coder);
        AvroByteReader<String> reader = new AvroByteReader<String>(fileInfo.filename, 0L, Long.MAX_VALUE, coder, PipelineOptionsFactory.create());
        AvroByteFileIterator iterator = reader.iterator();
        Assert.assertTrue(iterator.start());
        ApproximateReportedProgress progress = SourceTranslationUtils.readerProgressToCloudProgress(reader.iterator().getProgress());
        Assert.assertEquals(0.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertEquals(0.0, progress.getFractionConsumed(), 1.0E-6);
        Assert.assertNull(progress.getRemainingParallelism());
        Assert.assertTrue(iterator.advance());
        progress = SourceTranslationUtils.readerProgressToCloudProgress(iterator.getProgress());
        Assert.assertEquals(1.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertNull(progress.getRemainingParallelism());
        // Advance to the end of last block and check consumed parallelism along the way.
        Assert.assertTrue(iterator.advance());
        progress = SourceTranslationUtils.readerProgressToCloudProgress(iterator.getProgress());
        Assert.assertEquals(2.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertNull(progress.getRemainingParallelism());
        Assert.assertTrue(iterator.advance());
        progress = SourceTranslationUtils.readerProgressToCloudProgress(iterator.getProgress());
        Assert.assertEquals(3.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertEquals(1.0, progress.getRemainingParallelism().getValue(), 1.0E-6);
        Assert.assertFalse(iterator.advance());
        progress = SourceTranslationUtils.readerProgressToCloudProgress(iterator.getProgress());
        Assert.assertEquals(4.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertEquals(0.0, progress.getRemainingParallelism().getValue(), 1.0E-6);
        Assert.assertEquals(1.0, progress.getFractionConsumed(), 1.0E-6);
    }
}

