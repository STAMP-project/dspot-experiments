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
package com.gs.collections.impl.lazy.parallel.set.sorted;


import SortedSets.immutable;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.lazy.parallel.ParallelIterableTestCase;
import java.util.NoSuchElementException;
import org.junit.Test;


public class ImmutableEmptySortedSetParallelTest extends NonParallelSortedSetIterableTestCase {
    @Test(expected = IllegalArgumentException.class)
    public void asParallel_small_batch() {
        immutable.with(Comparators.reverseNaturalOrder()).asParallel(this.executorService, 0);
    }

    @Test(expected = NullPointerException.class)
    public void asParallel_null_executorService() {
        immutable.with(Comparators.reverseNaturalOrder()).asParallel(null, 2);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min() {
        this.classUnderTest().min(Integer::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max() {
        this.classUnderTest().max(Integer::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy() {
        this.classUnderTest().minBy(String::valueOf);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy() {
        this.classUnderTest().maxBy(String::valueOf);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min_without_comparator() {
        this.classUnderTest().min();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max_without_comparator() {
        this.classUnderTest().max();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minWithEmptyBatch() {
        super.minWithEmptyBatch();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxWithEmptyBatch() {
        super.minWithEmptyBatch();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min_null_throws() {
        this.classUnderTest().min(Integer::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max_null_throws() {
        this.classUnderTest().max(Integer::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy_null_throws() {
        this.classUnderTest().minBy(Integer::valueOf);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy_null_throws() {
        this.classUnderTest().maxBy(Integer::valueOf);
    }
}

