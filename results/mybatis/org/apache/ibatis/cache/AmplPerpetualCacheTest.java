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


public class AmplPerpetualCacheTest {
    @org.junit.Test
    public void shouldDemonstrateHowAllObjectsAreKept() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    @org.junit.Test
    public void shouldDemonstrateCopiesAreEqual() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    @org.junit.Test
    public void shouldFlushAllItemsOnDemand() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
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
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand__8 = cache.removeObject(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand__8, 0);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf17() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_3 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_shouldDemonstrateCopiesAreEqual_cf17__12 = // StatementAdderMethod cloned existing statement
cache.equals(vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldDemonstrateCopiesAreEqual_cf17__12);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf35() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_15 = new java.lang.Object();
            // AssertGenerator replace invocation
            java.lang.Object o_shouldDemonstrateCopiesAreEqual_cf35__12 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_15);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf35__12);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf24() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            // AssertGenerator replace invocation
            int o_shouldDemonstrateCopiesAreEqual_cf24__10 = // StatementAdderMethod cloned existing statement
cache.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_shouldDemonstrateCopiesAreEqual_cf24__10, 1544803905);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf42() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateCopiesAreEqual_cf42__10 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf42__10);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test
    public void shouldDemonstrateCopiesAreEqual_literalMutation4() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("de*ault");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "de*ault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf35_cf873_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_15 = new java.lang.Object();
                // AssertGenerator replace invocation
                java.lang.Object o_shouldDemonstrateCopiesAreEqual_cf35__12 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_15);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf35__12);
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_315 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_312 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_312.equals(vc_315);
                // MethodAssertGenerator build local variable
                Object o_22_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf35_cf873 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf34_cf794_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_14 = (java.lang.Object)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_14);
                // AssertGenerator replace invocation
                java.lang.Object o_shouldDemonstrateCopiesAreEqual_cf34__12 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_14);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf34__12);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_288 = (java.lang.Object)null;
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_286 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_286.equals(vc_288);
                // MethodAssertGenerator build local variable
                Object o_24_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf34_cf794 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_literalMutation2_cf72_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_30 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_30.getSize();
                // MethodAssertGenerator build local variable
                Object o_20_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_literalMutation2_cf72 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_literalMutation3_cf128() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 1000; i++) {
            cache.putObject(i, i);
            // AssertGenerator replace invocation
            int o_shouldDemonstrateCopiesAreEqual_literalMutation3_cf128__16 = // StatementAdderMethod cloned existing statement
cache.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_shouldDemonstrateCopiesAreEqual_literalMutation3_cf128__16, 1550737404);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf42_cf1026_failAssert32() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // AssertGenerator replace invocation
                java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateCopiesAreEqual_cf42__10 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf42__10);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_366 = (java.lang.Object)null;
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_364 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_364.equals(vc_366);
                // MethodAssertGenerator build local variable
                Object o_20_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf42_cf1026 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test
    public void shouldDemonstrateCopiesAreEqual_literalMutation6_literalMutation271() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 0; i < 999; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf16_cf373_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_2 = (java.lang.Object)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_2);
                // AssertGenerator replace invocation
                boolean o_shouldDemonstrateCopiesAreEqual_cf16__12 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_shouldDemonstrateCopiesAreEqual_cf16__12);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_164 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_164.getObject(vc_2);
                // MethodAssertGenerator build local variable
                Object o_22_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf16_cf373 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf17_cf448_failAssert39() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_3 = new java.lang.Object();
                // AssertGenerator replace invocation
                boolean o_shouldDemonstrateCopiesAreEqual_cf17__12 = // StatementAdderMethod cloned existing statement
cache.equals(vc_3);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_shouldDemonstrateCopiesAreEqual_cf17__12);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_192 = (java.lang.Object)null;
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_190 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_190.getObject(vc_192);
                // MethodAssertGenerator build local variable
                Object o_22_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf17_cf448 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_literalMutation3_cf128_cf4106_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("GdhscbC");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // AssertGenerator replace invocation
                int o_shouldDemonstrateCopiesAreEqual_literalMutation3_cf128__16 = // StatementAdderMethod cloned existing statement
