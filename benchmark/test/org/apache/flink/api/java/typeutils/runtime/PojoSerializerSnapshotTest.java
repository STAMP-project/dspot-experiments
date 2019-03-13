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
package org.apache.flink.api.java.typeutils.runtime;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.base.DoubleSerializer;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterMigration;
import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsCompatibleWithNextSerializerAfterReconfiguration;
import static org.apache.flink.testutils.migration.SchemaCompatibilityTestingSerializer.SchemaCompatibilityTestingSnapshot.thatIsIncompatibleWithTheNextSerializer;


/**
 * Tests for the {@link PojoSerializerSnapshot}.
 */
public class PojoSerializerSnapshotTest {
    public static class TestPojo {
        public int id;

        public String name;

        public double height;

        public TestPojo() {
        }

        public TestPojo(int id, String name, double height) {
            this.id = id;
            this.name = name;
            this.height = height;
        }
    }

    private static class TestPojoField {
        String name;

        Field field;

        TypeSerializer<?> serializer;

        TypeSerializerSnapshot<?> serializerSnapshot;

        TestPojoField(String name, TypeSerializer<?> serializer) throws Exception {
            this.name = name;
            this.field = PojoSerializerSnapshotTest.TestPojo.class.getDeclaredField(name);
            this.serializer = serializer;
            this.serializerSnapshot = serializer.snapshotConfiguration();
        }

        TestPojoField(String name, Field field, TypeSerializerSnapshot<?> serializerSnapshot) {
            this.name = name;
            this.field = field;
            this.serializerSnapshot = serializerSnapshot;
        }

        PojoSerializerSnapshotTest.TestPojoField shallowCopy() {
            return new PojoSerializerSnapshotTest.TestPojoField(name, field, serializerSnapshot);
        }
    }

    private static final Map<String, Field> FIELDS = new HashMap<>(3);

