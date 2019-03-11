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
package org.apache.flink.api.common.typeutils;


import org.apache.flink.api.common.typeutils.CompositeTypeSerializerUtil.IntermediateCompatibilityResult;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializer;
import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterMigration;
import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterReconfiguration;
import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsIncompatibleWithTheNextSerializer;


/**
 * Tests for the {@link CompositeTypeSerializerUtil}.
 */
public class CompositeTypeSerializerUtilTest {
    // ------------------------------------------------------------------------------------------------
    // Tests for CompositeTypeSerializerUtil#constructIntermediateCompatibilityResult
    // ------------------------------------------------------------------------------------------------
    @Test
    public void testCompatibleAsIsIntermediateCompatibilityResult() {
        final TypeSerializerSnapshot<?>[] testSerializerSnapshots = new TypeSerializerSnapshot<?>[]{ SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializer("first serializer"), SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializer("second serializer") };
        final TypeSerializer<?>[] testNewSerializers = new TypeSerializer<?>[]{ new SchemaCompatibilityTestingSerializer("first serializer"), new SchemaCompatibilityTestingSerializer("second serializer") };
        IntermediateCompatibilityResult<?> intermediateCompatibilityResult = CompositeTypeSerializerUtil.constructIntermediateCompatibilityResult(testNewSerializers, testSerializerSnapshots);
        Assert.assertTrue(intermediateCompatibilityResult.isCompatibleAsIs());
        Assert.assertTrue(intermediateCompatibilityResult.getFinalResult().isCompatibleAsIs());
        Assert.assertArrayEquals(testNewSerializers, intermediateCompatibilityResult.getNestedSerializers());
    }

    @Test
    public void testCompatibleWithReconfiguredSerializerIntermediateCompatibilityResult() {
        final TypeSerializerSnapshot<?>[] testSerializerSnapshots = new TypeSerializerSnapshot<?>[]{ SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializer("a"), SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterReconfiguration("b") };
        final TypeSerializer<?>[] testNewSerializers = new TypeSerializer<?>[]{ new SchemaCompatibilityTestingSerializer("a"), new SchemaCompatibilityTestingSerializer("b") };
        IntermediateCompatibilityResult<?> intermediateCompatibilityResult = CompositeTypeSerializerUtil.constructIntermediateCompatibilityResult(testNewSerializers, testSerializerSnapshots);
        final TypeSerializer<?>[] expectedReconfiguredNestedSerializers = new TypeSerializer<?>[]{ new SchemaCompatibilityTestingSerializer("a"), new SchemaCompatibilityTestingSerializer("b") };
        Assert.assertTrue(intermediateCompatibilityResult.isCompatibleWithReconfiguredSerializer());
        Assert.assertArrayEquals(expectedReconfiguredNestedSerializers, intermediateCompatibilityResult.getNestedSerializers());
    }

    @Test
    public void testCompatibleAfterMigrationIntermediateCompatibilityResult() {
        final TypeSerializerSnapshot<?>[] testSerializerSnapshots = new TypeSerializerSnapshot<?>[]{ SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterReconfiguration("a"), SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterMigration("b"), SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializer("c") };
        final TypeSerializer<?>[] testNewSerializers = new TypeSerializer<?>[]{ new SchemaCompatibilityTestingSerializer("a"), new SchemaCompatibilityTestingSerializer("b"), new SchemaCompatibilityTestingSerializer("c") };
        IntermediateCompatibilityResult<?> intermediateCompatibilityResult = CompositeTypeSerializerUtil.constructIntermediateCompatibilityResult(testNewSerializers, testSerializerSnapshots);
        Assert.assertTrue(intermediateCompatibilityResult.isCompatibleAfterMigration());
        Assert.assertTrue(intermediateCompatibilityResult.getFinalResult().isCompatibleAfterMigration());
    }

    @Test
    public void testIncompatibleIntermediateCompatibilityResult() {
        final TypeSerializerSnapshot<?>[] testSerializerSnapshots = new TypeSerializerSnapshot<?>[]{ thatIsCompatibleWithNextSerializer(), thatIsIncompatibleWithTheNextSerializer(), thatIsCompatibleWithNextSerializerAfterReconfiguration(), thatIsCompatibleWithNextSerializerAfterMigration() };
        final TypeSerializer<?>[] testNewSerializers = new TypeSerializer<?>[]{ new SchemaCompatibilityTestingSerializer(), new SchemaCompatibilityTestingSerializer(), new SchemaCompatibilityTestingSerializer(), new SchemaCompatibilityTestingSerializer() };
        IntermediateCompatibilityResult<?> intermediateCompatibilityResult = CompositeTypeSerializerUtil.constructIntermediateCompatibilityResult(testNewSerializers, testSerializerSnapshots);
        Assert.assertTrue(intermediateCompatibilityResult.isIncompatible());
        Assert.assertTrue(intermediateCompatibilityResult.getFinalResult().isIncompatible());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFinalResultOnUndefinedReconfigureIntermediateCompatibilityResultFails() {
        IntermediateCompatibilityResult<Integer> intermediateCompatibilityResult = IntermediateCompatibilityResult.undefinedReconfigureResult(new TypeSerializer[]{ IntSerializer.INSTANCE });
        intermediateCompatibilityResult.getFinalResult();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNestedSerializersOnCompatibleAfterMigrationIntermediateCompatibilityResultFails() {
        IntermediateCompatibilityResult<Integer> intermediateCompatibilityResult = IntermediateCompatibilityResult.definedCompatibleAfterMigrationResult();
        intermediateCompatibilityResult.getNestedSerializers();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNestedSerializersOnIncompatibleIntermediateCompatibilityResultFails() {
        IntermediateCompatibilityResult<Integer> intermediateCompatibilityResult = IntermediateCompatibilityResult.definedIncompatibleResult();
        intermediateCompatibilityResult.getNestedSerializers();
    }
}