cache.hashCode();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_shouldDemonstrateCopiesAreEqual_literalMutation3_cf128__16, 1550737404);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_1226 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_1226.getSize();
                // MethodAssertGenerator build local variable
                Object o_30_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_literalMutation3_cf128_cf4106 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_literalMutation5_cf243_cf1966_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defa}ult");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defa}ult");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defa}ult");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_119 = new java.lang.Object();
                // AssertGenerator replace invocation
                java.lang.Object o_shouldDemonstrateCopiesAreEqual_literalMutation5_cf243__18 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_119);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_literalMutation5_cf243__18);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_616 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_616.getReadWriteLock();
                // MethodAssertGenerator build local variable
                Object o_32_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_literalMutation5_cf243_cf1966 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf29_cf695_cf3270_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_11 = new java.lang.Object();
                // AssertGenerator replace invocation
                java.lang.Object o_shouldDemonstrateCopiesAreEqual_cf29__12 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_11);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf29__12);
                // AssertGenerator replace invocation
                boolean o_shouldDemonstrateCopiesAreEqual_cf29_cf695__16 = // StatementAdderMethod cloned existing statement
cache.equals(vc_11);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_shouldDemonstrateCopiesAreEqual_cf29_cf695__16);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_978 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_978.getId();
                // MethodAssertGenerator build local variable
                Object o_24_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf29_cf695_cf3270 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_literalMutation6_cf302_literalMutation1504() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 999; i < 1000; i++) {
            cache.putObject(i, i);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateCopiesAreEqual_literalMutation6_cf302__16 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_literalMutation6_cf302__16);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_literalMutation3_cf133_literalMutation2588() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbC");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        for (int i = 2000; i < 1000; i++) {
            cache.putObject(i, i);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_63 = new java.lang.Object();
            // AssertGenerator replace invocation
            java.lang.Object o_shouldDemonstrateCopiesAreEqual_literalMutation3_cf133__18 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_63);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_literalMutation3_cf133__18);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf16_cf373_failAssert35_literalMutation3395() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_2 = (java.lang.Object)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_2);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_2);
                // AssertGenerator replace invocation
                boolean o_shouldDemonstrateCopiesAreEqual_cf16__12 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_shouldDemonstrateCopiesAreEqual_cf16__12);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_164 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_164);
                // StatementAdderMethod cloned existing statement
                vc_164.getObject(vc_2);
                // MethodAssertGenerator build local variable
                Object o_22_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf16_cf373 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateCopiesAreEqual */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateCopiesAreEqual_cf42_cf1040_cf2965_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
            for (int i = 0; i < 1000; i++) {
                cache.putObject(i, i);
                // AssertGenerator replace invocation
                java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateCopiesAreEqual_cf42__10 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf42__10);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_374 = (java.lang.Object)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_374);
                // AssertGenerator replace invocation
                java.lang.Object o_shouldDemonstrateCopiesAreEqual_cf42_cf1040__16 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_374);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_shouldDemonstrateCopiesAreEqual_cf42_cf1040__16);
                // StatementAdderOnAssert create null value
                org.apache.ibatis.cache.impl.PerpetualCache vc_888 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
                // StatementAdderMethod cloned existing statement
                vc_888.getSize();
                // MethodAssertGenerator build local variable
                Object o_26_0 = cache.getObject(i);
            }
            org.junit.Assert.fail("shouldDemonstrateCopiesAreEqual_cf42_cf1040_cf2965 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4531() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        // AssertGenerator replace invocation
        int o_shouldDemonstrateHowAllObjectsAreKept_cf4531__12 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldDemonstrateHowAllObjectsAreKept_cf4531__12, 1544803905);
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4542() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1367 = new java.lang.Object();
        // AssertGenerator replace invocation
        java.lang.Object o_shouldDemonstrateHowAllObjectsAreKept_cf4542__14 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_1367);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4542__14);
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4523() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1354 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1354);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateHowAllObjectsAreKept_cf4523__14 = // StatementAdderMethod cloned existing statement
cache.equals(vc_1354);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateHowAllObjectsAreKept_cf4523__14);
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test
    public void shouldDemonstrateHowAllObjectsAreKept_literalMutation4506() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("depfault");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "depfault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4549() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateHowAllObjectsAreKept_cf4549__12 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4549__12);
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_literalMutation4506_cf4639_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("depfault");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "depfault");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_16_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1408 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_1408.getSize();
            // MethodAssertGenerator build local variable
            Object o_22_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_literalMutation4506_cf4639 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4549_cf5802_failAssert51() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateHowAllObjectsAreKept_cf4549__12 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4549__12);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1760 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_1760.getReadWriteLock();
            // MethodAssertGenerator build local variable
            Object o_20_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4549_cf5802 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4541_cf5562_failAssert59() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1366 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1366);
            // AssertGenerator replace invocation
            java.lang.Object o_shouldDemonstrateHowAllObjectsAreKept_cf4541__14 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_1366);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4541__14);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1676 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_1676.removeObject(vc_1366);
            // MethodAssertGenerator build local variable
            Object o_24_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4541_cf5562 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4524_cf5143_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1355 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_shouldDemonstrateHowAllObjectsAreKept_cf4524__14 = // StatementAdderMethod cloned existing statement
