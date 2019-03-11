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
import org.apache.geode.internal.cache.DiskStoreImpl.OplogEntryIdSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests DiskStoreImpl.OplogEntryIdSet
 */
public class OplogEntryIdSetJUnitTest {
    @Test
    public void testBasics() {
        OplogEntryIdSet s = new OplogEntryIdSet();
        for (long i = 1; i < 777777; i++) {
            Assert.assertEquals(false, s.contains(i));
        }
        for (long i = 1; i < 777777; i++) {
            s.add(i);
        }
        for (long i = 1; i < 777777; i++) {
            Assert.assertEquals(true, s.contains(i));
        }
        try {
            s.add(INVALID_ID);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(false, s.contains(0));
        Assert.assertEquals(false, s.contains(4294967295L));
        s.add(4294967295L);
        Assert.assertEquals(true, s.contains(4294967295L));
        for (long i = 4294967295L + 1; i < (4294967295L + 777777); i++) {
            Assert.assertEquals(false, s.contains(i));
        }
        for (long i = 4294967295L + 1; i < (4294967295L + 777777); i++) {
            s.add(i);
        }
        for (long i = 4294967295L + 1; i < (4294967295L + 777777); i++) {
            Assert.assertEquals(true, s.contains(i));
        }
        for (long i = 1; i < 777777; i++) {
            Assert.assertEquals(true, s.contains(i));
        }
        Assert.assertEquals(false, s.contains(Long.MAX_VALUE));
        s.add(Long.MAX_VALUE);
        Assert.assertEquals(true, s.contains(Long.MAX_VALUE));
        Assert.assertEquals(false, s.contains(Long.MIN_VALUE));
        s.add(Long.MIN_VALUE);
        Assert.assertEquals(true, s.contains(Long.MIN_VALUE));
    }
}

