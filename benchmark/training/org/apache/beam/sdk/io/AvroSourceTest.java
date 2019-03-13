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
package org.apache.beam.sdk.io;


import BoundedReader.SPLIT_POINTS_UNKNOWN;
import DataFileConstants.NULL_CODEC;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileConstants;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.ReflectData;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.io.AvroSource.AvroMetadata;
import org.apache.beam.sdk.io.AvroSource.AvroReader.Seeker;
import org.apache.beam.sdk.io.BlockBasedSource.BlockBasedReader;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.sdk.util.SerializableUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for AvroSource.
 */
@RunWith(JUnit4.class)
public class AvroSourceTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // Sync at default intervals (i.e., no manual syncing).
    private enum SyncBehavior {

        SYNC_REGULAR,
        // Sync at regular, user defined intervals
        SYNC_RANDOM,
        // Sync at random intervals
        SYNC_DEFAULT;}

    private static final int DEFAULT_RECORD_COUNT = 1000;

    @Test
    public void testReadWithDifferentCodecs() throws Exception {
        // Test reading files generated using all codecs.
        String[] codecs = new String[]{ DataFileConstants.NULL_CODEC, DataFileConstants.BZIP2_CODEC, DataFileConstants.DEFLATE_CODEC, DataFileConstants.SNAPPY_CODEC, DataFileConstants.XZ_CODEC };
        // As Avro's default block size is 64KB, write 64K records to ensure at least one full block.
        // We could make this smaller than 64KB assuming each record is at least B bytes, but then the
        // test could silently stop testing the failure condition from BEAM-422.
        List<AvroSourceTest.Bird> expected = AvroSourceTest.createRandomRecords((1 << 16));
        for (String codec : codecs) {
            String filename = generateTestFile(codec, expected, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), codec);
            AvroSource<AvroSourceTest.Bird> source = AvroSource.from(filename).withSchema(AvroSourceTest.Bird.class);
            List<AvroSourceTest.Bird> actual = SourceTestUtils.readFromSource(source, null);
            Assert.assertThat(expected, Matchers.containsInAnyOrder(actual.toArray()));
        }
    }

    @Test
    public void testSplitAtFraction() throws Exception {
        // A reduced dataset is enough here.
        List<AvroSourceTest.FixedRecord> expected = AvroSourceTest.createFixedRecords(AvroSourceTest.DEFAULT_RECORD_COUNT);
        // Create an AvroSource where each block is 1/10th of the total set of records.
        String filename = /* max records per block */
        generateTestFile("tmp.avro", expected, AvroSourceTest.SyncBehavior.SYNC_REGULAR, ((AvroSourceTest.DEFAULT_RECORD_COUNT) / 10), AvroCoder.of(AvroSourceTest.FixedRecord.class), NULL_CODEC);
        File file = new File(filename);
        AvroSource<AvroSourceTest.FixedRecord> source = AvroSource.from(filename).withSchema(AvroSourceTest.FixedRecord.class);
        List<? extends BoundedSource<AvroSourceTest.FixedRecord>> splits = source.split(((file.length()) / 3), null);
        for (BoundedSource<AvroSourceTest.FixedRecord> subSource : splits) {
            int items = SourceTestUtils.readFromSource(subSource, null).size();
            // Shouldn't split while unstarted.
            SourceTestUtils.assertSplitAtFractionFails(subSource, 0, 0.0, null);
            SourceTestUtils.assertSplitAtFractionFails(subSource, 0, 0.7, null);
            SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(subSource, 1, 0.7, null);
            SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(subSource, ((AvroSourceTest.DEFAULT_RECORD_COUNT) / 100), 0.7, null);
            SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(subSource, ((AvroSourceTest.DEFAULT_RECORD_COUNT) / 10), 0.1, null);
            SourceTestUtils.assertSplitAtFractionFails(subSource, (((AvroSourceTest.DEFAULT_RECORD_COUNT) / 10) + 1), 0.1, null);
            SourceTestUtils.assertSplitAtFractionFails(subSource, ((AvroSourceTest.DEFAULT_RECORD_COUNT) / 3), 0.3, null);
            SourceTestUtils.assertSplitAtFractionFails(subSource, items, 0.9, null);
            SourceTestUtils.assertSplitAtFractionFails(subSource, items, 1.0, null);
            SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(subSource, items, 0.999, null);
        }
    }

    @Test
    public void testGetProgressFromUnstartedReader() throws Exception {
        List<AvroSourceTest.FixedRecord> records = AvroSourceTest.createFixedRecords(AvroSourceTest.DEFAULT_RECORD_COUNT);
        String filename = generateTestFile("tmp.avro", records, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 1000, AvroCoder.of(AvroSourceTest.FixedRecord.class), NULL_CODEC);
        File file = new File(filename);
        AvroSource<AvroSourceTest.FixedRecord> source = AvroSource.from(filename).withSchema(AvroSourceTest.FixedRecord.class);
        try (BoundedSource.BoundedReader<AvroSourceTest.FixedRecord> reader = source.createReader(null)) {
            Assert.assertEquals(Double.valueOf(0.0), reader.getFractionConsumed());
        }
        List<? extends BoundedSource<AvroSourceTest.FixedRecord>> splits = source.split(((file.length()) / 3), null);
        for (BoundedSource<AvroSourceTest.FixedRecord> subSource : splits) {
            try (BoundedSource.BoundedReader<AvroSourceTest.FixedRecord> reader = subSource.createReader(null)) {
                Assert.assertEquals(Double.valueOf(0.0), reader.getFractionConsumed());
            }
        }
    }

    @Test
    public void testProgress() throws Exception {
        // 5 records, 2 per block.
        List<AvroSourceTest.FixedRecord> records = AvroSourceTest.createFixedRecords(5);
        String filename = generateTestFile("tmp.avro", records, AvroSourceTest.SyncBehavior.SYNC_REGULAR, 2, AvroCoder.of(AvroSourceTest.FixedRecord.class), NULL_CODEC);
        AvroSource<AvroSourceTest.FixedRecord> source = AvroSource.from(filename).withSchema(AvroSourceTest.FixedRecord.class);
        try (BoundedSource.BoundedReader<AvroSourceTest.FixedRecord> readerOrig = source.createReader(null)) {
            Assert.assertThat(readerOrig, Matchers.instanceOf(BlockBasedReader.class));
            BlockBasedReader<AvroSourceTest.FixedRecord> reader = ((BlockBasedReader<AvroSourceTest.FixedRecord>) (readerOrig));
            // Before starting
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // First 2 records are in the same block.
            Assert.assertTrue(reader.start());
            Assert.assertTrue(reader.isAtSplitPoint());
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // continued
            Assert.assertTrue(reader.advance());
            Assert.assertFalse(reader.isAtSplitPoint());
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // Second block -> parallelism consumed becomes 1.
            Assert.assertTrue(reader.advance());
            Assert.assertTrue(reader.isAtSplitPoint());
            Assert.assertEquals(1, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // continued
            Assert.assertTrue(reader.advance());
            Assert.assertFalse(reader.isAtSplitPoint());
            Assert.assertEquals(1, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // Third and final block -> parallelism consumed becomes 2, remaining becomes 1.
            Assert.assertTrue(reader.advance());
            Assert.assertTrue(reader.isAtSplitPoint());
            Assert.assertEquals(2, reader.getSplitPointsConsumed());
            Assert.assertEquals(1, reader.getSplitPointsRemaining());
            // Done
            Assert.assertFalse(reader.advance());
            Assert.assertEquals(3, reader.getSplitPointsConsumed());
            Assert.assertEquals(0, reader.getSplitPointsRemaining());
            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
        }
    }

    @Test
    public void testProgressEmptySource() throws Exception {
        // 0 records, 20 per block.
        List<AvroSourceTest.FixedRecord> records = Collections.emptyList();
        String filename = generateTestFile("tmp.avro", records, AvroSourceTest.SyncBehavior.SYNC_REGULAR, 2, AvroCoder.of(AvroSourceTest.FixedRecord.class), NULL_CODEC);
        AvroSource<AvroSourceTest.FixedRecord> source = AvroSource.from(filename).withSchema(AvroSourceTest.FixedRecord.class);
        try (BoundedSource.BoundedReader<AvroSourceTest.FixedRecord> readerOrig = source.createReader(null)) {
            Assert.assertThat(readerOrig, Matchers.instanceOf(BlockBasedReader.class));
            BlockBasedReader<AvroSourceTest.FixedRecord> reader = ((BlockBasedReader<AvroSourceTest.FixedRecord>) (readerOrig));
            // before starting
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // confirm empty
            Assert.assertFalse(reader.start());
            // after reading empty source
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(0, reader.getSplitPointsRemaining());
            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
        }
    }

    @Test
    public void testGetCurrentFromUnstartedReader() throws Exception {
        List<AvroSourceTest.FixedRecord> records = AvroSourceTest.createFixedRecords(AvroSourceTest.DEFAULT_RECORD_COUNT);
        String filename = generateTestFile("tmp.avro", records, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 1000, AvroCoder.of(AvroSourceTest.FixedRecord.class), NULL_CODEC);
        AvroSource<AvroSourceTest.FixedRecord> source = AvroSource.from(filename).withSchema(AvroSourceTest.FixedRecord.class);
        try (BlockBasedSource.BlockBasedReader<AvroSourceTest.FixedRecord> reader = ((BlockBasedSource.BlockBasedReader<AvroSourceTest.FixedRecord>) (source.createReader(null)))) {
            Assert.assertEquals(null, reader.getCurrentBlock());
            expectedException.expect(NoSuchElementException.class);
            expectedException.expectMessage("No block has been successfully read from");
            reader.getCurrent();
        }
    }

    @Test
    public void testSplitAtFractionExhaustive() throws Exception {
        // A small-sized input is sufficient, because the test verifies that splitting is non-vacuous.
        List<AvroSourceTest.FixedRecord> expected = AvroSourceTest.createFixedRecords(20);
        String filename = generateTestFile("tmp.avro", expected, AvroSourceTest.SyncBehavior.SYNC_REGULAR, 5, AvroCoder.of(AvroSourceTest.FixedRecord.class), NULL_CODEC);
        AvroSource<AvroSourceTest.FixedRecord> source = AvroSource.from(filename).withSchema(AvroSourceTest.FixedRecord.class);
        SourceTestUtils.assertSplitAtFractionExhaustive(source, null);
    }

    @Test
    public void testSplitsWithSmallBlocks() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        // Test reading from an object file with many small random-sized blocks.
        // The file itself doesn't have to be big; we can use a decreased record count.
        List<AvroSourceTest.Bird> expected = AvroSourceTest.createRandomRecords(AvroSourceTest.DEFAULT_RECORD_COUNT);
        String filename = /* max records/block */
        generateTestFile("tmp.avro", expected, AvroSourceTest.SyncBehavior.SYNC_RANDOM, ((AvroSourceTest.DEFAULT_RECORD_COUNT) / 20), AvroCoder.of(AvroSourceTest.Bird.class), NULL_CODEC);
        File file = new File(filename);
        // Small minimum bundle size
        AvroSource<AvroSourceTest.Bird> source = AvroSource.from(filename).withSchema(AvroSourceTest.Bird.class).withMinBundleSize(100L);
        // Assert that the source produces the expected records
        Assert.assertEquals(expected, SourceTestUtils.readFromSource(source, options));
        List<? extends BoundedSource<AvroSourceTest.Bird>> splits;
        int nonEmptySplits;
        // Split with the minimum bundle size
        splits = source.split(100L, options);
        Assert.assertTrue(((splits.size()) > 2));
        SourceTestUtils.assertSourcesEqualReferenceSource(source, splits, options);
        nonEmptySplits = 0;
        for (BoundedSource<AvroSourceTest.Bird> subSource : splits) {
            if ((SourceTestUtils.readFromSource(subSource, options).size()) > 0) {
                nonEmptySplits += 1;
            }
        }
        Assert.assertTrue((nonEmptySplits > 2));
        // Split with larger bundle size
        splits = source.split(((file.length()) / 4), options);
        Assert.assertTrue(((splits.size()) > 2));
        SourceTestUtils.assertSourcesEqualReferenceSource(source, splits, options);
        nonEmptySplits = 0;
        for (BoundedSource<AvroSourceTest.Bird> subSource : splits) {
            if ((SourceTestUtils.readFromSource(subSource, options).size()) > 0) {
                nonEmptySplits += 1;
            }
        }
        Assert.assertTrue((nonEmptySplits > 2));
        // Split with the file length
        splits = source.split(file.length(), options);
        Assert.assertTrue(((splits.size()) == 1));
        SourceTestUtils.assertSourcesEqualReferenceSource(source, splits, options);
    }

    @Test
    public void testMultipleFiles() throws Exception {
        String baseName = "tmp-";
        List<AvroSourceTest.Bird> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<AvroSourceTest.Bird> contents = AvroSourceTest.createRandomRecords(((AvroSourceTest.DEFAULT_RECORD_COUNT) / 10));
            expected.addAll(contents);
            generateTestFile((baseName + i), contents, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), NULL_CODEC);
        }
        AvroSource<AvroSourceTest.Bird> source = AvroSource.from(new File(tmpFolder.getRoot().toString(), (baseName + "*")).toString()).withSchema(AvroSourceTest.Bird.class);
        List<AvroSourceTest.Bird> actual = SourceTestUtils.readFromSource(source, null);
        Assert.assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testCreationWithSchema() throws Exception {
        List<AvroSourceTest.Bird> expected = AvroSourceTest.createRandomRecords(100);
        String filename = generateTestFile("tmp.avro", expected, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), NULL_CODEC);
        // Create a source with a schema object
        Schema schema = ReflectData.get().getSchema(AvroSourceTest.Bird.class);
        AvroSource<GenericRecord> source = AvroSource.from(filename).withSchema(schema);
        List<GenericRecord> records = SourceTestUtils.readFromSource(source, null);
        assertEqualsWithGeneric(expected, records);
        // Create a source with a JSON schema
        String schemaString = ReflectData.get().getSchema(AvroSourceTest.Bird.class).toString();
        source = AvroSource.from(filename).withSchema(schemaString);
        records = SourceTestUtils.readFromSource(source, null);
        assertEqualsWithGeneric(expected, records);
    }

    @Test
    public void testSchemaUpdate() throws Exception {
        List<AvroSourceTest.Bird> birds = AvroSourceTest.createRandomRecords(100);
        String filename = generateTestFile("tmp.avro", birds, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), NULL_CODEC);
        AvroSource<AvroSourceTest.FancyBird> source = AvroSource.from(filename).withSchema(AvroSourceTest.FancyBird.class);
        List<AvroSourceTest.FancyBird> actual = SourceTestUtils.readFromSource(source, null);
        List<AvroSourceTest.FancyBird> expected = new ArrayList<>();
        for (AvroSourceTest.Bird bird : birds) {
            expected.add(new AvroSourceTest.FancyBird(bird.number, bird.species, bird.quality, bird.quantity, null, "MAXIMUM OVERDRIVE"));
        }
        Assert.assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testSchemaStringIsInterned() throws Exception {
        List<AvroSourceTest.Bird> birds = AvroSourceTest.createRandomRecords(100);
        String filename = generateTestFile("tmp.avro", birds, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), NULL_CODEC);
        Metadata fileMetadata = FileSystems.matchSingleFileSpec(filename);
        String schema = AvroSource.readMetadataFromFile(fileMetadata.resourceId()).getSchemaString();
        // Add "" to the schema to make sure it is not interned.
        AvroSource<GenericRecord> sourceA = AvroSource.from(filename).withSchema(("" + schema));
        AvroSource<GenericRecord> sourceB = AvroSource.from(filename).withSchema(("" + schema));
        Assert.assertSame(sourceA.getReaderSchemaString(), sourceB.getReaderSchemaString());
        // Ensure that deserialization still goes through interning
        AvroSource<GenericRecord> sourceC = SerializableUtils.clone(sourceB);
        Assert.assertSame(sourceA.getReaderSchemaString(), sourceC.getReaderSchemaString());
    }

    @Test
    public void testParseFn() throws Exception {
        List<AvroSourceTest.Bird> expected = AvroSourceTest.createRandomRecords(100);
        String filename = generateTestFile("tmp.avro", expected, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), NULL_CODEC);
        AvroSource<AvroSourceTest.Bird> source = AvroSource.from(filename).withParseFn(( input) -> new org.apache.beam.sdk.io.Bird(((long) (input.get("number"))), input.get("species").toString(), input.get("quality").toString(), ((long) (input.get("quantity")))), AvroCoder.of(AvroSourceTest.Bird.class));
        List<AvroSourceTest.Bird> actual = SourceTestUtils.readFromSource(source, null);
        Assert.assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testAdvancePastNextSyncMarker() throws IOException {
        // Test placing the sync marker at different locations at the start and in the middle of the
        // buffer.
        for (int i = 0; i <= 16; i++) {
            testAdvancePastNextSyncMarkerAt(i, 1000);
            testAdvancePastNextSyncMarkerAt((160 + i), 1000);
        }
        // Test placing the sync marker at the end of the buffer.
        testAdvancePastNextSyncMarkerAt(983, 1000);
        // Test placing the sync marker so that it begins at the end of the buffer.
        testAdvancePastNextSyncMarkerAt(984, 1000);
        testAdvancePastNextSyncMarkerAt(985, 1000);
        testAdvancePastNextSyncMarkerAt(999, 1000);
        // Test with no sync marker.
        testAdvancePastNextSyncMarkerAt(1000, 1000);
    }

    // Tests for Seeker.
    @Test
    public void testSeekerFind() {
        byte[] marker = new byte[]{ 0, 1, 2, 3 };
        byte[] buffer;
        Seeker s;
        s = new Seeker(marker);
        buffer = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7 };
        Assert.assertEquals(3, s.find(buffer, buffer.length));
        buffer = new byte[]{ 0, 0, 0, 0, 0, 1, 2, 3 };
        Assert.assertEquals(7, s.find(buffer, buffer.length));
        buffer = new byte[]{ 0, 1, 2, 0, 0, 1, 2, 3 };
        Assert.assertEquals(7, s.find(buffer, buffer.length));
        buffer = new byte[]{ 0, 1, 2, 3 };
        Assert.assertEquals(3, s.find(buffer, buffer.length));
    }

    @Test
    public void testSeekerFindResume() {
        byte[] marker = new byte[]{ 0, 1, 2, 3 };
        byte[] buffer;
        Seeker s;
        s = new Seeker(marker);
        buffer = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 1, 2, 3, 0, 0, 0, 0, 0 };
        Assert.assertEquals(2, s.find(buffer, buffer.length));
        buffer = new byte[]{ 0, 0, 0, 0, 0, 0, 1, 2 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 3, 0, 1, 2, 3, 0, 1, 2 };
        Assert.assertEquals(0, s.find(buffer, buffer.length));
        buffer = new byte[]{ 0 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 1 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 2 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 3 };
        Assert.assertEquals(0, s.find(buffer, buffer.length));
    }

    @Test
    public void testSeekerUsesBufferLength() {
        byte[] marker = new byte[]{ 0, 0, 1 };
        byte[] buffer;
        Seeker s;
        s = new Seeker(marker);
        buffer = new byte[]{ 0, 0, 0, 1 };
        Assert.assertEquals((-1), s.find(buffer, 3));
        s = new Seeker(marker);
        buffer = new byte[]{ 0, 0 };
        Assert.assertEquals((-1), s.find(buffer, 1));
        buffer = new byte[]{ 1, 0 };
        Assert.assertEquals((-1), s.find(buffer, 1));
        s = new Seeker(marker);
        buffer = new byte[]{ 0, 2 };
        Assert.assertEquals((-1), s.find(buffer, 1));
        buffer = new byte[]{ 0, 2 };
        Assert.assertEquals((-1), s.find(buffer, 1));
        buffer = new byte[]{ 1, 2 };
        Assert.assertEquals(0, s.find(buffer, 1));
    }

    @Test
    public void testSeekerFindPartial() {
        byte[] marker = new byte[]{ 0, 0, 1 };
        byte[] buffer;
        Seeker s;
        s = new Seeker(marker);
        buffer = new byte[]{ 0, 0, 0, 1 };
        Assert.assertEquals(3, s.find(buffer, buffer.length));
        marker = new byte[]{ 1, 1, 1, 2 };
        s = new Seeker(marker);
        buffer = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 1, 1, 2 };
        Assert.assertEquals(2, s.find(buffer, buffer.length));
        buffer = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertEquals((-1), s.find(buffer, buffer.length));
        buffer = new byte[]{ 2, 1, 1, 1, 2 };
        Assert.assertEquals(0, s.find(buffer, buffer.length));
    }

    @Test
    public void testSeekerFindAllLocations() {
        byte[] marker = new byte[]{ 1, 1, 2 };
        byte[] allOnes = new byte[]{ 1, 1, 1, 1 };
        byte[] findIn = new byte[]{ 1, 1, 1, 1 };
        Seeker s = new Seeker(marker);
        for (int i = 0; i < (findIn.length); i++) {
            Assert.assertEquals((-1), s.find(allOnes, allOnes.length));
            findIn[i] = 2;
            Assert.assertEquals(i, s.find(findIn, findIn.length));
            findIn[i] = 1;
        }
    }

    @Test
    public void testDisplayData() {
        AvroSource<AvroSourceTest.Bird> source = AvroSource.from("foobar.txt").withSchema(AvroSourceTest.Bird.class).withMinBundleSize(1234);
        DisplayData displayData = DisplayData.from(source);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("filePattern", "foobar.txt"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("minBundleSize", 1234));
    }

    @Test
    public void testReadMetadataWithCodecs() throws Exception {
        // Test reading files generated using all codecs.
        String[] codecs = new String[]{ DataFileConstants.NULL_CODEC, DataFileConstants.BZIP2_CODEC, DataFileConstants.DEFLATE_CODEC, DataFileConstants.SNAPPY_CODEC, DataFileConstants.XZ_CODEC };
        List<AvroSourceTest.Bird> expected = AvroSourceTest.createRandomRecords(AvroSourceTest.DEFAULT_RECORD_COUNT);
        for (String codec : codecs) {
            String filename = generateTestFile(codec, expected, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), codec);
            Metadata fileMeta = FileSystems.matchSingleFileSpec(filename);
            AvroMetadata metadata = AvroSource.readMetadataFromFile(fileMeta.resourceId());
            Assert.assertEquals(codec, metadata.getCodec());
        }
    }

    @Test
    public void testReadSchemaString() throws Exception {
        List<AvroSourceTest.Bird> expected = AvroSourceTest.createRandomRecords(AvroSourceTest.DEFAULT_RECORD_COUNT);
        String codec = DataFileConstants.NULL_CODEC;
        String filename = generateTestFile(codec, expected, AvroSourceTest.SyncBehavior.SYNC_DEFAULT, 0, AvroCoder.of(AvroSourceTest.Bird.class), codec);
        Metadata fileMeta = FileSystems.matchSingleFileSpec(filename);
        AvroMetadata metadata = AvroSource.readMetadataFromFile(fileMeta.resourceId());
        // By default, parse validates the schema, which is what we want.
        Schema schema = new Schema.Parser().parse(metadata.getSchemaString());
        Assert.assertEquals(4, schema.getFields().size());
    }

    /**
     * Class that will encode to a fixed size: 16 bytes.
     *
     * <p>Each object has a 15-byte array. Avro encodes an object of this type as a byte array, so
     * each encoded object will consist of 1 byte that encodes the length of the array, followed by 15
     * bytes.
     */
    @DefaultCoder(AvroCoder.class)
    public static class FixedRecord {
        private byte[] value = new byte[15];

        public FixedRecord() {
            this(0);
        }

        public FixedRecord(int i) {
            value[0] = ((byte) (i));
            value[1] = ((byte) (i >> 8));
            value[2] = ((byte) (i >> 16));
            value[3] = ((byte) (i >> 24));
        }

        public int asInt() {
            return (((value[0]) | ((value[1]) << 8)) | ((value[2]) << 16)) | ((value[3]) << 24);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AvroSourceTest.FixedRecord) {
                AvroSourceTest.FixedRecord other = ((AvroSourceTest.FixedRecord) (o));
                return (this.asInt()) == (other.asInt());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return Integer.toString(this.asInt());
        }
    }

    /**
     * Class used as the record type in tests.
     */
    @DefaultCoder(AvroCoder.class)
    static class Bird {
        long number;

        String species;

        String quality;

        long quantity;

        public Bird() {
        }

        public Bird(long number, String species, String quality, long quantity) {
            this.number = number;
            this.species = species;
            this.quality = quality;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return org.apache.beam.vendor.guava.v20_0.com.google.common.base.MoreObjects.toStringHelper(AvroSourceTest.Bird.class).addValue(number).addValue(species).addValue(quantity).addValue(quality).toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AvroSourceTest.Bird) {
                AvroSourceTest.Bird other = ((AvroSourceTest.Bird) (obj));
                return (((Objects.equals(species, other.species)) && (Objects.equals(quality, other.quality))) && ((quantity) == (other.quantity))) && ((number) == (other.number));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, species, quality, quantity);
        }
    }

    /**
     * Class used as the record type in tests.
     *
     * <p>Contains nullable fields and fields with default values. Can be read using a file written
     * with the Bird schema.
     */
    @DefaultCoder(AvroCoder.class)
    public static class FancyBird {
        long number;

        String species;

        String quality;

        long quantity;

        @Nullable
        String habitat;

        @AvroDefault("\"MAXIMUM OVERDRIVE\"")
        String fancinessLevel;

        public FancyBird() {
        }

        public FancyBird(long number, String species, String quality, long quantity, String habitat, String fancinessLevel) {
            this.number = number;
            this.species = species;
            this.quality = quality;
            this.quantity = quantity;
            this.habitat = habitat;
            this.fancinessLevel = fancinessLevel;
        }

        @Override
        public String toString() {
            return org.apache.beam.vendor.guava.v20_0.com.google.common.base.MoreObjects.toStringHelper(AvroSourceTest.FancyBird.class).addValue(number).addValue(species).addValue(quality).addValue(quantity).addValue(habitat).addValue(fancinessLevel).toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AvroSourceTest.FancyBird) {
                AvroSourceTest.FancyBird other = ((AvroSourceTest.FancyBird) (obj));
                return (((((Objects.equals(species, other.species)) && (Objects.equals(quality, other.quality))) && ((quantity) == (other.quantity))) && ((number) == (other.number))) && (Objects.equals(fancinessLevel, other.fancinessLevel))) && (Objects.equals(habitat, other.habitat));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, species, quality, quantity, habitat, fancinessLevel);
        }
    }
}

