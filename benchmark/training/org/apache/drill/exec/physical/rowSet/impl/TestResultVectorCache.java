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
package org.apache.drill.exec.physical.rowSet.impl;


import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import MinorType.INT;
import MinorType.VARCHAR;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.exec.physical.rowSet.ResultVectorCache;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(RowSetTests.class)
public class TestResultVectorCache extends SubOperatorTest {
    @Test
    public void testIsPromotable() {
        final MaterializedField required = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(REQUIRED).build());
        // Type is promotable to itself
        Assert.assertTrue(required.isPromotableTo(required, true));
        Assert.assertTrue(required.isPromotableTo(required, false));
        // Required is promotable to null
        final MaterializedField nullable = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(OPTIONAL).build());
        Assert.assertTrue(required.isPromotableTo(nullable, true));
        Assert.assertFalse(required.isPromotableTo(nullable, false));
        // Nullable not promotable to required
        Assert.assertFalse(nullable.isPromotableTo(required, true));
        // Arrays cannot be promoted to/from other types
        final MaterializedField repeated = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(REPEATED).build());
        Assert.assertFalse(required.isPromotableTo(repeated, true));
        Assert.assertFalse(nullable.isPromotableTo(repeated, true));
        Assert.assertFalse(repeated.isPromotableTo(required, true));
        Assert.assertFalse(repeated.isPromotableTo(nullable, true));
        // Narrower precision promotable to wider
        final MaterializedField narrow = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(10).build());
        final MaterializedField wide = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(20).build());
        final MaterializedField unset = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).build());
        Assert.assertTrue(narrow.isPromotableTo(wide, false));
        Assert.assertTrue(unset.isPromotableTo(narrow, false));
        Assert.assertTrue(unset.isPromotableTo(wide, false));
        Assert.assertFalse(wide.isPromotableTo(narrow, false));
        Assert.assertFalse(narrow.isPromotableTo(unset, false));
    }

    @Test
    public void testBasics() {
        final ResultVectorCache cache = new ResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        // Create a vector
        final MaterializedField required = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(REQUIRED).build());
        final ValueVector vector1 = cache.addOrGet(required);
        Assert.assertTrue(vector1.getField().isEquivalent(required));
        // Request the same schema, should get the same vector.
        final ValueVector vector2 = cache.addOrGet(required);
        Assert.assertSame(vector1, vector2);
        // Non-permissive. Change in mode means different vector.
        final MaterializedField optional = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(OPTIONAL).build());
        final ValueVector vector3 = cache.addOrGet(optional);
        Assert.assertTrue(vector3.getField().isEquivalent(optional));
        Assert.assertNotSame(vector1, vector3);
        // Asking for the required type again produces a new vector.
        // Name is the key, and we can have only one type associated
        // with each name.
        final ValueVector vector4 = cache.addOrGet(required);
        Assert.assertTrue(vector4.getField().isEquivalent(required));
        Assert.assertNotSame(vector3, vector4);
        Assert.assertNotSame(vector1, vector4);
        // Varchar, no precision.
        final MaterializedField varchar1 = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).build());
        final ValueVector vector5 = cache.addOrGet(varchar1);
        Assert.assertTrue(vector5.getField().isEquivalent(varchar1));
        // Varchar, with precision, no match.
        final MaterializedField varchar2 = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(10).build());
        final ValueVector vector6 = cache.addOrGet(varchar2);
        Assert.assertTrue(vector6.getField().isEquivalent(varchar2));
        Assert.assertNotSame(vector5, vector6);
        // Does match if same precision.
        final ValueVector vector7 = cache.addOrGet(varchar2);
        Assert.assertTrue(vector7.getField().isEquivalent(varchar2));
        Assert.assertSame(vector6, vector7);
        // Different names have different types
        final MaterializedField varchar3 = MaterializedField.create("b", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(10).build());
        final ValueVector vector8 = cache.addOrGet(varchar3);
        Assert.assertTrue(vector8.getField().isEquivalent(varchar3));
        Assert.assertSame(vector7, cache.addOrGet(varchar2));
        Assert.assertSame(vector8, cache.addOrGet(varchar3));
        close();
    }

    @Test
    public void testPermissive() {
        final ResultVectorCache cache = new ResultVectorCacheImpl(SubOperatorTest.fixture.allocator(), true);
        // Create a nullable vector
        final MaterializedField optional = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(OPTIONAL).build());
        final ValueVector vector1 = cache.addOrGet(optional);
        // Ask for a required version of the same name and type.
        // Should return the nullable version.
        final MaterializedField required = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(REQUIRED).build());
        final ValueVector vector2 = cache.addOrGet(required);
        Assert.assertTrue(vector2.getField().isEquivalent(optional));
        Assert.assertSame(vector1, vector2);
        // Repeat with Varchar
        final MaterializedField varchar1 = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).build());
        final ValueVector vector3 = cache.addOrGet(varchar1);
        final MaterializedField varchar2 = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).build());
        final ValueVector vector4 = cache.addOrGet(varchar2);
        Assert.assertSame(vector3, vector4);
        // Larger precision. Needs new vector.
        final MaterializedField varchar3 = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).setPrecision(10).build());
        final ValueVector vector5 = cache.addOrGet(varchar3);
        Assert.assertTrue(vector5.getField().isEquivalent(varchar3));
        Assert.assertNotSame(vector4, vector5);
        // Smaller precision, reuse vector.
        final ValueVector vector6 = cache.addOrGet(varchar1);
        Assert.assertTrue(vector6.getField().isEquivalent(varchar3));
        Assert.assertSame(vector5, vector6);
        // Same precision, required: reuse vector.
        final MaterializedField varchar4 = MaterializedField.create("a", MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).setPrecision(5).build());
        final ValueVector vector7 = cache.addOrGet(varchar4);
        Assert.assertTrue(vector7.getField().isEquivalent(varchar3));
        Assert.assertSame(vector5, vector7);
        // TODO: Repeat with decimal precision and scale.
        close();
    }

    @Test
    public void testClose() {
        final ResultVectorCache cache = new ResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        // Create a vector
        final MaterializedField required = MaterializedField.create("a", MajorType.newBuilder().setMinorType(INT).setMode(REQUIRED).build());
        final IntVector vector1 = ((IntVector) (cache.addOrGet(required)));
        vector1.allocateNew(100);
        // Close the cache. Note: close is on the implementation, not
        // the interface, because only the implementation should decide
        // when to close.
        // Close should release the allocated vector. If not, then
        // this test suite will fail with a memory leak when shutting
        // down the root allocator.
        close();
        Assert.assertEquals(0, vector1.getBuffer().capacity());
    }
}

