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
 * Testing {@link ReadCache}
 */
public class ReadCacheTest {
    @Test
    public void test1() {
        Source<byte[], byte[]> src = new org.ethereum.datasource.inmem.HashMapDB();
        ReadCache<byte[], byte[]> readCache = new ReadCache.BytesKey<>(src);
        for (int i = 0; i < 10000; ++i) {
            src.put(intToKey(i), intToValue(i));
        }
        // Nothing is cached
        Assert.assertNull(readCache.getCached(intToKey(0)));
        Assert.assertNull(readCache.getCached(intToKey(9999)));
        for (int i = 0; i < 10000; ++i) {
            readCache.get(intToKey(i));
        }
        // Everything is cached
        Assert.assertEquals(str(intToValue(0)), str(readCache.getCached(intToKey(0)).value()));
        Assert.assertEquals(str(intToValue(9999)), str(readCache.getCached(intToKey(9999)).value()));
        // Source changes doesn't affect cache
        src.delete(intToKey(13));
        Assert.assertEquals(str(intToValue(13)), str(readCache.getCached(intToKey(13)).value()));
        // Flush is not implemented
        Assert.assertFalse(readCache.flush());
    }

    @Test
    public void testMaxCapacity() {
        Source<byte[], byte[]> src = new org.ethereum.datasource.inmem.HashMapDB();
        ReadCache<byte[], byte[]> readCache = new ReadCache.BytesKey<>(src).withMaxCapacity(100);
        for (int i = 0; i < 10000; ++i) {
            src.put(intToKey(i), intToValue(i));
            readCache.get(intToKey(i));
        }
        // Only 100 latest are cached
        Assert.assertNull(readCache.getCached(intToKey(0)));
        Assert.assertEquals(str(intToValue(0)), str(readCache.get(intToKey(0))));
        Assert.assertEquals(str(intToValue(0)), str(readCache.getCached(intToKey(0)).value()));
        Assert.assertEquals(str(intToValue(9999)), str(readCache.getCached(intToKey(9999)).value()));
        // 99_01 - 99_99 and 0 (totally 100)
        Assert.assertEquals(str(intToValue(9901)), str(readCache.getCached(intToKey(9901)).value()));
        Assert.assertNull(readCache.getCached(intToKey(9900)));
    }
}

