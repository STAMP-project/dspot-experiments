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


public class AmplFifoCacheTest {
    @org.junit.Test
    public void shouldRemoveFirstItemInBeyondFiveEntries() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.setSize(5);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertEquals(0, cache.getObject(0));
        cache.putObject(5, 5);
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertEquals(5, cache.getSize());
    }

    @org.junit.Test
    public void shouldFlushAllItemsOnDemand() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
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
    public void shouldRemoveItemOnDemand() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.putObject(0, 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getId(), "default");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getSize(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.FifoCache)cache).getReadWriteLock());
        org.junit.Assert.assertNotNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand__7 = cache.removeObject(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand__7, 0);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf34() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
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
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf34_cf1392() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
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
        // AssertGenerator replace invocation
        int o_shouldFlushAllItemsOnDemand_cf34_cf1392__20 = // StatementAdderMethod cloned existing statement
cache.getSize();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldFlushAllItemsOnDemand_cf34_cf1392__20, 0);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf20_cf863_literalMutation7635() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(3));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldFlushAllItemsOnDemand_cf20__18 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf20__18);
        // StatementAdderOnAssert create literal from method
        int int_vc_15 = 4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(int_vc_15, 4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(int_vc_15, 4);
        // StatementAdderMethod cloned existing statement
        cache.setSize(int_vc_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getId(), "default");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.FifoCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getId(), "default");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.FifoCache)cache).getReadWriteLock());
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldRemoveFirstItemInBeyondFiveEntries */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveFirstItemInBeyondFiveEntries_cf7737() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.setSize(5);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertEquals(0, cache.getObject(0));
        cache.putObject(5, 5);
        org.junit.Assert.assertNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldRemoveFirstItemInBeyondFiveEntries_cf7737__15 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveFirstItemInBeyondFiveEntries_cf7737__15);
        org.junit.Assert.assertEquals(5, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldRemoveFirstItemInBeyondFiveEntries */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveFirstItemInBeyondFiveEntries_cf7727_failAssert16_cf9174() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
            cache.setSize(5);
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            // MethodAssertGenerator build local variable
            Object o_10_0 = cache.getObject(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, 0);
            cache.putObject(5, 5);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldRemoveFirstItemInBeyondFiveEntries_cf7727_failAssert16_cf9174__16 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldRemoveFirstItemInBeyondFiveEntries_cf7727_failAssert16_cf9174__16);
            org.junit.Assert.assertNull(cache.getObject(0));
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2952 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2952);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.FifoCache vc_2950 = (org.apache.ibatis.cache.decorators.FifoCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2950);
            // StatementAdderMethod cloned existing statement
            vc_2950.removeObject(vc_2952);
            // MethodAssertGenerator build local variable
            Object o_21_0 = cache.getSize();
            org.junit.Assert.fail("shouldRemoveFirstItemInBeyondFiveEntries_cf7727 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldRemoveFirstItemInBeyondFiveEntries */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveFirstItemInBeyondFiveEntries_cf7723_literalMutation9001_cf15372() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
        cache.setSize(5);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertEquals(0, cache.getObject(0));
        cache.putObject(5, 5);
        org.junit.Assert.assertNull(cache.getObject(10));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2948 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2948);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2948);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2948);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveFirstItemInBeyondFiveEntries_cf7723__17 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_2948);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveFirstItemInBeyondFiveEntries_cf7723__17);
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldRemoveFirstItemInBeyondFiveEntries_cf7723_literalMutation9001_cf15372__26 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveFirstItemInBeyondFiveEntries_cf7723_literalMutation9001_cf15372__26);
        org.junit.Assert.assertEquals(5, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test
    public void shouldRemoveItemOnDemand_literalMutation17942() {
        org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("defaut"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getId(), "defaut");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.FifoCache)cache).getReadWriteLock());
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf17963_cf19023_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_cf17963__8 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf17963__8);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.FifoCache vc_7521 = (org.apache.ibatis.cache.decorators.FifoCache)null;
            // StatementAdderMethod cloned existing statement
            vc_7521.getSize();
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_cf17963_cf19023 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.FifoCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf17963_cf19023_failAssert8_add20718() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.decorators.FifoCache cache = new org.apache.ibatis.cache.decorators.FifoCache(new org.apache.ibatis.cache.impl.PerpetualCache("default"));
            // MethodCallAdder
            cache.putObject(0, 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getId(), "default");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.decorators.FifoCache)cache).getSize(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.decorators.FifoCache)cache).getReadWriteLock());
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_cf17963__8 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf17963__8);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.decorators.FifoCache vc_7521 = (org.apache.ibatis.cache.decorators.FifoCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7521);
            // StatementAdderMethod cloned existing statement
            vc_7521.getSize();
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_cf17963_cf19023 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

