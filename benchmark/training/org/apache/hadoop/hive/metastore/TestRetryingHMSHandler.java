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


import ColumnType.STRING_TYPE_NAME;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * TestRetryingHMSHandler. Test case for
 * {@link org.apache.hadoop.hive.metastore.RetryingHMSHandler}
 */
@Category(MetastoreCheckinTest.class)
public class TestRetryingHMSHandler {
    private Configuration conf;

    private HiveMetaStoreClient msc;

    // Create a database and a table in that database.  Because the AlternateFailurePreListener is
    // being used each attempt to create something should require two calls by the RetryingHMSHandler
    @Test
    public void testRetryingHMSHandler() throws Exception {
        String dbName = "hive4159";
        String tblName = "tmptbl";
        new DatabaseBuilder().setName(dbName).create(msc, conf);
        Assert.assertEquals(2, AlternateFailurePreListener.getCallCount());
        new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("c1", STRING_TYPE_NAME).create(msc, conf);
        Assert.assertEquals(4, AlternateFailurePreListener.getCallCount());
    }
}

