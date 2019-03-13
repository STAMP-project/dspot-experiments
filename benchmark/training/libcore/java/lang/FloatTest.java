/**
 * Copyright (C) 2009 The Android Open Source Project
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
package libcore.java.lang;


import junit.framework.TestCase;


public class FloatTest extends TestCase {
    // Needed to prevent testVerifierTyping from statically resolving the if statement.
    static boolean testVerifierTypingBool = false;

    public void test_valueOf_String1() throws Exception {
        // This threw OutOfMemoryException.
        // http://code.google.com/p/android/issues/detail?id=4185
        TestCase.assertEquals(2358.166F, Float.valueOf("2358.166016"));
    }

    public void test_valueOf_String2() throws Exception {
        // This threw OutOfMemoryException.
        // http://code.google.com/p/android/issues/detail?id=3156
        TestCase.assertEquals((-2.14748365E9F), Float.valueOf(String.valueOf(Integer.MIN_VALUE)));
    }

    public void testNamedFloats() throws Exception {
        TestCase.assertEquals(Float.NaN, Float.parseFloat("NaN"));
        TestCase.assertEquals(Float.NaN, Float.parseFloat("-NaN"));
        TestCase.assertEquals(Float.NaN, Float.parseFloat("+NaN"));
        try {
            Float.parseFloat("NNaN");
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        try {
            Float.parseFloat("NaNN");
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.parseFloat("+Infinity"));
        TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.parseFloat("Infinity"));
        TestCase.assertEquals(Float.NEGATIVE_INFINITY, Float.parseFloat("-Infinity"));
        try {
            Float.parseFloat("IInfinity");
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        try {
            Float.parseFloat("Infinityy");
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
    }

    public void testSuffixParsing() throws Exception {
        String[] badStrings = new String[]{ "1ff", "1fd", "1df", "1dd" };
        for (String string : badStrings) {
            try {
                Float.parseFloat(string);
                TestCase.fail(string);
            } catch (NumberFormatException expected) {
            }
        }
        TestCase.assertEquals(1.0F, Float.parseFloat("1f"));
        TestCase.assertEquals(1.0F, Float.parseFloat("1d"));
        TestCase.assertEquals(1.0F, Float.parseFloat("1F"));
        TestCase.assertEquals(1.0F, Float.parseFloat("1D"));
        TestCase.assertEquals(1.0F, Float.parseFloat("1.D"));
        TestCase.assertEquals(1.0F, Float.parseFloat("1.E0D"));
        TestCase.assertEquals(1.0F, Float.parseFloat(".1E1D"));
    }

    public void testExponentParsing() throws Exception {
        String[] strings = new String[]{ // Exponents missing integer values.
        "1.0e", "1.0e+", "1.0e-", // Exponents with too many explicit signs.
        "1.0e++1", "1.0e+-1", "1.0e-+1", "1.0e--1" };
        for (String string : strings) {
            try {
                Float.parseFloat(string);
                TestCase.fail(string);
            } catch (NumberFormatException expected) {
            }
        }
        TestCase.assertEquals(1.4E-45F, Float.parseFloat("1.0e-45"));
        TestCase.assertEquals(0.0F, Float.parseFloat("1.0e-46"));
        TestCase.assertEquals((-1.4E-45F), Float.parseFloat("-1.0e-45"));
        TestCase.assertEquals((-0.0F), Float.parseFloat("-1.0e-46"));
        TestCase.assertEquals(1.0E38F, Float.parseFloat("1.0e+38"));
        TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.parseFloat("1.0e+39"));
        TestCase.assertEquals((-1.0E38F), Float.parseFloat("-1.0e+38"));
        TestCase.assertEquals(Float.NEGATIVE_INFINITY, Float.parseFloat("-1.0e+39"));
        TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.parseFloat("1.0e+9999999999"));
        TestCase.assertEquals(Float.NEGATIVE_INFINITY, Float.parseFloat("-1.0e+9999999999"));
        TestCase.assertEquals(0.0F, Float.parseFloat("1.0e-9999999999"));
        TestCase.assertEquals((-0.0F), Float.parseFloat("-1.0e-9999999999"));
        TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.parseFloat("320.0E+2147483647"));
        TestCase.assertEquals((-0.0F), Float.parseFloat("-1.4E-2147483314"));
    }

    public void testVerifierTyping() throws Exception {
        float f1 = 0;
        if (FloatTest.testVerifierTypingBool) {
            f1 = Float.MIN_VALUE;
        }
        TestCase.assertEquals(f1, 0.0F);
    }
}

