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


import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class Object2IntHashMapNotAvoidingAllocationTest extends Object2IntHashMapTest {
    @Test
    public void valuesIteratorIsNotCached() {
        Assert.assertNotSame(objectToIntMap.values().iterator(), objectToIntMap.values().iterator());
    }

    @Test
    public void keysIteratorIsNotCached() {
        Assert.assertNotSame(objectToIntMap.keySet().iterator(), objectToIntMap.keySet().iterator());
    }

    @Test
    public void entryIteratorIsNotCached() {
        Assert.assertNotSame(objectToIntMap.entrySet().iterator(), objectToIntMap.entrySet().iterator());
    }

    @Test
    public void entriesAreAllocatedByEntriesIterator() {
        objectToIntMap.put("1", 1);
        objectToIntMap.put("2", 2);
        final Iterator<Map.Entry<String, Integer>> entryIterator = objectToIntMap.entrySet().iterator();
        final Map.Entry<String, Integer> entry1 = entryIterator.next();
        final Map.Entry<String, Integer> entry2 = entryIterator.next();
        Assert.assertNotEquals(entry1, entry2);
    }
}

