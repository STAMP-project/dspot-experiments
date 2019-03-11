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
package org.apache.hadoop.yarn.server.timelineservice.storage;


import EntityTableRW.DEFAULT_TABLE_NAME;
import EntityTableRW.TABLE_NAME_CONF_NAME;
import YarnConfiguration.DEFAULT_TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX;
import YarnConfiguration.TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX_NAME;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.BaseTableRW;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for checking different schema prefixes.
 */
public class TestHBaseTimelineStorageSchema {
    private static HBaseTestingUtility util;

    @Test
    public void createWithDefaultPrefix() throws IOException {
        Configuration hbaseConf = TestHBaseTimelineStorageSchema.util.getConfiguration();
        DataGeneratorForTest.createSchema(hbaseConf);
        Connection conn = null;
        conn = ConnectionFactory.createConnection(hbaseConf);
        Admin admin = conn.getAdmin();
        TableName entityTableName = BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME);
        Assert.assertTrue(admin.tableExists(entityTableName));
        Assert.assertTrue(entityTableName.getNameAsString().startsWith(DEFAULT_TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX));
        Table entityTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME));
        Assert.assertNotNull(entityTable);
        TableName flowRunTableName = BaseTableRW.getTableName(hbaseConf, FlowRunTableRW.TABLE_NAME_CONF_NAME, FlowRunTableRW.DEFAULT_TABLE_NAME);
        Assert.assertTrue(admin.tableExists(flowRunTableName));
        Assert.assertTrue(flowRunTableName.getNameAsString().startsWith(DEFAULT_TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX));
        Table flowRunTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, FlowRunTableRW.TABLE_NAME_CONF_NAME, FlowRunTableRW.DEFAULT_TABLE_NAME));
        Assert.assertNotNull(flowRunTable);
    }

    @Test
    public void createWithSetPrefix() throws IOException {
        Configuration hbaseConf = TestHBaseTimelineStorageSchema.util.getConfiguration();
        String prefix = "unit-test.";
        hbaseConf.set(TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX_NAME, prefix);
        DataGeneratorForTest.createSchema(hbaseConf);
        Connection conn = null;
        conn = ConnectionFactory.createConnection(hbaseConf);
        Admin admin = conn.getAdmin();
        TableName entityTableName = BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME);
        Assert.assertTrue(admin.tableExists(entityTableName));
        Assert.assertTrue(entityTableName.getNameAsString().startsWith(prefix));
        Table entityTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME));
        Assert.assertNotNull(entityTable);
        TableName flowRunTableName = BaseTableRW.getTableName(hbaseConf, FlowRunTableRW.TABLE_NAME_CONF_NAME, FlowRunTableRW.DEFAULT_TABLE_NAME);
        Assert.assertTrue(admin.tableExists(flowRunTableName));
        Assert.assertTrue(flowRunTableName.getNameAsString().startsWith(prefix));
        Table flowRunTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, FlowRunTableRW.TABLE_NAME_CONF_NAME, FlowRunTableRW.DEFAULT_TABLE_NAME));
        Assert.assertNotNull(flowRunTable);
        // create another set with a diff prefix
        hbaseConf.unset(TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX_NAME);
        prefix = "yet-another-unit-test.";
        hbaseConf.set(TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX_NAME, prefix);
        DataGeneratorForTest.createSchema(hbaseConf);
        entityTableName = BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME);
        Assert.assertTrue(admin.tableExists(entityTableName));
        Assert.assertTrue(entityTableName.getNameAsString().startsWith(prefix));
        entityTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME));
        Assert.assertNotNull(entityTable);
        flowRunTableName = BaseTableRW.getTableName(hbaseConf, FlowRunTableRW.TABLE_NAME_CONF_NAME, FlowRunTableRW.DEFAULT_TABLE_NAME);
        Assert.assertTrue(admin.tableExists(flowRunTableName));
        Assert.assertTrue(flowRunTableName.getNameAsString().startsWith(prefix));
        flowRunTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, FlowRunTableRW.TABLE_NAME_CONF_NAME, FlowRunTableRW.DEFAULT_TABLE_NAME));
        Assert.assertNotNull(flowRunTable);
        hbaseConf.unset(TIMELINE_SERVICE_HBASE_SCHEMA_PREFIX_NAME);
    }
}

