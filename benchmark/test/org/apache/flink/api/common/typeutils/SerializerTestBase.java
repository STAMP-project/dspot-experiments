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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.flink.api.java.typeutils.runtime.NullableSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.testutils.CustomEqualityMatcher;
import org.apache.flink.testutils.DeeplyEqualsChecker;
import org.apache.flink.util.InstantiationUtil;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test base for serializers.
 *
 * We have a toString() call on all deserialized
 * values because this is further evidence that the deserialized value is actually correct.
 * (JodaTime DataTime) with the default KryoSerializer used to pass this test but the
 * internal state would be corrupt, which becomes evident when toString is called.
 */
public abstract class SerializerTestBase<T> extends TestLogger {
    private final DeeplyEqualsChecker checker;

    protected SerializerTestBase() {
        this.checker = new DeeplyEqualsChecker();
    }

    protected SerializerTestBase(DeeplyEqualsChecker checker) {
        this.checker = checker;
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testInstantiate() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            if (serializer.getClass().getName().endsWith("KryoSerializer")) {
                // the kryo serializer will return null. We ignore this test for Kryo.
                return;
            }
            T instance = serializer.createInstance();
            Assert.assertNotNull("The created instance must not be null.", instance);
            Class<T> type = getTypeClass();
            Assert.assertNotNull("The test is corrupt: type class is null.", type);
            if (!(type.isAssignableFrom(instance.getClass()))) {
                Assert.fail((((("Type of the instantiated object is wrong. " + "Expected Type: ") + type) + " present type ") + (instance.getClass())));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testConfigSnapshotInstantiation() {
        TypeSerializerSnapshot<T> configSnapshot = getSerializer().snapshotConfiguration();
        InstantiationUtil.instantiate(configSnapshot.getClass());
    }

    @Test
    public void testSnapshotConfigurationAndReconfigure() throws Exception {
        final TypeSerializer<T> serializer = getSerializer();
        final TypeSerializerSnapshot<T> configSnapshot = serializer.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), configSnapshot, serializer);
            serializedConfig = out.toByteArray();
        }
        TypeSerializerSnapshot<T> restoredConfig;
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            restoredConfig = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), getSerializer());
        }
        TypeSerializerSchemaCompatibility<T> strategy = restoredConfig.resolveSchemaCompatibility(getSerializer());
        final TypeSerializer<T> restoreSerializer;
        if (strategy.isCompatibleAsIs()) {
            restoreSerializer = restoredConfig.restoreSerializer();
        } else
            if (strategy.isCompatibleWithReconfiguredSerializer()) {
                restoreSerializer = strategy.getReconfiguredSerializer();
            } else {
                throw new AssertionError(("Unable to restore serializer with " + strategy));
            }

        Assert.assertEquals(serializer.getClass(), restoreSerializer.getClass());
    }

    @Test
    public void testGetLength() {
        final int len = getLength();
        if (len == 0) {
            Assert.fail("Broken serializer test base - zero length cannot be the expected length");
        }
        try {
            TypeSerializer<T> serializer = getSerializer();
            Assert.assertEquals(len, serializer.getLength());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testCopy() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            for (T datum : testData) {
                T copy = serializer.copy(datum);
                SerializerTestBase.checkToString(copy);
                deepEquals("Copied element is not equal to the original element.", datum, copy);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testCopyIntoNewElements() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            for (T datum : testData) {
                T copy = serializer.copy(datum, serializer.createInstance());
                SerializerTestBase.checkToString(copy);
                deepEquals("Copied element is not equal to the original element.", datum, copy);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testCopyIntoReusedElements() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            T target = serializer.createInstance();
            for (T datum : testData) {
                T copy = serializer.copy(datum, target);
                SerializerTestBase.checkToString(copy);
                deepEquals("Copied element is not equal to the original element.", datum, copy);
                target = copy;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializeIndividually() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            for (T value : testData) {
                SerializerTestBase.TestOutputView out = new SerializerTestBase.TestOutputView();
                serializer.serialize(value, out);
                SerializerTestBase.TestInputView in = out.getInputView();
                Assert.assertTrue("No data available during deserialization.", ((in.available()) > 0));
                T deserialized = serializer.deserialize(serializer.createInstance(), in);
                SerializerTestBase.checkToString(deserialized);
                deepEquals("Deserialized value if wrong.", value, deserialized);
                Assert.assertTrue("Trailing data available after deserialization.", ((in.available()) == 0));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializeIndividuallyReusingValues() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            T reuseValue = serializer.createInstance();
            for (T value : testData) {
                SerializerTestBase.TestOutputView out = new SerializerTestBase.TestOutputView();
                serializer.serialize(value, out);
                SerializerTestBase.TestInputView in = out.getInputView();
                Assert.assertTrue("No data available during deserialization.", ((in.available()) > 0));
                T deserialized = serializer.deserialize(reuseValue, in);
                SerializerTestBase.checkToString(deserialized);
                deepEquals("Deserialized value if wrong.", value, deserialized);
                Assert.assertTrue("Trailing data available after deserialization.", ((in.available()) == 0));
                reuseValue = deserialized;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializeAsSequenceNoReuse() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            SerializerTestBase.TestOutputView out = new SerializerTestBase.TestOutputView();
            for (T value : testData) {
                serializer.serialize(value, out);
            }
            SerializerTestBase.TestInputView in = out.getInputView();
            int num = 0;
            while ((in.available()) > 0) {
                T deserialized = serializer.deserialize(in);
                SerializerTestBase.checkToString(deserialized);
                deepEquals("Deserialized value if wrong.", testData[num], deserialized);
                num++;
            } 
            Assert.assertEquals("Wrong number of elements deserialized.", testData.length, num);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializeAsSequenceReusingValues() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            SerializerTestBase.TestOutputView out = new SerializerTestBase.TestOutputView();
            for (T value : testData) {
                serializer.serialize(value, out);
            }
            SerializerTestBase.TestInputView in = out.getInputView();
            T reuseValue = serializer.createInstance();
            int num = 0;
            while ((in.available()) > 0) {
                T deserialized = serializer.deserialize(reuseValue, in);
                SerializerTestBase.checkToString(deserialized);
                deepEquals("Deserialized value if wrong.", testData[num], deserialized);
                reuseValue = deserialized;
                num++;
            } 
            Assert.assertEquals("Wrong number of elements deserialized.", testData.length, num);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializedCopyIndividually() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            for (T value : testData) {
                SerializerTestBase.TestOutputView out = new SerializerTestBase.TestOutputView();
                serializer.serialize(value, out);
                SerializerTestBase.TestInputView source = out.getInputView();
                SerializerTestBase.TestOutputView target = new SerializerTestBase.TestOutputView();
                serializer.copy(source, target);
                SerializerTestBase.TestInputView toVerify = target.getInputView();
                Assert.assertTrue("No data available copying.", ((toVerify.available()) > 0));
                T deserialized = serializer.deserialize(serializer.createInstance(), toVerify);
                SerializerTestBase.checkToString(deserialized);
                deepEquals("Deserialized value if wrong.", value, deserialized);
                Assert.assertTrue("Trailing data available after deserialization.", ((toVerify.available()) == 0));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializedCopyAsSequence() {
        try {
            TypeSerializer<T> serializer = getSerializer();
            T[] testData = getData();
            SerializerTestBase.TestOutputView out = new SerializerTestBase.TestOutputView();
            for (T value : testData) {
                serializer.serialize(value, out);
            }
            SerializerTestBase.TestInputView source = out.getInputView();
            SerializerTestBase.TestOutputView target = new SerializerTestBase.TestOutputView();
            for (int i = 0; i < (testData.length); i++) {
                serializer.copy(source, target);
            }
            SerializerTestBase.TestInputView toVerify = target.getInputView();
            int num = 0;
            while ((toVerify.available()) > 0) {
                T deserialized = serializer.deserialize(serializer.createInstance(), toVerify);
                SerializerTestBase.checkToString(deserialized);
                deepEquals("Deserialized value if wrong.", testData[num], deserialized);
                num++;
            } 
            Assert.assertEquals("Wrong number of elements copied.", testData.length, num);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testSerializabilityAndEquals() {
        try {
            TypeSerializer<T> ser1 = getSerializer();
            TypeSerializer<T> ser2;
            try {
                ser2 = SerializationUtils.clone(ser1);
            } catch (SerializationException e) {
                Assert.fail(("The serializer is not serializable: " + e));
                return;
            }
            Assert.assertEquals("The copy of the serializer is not equal to the original one.", ser1, ser2);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testNullability() {
        TypeSerializer<T> serializer = getSerializer();
        try {
            NullableSerializer.checkIfNullSupported(serializer);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
            Assert.fail(("Unexpected failure of null value handling: " + (t.getMessage())));
        }
    }

    @Test
    public void testDuplicate() throws Exception {
        final int numThreads = 10;
        final TypeSerializer<T> serializer = getSerializer();
        final CyclicBarrier startLatch = new CyclicBarrier(numThreads);
        final List<SerializerTestBase.SerializerRunner<T>> concurrentRunners = new ArrayList<>(numThreads);
        Assert.assertEquals(serializer, serializer.duplicate());
        T[] testData = getData();
        for (int i = 0; i < numThreads; ++i) {
            SerializerTestBase.SerializerRunner<T> runner = new SerializerTestBase.SerializerRunner(startLatch, serializer.duplicate(), testData, 120L, checker);
            runner.start();
            concurrentRunners.add(runner);
        }
        for (SerializerTestBase.SerializerRunner<T> concurrentRunner : concurrentRunners) {
            concurrentRunner.join();
            concurrentRunner.checkResult();
        }
    }

    // --------------------------------------------------------------------------------------------
    private static final class TestOutputView extends DataOutputStream implements DataOutputView {
        public TestOutputView() {
            super(new ByteArrayOutputStream(4096));
        }

        public SerializerTestBase.TestInputView getInputView() {
            ByteArrayOutputStream baos = ((ByteArrayOutputStream) (out));
            return new SerializerTestBase.TestInputView(baos.toByteArray());
        }

        @Override
        public void skipBytesToWrite(int numBytes) throws IOException {
            for (int i = 0; i < numBytes; i++) {
                write(0);
            }
        }

        @Override
        public void write(DataInputView source, int numBytes) throws IOException {
            byte[] buffer = new byte[numBytes];
            source.readFully(buffer);
            write(buffer);
        }
    }

    /**
     * Runner to test serializer duplication via concurrency.
     *
     * @param <T>
     * 		type of the test elements.
     */
    static class SerializerRunner<T> extends Thread {
        final CyclicBarrier allReadyBarrier;

        final TypeSerializer<T> serializer;

        final T[] testData;

        final long durationLimitMillis;

        Throwable failure;

        final DeeplyEqualsChecker checker;

        SerializerRunner(CyclicBarrier allReadyBarrier, TypeSerializer<T> serializer, T[] testData, long testTargetDurationMillis, DeeplyEqualsChecker checker) {
            this.allReadyBarrier = allReadyBarrier;
            this.serializer = serializer;
            this.testData = testData;
            this.durationLimitMillis = testTargetDurationMillis;
            this.checker = checker;
            this.failure = null;
        }

        @Override
        public void run() {
            DataInputDeserializer dataInputDeserializer = new DataInputDeserializer();
            DataOutputSerializer dataOutputSerializer = new DataOutputSerializer(128);
            try {
                allReadyBarrier.await();
                final long endTimeNanos = (System.nanoTime()) + ((durationLimitMillis) * 1000000L);
                while (true) {
                    for (T testItem : testData) {
                        serializer.serialize(testItem, dataOutputSerializer);
                        dataInputDeserializer.setBuffer(dataOutputSerializer.getSharedBuffer(), 0, dataOutputSerializer.length());
                        T serdeTestItem = serializer.deserialize(dataInputDeserializer);
                        T copySerdeTestItem = serializer.copy(serdeTestItem);
                        dataOutputSerializer.clear();
                        Assert.assertThat("Serialization/Deserialization cycle resulted in an object that are not equal to the original.", copySerdeTestItem, CustomEqualityMatcher.deeplyEquals(testItem).withChecker(checker));
                        // try to enforce some upper bound to the test time
                        if ((System.nanoTime()) >= endTimeNanos) {
                            return;
                        }
                    }
                } 
            } catch (Throwable ex) {
                failure = ex;
            }
        }

        void checkResult() throws Exception {
            if ((failure) != null) {
                if ((failure) instanceof AssertionError) {
                    throw ((AssertionError) (failure));
                } else {
                    throw ((Exception) (failure));
                }
            }
        }
    }

    private static final class TestInputView extends DataInputStream implements DataInputView {
        public TestInputView(byte[] data) {
            super(new ByteArrayInputStream(data));
        }

        @Override
        public void skipBytesToRead(int numBytes) throws IOException {
            while (numBytes > 0) {
                int skipped = skipBytes(numBytes);
                numBytes -= skipped;
            } 
        }
    }
}

