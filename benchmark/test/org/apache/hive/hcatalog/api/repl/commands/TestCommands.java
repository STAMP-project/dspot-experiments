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
package org.apache.hive.hcatalog.api.repl.commands;


import HCatClient.DropDBMode.CASCADE;
import ReplicationUtils.REPL_STATE_ID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hive.hcatalog.HcatTestUtils;
import org.apache.hive.hcatalog.api.HCatAddPartitionDesc;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.hive.hcatalog.api.HCatCreateDBDesc;
import org.apache.hive.hcatalog.api.HCatCreateTableDesc;
import org.apache.hive.hcatalog.api.HCatDatabase;
import org.apache.hive.hcatalog.api.HCatPartition;
import org.apache.hive.hcatalog.api.HCatTable;
import org.apache.hive.hcatalog.api.ObjectNotFoundException;
import org.apache.hive.hcatalog.api.repl.Command;
import org.apache.hive.hcatalog.api.repl.CommandTestUtils;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCommands {
    private static Logger LOG = LoggerFactory.getLogger(CommandTestUtils.class.getName());

    private static HiveConf hconf;

    private static IDriver driver;

    private static HCatClient client;

    private static String TEST_PATH;

    @Test
    public void testDropDatabaseCommand() throws HCatException {
        String dbName = "cmd_testdb";
        int evid = 999;
        Command testCmd = new DropDatabaseCommand(dbName, evid);
        Assert.assertEquals(evid, testCmd.getEventId());
        Assert.assertEquals(1, testCmd.get().size());
        Assert.assertEquals(true, testCmd.isRetriable());
        Assert.assertEquals(false, testCmd.isUndoable());
        CommandTestUtils.testCommandSerialization(testCmd);
        TestCommands.client.dropDatabase(dbName, true, CASCADE);
        TestCommands.client.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
        HCatDatabase db = TestCommands.client.getDatabase(dbName);
        Assert.assertNotNull(db);
        TestCommands.LOG.info(("About to run :" + (testCmd.get().get(0))));
        TestCommands.driver.run(testCmd.get().get(0));
        Exception onfe = null;
        try {
            HCatDatabase db_del = TestCommands.client.getDatabase(dbName);
        } catch (Exception e) {
            onfe = e;
        }
        Assert.assertNotNull(onfe);
        Assert.assertTrue((onfe instanceof ObjectNotFoundException));
    }

    @Test
    public void testDropTableCommand() throws HCatException {
        String dbName = "cmd_testdb";
        String tableName = "cmd_testtable";
        int evid = 789;
        List<HCatFieldSchema> cols = HCatSchemaUtils.getHCatSchema("a:int,b:string").getFields();
        Command testReplicatedDropCmd = new DropTableCommand(dbName, tableName, true, evid);
        Assert.assertEquals(evid, testReplicatedDropCmd.getEventId());
        Assert.assertEquals(1, testReplicatedDropCmd.get().size());
        Assert.assertEquals(true, testReplicatedDropCmd.isRetriable());
        Assert.assertEquals(false, testReplicatedDropCmd.isUndoable());
        CommandTestUtils.testCommandSerialization(testReplicatedDropCmd);
        Command testNormalDropCmd = new DropTableCommand(dbName, tableName, false, evid);
        Assert.assertEquals(evid, testNormalDropCmd.getEventId());
        Assert.assertEquals(1, testNormalDropCmd.get().size());
        Assert.assertEquals(true, testNormalDropCmd.isRetriable());
        Assert.assertEquals(false, testNormalDropCmd.isUndoable());
        CommandTestUtils.testCommandSerialization(testNormalDropCmd);
        TestCommands.client.dropDatabase(dbName, true, CASCADE);
        TestCommands.client.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
        Map<String, String> tprops = new HashMap<String, String>();
        tprops.put(REPL_STATE_ID, String.valueOf((evid + 5)));
        HCatTable tableToCreate = new HCatTable(dbName, tableName).tblProps(tprops).cols(cols);
        TestCommands.client.createTable(HCatCreateTableDesc.create(tableToCreate).build());
        HCatTable t1 = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(t1);
        // Test replicated drop, should not drop, because evid < repl.state.id
        TestCommands.LOG.info(("About to run :" + (testReplicatedDropCmd.get().get(0))));
        TestCommands.driver.run(testReplicatedDropCmd.get().get(0));
        HCatTable t2 = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(t2);
        // Test normal drop, should drop unconditionally.
        TestCommands.LOG.info(("About to run :" + (testNormalDropCmd.get().get(0))));
        TestCommands.driver.run(testNormalDropCmd.get().get(0));
        Exception onfe = null;
        try {
            HCatTable t_del = TestCommands.client.getTable(dbName, tableName);
        } catch (Exception e) {
            onfe = e;
        }
        Assert.assertNotNull(onfe);
        Assert.assertTrue((onfe instanceof ObjectNotFoundException));
        Map<String, String> tprops2 = new HashMap<String, String>();
        tprops2.put(REPL_STATE_ID, String.valueOf((evid - 5)));
        HCatTable tableToCreate2 = new HCatTable(dbName, tableName).tblProps(tprops2).cols(cols);
        TestCommands.client.createTable(HCatCreateTableDesc.create(tableToCreate2).build());
        HCatTable t3 = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(t3);
        // Test replicated drop, should drop this time, since repl.state.id < evid.
        TestCommands.LOG.info(("About to run :" + (testReplicatedDropCmd.get().get(0))));
        TestCommands.driver.run(testReplicatedDropCmd.get().get(0));
        Exception onfe2 = null;
        try {
            HCatTable t_del = TestCommands.client.getTable(dbName, tableName);
        } catch (Exception e) {
            onfe2 = e;
        }
        Assert.assertNotNull(onfe2);
        Assert.assertTrue((onfe2 instanceof ObjectNotFoundException));
    }

    @Test
    public void testDropPartitionCommand() throws MetaException, HCatException {
        String dbName = "cmd_testdb";
        String tableName = "cmd_testtable";
        int evid = 789;
        List<HCatFieldSchema> pcols = HCatSchemaUtils.getHCatSchema("b:string").getFields();
        List<HCatFieldSchema> cols = HCatSchemaUtils.getHCatSchema("a:int").getFields();
        Map<String, String> ptnDesc = new HashMap<String, String>();
        ptnDesc.put("b", "test");
        Command testReplicatedDropPtnCmd = new DropPartitionCommand(dbName, tableName, ptnDesc, true, evid);
        Assert.assertEquals(evid, testReplicatedDropPtnCmd.getEventId());
        Assert.assertEquals(1, testReplicatedDropPtnCmd.get().size());
        Assert.assertEquals(true, testReplicatedDropPtnCmd.isRetriable());
        Assert.assertEquals(false, testReplicatedDropPtnCmd.isUndoable());
        CommandTestUtils.testCommandSerialization(testReplicatedDropPtnCmd);
        Command testNormalDropPtnCmd = new DropPartitionCommand(dbName, tableName, ptnDesc, false, evid);
        Assert.assertEquals(evid, testNormalDropPtnCmd.getEventId());
        Assert.assertEquals(1, testNormalDropPtnCmd.get().size());
        Assert.assertEquals(true, testNormalDropPtnCmd.isRetriable());
        Assert.assertEquals(false, testNormalDropPtnCmd.isUndoable());
        CommandTestUtils.testCommandSerialization(testNormalDropPtnCmd);
        TestCommands.client.dropDatabase(dbName, true, CASCADE);
        TestCommands.client.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
        Map<String, String> props = new HashMap<String, String>();
        props.put(REPL_STATE_ID, String.valueOf((evid + 5)));
        HCatTable table = new HCatTable(dbName, tableName).tblProps(props).cols(cols).partCols(pcols);
        TestCommands.client.createTable(HCatCreateTableDesc.create(table).build());
        HCatTable tableCreated = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(tableCreated);
        HCatPartition ptnToAdd = parameters(props);
        TestCommands.client.addPartition(HCatAddPartitionDesc.create(ptnToAdd).build());
        HCatPartition p1 = TestCommands.client.getPartition(dbName, tableName, ptnDesc);
        Assert.assertNotNull(p1);
        // Test replicated drop, should not drop, because evid < repl.state.id
        TestCommands.LOG.info(("About to run :" + (testReplicatedDropPtnCmd.get().get(0))));
        TestCommands.driver.run(testReplicatedDropPtnCmd.get().get(0));
        HCatPartition p2 = TestCommands.client.getPartition(dbName, tableName, ptnDesc);
        Assert.assertNotNull(p2);
        // Test normal drop, should drop unconditionally.
        TestCommands.LOG.info(("About to run :" + (testNormalDropPtnCmd.get().get(0))));
        TestCommands.driver.run(testNormalDropPtnCmd.get().get(0));
        Exception onfe = null;
        try {
            HCatPartition p_del = TestCommands.client.getPartition(dbName, tableName, ptnDesc);
        } catch (Exception e) {
            onfe = e;
        }
        Assert.assertNotNull(onfe);
        Assert.assertTrue((onfe instanceof ObjectNotFoundException));
        Map<String, String> props2 = new HashMap<String, String>();
        props2.put(REPL_STATE_ID, String.valueOf((evid - 5)));
        HCatPartition ptnToAdd2 = parameters(props2);
        TestCommands.client.addPartition(HCatAddPartitionDesc.create(ptnToAdd2).build());
        HCatPartition p3 = TestCommands.client.getPartition(dbName, tableName, ptnDesc);
        Assert.assertNotNull(p3);
        // Test replicated drop, should drop this time, since repl.state.id < evid.
        TestCommands.LOG.info(("About to run :" + (testReplicatedDropPtnCmd.get().get(0))));
        TestCommands.driver.run(testReplicatedDropPtnCmd.get().get(0));
        Exception onfe2 = null;
        try {
            HCatPartition p_del = TestCommands.client.getPartition(dbName, tableName, ptnDesc);
        } catch (Exception e) {
            onfe2 = e;
        }
        Assert.assertNotNull(onfe2);
        Assert.assertTrue((onfe2 instanceof ObjectNotFoundException));
    }

    @Test
    public void testDropTableCommand2() throws MetaException, HCatException {
        // Secondary DropTableCommand test for testing repl-drop-tables' effect on partitions inside a partitioned table
        // when there exist partitions inside the table which are older than the drop event.
        // Our goal is this : Create a table t, with repl.last.id=157, say.
        // Create 2 partitions inside it, with repl.last.id=150 and 160, say.
        // Now, process a drop table command with eventid=155.
        // It should result in the table and the partition with repl.last.id=160 continuing to exist,
        // but dropping the partition with repl.last.id=150.
        String dbName = "cmd_testdb";
        String tableName = "cmd_testtable";
        int evid = 157;
        List<HCatFieldSchema> pcols = HCatSchemaUtils.getHCatSchema("b:string").getFields();
        List<HCatFieldSchema> cols = HCatSchemaUtils.getHCatSchema("a:int").getFields();
        Command testReplicatedDropCmd = new DropTableCommand(dbName, tableName, true, evid);
        TestCommands.client.dropDatabase(dbName, true, CASCADE);
        TestCommands.client.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
        Map<String, String> tprops = new HashMap<String, String>();
        tprops.put(REPL_STATE_ID, String.valueOf((evid + 2)));
        HCatTable table = new HCatTable(dbName, tableName).tblProps(tprops).cols(cols).partCols(pcols);
        TestCommands.client.createTable(HCatCreateTableDesc.create(table).build());
        HCatTable tableCreated = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(tableCreated);
        Map<String, String> ptnDesc1 = new HashMap<String, String>();
        ptnDesc1.put("b", "test-older");
        Map<String, String> props1 = new HashMap<String, String>();
        props1.put(REPL_STATE_ID, String.valueOf((evid - 5)));
        HCatPartition ptnToAdd1 = parameters(props1);
        TestCommands.client.addPartition(HCatAddPartitionDesc.create(ptnToAdd1).build());
        Map<String, String> ptnDesc2 = new HashMap<String, String>();
        ptnDesc2.put("b", "test-newer");
        Map<String, String> props2 = new HashMap<String, String>();
        props2.put(REPL_STATE_ID, String.valueOf((evid + 5)));
        HCatPartition ptnToAdd2 = parameters(props2);
        TestCommands.client.addPartition(HCatAddPartitionDesc.create(ptnToAdd2).build());
        HCatPartition p1 = TestCommands.client.getPartition(dbName, tableName, ptnDesc1);
        Assert.assertNotNull(p1);
        HCatPartition p2 = TestCommands.client.getPartition(dbName, tableName, ptnDesc2);
        Assert.assertNotNull(p2);
        TestCommands.LOG.info(("About to run :" + (testReplicatedDropCmd.get().get(0))));
        TestCommands.driver.run(testReplicatedDropCmd.get().get(0));
        HCatTable t_stillExists = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(t_stillExists);
        HCatPartition p2_stillExists = TestCommands.client.getPartition(dbName, tableName, ptnDesc2);
        Exception onfe = null;
        try {
            HCatPartition p1_del = TestCommands.client.getPartition(dbName, tableName, ptnDesc1);
        } catch (Exception e) {
            onfe = e;
        }
        Assert.assertNotNull(onfe);
        Assert.assertTrue((onfe instanceof ObjectNotFoundException));
    }

    @Test
    public void testBasicReplEximCommands() throws IOException {
        // repl export, has repl.last.id and repl.scope=all in it
        // import repl dump, table has repl.last.id on it (will likely be 0)
        int evid = 111;
        String exportLocation = ((TestCommands.TEST_PATH) + (File.separator)) + "testBasicReplExim";
        Path tempPath = new Path(TestCommands.TEST_PATH, "testBasicReplEximTmp");
        String tempLocation = tempPath.toUri().getPath();
        String dbName = "exim";
        String tableName = "basicSrc";
        String importedTableName = "basicDst";
        List<HCatFieldSchema> cols = HCatSchemaUtils.getHCatSchema("b:string").getFields();
        TestCommands.client.dropDatabase(dbName, true, CASCADE);
        TestCommands.client.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
        HCatTable table = new HCatTable(dbName, tableName).cols(cols).fileFormat("textfile");
        TestCommands.client.createTable(HCatCreateTableDesc.create(table).build());
        HCatTable t = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(t);
        String[] data = new String[]{ "eleven", "twelve" };
        HcatTestUtils.createTestDataFile(tempLocation, data);
        CommandProcessorResponse ret = TestCommands.driver.run(((((("LOAD DATA LOCAL INPATH '" + tempLocation) + "' OVERWRITE INTO TABLE ") + dbName) + ".") + tableName));
        Assert.assertEquals((((ret.getResponseCode()) + ":") + (ret.getErrorMessage())), null, ret.getException());
        CommandProcessorResponse selectRet = TestCommands.driver.run(((("SELECT * from " + dbName) + ".") + tableName));
        Assert.assertEquals((((selectRet.getResponseCode()) + ":") + (selectRet.getErrorMessage())), null, selectRet.getException());
        List<String> values = new ArrayList<String>();
        TestCommands.driver.getResults(values);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(data[0], values.get(0));
        Assert.assertEquals(data[1], values.get(1));
        ExportCommand exportCmd = new ExportCommand(dbName, tableName, null, exportLocation, false, evid);
        TestCommands.LOG.info(("About to run :" + (exportCmd.get().get(0))));
        CommandProcessorResponse ret2 = TestCommands.driver.run(exportCmd.get().get(0));
        Assert.assertEquals((((ret2.getResponseCode()) + ":") + (ret2.getErrorMessage())), null, ret2.getException());
        List<String> exportPaths = exportCmd.cleanupLocationsAfterEvent();
        Assert.assertEquals(1, exportPaths.size());
        String metadata = TestCommands.getMetadataContents(exportPaths.get(0));
        TestCommands.LOG.info("Export returned the following _metadata contents:");
        TestCommands.LOG.info(metadata);
        Assert.assertTrue((metadata + "did not match \"repl.scope\"=\"all\""), metadata.matches(".*\"repl.scope\":\"all\".*"));
        Assert.assertTrue((metadata + "has \"repl.last.id\""), metadata.matches(".*\"repl.last.id\":.*"));
        ImportCommand importCmd = new ImportCommand(dbName, importedTableName, null, exportLocation, false, evid);
        TestCommands.LOG.info(("About to run :" + (importCmd.get().get(0))));
        CommandProcessorResponse ret3 = TestCommands.driver.run(importCmd.get().get(0));
        Assert.assertEquals((((ret3.getResponseCode()) + ":") + (ret3.getErrorMessage())), null, ret3.getException());
        CommandProcessorResponse selectRet2 = TestCommands.driver.run(((("SELECT * from " + dbName) + ".") + importedTableName));
        Assert.assertEquals((((selectRet2.getResponseCode()) + ":") + (selectRet2.getErrorMessage())), null, selectRet2.getException());
        List<String> values2 = new ArrayList<String>();
        TestCommands.driver.getResults(values2);
        Assert.assertEquals(2, values2.size());
        Assert.assertEquals(data[0], values2.get(0));
        Assert.assertEquals(data[1], values2.get(1));
        HCatTable importedTable = TestCommands.client.getTable(dbName, importedTableName);
        Assert.assertNotNull(importedTable);
        Assert.assertTrue(importedTable.getTblProps().containsKey("repl.last.id"));
    }

    @Test
    public void testMetadataReplEximCommands() throws IOException {
        // repl metadata export, has repl.last.id and repl.scope=metadata
        // import repl metadata dump, table metadata changed, allows override, has repl.last.id
        int evid = 222;
        String exportLocation = ((TestCommands.TEST_PATH) + (File.separator)) + "testMetadataReplExim";
        Path tempPath = new Path(TestCommands.TEST_PATH, "testMetadataReplEximTmp");
        String tempLocation = tempPath.toUri().getPath();
        String dbName = "exim";
        String tableName = "basicSrc";
        String importedTableName = "basicDst";
        List<HCatFieldSchema> cols = HCatSchemaUtils.getHCatSchema("b:string").getFields();
        TestCommands.client.dropDatabase(dbName, true, CASCADE);
        TestCommands.client.createDatabase(HCatCreateDBDesc.create(dbName).ifNotExists(false).build());
        HCatTable table = new HCatTable(dbName, tableName).cols(cols).fileFormat("textfile");
        TestCommands.client.createTable(HCatCreateTableDesc.create(table).build());
        HCatTable t = TestCommands.client.getTable(dbName, tableName);
        Assert.assertNotNull(t);
        String[] data = new String[]{ "eleven", "twelve" };
        HcatTestUtils.createTestDataFile(tempLocation, data);
        CommandProcessorResponse ret = TestCommands.driver.run(((((("LOAD DATA LOCAL INPATH '" + tempLocation) + "' OVERWRITE INTO TABLE ") + dbName) + ".") + tableName));
        Assert.assertEquals((((ret.getResponseCode()) + ":") + (ret.getErrorMessage())), null, ret.getException());
        CommandProcessorResponse selectRet = TestCommands.driver.run(((("SELECT * from " + dbName) + ".") + tableName));
        Assert.assertEquals((((selectRet.getResponseCode()) + ":") + (selectRet.getErrorMessage())), null, selectRet.getException());
        List<String> values = new ArrayList<String>();
        TestCommands.driver.getResults(values);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(data[0], values.get(0));
        Assert.assertEquals(data[1], values.get(1));
        ExportCommand exportMdCmd = new ExportCommand(dbName, tableName, null, exportLocation, true, evid);
        TestCommands.LOG.info(("About to run :" + (exportMdCmd.get().get(0))));
        CommandProcessorResponse ret2 = TestCommands.driver.run(exportMdCmd.get().get(0));
        Assert.assertEquals((((ret2.getResponseCode()) + ":") + (ret2.getErrorMessage())), null, ret2.getException());
        List<String> exportPaths = exportMdCmd.cleanupLocationsAfterEvent();
        Assert.assertEquals(1, exportPaths.size());
        String metadata = TestCommands.getMetadataContents(exportPaths.get(0));
        TestCommands.LOG.info("Export returned the following _metadata contents:");
        TestCommands.LOG.info(metadata);
        Assert.assertTrue((metadata + "did not match \"repl.scope\"=\"metadata\""), metadata.matches(".*\"repl.scope\":\"metadata\".*"));
        Assert.assertTrue((metadata + "has \"repl.last.id\""), metadata.matches(".*\"repl.last.id\":.*"));
        ImportCommand importMdCmd = new ImportCommand(dbName, importedTableName, null, exportLocation, true, evid);
        TestCommands.LOG.info(("About to run :" + (importMdCmd.get().get(0))));
        CommandProcessorResponse ret3 = TestCommands.driver.run(importMdCmd.get().get(0));
        Assert.assertEquals((((ret3.getResponseCode()) + ":") + (ret3.getErrorMessage())), null, ret3.getException());
        CommandProcessorResponse selectRet2 = TestCommands.driver.run(((("SELECT * from " + dbName) + ".") + importedTableName));
        Assert.assertEquals((((selectRet2.getResponseCode()) + ":") + (selectRet2.getErrorMessage())), null, selectRet2.getException());
        List<String> values2 = new ArrayList<String>();
        TestCommands.driver.getResults(values2);
        Assert.assertEquals(0, values2.size());
        HCatTable importedTable = TestCommands.client.getTable(dbName, importedTableName);
        Assert.assertNotNull(importedTable);
        Assert.assertTrue(importedTable.getTblProps().containsKey("repl.last.id"));
    }

    @Test
    public void testNoopReplEximCommands() throws Exception {
        // repl noop export on non-existant table, has repl.noop, does not error
        // import repl noop dump, no error
        int evid = 333;
        String exportLocation = ((TestCommands.TEST_PATH) + (File.separator)) + "testNoopReplExim";
        String dbName = "doesNotExist" + (System.currentTimeMillis());
        String tableName = "nope" + (System.currentTimeMillis());
        ExportCommand noopExportCmd = new ExportCommand(dbName, tableName, null, exportLocation, false, evid);
        TestCommands.LOG.info(("About to run :" + (noopExportCmd.get().get(0))));
        CommandProcessorResponse ret = TestCommands.driver.run(noopExportCmd.get().get(0));
        Assert.assertEquals((((ret.getResponseCode()) + ":") + (ret.getErrorMessage())), null, ret.getException());
        List<String> exportPaths = noopExportCmd.cleanupLocationsAfterEvent();
        Assert.assertEquals(1, exportPaths.size());
        String metadata = TestCommands.getMetadataContents(exportPaths.get(0));
        TestCommands.LOG.info("Export returned the following _metadata contents:");
        TestCommands.LOG.info(metadata);
        Assert.assertTrue((metadata + "did not match \"repl.noop\"=\"true\""), metadata.matches(".*\"repl.noop\":\"true\".*"));
        ImportCommand noopImportCmd = new ImportCommand(dbName, tableName, null, exportLocation, false, evid);
        TestCommands.LOG.info(("About to run :" + (noopImportCmd.get().get(0))));
        CommandProcessorResponse ret2 = TestCommands.driver.run(noopImportCmd.get().get(0));
        Assert.assertEquals((((ret2.getResponseCode()) + ":") + (ret2.getErrorMessage())), null, ret2.getException());
        Exception onfe = null;
        try {
            HCatDatabase d_doesNotExist = TestCommands.client.getDatabase(dbName);
        } catch (Exception e) {
            onfe = e;
        }
        Assert.assertNotNull(onfe);
        Assert.assertTrue((onfe instanceof ObjectNotFoundException));
    }
}

