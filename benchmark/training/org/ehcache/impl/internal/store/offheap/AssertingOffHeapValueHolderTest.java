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
import org.ehcache.spi.serialization.Serializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.offheapstore.storage.portability.WriteContext;


public class AssertingOffHeapValueHolderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testLockingAssertionsOnDetach() {
        OffHeapValueHolder<String> valueHolder = new AssertingOffHeapValueHolder(1L, ByteBuffer.allocate(1), Mockito.mock(Serializer.class), 10L, 20L, 15L, Mockito.mock(WriteContext.class));
        try {
            valueHolder.detach();
            Assert.fail("Expected AssertionError");
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLockingAssertionsOnForceDeserialize() {
        OffHeapValueHolder<String> valueHolder = new AssertingOffHeapValueHolder(1L, ByteBuffer.allocate(1), Mockito.mock(Serializer.class), 10L, 20L, 15L, Mockito.mock(WriteContext.class));
        try {
            valueHolder.forceDeserialization();
            Assert.fail("Expected AssertionError");
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLockingAssertionsOnWriteBack() {
        OffHeapValueHolder<String> valueHolder = new AssertingOffHeapValueHolder(1L, ByteBuffer.allocate(1), Mockito.mock(Serializer.class), 10L, 20L, 15L, Mockito.mock(WriteContext.class));
        try {
            valueHolder.writeBack();
            Assert.fail("Expected AssertionError");
        } catch (AssertionError e) {
            // expected
        }
    }
}

