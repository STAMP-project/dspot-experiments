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


import CompressionType.NONE;
import SequenceFile.Reader;
import SequenceFile.Writer;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests sync based seek reads/write intervals inside SequenceFiles.
 */
public class TestSequenceFileSync {
    private static final int NUMRECORDS = 2000;

    private static final int RECORDSIZE = 80;

    private static final Random RAND = new Random();

    private static final String REC_FMT = "%d RECORDID %d : ";

    @Test
    public void testDefaultSyncInterval() throws IOException {
        // Uses the default sync interval of 100 KB
        final Configuration conf = new Configuration();
        final FileSystem fs = FileSystem.getLocal(conf);
        final Path path = new Path(GenericTestUtils.getTempPath("sequencefile.sync.test"));
        final IntWritable input = new IntWritable();
        final Text val = new Text();
        SequenceFile.Writer writer = new SequenceFile.Writer(conf, Writer.file(path), Writer.compression(NONE), Writer.keyClass(IntWritable.class), Writer.valueClass(Text.class));
        try {
            TestSequenceFileSync.writeSequenceFile(writer, ((TestSequenceFileSync.NUMRECORDS) * 4));
            for (int i = 0; i < 5; i++) {
                final SequenceFile.Reader reader;
                // try different SequenceFile.Reader constructors
                if ((i % 2) == 0) {
                    final int buffersize = conf.getInt("io.file.buffer.size", 4096);
                    reader = new SequenceFile.Reader(conf, Reader.file(path), Reader.bufferSize(buffersize));
                } else {
                    final FSDataInputStream in = fs.open(path);
                    final long length = fs.getFileStatus(path).getLen();
                    reader = new SequenceFile.Reader(conf, Reader.stream(in), Reader.start(0L), Reader.length(length));
                }
                try {
                    TestSequenceFileSync.forOffset(reader, input, val, i, 0, 0);
                    TestSequenceFileSync.forOffset(reader, input, val, i, 65, 0);
                    // There would be over 1000 records within
                    // this sync interval
                    TestSequenceFileSync.forOffset(reader, input, val, i, 2000, 1101);
                    TestSequenceFileSync.forOffset(reader, input, val, i, 0, 0);
                } finally {
                    reader.close();
                }
            }
        } finally {
            fs.delete(path, false);
        }
    }

    @Test
    public void testLowSyncpoint() throws IOException {
        // Uses a smaller sync interval of 2000 bytes
        final Configuration conf = new Configuration();
        final FileSystem fs = FileSystem.getLocal(conf);
        final Path path = new Path(GenericTestUtils.getTempPath("sequencefile.sync.test"));
        final IntWritable input = new IntWritable();
        final Text val = new Text();
        SequenceFile.Writer writer = new SequenceFile.Writer(conf, Writer.file(path), Writer.compression(NONE), Writer.keyClass(IntWritable.class), Writer.valueClass(Text.class), Writer.syncInterval((20 * 100)));
        // Ensure the custom sync interval value is set
        Assert.assertEquals(writer.syncInterval, (20 * 100));
        try {
            TestSequenceFileSync.writeSequenceFile(writer, TestSequenceFileSync.NUMRECORDS);
            for (int i = 0; i < 5; i++) {
                final SequenceFile.Reader reader;
                // try different SequenceFile.Reader constructors
                if ((i % 2) == 0) {
                    final int bufferSize = conf.getInt("io.file.buffer.size", 4096);
                    reader = new SequenceFile.Reader(conf, Reader.file(path), Reader.bufferSize(bufferSize));
                } else {
                    final FSDataInputStream in = fs.open(path);
                    final long length = fs.getFileStatus(path).getLen();
                    reader = new SequenceFile.Reader(conf, Reader.stream(in), Reader.start(0L), Reader.length(length));
                }
                try {
                    TestSequenceFileSync.forOffset(reader, input, val, i, 0, 0);
                    TestSequenceFileSync.forOffset(reader, input, val, i, 65, 0);
                    // There would be only a few records within
                    // this sync interval
                    TestSequenceFileSync.forOffset(reader, input, val, i, 2000, 21);
                    TestSequenceFileSync.forOffset(reader, input, val, i, 0, 0);
                } finally {
                    reader.close();
                }
            }
        } finally {
            fs.delete(path, false);
        }
    }
}

