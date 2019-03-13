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
package org.apache.hive.hcatalog.hbase;


import DataType.CHARARRAY;
import DataType.FLOAT;
import DataType.INTEGER;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
import org.junit.Assert;
import org.junit.Test;


public class TestPigHBaseStorageHandler extends SkeletonHBaseTest {
    private static HiveConf hcatConf;

    private static IDriver driver;

    private static String mypath;

    private final byte[] FAMILY = Bytes.toBytes("testFamily");

    private final byte[] QUALIFIER1 = Bytes.toBytes("testQualifier1");

    private final byte[] QUALIFIER2 = Bytes.toBytes("testQualifier2");

    @Test
    public void testPigHBaseSchema() throws Exception {
        Initialize();
        String tableName = newTableName("MyTable");
        String databaseName = newTableName("MyDatabase");
        // Table name will be lower case unless specified by hbase.table.name property
        String hbaseTableName = "testTable";
        String db_dir = HCatUtil.makePathASafeFileName(((getTestDir()) + "/hbasedb"));
        String dbQuery = ((("CREATE DATABASE IF NOT EXISTS " + databaseName) + " LOCATION '") + db_dir) + "'";
        String deleteQuery = (("DROP TABLE " + databaseName) + ".") + tableName;
        String tableQuery = (((((((("CREATE TABLE " + databaseName) + ".") + tableName) + "(key float, testqualifier1 string, testqualifier2 int) STORED BY ") + "'org.apache.hadoop.hive.hbase.HBaseStorageHandler'") + " WITH SERDEPROPERTIES ('hbase.columns.mapping'=':key,testFamily:testQualifier1,testFamily:testQualifier2')") + " TBLPROPERTIES ('hbase.table.name'='") + hbaseTableName) + "')";
        CommandProcessorResponse responseOne = TestPigHBaseStorageHandler.driver.run(deleteQuery);
        Assert.assertEquals(0, responseOne.getResponseCode());
        CommandProcessorResponse responseTwo = TestPigHBaseStorageHandler.driver.run(dbQuery);
        Assert.assertEquals(0, responseTwo.getResponseCode());
        CommandProcessorResponse responseThree = TestPigHBaseStorageHandler.driver.run(tableQuery);
        Connection connection = null;
        Admin hAdmin = null;
        boolean doesTableExist = false;
        try {
            connection = ConnectionFactory.createConnection(getHbaseConf());
            hAdmin = connection.getAdmin();
            doesTableExist = hAdmin.tableExists(TableName.valueOf(hbaseTableName));
        } finally {
            if (hAdmin != null) {
                hAdmin.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        Assert.assertTrue(doesTableExist);
        PigServer server = HCatBaseTest.createPigServer(false, TestPigHBaseStorageHandler.hcatConf.getAllProperties());
        server.registerQuery((((("A = load '" + databaseName) + ".") + tableName) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Schema dumpedASchema = server.dumpSchema("A");
        List<FieldSchema> fields = dumpedASchema.getFields();
        Assert.assertEquals(3, fields.size());
        Assert.assertEquals(FLOAT, fields.get(0).type);
        Assert.assertEquals("key", fields.get(0).alias.toLowerCase());
        Assert.assertEquals(CHARARRAY, fields.get(1).type);
        Assert.assertEquals("testQualifier1".toLowerCase(), fields.get(1).alias.toLowerCase());
        Assert.assertEquals(INTEGER, fields.get(2).type);
        Assert.assertEquals("testQualifier2".toLowerCase(), fields.get(2).alias.toLowerCase());
    }

    @Test
    public void testPigFilterProjection() throws Exception {
        Initialize();
        String tableName = newTableName("MyTable");
        String databaseName = newTableName("MyDatabase");
        // Table name will be lower case unless specified by hbase.table.name property
        String hbaseTableName = ((databaseName + ".") + tableName).toLowerCase();
        String db_dir = HCatUtil.makePathASafeFileName(((getTestDir()) + "/hbasedb"));
        String dbQuery = ((("CREATE DATABASE IF NOT EXISTS " + databaseName) + " LOCATION '") + db_dir) + "'";
        String deleteQuery = (("DROP TABLE " + databaseName) + ".") + tableName;
        String tableQuery = (((((("CREATE TABLE " + databaseName) + ".") + tableName) + "(key int, testqualifier1 string, testqualifier2 string) STORED BY ") + "'org.apache.hadoop.hive.hbase.HBaseStorageHandler'") + " WITH SERDEPROPERTIES ('hbase.columns.mapping'=':key,testFamily:testQualifier1,testFamily:testQualifier2')") + " TBLPROPERTIES ('hbase.table.default.storage.type'='binary')";
        CommandProcessorResponse responseOne = TestPigHBaseStorageHandler.driver.run(deleteQuery);
        Assert.assertEquals(0, responseOne.getResponseCode());
        CommandProcessorResponse responseTwo = TestPigHBaseStorageHandler.driver.run(dbQuery);
        Assert.assertEquals(0, responseTwo.getResponseCode());
        CommandProcessorResponse responseThree = TestPigHBaseStorageHandler.driver.run(tableQuery);
        Connection connection = null;
        Admin hAdmin = null;
        Table table = null;
        ResultScanner scanner = null;
        boolean doesTableExist = false;
        try {
            connection = ConnectionFactory.createConnection(getHbaseConf());
            hAdmin = connection.getAdmin();
            doesTableExist = hAdmin.tableExists(TableName.valueOf(hbaseTableName));
            Assert.assertTrue(doesTableExist);
            populateHBaseTable(hbaseTableName, connection);
            table = connection.getTable(TableName.valueOf(hbaseTableName));
            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes("testFamily"));
            scanner = table.getScanner(scan);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (table != null) {
                table.close();
            }
            if (hAdmin != null) {
                hAdmin.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        int index = 1;
        PigServer server = HCatBaseTest.createPigServer(false, TestPigHBaseStorageHandler.hcatConf.getAllProperties());
        server.registerQuery((((("A = load '" + databaseName) + ".") + tableName) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        server.registerQuery("B = filter A by key < 5;");
        server.registerQuery("C = foreach B generate key,testqualifier2;");
        Iterator<Tuple> itr = server.openIterator("C");
        // verify if the filter is correct and returns 2 rows and contains 2 columns and the contents match
        while (itr.hasNext()) {
            Tuple t = itr.next();
            Assert.assertTrue(((t.size()) == 2));
            Assert.assertTrue(((t.get(0).getClass()) == (Integer.class)));
            Assert.assertEquals(index, t.get(0));
            Assert.assertTrue(((t.get(1).getClass()) == (String.class)));
            Assert.assertEquals(("textB-" + index), t.get(1));
            index++;
        } 
        Assert.assertEquals((index - 1), 4);
    }

    @Test
    public void testPigPopulation() throws Exception {
        Initialize();
        String tableName = newTableName("MyTable");
        String databaseName = newTableName("MyDatabase");
        // Table name will be lower case unless specified by hbase.table.name property
        String hbaseTableName = ((databaseName + ".") + tableName).toLowerCase();
        String db_dir = HCatUtil.makePathASafeFileName(((getTestDir()) + "/hbasedb"));
        String POPTXT_FILE_NAME = db_dir + "testfile.txt";
        float f = -100.1F;
        String dbQuery = ((("CREATE DATABASE IF NOT EXISTS " + databaseName) + " LOCATION '") + db_dir) + "'";
        String deleteQuery = (("DROP TABLE " + databaseName) + ".") + tableName;
        String tableQuery = (((((("CREATE TABLE " + databaseName) + ".") + tableName) + "(key int, testqualifier1 float, testqualifier2 string) STORED BY ") + "'org.apache.hadoop.hive.hbase.HBaseStorageHandler'") + " WITH SERDEPROPERTIES ('hbase.columns.mapping'=':key,testFamily:testQualifier1,testFamily:testQualifier2')") + " TBLPROPERTIES ('hbase.table.default.storage.type'='binary')";
        String selectQuery = (("SELECT * from " + (databaseName.toLowerCase())) + ".") + (tableName.toLowerCase());
        CommandProcessorResponse responseOne = TestPigHBaseStorageHandler.driver.run(deleteQuery);
        Assert.assertEquals(0, responseOne.getResponseCode());
        CommandProcessorResponse responseTwo = TestPigHBaseStorageHandler.driver.run(dbQuery);
        Assert.assertEquals(0, responseTwo.getResponseCode());
        CommandProcessorResponse responseThree = TestPigHBaseStorageHandler.driver.run(tableQuery);
        Connection connection = null;
        Admin hAdmin = null;
        Table table = null;
        ResultScanner scanner = null;
        boolean doesTableExist = false;
        try {
            connection = ConnectionFactory.createConnection(getHbaseConf());
            hAdmin = connection.getAdmin();
            doesTableExist = hAdmin.tableExists(TableName.valueOf(hbaseTableName));
            Assert.assertTrue(doesTableExist);
            TestPigHBaseStorageHandler.createTestDataFile(POPTXT_FILE_NAME);
            PigServer server = HCatBaseTest.createPigServer(false, TestPigHBaseStorageHandler.hcatConf.getAllProperties());
            server.registerQuery((("A = load '" + POPTXT_FILE_NAME) + "' using PigStorage() as (key:int, testqualifier1:float, testqualifier2:chararray);"));
            server.registerQuery("B = filter A by (key > 2) AND (key < 8) ;");
            server.registerQuery((((("store B into '" + (databaseName.toLowerCase())) + ".") + (tableName.toLowerCase())) + "' using  org.apache.hive.hcatalog.pig.HCatStorer();"));
            server.registerQuery((((("C = load '" + (databaseName.toLowerCase())) + ".") + (tableName.toLowerCase())) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
            // Schema should be same
            Schema dumpedBSchema = server.dumpSchema("C");
            List<FieldSchema> fields = dumpedBSchema.getFields();
            Assert.assertEquals(3, fields.size());
            Assert.assertEquals(INTEGER, fields.get(0).type);
            Assert.assertEquals("key", fields.get(0).alias.toLowerCase());
            Assert.assertEquals(FLOAT, fields.get(1).type);
            Assert.assertEquals("testQualifier1".toLowerCase(), fields.get(1).alias.toLowerCase());
            Assert.assertEquals(CHARARRAY, fields.get(2).type);
            Assert.assertEquals("testQualifier2".toLowerCase(), fields.get(2).alias.toLowerCase());
            // Query the hbase table and check the key is valid and only 5  are present
            table = connection.getTable(TableName.valueOf(hbaseTableName));
            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes("testFamily"));
            byte[] familyNameBytes = Bytes.toBytes("testFamily");
            scanner = table.getScanner(scan);
            int index = 3;
            int count = 0;
            for (Result result : scanner) {
                // key is correct
                Assert.assertEquals(index, Bytes.toInt(result.getRow()));
                // first column exists
                Assert.assertTrue(result.containsColumn(familyNameBytes, Bytes.toBytes("testQualifier1")));
                // value is correct
                Assert.assertEquals((index + f), Bytes.toFloat(result.getValue(familyNameBytes, Bytes.toBytes("testQualifier1"))), 0);
                // second column exists
                Assert.assertTrue(result.containsColumn(familyNameBytes, Bytes.toBytes("testQualifier2")));
                // value is correct
                Assert.assertEquals(("textB-" + index).toString(), Bytes.toString(result.getValue(familyNameBytes, Bytes.toBytes("testQualifier2"))));
                index++;
                count++;
            }
            // 5 rows should be returned
            Assert.assertEquals(count, 5);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (table != null) {
                table.close();
            }
            if (hAdmin != null) {
                hAdmin.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        // Check if hive returns results correctly
        TestPigHBaseStorageHandler.driver.run(selectQuery);
        ArrayList<String> result = new ArrayList<String>();
        TestPigHBaseStorageHandler.driver.getResults(result);
        // Query using the hive command line
        Assert.assertEquals(5, result.size());
        Iterator<String> itr = result.iterator();
        for (int i = 3; i <= 7; i++) {
            String[] tokens = itr.next().split("\\s+");
            Assert.assertEquals(i, Integer.parseInt(tokens[0]));
            Assert.assertEquals((i + f), Float.parseFloat(tokens[1]), 0);
            Assert.assertEquals(("textB-" + i).toString(), tokens[2]);
        }
        // delete the table from the database
        CommandProcessorResponse responseFour = TestPigHBaseStorageHandler.driver.run(deleteQuery);
        Assert.assertEquals(0, responseFour.getResponseCode());
    }
}

