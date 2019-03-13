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
package org.apache.hive.hcatalog.api;


import HCatClient.DropDBMode.CASCADE;
import HCatConstants.HCAT_MSGBUS_TOPIC_NAME;
import HCatPartitionSpec.HCatPartitionIterator;
import HCatTable.NO_DIFF;
import HCatTable.TableAttribute;
import HCatTable.TableAttribute.COLUMNS;
import HCatTable.TableAttribute.INPUT_FORMAT;
import HCatTable.TableAttribute.OUTPUT_FORMAT;
import HCatTable.TableAttribute.SERDE;
import HCatTable.TableAttribute.SERDE_PROPERTIES;
import HCatTable.TableAttribute.TABLE_PROPERTIES;
import HiveConf.ConfVars.METASTORE_BATCH_RETRIEVE_MAX.varname;
import PartitionEventType.LOAD_DONE;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat;
import org.apache.hadoop.hive.ql.io.RCFileInputFormat;
import org.apache.hadoop.hive.ql.io.RCFileOutputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcInputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcSerde;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.columnar.LazyBinaryColumnarSerDe;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hive.hcatalog.api.repl.Command;
import org.apache.hive.hcatalog.api.repl.ReplicationTask;
import org.apache.hive.hcatalog.api.repl.ReplicationUtils;
import org.apache.hive.hcatalog.api.repl.StagingDirectoryProvider;
import org.apache.hive.hcatalog.api.repl.exim.EximReplicationTaskFactory;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema.Type;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serdeConstants.COLLECTION_DELIM;
import serdeConstants.ESCAPE_CHAR;
import serdeConstants.FIELD_DELIM;
import serdeConstants.LINE_DELIM;
import serdeConstants.MAPKEY_DELIM;
import serdeConstants.SERIALIZATION_NULL_FORMAT;


