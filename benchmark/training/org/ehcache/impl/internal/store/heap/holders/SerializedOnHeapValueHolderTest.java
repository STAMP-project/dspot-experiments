/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.internal.store.heap.holders;


import java.nio.ByteBuffer;
import java.util.concurrent.Exchanger;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.impl.serialization.JavaSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author vfunshteyn
 */
public class SerializedOnHeapValueHolderTest {
    @Test
    public void testValue() {
        String o = "foo";
        Store.ValueHolder<?> vh1 = SerializedOnHeapValueHolderTest.newValueHolder(o);
        Store.ValueHolder<?> vh2 = SerializedOnHeapValueHolderTest.newValueHolder(o);
        Assert.assertFalse(((vh1.get()) == (vh2.get())));
        Assert.assertEquals(vh1.get(), vh2.get());
        Assert.assertNotSame(vh1.get(), vh1.get());
    }

    @Test
    public void testHashCode() {
        Store.ValueHolder<Integer> vh1 = SerializedOnHeapValueHolderTest.newValueHolder(10);
        Store.ValueHolder<Integer> vh2 = SerializedOnHeapValueHolderTest.newValueHolder(10);
        // make sure reading the value multiple times doesn't change the hashcode
        vh1.get();
        vh1.get();
        vh2.get();
        vh2.get();
        Assert.assertThat(vh1.hashCode(), Matchers.is(vh2.hashCode()));
    }

    @Test
    public void testEquals() {
        Store.ValueHolder<Integer> vh = SerializedOnHeapValueHolderTest.newValueHolder(10);
        Assert.assertThat(SerializedOnHeapValueHolderTest.newValueHolder(10), Matchers.equalTo(vh));
    }

    @Test
    public void testNotEquals() {
        Store.ValueHolder<Integer> vh = SerializedOnHeapValueHolderTest.newValueHolder(10);
        Assert.assertThat(SerializedOnHeapValueHolderTest.newValueHolder(101), Matchers.not(Matchers.equalTo(vh)));
    }

    @Test(expected = NullPointerException.class)
    public void testNullValue() {
        SerializedOnHeapValueHolderTest.newValueHolder(null);
    }

    @Test
    public void testSerializerGetsDifferentByteBufferOnRead() {
        final Exchanger<ByteBuffer> exchanger = new Exchanger<>();
        final SerializedOnHeapValueHolderTest.ReadExchangeSerializer serializer = new SerializedOnHeapValueHolderTest.ReadExchangeSerializer(exchanger);
        final SerializedOnHeapValueHolder<String> valueHolder = new SerializedOnHeapValueHolder<>("test it!", System.currentTimeMillis(), false, serializer);
        new Thread(valueHolder::get).start();
        valueHolder.get();
    }

    private static class ReadExchangeSerializer implements Serializer<String> {
        private final Exchanger<ByteBuffer> exchanger;

        private final Serializer<String> delegate = new JavaSerializer<>(SerializedOnHeapValueHolderTest.class.getClassLoader());

        private ReadExchangeSerializer(Exchanger<ByteBuffer> exchanger) {
            this.exchanger = exchanger;
        }

        @Override
        public ByteBuffer serialize(String object) {
            return delegate.serialize(object);
        }

        @Override
        public String read(ByteBuffer binary) throws ClassNotFoundException {
            ByteBuffer received = binary;
            ByteBuffer exchanged = null;
            try {
                exchanged = exchanger.exchange(received);
            } catch (InterruptedException e) {
                Assert.fail("Received InterruptedException");
            }
            Assert.assertNotSame(exchanged, received);
            return delegate.read(received);
        }

        @Override
        public boolean equals(String object, ByteBuffer binary) throws ClassNotFoundException {
            throw new UnsupportedOperationException("TODO Implement me!");
        }
    }

    private static class TestTimeSource implements TimeSource {
        static final SerializedOnHeapValueHolderTest.TestTimeSource INSTANCE = new SerializedOnHeapValueHolderTest.TestTimeSource();

        private long time = 1;

        @Override
        public long getTimeMillis() {
            return time;
        }

        private void advanceTime(long delta) {
            this.time += delta;
        }
    }
}

