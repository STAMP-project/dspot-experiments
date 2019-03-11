/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.util;


import java.util.List;
import org.apache.groovy.stress.util.GCUtils;
import org.junit.Assert;
import org.junit.Test;


public class ManagedConcurrentMapStressTest {
    static final int ENTRY_COUNT = 10371;

    static final ReferenceBundle bundle = ReferenceBundle.getWeakBundle();

    @Test
    public void testMapRemovesCollectedReferences() throws Exception {
        ManagedConcurrentMap<Object, String> map = new ManagedConcurrentMap<Object, String>(ManagedConcurrentMapStressTest.bundle);
        // Keep a hardref so we can test get later
        List<Object> keyList = populate(map);
        Assert.assertEquals(ManagedConcurrentMapStressTest.ENTRY_COUNT, map.size());
        // Make sure we still have our entries, sample a few
        Object key1337 = keyList.remove(1337);
        Assert.assertEquals("value1337", map.get(key1337));
        Object key77 = keyList.remove(77);
        Assert.assertEquals("value77", map.get(key77));
        key1337 = null;
        key77 = null;
        GCUtils.gc();
        Assert.assertEquals(((ManagedConcurrentMapStressTest.ENTRY_COUNT) - 2), map.size());
        for (Object o : map.values()) {
            if (o instanceof AbstractConcurrentMapBase.Entry<?>) {
                @SuppressWarnings("unchecked")
                AbstractConcurrentMapBase.Entry<String> e = ((AbstractConcurrentMapBase.Entry) (o));
                if (("value77".equals(e.getValue())) || ("value1337".equals(e.getValue()))) {
                    Assert.fail("Entries not removed from map");
                }
            } else {
                Assert.fail("No Entry found");
            }
        }
        // Clear all refs and gc()
        keyList.clear();
        GCUtils.gc();
        // Add an entries to force ReferenceManager.removeStaleEntries
        map.put(new Object(), "last");
        Assert.assertEquals("Map removed weak entries", 1, map.size());
    }

    /**
     * This tests for deadlock which can happen if more than one thread is allowed
     * to process entries from the same RefQ. We run multiple iterations because it
     * wont always be detected one run.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMultipleThreadsPutWhileRemovingRefs() throws Exception {
        for (int i = 0; i < 10; i++) {
            ManagedConcurrentMap<Object, String> map = new ManagedConcurrentMap<Object, String>(ManagedConcurrentMapStressTest.bundle);
            multipleThreadsPutWhileRemovingRefs(map);
        }
    }
}

