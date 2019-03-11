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
package org.apache.hive.hcatalog.cli;


import HCatConstants.HCAT_GROUP;
import HCatConstants.HCAT_PERMS;
import Warehouse.DEFAULT_DATABASE_NAME;
import java.io.FileNotFoundException;
import junit.framework.TestCase;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestPermsGrp extends TestCase {
    private boolean isServerRunning = false;

    private HiveConf hcatConf;

    private Warehouse clientWH;

    private HiveMetaStoreClient msc;

    private static final Logger LOG = LoggerFactory.getLogger(TestPermsGrp.class);

    public void testCustomPerms() throws Exception {
        String dbName = Warehouse.DEFAULT_DATABASE_NAME;
        String tblName = "simptbl";
        String typeName = "Person";
        try {
            // Lets first test for default permissions, this is the case when user specified nothing.
            Table tbl = getTable(dbName, tblName, typeName);
            msc.createTable(tbl);
            Database db = Hive.get(hcatConf).getDatabase(dbName);
            Path dfsPath = clientWH.getDefaultTablePath(db, tblName);
            cleanupTbl(dbName, tblName, typeName);
            // Next user did specify perms.
            int ret = callHCatCli(new String[]{ "-e", "create table simptbl (name string) stored as RCFILE", "-p", "rwx-wx---" });
            TestCase.assertEquals(ret, 0);
            dfsPath = clientWH.getDefaultTablePath(db, tblName);
            TestCase.assertEquals(FsPermission.valueOf("drwx-wx---"), dfsPath.getFileSystem(hcatConf).getFileStatus(dfsPath).getPermission());
            cleanupTbl(dbName, tblName, typeName);
            // User specified perms in invalid format.
            hcatConf.set(HCAT_PERMS, "rwx");
            // make sure create table fails.
            ret = callHCatCli(new String[]{ "-e", "create table simptbl (name string) stored as RCFILE", "-p", "rwx" });
            TestCase.assertFalse((ret == 0));
            // No physical dir gets created.
            dfsPath = clientWH.getDefaultTablePath(db, tblName);
            try {
                dfsPath.getFileSystem(hcatConf).getFileStatus(dfsPath);
                TestCase.fail();
            } catch (Exception fnfe) {
                TestCase.assertTrue((fnfe instanceof FileNotFoundException));
            }
            // And no metadata gets created.
            try {
                msc.getTable(DEFAULT_DATABASE_NAME, tblName);
                TestCase.fail();
            } catch (Exception e) {
                TestCase.assertTrue((e instanceof NoSuchObjectException));
                TestCase.assertEquals("hive.default.simptbl table not found", e.getMessage());
            }
            // test for invalid group name
            hcatConf.set(HCAT_PERMS, "drw-rw-rw-");
            hcatConf.set(HCAT_GROUP, "THIS_CANNOT_BE_A_VALID_GRP_NAME_EVER");
            // create table must fail.
            ret = callHCatCli(new String[]{ "-e", "create table simptbl (name string) stored as RCFILE", "-p", "rw-rw-rw-", "-g", "THIS_CANNOT_BE_A_VALID_GRP_NAME_EVER" });
            TestCase.assertFalse((ret == 0));
            try {
                // no metadata should get created.
                msc.getTable(dbName, tblName);
                TestCase.fail();
            } catch (Exception e) {
                TestCase.assertTrue((e instanceof NoSuchObjectException));
                TestCase.assertEquals("hive.default.simptbl table not found", e.getMessage());
            }
            try {
                // neither dir should get created.
                dfsPath.getFileSystem(hcatConf).getFileStatus(dfsPath);
                TestCase.fail();
            } catch (Exception e) {
                TestCase.assertTrue((e instanceof FileNotFoundException));
            }
        } catch (Exception e) {
            TestPermsGrp.LOG.error("testCustomPerms failed.", e);
            throw e;
        }
    }

    private SecurityManager securityManager;
}

