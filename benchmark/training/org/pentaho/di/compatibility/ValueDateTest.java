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


import Value.VALUE_TYPE_DATE;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of ValueDate.
 *
 * @author Sven Boden
 */
public class ValueDateTest {
    private Date dt = null;

    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * Constructor test 1.
     */
    @Test
    public void testConstructor1() {
        ValueDate vs = new ValueDate();
        Assert.assertEquals(VALUE_TYPE_DATE, vs.getType());
        Assert.assertEquals("Date", vs.getTypeDesc());
        Assert.assertNull(vs.getDate());
        Assert.assertEquals((-1), vs.getLength());
        Assert.assertEquals((-1), vs.getPrecision());
        ValueDate vs1 = new ValueDate(dt);
        // Length and precision are ignored
        vs1.setLength(2);
        Assert.assertEquals((-1), vs1.getLength());
        Assert.assertEquals((-1), vs1.getPrecision());
        vs1.setLength(4, 2);
        Assert.assertEquals((-1), vs1.getLength());
        Assert.assertEquals(2, vs1.getPrecision());
        vs1.setPrecision(3);
        Assert.assertEquals(3, vs1.getPrecision());
    }

    /**
     * Test the getters of ValueDate.
     */
    @Test
    public void testGetters() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        ValueDate vs1 = new ValueDate();
        ValueDate vs2 = new ValueDate(dt);
        Assert.assertEquals(false, vs1.getBoolean());
        Assert.assertEquals(false, vs2.getBoolean());
        Assert.assertNull(vs1.getString());
        Assert.assertEquals("2006/06/07 01:02:03.004", vs2.getString());
        Assert.assertEquals(0.0, vs1.getNumber(), 0.001);
        // 1.149634923004E12
        // 1.149656523004E12
        Assert.assertEquals(1.149634923004E12, vs2.getNumber(), 1.0E9);
        Assert.assertEquals(0L, vs1.getInteger());
        Assert.assertEquals(1149634923004L, vs2.getInteger());
        Assert.assertEquals(BigDecimal.ZERO, vs1.getBigNumber());
        Assert.assertEquals(new BigDecimal(1149634923004L), vs2.getBigNumber());
        Assert.assertNull(vs1.getDate());
        Assert.assertEquals(1149634923004L, vs2.getDate().getTime());
        Assert.assertNull(vs1.getSerializable());
        Assert.assertEquals(dt, vs2.getSerializable());
    }

    /**
     * Test the setters of ValueDate.
     */
    @Test
    public void testSetters() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        ValueDate vs = new ValueDate();
        try {
            vs.setString(null);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException ex) {
        }
        vs.setString("unknown");
        Assert.assertNull(vs.getDate());
        vs.setString("2006/06/07 01:02:03.004");
        Assert.assertEquals(dt, vs.getDate());
        vs.setDate(dt);
        Assert.assertEquals(dt, vs.getDate());
        vs.setBoolean(true);
        Assert.assertNull(vs.getDate());
        vs.setBoolean(false);
        Assert.assertNull(vs.getDate());
        vs.setNumber(dt.getTime());
        Assert.assertEquals(dt, vs.getDate());
        vs.setInteger(dt.getTime());
        Assert.assertEquals(dt, vs.getDate());
        vs.setBigNumber(new BigDecimal(dt.getTime()));
        Assert.assertEquals(dt, vs.getDate());
        // setSerializable is ignored ???
    }
}

