/**
 * Copyright (C) 2011 The Android Open Source Project
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
package libcore.java.util;


import java.util.Arrays;
import java.util.BitSet;
import junit.framework.TestCase;


public class BitSetTest extends TestCase {
    public void test_toString() throws Exception {
        // From the RI javadoc.
        BitSet bs = new BitSet();
        TestCase.assertEquals("{}", bs.toString());
        bs.set(2);
        TestCase.assertEquals("{2}", bs.toString());
        bs.set(4);
        bs.set(10);
        TestCase.assertEquals("{2, 4, 10}", bs.toString());
    }

    public void test_valueOf_long() throws Exception {
        BitSetTest.assertBitSet(new long[0], "{}");
        BitSetTest.assertBitSet(new long[]{ 1L }, "{0}");
        BitSetTest.assertBitSet(new long[]{ 273L }, "{0, 4, 8}");
        BitSetTest.assertBitSet(new long[]{ 257L, 4611686018427387904L }, "{0, 8, 126}");
    }

    public void test_valueOf_byte() throws Exception {
        // Nothing...
        BitSetTest.assertBitSet(new byte[0], "{}");
        // Less than a long...
        BitSetTest.assertBitSet(new byte[]{ 1 }, "{0}");
        BitSetTest.assertBitSet(new byte[]{ 1, 17 }, "{0, 8, 12}");
        BitSetTest.assertBitSet(new byte[]{ 1, 1, 0, 0, 1 }, "{0, 8, 32}");
        // Exactly one long....
        BitSetTest.assertBitSet(new byte[]{ 1, 0, 0, 0, 0, 0, 0, ((byte) (128)) }, "{0, 63}");
        // One long and a byte left over...
        BitSetTest.assertBitSet(new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0, 1 }, "{0, 64}");
        // Two longs...
        byte[] bytes = new byte[]{ 1, 0, 0, 0, 0, 0, 0, ((byte) (128)), 1, 0, 0, 0, 0, 0, 0, ((byte) (128)) };
        BitSetTest.assertBitSet(bytes, "{0, 63, 64, 127}");
    }

    public void test_toLongArray() throws Exception {
        TestCase.assertEquals("[]", Arrays.toString(BitSet.valueOf(new long[0]).toLongArray()));
        TestCase.assertEquals("[1]", Arrays.toString(BitSet.valueOf(new long[]{ 1 }).toLongArray()));
        TestCase.assertEquals("[1, 2]", Arrays.toString(BitSet.valueOf(new long[]{ 1, 2 }).toLongArray()));
        // Check that we're not returning trailing empty space.
        TestCase.assertEquals("[]", Arrays.toString(new BitSet(128).toLongArray()));
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(64, 66);
        bs.clear(64, 66);
        TestCase.assertEquals("[1]", Arrays.toString(bs.toLongArray()));
    }

    public void test_toByteArray() throws Exception {
        TestCase.assertEquals("[]", Arrays.toString(BitSet.valueOf(new long[0]).toByteArray()));
        TestCase.assertEquals("[1]", Arrays.toString(BitSet.valueOf(new long[]{ 1 }).toByteArray()));
        TestCase.assertEquals("[-17, -51, -85, -112, 120, 86, 52, 18]", Arrays.toString(BitSet.valueOf(new long[]{ 1311768467294899695L }).toByteArray()));
        TestCase.assertEquals("[1, 0, 0, 0, 0, 0, 0, 0, 2]", Arrays.toString(BitSet.valueOf(new long[]{ 1, 2 }).toByteArray()));
    }

    public void test_previousSetBit() {
        TestCase.assertEquals((-1), new BitSet().previousSetBit(666));
        BitSet bs;
        bs = new BitSet();
        bs.set(32);
        TestCase.assertEquals(32, bs.previousSetBit(999));
        TestCase.assertEquals(32, bs.previousSetBit(33));
        TestCase.assertEquals(32, bs.previousSetBit(32));
        TestCase.assertEquals((-1), bs.previousSetBit(31));
        bs = new BitSet();
        bs.set(0);
        bs.set(1);
        bs.set(32);
        bs.set(192);
        bs.set(666);
        TestCase.assertEquals(666, bs.previousSetBit(999));
        TestCase.assertEquals(666, bs.previousSetBit(667));
        TestCase.assertEquals(666, bs.previousSetBit(666));
        TestCase.assertEquals(192, bs.previousSetBit(665));
        TestCase.assertEquals(32, bs.previousSetBit(191));
        TestCase.assertEquals(1, bs.previousSetBit(31));
        TestCase.assertEquals(0, bs.previousSetBit(0));
        TestCase.assertEquals((-1), bs.previousSetBit((-1)));
    }

    public void test_differentSizes() {
        BitSet result = BitSetTest.big();
        result.and(BitSetTest.small());
        TestCase.assertEquals("{}", result.toString());
        result = BitSetTest.small();
        result.and(BitSetTest.big());
        TestCase.assertEquals("{}", result.toString());
        result = BitSetTest.big();
        result.andNot(BitSetTest.small());
        TestCase.assertEquals("{1000}", result.toString());
        result = BitSetTest.small();
        result.andNot(BitSetTest.big());
        TestCase.assertEquals("{10}", result.toString());
        TestCase.assertFalse(BitSetTest.big().intersects(BitSetTest.small()));
        TestCase.assertFalse(BitSetTest.small().intersects(BitSetTest.big()));
        result = BitSetTest.big();
        result.or(BitSetTest.small());
        TestCase.assertEquals("{10, 1000}", result.toString());
        result = BitSetTest.small();
        result.or(BitSetTest.big());
        TestCase.assertEquals("{10, 1000}", result.toString());
        result = BitSetTest.big();
        result.xor(BitSetTest.small());
        TestCase.assertEquals("{10, 1000}", result.toString());
        result = BitSetTest.small();
        result.xor(BitSetTest.big());
        TestCase.assertEquals("{10, 1000}", result.toString());
    }
}

