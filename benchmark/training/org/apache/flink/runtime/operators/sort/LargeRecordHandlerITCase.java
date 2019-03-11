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
package org.apache.flink.runtime.operators.sort;


import FileIOChannel.ID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.core.memory.MemoryType;
import org.apache.flink.runtime.io.disk.FileChannelOutputView;
import org.apache.flink.runtime.io.disk.SeekableFileChannelInputView;
import org.apache.flink.runtime.io.disk.iomanager.FileIOChannel;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.disk.iomanager.IOManagerAsync;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.types.Value;
import org.apache.flink.util.MutableObjectIterator;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class LargeRecordHandlerITCase extends TestLogger {
    @Test
    public void testRecordHandlerCompositeKey() {
        final IOManager ioMan = new IOManagerAsync();
        final int PAGE_SIZE = 4 * 1024;
        final int NUM_PAGES = 1000;
        final int NUM_RECORDS = 10;
        try {
            final MemoryManager memMan = new MemoryManager((NUM_PAGES * PAGE_SIZE), 1, PAGE_SIZE, MemoryType.HEAP, true);
            final AbstractInvokable owner = new DummyInvokable();
            final List<MemorySegment> initialMemory = memMan.allocatePages(owner, 6);
            final List<MemorySegment> sortMemory = memMan.allocatePages(owner, (NUM_PAGES - 6));
            final TypeInformation<?>[] types = new TypeInformation<?>[]{ BasicTypeInfo.LONG_TYPE_INFO, new org.apache.flink.api.java.typeutils.ValueTypeInfo<LargeRecordHandlerITCase.SomeVeryLongValue>(LargeRecordHandlerITCase.SomeVeryLongValue.class), BasicTypeInfo.BYTE_TYPE_INFO };
            final TupleTypeInfo<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> typeInfo = new TupleTypeInfo<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>>(types);
            final TypeSerializer<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> serializer = typeInfo.createSerializer(new ExecutionConfig());
            final TypeComparator<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> comparator = typeInfo.createComparator(new int[]{ 2, 0 }, new boolean[]{ true, true }, 0, new ExecutionConfig());
            LargeRecordHandler<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> handler = new LargeRecordHandler<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>>(serializer, comparator, ioMan, memMan, initialMemory, owner, 128);
            Assert.assertFalse(handler.hasData());
            // add the test data
            Random rnd = new Random();
            for (int i = 0; i < NUM_RECORDS; i++) {
                long val = rnd.nextLong();
                handler.addRecord(new Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>(val, new LargeRecordHandlerITCase.SomeVeryLongValue(((int) (val))), ((byte) (val))));
                Assert.assertTrue(handler.hasData());
            }
            MutableObjectIterator<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> sorted = handler.finishWriteAndSortKeys(sortMemory);
            try {
                handler.addRecord(new Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>(92L, null, ((byte) (1))));
                Assert.fail("should throw an exception");
            } catch (IllegalStateException e) {
                // expected
            }
            Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte> previous = null;
            Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte> next;
            while ((next = sorted.next(null)) != null) {
                // key and value must be equal
                Assert.assertTrue(((next.f0.intValue()) == (next.f1.val())));
                Assert.assertTrue(((next.f0.byteValue()) == (next.f2)));
                // order must be correct
                if (previous != null) {
                    Assert.assertTrue(((previous.f2) <= (next.f2)));
                    Assert.assertTrue((((previous.f2.byteValue()) != (next.f2.byteValue())) || ((previous.f0) <= (next.f0))));
                }
                previous = next;
            } 
            handler.close();
            Assert.assertFalse(handler.hasData());
            handler.close();
            try {
                handler.addRecord(new Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>(92L, null, ((byte) (1))));
                Assert.fail("should throw an exception");
            } catch (IllegalStateException e) {
                // expected
            }
            Assert.assertTrue(memMan.verifyEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ioMan.shutdown();
        }
    }

    public static final class SomeVeryLongValue implements Value {
        private static final long serialVersionUID = 1L;

        private static final byte[] BUFFER = new byte[50000000];

        static {
            for (int i = 0; i < (LargeRecordHandlerITCase.SomeVeryLongValue.BUFFER.length); i++) {
                LargeRecordHandlerITCase.SomeVeryLongValue.BUFFER[i] = ((byte) (i));
            }
        }

        private int val;

        public SomeVeryLongValue() {
        }

        public SomeVeryLongValue(int val) {
            this.val = val;
        }

        public int val() {
            return val;
        }

        @Override
        public void read(DataInputView in) throws IOException {
            val = in.readInt();
            for (byte bufferByte : LargeRecordHandlerITCase.SomeVeryLongValue.BUFFER) {
                byte b = in.readByte();
                Assert.assertEquals(bufferByte, b);
            }
        }

        @Override
        public void write(DataOutputView out) throws IOException {
            out.writeInt(val);
            out.write(LargeRecordHandlerITCase.SomeVeryLongValue.BUFFER);
        }
    }

    @Test
    public void fileTest() {
        final IOManager ioMan = new IOManagerAsync();
        final int PAGE_SIZE = 4 * 1024;
        final int NUM_PAGES = 4;
        final int NUM_RECORDS = 10;
        FileIOChannel.ID channel = null;
        try {
            final MemoryManager memMan = new MemoryManager((NUM_PAGES * PAGE_SIZE), 1, PAGE_SIZE, MemoryType.HEAP, true);
            final AbstractInvokable owner = new DummyInvokable();
            final List<MemorySegment> memory = memMan.allocatePages(owner, NUM_PAGES);
            final TypeInformation<?>[] types = new TypeInformation<?>[]{ BasicTypeInfo.LONG_TYPE_INFO, new org.apache.flink.api.java.typeutils.ValueTypeInfo<LargeRecordHandlerITCase.SomeVeryLongValue>(LargeRecordHandlerITCase.SomeVeryLongValue.class), BasicTypeInfo.BYTE_TYPE_INFO };
            final TupleTypeInfo<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> typeInfo = new TupleTypeInfo<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>>(types);
            final TypeSerializer<Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>> serializer = typeInfo.createSerializer(new ExecutionConfig());
            channel = ioMan.createChannel();
            FileChannelOutputView out = new FileChannelOutputView(ioMan.createBlockChannelWriter(channel), memMan, memory, PAGE_SIZE);
            // add the test data
            Random rnd = new Random();
            List<Long> offsets = new ArrayList<Long>();
            for (int i = 0; i < NUM_RECORDS; i++) {
                offsets.add(out.getWriteOffset());
                long val = rnd.nextLong();
                Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte> next = new Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte>(val, new LargeRecordHandlerITCase.SomeVeryLongValue(((int) (val))), ((byte) (val)));
                serializer.serialize(next, out);
            }
            out.close();
            for (int i = 1; i < (offsets.size()); i++) {
                Assert.assertTrue(((offsets.get(i)) > (offsets.get((i - 1)))));
            }
            memMan.allocatePages(owner, memory, NUM_PAGES);
            SeekableFileChannelInputView in = new SeekableFileChannelInputView(ioMan, channel, memMan, memory, out.getBytesInLatestSegment());
            for (int i = 0; i < NUM_RECORDS; i++) {
                in.seek(offsets.get(i));
                Tuple3<Long, LargeRecordHandlerITCase.SomeVeryLongValue, Byte> next = serializer.deserialize(in);
                // key and value must be equal
                Assert.assertTrue(((next.f0.intValue()) == (next.f1.val())));
                Assert.assertTrue(((next.f0.byteValue()) == (next.f2)));
            }
            in.closeAndDelete();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            if (channel != null) {
                try {
                    ioMan.deleteChannel(channel);
                } catch (IOException ignored) {
                }
            }
            ioMan.shutdown();
        }
    }
}

