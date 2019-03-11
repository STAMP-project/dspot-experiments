/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mycat.memory.unsafe.hash;


import Platform.BYTE_ARRAY_OFFSET;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test file based on Guava's Murmur3Hash32Test.
 */
public class Murmur3_x86_32Suite {
    private static final Murmur3_x86_32 hasher = new Murmur3_x86_32(0);

    @Test
    public void testKnownIntegerInputs() {
        Assert.assertEquals(593689054, Murmur3_x86_32Suite.hasher.hashInt(0));
        Assert.assertEquals((-189366624), Murmur3_x86_32Suite.hasher.hashInt((-42)));
        Assert.assertEquals((-1134849565), Murmur3_x86_32Suite.hasher.hashInt(42));
        Assert.assertEquals((-1718298732), Murmur3_x86_32Suite.hasher.hashInt(Integer.MIN_VALUE));
        Assert.assertEquals((-1653689534), Murmur3_x86_32Suite.hasher.hashInt(Integer.MAX_VALUE));
    }

    @Test
    public void testKnownLongInputs() {
        Assert.assertEquals(1669671676, Murmur3_x86_32Suite.hasher.hashLong(0L));
        Assert.assertEquals((-846261623), Murmur3_x86_32Suite.hasher.hashLong((-42L)));
        Assert.assertEquals(1871679806, Murmur3_x86_32Suite.hasher.hashLong(42L));
        Assert.assertEquals(1366273829, Murmur3_x86_32Suite.hasher.hashLong(Long.MIN_VALUE));
        Assert.assertEquals((-2106506049), Murmur3_x86_32Suite.hasher.hashLong(Long.MAX_VALUE));
    }

    @Test
    public void randomizedStressTest() {
        int size = 65536;
        Random rand = new Random();
        // A set used to track collision rate.
        Set<Integer> hashcodes = new HashSet<Integer>();
        for (int i = 0; i < size; i++) {
            int vint = rand.nextInt();
            long lint = rand.nextLong();
            Assert.assertEquals(Murmur3_x86_32Suite.hasher.hashInt(vint), Murmur3_x86_32Suite.hasher.hashInt(vint));
            Assert.assertEquals(Murmur3_x86_32Suite.hasher.hashLong(lint), Murmur3_x86_32Suite.hasher.hashLong(lint));
            hashcodes.add(Murmur3_x86_32Suite.hasher.hashLong(lint));
        }
        // A very loose bound.
        Assert.assertTrue(((hashcodes.size()) > (size * 0.95)));
    }

    @Test
    public void randomizedStressTestBytes() {
        int size = 65536;
        Random rand = new Random();
        // A set used to track collision rate.
        Set<Integer> hashcodes = new HashSet<Integer>();
        for (int i = 0; i < size; i++) {
            int byteArrSize = (rand.nextInt(100)) * 8;
            byte[] bytes = new byte[byteArrSize];
            rand.nextBytes(bytes);
            Assert.assertEquals(Murmur3_x86_32Suite.hasher.hashUnsafeWords(bytes, BYTE_ARRAY_OFFSET, byteArrSize), Murmur3_x86_32Suite.hasher.hashUnsafeWords(bytes, BYTE_ARRAY_OFFSET, byteArrSize));
            hashcodes.add(Murmur3_x86_32Suite.hasher.hashUnsafeWords(bytes, BYTE_ARRAY_OFFSET, byteArrSize));
        }
        // A very loose bound.
        Assert.assertTrue(((hashcodes.size()) > (size * 0.95)));
    }

    @Test
    public void randomizedStressTestPaddedStrings() {
        int size = 64000;
        // A set used to track collision rate.
        Set<Integer> hashcodes = new HashSet<Integer>();
        for (int i = 0; i < size; i++) {
            int byteArrSize = 8;
            byte[] strBytes = String.valueOf(i).getBytes(StandardCharsets.UTF_8);
            byte[] paddedBytes = new byte[byteArrSize];
            System.arraycopy(strBytes, 0, paddedBytes, 0, strBytes.length);
            Assert.assertEquals(Murmur3_x86_32Suite.hasher.hashUnsafeWords(paddedBytes, BYTE_ARRAY_OFFSET, byteArrSize), Murmur3_x86_32Suite.hasher.hashUnsafeWords(paddedBytes, BYTE_ARRAY_OFFSET, byteArrSize));
            hashcodes.add(Murmur3_x86_32Suite.hasher.hashUnsafeWords(paddedBytes, BYTE_ARRAY_OFFSET, byteArrSize));
        }
        // A very loose bound.
        Assert.assertTrue(((hashcodes.size()) > (size * 0.95)));
    }
}