    static {
        try {
            PojoSerializerSnapshotTest.FIELDS.put("id", PojoSerializerSnapshotTest.TestPojo.class.getDeclaredField("id"));
            PojoSerializerSnapshotTest.FIELDS.put("name", PojoSerializerSnapshotTest.TestPojo.class.getDeclaredField("name"));
            PojoSerializerSnapshotTest.FIELDS.put("height", PojoSerializerSnapshotTest.TestPojo.class.getDeclaredField("height"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PojoSerializerSnapshotTest.TestPojoField ID_FIELD;

    private static PojoSerializerSnapshotTest.TestPojoField NAME_FIELD;

    private static PojoSerializerSnapshotTest.TestPojoField HEIGHT_FIELD;

    static {
        try {
            PojoSerializerSnapshotTest.ID_FIELD = new PojoSerializerSnapshotTest.TestPojoField("id", IntSerializer.INSTANCE);
            PojoSerializerSnapshotTest.NAME_FIELD = new PojoSerializerSnapshotTest.TestPojoField("name", StringSerializer.INSTANCE);
            PojoSerializerSnapshotTest.HEIGHT_FIELD = new PojoSerializerSnapshotTest.TestPojoField("height", DoubleSerializer.INSTANCE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Tests for PojoSerializerSnapshot#restoreSerializer
    // ------------------------------------------------------------------------------------------------
    @Test
    public void testRestoreSerializerWithSameFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializer<PojoSerializerSnapshotTest.TestPojo> restoredSerializer = testSnapshot.restoreSerializer();
        Assert.assertSame(restoredSerializer.getClass(), PojoSerializer.class);
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> restoredPojoSerializer = ((PojoSerializer<PojoSerializerSnapshotTest.TestPojo>) (restoredSerializer));
        final Field[] restoredFields = restoredPojoSerializer.getFields();
        Assert.assertArrayEquals(new Field[]{ PojoSerializerSnapshotTest.ID_FIELD.field, PojoSerializerSnapshotTest.NAME_FIELD.field, PojoSerializerSnapshotTest.HEIGHT_FIELD.field }, restoredFields);
        final TypeSerializer<?>[] restoredFieldSerializers = restoredPojoSerializer.getFieldSerializers();
        Assert.assertArrayEquals(new TypeSerializer[]{ IntSerializer.INSTANCE, StringSerializer.INSTANCE, DoubleSerializer.INSTANCE }, restoredFieldSerializers);
    }

    @Test
    public void testRestoreSerializerWithRemovedFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.mockRemovedField(PojoSerializerSnapshotTest.ID_FIELD), PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.mockRemovedField(PojoSerializerSnapshotTest.HEIGHT_FIELD)));
        final TypeSerializer<PojoSerializerSnapshotTest.TestPojo> restoredSerializer = testSnapshot.restoreSerializer();
        Assert.assertTrue(((restoredSerializer.getClass()) == (PojoSerializer.class)));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> restoredPojoSerializer = ((PojoSerializer<PojoSerializerSnapshotTest.TestPojo>) (restoredSerializer));
        final Field[] restoredFields = restoredPojoSerializer.getFields();
        Assert.assertArrayEquals(new Field[]{ null, PojoSerializerSnapshotTest.NAME_FIELD.field, null }, restoredFields);
        final TypeSerializer<?>[] restoredFieldSerializers = restoredPojoSerializer.getFieldSerializers();
        Assert.assertArrayEquals(new TypeSerializer[]{ IntSerializer.INSTANCE, StringSerializer.INSTANCE, DoubleSerializer.INSTANCE }, restoredFieldSerializers);
    }

    @Test
    public void testRestoreSerializerWithNewFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Collections.singletonList(PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializer<PojoSerializerSnapshotTest.TestPojo> restoredSerializer = testSnapshot.restoreSerializer();
        Assert.assertTrue(((restoredSerializer.getClass()) == (PojoSerializer.class)));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> restoredPojoSerializer = ((PojoSerializer<PojoSerializerSnapshotTest.TestPojo>) (restoredSerializer));
        final Field[] restoredFields = restoredPojoSerializer.getFields();
        Assert.assertArrayEquals(new Field[]{ PojoSerializerSnapshotTest.HEIGHT_FIELD.field }, restoredFields);
        final TypeSerializer<?>[] restoredFieldSerializers = restoredPojoSerializer.getFieldSerializers();
        Assert.assertArrayEquals(new TypeSerializer[]{ DoubleSerializer.INSTANCE }, restoredFieldSerializers);
    }

    // ------------------------------------------------------------------------------------------------
    // Tests for PojoSerializerSnapshot#resolveSchemaCompatibility
    // ------------------------------------------------------------------------------------------------
    @Test
    public void testResolveSchemaCompatibilityWithSameFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isCompatibleAsIs());
    }

    @Test
    public void testResolveSchemaCompatibilityWithRemovedFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.mockRemovedField(PojoSerializerSnapshotTest.ID_FIELD), PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.mockRemovedField(PojoSerializerSnapshotTest.HEIGHT_FIELD)));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Collections.singletonList(PojoSerializerSnapshotTest.NAME_FIELD));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isCompatibleAfterMigration());
    }

    @Test
    public void testResolveSchemaCompatibilityWithNewFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Collections.singletonList(PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isCompatibleAfterMigration());
    }

    @Test
    public void testResolveSchemaCompatibilityWithNewAndRemovedFields() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Collections.singletonList(PojoSerializerSnapshotTest.mockRemovedField(PojoSerializerSnapshotTest.ID_FIELD)));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Arrays.asList(PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isCompatibleAfterMigration());
    }

    @Test
    public void testResolveSchemaCompatibilityWithIncompatibleFieldSerializers() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.mockFieldSerializerSnapshot(PojoSerializerSnapshotTest.NAME_FIELD, thatIsIncompatibleWithTheNextSerializer()), PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.mockFieldSerializer(PojoSerializerSnapshotTest.NAME_FIELD, new SchemaCompatibilityTestingSerializer()), PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isIncompatible());
    }

    @Test
    public void testResolveSchemaCompatibilityWithCompatibleAfterMigrationFieldSerializers() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.mockFieldSerializerSnapshot(PojoSerializerSnapshotTest.HEIGHT_FIELD, thatIsCompatibleWithNextSerializerAfterMigration())));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Arrays.asList(PojoSerializerSnapshotTest.ID_FIELD, PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.mockFieldSerializer(PojoSerializerSnapshotTest.HEIGHT_FIELD, new SchemaCompatibilityTestingSerializer())));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isCompatibleAfterMigration());
    }

    @Test
    public void testResolveSchemaCompatibilityWithCompatibleWithReconfigurationFieldSerializers() {
        final PojoSerializerSnapshot<PojoSerializerSnapshotTest.TestPojo> testSnapshot = PojoSerializerSnapshotTest.buildTestSnapshot(Arrays.asList(PojoSerializerSnapshotTest.mockFieldSerializerSnapshot(PojoSerializerSnapshotTest.ID_FIELD, thatIsCompatibleWithNextSerializerAfterReconfiguration()), PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> newPojoSerializer = PojoSerializerSnapshotTest.buildTestNewPojoSerializer(Arrays.asList(PojoSerializerSnapshotTest.mockFieldSerializer(PojoSerializerSnapshotTest.ID_FIELD, new SchemaCompatibilityTestingSerializer()), PojoSerializerSnapshotTest.NAME_FIELD, PojoSerializerSnapshotTest.HEIGHT_FIELD));
        final TypeSerializerSchemaCompatibility<PojoSerializerSnapshotTest.TestPojo> resultCompatibility = testSnapshot.resolveSchemaCompatibility(newPojoSerializer);
        Assert.assertTrue(resultCompatibility.isCompatibleWithReconfiguredSerializer());
        final TypeSerializer<PojoSerializerSnapshotTest.TestPojo> reconfiguredSerializer = resultCompatibility.getReconfiguredSerializer();
        Assert.assertSame(reconfiguredSerializer.getClass(), PojoSerializer.class);
        final PojoSerializer<PojoSerializerSnapshotTest.TestPojo> reconfiguredPojoSerializer = ((PojoSerializer<PojoSerializerSnapshotTest.TestPojo>) (reconfiguredSerializer));
        final TypeSerializer<?>[] reconfiguredFieldSerializers = reconfiguredPojoSerializer.getFieldSerializers();
        Assert.assertArrayEquals(new TypeSerializer[]{ new SchemaCompatibilityTestingSerializer(), StringSerializer.INSTANCE, DoubleSerializer.INSTANCE }, reconfiguredFieldSerializers);
    }
}