cache.equals(vc_1355);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldDemonstrateHowAllObjectsAreKept_cf4524__14);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1554 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_1554.clear();
            // MethodAssertGenerator build local variable
            Object o_22_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4524_cf5143 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4523_cf5063_failAssert54() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1354 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1354);
            // AssertGenerator replace invocation
            boolean o_shouldDemonstrateHowAllObjectsAreKept_cf4523__14 = // StatementAdderMethod cloned existing statement
cache.equals(vc_1354);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldDemonstrateHowAllObjectsAreKept_cf4523__14);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1528 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_1528.clear();
            // MethodAssertGenerator build local variable
            Object o_24_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4523_cf5063 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_literalMutation4507_cf4699() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 100000; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        // AssertGenerator replace invocation
        int o_shouldDemonstrateHowAllObjectsAreKept_literalMutation4507_cf4699__18 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldDemonstrateHowAllObjectsAreKept_literalMutation4507_cf4699__18, -1335641212);
        org.junit.Assert.assertEquals(100000, cache.getSize());
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4549_cf5785_cf7308_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldDemonstrateHowAllObjectsAreKept_cf4549__12 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4549__12);
            // AssertGenerator replace invocation
            int o_shouldDemonstrateHowAllObjectsAreKept_cf4549_cf5785__16 = // StatementAdderMethod cloned existing statement
cache.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_shouldDemonstrateHowAllObjectsAreKept_cf4549_cf5785__16, 1544803905);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_2065 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_2062 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_2062.getObject(vc_2065);
            // MethodAssertGenerator build local variable
            Object o_26_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4549_cf5785_cf7308 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4541_cf5562_failAssert59_literalMutation8367() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 100001; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1366 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1366);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1366);
            // AssertGenerator replace invocation
            java.lang.Object o_shouldDemonstrateHowAllObjectsAreKept_cf4541__14 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_1366);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4541__14);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1676 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1676);
            // StatementAdderMethod cloned existing statement
            vc_1676.removeObject(vc_1366);
            // MethodAssertGenerator build local variable
            Object o_24_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4541_cf5562 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4524_cf5130_cf7912_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1355 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_shouldDemonstrateHowAllObjectsAreKept_cf4524__14 = // StatementAdderMethod cloned existing statement
