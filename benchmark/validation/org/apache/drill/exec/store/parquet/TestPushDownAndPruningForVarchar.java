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
package org.apache.drill.exec.store.parquet;


import ExecConstants.PARQUET_READER_STRINGS_SIGNED_MIN_MAX;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.drill.test.ClusterTest;
import org.junit.Assert;
import org.junit.Test;


public class TestPushDownAndPruningForVarchar extends ClusterTest {
    private static File fileStore;

    @Test
    public void testOldFilesPruningWithAndWithoutMeta() throws Exception {
        String tableNoMeta = createTable("varchar_pruning_old_without_meta", true);
        String tableWithMeta = createTable("varchar_pruning_old_with_meta", false);
        Map<String, String> properties = new HashMap<>();
        properties.put(tableNoMeta, "false");
        properties.put(tableWithMeta, "true");
        try {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                for (String optionValue : Arrays.asList("true", "false", "")) {
                    ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, optionValue);
                    String query = String.format("select * from %s where part = 'A'", property.getKey());
                    String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                    Assert.assertTrue(plan.contains("numRowGroups=1"));
                    Assert.assertTrue(plan.contains(String.format("usedMetadataFile=%s", property.getValue())));
                    Assert.assertFalse(plan.contains("Filter"));
                    ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").baselineValues("A", "A2").go();
                }
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            properties.keySet().forEach(( k) -> ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", k)));
        }
    }

    @Test
    public void testOldFilesPruningWithNewMeta() throws Exception {
        String table = createTable("varchar_pruning_old_with_new_meta", true);
        try {
            for (String optionValue : Arrays.asList("true", "false", "")) {
                ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, optionValue);
                queryBuilder().sql(String.format("refresh table metadata %s", table)).run();
                String query = String.format("select * from %s where part = 'A'", table);
                String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                Assert.assertTrue(plan.contains("numRowGroups=1"));
                Assert.assertTrue(plan.contains("usedMetadataFile=true"));
                Assert.assertFalse(plan.contains("Filter"));
                ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").baselineValues("A", "A2").go();
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", table));
        }
    }

    @Test
    public void testNewFilesPruningNoMeta() throws Exception {
        String oldTable = createTable("varchar_pruning_old_without_meta", true);
        String newTable = "dfs.`tmp`.`varchar_pruning_new_without_meta`";
        try {
            queryBuilder().sql(String.format("create table %s partition by (part) as select * from %s", newTable, oldTable)).run();
            for (String optionValue : Arrays.asList("true", "false", "")) {
                ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, optionValue);
                String query = String.format("select * from %s where part = 'A'", newTable);
                String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                Assert.assertTrue(plan.contains("numRowGroups=1"));
                Assert.assertTrue(plan.contains("usedMetadataFile=false"));
                Assert.assertFalse(plan.contains("Filter"));
                ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").baselineValues("A", "A2").go();
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", oldTable));
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", newTable));
        }
    }

    @Test
    public void testNewFilesPruningWithNewMeta() throws Exception {
        String oldTable = createTable("varchar_pruning_old_without_meta", true);
        String newTable = "dfs.`tmp`.`varchar_pruning_new_with_new_meta`";
        try {
            queryBuilder().sql(String.format("create table %s partition by (part) as select * from %s", newTable, oldTable)).run();
            queryBuilder().sql(String.format("refresh table metadata %s", newTable)).run();
            for (String optionValue : Arrays.asList("true", "false", "")) {
                ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, optionValue);
                String query = String.format("select * from %s where part = 'A'", newTable);
                String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                Assert.assertTrue(plan.contains("numRowGroups=1"));
                Assert.assertTrue(plan.contains("usedMetadataFile=true"));
                Assert.assertFalse(plan.contains("Filter"));
                ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").baselineValues("A", "A2").go();
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", oldTable));
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", newTable));
        }
    }

    @Test
    public void testOldFilesPushDownNoMeta() throws Exception {
        String table = createTable("varchar_push_down_old_without_meta", true);
        Map<String, String> properties = new HashMap<>();
        properties.put("true", "numRowGroups=1");
        properties.put("false", "numRowGroups=2");
        try {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, property.getKey());
                String query = String.format("select * from %s where val = 'A1'", table);
                String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                Assert.assertTrue(plan.contains(property.getValue()));
                Assert.assertTrue(plan.contains("usedMetadataFile=false"));
                ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").go();
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", table));
        }
    }

    @Test
    public void testOldFilesPushDownWithOldMeta() throws Exception {
        String table = createTable("varchar_push_down_old_with_old_meta", false);
        Map<String, String> properties = new HashMap<>();
        properties.put("false", "numRowGroups=2");
        properties.put("true", "numRowGroups=1");
        try {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, property.getKey());
                String query = String.format("select * from %s where val = 'A1'", table);
                String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                Assert.assertTrue(plan.contains(property.getValue()));
                Assert.assertTrue(plan.contains("usedMetadataFile=true"));
                ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").go();
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", table));
        }
    }

    @Test
    public void testNewFilesPushDownNoMeta() throws Exception {
        String oldTable = createTable("varchar_push_down_old_without_meta", true);
        String newTable = "dfs.`tmp`.`varchar_push_down_new_without_meta`";
        try {
            queryBuilder().sql(String.format("create table %s partition by (part) as select * from %s", newTable, oldTable)).run();
            for (String optionValue : Arrays.asList("true", "false", "")) {
                ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, optionValue);
                String query = String.format("select * from %s where val = 'A1'", newTable);
                String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
                Assert.assertTrue(plan.contains("numRowGroups=1"));
                Assert.assertTrue(plan.contains("usedMetadataFile=false"));
                ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").go();
            }
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", oldTable));
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", newTable));
        }
    }

    @Test
    public void testNewFilesPushDownWithMeta() throws Exception {
        String oldTable = createTable("varchar_push_down_old_without_meta", true);
        String newTable = "dfs.`tmp`.`varchar_push_down_new_with_meta`";
        try {
            queryBuilder().sql(String.format("create table %s partition by (part) as select * from %s", newTable, oldTable)).run();
            queryBuilder().sql(String.format("refresh table metadata %s", newTable)).run();
            String query = String.format("select * from %s where val = 'A1'", newTable);
            // metadata for binary is allowed only after Drill 1.15.0
            // set string signed option to true, to read it on current Drill 1.15.0-SNAPSHOT version
            ClusterTest.client.alterSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, "true");
            String plan = ClusterTest.client.queryBuilder().sql(query).explainText();
            Assert.assertTrue(plan.contains("numRowGroups=1"));
            Assert.assertTrue(plan.contains("usedMetadataFile=true"));
            ClusterTest.client.testBuilder().sqlQuery(query).unOrdered().baselineColumns("part", "val").baselineValues("A", "A1").go();
        } finally {
            ClusterTest.client.resetSession(PARQUET_READER_STRINGS_SIGNED_MIN_MAX);
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", oldTable));
            ClusterTest.client.runSqlSilently(String.format("drop table if exists %s", newTable));
        }
    }
}

