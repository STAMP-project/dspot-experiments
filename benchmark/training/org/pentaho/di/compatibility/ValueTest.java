/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.compatibility;


import Value.VALUE_TYPE_BIGNUMBER;
import Value.VALUE_TYPE_BINARY;
import Value.VALUE_TYPE_BOOLEAN;
import Value.VALUE_TYPE_DATE;
import Value.VALUE_TYPE_INTEGER;
import Value.VALUE_TYPE_NONE;
import Value.VALUE_TYPE_NUMBER;
import Value.VALUE_TYPE_SERIALIZABLE;
import Value.VALUE_TYPE_STRING;
import ValueMetaInterface.TYPE_BIGNUMBER;
import ValueMetaInterface.TYPE_BINARY;
import ValueMetaInterface.TYPE_BOOLEAN;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NONE;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_SERIALIZABLE;
import ValueMetaInterface.TYPE_STRING;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;

import static Value.VALUE_TYPE_BIGNUMBER;
import static Value.VALUE_TYPE_BOOLEAN;
import static Value.VALUE_TYPE_DATE;
import static Value.VALUE_TYPE_INTEGER;
import static Value.VALUE_TYPE_NONE;
import static Value.VALUE_TYPE_NUMBER;
import static Value.VALUE_TYPE_STRING;


/**
 * Test class for the basic functionality of Value.
 *
 * @author Sven Boden
 */
