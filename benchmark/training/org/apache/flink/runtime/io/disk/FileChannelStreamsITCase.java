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
package org.apache.flink.runtime.io.disk;


import FileIOChannel.ID;
import java.io.EOFException;
import java.util.List;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.runtime.io.disk.iomanager.BlockChannelReader;
import org.apache.flink.runtime.io.disk.iomanager.BlockChannelWriter;
import org.apache.flink.runtime.io.disk.iomanager.FileIOChannel;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.runtime.operators.testutils.PairGenerator;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.runtime.operators.testutils.PairGenerator.KeyMode.RANDOM;
import static org.apache.flink.runtime.operators.testutils.PairGenerator.ValueMode.RANDOM_LENGTH;


public class FileChannelStreamsITCase extends TestLogger {
    private static final long SEED = 649180756312423613L;

    private static final int KEY_MAX = Integer.MAX_VALUE;

    private static final int VALUE_SHORT_LENGTH = 114;

    private static final int VALUE_LONG_LENGTH = 112 * 1024;

    private static final int NUM_PAIRS_SHORT = 1000000;

    private static final int NUM_PAIRS_LONG = 3000;

    private static final int MEMORY_PAGE_SIZE = 32 * 1024;

    private static final int NUM_MEMORY_SEGMENTS = 3;

    private IOManager ioManager;

    private MemoryManager memManager;

