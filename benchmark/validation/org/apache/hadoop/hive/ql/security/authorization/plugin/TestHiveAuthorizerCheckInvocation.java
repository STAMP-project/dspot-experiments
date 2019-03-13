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
package org.apache.hadoop.hive.ql.security.authorization.plugin;


import HivePrivilegeObjectType.DATABASE;
import HivePrivilegeObjectType.FUNCTION;
import HivePrivilegeObjectType.LOCAL_URI;
import HivePrivilegeObjectType.TABLE_OR_VIEW;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject.HivePrivilegeObjectType;
import org.apache.hadoop.hive.ql.stats.StatsUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test HiveAuthorizer api invocation
 */
public class TestHiveAuthorizerCheckInvocation {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    protected static HiveConf conf;

    protected static Driver driver;

    private static final String tableName = (TestHiveAuthorizerCheckInvocation.class.getSimpleName()) + "Table";

    private static final String viewName = (TestHiveAuthorizerCheckInvocation.class.getSimpleName()) + "View";

    private static final String inDbTableName = (TestHiveAuthorizerCheckInvocation.tableName) + "_in_db";

    private static final String acidTableName = (TestHiveAuthorizerCheckInvocation.tableName) + "_acid";

    private static final String dbName = (TestHiveAuthorizerCheckInvocation.class.getSimpleName()) + "Db";

    private static final String fullInTableName = StatsUtils.getFullyQualifiedTableName(TestHiveAuthorizerCheckInvocation.dbName, TestHiveAuthorizerCheckInvocation.inDbTableName);

    static HiveAuthorizer mockedAuthorizer;

    /**
     * This factory creates a mocked HiveAuthorizer class. Use the mocked class to
     * capture the argument passed to it in the test case.
     */
    static class MockedHiveAuthorizerFactory implements HiveAuthorizerFactory {
        @Override
        public HiveAuthorizer createHiveAuthorizer(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, HiveAuthzSessionContext ctx) {
            TestHiveAuthorizerCheckInvocation.mockedAuthorizer = Mockito.mock(HiveAuthorizer.class);
            return TestHiveAuthorizerCheckInvocation.mockedAuthorizer;
        }
    }