cache.equals(vc_1355);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldDemonstrateHowAllObjectsAreKept_cf4524__14);
            // AssertGenerator replace invocation
            java.lang.Object o_shouldDemonstrateHowAllObjectsAreKept_cf4524_cf5130__18 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_1355);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldDemonstrateHowAllObjectsAreKept_cf4524_cf5130__18);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_2170 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_2170.removeObject(vc_1355);
            // MethodAssertGenerator build local variable
            Object o_26_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4524_cf5130_cf7912 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_literalMutation4506_cf4646_failAssert48_literalMutation8034() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("depfault");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "depfault");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "depfault");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_16_0 = cache.getObject(i);
            }
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1415 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1412 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1412);
            // StatementAdderMethod cloned existing statement
            vc_1412.getObject(vc_1415);
            // MethodAssertGenerator build local variable
            Object o_24_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_literalMutation4506_cf4646 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldDemonstrateHowAllObjectsAreKept */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateHowAllObjectsAreKept_cf4523_cf5057_failAssert15_literalMutation6716() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 99999; i < 100000; i++) {
                cache.putObject(i, i);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cache.getObject(i);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_10_0, 99999);
            }
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1354 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1354);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1354);
            // AssertGenerator replace invocation
            boolean o_shouldDemonstrateHowAllObjectsAreKept_cf4523__14 = // StatementAdderMethod cloned existing statement
