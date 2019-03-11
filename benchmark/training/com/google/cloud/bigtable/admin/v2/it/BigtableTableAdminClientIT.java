/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.admin.v2.it;


import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.ColumnFamily;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.GCRules;
import com.google.cloud.bigtable.admin.v2.models.ModifyColumnFamiliesRequest;
import com.google.cloud.bigtable.admin.v2.models.Table;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Duration;


public class BigtableTableAdminClientIT {
    private static final Logger LOGGER = Logger.getLogger(BigtableTableAdminClientIT.class.getName());

    private static final String PROJECT_PROPERTY_NAME = "bigtable.project";

    private static final String INSTANCE_PROPERTY_NAME = "bigtable.instance";

    private static List<String> missingProperties;

    private static BigtableTableAdminClient tableAdmin;

    private static String prefix;

    @Test
    public void createTable() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminCreateTest");
        CreateTableRequest createTableReq = CreateTableRequest.of(tableId).addFamily("cf1").addFamily("cf2", GCRules.GCRULES.maxVersions(10)).addSplit(ByteString.copyFromUtf8("b")).addSplit(ByteString.copyFromUtf8("q"));
        try {
            Table tableResponse = BigtableTableAdminClientIT.tableAdmin.createTable(createTableReq);
            Assert.assertNotNull(tableResponse);
            Assert.assertEquals(tableId, tableResponse.getId());
            Map<String, ColumnFamily> columnFamilyById = Maps.newHashMap();
            for (ColumnFamily columnFamily : tableResponse.getColumnFamilies()) {
                columnFamilyById.put(columnFamily.getId(), columnFamily);
            }
            Assert.assertEquals(2, tableResponse.getColumnFamilies().size());
            Assert.assertFalse(columnFamilyById.get("cf1").hasGCRule());
            Assert.assertTrue(columnFamilyById.get("cf2").hasGCRule());
            Assert.assertEquals(10, getMaxVersions());
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }

    @Test
    public void modifyFamilies() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminModifyFamTest");
        ModifyColumnFamiliesRequest modifyFamiliesReq = ModifyColumnFamiliesRequest.of(tableId);
        modifyFamiliesReq.addFamily("mf1").addFamily("mf2", GCRules.GCRULES.maxAge(Duration.ofSeconds(1000, 20000))).updateFamily("mf1", GCRules.GCRULES.union().rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(100))).rule(GCRules.GCRULES.maxVersions(1))).addFamily("mf3", GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(2000))).rule(GCRules.GCRULES.maxVersions(10))).addFamily("mf4", GCRules.GCRULES.intersection().rule(GCRules.GCRULES.maxAge(Duration.ofSeconds(360)))).addFamily("mf5").addFamily("mf6").dropFamily("mf5").dropFamily("mf6").addFamily("mf7");
        try {
            BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
            Table tableResponse = BigtableTableAdminClientIT.tableAdmin.modifyFamilies(modifyFamiliesReq);
            Map<String, ColumnFamily> columnFamilyById = Maps.newHashMap();
            for (ColumnFamily columnFamily : tableResponse.getColumnFamilies()) {
                columnFamilyById.put(columnFamily.getId(), columnFamily);
            }
            Assert.assertEquals(5, columnFamilyById.size());
            Assert.assertNotNull(columnFamilyById.get("mf1"));
            Assert.assertNotNull(columnFamilyById.get("mf2"));
            Assert.assertEquals(2, getRulesList().size());
            Assert.assertEquals(1000, getMaxAge().getSeconds());
            Assert.assertEquals(20000, getMaxAge().getNano());
            Assert.assertEquals(2, getRulesList().size());
            Assert.assertEquals(360, getMaxAge().getSeconds());
            Assert.assertNotNull(columnFamilyById.get("mf7"));
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }

    @Test
    public void deleteTable() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminDeleteTest");
        BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
        BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
    }

    @Test
    public void getTable() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminGetTest");
        try {
            BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
            Table tableResponse = BigtableTableAdminClientIT.tableAdmin.getTable(tableId);
            Assert.assertNotNull(tableResponse);
            Assert.assertEquals(tableId, tableResponse.getId());
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }

    @Test
    public void listTables() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminListTest");
        try {
            BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
            List<String> tables = BigtableTableAdminClientIT.tableAdmin.listTables();
            Assert.assertNotNull(tables);
            Assert.assertFalse("List tables did not return any tables", tables.isEmpty());
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }

    @Test
    public void listTablesAsync() throws Exception {
        String tableId = BigtableTableAdminClientIT.getTableId("adminListTest");
        try {
            BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
            List<String> tables = BigtableTableAdminClientIT.tableAdmin.listTablesAsync().get();
            Assert.assertNotNull(tables);
            Assert.assertFalse("List tables did not return any tables", tables.isEmpty());
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }

    @Test
    public void dropRowRange() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminDropRowrangeTest");
        try {
            BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
            BigtableTableAdminClientIT.tableAdmin.dropRowRange(tableId, "rowPrefix");
            BigtableTableAdminClientIT.tableAdmin.dropAllRows(tableId);
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }

    @Test
    public void awaitReplication() {
        String tableId = BigtableTableAdminClientIT.getTableId("adminConsistencyTest");
        try {
            BigtableTableAdminClientIT.tableAdmin.createTable(CreateTableRequest.of(tableId));
            BigtableTableAdminClientIT.tableAdmin.awaitReplication(tableId);
        } finally {
            BigtableTableAdminClientIT.tableAdmin.deleteTable(tableId);
        }
    }
}

