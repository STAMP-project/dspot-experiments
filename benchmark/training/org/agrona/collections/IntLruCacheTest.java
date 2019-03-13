/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona.collections;


import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class IntLruCacheTest {
    public static final int CAPACITY = 2;

    @SuppressWarnings("unchecked")
    private final IntFunction<AutoCloseable> mockFactory = Mockito.mock(IntFunction.class);

    @SuppressWarnings("unchecked")
    private final Consumer<AutoCloseable> mockCloser = Mockito.mock(Consumer.class);

    private final IntLruCache<AutoCloseable> cache = new IntLruCache(IntLruCacheTest.CAPACITY, mockFactory, mockCloser);

    private AutoCloseable lastValue;

    @Test
    public void shouldUseFactoryToConstructValues() {
        final AutoCloseable actual = cache.lookup(1);
        Assert.assertSame(lastValue, actual);
        Assert.assertNotNull(lastValue);
        verifyOneConstructed(1);
    }

    @Test
    public void shouldCacheValues() {
        final AutoCloseable first = cache.lookup(1);
        final AutoCloseable second = cache.lookup(1);
        Assert.assertSame(lastValue, first);
        Assert.assertSame(lastValue, second);
        Assert.assertNotNull(lastValue);
        verifyOneConstructed(1);
    }

    @Test
    public void shouldEvictLeastRecentlyUsedItem() {
        final AutoCloseable first = cache.lookup(1);
        cache.lookup(2);
        cache.lookup(3);
        Mockito.verify(mockCloser).accept(first);
    }

    @Test
    public void shouldReconstructItemsAfterEviction() {
        cache.lookup(1);
        final AutoCloseable second = cache.lookup(2);
        cache.lookup(3);
        cache.lookup(1);
        Mockito.verify(mockCloser).accept(second);
        verifyOneConstructed(2);
    }

    @Test
    public void shouldSupportKeyOfZero() {
        final AutoCloseable actual = cache.lookup(0);
        Assert.assertSame(lastValue, actual);
        Assert.assertNotNull(lastValue);
    }

    @Test
    public void shouldCloseAllOpenResources() {
        final AutoCloseable first = cache.lookup(1);
        final AutoCloseable second = cache.lookup(2);
        cache.close();
        Mockito.verify(mockCloser).accept(first);
        Mockito.verify(mockCloser).accept(second);
    }
}

