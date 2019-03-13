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
package org.pentaho.di.core;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.row.RowMeta;


public class QueueRowSetTest {
    Object[] row;

    QueueRowSet rowSet;

    @Test
    public void testPutRow() throws Exception {
        rowSet.putRow(new RowMeta(), row);
        Assert.assertSame(row, rowSet.getRow());
    }

    @Test
    public void testPutRowWait() throws Exception {
        rowSet.putRowWait(new RowMeta(), row, 1, TimeUnit.SECONDS);
        Assert.assertSame(row, rowSet.getRowWait(1, TimeUnit.SECONDS));
    }

    @Test
    public void testGetRowImmediate() throws Exception {
        rowSet.putRow(new RowMeta(), row);
        Assert.assertSame(row, rowSet.getRowImmediate());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, rowSet.size());
        rowSet.putRow(new RowMeta(), row);
        Assert.assertEquals(1, rowSet.size());
        rowSet.putRow(new RowMeta(), row);
        Assert.assertEquals(2, rowSet.size());
        rowSet.clear();
        Assert.assertEquals(0, rowSet.size());
    }
}

