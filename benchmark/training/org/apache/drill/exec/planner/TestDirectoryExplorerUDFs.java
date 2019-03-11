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
package org.apache.drill.exec.planner;


import StoragePluginTestUtils.DFS_PLUGIN_NAME;
import StoragePluginTestUtils.TMP_SCHEMA;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.store.StoragePluginRegistry;
import org.apache.drill.exec.util.JsonStringArrayList;
import org.apache.drill.exec.util.StoragePluginTestUtils;
import org.apache.drill.exec.util.Text;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableMap;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SqlTest.class, PlannerTest.class })
public class TestDirectoryExplorerUDFs extends PlanTestBase {
    private static class ConstantFoldingTestConfig {
        String funcName;

        String expectedFolderName;

        public ConstantFoldingTestConfig(String funcName, String expectedFolderName) {
            this.funcName = funcName;
            this.expectedFolderName = expectedFolderName;
        }
    }

    private static List<TestDirectoryExplorerUDFs.ConstantFoldingTestConfig> tests;

    private static Path path;

    private static File test;

    @Test
    public void testConstExprFolding_maxDir0() throws Exception {
        BaseTestQuery.test("use dfs.root");
        List<String> allFiles = ImmutableList.<String>builder().add("smallfile").add("SMALLFILE_2").add("bigfile").add("BIGFILE_2").build();
        String query = "select * from dfs.`%s/*/*.csv` where dir0 = %s('dFs.RoOt','%s')";
        for (TestDirectoryExplorerUDFs.ConstantFoldingTestConfig config : TestDirectoryExplorerUDFs.tests) {
            // make all of the other folders unexpected patterns, except for the one expected in this case
            List<String> excludedPatterns = Lists.newArrayList();
            excludedPatterns.addAll(allFiles);
            excludedPatterns.remove(config.expectedFolderName);
            // The list is easier to construct programmatically, but the API below takes an array to make it easier
            // to write a list as a literal array in a typical test definition
            String[] excludedArray = new String[excludedPatterns.size()];
            PlanTestBase.testPlanMatchingPatterns(String.format(query, TestDirectoryExplorerUDFs.path, config.funcName, TestDirectoryExplorerUDFs.path), new String[]{ config.expectedFolderName }, excludedPatterns.toArray(excludedArray));
        }
        JsonStringArrayList<Text> list = new JsonStringArrayList();
        list.add(new Text("1"));
        list.add(new Text("2"));
        list.add(new Text("3"));
        BaseTestQuery.testBuilder().sqlQuery(query, TestDirectoryExplorerUDFs.path, TestDirectoryExplorerUDFs.tests.get(0).funcName, TestDirectoryExplorerUDFs.path).unOrdered().baselineColumns("columns", "dir0").baselineValues(list, TestDirectoryExplorerUDFs.tests.get(0).expectedFolderName).go();
    }

