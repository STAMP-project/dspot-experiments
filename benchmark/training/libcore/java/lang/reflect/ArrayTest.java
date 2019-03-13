/**
 * Copyright (C) 2012 The Android Open Source Project
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
package libcore.java.lang.reflect;


import java.lang.reflect.Array;
import junit.framework.TestCase;


public class ArrayTest extends TestCase {
    private static boolean[] booleans;

    private static byte[] bytes;

    private static char[] chars;

    private static double[] doubles;

    private static float[] floats;

    private static int[] ints;

    private static long[] longs;

    private static short[] shorts;

    public void testGetBoolean() throws Exception {
        TestCase.assertEquals(ArrayTest.booleans[0], Array.getBoolean(ArrayTest.booleans, 0));
        try {
            Array.getBoolean(ArrayTest.bytes, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(ArrayTest.chars, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(ArrayTest.floats, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(ArrayTest.ints, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(ArrayTest.longs, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(ArrayTest.shorts, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getBoolean(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetByte() throws Exception {
        try {
            Array.getByte(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(ArrayTest.bytes[0], Array.getByte(ArrayTest.bytes, 0));
        try {
            Array.getByte(ArrayTest.chars, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getByte(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getByte(ArrayTest.floats, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getByte(ArrayTest.ints, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getByte(ArrayTest.longs, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getByte(ArrayTest.shorts, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getByte(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetChar() throws Exception {
        try {
            Array.getChar(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getChar(ArrayTest.bytes, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(ArrayTest.chars[0], Array.getChar(ArrayTest.chars, 0));
        try {
            Array.getChar(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getChar(ArrayTest.floats, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getChar(ArrayTest.ints, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getChar(ArrayTest.longs, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getChar(ArrayTest.shorts, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getChar(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetDouble() throws Exception {
        try {
            Array.getDouble(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((double) (ArrayTest.bytes[0])), Array.getDouble(ArrayTest.bytes, 0));
        TestCase.assertEquals(((double) (ArrayTest.chars[0])), Array.getDouble(ArrayTest.chars, 0));
        TestCase.assertEquals(ArrayTest.doubles[0], Array.getDouble(ArrayTest.doubles, 0));
        TestCase.assertEquals(((double) (ArrayTest.floats[0])), Array.getDouble(ArrayTest.floats, 0));
        TestCase.assertEquals(((double) (ArrayTest.ints[0])), Array.getDouble(ArrayTest.ints, 0));
        TestCase.assertEquals(((double) (ArrayTest.longs[0])), Array.getDouble(ArrayTest.longs, 0));
        TestCase.assertEquals(((double) (ArrayTest.shorts[0])), Array.getDouble(ArrayTest.shorts, 0));
        try {
            Array.getDouble(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetFloat() throws Exception {
        try {
            Array.getFloat(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((float) (ArrayTest.bytes[0])), Array.getFloat(ArrayTest.bytes, 0));
        TestCase.assertEquals(((float) (ArrayTest.chars[0])), Array.getFloat(ArrayTest.chars, 0));
        TestCase.assertEquals(ArrayTest.floats[0], Array.getFloat(ArrayTest.floats, 0));
        try {
            Array.getFloat(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((float) (ArrayTest.ints[0])), Array.getFloat(ArrayTest.ints, 0));
        TestCase.assertEquals(((float) (ArrayTest.longs[0])), Array.getFloat(ArrayTest.longs, 0));
        TestCase.assertEquals(((float) (ArrayTest.shorts[0])), Array.getFloat(ArrayTest.shorts, 0));
        try {
            Array.getFloat(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetInt() throws Exception {
        try {
            Array.getInt(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((int) (ArrayTest.bytes[0])), Array.getInt(ArrayTest.bytes, 0));
        TestCase.assertEquals(((int) (ArrayTest.chars[0])), Array.getInt(ArrayTest.chars, 0));
        try {
            Array.getInt(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getInt(ArrayTest.floats, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(ArrayTest.ints[0], Array.getInt(ArrayTest.ints, 0));
        try {
            Array.getInt(ArrayTest.longs, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((int) (ArrayTest.shorts[0])), Array.getInt(ArrayTest.shorts, 0));
        try {
            Array.getInt(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetLong() throws Exception {
        try {
            Array.getLong(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((long) (ArrayTest.bytes[0])), Array.getLong(ArrayTest.bytes, 0));
        TestCase.assertEquals(((long) (ArrayTest.chars[0])), Array.getLong(ArrayTest.chars, 0));
        try {
            Array.getLong(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getLong(ArrayTest.floats, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((long) (ArrayTest.ints[0])), Array.getLong(ArrayTest.ints, 0));
        TestCase.assertEquals(ArrayTest.longs[0], Array.getLong(ArrayTest.longs, 0));
        TestCase.assertEquals(((long) (ArrayTest.shorts[0])), Array.getLong(ArrayTest.shorts, 0));
        try {
            Array.getLong(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetShort() throws Exception {
        try {
            Array.getShort(ArrayTest.booleans, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(((int) (ArrayTest.bytes[0])), Array.getShort(ArrayTest.bytes, 0));
        try {
            Array.getShort(ArrayTest.chars, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getShort(ArrayTest.doubles, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getShort(ArrayTest.floats, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getShort(ArrayTest.ints, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.getShort(ArrayTest.longs, 0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(ArrayTest.shorts[0], Array.getShort(ArrayTest.shorts, 0));
        try {
            Array.getShort(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetBoolean() throws Exception {
        Array.setBoolean(ArrayTest.booleans, 0, ArrayTest.booleans[0]);
        try {
            Array.setBoolean(ArrayTest.bytes, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(ArrayTest.chars, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(ArrayTest.doubles, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(ArrayTest.floats, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(ArrayTest.ints, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(ArrayTest.longs, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(ArrayTest.shorts, 0, true);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setBoolean(null, 0, true);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetByte() throws Exception {
        try {
            Array.setByte(ArrayTest.booleans, 0, ArrayTest.bytes[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setByte(ArrayTest.bytes, 0, ArrayTest.bytes[0]);
        try {
            Array.setByte(ArrayTest.chars, 0, ArrayTest.bytes[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setByte(ArrayTest.doubles, 0, ArrayTest.bytes[0]);
        Array.setByte(ArrayTest.floats, 0, ArrayTest.bytes[0]);
        Array.setByte(ArrayTest.ints, 0, ArrayTest.bytes[0]);
        Array.setByte(ArrayTest.longs, 0, ArrayTest.bytes[0]);
        Array.setByte(ArrayTest.shorts, 0, ArrayTest.bytes[0]);
        try {
            Array.setByte(null, 0, ArrayTest.bytes[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetChar() throws Exception {
        try {
            Array.setChar(ArrayTest.booleans, 0, ArrayTest.chars[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setChar(ArrayTest.bytes, 0, ArrayTest.chars[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setChar(ArrayTest.chars, 0, ArrayTest.chars[0]);
        Array.setChar(ArrayTest.doubles, 0, ArrayTest.chars[0]);
        Array.setChar(ArrayTest.floats, 0, ArrayTest.chars[0]);
        Array.setChar(ArrayTest.ints, 0, ArrayTest.chars[0]);
        Array.setChar(ArrayTest.longs, 0, ArrayTest.chars[0]);
        try {
            Array.setChar(ArrayTest.shorts, 0, ArrayTest.chars[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setChar(null, 0, ArrayTest.chars[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetDouble() throws Exception {
        try {
            Array.setDouble(ArrayTest.booleans, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setDouble(ArrayTest.bytes, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setDouble(ArrayTest.chars, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setDouble(ArrayTest.doubles, 0, ArrayTest.doubles[0]);
        try {
            Array.setDouble(ArrayTest.floats, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setDouble(ArrayTest.ints, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setDouble(ArrayTest.longs, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setDouble(ArrayTest.shorts, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setDouble(null, 0, ArrayTest.doubles[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetFloat() throws Exception {
        try {
            Array.setFloat(ArrayTest.booleans, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setFloat(ArrayTest.bytes, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setFloat(ArrayTest.chars, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setFloat(ArrayTest.floats, 0, ArrayTest.floats[0]);
        Array.setFloat(ArrayTest.doubles, 0, ArrayTest.floats[0]);
        try {
            Array.setFloat(ArrayTest.ints, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setFloat(ArrayTest.longs, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setFloat(ArrayTest.shorts, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setFloat(null, 0, ArrayTest.floats[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetInt() throws Exception {
        try {
            Array.setInt(ArrayTest.booleans, 0, ArrayTest.ints[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setInt(ArrayTest.bytes, 0, ArrayTest.ints[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setInt(ArrayTest.chars, 0, ArrayTest.ints[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setInt(ArrayTest.doubles, 0, ArrayTest.ints[0]);
        Array.setInt(ArrayTest.floats, 0, ArrayTest.ints[0]);
        Array.setInt(ArrayTest.ints, 0, ArrayTest.ints[0]);
        Array.setInt(ArrayTest.longs, 0, ArrayTest.ints[0]);
        try {
            Array.setInt(ArrayTest.shorts, 0, ArrayTest.ints[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setInt(null, 0, ArrayTest.ints[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetLong() throws Exception {
        try {
            Array.setLong(ArrayTest.booleans, 0, ArrayTest.longs[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setLong(ArrayTest.bytes, 0, ArrayTest.longs[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setLong(ArrayTest.chars, 0, ArrayTest.longs[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setLong(ArrayTest.doubles, 0, ArrayTest.longs[0]);
        Array.setLong(ArrayTest.floats, 0, ArrayTest.longs[0]);
        try {
            Array.setLong(ArrayTest.ints, 0, ArrayTest.longs[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setLong(ArrayTest.longs, 0, ArrayTest.longs[0]);
        try {
            Array.setLong(ArrayTest.shorts, 0, ArrayTest.longs[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setLong(null, 0, ArrayTest.longs[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testSetShort() throws Exception {
        try {
            Array.setShort(ArrayTest.booleans, 0, ArrayTest.shorts[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setShort(ArrayTest.bytes, 0, ArrayTest.shorts[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Array.setShort(ArrayTest.chars, 0, ArrayTest.shorts[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        Array.setShort(ArrayTest.doubles, 0, ArrayTest.shorts[0]);
        Array.setShort(ArrayTest.floats, 0, ArrayTest.shorts[0]);
        Array.setShort(ArrayTest.ints, 0, ArrayTest.shorts[0]);
        Array.setShort(ArrayTest.longs, 0, ArrayTest.shorts[0]);
        Array.setShort(ArrayTest.shorts, 0, ArrayTest.shorts[0]);
        try {
            Array.setShort(null, 0, ArrayTest.shorts[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

