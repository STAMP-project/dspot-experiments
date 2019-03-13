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


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * BasicOffHeapValueHolderTest
 */
public class BasicOffHeapValueHolderTest {
    private String value;

    private BasicOffHeapValueHolder<String> valueHolder;

    @Test
    public void testCanAccessValue() {
        Assert.assertThat(valueHolder.get(), Matchers.is(value));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDoesNotSupportDelayedDeserialization() {
        valueHolder.detach();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDoesNotSupportForceDeserialization() {
        valueHolder.forceDeserialization();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDoesNotSupportWriteBack() {
        valueHolder.writeBack();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDoesNotSupportUpdateMetadata() {
        valueHolder.updateMetadata(valueHolder);
    }
}

