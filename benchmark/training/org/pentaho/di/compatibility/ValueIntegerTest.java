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


import Value.VALUE_TYPE_INTEGER;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of ValueInteger.
 *
 * @author Sven Boden
 */
public class ValueIntegerTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * Constructor test 1.
     */
    @Test
    public void testConstructor1() {
        ValueInteger vs = new ValueInteger();
        Assert.assertEquals(VALUE_TYPE_INTEGER, vs.getType());
        Assert.assertEquals("Integer", vs.getTypeDesc());
        Assert.assertEquals(0, vs.getInteger());
        Assert.assertEquals((-1), vs.getLength());
        Assert.assertEquals(0, vs.getPrecision());
        // Precision is ignored
        ValueInteger vs1 = new ValueInteger(10);
        vs1.setLength(2);
        Assert.assertEquals(2, vs1.getLength());
        Assert.assertEquals(0, vs1.getPrecision());
        vs1.setLength(4, 2);
        Assert.assertEquals(4, vs1.getLength());
        Assert.assertEquals(0, vs1.getPrecision());
        vs1.setPrecision(3);
        Assert.assertEquals(0, vs1.getPrecision());
    }

    /**
     * Test the getters of ValueInteger
     */
    @Test
    public void testGetters() {
        ValueInteger vs1 = new ValueInteger((-4));
        ValueInteger vs2 = new ValueInteger(0);
        ValueInteger vs3 = new ValueInteger(3);
        Assert.assertTrue(vs1.getBoolean());
        Assert.assertFalse(vs2.getBoolean());
        Assert.assertTrue(vs3.getBoolean());
        Assert.assertEquals("-4", vs1.getString());
        Assert.assertEquals("0", vs2.getString());
        Assert.assertEquals("3", vs3.getString());
        Assert.assertEquals((-4.0), vs1.getNumber(), 0.001);
        Assert.assertEquals(0.0, vs2.getNumber(), 0.001);
        Assert.assertEquals(3.0, vs3.getNumber(), 0.001);
        Assert.assertEquals((-4L), vs1.getInteger());
        Assert.assertEquals(0L, vs2.getInteger());
        Assert.assertEquals(3L, vs3.getInteger());
        Assert.assertEquals(new BigDecimal((-4L)), vs1.getBigNumber());
        Assert.assertEquals(new BigDecimal(0L), vs2.getBigNumber());
        Assert.assertEquals(new BigDecimal(3L), vs3.getBigNumber());
        Assert.assertEquals((-4L), vs1.getDate().getTime());
        Assert.assertEquals(0L, vs2.getDate().getTime());
        Assert.assertEquals(3L, vs3.getDate().getTime());
        Assert.assertEquals(new Long((-4L)), vs1.getSerializable());
        Assert.assertEquals(new Long(0L), vs2.getSerializable());
        Assert.assertEquals(new Long(3L), vs3.getSerializable());
    }

    /**
     * Test the setters of ValueInteger
     */
    @Test
    public void testSetters() {
        ValueInteger vs = new ValueInteger(0);
        vs.setString("unknown");
        Assert.assertEquals(0, vs.getInteger());
        vs.setString("-4.0");
        Assert.assertEquals(0, vs.getInteger());
        vs.setString("-4");
        Assert.assertEquals((-4), vs.getInteger());
        vs.setString("0");
        Assert.assertEquals(0, vs.getInteger());
        vs.setString("3");
        Assert.assertEquals(3, vs.getInteger());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z");
        Date dt = null;
        try {
            dt = format.parse("2006/06/07 01:02:03.004 CET");
        } catch (ParseException ex) {
            dt = null;
        }
        vs.setDate(dt);
        // Epoch time conversion
        Assert.assertEquals(1149638523004L, vs.getInteger());
        vs.setBoolean(true);
        Assert.assertEquals(1, vs.getInteger());
        vs.setBoolean(false);
        Assert.assertEquals(0, vs.getInteger());
        vs.setNumber(5);
        Assert.assertEquals(5, vs.getInteger());
        vs.setNumber(0);
        Assert.assertEquals(0, vs.getInteger());
        vs.setInteger(5L);
        Assert.assertEquals(5, vs.getInteger());
        vs.setInteger(0L);
        Assert.assertEquals(0, vs.getInteger());
        vs.setBigNumber(new BigDecimal(5));
        Assert.assertEquals(5, vs.getInteger());
        vs.setBigNumber(new BigDecimal(0));
        Assert.assertEquals(0, vs.getInteger());
        // setSerializable is ignored ???
    }
}

