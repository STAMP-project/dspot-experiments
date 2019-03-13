/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.record.vector;


import ImmutableMap.Builder;
import TypeProtos.MajorType;
import TypeProtos.MinorType.LIST;
import TypeProtos.MinorType.VARCHAR;
import UInt4Holder.TYPE;
import UInt4Vector.Accessor;
import UInt4Vector.Mutator;
import UserBitShared.SerializedField;
import io.netty.buffer.DrillBuf;
import java.nio.charset.Charset;
import junit.framework.TestCase;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.common.AutoCloseables;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.exception.OversizedAllocationException;
import org.apache.drill.exec.expr.TypeHelper;
import org.apache.drill.exec.expr.holders.UInt1Holder;
import org.apache.drill.exec.expr.holders.UInt4Holder;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.proto.UserBitShared;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.vector.BaseValueVector;
import org.apache.drill.exec.vector.BitVector;
import org.apache.drill.exec.vector.NullableFloat4Vector;
import org.apache.drill.exec.vector.NullableUInt4Vector;
import org.apache.drill.exec.vector.NullableVarCharVector;
import org.apache.drill.exec.vector.RepeatedIntVector;
import org.apache.drill.exec.vector.UInt4Vector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.exec.vector.VariableWidthVector;
import org.apache.drill.exec.vector.complex.ListVector;
import org.apache.drill.exec.vector.complex.MapVector;
import org.apache.drill.exec.vector.complex.RepeatedListVector;
import org.apache.drill.exec.vector.complex.RepeatedMapVector;
import org.apache.drill.shaded.guava.com.google.common.base.Preconditions;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(VectorTest.class)
public class TestValueVector extends ExecTest {
    private static final String EMPTY_SCHEMA_PATH = "";

    private DrillConfig drillConfig;

    private BufferAllocator allocator;

    private static final Charset utf8Charset = Charset.forName("UTF-8");

    private static final byte[] STR1 = new String("AAAAA1").getBytes(TestValueVector.utf8Charset);

    private static final byte[] STR2 = new String("BBBBBBBBB2").getBytes(TestValueVector.utf8Charset);

    private static final byte[] STR3 = new String("CCCC3").getBytes(TestValueVector.utf8Charset);

