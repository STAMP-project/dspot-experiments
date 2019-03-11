/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.item.file.transform;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class DefaultFieldSetTests {
    DefaultFieldSet fieldSet;

    String[] tokens;

    String[] names;

    @Test
    public void testNames() throws Exception {
        Assert.assertTrue(fieldSet.hasNames());
        Assert.assertEquals(fieldSet.getFieldCount(), fieldSet.getNames().length);
    }

    @Test
    public void testNamesNotKnown() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "foo" });
        Assert.assertFalse(fieldSet.hasNames());
        try {
            fieldSet.getNames();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testReadString() throws ParseException {
        Assert.assertEquals(fieldSet.readString(0), "TestString");
        Assert.assertEquals(fieldSet.readString("String"), "TestString");
    }

    @Test
    public void testReadChar() throws Exception {
        Assert.assertTrue(((fieldSet.readChar(2)) == 'C'));
        Assert.assertTrue(((fieldSet.readChar("Char")) == 'C'));
    }

    @Test
    public void testReadBooleanTrue() throws Exception {
        Assert.assertTrue(fieldSet.readBoolean(1));
        Assert.assertTrue(fieldSet.readBoolean("Boolean"));
    }

    @Test
    public void testReadByte() throws Exception {
        Assert.assertTrue(((fieldSet.readByte(3)) == 10));
        Assert.assertTrue(((fieldSet.readByte("Byte")) == 10));
    }

    @Test
    public void testReadShort() throws Exception {
        Assert.assertTrue(((fieldSet.readShort(4)) == (-472)));
        Assert.assertTrue(((fieldSet.readShort("Short")) == (-472)));
    }

    @Test
    public void testReadIntegerAsFloat() throws Exception {
        Assert.assertEquals(354224, fieldSet.readFloat(5), 0.001);
        Assert.assertEquals(354224, fieldSet.readFloat("Integer"), 0.001);
    }

    @Test
    public void testReadFloat() throws Exception {
        Assert.assertTrue(((fieldSet.readFloat(7)) == 124.3F));
        Assert.assertTrue(((fieldSet.readFloat("Float")) == 124.3F));
    }

    @Test
    public void testReadIntegerAsDouble() throws Exception {
        Assert.assertEquals(354224, fieldSet.readDouble(5), 0.001);
        Assert.assertEquals(354224, fieldSet.readDouble("Integer"), 0.001);
    }

    @Test
    public void testReadDouble() throws Exception {
        Assert.assertTrue(((fieldSet.readDouble(8)) == 424.3));
        Assert.assertTrue(((fieldSet.readDouble("Double")) == 424.3));
    }

    @Test
    public void testReadBigDecimal() throws Exception {
        BigDecimal bd = new BigDecimal("424.3");
        Assert.assertEquals(bd, fieldSet.readBigDecimal(8));
        Assert.assertEquals(bd, fieldSet.readBigDecimal("Double"));
    }

    @Test
    public void testReadBigBigDecimal() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "12345678901234567890" });
        BigDecimal bd = new BigDecimal("12345678901234567890");
        Assert.assertEquals(bd, fieldSet.readBigDecimal(0));
    }

    @Test
    public void testReadBigDecimalWithFormat() throws Exception {
        fieldSet.setNumberFormat(NumberFormat.getInstance(Locale.US));
        BigDecimal bd = new BigDecimal("424.3");
        Assert.assertEquals(bd, fieldSet.readBigDecimal(8));
    }

    @Test
    public void testReadBigDecimalWithEuroFormat() throws Exception {
        fieldSet.setNumberFormat(NumberFormat.getInstance(Locale.GERMANY));
        BigDecimal bd = new BigDecimal("1.3245");
        Assert.assertEquals(bd, fieldSet.readBigDecimal(9));
    }

    @Test
    public void testReadBigDecimalWithDefaultvalue() throws Exception {
        BigDecimal bd = new BigDecimal(324);
        Assert.assertEquals(bd, fieldSet.readBigDecimal(10, bd));
        Assert.assertEquals(bd, fieldSet.readBigDecimal("Null", bd));
    }

    @Test
    public void testReadNonExistentField() throws Exception {
        try {
            fieldSet.readString("something");
            Assert.fail("field set returns value even value was never put in!");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("something")) > 0));
        }
    }

    @Test
    public void testReadIndexOutOfRange() throws Exception {
        try {
            fieldSet.readShort((-1));
            Assert.fail("field set returns value even index is out of range!");
        } catch (IndexOutOfBoundsException e) {
            Assert.assertTrue(true);
        }
        try {
            fieldSet.readShort(99);
            Assert.fail("field set returns value even index is out of range!");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testReadBooleanWithTrueValue() {
        Assert.assertTrue(fieldSet.readBoolean(1, "true"));
        Assert.assertFalse(fieldSet.readBoolean(1, "incorrect trueValue"));
        Assert.assertTrue(fieldSet.readBoolean("Boolean", "true"));
        Assert.assertFalse(fieldSet.readBoolean("Boolean", "incorrect trueValue"));
    }

    @Test
    public void testReadBooleanFalse() {
        fieldSet = new DefaultFieldSet(new String[]{ "false" });
        Assert.assertFalse(fieldSet.readBoolean(0));
    }

    @Test
    public void testReadCharException() {
        try {
            fieldSet.readChar(1);
            Assert.fail("the value read was not a character, exception expected");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(true);
        }
        try {
            fieldSet.readChar("Boolean");
            Assert.fail("the value read was not a character, exception expected");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testReadInt() throws Exception {
        Assert.assertEquals(354224, fieldSet.readInt(5));
        Assert.assertEquals(354224, fieldSet.readInt("Integer"));
    }

    @Test
    public void testReadIntWithSeparator() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "354,224" });
        Assert.assertEquals(354224, fieldSet.readInt(0));
    }

    @Test
    public void testReadIntWithSeparatorAndFormat() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "354.224" });
        fieldSet.setNumberFormat(NumberFormat.getInstance(Locale.GERMAN));
        Assert.assertEquals(354224, fieldSet.readInt(0));
    }

    @Test
    public void testReadBlankInt() {
        // Trying to parse a blank field as an integer, but without a default
        // value should throw a NumberFormatException
        try {
            fieldSet.readInt(13);
            Assert.fail();
        } catch (NumberFormatException ex) {
            // expected
        }
        try {
            fieldSet.readInt("BlankInput");
            Assert.fail();
        } catch (NumberFormatException ex) {
            // expected
        }
    }

    @Test
    public void testReadLong() throws Exception {
        Assert.assertEquals(543, fieldSet.readLong(6));
        Assert.assertEquals(543, fieldSet.readLong("Long"));
    }

    @Test
    public void testReadLongWithPadding() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "000009" });
        Assert.assertEquals(9, fieldSet.readLong(0));
    }

    @Test
    public void testReadIntWithNullValue() {
        Assert.assertEquals(5, fieldSet.readInt(10, 5));
        Assert.assertEquals(5, fieldSet.readInt("Null", 5));
    }

    @Test
    public void testReadIntWithDefaultAndNotNull() throws Exception {
        Assert.assertEquals(354224, fieldSet.readInt(5, 5));
        Assert.assertEquals(354224, fieldSet.readInt("Integer", 5));
    }

    @Test
    public void testReadLongWithNullValue() {
        int defaultValue = 5;
        int indexOfNull = 10;
        int indexNotNull = 6;
        String nameNull = "Null";
        String nameNotNull = "Long";
        long longValueAtIndex = 543;
        Assert.assertEquals(fieldSet.readLong(indexOfNull, defaultValue), defaultValue);
        Assert.assertEquals(fieldSet.readLong(indexNotNull, defaultValue), longValueAtIndex);
        Assert.assertEquals(fieldSet.readLong(nameNull, defaultValue), defaultValue);
        Assert.assertEquals(fieldSet.readLong(nameNotNull, defaultValue), longValueAtIndex);
    }

    @Test
    public void testReadBigDecimalInvalid() {
        int index = 0;
        try {
            fieldSet.readBigDecimal(index);
            Assert.fail("field value is not a number, exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("TestString")) > 0));
        }
    }

    @Test
    public void testReadBigDecimalByNameInvalid() throws Exception {
        try {
            fieldSet.readBigDecimal("String");
            Assert.fail("field value is not a number, exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("TestString")) > 0));
            Assert.assertTrue(((e.getMessage().indexOf("name: [String]")) > 0));
        }
    }

    @Test
    public void testReadDate() throws Exception {
        Assert.assertNotNull(fieldSet.readDate(11));
        Assert.assertNotNull(fieldSet.readDate("Date"));
    }

    @Test
    public void testReadDateWithDefault() throws Exception {
        Date date = null;
        Assert.assertEquals(date, fieldSet.readDate(13, date));
        Assert.assertEquals(date, fieldSet.readDate("BlankInput", date));
    }

    @Test
    public void testReadDateWithFormat() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "13/01/1999" });
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fieldSet.setDateFormat(dateFormat);
        Assert.assertEquals(dateFormat.parse("13/01/1999"), fieldSet.readDate(0));
    }

    @Test
    public void testReadDateInvalid() throws Exception {
        try {
            fieldSet.readDate(0);
            Assert.fail("field value is not a date, exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("TestString")) > 0));
        }
    }

    @Test
    public void testReadDateInvalidByName() throws Exception {
        try {
            fieldSet.readDate("String");
            Assert.fail("field value is not a date, exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("name: [String]")) > 0));
        }
    }

    @Test
    public void testReadDateInvalidWithPattern() throws Exception {
        try {
            fieldSet.readDate(0, "dd-MM-yyyy");
            Assert.fail("field value is not a date, exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("dd-MM-yyyy")) > 0));
        }
    }

    @Test
    public void testReadDateWithPatternAndDefault() throws Exception {
        Date date = null;
        Assert.assertEquals(date, fieldSet.readDate(13, "dd-MM-yyyy", date));
        Assert.assertEquals(date, fieldSet.readDate("BlankInput", "dd-MM-yyyy", date));
    }

    @Test
    public void testReadDateInvalidWithDefault() throws Exception {
        Date defaultDate = new Date();
        try {
            fieldSet.readDate(1, defaultDate);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("yyyy-MM-dd")) > 0));
        }
        try {
            fieldSet.readDate("String", defaultDate);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("yyyy-MM-dd")) > 0));
            Assert.assertTrue(((e.getMessage().indexOf("name: [String]")) > 0));
        }
        try {
            fieldSet.readDate(1, "dd-MM-yyyy", defaultDate);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("dd-MM-yyyy")) > 0));
        }
        try {
            fieldSet.readDate("String", "dd-MM-yyyy", defaultDate);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("dd-MM-yyyy")) > 0));
            Assert.assertTrue(((e.getMessage().indexOf("name: [String]")) > 0));
        }
    }

    @Test
    public void testStrictReadDateWithPattern() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "50-2-13" });
        try {
            fieldSet.readDate(0, "dd-MM-yyyy");
            Assert.fail("field value is not a valid date for strict parser, exception expected");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Message did not contain: " + message), ((message.indexOf("dd-MM-yyyy")) > 0));
        }
    }

    @Test
    public void testStrictReadDateWithPatternAndStrangeDate() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "5550212" });
        try {
            System.err.println(fieldSet.readDate(0, "yyyyMMdd"));
            Assert.fail("field value is not a valid date for strict parser, exception expected");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Message did not contain: " + message), ((message.indexOf("yyyyMMdd")) > 0));
        }
    }

    @Test
    public void testReadDateByNameInvalidWithPattern() throws Exception {
        try {
            fieldSet.readDate("String", "dd-MM-yyyy");
            Assert.fail("field value is not a date, exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getMessage().indexOf("dd-MM-yyyy")) > 0));
            Assert.assertTrue(((e.getMessage().indexOf("String")) > 0));
        }
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(fieldSet, fieldSet);
        Assert.assertEquals(fieldSet, new DefaultFieldSet(tokens));
        String[] tokens1 = new String[]{ "token1" };
        String[] tokens2 = new String[]{ "token1" };
        FieldSet fs1 = new DefaultFieldSet(tokens1);
        FieldSet fs2 = new DefaultFieldSet(tokens2);
        Assert.assertEquals(fs1, fs2);
    }

    @Test
    public void testNullField() {
        Assert.assertEquals(null, fieldSet.readString(10));
    }

    @Test
    public void testEqualsNull() {
        Assert.assertFalse(fieldSet.equals(null));
    }

    @Test
    public void testEqualsNullTokens() {
        Assert.assertFalse(new DefaultFieldSet(null).equals(fieldSet));
    }

    @Test
    public void testEqualsNotEqual() throws Exception {
        String[] tokens1 = new String[]{ "token1" };
        String[] tokens2 = new String[]{ "token1", "token2" };
        FieldSet fs1 = new DefaultFieldSet(tokens1);
        FieldSet fs2 = new DefaultFieldSet(tokens2);
        Assert.assertFalse(fs1.equals(fs2));
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(fieldSet.hashCode(), new DefaultFieldSet(tokens).hashCode());
    }

    @Test
    public void testHashCodeWithNullTokens() throws Exception {
        Assert.assertEquals(0, new DefaultFieldSet(null).hashCode());
    }

    @Test
    public void testConstructor() throws Exception {
        try {
            new DefaultFieldSet(new String[]{ "1", "2" }, new String[]{ "a" });
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testToStringWithNames() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "foo", "bar" }, new String[]{ "Foo", "Bar" });
        Assert.assertTrue(((fieldSet.toString().indexOf("Foo=foo")) >= 0));
    }

    @Test
    public void testToStringWithoutNames() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ "foo", "bar" });
        Assert.assertTrue(((fieldSet.toString().indexOf("foo")) >= 0));
    }

    @Test
    public void testToStringNullTokens() throws Exception {
        fieldSet = new DefaultFieldSet(null);
        Assert.assertEquals("", fieldSet.toString());
    }

    @Test
    public void testProperties() throws Exception {
        Assert.assertEquals("foo", new DefaultFieldSet(new String[]{ "foo", "bar" }, new String[]{ "Foo", "Bar" }).getProperties().getProperty("Foo"));
    }

    @Test
    public void testPropertiesWithNoNames() throws Exception {
        try {
            new DefaultFieldSet(new String[]{ "foo", "bar" }).getProperties();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testPropertiesWithWhiteSpace() throws Exception {
        Assert.assertEquals("bar", new DefaultFieldSet(new String[]{ "foo", "bar   " }, new String[]{ "Foo", "Bar" }).getProperties().getProperty("Bar"));
    }

    @Test
    public void testPropertiesWithNullValues() throws Exception {
        fieldSet = new DefaultFieldSet(new String[]{ null, "bar" }, new String[]{ "Foo", "Bar" });
        Assert.assertEquals("bar", fieldSet.getProperties().getProperty("Bar"));
        Assert.assertEquals(null, fieldSet.getProperties().getProperty("Foo"));
    }

    @Test
    public void testAccessByNameWhenNamesMissing() throws Exception {
        try {
            new DefaultFieldSet(new String[]{ "1", "2" }).readInt("a");
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetValues() {
        String[] values = fieldSet.getValues();
        Assert.assertEquals(tokens.length, values.length);
        for (int i = 0; i < (tokens.length); i++) {
            Assert.assertEquals(tokens[i], values[i]);
        }
    }

    @Test
    public void testPaddedLong() {
        FieldSet fs = new DefaultFieldSet(new String[]{ "00000009" });
        long value = fs.readLong(0);
        Assert.assertEquals(value, 9);
    }

    @Test
    public void testReadRawString() {
        String name = "fieldName";
        String value = " string with trailing whitespace   ";
        FieldSet fs = new DefaultFieldSet(new String[]{ value }, new String[]{ name });
        Assert.assertEquals(value, fs.readRawString(0));
        Assert.assertEquals(value, fs.readRawString(name));
    }
}

