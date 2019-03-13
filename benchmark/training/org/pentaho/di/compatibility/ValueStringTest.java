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


import Value.VALUE_TYPE_STRING;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of ValueString.
 *
 * @author Sven Boden
 */
public class ValueStringTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * Constructor test 1.
     */
    @Test
    public void testConstructor1() {
        ValueString vs = new ValueString();
        Assert.assertEquals(VALUE_TYPE_STRING, vs.getType());
        Assert.assertEquals("String", vs.getTypeDesc());
        Assert.assertEquals((-1), vs.getLength());
        Assert.assertEquals((-1), vs.getPrecision());
        ValueString vs1 = new ValueString("Boden Test");
        Assert.assertEquals(VALUE_TYPE_STRING, vs1.getType());
        Assert.assertEquals("String", vs1.getTypeDesc());
        // is the length of the field, not the length of the value
        Assert.assertEquals((-1), vs1.getLength());
        Assert.assertEquals((-1), vs1.getPrecision());
        ValueString vs2 = new ValueString();
        // precision is ignored
        vs2.setPrecision(2);
        Assert.assertEquals((-1), vs2.getPrecision());
        vs2.setLength(10);
        Assert.assertEquals(10, vs2.getLength());
    }

    /**
     * Set the value to null and see what comes out on conversions.
     */
    @Test
    public void testGetNullValue() {
        ValueString vs = new ValueString();
        Assert.assertNull(vs.getString());
        Assert.assertEquals(0.0, vs.getNumber(), 0.0);
        Assert.assertNull(vs.getDate());
        Assert.assertEquals(false, vs.getBoolean());
        Assert.assertEquals(0, vs.getInteger());
        Assert.assertEquals(null, vs.getBigNumber());
        Assert.assertNull(vs.getSerializable());
    }

    /**
     * Set the value to an integer number and see what comes out on conversions.
     */
    @Test
    public void testGetNumericValue1() {
        ValueString vs = new ValueString("1000");
        Assert.assertEquals("1000", vs.getString());
        Assert.assertEquals(1000.0, vs.getNumber(), 0.0);
        Assert.assertNull(vs.getDate());// will fail parsing

        Assert.assertEquals(false, vs.getBoolean());
        Assert.assertEquals(1000, vs.getInteger());
        Assert.assertEquals(BigDecimal.valueOf(1000), vs.getBigNumber());
    }

    /**
     * Set the value to an "float" number and see what comes out on conversions.
     */
    @Test
    public void testGetNumericValue2() {
        ValueString vs = new ValueString("2.8");
        Assert.assertEquals("2.8", vs.getString());
        Assert.assertEquals(2.8, vs.getNumber(), 0.0);
        Assert.assertNull(vs.getDate());// will fail parsing

        Assert.assertEquals(false, vs.getBoolean());
        Assert.assertEquals(0, vs.getInteger());
        Assert.assertEquals(2.8, vs.getBigNumber().doubleValue(), 0.1);
    }

    /**
     * Set the value to a non numeric string.
     */
    @Test
    public void testGetString() {
        ValueString vs = new ValueString("Boden");
        Assert.assertEquals("Boden", vs.getString());
        Assert.assertEquals(0.0, vs.getNumber(), 0.0);
        Assert.assertNull(vs.getDate());// will fail parsing

        Assert.assertEquals(false, vs.getBoolean());
        Assert.assertEquals(0, vs.getInteger());
        try {
            vs.getBigNumber();
            Assert.fail("Expected a NumberFormatException");
        } catch (NumberFormatException ex) {
            vs = null;
        }
    }

    /**
     * Test setting a string.
     */
    @Test
    public void testSetString() {
        ValueString vs = new ValueString();
        vs.setString(null);
        Assert.assertNull(vs.getString());
        vs.setString("");
        Assert.assertEquals("", vs.getString());
        vs.setString("Boden");
        Assert.assertEquals("Boden", vs.getString());
    }

    /**
     * Test setting a number.
     */
    @Test
    public void testSetNumber() {
        ValueString vs = new ValueString();
        vs.setNumber(0);
        Assert.assertEquals("0.0", vs.getString());
        vs.setNumber(1);
        Assert.assertEquals("1.0", vs.getString());
        vs.setNumber((-1));
        Assert.assertEquals("-1.0", vs.getString());
        vs.setNumber(2.5);
        Assert.assertEquals("2.5", vs.getString());
        vs.setNumber(2.8);
        Assert.assertEquals("2.8", vs.getString());
    }

    /**
     * Test dates in ValueString
     */
    @Test
    public void testSetDate() throws ParseException {
        ValueString vs = new ValueString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.US);
        try {
            vs.setDate(null);
            // assertNull(vs.getString());
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException ex) {
            // This is the original behaviour
        }
        vs.setDate(format.parse("2006/06/07 01:02:03.004"));
        Assert.assertEquals("2006/06/07 01:02:03.004", vs.getString());
    }

    /**
     * Test booleans in ValueString
     */
    @Test
    public void testSetBoolean() {
        ValueString vs = new ValueString();
        vs.setBoolean(false);
        Assert.assertEquals("N", vs.getString());
        vs.setBoolean(true);
        Assert.assertEquals("Y", vs.getString());
    }

    @Test
    public void testSetInteger() {
        ValueString vs = new ValueString();
        vs.setInteger((-1L));
        Assert.assertEquals("-1", vs.getString());
        vs.setInteger(0L);
        Assert.assertEquals("0", vs.getString());
        vs.setInteger(1L);
        Assert.assertEquals("1", vs.getString());
    }

    @Test
    public void testSetBigNumber() {
        ValueString vs = new ValueString();
        try {
            vs.setBigNumber(null);
            // assertNull(vs.getString());
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException ex) {
            // This is the original behaviour
        }
        vs.setBigNumber(BigDecimal.ZERO);
        Assert.assertEquals("0", vs.getString());
    }

    @Test
    public void testClone() {
        ValueString vs = new ValueString("Boden");
        ValueString vs1 = ((ValueString) (vs.clone()));
        Assert.assertFalse(vs.equals(vs1));// not the same object, equals not implement

        Assert.assertTrue((vs != vs1));// not the same object

        Assert.assertEquals(vs.getString(), vs1.getString());
    }
}

