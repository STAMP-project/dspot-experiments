/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import com.google.j2objc.util.ReflectionUtil;
import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;
import tests.util.SerializationTester;


public class EnumTest extends TestCase {
    enum Sample {

        LARRY,
        MOE,
        CURLY;}

    EnumTest.Sample larry = EnumTest.Sample.LARRY;

    EnumTest.Sample moe = EnumTest.Sample.MOE;

    enum Empty {
        ;
    }

    enum Bogus {

        UNUSED;}

    enum Color {

        Red,
        Green,
        Blue() {};}

    enum MockCloneEnum {

        ONE;
        public void callClone() throws CloneNotSupportedException {
            super.clone();
        }
    }

    /**
     * java.lang.Enum#compareTo(java.lang.Enum)
     */
    public void test_compareToLjava_lang_Enum() {
        TestCase.assertTrue((0 < (EnumTest.Sample.MOE.compareTo(EnumTest.Sample.LARRY))));
        TestCase.assertEquals(0, EnumTest.Sample.MOE.compareTo(EnumTest.Sample.MOE));
        TestCase.assertTrue((0 > (EnumTest.Sample.MOE.compareTo(EnumTest.Sample.CURLY))));
        try {
            EnumTest.Sample.MOE.compareTo(((EnumTest.Sample) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.Enum#equals(Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertFalse(moe.equals("bob"));
        TestCase.assertTrue(moe.equals(EnumTest.Sample.MOE));
        TestCase.assertFalse(EnumTest.Sample.LARRY.equals(EnumTest.Sample.CURLY));
        TestCase.assertTrue(EnumTest.Sample.LARRY.equals(larry));
        TestCase.assertFalse(EnumTest.Sample.CURLY.equals(null));
    }

    /**
     * java.lang.Enum#getDeclaringClass()
     */
    public void test_getDeclaringClass() {
        TestCase.assertEquals(EnumTest.Sample.class, moe.getDeclaringClass());
    }

    /**
     * java.lang.Enum#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals(moe.hashCode(), moe.hashCode());
    }

    /**
     * java.lang.Enum#name()
     */
    public void test_name() {
        TestCase.assertEquals("MOE", moe.name());
    }

    /**
     * java.lang.Enum#ordinal()
     */
    public void test_ordinal() {
        TestCase.assertEquals(0, larry.ordinal());
        TestCase.assertEquals(1, moe.ordinal());
        TestCase.assertEquals(2, EnumTest.Sample.CURLY.ordinal());
    }

    /**
     * java.lang.Enum#toString()
     */
    public void test_toString() {
        TestCase.assertTrue(moe.toString().equals("MOE"));
    }

    /**
     * java.lang.Enum#valueOf(Class, String)
     */
    public void test_valueOfLjava_lang_String() {
        TestCase.assertSame(EnumTest.Sample.CURLY, EnumTest.Sample.valueOf("CURLY"));
        TestCase.assertSame(EnumTest.Sample.LARRY, EnumTest.Sample.valueOf("LARRY"));
        TestCase.assertSame(moe, EnumTest.Sample.valueOf("MOE"));
        try {
            EnumTest.Sample.valueOf("non-existant");
            TestCase.fail("Expected an exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            EnumTest.Sample.valueOf(null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // May be caused by some compilers' code
        } catch (IllegalArgumentException e) {
            // other compilers will throw this
        }
        EnumTest.Sample s = Enum.valueOf(EnumTest.Sample.class, "CURLY");
        TestCase.assertSame(s, EnumTest.Sample.CURLY);
        s = Enum.valueOf(EnumTest.Sample.class, "LARRY");
        TestCase.assertSame(larry, s);
        s = Enum.valueOf(EnumTest.Sample.class, "MOE");
        TestCase.assertSame(s, moe);
        try {
            Enum.valueOf(EnumTest.Bogus.class, "MOE");
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            Enum.valueOf(((Class<EnumTest.Sample>) (null)), "a string");
            TestCase.fail("Expected an exception");
        } catch (NullPointerException e) {
            // May be caused by some compilers' code
        } catch (IllegalArgumentException e) {
            // other compilers will throw this
        }
        try {
            Enum.valueOf(EnumTest.Sample.class, null);
            TestCase.fail("Expected an exception");
        } catch (NullPointerException e) {
            // May be caused by some compilers' code
        } catch (IllegalArgumentException e) {
            // other compilers will throw this
        }
        try {
            Enum.valueOf(((Class<EnumTest.Sample>) (null)), ((String) (null)));
            TestCase.fail("Expected an exception");
        } catch (NullPointerException e) {
            // May be caused by some compilers' code
        } catch (IllegalArgumentException e) {
            // other compilers will throw this
        }
    }

    /**
     * java.lang.Enum#values
     */
    public void test_values() {
        EnumTest.Sample[] myValues = EnumTest.Sample.values();
        TestCase.assertEquals(3, myValues.length);
        TestCase.assertEquals(EnumTest.Sample.LARRY, myValues[0]);
        TestCase.assertEquals(EnumTest.Sample.MOE, myValues[1]);
        TestCase.assertEquals(EnumTest.Sample.CURLY, myValues[2]);
        TestCase.assertEquals(0, EnumTest.Empty.values().length);
    }

    /**
     * java.lang.Enum#clone()
     */
    public void test_clone() {
        try {
            EnumTest.MockCloneEnum.ONE.callClone();
            TestCase.fail("Should throw CloneNotSupprotedException");
        } catch (CloneNotSupportedException e1) {
            // expected
        }
    }

    public void test_compatibilitySerialization_inClass_Complex_Harmony() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        // TODO migrate to the new testing framework
        TestCase.assertTrue(SerializationTester.assertCompabilityEquals(new MockEnum2(), "serialization/org/apache/harmony/tests/java/lang/EnumTest.harmony.ser"));
    }

    /**
     * serialization/deserialization compatibility.
     */
    public void testSerializationSelf() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        // test a map class that has enums.
        // regression test for Harmony-1163
        HashMap<EnumTest.Color, Integer> enumColorMap = new HashMap<EnumTest.Color, Integer>();
        enumColorMap.put(EnumTest.Color.Red, 1);
        enumColorMap.put(EnumTest.Color.Blue, 3);
        Object[] testCases = new Object[]{ enumColorMap, EnumTest.Sample.CURLY };
        SerializationTest.verifySelf(testCases);
        // test a class that has enums as its fields.
        MockEnum mock = new MockEnum();
        MockEnum test = ((MockEnum) (SerializationTest.copySerializable(mock)));
        TestCase.assertEquals(mock.i, test.i);
        TestCase.assertEquals(mock.str, test.str);
        TestCase.assertEquals(mock.samEnum, test.samEnum);
        // test a class that has enums and a string of same name as its fields.
        MockEnum2 mock2 = new MockEnum2();
        MockEnum2 test2 = ((MockEnum2) (SerializationTest.copySerializable(mock2)));
        TestCase.assertEquals(mock2.i, test2.i);
        TestCase.assertEquals(mock2.str, test2.str);
        TestCase.assertEquals(mock2.samEnum, test2.samEnum);
    }

    /**
     * serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        // regression test for Harmony-1163
        HashMap<EnumTest.Color, Integer> enumColorMap = new HashMap<EnumTest.Color, Integer>();
        enumColorMap.put(EnumTest.Color.Red, 1);
        enumColorMap.put(EnumTest.Color.Blue, 3);
        Object[] testCases = new Object[]{ EnumTest.Sample.CURLY, new MockEnum(), // test a class that has enums and a string of same name as its fields.
        new MockEnum2(), // test a map class that has enums.
        enumColorMap };
        SerializationTest.verifyGolden(this, testCases);
    }
}

