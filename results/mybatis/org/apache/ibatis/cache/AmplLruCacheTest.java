/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.cache;


public class AmplLruCacheTest {
    @org.junit.Test
    public void shouldRemoveItemOnDemand() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    @org.junit.Test
    public void shouldFlushAllItemsOnDemand() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    @org.junit.Test
    public void shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries() {
        org.apache.ibatis.cache.decorators.LruCache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.setSize(5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "default");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "default");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        }
        org.junit.Assert.assertEquals(0, cache.getObject(0));
        cache.putObject(5, 5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "default");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        org.junit.Assert.assertNull(cache.getObject(1));
        org.junit.Assert.assertEquals(5, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test
    public void shouldFlushAllItemsOnDemand_literalMutation3() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: "));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "Error serializing object.  Cause: ");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test
    public void shouldFlushAllItemsOnDemand_literalMutation4_literalMutation114() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("GdhscbC"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        for (int i = 0; i < 6; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf34_cf1298_failAssert6_cf2615() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            org.junit.Assert.assertNotNull(cache.getObject(0));
            org.junit.Assert.assertNotNull(cache.getObject(4));
            cache.clear();
            org.junit.Assert.assertNull(cache.getObject(0));
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldFlushAllItemsOnDemand_cf34__16 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf34__16);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_514 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_514);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.LruCache vc_512 = (org.apache.ibatis.cache.decorators.LruCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_512);
            // StatementAdderMethod cloned existing statement
            vc_512.removeObject(vc_514);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.LruCache vc_999 = (org.apache.ibatis.cache.decorators.LruCache)null;
            // StatementAdderMethod cloned existing statement
            vc_999.getId();
            org.junit.Assert.assertNull(cache.getObject(4));
            org.junit.Assert.fail("shouldFlushAllItemsOnDemand_cf34_cf1298 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf6623() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_cf6623__8 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf6623__8);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_literalMutation6599_cf6706() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: "));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "Error serializing object.  Cause: ");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "Error serializing object.  Cause: ");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // AssertGenerator replace invocation
        java.lang.String o_shouldRemoveItemOnDemand_literalMutation6599_cf6706__14 = // StatementAdderMethod cloned existing statement
cache.getId();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand_literalMutation6599_cf6706__14, "Error serializing object.  Cause: ");
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf6623_cf7642_failAssert29_cf11656() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_cf6623__8 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf6623__8);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2879 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2879);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.LruCache vc_2877 = (org.apache.ibatis.cache.decorators.LruCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2877);
            // StatementAdderMethod cloned existing statement
            vc_2877.getObject(vc_2879);
            // StatementAdderOnAssert create literal from method
            int int_vc_194 = 0;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.LruCache vc_4482 = (org.apache.ibatis.cache.decorators.LruCache)null;
            // StatementAdderMethod cloned existing statement
            vc_4482.setSize(int_vc_194);
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_cf6623_cf7642 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12446() {
        org.apache.ibatis.cache.decorators.LruCache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.setSize(5);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertEquals(0, cache.getObject(0));
        cache.putObject(5, 5);
        org.junit.Assert.assertNull(cache.getObject(1));
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12446__15 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12446__15);
        org.junit.Assert.assertEquals(5, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12432_add13705() {
        org.apache.ibatis.cache.decorators.LruCache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.setSize(5);
        for (int i = 0; i < 5; i++) {
            // MethodCallAdder
            cache.putObject(i, i);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "default");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
            cache.putObject(i, i);
        }
        org.junit.Assert.assertEquals(0, cache.getObject(0));
        cache.putObject(5, 5);
        org.junit.Assert.assertNull(cache.getObject(1));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4765 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4765);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4765);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12432__17 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_4765);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12432__17);
        org.junit.Assert.assertEquals(5, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.LruCacheTest#shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12436_failAssert14_literalMutation13874_cf14729() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.decorators.LruCache cache = new org.apache.ibatis.cache.decorators.LruCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
            cache.setSize(5);
            for (int i = 4; i < 5; i++) {
                cache.putObject(i, i);
            }
            // MethodAssertGenerator build local variable
            Object o_10_0 = cache.getObject(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_10_0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_10_0);
            cache.putObject(5, 5);
            // StatementAdderOnAssert create literal from method
            int int_vc_247 = 5;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_247, 5);
            // StatementAdderMethod cloned existing statement
            cache.setSize(int_vc_247);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getSize(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.LruCache)cache).getId(), "default");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.LruCache)cache).getReadWriteLock());
            org.junit.Assert.assertNull(cache.getObject(1));
            // StatementAdderOnAssert create null value
            java.lang.Object vc_4769 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4769);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4769);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.LruCache vc_4767 = (org.apache.ibatis.cache.decorators.LruCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4767);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4767);
            // StatementAdderMethod cloned existing statement
            vc_4767.removeObject(vc_4769);
            // MethodAssertGenerator build local variable
            Object o_21_0 = cache.getSize();
            org.junit.Assert.fail("shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries_cf12436 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

