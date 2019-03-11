/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.record;


import CompressionType.GZIP;
import CompressionType.NONE;
import RecordBatch.MAGIC_VALUE_V0;
import RecordBatch.MAGIC_VALUE_V1;
import RecordBatch.MAGIC_VALUE_V2;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static RecordBatch.MAGIC_VALUE_V0;


public class FileRecordsTest {
    private byte[][] values = new byte[][]{ "abcd".getBytes(), "efgh".getBytes(), "ijkl".getBytes() };

    private FileRecords fileRecords;

    private Time time;

    @Test(expected = IllegalArgumentException.class)
    public void testAppendProtectsFromOverflow() throws Exception {
        File fileMock = Mockito.mock(File.class);
        FileChannel fileChannelMock = Mockito.mock(FileChannel.class);
        Mockito.when(fileChannelMock.size()).thenReturn(((long) (Integer.MAX_VALUE)));
        FileRecords records = new FileRecords(fileMock, fileChannelMock, 0, Integer.MAX_VALUE, false);
        append(records, values);
    }

    @Test(expected = KafkaException.class)
    public void testOpenOversizeFile() throws Exception {
        File fileMock = Mockito.mock(File.class);
        FileChannel fileChannelMock = Mockito.mock(FileChannel.class);
        Mockito.when(fileChannelMock.size()).thenReturn(((Integer.MAX_VALUE) + 5L));
        new FileRecords(fileMock, fileChannelMock, 0, Integer.MAX_VALUE, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfRangeSlice() throws Exception {
        this.fileRecords.slice(((fileRecords.sizeInBytes()) + 1), 15).sizeInBytes();
    }

    /**
     * Test that the cached size variable matches the actual file size as we append messages
     */
    @Test
    public void testFileSize() throws IOException {
        Assert.assertEquals(fileRecords.channel().size(), fileRecords.sizeInBytes());
        for (int i = 0; i < 20; i++) {
            fileRecords.append(MemoryRecords.withRecords(NONE, new SimpleRecord("abcd".getBytes())));
            Assert.assertEquals(fileRecords.channel().size(), fileRecords.sizeInBytes());
        }
    }

    /**
     * Test that adding invalid bytes to the end of the log doesn't break iteration
     */
    @Test
    public void testIterationOverPartialAndTruncation() throws IOException {
        testPartialWrite(0, fileRecords);
        testPartialWrite(2, fileRecords);
        testPartialWrite(4, fileRecords);
        testPartialWrite(5, fileRecords);
        testPartialWrite(6, fileRecords);
    }

    /**
     * Iterating over the file does file reads but shouldn't change the position of the underlying FileChannel.
     */
    @Test
    public void testIterationDoesntChangePosition() throws IOException {
        long position = fileRecords.channel().position();
        Iterator<Record> records = fileRecords.records().iterator();
        for (byte[] value : values) {
            Assert.assertTrue(records.hasNext());
            Assert.assertEquals(records.next().value(), ByteBuffer.wrap(value));
        }
        Assert.assertEquals(position, fileRecords.channel().position());
    }

    /**
     * Test a simple append and read.
     */
    @Test
    public void testRead() throws IOException {
        FileRecords read = fileRecords.slice(0, fileRecords.sizeInBytes());
        Assert.assertEquals(fileRecords.sizeInBytes(), read.sizeInBytes());
        TestUtils.checkEquals(fileRecords.batches(), read.batches());
        List<RecordBatch> items = FileRecordsTest.batches(read);
        RecordBatch first = items.get(0);
        // read from second message until the end
        read = fileRecords.slice(first.sizeInBytes(), ((fileRecords.sizeInBytes()) - (first.sizeInBytes())));
        Assert.assertEquals(((fileRecords.sizeInBytes()) - (first.sizeInBytes())), read.sizeInBytes());
        Assert.assertEquals("Read starting from the second message", items.subList(1, items.size()), FileRecordsTest.batches(read));
        // read from second message and size is past the end of the file
        read = fileRecords.slice(first.sizeInBytes(), fileRecords.sizeInBytes());
        Assert.assertEquals(((fileRecords.sizeInBytes()) - (first.sizeInBytes())), read.sizeInBytes());
        Assert.assertEquals("Read starting from the second message", items.subList(1, items.size()), FileRecordsTest.batches(read));
        // read from second message and position + size overflows
        read = fileRecords.slice(first.sizeInBytes(), Integer.MAX_VALUE);
        Assert.assertEquals(((fileRecords.sizeInBytes()) - (first.sizeInBytes())), read.sizeInBytes());
        Assert.assertEquals("Read starting from the second message", items.subList(1, items.size()), FileRecordsTest.batches(read));
        // read from second message and size is past the end of the file on a view/slice
        read = fileRecords.slice(1, ((fileRecords.sizeInBytes()) - 1)).slice(((first.sizeInBytes()) - 1), fileRecords.sizeInBytes());
        Assert.assertEquals(((fileRecords.sizeInBytes()) - (first.sizeInBytes())), read.sizeInBytes());
        Assert.assertEquals("Read starting from the second message", items.subList(1, items.size()), FileRecordsTest.batches(read));
        // read from second message and position + size overflows on a view/slice
        read = fileRecords.slice(1, ((fileRecords.sizeInBytes()) - 1)).slice(((first.sizeInBytes()) - 1), Integer.MAX_VALUE);
        Assert.assertEquals(((fileRecords.sizeInBytes()) - (first.sizeInBytes())), read.sizeInBytes());
        Assert.assertEquals("Read starting from the second message", items.subList(1, items.size()), FileRecordsTest.batches(read));
        // read a single message starting from second message
        RecordBatch second = items.get(1);
        read = fileRecords.slice(first.sizeInBytes(), second.sizeInBytes());
        Assert.assertEquals(second.sizeInBytes(), read.sizeInBytes());
        Assert.assertEquals("Read a single message starting from the second message", Collections.singletonList(second), FileRecordsTest.batches(read));
    }

    /**
     * Test the MessageSet.searchFor API.
     */
    @Test
    public void testSearch() throws IOException {
        // append a new message with a high offset
        SimpleRecord lastMessage = new SimpleRecord("test".getBytes());
        fileRecords.append(MemoryRecords.withRecords(50L, NONE, lastMessage));
        List<RecordBatch> batches = FileRecordsTest.batches(fileRecords);
        int position = 0;
        int message1Size = batches.get(0).sizeInBytes();
        Assert.assertEquals("Should be able to find the first message by its offset", new FileRecords.LogOffsetPosition(0L, position, message1Size), fileRecords.searchForOffsetWithSize(0, 0));
        position += message1Size;
        int message2Size = batches.get(1).sizeInBytes();
        Assert.assertEquals("Should be able to find second message when starting from 0", new FileRecords.LogOffsetPosition(1L, position, message2Size), fileRecords.searchForOffsetWithSize(1, 0));
        Assert.assertEquals("Should be able to find second message starting from its offset", new FileRecords.LogOffsetPosition(1L, position, message2Size), fileRecords.searchForOffsetWithSize(1, position));
        position += message2Size + (batches.get(2).sizeInBytes());
        int message4Size = batches.get(3).sizeInBytes();
        Assert.assertEquals("Should be able to find fourth message from a non-existent offset", new FileRecords.LogOffsetPosition(50L, position, message4Size), fileRecords.searchForOffsetWithSize(3, position));
        Assert.assertEquals("Should be able to find fourth message by correct offset", new FileRecords.LogOffsetPosition(50L, position, message4Size), fileRecords.searchForOffsetWithSize(50, position));
    }

    /**
     * Test that the message set iterator obeys start and end slicing
     */
    @Test
    public void testIteratorWithLimits() throws IOException {
        RecordBatch batch = FileRecordsTest.batches(fileRecords).get(1);
        int start = fileRecords.searchForOffsetWithSize(1, 0).position;
        int size = batch.sizeInBytes();
        FileRecords slice = fileRecords.slice(start, size);
        Assert.assertEquals(Collections.singletonList(batch), FileRecordsTest.batches(slice));
        FileRecords slice2 = fileRecords.slice(start, (size - 1));
        Assert.assertEquals(Collections.emptyList(), FileRecordsTest.batches(slice2));
    }

    /**
     * Test the truncateTo method lops off messages and appropriately updates the size
     */
    @Test
    public void testTruncate() throws IOException {
        RecordBatch batch = FileRecordsTest.batches(fileRecords).get(0);
        int end = fileRecords.searchForOffsetWithSize(1, 0).position;
        fileRecords.truncateTo(end);
        Assert.assertEquals(Collections.singletonList(batch), FileRecordsTest.batches(fileRecords));
        Assert.assertEquals(batch.sizeInBytes(), fileRecords.sizeInBytes());
    }

    /**
     * Test that truncateTo only calls truncate on the FileChannel if the size of the
     * FileChannel is bigger than the target size. This is important because some JVMs
     * change the mtime of the file, even if truncate should do nothing.
     */
    @Test
    public void testTruncateNotCalledIfSizeIsSameAsTargetSize() throws IOException {
        FileChannel channelMock = Mockito.mock(FileChannel.class);
        Mockito.when(channelMock.size()).thenReturn(42L);
        Mockito.when(channelMock.position(42L)).thenReturn(null);
        FileRecords fileRecords = new FileRecords(TestUtils.tempFile(), channelMock, 0, Integer.MAX_VALUE, false);
        fileRecords.truncateTo(42);
        Mockito.verify(channelMock, Mockito.atLeastOnce()).size();
        Mockito.verify(channelMock, Mockito.times(0)).truncate(ArgumentMatchers.anyLong());
    }

    /**
     * Expect a KafkaException if targetSize is bigger than the size of
     * the FileRecords.
     */
    @Test
    public void testTruncateNotCalledIfSizeIsBiggerThanTargetSize() throws IOException {
        FileChannel channelMock = Mockito.mock(FileChannel.class);
        Mockito.when(channelMock.size()).thenReturn(42L);
        FileRecords fileRecords = new FileRecords(TestUtils.tempFile(), channelMock, 0, Integer.MAX_VALUE, false);
        try {
            fileRecords.truncateTo(43);
            Assert.fail("Should throw KafkaException");
        } catch (KafkaException e) {
            // expected
        }
        Mockito.verify(channelMock, Mockito.atLeastOnce()).size();
    }

    /**
     * see #testTruncateNotCalledIfSizeIsSameAsTargetSize
     */
    @Test
    public void testTruncateIfSizeIsDifferentToTargetSize() throws IOException {
        FileChannel channelMock = Mockito.mock(FileChannel.class);
        Mockito.when(channelMock.size()).thenReturn(42L);
        Mockito.when(channelMock.truncate(ArgumentMatchers.anyLong())).thenReturn(channelMock);
        FileRecords fileRecords = new FileRecords(TestUtils.tempFile(), channelMock, 0, Integer.MAX_VALUE, false);
        fileRecords.truncateTo(23);
        Mockito.verify(channelMock, Mockito.atLeastOnce()).size();
        Mockito.verify(channelMock).truncate(23);
    }

    /**
     * Test the new FileRecords with pre allocate as true
     */
    @Test
    public void testPreallocateTrue() throws IOException {
        File temp = TestUtils.tempFile();
        FileRecords fileRecords = FileRecords.open(temp, false, (1024 * 1024), true);
        long position = fileRecords.channel().position();
        int size = fileRecords.sizeInBytes();
        Assert.assertEquals(0, position);
        Assert.assertEquals(0, size);
        Assert.assertEquals((1024 * 1024), temp.length());
    }

    /**
     * Test the new FileRecords with pre allocate as false
     */
    @Test
    public void testPreallocateFalse() throws IOException {
        File temp = TestUtils.tempFile();
        FileRecords set = FileRecords.open(temp, false, (1024 * 1024), false);
        long position = set.channel().position();
        int size = set.sizeInBytes();
        Assert.assertEquals(0, position);
        Assert.assertEquals(0, size);
        Assert.assertEquals(0, temp.length());
    }

    /**
     * Test the new FileRecords with pre allocate as true and file has been clearly shut down, the file will be truncate to end of valid data.
     */
    @Test
    public void testPreallocateClearShutdown() throws IOException {
        File temp = TestUtils.tempFile();
        FileRecords fileRecords = FileRecords.open(temp, false, (1024 * 1024), true);
        append(fileRecords, values);
        int oldPosition = ((int) (fileRecords.channel().position()));
        int oldSize = fileRecords.sizeInBytes();
        Assert.assertEquals(this.fileRecords.sizeInBytes(), oldPosition);
        Assert.assertEquals(this.fileRecords.sizeInBytes(), oldSize);
        fileRecords.close();
        File tempReopen = new File(temp.getAbsolutePath());
        FileRecords setReopen = FileRecords.open(tempReopen, true, (1024 * 1024), true);
        int position = ((int) (setReopen.channel().position()));
        int size = setReopen.sizeInBytes();
        Assert.assertEquals(oldPosition, position);
        Assert.assertEquals(oldPosition, size);
        Assert.assertEquals(oldPosition, tempReopen.length());
    }

    @Test
    public void testFormatConversionWithPartialMessage() throws IOException {
        RecordBatch batch = FileRecordsTest.batches(fileRecords).get(1);
        int start = fileRecords.searchForOffsetWithSize(1, 0).position;
        int size = batch.sizeInBytes();
        FileRecords slice = fileRecords.slice(start, (size - 1));
        Records messageV0 = slice.downConvert(MAGIC_VALUE_V0, 0, time).records();
        Assert.assertTrue("No message should be there", FileRecordsTest.batches(messageV0).isEmpty());
        Assert.assertEquals((("There should be " + (size - 1)) + " bytes"), (size - 1), messageV0.sizeInBytes());
        // Lazy down-conversion will not return any messages for a partial input batch
        TopicPartition tp = new TopicPartition("topic-1", 0);
        LazyDownConversionRecords lazyRecords = new LazyDownConversionRecords(tp, slice, MAGIC_VALUE_V0, 0, Time.SYSTEM);
        Iterator<ConvertedRecords<?>> it = lazyRecords.iterator((16 * 1024L));
        Assert.assertTrue("No messages should be returned", (!(it.hasNext())));
    }

    @Test
    public void testSearchForTimestamp() throws IOException {
        for (RecordVersion version : RecordVersion.values()) {
            testSearchForTimestamp(version);
        }
    }

    @Test
    public void testConversion() throws IOException {
        doTestConversion(NONE, MAGIC_VALUE_V0);
        doTestConversion(GZIP, MAGIC_VALUE_V0);
        doTestConversion(NONE, MAGIC_VALUE_V1);
        doTestConversion(GZIP, MAGIC_VALUE_V1);
        doTestConversion(NONE, MAGIC_VALUE_V2);
        doTestConversion(GZIP, MAGIC_VALUE_V2);
    }
}

