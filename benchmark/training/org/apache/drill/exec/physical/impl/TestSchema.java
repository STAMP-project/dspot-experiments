/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl;


import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.ClientFixture;
import org.apache.drill.test.ClusterMockStorageFixture;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class TestSchema extends DrillTest {
    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    private static ClusterMockStorageFixture cluster;

    private static ClientFixture client;

    @Test(expected = Exception.class)
    public void testQueryBrokenStorage() throws Exception {
        String sql = "SELECT id_i, name_s10 FROM `mock_broken`.`employees_5`";
        try {
            TestSchema.client.queryBuilder().sql(sql).run();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("RESOURCE ERROR: Failed to load schema"));
            throw ex;
        }
    }

    @Test
    public void testQueryGoodStorage() throws Exception {
        String sql = "SELECT id_i, name_s10 FROM `mock_good`.`employees_5`";
        TestSchema.client.queryBuilder().sql(sql).run();
    }

    @Test
    public void testQueryGoodStorageWithDefaultSchema() throws Exception {
        String use_dfs = "use dfs.tmp";
        TestSchema.client.queryBuilder().sql(use_dfs).run();
        String sql = "SELECT id_i, name_s10 FROM `mock_good`.`employees_5`";
        TestSchema.client.queryBuilder().sql(sql).run();
    }

    @Test(expected = Exception.class)
    public void testUseBrokenStorage() throws Exception {
        try {
            String use_dfs = "use mock_broken";
            TestSchema.client.queryBuilder().sql(use_dfs).run();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("RESOURCE ERROR: Failed to load schema"));
            throw ex;
        }
    }

    @Test
    public void testLocal() throws Exception {
        try {
            TestSchema.client.queryBuilder().sql("create table dfs.tmp.t1 as select 1 as c1").run();
            TestSchema.client.queryBuilder().sql("create table dfs.tmp.t2 as select 1 as c1").run();
            TestSchema.client.testBuilder().sqlQuery(("select a.c1 from dfs.tmp.`t1` a\n" + "join `dfs.tmp`.`t2` b ON b.c1 = a.c1\n")).unOrdered().baselineColumns("c1").baselineValues(1).go();
        } finally {
            TestSchema.client.queryBuilder().sql("drop table if exists `dfs.tmp`.t1").run();
            TestSchema.client.queryBuilder().sql("drop table if exists dfs.tmp.t2").run();
        }
    }
}

