/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.datastore.cassandra;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class DataCacheTest {
    public class TestObject {
        private final String m_data;

        public TestObject(String data) {
            m_data = data;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            DataCacheTest.TestObject that = ((DataCacheTest.TestObject) (o));
            if (!(m_data.equals(that.m_data)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return m_data.hashCode();
        }
    }

    @Test
    public void test_isCached() {
        DataCache<String> cache = new DataCache<String>(3);
        Assert.assertNull(cache.cacheItem("one"));
        Assert.assertNull(cache.cacheItem("two"));
        Assert.assertNull(cache.cacheItem("three"));
        Assert.assertNotNull(cache.cacheItem("one"));// This puts 'one' as the newest

        Assert.assertNull(cache.cacheItem("four"));// This should boot out 'two'

        Assert.assertNull(cache.cacheItem("two"));// Should have booted 'three'

        Assert.assertNotNull(cache.cacheItem("one"));
        Assert.assertNull(cache.cacheItem("three"));// Should have booted 'four'

        Assert.assertNotNull(cache.cacheItem("one"));
    }

    @Test
    public void test_uniqueCache() {
        DataCacheTest.TestObject td1 = new DataCacheTest.TestObject("td1");
        DataCacheTest.TestObject td2 = new DataCacheTest.TestObject("td2");
        DataCacheTest.TestObject td3 = new DataCacheTest.TestObject("td3");
        DataCache<DataCacheTest.TestObject> cache = new DataCache<DataCacheTest.TestObject>(10);
        cache.cacheItem(td1);
        cache.cacheItem(td2);
        cache.cacheItem(td3);
        DataCacheTest.TestObject ret = cache.cacheItem(new DataCacheTest.TestObject("td1"));
        TestCase.assertTrue((td1 == ret));
        ret = cache.cacheItem(new DataCacheTest.TestObject("td2"));
        TestCase.assertTrue((td2 == ret));
        ret = cache.cacheItem(new DataCacheTest.TestObject("td3"));
        TestCase.assertTrue((td3 == ret));
        // Now if we do this again we should still get the original objects
        ret = cache.cacheItem(new DataCacheTest.TestObject("td1"));
        TestCase.assertTrue((td1 == ret));
        ret = cache.cacheItem(new DataCacheTest.TestObject("td2"));
        TestCase.assertTrue((td2 == ret));
        ret = cache.cacheItem(new DataCacheTest.TestObject("td3"));
        TestCase.assertTrue((td3 == ret));
    }
}