    // --------------------------------------------------------------------------------------------
    @Test
    public void testWriteReadSmallRecords() {
        try {
            List<MemorySegment> memory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final PairGenerator generator = new PairGenerator(FileChannelStreamsITCase.SEED, FileChannelStreamsITCase.KEY_MAX, FileChannelStreamsITCase.VALUE_SHORT_LENGTH, RANDOM, RANDOM_LENGTH);
            final FileIOChannel.ID channel = ioManager.createChannel();
            // create the writer output view
            final BlockChannelWriter<MemorySegment> writer = ioManager.createBlockChannelWriter(channel);
            final FileChannelOutputView outView = new FileChannelOutputView(writer, memManager, memory, FileChannelStreamsITCase.MEMORY_PAGE_SIZE);
            // write a number of pairs
            PairGenerator.Pair pair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_SHORT); i++) {
                generator.next(pair);
                pair.write(outView);
            }
            outView.close();
            // create the reader input view
            List<MemorySegment> readMemory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final BlockChannelReader<MemorySegment> reader = ioManager.createBlockChannelReader(channel);
            final FileChannelInputView inView = new FileChannelInputView(reader, memManager, readMemory, outView.getBytesInLatestSegment());
            generator.reset();
            // read and re-generate all records and compare them
            PairGenerator.Pair readPair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_SHORT); i++) {
                generator.next(pair);
                readPair.read(inView);
                Assert.assertEquals("The re-generated and the read record do not match.", pair, readPair);
            }
            inView.close();
            reader.deleteChannel();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWriteAndReadLongRecords() {
        try {
            final List<MemorySegment> memory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final PairGenerator generator = new PairGenerator(FileChannelStreamsITCase.SEED, FileChannelStreamsITCase.KEY_MAX, FileChannelStreamsITCase.VALUE_LONG_LENGTH, RANDOM, RANDOM_LENGTH);
            final FileIOChannel.ID channel = this.ioManager.createChannel();
            // create the writer output view
            final BlockChannelWriter<MemorySegment> writer = this.ioManager.createBlockChannelWriter(channel);
            final FileChannelOutputView outView = new FileChannelOutputView(writer, memManager, memory, FileChannelStreamsITCase.MEMORY_PAGE_SIZE);
            // write a number of pairs
            PairGenerator.Pair pair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_LONG); i++) {
                generator.next(pair);
                pair.write(outView);
            }
            outView.close();
            // create the reader input view
            List<MemorySegment> readMemory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final BlockChannelReader<MemorySegment> reader = ioManager.createBlockChannelReader(channel);
            final FileChannelInputView inView = new FileChannelInputView(reader, memManager, readMemory, outView.getBytesInLatestSegment());
            generator.reset();
            // read and re-generate all records and compare them
            PairGenerator.Pair readPair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_LONG); i++) {
                generator.next(pair);
                readPair.read(inView);
                Assert.assertEquals("The re-generated and the read record do not match.", pair, readPair);
            }
            inView.close();
            reader.deleteChannel();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testReadTooMany() {
        try {
            final List<MemorySegment> memory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final PairGenerator generator = new PairGenerator(FileChannelStreamsITCase.SEED, FileChannelStreamsITCase.KEY_MAX, FileChannelStreamsITCase.VALUE_SHORT_LENGTH, RANDOM, RANDOM_LENGTH);
            final FileIOChannel.ID channel = this.ioManager.createChannel();
            // create the writer output view
            final BlockChannelWriter<MemorySegment> writer = this.ioManager.createBlockChannelWriter(channel);
            final FileChannelOutputView outView = new FileChannelOutputView(writer, memManager, memory, FileChannelStreamsITCase.MEMORY_PAGE_SIZE);
            // write a number of pairs
            PairGenerator.Pair pair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_SHORT); i++) {
                generator.next(pair);
                pair.write(outView);
            }
            outView.close();
            // create the reader input view
            List<MemorySegment> readMemory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final BlockChannelReader<MemorySegment> reader = ioManager.createBlockChannelReader(channel);
            final FileChannelInputView inView = new FileChannelInputView(reader, memManager, readMemory, outView.getBytesInLatestSegment());
            generator.reset();
            // read and re-generate all records and compare them
            try {
                PairGenerator.Pair readPair = new PairGenerator.Pair();
                for (int i = 0; i < ((FileChannelStreamsITCase.NUM_PAIRS_SHORT) + 1); i++) {
                    generator.next(pair);
                    readPair.read(inView);
                    Assert.assertEquals("The re-generated and the read record do not match.", pair, readPair);
                }
                Assert.fail("Expected an EOFException which did not occur.");
            } catch (EOFException eofex) {
                // expected
            }
            inView.close();
            reader.deleteChannel();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWriteReadOneBufferOnly() {
        try {
            final List<MemorySegment> memory = memManager.allocatePages(new DummyInvokable(), 1);
            final PairGenerator generator = new PairGenerator(FileChannelStreamsITCase.SEED, FileChannelStreamsITCase.KEY_MAX, FileChannelStreamsITCase.VALUE_SHORT_LENGTH, RANDOM, RANDOM_LENGTH);
            final FileIOChannel.ID channel = this.ioManager.createChannel();
            // create the writer output view
            final BlockChannelWriter<MemorySegment> writer = this.ioManager.createBlockChannelWriter(channel);
            final FileChannelOutputView outView = new FileChannelOutputView(writer, memManager, memory, FileChannelStreamsITCase.MEMORY_PAGE_SIZE);
            // write a number of pairs
            PairGenerator.Pair pair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_SHORT); i++) {
                generator.next(pair);
                pair.write(outView);
            }
            outView.close();
            // create the reader input view
            List<MemorySegment> readMemory = memManager.allocatePages(new DummyInvokable(), 1);
            final BlockChannelReader<MemorySegment> reader = ioManager.createBlockChannelReader(channel);
            final FileChannelInputView inView = new FileChannelInputView(reader, memManager, readMemory, outView.getBytesInLatestSegment());
            generator.reset();
            // read and re-generate all records and compare them
            PairGenerator.Pair readPair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_SHORT); i++) {
                generator.next(pair);
                readPair.read(inView);
                Assert.assertEquals("The re-generated and the read record do not match.", pair, readPair);
            }
            inView.close();
            reader.deleteChannel();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWriteReadNotAll() {
        try {
            final List<MemorySegment> memory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final PairGenerator generator = new PairGenerator(FileChannelStreamsITCase.SEED, FileChannelStreamsITCase.KEY_MAX, FileChannelStreamsITCase.VALUE_SHORT_LENGTH, RANDOM, RANDOM_LENGTH);
            final FileIOChannel.ID channel = this.ioManager.createChannel();
            // create the writer output view
            final BlockChannelWriter<MemorySegment> writer = this.ioManager.createBlockChannelWriter(channel);
            final FileChannelOutputView outView = new FileChannelOutputView(writer, memManager, memory, FileChannelStreamsITCase.MEMORY_PAGE_SIZE);
            // write a number of pairs
            PairGenerator.Pair pair = new PairGenerator.Pair();
            for (int i = 0; i < (FileChannelStreamsITCase.NUM_PAIRS_SHORT); i++) {
                generator.next(pair);
                pair.write(outView);
            }
            outView.close();
            // create the reader input view
            List<MemorySegment> readMemory = memManager.allocatePages(new DummyInvokable(), FileChannelStreamsITCase.NUM_MEMORY_SEGMENTS);
            final BlockChannelReader<MemorySegment> reader = ioManager.createBlockChannelReader(channel);
            final FileChannelInputView inView = new FileChannelInputView(reader, memManager, readMemory, outView.getBytesInLatestSegment());
            generator.reset();
            // read and re-generate all records and compare them
            PairGenerator.Pair readPair = new PairGenerator.Pair();
            for (int i = 0; i < ((FileChannelStreamsITCase.NUM_PAIRS_SHORT) / 2); i++) {
                generator.next(pair);
                readPair.read(inView);
                Assert.assertEquals("The re-generated and the read record do not match.", pair, readPair);
            }
            inView.close();
            reader.deleteChannel();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

