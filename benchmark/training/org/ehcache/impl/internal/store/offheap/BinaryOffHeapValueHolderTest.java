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
import org.ehcache.impl.serialization.StringSerializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * BinaryOffHeapValueHolderTest
 */
public class BinaryOffHeapValueHolderTest {
    private String value;

    private StringSerializer serializer;

    private BinaryOffHeapValueHolder<String> valueHolder;

    @Test
    public void testCanAccessBinaryValue() throws ClassNotFoundException {
        Assert.assertThat(valueHolder.isBinaryValueAvailable(), Matchers.is(true));
        ByteBuffer binaryValue = valueHolder.getBinaryValue();
        Assert.assertThat(serializer.read(binaryValue), Matchers.is(value));
    }

    @Test
    public void testCanAccessValue() {
        Assert.assertThat(valueHolder.get(), Matchers.is(value));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCantBeDetached() {
        valueHolder.detach();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCantUpdateMetadata() {
        valueHolder.updateMetadata(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCantForceDeserialization() {
        valueHolder.forceDeserialization();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCantWriteback() {
        valueHolder.writeBack();
    }
}

