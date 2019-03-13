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
package com.gs.collections.impl.map.sorted.mutable;


import com.gs.collections.api.map.sorted.MutableSortedMap;
import java.util.SortedMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link SortedMapAdapter}.
 */
public class SortedMapAdapterTest extends MutableSortedMapTestCase {
    @Test(expected = NullPointerException.class)
    public void testNewNull() {
        SortedMapAdapter.adapt(null);
    }

    @Test
    public void testAdapt() {
        TreeSortedMap<Integer, String> sortedMap = TreeSortedMap.newMapWith(1, "1", 2, "2");
        MutableSortedMap<Integer, String> adapt = SortedMapAdapter.adapt(sortedMap);
        Assert.assertSame(sortedMap, adapt);
        SortedMap<Integer, String> treeMap = new java.util.TreeMap(sortedMap);
        MutableSortedMap<Integer, String> treeAdapt = SortedMapAdapter.adapt(treeMap);
        Assert.assertNotSame(treeMap, treeAdapt);
        Assert.assertEquals(treeMap, treeAdapt);
    }
}

