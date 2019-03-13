/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.osgi;


import java.io.Serializable;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OffHeapOsgiTest {
    @Test
    public void testOffHeapInOsgi() {
        OffHeapOsgiTest.TestMethods.testOffHeapInOsgi();
    }

    @Test
    public void testOffHeapClientClass() {
        OffHeapOsgiTest.TestMethods.testOffHeapClientClass();
    }

    private static class TestMethods {
        public static void testOffHeapInOsgi() {
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(10, MemoryUnit.MB)).build()).build(true);
            Cache<Long, String> cache = cacheManager.getCache("myCache", Long.class, String.class);
            cache.put(42L, "I am out of heap!!");
            cache.get(42L);
        }

        public static void testOffHeapClientClass() {
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withClassLoader(OffHeapOsgiTest.TestMethods.class.getClassLoader()).withCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, OffHeapOsgiTest.TestMethods.Order.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(2, MemoryUnit.MB)).build()).build(true);
            Cache<Long, OffHeapOsgiTest.TestMethods.Order> cache = cacheManager.getCache("myCache", Long.class, OffHeapOsgiTest.TestMethods.Order.class);
            OffHeapOsgiTest.TestMethods.Order order = new OffHeapOsgiTest.TestMethods.Order(42L);
            cache.put(42L, order);
            Assert.assertTrue(((cache.get(42L)) instanceof OffHeapOsgiTest.TestMethods.Order));
            cache.replace(42L, order, new OffHeapOsgiTest.TestMethods.Order((-1L)));
            Assert.assertEquals((-1L), cache.get(42L).id);
        }

        private static class Order implements Serializable {
            private static final long serialVersionUID = 1L;

            final long id;

            Order(long id) {
                this.id = id;
            }

            @Override
            public int hashCode() {
                return ((int) (id));
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof OffHeapOsgiTest.TestMethods.Order) {
                    return (((OffHeapOsgiTest.TestMethods.Order) (obj)).id) == (this.id);
                }
                return false;
            }
        }
    }
}

