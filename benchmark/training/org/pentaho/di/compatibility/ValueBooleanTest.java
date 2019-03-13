/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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


import Value.VALUE_TYPE_BOOLEAN;
import java.math.BigDecimal;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Test class for the basic functionality of ValueBoolean.
 *
 * @author Sven Boden
 */
public class ValueBooleanTest extends TestCase {
    /**
     * Constructor test 1.
     */
    public void testConstructor1() {
        ValueBoolean vs = new ValueBoolean();
        TestCase.assertEquals(VALUE_TYPE_BOOLEAN, vs.getType());
        TestCase.assertEquals("Boolean", vs.getTypeDesc());
        TestCase.assertEquals(false, vs.getBoolean());
        TestCase.assertEquals((-1), vs.getLength());
        TestCase.assertEquals((-1), vs.getPrecision());
        ValueBoolean vs1 = new ValueBoolean(true);
        TestCase.assertEquals(VALUE_TYPE_BOOLEAN, vs1.getType());
        // Length and precision are ignored
        vs1.setLength(2);
        TestCase.assertEquals((-1), vs1.getLength());
        TestCase.assertEquals((-1), vs1.getPrecision());
        vs1.setLength(2, 2);
        TestCase.assertEquals((-1), vs1.getLength());
        TestCase.assertEquals((-1), vs1.getPrecision());
        vs1.setPrecision(2);
        TestCase.assertEquals((-1), vs1.getLength());
        TestCase.assertEquals((-1), vs1.getPrecision());
    }

    /**
     * Test the getters of ValueBoolean
     */
    public void testGetters() {
        ValueBoolean vs1 = new ValueBoolean(true);
        ValueBoolean vs2 = new ValueBoolean(false);
        TestCase.assertEquals(true, vs1.getBoolean());
        TestCase.assertEquals(false, vs2.getBoolean());
        TestCase.assertEquals("Y", vs1.getString());
        TestCase.assertEquals("N", vs2.getString());
        TestCase.assertEquals(1.0, vs1.getNumber(), 0.001);
        TestCase.assertEquals(0.0, vs2.getNumber(), 0.001);
        TestCase.assertEquals(1L, vs1.getInteger());
        TestCase.assertEquals(0L, vs2.getInteger());
        TestCase.assertEquals(new BigDecimal(1), vs1.getBigNumber());
        TestCase.assertEquals(new BigDecimal(0), vs2.getBigNumber());
        TestCase.assertNull(vs1.getDate());
        TestCase.assertNull(vs2.getDate());
        TestCase.assertEquals(new Boolean(true), vs1.getSerializable());
        TestCase.assertEquals(new Boolean(false), vs2.getSerializable());
    }

    /**
     * Test the setters of ValueBoolean
     */
    public void testSetters() {
        ValueBoolean vs = new ValueBoolean(true);
        vs.setString("unknown");
        TestCase.assertEquals(false, vs.getBoolean());
        vs.setString("y");
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setString("Y");
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setString("yes");
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setString("YES");
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setString("true");
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setString("TRUE");
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setString("false");
        TestCase.assertEquals(false, vs.getBoolean());
        vs.setDate(new Date());
        TestCase.assertEquals(false, vs.getBoolean());
        vs.setBoolean(true);
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setBoolean(false);
        TestCase.assertEquals(false, vs.getBoolean());
        vs.setNumber(5.0);
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setNumber(0.0);
        TestCase.assertEquals(false, vs.getBoolean());
        vs.setInteger(5L);
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setInteger(0L);
        TestCase.assertEquals(false, vs.getBoolean());
        vs.setBigNumber(new BigDecimal(5));
        TestCase.assertEquals(true, vs.getBoolean());
        vs.setBigNumber(new BigDecimal(0));
        TestCase.assertEquals(false, vs.getBoolean());
        // setSerializable is ignored ???
    }

    /**
     * Test clone()
     */
    public void testClone() {
        ValueBoolean vs1 = new ValueBoolean(true);
        ValueBoolean cloneVs1 = ((ValueBoolean) (vs1.clone()));
        TestCase.assertTrue(((cloneVs1.getBoolean()) == (vs1.getBoolean())));
        TestCase.assertFalse((cloneVs1 == vs1));
        ValueBoolean vs2 = new ValueBoolean(false);
        ValueBoolean cloneVs2 = ((ValueBoolean) (vs2.clone()));
        TestCase.assertTrue(((cloneVs2.getBoolean()) == (vs2.getBoolean())));
        TestCase.assertFalse((cloneVs2 == vs2));
    }

    public void testSetBigNumber() {
        ValueBoolean vs1 = new ValueBoolean(true);
        vs1.setBigNumber(new BigDecimal("1.7976E308"));
        TestCase.assertTrue(vs1.getBoolean());
        vs1.setBigNumber(new BigDecimal("234"));
        TestCase.assertTrue(vs1.getBoolean());
        vs1.setBigNumber(new BigDecimal("-234"));
        TestCase.assertTrue(vs1.getBoolean());
        vs1.setBigNumber(new BigDecimal("0"));
        TestCase.assertFalse(vs1.getBoolean());
    }
}

