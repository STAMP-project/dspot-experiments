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


public class ManagedConcurrentValueMapStressTest {
    static final int ENTRY_COUNT = 10371;

    static final ReferenceBundle bundle = ReferenceBundle.getWeakBundle();

    @Test
    public void testMapRemovesCollectedReferences() throws InterruptedException {
        ManagedConcurrentValueMap<String, Object> map = new ManagedConcurrentValueMap<String, Object>(ManagedConcurrentValueMapStressTest.bundle);
        // Keep a hardref so we can test get later
        List<Object> valueList = populate(map);
        // Make sure we still have our entries, sample a few
        Object value77 = map.get("key77");
        Assert.assertEquals(valueList.get(77), value77);
        Object value1337 = map.get("key1337");
        Assert.assertEquals(valueList.get(1337), value1337);
        // Clear hardrefs and gc()
        value77 = null;
        value1337 = null;
        valueList.clear();
        GCUtils.gc();
        // Add an entries to force ReferenceManager.removeStaleEntries
        map.put("keyLast", new Object());
        // No size() method, so let's just check a few keys we that should have been collected
        Assert.assertEquals(null, map.get("key77"));
        Assert.assertEquals(null, map.get("key1337"));
        Assert.assertEquals(null, map.get("key3559"));
        Assert.assertEquals(1, ManagedConcurrentValueMapStressTest.size(map));
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
            ManagedConcurrentValueMap<String, Object> map = new ManagedConcurrentValueMap<String, Object>(ManagedConcurrentValueMapStressTest.bundle);
            multipleThreadsPutWhileRemovingRefs(map);
        }
    }
}

