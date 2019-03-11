/**
 * Copyright 2016-present Open Networking Foundation
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
package io.atomix.core.counter;


import io.atomix.core.AbstractPrimitiveTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AtomicCounterProxy}.
 */
public class AtomicCounterTest extends AbstractPrimitiveTest {
    @Test
    public void testBasicOperations() throws Throwable {
        AtomicCounter along = atomix().atomicCounterBuilder("test-counter-basic-operations").withProtocol(protocol()).build();
        Assert.assertEquals(0, along.get());
        Assert.assertEquals(1, along.incrementAndGet());
        along.set(100);
        Assert.assertEquals(100, along.get());
        Assert.assertEquals(100, along.getAndAdd(10));
        Assert.assertEquals(110, along.get());
        Assert.assertFalse(along.compareAndSet(109, 111));
        Assert.assertTrue(along.compareAndSet(110, 111));
        Assert.assertEquals(100, along.addAndGet((-11)));
        Assert.assertEquals(100, along.getAndIncrement());
        Assert.assertEquals(101, along.get());
        Assert.assertEquals(100, along.decrementAndGet());
        Assert.assertEquals(100, along.getAndDecrement());
        Assert.assertEquals(99, along.get());
    }
}

