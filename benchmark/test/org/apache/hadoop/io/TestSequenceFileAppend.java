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
package org.apache.hadoop.io;


import CompressionType.BLOCK;
import CompressionType.NONE;
import CompressionType.RECORD;
import SequenceFile.Metadata;
import SequenceFile.Sorter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.SequenceFile.Writer.Option;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestSequenceFileAppend {
    private static Configuration conf;

    private static FileSystem fs;

    private static Path ROOT_PATH = new Path(GenericTestUtils.getTestDir().getAbsolutePath());

    @Test(timeout = 30000)
    public void testAppend() throws Exception {
        Path file = new Path(TestSequenceFileAppend.ROOT_PATH, "testseqappend.seq");
        TestSequenceFileAppend.fs.delete(file, true);
        Text key1 = new Text("Key1");
        Text value1 = new Text("Value1");
        Text value2 = new Text("Updated");
        SequenceFile.Metadata metadata = new SequenceFile.Metadata();
        metadata.set(key1, value1);
        Writer.Option metadataOption = Writer.metadata(metadata);
        Writer writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), metadataOption);
        writer.append(1L, "one");
        writer.append(2L, "two");
        writer.close();
        verify2Values(file);
        metadata.set(key1, value2);
        writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), metadataOption);
        // Verify the Meta data is not changed
        Assert.assertEquals(value1, writer.metadata.get(key1));
        writer.append(3L, "three");
        writer.append(4L, "four");
        writer.close();
        verifyAll4Values(file);
        // Verify the Meta data readable after append
        Reader reader = new Reader(TestSequenceFileAppend.conf, Reader.file(file));
        Assert.assertEquals(value1, reader.getMetadata().get(key1));
        reader.close();
        // Verify failure if the compression details are different
        try {
            Option wrongCompressOption = Writer.compression(RECORD, new GzipCodec());
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), wrongCompressOption);
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException IAE) {
            // Expected exception. Ignore it
        }
        try {
            Option wrongCompressOption = Writer.compression(BLOCK, new DefaultCodec());
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), wrongCompressOption);
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException IAE) {
            // Expected exception. Ignore it
        }
        TestSequenceFileAppend.fs.deleteOnExit(file);
    }

    @Test(timeout = 30000)
    public void testAppendRecordCompression() throws Exception {
        GenericTestUtils.assumeInNativeProfile();
        Path file = new Path(TestSequenceFileAppend.ROOT_PATH, "testseqappendblockcompr.seq");
        TestSequenceFileAppend.fs.delete(file, true);
        Option compressOption = Writer.compression(RECORD, new GzipCodec());
        Writer writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), compressOption);
        writer.append(1L, "one");
        writer.append(2L, "two");
        writer.close();
        verify2Values(file);
        writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), compressOption);
        writer.append(3L, "three");
        writer.append(4L, "four");
        writer.close();
        verifyAll4Values(file);
        TestSequenceFileAppend.fs.deleteOnExit(file);
    }

    @Test(timeout = 30000)
    public void testAppendBlockCompression() throws Exception {
        GenericTestUtils.assumeInNativeProfile();
        Path file = new Path(TestSequenceFileAppend.ROOT_PATH, "testseqappendblockcompr.seq");
        TestSequenceFileAppend.fs.delete(file, true);
        Option compressOption = Writer.compression(BLOCK, new GzipCodec());
        Writer writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), compressOption);
        writer.append(1L, "one");
        writer.append(2L, "two");
        writer.close();
        verify2Values(file);
        writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), compressOption);
        writer.append(3L, "three");
        writer.append(4L, "four");
        writer.close();
        verifyAll4Values(file);
        // Verify failure if the compression details are different or not Provided
        try {
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true));
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException IAE) {
            // Expected exception. Ignore it
        }
        // Verify failure if the compression details are different
        try {
            Option wrongCompressOption = Writer.compression(RECORD, new GzipCodec());
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), wrongCompressOption);
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException IAE) {
            // Expected exception. Ignore it
        }
        try {
            Option wrongCompressOption = Writer.compression(BLOCK, new DefaultCodec());
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), wrongCompressOption);
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException IAE) {
            // Expected exception. Ignore it
        }
        TestSequenceFileAppend.fs.deleteOnExit(file);
    }

    @Test(timeout = 30000)
    public void testAppendNoneCompression() throws Exception {
        Path file = new Path(TestSequenceFileAppend.ROOT_PATH, "testseqappendnonecompr.seq");
        TestSequenceFileAppend.fs.delete(file, true);
        Option compressOption = Writer.compression(NONE);
        Writer writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), compressOption);
        writer.append(1L, "one");
        writer.append(2L, "two");
        writer.close();
        verify2Values(file);
        writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), compressOption);
        writer.append(3L, "three");
        writer.append(4L, "four");
        writer.close();
        verifyAll4Values(file);
        // Verify failure if the compression details are different or not Provided
        try {
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true));
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException iae) {
            // Expected exception. Ignore it
        }
        // Verify failure if the compression details are different
        try {
            Option wrongCompressOption = Writer.compression(RECORD, new GzipCodec());
            writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), wrongCompressOption);
            writer.close();
            Assert.fail("Expected IllegalArgumentException for compression options");
        } catch (IllegalArgumentException iae) {
            // Expected exception. Ignore it
        }
        // Codec should be ignored
        Option noneWithCodec = Writer.compression(NONE, new DefaultCodec());
        writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), noneWithCodec);
        writer.close();
        TestSequenceFileAppend.fs.deleteOnExit(file);
    }

    @Test(timeout = 30000)
    public void testAppendSort() throws Exception {
        GenericTestUtils.assumeInNativeProfile();
        Path file = new Path(TestSequenceFileAppend.ROOT_PATH, "testseqappendSort.seq");
        TestSequenceFileAppend.fs.delete(file, true);
        Path sortedFile = new Path(TestSequenceFileAppend.ROOT_PATH, "testseqappendSort.seq.sort");
        TestSequenceFileAppend.fs.delete(sortedFile, true);
        SequenceFile.Sorter sorter = new SequenceFile.Sorter(TestSequenceFileAppend.fs, new org.apache.hadoop.io.serializer.JavaSerializationComparator<Long>(), Long.class, String.class, TestSequenceFileAppend.conf);
        Option compressOption = Writer.compression(BLOCK, new GzipCodec());
        Writer writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), compressOption);
        writer.append(2L, "two");
        writer.append(1L, "one");
        writer.close();
        writer = SequenceFile.createWriter(TestSequenceFileAppend.conf, SequenceFile.Writer.file(file), SequenceFile.Writer.keyClass(Long.class), SequenceFile.Writer.valueClass(String.class), SequenceFile.Writer.appendIfExists(true), compressOption);
        writer.append(4L, "four");
        writer.append(3L, "three");
        writer.close();
        // Sort file after append
        sorter.sort(file, sortedFile);
        verifyAll4Values(sortedFile);
        TestSequenceFileAppend.fs.deleteOnExit(file);
        TestSequenceFileAppend.fs.deleteOnExit(sortedFile);
    }
}

