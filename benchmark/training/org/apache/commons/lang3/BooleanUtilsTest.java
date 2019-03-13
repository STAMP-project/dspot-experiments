/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.BooleanUtils}.
 */
public class BooleanUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new BooleanUtils());
        final Constructor<?>[] cons = BooleanUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(BooleanUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(BooleanUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_negate_Boolean() {
        Assertions.assertSame(null, BooleanUtils.negate(null));
        Assertions.assertSame(Boolean.TRUE, BooleanUtils.negate(Boolean.FALSE));
        Assertions.assertSame(Boolean.FALSE, BooleanUtils.negate(Boolean.TRUE));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_isTrue_Boolean() {
        Assertions.assertTrue(BooleanUtils.isTrue(Boolean.TRUE));
        Assertions.assertFalse(BooleanUtils.isTrue(Boolean.FALSE));
        Assertions.assertFalse(BooleanUtils.isTrue(null));
    }

    @Test
    public void test_isNotTrue_Boolean() {
        Assertions.assertFalse(BooleanUtils.isNotTrue(Boolean.TRUE));
        Assertions.assertTrue(BooleanUtils.isNotTrue(Boolean.FALSE));
        Assertions.assertTrue(BooleanUtils.isNotTrue(null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_isFalse_Boolean() {
        Assertions.assertFalse(BooleanUtils.isFalse(Boolean.TRUE));
        Assertions.assertTrue(BooleanUtils.isFalse(Boolean.FALSE));
        Assertions.assertFalse(BooleanUtils.isFalse(null));
    }

    @Test
    public void test_isNotFalse_Boolean() {
        Assertions.assertTrue(BooleanUtils.isNotFalse(Boolean.TRUE));
        Assertions.assertFalse(BooleanUtils.isNotFalse(Boolean.FALSE));
        Assertions.assertTrue(BooleanUtils.isNotFalse(null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toBoolean_Boolean() {
        Assertions.assertTrue(BooleanUtils.toBoolean(Boolean.TRUE));
        Assertions.assertFalse(BooleanUtils.toBoolean(Boolean.FALSE));
        Assertions.assertFalse(BooleanUtils.toBoolean(((Boolean) (null))));
    }

    @Test
    public void test_toBooleanDefaultIfNull_Boolean_boolean() {
        Assertions.assertTrue(BooleanUtils.toBooleanDefaultIfNull(Boolean.TRUE, true));
        Assertions.assertTrue(BooleanUtils.toBooleanDefaultIfNull(Boolean.TRUE, false));
        Assertions.assertFalse(BooleanUtils.toBooleanDefaultIfNull(Boolean.FALSE, true));
        Assertions.assertFalse(BooleanUtils.toBooleanDefaultIfNull(Boolean.FALSE, false));
        Assertions.assertTrue(BooleanUtils.toBooleanDefaultIfNull(null, true));
        Assertions.assertFalse(BooleanUtils.toBooleanDefaultIfNull(null, false));
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_toBoolean_int() {
        Assertions.assertTrue(BooleanUtils.toBoolean(1));
        Assertions.assertTrue(BooleanUtils.toBoolean((-1)));
        Assertions.assertFalse(BooleanUtils.toBoolean(0));
    }

    @Test
    public void test_toBooleanObject_int() {
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject(1));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject((-1)));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject(0));
    }

    @Test
    public void test_toBooleanObject_Integer() {
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject(Integer.valueOf(1)));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject(Integer.valueOf((-1))));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject(Integer.valueOf(0)));
        Assertions.assertNull(BooleanUtils.toBooleanObject(((Integer) (null))));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toBoolean_int_int_int() {
        Assertions.assertTrue(BooleanUtils.toBoolean(6, 6, 7));
        Assertions.assertFalse(BooleanUtils.toBoolean(7, 6, 7));
    }

    @Test
    public void test_toBoolean_int_int_int_noMatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBoolean(8, 6, 7));
    }

    @Test
    public void test_toBoolean_Integer_Integer_Integer() {
        final Integer six = Integer.valueOf(6);
        final Integer seven = Integer.valueOf(7);
        Assertions.assertTrue(BooleanUtils.toBoolean(null, null, seven));
        Assertions.assertFalse(BooleanUtils.toBoolean(null, six, null));
        Assertions.assertTrue(BooleanUtils.toBoolean(Integer.valueOf(6), six, seven));
        Assertions.assertFalse(BooleanUtils.toBoolean(Integer.valueOf(7), six, seven));
    }

    @Test
    public void test_toBoolean_Integer_Integer_Integer_nullValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBoolean(null, Integer.valueOf(6), Integer.valueOf(7)));
    }

    @Test
    public void test_toBoolean_Integer_Integer_Integer_noMatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBoolean(Integer.valueOf(8), Integer.valueOf(6), Integer.valueOf(7)));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toBooleanObject_int_int_int() {
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject(6, 6, 7, 8));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject(7, 6, 7, 8));
        Assertions.assertNull(BooleanUtils.toBooleanObject(8, 6, 7, 8));
    }

    @Test
    public void test_toBooleanObject_int_int_int_noMatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBooleanObject(9, 6, 7, 8));
    }

    @Test
    public void test_toBooleanObject_Integer_Integer_Integer_Integer() {
        final Integer six = Integer.valueOf(6);
        final Integer seven = Integer.valueOf(7);
        final Integer eight = Integer.valueOf(8);
        Assertions.assertSame(Boolean.TRUE, BooleanUtils.toBooleanObject(null, null, seven, eight));
        Assertions.assertSame(Boolean.FALSE, BooleanUtils.toBooleanObject(null, six, null, eight));
        Assertions.assertSame(null, BooleanUtils.toBooleanObject(null, six, seven, null));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject(Integer.valueOf(6), six, seven, eight));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject(Integer.valueOf(7), six, seven, eight));
        Assertions.assertNull(BooleanUtils.toBooleanObject(Integer.valueOf(8), six, seven, eight));
    }

    @Test
    public void test_toBooleanObject_Integer_Integer_Integer_Integer_nullValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBooleanObject(null, Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8)));
    }

    @Test
    public void test_toBooleanObject_Integer_Integer_Integer_Integer_noMatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBooleanObject(Integer.valueOf(9), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8)));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toInteger_boolean() {
        Assertions.assertEquals(1, BooleanUtils.toInteger(true));
        Assertions.assertEquals(0, BooleanUtils.toInteger(false));
    }

    @Test
    public void test_toIntegerObject_boolean() {
        Assertions.assertEquals(Integer.valueOf(1), BooleanUtils.toIntegerObject(true));
        Assertions.assertEquals(Integer.valueOf(0), BooleanUtils.toIntegerObject(false));
    }

    @Test
    public void test_toIntegerObject_Boolean() {
        Assertions.assertEquals(Integer.valueOf(1), BooleanUtils.toIntegerObject(Boolean.TRUE));
        Assertions.assertEquals(Integer.valueOf(0), BooleanUtils.toIntegerObject(Boolean.FALSE));
        Assertions.assertNull(BooleanUtils.toIntegerObject(null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toInteger_boolean_int_int() {
        Assertions.assertEquals(6, BooleanUtils.toInteger(true, 6, 7));
        Assertions.assertEquals(7, BooleanUtils.toInteger(false, 6, 7));
    }

    @Test
    public void test_toInteger_Boolean_int_int_int() {
        Assertions.assertEquals(6, BooleanUtils.toInteger(Boolean.TRUE, 6, 7, 8));
        Assertions.assertEquals(7, BooleanUtils.toInteger(Boolean.FALSE, 6, 7, 8));
        Assertions.assertEquals(8, BooleanUtils.toInteger(null, 6, 7, 8));
    }

    @Test
    public void test_toIntegerObject_boolean_Integer_Integer() {
        final Integer six = Integer.valueOf(6);
        final Integer seven = Integer.valueOf(7);
        Assertions.assertEquals(six, BooleanUtils.toIntegerObject(true, six, seven));
        Assertions.assertEquals(seven, BooleanUtils.toIntegerObject(false, six, seven));
    }

    @Test
    public void test_toIntegerObject_Boolean_Integer_Integer_Integer() {
        final Integer six = Integer.valueOf(6);
        final Integer seven = Integer.valueOf(7);
        final Integer eight = Integer.valueOf(8);
        Assertions.assertEquals(six, BooleanUtils.toIntegerObject(Boolean.TRUE, six, seven, eight));
        Assertions.assertEquals(seven, BooleanUtils.toIntegerObject(Boolean.FALSE, six, seven, eight));
        Assertions.assertEquals(eight, BooleanUtils.toIntegerObject(null, six, seven, eight));
        Assertions.assertNull(BooleanUtils.toIntegerObject(null, six, seven, null));
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_toBooleanObject_String() {
        Assertions.assertNull(BooleanUtils.toBooleanObject(((String) (null))));
        Assertions.assertNull(BooleanUtils.toBooleanObject(""));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("false"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("no"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("off"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("FALSE"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("NO"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("OFF"));
        Assertions.assertNull(BooleanUtils.toBooleanObject("oof"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("true"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("yes"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("on"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("TRUE"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("ON"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("YES"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("TruE"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("TruE"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("y"));// yes

        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("Y"));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("t"));// true

        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("T"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("f"));// false

        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("F"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("n"));// No

        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("N"));
        Assertions.assertNull(BooleanUtils.toBooleanObject("z"));
        Assertions.assertNull(BooleanUtils.toBooleanObject("ab"));
        Assertions.assertNull(BooleanUtils.toBooleanObject("yoo"));
        Assertions.assertNull(BooleanUtils.toBooleanObject("true "));
        Assertions.assertNull(BooleanUtils.toBooleanObject("ono"));
    }

    @Test
    public void test_toBooleanObject_String_String_String_String() {
        Assertions.assertSame(Boolean.TRUE, BooleanUtils.toBooleanObject(null, null, "N", "U"));
        Assertions.assertSame(Boolean.FALSE, BooleanUtils.toBooleanObject(null, "Y", null, "U"));
        Assertions.assertSame(null, BooleanUtils.toBooleanObject(null, "Y", "N", null));
        Assertions.assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("Y", "Y", "N", "U"));
        Assertions.assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("N", "Y", "N", "U"));
        Assertions.assertNull(BooleanUtils.toBooleanObject("U", "Y", "N", "U"));
    }

    @Test
    public void test_toBooleanObject_String_String_String_String_nullValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBooleanObject(null, "Y", "N", "U"));
    }

    @Test
    public void test_toBooleanObject_String_String_String_String_noMatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBooleanObject("X", "Y", "N", "U"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toBoolean_String() {
        Assertions.assertFalse(BooleanUtils.toBoolean(((String) (null))));
        Assertions.assertFalse(BooleanUtils.toBoolean(""));
        Assertions.assertFalse(BooleanUtils.toBoolean("off"));
        Assertions.assertFalse(BooleanUtils.toBoolean("oof"));
        Assertions.assertFalse(BooleanUtils.toBoolean("yep"));
        Assertions.assertFalse(BooleanUtils.toBoolean("trux"));
        Assertions.assertFalse(BooleanUtils.toBoolean("false"));
        Assertions.assertFalse(BooleanUtils.toBoolean("a"));
        Assertions.assertTrue(BooleanUtils.toBoolean("true"));// interned handled differently

        Assertions.assertTrue(BooleanUtils.toBoolean(new StringBuilder("tr").append("ue").toString()));
        Assertions.assertTrue(BooleanUtils.toBoolean("truE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("trUe"));
        Assertions.assertTrue(BooleanUtils.toBoolean("trUE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("tRue"));
        Assertions.assertTrue(BooleanUtils.toBoolean("tRuE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("tRUe"));
        Assertions.assertTrue(BooleanUtils.toBoolean("tRUE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TRUE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TRUe"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TRuE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TRue"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TrUE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TrUe"));
        Assertions.assertTrue(BooleanUtils.toBoolean("TruE"));
        Assertions.assertTrue(BooleanUtils.toBoolean("True"));
        Assertions.assertTrue(BooleanUtils.toBoolean("on"));
        Assertions.assertTrue(BooleanUtils.toBoolean("oN"));
        Assertions.assertTrue(BooleanUtils.toBoolean("On"));
        Assertions.assertTrue(BooleanUtils.toBoolean("ON"));
        Assertions.assertTrue(BooleanUtils.toBoolean("yes"));
        Assertions.assertTrue(BooleanUtils.toBoolean("yeS"));
        Assertions.assertTrue(BooleanUtils.toBoolean("yEs"));
        Assertions.assertTrue(BooleanUtils.toBoolean("yES"));
        Assertions.assertTrue(BooleanUtils.toBoolean("Yes"));
        Assertions.assertTrue(BooleanUtils.toBoolean("YeS"));
        Assertions.assertTrue(BooleanUtils.toBoolean("YEs"));
        Assertions.assertTrue(BooleanUtils.toBoolean("YES"));
        Assertions.assertFalse(BooleanUtils.toBoolean("yes?"));
        Assertions.assertFalse(BooleanUtils.toBoolean("tru"));
        Assertions.assertFalse(BooleanUtils.toBoolean("no"));
        Assertions.assertFalse(BooleanUtils.toBoolean("off"));
        Assertions.assertFalse(BooleanUtils.toBoolean("yoo"));
    }

    @Test
    public void test_toBoolean_String_String_String() {
        Assertions.assertTrue(BooleanUtils.toBoolean(null, null, "N"));
        Assertions.assertFalse(BooleanUtils.toBoolean(null, "Y", null));
        Assertions.assertTrue(BooleanUtils.toBoolean("Y", "Y", "N"));
        Assertions.assertTrue(BooleanUtils.toBoolean("Y", new String("Y"), new String("N")));
        Assertions.assertFalse(BooleanUtils.toBoolean("N", "Y", "N"));
        Assertions.assertFalse(BooleanUtils.toBoolean("N", new String("Y"), new String("N")));
        Assertions.assertTrue(BooleanUtils.toBoolean(((String) (null)), null, null));
        Assertions.assertTrue(BooleanUtils.toBoolean("Y", "Y", "Y"));
        Assertions.assertTrue(BooleanUtils.toBoolean("Y", new String("Y"), new String("Y")));
    }

    @Test
    public void test_toBoolean_String_String_String_nullValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBoolean(null, "Y", "N"));
    }

    @Test
    public void test_toBoolean_String_String_String_noMatch() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.toBoolean("X", "Y", "N"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toStringTrueFalse_Boolean() {
        Assertions.assertNull(BooleanUtils.toStringTrueFalse(null));
        Assertions.assertEquals("true", BooleanUtils.toStringTrueFalse(Boolean.TRUE));
        Assertions.assertEquals("false", BooleanUtils.toStringTrueFalse(Boolean.FALSE));
    }

    @Test
    public void test_toStringOnOff_Boolean() {
        Assertions.assertNull(BooleanUtils.toStringOnOff(null));
        Assertions.assertEquals("on", BooleanUtils.toStringOnOff(Boolean.TRUE));
        Assertions.assertEquals("off", BooleanUtils.toStringOnOff(Boolean.FALSE));
    }

    @Test
    public void test_toStringYesNo_Boolean() {
        Assertions.assertNull(BooleanUtils.toStringYesNo(null));
        Assertions.assertEquals("yes", BooleanUtils.toStringYesNo(Boolean.TRUE));
        Assertions.assertEquals("no", BooleanUtils.toStringYesNo(Boolean.FALSE));
    }

    @Test
    public void test_toString_Boolean_String_String_String() {
        Assertions.assertEquals("U", BooleanUtils.toString(null, "Y", "N", "U"));
        Assertions.assertEquals("Y", BooleanUtils.toString(Boolean.TRUE, "Y", "N", "U"));
        Assertions.assertEquals("N", BooleanUtils.toString(Boolean.FALSE, "Y", "N", "U"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_toStringTrueFalse_boolean() {
        Assertions.assertEquals("true", BooleanUtils.toStringTrueFalse(true));
        Assertions.assertEquals("false", BooleanUtils.toStringTrueFalse(false));
    }

    @Test
    public void test_toStringOnOff_boolean() {
        Assertions.assertEquals("on", BooleanUtils.toStringOnOff(true));
        Assertions.assertEquals("off", BooleanUtils.toStringOnOff(false));
    }

    @Test
    public void test_toStringYesNo_boolean() {
        Assertions.assertEquals("yes", BooleanUtils.toStringYesNo(true));
        Assertions.assertEquals("no", BooleanUtils.toStringYesNo(false));
    }

    @Test
    public void test_toString_boolean_String_String_String() {
        Assertions.assertEquals("Y", BooleanUtils.toString(true, "Y", "N"));
        Assertions.assertEquals("N", BooleanUtils.toString(false, "Y", "N"));
    }

    // testXor
    // -----------------------------------------------------------------------
    @Test
    public void testXor_primitive_nullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.xor(((boolean[]) (null))));
    }

    @Test
    public void testXor_primitive_emptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.xor(new boolean[]{  }));
    }

    @Test
    public void testXor_primitive_validInput_2items() {
        Assertions.assertEquals((true ^ true), BooleanUtils.xor(new boolean[]{ true, true }), "true ^ true");
        Assertions.assertEquals((false ^ false), BooleanUtils.xor(new boolean[]{ false, false }), "false ^ false");
        Assertions.assertEquals((true ^ false), BooleanUtils.xor(new boolean[]{ true, false }), "true ^ false");
        Assertions.assertEquals((false ^ true), BooleanUtils.xor(new boolean[]{ false, true }), "false ^ true");
    }

    @Test
    public void testXor_primitive_validInput_3items() {
        Assertions.assertEquals(((false ^ false) ^ false), BooleanUtils.xor(new boolean[]{ false, false, false }), "false ^ false ^ false");
        Assertions.assertEquals(((false ^ false) ^ true), BooleanUtils.xor(new boolean[]{ false, false, true }), "false ^ false ^ true");
        Assertions.assertEquals(((false ^ true) ^ false), BooleanUtils.xor(new boolean[]{ false, true, false }), "false ^ true ^ false");
        Assertions.assertEquals(((false ^ true) ^ true), BooleanUtils.xor(new boolean[]{ false, true, true }), "false ^ true ^ true");
        Assertions.assertEquals(((true ^ false) ^ false), BooleanUtils.xor(new boolean[]{ true, false, false }), "true ^ false ^ false");
        Assertions.assertEquals(((true ^ false) ^ true), BooleanUtils.xor(new boolean[]{ true, false, true }), "true ^ false ^ true");
        Assertions.assertEquals(((true ^ true) ^ false), BooleanUtils.xor(new boolean[]{ true, true, false }), "true ^ true ^ false");
        Assertions.assertEquals(((true ^ true) ^ true), BooleanUtils.xor(new boolean[]{ true, true, true }), "true ^ true ^ true");
    }

    @Test
    public void testXor_object_nullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.xor(((Boolean[]) (null))));
    }

    @Test
    public void testXor_object_emptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.xor(new Boolean[]{  }));
    }

    @Test
    public void testXor_object_nullElementInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.xor(new Boolean[]{ null }));
    }

    @Test
    public void testXor_object_validInput_2items() {
        Assertions.assertEquals((false ^ false), BooleanUtils.xor(new Boolean[]{ Boolean.FALSE, Boolean.FALSE }).booleanValue(), "false ^ false");
        Assertions.assertEquals((false ^ true), BooleanUtils.xor(new Boolean[]{ Boolean.FALSE, Boolean.TRUE }).booleanValue(), "false ^ true");
        Assertions.assertEquals((true ^ false), BooleanUtils.xor(new Boolean[]{ Boolean.TRUE, Boolean.FALSE }).booleanValue(), "true ^ false");
        Assertions.assertEquals((true ^ true), BooleanUtils.xor(new Boolean[]{ Boolean.TRUE, Boolean.TRUE }).booleanValue(), "true ^ true");
    }

    @Test
    public void testXor_object_validInput_3items() {
        Assertions.assertEquals(((false ^ false) ^ false), BooleanUtils.xor(new Boolean[]{ Boolean.FALSE, Boolean.FALSE, Boolean.FALSE }).booleanValue(), "false ^ false ^ false");
        Assertions.assertEquals(((false ^ false) ^ true), BooleanUtils.xor(new Boolean[]{ Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }).booleanValue(), "false ^ false ^ true");
        Assertions.assertEquals(((false ^ true) ^ false), BooleanUtils.xor(new Boolean[]{ Boolean.FALSE, Boolean.TRUE, Boolean.FALSE }).booleanValue(), "false ^ true ^ false");
        Assertions.assertEquals(((true ^ false) ^ false), BooleanUtils.xor(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }).booleanValue(), "true ^ false ^ false");
        Assertions.assertEquals(((true ^ false) ^ true), BooleanUtils.xor(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }).booleanValue(), "true ^ false ^ true");
        Assertions.assertEquals(((true ^ true) ^ false), BooleanUtils.xor(new Boolean[]{ Boolean.TRUE, Boolean.TRUE, Boolean.FALSE }).booleanValue(), "true ^ true ^ false");
        Assertions.assertEquals(((false ^ true) ^ true), BooleanUtils.xor(new Boolean[]{ Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }).booleanValue(), "false ^ true ^ true");
        Assertions.assertEquals(((true ^ true) ^ true), BooleanUtils.xor(new Boolean[]{ Boolean.TRUE, Boolean.TRUE, Boolean.TRUE }).booleanValue(), "true ^ true ^ true");
    }

    // testAnd
    // -----------------------------------------------------------------------
    @Test
    public void testAnd_primitive_nullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.and(((boolean[]) (null))));
    }

    @Test
    public void testAnd_primitive_emptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.and(new boolean[]{  }));
    }

    @Test
    public void testAnd_primitive_validInput_2items() {
        Assertions.assertTrue(BooleanUtils.and(new boolean[]{ true, true }), "False result for (true, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ false, false }))), "True result for (false, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ true, false }))), "True result for (true, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ false, true }))), "True result for (false, true)");
    }

    @Test
    public void testAnd_primitive_validInput_3items() {
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ false, false, true }))), "True result for (false, false, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ false, true, false }))), "True result for (false, true, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ true, false, false }))), "True result for (true, false, false)");
        Assertions.assertTrue(BooleanUtils.and(new boolean[]{ true, true, true }), "False result for (true, true, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ false, false, false }))), "True result for (false, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ true, true, false }))), "True result for (true, true, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ true, false, true }))), "True result for (true, false, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new boolean[]{ false, true, true }))), "True result for (false, true, true)");
    }

    @Test
    public void testAnd_object_nullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.and(((Boolean[]) (null))));
    }

    @Test
    public void testAnd_object_emptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.and(new Boolean[]{  }));
    }

    @Test
    public void testAnd_object_nullElementInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.and(new Boolean[]{ null }));
    }

    @Test
    public void testAnd_object_validInput_2items() {
        Assertions.assertTrue(BooleanUtils.and(new Boolean[]{ Boolean.TRUE, Boolean.TRUE }).booleanValue(), "False result for (true, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.FALSE, Boolean.FALSE }).booleanValue())), "True result for (false, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.TRUE, Boolean.FALSE }).booleanValue())), "True result for (true, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.FALSE, Boolean.TRUE }).booleanValue())), "True result for (false, true)");
    }

    @Test
    public void testAnd_object_validInput_3items() {
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }).booleanValue())), "True result for (false, false, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.FALSE, Boolean.TRUE, Boolean.FALSE }).booleanValue())), "True result for (false, true, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }).booleanValue())), "True result for (true, false, false)");
        Assertions.assertTrue(BooleanUtils.and(new Boolean[]{ Boolean.TRUE, Boolean.TRUE, Boolean.TRUE }).booleanValue(), "False result for (true, true, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.FALSE, Boolean.FALSE, Boolean.FALSE }).booleanValue())), "True result for (false, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.TRUE, Boolean.TRUE, Boolean.FALSE }).booleanValue())), "True result for (true, true, false)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }).booleanValue())), "True result for (true, false, true)");
        Assertions.assertTrue((!(BooleanUtils.and(new Boolean[]{ Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }).booleanValue())), "True result for (false, true, true)");
    }

    // testOr
    // -----------------------------------------------------------------------
    @Test
    public void testOr_primitive_nullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.or(((boolean[]) (null))));
    }

    @Test
    public void testOr_primitive_emptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.or(new boolean[]{  }));
    }

    @Test
    public void testOr_primitive_validInput_2items() {
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ true, true }), "False result for (true, true)");
        Assertions.assertTrue((!(BooleanUtils.or(new boolean[]{ false, false }))), "True result for (false, false)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ true, false }), "False result for (true, false)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ false, true }), "False result for (false, true)");
    }

    @Test
    public void testOr_primitive_validInput_3items() {
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ false, false, true }), "False result for (false, false, true)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ false, true, false }), "False result for (false, true, false)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ true, false, false }), "False result for (true, false, false)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ true, true, true }), "False result for (true, true, true)");
        Assertions.assertTrue((!(BooleanUtils.or(new boolean[]{ false, false, false }))), "True result for (false, false)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ true, true, false }), "False result for (true, true, false)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ true, false, true }), "False result for (true, false, true)");
        Assertions.assertTrue(BooleanUtils.or(new boolean[]{ false, true, true }), "False result for (false, true, true)");
    }

    @Test
    public void testOr_object_nullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.or(((Boolean[]) (null))));
    }

    @Test
    public void testOr_object_emptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.or(new Boolean[]{  }));
    }

    @Test
    public void testOr_object_nullElementInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BooleanUtils.or(new Boolean[]{ null }));
    }

    @Test
    public void testOr_object_validInput_2items() {
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.TRUE, Boolean.TRUE }).booleanValue(), "False result for (true, true)");
        Assertions.assertTrue((!(BooleanUtils.or(new Boolean[]{ Boolean.FALSE, Boolean.FALSE }).booleanValue())), "True result for (false, false)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.TRUE, Boolean.FALSE }).booleanValue(), "False result for (true, false)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.FALSE, Boolean.TRUE }).booleanValue(), "False result for (false, true)");
    }

    @Test
    public void testOr_object_validInput_3items() {
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }).booleanValue(), "False result for (false, false, true)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.FALSE, Boolean.TRUE, Boolean.FALSE }).booleanValue(), "False result for (false, true, false)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }).booleanValue(), "False result for (true, false, false)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.TRUE, Boolean.TRUE, Boolean.TRUE }).booleanValue(), "False result for (true, true, true)");
        Assertions.assertTrue((!(BooleanUtils.or(new Boolean[]{ Boolean.FALSE, Boolean.FALSE, Boolean.FALSE }).booleanValue())), "True result for (false, false)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.TRUE, Boolean.TRUE, Boolean.FALSE }).booleanValue(), "False result for (true, true, false)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.TRUE, Boolean.FALSE, Boolean.TRUE }).booleanValue(), "False result for (true, false, true)");
        Assertions.assertTrue(BooleanUtils.or(new Boolean[]{ Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }).booleanValue(), "False result for (false, true, true)");
    }

    @Test
    public void testCompare() {
        Assertions.assertTrue(((BooleanUtils.compare(true, false)) > 0));
        Assertions.assertEquals(0, BooleanUtils.compare(true, true));
        Assertions.assertEquals(0, BooleanUtils.compare(false, false));
        Assertions.assertTrue(((BooleanUtils.compare(false, true)) < 0));
    }
}

