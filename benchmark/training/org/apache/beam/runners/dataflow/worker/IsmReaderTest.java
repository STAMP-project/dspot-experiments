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


import RandomAccessData.UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
import java.io.Closeable;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecord;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecordCoder;
import org.apache.beam.runners.dataflow.internal.IsmFormat.MetadataKeyCoder;
import org.apache.beam.runners.dataflow.util.RandomAccessData;
import org.apache.beam.runners.dataflow.worker.IsmReaderImpl.CachedTailSeekableByteChannel;
import org.apache.beam.runners.dataflow.worker.IsmReaderImpl.IsmShardKey;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Sink.SinkWriter;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.testing.CoderPropertiesTest.NonDeterministicCoder;
import org.apache.beam.sdk.util.WeightedValue;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Predicate;
import org.apache.beam.vendor.guava.v20_0.com.google.common.cache.Cache;
import org.apache.beam.vendor.guava.v20_0.com.google.common.cache.CacheBuilder;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.Ints;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static IsmReaderImpl.MAX_SHARD_INDEX_AND_FOOTER_SIZE;


/**
 * Tests for {@link IsmReader}.
 */
@RunWith(JUnit4.class)
public class IsmReaderTest {
    private static final long BLOOM_FILTER_SIZE_LIMIT = 10000;

    private static final int TEST_BLOCK_SIZE = 1024;

    private static final IsmRecordCoder<byte[]> CODER = // number or shard key coders for value records
    // number of shard key coders for metadata records
    IsmRecordCoder.of(1, 1, ImmutableList.<Coder<?>>of(MetadataKeyCoder.of(ByteArrayCoder.of()), ByteArrayCoder.of()), ByteArrayCoder.of());

    private static final Coder<String> NON_DETERMINISTIC_CODER = new NonDeterministicCoder();

    private static final byte[] EMPTY = new byte[0];

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Cache<IsmReaderImpl.IsmShardKey, WeightedValue<NavigableMap<RandomAccessData, WindowedValue<IsmRecord<byte[]>>>>> cache;

    private DataflowExecutionContext executionContext;

    private DataflowOperationContext operationContext;

    private SideInputReadCounter sideInputReadCounter;

    private Closeable stateCloseable;

    @Test
    public void testReadEmpty() throws Exception {
        writeElementsToFileAndReadInOrder(Collections.<IsmRecord<byte[]>>emptyList());
    }