    @Test
    public void testIncorrectFunctionPlacement() throws Exception {
        Map<String, String> configMap = ImmutableMap.<String, String>builder().put((((("select %s('dfs.root','" + (TestDirectoryExplorerUDFs.path)) + "') from dfs.`") + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv`"), "Select List").put((((("select dir0 from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` order by %s('dfs.root','") + (TestDirectoryExplorerUDFs.path)) + "')"), "Order By").put((((("select max(dir0) from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` group by %s('dfs.root','") + (TestDirectoryExplorerUDFs.path)) + "')"), "Group By").put((((("select concat(concat(%s('dfs.root','" + (TestDirectoryExplorerUDFs.path)) + "'),'someName'),'someName') from dfs.`") + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv`"), "Select List").put((((("select dir0 from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` order by concat(%s('dfs.root','") + (TestDirectoryExplorerUDFs.path)) + "'),'someName')"), "Order By").put((((("select max(dir0) from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` group by concat(%s('dfs.root','") + (TestDirectoryExplorerUDFs.path)) + "'),'someName')"), "Group By").build();
        for (Map.Entry<String, String> configEntry : configMap.entrySet()) {
            for (TestDirectoryExplorerUDFs.ConstantFoldingTestConfig functionConfig : TestDirectoryExplorerUDFs.tests) {
                try {
                    BaseTestQuery.test(configEntry.getKey(), functionConfig.funcName);
                } catch (UserRemoteException e) {
                    Assert.assertThat(e.getMessage(), CoreMatchers.containsString(String.format("Directory explorers [MAXDIR, IMAXDIR, MINDIR, IMINDIR] functions are not supported in %s", configEntry.getValue())));
                }
            }
        }
    }

    @Test
    public void testConstantFoldingOff() throws Exception {
        try {
            BaseTestQuery.test("set `planner.enable_constant_folding` = false;");
            String query = ((("select * from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` where dir0 = %s('dfs.root','") + (TestDirectoryExplorerUDFs.path)) + "')";
            for (TestDirectoryExplorerUDFs.ConstantFoldingTestConfig config : TestDirectoryExplorerUDFs.tests) {
                try {
                    BaseTestQuery.test(query, config.funcName);
                } catch (UserRemoteException e) {
                    Assert.assertThat(e.getMessage(), CoreMatchers.containsString(("Directory explorers [MAXDIR, IMAXDIR, MINDIR, IMINDIR] functions can not be used " + "when planner.enable_constant_folding option is set to false")));
                }
            }
        } finally {
            BaseTestQuery.test("set `planner.enable_constant_folding` = true;");
        }
    }

    @Test
    public void testOneArgQueryDirFunctions() throws Exception {
        // Initially update the location of dfs_test.tmp workspace with "path" temp directory just for use in this UTest
        final StoragePluginRegistry pluginRegistry = BaseTestQuery.getDrillbitContext().getStorage();
        try {
            StoragePluginTestUtils.updateSchemaLocation(DFS_PLUGIN_NAME, pluginRegistry, TestDirectoryExplorerUDFs.test, TMP_SCHEMA);
            // Results comparison of using Query Directory Functions (MAXDIR, IMAXDIR, MINDIR, IMINDIR) with one and two arguments
            String queryWithTwoArgFunc = ((("select * from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` where dir0 = %s('dfs.root','") + (TestDirectoryExplorerUDFs.path)) + "')";
            String queryWithOneArgFunc = ("select * from dfs.`" + (TestDirectoryExplorerUDFs.path)) + "/*/*.csv` where dir0 = %s('dfs.tmp')";
            for (TestDirectoryExplorerUDFs.ConstantFoldingTestConfig config : TestDirectoryExplorerUDFs.tests) {
                BaseTestQuery.testBuilder().sqlQuery(queryWithOneArgFunc, config.funcName).unOrdered().sqlBaselineQuery(queryWithTwoArgFunc, config.funcName).go();
            }
        } finally {
            StoragePluginTestUtils.updateSchemaLocation(DFS_PLUGIN_NAME, pluginRegistry, ExecTest.dirTestWatcher.getDfsTestTmpDir(), TMP_SCHEMA);
        }
    }

    // DRILL-4720
    @Test
    public void testDirectoryUDFsWithAndWithoutMetadataCache() throws Exception {
        // prepare test table with partitions
        Path tableRelPath = Paths.get("table_with_partitions");
        File tableDir = ExecTest.dirTestWatcher.makeRootSubDir(tableRelPath);
        File dataFile = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(Paths.get("parquet", "alltypes_required.parquet")).toFile();
        createPartitions(tableDir.toPath(), dataFile, 2);
        Map<String, String> configurations = ImmutableMap.<String, String>builder().put("mindir", "part_1").put("imindir", "part_1").put("maxdir", "part_2").put("imaxdir", "part_2").build();
        String query = "select dir0 from dfs.`%s` where dir0 = %s('dfs', '%s') limit 1";
        // run tests without metadata cache
        for (Map.Entry<String, String> entry : configurations.entrySet()) {
            BaseTestQuery.testBuilder().sqlQuery(query, tableRelPath, entry.getKey(), tableRelPath).unOrdered().baselineColumns("dir0").baselineValues(entry.getValue()).go();
        }
        // generate metadata
        BaseTestQuery.test("refresh table metadata dfs.`%s`", tableRelPath);
        // run tests with metadata cache
        for (Map.Entry<String, String> entry : configurations.entrySet()) {
            BaseTestQuery.testBuilder().sqlQuery(query, tableRelPath, entry.getKey(), tableRelPath).unOrdered().baselineColumns("dir0").baselineValues(entry.getValue()).go();
        }
    }
}

