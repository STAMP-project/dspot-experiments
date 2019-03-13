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


import HCatClient.DropDBMode.RESTRICT;
import HCatConstants.HCAT_ADD_PARTITION_EVENT;
import HCatConstants.HCAT_CREATE_DATABASE_EVENT;
import HCatConstants.HCAT_CREATE_TABLE_EVENT;
import HCatConstants.HCAT_DROP_DATABASE_EVENT;
import HCatConstants.HCAT_DROP_PARTITION_EVENT;
import HCatConstants.HCAT_DROP_TABLE_EVENT;
import HCatTable.NO_DIFF;
import IMetaStoreClient.NotificationFilter;
import TableType.MANAGED_TABLE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hive.hcatalog.messaging.AddPartitionMessage;
import org.apache.hive.hcatalog.messaging.CreateDatabaseMessage;
import org.apache.hive.hcatalog.messaging.CreateTableMessage;
import org.apache.hive.hcatalog.messaging.DropDatabaseMessage;
import org.apache.hive.hcatalog.messaging.DropPartitionMessage;
import org.apache.hive.hcatalog.messaging.DropTableMessage;
import org.apache.hive.hcatalog.messaging.MessageDeserializer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This can't use TestHCatClient because it has to have control over certain conf variables when
 * the metastore is started.  Plus, we don't need a metastore running in another thread.  The
 * local one is fine.
 */