public class TestHCatClient {
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatClient.class);

    private static int msPort;

    private static HiveConf hcatConf;

    private static boolean isReplicationTargetHCatRunning = false;

    private static int replicationTargetHCatPort;

    private static HiveConf replicationTargetHCatConf;

    private static SecurityManager securityManager;

    private static boolean useExternalMS = false;

    @Test
    public void testBasicDDLCommands() throws Exception {
        String db = "testdb";
        String tableOne = "testTable1";
        String tableTwo = "testTable2";
        String tableThree = "testTable3";
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        client.dropDatabase(db, true, CASCADE);
        HCatCreateDBDesc dbDesc = HCatCreateDBDesc.create(db).ifNotExists(false).build();
        client.createDatabase(dbDesc);
        List<String> dbNames = client.listDatabaseNamesByPattern("*");
        Assert.assertTrue(dbNames.contains("default"));
        Assert.assertTrue(dbNames.contains(db));
        HCatDatabase testDb = client.getDatabase(db);
        Assert.assertTrue(((testDb.getComment()) == null));
        Assert.assertTrue(((testDb.getProperties().size()) == 0));
        String warehouseDir = System.getProperty("test.warehouse.dir", "/user/hive/warehouse");
        if (TestHCatClient.useExternalMS) {
            Assert.assertTrue(testDb.getLocation().matches((((".*" + "/") + db) + ".db")));
        } else {
            String expectedDir = ((warehouseDir.replaceFirst("pfile:///", "pfile:/")) + "/") + (TestHCatClient.msPort);
            Assert.assertEquals((((expectedDir + "/") + db) + ".db"), testDb.getLocation());
        }
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("id", Type.INT, "id comment"));
        cols.add(new HCatFieldSchema("value", Type.STRING, "value comment"));
        HCatCreateTableDesc tableDesc = HCatCreateTableDesc.create(db, tableOne, cols).fileFormat("rcfile").build();
        client.createTable(tableDesc);
        HCatTable table1 = client.getTable(db, tableOne);
        Assert.assertTrue(table1.getInputFileFormat().equalsIgnoreCase(RCFileInputFormat.class.getName()));
        Assert.assertTrue(table1.getOutputFileFormat().equalsIgnoreCase(RCFileOutputFormat.class.getName()));
        Assert.assertTrue(table1.getSerdeLib().equalsIgnoreCase(LazyBinaryColumnarSerDe.class.getName()));
        Assert.assertTrue(table1.getCols().equals(cols));
        // Since "ifexists" was not set to true, trying to create the same table
        // again
        // will result in an exception.
        try {
            client.createTable(tableDesc);
            Assert.fail("Expected exception");
        } catch (HCatException e) {
            Assert.assertTrue(e.getMessage().contains("AlreadyExistsException while creating table."));
        }
        client.dropTable(db, tableOne, true);
        HCatCreateTableDesc tableDesc2 = HCatCreateTableDesc.create(db, tableTwo, cols).fieldsTerminatedBy('\u0001').escapeChar('\u0002').linesTerminatedBy('\u0003').mapKeysTerminatedBy('\u0004').collectionItemsTerminatedBy('\u0005').nullDefinedAs('\u0006').build();
        client.createTable(tableDesc2);
        HCatTable table2 = client.getTable(db, tableTwo);
        Assert.assertTrue(("Expected TextInputFormat, but got: " + (table2.getInputFileFormat())), table2.getInputFileFormat().equalsIgnoreCase(TextInputFormat.class.getName()));
        Assert.assertTrue(table2.getOutputFileFormat().equalsIgnoreCase(HiveIgnoreKeyTextOutputFormat.class.getName()));
        Assert.assertTrue("SerdeParams not found", ((table2.getSerdeParams()) != null));
        Assert.assertEquals(("checking " + (serdeConstants.FIELD_DELIM)), Character.toString('\u0001'), table2.getSerdeParams().get(FIELD_DELIM));
        Assert.assertEquals(("checking " + (serdeConstants.ESCAPE_CHAR)), Character.toString('\u0002'), table2.getSerdeParams().get(ESCAPE_CHAR));
        Assert.assertEquals(("checking " + (serdeConstants.LINE_DELIM)), Character.toString('\u0003'), table2.getSerdeParams().get(LINE_DELIM));
        Assert.assertEquals(("checking " + (serdeConstants.MAPKEY_DELIM)), Character.toString('\u0004'), table2.getSerdeParams().get(MAPKEY_DELIM));
        Assert.assertEquals(("checking " + (serdeConstants.COLLECTION_DELIM)), Character.toString('\u0005'), table2.getSerdeParams().get(COLLECTION_DELIM));
        Assert.assertEquals(("checking " + (serdeConstants.SERIALIZATION_NULL_FORMAT)), Character.toString('\u0006'), table2.getSerdeParams().get(SERIALIZATION_NULL_FORMAT));
        Assert.assertTrue(table2.getLocation().toLowerCase().matches((".*" + (((("/" + db) + ".db/") + tableTwo).toLowerCase()))));
        HCatCreateTableDesc tableDesc3 = HCatCreateTableDesc.create(db, tableThree, cols).fileFormat("orcfile").build();
        client.createTable(tableDesc3);
        HCatTable table3 = client.getTable(db, tableThree);
        Assert.assertTrue(table3.getInputFileFormat().equalsIgnoreCase(OrcInputFormat.class.getName()));
        Assert.assertTrue(table3.getOutputFileFormat().equalsIgnoreCase(OrcOutputFormat.class.getName()));
        Assert.assertTrue(table3.getSerdeLib().equalsIgnoreCase(OrcSerde.class.getName()));
        Assert.assertTrue(table1.getCols().equals(cols));
        client.close();
    }

    /**
     * This test tests that a plain table instantiation matches what hive says an
     * empty table create should look like.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEmptyTableInstantiation() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String dbName = "default";
        String tblName = "testEmptyCreate";
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("id", Type.INT, "id comment"));
        cols.add(new HCatFieldSchema("value", Type.STRING, "value comment"));
        client.dropTable(dbName, tblName, true);
        // Create a minimalistic table
        client.createTable(HCatCreateTableDesc.create(new HCatTable(dbName, tblName).cols(cols), false).build());
        HCatTable tCreated = client.getTable(dbName, tblName);
        Table emptyTable = org.apache.hadoop.hive.ql.metadata.Table.getEmptyTable(dbName, tblName);
        Map<String, String> createdProps = tCreated.getTblProps();
        Map<String, String> emptyProps = emptyTable.getParameters();
        mapEqualsContainedIn(emptyProps, createdProps);
        // Test sd params - we check that all the parameters in an empty table
        // are retained as-is. We may add beyond it, but not change values for
        // any parameters that hive defines for an empty table.
        Map<String, String> createdSdParams = tCreated.getSerdeParams();
        Map<String, String> emptySdParams = emptyTable.getSd().getSerdeInfo().getParameters();
        mapEqualsContainedIn(emptySdParams, createdSdParams);
    }

    @Test
    public void testPartitionsHCatClientImpl() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String dbName = "ptnDB";
        String tableName = "pageView";
        client.dropDatabase(dbName, true, CASCADE);
        HCatCreateDBDesc dbDesc = HCatCreateDBDesc.create(dbName).ifNotExists(true).build();
        client.createDatabase(dbDesc);
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("userid", Type.INT, "id columns"));
        cols.add(new HCatFieldSchema("viewtime", Type.BIGINT, "view time columns"));
        cols.add(new HCatFieldSchema("pageurl", Type.STRING, ""));
        cols.add(new HCatFieldSchema("ip", Type.STRING, "IP Address of the User"));
        ArrayList<HCatFieldSchema> ptnCols = new ArrayList<HCatFieldSchema>();
        ptnCols.add(new HCatFieldSchema("dt", Type.STRING, "date column"));
        ptnCols.add(new HCatFieldSchema("country", Type.STRING, "country column"));
        HCatTable table = new HCatTable(dbName, tableName).cols(cols).partCols(ptnCols).fileFormat("sequenceFile");
        HCatCreateTableDesc tableDesc = HCatCreateTableDesc.create(table, false).build();
        client.createTable(tableDesc);
        // Verify that the table is created successfully.
        table = client.getTable(dbName, tableName);
        Map<String, String> firstPtn = new HashMap<String, String>();
        firstPtn.put("dt", "04/30/2012");
        firstPtn.put("country", "usa");
        // Test new HCatAddPartitionsDesc API.
        HCatAddPartitionDesc addPtn = HCatAddPartitionDesc.create(new HCatPartition(table, firstPtn, null)).build();
        client.addPartition(addPtn);
        Map<String, String> secondPtn = new HashMap<String, String>();
        secondPtn.put("dt", "04/12/2012");
        secondPtn.put("country", "brazil");
        // Test deprecated HCatAddPartitionsDesc API.
        HCatAddPartitionDesc addPtn2 = HCatAddPartitionDesc.create(dbName, tableName, null, secondPtn).build();
        client.addPartition(addPtn2);
        Map<String, String> thirdPtn = new HashMap<String, String>();
        thirdPtn.put("dt", "04/13/2012");
        thirdPtn.put("country", "argentina");
        // Test deprecated HCatAddPartitionsDesc API.
        HCatAddPartitionDesc addPtn3 = HCatAddPartitionDesc.create(dbName, tableName, null, thirdPtn).build();
        client.addPartition(addPtn3);
        List<HCatPartition> ptnList = client.listPartitionsByFilter(dbName, tableName, null);
        Assert.assertTrue(((ptnList.size()) == 3));
        HCatPartition ptn = client.getPartition(dbName, tableName, firstPtn);
        Assert.assertTrue((ptn != null));
        client.dropPartitions(dbName, tableName, firstPtn, true);
        ptnList = client.listPartitionsByFilter(dbName, tableName, null);
        Assert.assertTrue(((ptnList.size()) == 2));
        List<HCatPartition> ptnListTwo = client.listPartitionsByFilter(dbName, tableName, "country = \"argentina\"");
        Assert.assertTrue(((ptnListTwo.size()) == 1));
        client.markPartitionForEvent(dbName, tableName, thirdPtn, LOAD_DONE);
        boolean isMarked = client.isPartitionMarkedForEvent(dbName, tableName, thirdPtn, LOAD_DONE);
        Assert.assertTrue(isMarked);
        client.close();
    }

    @Test
    public void testDatabaseLocation() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String dbName = "locationDB";
        client.dropDatabase(dbName, true, CASCADE);
        HCatCreateDBDesc dbDesc = HCatCreateDBDesc.create(dbName).ifNotExists(true).location(("/tmp/" + dbName)).build();
        client.createDatabase(dbDesc);
        HCatDatabase newDB = client.getDatabase(dbName);
        Assert.assertTrue(newDB.getLocation().matches((".*/tmp/" + dbName)));
        client.close();
    }

    @Test
    public void testCreateTableLike() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String tableName = "tableone";
        String cloneTable = "tabletwo";
        client.dropTable(null, tableName, true);
        client.dropTable(null, cloneTable, true);
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("id", Type.INT, "id columns"));
        cols.add(new HCatFieldSchema("value", Type.STRING, "id columns"));
        HCatCreateTableDesc tableDesc = HCatCreateTableDesc.create(null, tableName, cols).fileFormat("rcfile").build();
        client.createTable(tableDesc);
        // create a new table similar to previous one.
        client.createTableLike(null, tableName, cloneTable, true, false, null);
        List<String> tables = client.listTableNamesByPattern(null, "table*");
        Assert.assertTrue(((tables.size()) == 2));
        client.close();
    }

    @Test
    public void testRenameTable() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String tableName = "temptable";
        String newName = "mytable";
        client.dropTable(null, tableName, true);
        client.dropTable(null, newName, true);
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("id", Type.INT, "id columns"));
        cols.add(new HCatFieldSchema("value", Type.STRING, "id columns"));
        HCatCreateTableDesc tableDesc = HCatCreateTableDesc.create(null, tableName, cols).fileFormat("rcfile").build();
        client.createTable(tableDesc);
        client.renameTable(null, tableName, newName);
        try {
            client.getTable(null, tableName);
        } catch (HCatException exp) {
            Assert.assertTrue(("Unexpected exception message: " + (exp.getMessage())), exp.getMessage().contains("NoSuchObjectException while fetching table"));
        }
        HCatTable newTable = client.getTable(null, newName);
        Assert.assertTrue((newTable != null));
        Assert.assertTrue(newTable.getTableName().equals(newName));
        client.close();
    }

    @Test
    public void testTransportFailure() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        boolean isExceptionCaught = false;
        // Table creation with a long table name causes ConnectionFailureException
        final String tableName = "Temptable" + (new BigInteger(260, new Random()).toString(2));
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("id", Type.INT, "id columns"));
        cols.add(new HCatFieldSchema("value", Type.STRING, "id columns"));
        try {
            HCatCreateTableDesc tableDesc = HCatCreateTableDesc.create(null, tableName, cols).fileFormat("rcfile").build();
            client.createTable(tableDesc);
        } catch (Exception exp) {
            isExceptionCaught = true;
            Assert.assertEquals("Unexpected exception type.", HCatException.class, exp.getClass());
            // The connection was closed, so create a new one.
            client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            String newName = "goodTable";
            client.dropTable(null, newName, true);
            HCatCreateTableDesc tableDesc2 = HCatCreateTableDesc.create(null, newName, cols).fileFormat("rcfile").build();
            client.createTable(tableDesc2);
            HCatTable newTable = client.getTable(null, newName);
            Assert.assertTrue((newTable != null));
            Assert.assertTrue(newTable.getTableName().equalsIgnoreCase(newName));
        } finally {
            client.close();
            Assert.assertTrue("The expected exception was never thrown.", isExceptionCaught);
        }
    }

    @Test
    public void testOtherFailure() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String tableName = "Temptable";
        boolean isExceptionCaught = false;
        client.dropTable(null, tableName, true);
        ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
        cols.add(new HCatFieldSchema("id", Type.INT, "id columns"));
        cols.add(new HCatFieldSchema("value", Type.STRING, "id columns"));
        try {
            HCatCreateTableDesc tableDesc = HCatCreateTableDesc.create(null, tableName, cols).fileFormat("rcfile").build();
            client.createTable(tableDesc);
            // The DB foo is non-existent.
            client.getTable("foo", tableName);
        } catch (Exception exp) {
            isExceptionCaught = true;
            Assert.assertTrue((exp instanceof HCatException));
            String newName = "goodTable";
            client.dropTable(null, newName, true);
            HCatCreateTableDesc tableDesc2 = HCatCreateTableDesc.create(null, newName, cols).fileFormat("rcfile").build();
            client.createTable(tableDesc2);
            HCatTable newTable = client.getTable(null, newName);
            Assert.assertTrue((newTable != null));
            Assert.assertTrue(newTable.getTableName().equalsIgnoreCase(newName));
        } finally {
            client.close();
            Assert.assertTrue("The expected exception was never thrown.", isExceptionCaught);
        }
    }

    @Test
    public void testDropTableException() throws Exception {
        HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
        String tableName = "tableToBeDropped";
        boolean isExceptionCaught = false;
        client.dropTable(null, tableName, true);
        try {
            client.dropTable(null, tableName, false);
        } catch (Exception exp) {
            isExceptionCaught = true;
            Assert.assertTrue((exp instanceof HCatException));
            TestHCatClient.LOG.info(("Drop Table Exception: " + (exp.getCause())));
        } finally {
            client.close();
            Assert.assertTrue("The expected exception was never thrown.", isExceptionCaught);
        }
    }

    @Test
    public void testUpdateTableSchema() throws Exception {
        try {
            HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            final String dbName = "testUpdateTableSchema_DBName";
            final String tableName = "testUpdateTableSchema_TableName";
            client.dropDatabase(dbName, true, CASCADE);
            client.createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> oldSchema = Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, ""));
            client.createTable(HCatCreateTableDesc.create(dbName, tableName, oldSchema).build());
            List<HCatFieldSchema> newSchema = Arrays.asList(new HCatFieldSchema("completely", Type.DOUBLE, ""), new HCatFieldSchema("new", Type.STRING, ""), new HCatFieldSchema("fields", Type.STRING, ""));
            client.updateTableSchema(dbName, tableName, newSchema);
            Assert.assertArrayEquals(newSchema.toArray(), client.getTable(dbName, tableName).getCols().toArray());
            client.dropDatabase(dbName, false, CASCADE);
        } catch (Exception exception) {
            TestHCatClient.LOG.error("Unexpected exception.", exception);
            Assert.assertTrue(("Unexpected exception: " + (exception.getMessage())), false);
        }
    }

    @Test
    public void testObjectNotFoundException() throws Exception {
        try {
            HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            String dbName = "testObjectNotFoundException_DBName";
            String tableName = "testObjectNotFoundException_TableName";
            client.dropDatabase(dbName, true, CASCADE);
            try {
                // Test that fetching a non-existent db-name yields ObjectNotFound.
                client.getDatabase(dbName);
                Assert.assertTrue("Expected ObjectNotFoundException.", false);
            } catch (Exception exception) {
                TestHCatClient.LOG.info("Got exception: ", exception);
                Assert.assertTrue(("Expected ObjectNotFoundException. Got:" + (exception.getClass())), (exception instanceof ObjectNotFoundException));
            }
            client.createDatabase(HCatCreateDBDesc.create(dbName).build());
            try {
                // Test that fetching a non-existent table-name yields ObjectNotFound.
                client.getTable(dbName, tableName);
                Assert.assertTrue("Expected ObjectNotFoundException.", false);
            } catch (Exception exception) {
                TestHCatClient.LOG.info("Got exception: ", exception);
                Assert.assertTrue(("Expected ObjectNotFoundException. Got:" + (exception.getClass())), (exception instanceof ObjectNotFoundException));
            }
            String partitionColumn = "part";
            List<HCatFieldSchema> columns = Arrays.asList(new HCatFieldSchema("col", Type.STRING, ""));
            ArrayList<HCatFieldSchema> partitionColumns = new ArrayList<HCatFieldSchema>(Arrays.asList(new HCatFieldSchema(partitionColumn, Type.STRING, "")));
            HCatTable table = new HCatTable(dbName, tableName).cols(columns).partCols(partitionColumns);
            client.createTable(HCatCreateTableDesc.create(table, false).build());
            HCatTable createdTable = client.getTable(dbName, tableName);
            Map<String, String> partitionSpec = new HashMap<String, String>();
            partitionSpec.put(partitionColumn, "foobar");
            try {
                // Test that fetching a non-existent partition yields ObjectNotFound.
                client.getPartition(dbName, tableName, partitionSpec);
                Assert.assertTrue("Expected ObjectNotFoundException.", false);
            } catch (Exception exception) {
                TestHCatClient.LOG.info("Got exception: ", exception);
                Assert.assertTrue(("Expected ObjectNotFoundException. Got:" + (exception.getClass())), (exception instanceof ObjectNotFoundException));
            }
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(createdTable, partitionSpec, TestHCatClient.makePartLocation(createdTable, partitionSpec))).build());
            // Test that listPartitionsByFilter() returns an empty-set, if the filter selects no partitions.
            Assert.assertEquals("Expected empty set of partitions.", 0, client.listPartitionsByFilter(dbName, tableName, (partitionColumn + " < 'foobar'")).size());
            try {
                // Test that listPartitionsByFilter() throws HCatException if the partition-key is incorrect.
                partitionSpec.put("NonExistentKey", "foobar");
                client.getPartition(dbName, tableName, partitionSpec);
                Assert.assertTrue("Expected HCatException.", false);
            } catch (Exception exception) {
                TestHCatClient.LOG.info("Got exception: ", exception);
                Assert.assertTrue(("Expected HCatException. Got:" + (exception.getClass())), (exception instanceof HCatException));
                Assert.assertFalse("Did not expect ObjectNotFoundException.", (exception instanceof ObjectNotFoundException));
            }
        } catch (Throwable t) {
            TestHCatClient.LOG.error("Unexpected exception!", t);
            Assert.assertTrue(("Unexpected exception! " + (t.getMessage())), false);
        }
    }

    @Test
    public void testGetMessageBusTopicName() throws Exception {
        try {
            HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            String dbName = "testGetMessageBusTopicName_DBName";
            String tableName = "testGetMessageBusTopicName_TableName";
            client.dropDatabase(dbName, true, CASCADE);
            client.createDatabase(HCatCreateDBDesc.create(dbName).build());
            String messageBusTopicName = "MY.topic.name";
            Map<String, String> tableProperties = new HashMap<String, String>(1);
            tableProperties.put(HCAT_MSGBUS_TOPIC_NAME, messageBusTopicName);
            client.createTable(HCatCreateTableDesc.create(dbName, tableName, Arrays.asList(new HCatFieldSchema("foo", Type.STRING, ""))).tblProps(tableProperties).build());
            Assert.assertEquals("MessageBus topic-name doesn't match!", messageBusTopicName, client.getMessageBusTopicName(dbName, tableName));
            client.dropDatabase(dbName, true, CASCADE);
            client.close();
        } catch (Exception exception) {
            TestHCatClient.LOG.error("Unexpected exception.", exception);
            Assert.assertTrue(("Unexpected exception:" + (exception.getMessage())), false);
        }
    }

    @Test
    public void testPartitionSchema() throws Exception {
        try {
            HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            final String dbName = "myDb";
            final String tableName = "myTable";
            client.dropDatabase(dbName, true, CASCADE);
            client.createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> columnSchema = Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, ""));
            List<HCatFieldSchema> partitionSchema = Arrays.asList(new HCatFieldSchema("dt", Type.STRING, ""), new HCatFieldSchema("grid", Type.STRING, ""));
            client.createTable(HCatCreateTableDesc.create(dbName, tableName, columnSchema).partCols(partitionSchema).build());
            HCatTable table = client.getTable(dbName, tableName);
            List<HCatFieldSchema> partitionColumns = table.getPartCols();
            Assert.assertArrayEquals("Didn't get expected partition-schema back from the HCatTable.", partitionSchema.toArray(), partitionColumns.toArray());
            client.dropDatabase(dbName, false, CASCADE);
        } catch (Exception unexpected) {
            TestHCatClient.LOG.error("Unexpected exception!", unexpected);
            Assert.assertTrue(("Unexpected exception! " + (unexpected.getMessage())), false);
        }
    }

    @Test
    public void testGetPartitionsWithPartialSpec() throws Exception {
        try {
            HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            final String dbName = "myDb";
            final String tableName = "myTable";
            client.dropDatabase(dbName, true, CASCADE);
            client.createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> columnSchema = Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, ""));
            List<HCatFieldSchema> partitionSchema = Arrays.asList(new HCatFieldSchema("dt", Type.STRING, ""), new HCatFieldSchema("grid", Type.STRING, ""));
            HCatTable table = new HCatTable(dbName, tableName).cols(columnSchema).partCols(partitionSchema);
            client.createTable(HCatCreateTableDesc.create(table, false).build());
            // Verify that the table was created successfully.
            table = client.getTable(dbName, tableName);
            Assert.assertNotNull("The created just now can't be null.", table);
            Map<String, String> partitionSpec = new HashMap<String, String>();
            partitionSpec.put("grid", "AB");
            partitionSpec.put("dt", "2011_12_31");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            partitionSpec.put("grid", "AB");
            partitionSpec.put("dt", "2012_01_01");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            partitionSpec.put("dt", "2012_01_01");
            partitionSpec.put("grid", "OB");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            partitionSpec.put("dt", "2012_01_01");
            partitionSpec.put("grid", "XB");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            Map<String, String> partialPartitionSpec = new HashMap<String, String>();
            partialPartitionSpec.put("dt", "2012_01_01");
            List<HCatPartition> partitions = client.getPartitions(dbName, tableName, partialPartitionSpec);
            Assert.assertEquals("Unexpected number of partitions.", 3, partitions.size());
            Assert.assertArrayEquals("Mismatched partition.", new String[]{ "2012_01_01", "AB" }, partitions.get(0).getValues().toArray());
            Assert.assertArrayEquals("Mismatched partition.", new String[]{ "2012_01_01", "OB" }, partitions.get(1).getValues().toArray());
            Assert.assertArrayEquals("Mismatched partition.", new String[]{ "2012_01_01", "XB" }, partitions.get(2).getValues().toArray());
            client.dropDatabase(dbName, false, CASCADE);
        } catch (Exception unexpected) {
            TestHCatClient.LOG.error("Unexpected exception!", unexpected);
            Assert.assertTrue(("Unexpected exception! " + (unexpected.getMessage())), false);
        }
    }

    @Test
    public void testDropPartitionsWithPartialSpec() throws Exception {
        try {
            HCatClient client = HCatClient.create(new Configuration(TestHCatClient.hcatConf));
            final String dbName = "myDb";
            final String tableName = "myTable";
            client.dropDatabase(dbName, true, CASCADE);
            client.createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> columnSchema = Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, ""));
            List<HCatFieldSchema> partitionSchema = Arrays.asList(new HCatFieldSchema("dt", Type.STRING, ""), new HCatFieldSchema("grid", Type.STRING, ""));
            HCatTable table = new HCatTable(dbName, tableName).cols(columnSchema).partCols(partitionSchema);
            client.createTable(HCatCreateTableDesc.create(table, false).build());
            // Verify that the table was created successfully.
            table = client.getTable(dbName, tableName);
            Assert.assertNotNull("Table couldn't be queried for. ", table);
            Map<String, String> partitionSpec = new HashMap<String, String>();
            partitionSpec.put("grid", "AB");
            partitionSpec.put("dt", "2011_12_31");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            partitionSpec.put("grid", "AB");
            partitionSpec.put("dt", "2012_01_01");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            partitionSpec.put("dt", "2012_01_01");
            partitionSpec.put("grid", "OB");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            partitionSpec.put("dt", "2012_01_01");
            partitionSpec.put("grid", "XB");
            client.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partitionSpec, TestHCatClient.makePartLocation(table, partitionSpec))).build());
            Map<String, String> partialPartitionSpec = new HashMap<String, String>();
            partialPartitionSpec.put("dt", "2012_01_01");
            client.dropPartitions(dbName, tableName, partialPartitionSpec, true);
            List<HCatPartition> partitions = client.getPartitions(dbName, tableName);
            Assert.assertEquals("Unexpected number of partitions.", 1, partitions.size());
            Assert.assertArrayEquals("Mismatched partition.", new String[]{ "2011_12_31", "AB" }, partitions.get(0).getValues().toArray());
            List<HCatFieldSchema> partColumns = partitions.get(0).getPartColumns();
            Assert.assertEquals(2, partColumns.size());
            Assert.assertEquals("dt", partColumns.get(0).getName());
            Assert.assertEquals("grid", partColumns.get(1).getName());
            client.dropDatabase(dbName, false, CASCADE);
        } catch (Exception unexpected) {
            TestHCatClient.LOG.error("Unexpected exception!", unexpected);
            Assert.assertTrue(("Unexpected exception! " + (unexpected.getMessage())), false);
        }
    }

    /**
     * Test for event-based replication scenario
     *
     * Does not test if replication actually happened, merely tests if we're able to consume a repl task
     * iter appropriately, calling all the functions expected of the interface, without errors.
     */
    @Test
    public void testReplicationTaskIter() throws Exception {
        Configuration cfg = new Configuration(TestHCatClient.hcatConf);
        cfg.set(varname, "10");// set really low batch size to ensure batching

        cfg.set(HiveConf.ConfVars.HIVE_REPL_TASK_FACTORY.varname, EximReplicationTaskFactory.class.getName());
        HCatClient sourceMetastore = HCatClient.create(cfg);
        String dbName = "testReplicationTaskIter";
        long baseId = sourceMetastore.getCurrentNotificationEventId();
        {
            // Perform some operations
            // 1: Create a db after dropping if needed => 1 or 2 events
            sourceMetastore.dropDatabase(dbName, true, CASCADE);
            sourceMetastore.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
            // 2: Create an unpartitioned table T1 => 1 event
            String tblName1 = "T1";
            List<HCatFieldSchema> cols1 = HCatSchemaUtils.getHCatSchema("a:int,b:string").getFields();
            HCatTable table1 = new HCatTable(dbName, tblName1).cols(cols1);
            sourceMetastore.createTable(HCatCreateTableDesc.create(table1).build());
            // 3: Create a partitioned table T2 => 1 event
            String tblName2 = "T2";
            List<HCatFieldSchema> cols2 = HCatSchemaUtils.getHCatSchema("a:int").getFields();
            List<HCatFieldSchema> pcols2 = HCatSchemaUtils.getHCatSchema("b:string").getFields();
            HCatTable table2 = new HCatTable(dbName, tblName2).cols(cols2).partCols(pcols2);
            sourceMetastore.createTable(HCatCreateTableDesc.create(table2).build());
            // 4: Add a partition P1 to T2 => 1 event
            HCatTable table2Created = sourceMetastore.getTable(dbName, tblName2);
            Map<String, String> ptnDesc1 = new HashMap<String, String>();
            ptnDesc1.put("b", "test1");
            HCatPartition ptn1 = new HCatPartition(table2Created, ptnDesc1, TestHCatClient.makePartLocation(table2Created, ptnDesc1));
            sourceMetastore.addPartition(HCatAddPartitionDesc.create(ptn1).build());
            // 5 : Create and drop partition P2 to T2 10 times => 20 events
            for (int i = 0; i < 20; i++) {
                Map<String, String> ptnDesc = new HashMap<String, String>();
                ptnDesc.put("b", ("testmul" + i));
                HCatPartition ptn = new HCatPartition(table2Created, ptnDesc, TestHCatClient.makePartLocation(table2Created, ptnDesc));
                sourceMetastore.addPartition(HCatAddPartitionDesc.create(ptn).build());
                sourceMetastore.dropPartitions(dbName, tblName2, ptnDesc, true);
            }
            // 6 : Drop table T1 => 1 event
            sourceMetastore.dropTable(dbName, tblName1, true);
            // 7 : Drop table T2 => 1 event
            sourceMetastore.dropTable(dbName, tblName2, true);
            // verify that the number of events since we began is at least 25 more
            long currId = sourceMetastore.getCurrentNotificationEventId();
            Assert.assertTrue((((("currId[" + currId) + "] must be more than 25 greater than baseId[") + baseId) + "]"), (currId > (baseId + 25)));
        }
        // do rest of tests on db we just picked up above.
        List<HCatNotificationEvent> notifs = sourceMetastore.getNextNotification(0, 0, new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent event) {
                return true;
            }
        });
        for (HCatNotificationEvent n : notifs) {
            TestHCatClient.LOG.info(((((((((("notif from dblistener:" + (n.getEventId())) + ":") + (n.getEventTime())) + ",t:") + (n.getEventType())) + ",o:") + (n.getDbName())) + ".") + (n.getTableName())));
        }
        Iterator<ReplicationTask> taskIter = sourceMetastore.getReplicationTasks(0, (-1), dbName, null);
        while (taskIter.hasNext()) {
            ReplicationTask task = taskIter.next();
            HCatNotificationEvent n = task.getEvent();
            TestHCatClient.LOG.info(((((((((((("notif from tasks:" + (n.getEventId())) + ":") + (n.getEventTime())) + ",t:") + (n.getEventType())) + ",o:") + (n.getDbName())) + ".") + (n.getTableName())) + ",s:") + (n.getEventScope())));
            TestHCatClient.LOG.info(("task :" + (task.getClass().getName())));
            if (task.needsStagingDirs()) {
                StagingDirectoryProvider provider = new StagingDirectoryProvider() {
                    @Override
                    public String getStagingDirectory(String key) {
                        TestHCatClient.LOG.info((("getStagingDirectory(" + key) + ") called!"));
                        return "/tmp/" + (key.replaceAll(" ", "_"));
                    }
                };
                task.withSrcStagingDirProvider(provider).withDstStagingDirProvider(provider);
            }
            if (task.isActionable()) {
                TestHCatClient.LOG.info("task was actionable!");
                Function<Command, String> commandDebugPrinter = new Function<Command, String>() {
                    @Override
                    public String apply(@Nullable
                    Command cmd) {
                        StringBuilder sb = new StringBuilder();
                        String serializedCmd = null;
                        try {
                            serializedCmd = ReplicationUtils.serializeCommand(cmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        sb.append((("SERIALIZED:" + serializedCmd) + "\n"));
                        Command command = null;
                        try {
                            command = ReplicationUtils.deserializeCommand(serializedCmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        sb.append((("CMD:[" + (command.getClass().getName())) + "]\n"));
                        sb.append((("EVENTID:[" + (command.getEventId())) + "]\n"));
                        for (String s : command.get()) {
                            sb.append(("CMD:" + s));
                            sb.append("\n");
                        }
                        sb.append((("Retriable:" + (command.isRetriable())) + "\n"));
                        sb.append((("Undoable:" + (command.isUndoable())) + "\n"));
                        if (command.isUndoable()) {
                            for (String s : command.getUndo()) {
                                sb.append(("UNDO:" + s));
                                sb.append("\n");
                            }
                        }
                        List<String> locns = command.cleanupLocationsPerRetry();
                        sb.append(("cleanupLocationsPerRetry entries :" + (locns.size())));
                        for (String s : locns) {
                            sb.append(("RETRY_CLEANUP:" + s));
                            sb.append("\n");
                        }
                        locns = command.cleanupLocationsAfterEvent();
                        sb.append(("cleanupLocationsAfterEvent entries :" + (locns.size())));
                        for (String s : locns) {
                            sb.append(("AFTER_EVENT_CLEANUP:" + s));
                            sb.append("\n");
                        }
                        return sb.toString();
                    }
                };
                TestHCatClient.LOG.info("On src:");
                for (String s : Iterables.transform(task.getSrcWhCommands(), commandDebugPrinter)) {
                    TestHCatClient.LOG.info(s);
                }
                TestHCatClient.LOG.info("On dest:");
                for (String s : Iterables.transform(task.getDstWhCommands(), commandDebugPrinter)) {
                    TestHCatClient.LOG.info(s);
                }
            } else {
                TestHCatClient.LOG.info("task was not actionable.");
            }
        } 
    }

    /**
     * Test for detecting schema-changes for an HCatalog table, across 2 different HCat instances.
     * A table is created with the same schema on 2 HCat instances. The table-schema is modified on the source HCat
     * instance (columns, I/O formats, SerDe definitions, etc.). The table metadata is compared between source
     * and target, the changes are detected and propagated to target.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTableSchemaPropagation() throws Exception {
        try {
            startReplicationTargetMetaStoreIfRequired();
            final String dbName = "myDb";
            final String tableName = "myTable";
            sourceMetaStore().dropDatabase(dbName, true, CASCADE);
            sourceMetaStore().createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> columnSchema = Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, ""));
            List<HCatFieldSchema> partitionSchema = Arrays.asList(new HCatFieldSchema("dt", Type.STRING, ""), new HCatFieldSchema("grid", Type.STRING, ""));
            HCatTable sourceTable = new HCatTable(dbName, tableName).cols(columnSchema).partCols(partitionSchema);
            sourceMetaStore().createTable(HCatCreateTableDesc.create(sourceTable).build());
            // Verify that the sourceTable was created successfully.
            sourceTable = sourceMetaStore().getTable(dbName, tableName);
            Assert.assertNotNull("Table couldn't be queried for. ", sourceTable);
            // Serialize Table definition. Deserialize using the target HCatClient instance.
            String tableStringRep = sourceMetaStore().serializeTable(sourceTable);
            targetMetaStore().dropDatabase(dbName, true, CASCADE);
            targetMetaStore().createDatabase(HCatCreateDBDesc.create(dbName).build());
            HCatTable targetTable = targetMetaStore().deserializeTable(tableStringRep);
            Assert.assertEquals("Table after deserialization should have been identical to sourceTable.", NO_DIFF, sourceTable.diff(targetTable));
            // Create table on Target.
            targetMetaStore().createTable(HCatCreateTableDesc.create(targetTable).build());
            // Verify that the created table is identical to sourceTable.
            targetTable = targetMetaStore().getTable(dbName, tableName);
            Assert.assertEquals("Table after deserialization should have been identical to sourceTable.", NO_DIFF, sourceTable.diff(targetTable));
            // Modify sourceTable.
            List<HCatFieldSchema> newColumnSchema = new ArrayList<HCatFieldSchema>(columnSchema);
            newColumnSchema.add(new HCatFieldSchema("goo_new", Type.DOUBLE, ""));
            Map<String, String> tableParams = new HashMap<String, String>(1);
            tableParams.put("orc.compress", "ZLIB");
            // Change SerDe, File I/O formats.
            // Add a column.
            sourceTable.cols(newColumnSchema).fileFormat("orcfile").tblProps(tableParams).serdeParam(FIELD_DELIM, Character.toString('\u0001'));
            sourceMetaStore().updateTableSchema(dbName, tableName, sourceTable);
            sourceTable = sourceMetaStore().getTable(dbName, tableName);
            // Diff against table on target.
            EnumSet<HCatTable.TableAttribute> diff = targetTable.diff(sourceTable);
            Assert.assertTrue("Couldn't find change in column-schema.", diff.contains(COLUMNS));
            Assert.assertTrue("Couldn't find change in InputFormat.", diff.contains(INPUT_FORMAT));
            Assert.assertTrue("Couldn't find change in OutputFormat.", diff.contains(OUTPUT_FORMAT));
            Assert.assertTrue("Couldn't find change in SerDe.", diff.contains(SERDE));
            Assert.assertTrue("Couldn't find change in SerDe parameters.", diff.contains(SERDE_PROPERTIES));
            Assert.assertTrue("Couldn't find change in Table parameters.", diff.contains(TABLE_PROPERTIES));
            // Replicate the changes to the replicated-table.
            targetMetaStore().updateTableSchema(dbName, tableName, targetTable.resolve(sourceTable, diff));
            targetTable = targetMetaStore().getTable(dbName, tableName);
            Assert.assertEquals("After propagating schema changes, source and target tables should have been equivalent.", NO_DIFF, targetTable.diff(sourceTable));
        } catch (Exception unexpected) {
            TestHCatClient.LOG.error("Unexpected exception!", unexpected);
            Assert.assertTrue(("Unexpected exception! " + (unexpected.getMessage())), false);
        }
    }

    /**
     * Test that partition-definitions can be replicated between HCat-instances,
     * independently of table-metadata replication.
     * 2 identical tables are created on 2 different HCat instances ("source" and "target").
     * On the source instance,
     * 1. One partition is added with the old format ("TEXTFILE").
     * 2. The table is updated with an additional column and the data-format changed to ORC.
     * 3. Another partition is added with the new format.
     * 4. The partitions' metadata is copied to the target HCat instance, without updating the target table definition.
     * 5. The partitions' metadata is tested to be an exact replica of that on the source.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartitionRegistrationWithCustomSchema() throws Exception {
        try {
            startReplicationTargetMetaStoreIfRequired();
            final String dbName = "myDb";
            final String tableName = "myTable";
            sourceMetaStore().dropDatabase(dbName, true, CASCADE);
            sourceMetaStore().createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> columnSchema = new ArrayList<HCatFieldSchema>(Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, "")));
            List<HCatFieldSchema> partitionSchema = Arrays.asList(new HCatFieldSchema("dt", Type.STRING, ""), new HCatFieldSchema("grid", Type.STRING, ""));
            HCatTable sourceTable = new HCatTable(dbName, tableName).cols(columnSchema).partCols(partitionSchema).comment("Source table.");
            sourceMetaStore().createTable(HCatCreateTableDesc.create(sourceTable).build());
            // Verify that the sourceTable was created successfully.
            sourceTable = sourceMetaStore().getTable(dbName, tableName);
            Assert.assertNotNull("Table couldn't be queried for. ", sourceTable);
            // Partitions added now should inherit table-schema, properties, etc.
            Map<String, String> partitionSpec_1 = new HashMap<String, String>();
            partitionSpec_1.put("grid", "AB");
            partitionSpec_1.put("dt", "2011_12_31");
            HCatPartition sourcePartition_1 = new HCatPartition(sourceTable, partitionSpec_1, TestHCatClient.makePartLocation(sourceTable, partitionSpec_1));
            sourceMetaStore().addPartition(HCatAddPartitionDesc.create(sourcePartition_1).build());
            Assert.assertEquals("Unexpected number of partitions. ", 1, sourceMetaStore().getPartitions(dbName, tableName).size());
            // Verify that partition_1 was added correctly, and properties were inherited from the HCatTable.
            HCatPartition addedPartition_1 = sourceMetaStore().getPartition(dbName, tableName, partitionSpec_1);
            Assert.assertEquals("Column schema doesn't match.", sourceTable.getCols(), addedPartition_1.getColumns());
            Assert.assertEquals("InputFormat doesn't match.", sourceTable.getInputFileFormat(), addedPartition_1.getInputFormat());
            Assert.assertEquals("OutputFormat doesn't match.", sourceTable.getOutputFileFormat(), addedPartition_1.getOutputFormat());
            Assert.assertEquals("SerDe doesn't match.", sourceTable.getSerdeLib(), addedPartition_1.getSerDe());
            Assert.assertEquals("SerDe params don't match.", sourceTable.getSerdeParams(), addedPartition_1.getSerdeParams());
            // Replicate table definition.
            targetMetaStore().dropDatabase(dbName, true, CASCADE);
            targetMetaStore().createDatabase(HCatCreateDBDesc.create(dbName).build());
            // Make a copy of the source-table, as would be done across class-loaders.
            HCatTable targetTable = targetMetaStore().deserializeTable(sourceMetaStore().serializeTable(sourceTable));
            targetMetaStore().createTable(HCatCreateTableDesc.create(targetTable).build());
            targetTable = targetMetaStore().getTable(dbName, tableName);
            Assert.assertEquals("Created table doesn't match the source.", NO_DIFF, targetTable.diff(sourceTable));
            // Modify Table schema at the source.
            List<HCatFieldSchema> newColumnSchema = new ArrayList<HCatFieldSchema>(columnSchema);
            newColumnSchema.add(new HCatFieldSchema("goo_new", Type.DOUBLE, ""));
            Map<String, String> tableParams = new HashMap<String, String>(1);
            tableParams.put("orc.compress", "ZLIB");
            // Change SerDe, File I/O formats.
            // Add a column.
            sourceTable.cols(newColumnSchema).fileFormat("orcfile").tblProps(tableParams).serdeParam(FIELD_DELIM, Character.toString('\u0001'));
            sourceMetaStore().updateTableSchema(dbName, tableName, sourceTable);
            sourceTable = sourceMetaStore().getTable(dbName, tableName);
            // Add another partition to the source.
            Map<String, String> partitionSpec_2 = new HashMap<String, String>();
            partitionSpec_2.put("grid", "AB");
            partitionSpec_2.put("dt", "2012_01_01");
            HCatPartition sourcePartition_2 = new HCatPartition(sourceTable, partitionSpec_2, TestHCatClient.makePartLocation(sourceTable, partitionSpec_2));
            sourceMetaStore().addPartition(HCatAddPartitionDesc.create(sourcePartition_2).build());
            // The source table now has 2 partitions, one in TEXTFILE, the other in ORC.
            // Test adding these partitions to the target-table *without* replicating the table-change.
            List<HCatPartition> sourcePartitions = sourceMetaStore().getPartitions(dbName, tableName);
            Assert.assertEquals("Unexpected number of source partitions.", 2, sourcePartitions.size());
            List<HCatAddPartitionDesc> addPartitionDescs = new ArrayList<HCatAddPartitionDesc>(sourcePartitions.size());
            for (HCatPartition partition : sourcePartitions) {
                addPartitionDescs.add(HCatAddPartitionDesc.create(partition).build());
            }
            targetMetaStore().addPartitions(addPartitionDescs);
            List<HCatPartition> targetPartitions = targetMetaStore().getPartitions(dbName, tableName);
            Assert.assertEquals("Expected the same number of partitions. ", sourcePartitions.size(), targetPartitions.size());
            for (int i = 0; i < (targetPartitions.size()); ++i) {
                HCatPartition sourcePartition = sourcePartitions.get(i);
                HCatPartition targetPartition = targetPartitions.get(i);
                Assert.assertEquals("Column schema doesn't match.", sourcePartition.getColumns(), targetPartition.getColumns());
                Assert.assertEquals("InputFormat doesn't match.", sourcePartition.getInputFormat(), targetPartition.getInputFormat());
                Assert.assertEquals("OutputFormat doesn't match.", sourcePartition.getOutputFormat(), targetPartition.getOutputFormat());
                Assert.assertEquals("SerDe doesn't match.", sourcePartition.getSerDe(), targetPartition.getSerDe());
                Assert.assertEquals("SerDe params don't match.", sourcePartition.getSerdeParams(), targetPartition.getSerdeParams());
            }
        } catch (Exception unexpected) {
            TestHCatClient.LOG.error("Unexpected exception! ", unexpected);
            Assert.assertTrue(("Unexpected exception! " + (unexpected.getMessage())), false);
        }
    }

    /**
     * Test that partition-definitions can be replicated between HCat-instances,
     * independently of table-metadata replication, using PartitionSpec interfaces.
     * (This is essentially the same test as testPartitionRegistrationWithCustomSchema(),
     * transliterated to use the PartitionSpec APIs.)
     * 2 identical tables are created on 2 different HCat instances ("source" and "target").
     * On the source instance,
     * 1. One partition is added with the old format ("TEXTFILE").
     * 2. The table is updated with an additional column and the data-format changed to ORC.
     * 3. Another partition is added with the new format.
     * 4. The partitions' metadata is copied to the target HCat instance, without updating the target table definition.
     * 5. The partitions' metadata is tested to be an exact replica of that on the source.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartitionSpecRegistrationWithCustomSchema() throws Exception {
        try {
            startReplicationTargetMetaStoreIfRequired();
            final String dbName = "myDb";
            final String tableName = "myTable";
            sourceMetaStore().dropDatabase(dbName, true, CASCADE);
            sourceMetaStore().createDatabase(HCatCreateDBDesc.create(dbName).build());
            List<HCatFieldSchema> columnSchema = new ArrayList<HCatFieldSchema>(Arrays.asList(new HCatFieldSchema("foo", Type.INT, ""), new HCatFieldSchema("bar", Type.STRING, "")));
            List<HCatFieldSchema> partitionSchema = Arrays.asList(new HCatFieldSchema("dt", Type.STRING, ""), new HCatFieldSchema("grid", Type.STRING, ""));
            HCatTable sourceTable = new HCatTable(dbName, tableName).cols(columnSchema).partCols(partitionSchema).comment("Source table.");
            sourceMetaStore().createTable(HCatCreateTableDesc.create(sourceTable).build());
            // Verify that the sourceTable was created successfully.
            sourceTable = sourceMetaStore().getTable(dbName, tableName);
            Assert.assertNotNull("Table couldn't be queried for. ", sourceTable);
            // Partitions added now should inherit table-schema, properties, etc.
            Map<String, String> partitionSpec_1 = new HashMap<String, String>();
            partitionSpec_1.put("grid", "AB");
            partitionSpec_1.put("dt", "2011_12_31");
            HCatPartition sourcePartition_1 = new HCatPartition(sourceTable, partitionSpec_1, TestHCatClient.makePartLocation(sourceTable, partitionSpec_1));
            sourceMetaStore().addPartition(HCatAddPartitionDesc.create(sourcePartition_1).build());
            Assert.assertEquals("Unexpected number of partitions. ", 1, sourceMetaStore().getPartitions(dbName, tableName).size());
            // Verify that partition_1 was added correctly, and properties were inherited from the HCatTable.
            HCatPartition addedPartition_1 = sourceMetaStore().getPartition(dbName, tableName, partitionSpec_1);
            Assert.assertEquals("Column schema doesn't match.", sourceTable.getCols(), addedPartition_1.getColumns());
            Assert.assertEquals("InputFormat doesn't match.", sourceTable.getInputFileFormat(), addedPartition_1.getInputFormat());
            Assert.assertEquals("OutputFormat doesn't match.", sourceTable.getOutputFileFormat(), addedPartition_1.getOutputFormat());
            Assert.assertEquals("SerDe doesn't match.", sourceTable.getSerdeLib(), addedPartition_1.getSerDe());
            Assert.assertEquals("SerDe params don't match.", sourceTable.getSerdeParams(), addedPartition_1.getSerdeParams());
            // Replicate table definition.
            targetMetaStore().dropDatabase(dbName, true, CASCADE);
            targetMetaStore().createDatabase(HCatCreateDBDesc.create(dbName).build());
            // Make a copy of the source-table, as would be done across class-loaders.
            HCatTable targetTable = targetMetaStore().deserializeTable(sourceMetaStore().serializeTable(sourceTable));
            targetMetaStore().createTable(HCatCreateTableDesc.create(targetTable).build());
            targetTable = targetMetaStore().getTable(dbName, tableName);
            Assert.assertEquals("Created table doesn't match the source.", NO_DIFF, targetTable.diff(sourceTable));
            // Modify Table schema at the source.
            List<HCatFieldSchema> newColumnSchema = new ArrayList<HCatFieldSchema>(columnSchema);
            newColumnSchema.add(new HCatFieldSchema("goo_new", Type.DOUBLE, ""));
            Map<String, String> tableParams = new HashMap<String, String>(1);
            tableParams.put("orc.compress", "ZLIB");
            // Change SerDe, File I/O formats.
            // Add a column.
            sourceTable.cols(newColumnSchema).fileFormat("orcfile").tblProps(tableParams).serdeParam(FIELD_DELIM, Character.toString('\u0001'));
            sourceMetaStore().updateTableSchema(dbName, tableName, sourceTable);
            sourceTable = sourceMetaStore().getTable(dbName, tableName);
            // Add another partition to the source.
            Map<String, String> partitionSpec_2 = new HashMap<String, String>();
            partitionSpec_2.put("grid", "AB");
            partitionSpec_2.put("dt", "2012_01_01");
            HCatPartition sourcePartition_2 = new HCatPartition(sourceTable, partitionSpec_2, TestHCatClient.makePartLocation(sourceTable, partitionSpec_2));
            sourceMetaStore().addPartition(HCatAddPartitionDesc.create(sourcePartition_2).build());
            // The source table now has 2 partitions, one in TEXTFILE, the other in ORC.
            // Test adding these partitions to the target-table *without* replicating the table-change.
            HCatPartitionSpec sourcePartitionSpec = sourceMetaStore().getPartitionSpecs(dbName, tableName, (-1));
            Assert.assertEquals("Unexpected number of source partitions.", 2, sourcePartitionSpec.size());
            // Serialize the hcatPartitionSpec.
            List<String> partitionSpecString = sourceMetaStore().serializePartitionSpec(sourcePartitionSpec);
            // Deserialize the HCatPartitionSpec using the target HCatClient instance.
            HCatPartitionSpec targetPartitionSpec = targetMetaStore().deserializePartitionSpec(partitionSpecString);
            Assert.assertEquals("Could not add the expected number of partitions.", sourcePartitionSpec.size(), targetMetaStore().addPartitionSpec(targetPartitionSpec));
            // Retrieve partitions.
            targetPartitionSpec = targetMetaStore().getPartitionSpecs(dbName, tableName, (-1));
            Assert.assertEquals("Could not retrieve the expected number of partitions.", sourcePartitionSpec.size(), targetPartitionSpec.size());
            // Assert that the source and target partitions are equivalent.
            HCatPartitionSpec.HCatPartitionIterator sourceIterator = sourcePartitionSpec.getPartitionIterator();
            HCatPartitionSpec.HCatPartitionIterator targetIterator = targetPartitionSpec.getPartitionIterator();
            while (targetIterator.hasNext()) {
                Assert.assertTrue("Fewer target partitions than source.", sourceIterator.hasNext());
                HCatPartition sourcePartition = sourceIterator.next();
                HCatPartition targetPartition = targetIterator.next();
                Assert.assertEquals("Column schema doesn't match.", sourcePartition.getColumns(), targetPartition.getColumns());
                Assert.assertEquals("InputFormat doesn't match.", sourcePartition.getInputFormat(), targetPartition.getInputFormat());
                Assert.assertEquals("OutputFormat doesn't match.", sourcePartition.getOutputFormat(), targetPartition.getOutputFormat());
                Assert.assertEquals("SerDe doesn't match.", sourcePartition.getSerDe(), targetPartition.getSerDe());
                Assert.assertEquals("SerDe params don't match.", sourcePartition.getSerdeParams(), targetPartition.getSerdeParams());
            } 
        } catch (Exception unexpected) {
            TestHCatClient.LOG.error("Unexpected exception! ", unexpected);
            Assert.assertTrue(("Unexpected exception! " + (unexpected.getMessage())), false);
        }
    }
}

