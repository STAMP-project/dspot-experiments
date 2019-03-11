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
package org.apache.drill.exec.store.text;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.junit.Test;


public class TestCsvHeader extends BaseTestQuery {
    private static final String ROOT = "store/text/data/cars.csvh";

    // DRILL-951
    @Test
    public void testCsvWithHeader() throws Exception {
        // Pre DRILL-951: Qry: select * from cp.`%s` LIMIT 2
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`%s` LIMIT 2", TestCsvHeader.ROOT).unOrdered().baselineColumns("Year", "Make", "Model", "Description", "Price").baselineValues("1997", "Ford", "E350", "ac, abs, moon", "3000.00").baselineValues("1999", "Chevy", "Venture \"Extended Edition\"", "", "4900.00").go();
    }

    // DRILL-951
    @Test
    public void testCsvWhereWithHeader() throws Exception {
        // Pre DRILL-951: Qry: select * from cp.`%s` where columns[1] = 'Chevy'
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`%s` where Make = 'Chevy'", TestCsvHeader.ROOT).unOrdered().baselineColumns("Year", "Make", "Model", "Description", "Price").baselineValues("1999", "Chevy", "Venture \"Extended Edition\"", "", "4900.00").baselineValues("1999", "Chevy", "Venture \"Extended Edition, Very Large\"", "", "5000.00").go();
    }

    // DRILL-951
    @Test
    public void testCsvStarPlusWithHeader() throws Exception {
        // Pre DRILL-951: Qry: select *,columns[1] from cp.`%s` where columns[1] = 'Chevy'
        BaseTestQuery.testBuilder().sqlQuery("select *, Make from cp.`%s` where Make = 'Chevy'", TestCsvHeader.ROOT).unOrdered().baselineColumns("Year", "Make", "Model", "Description", "Price", "Make0").baselineValues("1999", "Chevy", "Venture \"Extended Edition\"", "", "4900.00", "Chevy").baselineValues("1999", "Chevy", "Venture \"Extended Edition, Very Large\"", "", "5000.00", "Chevy").go();
    }

    // DRILL-951
    @Test
    public void testCsvWhereColumnsWithHeader() throws Exception {
        // Pre DRILL-951: Qry: select columns[1] from cp.`%s` where columns[1] = 'Chevy'
        BaseTestQuery.testBuilder().sqlQuery("select Make from cp.`%s` where Make = 'Chevy'", TestCsvHeader.ROOT).unOrdered().baselineColumns("Make").baselineValues("Chevy").baselineValues("Chevy").go();
    }

    // DRILL-951
    @Test
    public void testCsvColumnsWithHeader() throws Exception {
        // Pre DRILL-951: Qry: select columns[0],columns[2],columns[4] from cp.`%s` where columns[1] = 'Chevy'
        BaseTestQuery.testBuilder().sqlQuery("select `Year`, Model, Price from cp.`%s` where Make = 'Chevy'", TestCsvHeader.ROOT).unOrdered().baselineColumns("Year", "Model", "Price").baselineValues("1999", "Venture \"Extended Edition\"", "4900.00").baselineValues("1999", "Venture \"Extended Edition, Very Large\"", "5000.00").go();
    }

    // DRILL-951
    @Test
    public void testCsvHeaderShortCircuitReads() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select `Year`, Model from cp.`%s` where Make = 'Chevy'", TestCsvHeader.ROOT).unOrdered().baselineColumns("Year", "Model").baselineValues("1999", "Venture \"Extended Edition\"").baselineValues("1999", "Venture \"Extended Edition, Very Large\"").go();
    }

    // DRILL-4108
    @Test
    public void testCsvHeaderNonExistingColumn() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select `Year`, Model, Category from cp.`%s` where Make = 'Chevy'", TestCsvHeader.ROOT).unOrdered().baselineColumns("Year", "Model", "Category").baselineValues("1999", "Venture \"Extended Edition\"", "").baselineValues("1999", "Venture \"Extended Edition, Very Large\"", "").go();
    }

    // DRILL-4108
    @Test
    public void testCsvHeaderMismatch() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select `Year`, Model, Category from dfs.`store/text/data/d2` where Make = 'Chevy'").unOrdered().baselineColumns("Year", "Model", "Category").baselineValues("1999", "", "Venture \"Extended Edition\"").baselineValues("1999", "", "Venture \"Extended Edition, Very Large\"").baselineValues("1999", "Venture \"Extended Edition\"", "").baselineValues("1999", "Venture \"Extended Edition, Very Large\"", "").go();
    }

    // DRILL-4108
    @Test
    public void testCsvHeaderSkipFirstLine() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select `Year`, Model from cp.`store/text/data/cars.csvh-test` where Make = 'Chevy'").unOrdered().baselineColumns("Year", "Model").baselineValues("1999", "Venture \"Extended Edition\"").baselineValues("1999", "Venture \"Extended Edition, Very Large\"").go();
    }

    @Test
    public void testEmptyFinalColumn() throws Exception {
        File table_dir = ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get("emptyFinalColumn"));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(table_dir, "a.csvh")));
        os.write("field1,field2\n".getBytes());
        for (int i = 0; i < 10000; i++) {
            os.write("a,\n".getBytes());
        }
        os.flush();
        os.close();
        String query = "select * from dfs.tmp.emptyFinalColumn";
        TestBuilder builder = BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("field1", "field2");
        for (int i = 0; i < 10000; i++) {
            builder.baselineValues("a", "");
        }
        builder.go();
    }

    @Test
    public void testCountOnCsvWithHeader() throws Exception {
        final String query = "select count(%s) as cnt from cp.`%s`";
        final List<Object> options = Lists.<Object>newArrayList("*", 1, "'A'");
        for (Object option : options) {
            BaseTestQuery.testBuilder().sqlQuery(query, option, TestCsvHeader.ROOT).unOrdered().baselineColumns("cnt").baselineValues(4L).build().run();
        }
    }
}

