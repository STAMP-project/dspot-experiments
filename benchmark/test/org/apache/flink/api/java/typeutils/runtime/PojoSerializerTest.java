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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.operators.Keys.ExpressionKeys;
import org.apache.flink.api.common.operators.Keys.IncompatibleKeysException;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.CompositeType.FlatFieldDescriptor;
import org.apache.flink.api.common.typeutils.SerializerTestBase;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshotSerializationUtil;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test for the {@link PojoSerializer}.
 */
public class PojoSerializerTest extends SerializerTestBase<PojoSerializerTest.TestUserClass> {
    private TypeInformation<PojoSerializerTest.TestUserClass> type = TypeExtractor.getForClass(PojoSerializerTest.TestUserClass.class);

    // User code class for testing the serializer
    public static class TestUserClass {
        public int dumm1;

        public String dumm2;

        public double dumm3;

        public int[] dumm4;

        public Date dumm5;

        public PojoSerializerTest.NestedTestUserClass nestedClass;

        public TestUserClass() {
        }

        public TestUserClass(int dumm1, String dumm2, double dumm3, int[] dumm4, Date dumm5, PojoSerializerTest.NestedTestUserClass nestedClass) {
            this.dumm1 = dumm1;
            this.dumm2 = dumm2;
            this.dumm3 = dumm3;
            this.dumm4 = dumm4;
            this.dumm5 = dumm5;
            this.nestedClass = nestedClass;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dumm1, dumm2, dumm3, dumm4, nestedClass);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PojoSerializerTest.TestUserClass)) {
                return false;
            }
            PojoSerializerTest.TestUserClass otherTUC = ((PojoSerializerTest.TestUserClass) (other));
            if ((dumm1) != (otherTUC.dumm1)) {
                return false;
            }
            if ((((dumm2) == null) && ((otherTUC.dumm2) != null)) || (((dumm2) != null) && (!(dumm2.equals(otherTUC.dumm2))))) {
                return false;
            }
            if ((dumm3) != (otherTUC.dumm3)) {
                return false;
            }
            if (((((dumm4) != null) && ((otherTUC.dumm4) == null)) || (((dumm4) == null) && ((otherTUC.dumm4) != null))) || ((((dumm4) != null) && ((otherTUC.dumm4) != null)) && ((dumm4.length) != (otherTUC.dumm4.length)))) {
                return false;
            }
            if (((dumm4) != null) && ((otherTUC.dumm4) != null)) {
                for (int i = 0; i < (dumm4.length); i++) {
                    if ((dumm4[i]) != (otherTUC.dumm4[i])) {
                        return false;
                    }
                }
            }
            if ((((nestedClass) == null) && ((otherTUC.nestedClass) != null)) || (((nestedClass) != null) && (!(nestedClass.equals(otherTUC.nestedClass))))) {
                return false;
            }
            return true;
        }
    }

    public static class NestedTestUserClass {
        public int dumm1;

        public String dumm2;

        public double dumm3;

        public int[] dumm4;

        public NestedTestUserClass() {
        }

        public NestedTestUserClass(int dumm1, String dumm2, double dumm3, int[] dumm4) {
            this.dumm1 = dumm1;
            this.dumm2 = dumm2;
            this.dumm3 = dumm3;
            this.dumm4 = dumm4;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dumm1, dumm2, dumm3, dumm4);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PojoSerializerTest.NestedTestUserClass)) {
                return false;
            }
            PojoSerializerTest.NestedTestUserClass otherTUC = ((PojoSerializerTest.NestedTestUserClass) (other));
            if ((dumm1) != (otherTUC.dumm1)) {
                return false;
            }
            if (!(dumm2.equals(otherTUC.dumm2))) {
                return false;
            }
            if ((dumm3) != (otherTUC.dumm3)) {
                return false;
            }
            if ((dumm4.length) != (otherTUC.dumm4.length)) {
                return false;
            }
            for (int i = 0; i < (dumm4.length); i++) {
                if ((dumm4[i]) != (otherTUC.dumm4[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class SubTestUserClassA extends PojoSerializerTest.TestUserClass {
        public int subDumm1;

        public String subDumm2;

        public SubTestUserClassA() {
        }
    }

    public static class SubTestUserClassB extends PojoSerializerTest.TestUserClass {
        public Double subDumm1;

        public float subDumm2;

        public SubTestUserClassB() {
        }
    }

    /**
     * This tests if the hashes returned by the pojo and tuple comparators are the same
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testTuplePojoTestEquality() {
        // test with a simple, string-key first.
        PojoTypeInfo<PojoSerializerTest.TestUserClass> pType = ((PojoTypeInfo<PojoSerializerTest.TestUserClass>) (type));
        List<FlatFieldDescriptor> result = new ArrayList<FlatFieldDescriptor>();
        pType.getFlatFields("nestedClass.dumm2", 0, result);
        int[] fields = new int[1];// see below

        fields[0] = result.get(0).getPosition();
        TypeComparator<PojoSerializerTest.TestUserClass> pojoComp = pType.createComparator(fields, new boolean[]{ true }, 0, new ExecutionConfig());
        PojoSerializerTest.TestUserClass pojoTestRecord = new PojoSerializerTest.TestUserClass(0, "abc", 3.0, new int[]{ 1, 2, 3 }, new Date(), new PojoSerializerTest.NestedTestUserClass(1, "haha", 4.0, new int[]{ 5, 4, 3 }));
        int pHash = pojoComp.hash(pojoTestRecord);
        Tuple1<String> tupleTest = new Tuple1<String>("haha");
        TupleTypeInfo<Tuple1<String>> tType = ((TupleTypeInfo<Tuple1<String>>) (TypeExtractor.getForObject(tupleTest)));
        TypeComparator<Tuple1<String>> tupleComp = tType.createComparator(new int[]{ 0 }, new boolean[]{ true }, 0, new ExecutionConfig());
        int tHash = tupleComp.hash(tupleTest);
        Assert.assertTrue("The hashing for tuples and pojos must be the same, so that they are mixable", (pHash == tHash));
        Tuple3<Integer, String, Double> multiTupleTest = new Tuple3<Integer, String, Double>(1, "haha", 4.0);// its important here to use the same values.

        TupleTypeInfo<Tuple3<Integer, String, Double>> multiTupleType = ((TupleTypeInfo<Tuple3<Integer, String, Double>>) (TypeExtractor.getForObject(multiTupleTest)));
        ExpressionKeys fieldKey = new ExpressionKeys(new int[]{ 1, 0, 2 }, multiTupleType);
        ExpressionKeys expressKey = new ExpressionKeys(new String[]{ "nestedClass.dumm2", "nestedClass.dumm1", "nestedClass.dumm3" }, pType);
        try {
            Assert.assertTrue("Expecting the keys to be compatible", fieldKey.areCompatible(expressKey));
        } catch (IncompatibleKeysException e) {
            e.printStackTrace();
            Assert.fail(("Keys must be compatible: " + (e.getMessage())));
        }
        TypeComparator<PojoSerializerTest.TestUserClass> multiPojoComp = pType.createComparator(expressKey.computeLogicalKeyPositions(), new boolean[]{ true, true, true }, 0, new ExecutionConfig());
        int multiPojoHash = multiPojoComp.hash(pojoTestRecord);
        // pojo order is: dumm2 (str), dumm1 (int), dumm3 (double).
        TypeComparator<Tuple3<Integer, String, Double>> multiTupleComp = multiTupleType.createComparator(fieldKey.computeLogicalKeyPositions(), new boolean[]{ true, true, true }, 0, new ExecutionConfig());
        int multiTupleHash = multiTupleComp.hash(multiTupleTest);
        Assert.assertTrue("The hashing for tuples and pojos must be the same, so that they are mixable. Also for those with multiple key fields", (multiPojoHash == multiTupleHash));
    }

    // --------------------------------------------------------------------------------------------
    // Configuration snapshotting & reconfiguring tests
    // --------------------------------------------------------------------------------------------
    /**
     * Verifies that reconfiguring with a config snapshot of a preceding POJO serializer
     * with different POJO type will result in INCOMPATIBLE.
     */
    @Test
    public void testReconfigureWithDifferentPojoType() throws Exception {
        PojoSerializer<PojoSerializerTest.SubTestUserClassB> pojoSerializer1 = ((PojoSerializer<PojoSerializerTest.SubTestUserClassB>) (TypeExtractor.getForClass(PojoSerializerTest.SubTestUserClassB.class).createSerializer(new ExecutionConfig())));
        // snapshot configuration and serialize to bytes
        TypeSerializerSnapshot pojoSerializerConfigSnapshot = pojoSerializer1.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), pojoSerializerConfigSnapshot, pojoSerializer1);
            serializedConfig = out.toByteArray();
        }
        PojoSerializer<PojoSerializerTest.SubTestUserClassA> pojoSerializer2 = ((PojoSerializer<PojoSerializerTest.SubTestUserClassA>) (TypeExtractor.getForClass(PojoSerializerTest.SubTestUserClassA.class).createSerializer(new ExecutionConfig())));
        // read configuration again from bytes
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            pojoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), pojoSerializer2);
        }
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<PojoSerializerTest.SubTestUserClassA> compatResult = pojoSerializerConfigSnapshot.resolveSchemaCompatibility(pojoSerializer2);
        Assert.assertTrue(compatResult.isIncompatible());
    }

    /**
     * Tests that reconfiguration correctly reorders subclass registrations to their previous order.
     */
    @Test
    public void testReconfigureDifferentSubclassRegistrationOrder() throws Exception {
        ExecutionConfig executionConfig = new ExecutionConfig();
        executionConfig.registerPojoType(PojoSerializerTest.SubTestUserClassA.class);
        executionConfig.registerPojoType(PojoSerializerTest.SubTestUserClassB.class);
        PojoSerializer<PojoSerializerTest.TestUserClass> pojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (type.createSerializer(executionConfig)));
        // get original registration ids
        int subClassATag = pojoSerializer.getRegisteredClasses().get(PojoSerializerTest.SubTestUserClassA.class);
        int subClassBTag = pojoSerializer.getRegisteredClasses().get(PojoSerializerTest.SubTestUserClassB.class);
        // snapshot configuration and serialize to bytes
        TypeSerializerSnapshot pojoSerializerConfigSnapshot = pojoSerializer.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), pojoSerializerConfigSnapshot, pojoSerializer);
            serializedConfig = out.toByteArray();
        }
        // use new config and instantiate new PojoSerializer
        executionConfig = new ExecutionConfig();
        executionConfig.registerPojoType(PojoSerializerTest.SubTestUserClassB.class);// test with B registered before A

        executionConfig.registerPojoType(PojoSerializerTest.SubTestUserClassA.class);
        pojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (type.createSerializer(executionConfig)));
        // read configuration from bytes
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            pojoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), pojoSerializer);
        }
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<PojoSerializerTest.TestUserClass> compatResult = pojoSerializerConfigSnapshot.resolveSchemaCompatibility(pojoSerializer);
        Assert.assertTrue(compatResult.isCompatibleWithReconfiguredSerializer());
        Assert.assertTrue(((compatResult.getReconfiguredSerializer()) instanceof PojoSerializer));
        // reconfigure - check reconfiguration result and that registration ids remains the same
        // assertEquals(ReconfigureResult.COMPATIBLE, pojoSerializer.reconfigure(pojoSerializerConfigSnapshot));
        PojoSerializer<PojoSerializerTest.TestUserClass> reconfiguredPojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (compatResult.getReconfiguredSerializer()));
        Assert.assertEquals(subClassATag, reconfiguredPojoSerializer.getRegisteredClasses().get(PojoSerializerTest.SubTestUserClassA.class).intValue());
        Assert.assertEquals(subClassBTag, reconfiguredPojoSerializer.getRegisteredClasses().get(PojoSerializerTest.SubTestUserClassB.class).intValue());
    }

    /**
     * Tests that reconfiguration repopulates previously cached subclass serializers.
     */
    @Test
    public void testReconfigureRepopulateNonregisteredSubclassSerializerCache() throws Exception {
        // don't register any subclasses
        PojoSerializer<PojoSerializerTest.TestUserClass> pojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (type.createSerializer(new ExecutionConfig())));
        // create cached serializers for SubTestUserClassA and SubTestUserClassB
        pojoSerializer.getSubclassSerializer(PojoSerializerTest.SubTestUserClassA.class);
        pojoSerializer.getSubclassSerializer(PojoSerializerTest.SubTestUserClassB.class);
        Assert.assertEquals(2, pojoSerializer.getSubclassSerializerCache().size());
        Assert.assertTrue(pojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassA.class));
        Assert.assertTrue(pojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassB.class));
        // snapshot configuration and serialize to bytes
        TypeSerializerSnapshot pojoSerializerConfigSnapshot = pojoSerializer.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), pojoSerializerConfigSnapshot, pojoSerializer);
            serializedConfig = out.toByteArray();
        }
        // instantiate new PojoSerializer
        pojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (type.createSerializer(new ExecutionConfig())));
        // read configuration from bytes
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            pojoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), pojoSerializer);
        }
        // reconfigure - check reconfiguration result and that subclass serializer cache is repopulated
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<PojoSerializerTest.TestUserClass> compatResult = pojoSerializerConfigSnapshot.resolveSchemaCompatibility(pojoSerializer);
        Assert.assertTrue(compatResult.isCompatibleWithReconfiguredSerializer());
        Assert.assertTrue(((compatResult.getReconfiguredSerializer()) instanceof PojoSerializer));
        PojoSerializer<PojoSerializerTest.TestUserClass> reconfiguredPojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (compatResult.getReconfiguredSerializer()));
        Assert.assertEquals(2, reconfiguredPojoSerializer.getSubclassSerializerCache().size());
        Assert.assertTrue(reconfiguredPojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassA.class));
        Assert.assertTrue(reconfiguredPojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassB.class));
    }

    /**
     * Tests that:
     *  - Previous Pojo serializer did not have registrations, and created cached serializers for subclasses
     *  - On restore, it had those subclasses registered
     *
     * In this case, after reconfiguration, the cache should be repopulated, and registrations should
     * also exist for the subclasses.
     *
     * Note: the cache still needs to be repopulated because previous data of those subclasses were
     * written with the cached serializers. In this case, the repopulated cache has reconfigured serializers
     * for the subclasses so that previous written data can be read, but the registered serializers
     * for the subclasses do not necessarily need to be reconfigured since they will only be used to
     * write new data.
     */
    @Test
    public void testReconfigureWithPreviouslyNonregisteredSubclasses() throws Exception {
        // don't register any subclasses at first
        PojoSerializer<PojoSerializerTest.TestUserClass> pojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (type.createSerializer(new ExecutionConfig())));
        // create cached serializers for SubTestUserClassA and SubTestUserClassB
        pojoSerializer.getSubclassSerializer(PojoSerializerTest.SubTestUserClassA.class);
        pojoSerializer.getSubclassSerializer(PojoSerializerTest.SubTestUserClassB.class);
        // make sure serializers are in cache
        Assert.assertEquals(2, pojoSerializer.getSubclassSerializerCache().size());
        Assert.assertTrue(pojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassA.class));
        Assert.assertTrue(pojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassB.class));
        // make sure that registrations are empty
        Assert.assertTrue(pojoSerializer.getRegisteredClasses().isEmpty());
        Assert.assertEquals(0, pojoSerializer.getRegisteredSerializers().length);
        // snapshot configuration and serialize to bytes
        TypeSerializerSnapshot pojoSerializerConfigSnapshot = pojoSerializer.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), pojoSerializerConfigSnapshot, pojoSerializer);
            serializedConfig = out.toByteArray();
        }
        // instantiate new PojoSerializer, with new execution config that has the subclass registrations
        ExecutionConfig newExecutionConfig = new ExecutionConfig();
        newExecutionConfig.registerPojoType(PojoSerializerTest.SubTestUserClassA.class);
        newExecutionConfig.registerPojoType(PojoSerializerTest.SubTestUserClassB.class);
        pojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (type.createSerializer(newExecutionConfig)));
        // read configuration from bytes
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            pojoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), pojoSerializer);
        }
        // reconfigure - check reconfiguration result and that
        // 1) subclass serializer cache is repopulated
        // 2) registrations also contain the now registered subclasses
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<PojoSerializerTest.TestUserClass> compatResult = pojoSerializerConfigSnapshot.resolveSchemaCompatibility(pojoSerializer);
        Assert.assertTrue(compatResult.isCompatibleWithReconfiguredSerializer());
        Assert.assertTrue(((compatResult.getReconfiguredSerializer()) instanceof PojoSerializer));
        PojoSerializer<PojoSerializerTest.TestUserClass> reconfiguredPojoSerializer = ((PojoSerializer<PojoSerializerTest.TestUserClass>) (compatResult.getReconfiguredSerializer()));
        Assert.assertEquals(2, reconfiguredPojoSerializer.getSubclassSerializerCache().size());
        Assert.assertTrue(reconfiguredPojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassA.class));
        Assert.assertTrue(reconfiguredPojoSerializer.getSubclassSerializerCache().containsKey(PojoSerializerTest.SubTestUserClassB.class));
        Assert.assertEquals(2, reconfiguredPojoSerializer.getRegisteredClasses().size());
        Assert.assertTrue(reconfiguredPojoSerializer.getRegisteredClasses().containsKey(PojoSerializerTest.SubTestUserClassA.class));
        Assert.assertTrue(reconfiguredPojoSerializer.getRegisteredClasses().containsKey(PojoSerializerTest.SubTestUserClassB.class));
    }
}