    @Test(expected = OversizedAllocationException.class)
    @Category(UnlikelyTest.class)
    public void testFixedVectorReallocation() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, TYPE);
        final UInt4Vector vector = new UInt4Vector(field, allocator);
        // edge case 1: buffer size = max value capacity
        final int expectedValueCapacity = (BaseValueVector.MAX_ALLOCATION_SIZE) / 4;
        try {
            vector.allocateNew(expectedValueCapacity);
            Assert.assertEquals(expectedValueCapacity, vector.getValueCapacity());
            vector.reAlloc();
            Assert.assertEquals((expectedValueCapacity * 2), vector.getValueCapacity());
        } finally {
            vector.close();
        }
        // common case: value count < max value capacity
        try {
            vector.allocateNew(((BaseValueVector.MAX_ALLOCATION_SIZE) / 8));
            vector.reAlloc();// value allocation reaches to MAX_VALUE_ALLOCATION

            vector.reAlloc();// this should throw an IOOB

        } finally {
            vector.close();
        }
    }

    @Test(expected = OversizedAllocationException.class)
    @Category(UnlikelyTest.class)
    public void testBitVectorReallocation() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, TYPE);
        final BitVector vector = new BitVector(field, allocator);
        // edge case 1: buffer size ~ max value capacity
        final int expectedValueCapacity = 1 << 29;
        try {
            vector.allocateNew(expectedValueCapacity);
            Assert.assertEquals(expectedValueCapacity, vector.getValueCapacity());
            vector.reAlloc();
            Assert.assertEquals((expectedValueCapacity * 2), vector.getValueCapacity());
        } finally {
            vector.close();
        }
        // common: value count < MAX_VALUE_ALLOCATION
        try {
            vector.allocateNew(expectedValueCapacity);
            for (int i = 0; i < 3; i++) {
                vector.reAlloc();// expand buffer size

            }
            Assert.assertEquals(Integer.MAX_VALUE, vector.getValueCapacity());
            vector.reAlloc();// buffer size ~ max allocation

            Assert.assertEquals(Integer.MAX_VALUE, vector.getValueCapacity());
            vector.reAlloc();// overflow

        } finally {
            vector.close();
        }
    }

    @Test(expected = OversizedAllocationException.class)
    @Category(UnlikelyTest.class)
    public void testVariableVectorReallocation() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, TYPE);
        final VarCharVector vector = new VarCharVector(field, allocator);
        // edge case 1: value count = MAX_VALUE_ALLOCATION
        final int expectedAllocationInBytes = BaseValueVector.MAX_ALLOCATION_SIZE;
        final int expectedOffsetSize = 10;
        try {
            vector.allocateNew(expectedAllocationInBytes, 10);
            Assert.assertTrue((expectedOffsetSize <= (vector.getValueCapacity())));
            Assert.assertTrue((expectedAllocationInBytes <= (vector.getBuffer().capacity())));
            vector.reAlloc();
            Assert.assertTrue(((expectedOffsetSize * 2) <= (vector.getValueCapacity())));
            Assert.assertTrue(((expectedAllocationInBytes * 2) <= (vector.getBuffer().capacity())));
        } finally {
            vector.close();
        }
        // common: value count < MAX_VALUE_ALLOCATION
        try {
            vector.allocateNew(((BaseValueVector.MAX_ALLOCATION_SIZE) / 2), 0);
            vector.reAlloc();// value allocation reaches to MAX_VALUE_ALLOCATION

            vector.reAlloc();// this tests if it overflows

        } finally {
            vector.close();
        }
    }

    @Test
    public void testFixedType() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, TYPE);
        // Create a new value vector for 1024 integers.
        try (final UInt4Vector vector = new UInt4Vector(field, allocator)) {
            final UInt4Vector.Mutator m = vector.getMutator();
            vector.allocateNew(1024);
            // Put and set a few values
            m.setSafe(0, 100);
            m.setSafe(1, 101);
            m.setSafe(100, 102);
            m.setSafe(1022, 103);
            m.setSafe(1023, 104);
            final UInt4Vector.Accessor accessor = vector.getAccessor();
            Assert.assertEquals(100, accessor.get(0));
            Assert.assertEquals(101, accessor.get(1));
            Assert.assertEquals(102, accessor.get(100));
            Assert.assertEquals(103, accessor.get(1022));
            Assert.assertEquals(104, accessor.get(1023));
        }
    }

    @Test
    public void testNullableVarLen2() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableVarCharHolder.TYPE);
        // Create a new value vector for 1024 integers.
        try (final NullableVarCharVector vector = new NullableVarCharVector(field, allocator)) {
            final NullableVarCharVector.Mutator m = vector.getMutator();
            vector.allocateNew((1024 * 10), 1024);
            m.set(0, TestValueVector.STR1);
            m.set(1, TestValueVector.STR2);
            m.set(2, TestValueVector.STR3);
            // Check the sample strings.
            final NullableVarCharVector.Accessor accessor = vector.getAccessor();
            Assert.assertArrayEquals(TestValueVector.STR1, accessor.get(0));
            Assert.assertArrayEquals(TestValueVector.STR2, accessor.get(1));
            Assert.assertArrayEquals(TestValueVector.STR3, accessor.get(2));
            // Ensure null value throws.
            boolean b = false;
            try {
                vector.getAccessor().get(3);
            } catch (IllegalStateException e) {
                b = true;
            } finally {
                Assert.assertTrue(b);
            }
        }
    }

    @Test
    public void testRepeatedIntVector() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, RepeatedIntHolder.TYPE);
        // Create a new value vector.
        final RepeatedIntVector vector1 = new RepeatedIntVector(field, allocator);
        // Populate the vector.
        final int[] values = new int[]{ 2, 3, 5, 7, 11, 13, 17, 19, 23, 27 };// some tricksy primes

        final int nRecords = 7;
        final int nElements = values.length;
        vector1.allocateNew(nRecords, (nRecords * nElements));
        final RepeatedIntVector.Mutator mutator = vector1.getMutator();
        for (int recordIndex = 0; recordIndex < nRecords; ++recordIndex) {
            mutator.startNewValue(recordIndex);
            for (int elementIndex = 0; elementIndex < nElements; ++elementIndex) {
                mutator.add(recordIndex, (recordIndex * (values[elementIndex])));
            }
        }
        mutator.setValueCount(nRecords);
        // Verify the contents.
        final RepeatedIntVector.Accessor accessor1 = vector1.getAccessor();
        Assert.assertEquals(nRecords, accessor1.getValueCount());
        for (int recordIndex = 0; recordIndex < nRecords; ++recordIndex) {
            for (int elementIndex = 0; elementIndex < nElements; ++elementIndex) {
                final int value = accessor1.get(recordIndex, elementIndex);
                Assert.assertEquals((recordIndex * (values[elementIndex])), value);
            }
        }
        /* TODO(cwestin)
        the interface to load has changed
        // Serialize, reify, and verify.
        final DrillBuf[] buffers1 = vector1.getBuffers(false);
        final DrillBuf buffer1 = combineBuffers(allocator, buffers1);
        final RepeatedIntVector vector2 = new RepeatedIntVector(field, allocator);
        vector2.load(nRecords, nRecords * nElements, buffer1);

        final RepeatedIntVector.Accessor accessor2 = vector2.getAccessor();
        for(int recordIndex = 0; recordIndex < nRecords; ++recordIndex) {
        for(int elementIndex = 0; elementIndex < nElements; ++elementIndex) {
        final int value = accessor2.get(recordIndex, elementIndex);
        assertEquals(accessor1.get(recordIndex,  elementIndex), value);
        }
        }
         */
        vector1.close();
        /* TODO(cwestin)
        vector2.close();
        buffer1.release();
         */
    }

    @Test
    public void testVarCharVectorLoad() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, VarCharHolder.TYPE);
        // Create a new value vector for 1024 variable length strings.
        final VarCharVector vector1 = new VarCharVector(field, allocator);
        final VarCharVector.Mutator mutator = vector1.getMutator();
        vector1.allocateNew((1024 * 10), 1024);
        // Populate the vector.
        final StringBuilder stringBuilder = new StringBuilder();
        final int valueCount = 10;
        for (int i = 0; i < valueCount; ++i) {
            stringBuilder.append('x');
            mutator.setSafe(i, stringBuilder.toString().getBytes(TestValueVector.utf8Charset));
        }
        mutator.setValueCount(valueCount);
        Assert.assertEquals(valueCount, vector1.getAccessor().getValueCount());
        // Combine the backing buffers so we can load them into a new vector.
        final DrillBuf[] buffers1 = vector1.getBuffers(false);
        final DrillBuf buffer1 = TestValueVector.combineBuffers(allocator, buffers1);
        final VarCharVector vector2 = new VarCharVector(field, allocator);
        vector2.load(vector1.getMetadata(), buffer1);
        // Check the contents of the new vector.
        final VarCharVector.Accessor accessor = vector2.getAccessor();
        stringBuilder.setLength(0);
        for (int i = 0; i < valueCount; ++i) {
            stringBuilder.append('x');
            final Object object = accessor.getObject(i);
            Assert.assertEquals(stringBuilder.toString(), object.toString());
        }
        vector1.close();
        vector2.close();
        buffer1.release();
    }

    @Test
    public void testNullableVarCharVectorLoad() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableVarCharHolder.TYPE);
        // Create a new value vector for 1024 nullable variable length strings.
        final NullableVarCharVector vector1 = new NullableVarCharVector(field, allocator);
        final NullableVarCharVector.Mutator mutator = vector1.getMutator();
        vector1.allocateNew((1024 * 10), 1024);
        // Populate the vector.
        final StringBuilder stringBuilder = new StringBuilder();
        final int valueCount = 10;
        for (int i = 0; i < valueCount; ++i) {
            stringBuilder.append('x');
            mutator.set(i, stringBuilder.toString().getBytes(TestValueVector.utf8Charset));
        }
        // Check the contents.
        final NullableVarCharVector.Accessor accessor1 = vector1.getAccessor();
        stringBuilder.setLength(0);
        for (int i = 0; i < valueCount; ++i) {
            stringBuilder.append('x');
            final Object object = accessor1.getObject(i);
            Assert.assertEquals(stringBuilder.toString(), object.toString());
        }
        mutator.setValueCount(valueCount);
        Assert.assertEquals(valueCount, vector1.getAccessor().getValueCount());
        // Still ok after setting value count?
        stringBuilder.setLength(0);
        for (int i = 0; i < valueCount; ++i) {
            stringBuilder.append('x');
            final Object object = accessor1.getObject(i);
            Assert.assertEquals(stringBuilder.toString(), object.toString());
        }
        // Combine into a single buffer so we can load it into a new vector.
        final DrillBuf[] buffers1 = vector1.getBuffers(false);
        @SuppressWarnings("resource")
        final DrillBuf buffer1 = TestValueVector.combineBuffers(allocator, buffers1);
        @SuppressWarnings("resource")
        final NullableVarCharVector vector2 = new NullableVarCharVector(field, allocator);
        vector2.load(vector1.getMetadata(), buffer1);
        // Check the vector's contents.
        final NullableVarCharVector.Accessor accessor2 = vector2.getAccessor();
        stringBuilder.setLength(0);
        for (int i = 0; i < valueCount; ++i) {
            stringBuilder.append('x');
            final Object object = accessor2.getObject(i);
            Assert.assertEquals(stringBuilder.toString(), object.toString());
        }
        vector1.close();
        vector2.close();
        buffer1.release();
    }

    @Test
    public void testNullableFixedType() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableUInt4Holder.TYPE);
        // Create a new value vector for 1024 integers.
        try (final NullableUInt4Vector vector = new NullableUInt4Vector(field, allocator)) {
            final NullableUInt4Vector.Mutator m = vector.getMutator();
            vector.allocateNew(1024);
            // Put and set a few values
            m.set(0, 100);
            m.set(1, 101);
            m.set(100, 102);
            m.set(1022, 103);
            m.set(1023, 104);
            final NullableUInt4Vector.Accessor accessor = vector.getAccessor();
            Assert.assertEquals(100, accessor.get(0));
            Assert.assertEquals(101, accessor.get(1));
            Assert.assertEquals(102, accessor.get(100));
            Assert.assertEquals(103, accessor.get(1022));
            Assert.assertEquals(104, accessor.get(1023));
            // Ensure null values throw
            {
                boolean b = false;
                try {
                    accessor.get(3);
                } catch (IllegalStateException e) {
                    b = true;
                } finally {
                    Assert.assertTrue(b);
                }
            }
            vector.allocateNew(2048);
            {
                boolean b = false;
                try {
                    accessor.get(0);
                } catch (IllegalStateException e) {
                    b = true;
                } finally {
                    Assert.assertTrue(b);
                }
            }
            m.set(0, 100);
            m.set(1, 101);
            m.set(100, 102);
            m.set(1022, 103);
            m.set(1023, 104);
            Assert.assertEquals(100, accessor.get(0));
            Assert.assertEquals(101, accessor.get(1));
            Assert.assertEquals(102, accessor.get(100));
            Assert.assertEquals(103, accessor.get(1022));
            Assert.assertEquals(104, accessor.get(1023));
            // Ensure null values throw.
            {
                boolean b = false;
                try {
                    vector.getAccessor().get(3);
                } catch (IllegalStateException e) {
                    b = true;
                } finally {
                    Assert.assertTrue(b);
                }
            }
        }
    }

    @Test
    public void testNullableFloat() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableFloat4Holder.TYPE);
        // Create a new value vector for 1024 integers
        try (final NullableFloat4Vector vector = ((NullableFloat4Vector) (TypeHelper.getNewVector(field, allocator)))) {
            final NullableFloat4Vector.Mutator m = vector.getMutator();
            vector.allocateNew(1024);
            // Put and set a few values.
            m.set(0, 100.1F);
            m.set(1, 101.2F);
            m.set(100, 102.3F);
            m.set(1022, 103.4F);
            m.set(1023, 104.5F);
            final NullableFloat4Vector.Accessor accessor = vector.getAccessor();
            Assert.assertEquals(100.1F, accessor.get(0), 0);
            Assert.assertEquals(101.2F, accessor.get(1), 0);
            Assert.assertEquals(102.3F, accessor.get(100), 0);
            Assert.assertEquals(103.4F, accessor.get(1022), 0);
            Assert.assertEquals(104.5F, accessor.get(1023), 0);
            // Ensure null values throw.
            {
                boolean b = false;
                try {
                    vector.getAccessor().get(3);
                } catch (IllegalStateException e) {
                    b = true;
                } finally {
                    Assert.assertTrue(b);
                }
            }
            vector.allocateNew(2048);
            {
                boolean b = false;
                try {
                    accessor.get(0);
                } catch (IllegalStateException e) {
                    b = true;
                } finally {
                    Assert.assertTrue(b);
                }
            }
        }
    }

    @Test
    public void testBitVector() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, BitHolder.TYPE);
        // Create a new value vector for 1024 integers
        try (final BitVector vector = new BitVector(field, allocator)) {
            final BitVector.Mutator m = vector.getMutator();
            vector.allocateNew(1024);
            // Put and set a few values
            m.set(0, 1);
            m.set(1, 0);
            m.set(100, 0);
            m.set(1022, 1);
            m.setValueCount(1023);
            final BitVector.Accessor accessor = vector.getAccessor();
            Assert.assertEquals(1, accessor.get(0));
            Assert.assertEquals(0, accessor.get(1));
            Assert.assertEquals(0, accessor.get(100));
            Assert.assertEquals(1, accessor.get(1022));
            // test setting the same value twice
            m.set(0, 1);
            m.set(0, 1);
            m.set(1, 0);
            m.set(1, 0);
            m.setValueCount(2);
            Assert.assertEquals(1, accessor.get(0));
            Assert.assertEquals(0, accessor.get(1));
            // test toggling the values
            m.set(0, 0);
            m.set(1, 1);
            m.setValueCount(2);
            Assert.assertEquals(0, accessor.get(0));
            Assert.assertEquals(1, accessor.get(1));
            // Ensure unallocated space returns 0
            Assert.assertEquals(0, accessor.get(3));
        }
    }

    @Test
    public void testReAllocNullableFixedWidthVector() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableFloat4Holder.TYPE);
        // Create a new value vector for 1024 integers
        try (final NullableFloat4Vector vector = ((NullableFloat4Vector) (TypeHelper.getNewVector(field, allocator)))) {
            final NullableFloat4Vector.Mutator m = vector.getMutator();
            vector.allocateNew(1024);
            Assert.assertEquals(1024, vector.getValueCapacity());
            // Put values in indexes that fall within the initial allocation
            m.setSafe(0, 100.1F);
            m.setSafe(100, 102.3F);
            m.setSafe(1023, 104.5F);
            // Now try to put values in space that falls beyond the initial allocation
            m.setSafe(2000, 105.5F);
            // Check valueCapacity is more than initial allocation
            Assert.assertEquals((1024 * 2), vector.getValueCapacity());
            final NullableFloat4Vector.Accessor accessor = vector.getAccessor();
            Assert.assertEquals(100.1F, accessor.get(0), 0);
            Assert.assertEquals(102.3F, accessor.get(100), 0);
            Assert.assertEquals(104.5F, accessor.get(1023), 0);
            Assert.assertEquals(105.5F, accessor.get(2000), 0);
            // Set the valueCount to be more than valueCapacity of current allocation. This is possible for NullableValueVectors
            // as we don't call setSafe for null values, but we do call setValueCount when all values are inserted into the
            // vector
            m.setValueCount(((vector.getValueCapacity()) + 200));
        }
    }

    @Test
    public void testReAllocNullableVariableWidthVector() {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableVarCharHolder.TYPE);
        // Create a new value vector for 1024 integers
        try (final NullableVarCharVector vector = ((NullableVarCharVector) (TypeHelper.getNewVector(field, allocator)))) {
            final NullableVarCharVector.Mutator m = vector.getMutator();
            vector.allocateNew();
            int initialCapacity = vector.getValueCapacity();
            // Put values in indexes that fall within the initial allocation
            m.setSafe(0, TestValueVector.STR1, 0, TestValueVector.STR1.length);
            m.setSafe((initialCapacity - 1), TestValueVector.STR2, 0, TestValueVector.STR2.length);
            // Now try to put values in space that falls beyond the initial allocation
            m.setSafe((initialCapacity + 200), TestValueVector.STR3, 0, TestValueVector.STR3.length);
            // Check valueCapacity is more than initial allocation
            Assert.assertEquals((((initialCapacity + 1) * 2) - 1), vector.getValueCapacity());
            final NullableVarCharVector.Accessor accessor = vector.getAccessor();
            Assert.assertArrayEquals(TestValueVector.STR1, accessor.get(0));
            Assert.assertArrayEquals(TestValueVector.STR2, accessor.get((initialCapacity - 1)));
            Assert.assertArrayEquals(TestValueVector.STR3, accessor.get((initialCapacity + 200)));
            // Set the valueCount to be more than valueCapacity of current allocation. This is possible for NullableValueVectors
            // as we don't call setSafe for null values, but we do call setValueCount when the current batch is processed.
            m.setValueCount(((vector.getValueCapacity()) + 200));
        }
    }

    @Test
    public void testVVInitialCapacity() throws Exception {
        final MaterializedField[] fields = new MaterializedField[9];
        final ValueVector[] valueVectors = new ValueVector[9];
        fields[0] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, BitHolder.TYPE);
        fields[1] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, IntHolder.TYPE);
        fields[2] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, VarCharHolder.TYPE);
        fields[3] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, NullableVar16CharHolder.TYPE);
        fields[4] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, RepeatedFloat4Holder.TYPE);
        fields[5] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, RepeatedVarBinaryHolder.TYPE);
        fields[6] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, MapVector.TYPE);
        /* bit */
        fields[6].addChild(fields[0]);
        /* varchar */
        fields[6].addChild(fields[2]);
        fields[7] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, RepeatedMapVector.TYPE);
        /* int */
        fields[7].addChild(fields[1]);
        /* optional var16char */
        fields[7].addChild(fields[3]);
        fields[8] = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, RepeatedListVector.TYPE);
        /* int */
        fields[8].addChild(fields[1]);
        final int initialCapacity = 1024;
        try {
            for (int i = 0; i < (valueVectors.length); i++) {
                valueVectors[i] = TypeHelper.getNewVector(fields[i], allocator);
                valueVectors[i].setInitialCapacity(initialCapacity);
                valueVectors[i].allocateNew();
            }
            for (int i = 0; i < (valueVectors.length); i++) {
                final ValueVector vv = valueVectors[i];
                final int vvCapacity = vv.getValueCapacity();
                // this can't be equality because Nullables will be allocated using power of two sized buffers (thus need 1025
                // spots in one vector > power of two is 2048, available capacity will be 2048 => 2047)
                Assert.assertTrue(String.format("Incorrect value capacity for %s [%d]", vv.getField(), vvCapacity), (initialCapacity <= vvCapacity));
            }
        } finally {
            AutoCloseables.close(valueVectors);
        }
    }

    protected interface VectorVerifier {
        void verify(ValueVector vector) throws Exception;
    }

    protected static class ChildVerifier implements TestValueVector.VectorVerifier {
        public final MajorType[] types;

        public ChildVerifier(TypeProtos... childTypes) {
            this.types = Preconditions.checkNotNull(childTypes);
        }

        @Override
        public void verify(ValueVector vector) throws Exception {
            final String hint = String.format("%s failed the test case", vector.getClass().getSimpleName());
            final UserBitShared.SerializedField metadata = vector.getMetadata();
            final int actual = metadata.getChildCount();
            Assert.assertEquals(hint, types.length, actual);
            for (int i = 0; i < (types.length); i++) {
                final UserBitShared.SerializedField child = metadata.getChild(i);
                Assert.assertEquals(hint, types[i], child.getMajorType());
            }
        }
    }

    @Test
    public void testVectorMetadataIsAccurate() throws Exception {
        final TestValueVector.VectorVerifier noChild = new TestValueVector.ChildVerifier();
        final TestValueVector.VectorVerifier offsetChild = new TestValueVector.ChildVerifier(UInt4Holder.TYPE);
        final Builder<Class<? extends ValueVector>, TestValueVector.VectorVerifier> builder = ImmutableMap.builder();
        builder.put(UInt4Vector.class, noChild);
        builder.put(BitVector.class, noChild);
        builder.put(VarCharVector.class, offsetChild);
        builder.put(NullableVarCharVector.class, new TestValueVector.ChildVerifier(UInt1Holder.TYPE, Types.optional(VARCHAR)));
        builder.put(RepeatedListVector.class, new TestValueVector.ChildVerifier(UInt4Holder.TYPE, Types.LATE_BIND_TYPE));
        builder.put(MapVector.class, noChild);
        builder.put(RepeatedMapVector.class, offsetChild);
        final ImmutableMap<Class<? extends ValueVector>, TestValueVector.VectorVerifier> children = builder.build();
        testVectors(new TestValueVector.VectorVerifier() {
            @Override
            public void verify(ValueVector vector) throws Exception {
                final Class<?> klazz = vector.getClass();
                final TestValueVector.VectorVerifier verifier = children.get(klazz);
                verifier.verify(vector);
            }
        });
    }

    @Test
    public void testVectorCanLoadEmptyBuffer() throws Exception {
        final DrillBuf empty = allocator.getEmpty();
        testVectors(new TestValueVector.VectorVerifier() {
            @Override
            public void verify(ValueVector vector) {
                final String hint = String.format("%s failed the test case", vector.getClass().getSimpleName());
                final UserBitShared.SerializedField metadata = vector.getMetadata();
                Assert.assertEquals(hint, 0, metadata.getBufferLength());
                Assert.assertEquals(hint, 0, metadata.getValueCount());
                vector.load(metadata, empty);
                Assert.assertEquals(hint, 0, vector.getValueCapacity());
                Assert.assertEquals(hint, 0, vector.getAccessor().getValueCount());
                vector.clear();
            }
        });
    }

    @SuppressWarnings("resource")
    @Test
    public void testListVectorShouldNotThrowOversizedAllocationException() throws Exception {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, Types.optional(LIST));
        ListVector vector = new ListVector(field, allocator, null);
        ListVector vectorFrom = new ListVector(field, allocator, null);
        vectorFrom.allocateNew();
        for (int i = 0; i < 10000; i++) {
            vector.allocateNew();
            vector.copyFromSafe(0, 0, vectorFrom);
            vector.clear();
        }
        vectorFrom.clear();
        vector.clear();
    }

    /**
     * For VariableLengthVectors when we clear of the vector and then explicitly set the
     * ValueCount of zero, then it should not fail with IndexOutOfBoundException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVarLengthVector_SetCountZeroAfterClear() throws Exception {
        try {
            final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, VarCharHolder.TYPE);
            VariableWidthVector vector = new VarCharVector(field, allocator);
            vector.allocateNew();
            vector.clear();
            Assert.assertTrue(((vector.getAccessor().getValueCount()) == 0));
            vector.getMutator().setValueCount(0);
            Assert.assertTrue(((vector.getAccessor().getValueCount()) == 0));
        } catch (Exception ex) {
            TestCase.fail();
        }
    }

    /**
     * For VariableLengthVectors when we try to set value count greater than value count for which memory is allocated,
     * then it should fail with IndexOutOfBoundException.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVarLengthVector_SetOOBCount() throws Exception {
        final MaterializedField field = MaterializedField.create(TestValueVector.EMPTY_SCHEMA_PATH, VarCharHolder.TYPE);
        VariableWidthVector vector = new VarCharVector(field, allocator);
        try {
            vector.allocateNew(10, 1);
            vector.getMutator().setValueCount(4);
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof IndexOutOfBoundsException));
        } finally {
            vector.clear();
        }
    }
}