cache.equals(vc_1354);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldDemonstrateHowAllObjectsAreKept_cf4523__14);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_1524 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1524);
            // StatementAdderMethod cloned existing statement
            vc_1524.getId();
            // MethodAssertGenerator build local variable
            Object o_24_0 = cache.getSize();
            org.junit.Assert.fail("shouldDemonstrateHowAllObjectsAreKept_cf4523_cf5057 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8435() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // AssertGenerator replace invocation
        int o_shouldFlushAllItemsOnDemand_cf8435__17 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldFlushAllItemsOnDemand_cf8435__17, 1544803905);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8445() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2224 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2224);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldFlushAllItemsOnDemand_cf8445__19 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_2224);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8445__19);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test
    public void shouldFlushAllItemsOnDemand_literalMutation8417() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8453() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldFlushAllItemsOnDemand_cf8453__17 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8453__17);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8427() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2212 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2212);
        // AssertGenerator replace invocation
        boolean o_shouldFlushAllItemsOnDemand_cf8427__19 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2212);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldFlushAllItemsOnDemand_cf8427__19);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test
    public void shouldFlushAllItemsOnDemand_literalMutation8414_literalMutation8525() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defalt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = -1; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8453_cf10221() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldFlushAllItemsOnDemand_cf8453__17 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8453__17);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_2949 = new java.lang.Object();
        // AssertGenerator replace invocation
        java.lang.Object o_shouldFlushAllItemsOnDemand_cf8453_cf10221__23 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_2949);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8453_cf10221__23);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8427_cf9072_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            org.junit.Assert.assertNotNull(cache.getObject(0));
            org.junit.Assert.assertNotNull(cache.getObject(4));
            cache.clear();
            org.junit.Assert.assertNull(cache.getObject(0));
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2212 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2212);
            // AssertGenerator replace invocation
            boolean o_shouldFlushAllItemsOnDemand_cf8427__19 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2212);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldFlushAllItemsOnDemand_cf8427__19);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_2502 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_2502.hashCode();
            org.junit.Assert.assertNull(cache.getObject(4));
            org.junit.Assert.fail("shouldFlushAllItemsOnDemand_cf8427_cf9072 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8428_cf9149_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            org.junit.Assert.assertNotNull(cache.getObject(0));
            org.junit.Assert.assertNotNull(cache.getObject(4));
            cache.clear();
            org.junit.Assert.assertNull(cache.getObject(0));
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_2213 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_shouldFlushAllItemsOnDemand_cf8428__19 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2213);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldFlushAllItemsOnDemand_cf8428__19);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_2528 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_2528.hashCode();
            org.junit.Assert.assertNull(cache.getObject(4));
            org.junit.Assert.fail("shouldFlushAllItemsOnDemand_cf8428_cf9149 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_literalMutation8417_cf8700() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // AssertGenerator replace invocation
        int o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8700__23 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8700__23, 1396831549);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8445_cf9916() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2224 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2224);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2224);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldFlushAllItemsOnDemand_cf8445__19 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_2224);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8445__19);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_2819 = new java.lang.Object();
        // AssertGenerator replace invocation
        java.lang.Object o_shouldFlushAllItemsOnDemand_cf8445_cf9916__27 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_2819);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8445_cf9916__27);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_literalMutation8417_cf8693_cf13961_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("dDefault");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            org.junit.Assert.assertNotNull(cache.getObject(0));
            org.junit.Assert.assertNotNull(cache.getObject(4));
            cache.clear();
            org.junit.Assert.assertNull(cache.getObject(0));
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_2343 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8693__25 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2343);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8693__25);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_4378 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_4376 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_4376.getObject(vc_4378);
            org.junit.Assert.assertNull(cache.getObject(4));
            org.junit.Assert.fail("shouldFlushAllItemsOnDemand_literalMutation8417_cf8693_cf13961 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_literalMutation8413_cf8478_failAssert19_cf13427() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            org.junit.Assert.assertNotNull(cache.getObject(0));
            org.junit.Assert.assertNotNull(cache.getObject(4));
            cache.clear();
            org.junit.Assert.assertNull(cache.getObject(0));
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2238 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2238);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_2236 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2236);
            // StatementAdderMethod cloned existing statement
            vc_2236.equals(vc_2238);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_4200 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_4198 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_4198.removeObject(vc_4200);
            org.junit.Assert.assertNull(cache.getObject(4));
            org.junit.Assert.fail("shouldFlushAllItemsOnDemand_literalMutation8413_cf8478 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_literalMutation8415_cf8612_cf14737() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defaLlt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defaLlt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defaLlt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defaLlt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldFlushAllItemsOnDemand_literalMutation8415_cf8612__23 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_literalMutation8415_cf8612__23);
        // AssertGenerator replace invocation
        int o_shouldFlushAllItemsOnDemand_literalMutation8415_cf8612_cf14737__33 = // StatementAdderMethod cloned existing statement
cache.getSize();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldFlushAllItemsOnDemand_literalMutation8415_cf8612_cf14737__33, 0);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_literalMutation8417_cf8693_cf13959() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "dDefault");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_2343 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8693__25 = // StatementAdderMethod cloned existing statement
cache.equals(vc_2343);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8693__25);
        // AssertGenerator replace invocation
        int o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8693_cf13959__35 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldFlushAllItemsOnDemand_literalMutation8417_cf8693_cf13959__35, 1396831549);
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldFlushAllItemsOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldFlushAllItemsOnDemand_cf8445_cf9922_failAssert5_cf11072() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            for (int i = 0; i < 5; i++) {
                cache.putObject(i, i);
            }
            org.junit.Assert.assertNotNull(cache.getObject(0));
            org.junit.Assert.assertNotNull(cache.getObject(4));
            cache.clear();
            org.junit.Assert.assertNull(cache.getObject(0));
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2224 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2224);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2224);
            // AssertGenerator replace invocation
            java.lang.Object o_shouldFlushAllItemsOnDemand_cf8445__19 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_2224);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldFlushAllItemsOnDemand_cf8445__19);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_2820 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2820);
            // StatementAdderMethod cloned existing statement
            vc_2820.removeObject(vc_2224);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3286 = (java.lang.Object)null;
            // StatementAdderMethod cloned existing statement
            cache.getObject(vc_3286);
            org.junit.Assert.assertNull(cache.getObject(4));
            org.junit.Assert.fail("shouldFlushAllItemsOnDemand_cf8445_cf9922 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16067() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5150 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5150);
        // AssertGenerator replace invocation
        boolean o_shouldRemoveItemOnDemand_cf16067__11 = // StatementAdderMethod cloned existing statement
cache.equals(vc_5150);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldRemoveItemOnDemand_cf16067__11);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16075() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // AssertGenerator replace invocation
        int o_shouldRemoveItemOnDemand_cf16075__9 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand_cf16075__9, 1544803905);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test
    public void shouldRemoveItemOnDemand_literalMutation16060() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: ");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16085() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5162 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5162);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand_cf16085__11 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_5162);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16085__11);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16093() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_cf16093__9 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16093__9);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_literalMutation16062_cf16259() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // AssertGenerator replace invocation
        int o_shouldRemoveItemOnDemand_literalMutation16062_cf16259__15 = // StatementAdderMethod cloned existing statement
