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
 * Test for {@link CountingBytesSource}
 */
public class CountingBytesSourceTest {
    private Source<byte[], byte[]> src;

    @Test(expected = NullPointerException.class)
    public void testKeyNull() {
        src.put(null, null);
    }

    @Test
    public void testValueNull() {
        src.put(intToKey(0), null);
        Assert.assertNull(src.get(intToKey(0)));
    }

    @Test
    public void testDelete() {
        src.put(intToKey(0), intToValue(0));
        src.delete(intToKey(0));
        Assert.assertNull(src.get(intToKey(0)));
        src.put(intToKey(0), intToValue(0));
        src.put(intToKey(0), intToValue(0));
        src.delete(intToKey(0));
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        src.delete(intToKey(0));
        Assert.assertNull(src.get(intToKey(0)));
        src.put(intToKey(1), intToValue(1));
        src.put(intToKey(1), intToValue(1));
        src.put(intToKey(1), null);
        Assert.assertEquals(str(intToValue(1)), str(src.get(intToKey(1))));
        src.put(intToKey(1), null);
        Assert.assertNull(src.get(intToKey(1)));
        src.put(intToKey(1), intToValue(1));
        src.put(intToKey(1), intToValue(2));
        src.delete(intToKey(1));
        Assert.assertEquals(str(intToValue(2)), str(src.get(intToKey(1))));
        src.delete(intToKey(1));
        Assert.assertNull(src.get(intToKey(1)));
    }

    @Test
    public void testALotRefs() {
        for (int i = 0; i < 100000; ++i) {
            src.put(intToKey(0), intToValue(0));
        }
        for (int i = 0; i < 99999; ++i) {
            src.delete(intToKey(0));
            Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        }
        src.delete(intToKey(0));
        Assert.assertNull(src.get(intToKey(0)));
    }

    @Test
    public void testFlushDoNothing() {
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j <= i; ++j) {
                src.put(intToKey(i), intToValue(i));
            }
        }
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        Assert.assertEquals(str(intToValue(99)), str(src.get(intToKey(99))));
        Assert.assertFalse(src.flush());
        Assert.assertEquals(str(intToValue(0)), str(src.get(intToKey(0))));
        Assert.assertEquals(str(intToValue(99)), str(src.get(intToKey(99))));
    }

    @Test
    public void testEmptyValue() {
        byte[] value = new byte[0];
        src.put(intToKey(0), value);
        src.put(intToKey(0), value);
        src.delete(intToKey(0));
        Assert.assertEquals(str(value), str(src.get(intToKey(0))));
        src.delete(intToKey(0));
        Assert.assertNull(src.get(intToKey(0)));
    }
}

