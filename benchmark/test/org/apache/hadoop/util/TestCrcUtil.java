/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import DataChecksum.Type.CRC32;
import DataChecksum.Type.CRC32C;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Unittests for CrcUtil.
 */
public class TestCrcUtil {
    @Rule
    public Timeout globalTimeout = new Timeout(10000);

    private Random rand = new Random(1234);

    @Test
    public void testComposeCrc32() throws IOException {
        byte[] data = new byte[64 * 1024];
        rand.nextBytes(data);
        TestCrcUtil.doTestComposeCrc(data, CRC32, 512, false);
        TestCrcUtil.doTestComposeCrc(data, CRC32, 511, false);
        TestCrcUtil.doTestComposeCrc(data, CRC32, (32 * 1024), false);
        TestCrcUtil.doTestComposeCrc(data, CRC32, ((32 * 1024) - 1), false);
    }

    @Test
    public void testComposeCrc32c() throws IOException {
        byte[] data = new byte[64 * 1024];
        rand.nextBytes(data);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, 512, false);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, 511, false);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, (32 * 1024), false);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, ((32 * 1024) - 1), false);
    }

    @Test
    public void testComposeCrc32WithMonomial() throws IOException {
        byte[] data = new byte[64 * 1024];
        rand.nextBytes(data);
        TestCrcUtil.doTestComposeCrc(data, CRC32, 512, true);
        TestCrcUtil.doTestComposeCrc(data, CRC32, 511, true);
        TestCrcUtil.doTestComposeCrc(data, CRC32, (32 * 1024), true);
        TestCrcUtil.doTestComposeCrc(data, CRC32, ((32 * 1024) - 1), true);
    }

    @Test
    public void testComposeCrc32cWithMonomial() throws IOException {
        byte[] data = new byte[64 * 1024];
        rand.nextBytes(data);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, 512, true);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, 511, true);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, (32 * 1024), true);
        TestCrcUtil.doTestComposeCrc(data, CRC32C, ((32 * 1024) - 1), true);
    }

    @Test
    public void testComposeCrc32ZeroLength() throws IOException {
        TestCrcUtil.doTestComposeCrcZerolength(CRC32);
    }

    @Test
    public void testComposeCrc32CZeroLength() throws IOException {
        TestCrcUtil.doTestComposeCrcZerolength(CRC32C);
    }

    @Test
    public void testIntSerialization() throws IOException {
        byte[] bytes = CrcUtil.intToBytes(-889274641);
        Assert.assertEquals(-889274641, CrcUtil.readInt(bytes, 0));
        bytes = new byte[8];
        CrcUtil.writeInt(bytes, 0, -889274641);
        Assert.assertEquals(-889274641, CrcUtil.readInt(bytes, 0));
        CrcUtil.writeInt(bytes, 4, -1412584499);
        Assert.assertEquals(-1412584499, CrcUtil.readInt(bytes, 4));
        // Assert big-endian format for general Java consistency.
        Assert.assertEquals(-1091589171, CrcUtil.readInt(bytes, 2));
    }

    @Test
    public void testToSingleCrcStringBadLength() throws Exception {
        LambdaTestUtils.intercept(IOException.class, "length", () -> CrcUtil.toSingleCrcString(new byte[8]));
    }

    @Test
    public void testToSingleCrcString() throws IOException {
        byte[] buf = CrcUtil.intToBytes(-889274641);
        Assert.assertEquals("0xcafebeef", CrcUtil.toSingleCrcString(buf));
    }

    @Test
    public void testToMultiCrcStringBadLength() throws Exception {
        LambdaTestUtils.intercept(IOException.class, "length", () -> CrcUtil.toMultiCrcString(new byte[6]));
    }

    @Test
    public void testToMultiCrcStringMultipleElements() throws IOException {
        byte[] buf = new byte[12];
        CrcUtil.writeInt(buf, 0, -889274641);
        CrcUtil.writeInt(buf, 4, -1414804276);
        CrcUtil.writeInt(buf, 8, -572657681);
        Assert.assertEquals("[0xcafebeef, 0xababcccc, 0xddddefef]", CrcUtil.toMultiCrcString(buf));
    }

    @Test
    public void testToMultiCrcStringSingleElement() throws IOException {
        byte[] buf = new byte[4];
        CrcUtil.writeInt(buf, 0, -889274641);
        Assert.assertEquals("[0xcafebeef]", CrcUtil.toMultiCrcString(buf));
    }

    @Test
    public void testToMultiCrcStringNoElements() throws IOException {
        Assert.assertEquals("[]", CrcUtil.toMultiCrcString(new byte[0]));
    }
}

