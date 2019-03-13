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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of ValueNumber.
 *
 * @author Sven Boden
 */
public class ValueBigNumberTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * Constructor test 1.
     */
    @Test
    public void testConstructor1() {
        ValueBigNumber vs = new ValueBigNumber();
        Assert.assertEquals(VALUE_TYPE_BIGNUMBER, vs.getType());
        Assert.assertEquals("BigNumber", vs.getTypeDesc());
        Assert.assertNull(vs.getBigNumber());
        Assert.assertEquals((-1), vs.getLength());
        Assert.assertEquals((-1), vs.getPrecision());
        ValueBigNumber vs1 = new ValueBigNumber(BigDecimal.ONE);
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
     * Test the getters of ValueBigNumber
     */
    @Test
    public void testGetters() {
        ValueBigNumber vs1 = new ValueBigNumber();
        ValueBigNumber vs2 = new ValueBigNumber(BigDecimal.ZERO);
        ValueBigNumber vs3 = new ValueBigNumber(BigDecimal.ONE);
        Assert.assertEquals(false, vs1.getBoolean());
        Assert.assertEquals(false, vs2.getBoolean());
        Assert.assertEquals(true, vs3.getBoolean());
        Assert.assertEquals(null, vs1.getString());
        Assert.assertEquals("0", vs2.getString());
        Assert.assertEquals("1", vs3.getString());
        Assert.assertEquals(0.0, vs1.getNumber(), 0.001);
        Assert.assertEquals(0.0, vs2.getNumber(), 0.001);
        Assert.assertEquals(1.0, vs3.getNumber(), 0.001);
        Assert.assertEquals(0L, vs1.getInteger());
        Assert.assertEquals(0L, vs2.getInteger());
        Assert.assertEquals(1L, vs3.getInteger());
        Assert.assertNull(vs1.getBigNumber());
        Assert.assertEquals(new BigDecimal(0L), vs2.getBigNumber());
        Assert.assertEquals(new BigDecimal(1L), vs3.getBigNumber());
        Assert.assertNull(vs1.getDate());
        Assert.assertEquals(0L, vs2.getDate().getTime());
        Assert.assertEquals(1L, vs3.getDate().getTime());
        Assert.assertNull(vs1.getSerializable());
        Assert.assertEquals(BigDecimal.ZERO, vs2.getSerializable());
        Assert.assertEquals(BigDecimal.ONE, vs3.getSerializable());
    }

    /**
     * Test the setters of ValueBigNumber
     */
    @Test
    public void testSetters() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        ValueBigNumber vs = new ValueBigNumber();
        vs.setString("unknown");
        Assert.assertEquals(BigDecimal.ZERO, vs.getBigNumber());
        vs.setString("-4.0");
        Assert.assertEquals(BigDecimal.valueOf((-4.0)), vs.getBigNumber());
        vs.setString("0.0");
        Assert.assertEquals(BigDecimal.valueOf(0.0), vs.getBigNumber());
        vs.setString("0");
        Assert.assertEquals(BigDecimal.ZERO, vs.getBigNumber());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.US);
        Date dt = null;
        try {
            dt = format.parse("2006/06/07 01:02:03.004");
        } catch (ParseException ex) {
            dt = null;
        }
        vs.setDate(dt);
        Assert.assertEquals(new BigDecimal("1149634923004"), vs.getBigNumber());
        vs.setBoolean(true);
        Assert.assertEquals(BigDecimal.ONE, vs.getBigNumber());
        vs.setBoolean(false);
        Assert.assertEquals(BigDecimal.ZERO, vs.getBigNumber());
        vs.setNumber(5.0);
        Assert.assertEquals(BigDecimal.valueOf(5.0), vs.getBigNumber());
        vs.setNumber(0.0);
        Assert.assertEquals(BigDecimal.valueOf(0.0), vs.getBigNumber());
        vs.setInteger(5L);
        Assert.assertEquals(BigDecimal.valueOf(5L), vs.getBigNumber());
        vs.setInteger(0L);
        Assert.assertEquals(BigDecimal.ZERO, vs.getBigNumber());
        vs.setBigNumber(new BigDecimal(5));
        Assert.assertEquals(5.0, vs.getNumber(), 0.1);
        vs.setBigNumber(new BigDecimal(0));
        Assert.assertEquals(0.0, vs.getNumber(), 0.1);
        // setSerializable is ignored ???
    }
}

