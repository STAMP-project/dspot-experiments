/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore;


import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.TableMeta;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MetastoreUnitTest.class)
public class TestObjectStoreStatementVerify {
    private ObjectStore objectStore = null;

    private Configuration conf = null;

    private final String DB1 = "db1";

    private final String TBL1 = "db1_tbl1";

    @Test
    public void testGetTableMetaFetchGroup() throws InvalidObjectException, InvalidOperationException, MetaException {
        objectStore = createObjectStore();
        Database db = new Database(DB1, "description", "locurl", null);
        db.setCatalogName("hive");
        objectStore.createDatabase(db);
        objectStore.createTable(makeTable(DB1, TBL1));
        List<TableMeta> tableMeta = objectStore.getTableMeta("hive", "*", "*", Collections.emptyList());
        Assert.assertEquals("Number of items for tableMeta is incorrect", 1, tableMeta.size());
        Assert.assertEquals("Table name incorrect", TBL1, tableMeta.get(0).getTableName());
        Assert.assertEquals("Db name incorrect", DB1, tableMeta.get(0).getDbName());
    }
}

