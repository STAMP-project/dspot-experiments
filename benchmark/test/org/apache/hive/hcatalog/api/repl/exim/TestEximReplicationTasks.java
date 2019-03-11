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
package org.apache.hive.hcatalog.api.repl.exim;


import com.google.common.base.Function;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.hive.hcatalog.api.HCatNotificationEvent;
import org.apache.hive.hcatalog.api.repl.ReplicationTask;
import org.apache.hive.hcatalog.api.repl.StagingDirectoryProvider;
import org.apache.hive.hcatalog.common.HCatConstants;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;
import org.apache.hive.hcatalog.messaging.MessageFactory;
import org.junit.Assert;
import org.junit.Test;


public class TestEximReplicationTasks {
    private static MessageFactory msgFactory = MessageFactory.getInstance();

    private static StagingDirectoryProvider stagingDirectoryProvider = new StagingDirectoryProvider.TrivialImpl("/tmp", "/");

    private static HCatClient client;

    // Dummy mapping used for all db and table name mappings
    static Function<String, String> debugMapping = new Function<String, String>() {
        @Nullable
        @Override
        public String apply(@Nullable
        String s) {
            if (s == null) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder(s);
                return (sb.toString()) + (sb.reverse().toString());
            }
        }
    };

    @Test
    public void testDebugMapper() {
        Assert.assertEquals("BlahhalB", TestEximReplicationTasks.debugMapping.apply("Blah"));
        Assert.assertEquals(null, TestEximReplicationTasks.debugMapping.apply(null));
        Assert.assertEquals("", TestEximReplicationTasks.debugMapping.apply(""));
    }

    @Test
    public void testCreateDb() {
        Database db = new Database();
        db.setName("testdb");
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_CREATE_DATABASE_EVENT, TestEximReplicationTasks.msgFactory.buildCreateDatabaseMessage(db).toString());
        event.setDbName(db.getName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyCreateDbReplicationTask(rtask);// CREATE DB currently replicated as Noop.

    }

    @Test
    public void testDropDb() throws IOException {
        Database db = new Database();
        db.setName("testdb");
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_DROP_DATABASE_EVENT, TestEximReplicationTasks.msgFactory.buildCreateDatabaseMessage(db).toString());
        event.setDbName(db.getName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyDropDbReplicationTask(rtask);
    }

    @Test
    public void testCreateTable() throws IOException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_CREATE_TABLE_EVENT, TestEximReplicationTasks.msgFactory.buildCreateTableMessage(t).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyCreateTableReplicationTask(rtask);
    }

    @Test
    public void testDropTable() throws IOException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_DROP_TABLE_EVENT, TestEximReplicationTasks.msgFactory.buildDropTableMessage(t).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyDropTableReplicationTask(rtask);
    }

    @Test
    public void testAlterTable() throws IOException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_ALTER_TABLE_EVENT, TestEximReplicationTasks.msgFactory.buildAlterTableMessage(t, t, t.getWriteId()).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyAlterTableReplicationTask(rtask);
    }

    @Test
    public void testAddPartition() throws IOException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        List<FieldSchema> pkeys = HCatSchemaUtils.getFieldSchemas(HCatSchemaUtils.getHCatSchema("a:int,b:string").getFields());
        t.setPartitionKeys(pkeys);
        List<Partition> addedPtns = new ArrayList<Partition>();
        addedPtns.add(TestEximReplicationTasks.createPtn(t, Arrays.asList("120", "abc")));
        addedPtns.add(TestEximReplicationTasks.createPtn(t, Arrays.asList("201", "xyz")));
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_ADD_PARTITION_EVENT, TestEximReplicationTasks.msgFactory.buildAddPartitionMessage(t, addedPtns.iterator()).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyAddPartitionReplicationTask(rtask, t, addedPtns);
    }

    @Test
    public void testDropPartition() throws HCatException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        List<FieldSchema> pkeys = HCatSchemaUtils.getFieldSchemas(HCatSchemaUtils.getHCatSchema("a:int,b:string").getFields());
        t.setPartitionKeys(pkeys);
        Partition p = TestEximReplicationTasks.createPtn(t, Arrays.asList("102", "lmn"));
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_DROP_PARTITION_EVENT, TestEximReplicationTasks.msgFactory.buildDropPartitionMessage(t, Collections.singletonList(p).iterator()).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyDropPartitionReplicationTask(rtask, t, p);
    }

    @Test
    public void testAlterPartition() throws HCatException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        List<FieldSchema> pkeys = HCatSchemaUtils.getFieldSchemas(HCatSchemaUtils.getHCatSchema("a:int,b:string").getFields());
        t.setPartitionKeys(pkeys);
        Partition p = TestEximReplicationTasks.createPtn(t, Arrays.asList("102", "lmn"));
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_ALTER_PARTITION_EVENT, TestEximReplicationTasks.msgFactory.buildAlterPartitionMessage(t, p, p, p.getWriteId()).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyAlterPartitionReplicationTask(rtask, t, p);
    }

    @Test
    public void testInsert() throws HCatException {
        Table t = new Table();
        t.setDbName("testdb");
        t.setTableName("testtable");
        List<FieldSchema> pkeys = HCatSchemaUtils.getFieldSchemas(HCatSchemaUtils.getHCatSchema("a:int,b:string").getFields());
        t.setPartitionKeys(pkeys);
        Partition p = TestEximReplicationTasks.createPtn(t, Arrays.asList("102", "lmn"));
        List<String> files = Arrays.asList("/tmp/test123");
        NotificationEvent event = new NotificationEvent(TestEximReplicationTasks.getEventId(), TestEximReplicationTasks.getTime(), HCatConstants.HCAT_INSERT_EVENT, TestEximReplicationTasks.msgFactory.buildInsertMessage(t.getDbName(), t.getTableName(), TestEximReplicationTasks.getPtnDesc(t, p), files).toString());
        event.setDbName(t.getDbName());
        event.setTableName(t.getTableName());
        HCatNotificationEvent hev = new HCatNotificationEvent(event);
        ReplicationTask rtask = ReplicationTask.create(TestEximReplicationTasks.client, hev);
        Assert.assertEquals(hev.toString(), rtask.getEvent().toString());
        TestEximReplicationTasks.verifyInsertReplicationTask(rtask, t, p);
    }
}