public class TestHCatClientNotification {
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatClientNotification.class.getName());

    private static HCatClient hCatClient;

    private static MessageDeserializer md = null;

    private int startTime;

    private long firstEventId;

    @Test
    public void createDatabase() throws Exception {
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("myhcatdb").build());
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, events.size());
        HCatNotificationEvent event = events.get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(HCAT_CREATE_DATABASE_EVENT, event.getEventType());
        Assert.assertEquals("myhcatdb", event.getDbName());
        Assert.assertNull(event.getTableName());
        CreateDatabaseMessage createDatabaseMessage = TestHCatClientNotification.md.getCreateDatabaseMessage(event.getMessage());
        Assert.assertEquals("myhcatdb", createDatabaseMessage.getDB());
    }

    @Test
    public void dropDatabase() throws Exception {
        String dbname = "hcatdropdb";
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create(dbname).build());
        TestHCatClientNotification.hCatClient.dropDatabase(dbname, false, RESTRICT);
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, events.size());
        HCatNotificationEvent event = events.get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(HCAT_DROP_DATABASE_EVENT, event.getEventType());
        Assert.assertEquals(dbname, event.getDbName());
        Assert.assertNull(event.getTableName());
        DropDatabaseMessage dropDatabaseMessage = TestHCatClientNotification.md.getDropDatabaseMessage(event.getMessage());
        Assert.assertEquals(dbname, dropDatabaseMessage.getDB());
    }

    @Test
    public void createTable() throws Exception {
        String dbName = "default";
        String tableName = "hcatcreatetable";
        HCatTable table = new HCatTable(dbName, tableName);
        table.cols(Arrays.asList(new org.apache.hive.hcatalog.data.schema.HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
        TestHCatClientNotification.hCatClient.createTable(HCatCreateTableDesc.create(table).build());
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, events.size());
        HCatNotificationEvent event = events.get(0);
        Assert.assertEquals(((firstEventId) + 1), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(HCAT_CREATE_TABLE_EVENT, event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertEquals("hcatcreatetable", event.getTableName());
        // Parse the message field
        CreateTableMessage createTableMessage = TestHCatClientNotification.md.getCreateTableMessage(event.getMessage());
        Assert.assertEquals(dbName, createTableMessage.getDB());
        Assert.assertEquals(tableName, createTableMessage.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), createTableMessage.getTableType());
        // fetch the table marked by the message and compare
        HCatTable createdTable = TestHCatClientNotification.hCatClient.getTable(dbName, tableName);
        Assert.assertTrue(createdTable.diff(table).equals(NO_DIFF));
    }

    // TODO - Currently no way to test alter table, as this interface doesn't support alter table
    @Test
    public void dropTable() throws Exception {
        String dbName = "default";
        String tableName = "hcatdroptable";
        HCatTable table = new HCatTable(dbName, tableName);
        table.cols(Arrays.asList(new org.apache.hive.hcatalog.data.schema.HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
        TestHCatClientNotification.hCatClient.createTable(HCatCreateTableDesc.create(table).build());
        TestHCatClientNotification.hCatClient.dropTable(dbName, tableName, false);
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, events.size());
        HCatNotificationEvent event = events.get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(HCAT_DROP_TABLE_EVENT, event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertEquals(tableName, event.getTableName());
        DropTableMessage dropTableMessage = TestHCatClientNotification.md.getDropTableMessage(event.getMessage());
        Assert.assertEquals(dbName, dropTableMessage.getDB());
        Assert.assertEquals(tableName, dropTableMessage.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), dropTableMessage.getTableType());
    }

    @Test
    public void addPartition() throws Exception {
        String dbName = "default";
        String tableName = "hcataddparttable";
        String partColName = "pc";
        HCatTable table = new HCatTable(dbName, tableName);
        table.partCol(new org.apache.hive.hcatalog.data.schema.HCatFieldSchema(partColName, TypeInfoFactory.stringTypeInfo, ""));
        table.cols(Arrays.asList(new org.apache.hive.hcatalog.data.schema.HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
        TestHCatClientNotification.hCatClient.createTable(HCatCreateTableDesc.create(table).build());
        String partName = "testpart";
        Map<String, String> partSpec = new HashMap<String, String>(1);
        partSpec.put(partColName, partName);
        HCatPartition part = new HCatPartition(table, partSpec, null);
        TestHCatClientNotification.hCatClient.addPartition(HCatAddPartitionDesc.create(part).build());
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(2, events.size());
        HCatNotificationEvent event = events.get(1);
        Assert.assertEquals(((firstEventId) + 2), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(HCAT_ADD_PARTITION_EVENT, event.getEventType());
        Assert.assertEquals("default", event.getDbName());
        Assert.assertEquals(tableName, event.getTableName());
        // Parse the message field
        AddPartitionMessage addPartitionMessage = TestHCatClientNotification.md.getAddPartitionMessage(event.getMessage());
        Assert.assertEquals(dbName, addPartitionMessage.getDB());
        Assert.assertEquals(tableName, addPartitionMessage.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), addPartitionMessage.getTableType());
        List<Map<String, String>> ptndescs = addPartitionMessage.getPartitions();
        // fetch the partition referred to by the message and compare
        HCatPartition addedPart = TestHCatClientNotification.hCatClient.getPartition(dbName, tableName, ptndescs.get(0));
        Assert.assertEquals(part.getDatabaseName(), addedPart.getDatabaseName());
        Assert.assertEquals(part.getTableName(), addedPart.getTableName());
        Assert.assertEquals(part.getValues(), addedPart.getValues());
        Assert.assertEquals(part.getColumns(), addedPart.getColumns());
        Assert.assertEquals(part.getPartColumns(), addedPart.getPartColumns());
        Assert.assertEquals(part.getLocation(), addedPart.getLocation());
    }

    // TODO - currently no way to test alter partition, as HCatClient doesn't support it.
    @Test
    public void dropPartition() throws Exception {
        String dbName = "default";
        String tableName = "hcatdropparttable";
        String partColName = "pc";
        HCatTable table = new HCatTable(dbName, tableName);
        table.partCol(new org.apache.hive.hcatalog.data.schema.HCatFieldSchema(partColName, TypeInfoFactory.stringTypeInfo, ""));
        table.cols(Arrays.asList(new org.apache.hive.hcatalog.data.schema.HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
        TestHCatClientNotification.hCatClient.createTable(HCatCreateTableDesc.create(table).build());
        String partName = "testpart";
        Map<String, String> partSpec = new HashMap<String, String>(1);
        partSpec.put(partColName, partName);
        TestHCatClientNotification.hCatClient.addPartition(HCatAddPartitionDesc.create(new HCatPartition(table, partSpec, null)).build());
        TestHCatClientNotification.hCatClient.dropPartitions(dbName, tableName, partSpec, false);
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(3, events.size());
        HCatNotificationEvent event = events.get(2);
        Assert.assertEquals(((firstEventId) + 3), event.getEventId());
        Assert.assertTrue(((event.getEventTime()) >= (startTime)));
        Assert.assertEquals(HCAT_DROP_PARTITION_EVENT, event.getEventType());
        Assert.assertEquals(dbName, event.getDbName());
        Assert.assertEquals(tableName, event.getTableName());
        // Parse the message field
        DropPartitionMessage dropPartitionMessage = TestHCatClientNotification.md.getDropPartitionMessage(event.getMessage());
        Assert.assertEquals(dbName, dropPartitionMessage.getDB());
        Assert.assertEquals(tableName, dropPartitionMessage.getTable());
        Assert.assertEquals(MANAGED_TABLE.toString(), dropPartitionMessage.getTableType());
        List<Map<String, String>> droppedPartSpecs = dropPartitionMessage.getPartitions();
        Assert.assertNotNull(droppedPartSpecs);
        Assert.assertEquals(1, droppedPartSpecs.size());
        Assert.assertEquals(partSpec, droppedPartSpecs.get(0));
    }

    @Test
    public void getOnlyMaxEvents() throws Exception {
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatdb1").build());
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatdb2").build());
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatdb3").build());
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 2, null);
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(((firstEventId) + 1), events.get(0).getEventId());
        Assert.assertEquals(((firstEventId) + 2), events.get(1).getEventId());
    }

    @Test
    public void filter() throws Exception {
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatf1").build());
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatf2").build());
        TestHCatClientNotification.hCatClient.dropDatabase("hcatf2", false, RESTRICT);
        IMetaStoreClient.NotificationFilter filter = new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent event) {
                return event.getEventType().equals(HCAT_DROP_DATABASE_EVENT);
            }
        };
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 0, filter);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(((firstEventId) + 3), events.get(0).getEventId());
    }

    @Test
    public void filterWithMax() throws Exception {
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatm1").build());
        TestHCatClientNotification.hCatClient.createDatabase(HCatCreateDBDesc.create("hcatm2").build());
        TestHCatClientNotification.hCatClient.dropDatabase("hcatm2", false, RESTRICT);
        IMetaStoreClient.NotificationFilter filter = new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent event) {
                return event.getEventType().equals(HCAT_CREATE_DATABASE_EVENT);
            }
        };
        List<HCatNotificationEvent> events = TestHCatClientNotification.hCatClient.getNextNotification(firstEventId, 1, filter);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(((firstEventId) + 1), events.get(0).getEventId());
    }
}

