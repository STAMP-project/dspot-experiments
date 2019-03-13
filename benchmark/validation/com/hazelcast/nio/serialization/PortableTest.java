/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.nio.serialization;


import FieldType.LONG;
import FieldType.PORTABLE;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.PortableContext;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PortableTest {
    static final int PORTABLE_FACTORY_ID = TestSerializationConstants.PORTABLE_FACTORY_ID;

    static final int IDENTIFIED_FACTORY_ID = TestSerializationConstants.DATA_SERIALIZABLE_FACTORY_ID;

    @Test
    public void testBasics() {
        testBasics(ByteOrder.BIG_ENDIAN, false);
    }

    @Test
    public void testBasicsLittleEndian() {
        testBasics(ByteOrder.LITTLE_ENDIAN, false);
    }

    @Test
    public void testBasicsNativeOrder() {
        testBasics(ByteOrder.nativeOrder(), false);
    }

    @Test
    public void testBasicsNativeOrderUsingUnsafe() {
        testBasics(ByteOrder.nativeOrder(), true);
    }

    @Test
    public void testRawData() {
        int portableVersion = 1;
        final InternalSerializationService serializationService = PortableTest.createSerializationService(1);
        RawDataPortable p = new RawDataPortable(System.currentTimeMillis(), "test chars".toCharArray(), new NamedPortable("named portable", 34567), 9876, "Testing raw portable", new ByteArrayDataSerializable("test bytes".getBytes()));
        ClassDefinitionBuilder builder = new ClassDefinitionBuilder(p.getFactoryId(), p.getClassId(), portableVersion);
        builder.addLongField("l").addCharArrayField("c").addPortableField("p", PortableTest.createNamedPortableClassDefinition(portableVersion));
        serializationService.getPortableContext().registerClassDefinition(builder.build());
        final Data data = serializationService.toData(p);
        Assert.assertEquals(p, serializationService.toObject(data));
    }

    @Test
    public void testRawDataWithoutRegistering() {
        final SerializationService serializationService = PortableTest.createSerializationService(1);
        RawDataPortable p = new RawDataPortable(System.currentTimeMillis(), "test chars".toCharArray(), new NamedPortable("named portable", 34567), 9876, "Testing raw portable", new ByteArrayDataSerializable("test bytes".getBytes()));
        final Data data = serializationService.toData(p);
        Assert.assertEquals(p, serializationService.toObject(data));
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testRawDataInvalidWrite() {
        int portableVersion = 1;
        final InternalSerializationService serializationService = PortableTest.createSerializationService(1);
        RawDataPortable p = new InvalidRawDataPortable(System.currentTimeMillis(), "test chars".toCharArray(), new NamedPortable("named portable", 34567), 9876, "Testing raw portable", new ByteArrayDataSerializable("test bytes".getBytes()));
        ClassDefinitionBuilder builder = new ClassDefinitionBuilder(p.getFactoryId(), p.getClassId(), portableVersion);
        builder.addLongField("l").addCharArrayField("c").addPortableField("p", PortableTest.createNamedPortableClassDefinition(portableVersion));
        serializationService.getPortableContext().registerClassDefinition(builder.build());
        final Data data = serializationService.toData(p);
        Assert.assertEquals(p, serializationService.toObject(data));
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testRawDataInvalidRead() {
        int portableVersion = 1;
        final InternalSerializationService serializationService = PortableTest.createSerializationService(1);
        RawDataPortable p = new InvalidRawDataPortable2(System.currentTimeMillis(), "test chars".toCharArray(), new NamedPortable("named portable", 34567), 9876, "Testing raw portable", new ByteArrayDataSerializable("test bytes".getBytes()));
        ClassDefinitionBuilder builder = new ClassDefinitionBuilder(p.getFactoryId(), p.getClassId(), portableVersion);
        builder.addLongField("l").addCharArrayField("c").addPortableField("p", PortableTest.createNamedPortableClassDefinition(portableVersion));
        serializationService.getPortableContext().registerClassDefinition(builder.build());
        final Data data = serializationService.toData(p);
        Assert.assertEquals(p, serializationService.toObject(data));
    }

    @Test
    public void testClassDefinitionConfigWithErrors() throws Exception {
        SerializationConfig serializationConfig = new SerializationConfig();
        serializationConfig.addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableTest.TestPortableFactory());
        serializationConfig.setPortableVersion(1);
        serializationConfig.addClassDefinition(new ClassDefinitionBuilder(PortableTest.PORTABLE_FACTORY_ID, TestSerializationConstants.RAW_DATA_PORTABLE, 1).addLongField("l").addCharArrayField("c").addPortableField("p", PortableTest.createNamedPortableClassDefinition(1)).build());
        try {
            new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
            Assert.fail("Should throw HazelcastSerializationException!");
        } catch (HazelcastSerializationException ignored) {
        }
        new DefaultSerializationServiceBuilder().setConfig(serializationConfig).setCheckClassDefErrors(false).build();
        // -- OR --
        serializationConfig.setCheckClassDefErrors(false);
        new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
    }

    @Test
    public void testClassDefinitionConfig() throws Exception {
        int portableVersion = 1;
        SerializationConfig serializationConfig = new SerializationConfig();
        serializationConfig.addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableTest.TestPortableFactory());
        serializationConfig.setPortableVersion(portableVersion);
        serializationConfig.addClassDefinition(new ClassDefinitionBuilder(PortableTest.PORTABLE_FACTORY_ID, TestSerializationConstants.RAW_DATA_PORTABLE, portableVersion).addLongField("l").addCharArrayField("c").addPortableField("p", PortableTest.createNamedPortableClassDefinition(portableVersion)).build()).addClassDefinition(new ClassDefinitionBuilder(PortableTest.PORTABLE_FACTORY_ID, TestSerializationConstants.NAMED_PORTABLE, portableVersion).addUTFField("name").addIntField("myint").build());
        SerializationService serializationService = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        RawDataPortable p = new RawDataPortable(System.currentTimeMillis(), "test chars".toCharArray(), new NamedPortable("named portable", 34567), 9876, "Testing raw portable", new ByteArrayDataSerializable("test bytes".getBytes()));
        final Data data = serializationService.toData(p);
        Assert.assertEquals(p, serializationService.toObject(data));
    }

    @Test
    public void testPortableNestedInOthers() {
        SerializationService serializationService = PortableTest.createSerializationService(1);
        Object o1 = new ComplexDataSerializable(new NamedPortable("test-portable", 137), new ByteArrayDataSerializable("test-data-serializable".getBytes()), new ByteArrayDataSerializable("test-data-serializable-2".getBytes()));
        Data data = serializationService.toData(o1);
        SerializationService serializationService2 = PortableTest.createSerializationService(2);
        Object o2 = serializationService2.toObject(data);
        Assert.assertEquals(o1, o2);
    }

    // https://github.com/hazelcast/hazelcast/issues/1096
    @Test
    public void test_1096_ByteArrayContentSame() {
        SerializationService ss = new DefaultSerializationServiceBuilder().addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableTest.TestPortableFactory()).build();
        PortableTest.assertRepeatedSerialisationGivesSameByteArrays(ss, new NamedPortable("issue-1096", 1096));
        PortableTest.assertRepeatedSerialisationGivesSameByteArrays(ss, new InnerPortable(new byte[3], new char[5], new short[2], new int[10], new long[7], new float[9], new double[1], new NamedPortable[]{ new NamedPortable("issue-1096", 1096) }));
        PortableTest.assertRepeatedSerialisationGivesSameByteArrays(ss, new RawDataPortable(1096L, "issue-1096".toCharArray(), new NamedPortable("issue-1096", 1096), 1096, "issue-1096", new ByteArrayDataSerializable(new byte[1])));
    }

    // https://github.com/hazelcast/hazelcast/issues/2172
    @Test
    public void test_issue2172_WritePortableArray() {
        final SerializationService ss = new DefaultSerializationServiceBuilder().setInitialOutputBufferSize(16).build();
        final PortableTest.TestObject2[] testObject2s = new PortableTest.TestObject2[100];
        for (int i = 0; i < (testObject2s.length); i++) {
            testObject2s[i] = new PortableTest.TestObject2();
        }
        final PortableTest.TestObject1 testObject1 = new PortableTest.TestObject1(testObject2s);
        ss.toData(testObject1);
    }

    @Test
    public void testClassDefinitionLookupBigEndianHeapData() throws IOException {
        InternalSerializationService ss = new DefaultSerializationServiceBuilder().setByteOrder(ByteOrder.BIG_ENDIAN).build();
        PortableTest.testClassDefinitionLookup(ss);
    }

    @Test
    public void testClassDefinitionLookupLittleEndianHeapData() throws IOException {
        InternalSerializationService ss = new DefaultSerializationServiceBuilder().setByteOrder(ByteOrder.LITTLE_ENDIAN).build();
        PortableTest.testClassDefinitionLookup(ss);
    }

    @Test
    public void testClassDefinitionLookupNativeOrderHeapData() throws IOException {
        InternalSerializationService ss = new DefaultSerializationServiceBuilder().setUseNativeByteOrder(true).build();
        PortableTest.testClassDefinitionLookup(ss);
    }

    @Test
    public void testSerializationService_createPortableReader() throws IOException {
        InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        long timestamp1 = System.nanoTime();
        PortableTest.ChildPortableObject child = new PortableTest.ChildPortableObject(timestamp1);
        long timestamp2 = System.currentTimeMillis();
        PortableTest.ParentPortableObject parent = new PortableTest.ParentPortableObject(timestamp2, child);
        long timestamp3 = timestamp1 + timestamp2;
        PortableTest.GrandParentPortableObject grandParent = new PortableTest.GrandParentPortableObject(timestamp3, parent);
        Data data = serializationService.toData(grandParent);
        PortableReader reader = serializationService.createPortableReader(data);
        Assert.assertEquals(grandParent.timestamp, reader.readLong("timestamp"));
        Assert.assertEquals(parent.timestamp, reader.readLong("child.timestamp"));
        Assert.assertEquals(child.timestamp, reader.readLong("child.child.timestamp"));
    }

    @Test
    public void testClassDefinition_getNestedField() throws IOException {
        InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        PortableContext portableContext = serializationService.getPortableContext();
        PortableTest.ChildPortableObject child = new PortableTest.ChildPortableObject(System.nanoTime());
        PortableTest.ParentPortableObject parent = new PortableTest.ParentPortableObject(System.currentTimeMillis(), child);
        PortableTest.GrandParentPortableObject grandParent = new PortableTest.GrandParentPortableObject(System.nanoTime(), parent);
        Data data = serializationService.toData(grandParent);
        ClassDefinition classDefinition = portableContext.lookupClassDefinition(data);
        FieldDefinition fd = portableContext.getFieldDefinition(classDefinition, "child");
        Assert.assertNotNull(fd);
        Assert.assertEquals(PORTABLE, fd.getType());
        fd = portableContext.getFieldDefinition(classDefinition, "child.child");
        Assert.assertNotNull(fd);
        Assert.assertEquals(PORTABLE, fd.getType());
        fd = portableContext.getFieldDefinition(classDefinition, "child.child.timestamp");
        Assert.assertNotNull(fd);
        Assert.assertEquals(LONG, fd.getType());
    }

    @Test
    public void testWriteRead_withNullPortableArray() {
        int portableVersion = 1;
        ClassDefinitionBuilder builder0 = new ClassDefinitionBuilder(PortableTest.PORTABLE_FACTORY_ID, 1, portableVersion);
        ClassDefinitionBuilder builder1 = new ClassDefinitionBuilder(PortableTest.PORTABLE_FACTORY_ID, 2, portableVersion);
        builder0.addPortableArrayField("list", builder1.build());
        SerializationService ss = new DefaultSerializationServiceBuilder().setPortableVersion(portableVersion).addClassDefinition(builder0.build()).addClassDefinition(builder1.build()).build();
        Data data = ss.toData(new PortableTest.TestObject1());
        SerializationService ss2 = new DefaultSerializationServiceBuilder().addPortableFactory(1, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                switch (classId) {
                    case 1 :
                        return new PortableTest.TestObject1();
                    case 2 :
                        return new PortableTest.TestObject2();
                    default :
                        return null;
                }
            }
        }).build();
        Object object = ss2.toObject(data);
        Assert.assertNotNull(object);
        Assert.assertTrue((object instanceof PortableTest.TestObject1));
    }

    public static class GrandParentPortableObject implements Portable {
        long timestamp;

        PortableTest.ParentPortableObject child;

        public GrandParentPortableObject(long timestamp) {
            this.timestamp = timestamp;
            child = new PortableTest.ParentPortableObject(timestamp);
        }

        public GrandParentPortableObject(long timestamp, PortableTest.ParentPortableObject child) {
            this.timestamp = timestamp;
            this.child = child;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getClassId() {
            return 1;
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            timestamp = reader.readLong("timestamp");
            child = reader.readPortable("child");
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeLong("timestamp", timestamp);
            writer.writePortable("child", child);
        }
    }

    public static class ParentPortableObject implements Portable {
        long timestamp;

        PortableTest.ChildPortableObject child;

        public ParentPortableObject(long timestamp) {
            this.timestamp = timestamp;
            child = new PortableTest.ChildPortableObject(timestamp);
        }

        public ParentPortableObject(long timestamp, PortableTest.ChildPortableObject child) {
            this.timestamp = timestamp;
            this.child = child;
        }

        @Override
        public int getFactoryId() {
            return 2;
        }

        @Override
        public int getClassId() {
            return 2;
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            timestamp = reader.readLong("timestamp");
            child = reader.readPortable("child");
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeLong("timestamp", timestamp);
            writer.writePortable("child", child);
        }
    }

    public static class ChildPortableObject implements Portable {
        long timestamp;

        public ChildPortableObject(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public int getFactoryId() {
            return 3;
        }

        @Override
        public int getClassId() {
            return 3;
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            timestamp = reader.readLong("timestamp");
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeLong("timestamp", timestamp);
        }
    }

    static class TestObject1 implements Portable {
        private Portable[] portables;

        public TestObject1() {
        }

        public TestObject1(Portable[] p) {
            portables = p;
        }

        @Override
        public int getFactoryId() {
            return PortableTest.PORTABLE_FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return 1;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writePortableArray("list", portables);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            portables = reader.readPortableArray("list");
        }
    }

    static class TestObject2 implements Portable {
        private String shortString;

        public TestObject2() {
            shortString = "Hello World";
        }

        @Override
        public int getFactoryId() {
            return PortableTest.PORTABLE_FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return 2;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeUTF("shortString", shortString);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    public static class TestPortableFactory implements PortableFactory {
        @Override
        public Portable create(int classId) {
            switch (classId) {
                case TestSerializationConstants.MAIN_PORTABLE :
                    return new MainPortable();
                case TestSerializationConstants.INNER_PORTABLE :
                    return new InnerPortable();
                case TestSerializationConstants.NAMED_PORTABLE :
                    return new NamedPortable();
                case TestSerializationConstants.RAW_DATA_PORTABLE :
                    return new RawDataPortable();
                case TestSerializationConstants.INVALID_RAW_DATA_PORTABLE :
                    return new InvalidRawDataPortable();
                case TestSerializationConstants.INVALID_RAW_DATA_PORTABLE_2 :
                    return new InvalidRawDataPortable2();
                case TestSerializationConstants.OBJECT_CARRYING_PORTABLE :
                    return new ObjectCarryingPortable();
                case TestSerializationConstants.ALL_FIELD_OBJECT_PORTABLE :
                    return new SerializationV1Portable();
                default :
                    return null;
            }
        }
    }

    public static class TestDataSerializableFactory implements DataSerializableFactory {
        @Override
        public IdentifiedDataSerializable create(int typeId) {
            switch (typeId) {
                case TestSerializationConstants.SAMPLE_IDENTIFIED_DATA_SERIALIZABLE :
                    return new SampleIdentifiedDataSerializable();
                default :
                    return null;
            }
        }
    }

    @Test
    public void testWriteObject_withPortable() {
        SerializationService ss = new DefaultSerializationServiceBuilder().addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                return new NamedPortableV2();
            }
        }).build();
        SerializationService ss2 = new DefaultSerializationServiceBuilder().addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                return new NamedPortable();
            }
        }).setPortableVersion(5).build();
        Object o1 = new ComplexDataSerializable(new NamedPortableV2("test", 123, 500), new ByteArrayDataSerializable(new byte[3]), null);
        Data data = ss.toData(o1);
        Object o2 = ss2.toObject(data);
        Assert.assertEquals(o1, o2);
    }

    @Test
    public void testWriteData_withPortable() {
        SerializationService ss = new DefaultSerializationServiceBuilder().addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                return new NamedPortableV2();
            }
        }).build();
        SerializationService ss2 = new DefaultSerializationServiceBuilder().addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                return new NamedPortable();
            }
        }).setPortableVersion(5).build();
        Portable p1 = new NamedPortableV2("test", 456, 500);
        Object o1 = new DataDataSerializable(ss.toData(p1));
        Data data = ss.toData(o1);
        DataDataSerializable o2 = ss2.toObject(data);
        Assert.assertEquals(o1, o2);
        Portable p2 = ss2.toObject(o2.data);
        Assert.assertEquals(p1, p2);
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testGenericPortable_whenMultipleTypesAreUsed() {
        SerializationService ss = new DefaultSerializationServiceBuilder().addPortableFactory(1, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                switch (classId) {
                    case PortableTest.ParentGenericPortable.CLASS_ID :
                        return new PortableTest.ParentGenericPortable();
                    case PortableTest.ChildGenericPortable1.CLASS_ID :
                        return new PortableTest.ChildGenericPortable1();
                    case PortableTest.ChildGenericPortable2.CLASS_ID :
                        return new PortableTest.ChildGenericPortable2();
                    default :
                        throw new IllegalArgumentException();
                }
            }
        }).build();
        ss.toData(new PortableTest.ParentGenericPortable<PortableTest.ChildGenericPortable1>(new PortableTest.ChildGenericPortable1("aaa", "bbb")));
        Data data = ss.toData(new PortableTest.ParentGenericPortable<PortableTest.ChildGenericPortable2>(new PortableTest.ChildGenericPortable2("ccc")));
        ss.toObject(data);
    }

    @Test
    public void testWriteObjectWithPortable() {
        SerializationService serializationService = PortableTest.createSerializationService(1);
        NamedPortable namedPortable = new NamedPortable("name", 2);
        ObjectCarryingPortable objectCarryingPortable1 = new ObjectCarryingPortable(namedPortable);
        Data data = serializationService.toData(objectCarryingPortable1);
        ObjectCarryingPortable objectCarryingPortable2 = serializationService.toObject(data);
        Assert.assertEquals(objectCarryingPortable1, objectCarryingPortable2);
    }

    @Test
    public void testWriteObjectWithIdentifiedDataSerializable() {
        SerializationService serializationService = PortableTest.createSerializationService(1);
        SampleIdentifiedDataSerializable namedPortable = new SampleIdentifiedDataSerializable('c', 2);
        ObjectCarryingPortable objectCarryingPortable1 = new ObjectCarryingPortable(namedPortable);
        Data data = serializationService.toData(objectCarryingPortable1);
        ObjectCarryingPortable objectCarryingPortable2 = serializationService.toObject(data);
        Assert.assertEquals(objectCarryingPortable1, objectCarryingPortable2);
    }

    @Test
    public void testWriteObjectWithCustomSerializable() {
        SerializationConfig config = new SerializationConfig();
        SerializerConfig sc = new SerializerConfig().setImplementation(new CustomSerializationTest.FooXmlSerializer()).setTypeClass(CustomSerializationTest.Foo.class);
        config.addSerializerConfig(sc);
        SerializationService serializationService = new DefaultSerializationServiceBuilder().setPortableVersion(1).addPortableFactory(PortableTest.PORTABLE_FACTORY_ID, new PortableTest.TestPortableFactory()).setConfig(config).build();
        CustomSerializationTest.Foo foo = new CustomSerializationTest.Foo("f");
        ObjectCarryingPortable objectCarryingPortable1 = new ObjectCarryingPortable(foo);
        Data data = serializationService.toData(objectCarryingPortable1);
        ObjectCarryingPortable objectCarryingPortable2 = serializationService.toObject(data);
        Assert.assertEquals(objectCarryingPortable1, objectCarryingPortable2);
    }

    static class ParentGenericPortable<T extends Portable> implements Portable {
        static final int CLASS_ID = 1;

        T child;

        ParentGenericPortable() {
        }

        ParentGenericPortable(T child) {
            this.child = child;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getClassId() {
            return PortableTest.ParentGenericPortable.CLASS_ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writePortable("c", child);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            child = reader.readPortable("c");
        }
    }

    static class ChildGenericPortable1 implements Portable {
        static final int CLASS_ID = 2;

        String s1;

        String s2;

        ChildGenericPortable1() {
        }

        ChildGenericPortable1(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getClassId() {
            return PortableTest.ChildGenericPortable1.CLASS_ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeUTF("s1", s1);
            writer.writeUTF("s2", s2);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            s1 = reader.readUTF("s1");
            s2 = reader.readUTF("s2");
        }
    }

    static class ChildGenericPortable2 implements Portable {
        static final int CLASS_ID = 3;

        String s;

        ChildGenericPortable2() {
        }

        ChildGenericPortable2(String s1) {
            this.s = s1;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getClassId() {
            return PortableTest.ChildGenericPortable2.CLASS_ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeUTF("s", s);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            s = reader.readUTF("s");
        }
    }
}

