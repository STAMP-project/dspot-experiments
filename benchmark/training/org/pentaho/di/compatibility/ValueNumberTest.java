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


import Value.VALUE_TYPE_NUMBER;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of ValueNumber.
 *
 * @author Sven Boden
 */
public class ValueNumberTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * Constructor test 1.
     */
    @Test
    public void testConstructor1() {
        ValueNumber vs = new ValueNumber();
        Assert.assertEquals(VALUE_TYPE_NUMBER, vs.getType());
        Assert.assertEquals("Number", vs.getTypeDesc());
        Assert.assertEquals(0.0, vs.getNumber(), 0.001);
        Assert.assertEquals((-1), vs.getLength());
        Assert.assertEquals((-1), vs.getPrecision());
        ValueNumber vs1 = new ValueNumber(10.0);
        vs1.setLength(2);
        Assert.assertEquals(2, vs1.getLength());
        Assert.assertEquals((-1), vs1.getPrecision());
        vs1.setLength(4, 2);
        Assert.assertEquals(4, vs1.getLength());
        Assert.assertEquals(2, vs1.getPrecision());
        vs1.setPrecision(3);
        Assert.assertEquals(3, vs1.getPrecision());
    }

    /**
     * Test the getters of ValueNumber
     */
    @Test
    public void testGetters() {
        ValueNumber vs1 = new ValueNumber((-4.0));
        ValueNumber vs2 = new ValueNumber(0.0);
        ValueNumber vs3 = new ValueNumber(3.0);
        ValueNumber vs4 = new ValueNumber(3.5);
        Assert.assertEquals(true, vs1.getBoolean());
        Assert.assertEquals(false, vs2.getBoolean());
        Assert.assertEquals(true, vs3.getBoolean());
        Assert.assertEquals("-4.0", vs1.getString());
        Assert.assertEquals("0.0", vs2.getString());
        Assert.assertEquals("3.0", vs3.getString());
        Assert.assertEquals((-4.0), vs1.getNumber(), 0.001);
        Assert.assertEquals(0.0, vs2.getNumber(), 0.001);
        Assert.assertEquals(3.0, vs3.getNumber(), 0.001);
        Assert.assertEquals((-4L), vs1.getInteger());
        Assert.assertEquals(0L, vs2.getInteger());
        Assert.assertEquals(3L, vs3.getInteger());
        Assert.assertEquals(4L, vs4.getInteger());
        Assert.assertEquals(BigDecimal.valueOf((-4.0)), vs1.getBigNumber());
        Assert.assertEquals(BigDecimal.valueOf(0.0), vs2.getBigNumber());
        Assert.assertEquals(BigDecimal.valueOf(3.0), vs3.getBigNumber());
        Assert.assertEquals(BigDecimal.valueOf(3.5), vs4.getBigNumber());
        Assert.assertEquals((-4L), vs1.getDate().getTime());
        Assert.assertEquals(0L, vs2.getDate().getTime());
        Assert.assertEquals(3L, vs3.getDate().getTime());
        Assert.assertEquals(3L, vs4.getDate().getTime());
        Assert.assertEquals(new Double((-4.0)), vs1.getSerializable());
        Assert.assertEquals(new Double(0.0), vs2.getSerializable());
        Assert.assertEquals(new Double(3.0), vs3.getSerializable());
    }

    /**
     * Test the setters of ValueNumber
     */
    @Test
    public void testSetters() {
        ValueNumber vs = new ValueNumber(0.0);
        vs.setString("unknown");
        Assert.assertEquals(0.0, vs.getNumber(), 0.001);
        vs.setString("-4.0");
        Assert.assertEquals((-4.0), vs.getNumber(), 0.001);
        vs.setString("0.0");
        Assert.assertEquals(0.0, vs.getNumber(), 0.001);
        vs.setString("0");
        Assert.assertEquals(0.0, vs.getNumber(), 0.001);
        vs.setString("3.0");
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.US);
        Date dt = null;
        try {
            dt = format.parse("2006/06/07 01:02:03.004");
        } catch (ParseException ex) {
            dt = null;
        }
        vs.setDate(dt);
        Assert.assertEquals(1.149634923004E12, vs.getNumber(), 5.0E8);
        vs.setBoolean(true);
        Assert.assertEquals(1.0, vs.getNumber(), 0.1);
        vs.setBoolean(false);
        Assert.assertEquals(0.0, vs.getNumber(), 0.1);
        vs.setNumber(5.0);
        Assert.assertEquals(5.0, vs.getNumber(), 0.1);
        vs.setNumber(0.0);
        Assert.assertEquals(0.0, vs.getNumber(), 0.1);
        vs.setInteger(5L);
        Assert.assertEquals(5.0, vs.getNumber(), 0.1);
        vs.setInteger(0L);
        Assert.assertEquals(0.0, vs.getNumber(), 0.1);
        vs.setBigNumber(new BigDecimal(5));
        Assert.assertEquals(5.0, vs.getNumber(), 0.1);
        vs.setBigNumber(new BigDecimal(0));
        Assert.assertEquals(0.0, vs.getNumber(), 0.1);
        // setSerializable is ignored ???
    }
}

