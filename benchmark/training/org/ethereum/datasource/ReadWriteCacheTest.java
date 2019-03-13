/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.datasource;


import org.junit.Assert;
import org.junit.Test;


/**
 * Testing {@link ReadWriteCache}
 */
public class ReadWriteCacheTest {
    @Test
    public void testSimple() {
        Source<byte[], byte[]> src = new org.ethereum.datasource.inmem.HashMapDB();
        ReadWriteCache<byte[], byte[]> cache = new ReadWriteCache.BytesKey<>(src, WriteCache.CacheType.SIMPLE);
        for (int i = 0; i < 10000; ++i) {
            cache.put(intToKey(i), intToValue(i));
        }
        // Everything is cached
        Assert.assertEquals(str(intToValue(0)), str(cache.getCached(intToKey(0)).value()));
        Assert.assertEquals(str(intToValue(9999)), str(cache.getCached(intToKey(9999)).value()));
        // Source is empty
        Assert.assertNull(src.get(intToKey(0)));
        Assert.assertNull(src.get(intToKey(9999)));
        // After flush src is filled
        cache.flush();
        Assert.assertEquals(str(intToValue(9999)), str(src.get(intToKey(9999))));
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        // Deleting key that is currently in cache
        cache.put(intToKey(0), intToValue(12345));
        Assert.assertEquals(str(intToValue(12345)), str(cache.getCached(intToKey(0)).value()));
        cache.delete(intToKey(0));
        Assert.assertTrue(((null == (cache.getCached(intToKey(0)))) || (null == (cache.getCached(intToKey(0)).value()))));
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        cache.flush();
        Assert.assertNull(src.get(intToKey(0)));
        // No size estimators
        Assert.assertEquals(0, cache.estimateCacheSize());
    }
}

