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
package com.gs.collections.impl.lazy.parallel.list;


import com.gs.collections.impl.lazy.parallel.ParallelIterableTestCase;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.gs.collections.impl.list.mutable.MultiReaderFastList;
import org.junit.Test;


public class MultiReaderFastListParallelListIterableTest extends ParallelListIterableTestCase {
    @Test(expected = IllegalArgumentException.class)
    public void asParallel_small_batch() {
        ListAdapter.adapt(MultiReaderFastList.newListWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)).asParallel(this.executorService, 0);
    }

    @Test(expected = NullPointerException.class)
    public void asParallel_null_executorService() {
        ListAdapter.adapt(MultiReaderFastList.newListWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)).asParallel(null, 2);
    }
}

