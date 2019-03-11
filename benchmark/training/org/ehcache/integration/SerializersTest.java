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
package org.ehcache.integration;


import java.nio.ByteBuffer;
import org.ehcache.spi.persistence.StateRepository;
import org.ehcache.spi.serialization.SerializerException;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class SerializersTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testStatefulSerializer() throws Exception {
        SerializersTest.StatefulSerializerImpl<Long> serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithByRefHeapCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(0));
        serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithByValueHeapCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(1));
        serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithOffheapCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(1));
        serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithHeapOffheapCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(1));
        serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithDiskCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(1));
        serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithHeapDiskCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(1));
        serializer = new SerializersTest.StatefulSerializerImpl<>();
        testSerializerWithThreeTierCache(serializer);
        Assert.assertThat(serializer.initCount, Matchers.is(1));
    }

    public static class StatefulSerializerImpl<T> implements StatefulSerializer<T> {
        private int initCount = 0;

        @Override
        public void init(final StateRepository stateRepository) {
            (initCount)++;
        }

        @Override
        public ByteBuffer serialize(final T object) throws SerializerException {
            return null;
        }

        @Override
        public T read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return null;
        }

        @Override
        public boolean equals(final T object, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            return false;
        }
    }
}

