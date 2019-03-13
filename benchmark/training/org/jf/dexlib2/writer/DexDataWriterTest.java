/**
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jf.dexlib2.writer;


import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.jf.util.ExceptionWithContext;
import org.junit.Test;


public class DexDataWriterTest {
    private Random random;

    private NakedByteArrayOutputStream output = new NakedByteArrayOutputStream();

    private int startPosition;

    private DexDataWriter writer;

    @Test
    public void testWriteByte() throws IOException {
        byte[] arr = new byte[257];
        for (int i = 0; i < 256; i++) {
            arr[i] = ((byte) (i));
            writer.write(i);
        }
        arr[256] = ((byte) (128));
        writer.write(384);
        expectData(arr);
    }

    @Test
    public void testWriteByteArray() throws IOException {
        byte[] arr = new byte[345];
        random.nextBytes(arr);
        writer.write(arr);
        expectData(arr);
    }

    @Test
    public void testWriteByteArrayWithLengthAndOffset() throws IOException {
        byte[] arr = new byte[345];
        random.nextBytes(arr);
        writer.write(arr, 10, 300);
        expectData(Arrays.copyOfRange(arr, 10, 310));
    }

    @Test
    public void testWriteLong() throws IOException {
        writer.writeLong(1234605616436508552L);
        writer.writeLong((-1234605616436508552L));
        expectData(136, 119, 102, 85, 68, 51, 34, 17, 120, 136, 153, 170, 187, 204, 221, 238);
    }

    @Test
    public void testWriteInt() throws IOException {
        writer.writeInt(287454020);
        writer.writeInt((-287454020));
        expectData(68, 51, 34, 17, 188, 204, 221, 238);
    }

    @Test
    public void testWriteShort() throws IOException {
        writer.writeShort(0);
        writer.writeShort(4386);
        writer.writeShort((-4386));
        writer.writeShort(32767);
        writer.writeShort((-32768));
        expectData(0, 0, 34, 17, 222, 238, 255, 127, 0, 128);
    }

    @Test(expected = ExceptionWithContext.class)
    public void testWriteShortOutOfBounds() throws IOException {
        writer.writeShort(32768);
    }

    @Test(expected = ExceptionWithContext.class)
    public void testWriteShortOutOfBounds2() throws IOException {
        writer.writeShort((-32769));
    }

    @Test
    public void testWriteUshort() throws IOException {
        writer.writeUshort(0);
        writer.writeUshort(4386);
        writer.writeUshort(34969);
        writer.writeUshort(65535);
        expectData(0, 0, 34, 17, 153, 136, 255, 255);
    }

    @Test(expected = ExceptionWithContext.class)
    public void testWriteUshortOutOfBounds() throws IOException {
        writer.writeUshort((-1));
    }

    @Test(expected = ExceptionWithContext.class)
    public void testWriteUshortOutOfBounds2() throws IOException {
        writer.writeUshort(65536);
    }

    @Test
    public void testWriteUbyte() throws IOException {
        writer.writeUbyte(0);
        writer.writeUbyte(1);
        writer.writeUbyte(18);
        writer.writeUbyte(255);
        expectData(0, 1, 18, 255);
    }

    @Test(expected = ExceptionWithContext.class)
    public void testWriteUbyteOutOfBounds() throws IOException {
        writer.writeUbyte((-1));
    }

    @Test(expected = ExceptionWithContext.class)
    public void testWriteUbyteOutOfBounds2() throws IOException {
        writer.writeUbyte(256);
    }

    @Test
    public void testWriteEncodedValueHeader() throws IOException {
        writer.writeEncodedValueHeader(2, 1);
        expectData(34);
    }

    @Test
    public void testWriteEncodedInt() throws IOException {
        testWriteEncodedIntHelper(0, 0);
        testWriteEncodedIntHelper(64, 64);
        testWriteEncodedIntHelper(127, 127);
        testWriteEncodedIntHelper(255, 255, 0);
        testWriteEncodedIntHelper(16777088, 128, 255, 255, 0);
        testWriteEncodedIntHelper(-128, 128);
        testWriteEncodedIntHelper(-1, 255);
        testWriteEncodedIntHelper(256, 0, 1);
        testWriteEncodedIntHelper(32767, 255, 127);
        testWriteEncodedIntHelper(32768, 0, 128, 0);
        testWriteEncodedIntHelper(-32768, 0, 128);
        testWriteEncodedIntHelper(65536, 0, 0, 1);
        testWriteEncodedIntHelper(66051, 3, 2, 1);
        testWriteEncodedIntHelper(8454659, 3, 2, 129, 0);
        testWriteEncodedIntHelper(-8322557, 3, 2, 129);
        testWriteEncodedIntHelper(16777216, 0, 0, 0, 1);
        testWriteEncodedIntHelper(16909060, 4, 3, 2, 1);
        testWriteEncodedIntHelper(2147483647, 255, 255, 255, 127);
        testWriteEncodedIntHelper(-2147483648, 0, 0, 0, 128);
        testWriteEncodedIntHelper(-2147483647, 1, 0, 0, 128);
    }

    @Test
    public void testWriteEncodedUint() throws IOException {
        testWriteEncodedUintHelper(0, 0);
        testWriteEncodedUintHelper(1, 1);
        testWriteEncodedUintHelper(64, 64);
        testWriteEncodedUintHelper(127, 127);
        testWriteEncodedUintHelper(128, 128);
        testWriteEncodedUintHelper(129, 129);
        testWriteEncodedUintHelper(255, 255);
        testWriteEncodedUintHelper(256, 0, 1);
        testWriteEncodedUintHelper(384, 128, 1);
        testWriteEncodedUintHelper(32896, 128, 128);
        testWriteEncodedUintHelper(4660, 52, 18);
        testWriteEncodedUintHelper(4096, 0, 16);
        testWriteEncodedUintHelper(32768, 0, 128);
        testWriteEncodedUintHelper(65280, 0, 255);
        testWriteEncodedUintHelper(65535, 255, 255);
        testWriteEncodedUintHelper(65536, 0, 0, 1);
        testWriteEncodedUintHelper(131071, 255, 255, 1);
        testWriteEncodedUintHelper(8454143, 255, 255, 128);
        testWriteEncodedUintHelper(16777215, 255, 255, 255);
        testWriteEncodedUintHelper(16777216, 0, 0, 0, 1);
        testWriteEncodedUintHelper(16909060, 4, 3, 2, 1);
        testWriteEncodedUintHelper(-2147483648, 0, 0, 0, 128);
        testWriteEncodedUintHelper(-2130706433, 255, 255, 255, 128);
        testWriteEncodedUintHelper(-1, 255, 255, 255, 255);
    }

    @Test
    public void testWriteEncodedLong() throws IOException {
        testWriteEncodedLongHelper(0L, 0);
        testWriteEncodedLongHelper(64L, 64);
        testWriteEncodedLongHelper(127L, 127);
        testWriteEncodedLongHelper(255L, 255, 0);
        testWriteEncodedLongHelper(-128L, 128);
        testWriteEncodedLongHelper(-1L, 255);
        testWriteEncodedLongHelper(256L, 0, 1);
        testWriteEncodedLongHelper(32767L, 255, 127);
        testWriteEncodedLongHelper(32768L, 0, 128, 0);
        testWriteEncodedLongHelper(-32768L, 0, 128);
        testWriteEncodedLongHelper(65536L, 0, 0, 1);
        testWriteEncodedLongHelper(66051L, 3, 2, 1);
        testWriteEncodedLongHelper(8454659L, 3, 2, 129, 0);
        testWriteEncodedLongHelper(-8322557L, 3, 2, 129);
        testWriteEncodedLongHelper(16777216L, 0, 0, 0, 1);
        testWriteEncodedLongHelper(16909060L, 4, 3, 2, 1);
        testWriteEncodedLongHelper(2147483647L, 255, 255, 255, 127);
        testWriteEncodedLongHelper(2147483648L, 0, 0, 0, 128, 0);
        testWriteEncodedLongHelper(-2147483648L, 0, 0, 0, 128);
        testWriteEncodedLongHelper(-2147483647L, 1, 0, 0, 128);
        testWriteEncodedLongHelper(4294967296L, 0, 0, 0, 0, 1);
        testWriteEncodedLongHelper(4328719365L, 5, 4, 3, 2, 1);
        testWriteEncodedLongHelper(549755813887L, 255, 255, 255, 255, 127);
        testWriteEncodedLongHelper(549755813888L, 0, 0, 0, 0, 128, 0);
        testWriteEncodedLongHelper(-549755813888L, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(-549755813887L, 1, 0, 0, 0, 128);
        testWriteEncodedLongHelper(1099511627776L, 0, 0, 0, 0, 0, 1);
        testWriteEncodedLongHelper(1108152157446L, 6, 5, 4, 3, 2, 1);
        testWriteEncodedLongHelper(140737488355327L, 255, 255, 255, 255, 255, 127);
        testWriteEncodedLongHelper(140737488355328L, 0, 0, 0, 0, 0, 128, 0);
        testWriteEncodedLongHelper(-140737488355328L, 0, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(-140737488355327L, 1, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(281474976710656L, 0, 0, 0, 0, 0, 0, 1);
        testWriteEncodedLongHelper(283686952306183L, 7, 6, 5, 4, 3, 2, 1);
        testWriteEncodedLongHelper(36028797018963967L, 255, 255, 255, 255, 255, 255, 127);
        testWriteEncodedLongHelper(36028797018963968L, 0, 0, 0, 0, 0, 0, 128, 0);
        testWriteEncodedLongHelper(-36028797018963968L, 0, 0, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(-36028797018963967L, 1, 0, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(72057594037927936L, 0, 0, 0, 0, 0, 0, 0, 1);
        testWriteEncodedLongHelper(72623859790382856L, 8, 7, 6, 5, 4, 3, 2, 1);
        testWriteEncodedLongHelper(9223372036854775807L, 255, 255, 255, 255, 255, 255, 255, 127);
        testWriteEncodedLongHelper(-9223372036854775808L, 0, 0, 0, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(-9223372036854775807L, 1, 0, 0, 0, 0, 0, 0, 128);
        testWriteEncodedLongHelper(-72057594037927937L, 255, 255, 255, 255, 255, 255, 255, 254);
        testWriteEncodedLongHelper(1311768467463790320L, 240, 222, 188, 154, 120, 86, 52, 18);
    }

    @Test
    public void testWriteRightZeroExtendedInt() throws IOException {
        testWriteRightZeroExtendedIntHelper(0, 0);
        testWriteRightZeroExtendedIntHelper(16777216, 1);
        testWriteRightZeroExtendedIntHelper(2130706432, 127);
        testWriteRightZeroExtendedIntHelper(-2147483648, 128);
        testWriteRightZeroExtendedIntHelper(-268435456, 240);
        testWriteRightZeroExtendedIntHelper(-16777216, 255);
        testWriteRightZeroExtendedIntHelper(65536, 1, 0);
        testWriteRightZeroExtendedIntHelper(17825792, 16, 1);
        testWriteRightZeroExtendedIntHelper(2131755008, 16, 127);
        testWriteRightZeroExtendedIntHelper(-2146435072, 16, 128);
        testWriteRightZeroExtendedIntHelper(-267386880, 16, 240);
        testWriteRightZeroExtendedIntHelper(-15728640, 16, 255);
        testWriteRightZeroExtendedIntHelper(-16777216, 255);
        testWriteRightZeroExtendedIntHelper(256, 1, 0, 0);
        testWriteRightZeroExtendedIntHelper(17829888, 16, 16, 1);
        testWriteRightZeroExtendedIntHelper(2131759104, 16, 16, 127);
        testWriteRightZeroExtendedIntHelper(-2146430976, 16, 16, 128);
        testWriteRightZeroExtendedIntHelper(-267382784, 16, 16, 240);
        testWriteRightZeroExtendedIntHelper(-15724544, 16, 16, 255);
        testWriteRightZeroExtendedIntHelper(1, 1, 0, 0, 0);
        testWriteRightZeroExtendedIntHelper(128, 128, 0, 0, 0);
        testWriteRightZeroExtendedIntHelper(255, 255, 0, 0, 0);
        testWriteRightZeroExtendedIntHelper(17829904, 16, 16, 16, 1);
        testWriteRightZeroExtendedIntHelper(2131759120, 16, 16, 16, 127);
        testWriteRightZeroExtendedIntHelper(-2146430960, 16, 16, 16, 128);
        testWriteRightZeroExtendedIntHelper(-267382768, 16, 16, 16, 240);
        testWriteRightZeroExtendedIntHelper(-15724528, 16, 16, 16, 255);
    }

    @Test
    public void testWriteRightZeroExtendedLong() throws IOException {
        testWriteRightZeroExtendedLongHelper(0, 0);
        testWriteRightZeroExtendedLongHelper(72057594037927936L, 1);
        testWriteRightZeroExtendedLongHelper(9151314442816847872L, 127);
        testWriteRightZeroExtendedLongHelper(-9223372036854775808L, 128);
        testWriteRightZeroExtendedLongHelper(-1152921504606846976L, 240);
        testWriteRightZeroExtendedLongHelper(-72057594037927936L, 255);
        testWriteRightZeroExtendedLongHelper(281474976710656L, 1, 0);
        testWriteRightZeroExtendedLongHelper(76561193665298432L, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155818042444218368L, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218868437227405312L, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148417904979476480L, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67553994410557440L, 16, 255);
        testWriteRightZeroExtendedLongHelper(9223090561878065152L, 255, 127);
        testWriteRightZeroExtendedLongHelper(1099511627776L, 1, 0, 0);
        testWriteRightZeroExtendedLongHelper(76578785851342848L, 16, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155835634630262784L, 16, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218850845041360896L, 16, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148400312793432064L, 16, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67536402224513024L, 16, 16, 255);
        testWriteRightZeroExtendedLongHelper(9223370937343148032L, 255, 255, 127);
        testWriteRightZeroExtendedLongHelper(4294967296L, 1, 0, 0, 0);
        testWriteRightZeroExtendedLongHelper(76578854570819584L, 16, 16, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155835703349739520L, 16, 16, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218850776321884160L, 16, 16, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148400244073955328L, 16, 16, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67536333505036288L, 16, 16, 16, 255);
        testWriteRightZeroExtendedLongHelper(9223372032559808512L, 255, 255, 255, 127);
        testWriteRightZeroExtendedLongHelper(16777216L, 1, 0, 0, 0, 0);
        testWriteRightZeroExtendedLongHelper(76578854839255040L, 16, 16, 16, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155835703618174976L, 16, 16, 16, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218850776053448704L, 16, 16, 16, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148400243805519872L, 16, 16, 16, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67536333236600832L, 16, 16, 16, 16, 255);
        testWriteRightZeroExtendedLongHelper(9223372036837998592L, 255, 255, 255, 255, 127);
        testWriteRightZeroExtendedLongHelper(65536L, 1, 0, 0, 0, 0, 0);
        testWriteRightZeroExtendedLongHelper(76578854840303616L, 16, 16, 16, 16, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155835703619223552L, 16, 16, 16, 16, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218850776052400128L, 16, 16, 16, 16, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148400243804471296L, 16, 16, 16, 16, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67536333235552256L, 16, 16, 16, 16, 16, 255);
        testWriteRightZeroExtendedLongHelper(9223372036854710272L, 255, 255, 255, 255, 255, 127);
        testWriteRightZeroExtendedLongHelper(256L, 1, 0, 0, 0, 0, 0, 0);
        testWriteRightZeroExtendedLongHelper(76578854840307712L, 16, 16, 16, 16, 16, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155835703619227648L, 16, 16, 16, 16, 16, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218850776052396032L, 16, 16, 16, 16, 16, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148400243804467200L, 16, 16, 16, 16, 16, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67536333235548160L, 16, 16, 16, 16, 16, 16, 255);
        testWriteRightZeroExtendedLongHelper(9223372036854775552L, 255, 255, 255, 255, 255, 255, 127);
        testWriteRightZeroExtendedLongHelper(1L, 1, 0, 0, 0, 0, 0, 0, 0);
        testWriteRightZeroExtendedLongHelper(76578854840307728L, 16, 16, 16, 16, 16, 16, 16, 1);
        testWriteRightZeroExtendedLongHelper(9155835703619227664L, 16, 16, 16, 16, 16, 16, 16, 127);
        testWriteRightZeroExtendedLongHelper(-9218850776052396016L, 16, 16, 16, 16, 16, 16, 16, 128);
        testWriteRightZeroExtendedLongHelper(-1148400243804467184L, 16, 16, 16, 16, 16, 16, 16, 240);
        testWriteRightZeroExtendedLongHelper(-67536333235548144L, 16, 16, 16, 16, 16, 16, 16, 255);
        testWriteRightZeroExtendedLongHelper(Long.MAX_VALUE, 255, 255, 255, 255, 255, 255, 255, 127);
        testWriteRightZeroExtendedLongHelper(Long.MIN_VALUE, 128);
        testWriteRightZeroExtendedLongHelper((-1), 255, 255, 255, 255, 255, 255, 255, 255);
    }

    @Test
    public void testWriteString() throws IOException {
        testWriteStringHelper(new String(new char[]{ 0 }), 192, 128);
        testWriteStringHelper(new String(new char[]{ 1 }), 1);
        testWriteStringHelper(new String(new char[]{ 64 }), 64);
        testWriteStringHelper(new String(new char[]{ 127 }), 127);
        testWriteStringHelper(new String(new char[]{ 128 }), 194, 128);
        testWriteStringHelper(new String(new char[]{ 129 }), 194, 129);
        testWriteStringHelper(new String(new char[]{ 256 }), 196, 128);
        testWriteStringHelper(new String(new char[]{ 2047 }), 223, 191);
        testWriteStringHelper(new String(new char[]{ 2048 }), 224, 160, 128);
        testWriteStringHelper(new String(new char[]{ 2049 }), 224, 160, 129);
        testWriteStringHelper(new String(new char[]{ 4096 }), 225, 128, 128);
        testWriteStringHelper(new String(new char[]{ 32767 }), 231, 191, 191);
        testWriteStringHelper(new String(new char[]{ 32768 }), 232, 128, 128);
        testWriteStringHelper(new String(new char[]{ 32769 }), 232, 128, 129);
        testWriteStringHelper(new String(new char[]{ 65535 }), 239, 191, 191);
    }

    @Test
    public void testAlign() throws IOException {
        // create a new writer so we can start at file position 0
        startPosition = 0;
        writer = new DexDataWriter(output, startPosition, 256);
        writer.align();
        writer.write(1);
        writer.align();
        writer.align();
        writer.write(1);
        writer.write(2);
        writer.align();
        writer.write(1);
        writer.write(2);
        writer.write(3);
        writer.align();
        writer.align();
        writer.write(1);
        writer.write(2);
        writer.write(3);
        writer.write(4);
        writer.align();
        writer.align();
        writer.align();
        writer.align();
        writer.write(1);
        writer.align();
        expectData(1, 0, 0, 0, 1, 2, 0, 0, 1, 2, 3, 0, 1, 2, 3, 4, 1, 0, 0, 0);
    }
}

