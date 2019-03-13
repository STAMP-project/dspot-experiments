/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.filter;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.druid.io.ByteBufferInputStream;
import org.junit.Assert;
import org.junit.Test;


public class BloomKFilterTest {
    private static final int COUNT = 100;

    Random rand = ThreadLocalRandom.current();

    @Test
    public void testBloomKFilterBytes() throws IOException {
        BloomKFilter bf = new BloomKFilter(10000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        byte[] val = new byte[]{ 1, 2, 3 };
        byte[] val1 = new byte[]{ 1, 2, 3, 4 };
        byte[] val2 = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] val3 = new byte[]{ 1, 2, 3, 4, 5, 6 };
        bf.add(val);
        BloomKFilter.add(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.test(val));
        Assert.assertEquals(false, rehydrated.test(val1));
        Assert.assertEquals(false, rehydrated.test(val2));
        Assert.assertEquals(false, rehydrated.test(val3));
        BloomKFilter.add(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.test(val));
        Assert.assertEquals(true, rehydrated.test(val1));
        Assert.assertEquals(false, rehydrated.test(val2));
        Assert.assertEquals(false, rehydrated.test(val3));
        BloomKFilter.add(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.test(val));
        Assert.assertEquals(true, rehydrated.test(val1));
        Assert.assertEquals(true, rehydrated.test(val2));
        Assert.assertEquals(false, rehydrated.test(val3));
        BloomKFilter.add(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.test(val));
        Assert.assertEquals(true, rehydrated.test(val1));
        Assert.assertEquals(true, rehydrated.test(val2));
        Assert.assertEquals(true, rehydrated.test(val3));
        byte[] randVal = new byte[BloomKFilterTest.COUNT];
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            rand.nextBytes(randVal);
            BloomKFilter.add(buffer, randVal);
        }
        // last value should be present
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        Assert.assertEquals(true, rehydrated.test(randVal));
        // most likely this value should not exist
        randVal[0] = 0;
        randVal[1] = 0;
        randVal[2] = 0;
        randVal[3] = 0;
        randVal[4] = 0;
        Assert.assertEquals(false, rehydrated.test(randVal));
        Assert.assertEquals(7808, rehydrated.sizeInBytes());
    }

    @Test
    public void testBloomKFilterByte() throws IOException {
        BloomKFilter bf = new BloomKFilter(10000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        byte val = Byte.MIN_VALUE;
        byte val1 = 1;
        byte val2 = 2;
        byte val3 = Byte.MAX_VALUE;
        BloomKFilter.addLong(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(false, rehydrated.testLong(val1));
        Assert.assertEquals(false, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(false, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(true, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(true, rehydrated.testLong(val2));
        Assert.assertEquals(true, rehydrated.testLong(val3));
        byte randVal = 0;
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            randVal = ((byte) (rand.nextInt(Byte.MAX_VALUE)));
            BloomKFilter.addLong(buffer, randVal);
        }
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        // last value should be present
        Assert.assertEquals(true, rehydrated.testLong(randVal));
        // most likely this value should not exist
        Assert.assertEquals(false, rehydrated.testLong(((byte) (-120))));
        Assert.assertEquals(7808, rehydrated.sizeInBytes());
    }

    @Test
    public void testBloomKFilterInt() throws IOException {
        BloomKFilter bf = new BloomKFilter(10000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        int val = Integer.MIN_VALUE;
        int val1 = 1;
        int val2 = 2;
        int val3 = Integer.MAX_VALUE;
        BloomKFilter.addLong(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(false, rehydrated.testLong(val1));
        Assert.assertEquals(false, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(false, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(true, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(true, rehydrated.testLong(val2));
        Assert.assertEquals(true, rehydrated.testLong(val3));
        int randVal = 0;
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            randVal = rand.nextInt();
            BloomKFilter.addLong(buffer, randVal);
        }
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        // last value should be present
        Assert.assertEquals(true, rehydrated.testLong(randVal));
        // most likely this value should not exist
        Assert.assertEquals(false, rehydrated.testLong((-120)));
        Assert.assertEquals(7808, rehydrated.sizeInBytes());
    }

    @Test
    public void testBloomKFilterLong() throws IOException {
        BloomKFilter bf = new BloomKFilter(10000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        long val = Long.MIN_VALUE;
        long val1 = 1;
        long val2 = 2;
        long val3 = Long.MAX_VALUE;
        BloomKFilter.addLong(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(false, rehydrated.testLong(val1));
        Assert.assertEquals(false, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(false, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(true, rehydrated.testLong(val2));
        Assert.assertEquals(false, rehydrated.testLong(val3));
        BloomKFilter.addLong(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testLong(val));
        Assert.assertEquals(true, rehydrated.testLong(val1));
        Assert.assertEquals(true, rehydrated.testLong(val2));
        Assert.assertEquals(true, rehydrated.testLong(val3));
        int randVal = 0;
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            randVal = rand.nextInt();
            BloomKFilter.addLong(buffer, randVal);
        }
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        // last value should be present
        Assert.assertEquals(true, rehydrated.testLong(randVal));
        // most likely this value should not exist
        Assert.assertEquals(false, rehydrated.testLong((-120)));
        Assert.assertEquals(7808, rehydrated.sizeInBytes());
    }

    @Test
    public void testBloomKFilterFloat() throws IOException {
        BloomKFilter bf = new BloomKFilter(10000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        float val = Float.NEGATIVE_INFINITY;
        float val1 = 1.1F;
        float val2 = 2.2F;
        float val3 = Float.POSITIVE_INFINITY;
        BloomKFilter.addFloat(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testFloat(val));
        Assert.assertEquals(false, rehydrated.testFloat(val1));
        Assert.assertEquals(false, rehydrated.testFloat(val2));
        Assert.assertEquals(false, rehydrated.testFloat(val3));
        BloomKFilter.addFloat(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testFloat(val));
        Assert.assertEquals(true, rehydrated.testFloat(val1));
        Assert.assertEquals(false, rehydrated.testFloat(val2));
        Assert.assertEquals(false, rehydrated.testFloat(val3));
        BloomKFilter.addFloat(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testFloat(val));
        Assert.assertEquals(true, rehydrated.testFloat(val1));
        Assert.assertEquals(true, rehydrated.testFloat(val2));
        Assert.assertEquals(false, rehydrated.testFloat(val3));
        BloomKFilter.addFloat(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testFloat(val));
        Assert.assertEquals(true, rehydrated.testFloat(val1));
        Assert.assertEquals(true, rehydrated.testFloat(val2));
        Assert.assertEquals(true, rehydrated.testFloat(val3));
        float randVal = 0;
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            randVal = rand.nextFloat();
            BloomKFilter.addFloat(buffer, randVal);
        }
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        // last value should be present
        Assert.assertEquals(true, rehydrated.testFloat(randVal));
        // most likely this value should not exist
        Assert.assertEquals(false, rehydrated.testFloat((-120.2F)));
        Assert.assertEquals(7808, rehydrated.sizeInBytes());
    }

    @Test
    public void testBloomKFilterDouble() throws IOException {
        BloomKFilter bf = new BloomKFilter(10000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        double val = Double.NEGATIVE_INFINITY;
        double val1 = 1.1;
        double val2 = 2.2;
        double val3 = Double.POSITIVE_INFINITY;
        BloomKFilter.addDouble(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testDouble(val));
        Assert.assertEquals(false, rehydrated.testDouble(val1));
        Assert.assertEquals(false, rehydrated.testDouble(val2));
        Assert.assertEquals(false, rehydrated.testDouble(val3));
        BloomKFilter.addDouble(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testDouble(val));
        Assert.assertEquals(true, rehydrated.testDouble(val1));
        Assert.assertEquals(false, rehydrated.testDouble(val2));
        Assert.assertEquals(false, rehydrated.testDouble(val3));
        BloomKFilter.addDouble(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testDouble(val));
        Assert.assertEquals(true, rehydrated.testDouble(val1));
        Assert.assertEquals(true, rehydrated.testDouble(val2));
        Assert.assertEquals(false, rehydrated.testDouble(val3));
        BloomKFilter.addDouble(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testDouble(val));
        Assert.assertEquals(true, rehydrated.testDouble(val1));
        Assert.assertEquals(true, rehydrated.testDouble(val2));
        Assert.assertEquals(true, rehydrated.testDouble(val3));
        double randVal = 0;
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            randVal = rand.nextDouble();
            BloomKFilter.addDouble(buffer, randVal);
        }
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        // last value should be present
        Assert.assertEquals(true, rehydrated.testDouble(randVal));
        // most likely this value should not exist
        Assert.assertEquals(false, rehydrated.testDouble((-120.2)));
        Assert.assertEquals(7808, rehydrated.sizeInBytes());
    }

    @Test
    public void testBloomKFilterString() throws IOException {
        BloomKFilter bf = new BloomKFilter(100000);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf);
        byte[] bfBytes = bytesOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bfBytes);
        String val = "bloo";
        String val1 = "bloom fil";
        String val2 = "bloom filter";
        String val3 = "cuckoo filter";
        BloomKFilter.addString(buffer, val);
        BloomKFilter rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testString(val));
        Assert.assertEquals(false, rehydrated.testString(val1));
        Assert.assertEquals(false, rehydrated.testString(val2));
        Assert.assertEquals(false, rehydrated.testString(val3));
        BloomKFilter.addString(buffer, val1);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testString(val));
        Assert.assertEquals(true, rehydrated.testString(val1));
        Assert.assertEquals(false, rehydrated.testString(val2));
        Assert.assertEquals(false, rehydrated.testString(val3));
        BloomKFilter.addString(buffer, val2);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testString(val));
        Assert.assertEquals(true, rehydrated.testString(val1));
        Assert.assertEquals(true, rehydrated.testString(val2));
        Assert.assertEquals(false, rehydrated.testString(val3));
        BloomKFilter.addString(buffer, val3);
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        buffer.position(0);
        Assert.assertEquals(true, rehydrated.testString(val));
        Assert.assertEquals(true, rehydrated.testString(val1));
        Assert.assertEquals(true, rehydrated.testString(val2));
        Assert.assertEquals(true, rehydrated.testString(val3));
        long randVal = 0;
        for (int i = 0; i < (BloomKFilterTest.COUNT); i++) {
            randVal = rand.nextLong();
            BloomKFilter.addString(buffer, Long.toString(randVal));
        }
        rehydrated = BloomKFilter.deserialize(new ByteBufferInputStream(buffer));
        // last value should be present
        Assert.assertEquals(true, rehydrated.testString(Long.toString(randVal)));
        // most likely this value should not exist
        Assert.assertEquals(false, rehydrated.testString(Long.toString((-120))));
        Assert.assertEquals(77952, rehydrated.sizeInBytes());
    }

    @Test
    public void testMergeBloomKFilterByteBuffers() throws Exception {
        BloomKFilter bf1 = new BloomKFilter(10000);
        BloomKFilter bf2 = new BloomKFilter(10000);
        String[] inputs1 = new String[]{ "bloo", "bloom fil", "bloom filter", "cuckoo filter" };
        String[] inputs2 = new String[]{ "2_bloo", "2_bloom fil", "2_bloom filter", "2_cuckoo filter" };
        for (String val : inputs1) {
            bf1.addString(val);
        }
        for (String val : inputs2) {
            bf2.addString(val);
        }
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bf1);
        byte[] bf1Bytes = bytesOut.toByteArray();
        bytesOut.reset();
        BloomKFilter.serialize(bytesOut, bf2);
        byte[] bf2Bytes = bytesOut.toByteArray();
        ByteBuffer buf1 = ByteBuffer.wrap(bf1Bytes);
        ByteBuffer buf2 = ByteBuffer.wrap(bf2Bytes);
        // Merge bytes
        BloomKFilter.mergeBloomFilterByteBuffers(buf1, 0, buf2, 0);
        // Deserialize and test
        byte[] merged = new byte[bf1Bytes.length];
        buf1.get(merged, 0, bf1Bytes.length);
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(merged, 0, bf1Bytes.length);
        BloomKFilter bfMerged = BloomKFilter.deserialize(bytesIn);
        // All values should pass test
        for (String val : inputs1) {
            assert bfMerged.testString(val);
        }
        for (String val : inputs2) {
            assert bfMerged.testString(val);
        }
    }

    @Test
    public void testCountBitBloomKFilterByteBuffersEmpty() throws Exception {
        BloomKFilter bfWithValues = new BloomKFilter(10000);
        BloomKFilter bfEmpty = new BloomKFilter(10000);
        BloomKFilter bfNull = new BloomKFilter(10000);
        for (int i = 0; i < 1000; i++) {
            bfWithValues.addInt(rand.nextInt());
        }
        bfNull.addBytes(null, 0, 0);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BloomKFilter.serialize(bytesOut, bfWithValues);
        ByteBuffer bufWithValues = ByteBuffer.wrap(bytesOut.toByteArray());
        bytesOut.reset();
        BloomKFilter.serialize(bytesOut, bfEmpty);
        ByteBuffer bufEmpty = ByteBuffer.wrap(bytesOut.toByteArray());
        bytesOut.reset();
        BloomKFilter.serialize(bytesOut, bfNull);
        ByteBuffer bufWithNull = ByteBuffer.wrap(bytesOut.toByteArray());
        Assert.assertTrue(((BloomKFilter.getNumSetBits(bufWithValues, 0)) > 0));
        Assert.assertFalse(((BloomKFilter.getNumSetBits(bufEmpty, 0)) > 0));
        Assert.assertTrue(((BloomKFilter.getNumSetBits(bufWithNull, 0)) > 0));
        Assert.assertTrue(((BloomKFilter.getNumSetBits(bufWithValues, 0)) > (BloomKFilter.getNumSetBits(bufWithNull, 0))));
    }
}