cache.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand_literalMutation16062_cf16259__15, 644272018);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16079_cf17031() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5158 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5158);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5158);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand_cf16079__11 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_5158);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16079__11);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5604 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5604);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand_cf16079_cf17031__19 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_5604);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16079_cf17031__19);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16093_cf17562() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // AssertGenerator replace invocation
        java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_cf16093__9 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16093__9);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_5827 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_shouldRemoveItemOnDemand_cf16093_cf17562__15 = // StatementAdderMethod cloned existing statement
cache.equals(vc_5827);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldRemoveItemOnDemand_cf16093_cf17562__15);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_literalMutation16060_cf16169_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5210 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_5208 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_5208.getObject(vc_5210);
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_literalMutation16060_cf16169 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_literalMutation16061_cf16231_cf19467_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("{7gkyOz");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "{7gkyOz");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "{7gkyOz");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // AssertGenerator replace invocation
            java.util.concurrent.locks.ReadWriteLock o_shouldRemoveItemOnDemand_literalMutation16061_cf16231__15 = // StatementAdderMethod cloned existing statement
cache.getReadWriteLock();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_literalMutation16061_cf16231__15);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_6672 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_6672.getId();
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_literalMutation16061_cf16231_cf19467 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16075_cf16839_failAssert14_cf19990() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // AssertGenerator replace invocation
            int o_shouldRemoveItemOnDemand_cf16075__9 = // StatementAdderMethod cloned existing statement
cache.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand_cf16075__9, 1544803905);
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_5532 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5532);
            // StatementAdderMethod cloned existing statement
            vc_5532.clear();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_6866 = (java.lang.Object)null;
            // StatementAdderMethod cloned existing statement
            vc_5532.equals(vc_6866);
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_cf16075_cf16839 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_cf16079_cf17031_cf21288() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5158 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5158);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5158);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5158);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand_cf16079__11 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_5158);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16079__11);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5604 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5604);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5604);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand_cf16079_cf17031__19 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_5604);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16079_cf17031__19);
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand_cf16079_cf17031_cf21288__27 = // StatementAdderMethod cloned existing statement
cache.getObject(vc_5604);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_cf16079_cf17031_cf21288__27);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_literalMutation16062_cf16252_add22768() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "defauclt");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        // MethodCallAdder
        cache.putObject(0, 0);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        cache.removeObject(0);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_5255 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_shouldRemoveItemOnDemand_literalMutation16062_cf16252__17 = // StatementAdderMethod cloned existing statement
cache.equals(vc_5255);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldRemoveItemOnDemand_literalMutation16062_cf16252__17);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.PerpetualCacheTest#shouldRemoveItemOnDemand */
    @org.junit.Test(timeout = 1000)
    public void shouldRemoveItemOnDemand_literalMutation16060_cf16178_cf18297_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
            cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
            cache.putObject(0, 0);
            org.junit.Assert.assertNotNull(cache.getObject(0));
            cache.removeObject(0);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_5215 = new java.lang.Object();
            // AssertGenerator replace invocation
            java.lang.Object o_shouldRemoveItemOnDemand_literalMutation16060_cf16178__17 = // StatementAdderMethod cloned existing statement
cache.removeObject(vc_5215);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldRemoveItemOnDemand_literalMutation16060_cf16178__17);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_6150 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.cache.impl.PerpetualCache vc_6148 = (org.apache.ibatis.cache.impl.PerpetualCache)null;
            // StatementAdderMethod cloned existing statement
            vc_6148.removeObject(vc_6150);
            org.junit.Assert.assertNull(cache.getObject(0));
            org.junit.Assert.fail("shouldRemoveItemOnDemand_literalMutation16060_cf16178_cf18297 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

