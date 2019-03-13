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


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Test class for the basic functionality of the blocking & batching row set.
 *
 * @author Matt Casters
 */
public class BlockingBatchingRowSetTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     * The basic stuff.
     */
    @Test
    public void testBasicCreation() {
        RowSet set = new BlockingBatchingRowSet(10);
        Assert.assertTrue((!(set.isDone())));
        Assert.assertEquals(0, set.size());
    }

    /**
     * Functionality test.
     */
    @Test
    public void testFuntionality1() {
        BlockingBatchingRowSet set = new BlockingBatchingRowSet(10);
        RowMetaInterface rm = createRowMetaInterface();
        List<Object[]> rows = new ArrayList<Object[]>();
        for (int i = 0; i < 5; i++) {
            rows.add(new Object[]{ new Long(i) });
        }
        Assert.assertEquals(0, set.size());
        // Pop off row. This should return null (no row available: has a timeout)
        // 
        Object[] r = set.getRow();
        Assert.assertNull(r);
        // Add rows. set doesn't report rows, batches them
        // this batching row set has 2 buffers with 2 rows, the 5th row will cause the rows to be exposed.
        // 
        int index = 0;
        while (index < 4) {
            set.putRow(rm, rows.get((index++)));
            Assert.assertEquals(0, set.size());
        } 
        set.putRow(rm, rows.get((index++)));
        Assert.assertEquals(5, set.size());
        // Signal done...
        // 
        set.setDone();
        Assert.assertTrue(set.isDone());
        // Get a row back...
        // 
        r = set.getRow();
        Assert.assertNotNull(r);
        Assert.assertArrayEquals(rows.get(0), r);
        // Get a row back...
        // 
        r = set.getRow();
        Assert.assertNotNull(r);
        Assert.assertArrayEquals(rows.get(1), r);
        // Get a row back...
        // 
        r = set.getRow();
        Assert.assertNotNull(r);
        Assert.assertArrayEquals(rows.get(2), r);
    }
}

