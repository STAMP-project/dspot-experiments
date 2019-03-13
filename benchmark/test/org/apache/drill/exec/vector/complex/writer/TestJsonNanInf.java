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
package org.apache.drill.exec.vector.complex.writer;


import ExecConstants.JSON_READER_NAN_INF_NUMBERS;
import ExecConstants.JSON_READ_NUMBERS_AS_DOUBLE;
import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.physical.impl.join.JoinTestBase;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestJsonNanInf extends BaseTestQuery {
    @Test
    public void testNanInfSelect() throws Exception {
        String table = "nan_test.json";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        String json = "{\"nan_col\":NaN, \"inf_col\":Infinity}";
        String query = String.format("select * from dfs.`%s`", table);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("nan_col", "inf_col").baselineValues(Double.NaN, Double.POSITIVE_INFINITY).build().run();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testExcludeNan() throws Exception {
        String table = "nan_test.json";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        String json = "[{\"nan_col\":NaN, \"inf_col\":-Infinity}," + "{\"nan_col\":5.0, \"inf_col\":5.0}]";
        String query = String.format("select nan_col from dfs.`%s` where cast(nan_col as varchar) <> 'NaN'", table);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("nan_col").baselineValues(5.0).build().run();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testIncludeNan() throws Exception {
        String table = "nan_test.json";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        String json = "[{\"nan_col\":NaN, \"inf_col\":-Infinity}," + "{\"nan_col\":5.0, \"inf_col\":5.0}]";
        String query = String.format("select nan_col from dfs.`%s` where cast(nan_col as varchar) = 'NaN'", table);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("nan_col").baselineValues(Double.NaN).build().run();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test(expected = UserRemoteException.class)
    public void testNanInfFailure() throws Exception {
        String table = "nan_test.json";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        BaseTestQuery.test("alter session set `%s` = false", JSON_READER_NAN_INF_NUMBERS);
        String json = "{\"nan_col\":NaN, \"inf_col\":Infinity}";
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.test("select * from dfs.`%s`;", table);
        } catch (UserRemoteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Error parsing JSON"));
            throw e;
        } finally {
            BaseTestQuery.test("alter session reset `%s`", JSON_READER_NAN_INF_NUMBERS);
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testCreateTableNanInf() throws Exception {
        String table = "nan_test.json";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        String json = "{\"nan_col\":NaN, \"inf_col\":Infinity}";
        String newTable = "ctas_test";
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.test("alter session set `store.format`='json'");
            BaseTestQuery.test("create table dfs.`%s` as select * from dfs.`%s`;", newTable, table);
            // ensuring that `NaN` and `Infinity` tokens ARE NOT enclosed with double quotes
            File resultFile = new File(new File(file.getParent(), newTable), "0_0_0.json");
            String resultJson = FileUtils.readFileToString(resultFile);
            int nanIndex = resultJson.indexOf("NaN");
            Assert.assertFalse("`NaN` must not be enclosed with \"\" ", ((resultJson.charAt((nanIndex - 1))) == '"'));
            Assert.assertFalse("`NaN` must not be enclosed with \"\" ", ((resultJson.charAt((nanIndex + ("NaN".length())))) == '"'));
            int infIndex = resultJson.indexOf("Infinity");
            Assert.assertFalse("`Infinity` must not be enclosed with \"\" ", ((resultJson.charAt((infIndex - 1))) == '"'));
            Assert.assertFalse("`Infinity` must not be enclosed with \"\" ", ((resultJson.charAt((infIndex + ("Infinity".length())))) == '"'));
        } finally {
            BaseTestQuery.test("drop table if exists dfs.`%s`", newTable);
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testConvertFromJsonFunction() throws Exception {
        String table = "nan_test.csv";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        String csv = "col_0, {\"nan_col\":NaN}";
        try {
            FileUtils.writeStringToFile(file, csv);
            BaseTestQuery.testBuilder().sqlQuery(String.format("select convert_fromJSON(columns[1]) as col from dfs.`%s`", table)).unOrdered().baselineColumns("col").baselineValues(TestBuilder.mapOf("nan_col", Double.NaN)).build().run();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testConvertToJsonFunction() throws Exception {
        String table = "nan_test.csv";
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table);
        String csv = "col_0, {\"nan_col\":NaN}";
        String query = String.format(("select string_binary(convert_toJSON(convert_fromJSON(columns[1]))) as col " + "from dfs.`%s` where columns[0]='col_0'"), table);
        try {
            FileUtils.writeStringToFile(file, csv);
            List<QueryDataBatch> results = BaseTestQuery.testSqlWithResults(query);
            RecordBatchLoader batchLoader = new RecordBatchLoader(BaseTestQuery.getAllocator());
            Assert.assertTrue("Query result must contain 1 row", ((results.size()) == 1));
            QueryDataBatch batch = results.get(0);
            batchLoader.load(batch.getHeader().getDef(), batch.getData());
            VectorWrapper<?> vw = batchLoader.getValueAccessorById(VarCharVector.class, batchLoader.getValueVectorId(SchemaPath.getCompoundPath("col")).getFieldIds());
            // ensuring that `NaN` token ARE NOT enclosed with double quotes
            String resultJson = vw.getValueVector().getAccessor().getObject(0).toString();
            int nanIndex = resultJson.indexOf("NaN");
            Assert.assertFalse("`NaN` must not be enclosed with \"\" ", ((resultJson.charAt((nanIndex - 1))) == '"'));
            Assert.assertFalse("`NaN` must not be enclosed with \"\" ", ((resultJson.charAt((nanIndex + ("NaN".length())))) == '"'));
            batch.release();
            batchLoader.clear();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testOrderByWithNaN() throws Exception {
        String table_name = "nan_test.json";
        String json = "{\"name\":\"obj1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n" + (("{\"name\":\"obj1\", \"attr1\":1, \"attr2\":2, \"attr3\":4, \"attr4\":Infinity}\n" + "{\"name\":\"obj2\", \"attr1\":1, \"attr2\":2, \"attr3\":5, \"attr4\":-Infinity}\n") + "{\"name\":\"obj2\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}");
        String query = String.format("SELECT name, attr4 from dfs.`%s` order by name, attr4 ", table_name);
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table_name);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.test("alter session set `%s` = true", JSON_READ_NUMBERS_AS_DOUBLE);
            BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("name", "attr4").baselineValues("obj1", Double.POSITIVE_INFINITY).baselineValues("obj1", Double.NaN).baselineValues("obj2", Double.NEGATIVE_INFINITY).baselineValues("obj2", Double.NaN).build().run();
        } finally {
            BaseTestQuery.test("alter session set `%s` = false", JSON_READ_NUMBERS_AS_DOUBLE);
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testNestedLoopJoinWithNaN() throws Exception {
        String table_name = "nan_test.json";
        String json = "{\"name\":\"object1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n" + ((((((((("{\"name\":\"object1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n" + "{\"name\":\"object1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n") + "{\"name\":\"object1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n") + "{\"name\":\"object2\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":Infinity}\n") + "{\"name\":\"object2\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":Infinity}\n") + "{\"name\":\"object3\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":Infinity}\n") + "{\"name\":\"object3\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":Infinity}\n") + "{\"name\":\"object4\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n") + "{\"name\":\"object4\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n") + "{\"name\":\"object4\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":Infinity}");
        JoinTestBase.enableJoin(false, false, true);
        String query = String.format(("select distinct t.name from dfs.`%s` t inner join dfs.`%s` " + " tt on t.attr4 = tt.attr4 "), table_name, table_name);
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table_name);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.test("alter session set `%s` = true", JSON_READ_NUMBERS_AS_DOUBLE);
            BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("name").baselineValues("object1").baselineValues("object2").baselineValues("object3").baselineValues("object4").build().run();
        } finally {
            BaseTestQuery.test("alter session set `%s` = false", JSON_READ_NUMBERS_AS_DOUBLE);
            JoinTestBase.resetJoinOptions();
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testHashJoinWithNaN() throws Exception {
        String table_name = "nan_test.json";
        String json = "{\"name\":\"obj1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n" + (("{\"name\":\"obj1\", \"attr1\":1, \"attr2\":2, \"attr3\":4, \"attr4\":Infinity}\n" + "{\"name\":\"obj2\", \"attr1\":1, \"attr2\":2, \"attr3\":5, \"attr4\":-Infinity}\n") + "{\"name\":\"obj2\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}");
        JoinTestBase.enableJoin(true, false, false);
        String query = String.format(("select distinct t.name from dfs.`%s` t inner join dfs.`%s` " + " tt on t.attr4 = tt.attr4 "), table_name, table_name);
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table_name);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.test("alter session set `%s` = true", JSON_READ_NUMBERS_AS_DOUBLE);
            BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("name").baselineValues("obj1").baselineValues("obj2").build().run();
        } finally {
            BaseTestQuery.test("alter session set `%s` = false", JSON_READ_NUMBERS_AS_DOUBLE);
            JoinTestBase.resetJoinOptions();
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testMergeJoinWithNaN() throws Exception {
        String table_name = "nan_test.json";
        String json = "{\"name\":\"obj1\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}\n" + (("{\"name\":\"obj1\", \"attr1\":1, \"attr2\":2, \"attr3\":4, \"attr4\":Infinity}\n" + "{\"name\":\"obj2\", \"attr1\":1, \"attr2\":2, \"attr3\":5, \"attr4\":-Infinity}\n") + "{\"name\":\"obj2\", \"attr1\":1, \"attr2\":2, \"attr3\":3, \"attr4\":NaN}");
        JoinTestBase.enableJoin(false, true, false);
        String query = String.format(("select distinct t.name from dfs.`%s` t inner join dfs.`%s` " + " tt on t.attr4 = tt.attr4 "), table_name, table_name);
        File file = new File(ExecTest.dirTestWatcher.getRootDir(), table_name);
        try {
            FileUtils.writeStringToFile(file, json);
            BaseTestQuery.test("alter session set `%s` = true", JSON_READ_NUMBERS_AS_DOUBLE);
            BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("name").baselineValues("obj1").baselineValues("obj2").build().run();
        } finally {
            BaseTestQuery.test("alter session set `%s` = false", JSON_READ_NUMBERS_AS_DOUBLE);
            JoinTestBase.resetJoinOptions();
            FileUtils.deleteQuietly(file);
        }
    }
}

