/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import org.junit.Assert;
import org.junit.Test;


public class TableIdTest {
    private static final TableId TABLE = TableId.of("dataset", "table");

    private static final TableId TABLE_COMPLETE = TableId.of("project", "dataset", "table");

    @Test
    public void testOf() {
        Assert.assertEquals(null, TableIdTest.TABLE.getProject());
        Assert.assertEquals("dataset", TableIdTest.TABLE.getDataset());
        Assert.assertEquals("table", TableIdTest.TABLE.getTable());
        Assert.assertEquals("project", TableIdTest.TABLE_COMPLETE.getProject());
        Assert.assertEquals("dataset", TableIdTest.TABLE_COMPLETE.getDataset());
        Assert.assertEquals("table", TableIdTest.TABLE_COMPLETE.getTable());
    }

    @Test
    public void testEquals() {
        compareTableIds(TableIdTest.TABLE, TableId.of("dataset", "table"));
        compareTableIds(TableIdTest.TABLE_COMPLETE, TableId.of("project", "dataset", "table"));
    }

    @Test
    public void testToPbAndFromPb() {
        compareTableIds(TableIdTest.TABLE, TableId.fromPb(TableIdTest.TABLE.toPb()));
        compareTableIds(TableIdTest.TABLE_COMPLETE, TableId.fromPb(TableIdTest.TABLE_COMPLETE.toPb()));
    }

    @Test
    public void testSetProjectId() {
        TableId differentProjectTable = TableId.of("differentProject", "dataset", "table");
        Assert.assertEquals(differentProjectTable, TableIdTest.TABLE.setProjectId("differentProject"));
    }
}

