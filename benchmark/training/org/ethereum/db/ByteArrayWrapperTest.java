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
package org.ethereum.db;


import com.google.common.primitives.UnsignedBytes;
import java.util.Arrays;
import java.util.Comparator;
import org.ethereum.util.FastByteComparisons;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayWrapperTest {
    static ByteArrayWrapper wrapper1;

    static ByteArrayWrapper wrapper2;

    static ByteArrayWrapper wrapper3;

    static ByteArrayWrapper wrapper4;

    @Test
    public void testEqualsObject() {
        Assert.assertTrue(ByteArrayWrapperTest.wrapper1.equals(ByteArrayWrapperTest.wrapper2));
        Assert.assertFalse(ByteArrayWrapperTest.wrapper1.equals(ByteArrayWrapperTest.wrapper3));
        Assert.assertFalse(ByteArrayWrapperTest.wrapper1.equals(ByteArrayWrapperTest.wrapper4));
        Assert.assertFalse(ByteArrayWrapperTest.wrapper1.equals(null));
        Assert.assertFalse(ByteArrayWrapperTest.wrapper2.equals(ByteArrayWrapperTest.wrapper3));
    }

    @Test
    public void testCompareTo() {
        Assert.assertTrue(((ByteArrayWrapperTest.wrapper1.compareTo(ByteArrayWrapperTest.wrapper2)) == 0));
        Assert.assertTrue(((ByteArrayWrapperTest.wrapper1.compareTo(ByteArrayWrapperTest.wrapper3)) > 1));
        Assert.assertTrue(((ByteArrayWrapperTest.wrapper1.compareTo(ByteArrayWrapperTest.wrapper4)) > 1));
        Assert.assertTrue(((ByteArrayWrapperTest.wrapper2.compareTo(ByteArrayWrapperTest.wrapper3)) > 1));
    }

    @Test
    public void testEqualsPerformance() {
        boolean testEnabled = false;
        if (testEnabled) {
            final int ITERATIONS = 10000000;
            long start1 = System.currentTimeMillis();
            for (int i = 0; i < ITERATIONS; i++) {
                Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
                comparator.compare(ByteArrayWrapperTest.wrapper1.getData(), ByteArrayWrapperTest.wrapper2.getData());
            }
            System.out.println((((System.currentTimeMillis()) - start1) + "ms"));
            long start2 = System.currentTimeMillis();
            for (int i = 0; i < ITERATIONS; i++) {
                Arrays.equals(ByteArrayWrapperTest.wrapper1.getData(), ByteArrayWrapperTest.wrapper2.getData());
            }
            System.out.println((((System.currentTimeMillis()) - start2) + "ms"));
            long start3 = System.currentTimeMillis();
            for (int i = 0; i < ITERATIONS; i++) {
                FastByteComparisons.compareTo(ByteArrayWrapperTest.wrapper1.getData(), 0, ByteArrayWrapperTest.wrapper1.getData().length, ByteArrayWrapperTest.wrapper2.getData(), 0, ByteArrayWrapperTest.wrapper1.getData().length);
            }
            System.out.println((((System.currentTimeMillis()) - start3) + "ms"));
        }
    }
}

