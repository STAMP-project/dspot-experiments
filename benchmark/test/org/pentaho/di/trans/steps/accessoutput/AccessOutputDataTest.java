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
package org.pentaho.di.trans.steps.accessoutput;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AccessOutputDataTest {
    AccessOutputData data;

    File mdbFile;

    @Test
    public void testCreateDatabase() throws IOException {
        Assert.assertNull(data.db);
        data.createDatabase(mdbFile);
        Assert.assertNotNull(data.db);
        Assert.assertTrue(mdbFile.exists());
        Assert.assertNull(data.table);
        data.truncateTable();
        Assert.assertNull(data.table);
        data.closeDatabase();
    }

    @Test
    public void testCreateTable() throws IOException {
        data.createDatabase(mdbFile);
        data.createTable("thisSampleTable", generateRowMeta());
        Assert.assertTrue(data.db.getTableNames().contains("thisSampleTable"));
        data.closeDatabase();
    }

    @Test
    public void testTruncateTable() throws IOException {
        data.createDatabase(mdbFile);
        data.createTable("TruncatingThisTable", generateRowMeta());
        data.addRowsToTable(generateRowData(10));
        Assert.assertEquals(10, data.table.getRowCount());
        data.truncateTable();
        Assert.assertEquals(0, data.table.getRowCount());
        data.addRowToTable(generateRowData(1).get(0));
        Assert.assertEquals(1, data.table.getRowCount());
        data.closeDatabase();
    }
}

