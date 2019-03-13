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


import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Member;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.SimpleMemberImpl;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.internal.serialization.impl.JavaDefaultSerializers.JavaSerializer;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class SerializationTest extends HazelcastTestSupport {
    @Test
    public void testGlobalSerializer_withOverrideJavaSerializable() {
        GlobalSerializerConfig globalSerializerConfig = new GlobalSerializerConfig();
        globalSerializerConfig.setOverrideJavaSerialization(true);
        final AtomicInteger writeCounter = new AtomicInteger();
        final AtomicInteger readCounter = new AtomicInteger();
        final JavaSerializer javaSerializer = new JavaSerializer(true, false, null);
        SerializationConfig serializationConfig = new SerializationConfig().setGlobalSerializerConfig(globalSerializerConfig.setImplementation(new StreamSerializer<Object>() {
            @Override
            public void write(ObjectDataOutput out, Object v) throws IOException {
                writeCounter.incrementAndGet();
                if (v instanceof Serializable) {
                    out.writeBoolean(true);
                    javaSerializer.write(out, v);
                } else
                    if (v instanceof SerializationTest.DummyValue) {
                        out.writeBoolean(false);
                        out.writeUTF(((SerializationTest.DummyValue) (v)).s);
                        out.writeInt(((SerializationTest.DummyValue) (v)).k);
                    }

            }

            @Override
            public Object read(ObjectDataInput in) throws IOException {
                readCounter.incrementAndGet();
                boolean java = in.readBoolean();
                if (java) {
                    return javaSerializer.read(in);
                }
                return new SerializationTest.DummyValue(in.readUTF(), in.readInt());
            }

            public int getTypeId() {
                return 123;
            }

            public void destroy() {
            }
        }));
        SerializationService ss1 = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        SerializationTest.DummyValue value = new SerializationTest.DummyValue("test", 111);
        Data data1 = ss1.toData(value);
        Data data2 = ss1.toData(new SerializationTest.Foo());
        Assert.assertNotNull(data1);
        Assert.assertNotNull(data2);
        Assert.assertEquals(2, writeCounter.get());
        SerializationService ss2 = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        Object o1 = ss2.toObject(data1);
        Object o2 = ss2.toObject(data2);
        Assert.assertEquals(value, o1);
        Assert.assertNotNull(o2);
        Assert.assertEquals(2, readCounter.get());
    }

    @Test
    public void testGlobalSerializer_withoutOverrideJavaSerializable() {
        GlobalSerializerConfig globalSerializerConfig = new GlobalSerializerConfig();
        globalSerializerConfig.setOverrideJavaSerialization(false);
        final AtomicInteger writeCounter = new AtomicInteger();
        final AtomicInteger readCounter = new AtomicInteger();
        SerializationConfig serializationConfig = new SerializationConfig().setGlobalSerializerConfig(globalSerializerConfig.setImplementation(new StreamSerializer<Object>() {
            @Override
            public void write(ObjectDataOutput out, Object v) throws IOException {
                writeCounter.incrementAndGet();
                out.writeUTF(((SerializationTest.DummyValue) (v)).s);
                out.writeInt(((SerializationTest.DummyValue) (v)).k);
            }

            @Override
            public Object read(ObjectDataInput in) throws IOException {
                readCounter.incrementAndGet();
                return new SerializationTest.DummyValue(in.readUTF(), in.readInt());
            }

            public int getTypeId() {
                return 123;
            }

            public void destroy() {
            }
        }));
        SerializationService ss1 = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        SerializationTest.DummyValue value = new SerializationTest.DummyValue("test", 111);
        Data data1 = ss1.toData(value);
        Data data2 = ss1.toData(new SerializationTest.Foo());
        Assert.assertNotNull(data1);
        Assert.assertNotNull(data2);
        Assert.assertEquals(1, writeCounter.get());
        SerializationService ss2 = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        Object o1 = ss2.toObject(data1);
        Object o2 = ss2.toObject(data2);
        Assert.assertEquals(value, o1);
        Assert.assertNotNull(o2);
        Assert.assertEquals(1, readCounter.get());
    }

    private static class DummyValue {
        String s;

        int k;

        private DummyValue(String s, int k) {
            this.s = s;
            this.k = k;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SerializationTest.DummyValue that = ((SerializationTest.DummyValue) (o));
            if ((k) != (that.k)) {
                return false;
            }
            if ((s) != null ? !(s.equals(that.s)) : (that.s) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = ((s) != null) ? s.hashCode() : 0;
            result = (31 * result) + (k);
            return result;
        }
    }

    @Test
    public void testEmptyData() {
        SerializationConfig serializationConfig = new SerializationConfig().addSerializerConfig(new SerializerConfig().setTypeClass(SerializationTest.SingletonValue.class).setImplementation(new StreamSerializer<SerializationTest.SingletonValue>() {
            @Override
            public void write(ObjectDataOutput out, SerializationTest.SingletonValue v) throws IOException {
            }

            @Override
            public SerializationTest.SingletonValue read(ObjectDataInput in) throws IOException {
                return new SerializationTest.SingletonValue();
            }

            @Override
            public int getTypeId() {
                return 123;
            }

            @Override
            public void destroy() {
            }
        }));
        SerializationService ss1 = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        Data data = ss1.toData(new SerializationTest.SingletonValue());
        Assert.assertNotNull(data);
        SerializationService ss2 = new DefaultSerializationServiceBuilder().setConfig(serializationConfig).build();
        Object o = ss2.toObject(data);
        Assert.assertEquals(new SerializationTest.SingletonValue(), o);
    }

    private static class SingletonValue {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof SerializationTest.SingletonValue;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @Test
    public void testNullData() {
        Data data = new HeapData();
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        Assert.assertNull(ss.toObject(data));
    }

    /**
     * issue #1265
     */
    @Test
    public void testSharedJavaSerialization() {
        SerializationService ss = new DefaultSerializationServiceBuilder().setEnableSharedObject(true).build();
        Data data = ss.toData(new SerializationTest.Foo());
        SerializationTest.Foo foo = ((SerializationTest.Foo) (ss.toObject(data)));
        Assert.assertTrue("Objects are not identical!", (foo == (foo.getBar().getFoo())));
    }

    @Test
    public void testLinkedListSerialization() {
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        LinkedList<SerializationConcurrencyTest.Person> linkedList = new LinkedList<SerializationConcurrencyTest.Person>();
        linkedList.add(new SerializationConcurrencyTest.Person(35, 180, 100, "Orhan", null));
        linkedList.add(new SerializationConcurrencyTest.Person(12, 120, 60, "Osman", null));
        Data data = ss.toData(linkedList);
        LinkedList deserialized = ss.toObject(data);
        Assert.assertTrue("Objects are not identical!", linkedList.equals(deserialized));
    }

    @Test
    public void testArrayListSerialization() {
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        ArrayList<SerializationConcurrencyTest.Person> arrayList = new ArrayList<SerializationConcurrencyTest.Person>();
        arrayList.add(new SerializationConcurrencyTest.Person(35, 180, 100, "Orhan", null));
        arrayList.add(new SerializationConcurrencyTest.Person(12, 120, 60, "Osman", null));
        Data data = ss.toData(arrayList);
        ArrayList deserialized = ss.toObject(data);
        Assert.assertTrue("Objects are not identical!", arrayList.equals(deserialized));
    }

    @Test
    public void testArraySerialization() {
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        byte[] array = new byte[1024];
        new Random().nextBytes(array);
        Data data = ss.toData(array);
        byte[] deserialized = ss.toObject(data);
        Assert.assertArrayEquals(array, deserialized);
    }

    @Test
    public void testPartitionHash() {
        PartitioningStrategy partitionStrategy = new PartitioningStrategy() {
            @Override
            public Object getPartitionKey(Object key) {
                return key.hashCode();
            }
        };
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        String obj = String.valueOf(System.nanoTime());
        Data dataWithPartitionHash = ss.toData(obj, partitionStrategy);
        Data dataWithOutPartitionHash = ss.toData(obj);
        Assert.assertTrue(dataWithPartitionHash.hasPartitionHash());
        Assert.assertNotEquals(dataWithPartitionHash.hashCode(), dataWithPartitionHash.getPartitionHash());
        Assert.assertFalse(dataWithOutPartitionHash.hasPartitionHash());
        Assert.assertEquals(dataWithOutPartitionHash.hashCode(), dataWithOutPartitionHash.getPartitionHash());
    }

    /**
     * issue #1265
     */
    @Test
    public void testUnsharedJavaSerialization() {
        SerializationService ss = new DefaultSerializationServiceBuilder().setEnableSharedObject(false).build();
        Data data = ss.toData(new SerializationTest.Foo());
        SerializationTest.Foo foo = ss.toObject(data);
        Assert.assertFalse("Objects should not be identical!", (foo == (foo.getBar().getFoo())));
    }

    private static class Foo implements Serializable {
        public SerializationTest.Foo.Bar bar;

        public Foo() {
            this.bar = new SerializationTest.Foo.Bar();
        }

        public SerializationTest.Foo.Bar getBar() {
            return bar;
        }

        private class Bar implements Serializable {
            public SerializationTest.Foo getFoo() {
                return SerializationTest.Foo.this;
            }
        }
    }

    /**
     * Ensures that SerializationService correctly handles compressed Serializables,
     * using a Properties object as a test case.
     */
    @Test
    public void testCompressionOnSerializables() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().setEnableCompression(true).build();
        long key = 1;
        long value = 5000;
        Properties properties = new Properties();
        properties.put(key, value);
        Data data = serializationService.toData(properties);
        Properties output = serializationService.toObject(data);
        Assert.assertEquals(value, output.get(key));
    }

    /**
     * Ensures that SerializationService correctly handles compressed Serializables,
     * using a test-specific object as a test case.
     */
    @Test
    public void testCompressionOnExternalizables() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().setEnableCompression(true).build();
        String test = "test";
        SerializationTest.ExternalizableString ex = new SerializationTest.ExternalizableString(test);
        Data data = serializationService.toData(ex);
        SerializationTest.ExternalizableString actual = serializationService.toObject(data);
        Assert.assertEquals(test, actual.value);
    }

    private static class ExternalizableString implements Externalizable {
        String value;

        public ExternalizableString() {
        }

        public ExternalizableString(String value) {
            this.value = value;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(value);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            value = in.readUTF();
        }
    }

    @Test
    public void testMemberLeftException_usingMemberImpl() throws Exception {
        String uuid = UuidUtil.newUnsecureUuidString();
        String host = "127.0.0.1";
        int port = 5000;
        Member member = new com.hazelcast.instance.MemberImpl.Builder(new com.hazelcast.nio.Address(host, port)).version(MemberVersion.of("3.8.0")).uuid(uuid).build();
        testMemberLeftException(uuid, host, port, member);
    }

    @Test
    public void testMemberLeftException_usingSimpleMember() throws Exception {
        String uuid = UuidUtil.newUnsecureUuidString();
        String host = "127.0.0.1";
        int port = 5000;
        Member member = new SimpleMemberImpl(MemberVersion.of("3.8.0"), uuid, new InetSocketAddress(host, port));
        testMemberLeftException(uuid, host, port, member);
    }

    @Test
    public void testMemberLeftException_withLiteMemberImpl() throws Exception {
        String uuid = UuidUtil.newUnsecureUuidString();
        String host = "127.0.0.1";
        int port = 5000;
        Member member = new com.hazelcast.instance.MemberImpl.Builder(new com.hazelcast.nio.Address(host, port)).version(MemberVersion.of("3.8.0")).liteMember(true).uuid(uuid).build();
        testMemberLeftException(uuid, host, port, member);
    }

    @Test
    public void testMemberLeftException_withLiteSimpleMemberImpl() throws Exception {
        String uuid = UuidUtil.newUnsecureUuidString();
        String host = "127.0.0.1";
        int port = 5000;
        Member member = new SimpleMemberImpl(MemberVersion.of("3.8.0"), uuid, new InetSocketAddress(host, port), true);
        testMemberLeftException(uuid, host, port, member);
    }

    @Test
    public void testInternallySupportedClassExtended() {
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        SerializationTest.TheClassThatExtendArrayList obj = new SerializationTest.TheClassThatExtendArrayList();
        Data data = ss.toData(obj);
        Object obj2 = ss.toObject(data);
        Assert.assertEquals(obj2.getClass(), SerializationTest.TheClassThatExtendArrayList.class);
    }

    static class TheClassThatExtendArrayList<E> extends ArrayList<E> implements DataSerializable {
        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt(size());
            for (Object item : this) {
                out.writeObject(item);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void readData(ObjectDataInput in) throws IOException {
            int size = in.readInt();
            for (int k = 0; k < size; k++) {
                add(((E) (in.readObject())));
            }
        }
    }

    @Test
    public void testDynamicProxySerialization_withConfiguredClassLoader() {
        ClassLoader current = getClass().getClassLoader();
        SerializationTest.DynamicProxyTestClassLoader cl = new SerializationTest.DynamicProxyTestClassLoader(current);
        SerializationService ss = new DefaultSerializationServiceBuilder().setClassLoader(cl).build();
        SerializationTest.IObjectA oa = ((SerializationTest.IObjectA) (Proxy.newProxyInstance(current, new Class[]{ SerializationTest.IObjectA.class }, SerializationTest.DummyInvocationHandler.INSTANCE)));
        Data data = ss.toData(oa);
        Object o = ss.toObject(data);
        Assert.assertSame("configured classloader is not used", cl, o.getClass().getClassLoader());
        try {
            SerializationTest.IObjectA.class.cast(o);
            Assert.fail("the serialized object should not be castable");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void testDynamicProxySerialization_withContextClassLoader() {
        ClassLoader oldContextLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader current = getClass().getClassLoader();
            SerializationTest.DynamicProxyTestClassLoader cl = new SerializationTest.DynamicProxyTestClassLoader(current);
            Thread.currentThread().setContextClassLoader(cl);
            SerializationService ss = new DefaultSerializationServiceBuilder().setClassLoader(cl).build();
            SerializationTest.IObjectA oa = ((SerializationTest.IObjectA) (Proxy.newProxyInstance(current, new Class[]{ SerializationTest.IObjectA.class }, SerializationTest.DummyInvocationHandler.INSTANCE)));
            Data data = ss.toData(oa);
            Object o = ss.toObject(data);
            Assert.assertSame("context classloader is not used", cl, o.getClass().getClassLoader());
            try {
                SerializationTest.IObjectA.class.cast(o);
                Assert.fail("the serialized object should not be castable");
            } catch (ClassCastException expected) {
                // expected
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextLoader);
        }
    }

    @Test
    public void testNonPublicDynamicProxySerialization_withClassLoaderMess() {
        ClassLoader current = getClass().getClassLoader();
        SerializationTest.DynamicProxyTestClassLoader cl1 = new SerializationTest.DynamicProxyTestClassLoader(current, SerializationTest.IPrivateObjectB.class.getName());
        SerializationTest.DynamicProxyTestClassLoader cl2 = new SerializationTest.DynamicProxyTestClassLoader(cl1, SerializationTest.IPrivateObjectC.class.getName());
        SerializationService ss = new DefaultSerializationServiceBuilder().setClassLoader(cl2).build();
        Object ocd = Proxy.newProxyInstance(current, new Class[]{ SerializationTest.IPrivateObjectB.class, SerializationTest.IPrivateObjectC.class }, SerializationTest.DummyInvocationHandler.INSTANCE);
        Data data = ss.toData(ocd);
        try {
            ss.toObject(data);
            Assert.fail("the object should not be deserializable");
        } catch (IllegalAccessError expected) {
            // expected
        }
    }

    @Test
    public void testVersionedDataSerializable_outputHasMemberVersion() {
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        VersionedDataSerializable object = new VersionedDataSerializable();
        ss.toData(object);
        Assert.assertEquals("ObjectDataOutput.getVersion should be equal to member version", Version.of(BuildInfoProvider.getBuildInfo().getVersion()), object.getVersion());
    }

    @Test
    public void testVersionedDataSerializable_inputHasMemberVersion() {
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        VersionedDataSerializable object = new VersionedDataSerializable();
        VersionedDataSerializable otherObject = ss.toObject(ss.toData(object));
        Assert.assertEquals("ObjectDataInput.getVersion should be equal to member version", Version.of(BuildInfoProvider.getBuildInfo().getVersion()), otherObject.getVersion());
    }

    private static final class DynamicProxyTestClassLoader extends ClassLoader {
        private static final Set<String> WELL_KNOWN_TEST_CLASSES = new HashSet<String>(Arrays.asList(SerializationTest.IObjectA.class.getName(), SerializationTest.IPrivateObjectB.class.getName(), SerializationTest.IPrivateObjectC.class.getName()));

        private final Set<String> wellKnownClasses = new HashSet<String>();

        private DynamicProxyTestClassLoader(ClassLoader parent, String... classesToLoad) {
            super(parent);
            if ((classesToLoad.length) == 0) {
                wellKnownClasses.addAll(SerializationTest.DynamicProxyTestClassLoader.WELL_KNOWN_TEST_CLASSES);
            } else {
                wellKnownClasses.addAll(Arrays.asList(classesToLoad));
            }
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (!(SerializationTest.DynamicProxyTestClassLoader.WELL_KNOWN_TEST_CLASSES.contains(name))) {
                return super.loadClass(name, resolve);
            }
            synchronized(this) {
                // first, check if the class has already been loaded
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    c = findClass(name);
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (!(wellKnownClasses.contains(name))) {
                return super.findClass(name);
            }
            String path = (name.replace('.', '/')) + ".class";
            InputStream in = null;
            try {
                in = getParent().getResourceAsStream(path);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int read;
                while ((read = in.read(buf)) != (-1)) {
                    bout.write(buf, 0, read);
                } 
                byte[] code = bout.toByteArray();
                return defineClass(name, code, 0, code.length);
            } catch (IOException e) {
                return super.findClass(name);
            } finally {
                IOUtil.closeResource(in);
            }
        }
    }

    public static final class DummyInvocationHandler implements Serializable , InvocationHandler {
        private static final long serialVersionUID = 3459316091095397098L;

        private static final SerializationTest.DummyInvocationHandler INSTANCE = new SerializationTest.DummyInvocationHandler();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public interface IObjectA {
        void doA();
    }

    @SuppressWarnings("unused")
    interface IPrivateObjectB {
        void doC();
    }

    @SuppressWarnings("unused")
    interface IPrivateObjectC {
        void doD();
    }
}

