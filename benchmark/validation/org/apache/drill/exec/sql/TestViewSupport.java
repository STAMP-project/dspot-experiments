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
package org.apache.drill.exec.sql;


import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.util.StoragePluginTestUtils;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlTest.class)
public class TestViewSupport extends TestBaseViewSupport {
    @Test
    public void referToSchemaInsideAndOutsideView() throws Exception {
        String use = "use dfs.tmp;";
        String selectInto = "create table monkey as select c_custkey, c_nationkey from cp.`tpch/customer.parquet`";
        String createView = "create or replace view myMonkeyView as select c_custkey, c_nationkey from monkey";
        String selectInside = "select * from myMonkeyView;";
        String use2 = "use cp;";
        String selectOutside = "select * from dfs.tmp.myMonkeyView;";
        BaseTestQuery.test(use);
        BaseTestQuery.test(selectInto);
        BaseTestQuery.test(createView);
        BaseTestQuery.test(selectInside);
        BaseTestQuery.test(use2);
        BaseTestQuery.test(selectOutside);
    }

    /**
     * DRILL-2342 This test is for case where output columns are nullable. Existing tests already cover the case
     * where columns are required.
     */
    @Test
    public void nullabilityPropertyInViewPersistence() throws Exception {
        final String viewName = "testNullabilityPropertyInViewPersistence";
        try {
            BaseTestQuery.test("USE dfs.tmp");
            BaseTestQuery.test(String.format(("CREATE OR REPLACE VIEW %s AS SELECT " + ((((("CAST(customer_id AS BIGINT) as cust_id, " + "CAST(fname AS VARCHAR(25)) as fname, ") + "CAST(country AS VARCHAR(20)) as country ") + "FROM cp.`customer.json` ") + "ORDER BY customer_id ") + "LIMIT 1;")), viewName));
            BaseTestQuery.testBuilder().sqlQuery(String.format("DESCRIBE %s", viewName)).unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("cust_id", "BIGINT", "YES").baselineValues("fname", "CHARACTER VARYING", "YES").baselineValues("country", "CHARACTER VARYING", "YES").go();
            BaseTestQuery.testBuilder().sqlQuery(String.format("SELECT * FROM %s", viewName)).ordered().baselineColumns("cust_id", "fname", "country").baselineValues(1L, "Sheri", "Mexico").go();
        } finally {
            BaseTestQuery.test((("drop view " + viewName) + ";"));
        }
    }

