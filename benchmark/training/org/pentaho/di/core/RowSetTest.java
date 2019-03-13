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
package org.pentaho.di.core;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of RowSet.
 *
 * @author Sven Boden
 */
public class RowSetTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * The basic stuff.
     */
    @Test
    public void testBasicCreation() {
        RowSet set = new BlockingRowSet(10);
        Assert.assertTrue((!(set.isDone())));
        // TODO assertTrue(set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(0, set.size());
    }

    /**
     * Functionality test.
     */
    @Test
    public void testFuntionality1() {
        RowSet set = new BlockingRowSet(3);
        RowMetaInterface rm = createRowMetaInterface();
        Object[] r1 = new Object[]{ new Long(1L) };
        Object[] r2 = new Object[]{ new Long(2L) };
        Object[] r3 = new Object[]{ new Long(3L) };
        Object[] r4 = new Object[]{ new Long(4L) };
        Object[] r5 = new Object[]{ new Long(5L) };
        // TODO assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.size());
        // Add first row. State 1
        set.putRow(rm, r1);
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(1, set.size());
        // Add another row. State: 1 2
        set.putRow(rm, r2);
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(2, set.size());
        // Pop off row. State: 2
        Object[] r = set.getRow();
        int i = rm.indexOfValue("ROWNR");
        Assert.assertEquals(1L, ((Long) (r[i])).longValue());
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(1, set.size());
        // Add another row. State: 2 3
        set.putRow(rm, r3);
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(2, set.size());
        // Add another row. State: 2 3 4
        set.putRow(rm, r4);
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(set.isFull());
        Assert.assertEquals(3, set.size());
        /**
         * *******************************************************************
         * This was made in more restrict in v2.5.0 with a new RowSet implementation. After v2.5.0 you may not try to put
         * more rows in a rowset then it can hold (this functionality was also never used in PDI anyway).
         *
         * // Add another row. State: 2 3 4 5 // Note that we can still add rows after the set is full. set.putRow(r5);
         * assertTrue(!set.isEmpty()); assertTrue(set.isFull()); assertEquals(4, set.size());
         * *******************************************************************
         */
        // Pop off row. State: 3 4
        r = set.getRow();
        i = rm.indexOfValue("ROWNR");
        Assert.assertEquals(2L, ((Long) (r[i])).longValue());
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(2, set.size());
        // Add another row. State: 3 4 5
        set.putRow(rm, r5);
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(set.isFull());
        Assert.assertEquals(3, set.size());
        // Pop off row. State: 4 5
        r = set.getRow();
        i = rm.indexOfValue("ROWNR");
        Assert.assertEquals(3L, ((Long) (r[i])).longValue());
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(2, set.size());
        // Pop off row. State: 5
        r = set.getRow();
        i = rm.indexOfValue("ROWNR");
        Assert.assertEquals(4L, ((Long) (r[i])).longValue());
        // TODO assertTrue(!set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(1, set.size());
        // Pop off row. State:
        r = set.getRow();
        i = rm.indexOfValue("ROWNR");
        Assert.assertEquals(5L, ((Long) (r[i])).longValue());
        // TODO assertTrue(set.isEmpty());
        // TODO assertTrue(!set.isFull());
        Assert.assertEquals(0, set.size());
        /**
         * *******************************************************************
         * This was changed v2.5.0 with a new RowSet // Pop off row. State: try { r = set.getRow();
         * fail("expected NoSuchElementException"); } catch ( IndexOutOfBoundsException ex ) { } assertTrue(set.isEmpty());
         * assertTrue(!set.isFull()); assertEquals(0, set.size());
         * ********************************************************************
         */
    }

    /**
     * Names test. Just for completeness.
     */
    @Test
    public void testNames() {
        RowSet set = new BlockingRowSet(3);
        set.setThreadNameFromToCopy("from", 2, "to", 3);
        Assert.assertEquals("from", set.getOriginStepName());
        Assert.assertEquals(2, set.getOriginStepCopy());
        Assert.assertEquals("to", set.getDestinationStepName());
        Assert.assertEquals(3, set.getDestinationStepCopy());
        Assert.assertEquals(set.toString(), set.getName());
        Assert.assertEquals("from.2 - to.3", set.getName());
    }
}

