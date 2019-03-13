/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.metadata;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.druid.java.util.common.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import static org.junit.Assert.assertTrue;


public class SQLMetadataConnectorTest {
    @Rule
    public final TestDerbyConnector.DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private TestDerbyConnector connector;

    private MetadataStorageTablesConfig tablesConfig;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void testCreateTables() {
        final List<String> tables = new ArrayList<>();
        final String entryType = tablesConfig.getTaskEntryType();
        tables.add(tablesConfig.getConfigTable());
        tables.add(tablesConfig.getSegmentsTable());
        tables.add(tablesConfig.getRulesTable());
        tables.add(tablesConfig.getLockTable(entryType));
        tables.add(tablesConfig.getLogTable(entryType));
        tables.add(tablesConfig.getEntryTable(entryType));
        tables.add(tablesConfig.getAuditTable());
        tables.add(tablesConfig.getSupervisorTable());
        createSegmentTable();
        connector.createConfigTable();
        createRulesTable();
        createTaskTables();
        createAuditTable();
        createSupervisorsTable();
        getDBI().withHandle(new org.skife.jdbi.v2.tweak.HandleCallback<Void>() {
            @Override
            public Void withHandle(Handle handle) {
                for (String table : tables) {
                    assertTrue(StringUtils.format("table %s was not created!", table), connector.tableExists(handle, table));
                }
                return null;
            }
        });
        for (String table : tables) {
            dropTable(table);
        }
    }

    @Test
    public void testInsertOrUpdate() {
        final String tableName = "test";
        createConfigTable(tableName);
        Assert.assertNull(lookup(tableName, "name", "payload", "emperor"));
        connector.insertOrUpdate(tableName, "name", "payload", "emperor", StringUtils.toUtf8("penguin"));
        Assert.assertArrayEquals(StringUtils.toUtf8("penguin"), lookup(tableName, "name", "payload", "emperor"));
        connector.insertOrUpdate(tableName, "name", "payload", "emperor", StringUtils.toUtf8("penguin chick"));
        Assert.assertArrayEquals(StringUtils.toUtf8("penguin chick"), lookup(tableName, "name", "payload", "emperor"));
        dropTable(tableName);
    }

    static class TestSQLMetadataConnector extends SQLMetadataConnector {
        public TestSQLMetadataConnector(Supplier<MetadataStorageConnectorConfig> config, Supplier<MetadataStorageTablesConfig> tablesConfigSupplier) {
            super(config, tablesConfigSupplier);
        }

        @Override
        protected String getSerialType() {
            return null;
        }

        @Override
        protected int getStreamingFetchSize() {
            return 0;
        }

        @Override
        public String getQuoteString() {
            return null;
        }

        @Override
        public boolean tableExists(Handle handle, String tableName) {
            return false;
        }

        @Override
        public DBI getDBI() {
            return null;
        }

        @Override
        protected BasicDataSource getDatasource() {
            return super.getDatasource();
        }
    }

    @Test
    public void testBasicDataSourceCreation() throws Exception {
        MetadataStorageConnectorConfig config = getDbcpPropertiesFile(true, "host", 1234, "connectURI", "user", "{\"type\":\"default\",\"password\":\"nothing\"}", "nothing");
        SQLMetadataConnectorTest.TestSQLMetadataConnector testSQLMetadataConnector = new SQLMetadataConnectorTest.TestSQLMetadataConnector(Suppliers.ofInstance(config), Suppliers.ofInstance(tablesConfig));
        BasicDataSource dataSource = testSQLMetadataConnector.getDatasource();
        Assert.assertEquals(dataSource.getMaxConnLifetimeMillis(), 1200000);
        Assert.assertEquals(((long) (dataSource.getDefaultQueryTimeout())), 30000);
    }
}

