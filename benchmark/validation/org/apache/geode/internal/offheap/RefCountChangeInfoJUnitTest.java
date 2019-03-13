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
package org.apache.geode.internal.offheap;


import org.junit.Assert;
import org.junit.Test;


public class RefCountChangeInfoJUnitTest {
    @Test
    public void testGetOwner() {
        String owner1 = new String("Info1");
        String notOwner1 = new String("notInfo1");
        RefCountChangeInfo refInfo1 = new RefCountChangeInfo(true, 1, owner1);
        RefCountChangeInfo refInfo2 = new RefCountChangeInfo(true, 1, notOwner1);
        Assert.assertEquals(owner1, refInfo1.getOwner());
        Assert.assertEquals(notOwner1, refInfo2.getOwner());
        Assert.assertFalse(refInfo1.getOwner().equals(refInfo2.getOwner()));
    }

    @Test
    public void testNullOwner() {
        String owner1 = null;
        String notOwner1 = new String("notInfo1");
        RefCountChangeInfo refInfo1 = new RefCountChangeInfo(true, 1, owner1);
        RefCountChangeInfo refInfo2 = new RefCountChangeInfo(true, 1, notOwner1);
        Assert.assertFalse(isOwnerNull(refInfo2.getOwner()));
        Assert.assertTrue(hasStringLit(refInfo2.toString(), " owner="));
        Assert.assertEquals(owner1, refInfo1.getOwner());
        Assert.assertEquals(notOwner1, refInfo2.getOwner());
        Assert.assertTrue(isOwnerNull(refInfo1.getOwner()));
        Assert.assertFalse(hasStringLit(refInfo1.toString(), " owner="));
    }

    @Test
    public void testGetUseCount() {
        String owner1 = new String("Info1");
        String owner2 = new String("Info2");
        RefCountChangeInfo refInfo1 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertEquals(0, refInfo1.getUseCount());
        RefCountChangeInfo refInfo2 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertTrue(refInfo1.isSameCaller(refInfo2));
        refInfo1.incUseCount();
        Assert.assertEquals(1, refInfo1.getUseCount());
        // owner not used in isDup
        RefCountChangeInfo refInfo3 = new RefCountChangeInfo(true, 1, owner2);
        Assert.assertTrue(refInfo1.isSameCaller(refInfo3));
        refInfo1.incUseCount();
        Assert.assertEquals(2, refInfo1.getUseCount());
        RefCountChangeInfo refInfo4 = new RefCountChangeInfo(false, 1, owner2);
        Assert.assertFalse(refInfo1.isSameCaller(refInfo4));
        Assert.assertEquals(2, refInfo1.getUseCount());
    }

    @Test
    public void testDecUseCount() {
        String owner1 = new String("Info1");
        String owner2 = new String("Info2");
        RefCountChangeInfo refInfo1 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertEquals(0, refInfo1.getUseCount());
        RefCountChangeInfo refInfo2 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertTrue(refInfo1.isSameCaller(refInfo2));
        refInfo1.incUseCount();
        Assert.assertEquals(1, refInfo1.getUseCount());
        // owner not used in isSameCaller check
        RefCountChangeInfo refInfo3 = new RefCountChangeInfo(true, 1, owner2);
        Assert.assertTrue(refInfo1.isSameCaller(refInfo3));
        refInfo1.incUseCount();
        Assert.assertEquals(2, refInfo1.getUseCount());
        refInfo1.decUseCount();
        Assert.assertEquals(1, refInfo1.getUseCount());
        refInfo1.decUseCount();
        Assert.assertEquals(0, refInfo1.getUseCount());
    }

    @Test
    public void testToString() {
        String owner1 = new String("Info1");
        RefCountChangeInfo refInfo1 = new RefCountChangeInfo(true, 1, owner1);
        RefCountChangeInfo refInfo2 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertEquals(refInfo1.toString(), refInfo2.toString());
        RefCountChangeInfo refInfo3 = new RefCountChangeInfo(false, 1, owner1);
        Assert.assertFalse(refInfo1.toString().equals(refInfo3.toString()));
        RefCountChangeInfo refInfo4 = new RefCountChangeInfo(true, 2, owner1);
        Assert.assertFalse(refInfo1.toString().equals(refInfo4.toString()));
    }

    @Test
    public void testisSameCaller() {
        String owner1 = new String("Info1");
        String owner2 = new String("Info2");
        RefCountChangeInfo refInfo1 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertEquals(0, refInfo1.getUseCount());
        RefCountChangeInfo refInfo2 = new RefCountChangeInfo(true, 1, owner1);
        Assert.assertTrue(refInfo1.isSameCaller(refInfo2));
        refInfo1.incUseCount();
        Assert.assertEquals(1, refInfo1.getUseCount());
        String str = refInfo1.toString();
        str = refInfo1.toString();
        Assert.assertTrue(hasStringLit(refInfo1.toString(), " useCount=1"));
        RefCountChangeInfo refInfo3 = new RefCountChangeInfo(false, 1, owner1);
        Assert.assertFalse(refInfo1.isSameCaller(refInfo3));
        Assert.assertEquals(1, refInfo1.getUseCount());
        RefCountChangeInfo refInfo4 = new RefCountChangeInfo(true, 1, owner2);
        Assert.assertTrue(refInfo1.isSameCaller(refInfo4));
        refInfo1.incUseCount();
        Assert.assertEquals(2, refInfo1.getUseCount());
        Assert.assertTrue(hasStringLit(refInfo1.toString(), " useCount=2"));
        refInfo1.setStackTraceString("not_the_same");
        Assert.assertFalse(refInfo1.isSameCaller(refInfo4));
        Assert.assertEquals(2, refInfo1.getUseCount());
        refInfo1.setStackTraceString(null);
        refInfo1.setStackTraceString(new RefCountChangeInfoJUnitTest.SameHashDifferentTrace());
        refInfo4.setStackTraceString(new RefCountChangeInfoJUnitTest.SameHashDifferentTrace());
        Assert.assertFalse(refInfo1.isSameCaller(refInfo4));
        Assert.assertEquals(2, refInfo1.getUseCount());
    }

    private static class SameHashDifferentTrace {
        public int hashCode() {
            return 1;
        }

        public boolean equals(Object notused) {
            return false;
        }
    }
}

