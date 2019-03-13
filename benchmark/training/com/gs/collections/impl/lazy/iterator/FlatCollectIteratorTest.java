/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.lazy.iterator;


import Lists.fixedSize;
import Lists.immutable;
import com.gs.collections.impl.block.factory.Functions;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class FlatCollectIteratorTest {
    @Test(expected = NoSuchElementException.class)
    public void nextIfDoesntHaveAnything() {
        new FlatCollectIterator(immutable.of(), ( object) -> null).next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeIsUnsupported() {
        new FlatCollectIterator(immutable.of().iterator(), ( object) -> null).remove();
    }

    @Test
    public void nextAfterEmptyIterable() {
        Object expected = new Object();
        FlatCollectIterator<List<Object>, Object> flattenIterator = new FlatCollectIterator(fixedSize.<List<Object>>of(fixedSize.of(), fixedSize.of(expected)), Functions.<List<Object>>getPassThru());
        Assert.assertSame(expected, flattenIterator.next());
    }
}

