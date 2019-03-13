/**
 * Copyright 2009-2012 the original author or authors.
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


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class CacheKeyTest {
    @Test
    public void shouldTestCacheKeysEqual() {
        Date date = new Date();
        CacheKey key1 = new CacheKey(new Object[]{ 1, "hello", null, new Date(date.getTime()) });
        CacheKey key2 = new CacheKey(new Object[]{ 1, "hello", null, new Date(date.getTime()) });
        Assert.assertTrue(key1.equals(key2));
        Assert.assertTrue(key2.equals(key1));
        Assert.assertTrue(((key1.hashCode()) == (key2.hashCode())));
        Assert.assertTrue(key1.toString().equals(key2.toString()));
    }

    @Test
    public void shouldTestCacheKeysNotEqualDueToDateDifference() throws Exception {
        CacheKey key1 = new CacheKey(new Object[]{ 1, "hello", null, new Date() });
        Thread.sleep(1000);
        CacheKey key2 = new CacheKey(new Object[]{ 1, "hello", null, new Date() });
        Assert.assertFalse(key1.equals(key2));
        Assert.assertFalse(key2.equals(key1));
        Assert.assertFalse(((key1.hashCode()) == (key2.hashCode())));
        Assert.assertFalse(key1.toString().equals(key2.toString()));
    }

    @Test
    public void shouldTestCacheKeysNotEqualDueToOrder() throws Exception {
        CacheKey key1 = new CacheKey(new Object[]{ 1, "hello", null });
        Thread.sleep(1000);
        CacheKey key2 = new CacheKey(new Object[]{ 1, null, "hello" });
        Assert.assertFalse(key1.equals(key2));
        Assert.assertFalse(key2.equals(key1));
        Assert.assertFalse(((key1.hashCode()) == (key2.hashCode())));
        Assert.assertFalse(key1.toString().equals(key2.toString()));
    }

    @Test
    public void shouldDemonstrateEmptyAndNullKeysAreEqual() {
        CacheKey key1 = new CacheKey();
        CacheKey key2 = new CacheKey();
        Assert.assertEquals(key1, key2);
        Assert.assertEquals(key2, key1);
        key1.update(null);
        key2.update(null);
        Assert.assertEquals(key1, key2);
        Assert.assertEquals(key2, key1);
        key1.update(null);
        key2.update(null);
        Assert.assertEquals(key1, key2);
        Assert.assertEquals(key2, key1);
    }

    @Test
    public void shouldTestCacheKeysWithBinaryArrays() throws Exception {
        byte[] array1 = new byte[]{ 1 };
        byte[] array2 = new byte[]{ 1 };
        CacheKey key1 = new CacheKey(new Object[]{ array1 });
        CacheKey key2 = new CacheKey(new Object[]{ array2 });
        Assert.assertTrue(key1.equals(key2));
    }
}

