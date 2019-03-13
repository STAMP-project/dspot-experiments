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
package org.apache.drill.exec.store.ischema;


import ExecConstants.LIST_FILES_RECURSIVELY;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.QueryBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category(SqlTest.class)
public class TestFilesTable extends ClusterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSelectWithoutRecursion() throws Exception {
        ClusterTest.client.testBuilder().sqlQuery(("select schema_name, root_schema_name, workspace_name, file_name, relative_path, is_directory, is_file " + "from INFORMATION_SCHEMA.`FILES`")).unOrdered().baselineColumns("schema_name", "root_schema_name", "workspace_name", "file_name", "relative_path", "is_directory", "is_file").baselineValues("dfs.files", "dfs", "files", "file0.txt", "file0.txt", false, true).baselineValues("dfs.files", "dfs", "files", "folder1", "folder1", true, false).go();
    }

    @Test
    public void testSelectWithRecursion() throws Exception {
        try {
            ClusterTest.client.alterSession(LIST_FILES_RECURSIVELY, true);
            ClusterTest.client.testBuilder().sqlQuery(("select schema_name, root_schema_name, workspace_name, file_name, relative_path, is_directory, is_file " + "from INFORMATION_SCHEMA.`FILES`")).unOrdered().baselineColumns("schema_name", "root_schema_name", "workspace_name", "file_name", "relative_path", "is_directory", "is_file").baselineValues("dfs.files", "dfs", "files", "file0.txt", "file0.txt", false, true).baselineValues("dfs.files", "dfs", "files", "folder1", "folder1", true, false).baselineValues("dfs.files", "dfs", "files", "file1.txt", "folder1/file1.txt", false, true).baselineValues("dfs.files", "dfs", "files", "folder2", "folder1/folder2", true, false).baselineValues("dfs.files", "dfs", "files", "file2.txt", "folder1/folder2/file2.txt", false, true).go();
        } finally {
            ClusterTest.client.resetSession(LIST_FILES_RECURSIVELY);
        }
    }

    @Test
    public void testShowFilesWithInCondition() throws Exception {
        checkCounts("show files in dfs.`files`", "select * from INFORMATION_SCHEMA.`FILES` where schema_name = 'dfs.files'");
    }

    @Test
    public void testShowFilesForSpecificDirectory() throws Exception {
        try {
            ClusterTest.client.alterSession(LIST_FILES_RECURSIVELY, false);
            QueryBuilder queryBuilder = ClusterTest.client.queryBuilder().sql("show files in dfs.`files`.folder1");
            QueryBuilder.QuerySummary querySummary = queryBuilder.run();
            Assert.assertTrue(querySummary.succeeded());
            Assert.assertEquals(2, querySummary.recordCount());
            // option has no effect
            ClusterTest.client.alterSession(LIST_FILES_RECURSIVELY, true);
            querySummary = queryBuilder.run();
            Assert.assertTrue(querySummary.succeeded());
            Assert.assertEquals(2, querySummary.recordCount());
        } finally {
            ClusterTest.client.resetSession(LIST_FILES_RECURSIVELY);
        }
    }

    @Test
    public void testShowFilesWithUseClause() throws Exception {
        queryBuilder().sql("use dfs.`files`").run();
        checkCounts("show files", "select * from INFORMATION_SCHEMA.`FILES` where schema_name = 'dfs.files'");
    }

    @Test
    public void testShowFilesWithPartialUseClause() throws Exception {
        queryBuilder().sql("use dfs").run();
        checkCounts("show files in `files`", "select * from INFORMATION_SCHEMA.`FILES` where schema_name = 'dfs.files'");
    }

    @Test
    public void testShowFilesForDefaultSchema() throws Exception {
        queryBuilder().sql("use dfs").run().succeeded();
        checkCounts("show files", "select * from INFORMATION_SCHEMA.`FILES` where schema_name = 'dfs.default'");
    }

    @Test
    public void testFilterPushDown_None() throws Exception {
        String plan = queryBuilder().sql("select * from INFORMATION_SCHEMA.`FILES` where file_name = 'file1.txt'").explainText();
        Assert.assertTrue(plan.contains("filter=null"));
        Assert.assertTrue(plan.contains("Filter(condition="));
    }

    @Test
    public void testFilterPushDown_Partial() throws Exception {
        String plan = queryBuilder().sql("select * from INFORMATION_SCHEMA.`FILES` where schema_name = 'dfs.files' and file_name = 'file1.txt'").explainText();
        Assert.assertTrue(plan.contains("filter=booleanand(equal(Field=SCHEMA_NAME,Literal=dfs.files))"));
        Assert.assertTrue(plan.contains("Filter(condition="));
    }

    @Test
    public void testFilterPushDown_Full() throws Exception {
        String plan = queryBuilder().sql("select * from INFORMATION_SCHEMA.`FILES` where schema_name = 'dfs.files'").explainText();
        Assert.assertTrue(plan.contains("filter=equal(Field=SCHEMA_NAME,Literal=dfs.files)"));
        Assert.assertFalse(plan.contains("Filter(condition="));
    }
}

