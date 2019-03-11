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
package dalvik.system;


import junit.framework.TestCase;


/**
 * Test JNI behavior
 */
public final class JniTest extends TestCase {
    static {
        System.loadLibrary("javacoretests");
    }

    /**
     * Test cases for implicit this argument
     */
    public void testPassingThis() {
        TestCase.assertEquals(this, returnThis());
    }

    /**
     * Test cases for implicit class argument
     */
    public void testPassingClass() {
        TestCase.assertEquals(JniTest.class, JniTest.returnClass());
    }

    /**
     * Test passing object references as arguments to a native method
     */
    public void testPassingObjectReferences() {
        final Object[] literals = new Object[]{ "Bradshaw", "Isherwood", "Oldknow", "Mallet", JniTest.class, null, Integer.valueOf(0) };
        final Object[] a = new Object[16];
        // test selection from a list of object literals where the literals are all the same
        for (Object literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnObjectArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnObjectArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing booleans as arguments to a native method
     */
    public void testPassingBooleans() {
        final boolean[] literals = new boolean[]{ true, false, false, true };
        final boolean[] a = new boolean[16];
        // test selection from a list of object literals where the literals are all the same
        for (boolean literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnBooleanArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnBooleanArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing characters as arguments to a native method
     */
    public void testPassingChars() {
        final char[] literals = new char[]{ Character.MAX_VALUE, Character.MIN_VALUE, Character.MAX_HIGH_SURROGATE, Character.MAX_LOW_SURROGATE, Character.MIN_HIGH_SURROGATE, Character.MIN_LOW_SURROGATE, 'a', 'z', 'A', 'Z', '0', '9' };
        final char[] a = new char[16];
        // test selection from a list of object literals where the literals are all the same
        for (char literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnCharArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnCharArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing bytes as arguments to a native method
     */
    public void testPassingBytes() {
        final byte[] literals = new byte[]{ Byte.MAX_VALUE, Byte.MIN_VALUE, 0, -1 };
        final byte[] a = new byte[16];
        // test selection from a list of object literals where the literals are all the same
        for (byte literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnByteArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnByteArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing shorts as arguments to a native method
     */
    public void testPassingShorts() {
        final short[] literals = new short[]{ Byte.MAX_VALUE, Byte.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, 0, -1 };
        final short[] a = new short[16];
        // test selection from a list of object literals where the literals are all the same
        for (short literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnShortArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnShortArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing ints as arguments to a native method
     */
    public void testPassingInts() {
        final int[] literals = new int[]{ Byte.MAX_VALUE, Byte.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1 };
        final int[] a = new int[16];
        // test selection from a list of object literals where the literals are all the same
        for (int literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnIntArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnIntArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing longs as arguments to a native method
     */
    public void testPassingLongs() {
        final long[] literals = new long[]{ Byte.MAX_VALUE, Byte.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, 0, -1 };
        final long[] a = new long[16];
        // test selection from a list of object literals where the literals are all the same
        for (long literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnLongArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnLongArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing floats as arguments to a native method
     */
    public void testPassingFloats() {
        final float[] literals = new float[]{ Byte.MAX_VALUE, Byte.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_NORMAL, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, ((float) (Math.E)), ((float) (Math.PI)), 0, -1 };
        final float[] a = new float[16];
        // test selection from a list of object literals where the literals are all the same
        for (float literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnFloatArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnFloatArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    /**
     * Test passing doubles as arguments to a native method
     */
    public void testPassingDoubles() {
        final double[] literals = new double[]{ Byte.MAX_VALUE, Byte.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_NORMAL, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Math.E, Math.PI, 0, -1 };
        final double[] a = new double[16];
        // test selection from a list of object literals where the literals are all the same
        for (double literal : literals) {
            for (int i = 0; i < 16; i++) {
                a[i] = literal;
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnDoubleArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
        // test selection from a list of object literals where the literals are shuffled
        for (int j = 0; j < (literals.length); j++) {
            for (int i = 0; i < 16; i++) {
                a[i] = literals[((i + j) % (literals.length))];
            }
            for (int i = 0; i < 16; i++) {
                TestCase.assertEquals(a[i], returnDoubleArgFrom16(i, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
            }
        }
    }

    public void testGetSuperclass() {
        TestCase.assertEquals(Object.class, JniTest.envGetSuperclass(String.class));
        TestCase.assertEquals(null, JniTest.envGetSuperclass(Object.class));
        TestCase.assertEquals(null, JniTest.envGetSuperclass(int.class));
        TestCase.assertEquals(null, JniTest.envGetSuperclass(Runnable.class));
    }
}