    @Test
    public void testInputSomeColumnsUsed() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("select i from " + (TestHiveAuthorizerCheckInvocation.tableName)) + " where k = 'X' and city = 'Scottsdale-AZ' "));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        checkSingleTableInput(inputs);
        HivePrivilegeObject tableObj = inputs.get(0);
        Assert.assertEquals("no of columns used", 3, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("city", "i", "k"), getSortedList(tableObj.getColumns()));
    }

    @Test
    public void testInputSomeColumnsUsedView() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("select i from " + (TestHiveAuthorizerCheckInvocation.viewName)) + " where k = 'X' and city = 'Scottsdale-AZ' "));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        checkSingleViewInput(inputs);
        HivePrivilegeObject tableObj = inputs.get(0);
        Assert.assertEquals("no of columns used", 3, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("city", "i", "k"), getSortedList(tableObj.getColumns()));
    }

    @Test
    public void testInputSomeColumnsUsedJoin() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((((((((((((((("select " + (TestHiveAuthorizerCheckInvocation.viewName)) + ".i, ") + (TestHiveAuthorizerCheckInvocation.tableName)) + ".city from ") + (TestHiveAuthorizerCheckInvocation.viewName)) + " join ") + (TestHiveAuthorizerCheckInvocation.tableName)) + " on ") + (TestHiveAuthorizerCheckInvocation.viewName)) + ".city = ") + (TestHiveAuthorizerCheckInvocation.tableName)) + ".city where ") + (TestHiveAuthorizerCheckInvocation.tableName)) + ".k = 'X'"));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        Collections.sort(inputs);
        Assert.assertEquals(inputs.size(), 2);
        HivePrivilegeObject tableObj = inputs.get(0);
        Assert.assertEquals(tableObj.getObjectName().toLowerCase(), TestHiveAuthorizerCheckInvocation.tableName.toLowerCase());
        Assert.assertEquals("no of columns used", 2, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("city", "k"), getSortedList(tableObj.getColumns()));
        tableObj = inputs.get(1);
        Assert.assertEquals(tableObj.getObjectName().toLowerCase(), TestHiveAuthorizerCheckInvocation.viewName.toLowerCase());
        Assert.assertEquals("no of columns used", 2, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("city", "i"), getSortedList(tableObj.getColumns()));
    }

    @Test
    public void testInputAllColumnsUsed() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("select * from " + (TestHiveAuthorizerCheckInvocation.tableName)) + " order by i"));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        checkSingleTableInput(inputs);
        HivePrivilegeObject tableObj = inputs.get(0);
        Assert.assertEquals("no of columns used", 5, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("city", "date", "i", "j", "k"), getSortedList(tableObj.getColumns()));
    }

    @Test
    public void testCreateTableWithDb() throws Exception {
        final String newTable = "ctTableWithDb";
        checkCreateViewOrTableWithDb(newTable, (((("create table " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + newTable) + "(i int)"));
    }

    @Test
    public void testCreateViewWithDb() throws Exception {
        final String newTable = "ctViewWithDb";
        checkCreateViewOrTableWithDb(newTable, (((("create table " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + newTable) + "(i int)"));
    }

    @Test
    public void testInputNoColumnsUsed() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile(("describe " + (TestHiveAuthorizerCheckInvocation.tableName)));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        checkSingleTableInput(inputs);
        HivePrivilegeObject tableObj = inputs.get(0);
        Assert.assertNull("columns used", tableObj.getColumns());
    }

    @Test
    public void testPermFunction() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        final String funcName = "testauthfunc1";
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((((("create function " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + funcName) + " as 'org.apache.hadoop.hive.ql.udf.UDFPI'"));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> outputs = getHivePrivilegeObjectInputs().getRight();
        HivePrivilegeObject funcObj;
        HivePrivilegeObject dbObj;
        Assert.assertEquals("number of output objects", 2, outputs.size());
        if ((outputs.get(0).getType()) == (HivePrivilegeObjectType.FUNCTION)) {
            funcObj = outputs.get(0);
            dbObj = outputs.get(1);
        } else {
            funcObj = outputs.get(1);
            dbObj = outputs.get(0);
        }
        Assert.assertEquals("input type", FUNCTION, funcObj.getType());
        Assert.assertTrue("function name", funcName.equalsIgnoreCase(funcObj.getObjectName()));
        Assert.assertTrue("db name", TestHiveAuthorizerCheckInvocation.dbName.equalsIgnoreCase(funcObj.getDbname()));
        Assert.assertEquals("input type", DATABASE, dbObj.getType());
        Assert.assertTrue("db name", TestHiveAuthorizerCheckInvocation.dbName.equalsIgnoreCase(dbObj.getDbname()));
        // actually create the permanent function
        CommandProcessorResponse cresponse = TestHiveAuthorizerCheckInvocation.driver.run(null, true);
        Assert.assertEquals(0, cresponse.getResponseCode());
        // Verify privilege objects
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        status = TestHiveAuthorizerCheckInvocation.driver.compile(((((("select  " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + funcName) + "() , i from ") + (TestHiveAuthorizerCheckInvocation.tableName)));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        Assert.assertEquals("number of input objects", 2, inputs.size());
        HivePrivilegeObject tableObj;
        if ((inputs.get(0).getType()) == (HivePrivilegeObjectType.FUNCTION)) {
            funcObj = inputs.get(0);
            tableObj = inputs.get(1);
        } else {
            funcObj = inputs.get(1);
            tableObj = inputs.get(0);
        }
        Assert.assertEquals("input type", FUNCTION, funcObj.getType());
        Assert.assertEquals("function name", funcName.toLowerCase(), funcObj.getObjectName().toLowerCase());
        Assert.assertEquals("db name", TestHiveAuthorizerCheckInvocation.dbName.toLowerCase(), funcObj.getDbname().toLowerCase());
        Assert.assertEquals("input type", TABLE_OR_VIEW, tableObj.getType());
        Assert.assertEquals("table name", TestHiveAuthorizerCheckInvocation.tableName.toLowerCase(), tableObj.getObjectName().toLowerCase());
        // create 2nd permanent function
        String funcName2 = "funcName2";
        cresponse = TestHiveAuthorizerCheckInvocation.driver.run((((("create function " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + funcName2) + " as 'org.apache.hadoop.hive.ql.udf.UDFRand'"));
        Assert.assertEquals(0, cresponse.getResponseCode());
        // try using 2nd permanent function and verify its only 2nd one that shows up
        // for auth
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        status = TestHiveAuthorizerCheckInvocation.driver.compile(((((("select  " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + funcName2) + "(i)  from ") + (TestHiveAuthorizerCheckInvocation.tableName)));
        Assert.assertEquals(0, status);
        inputs = getHivePrivilegeObjectInputs().getLeft();
        Assert.assertEquals("number of input objects", 2, inputs.size());
        if ((inputs.get(0).getType()) == (HivePrivilegeObjectType.FUNCTION)) {
            funcObj = inputs.get(0);
            tableObj = inputs.get(1);
        } else {
            funcObj = inputs.get(1);
            tableObj = inputs.get(0);
        }
        Assert.assertEquals("input type", FUNCTION, funcObj.getType());
        Assert.assertEquals("function name", funcName2.toLowerCase(), funcObj.getObjectName().toLowerCase());
        Assert.assertEquals("db name", TestHiveAuthorizerCheckInvocation.dbName.toLowerCase(), funcObj.getDbname().toLowerCase());
        Assert.assertEquals("input type", TABLE_OR_VIEW, tableObj.getType());
        Assert.assertEquals("table name", TestHiveAuthorizerCheckInvocation.tableName.toLowerCase(), tableObj.getObjectName().toLowerCase());
        // try using both permanent functions
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        status = TestHiveAuthorizerCheckInvocation.driver.compile(((((((((("select  " + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + funcName2) + "(i), ") + (TestHiveAuthorizerCheckInvocation.dbName)) + ".") + funcName) + "(), j  from ") + (TestHiveAuthorizerCheckInvocation.tableName)));
        Assert.assertEquals(0, status);
        inputs = getHivePrivilegeObjectInputs().getLeft();
        Assert.assertEquals("number of input objects", 3, inputs.size());
        boolean foundF1 = false;
        boolean foundF2 = false;
        boolean foundTable = false;
        for (HivePrivilegeObject inp : inputs) {
            if ((inp.getType()) == (HivePrivilegeObjectType.FUNCTION)) {
                if (funcName.equalsIgnoreCase(inp.getObjectName())) {
                    foundF1 = true;
                } else
                    if (funcName2.equalsIgnoreCase(inp.getObjectName())) {
                        foundF2 = true;
                    }

            } else
                if (((inp.getType()) == (HivePrivilegeObjectType.TABLE_OR_VIEW)) && (TestHiveAuthorizerCheckInvocation.tableName.equalsIgnoreCase(inp.getObjectName().toLowerCase()))) {
                    foundTable = true;
                }

        }
        Assert.assertTrue(("Found " + funcName), foundF1);
        Assert.assertTrue(("Found " + funcName2), foundF2);
        Assert.assertTrue("Found Table", foundTable);
    }

    @Test
    public void testTempFunction() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        final String funcName = "testAuthFunc2";
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("create temporary function " + funcName) + " as 'org.apache.hadoop.hive.ql.udf.UDFPI'"));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> outputs = getHivePrivilegeObjectInputs().getRight();
        HivePrivilegeObject funcObj = outputs.get(0);
        Assert.assertEquals("input type", FUNCTION, funcObj.getType());
        Assert.assertTrue("function name", funcName.equalsIgnoreCase(funcObj.getObjectName()));
        Assert.assertEquals("db name", null, funcObj.getDbname());
    }

    @Test
    public void testTempTable() throws Exception {
        String tmpTableDir = ((getDefaultTmp()) + (File.separator)) + "THSAC_testTableTable";
        final String tableName = "testTempTable";
        {
            // create temp table
            Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
            int status = TestHiveAuthorizerCheckInvocation.driver.run((((("create temporary table " + tableName) + "(i int) location '") + tmpTableDir) + "'")).getResponseCode();
            Assert.assertEquals(0, status);
            List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
            List<HivePrivilegeObject> outputs = getHivePrivilegeObjectInputs().getRight();
            // only the URI should be passed for authorization check
            Assert.assertEquals("input count", 1, inputs.size());
            Assert.assertEquals("input type", LOCAL_URI, inputs.get(0).getType());
            // only the dbname should be passed authorization check
            Assert.assertEquals("output count", 1, outputs.size());
            Assert.assertEquals("output type", DATABASE, outputs.get(0).getType());
            status = TestHiveAuthorizerCheckInvocation.driver.compile(("select * from " + tableName));
            Assert.assertEquals(0, status);
        }
        {
            // select from the temp table
            Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
            int status = TestHiveAuthorizerCheckInvocation.driver.compile((("insert into " + tableName) + " values(1)"));
            Assert.assertEquals(0, status);
            // temp tables should be skipped from authorization
            List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
            List<HivePrivilegeObject> outputs = getHivePrivilegeObjectInputs().getRight();
            System.err.println(("inputs " + inputs));
            System.err.println(("outputs " + outputs));
            Assert.assertEquals("input count", 0, inputs.size());
            Assert.assertEquals("output count", 0, outputs.size());
        }
        {
            // select from the temp table
            Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
            int status = TestHiveAuthorizerCheckInvocation.driver.compile(("select * from " + tableName));
            Assert.assertEquals(0, status);
            // temp tables should be skipped from authorization
            List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
            List<HivePrivilegeObject> outputs = getHivePrivilegeObjectInputs().getRight();
            System.err.println(("inputs " + inputs));
            System.err.println(("outputs " + outputs));
            Assert.assertEquals("input count", 0, inputs.size());
            Assert.assertEquals("output count", 0, outputs.size());
        }
    }

    @Test
    public void testTempTableImplicit() throws Exception {
        final String tableName = "testTempTableImplicit";
        int status = TestHiveAuthorizerCheckInvocation.driver.run((("create table " + tableName) + "(i int)")).getResponseCode();
        Assert.assertEquals(0, status);
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        status = TestHiveAuthorizerCheckInvocation.driver.compile((("insert into " + tableName) + " values (1)"));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        List<HivePrivilegeObject> outputs = getHivePrivilegeObjectInputs().getRight();
        // only the URI should be passed for authorization check
        Assert.assertEquals("input count", 0, inputs.size());
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        status = TestHiveAuthorizerCheckInvocation.driver.compile(("select * from " + tableName));
        Assert.assertEquals(0, status);
        inputs = getHivePrivilegeObjectInputs().getLeft();
        outputs = getHivePrivilegeObjectInputs().getRight();
        // temp tables should be skipped from authorization
        Assert.assertEquals("input count", 1, inputs.size());
        Assert.assertEquals("output count", 0, outputs.size());
    }

    @Test
    public void testUpdateSomeColumnsUsed() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("update " + (TestHiveAuthorizerCheckInvocation.acidTableName)) + " set i = 5 where j = 3"));
        Assert.assertEquals(0, status);
        Pair<List<HivePrivilegeObject>, List<HivePrivilegeObject>> io = getHivePrivilegeObjectInputs();
        List<HivePrivilegeObject> outputs = io.getRight();
        HivePrivilegeObject tableObj = outputs.get(0);
        LOG.debug(("Got privilege object " + tableObj));
        Assert.assertEquals("no of columns used", 1, tableObj.getColumns().size());
        Assert.assertEquals("Column used", "i", tableObj.getColumns().get(0));
        List<HivePrivilegeObject> inputs = io.getLeft();
        Assert.assertEquals(1, inputs.size());
        tableObj = inputs.get(0);
        Assert.assertEquals(2, tableObj.getColumns().size());
        Assert.assertEquals("j", tableObj.getColumns().get(0));
    }

    @Test
    public void testUpdateSomeColumnsUsedExprInSet() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("update " + (TestHiveAuthorizerCheckInvocation.acidTableName)) + " set i = 5, j = k where j = 3"));
        Assert.assertEquals(0, status);
        Pair<List<HivePrivilegeObject>, List<HivePrivilegeObject>> io = getHivePrivilegeObjectInputs();
        List<HivePrivilegeObject> outputs = io.getRight();
        HivePrivilegeObject tableObj = outputs.get(0);
        LOG.debug(("Got privilege object " + tableObj));
        Assert.assertEquals("no of columns used", 2, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("i", "j"), getSortedList(tableObj.getColumns()));
        List<HivePrivilegeObject> inputs = io.getLeft();
        Assert.assertEquals(1, inputs.size());
        tableObj = inputs.get(0);
        Assert.assertEquals(2, tableObj.getColumns().size());
        Assert.assertEquals("Columns used", Arrays.asList("j", "k"), getSortedList(tableObj.getColumns()));
    }

    @Test
    public void testDelete() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile((("delete from " + (TestHiveAuthorizerCheckInvocation.acidTableName)) + " where j = 3"));
        Assert.assertEquals(0, status);
        Pair<List<HivePrivilegeObject>, List<HivePrivilegeObject>> io = getHivePrivilegeObjectInputs();
        List<HivePrivilegeObject> inputs = io.getLeft();
        Assert.assertEquals(1, inputs.size());
        HivePrivilegeObject tableObj = inputs.get(0);
        Assert.assertEquals(1, tableObj.getColumns().size());
        Assert.assertEquals("j", tableObj.getColumns().get(0));
    }

    @Test
    public void testShowTables() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile("show tables");
        Assert.assertEquals(0, status);
        Pair<List<HivePrivilegeObject>, List<HivePrivilegeObject>> io = getHivePrivilegeObjectInputs();
        List<HivePrivilegeObject> inputs = io.getLeft();
        Assert.assertEquals(1, inputs.size());
        HivePrivilegeObject dbObj = inputs.get(0);
        Assert.assertEquals("default", dbObj.getDbname().toLowerCase());
    }

    @Test
    public void testDescDatabase() throws Exception {
        Mockito.reset(TestHiveAuthorizerCheckInvocation.mockedAuthorizer);
        int status = TestHiveAuthorizerCheckInvocation.driver.compile(("describe database " + (TestHiveAuthorizerCheckInvocation.dbName)));
        Assert.assertEquals(0, status);
        Pair<List<HivePrivilegeObject>, List<HivePrivilegeObject>> io = getHivePrivilegeObjectInputs();
        List<HivePrivilegeObject> inputs = io.getLeft();
        Assert.assertEquals(1, inputs.size());
        HivePrivilegeObject dbObj = inputs.get(0);
        Assert.assertEquals(TestHiveAuthorizerCheckInvocation.dbName.toLowerCase(), dbObj.getDbname().toLowerCase());
    }

    @Test
    public void testReplDump() throws Exception {
        resetAuthorizer();
        int status = TestHiveAuthorizerCheckInvocation.driver.compile(("repl dump " + (TestHiveAuthorizerCheckInvocation.dbName)));
        Assert.assertEquals(0, status);
        List<HivePrivilegeObject> inputs = getHivePrivilegeObjectInputs().getLeft();
        HivePrivilegeObject dbObj = inputs.get(0);
        Assert.assertEquals("input type", DATABASE, dbObj.getType());
        Assert.assertEquals("db name", TestHiveAuthorizerCheckInvocation.dbName.toLowerCase(), dbObj.getDbname());
        resetAuthorizer();
        status = TestHiveAuthorizerCheckInvocation.driver.compile(("repl dump " + (TestHiveAuthorizerCheckInvocation.fullInTableName)));
        Assert.assertEquals(0, status);
        inputs = getHivePrivilegeObjectInputs().getLeft();
        dbObj = inputs.get(0);
        Assert.assertEquals("input type", TABLE_OR_VIEW, dbObj.getType());
        Assert.assertEquals("db name", TestHiveAuthorizerCheckInvocation.dbName.toLowerCase(), dbObj.getDbname());
        Assert.assertEquals("table name", TestHiveAuthorizerCheckInvocation.inDbTableName.toLowerCase(), dbObj.getObjectName());
    }
}