    @Test
    public void testUsingNonDeterministicShardKeyCoder() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("is expected to be deterministic");
        new IsmReaderImpl(FileSystems.matchSingleFileSpec(tmpFolder.newFile().getPath()).resourceId(), // number or shard key coders for value records
        // number of shard key coders for metadata records
        IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(IsmReaderTest.NON_DETERMINISTIC_CODER, ByteArrayCoder.of()), ByteArrayCoder.of()), cache);
    }

    @Test
    public void testUsingNonDeterministicNonShardKeyCoder() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("is expected to be deterministic");
        new IsmReaderImpl(FileSystems.matchSingleFileSpec(tmpFolder.newFile().getPath()).resourceId(), // number or shard key coders for value records
        // number of shard key coders for metadata records
        IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(ByteArrayCoder.of(), IsmReaderTest.NON_DETERMINISTIC_CODER), ByteArrayCoder.of()), cache);
    }

    @Test
    public void testIsEmpty() throws Exception {
        File tmpFile = tmpFolder.newFile();
        List<IsmRecord<byte[]>> data = new ArrayList<>();
        IsmReaderTest.writeElementsToFile(data, tmpFile);
        IsmReader<byte[]> reader = new IsmReaderImpl<byte[]>(FileSystems.matchSingleFileSpec(tmpFile.getAbsolutePath()).resourceId(), IsmReaderTest.CODER, cache);
        Assert.assertFalse(reader.isInitialized());
        Assert.assertTrue(reader.isEmpty());
        Assert.assertTrue(reader.isInitialized());
    }

    @Test
    public void testRead() throws Exception {
        Random random = new Random(23498321490L);
        for (int i : Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            int minElements = ((int) (Math.pow(2, i)));
            int valueSize = 128;
            // Generates between 2^i and 2^(i + 1) elements.
            writeElementsToFileAndReadInOrder(/* number of primary keys */
            /* number of secondary keys */
            /* max key size */
            dataGenerator(8, (minElements + (random.nextInt(minElements))), 8, valueSize));
        }
    }

    @Test
    public void testReadThatProducesIndexEntries() throws Exception {
        Random random = new Random(23498323891L);
        int minElements = ((int) (Math.pow(2, 6)));
        int valueSize = 128;
        // Since we are generating more than 2 blocks worth of data, we are guaranteed that
        // at least one index entry is generated per shard.
        checkState(((minElements * valueSize) > (2 * (IsmReaderTest.TEST_BLOCK_SIZE))));
        writeElementsToFileAndReadInOrder(/* number of primary keys */
        /* number of secondary keys */
        /* max key size */
        /* max value size */
        dataGenerator(8, (minElements + (random.nextInt(minElements))), 8, valueSize));
    }

    @Test
    public void testReadRandomOrder() throws Exception {
        Random random = new Random(2348238943L);
        for (int i : Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)) {
            int minElements = ((int) (Math.pow(2, i)));
            int valueSize = 128;
            // Generates between 2^i and 2^(i + 1) elements.
            writeElementsToFileAndReadInRandomOrder(/* number of primary keys */
            /* number of secondary keys */
            /* max key size */
            /* max value size */
            dataGenerator(7, (minElements + (random.nextInt(minElements))), 8, valueSize));
        }
    }

    @Test
    public void testGetLastWithPrefix() throws Exception {
        Random random = new Random(2348238943L);
        for (int i : Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            int minElements = ((int) (Math.pow(2, i)));
            int valueSize = 128;
            // Generates between 2^i and 2^(i + 1) elements.
            writeElementsToFileAndFindLastElementPerPrimaryKey(/* max key size */
            /* max value size */
            dataGenerator(7, (minElements + (random.nextInt(minElements))), 8, valueSize));
        }
    }

    @Test
    public void testReadMissingKeys() throws Exception {
        File tmpFile = tmpFolder.newFile();
        List<IsmRecord<byte[]>> data = new ArrayList<>();
        data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 4 }), IsmReaderTest.EMPTY));
        data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 8 }), IsmReaderTest.EMPTY));
        IsmReaderTest.writeElementsToFile(data, tmpFile);
        IsmReader<byte[]> reader = new IsmReaderImpl<byte[]>(FileSystems.matchSingleFileSpec(tmpFile.getAbsolutePath()).resourceId(), IsmReaderTest.CODER, cache);
        // Check that we got false with a key before all keys contained in the file.
        Assert.assertFalse(reader.overKeyComponents(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 2 })).start());
        // Check that we got false with a key between two other keys contained in the file.
        Assert.assertFalse(reader.overKeyComponents(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 6 })).start());
        // Check that we got false with a key that is after all keys contained in the file.
        Assert.assertFalse(reader.overKeyComponents(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 16 })).start());
    }

    @Test
    public void testReadMissingKeysBypassingBloomFilter() throws Exception {
        File tmpFile = tmpFolder.newFile();
        List<IsmRecord<byte[]>> data = new ArrayList<>();
        data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 4 }), IsmReaderTest.EMPTY));
        data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 8 }), IsmReaderTest.EMPTY));
        IsmReaderTest.writeElementsToFile(data, tmpFile);
        IsmReader<byte[]> reader = new IsmReaderImpl<byte[]>(FileSystems.matchSingleFileSpec(tmpFile.getAbsolutePath()).resourceId(), IsmReaderTest.CODER, cache) {
            // We use this override to get around the Bloom filter saying that the key doesn't exist.
            @Override
            boolean bloomFilterMightContain(RandomAccessData keyBytes) {
                return true;
            }
        };
        // Check that we got false with a key before all keys contained in the file.
        Assert.assertFalse(reader.overKeyComponents(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 2 })).start());
        // Check that we got false with a key between two other keys contained in the file.
        Assert.assertFalse(reader.overKeyComponents(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 6 })).start());
        // Check that we got false with a key that is after all keys contained in the file.
        Assert.assertFalse(reader.overKeyComponents(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 16 })).start());
    }

    @Test
    public void testReadKeyThatEncodesToEmptyByteArray() throws Exception {
        File tmpFile = tmpFolder.newFile();
        IsmRecordCoder<Void> coder = IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(VoidCoder.of()), VoidCoder.of());
        IsmSink<Void> sink = new IsmSink(FileSystems.matchNewResource(tmpFile.getPath(), false), coder, IsmReaderTest.BLOOM_FILTER_SIZE_LIMIT);
        IsmRecord<Void> element = IsmRecord.of(Arrays.asList(((Void) (null))), ((Void) (null)));
        try (SinkWriter<WindowedValue<IsmRecord<Void>>> writer = sink.writer()) {
            writer.add(new org.apache.beam.runners.dataflow.worker.util.ValueInEmptyWindows(element));
        }
        Cache<IsmShardKey, WeightedValue<NavigableMap<RandomAccessData, WindowedValue<IsmRecord<Void>>>>> cache = CacheBuilder.newBuilder().weigher(Weighers.fixedWeightKeys(1)).maximumWeight(10000).build();
        IsmReader<Void> reader = new IsmReaderImpl(FileSystems.matchSingleFileSpec(tmpFile.getAbsolutePath()).resourceId(), coder, cache);
        IsmReader<Void>.IsmPrefixReaderIterator iterator = reader.iterator();
        Assert.assertTrue(iterator.start());
        Assert.assertEquals(coder.structuralValue(element), coder.structuralValue(iterator.getCurrent().getValue()));
    }

    @Test
    public void testInitializationForSmallFilesIsCached() throws Exception {
        File tmpFile = tmpFolder.newFile();
        IsmShardKey expectedShardKey = new IsmShardKey(tmpFile.getAbsolutePath(), new RandomAccessData(0), 0, 13);
        List<IsmRecord<byte[]>> data = new ArrayList<>();
        data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 4 }), new byte[]{ 4 }));
        data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, new byte[]{ 8 }), new byte[]{ 8 }));
        IsmReaderTest.writeElementsToFile(data, tmpFile);
        IsmReader<byte[]> reader = new IsmReaderImpl<byte[]>(FileSystems.matchSingleFileSpec(tmpFile.getAbsolutePath()).resourceId(), IsmReaderTest.CODER, cache);
        // Validate that reader and cache are in initial state
        Assert.assertFalse(reader.isInitialized());
        Assert.assertEquals(0, cache.size());
        // Force initialization
        reader.overKeyComponents(ImmutableList.of());
        // Validate reader is initialized and expected entry is cached
        Assert.assertTrue(reader.isInitialized());
        WeightedValue<NavigableMap<RandomAccessData, WindowedValue<IsmRecord<byte[]>>>> block = cache.getIfPresent(expectedShardKey);
        Assert.assertNotNull(block);
        Assert.assertArrayEquals(new byte[]{ 4 }, block.getValue().firstEntry().getValue().getValue().getValue());
        Assert.assertArrayEquals(new byte[]{ 8 }, block.getValue().lastEntry().getValue().getValue().getValue());
    }

    @Test
    public void testInitializationForLargeFilesIsNotCached() throws Exception {
        File tmpFile = tmpFolder.newFile();
        List<IsmRecord<byte[]>> data = new ArrayList<>();
        // Use enough data records which are smaller than the cache limit to exceed the 1 MB
        // footer read buffer used by IsmReader to optimize small file handling.
        for (int i = 0; i < (((MAX_SHARD_INDEX_AND_FOOTER_SIZE) / (IsmReaderTest.TEST_BLOCK_SIZE)) + 1); ++i) {
            data.add(IsmRecord.<byte[]>of(ImmutableList.of(IsmReaderTest.EMPTY, Ints.toByteArray(i)), new byte[IsmReaderTest.TEST_BLOCK_SIZE]));
        }
        IsmReaderTest.writeElementsToFile(data, tmpFile);
        IsmReader<byte[]> reader = new IsmReaderImpl<byte[]>(FileSystems.matchSingleFileSpec(tmpFile.getAbsolutePath()).resourceId(), IsmReaderTest.CODER, cache);
        Assert.assertFalse(reader.isInitialized());
        Assert.assertEquals(0, cache.size());
        reader.overKeyComponents(ImmutableList.of());
        Assert.assertTrue(reader.isInitialized());
        Assert.assertEquals(0, cache.size());
    }

    /**
     * A predicate which filters elements on whether the second key's last byte is odd or even. Allows
     * for a stable partitioning of generated data.
     */
    private static class EvenFilter implements Predicate<IsmRecord<byte[]>> {
        private static final IsmReaderTest.EvenFilter INSTANCE = new IsmReaderTest.EvenFilter();

        @Override
        public boolean apply(IsmRecord<byte[]> input) {
            byte[] secondKey = ((byte[]) (input.getKeyComponent(1)));
            return ((secondKey[((secondKey.length) - 1)]) % 2) == 0;
        }
    }

    static class IsmRecordKeyComparator<V> implements Comparator<IsmRecord<V>> {
        private final IsmRecordCoder<V> coder;

        IsmRecordKeyComparator(IsmRecordCoder<V> coder) {
            this.coder = coder;
        }

        @Override
        public int compare(IsmRecord<V> first, IsmRecord<V> second) {
            RandomAccessData firstKeyBytes = new RandomAccessData();
            coder.encodeAndHash(first.getKeyComponents(), firstKeyBytes);
            RandomAccessData secondKeyBytes = new RandomAccessData();
            coder.encodeAndHash(second.getKeyComponents(), secondKeyBytes);
            return UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(firstKeyBytes, secondKeyBytes);
        }
    }

    /**
     * Specifies the minimum key size so that we can produce a random byte array with enough of a
     * prefix to be able to create successively larger secondary keys.
     */
    private static final int MIN_KEY_SIZE = 4;

    /**
     * Specifies the percentage of keys that are metadata records when using the data generator.
     */
    private static final double PERCENT_METADATA_RECORDS = 0.01;

    @Test
    public void testCachedTailSeekableByteChannelThrowsOnTruncate() throws Exception {
        try (SeekableByteChannel channel = new CachedTailSeekableByteChannel(0, new byte[0])) {
            expectedException.expect(NonWritableChannelException.class);
            channel.truncate(0);
        }
    }

    @Test
    public void testCachedTailSeekableByteChannelThrowsOnWrite() throws Exception {
        try (SeekableByteChannel channel = new CachedTailSeekableByteChannel(0, new byte[0])) {
            expectedException.expect(NonWritableChannelException.class);
            channel.write(ByteBuffer.wrap(new byte[0]));
        }
    }

    @Test
    public void testCachedTailSeekableByteChannelRead() throws Exception {
        final int offset = 10;
        try (SeekableByteChannel channel = new CachedTailSeekableByteChannel(offset, new byte[]{ 0, 1, 2 })) {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            channel.position(offset);
            Assert.assertEquals(1, channel.read(buffer));
            Assert.assertEquals(0, buffer.get(0));
            Assert.assertEquals((offset + 1), channel.position());
            buffer.clear();
            Assert.assertEquals(1, channel.read(buffer));
            Assert.assertEquals(1, buffer.get(0));
            Assert.assertEquals((offset + 2), channel.position());
            buffer.clear();
            Assert.assertEquals(1, channel.read(buffer));
            Assert.assertEquals(2, buffer.get(0));
            Assert.assertEquals((offset + 3), channel.position());
            buffer.clear();
            // Reposition the stream and do a read
            channel.position((offset + 1));
            Assert.assertEquals(1, channel.read(buffer));
            Assert.assertEquals(1, buffer.get(0));
            Assert.assertEquals((offset + 2), channel.position());
            buffer.clear();
            Assert.assertEquals(1, channel.read(buffer));
            Assert.assertEquals(2, buffer.get(0));
            Assert.assertEquals((offset + 3), channel.position());
            buffer.clear();
            // This read is expected to return EOF
            Assert.assertEquals((-1), channel.read(buffer));
            buffer.clear();
            // Reposition the stream to EOF and do a read, expected to return EOF
            channel.position((offset + 3));
            Assert.assertEquals((-1), channel.read(buffer));
            buffer.clear();
        }
    }

    @Test
    public void testCachedTailSeekableByteChannelSeekBeforeBounds() throws Exception {
        try (SeekableByteChannel channel = new CachedTailSeekableByteChannel(1, new byte[0])) {
            // Seek to only valid position
            channel.position(1);
            expectedException.expect(IllegalArgumentException.class);
            channel.position(0);
        }
    }

    @Test
    public void testCachedTailSeekableByteChannelSeekBeyondBounds() throws Exception {
        try (SeekableByteChannel channel = new CachedTailSeekableByteChannel(1, new byte[0])) {
            // Seek to only valid position
            channel.position(1);
            expectedException.expect(IllegalArgumentException.class);
            channel.position(2);
        }
    }
}

