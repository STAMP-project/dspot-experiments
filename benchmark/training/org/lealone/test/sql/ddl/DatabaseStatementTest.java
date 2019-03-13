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
package org.lealone.test.sql.ddl;


import LealoneDatabase.NAME;
import org.junit.Assert;
import org.junit.Test;
import org.lealone.db.LealoneDatabase;
import org.lealone.test.sql.SqlTestBase;


public class DatabaseStatementTest extends SqlTestBase {
    public DatabaseStatementTest() {
        super(NAME);
    }

    @Test
    public void run() throws Exception {
        // ????lealone???
        try {
            executeUpdate(("DROP DATABASE " + (LealoneDatabase.NAME)));
            Assert.fail();
        } catch (Exception e) {
            printException(e);
        }
        executeUpdate("CREATE DATABASE IF NOT EXISTS DatabaseStatementTest");
        new DatabaseStatementTest.NoRightTest("DatabaseStatementTest").runTest();
        executeUpdate("DROP DATABASE IF EXISTS DatabaseStatementTest DELETE FILES");
    }

    private class NoRightTest extends SqlTestBase {
        public NoRightTest(String dbName) {
            super(dbName);
        }

        @Override
        protected void test() throws Exception {
            // ??????????lealone????????CREATE/ALTER/DROP DATABASE??
            try {
                executeUpdate("CREATE DATABASE IF NOT EXISTS db1");
                Assert.fail();
            } catch (Exception e) {
                printException(e);
            }
            try {
                executeUpdate("ALTER DATABASE DatabaseStatementTest RUN MODE REPLICATION");
                Assert.fail();
            } catch (Exception e) {
                printException(e);
            }
            try {
                executeUpdate("DROP DATABASE IF EXISTS DatabaseStatementTest");
                Assert.fail();
            } catch (Exception e) {
                printException(e);
            }
        }
    }
}

