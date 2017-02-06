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


public class AmplBaseCacheTest {
    @org.junit.Test
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("test_cache");
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__35 = caches.add(cache);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__35);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__36 = caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__36);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__38 = caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__38);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__40 = caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__40);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__42 = caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes__42);
        org.junit.Assert.assertEquals(1, caches.size());
    }

    /* amplification of org.apache.ibatis.cache.BaseCacheTest#shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes */
    @org.junit.Test
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation10() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("tet_cache");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "tet_cache");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        caches.add(cache);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        org.junit.Assert.assertEquals(1, caches.size());
    }

    /* amplification of org.apache.ibatis.cache.BaseCacheTest#shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("test_cache");
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1__35 = // MethodCallAdder
caches.add(cache);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1__35);
        caches.add(cache);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        org.junit.Assert.assertEquals(1, caches.size());
    }

    /* amplification of org.apache.ibatis.cache.BaseCacheTest#shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation10_add334() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("tet_cache");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "tet_cache");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "tet_cache");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        caches.add(cache);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation10_add334__46 = // MethodCallAdder
caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation10_add334__46);
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        org.junit.Assert.assertEquals(1, caches.size());
    }

    /* amplification of org.apache.ibatis.cache.BaseCacheTest#shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("test_cache");
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1__35 = // MethodCallAdder
caches.add(cache);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1__35);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34__39 = // MethodCallAdder
caches.add(cache);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34__39);
        caches.add(cache);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        org.junit.Assert.assertEquals(1, caches.size());
    }

    /* amplification of org.apache.ibatis.cache.BaseCacheTest#shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34_add1388() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("test_cache");
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1__35 = // MethodCallAdder
caches.add(cache);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1__35);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34__39 = // MethodCallAdder
caches.add(cache);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34__39);
        caches.add(cache);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34_add1388__44 = // MethodCallAdder
caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_add1_add34_add1388__44);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        org.junit.Assert.assertEquals(1, caches.size());
    }

    /* amplification of org.apache.ibatis.cache.BaseCacheTest#shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes */
    @org.junit.Test(timeout = 1000)
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation6_add200_add1324() {
        org.apache.ibatis.cache.impl.PerpetualCache cache = new org.apache.ibatis.cache.impl.PerpetualCache("Error serializing object.  Cause: ");
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
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "Error serializing object.  Cause: ");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        org.junit.Assert.assertTrue(cache.equals(cache));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        org.junit.Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        org.junit.Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        java.util.Set<org.apache.ibatis.cache.Cache> caches = new java.util.HashSet<org.apache.ibatis.cache.Cache>();
        caches.add(cache);
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation6_add200__42 = // MethodCallAdder
caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation6_add200__42);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        // AssertGenerator replace invocation
        boolean o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation6_add200_add1324__56 = // MethodCallAdder
caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes_literalMutation6_add200_add1324__56);
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        org.junit.Assert.assertEquals(1, caches.size());
    }
}

