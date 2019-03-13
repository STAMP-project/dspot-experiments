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
package org.apache.hive.hcatalog.listener;


import EventType.ABORT_TXN;
import EventType.ACID_WRITE;
import EventType.ADD_PARTITION;
import EventType.ALLOC_WRITE_ID;
import EventType.ALTER_DATABASE;
import EventType.ALTER_PARTITION;
import EventType.ALTER_TABLE;
import EventType.COMMIT_TXN;
import EventType.CREATE_DATABASE;
import EventType.CREATE_FUNCTION;
import EventType.CREATE_TABLE;
import EventType.DROP_DATABASE;
import EventType.DROP_FUNCTION;
import EventType.DROP_PARTITION;
import EventType.DROP_TABLE;
import EventType.INSERT;
import EventType.OPEN_TXN;
import EventType.UPDATE_PARTITION_COLUMN_STAT;
import FunctionType.JAVA;
import IMetaStoreClient.NotificationFilter;
import JSONMessageEncoder.FORMAT;
import MetaStoreEventListenerConstants.DB_NOTIFICATION_EVENT_ID_KEY_NAME;
import ResourceType.JAR;
import TableType.MANAGED_TABLE;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreEventListener;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.FireEventRequest;
import org.apache.hadoop.hive.metastore.api.FireEventRequestData;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.FunctionType;
import org.apache.hadoop.hive.metastore.api.InsertEventRequestData;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.ResourceType;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.events.AbortTxnEvent;
import org.apache.hadoop.hive.metastore.events.AcidWriteEvent;
import org.apache.hadoop.hive.metastore.events.AddPartitionEvent;
import org.apache.hadoop.hive.metastore.events.AllocWriteIdEvent;
import org.apache.hadoop.hive.metastore.events.AlterPartitionEvent;
import org.apache.hadoop.hive.metastore.events.AlterTableEvent;
import org.apache.hadoop.hive.metastore.events.CommitTxnEvent;
import org.apache.hadoop.hive.metastore.events.CreateDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.CreateFunctionEvent;
import org.apache.hadoop.hive.metastore.events.CreateTableEvent;
import org.apache.hadoop.hive.metastore.events.DropDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.DropFunctionEvent;
import org.apache.hadoop.hive.metastore.events.DropPartitionEvent;
import org.apache.hadoop.hive.metastore.events.DropTableEvent;
import org.apache.hadoop.hive.metastore.events.InsertEvent;
import org.apache.hadoop.hive.metastore.events.ListenerEvent;
import org.apache.hadoop.hive.metastore.events.OpenTxnEvent;
import org.apache.hadoop.hive.metastore.messaging.AddPartitionMessage;
import org.apache.hadoop.hive.metastore.messaging.AlterDatabaseMessage;
import org.apache.hadoop.hive.metastore.messaging.AlterPartitionMessage;
import org.apache.hadoop.hive.metastore.messaging.AlterTableMessage;
import org.apache.hadoop.hive.metastore.messaging.CreateDatabaseMessage;
import org.apache.hadoop.hive.metastore.messaging.CreateFunctionMessage;
import org.apache.hadoop.hive.metastore.messaging.CreateTableMessage;
import org.apache.hadoop.hive.metastore.messaging.DropDatabaseMessage;
import org.apache.hadoop.hive.metastore.messaging.DropFunctionMessage;
import org.apache.hadoop.hive.metastore.messaging.DropPartitionMessage;
import org.apache.hadoop.hive.metastore.messaging.DropTableMessage;
import org.apache.hadoop.hive.metastore.messaging.EventMessage.EventType;
import org.apache.hadoop.hive.metastore.messaging.InsertMessage;
import org.apache.hadoop.hive.metastore.messaging.MessageDeserializer;
import org.apache.hadoop.hive.metastore.messaging.MessageFactory;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hive.hcatalog.api.repl.ReplicationV1CompatRule;
import org.apache.hive.hcatalog.data.Pair;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests DbNotificationListener when used as a transactional event listener
 * (hive.metastore.transactional.event.listeners)
 */
public class TestDbNotificationListener {
    private static final Logger LOG = LoggerFactory.getLogger(TestDbNotificationListener.class.getName());

    private static final int EVENTS_TTL = 30;

    private static final int CLEANUP_SLEEP_TIME = 10;

    private static Map<String, String> emptyParameters = new HashMap<String, String>();

    private static IMetaStoreClient msClient;

    private static IDriver driver;

    private static MessageDeserializer md;

