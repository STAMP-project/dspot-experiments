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
package org.ehcache.impl.internal.store.offheap;


import java.nio.ByteBuffer;
import org.ehcache.impl.serialization.JavaSerializer;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.offheapstore.storage.portability.WriteContext;


/**
 * LazyOffHeapValueHolderTest
 */
public class LazyOffHeapValueHolderTest {
    @Test
    public void testDelayedDeserialization() {
        JavaSerializer<String> serializer = new JavaSerializer<>(getClass().getClassLoader());
        String testValue = "Let's get binary!";
        ByteBuffer serialized = serializer.serialize(testValue);
        OffHeapValueHolder<String> valueHolder = new LazyOffHeapValueHolder(1L, serialized, serializer, 10L, 20L, 15L, Mockito.mock(WriteContext.class));
        valueHolder.detach();
        serialized.clear();
        Assert.assertThat(valueHolder.get(), Is.is(testValue));
    }

    @Test
    public void testCanAccessBinaryValue() throws ClassNotFoundException {
        JavaSerializer<String> serializer = new JavaSerializer<>(getClass().getClassLoader());
        String testValue = "Let's get binary!";
        ByteBuffer serialized = serializer.serialize(testValue);
        LazyOffHeapValueHolder<String> valueHolder = new LazyOffHeapValueHolder(1L, serialized, serializer, 10L, 20L, 15L, Mockito.mock(WriteContext.class));
        valueHolder.detach();
        ByteBuffer binaryValue = valueHolder.getBinaryValue();
        Assert.assertThat(serializer.read(binaryValue), Is.is(testValue));
    }

    @Test
    public void testPreventAccessToBinaryValueIfNotPrepared() {
        JavaSerializer<String> serializer = new JavaSerializer<>(getClass().getClassLoader());
        String testValue = "Let's get binary!";
        ByteBuffer serialized = serializer.serialize(testValue);
        LazyOffHeapValueHolder<String> valueHolder = new LazyOffHeapValueHolder(1L, serialized, serializer, 10L, 20L, 15L, Mockito.mock(WriteContext.class));
        try {
            valueHolder.getBinaryValue();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("has not been prepared"));
        }
    }
}

