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

import static Serializers.StorageKeySerializer;
import static Serializers.StorageValueSerializer;


/**
 * Test for {@link SourceCodec}
 */
public class SourceCodecTest {
    @Test
    public void testDataWordKeySerializer() {
        Source<byte[], byte[]> parentSrc = new org.ethereum.datasource.inmem.HashMapDB();
        Serializer<DataWord, byte[]> keySerializer = StorageKeySerializer;
        Serializer<byte[], byte[]> valueSerializer = new Serializers.Identity<>();
        SourceCodec<DataWord, byte[], byte[], byte[]> src = new SourceCodec(parentSrc, keySerializer, valueSerializer);
        for (int i = 0; i < 10000; ++i) {
            src.put(intToDataWordKey(i), intToValue(i));
        }
        // Everything is in src
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToDataWordKey(0))));
        Assert.assertEquals(str(intToValue(9999)), str(src.get(intToDataWordKey(9999))));
        // Modifying key
        src.put(intToDataWordKey(0), intToValue(12345));
        Assert.assertEquals(str(intToValue(12345)), str(src.get(intToDataWordKey(0))));
        // Testing there is no cache
        Assert.assertEquals(str(intToValue(9990)), str(src.get(intToDataWordKey(9990))));
        parentSrc.delete(keySerializer.serialize(intToDataWordKey(9990)));
        Assert.assertNull(src.get(intToDataWordKey(9990)));
    }

    @Test
    public void testDataWordKeyValueSerializer() {
        Source<byte[], byte[]> parentSrc = new org.ethereum.datasource.inmem.HashMapDB();
        Serializer<DataWord, byte[]> keySerializer = StorageKeySerializer;
        Serializer<DataWord, byte[]> valueSerializer = StorageValueSerializer;
        SourceCodec<DataWord, DataWord, byte[], byte[]> src = new SourceCodec(parentSrc, keySerializer, valueSerializer);
        for (int i = 0; i < 10000; ++i) {
            src.put(intToDataWordKey(i), intToDataWord(i));
        }
        // Everything is in src
        Assert.assertEquals(str(intToDataWord(0)), str(src.get(intToDataWordKey(0))));
        Assert.assertEquals(str(intToDataWord(9999)), str(src.get(intToDataWordKey(9999))));
        // Modifying key
        src.put(intToDataWordKey(0), intToDataWord(12345));
        Assert.assertEquals(str(intToDataWord(12345)), str(src.get(intToDataWordKey(0))));
    }
}

