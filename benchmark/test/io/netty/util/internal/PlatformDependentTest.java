/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class PlatformDependentTest {
    private static final Random r = new Random();

    @Test
    public void testEqualsConsistentTime() {
        PlatformDependentTest.testEquals(new PlatformDependentTest.EqualityChecker() {
            @Override
            public boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
                return (PlatformDependent.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length)) != 0;
            }
        });
    }

    @Test
    public void testEquals() {
        PlatformDependentTest.testEquals(new PlatformDependentTest.EqualityChecker() {
            @Override
            public boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
                return PlatformDependent.equals(bytes1, startPos1, bytes2, startPos2, length);
            }
        });
    }

    @Test
    public void testIsZero() {
        byte[] bytes = new byte[100];
        Assert.assertTrue(PlatformDependent.isZero(bytes, 0, 0));
        Assert.assertTrue(PlatformDependent.isZero(bytes, 0, (-1)));
        Assert.assertTrue(PlatformDependent.isZero(bytes, 0, 100));
        Assert.assertTrue(PlatformDependent.isZero(bytes, 10, 90));
        bytes[10] = 1;
        Assert.assertTrue(PlatformDependent.isZero(bytes, 0, 10));
        Assert.assertFalse(PlatformDependent.isZero(bytes, 0, 11));
        Assert.assertFalse(PlatformDependent.isZero(bytes, 10, 1));
        Assert.assertTrue(PlatformDependent.isZero(bytes, 11, 89));
    }

    private interface EqualityChecker {
        boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length);
    }

    @Test
    public void testHashCodeAscii() {
        for (int i = 0; i < 1000; ++i) {
            // byte[] and char[] need to be initialized such that there values are within valid "ascii" range
            byte[] bytes = new byte[i];
            char[] bytesChar = new char[i];
            for (int j = 0; j < (bytesChar.length); ++j) {
                bytesChar[j] = PlatformDependentTest.randomCharInByteRange();
                bytes[j] = ((byte) ((bytesChar[j]) & 255));
            }
            String string = new String(bytesChar);
            Assert.assertEquals(("length=" + i), PlatformDependent.hashCodeAsciiSafe(bytes, 0, bytes.length), PlatformDependent.hashCodeAscii(bytes, 0, bytes.length));
            Assert.assertEquals(("length=" + i), PlatformDependent.hashCodeAscii(bytes, 0, bytes.length), PlatformDependent.hashCodeAscii(string));
        }
    }

    @Test
    public void testAllocateWithCapacity0() {
        Assume.assumeTrue(PlatformDependent.hasDirectBufferNoCleanerConstructor());
        ByteBuffer buffer = PlatformDependent.allocateDirectNoCleaner(0);
        Assert.assertNotEquals(0, PlatformDependent.directBufferAddress(buffer));
        Assert.assertEquals(0, buffer.capacity());
        PlatformDependent.freeDirectNoCleaner(buffer);
    }
}

