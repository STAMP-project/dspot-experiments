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


import java.io.IOException;
import java.util.Random;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.types.Value;
import org.apache.flink.util.MutableObjectIterator;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class ExternalSortLargeRecordsITCase extends TestLogger {
    private static final int MEMORY_SIZE = (1024 * 1024) * 78;

    private final AbstractInvokable parentTask = new DummyInvokable();

    private IOManager ioManager;

    private MemoryManager memoryManager;

    private boolean testSuccess;

    // --------------------------------------------------------------------------------------------
    @Test
    public void testSortWithLongRecordsOnly() {
        try {
            final int NUM_RECORDS = 10;
            final TypeInformation<?>[] types = new TypeInformation<?>[]{ BasicTypeInfo.LONG_TYPE_INFO, new org.apache.flink.api.java.typeutils.ValueTypeInfo<ExternalSortLargeRecordsITCase.SomeMaybeLongValue>(ExternalSortLargeRecordsITCase.SomeMaybeLongValue.class) };
            final TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> typeInfo = new TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>(types);
            final TypeSerializer<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> serializer = typeInfo.createSerializer(new ExecutionConfig());
            final TypeComparator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> comparator = typeInfo.createComparator(new int[]{ 0 }, new boolean[]{ false }, 0, new ExecutionConfig());
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> source = new MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>() {
                private final Random rnd = new Random(457821643089756298L);

                private int num = 0;

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> next(Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> reuse) {
                    return next();
                }

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> next() {
                    if (((num)++) < NUM_RECORDS) {
                        long val = rnd.nextLong();
                        return new Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>(val, new ExternalSortLargeRecordsITCase.SomeMaybeLongValue(((int) (val))));
                    } else {
                        return null;
                    }
                }
            };
            @SuppressWarnings("unchecked")
            Sorter<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> sorter = /* use large record handler */
            new UnilateralSortMerger<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>(this.memoryManager, this.ioManager, source, this.parentTask, new org.apache.flink.api.java.typeutils.runtime.RuntimeSerializerFactory<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>(serializer, ((Class<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>) ((Class<?>) (Tuple2.class)))), comparator, 1.0, 1, 128, 0.7F, true, false);
            // check order
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> iterator = sorter.getIterator();
            Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> val = serializer.createInstance();
            long prevKey = Long.MAX_VALUE;
            for (int i = 0; i < NUM_RECORDS; i++) {
                val = iterator.next(val);
                Assert.assertTrue(((val.f0) <= prevKey));
                Assert.assertTrue(((val.f0.intValue()) == (val.f1.val())));
            }
            Assert.assertNull(iterator.next(val));
            sorter.close();
            testSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSortWithLongAndShortRecordsMixed() {
        try {
            final int NUM_RECORDS = 1000000;
            final int LARGE_REC_INTERVAL = 100000;
            final TypeInformation<?>[] types = new TypeInformation<?>[]{ BasicTypeInfo.LONG_TYPE_INFO, new org.apache.flink.api.java.typeutils.ValueTypeInfo<ExternalSortLargeRecordsITCase.SomeMaybeLongValue>(ExternalSortLargeRecordsITCase.SomeMaybeLongValue.class) };
            final TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> typeInfo = new TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>(types);
            final TypeSerializer<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> serializer = typeInfo.createSerializer(new ExecutionConfig());
            final TypeComparator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> comparator = typeInfo.createComparator(new int[]{ 0 }, new boolean[]{ false }, 0, new ExecutionConfig());
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> source = new MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>() {
                private final Random rnd = new Random(145610843608763871L);

                private int num = -1;

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> next(Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> reuse) {
                    return next();
                }

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> next() {
                    if ((++(num)) < NUM_RECORDS) {
                        long val = rnd.nextLong();
                        return new Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>(val, new ExternalSortLargeRecordsITCase.SomeMaybeLongValue(((int) (val)), (((num) % LARGE_REC_INTERVAL) == 0)));
                    } else {
                        return null;
                    }
                }
            };
            @SuppressWarnings("unchecked")
            Sorter<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> sorter = /* use large record handler */
            new UnilateralSortMerger<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>(this.memoryManager, this.ioManager, source, this.parentTask, new org.apache.flink.api.java.typeutils.runtime.RuntimeSerializerFactory<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>(serializer, ((Class<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>>) ((Class<?>) (Tuple2.class)))), comparator, 1.0, 1, 128, 0.7F, true, true);
            // check order
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue>> iterator = sorter.getIterator();
            Tuple2<Long, ExternalSortLargeRecordsITCase.SomeMaybeLongValue> val = serializer.createInstance();
            long prevKey = Long.MAX_VALUE;
            for (int i = 0; i < NUM_RECORDS; i++) {
                val = iterator.next(val);
                Assert.assertTrue("Sort order violated", ((val.f0) <= prevKey));
                Assert.assertEquals("Serialization of test data type incorrect", val.f0.intValue(), val.f1.val());
            }
            Assert.assertNull(iterator.next(val));
            sorter.close();
            testSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSortWithShortMediumAndLargeRecords() {
        try {
            final int NUM_RECORDS = 50000;
            final int LARGE_REC_INTERVAL = 10000;
            final int MEDIUM_REC_INTERVAL = 500;
            final TypeInformation<?>[] types = new TypeInformation<?>[]{ BasicTypeInfo.LONG_TYPE_INFO, new org.apache.flink.api.java.typeutils.ValueTypeInfo<ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>(ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.class) };
            final TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> typeInfo = new TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>(types);
            final TypeSerializer<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> serializer = typeInfo.createSerializer(new ExecutionConfig());
            final TypeComparator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> comparator = typeInfo.createComparator(new int[]{ 0 }, new boolean[]{ false }, 0, new ExecutionConfig());
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> source = new MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>() {
                private final Random rnd = new Random(1456108743687167086L);

                private int num = -1;

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> next(Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> reuse) {
                    return next();
                }

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> next() {
                    if ((++(num)) < NUM_RECORDS) {
                        int size;
                        if (((num) % LARGE_REC_INTERVAL) == 0) {
                            size = ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.LARGE_SIZE;
                        } else
                            if (((num) % MEDIUM_REC_INTERVAL) == 0) {
                                size = ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.MEDIUM_SIZE;
                            } else {
                                size = ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.SMALL_SIZE;
                            }

                        long val = rnd.nextLong();
                        return new Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>(val, new ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue(((int) (val)), size));
                    } else {
                        return null;
                    }
                }
            };
            @SuppressWarnings("unchecked")
            Sorter<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> sorter = /* use large record handler */
            new UnilateralSortMerger<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>(this.memoryManager, this.ioManager, source, this.parentTask, new org.apache.flink.api.java.typeutils.runtime.RuntimeSerializerFactory<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>(serializer, ((Class<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>) ((Class<?>) (Tuple2.class)))), comparator, 1.0, 1, 128, 0.7F, true, false);
            // check order
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> iterator = sorter.getIterator();
            Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> val = serializer.createInstance();
            long prevKey = Long.MAX_VALUE;
            for (int i = 0; i < NUM_RECORDS; i++) {
                val = iterator.next(val);
                Assert.assertTrue(((val.f0) <= prevKey));
                Assert.assertTrue(((val.f0.intValue()) == (val.f1.val())));
            }
            Assert.assertNull(iterator.next(val));
            sorter.close();
            testSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSortWithMediumRecordsOnly() {
        try {
            final int NUM_RECORDS = 70;
            final TypeInformation<?>[] types = new TypeInformation<?>[]{ BasicTypeInfo.LONG_TYPE_INFO, new org.apache.flink.api.java.typeutils.ValueTypeInfo<ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>(ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.class) };
            final TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> typeInfo = new TupleTypeInfo<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>(types);
            final TypeSerializer<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> serializer = typeInfo.createSerializer(new ExecutionConfig());
            final TypeComparator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> comparator = typeInfo.createComparator(new int[]{ 0 }, new boolean[]{ false }, 0, new ExecutionConfig());
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> source = new MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>() {
                private final Random rnd = new Random(62360187263087678L);

                private int num = -1;

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> next(Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> reuse) {
                    return next();
                }

                @Override
                public Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> next() {
                    if ((++(num)) < NUM_RECORDS) {
                        long val = rnd.nextLong();
                        return new Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>(val, new ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue(((int) (val)), ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.MEDIUM_SIZE));
                    } else {
                        return null;
                    }
                }
            };
            @SuppressWarnings("unchecked")
            Sorter<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> sorter = /* use large record handler */
            new UnilateralSortMerger<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>(this.memoryManager, this.ioManager, source, this.parentTask, new org.apache.flink.api.java.typeutils.runtime.RuntimeSerializerFactory<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>(serializer, ((Class<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>>) ((Class<?>) (Tuple2.class)))), comparator, 1.0, 1, 128, 0.7F, true, true);
            // check order
            MutableObjectIterator<Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue>> iterator = sorter.getIterator();
            Tuple2<Long, ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue> val = serializer.createInstance();
            long prevKey = Long.MAX_VALUE;
            for (int i = 0; i < NUM_RECORDS; i++) {
                val = iterator.next(val);
                Assert.assertTrue(((val.f0) <= prevKey));
                Assert.assertTrue(((val.f0.intValue()) == (val.f1.val())));
            }
            Assert.assertNull(iterator.next(val));
            sorter.close();
            testSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class SomeMaybeLongValue implements Value {
        private static final long serialVersionUID = 1L;

        private static final byte[] BUFFER = new byte[(100 * 1024) * 1024];

        static {
            for (int i = 0; i < (ExternalSortLargeRecordsITCase.SomeMaybeLongValue.BUFFER.length); i++) {
                ExternalSortLargeRecordsITCase.SomeMaybeLongValue.BUFFER[i] = ((byte) (i));
            }
        }

        private int val;

        private boolean isLong;

        public SomeMaybeLongValue() {
            this.isLong = true;
        }

        public SomeMaybeLongValue(int val) {
            this.val = val;
            this.isLong = true;
        }

        public SomeMaybeLongValue(int val, boolean isLong) {
            this.val = val;
            this.isLong = isLong;
        }

        public int val() {
            return val;
        }

        public boolean isLong() {
            return isLong;
        }

        @Override
        public void read(DataInputView in) throws IOException {
            val = in.readInt();
            isLong = in.readBoolean();
            if (isLong) {
                for (int i = 0; i < (ExternalSortLargeRecordsITCase.SomeMaybeLongValue.BUFFER.length); i++) {
                    byte b = in.readByte();
                    Assert.assertEquals(ExternalSortLargeRecordsITCase.SomeMaybeLongValue.BUFFER[i], b);
                }
            }
        }

        @Override
        public void write(DataOutputView out) throws IOException {
            out.writeInt(val);
            out.writeBoolean(isLong);
            if (isLong) {
                out.write(ExternalSortLargeRecordsITCase.SomeMaybeLongValue.BUFFER);
            }
        }

        @Override
        public int hashCode() {
            return val;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof ExternalSortLargeRecordsITCase.SomeMaybeLongValue) && ((((ExternalSortLargeRecordsITCase.SomeMaybeLongValue) (obj)).val) == (this.val));
        }

        @Override
        public String toString() {
            return isLong ? "Large Value" : "Small Value";
        }
    }

    public static final class SmallOrMediumOrLargeValue implements Value {
        private static final long serialVersionUID = 1L;

        public static final int SMALL_SIZE = 0;

        public static final int MEDIUM_SIZE = (12 * 1024) * 1024;

        public static final int LARGE_SIZE = (100 * 1024) * 1024;

        private int val;

        private int size;

        public SmallOrMediumOrLargeValue() {
            this.size = ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.SMALL_SIZE;
        }

        public SmallOrMediumOrLargeValue(int val) {
            this.val = val;
            this.size = ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue.SMALL_SIZE;
        }

        public SmallOrMediumOrLargeValue(int val, int size) {
            this.val = val;
            this.size = size;
        }

        public int val() {
            return val;
        }

        public int getSize() {
            return size;
        }

        @Override
        public void read(DataInputView in) throws IOException {
            val = in.readInt();
            size = in.readInt();
            for (int i = 0; i < (size); i++) {
                byte b = in.readByte();
                Assert.assertEquals(((byte) (i)), b);
            }
        }

        @Override
        public void write(DataOutputView out) throws IOException {
            out.writeInt(val);
            out.writeInt(size);
            for (int i = 0; i < (size); i++) {
                out.write(((byte) (i)));
            }
        }

        @Override
        public int hashCode() {
            return val;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue) {
                ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue other = ((ExternalSortLargeRecordsITCase.SmallOrMediumOrLargeValue) (obj));
                return ((other.val) == (this.val)) && ((other.size) == (this.size));
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("Value %d (%d bytes)", val, size);
        }
    }
}

