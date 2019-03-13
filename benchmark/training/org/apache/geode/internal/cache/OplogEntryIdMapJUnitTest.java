/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import DiskStoreImpl.INVALID_ID;
import OplogEntryIdMap.Iterator;
import org.apache.geode.internal.cache.Oplog.OplogEntryIdMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests DiskStoreImpl.OplogEntryIdMap
 */
public class OplogEntryIdMapJUnitTest {
    @Test
    public void testBasics() {
        OplogEntryIdMap m = new OplogEntryIdMap();
        for (long i = 1; i <= 777777; i++) {
            Assert.assertEquals(null, m.get(i));
        }
        for (long i = 1; i <= 777777; i++) {
            m.put(i, new Long(i));
        }
        for (long i = 1; i <= 777777; i++) {
            Assert.assertEquals(new Long(i), m.get(i));
        }
        Assert.assertEquals(777777, m.size());
        try {
            m.put(INVALID_ID, new Object());
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(null, m.get(0));
        Assert.assertEquals(777777, m.size());
        Assert.assertEquals(null, m.get(4294967295L));
        m.put(4294967295L, new Long(4294967295L));
        Assert.assertEquals(new Long(4294967295L), m.get(4294967295L));
        Assert.assertEquals((777777 + 1), m.size());
        for (long i = 4294967295L + 1; i <= (4294967295L + 777777); i++) {
            Assert.assertEquals(null, m.get(i));
        }
        for (long i = 4294967295L + 1; i <= (4294967295L + 777777); i++) {
            m.put(i, new Long(i));
        }
        for (long i = 4294967295L + 1; i <= (4294967295L + 777777); i++) {
            Assert.assertEquals(new Long(i), m.get(i));
        }
        Assert.assertEquals(((777777 + 1) + 777777), m.size());
        for (long i = 1; i < 777777; i++) {
            Assert.assertEquals(new Long(i), m.get(i));
        }
        Assert.assertEquals(null, m.get(Long.MAX_VALUE));
        m.put(Long.MAX_VALUE, new Long(Long.MAX_VALUE));
        Assert.assertEquals(new Long(Long.MAX_VALUE), m.get(Long.MAX_VALUE));
        Assert.assertEquals((((777777 + 1) + 777777) + 1), m.size());
        Assert.assertEquals(null, m.get(Long.MIN_VALUE));
        m.put(Long.MIN_VALUE, new Long(Long.MIN_VALUE));
        Assert.assertEquals(new Long(Long.MIN_VALUE), m.get(Long.MIN_VALUE));
        Assert.assertEquals(((((777777 + 1) + 777777) + 1) + 1), m.size());
        int count = 0;
        for (OplogEntryIdMap.Iterator it = m.iterator(); it.hasNext();) {
            count++;
            it.advance();
            it.key();
            it.value();
        }
        Assert.assertEquals(((((777777 + 1) + 777777) + 1) + 1), count);
    }
}