    @Test
    public void viewWithStarInDef_StarInQuery() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT * FROM cp.`region.json` ORDER BY `region_id`", "SELECT * FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 1", TestBaseViewSupport.baselineColumns("region_id", "sales_city", "sales_state_province", "sales_district", "sales_region", "sales_country", "sales_district_id"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None", "None", "No District", "No Region", "No Country", 0L)));
    }

    @Test
    public void viewWithSelectFieldsInDef_StarInQuery() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id`", "SELECT * FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("region_id", "sales_city"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None"), TestBaseViewSupport.row(1L, "San Francisco")));
    }

    @Test
    public void viewWithSelectFieldsInDef_SelectFieldsInView_StarInQuery() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, "(regionid, salescity)", "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id`", "SELECT * FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("regionid", "salescity"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None"), TestBaseViewSupport.row(1L, "San Francisco")));
    }

    @Test
    public void viewWithStarInDef_SelectFieldsInQuery() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT * FROM cp.`region.json` ORDER BY `region_id`", "SELECT region_id, sales_city FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("region_id", "sales_city"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None"), TestBaseViewSupport.row(1L, "San Francisco")));
    }

    @Test
    public void viewWithSelectFieldsInDef_SelectFieldsInQuery1() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id`", "SELECT region_id, sales_city FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("region_id", "sales_city"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None"), TestBaseViewSupport.row(1L, "San Francisco")));
    }

    @Test
    public void viewWithSelectFieldsInDef_SelectFieldsInQuery2() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id`", "SELECT sales_city FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("sales_city"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row("None"), TestBaseViewSupport.row("San Francisco")));
    }

    @Test
    public void viewWithSelectFieldsInDef_SelectFieldsInView_SelectFieldsInQuery1() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, "(regionid, salescity)", "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id` LIMIT 2", "SELECT regionid, salescity FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("regionid", "salescity"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None"), TestBaseViewSupport.row(1L, "San Francisco")));
    }

    @Test
    public void viewWithSelectFieldsInDef_SelectFieldsInView_SelectFieldsInQuery2() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, "(regionid, salescity)", "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id` DESC", "SELECT regionid FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 2", TestBaseViewSupport.baselineColumns("regionid"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(109L), TestBaseViewSupport.row(108L)));
    }

    @Test
    public void viewCreatedFromAnotherView() throws Exception {
        final String innerView = TestBaseViewSupport.generateViewName();
        final String outerView = TestBaseViewSupport.generateViewName();
        try {
            TestBaseViewSupport.createViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, innerView, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id`");
            TestBaseViewSupport.createViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, outerView, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, String.format("SELECT region_id FROM %s.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, innerView));
            TestBaseViewSupport.queryViewHelper(String.format("SELECT region_id FROM %s.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, outerView), TestBaseViewSupport.baselineColumns("region_id"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L)));
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, outerView, StoragePluginTestUtils.DFS_TMP_SCHEMA);
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, innerView, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-1015
    @Test
    @Category(UnlikelyTest.class)
    public void viewWithCompoundIdentifiersInDef() throws Exception {
        final String viewDef = "SELECT " + (((("cast(columns[0] AS int) n_nationkey, " + "cast(columns[1] AS CHAR(25)) n_name, ") + "cast(columns[2] AS INT) n_regionkey, ") + "cast(columns[3] AS VARCHAR(152)) n_comment ") + "FROM dfs.`nation`");
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, null, viewDef, "SELECT * FROM TEST_SCHEMA.TEST_VIEW_NAME LIMIT 1", TestBaseViewSupport.baselineColumns("n_nationkey", "n_name", "n_regionkey", "n_comment"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0, "ALGERIA", 0, " haggle. carefully final deposits detect slyly agai")));
    }

    @Test
    public void createViewWhenViewAlreadyExists() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            final String viewDef1 = "SELECT region_id, sales_city FROM cp.`region.json`";
            // Create the view
            TestBaseViewSupport.createViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, viewDef1);
            // Try to create the view with same name in same schema.
            final String createViewSql = String.format("CREATE VIEW %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, viewDef1);
            BaseTestQuery.errorMsgTestHelper(createViewSql, String.format("A view with given name [%s] already exists in schema [%s]", viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA));
            // Try creating the view with same name in same schema, but with CREATE OR REPLACE VIEW clause
            final String viewDef2 = "SELECT sales_state_province FROM cp.`region.json` ORDER BY `region_id`";
            BaseTestQuery.testBuilder().sqlQuery("CREATE OR REPLACE VIEW %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, viewDef2).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("View '%s' replaced successfully in '%s' schema", viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA)).go();
            // Make sure the new view created returns the data expected.
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName), TestBaseViewSupport.baselineColumns("sales_state_province"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row("None")));
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-5952
    @Test
    public void createViewIfNotExistsWhenTableAlreadyExists() throws Exception {
        final String tableName = TestBaseViewSupport.generateViewName();
        try {
            final String tableDef = "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id` LIMIT 2";
            BaseTestQuery.test("CREATE TABLE %s.%s as %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName, tableDef);
            // Try to create the view with same name in same schema with if not exists clause.
            final String createViewSql = String.format("CREATE VIEW IF NOT EXISTS %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName, tableDef);
            BaseTestQuery.testBuilder().sqlQuery(createViewSql).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("A table or view with given name [%s] already exists in schema [%s]", tableName, StoragePluginTestUtils.DFS_TMP_SCHEMA)).go();
        } finally {
            FileUtils.deleteQuietly(new File(ExecTest.dirTestWatcher.getDfsTestTmpDir(), tableName));
        }
    }

    // DRILL-5952
    @Test
    public void createViewIfNotExistsWhenViewAlreadyExists() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            final String viewDef1 = "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id` LIMIT 2";
            // Create the view
            TestBaseViewSupport.createViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, viewDef1);
            // Try to create the view with same name in same schema with if not exists clause.
            final String viewDef2 = "SELECT sales_state_province FROM cp.`region.json` ORDER BY `region_id`";
            final String createViewSql = String.format("CREATE VIEW IF NOT EXISTS %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, viewDef2);
            BaseTestQuery.testBuilder().sqlQuery(createViewSql).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("A table or view with given name [%s] already exists in schema [%s]", viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA)).go();
            // Make sure the view created returns the data expected.
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName), TestBaseViewSupport.baselineColumns("region_id", "sales_city"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None")));
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-5952
    @Test
    public void testCreateViewIfNotExists() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            final String viewDef = "SELECT region_id, sales_city FROM cp.`region.json` ORDER BY `region_id` LIMIT 2";
            final String createViewSql = String.format("CREATE VIEW IF NOT EXISTS %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, viewDef);
            BaseTestQuery.test(createViewSql);
            // Make sure the view created returns the data expected.
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName), TestBaseViewSupport.baselineColumns("region_id", "sales_city"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, "None")));
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-5952
    @Test
    public void createViewWithBothOrReplaceAndIfNotExists() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        final String viewDef = "SELECT region_id, sales_city FROM cp.`region.json`";
        // Try to create the view with both <or replace> and <if not exists> clause.
        final String createViewSql = String.format("CREATE OR REPLACE VIEW IF NOT EXISTS %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, viewDef);
        BaseTestQuery.errorMsgTestHelper(createViewSql, "Create view statement cannot have both <OR REPLACE> and <IF NOT EXISTS> clause");
    }

    // DRILL-2422
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWhenATableWithSameNameAlreadyExists() throws Exception {
        final String tableName = TestBaseViewSupport.generateViewName();
        try {
            final String tableDef1 = "SELECT region_id, sales_city FROM cp.`region.json`";
            BaseTestQuery.test("CREATE TABLE %s.%s as %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName, tableDef1);
            // Try to create the view with same name in same schema.
            final String createViewSql = String.format("CREATE VIEW %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName, tableDef1);
            BaseTestQuery.errorMsgTestHelper(createViewSql, String.format("A non-view table with given name [%s] already exists in schema [%s]", tableName, StoragePluginTestUtils.DFS_TMP_SCHEMA));
            // Try creating the view with same name in same schema, but with CREATE OR REPLACE VIEW clause
            final String viewDef2 = "SELECT sales_state_province FROM cp.`region.json` ORDER BY `region_id`";
            BaseTestQuery.errorMsgTestHelper(String.format("CREATE OR REPLACE VIEW %s.`%s` AS %s", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName, viewDef2), String.format("A non-view table with given name [%s] already exists in schema [%s]", tableName, StoragePluginTestUtils.DFS_TMP_SCHEMA));
        } finally {
            FileUtils.deleteQuietly(new File(ExecTest.dirTestWatcher.getDfsTestTmpDir(), tableName));
        }
    }

    @Test
    public void infoSchemaWithView() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            BaseTestQuery.test(("USE " + (StoragePluginTestUtils.DFS_TMP_SCHEMA)));
            /* pass no schema */
            TestBaseViewSupport.createViewHelper(null, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT cast(`employee_id` as integer) employeeid FROM cp.`employee.json`");
            // Test SHOW TABLES on view
            BaseTestQuery.testBuilder().sqlQuery("SHOW TABLES like '%s'", viewName).unOrdered().baselineColumns("TABLE_SCHEMA", "TABLE_NAME").baselineValues(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName).go();
            // Test record in INFORMATION_SCHEMA.VIEWS
            BaseTestQuery.testBuilder().sqlQuery("SELECT * FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = '%s'", viewName).unOrdered().baselineColumns("TABLE_CATALOG", "TABLE_SCHEMA", "TABLE_NAME", "VIEW_DEFINITION").baselineValues("DRILL", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, "SELECT CAST(`employee_id` AS INTEGER) AS `employeeid`\nFROM `cp`.`employee.json`").go();
            // Test record in INFORMATION_SCHEMA.TABLES
            BaseTestQuery.testBuilder().sqlQuery("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME = '%s'", viewName).unOrdered().baselineColumns("TABLE_CATALOG", "TABLE_SCHEMA", "TABLE_NAME", "TABLE_TYPE").baselineValues("DRILL", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, "VIEW").go();
            // Test DESCRIBE view
            BaseTestQuery.testBuilder().sqlQuery("DESCRIBE `%s`", viewName).unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("employeeid", "INTEGER", "YES").go();
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    @Test
    public void viewWithPartialSchemaIdentifier() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            // Change default schema to just "dfs". View is actually created in "dfs.tmp" schema.
            BaseTestQuery.test("USE dfs");
            // Create a view with with "tmp" schema identifier
            TestBaseViewSupport.createViewHelper("tmp", viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, ("SELECT CAST(`employee_id` AS INTEGER) AS `employeeid`\n" + "FROM `cp`.`employee.json`"));
            final String[] baselineColumns = TestBaseViewSupport.baselineColumns("employeeid");
            final List<Object[]> baselineValues = TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(1156));
            // Query view from current schema "dfs" by referring to the view using "tmp.viewName"
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` ORDER BY `employeeid` DESC LIMIT 1", "tmp", viewName), baselineColumns, baselineValues);
            // Change the default schema to "dfs.tmp" and query view by referring to it using "viewName"
            BaseTestQuery.test("USE dfs.tmp");
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM `%s` ORDER BY `employeeid` DESC LIMIT 1", viewName), baselineColumns, baselineValues);
            // Change the default schema to "cp" and query view by referring to it using "dfs.tmp.viewName";
            BaseTestQuery.test("USE cp");
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` ORDER BY `employeeid` DESC LIMIT 1", "dfs.tmp", viewName), baselineColumns, baselineValues);
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-1114
    @Test
    @Category(UnlikelyTest.class)
    public void viewResolvingTablesInWorkspaceSchema() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            // Change default schema to "cp"
            BaseTestQuery.test("USE cp");
            // Create a view with full schema identifier and refer the "region.json" as without schema.
            TestBaseViewSupport.createViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT region_id, sales_city FROM `region.json`");
            final String[] baselineColumns = TestBaseViewSupport.baselineColumns("region_id", "sales_city");
            final List<Object[]> baselineValues = TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(109L, "Santa Fe"));
            // Query the view
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` ORDER BY region_id DESC LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName), baselineColumns, baselineValues);
            // Change default schema to "dfs" and query by referring to the view using "tmp.viewName"
            BaseTestQuery.test("USE dfs");
            TestBaseViewSupport.queryViewHelper(String.format("SELECT * FROM %s.`%s` ORDER BY region_id DESC LIMIT 1", StoragePluginTestUtils.TMP_SCHEMA, viewName), baselineColumns, baselineValues);
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-2341, View schema verification where view's field is not specified is already tested in
    // TestViewSupport.infoSchemaWithView.
    @Test
    @Category(UnlikelyTest.class)
    public void viewSchemaWhenSelectFieldsInDef_SelectFieldsInView() throws Exception {
        final String viewName = TestBaseViewSupport.generateViewName();
        try {
            BaseTestQuery.test("use %s", StoragePluginTestUtils.DFS_TMP_SCHEMA);
            TestBaseViewSupport.createViewHelper(null, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, "(id, name, bday)", ("SELECT " + ((("cast(`region_id` as integer), " + "cast(`full_name` as varchar(100)), ") + "cast(`birth_date` as date) ") + "FROM cp.`employee.json`")));
            // Test DESCRIBE view
            BaseTestQuery.testBuilder().sqlQuery("DESCRIBE `%s`", viewName).unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("id", "INTEGER", "YES").baselineValues("name", "CHARACTER VARYING", "YES").baselineValues("bday", "DATE", "YES").go();
        } finally {
            TestBaseViewSupport.dropViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA);
        }
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithDuplicateColumnsInDef1() throws Exception {
        TestViewSupport.createViewErrorTestHelper("CREATE VIEW %s.%s AS SELECT region_id, region_id FROM cp.`region.json`", String.format("Duplicate column name [%s]", "region_id"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithDuplicateColumnsInDef2() throws Exception {
        TestViewSupport.createViewErrorTestHelper("CREATE VIEW %s.%s AS SELECT region_id, sales_city, sales_city FROM cp.`region.json`", String.format("Duplicate column name [%s]", "sales_city"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithDuplicateColumnsInDef3() throws Exception {
        TestViewSupport.createViewErrorTestHelper("CREATE VIEW %s.%s(regionid, regionid) AS SELECT region_id, sales_city FROM cp.`region.json`", String.format("Duplicate column name [%s]", "regionid"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithDuplicateColumnsInDef4() throws Exception {
        TestViewSupport.createViewErrorTestHelper(("CREATE VIEW %s.%s(regionid, salescity, salescity) " + "AS SELECT region_id, sales_city, sales_city FROM cp.`region.json`"), String.format("Duplicate column name [%s]", "salescity"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithDuplicateColumnsInDef5() throws Exception {
        TestViewSupport.createViewErrorTestHelper(("CREATE VIEW %s.%s(regionid, salescity, SalesCity) " + "AS SELECT region_id, sales_city, sales_city FROM cp.`region.json`"), String.format("Duplicate column name [%s]", "SalesCity"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithDuplicateColumnsInDef6() throws Exception {
        TestViewSupport.createViewErrorTestHelper(("CREATE VIEW %s.%s " + ("AS SELECT t1.region_id, t2.region_id FROM cp.`region.json` t1 JOIN cp.`region.json` t2 " + "ON t1.region_id = t2.region_id LIMIT 1")), String.format("Duplicate column name [%s]", "region_id"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithUniqueColsInFieldListDuplicateColsInQuery1() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, "(regionid1, regionid2)", "SELECT region_id, region_id FROM cp.`region.json` LIMIT 1", "SELECT * FROM TEST_SCHEMA.TEST_VIEW_NAME", TestBaseViewSupport.baselineColumns("regionid1", "regionid2"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, 0L)));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWithUniqueColsInFieldListDuplicateColsInQuery2() throws Exception {
        TestBaseViewSupport.testViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, "(regionid1, regionid2)", ("SELECT t1.region_id, t2.region_id FROM cp.`region.json` t1 JOIN cp.`region.json` t2 " + "ON t1.region_id = t2.region_id LIMIT 1"), "SELECT * FROM TEST_SCHEMA.TEST_VIEW_NAME", TestBaseViewSupport.baselineColumns("regionid1", "regionid2"), TestBaseViewSupport.baselineRows(TestBaseViewSupport.row(0L, 0L)));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void createViewWhenInEqualColumnCountInViewDefVsInViewQuery() throws Exception {
        TestViewSupport.createViewErrorTestHelper(("CREATE VIEW %s.%s(regionid, salescity) " + "AS SELECT region_id, sales_city, sales_region FROM cp.`region.json`"), "view's field list and the view's query field list have different counts.");
    }

    // DRILL-2589
    @Test
    public void createViewWhenViewQueryColumnHasStarAndViewFiledListIsSpecified() throws Exception {
        TestViewSupport.createViewErrorTestHelper(("CREATE VIEW %s.%s(regionid, salescity) " + "AS SELECT region_id, * FROM cp.`region.json`"), "view's query field list has a '*', which is invalid when view's field list is specified.");
    }

    // DRILL-2423
    @Test
    @Category(UnlikelyTest.class)
    public void showProperMsgWhenDroppingNonExistentView() throws Exception {
        BaseTestQuery.errorMsgTestHelper("DROP VIEW dfs.tmp.nonExistentView", "Unknown view [nonExistentView] in schema [dfs.tmp].");
    }

    // DRILL-2423
    @Test
    @Category(UnlikelyTest.class)
    public void showProperMsgWhenTryingToDropAViewInImmutableSchema() throws Exception {
        BaseTestQuery.errorMsgTestHelper("DROP VIEW cp.nonExistentView", "Unable to create or drop objects. Schema [cp] is immutable.");
    }

    // DRILL-2423
    @Test
    @Category(UnlikelyTest.class)
    public void showProperMsgWhenTryingToDropANonViewTable() throws Exception {
        final String testTableName = "testTableShowErrorMsg";
        try {
            BaseTestQuery.test("CREATE TABLE %s.%s AS SELECT c_custkey, c_nationkey from cp.`tpch/customer.parquet`", StoragePluginTestUtils.DFS_TMP_SCHEMA, testTableName);
            BaseTestQuery.errorMsgTestHelper(String.format("DROP VIEW %s.%s", StoragePluginTestUtils.DFS_TMP_SCHEMA, testTableName), "[testTableShowErrorMsg] is not a VIEW in schema [dfs.tmp]");
        } finally {
            File tblPath = new File(ExecTest.dirTestWatcher.getDfsTestTmpDir(), testTableName);
            FileUtils.deleteQuietly(tblPath);
        }
    }

    // DRILL-4673
    @Test
    public void dropViewIfExistsWhenViewExists() throws Exception {
        final String existentViewName = TestBaseViewSupport.generateViewName();
        // successful dropping of existent view
        TestBaseViewSupport.createViewHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, existentViewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, null, "SELECT c_custkey, c_nationkey from cp.`tpch/customer.parquet`");
        TestBaseViewSupport.dropViewIfExistsHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, existentViewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, true);
    }

    // DRILL-4673
    @Test
    public void dropViewIfExistsWhenViewDoesNotExist() throws Exception {
        final String nonExistentViewName = TestBaseViewSupport.generateViewName();
        // dropping of non existent view without error
        TestBaseViewSupport.dropViewIfExistsHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, nonExistentViewName, StoragePluginTestUtils.DFS_TMP_SCHEMA, false);
    }

    // DRILL-4673
    @Test
    public void dropViewIfExistsWhenItIsATable() throws Exception {
        final String tableName = "table_name";
        try {
            // dropping of non existent view without error if the table with such name is existed
            BaseTestQuery.test("CREATE TABLE %s.%s as SELECT region_id, sales_city FROM cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName);
            TestBaseViewSupport.dropViewIfExistsHelper(StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName, StoragePluginTestUtils.DFS_TMP_SCHEMA, false);
        } finally {
            BaseTestQuery.test("DROP TABLE IF EXISTS %s.%s ", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName);
        }
    }

    @Test
    public void selectFromViewCreatedOnCalcite1_4() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select store_type from cp.`view/view_from_calcite_1_4.view.drill`").unOrdered().baselineColumns("store_type").baselineValues("HeadQuarters").go();
    }

    @Test
    public void testDropViewNameStartsWithSlash() throws Exception {
        String viewName = "view_name_starts_with_slash_drop";
        try {
            BaseTestQuery.test("CREATE VIEW `%s`.`%s` AS SELECT * FROM cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName);
            BaseTestQuery.testBuilder().sqlQuery("DROP VIEW `%s`.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, ("/" + viewName)).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("View [%s] deleted successfully from schema [%s].", viewName, StoragePluginTestUtils.DFS_TMP_SCHEMA)).go();
        } finally {
            BaseTestQuery.test("DROP VIEW IF EXISTS `%s`.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName);
        }
    }

    @Test
    public void testViewIsCreatedWithinWorkspace() throws Exception {
        String viewName = "view_created_within_workspace";
        try {
            BaseTestQuery.test("CREATE VIEW `%s`.`%s` AS SELECT * FROM cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, ("/" + viewName));
            BaseTestQuery.testBuilder().sqlQuery("SELECT region_id FROM `%s`.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName).unOrdered().baselineColumns("region_id").baselineValues(0L).go();
        } finally {
            BaseTestQuery.test("DROP VIEW IF EXISTS `%s`.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName);
        }
    }

    @Test
    public void testViewIsFoundWithinWorkspaceWhenNameStartsWithSlash() throws Exception {
        String viewName = "view_found_within_workspace";
        try {
            BaseTestQuery.test("CREATE VIEW `%s`.`%s` AS SELECT * FROM cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName);
            BaseTestQuery.testBuilder().sqlQuery("SELECT region_id FROM `%s`.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, ("/" + viewName)).unOrdered().baselineColumns("region_id").baselineValues(0L).go();
        } finally {
            BaseTestQuery.test("DROP VIEW IF EXISTS `%s`.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, viewName);
        }
    }

    // DRILL-6944
    @Test
    public void testSelectMapColumnOfNewlyCreatedView() throws Exception {
        try {
            BaseTestQuery.test("CREATE VIEW dfs.tmp.`mapf_view` AS SELECT `mapf` FROM dfs.`avro/map_string_to_long.avro`");
            BaseTestQuery.test("SELECT * FROM dfs.tmp.`mapf_view`");
            BaseTestQuery.testBuilder().sqlQuery("SELECT `mapf`['ki'] as ki FROM dfs.tmp.`mapf_view`").unOrdered().baselineColumns("ki").baselineValues(1L).go();
        } finally {
            BaseTestQuery.test("DROP VIEW IF EXISTS dfs.tmp.`mapf_view`");
        }
    }

    // DRILL-6944
    @Test
    public void testMapTypeFullyQualifiedInNewlyCreatedView() throws Exception {
        try {
            BaseTestQuery.test("CREATE VIEW dfs.tmp.`mapf_view` AS SELECT `mapf` FROM dfs.`avro/map_string_to_long.avro`");
            PlanTestBase.testPlanWithAttributesMatchingPatterns("SELECT * FROM dfs.tmp.`mapf_view`", new String[]{ "Screen : rowType = RecordType\\(\\(VARCHAR\\(65535\\), BIGINT\\) MAP mapf\\)", "Project\\(mapf=\\[\\$0\\]\\) : rowType = RecordType\\(\\(VARCHAR\\(65535\\), BIGINT\\) MAP mapf\\)", "Scan.*avro/map_string_to_long.avro.*rowType = RecordType\\(\\(VARCHAR\\(65535\\), BIGINT\\) MAP mapf\\)" }, null);
        } finally {
            BaseTestQuery.test("DROP VIEW IF EXISTS dfs.tmp.`mapf_view`");
        }
    }

    // DRILL-6944
    @Test
    public void testMapColumnOfOlderViewWithUntypedMap() throws Exception {
        BaseTestQuery.test("SELECT * FROM cp.`view/vw_before_drill_6944.view.drill`");
        BaseTestQuery.testBuilder().sqlQuery("SELECT `mapf`['ki'] as ki FROM cp.`view/vw_before_drill_6944.view.drill`").unOrdered().baselineColumns("ki").baselineValues(1L).go();
    }

    // DRILL-6944
    @Test
    public void testMapTypeTreatedAsAnyInOlderViewWithUntypedMap() throws Exception {
        PlanTestBase.testPlanWithAttributesMatchingPatterns("SELECT * FROM cp.`view/vw_before_drill_6944.view.drill`", new String[]{ "Screen : rowType = RecordType\\(ANY mapf\\)", "Project.mapf=.CAST\\(\\$0\\):ANY NOT NULL.*" }, null);
    }
}

