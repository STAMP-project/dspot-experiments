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
package org.lealone.test.db;


import ErrorCode.DATABASE_ALREADY_EXISTS_1;
import org.junit.Assert;
import org.junit.Test;
import org.lealone.common.exceptions.DbException;
import org.lealone.db.Database;
import org.lealone.db.LealoneDatabase;


public class DatabaseTest extends DbObjectTestBase {
    @Test
    public void run() {
        executeUpdate("CREATE DATABASE IF NOT EXISTS CreateDatabaseTest1");
        asserts("CreateDatabaseTest1");
        executeUpdate("CREATE DATABASE IF NOT EXISTS CreateDatabaseTest2 PARAMETERS(OPTIMIZE_DISTINCT=true, PERSISTENT=false)");
        asserts("CreateDatabaseTest2");
        executeUpdate("CREATE DATABASE IF NOT EXISTS CreateDatabaseTest3 PARAMETERS()");
        asserts("CreateDatabaseTest3");
        try {
            executeUpdate("CREATE DATABASE CreateDatabaseTest1");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DbException));
            Assert.assertEquals(DATABASE_ALREADY_EXISTS_1, getErrorCode());
        }
        try {
            executeUpdate(("CREATE DATABASE " + (LealoneDatabase.NAME)));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DbException));
            Assert.assertEquals(DATABASE_ALREADY_EXISTS_1, getErrorCode());
        }
        String dbName = "CreateDatabaseTest4";
        executeUpdate((("CREATE DATABASE IF NOT EXISTS " + dbName)// 
         + " RUN MODE REPLICATION WITH REPLICATION STRATEGY (class: 'SimpleStrategy', replication_factor:1)"));
        Database db = LealoneDatabase.getInstance().findDatabase(dbName);
        Assert.assertNotNull(db);
        Assert.assertNotNull(db.getReplicationProperties());
        Assert.assertTrue(db.getReplicationProperties().containsKey("class"));
        executeUpdate((("ALTER DATABASE " + dbName)// 
         + " RUN MODE REPLICATION WITH REPLICATION STRATEGY (class: 'SimpleStrategy', replication_factor:2)"));
        db = LealoneDatabase.getInstance().findDatabase(dbName);
        Assert.assertNotNull(db);
        Assert.assertNotNull(db.getReplicationProperties());
        Assert.assertEquals("2", db.getReplicationProperties().get("replication_factor"));
        // executeUpdate("DROP DATABASE IF EXISTS " + dbName);
    }
}

