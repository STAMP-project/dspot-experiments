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
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class TapIteratorTest {
    @Test(expected = NoSuchElementException.class)
    public void nextIfDoesntHaveAnything() {
        new TapIterator(immutable.of(), ( object) -> {
        }).next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeIsUnsupported() {
        new TapIterator(immutable.of().iterator(), ( object) -> {
        }).remove();
    }

    @Test
    public void nextAfterEmptyIterable() {
        Object expected = new Object();
        TapIterator<Object> iterator = new TapIterator(fixedSize.of(expected), ( object) -> {
        });
        Assert.assertSame(expected, iterator.next());
    }
}

