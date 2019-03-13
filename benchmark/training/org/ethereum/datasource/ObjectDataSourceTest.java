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


import org.ethereum.vm.DataWord;
import org.junit.Assert;
import org.junit.Test;

import static Serializers.StorageValueSerializer;


/**
 * Test for {@link ObjectDataSource}
 */
public class ObjectDataSourceTest {
    @Test
    public void testDummySerializer() {
        Source<byte[], byte[]> parentSrc = new org.ethereum.datasource.inmem.HashMapDB();
        Serializer<byte[], byte[]> serializer = new Serializers.Identity<>();
        ObjectDataSource<byte[]> src = new ObjectDataSource(parentSrc, serializer, 256);
        for (int i = 0; i < 10000; ++i) {
            src.put(intToKey(i), intToValue(i));
        }
        // Everything is in src and parentSrc w/o flush
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        Assert.assertEquals(str(intToValue(9999)), str(src.get(intToKey(9999))));
        Assert.assertEquals(str(intToValue(0)), str(parentSrc.get(intToKey(0))));
        Assert.assertEquals(str(intToValue(9999)), str(parentSrc.get(intToKey(9999))));
        // Testing read cache is available
        parentSrc.delete(intToKey(9999));
        Assert.assertEquals(str(intToValue(9999)), str(src.get(intToKey(9999))));
        src.delete(intToKey(9999));
        // Testing src delete invalidates read cache
        src.delete(intToKey(9998));
        Assert.assertNull(src.get(intToKey(9998)));
        // Modifying key
        src.put(intToKey(0), intToValue(12345));
        Assert.assertEquals(str(intToValue(12345)), str(src.get(intToKey(0))));
        Assert.assertEquals(str(intToValue(12345)), str(parentSrc.get(intToKey(0))));
    }

    @Test
    public void testDataWordValueSerializer() {
        Source<byte[], byte[]> parentSrc = new org.ethereum.datasource.inmem.HashMapDB();
        Serializer<DataWord, byte[]> serializer = StorageValueSerializer;
        ObjectDataSource<DataWord> src = new ObjectDataSource(parentSrc, serializer, 256);
        for (int i = 0; i < 10000; ++i) {
            src.put(intToKey(i), intToDataWord(i));
        }
        // Everything is in src
        Assert.assertEquals(str(intToDataWord(0)), str(src.get(intToKey(0))));
        Assert.assertEquals(str(intToDataWord(9999)), str(src.get(intToKey(9999))));
        // Modifying key
        src.put(intToKey(0), intToDataWord(12345));
        Assert.assertEquals(str(intToDataWord(12345)), str(src.get(intToKey(0))));
    }
}