    static {
        try {
            TestDbNotificationListener.md = MessageFactory.getInstance(FORMAT).getDeserializer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int startTime;

    private long firstEventId;

    private final String testTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "testDbNotif").toString();

    private static List<String> testsToSkipForReplV1BackwardCompatTesting = new ArrayList<>(Arrays.asList("cleanupNotifs", "cleanupNotificationWithError", "sqlTempTable"));

    // Make sure we skip backward-compat checking for those tests that don't generate events
    private static ReplicationV1CompatRule bcompat = null;

    @Rule
    public TestRule replV1BackwardCompatibleRule = TestDbNotificationListener.bcompat;

    // Note - above looks funny because it seems like we're instantiating a static var, and
    // then a non-static var as the rule, but the reason this is required is because Rules
    // are not allowed to be static, but we wind up needing it initialized from a static
    // context. So, bcompat is initialzed in a static context, but this rule is initialized
    // before the tests run, and will pick up an initialized value of bcompat.
    /* This class is used to verify that HiveMetaStore calls the non-transactional listeners with the
    current event ID set by the DbNotificationListener class
     */
    public static class MockMetaStoreEventListener extends MetaStoreEventListener {
        private static Stack<Pair<EventType, String>> eventsIds = new Stack<>();

        private static void pushEventId(EventType eventType, final ListenerEvent event) {
            if (event.getStatus()) {
                Map<String, String> parameters = event.getParameters();
                if (parameters.containsKey(DB_NOTIFICATION_EVENT_ID_KEY_NAME)) {
                    Pair<EventType, String> pair = new Pair(eventType, parameters.get(DB_NOTIFICATION_EVENT_ID_KEY_NAME));
                    TestDbNotificationListener.MockMetaStoreEventListener.eventsIds.push(pair);
                }
            }
        }

        public static void popAndVerifyLastEventId(EventType eventType, long id) {
            if (!(TestDbNotificationListener.MockMetaStoreEventListener.eventsIds.isEmpty())) {
                Pair<EventType, String> pair = TestDbNotificationListener.MockMetaStoreEventListener.eventsIds.pop();
                Assert.assertEquals("Last event type does not match.", eventType, pair.first);
                Assert.assertEquals("Last event ID does not match.", Long.toString(id), pair.second);
            } else {
                Assert.assertTrue("List of events is empty.", false);
            }
        }

        public static void clearEvents() {
            TestDbNotificationListener.MockMetaStoreEventListener.eventsIds.clear();
        }

        public MockMetaStoreEventListener(Configuration config) {
            super(config);
        }

        @Override
        public void onCreateTable(CreateTableEvent tableEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(CREATE_TABLE, tableEvent);
        }

        @Override
        public void onDropTable(DropTableEvent tableEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(DROP_TABLE, tableEvent);
        }

        @Override
        public void onAlterTable(AlterTableEvent tableEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(ALTER_TABLE, tableEvent);
        }

        @Override
        public void onAddPartition(AddPartitionEvent partitionEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(ADD_PARTITION, partitionEvent);
        }

        @Override
        public void onDropPartition(DropPartitionEvent partitionEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(DROP_PARTITION, partitionEvent);
        }

        @Override
        public void onAlterPartition(AlterPartitionEvent partitionEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(ALTER_PARTITION, partitionEvent);
        }

        @Override
        public void onCreateDatabase(CreateDatabaseEvent dbEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(CREATE_DATABASE, dbEvent);
        }

        @Override
        public void onDropDatabase(DropDatabaseEvent dbEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(DROP_DATABASE, dbEvent);
        }

        @Override
        public void onCreateFunction(CreateFunctionEvent fnEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(CREATE_FUNCTION, fnEvent);
        }

        @Override
        public void onDropFunction(DropFunctionEvent fnEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(DROP_FUNCTION, fnEvent);
        }

        @Override
        public void onInsert(InsertEvent insertEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(INSERT, insertEvent);
        }

        public void onOpenTxn(OpenTxnEvent openTxnEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(OPEN_TXN, openTxnEvent);
        }

        public void onCommitTxn(CommitTxnEvent commitTxnEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(COMMIT_TXN, commitTxnEvent);
        }

        public void onAbortTxn(AbortTxnEvent abortTxnEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(ABORT_TXN, abortTxnEvent);
        }

        public void onAllocWriteId(AllocWriteIdEvent allocWriteIdEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(ALLOC_WRITE_ID, allocWriteIdEvent);
        }

        public void onAcidWrite(AcidWriteEvent acidWriteEvent) throws MetaException {
            TestDbNotificationListener.MockMetaStoreEventListener.pushEventId(ACID_WRITE, acidWriteEvent);
        }
    }

    @Test
    public void createDatabase() throws Exception {
        String dbName = "createdb";
        String dbName2 = "createdb2";
        String dbLocationUri = testTempDir;
        String dbDescription = "no description";
        Database db = new Database(dbName, dbDescription, dbLocationUri, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        // Read notification from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        // Read event from notification
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(CREATE_DATABASE.toString(), event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertNull(event.getTableName());
        // Parse the message field
        CreateDatabaseMessage createDbMsg = TestDbNotificationListener.md.getCreateDatabaseMessage(event.getMessage());
        Assert.assertEquals(dbName, createDbMsg.getDB());
        Assert.assertEquals(db, createDbMsg.getDatabaseObject());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_DATABASE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        DummyRawStoreFailEvent.setEventSucceed(false);
        db = new Database(dbName2, dbDescription, dbLocationUri, TestDbNotificationListener.emptyParameters);
        try {
            TestDbNotificationListener.msClient.createDatabase(db);
            Assert.fail("Error: create database should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        // There's only one event corresponding to CREATE DATABASE
        testEventCounts(dbName, firstEventId, null, null, 1);
        testEventCounts(dbName2, firstEventId, null, null, 0);
    }

    @Test
    public void alterDatabase() throws Exception {
        String dbName = "alterdb";
        String dbLocationUri = testTempDir;
        String dbDescription = "no description";
        TestDbNotificationListener.msClient.createDatabase(new Database(dbName, dbDescription, dbLocationUri, TestDbNotificationListener.emptyParameters));
        // get the db for comparison below since it may include additional parameters
        Database dbBefore = TestDbNotificationListener.msClient.getDatabase(dbName);
        // create alter database notification
        String newDesc = "test database";
        Database dbAfter = dbBefore.deepCopy();
        dbAfter.setDescription(newDesc);
        TestDbNotificationListener.msClient.alterDatabase(dbName, dbAfter);
        dbAfter = TestDbNotificationListener.msClient.getDatabase(dbName);
        // Read notification from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        // check the contents of alter database notification
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(ALTER_DATABASE.toString(), event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertNull(event.getTableName());
        // Parse the message field
        AlterDatabaseMessage alterDatabaseMessage = TestDbNotificationListener.md.getAlterDatabaseMessage(event.getMessage());
        Assert.assertEquals(dbName, alterDatabaseMessage.getDB());
        Assert.assertEquals(dbBefore, alterDatabaseMessage.getDbObjBefore());
        Assert.assertEquals(dbAfter, alterDatabaseMessage.getDbObjAfter());
    }

    @Test
    public void dropDatabase() throws Exception {
        String dbName = "dropdb";
        String dbName2 = "dropdb2";
        String dbLocationUri = testTempDir;
        String dbDescription = "no description";
        Database db = new Database(dbName, dbDescription, dbLocationUri, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        TestDbNotificationListener.msClient.dropDatabase(dbName);
        // Read notification from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        // Two events: one for create db and other for drop db
        Assert.assertEquals(2, rsp.getEventsSize());
        testEventCounts(dbName, firstEventId, null, null, 2);
        // Read event from notification
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(DROP_DATABASE.toString(), event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertNull(event.getTableName());
        // Parse the message field
        DropDatabaseMessage dropDbMsg = TestDbNotificationListener.md.getDropDatabaseMessage(event.getMessage());
        Assert.assertEquals(dbName, dropDbMsg.getDB());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(DROP_DATABASE, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_DATABASE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        db = new Database(dbName2, dbDescription, dbLocationUri, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.dropDatabase(dbName2);
            Assert.fail("Error: drop database should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        testEventCounts(dbName2, firstEventId, null, null, 1);
    }

    @Test
    public void createTable() throws Exception {
        String defaultDbName = "default";
        String tblName = "createtable";
        String tblName2 = "createtable2";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, null, TestDbNotificationListener.emptyParameters, null, null, MANAGED_TABLE.toString());
        TestDbNotificationListener.msClient.createTable(table);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(CREATE_TABLE.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        CreateTableMessage createTblMsg = TestDbNotificationListener.md.getCreateTableMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, createTblMsg.getDB());
        Assert.assertEquals(tblName, createTblMsg.getTable());
        Assert.assertEquals(table, createTblMsg.getTableObj());
        Assert.assertEquals(MANAGED_TABLE.toString(), createTblMsg.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        table = new Table(tblName2, defaultDbName, tblOwner, startTime, startTime, 0, sd, null, TestDbNotificationListener.emptyParameters, null, null, null);
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.createTable(table);
            Assert.fail("Error: create table should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 1);
    }

    @Test
    public void alterTable() throws Exception {
        String defaultDbName = "default";
        String tblName = "altertabletbl";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        FieldSchema col2 = new FieldSchema("col2", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, new ArrayList<FieldSchema>(), TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        cols.add(col2);
        table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, new ArrayList<FieldSchema>(), TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 2
        TestDbNotificationListener.msClient.alter_table(defaultDbName, tblName, table);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(ALTER_TABLE.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        AlterTableMessage alterTableMessage = TestDbNotificationListener.md.getAlterTableMessage(event.getMessage());
        Assert.assertEquals(table, alterTableMessage.getTableObjAfter());
        Assert.assertEquals(MANAGED_TABLE.toString(), alterTableMessage.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ALTER_TABLE, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.alter_table(defaultDbName, tblName, table);
            Assert.fail("Error: alter table should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 2);
    }

    @Test
    public void dropTable() throws Exception {
        String defaultDbName = "default";
        String tblName = "droptbl";
        String tblName2 = "droptbl2";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, null, TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        // Event 2
        TestDbNotificationListener.msClient.dropTable(defaultDbName, tblName);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(DROP_TABLE.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        DropTableMessage dropTblMsg = TestDbNotificationListener.md.getDropTableMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, dropTblMsg.getDB());
        Assert.assertEquals(tblName, dropTblMsg.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), dropTblMsg.getTableType());
        Table tableObj = dropTblMsg.getTableObj();
        Assert.assertEquals(table.getDbName(), tableObj.getDbName());
        Assert.assertEquals(table.getTableName(), tableObj.getTableName());
        Assert.assertEquals(table.getOwner(), tableObj.getOwner());
        Assert.assertEquals(table.getParameters(), tableObj.getParameters());
        Assert.assertEquals(MANAGED_TABLE.toString(), tableObj.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(DROP_TABLE, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        table = new Table(tblName2, defaultDbName, tblOwner, startTime, startTime, 0, sd, null, TestDbNotificationListener.emptyParameters, null, null, null);
        TestDbNotificationListener.msClient.createTable(table);
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.dropTable(defaultDbName, tblName2);
            Assert.fail("Error: drop table should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 3);
    }

    @Test
    public void addPartition() throws Exception {
        String defaultDbName = "default";
        String tblName = "addptn";
        String tblName2 = "addptn2";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        FieldSchema partCol1 = new FieldSchema("ds", "string", "no comment");
        List<FieldSchema> partCols = new ArrayList<FieldSchema>();
        List<String> partCol1Vals = Arrays.asList("today");
        partCols.add(partCol1);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, partCols, TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        Partition partition = new Partition(partCol1Vals, defaultDbName, tblName, startTime, startTime, sd, TestDbNotificationListener.emptyParameters);
        // Event 2
        TestDbNotificationListener.msClient.add_partition(partition);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(ADD_PARTITION.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        AddPartitionMessage addPtnMsg = TestDbNotificationListener.md.getAddPartitionMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, addPtnMsg.getDB());
        Assert.assertEquals(tblName, addPtnMsg.getTable());
        Iterator<Partition> ptnIter = addPtnMsg.getPartitionObjs().iterator();
        Assert.assertTrue(ptnIter.hasNext());
        Assert.assertEquals(partition, ptnIter.next());
        Assert.assertEquals(MANAGED_TABLE.toString(), addPtnMsg.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ADD_PARTITION, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        partition = new Partition(Arrays.asList("tomorrow"), defaultDbName, tblName2, startTime, startTime, sd, TestDbNotificationListener.emptyParameters);
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.add_partition(partition);
            Assert.fail("Error: add partition should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 2);
    }

    @Test
    public void alterPartition() throws Exception {
        String defaultDbName = "default";
        String tblName = "alterptn";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        FieldSchema partCol1 = new FieldSchema("ds", "string", "no comment");
        List<FieldSchema> partCols = new ArrayList<FieldSchema>();
        List<String> partCol1Vals = Arrays.asList("today");
        partCols.add(partCol1);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, partCols, TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        Partition partition = new Partition(partCol1Vals, defaultDbName, tblName, startTime, startTime, sd, TestDbNotificationListener.emptyParameters);
        // Event 2
        TestDbNotificationListener.msClient.add_partition(partition);
        Partition newPart = new Partition(Arrays.asList("today"), defaultDbName, tblName, startTime, ((startTime) + 1), sd, TestDbNotificationListener.emptyParameters);
        // Event 3
        TestDbNotificationListener.msClient.alter_partition(defaultDbName, tblName, newPart, null);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(2);
        Assert.assertEquals(((firstEventId) + 3), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(ALTER_PARTITION.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        AlterPartitionMessage alterPtnMsg = TestDbNotificationListener.md.getAlterPartitionMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, alterPtnMsg.getDB());
        Assert.assertEquals(tblName, alterPtnMsg.getTable());
        Assert.assertEquals(newPart, alterPtnMsg.getPtnObjAfter());
        Assert.assertEquals(MANAGED_TABLE.toString(), alterPtnMsg.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ADD_PARTITION, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.alter_partition(defaultDbName, tblName, newPart, null);
            Assert.fail("Error: alter partition should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 3);
    }

    @Test
    public void dropPartition() throws Exception {
        String defaultDbName = "default";
        String tblName = "dropptn";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        FieldSchema partCol1 = new FieldSchema("ds", "string", "no comment");
        List<FieldSchema> partCols = new ArrayList<FieldSchema>();
        List<String> partCol1Vals = Arrays.asList("today");
        partCols.add(partCol1);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, partCols, TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        Partition partition = new Partition(partCol1Vals, defaultDbName, tblName, startTime, startTime, sd, TestDbNotificationListener.emptyParameters);
        // Event 2
        TestDbNotificationListener.msClient.add_partition(partition);
        // Event 3
        TestDbNotificationListener.msClient.dropPartition(defaultDbName, tblName, partCol1Vals, false);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(2);
        Assert.assertEquals(((firstEventId) + 3), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(DROP_PARTITION.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        DropPartitionMessage dropPtnMsg = TestDbNotificationListener.md.getDropPartitionMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, dropPtnMsg.getDB());
        Assert.assertEquals(tblName, dropPtnMsg.getTable());
        Table tableObj = dropPtnMsg.getTableObj();
        Assert.assertEquals(table.getDbName(), tableObj.getDbName());
        Assert.assertEquals(table.getTableName(), tableObj.getTableName());
        Assert.assertEquals(table.getOwner(), tableObj.getOwner());
        Assert.assertEquals(MANAGED_TABLE.toString(), dropPtnMsg.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(DROP_PARTITION, ((firstEventId) + 3));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ADD_PARTITION, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        List<String> newpartCol1Vals = Arrays.asList("tomorrow");
        partition = new Partition(newpartCol1Vals, defaultDbName, tblName, startTime, startTime, sd, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.add_partition(partition);
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.dropPartition(defaultDbName, tblName, newpartCol1Vals, false);
            Assert.fail("Error: drop partition should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(4, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 4);
    }

    @Test
    public void exchangePartition() throws Exception {
        String dbName = "default";
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(new FieldSchema("col1", "int", "nocomment"));
        List<FieldSchema> partCols = new ArrayList<FieldSchema>();
        partCols.add(new FieldSchema("part", "int", ""));
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd1 = new StorageDescriptor(cols, Paths.get(testTempDir, "1").toString(), "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Table tab1 = new Table("tab1", dbName, "me", startTime, startTime, 0, sd1, partCols, TestDbNotificationListener.emptyParameters, null, null, null);
        TestDbNotificationListener.msClient.createTable(tab1);
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());// add_table

        StorageDescriptor sd2 = new StorageDescriptor(cols, Paths.get(testTempDir, "2").toString(), "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Table tab2 = new Table("tab2", dbName, "me", startTime, startTime, 0, sd2, partCols, TestDbNotificationListener.emptyParameters, null, null, null);// add_table

        TestDbNotificationListener.msClient.createTable(tab2);
        rsp = TestDbNotificationListener.msClient.getNextNotification(((firstEventId) + 1), 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        StorageDescriptor sd1part = new StorageDescriptor(cols, Paths.get(testTempDir, "1", "part=1").toString(), "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        StorageDescriptor sd2part = new StorageDescriptor(cols, Paths.get(testTempDir, "1", "part=2").toString(), "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        StorageDescriptor sd3part = new StorageDescriptor(cols, Paths.get(testTempDir, "1", "part=3").toString(), "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Partition part1 = new Partition(Arrays.asList("1"), "default", tab1.getTableName(), startTime, startTime, sd1part, TestDbNotificationListener.emptyParameters);
        Partition part2 = new Partition(Arrays.asList("2"), "default", tab1.getTableName(), startTime, startTime, sd2part, TestDbNotificationListener.emptyParameters);
        Partition part3 = new Partition(Arrays.asList("3"), "default", tab1.getTableName(), startTime, startTime, sd3part, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.add_partitions(Arrays.asList(part1, part2, part3));
        rsp = TestDbNotificationListener.msClient.getNextNotification(((firstEventId) + 2), 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());// add_partition

        TestDbNotificationListener.msClient.exchange_partition(ImmutableMap.of("part", "1"), dbName, tab1.getTableName(), dbName, tab2.getTableName());
        rsp = TestDbNotificationListener.msClient.getNextNotification(((firstEventId) + 3), 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 4), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(ADD_PARTITION.toString(), event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertEquals(tab2.getTableName(), event.getTableName());
        // Parse the message field
        AddPartitionMessage addPtnMsg = TestDbNotificationListener.md.getAddPartitionMessage(event.getMessage());
        Assert.assertEquals(dbName, addPtnMsg.getDB());
        Assert.assertEquals(tab2.getTableName(), addPtnMsg.getTable());
        Iterator<Partition> ptnIter = addPtnMsg.getPartitionObjs().iterator();
        Assert.assertEquals(MANAGED_TABLE.toString(), addPtnMsg.getTableType());
        Assert.assertTrue(ptnIter.hasNext());
        Partition msgPart = ptnIter.next();
        Assert.assertEquals(part1.getValues(), msgPart.getValues());
        Assert.assertEquals(dbName, msgPart.getDbName());
        Assert.assertEquals(tab2.getTableName(), msgPart.getTableName());
        event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 5), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(DROP_PARTITION.toString(), event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertEquals(tab1.getTableName(), event.getTableName());
        // Parse the message field
        DropPartitionMessage dropPtnMsg = TestDbNotificationListener.md.getDropPartitionMessage(event.getMessage());
        Assert.assertEquals(dbName, dropPtnMsg.getDB());
        Assert.assertEquals(tab1.getTableName(), dropPtnMsg.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), dropPtnMsg.getTableType());
        Iterator<Map<String, String>> parts = dropPtnMsg.getPartitions().iterator();
        Assert.assertTrue(parts.hasNext());
        Assert.assertEquals(part1.getValues(), Lists.newArrayList(parts.next().values()));
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(DROP_PARTITION, ((firstEventId) + 5));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ADD_PARTITION, ((firstEventId) + 4));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ADD_PARTITION, ((firstEventId) + 3));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        testEventCounts(dbName, firstEventId, null, null, 5);
    }

    @Test
    public void createFunction() throws Exception {
        String defaultDbName = "default";
        String funcName = "createfunction";
        String funcName2 = "createfunction2";
        String ownerName = "me";
        String funcClass = "o.a.h.h.createfunc";
        String funcClass2 = "o.a.h.h.createfunc2";
        String funcResource = Paths.get(testTempDir, "somewhere").toString();
        String funcResource2 = Paths.get(testTempDir, "somewhere2").toString();
        Function func = new Function(funcName, defaultDbName, funcClass, ownerName, PrincipalType.USER, startTime, FunctionType.JAVA, Arrays.asList(new org.apache.hadoop.hive.metastore.api.ResourceUri(ResourceType.JAR, funcResource)));
        // Event 1
        TestDbNotificationListener.msClient.createFunction(func);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(CREATE_FUNCTION.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        // Parse the message field
        CreateFunctionMessage createFuncMsg = TestDbNotificationListener.md.getCreateFunctionMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, createFuncMsg.getDB());
        Function funcObj = createFuncMsg.getFunctionObj();
        Assert.assertEquals(defaultDbName, funcObj.getDbName());
        Assert.assertEquals(funcName, funcObj.getFunctionName());
        Assert.assertEquals(funcClass, funcObj.getClassName());
        Assert.assertEquals(ownerName, funcObj.getOwnerName());
        Assert.assertEquals(JAVA, funcObj.getFunctionType());
        Assert.assertEquals(1, funcObj.getResourceUrisSize());
        Assert.assertEquals(JAR, funcObj.getResourceUris().get(0).getResourceType());
        Assert.assertEquals(funcResource, funcObj.getResourceUris().get(0).getUri());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_FUNCTION, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        DummyRawStoreFailEvent.setEventSucceed(false);
        func = new Function(funcName2, defaultDbName, funcClass2, ownerName, PrincipalType.USER, startTime, FunctionType.JAVA, Arrays.asList(new org.apache.hadoop.hive.metastore.api.ResourceUri(ResourceType.JAR, funcResource2)));
        try {
            TestDbNotificationListener.msClient.createFunction(func);
            Assert.fail("Error: create function should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 1);
    }

    @Test
    public void dropFunction() throws Exception {
        String defaultDbName = "default";
        String funcName = "dropfunction";
        String funcName2 = "dropfunction2";
        String ownerName = "me";
        String funcClass = "o.a.h.h.dropfunction";
        String funcClass2 = "o.a.h.h.dropfunction2";
        String funcResource = Paths.get(testTempDir, "somewhere").toString();
        String funcResource2 = Paths.get(testTempDir, "somewhere2").toString();
        Function func = new Function(funcName, defaultDbName, funcClass, ownerName, PrincipalType.USER, startTime, FunctionType.JAVA, Arrays.asList(new org.apache.hadoop.hive.metastore.api.ResourceUri(ResourceType.JAR, funcResource)));
        // Event 1
        TestDbNotificationListener.msClient.createFunction(func);
        // Event 2
        TestDbNotificationListener.msClient.dropFunction(defaultDbName, funcName);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(DROP_FUNCTION.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        // Parse the message field
        DropFunctionMessage dropFuncMsg = TestDbNotificationListener.md.getDropFunctionMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, dropFuncMsg.getDB());
        Assert.assertEquals(funcName, dropFuncMsg.getFunctionName());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(DROP_FUNCTION, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_FUNCTION, ((firstEventId) + 1));
        // When hive.metastore.transactional.event.listeners is set,
        // a failed event should not create a new notification
        func = new Function(funcName2, defaultDbName, funcClass2, ownerName, PrincipalType.USER, startTime, FunctionType.JAVA, Arrays.asList(new org.apache.hadoop.hive.metastore.api.ResourceUri(ResourceType.JAR, funcResource2)));
        TestDbNotificationListener.msClient.createFunction(func);
        DummyRawStoreFailEvent.setEventSucceed(false);
        try {
            TestDbNotificationListener.msClient.dropFunction(defaultDbName, funcName2);
            Assert.fail("Error: drop function should've failed");
        } catch (Exception ex) {
            // expected
        }
        rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 3);
    }

    @Test
    public void insertTable() throws Exception {
        String defaultDbName = "default";
        String tblName = "inserttbl";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        String fileAdded = "/warehouse/mytable/b1";
        String checksumAdded = "1234";
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, null, TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        FireEventRequestData data = new FireEventRequestData();
        InsertEventRequestData insertData = new InsertEventRequestData();
        data.setInsertData(insertData);
        insertData.addToFilesAdded(fileAdded);
        insertData.addToFilesAddedChecksum(checksumAdded);
        FireEventRequest rqst = new FireEventRequest(true, data);
        rqst.setDbName(defaultDbName);
        rqst.setTableName(tblName);
        // Event 2
        TestDbNotificationListener.msClient.fireListenerEvent(rqst);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        verifyInsert(event, defaultDbName, tblName);
        // Parse the message field
        InsertMessage insertMessage = TestDbNotificationListener.md.getInsertMessage(event.getMessage());
        Assert.assertEquals(defaultDbName, insertMessage.getDB());
        Assert.assertEquals(tblName, insertMessage.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), insertMessage.getTableType());
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(INSERT, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        testEventCounts(defaultDbName, firstEventId, null, null, 2);
    }

    @Test
    public void insertPartition() throws Exception {
        String defaultDbName = "default";
        String tblName = "insertptn";
        String tblOwner = "me";
        String serdeLocation = testTempDir;
        String fileAdded = "/warehouse/mytable/b1";
        String checksumAdded = "1234";
        FieldSchema col1 = new FieldSchema("col1", "int", "no comment");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
        StorageDescriptor sd = new StorageDescriptor(cols, serdeLocation, "input", "output", false, 0, serde, null, null, TestDbNotificationListener.emptyParameters);
        FieldSchema partCol1 = new FieldSchema("ds", "string", "no comment");
        List<FieldSchema> partCols = new ArrayList<FieldSchema>();
        List<String> partCol1Vals = Arrays.asList("today");
        List<String> partKeyVals = new ArrayList<String>();
        partKeyVals.add("today");
        partCols.add(partCol1);
        Table table = new Table(tblName, defaultDbName, tblOwner, startTime, startTime, 0, sd, partCols, TestDbNotificationListener.emptyParameters, null, null, null);
        // Event 1
        TestDbNotificationListener.msClient.createTable(table);
        Partition partition = new Partition(partCol1Vals, defaultDbName, tblName, startTime, startTime, sd, TestDbNotificationListener.emptyParameters);
        // Event 2
        TestDbNotificationListener.msClient.add_partition(partition);
        FireEventRequestData data = new FireEventRequestData();
        InsertEventRequestData insertData = new InsertEventRequestData();
        data.setInsertData(insertData);
        insertData.addToFilesAdded(fileAdded);
        insertData.addToFilesAddedChecksum(checksumAdded);
        FireEventRequest rqst = new FireEventRequest(true, data);
        rqst.setDbName(defaultDbName);
        rqst.setTableName(tblName);
        rqst.setPartitionVals(partCol1Vals);
        // Event 3
        TestDbNotificationListener.msClient.fireListenerEvent(rqst);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(2);
        Assert.assertEquals(((firstEventId) + 3), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        Assert.assertEquals(defaultDbName, event.getDbName());
        Assert.assertEquals(tblName, event.getTableName());
        // Parse the message field
        verifyInsert(event, defaultDbName, tblName);
        InsertMessage insertMessage = TestDbNotificationListener.md.getInsertMessage(event.getMessage());
        List<String> ptnValues = insertMessage.getPtnObj().getValues();
        Assert.assertEquals(partKeyVals, ptnValues);
        // Verify the eventID was passed to the non-transactional listener
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(INSERT, ((firstEventId) + 3));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(ADD_PARTITION, ((firstEventId) + 2));
        TestDbNotificationListener.MockMetaStoreEventListener.popAndVerifyLastEventId(CREATE_TABLE, ((firstEventId) + 1));
        testEventCounts(defaultDbName, firstEventId, null, null, 3);
    }

    @Test
    public void getOnlyMaxEvents() throws Exception {
        Database db = new Database("db1", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        db = new Database("db2", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        db = new Database("db3", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 2, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        Assert.assertEquals(((firstEventId) + 1), rsp.getEvents().get(0).getEventId());
        Assert.assertEquals(((firstEventId) + 2), rsp.getEvents().get(1).getEventId());
    }

    @Test
    public void filter() throws Exception {
        Database db = new Database("f1", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        db = new Database("f2", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        TestDbNotificationListener.msClient.dropDatabase("f2");
        IMetaStoreClient.NotificationFilter filter = new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent event) {
                return event.getEventType().equals(DROP_DATABASE.toString());
            }
        };
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, filter);
        Assert.assertEquals(1, rsp.getEventsSize());
        Assert.assertEquals(((firstEventId) + 3), rsp.getEvents().get(0).getEventId());
    }

    @Test
    public void filterWithMax() throws Exception {
        Database db = new Database("f10", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        db = new Database("f11", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        TestDbNotificationListener.msClient.dropDatabase("f11");
        IMetaStoreClient.NotificationFilter filter = new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent event) {
                return event.getEventType().equals(CREATE_DATABASE.toString());
            }
        };
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 1, filter);
        Assert.assertEquals(1, rsp.getEventsSize());
        Assert.assertEquals(((firstEventId) + 1), rsp.getEvents().get(0).getEventId());
    }

    @Test
    public void sqlInsertTable() throws Exception {
        String defaultDbName = "default";
        String tblName = "sqlins";
        // Event 1
        TestDbNotificationListener.driver.run((("create table " + tblName) + " (c int)"));
        // Event 2 (alter: marker stats event), 3 (insert), 4 (alter: stats update event)
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " values (1)"));
        // Event 5
        TestDbNotificationListener.driver.run((("alter table " + tblName) + " add columns (c2 int)"));
        // Event 6
        TestDbNotificationListener.driver.run(("drop table " + tblName));
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(7, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertEquals(CREATE_TABLE.toString(), event.getEventType());
        event = rsp.getEvents().get(2);
        Assert.assertEquals(((firstEventId) + 3), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // Parse the message field
        verifyInsert(event, defaultDbName, tblName);
        event = rsp.getEvents().get(5);
        Assert.assertEquals(((firstEventId) + 6), event.getEventId());
        Assert.assertEquals(ALTER_TABLE.toString(), event.getEventType());
        event = rsp.getEvents().get(6);
        Assert.assertEquals(((firstEventId) + 7), event.getEventId());
        Assert.assertEquals(DROP_TABLE.toString(), event.getEventType());
        testEventCounts(defaultDbName, firstEventId, null, null, 7);
    }

    @Test
    public void sqlCTAS() throws Exception {
        String defaultDbName = "default";
        String sourceTblName = "sqlctasins1";
        String targetTblName = "sqlctasins2";
        // Event 1
        TestDbNotificationListener.driver.run((("create table " + sourceTblName) + " (c int)"));
        // Event 2 (alter: marker stats event), 3 (insert), 4 (alter: stats update event)
        TestDbNotificationListener.driver.run((("insert into table " + sourceTblName) + " values (1)"));
        // Event 5, 6 (alter), 7 (alter: stats update event)
        TestDbNotificationListener.driver.run(((("create table " + targetTblName) + " as select c from ") + sourceTblName));
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(8, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertEquals(CREATE_TABLE.toString(), event.getEventType());
        event = rsp.getEvents().get(2);
        Assert.assertEquals(((firstEventId) + 3), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // Parse the message field
        verifyInsert(event, null, sourceTblName);
        event = rsp.getEvents().get(5);
        Assert.assertEquals(((firstEventId) + 6), event.getEventId());
        Assert.assertEquals(CREATE_TABLE.toString(), event.getEventType());
        testEventCounts(defaultDbName, firstEventId, null, null, 8);
    }

    @Test
    public void sqlTempTable() throws Exception {
        String defaultDbName = "default";
        String tempTblName = "sqltemptbl";
        TestDbNotificationListener.driver.run((("create temporary table " + tempTblName) + "  (c int)"));
        TestDbNotificationListener.driver.run((("insert into table " + tempTblName) + " values (1)"));
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(0, rsp.getEventsSize());
        testEventCounts(defaultDbName, firstEventId, null, null, 0);
    }

    @Test
    public void sqlDb() throws Exception {
        String dbName = "sqldb";
        // Event 1
        TestDbNotificationListener.driver.run(("create database " + dbName));
        // Event 2
        TestDbNotificationListener.driver.run(("drop database " + dbName));
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertEquals(CREATE_DATABASE.toString(), event.getEventType());
        event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertEquals(DROP_DATABASE.toString(), event.getEventType());
    }

    @Test
    public void sqlInsertPartition() throws Exception {
        String defaultDbName = "default";
        String tblName = "sqlinsptn";
        // Event 1
        TestDbNotificationListener.driver.run((("create table " + tblName) + " (c int) partitioned by (ds string)"));
        // Event 2, 3, 4
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds = 'today') values (1)"));
        // Event 5, 6, 7
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds = 'today') values (2)"));
        // Event 8, 9, 10
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds) values (3, 'today')"));
        // Event 9, 10
        TestDbNotificationListener.driver.run((("alter table " + tblName) + " add partition (ds = 'yesterday')"));
        testEventCounts(defaultDbName, firstEventId, null, null, 13);
        // Test a limit higher than available events
        testEventCounts(defaultDbName, firstEventId, null, 100, 13);
        // Test toEventId lower than current eventId
        testEventCounts(defaultDbName, firstEventId, (((long) (firstEventId)) + 5), null, 5);
        // Event 10, 11, 12
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds = 'yesterday') values (2)"));
        // Event 12, 13, 14
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds) values (3, 'yesterday')"));
        // Event 15, 16, 17
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds) values (3, 'tomorrow')"));
        // Event 18
        TestDbNotificationListener.driver.run((("alter table " + tblName) + " drop partition (ds = 'tomorrow')"));
        // Event 19, 20, 21
        TestDbNotificationListener.driver.run((("insert into table " + tblName) + " partition (ds) values (42, 'todaytwo')"));
        // Event 22, 23, 24
        TestDbNotificationListener.driver.run((((("insert overwrite table " + tblName) + " partition(ds='todaytwo') select c from ") + tblName) + " where 'ds'='today'"));
        // Get notifications from metastore
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(31, rsp.getEventsSize());
        NotificationEvent event = rsp.getEvents().get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertEquals(ADD_PARTITION.toString(), event.getEventType());
        event = rsp.getEvents().get(3);
        Assert.assertEquals(((firstEventId) + 4), event.getEventId());
        Assert.assertEquals(UPDATE_PARTITION_COLUMN_STAT.toString(), event.getEventType());
        event = rsp.getEvents().get(4);
        Assert.assertEquals(((firstEventId) + 5), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // Parse the message field
        verifyInsert(event, null, tblName);
        event = rsp.getEvents().get(8);
        Assert.assertEquals(((firstEventId) + 9), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // Parse the message field
        verifyInsert(event, null, tblName);
        event = rsp.getEvents().get(12);
        Assert.assertEquals(((firstEventId) + 13), event.getEventId());
        Assert.assertEquals(ADD_PARTITION.toString(), event.getEventType());
        event = rsp.getEvents().get(13);
        Assert.assertEquals(((firstEventId) + 14), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // Parse the message field
        verifyInsert(event, null, tblName);
        event = rsp.getEvents().get(17);
        Assert.assertEquals(((firstEventId) + 18), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // Parse the message field
        verifyInsert(event, null, tblName);
        event = rsp.getEvents().get(21);
        Assert.assertEquals(((firstEventId) + 22), event.getEventId());
        Assert.assertEquals(ADD_PARTITION.toString(), event.getEventType());
        event = rsp.getEvents().get(24);
        Assert.assertEquals(((firstEventId) + 25), event.getEventId());
        Assert.assertEquals(DROP_PARTITION.toString(), event.getEventType());
        event = rsp.getEvents().get(25);
        Assert.assertEquals(((firstEventId) + 26), event.getEventId());
        Assert.assertEquals(ADD_PARTITION.toString(), event.getEventType());
        event = rsp.getEvents().get(26);
        Assert.assertEquals(((firstEventId) + 27), event.getEventId());
        Assert.assertEquals(ALTER_PARTITION.toString(), event.getEventType());
        Assert.assertTrue(event.getMessage().matches(".*\"ds\":\"todaytwo\".*"));
        // Test fromEventId different from the very first
        testEventCounts(defaultDbName, event.getEventId(), null, null, 4);
        event = rsp.getEvents().get(28);
        Assert.assertEquals(((firstEventId) + 29), event.getEventId());
        Assert.assertEquals(INSERT.toString(), event.getEventType());
        // replace-overwrite introduces no new files
        Assert.assertTrue(event.getMessage().matches(".*\"files\":\\[\\].*"));
        event = rsp.getEvents().get(29);
        Assert.assertEquals(((firstEventId) + 30), event.getEventId());
        Assert.assertEquals(ALTER_PARTITION.toString(), event.getEventType());
        Assert.assertTrue(event.getMessage().matches(".*\"ds\":\"todaytwo\".*"));
        event = rsp.getEvents().get(30);
        Assert.assertEquals(((firstEventId) + 31), event.getEventId());
        Assert.assertEquals(ALTER_PARTITION.toString(), event.getEventType());
        Assert.assertTrue(event.getMessage().matches(".*\"ds\":\"todaytwo\".*"));
        testEventCounts(defaultDbName, firstEventId, null, null, 31);
        // Test a limit within the available events
        testEventCounts(defaultDbName, firstEventId, null, 10, 10);
        // Test toEventId greater than current eventId
        testEventCounts(defaultDbName, firstEventId, (((long) (firstEventId)) + 100), null, 31);
        // Test toEventId greater than current eventId with some limit within available events
        testEventCounts(defaultDbName, firstEventId, (((long) (firstEventId)) + 100), 10, 10);
        // Test toEventId greater than current eventId with some limit beyond available events
        testEventCounts(defaultDbName, firstEventId, (((long) (firstEventId)) + 100), 50, 31);
    }

    @Test
    public void cleanupNotifs() throws Exception {
        Database db = new Database("cleanup1", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        TestDbNotificationListener.msClient.dropDatabase("cleanup1");
        TestDbNotificationListener.LOG.info("Pulling events immediately after createDatabase/dropDatabase");
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        // sleep for expiry time, and then fetch again
        // sleep twice the TTL interval - things should have been cleaned by then.
        Thread.sleep((((TestDbNotificationListener.EVENTS_TTL) * 2) * 1000));
        TestDbNotificationListener.LOG.info("Pulling events again after cleanup");
        NotificationEventResponse rsp2 = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        TestDbNotificationListener.LOG.info("second trigger done");
        Assert.assertEquals(0, rsp2.getEventsSize());
    }

    @Test
    public void cleanupNotificationWithError() throws Exception {
        Database db = new Database("cleanup1", "no description", testTempDir, TestDbNotificationListener.emptyParameters);
        TestDbNotificationListener.msClient.createDatabase(db);
        TestDbNotificationListener.msClient.dropDatabase("cleanup1");
        TestDbNotificationListener.LOG.info("Pulling events immediately after createDatabase/dropDatabase");
        NotificationEventResponse rsp = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, rsp.getEventsSize());
        // this simulates that cleaning thread will error out while cleaning the notifications
        DummyRawStoreFailEvent.setEventSucceed(false);
        // sleep for expiry time, and then fetch again
        // sleep twice the TTL interval - things should have been cleaned by then.
        Thread.sleep((((TestDbNotificationListener.EVENTS_TTL) * 2) * 1000));
        TestDbNotificationListener.LOG.info("Pulling events again after failing to cleanup");
        NotificationEventResponse rsp2 = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        TestDbNotificationListener.LOG.info("second trigger done");
        Assert.assertEquals(2, rsp2.getEventsSize());
        DummyRawStoreFailEvent.setEventSucceed(true);
        Thread.sleep((((TestDbNotificationListener.EVENTS_TTL) * 2) * 1000));
        TestDbNotificationListener.LOG.info("Pulling events again after cleanup");
        rsp2 = TestDbNotificationListener.msClient.getNextNotification(firstEventId, 0, null);
        TestDbNotificationListener.LOG.info("third trigger done");
        Assert.assertEquals(0, rsp2.getEventsSize());
    }
}

