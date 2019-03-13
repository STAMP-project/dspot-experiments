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
package org.apache.beam.sdk.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link VarInt}.
 */
@RunWith(JUnit4.class)
public class VarIntTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    // Long values to check for boundary cases.
    private static final long[] LONG_VALUES = new long[]{ 0, 1, 127, 128, 16383, 16384, 2097151, 2097152, 268435455, 268435456, 34359738367L, 34359738368L, 9223372036854775807L, -9223372036854775808L, -1 };

    // VarInt encoding of the above VALUES.
    private static final byte[][] LONG_ENCODED = new byte[][]{ // 0
    new byte[]{ 0 }, // 1
    new byte[]{ 1 }, // 127
    new byte[]{ 127 }, // 128
    new byte[]{ ((byte) (128)), 1 }, // 16383
    new byte[]{ ((byte) (255)), 127 }, // 16834
    new byte[]{ ((byte) (128)), ((byte) (128)), 1 }, // 2097151
    new byte[]{ ((byte) (255)), ((byte) (255)), 127 }, // 2097152
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 1 }, // 268435455
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 127 }, // 268435456
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 1 }, // 34359738367
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 127 }, // 34359738368
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 1 }, // 9223372036854775807
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) }, // -9223372036854775808L
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 1 }, // -1
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 1 } };

    // Integer values to check for boundary cases.
    private static final int[] INT_VALUES = new int[]{ 0, 1, 127, 128, 16383, 16384, 2097151, 2097152, 268435455, 268435456, 2147483647, -2147483648, -1 };

    // VarInt encoding of the above VALUES.
    private static final byte[][] INT_ENCODED = new byte[][]{ // 0
    new byte[]{ ((byte) (0)) }, // 1
    new byte[]{ ((byte) (1)) }, // 127
    new byte[]{ ((byte) (127)) }, // 128
    new byte[]{ ((byte) (128)), ((byte) (1)) }, // 16383
    new byte[]{ ((byte) (255)), ((byte) (127)) }, // 16834
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (1)) }, // 2097151
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (127)) }, // 2097152
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (1)) }, // 268435455
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) }, // 268435456
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (1)) }, // 2147483647
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (7)) }, // -2147483648
    new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (8)) }, // -1
    new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (15)) } };

    @Test
    public void decodeValues() throws IOException {
        Assert.assertEquals(VarIntTest.LONG_VALUES.length, VarIntTest.LONG_ENCODED.length);
        for (int i = 0; i < (VarIntTest.LONG_ENCODED.length); ++i) {
            ByteArrayInputStream stream = new ByteArrayInputStream(VarIntTest.LONG_ENCODED[i]);
            long parsed = VarInt.decodeLong(stream);
            Assert.assertEquals(VarIntTest.LONG_VALUES[i], parsed);
            Assert.assertEquals((-1), stream.read());
        }
        Assert.assertEquals(VarIntTest.INT_VALUES.length, VarIntTest.INT_ENCODED.length);
        for (int i = 0; i < (VarIntTest.INT_ENCODED.length); ++i) {
            ByteArrayInputStream stream = new ByteArrayInputStream(VarIntTest.INT_ENCODED[i]);
            int parsed = VarInt.decodeInt(stream);
            Assert.assertEquals(VarIntTest.INT_VALUES[i], parsed);
            Assert.assertEquals((-1), stream.read());
        }
    }

    @Test
    public void encodeValuesAndGetLength() throws IOException {
        Assert.assertEquals(VarIntTest.LONG_VALUES.length, VarIntTest.LONG_ENCODED.length);
        for (int i = 0; i < (VarIntTest.LONG_VALUES.length); ++i) {
            byte[] encoded = VarIntTest.encodeLong(VarIntTest.LONG_VALUES[i]);
            Assert.assertThat(encoded, Matchers.equalTo(VarIntTest.LONG_ENCODED[i]));
            Assert.assertEquals(VarIntTest.LONG_ENCODED[i].length, VarInt.getLength(VarIntTest.LONG_VALUES[i]));
        }
        Assert.assertEquals(VarIntTest.INT_VALUES.length, VarIntTest.INT_ENCODED.length);
        for (int i = 0; i < (VarIntTest.INT_VALUES.length); ++i) {
            byte[] encoded = VarIntTest.encodeInt(VarIntTest.INT_VALUES[i]);
            Assert.assertThat(encoded, Matchers.equalTo(VarIntTest.INT_ENCODED[i]));
            Assert.assertEquals(VarIntTest.INT_ENCODED[i].length, VarInt.getLength(VarIntTest.INT_VALUES[i]));
        }
    }

    @Test
    public void decodeThrowsExceptionForOverflow() throws IOException {
        final byte[] tooLargeNumber = new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 2 };
        thrown.expect(IOException.class);
        VarIntTest.decodeLong(tooLargeNumber);
    }

    @Test
    public void decodeThrowsExceptionForIntOverflow() throws IOException {
        byte[] encoded = VarIntTest.encodeLong((1L << 32));
        thrown.expect(IOException.class);
        VarIntTest.decodeInt(encoded);
    }

    @Test
    public void decodeThrowsExceptionForIntUnderflow() throws IOException {
        byte[] encoded = VarIntTest.encodeLong((-1));
        thrown.expect(IOException.class);
        VarIntTest.decodeInt(encoded);
    }

    @Test
    public void decodeThrowsExceptionForNonterminated() throws IOException {
        final byte[] nonTerminatedNumber = new byte[]{ ((byte) (255)), ((byte) (255)) };
        thrown.expect(IOException.class);
        VarIntTest.decodeLong(nonTerminatedNumber);
    }

    @Test
    public void decodeParsesEncodedValues() throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        for (int i = 10; i < (Integer.MAX_VALUE); i = ((int) (i * 1.1))) {
            VarInt.encode(i, outStream);
            VarInt.encode((-i), outStream);
        }
        for (long i = 10; i < (Long.MAX_VALUE); i = ((long) (i * 1.1))) {
            VarInt.encode(i, outStream);
            VarInt.encode((-i), outStream);
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        for (int i = 10; i < (Integer.MAX_VALUE); i = ((int) (i * 1.1))) {
            Assert.assertEquals(i, VarInt.decodeInt(inStream));
            Assert.assertEquals((-i), VarInt.decodeInt(inStream));
        }
        for (long i = 10; i < (Long.MAX_VALUE); i = ((long) (i * 1.1))) {
            Assert.assertEquals(i, VarInt.decodeLong(inStream));
            Assert.assertEquals((-i), VarInt.decodeLong(inStream));
        }
    }

    @Test
    public void endOfFileThrowsException() throws Exception {
        ByteArrayInputStream inStream = new ByteArrayInputStream(new byte[0]);
        thrown.expect(EOFException.class);
        VarInt.decodeInt(inStream);
    }

    @Test
    public void unterminatedThrowsException() throws Exception {
        byte[] e = VarIntTest.encodeLong(Long.MAX_VALUE);
        byte[] s = new byte[1];
        s[0] = e[0];
        ByteArrayInputStream inStream = new ByteArrayInputStream(s);
        thrown.expect(IOException.class);
        VarInt.decodeInt(inStream);
    }
}

