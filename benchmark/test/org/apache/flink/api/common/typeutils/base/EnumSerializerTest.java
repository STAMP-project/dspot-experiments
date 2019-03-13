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
package org.apache.flink.api.common.typeutils.base;


import EnumSerializer.EnumSerializerSnapshot;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshotSerializationUtil;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.util.InstantiationUtil;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class EnumSerializerTest extends TestLogger {
    @Test
    public void testPublicEnum() {
        testEnumSerializer(EnumSerializerTest.PrivateEnum.ONE, EnumSerializerTest.PrivateEnum.TWO, EnumSerializerTest.PrivateEnum.THREE);
    }

    @Test
    public void testPrivateEnum() {
        testEnumSerializer(EnumSerializerTest.PublicEnum.FOO, EnumSerializerTest.PublicEnum.BAR, EnumSerializerTest.PublicEnum.PETER, EnumSerializerTest.PublicEnum.NATHANIEL, EnumSerializerTest.PublicEnum.EMMA, EnumSerializerTest.PublicEnum.PAULA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyEnum() {
        new EnumSerializer(EnumSerializerTest.EmptyEnum.class);
    }

    @Test
    public void testReconfiguration() {
        // mock the previous ordering of enum constants to be BAR, PAULA, NATHANIEL
        EnumSerializerTest.PublicEnum[] mockPreviousOrder = new EnumSerializerTest.PublicEnum[]{ EnumSerializerTest.PublicEnum.BAR, EnumSerializerTest.PublicEnum.PAULA, EnumSerializerTest.PublicEnum.NATHANIEL };
        // now, the actual order of FOO, BAR, PETER, NATHANIEL, EMMA, PAULA will be the "new wrong order"
        EnumSerializer<EnumSerializerTest.PublicEnum> serializer = new EnumSerializer(EnumSerializerTest.PublicEnum.class);
        // verify that the serializer is first using the "wrong order" (i.e., the initial new configuration)
        Assert.assertEquals(EnumSerializerTest.PublicEnum.FOO.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.FOO).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.BAR.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.BAR).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PETER.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PETER).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.NATHANIEL.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.NATHANIEL).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.EMMA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.EMMA).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PAULA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PAULA).intValue());
        // reconfigure and verify compatibility
        EnumSerializer.EnumSerializerSnapshot serializerSnapshot = new EnumSerializer.EnumSerializerSnapshot(EnumSerializerTest.PublicEnum.class, mockPreviousOrder);
        TypeSerializerSchemaCompatibility compatibility = serializerSnapshot.resolveSchemaCompatibility(serializer);
        Assert.assertTrue(compatibility.isCompatibleWithReconfiguredSerializer());
        // after reconfiguration, the order should be first the original BAR, PAULA, NATHANIEL,
        // followed by the "new enum constants" FOO, PETER, EMMA
        EnumSerializerTest.PublicEnum[] expectedOrder = new EnumSerializerTest.PublicEnum[]{ EnumSerializerTest.PublicEnum.BAR, EnumSerializerTest.PublicEnum.PAULA, EnumSerializerTest.PublicEnum.NATHANIEL, EnumSerializerTest.PublicEnum.FOO, EnumSerializerTest.PublicEnum.PETER, EnumSerializerTest.PublicEnum.EMMA };
        EnumSerializer<EnumSerializerTest.PublicEnum> configuredSerializer = ((EnumSerializer<EnumSerializerTest.PublicEnum>) (compatibility.getReconfiguredSerializer()));
        int i = 0;
        for (EnumSerializerTest.PublicEnum constant : expectedOrder) {
            Assert.assertEquals(i, configuredSerializer.getValueToOrdinal().get(constant).intValue());
            i++;
        }
        Assert.assertTrue(Arrays.equals(expectedOrder, configuredSerializer.getValues()));
    }

    @Test
    public void testConfigurationSnapshotSerialization() throws Exception {
        EnumSerializer<EnumSerializerTest.PublicEnum> serializer = new EnumSerializer(EnumSerializerTest.PublicEnum.class);
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), serializer.snapshotConfiguration(), serializer);
            serializedConfig = out.toByteArray();
        }
        TypeSerializerSnapshot<EnumSerializerTest.PublicEnum> restoredConfig;
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            restoredConfig = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), serializer);
        }
        TypeSerializerSchemaCompatibility<EnumSerializerTest.PublicEnum> compatResult = restoredConfig.resolveSchemaCompatibility(serializer);
        Assert.assertTrue(compatResult.isCompatibleAsIs());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.FOO.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.FOO).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.BAR.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.BAR).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PETER.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PETER).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.NATHANIEL.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.NATHANIEL).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.EMMA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.EMMA).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PAULA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PAULA).intValue());
        Assert.assertTrue(Arrays.equals(EnumSerializerTest.PublicEnum.values(), serializer.getValues()));
    }

    @Test
    public void testSerializeEnumSerializer() throws Exception {
        EnumSerializer<EnumSerializerTest.PublicEnum> serializer = new EnumSerializer(EnumSerializerTest.PublicEnum.class);
        // verify original transient parameters
        Assert.assertEquals(EnumSerializerTest.PublicEnum.FOO.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.FOO).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.BAR.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.BAR).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PETER.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PETER).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.NATHANIEL.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.NATHANIEL).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.EMMA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.EMMA).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PAULA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PAULA).intValue());
        Assert.assertTrue(Arrays.equals(EnumSerializerTest.PublicEnum.values(), serializer.getValues()));
        byte[] serializedSerializer = InstantiationUtil.serializeObject(serializer);
        // deserialize and re-verify transient parameters
        serializer = InstantiationUtil.deserializeObject(serializedSerializer, Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.FOO.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.FOO).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.BAR.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.BAR).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PETER.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PETER).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.NATHANIEL.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.NATHANIEL).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.EMMA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.EMMA).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PAULA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PAULA).intValue());
        Assert.assertTrue(Arrays.equals(EnumSerializerTest.PublicEnum.values(), serializer.getValues()));
    }

    @Test
    public void testSerializeReconfiguredEnumSerializer() throws Exception {
        // mock the previous ordering of enum constants to be BAR, PAULA, NATHANIEL
        EnumSerializerTest.PublicEnum[] mockPreviousOrder = new EnumSerializerTest.PublicEnum[]{ EnumSerializerTest.PublicEnum.BAR, EnumSerializerTest.PublicEnum.PAULA, EnumSerializerTest.PublicEnum.NATHANIEL };
        // now, the actual order of FOO, BAR, PETER, NATHANIEL, EMMA, PAULA will be the "new wrong order"
        EnumSerializer<EnumSerializerTest.PublicEnum> serializer = new EnumSerializer(EnumSerializerTest.PublicEnum.class);
        // verify that the serializer is first using the "wrong order" (i.e., the initial new configuration)
        Assert.assertEquals(EnumSerializerTest.PublicEnum.FOO.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.FOO).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.BAR.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.BAR).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PETER.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PETER).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.NATHANIEL.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.NATHANIEL).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.EMMA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.EMMA).intValue());
        Assert.assertEquals(EnumSerializerTest.PublicEnum.PAULA.ordinal(), serializer.getValueToOrdinal().get(EnumSerializerTest.PublicEnum.PAULA).intValue());
        // reconfigure and verify compatibility
        EnumSerializer.EnumSerializerSnapshot serializerSnapshot = new EnumSerializer.EnumSerializerSnapshot(EnumSerializerTest.PublicEnum.class, mockPreviousOrder);
        TypeSerializerSchemaCompatibility compatibility = serializerSnapshot.resolveSchemaCompatibility(serializer);
        Assert.assertTrue(compatibility.isCompatibleWithReconfiguredSerializer());
        // verify that after the serializer was read, the reconfigured constant ordering is untouched
        EnumSerializerTest.PublicEnum[] expectedOrder = new EnumSerializerTest.PublicEnum[]{ EnumSerializerTest.PublicEnum.BAR, EnumSerializerTest.PublicEnum.PAULA, EnumSerializerTest.PublicEnum.NATHANIEL, EnumSerializerTest.PublicEnum.FOO, EnumSerializerTest.PublicEnum.PETER, EnumSerializerTest.PublicEnum.EMMA };
        EnumSerializer<EnumSerializerTest.PublicEnum> configuredSerializer = ((EnumSerializer<EnumSerializerTest.PublicEnum>) (compatibility.getReconfiguredSerializer()));
        int i = 0;
        for (EnumSerializerTest.PublicEnum constant : expectedOrder) {
            Assert.assertEquals(i, configuredSerializer.getValueToOrdinal().get(constant).intValue());
            i++;
        }
        Assert.assertTrue(Arrays.equals(expectedOrder, configuredSerializer.getValues()));
    }

    // ------------------------------------------------------------------------
    // Test enums
    // ------------------------------------------------------------------------
    public enum PublicEnum {

        FOO,
        BAR,
        PETER,
        NATHANIEL,
        EMMA,
        PAULA;}

    public enum EmptyEnum {
        ;
    }

    private enum PrivateEnum {

        ONE,
        TWO,
        THREE;}
}

