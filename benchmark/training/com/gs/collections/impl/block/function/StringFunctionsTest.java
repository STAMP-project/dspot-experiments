/**
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.block.function;


import com.gs.collections.api.block.function.Function;
import com.gs.collections.impl.block.factory.StringFunctions;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


public final class StringFunctionsTest {
    @Test
    public void toUpperCase() {
        Function<String, String> function = StringFunctions.toUpperCase();
        Assert.assertEquals("UPPER", function.valueOf("upper"));
        Assert.assertEquals("UPPER", function.valueOf("Upper"));
        Assert.assertEquals("UPPER", function.valueOf("UPPER"));
        Assert.assertSame("UPPER", function.valueOf("UPPER"));
    }

    @Test
    public void toLowerCase() {
        Function<String, String> function = StringFunctions.toLowerCase();
        Assert.assertEquals("lower", function.valueOf("LOWER"));
        Assert.assertEquals("lower", function.valueOf("Lower"));
        Assert.assertEquals("lower", function.valueOf("lower"));
        Assert.assertSame("lower", function.valueOf("lower"));
    }

    @Test
    public void toInteger() {
        Assert.assertEquals((-42L), StringFunctions.toInteger().valueOf("-42").longValue());
        Verify.assertInstanceOf(Integer.class, StringFunctions.toInteger().valueOf("10"));
    }

    @Test
    public void length() {
        Function<String, Integer> function = StringFunctions.length();
        Assert.assertEquals(Integer.valueOf(6), function.valueOf("string"));
        Assert.assertEquals(Integer.valueOf(0), function.valueOf(""));
        Assert.assertEquals("string.length()", function.toString());
    }

    @Test
    public void trim() {
        Function<String, String> function = StringFunctions.trim();
        Assert.assertEquals("trim", function.valueOf("trim "));
        Assert.assertEquals("trim", function.valueOf(" trim"));
        Assert.assertEquals("trim", function.valueOf("  trim  "));
        Assert.assertEquals("trim", function.valueOf("trim"));
        Assert.assertSame("trim", function.valueOf("trim"));
        Assert.assertEquals("string.trim()", function.toString());
    }

    @Test
    public void firstLetter() {
        Function<String, Character> function = StringFunctions.firstLetter();
        Assert.assertNull(function.valueOf(null));
        Assert.assertNull(function.valueOf(""));
        Assert.assertEquals('A', function.valueOf("Autocthonic").charValue());
    }

    @Test
    public void subString() {
        Function<String, String> function1 = StringFunctions.subString(2, 5);
        String testString = "habits";
        Assert.assertEquals("bit", function1.valueOf(testString));
        Verify.assertContains("string.subString", function1.toString());
        Function<String, String> function2 = StringFunctions.subString(0, testString.length());
        Assert.assertEquals(testString, function2.valueOf(testString));
        Function<String, String> function3 = StringFunctions.subString(0, ((testString.length()) + 1));
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> function3.valueOf(testString));
        Function<String, String> function4 = StringFunctions.subString((-1), 1);
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> function4.valueOf(testString));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subString_throws_on_short_string() {
        StringFunctions.subString(2, 4).valueOf("hi");
    }

    @Test(expected = NullPointerException.class)
    public void subString_throws_on_null() {
        StringFunctions.subString(2, 4).valueOf(null);
    }

    @Test
    public void toPrimitiveBoolean() {
        Assert.assertTrue(StringFunctions.toPrimitiveBoolean().booleanValueOf("true"));
        Assert.assertFalse(StringFunctions.toPrimitiveBoolean().booleanValueOf("nah"));
    }

    @Test
    public void toPrimitiveByte() {
        Assert.assertEquals(((byte) (16)), StringFunctions.toPrimitiveByte().byteValueOf("16"));
    }

    @Test
    public void toFirstChar() {
        Assert.assertEquals('X', StringFunctions.toFirstChar().charValueOf("X-ray"));
    }

    @Test
    public void toPrimitiveChar() {
        Assert.assertEquals('A', StringFunctions.toPrimitiveChar().charValueOf("65"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void toPrimitiveCharWithEmptyString() {
        StringFunctions.toFirstChar().charValueOf("");
    }

    @Test
    public void toPrimitiveDouble() {
        Assert.assertEquals(3.14159265359, StringFunctions.toPrimitiveDouble().doubleValueOf("3.14159265359"), 0.0);
    }

    @Test
    public void toPrimitiveFloat() {
        Assert.assertEquals(3.1415, StringFunctions.toPrimitiveFloat().floatValueOf("3.1415"), 1.0E-5);
    }

    @Test
    public void toPrimitiveInt() {
        Assert.assertEquals(256, StringFunctions.toPrimitiveInt().intValueOf("256"));
    }

    @Test
    public void toPrimitiveLong() {
        Assert.assertEquals(9223372036854775807L, StringFunctions.toPrimitiveLong().longValueOf("9223372036854775807"));
    }

    @Test
    public void toPrimitiveShort() {
        Assert.assertEquals((-32768), StringFunctions.toPrimitiveShort().shortValueOf("-32768"));
    }

    @Test
    public void append() {
        Verify.assertContainsAll(FastList.newListWith("1", "2", "3", "4", "5").collect(StringFunctions.append("!")), "1!", "2!", "3!", "4!", "5!");
    }

    @Test
    public void prepend() {
        Verify.assertContainsAll(FastList.newListWith("1", "2", "3", "4", "5").collect(StringFunctions.prepend("@")), "@1", "@2", "@3", "@4", "@5");
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(StringFunctions.class);
    }
}

