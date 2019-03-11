/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.essentials.hash;


import java.util.Random;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Ignore
public class Murmur3ASpeedTest {
    public static final int ITERATIONS = 1000;

    @Parameterized.Parameter
    public int alignment;

    @Test
    public void measureByteArrayPerformance() {
        System.out.println((("ByteArray align=" + (alignment)) + "\t----------------------------------------------------"));
        Murmur3A checksum = new Murmur3A();
        byte[] data = new byte[1024 * 1024];// 1MB

        new Random(23).nextBytes(data);
        // Warm up a bit
        checksum.update(data);
        long hash;
        long totalTime = 0;
        for (int i = 0; i < (Murmur3ASpeedTest.ITERATIONS); i++) {
            prepareChecksum(checksum);
            long start = System.nanoTime();
            checksum.update(data);
            hash = checksum.getValue();
            totalTime += (System.nanoTime()) - start;
            if (((i + 1) % ((Murmur3ASpeedTest.ITERATIONS) / 10)) == 0) {
                printStats((i + 1), data.length, totalTime, hash);
            }
        }
    }

    @Test
    public void measureShortArrayPerformance() {
        System.out.println((("ShortArray align=" + (alignment)) + "\t----------------------------------------------------"));
        Murmur3A checksum = new Murmur3A();
        short[] data = new short[512 * 1024];// 1MB

        Random random = new Random(23);
        for (int i = 0; i < (data.length); i++) {
            data[i] = ((short) (random.nextInt()));
        }
        // Warm up a bit
        checksum.updateShort(data);
        long hash;
        long totalTime = 0;
        for (int i = 0; i < (Murmur3ASpeedTest.ITERATIONS); i++) {
            prepareChecksum(checksum);
            long start = System.nanoTime();
            checksum.updateShort(data);
            hash = checksum.getValue();
            totalTime += (System.nanoTime()) - start;
            if (((i + 1) % ((Murmur3ASpeedTest.ITERATIONS) / 10)) == 0) {
                printStats((i + 1), ((data.length) * 2), totalTime, hash);
            }
        }
    }

    @Test
    public void measureIntArrayPerformance() {
        System.out.println((("IntArray align=" + (alignment)) + "\t----------------------------------------------------"));
        Murmur3A checksum = new Murmur3A();
        int[] data = new int[256 * 1024];// 1MB

        Random random = new Random(23);
        for (int i = 0; i < (data.length); i++) {
            data[i] = random.nextInt();
        }
        // Warm up a bit
        checksum.updateInt(data);
        long hash;
        long totalTime = 0;
        for (int i = 0; i < (Murmur3ASpeedTest.ITERATIONS); i++) {
            prepareChecksum(checksum);
            long start = System.nanoTime();
            checksum.updateInt(data);
            hash = checksum.getValue();
            totalTime += (System.nanoTime()) - start;
            if (((i + 1) % ((Murmur3ASpeedTest.ITERATIONS) / 10)) == 0) {
                printStats((i + 1), ((data.length) * 4), totalTime, hash);
            }
        }
    }

    @Test
    public void measureLongArrayPerformance() {
        System.out.println((("LongArray align=" + (alignment)) + "\t----------------------------------------------------"));
        Murmur3A checksum = new Murmur3A();
        long[] data = new long[128 * 1024];// 1MB

        Random random = new Random(23);
        for (int i = 0; i < (data.length); i++) {
            data[i] = random.nextLong();
        }
        // Warm up a bit
        checksum.updateLong(data);
        long hash;
        long totalTime = 0;
        for (int i = 0; i < (Murmur3ASpeedTest.ITERATIONS); i++) {
            prepareChecksum(checksum);
            long start = System.nanoTime();
            checksum.updateLong(data);
            hash = checksum.getValue();
            totalTime += (System.nanoTime()) - start;
            if (((i + 1) % ((Murmur3ASpeedTest.ITERATIONS) / 10)) == 0) {
                printStats((i + 1), ((data.length) * 4), totalTime, hash);
            }
        }
    }
}

