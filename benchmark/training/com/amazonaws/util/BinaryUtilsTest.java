/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import java.nio.ByteBuffer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class BinaryUtilsTest {
    @Test
    public void testHex() {
        {
            String hex = BinaryUtils.toHex(new byte[]{ 0 });
            System.out.println(hex);
            String hex2 = Base16Lower.encodeAsString(new byte[]{ 0 });
            Assert.assertEquals(hex, hex2);
        }
        {
            String hex = BinaryUtils.toHex(new byte[]{ -1 });
            System.out.println(hex);
            String hex2 = Base16Lower.encodeAsString(new byte[]{ -1 });
            Assert.assertEquals(hex, hex2);
        }
    }

    @Test
    public void testCopyBytes_Nulls() {
        Assert.assertNull(BinaryUtils.copyAllBytesFrom(null));
        Assert.assertNull(BinaryUtils.copyBytesFrom(null));
    }

    @Test
    public void testCopyBytesFromByteBuffer() {
        byte[] ba = new byte[]{ 1, 2, 3, 4, 5 };
        // capacity: 100
        final ByteBuffer b = ByteBuffer.allocate(100);
        b.put(ba);
        // limit: 5
        b.limit(5);
        Assert.assertTrue(((b.capacity()) > (b.limit())));
        b.rewind();
        Assert.assertTrue(((b.position()) == 0));
        b.get();
        Assert.assertTrue(((b.position()) == 1));
        // backing array
        byte[] array = b.array();
        Assert.assertTrue(((array.length) == 100));
        // actual data length
        byte[] allData = BinaryUtils.copyAllBytesFrom(b);
        Assert.assertTrue(((allData.length) == 5));
        // copy, not reference
        Assert.assertFalse((ba == allData));
        // partial data length
        byte[] partialData = BinaryUtils.copyBytesFrom(b);
        Assert.assertTrue(((partialData.length) == 4));
    }

    @Test
    public void testCopyBytesFrom_DirectByteBuffer() {
        byte[] ba = new byte[]{ 1, 2, 3, 4, 5 };
        // capacity: 100
        final ByteBuffer b = ByteBuffer.allocateDirect(100);
        b.put(ba);
        // limit: 5
        b.limit(5);
        Assert.assertTrue(((b.capacity()) > (b.limit())));
        b.rewind();
        Assert.assertTrue(((b.position()) == 0));
        b.get();
        Assert.assertTrue(((b.position()) == 1));
        // backing array
        Assert.assertFalse(b.hasArray());
        Assert.assertTrue(((b.capacity()) == 100));
        // actual data length
        byte[] allData = BinaryUtils.copyAllBytesFrom(b);
        Assert.assertTrue(((allData.length) == 5));
        // copy, not reference
        Assert.assertFalse((ba == allData));
        // partial data length
        byte[] partialData = BinaryUtils.copyBytesFrom(b);
        Assert.assertTrue(((partialData.length) == 4));
    }

    @Test
    public void testCopyBytesFromByteBuffer_Idempotent() {
        byte[] ba = new byte[]{ 1, 2, 3, 4, 5 };
        final ByteBuffer b = ByteBuffer.wrap(ba);
        b.limit(4);
        Assert.assertTrue(((b.limit()) == 4));
        b.rewind();
        Assert.assertTrue(((b.position()) == 0));
        b.get();
        Assert.assertTrue(((b.position()) == 1));
        // copy all bytes should be idempotent
        byte[] allData1 = BinaryUtils.copyAllBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        byte[] allData2 = BinaryUtils.copyAllBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        Assert.assertFalse((allData1 == allData2));
        Assert.assertTrue(((allData1.length) == 4));
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4 }, allData1));
        // copy partial bytes should be idempotent
        byte[] partial1 = BinaryUtils.copyBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        byte[] partial2 = BinaryUtils.copyBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        Assert.assertFalse((partial1 == partial2));
        Assert.assertTrue(((partial1.length) == 3));
        Assert.assertTrue(Arrays.equals(new byte[]{ 2, 3, 4 }, partial1));
    }

    @Test
    public void testCopyBytesFrom_DirectByteBuffer_Idempotent() {
        byte[] ba = new byte[]{ 1, 2, 3, 4, 5 };
        final ByteBuffer b = ByteBuffer.allocateDirect(ba.length);
        b.put(ba).rewind();
        b.limit(4);
        Assert.assertTrue(((b.limit()) == 4));
        b.rewind();
        Assert.assertTrue(((b.position()) == 0));
        b.get();
        Assert.assertTrue(((b.position()) == 1));
        // copy all bytes should be idempotent
        byte[] allData1 = BinaryUtils.copyAllBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        byte[] allData2 = BinaryUtils.copyAllBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        Assert.assertFalse((allData1 == allData2));
        Assert.assertTrue(((allData1.length) == 4));
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4 }, allData1));
        // copy partial bytes should be idempotent
        byte[] partial1 = BinaryUtils.copyBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        byte[] partial2 = BinaryUtils.copyBytesFrom(b);
        Assert.assertTrue(((b.position()) == 1));
        Assert.assertFalse((partial1 == partial2));
        Assert.assertTrue(((partial1.length) == 3));
        Assert.assertTrue(Arrays.equals(new byte[]{ 2, 3, 4 }, partial1));
    }
}

