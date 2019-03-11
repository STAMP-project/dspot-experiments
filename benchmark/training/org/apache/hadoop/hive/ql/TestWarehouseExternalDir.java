/**
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql;


import ConfVars.HIVESTATSCOLAUTOGATHER;
import ConfVars.HIVE_METASTORE_WAREHOUSE_EXTERNAL;
import ConfVars.HIVE_SERVER2_LOGGING_OPERATION_ENABLED;
import ConfVars.HIVE_SUPPORT_CONCURRENCY;
import ConfVars.METASTOREWAREHOUSE;
import MiniHS2.Builder;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.Table;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestWarehouseExternalDir {
    private static final Logger LOG = LoggerFactory.getLogger(TestWarehouseExternalDir.class);

    static MiniHS2 miniHS2;

    static Hive db;

    static Connection conn;

    String whRootExternal = "/wh_ext";

    Path whRootExternalPath;

    Path whRootManagedPath;

    FileSystem fs;

    public TestWarehouseExternalDir() throws Exception {
        HiveConf conf = new HiveConf();
        // Specify the external warehouse root
        conf.setVar(HIVE_METASTORE_WAREHOUSE_EXTERNAL, whRootExternal);
        // Settings borrowed from TestJdbcWithMiniHS2
        conf.setBoolVar(HIVE_SUPPORT_CONCURRENCY, false);
        conf.setBoolVar(HIVE_SERVER2_LOGGING_OPERATION_ENABLED, false);
        conf.setBoolVar(HIVESTATSCOLAUTOGATHER, false);
        MiniHS2.Builder builder = new MiniHS2.Builder().withConf(conf).cleanupLocalDirOnStartup(true).withMiniMR().withRemoteMetastore();
        TestWarehouseExternalDir.miniHS2 = builder.build();
        Map<String, String> confOverlay = new HashMap<String, String>();
        TestWarehouseExternalDir.miniHS2.start(confOverlay);
        HiveConf dbConf = TestWarehouseExternalDir.miniHS2.getHiveConf();
        TestWarehouseExternalDir.db = Hive.get(dbConf);
        fs = TestWarehouseExternalDir.miniHS2.getDfs().getFileSystem();
        whRootExternalPath = fs.makeQualified(new Path(whRootExternal));
        whRootManagedPath = fs.makeQualified(new Path(dbConf.getVar(METASTOREWAREHOUSE)));
        TestWarehouseExternalDir.LOG.info("fs: {}", TestWarehouseExternalDir.miniHS2.getDfs().getFileSystem().getUri());
        TestWarehouseExternalDir.LOG.info("warehouse location: {}", whRootManagedPath);
        TestWarehouseExternalDir.LOG.info("whRootExternalPath: {}", whRootExternalPath);
        TestWarehouseExternalDir.conn = TestWarehouseExternalDir.getConnection();
        try (Statement stmt = TestWarehouseExternalDir.conn.createStatement()) {
            stmt.execute("create database if not exists twed_db1");
        }
    }

    @Test
    public void testManagedPaths() throws Exception {
        try (Statement stmt = TestWarehouseExternalDir.conn.createStatement()) {
            // Confirm default managed table paths
            stmt.execute("create table default.twed_1(c1 string)");
            Table tab = TestWarehouseExternalDir.db.getTable("default", "twed_1");
            TestWarehouseExternalDir.checkTableLocation(tab, new Path(whRootManagedPath, "twed_1"));
            stmt.execute("create table twed_db1.tab1(c1 string, c2 string)");
            tab = TestWarehouseExternalDir.db.getTable("twed_db1", "tab1");
            TestWarehouseExternalDir.checkTableLocation(tab, new Path(new Path(whRootManagedPath, "twed_db1.db"), "tab1"));
        }
    }

    @Test
    public void testExternalDefaultPaths() throws Exception {
        try (Statement stmt = TestWarehouseExternalDir.conn.createStatement()) {
            stmt.execute("create external table default.twed_ext1(c1 string)");
            Table tab = TestWarehouseExternalDir.db.getTable("default", "twed_ext1");
            TestWarehouseExternalDir.checkTableLocation(tab, new Path(whRootExternalPath, "twed_ext1"));
            stmt.execute("create external table twed_db1.twed_ext2(c1 string)");
            tab = TestWarehouseExternalDir.db.getTable("twed_db1", "twed_ext2");
            TestWarehouseExternalDir.checkTableLocation(tab, new Path(new Path(whRootExternalPath, "twed_db1.db"), "twed_ext2"));
            stmt.execute("create external table default.twed_ext3 like default.twed_ext1");
            tab = TestWarehouseExternalDir.db.getTable("default", "twed_ext3");
            TestWarehouseExternalDir.checkTableLocation(tab, new Path(whRootExternalPath, "twed_ext3"));
            stmt.execute("create external table twed_db1.twed_ext4 like default.twed_ext1");
            tab = TestWarehouseExternalDir.db.getTable("twed_db1", "twed_ext4");
            TestWarehouseExternalDir.checkTableLocation(tab, new Path(new Path(whRootExternalPath, "twed_db1.db"), "twed_ext4"));
        }
    }
}

