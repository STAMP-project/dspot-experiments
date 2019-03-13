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
package org.lealone.test.runmode;


import org.junit.Test;
import org.lealone.test.sql.SqlTestBase;


// private static class QueryTest extends SqlTestBase {
// 
// public QueryTest(String dbName) {
// super(dbName);
// }
// 
// @Override
// protected void test() throws Exception {
// executeUpdate("insert into test(f1, f2, f3) values(3,2,3)");
// sql = "select * from test where _rowid_ = 10";
// printResultSet();
// }
// }
public class ClientServerToShardingTest extends RunModeTest {
    public ClientServerToShardingTest() {
        setHost("127.0.0.1");
    }

    @Test
    @Override
    public void run() throws Exception {
        String dbName = ClientServerToShardingTest.class.getSimpleName();
        executeUpdate((("CREATE DATABASE IF NOT EXISTS " + dbName) + " RUN MODE client_server"));
        new ClientServerToShardingTest.CrudTest(dbName).runTest();
        executeUpdate(((("ALTER DATABASE " + dbName)// 
         + " RUN MODE sharding WITH REPLICATION STRATEGY (class: 'SimpleStrategy', replication_factor: 2)") + " PARAMETERS (nodes=3)"));
        // new QueryTest(dbName).runTest();
    }

    private static class CrudTest extends SqlTestBase {
        public CrudTest(String dbName) {
            super(dbName);
            // setHost("127.0.0.1"); //????localhost?127.0.0.1????????TCP??
        }

        @Override
        protected void test() throws Exception {
            insert();
            select();
            batch();
        }

        void insert() {
            executeUpdate("drop table IF EXISTS test");
            executeUpdate("create table IF NOT EXISTS test(f1 int SELECTIVITY 10, f2 int, f3 int)");
            for (int j = 0; j < 50; j++) {
                long t1 = System.currentTimeMillis();
                executeUpdate("insert into test(f1, f2, f3) values(1,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(5,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(3,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(8,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(3,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(8,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(3,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(8,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(3,2,3)");
                executeUpdate("insert into test(f1, f2, f3) values(8,2,3)");
                long t2 = System.currentTimeMillis();
                System.out.println((t2 - t1));
            }
            // StringBuilder sql = new StringBuilder();
            // int rows = 200;
            // for (int j = 0; j < rows; j++) {
            // sql.append("insert into test values(0,1,2);");
            // }
            // sql.setLength(sql.length() - 1);
            // executeUpdate(sql.toString()); //TODO ?????????????
        }

        void select() {
            sql = "select distinct * from test where f1 > 3";
            sql = "select distinct f1 from test";
            printResultSet();
        }

        void batch() {
            int count = 0;
            for (int i = 0; i < count; i++) {
                String tableName = "run_mode_test_" + i;
                executeUpdate(((("create table IF NOT EXISTS " + tableName) + "(f0 int, f1 int, f2 int, f3 int, f4 int,") + " f5 int, f6 int, f7 int, f8 int, f9 int)"));
            }
        }
    }
}

