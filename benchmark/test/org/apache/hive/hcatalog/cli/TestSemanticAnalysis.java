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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.cli;


import ErrorMsg.DATABASE_NOT_EXISTS;
import Warehouse.DEFAULT_DATABASE_NAME;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat;
import org.apache.hadoop.hive.ql.io.RCFileInputFormat;
import org.apache.hadoop.hive.ql.io.RCFileOutputFormat;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// This test case currently fails, since add partitions don't inherit anything from tables.
// public void testAddPartInheritDrivers() throws MetaException, TException, NoSuchObjectException{
// 
// hcatDriver.run("drop table "+TBL_NAME);
// hcatDriver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE");
// hcatDriver.run("alter table "+TBL_NAME+" add partition (b='2010-10-10')");
// 
// List<String> partVals = new ArrayList<String>(1);
// partVals.add("2010-10-10");
// 
// Map<String,String> map = client.getPartition(MetaStoreUtils.DEFAULT_DATABASE_NAME, TBL_NAME, partVals).getParameters();
// assertEquals(map.get(InitializeInput.HOWL_ISD_CLASS), RCFileInputStorageDriver.class.getName());
// assertEquals(map.get(InitializeInput.HOWL_OSD_CLASS), RCFileOutputStorageDriver.class.getName());
// }
public class TestSemanticAnalysis extends HCatBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestSemanticAnalysis.class);

    private static final String TBL_NAME = "junit_sem_analysis";

    private IDriver hcatDriver = null;

    private String query;

    @Test
    public void testDescDB() throws Exception {
        hcatDriver.run("drop database mydb cascade");
        Assert.assertEquals(0, hcatDriver.run("create database mydb").getResponseCode());
        CommandProcessorResponse resp = hcatDriver.run("describe database mydb");
        Assert.assertEquals(0, resp.getResponseCode());
        ArrayList<String> result = new ArrayList<String>();
        hcatDriver.getResults(result);
        Assert.assertTrue(result.get(0).contains("mydb"));// location is not shown in test mode

        hcatDriver.run("drop database mydb cascade");
    }

    @Test
    public void testCreateTblWithLowerCasePartNames() throws Exception {
        driver.run("drop table junit_sem_analysis");
        CommandProcessorResponse resp = driver.run("create table junit_sem_analysis (a int) partitioned by (B string) stored as TEXTFILE");
        Assert.assertEquals(resp.getResponseCode(), 0);
        Assert.assertEquals(null, resp.getErrorMessage());
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        Assert.assertEquals("Partition key name case problem", "b", tbl.getPartitionKeys().get(0).getName());
        driver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAlterTblFFpart() throws Exception {
        driver.run("drop table junit_sem_analysis");
        driver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as TEXTFILE");
        driver.run("alter table junit_sem_analysis add partition (b='2010-10-10')");
        hcatDriver.run("alter table junit_sem_analysis partition (b='2010-10-10') set fileformat RCFILE");
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        Assert.assertEquals(TextInputFormat.class.getName(), tbl.getSd().getInputFormat());
        Assert.assertEquals(HiveIgnoreKeyTextOutputFormat.class.getName(), tbl.getSd().getOutputFormat());
        List<String> partVals = new ArrayList<String>(1);
        partVals.add("2010-10-10");
        Partition part = client.getPartition(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME, partVals);
        Assert.assertEquals(RCFileInputFormat.class.getName(), part.getSd().getInputFormat());
        Assert.assertEquals(RCFileOutputFormat.class.getName(), part.getSd().getOutputFormat());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testUsNonExistentDB() throws Exception {
        CommandProcessorResponse resp = hcatDriver.run("use no_such_db");
        Assert.assertEquals(DATABASE_NOT_EXISTS.getErrorCode(), resp.getResponseCode());
    }

    @Test
    public void testDatabaseOperations() throws Exception {
        List<String> dbs = client.getAllDatabases();
        String testDb1 = "testdatabaseoperatons1";
        String testDb2 = "testdatabaseoperatons2";
        if (dbs.contains(testDb1.toLowerCase())) {
            Assert.assertEquals(0, hcatDriver.run(("drop database " + testDb1)).getResponseCode());
        }
        if (dbs.contains(testDb2.toLowerCase())) {
            Assert.assertEquals(0, hcatDriver.run(("drop database " + testDb2)).getResponseCode());
        }
        Assert.assertEquals(0, hcatDriver.run(("create database " + testDb1)).getResponseCode());
        Assert.assertTrue(client.getAllDatabases().contains(testDb1));
        Assert.assertEquals(0, hcatDriver.run(("create database if not exists " + testDb1)).getResponseCode());
        Assert.assertTrue(client.getAllDatabases().contains(testDb1));
        Assert.assertEquals(0, hcatDriver.run(("create database if not exists " + testDb2)).getResponseCode());
        Assert.assertTrue(client.getAllDatabases().contains(testDb2));
        Assert.assertEquals(0, hcatDriver.run(("drop database " + testDb1)).getResponseCode());
        Assert.assertEquals(0, hcatDriver.run(("drop database " + testDb2)).getResponseCode());
        Assert.assertFalse(client.getAllDatabases().contains(testDb1));
        Assert.assertFalse(client.getAllDatabases().contains(testDb2));
    }

    @Test
    public void testCreateTableIfNotExists() throws Exception {
        hcatDriver.run(("drop table " + (TestSemanticAnalysis.TBL_NAME)));
        hcatDriver.run((("create table " + (TestSemanticAnalysis.TBL_NAME)) + " (a int) stored as RCFILE"));
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        List<FieldSchema> cols = tbl.getSd().getCols();
        Assert.assertEquals(1, cols.size());
        Assert.assertTrue(cols.get(0).equals(new FieldSchema("a", "int", null)));
        Assert.assertEquals(RCFileInputFormat.class.getName(), tbl.getSd().getInputFormat());
        Assert.assertEquals(RCFileOutputFormat.class.getName(), tbl.getSd().getOutputFormat());
        CommandProcessorResponse resp = hcatDriver.run("create table if not exists junit_sem_analysis (a int) stored as RCFILE");
        Assert.assertEquals(0, resp.getResponseCode());
        Assert.assertNull(resp.getErrorMessage());
        tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        cols = tbl.getSd().getCols();
        Assert.assertEquals(1, cols.size());
        Assert.assertTrue(cols.get(0).equals(new FieldSchema("a", "int", null)));
        Assert.assertEquals(RCFileInputFormat.class.getName(), tbl.getSd().getInputFormat());
        Assert.assertEquals(RCFileOutputFormat.class.getName(), tbl.getSd().getOutputFormat());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAlterTblTouch() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        hcatDriver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE");
        CommandProcessorResponse response = hcatDriver.run("alter table junit_sem_analysis touch");
        Assert.assertEquals(0, response.getResponseCode());
        hcatDriver.run("alter table junit_sem_analysis touch partition (b='12')");
        Assert.assertEquals(0, response.getResponseCode());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testChangeColumns() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        hcatDriver.run("create table junit_sem_analysis (a int, c string) partitioned by (b string) stored as RCFILE");
        CommandProcessorResponse response = hcatDriver.run("alter table junit_sem_analysis change a a1 int");
        Assert.assertEquals(0, response.getResponseCode());
        response = hcatDriver.run("alter table junit_sem_analysis change a1 a string");
        Assert.assertEquals(0, response.getResponseCode());
        response = hcatDriver.run("alter table junit_sem_analysis change a a int after c");
        Assert.assertEquals(0, response.getResponseCode());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAddReplaceCols() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        hcatDriver.run("create table junit_sem_analysis (a int, c string) partitioned by (b string) stored as RCFILE");
        CommandProcessorResponse response = hcatDriver.run("alter table junit_sem_analysis replace columns (a1 tinyint)");
        Assert.assertEquals(0, response.getResponseCode());
        response = hcatDriver.run("alter table junit_sem_analysis add columns (d tinyint)");
        Assert.assertEquals(0, response.getResponseCode());
        Assert.assertNull(response.getErrorMessage());
        response = hcatDriver.run("describe extended junit_sem_analysis");
        Assert.assertEquals(0, response.getResponseCode());
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        List<FieldSchema> cols = tbl.getSd().getCols();
        Assert.assertEquals(2, cols.size());
        Assert.assertTrue(cols.get(0).equals(new FieldSchema("a1", "tinyint", null)));
        Assert.assertTrue(cols.get(1).equals(new FieldSchema("d", "tinyint", null)));
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAlterTblClusteredBy() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        hcatDriver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE");
        CommandProcessorResponse response = hcatDriver.run("alter table junit_sem_analysis clustered by (a) into 7 buckets");
        Assert.assertEquals(0, response.getResponseCode());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAlterTableRename() throws Exception {
        hcatDriver.run("drop table oldname");
        hcatDriver.run("drop table newname");
        hcatDriver.run("create table oldname (a int)");
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, "oldname");
        Assert.assertTrue(("The old table location is: " + (tbl.getSd().getLocation())), tbl.getSd().getLocation().contains("oldname"));
        hcatDriver.run("alter table oldname rename to newNAME");
        tbl = client.getTable(DEFAULT_DATABASE_NAME, "newname");
        // since the oldname table is not under its database (See HIVE-15059), the renamed oldname table will keep
        // its location after HIVE-14909. I changed to check the existence of the newname table and its name instead
        // of verifying its location
        // assertTrue(tbl.getSd().getLocation().contains("newname"));
        Assert.assertTrue((tbl != null));
        Assert.assertTrue(tbl.getTableName().equalsIgnoreCase("newname"));
        hcatDriver.run("drop table newname");
    }

    @Test
    public void testAlterTableSetFF() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        hcatDriver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE");
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        Assert.assertEquals(RCFileInputFormat.class.getName(), tbl.getSd().getInputFormat());
        Assert.assertEquals(RCFileOutputFormat.class.getName(), tbl.getSd().getOutputFormat());
        hcatDriver.run(("alter table junit_sem_analysis set fileformat INPUTFORMAT 'org.apache.hadoop.hive.ql.io.RCFileInputFormat' OUTPUTFORMAT " + "'org.apache.hadoop.hive.ql.io.RCFileOutputFormat' inputdriver 'mydriver' outputdriver 'yourdriver'"));
        hcatDriver.run("desc extended junit_sem_analysis");
        tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        Assert.assertEquals(RCFileInputFormat.class.getName(), tbl.getSd().getInputFormat());
        Assert.assertEquals(RCFileOutputFormat.class.getName(), tbl.getSd().getOutputFormat());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAddPartFail() throws Exception {
        driver.run("drop table junit_sem_analysis");
        driver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE");
        CommandProcessorResponse response = hcatDriver.run("alter table junit_sem_analysis add partition (b='2') location 'README.txt'");
        Assert.assertEquals(0, response.getResponseCode());
        driver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAddPartPass() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        hcatDriver.run("create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE");
        CommandProcessorResponse response = hcatDriver.run((("alter table junit_sem_analysis add partition (b='2') location '" + (HCatBaseTest.TEST_DATA_DIR)) + "'"));
        Assert.assertEquals(0, response.getResponseCode());
        Assert.assertNull(response.getErrorMessage());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testCTAS() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int) as select * from tbl2";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(40000, response.getResponseCode());
        Assert.assertTrue(response.getErrorMessage().contains("FAILED: SemanticException Operation not supported. Create table as Select is not a valid operation."));
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testStoredAs() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int)";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(0, response.getResponseCode());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testAddDriverInfo() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int) partitioned by (b string)  stored as " + ("INPUTFORMAT 'org.apache.hadoop.hive.ql.io.RCFileInputFormat' OUTPUTFORMAT " + "'org.apache.hadoop.hive.ql.io.RCFileOutputFormat' inputdriver 'mydriver' outputdriver 'yourdriver' ");
        Assert.assertEquals(0, hcatDriver.run(query).getResponseCode());
        Table tbl = client.getTable(DEFAULT_DATABASE_NAME, TestSemanticAnalysis.TBL_NAME);
        Assert.assertEquals(RCFileInputFormat.class.getName(), tbl.getSd().getInputFormat());
        Assert.assertEquals(RCFileOutputFormat.class.getName(), tbl.getSd().getOutputFormat());
        hcatDriver.run("drop table junit_sem_analysis");
    }

    @Test
    public void testInvalidateNonStringPartition() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int) partitioned by (b int)  stored as RCFILE";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(40000, response.getResponseCode());
        Assert.assertEquals("FAILED: SemanticException Operation not supported. HCatalog only supports partition columns of type string. For column: b Found type: int", response.getErrorMessage());
    }

    @Test
    public void testInvalidateSeqFileStoredAs() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int) partitioned by (b string)  stored as SEQUENCEFILE";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(0, response.getResponseCode());
    }

    @Test
    public void testInvalidateTextFileStoredAs() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int) partitioned by (b string)  stored as TEXTFILE";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(0, response.getResponseCode());
    }

    @Test
    public void testInvalidateClusteredBy() throws Exception {
        hcatDriver.run("drop table junit_sem_analysis");
        query = "create table junit_sem_analysis (a int) partitioned by (b string) clustered by (a) into 10 buckets stored as TEXTFILE";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(0, response.getResponseCode());
    }

    @Test
    public void testCTLFail() throws Exception {
        driver.run("drop table junit_sem_analysis");
        driver.run("drop table like_table");
        query = "create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE";
        driver.run(query);
        query = "create table like_table like junit_sem_analysis";
        CommandProcessorResponse response = hcatDriver.run(query);
        Assert.assertEquals(0, response.getResponseCode());
    }

    @Test
    public void testCTLPass() throws Exception {
        try {
            hcatDriver.run("drop table junit_sem_analysis");
        } catch (Exception e) {
            TestSemanticAnalysis.LOG.error("Error in drop table.", e);
        }
        query = "create table junit_sem_analysis (a int) partitioned by (b string) stored as RCFILE";
        hcatDriver.run(query);
        String likeTbl = "like_table";
        hcatDriver.run(("drop table " + likeTbl));
        query = "create table like_table like junit_sem_analysis";
        CommandProcessorResponse resp = hcatDriver.run(query);
        Assert.assertEquals(0, resp.getResponseCode());
        // Table tbl = client.getTable(MetaStoreUtils.DEFAULT_DATABASE_NAME, likeTbl);
        // assertEquals(likeTbl,tbl.getTableName());
        // List<FieldSchema> cols = tbl.getSd().getCols();
        // assertEquals(1, cols.size());
        // assertEquals(new FieldSchema("a", "int", null), cols.get(0));
        // assertEquals("org.apache.hadoop.hive.ql.io.RCFileInputFormat",tbl.getSd().getInputFormat());
        // assertEquals("org.apache.hadoop.hive.ql.io.RCFileOutputFormat",tbl.getSd().getOutputFormat());
        // Map<String, String> tblParams = tbl.getParameters();
        // assertEquals("org.apache.hadoop.hive.hcat.rcfile.RCFileInputStorageDriver", tblParams.get("hcat.isd"));
        // assertEquals("org.apache.hadoop.hive.hcat.rcfile.RCFileOutputStorageDriver", tblParams.get("hcat.osd"));
        // 
        // hcatDriver.run("drop table junit_sem_analysis");
        // hcatDriver.run("drop table "+likeTbl);
    }
}