public class ValueTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * Constructor test 1.
     */
    @Test
    public void testConstructor1() {
        Value vs = new Value();
        // Set by clearValue()
        Assert.assertFalse(vs.isNull());// historical probably

        Assert.assertTrue(vs.isEmpty());// historical probably

        Assert.assertNull(vs.getName());
        Assert.assertNull(vs.getOrigin());
        Assert.assertEquals(VALUE_TYPE_NONE, vs.getType());
        Assert.assertFalse(vs.isString());
        Assert.assertFalse(vs.isDate());
        Assert.assertFalse(vs.isNumeric());
        Assert.assertFalse(vs.isInteger());
        Assert.assertFalse(vs.isBigNumber());
        Assert.assertFalse(vs.isNumber());
        Assert.assertFalse(vs.isBoolean());
        Value vs1 = new Value("Name");
        // Set by clearValue()
        Assert.assertFalse(vs1.isNull());// historical probably

        Assert.assertTrue(vs1.isEmpty());// historical probably

        Assert.assertEquals("Name", vs1.getName());
        Assert.assertNull(vs1.getOrigin());
        Assert.assertEquals(VALUE_TYPE_NONE, vs1.getType());
    }

    /**
     * Constructor test 2.
     */
    @Test
    public void testConstructor2() {
        Value vs = new Value("Name", VALUE_TYPE_NUMBER);
        Assert.assertFalse(vs.isNull());
        Assert.assertFalse(vs.isEmpty());
        Assert.assertEquals("Name", vs.getName());
        Assert.assertEquals(VALUE_TYPE_NUMBER, vs.getType());
        Assert.assertTrue(vs.isNumber());
        Assert.assertTrue(vs.isNumeric());
        Value vs1 = new Value("Name", VALUE_TYPE_STRING);
        Assert.assertFalse(vs1.isNull());
        Assert.assertFalse(vs1.isEmpty());
        Assert.assertEquals("Name", vs1.getName());
        Assert.assertEquals(VALUE_TYPE_STRING, vs1.getType());
        Assert.assertTrue(vs1.isString());
        Value vs2 = new Value("Name", VALUE_TYPE_DATE);
        Assert.assertFalse(vs2.isNull());
        Assert.assertFalse(vs2.isEmpty());
        Assert.assertEquals("Name", vs2.getName());
        Assert.assertEquals(VALUE_TYPE_DATE, vs2.getType());
        Assert.assertTrue(vs2.isDate());
        Value vs3 = new Value("Name", VALUE_TYPE_BOOLEAN);
        Assert.assertFalse(vs3.isNull());
        Assert.assertFalse(vs3.isEmpty());
        Assert.assertEquals("Name", vs3.getName());
        Assert.assertEquals(VALUE_TYPE_BOOLEAN, vs3.getType());
        Assert.assertTrue(vs3.isBoolean());
        Value vs4 = new Value("Name", VALUE_TYPE_INTEGER);
        Assert.assertFalse(vs4.isNull());
        Assert.assertFalse(vs4.isEmpty());
        Assert.assertEquals("Name", vs4.getName());
        Assert.assertEquals(VALUE_TYPE_INTEGER, vs4.getType());
        Assert.assertTrue(vs4.isInteger());
        Assert.assertTrue(vs4.isNumeric());
        Value vs5 = new Value("Name", VALUE_TYPE_BIGNUMBER);
        Assert.assertFalse(vs5.isNull());
        Assert.assertFalse(vs5.isEmpty());
        Assert.assertEquals("Name", vs5.getName());
        Assert.assertEquals(VALUE_TYPE_BIGNUMBER, vs5.getType());
        Assert.assertTrue(vs5.isBigNumber());
        Assert.assertTrue(vs5.isNumeric());
        Value vs6 = new Value("Name", 1000000);
        Assert.assertEquals(VALUE_TYPE_NONE, vs6.getType());
    }

    /**
     * Constructors using Values
     */
    @Test
    public void testConstructor3() {
        Value vs = new Value("Name", VALUE_TYPE_NUMBER);
        vs.setValue(10.0);
        vs.setOrigin("origin");
        vs.setLength(4, 2);
        Value copy = new Value(vs);
        Assert.assertEquals(vs.getType(), copy.getType());
        Assert.assertEquals(vs.getNumber(), copy.getNumber(), 0.1);
        Assert.assertEquals(vs.getLength(), copy.getLength());
        Assert.assertEquals(vs.getPrecision(), copy.getPrecision());
        Assert.assertEquals(vs.isNull(), copy.isNull());
        Assert.assertEquals(vs.getOrigin(), copy.getOrigin());
        Assert.assertEquals(vs.getName(), copy.getName());
        // Show it's a deep copy
        copy.setName("newName");
        Assert.assertEquals("Name", vs.getName());
        Assert.assertEquals("newName", copy.getName());
        copy.setOrigin("newOrigin");
        Assert.assertEquals("origin", vs.getOrigin());
        Assert.assertEquals("newOrigin", copy.getOrigin());
        copy.setValue(11.0);
        Assert.assertEquals(10.0, vs.getNumber(), 0.1);
        Assert.assertEquals(11.0, copy.getNumber(), 0.1);
        Value vs1 = new Value("Name", VALUE_TYPE_NUMBER);
        vs1.setName(null);
        // name and origin are null
        Value copy1 = new Value(vs1);
        Assert.assertEquals(vs1.getType(), copy1.getType());
        Assert.assertEquals(vs1.getNumber(), copy1.getNumber(), 0.1);
        Assert.assertEquals(vs1.getLength(), copy1.getLength());
        Assert.assertEquals(vs1.getPrecision(), copy1.getPrecision());
        Assert.assertEquals(vs1.isNull(), copy1.isNull());
        Assert.assertEquals(vs1.getOrigin(), copy1.getOrigin());
        Assert.assertEquals(vs1.getName(), copy1.getName());
        Value vs2 = new Value(((Value) (null)));
        Assert.assertTrue(vs2.isNull());
        Assert.assertNull(vs2.getName());
        Assert.assertNull(vs2.getOrigin());
    }

    /**
     * Constructors using Values
     */
    @Test
    public void testConstructor4() {
        Value vs = new Value("Name", new StringBuffer("buffer"));
        Assert.assertEquals(VALUE_TYPE_STRING, vs.getType());
        Assert.assertEquals("buffer", vs.getString());
    }

    /**
     * Constructors using Values
     */
    @Test
    public void testConstructor5() {
        Value vs = new Value("Name", 10.0);
        Assert.assertEquals(VALUE_TYPE_NUMBER, vs.getType());
        Assert.assertEquals("Name", vs.getName());
        Value copy = new Value("newName", vs);
        Assert.assertEquals("newName", copy.getName());
        Assert.assertFalse((!(vs.equals(copy))));
        copy.setName("Name");
        Assert.assertTrue(vs.equals(copy));
    }

    /**
     * Test of string representation of String Value.
     */
    @Test
    public void testToStringString() {
        String result = null;
        Value vs = new Value("Name", VALUE_TYPE_STRING);
        vs.setValue("test string");
        result = vs.toString(true);
        Assert.assertEquals("test string", result);
        vs.setLength(20);
        result = vs.toString(true);// padding

        Assert.assertEquals("test string         ", result);
        vs.setLength(4);
        result = vs.toString(true);// truncate

        Assert.assertEquals("test", result);
        vs.setLength(0);
        result = vs.toString(true);// on 0 => full string

        Assert.assertEquals("test string", result);
        // no padding
        result = vs.toString(false);
        Assert.assertEquals("test string", result);
        vs.setLength(20);
        result = vs.toString(false);
        Assert.assertEquals("test string", result);
        vs.setLength(4);
        result = vs.toString(false);
        Assert.assertEquals("test string", result);
        vs.setLength(0);
        result = vs.toString(false);
        Assert.assertEquals("test string", result);
        vs.setLength(4);
        vs.setNull();
        result = vs.toString(false);
        Assert.assertEquals("", result);
        Value vs1 = new Value("Name", VALUE_TYPE_STRING);
        Assert.assertEquals("", vs1.toString());
        // Just to get 100% coverage
        Value vs2 = new Value("Name", VALUE_TYPE_NONE);
        Assert.assertEquals("", vs2.toString());
    }

    /**
     * Test of string representation of Number Value.
     */
    @Test
    public void testToStringNumber() {
        Value vs1 = new Value("Name", VALUE_TYPE_NUMBER);
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        Assert.assertEquals(((" 0" + (symbols.getDecimalSeparator())) + "0"), vs1.toString(true));
        Value vs2 = new Value("Name", VALUE_TYPE_NUMBER);
        vs2.setNull();
        Assert.assertEquals("", vs2.toString(true));
        Value vs3 = new Value("Name", VALUE_TYPE_NUMBER);
        vs3.setValue(100.0);
        vs3.setLength(6);
        vs3.setNull();
        Assert.assertEquals("      ", vs3.toString(true));
        Value vs4 = new Value("Name", VALUE_TYPE_NUMBER);
        vs4.setValue(100.0);
        vs4.setLength(6);
        Assert.assertEquals(((" 000100" + (symbols.getDecimalSeparator())) + "00"), vs4.toString(true));
        Value vs5 = new Value("Name", VALUE_TYPE_NUMBER);
        vs5.setValue(100.5);
        vs5.setLength((-1));
        vs5.setPrecision((-1));
        Assert.assertEquals(((" 100" + (symbols.getDecimalSeparator())) + "5"), vs5.toString(true));
        Value vs6 = new Value("Name", VALUE_TYPE_NUMBER);
        vs6.setValue(100.5);
        vs6.setLength(8);
        vs6.setPrecision((-1));
        Assert.assertEquals(((" 00000100" + (symbols.getDecimalSeparator())) + "50"), vs6.toString(true));
        Value vs7 = new Value("Name", VALUE_TYPE_NUMBER);
        vs7.setValue(100.5);
        vs7.setLength(0);
        vs7.setPrecision(3);
        Assert.assertEquals(((" 100" + (symbols.getDecimalSeparator())) + "5"), vs7.toString(true));
        Value vs8 = new Value("Name", VALUE_TYPE_NUMBER);
        vs8.setValue(100.5);
        vs8.setLength(5);
        vs8.setPrecision(3);
        Assert.assertEquals("100.5", vs8.toString(false));
        Value vs9 = new Value("Name", VALUE_TYPE_NUMBER);
        vs9.setValue(100.0);
        vs9.setLength(6);
        Assert.assertEquals("100.0", vs9.toString(false));
        Value vs10 = new Value("Name", VALUE_TYPE_NUMBER);
        vs10.setValue(100.5);
        vs10.setLength((-1));
        vs10.setPrecision((-1));
        Assert.assertEquals("100.5", vs10.toString(false));
        Value vs11 = new Value("Name", VALUE_TYPE_NUMBER);
        vs11.setValue(100.5);
        vs11.setLength(8);
        vs11.setPrecision((-1));
        Assert.assertEquals("100.5", vs11.toString(false));
        Value vs12 = new Value("Name", VALUE_TYPE_NUMBER);
        vs12.setValue(100.5);
        vs12.setLength(0);
        vs12.setPrecision(3);
        Assert.assertEquals("100.5", vs12.toString(false));
        Value vs13 = new Value("Name", VALUE_TYPE_NUMBER);
        vs13.setValue(100.5);
        vs13.setLength(5);
        vs13.setPrecision(3);
        Assert.assertEquals("100.5", vs13.toString(false));
        Value vs14 = new Value("Name", VALUE_TYPE_NUMBER);
        vs14.setValue(100.5);
        vs14.setLength(5);
        vs14.setPrecision(3);
        Assert.assertEquals(((" 100" + (symbols.getDecimalSeparator())) + "500"), vs14.toString(true));
    }

    /**
     * Test of string representation of Integer Value.
     */
    @Test
    public void testToIntegerNumber() {
        Value vs1 = new Value("Name", VALUE_TYPE_INTEGER);
        Assert.assertEquals(" 0", vs1.toString(true));
        Value vs2 = new Value("Name", VALUE_TYPE_INTEGER);
        vs2.setNull();
        Assert.assertEquals("", vs2.toString(true));
        Value vs3 = new Value("Name", VALUE_TYPE_INTEGER);
        vs3.setValue(100);
        vs3.setLength(6);
        vs3.setNull();
        Assert.assertEquals("      ", vs3.toString(true));
        Value vs4 = new Value("Name", VALUE_TYPE_INTEGER);
        vs4.setValue(100);
        vs4.setLength(6);
        Assert.assertEquals(" 000100", vs4.toString(true));
        Value vs5 = new Value("Name", VALUE_TYPE_INTEGER);
        vs5.setValue(100);
        vs5.setLength((-1));
        vs5.setPrecision((-1));
        Assert.assertEquals(" 100", vs5.toString(true));
        Value vs6 = new Value("Name", VALUE_TYPE_INTEGER);
        vs6.setValue(105);
        vs6.setLength(8);
        vs6.setPrecision((-1));
        Assert.assertEquals(" 00000105", vs6.toString(true));
        Value vs7 = new Value("Name", VALUE_TYPE_INTEGER);
        vs7.setValue(100);
        vs7.setLength(0);
        vs7.setPrecision(3);
        Assert.assertEquals(" 100", vs7.toString(true));
        Value vs8 = new Value("Name", VALUE_TYPE_INTEGER);
        vs8.setValue(100);
        vs8.setLength(5);
        vs8.setPrecision(3);
        Assert.assertEquals("100", vs8.toString(false));
        Value vs9 = new Value("Name", VALUE_TYPE_INTEGER);
        vs9.setValue(100);
        vs9.setLength(6);
        Assert.assertEquals("100", vs9.toString(false));
        Value vs10 = new Value("Name", VALUE_TYPE_INTEGER);
        vs10.setValue(100);
        vs10.setLength((-1));
        vs10.setPrecision((-1));
        Assert.assertEquals(" 100", vs10.toString(false));
        Value vs11 = new Value("Name", VALUE_TYPE_INTEGER);
        vs11.setValue(100);
        vs11.setLength(8);
        vs11.setPrecision((-1));
        Assert.assertEquals("100", vs11.toString(false));
        Value vs12 = new Value("Name", VALUE_TYPE_INTEGER);
        vs12.setValue(100);
        vs12.setLength(0);
        vs12.setPrecision(3);
        Assert.assertEquals(" 100", vs12.toString(false));
        Value vs13 = new Value("Name", VALUE_TYPE_INTEGER);
        vs13.setValue(100);
        vs13.setLength(5);
        vs13.setPrecision(3);
        Assert.assertEquals("100", vs13.toString(false));
        Value vs14 = new Value("Name", VALUE_TYPE_INTEGER);
        vs14.setValue(100);
        vs14.setLength(5);
        vs14.setPrecision(3);
        Assert.assertEquals(" 00100", vs14.toString(true));
    }

    /**
     * Test of boolean representation of Value.
     */
    @Test
    public void testToStringBoolean() {
        String result = null;
        Value vs = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs.setValue(true);
        result = vs.toString(true);
        Assert.assertEquals("true", result);
        Value vs1 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs1.setValue(false);
        result = vs1.toString(true);
        Assert.assertEquals("false", result);
        // set to "null"
        Value vs2 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs2.setValue(true);
        vs2.setNull();
        result = vs2.toString(true);
        Assert.assertEquals("", result);
        // set to "null"
        Value vs3 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs3.setValue(false);
        vs3.setNull();
        result = vs3.toString(true);
        Assert.assertEquals("", result);
        // set to length = 1 => get Y/N
        Value vs4 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs4.setValue(true);
        vs4.setLength(1);
        result = vs4.toString(true);
        Assert.assertEquals("true", result);
        // set to length = 1 => get Y/N
        Value vs5 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs5.setValue(false);
        vs5.setLength(1);
        result = vs5.toString(true);
        Assert.assertEquals("false", result);
        // set to length > 1 => get true/false
        Value vs6 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs6.setValue(true);
        vs6.setLength(3);
        result = vs6.toString(true);
        Assert.assertEquals("true", result);
        // set to length > 1 => get true/false (+ truncation)
        Value vs7 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs7.setValue(false);
        vs7.setLength(3);
        result = vs7.toString(true);
        Assert.assertEquals("false", result);
    }

    /**
     * Test of boolean representation of Value.
     */
    @Test
    public void testToStringDate() {
        String result = null;
        Value vs1 = new Value("Name", VALUE_TYPE_DATE);
        result = vs1.toString(true);
        Assert.assertEquals("", result);
        Value vs2 = new Value("Name", VALUE_TYPE_DATE);
        vs2.setNull(true);
        result = vs2.toString(true);
        Assert.assertEquals("", result);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date dt = df.parse("2006/03/01 17:01:02.005", new ParsePosition(0));
        Value vs3 = new Value("Name", VALUE_TYPE_DATE);
        vs3.setValue(dt);
        result = vs3.toString(true);
        Assert.assertEquals("2006/03/01 17:01:02.005", result);
        Value vs4 = new Value("Name", VALUE_TYPE_DATE);
        vs4.setNull(true);
        vs4.setLength(2);
        result = vs4.toString(true);
        Assert.assertEquals("", result);
        Value vs5 = new Value("Name", VALUE_TYPE_DATE);
        vs3.setValue(dt);
        vs5.setLength(10);
        result = vs5.toString(true);
        Assert.assertEquals("", result);
    }

    @Test
    public void testToStringMeta() {
        String result = null;
        // Strings
        Value vs = new Value("Name", VALUE_TYPE_STRING);
        vs.setValue("test");
        result = vs.toStringMeta();
        Assert.assertEquals("String", result);
        Value vs1 = new Value("Name", VALUE_TYPE_STRING);
        vs1.setValue("test");
        vs1.setLength(0);
        result = vs1.toStringMeta();
        Assert.assertEquals("String", result);
        Value vs2 = new Value("Name", VALUE_TYPE_STRING);
        vs2.setValue("test");
        vs2.setLength(4);
        result = vs2.toStringMeta();
        Assert.assertEquals("String(4)", result);
        // Booleans: not affected by length on output
        Value vs3 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs3.setValue(false);
        result = vs3.toStringMeta();
        Assert.assertEquals("Boolean", result);
        Value vs4 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs4.setValue(false);
        vs4.setLength(0);
        result = vs4.toStringMeta();
        Assert.assertEquals("Boolean", result);
        Value vs5 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs5.setValue(false);
        vs5.setLength(4);
        result = vs5.toStringMeta();
        Assert.assertEquals("Boolean", result);
        // Integers
        Value vs6 = new Value("Name", VALUE_TYPE_INTEGER);
        vs6.setValue(10);
        result = vs6.toStringMeta();
        Assert.assertEquals("Integer", result);
        Value vs7 = new Value("Name", VALUE_TYPE_INTEGER);
        vs7.setValue(10);
        vs7.setLength(0);
        result = vs7.toStringMeta();
        Assert.assertEquals("Integer", result);
        Value vs8 = new Value("Name", VALUE_TYPE_INTEGER);
        vs8.setValue(10);
        vs8.setLength(4);
        result = vs8.toStringMeta();
        Assert.assertEquals("Integer(4)", result);
        // Numbers
        Value vs9 = new Value("Name", VALUE_TYPE_NUMBER);
        vs9.setValue(10.0);
        result = vs9.toStringMeta();
        Assert.assertEquals("Number", result);
        Value vs10 = new Value("Name", VALUE_TYPE_NUMBER);
        vs10.setValue(10.0);
        vs10.setLength(0);
        result = vs10.toStringMeta();
        Assert.assertEquals("Number", result);
        Value vs11 = new Value("Name", VALUE_TYPE_NUMBER);
        vs11.setValue(10.0);
        vs11.setLength(4);
        result = vs11.toStringMeta();
        Assert.assertEquals("Number(4)", result);
        Value vs12 = new Value("Name", VALUE_TYPE_NUMBER);
        vs12.setValue(10.0);
        vs12.setLength(4);
        vs12.setPrecision(2);
        result = vs12.toStringMeta();
        Assert.assertEquals("Number(4, 2)", result);
        // BigNumber
        Value vs13 = new Value("Name", VALUE_TYPE_BIGNUMBER);
        vs13.setValue(new BigDecimal(10));
        result = vs13.toStringMeta();
        Assert.assertEquals("BigNumber", result);
        Value vs14 = new Value("Name", VALUE_TYPE_BIGNUMBER);
        vs14.setValue(new BigDecimal(10));
        vs14.setLength(0);
        result = vs14.toStringMeta();
        Assert.assertEquals("BigNumber", result);
        Value vs15 = new Value("Name", VALUE_TYPE_BIGNUMBER);
        vs15.setValue(new BigDecimal(10));
        vs15.setLength(4);
        result = vs15.toStringMeta();
        Assert.assertEquals("BigNumber(4)", result);
        Value vs16 = new Value("Name", VALUE_TYPE_BIGNUMBER);
        vs16.setValue(new BigDecimal(10));
        vs16.setLength(4);
        vs16.setPrecision(2);
        result = vs16.toStringMeta();
        Assert.assertEquals("BigNumber(4, 2)", result);
        // Date
        Value vs17 = new Value("Name", VALUE_TYPE_DATE);
        vs17.setValue(new Date());
        result = vs17.toStringMeta();
        Assert.assertEquals("Date", result);
        Value vs18 = new Value("Name", VALUE_TYPE_DATE);
        vs18.setValue(new Date());
        vs18.setLength(0);
        result = vs18.toStringMeta();
        Assert.assertEquals("Date", result);
        Value vs19 = new Value("Name", VALUE_TYPE_DATE);
        vs19.setValue(new Date());
        vs19.setLength(4);
        result = vs19.toStringMeta();
        Assert.assertEquals("Date", result);
        Value vs20 = new Value("Name", VALUE_TYPE_DATE);
        vs20.setValue(new Date());
        vs20.setLength(4);
        vs20.setPrecision(2);
        result = vs20.toStringMeta();
        Assert.assertEquals("Date", result);
    }

    /**
     * Constructors using Values.
     */
    @Test
    public void testClone1() {
        Value vs = new Value("Name", VALUE_TYPE_NUMBER);
        vs.setValue(10.0);
        vs.setOrigin("origin");
        vs.setLength(4, 2);
        Value copy = vs.Clone();
        Assert.assertEquals(vs.getType(), copy.getType());
        Assert.assertEquals(vs.getNumber(), copy.getNumber(), 0.1);
        Assert.assertEquals(vs.getLength(), copy.getLength());
        Assert.assertEquals(vs.getPrecision(), copy.getPrecision());
        Assert.assertEquals(vs.isNull(), copy.isNull());
        Assert.assertEquals(vs.getOrigin(), copy.getOrigin());
        Assert.assertEquals(vs.getName(), copy.getName());
        // Show it's a deep copy
        copy.setName("newName");
        Assert.assertEquals("Name", vs.getName());
        Assert.assertEquals("newName", copy.getName());
        copy.setOrigin("newOrigin");
        Assert.assertEquals("origin", vs.getOrigin());
        Assert.assertEquals("newOrigin", copy.getOrigin());
        copy.setValue(11.0);
        Assert.assertEquals(10.0, vs.getNumber(), 0.1);
        Assert.assertEquals(11.0, copy.getNumber(), 0.1);
        Value vs1 = new Value("Name", VALUE_TYPE_NUMBER);
        vs1.setName(null);
        // name and origin are null
        Value copy1 = new Value(vs1);
        Assert.assertEquals(vs1.getType(), copy1.getType());
        Assert.assertEquals(vs1.getNumber(), copy1.getNumber(), 0.1);
        Assert.assertEquals(vs1.getLength(), copy1.getLength());
        Assert.assertEquals(vs1.getPrecision(), copy1.getPrecision());
        Assert.assertEquals(vs1.isNull(), copy1.isNull());
        Assert.assertEquals(vs1.getOrigin(), copy1.getOrigin());
        Assert.assertEquals(vs1.getName(), copy1.getName());
        Value vs2 = new Value(((Value) (null)));
        Assert.assertTrue(vs2.isNull());
        Assert.assertNull(vs2.getName());
        Assert.assertNull(vs2.getOrigin());
    }

    /**
     * Test of getStringLength().
     */
    @Test
    public void testGetStringLength() {
        int result = 0;
        Value vs1 = new Value("Name", VALUE_TYPE_STRING);
        result = vs1.getStringLength();
        Assert.assertEquals(0, result);
        Value vs2 = new Value("Name", VALUE_TYPE_STRING);
        vs2.setNull();
        result = vs2.getStringLength();
        Assert.assertEquals(0, result);
        Value vs3 = new Value("Name", VALUE_TYPE_STRING);
        vs3.setValue("stringlength");
        result = vs3.getStringLength();
        Assert.assertEquals(12, result);
    }

    @Test
    public void testGetXML() {
        String result = null;
        Value vs1 = new Value("Name", VALUE_TYPE_STRING);
        vs1.setValue("test");
        vs1.setLength(4);
        vs1.setPrecision(2);
        result = vs1.getXML();
        Assert.assertEquals("<value><name>Name</name><type>String</type><text>test</text><length>4</length><precision>-1</precision><isnull>N</isnull></value>", result);
        Value vs2 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs2.setValue(false);
        vs2.setLength(4);
        vs2.setPrecision(2);
        result = vs2.getXML();
        Assert.assertEquals("<value><name>Name</name><type>Boolean</type><text>false</text><length>-1</length><precision>-1</precision><isnull>N</isnull></value>", result);
        Value vs3 = new Value("Name", VALUE_TYPE_INTEGER);
        vs3.setValue(10);
        vs3.setLength(4);
        vs3.setPrecision(2);
        result = vs3.getXML();
        Assert.assertEquals("<value><name>Name</name><type>Integer</type><text>10</text><length>4</length><precision>0</precision><isnull>N</isnull></value>", result);
        Value vs4 = new Value("Name", VALUE_TYPE_NUMBER);
        vs4.setValue(10.0);
        vs4.setLength(4);
        vs4.setPrecision(2);
        result = vs4.getXML();
        Assert.assertEquals("<value><name>Name</name><type>Number</type><text>10.0</text><length>4</length><precision>2</precision><isnull>N</isnull></value>", result);
        Value vs5 = new Value("Name", VALUE_TYPE_BIGNUMBER);
        vs5.setValue(new BigDecimal(10));
        vs5.setLength(4);
        vs5.setPrecision(2);
        result = vs5.getXML();
        Assert.assertEquals("<value><name>Name</name><type>BigNumber</type><text>10</text><length>4</length><precision>2</precision><isnull>N</isnull></value>", result);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date dt = df.parse("2006/03/01 17:01:02.005", new ParsePosition(0));
        Value vs6 = new Value("Name", VALUE_TYPE_DATE);
        vs6.setValue(dt);
        vs6.setLength(4);
        vs6.setPrecision(2);
        result = vs6.getXML();
        Assert.assertEquals("<value><name>Name</name><type>Date</type><text>2006/03/01 17:01:02.005</text><length>-1</length><precision>2</precision><isnull>N</isnull></value>", result);
    }

    /**
     * Test of setValue()
     */
    @Test
    public void testSetValue() {
        Value vs = new Value("Name", VALUE_TYPE_INTEGER);
        vs.setValue(100L);
        vs.setOrigin("origin");
        Value vs1 = new Value(((Value) (null)));
        Assert.assertTrue(vs1.isNull());
        Assert.assertTrue(vs1.isEmpty());
        Assert.assertNull(vs1.getName());
        Assert.assertNull(vs1.getOrigin());
        Assert.assertEquals(VALUE_TYPE_NONE, vs1.getType());
        Value vs2 = new Value("newName", VALUE_TYPE_INTEGER);
        vs2.setOrigin("origin1");
        vs2.setValue(vs);
        Assert.assertEquals("origin", vs2.getOrigin());
        Assert.assertEquals(vs.getInteger(), vs2.getInteger());
        Value vs3 = new Value("newName", VALUE_TYPE_INTEGER);
        vs3.setValue(new StringBuffer("Sven"));
        Assert.assertEquals(VALUE_TYPE_STRING, vs3.getType());
        Assert.assertEquals("Sven", vs3.getString());
        Value vs4 = new Value("newName", VALUE_TYPE_STRING);
        vs4.setValue(new StringBuffer("Test"));
        vs4.setValue(new StringBuffer("Sven"));
        Assert.assertEquals(VALUE_TYPE_STRING, vs4.getType());
        Assert.assertEquals("Sven", vs4.getString());
        Value vs5 = new Value("Name", VALUE_TYPE_INTEGER);
        vs5.setValue(((byte) (4)));
        Assert.assertEquals(4L, vs5.getInteger());
        Value vs6 = new Value("Name", VALUE_TYPE_INTEGER);
        vs6.setValue(((Value) (null)));
        Assert.assertFalse(vs6.isNull());
        Assert.assertNull(vs6.getName());
        Assert.assertNull(vs6.getOrigin());
        Assert.assertEquals(VALUE_TYPE_NONE, vs6.getType());
    }

    /**
     * Test for isNumeric().
     */
    @Test
    public void testIsNumeric() {
        Assert.assertEquals(false, Value.isNumeric(VALUE_TYPE_NONE));
        Assert.assertEquals(true, Value.isNumeric(VALUE_TYPE_NUMBER));
        Assert.assertEquals(false, Value.isNumeric(VALUE_TYPE_STRING));
        Assert.assertEquals(false, Value.isNumeric(VALUE_TYPE_DATE));
        Assert.assertEquals(false, Value.isNumeric(VALUE_TYPE_BOOLEAN));
        Assert.assertEquals(true, Value.isNumeric(VALUE_TYPE_INTEGER));
        Assert.assertEquals(true, Value.isNumeric(VALUE_TYPE_BIGNUMBER));
        Assert.assertEquals(false, Value.isNumeric(VALUE_TYPE_SERIALIZABLE));
    }

    @Test
    public void testIsEqualTo() {
        Value vs1 = new Value("Name", VALUE_TYPE_STRING);
        vs1.setValue("test");
        Assert.assertTrue(vs1.isEqualTo("test"));
        Assert.assertFalse(vs1.isEqualTo("test1"));
        Value vs2 = new Value("Name", VALUE_TYPE_STRING);
        vs2.setValue(BigDecimal.ONE);
        Assert.assertTrue(vs2.isEqualTo(BigDecimal.ONE));
        Assert.assertFalse(vs2.isEqualTo(BigDecimal.valueOf(2.0)));
        Value vs3 = new Value("Name", VALUE_TYPE_NUMBER);
        vs3.setValue(10.0);
        Assert.assertTrue(vs3.isEqualTo(10.0));
        Assert.assertFalse(vs3.isEqualTo(11.0));
        Value vs4 = new Value("Name", VALUE_TYPE_INTEGER);
        vs4.setValue(10L);
        Assert.assertTrue(vs4.isEqualTo(10L));
        Assert.assertFalse(vs4.isEqualTo(11L));
        Value vs5 = new Value("Name", VALUE_TYPE_INTEGER);
        vs5.setValue(10);
        Assert.assertTrue(vs5.isEqualTo(10));
        Assert.assertFalse(vs5.isEqualTo(11));
        Value vs6 = new Value("Name", VALUE_TYPE_INTEGER);
        vs6.setValue(((byte) (10)));
        Assert.assertTrue(vs6.isEqualTo(10));
        Assert.assertFalse(vs6.isEqualTo(11));
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date dt = df.parse("2006/03/01 17:01:02.005", new ParsePosition(0));
        Value vs7 = new Value("Name", VALUE_TYPE_DATE);
        vs7.setValue(dt);
        Assert.assertTrue(vs7.isEqualTo(dt));
        Assert.assertFalse(vs7.isEqualTo(new Date()));// assume it's not 2006/03/01

    }

    /**
     * Test boolean operators.
     */
    @Test
    public void testBooleanOperators() {
        Value vs1 = new Value("Name", VALUE_TYPE_BOOLEAN);
        Value vs2 = new Value("Name", VALUE_TYPE_BOOLEAN);
        vs1.setValue(false);
        vs2.setValue(false);
        vs1.bool_and(vs2);
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(false);
        vs2.setValue(true);
        vs1.bool_and(vs2);
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(true);
        vs2.setValue(false);
        vs1.bool_and(vs2);
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(true);
        vs2.setValue(true);
        vs1.bool_and(vs2);
        Assert.assertEquals(true, vs1.getBoolean());
        vs1.setValue(false);
        vs2.setValue(false);
        vs1.bool_or(vs2);
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(false);
        vs2.setValue(true);
        vs1.bool_or(vs2);
        Assert.assertEquals(true, vs1.getBoolean());
        vs1.setValue(true);
        vs2.setValue(false);
        vs1.bool_or(vs2);
        Assert.assertEquals(true, vs1.getBoolean());
        vs1.setValue(true);
        vs2.setValue(true);
        vs1.bool_or(vs2);
        Assert.assertEquals(true, vs1.getBoolean());
        vs1.setValue(false);
        vs2.setValue(false);
        vs1.bool_xor(vs2);
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(false);
        vs2.setValue(true);
        vs1.bool_xor(vs2);
        Assert.assertEquals(true, vs1.getBoolean());
        vs1.setValue(true);
        vs2.setValue(false);
        vs1.bool_xor(vs2);
        Assert.assertEquals(true, vs1.getBoolean());
        vs1.setValue(true);
        vs2.setValue(true);
        vs1.bool_xor(vs2);
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(true);
        vs1.bool_not();
        Assert.assertEquals(false, vs1.getBoolean());
        vs1.setValue(false);
        vs1.bool_not();
        Assert.assertEquals(true, vs1.getBoolean());
    }

    /**
     * Test boolean operators.
     */
    @Test
    public void testBooleanOperators1() {
        Value vs1 = new Value("Name", VALUE_TYPE_INTEGER);
        Value vs2 = new Value("Name", VALUE_TYPE_INTEGER);
        vs1.setValue(255L);
        vs2.setValue(255L);
        vs1.and(vs2);
        Assert.assertEquals(255L, vs1.getInteger());
        vs1.setValue(255L);
        vs2.setValue(0L);
        vs1.and(vs2);
        Assert.assertEquals(0L, vs1.getInteger());
        vs1.setValue(0L);
        vs2.setValue(255L);
        vs1.and(vs2);
        Assert.assertEquals(0L, vs1.getInteger());
        vs1.setValue(0L);
        vs2.setValue(0L);
        vs1.and(vs2);
        Assert.assertEquals(0L, vs1.getInteger());
        vs1.setValue(255L);
        vs2.setValue(255L);
        vs1.or(vs2);
        Assert.assertEquals(255L, vs1.getInteger());
        vs1.setValue(255L);
        vs2.setValue(0L);
        vs1.or(vs2);
        Assert.assertEquals(255L, vs1.getInteger());
        vs1.setValue(0L);
        vs2.setValue(255L);
        vs1.or(vs2);
        Assert.assertEquals(255L, vs1.getInteger());
        vs1.setValue(0L);
        vs2.setValue(0L);
        vs1.or(vs2);
        Assert.assertEquals(0L, vs1.getInteger());
        vs1.setValue(255L);
        vs2.setValue(255L);
        vs1.xor(vs2);
        Assert.assertEquals(0L, vs1.getInteger());
        vs1.setValue(255L);
        vs2.setValue(0L);
        vs1.xor(vs2);
        Assert.assertEquals(255L, vs1.getInteger());
        vs1.setValue(0L);
        vs2.setValue(255L);
        vs1.xor(vs2);
        Assert.assertEquals(255L, vs1.getInteger());
        vs1.setValue(0L);
        vs2.setValue(0L);
        vs1.xor(vs2);
        Assert.assertEquals(0L, vs1.getInteger());
    }

    /**
     * Test comparators.
     */
    @Test
    public void testComparators() {
        Value vs1 = new Value("Name", VALUE_TYPE_INTEGER);
        Value vs2 = new Value("Name", VALUE_TYPE_INTEGER);
        Value vs3 = new Value("Name", VALUE_TYPE_INTEGER);
        vs1.setValue(128L);
        vs2.setValue(100L);
        vs3.setValue(128L);
        Assert.assertEquals(true, vs1.Clone().greater_equal(vs2).getBoolean());
        Assert.assertEquals(true, vs1.Clone().greater_equal(vs3).getBoolean());
        Assert.assertEquals(false, vs2.Clone().greater_equal(vs1).getBoolean());
        Assert.assertEquals(false, vs1.Clone().smaller_equal(vs2).getBoolean());
        Assert.assertEquals(true, vs1.Clone().smaller_equal(vs3).getBoolean());
        Assert.assertEquals(true, vs2.Clone().smaller_equal(vs1).getBoolean());
        Assert.assertEquals(true, vs1.Clone().different(vs2).getBoolean());
        Assert.assertEquals(false, vs1.Clone().different(vs3).getBoolean());
        Assert.assertEquals(false, vs1.Clone().equal(vs2).getBoolean());
        Assert.assertEquals(true, vs1.Clone().equal(vs3).getBoolean());
        Assert.assertEquals(true, vs1.Clone().greater(vs2).getBoolean());
        Assert.assertEquals(false, vs1.Clone().greater(vs3).getBoolean());
        Assert.assertEquals(false, vs2.Clone().greater(vs1).getBoolean());
        Assert.assertEquals(false, vs1.Clone().smaller(vs2).getBoolean());
        Assert.assertEquals(false, vs1.Clone().smaller(vs3).getBoolean());
        Assert.assertEquals(true, vs2.Clone().smaller(vs1).getBoolean());
    }

    /**
     * Test trim, ltrim, rtrim.
     */
    @Test
    public void testTrim() {
        Value vs1 = new Value("Name1", VALUE_TYPE_INTEGER);
        Value vs2 = new Value("Name2", VALUE_TYPE_STRING);
        vs1.setValue(128L);
        vs1.setNull();
        Assert.assertNull(vs1.Clone().ltrim().getString());
        Assert.assertNull(vs1.Clone().rtrim().getString());
        Assert.assertNull(vs1.Clone().trim().getString());
        vs1.setValue(128L);
        Assert.assertEquals("128", vs1.Clone().ltrim().getString());
        Assert.assertEquals(" 128", vs1.Clone().rtrim().getString());
        Assert.assertEquals("128", vs1.Clone().trim().getString());
        vs2.setValue("    Sven Boden trim test    ");
        Assert.assertEquals("Sven Boden trim test    ", vs2.Clone().ltrim().getString());
        Assert.assertEquals("    Sven Boden trim test", vs2.Clone().rtrim().getString());
        Assert.assertEquals("Sven Boden trim test", vs2.Clone().trim().getString());
        vs2.setValue("");
        Assert.assertEquals("", vs2.Clone().ltrim().getString());
        Assert.assertEquals("", vs2.Clone().rtrim().getString());
        Assert.assertEquals("", vs2.Clone().trim().getString());
        vs2.setValue("   ");
        Assert.assertEquals("", vs2.Clone().ltrim().getString());
        Assert.assertEquals("", vs2.Clone().rtrim().getString());
        Assert.assertEquals("", vs2.Clone().trim().getString());
    }

    /**
     * Test hexToByteDecode.
     */
    @Test
    public void testHexToByteDecode() throws KettleValueException {
        Value vs1 = new Value("Name1", VALUE_TYPE_INTEGER);
        vs1.setValue("6120622063");
        vs1.hexToByteDecode();
        Assert.assertEquals("a b c", vs1.getString());
        vs1.setValue("4161426243643039207A5A2E3F2F");
        vs1.hexToByteDecode();
        Assert.assertEquals("AaBbCd09 zZ.?/", vs1.getString());
        vs1.setValue("4161426243643039207a5a2e3f2f");
        vs1.hexToByteDecode();
        Assert.assertEquals("AaBbCd09 zZ.?/", vs1.getString());
        // leading 0 if odd.
        vs1.setValue("F6120622063");
        vs1.hexToByteDecode();
        Assert.assertEquals("\u000fa b c", vs1.getString());
        try {
            vs1.setValue("g");
            vs1.hexToByteDecode();
            Assert.fail("Expected KettleValueException");
        } catch (KettleValueException ex) {
        }
    }

    /**
     * Test hexEncode.
     */
    @Test
    public void testByteToHexEncode() {
        Value vs1 = new Value("Name1", VALUE_TYPE_INTEGER);
        vs1.setValue("AaBbCd09 zZ.?/");
        vs1.byteToHexEncode();
        Assert.assertEquals("4161426243643039207A5A2E3F2F", vs1.getString());
        vs1.setValue("1234567890");
        vs1.byteToHexEncode();
        Assert.assertEquals("31323334353637383930", vs1.getString());
        vs1.setNull();
        vs1.byteToHexEncode();
        Assert.assertNull(vs1.getString());
    }

    /**
     * Regression test for bug: hexdecode/encode would not work for some UTF8 strings.
     */
    @Test
    public void testHexByteRegression() throws KettleValueException {
        Value vs1 = new Value("Name1", VALUE_TYPE_INTEGER);
        vs1.setValue("9B");
        vs1.hexToByteDecode();
        vs1.byteToHexEncode();
        Assert.assertEquals("9B", vs1.getString());
        vs1.setValue("A7");
        vs1.hexToByteDecode();
        vs1.byteToHexEncode();
        Assert.assertEquals("A7", vs1.getString());
        vs1.setValue("74792121212121212121212121C2A7");
        vs1.hexToByteDecode();
        vs1.byteToHexEncode();
        Assert.assertEquals("74792121212121212121212121C2A7", vs1.getString());
        vs1.setValue("70616E2D6C656FC5A1");
        vs1.hexToByteDecode();
        vs1.byteToHexEncode();
        Assert.assertEquals("70616E2D6C656FC5A1", vs1.getString());
        vs1.setValue("736B76C49B6C6520");
        vs1.hexToByteDecode();
        vs1.byteToHexEncode();
        Assert.assertEquals("736B76C49B6C6520", vs1.getString());
    }

    /**
     * Test for Hex to Char decoding and vica versa.
     */
    @Test
    public void testHexCharTest() throws KettleValueException {
        Value vs1 = new Value("Name1", VALUE_TYPE_INTEGER);
        vs1.setValue("009B");
        vs1.hexToCharDecode();
        vs1.charToHexEncode();
        Assert.assertEquals("009B", vs1.getString());
        vs1.setValue("007400790021002100C200A7");
        vs1.hexToCharDecode();
        vs1.charToHexEncode();
        Assert.assertEquals("007400790021002100C200A7", vs1.getString());
        vs1.setValue("FFFF00FFFF000F0FF0F0");
        vs1.hexToCharDecode();
        vs1.charToHexEncode();
        Assert.assertEquals("FFFF00FFFF000F0FF0F0", vs1.getString());
    }

    /**
     * Test like.
     */
    @Test
    public void testLike() {
        Value vs1 = new Value("Name1", VALUE_TYPE_STRING);
        Value vs2 = new Value("Name2", VALUE_TYPE_STRING);
        Value vs3 = new Value("Name3", VALUE_TYPE_STRING);
        vs1.setValue("This is a test");
        vs2.setValue("is a");
        vs3.setValue("not");
        Assert.assertEquals(true, vs1.Clone().like(vs2).getBoolean());
        Assert.assertEquals(true, vs1.Clone().like(vs1).getBoolean());
        Assert.assertEquals(false, vs1.Clone().like(vs3).getBoolean());
        Assert.assertEquals(false, vs3.Clone().like(vs1).getBoolean());
    }

    /**
     * Stuff which we didn't get in other checks.
     */
    @Test
    public void testLooseEnds() {
        Assert.assertEquals(VALUE_TYPE_NONE, Value.getType("INVALID_TYPE"));
        Assert.assertEquals("String", Value.getTypeDesc(VALUE_TYPE_STRING));
    }

    /**
     * Constructors using Values.
     */
    @Test
    public void testClone2() {
        Value vs = new Value("Name", VALUE_TYPE_NUMBER);
        vs.setValue(10.0);
        vs.setOrigin("origin");
        vs.setLength(4, 2);
        Value copy = ((Value) (vs.clone()));
        Assert.assertEquals(vs.getType(), copy.getType());
        Assert.assertEquals(vs.getNumber(), copy.getNumber(), 0.1);
        Assert.assertEquals(vs.getLength(), copy.getLength());
        Assert.assertEquals(vs.getPrecision(), copy.getPrecision());
        Assert.assertEquals(vs.isNull(), copy.isNull());
        Assert.assertEquals(vs.getOrigin(), copy.getOrigin());
        Assert.assertEquals(vs.getName(), copy.getName());
        // Show it's a deep copy
        copy.setName("newName");
        Assert.assertEquals("Name", vs.getName());
        Assert.assertEquals("newName", copy.getName());
        copy.setOrigin("newOrigin");
        Assert.assertEquals("origin", vs.getOrigin());
        Assert.assertEquals("newOrigin", copy.getOrigin());
        copy.setValue(11.0);
        Assert.assertEquals(10.0, vs.getNumber(), 0.1);
        Assert.assertEquals(11.0, copy.getNumber(), 0.1);
        Value vs1 = new Value("Name", VALUE_TYPE_NUMBER);
        vs1.setName(null);
        // name and origin are null
        Value copy1 = new Value(vs1);
        Assert.assertEquals(vs1.getType(), copy1.getType());
        Assert.assertEquals(vs1.getNumber(), copy1.getNumber(), 0.1);
        Assert.assertEquals(vs1.getLength(), copy1.getLength());
        Assert.assertEquals(vs1.getPrecision(), copy1.getPrecision());
        Assert.assertEquals(vs1.isNull(), copy1.isNull());
        Assert.assertEquals(vs1.getOrigin(), copy1.getOrigin());
        Assert.assertEquals(vs1.getName(), copy1.getName());
        Value vs2 = new Value(((Value) (null)));
        Assert.assertTrue(vs2.isNull());
        Assert.assertNull(vs2.getName());
        Assert.assertNull(vs2.getOrigin());
    }

    @Test
    public void testValueMetaInterfaceEquality() {
        Assert.assertEquals(TYPE_NONE, VALUE_TYPE_NONE);
        Assert.assertEquals(TYPE_NUMBER, VALUE_TYPE_NUMBER);
        Assert.assertEquals(TYPE_STRING, VALUE_TYPE_STRING);
        Assert.assertEquals(TYPE_DATE, VALUE_TYPE_DATE);
        Assert.assertEquals(TYPE_BOOLEAN, VALUE_TYPE_BOOLEAN);
        Assert.assertEquals(TYPE_INTEGER, VALUE_TYPE_INTEGER);
        Assert.assertEquals(TYPE_BIGNUMBER, VALUE_TYPE_BIGNUMBER);
        Assert.assertEquals(TYPE_SERIALIZABLE, VALUE_TYPE_SERIALIZABLE);
        Assert.assertEquals(TYPE_BINARY, VALUE_TYPE_BINARY);
    }
}

