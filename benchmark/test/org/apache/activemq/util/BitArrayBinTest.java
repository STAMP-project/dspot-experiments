/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.util;


import org.junit.Assert;
import org.junit.Test;

import static BitArray.LONG_SIZE;


public class BitArrayBinTest {
    @Test
    public void testSetAroundWindow() throws Exception {
        doTestSetAroundWindow(500, 2000);
        doTestSetAroundWindow(512, 2000);
        doTestSetAroundWindow(128, 512);
    }

    @Test
    public void testSetHiLo() throws Exception {
        BitArrayBin toTest = new BitArrayBin(50);
        toTest.setBit(0, true);
        toTest.setBit(100, true);
        toTest.setBit(150, true);
        Assert.assertTrue("set", toTest.getBit(0));
        toTest.setBit(0, true);
        Assert.assertTrue("set", toTest.getBit(0));
    }

    @Test
    public void testSetUnsetAroundWindow() throws Exception {
        doTestSetUnSetAroundWindow(500, 2000);
        doTestSetUnSetAroundWindow(512, 2000);
        doTestSetUnSetAroundWindow(128, 512);
    }

    @Test
    public void testSetAroundLongSizeMultiplier() throws Exception {
        int window = 512;
        int dataSize = 1000;
        for (int muliplier = 1; muliplier < 8; muliplier++) {
            for (int value = 0; value < dataSize; value++) {
                BitArrayBin toTest = new BitArrayBin(window);
                int instance = value + (muliplier * (LONG_SIZE));
                Assert.assertTrue(("not already set: id=" + instance), (!(toTest.setBit(instance, Boolean.TRUE))));
                Assert.assertTrue(("not already set: id=" + value), (!(toTest.setBit(value, Boolean.TRUE))));
                Assert.assertEquals("max set correct", instance, toTest.getLastSetIndex());
            }
        }
    }

    @Test
    public void testLargeGapInData() throws Exception {
        doTestLargeGapInData(128);
        doTestLargeGapInData(500);
    }

    @Test
    public void testLastSeq() throws Exception {
        BitArrayBin toTest = new BitArrayBin(512);
        Assert.assertEquals("last not set", (-1), toTest.getLastSetIndex());
        toTest.setBit(1, Boolean.TRUE);
        Assert.assertEquals("last correct", 1, toTest.getLastSetIndex());
        toTest.setBit(64, Boolean.TRUE);
        Assert.assertEquals("last correct", 64, toTest.getLastSetIndex());
        toTest.setBit(68, Boolean.TRUE);
        Assert.assertEquals("last correct", 68, toTest.getLastSetIndex());
    }

    @Test
    public void testLargeNumber() throws Exception {
        BitArrayBin toTest = new BitArrayBin(1024);
        toTest.setBit(1, true);
        long largeNum = ((Integer.MAX_VALUE) * 2L) + 100L;
        toTest.setBit(largeNum, true);
        Assert.assertTrue("set", toTest.getBit(largeNum));
    }

    // This test is slightly different in that it doesn't set bit 1 to
    // true as above which was causing a different result before AMQ-6431
    @Test
    public void testLargeNumber2() {
        BitArrayBin toTest = new BitArrayBin(1024);
        long largeNum = ((Integer.MAX_VALUE) * 2L) + 100L;
        toTest.setBit(largeNum, true);
        Assert.assertTrue(toTest.getBit(largeNum));
    }
}

