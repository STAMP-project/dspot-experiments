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
package org.apache.hive.service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.shims.HadoopShims.MiniDFSShim;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test HiveServer2 sends correct user name to remote MetaStore server for user impersonation.
 */
public class TestHS2ImpersonationWithRemoteMS {
    private static MiniHS2 miniHS2 = null;

    @Test
    public void testImpersonation() throws Exception {
        Assert.assertTrue("Test setup failed. MiniHS2 is not initialized", (((TestHS2ImpersonationWithRemoteMS.miniHS2) != null) && (TestHS2ImpersonationWithRemoteMS.miniHS2.isStarted())));
        Class.forName(MiniHS2.getJdbcDriverName());
        // Create two tables one as user "foo" and other as user "bar"
        Connection hs2Conn = DriverManager.getConnection(TestHS2ImpersonationWithRemoteMS.miniHS2.getJdbcURL(), "foo", null);
        Statement stmt = hs2Conn.createStatement();
        String tableName = "foo_table";
        stmt.execute(("drop table if exists " + tableName));
        stmt.execute((("create table " + tableName) + " (value string)"));
        stmt.close();
        hs2Conn.close();
        hs2Conn = DriverManager.getConnection(TestHS2ImpersonationWithRemoteMS.miniHS2.getJdbcURL(), "bar", null);
        stmt = hs2Conn.createStatement();
        tableName = "bar_table";
        stmt.execute(("drop table if exists " + tableName));
        stmt.execute((("create table " + tableName) + " (value string)"));
        stmt.close();
        hs2Conn.close();
        MiniDFSShim dfs = TestHS2ImpersonationWithRemoteMS.miniHS2.getDfs();
        FileSystem fs = dfs.getFileSystem();
        FileStatus[] files = fs.listStatus(TestHS2ImpersonationWithRemoteMS.miniHS2.getWareHouseDir());
        boolean fooTableValidated = false;
        boolean barTableValidated = false;
        for (FileStatus file : files) {
            final String name = file.getPath().getName();
            final String owner = file.getOwner();
            if (name.equals("foo_table")) {
                fooTableValidated = owner.equals("foo");
                Assert.assertTrue(String.format("User 'foo' table has wrong ownership '%s'", owner), fooTableValidated);
            } else
                if (name.equals("bar_table")) {
                    barTableValidated = owner.equals("bar");
                    Assert.assertTrue(String.format("User 'bar' table has wrong ownership '%s'", owner), barTableValidated);
                } else {
                    Assert.fail(String.format("Unexpected table directory '%s' in warehouse", name));
                }

            System.out.println(String.format("File: %s, Owner: %s", name, owner));
        }
        Assert.assertTrue("User 'foo' table not found in warehouse", fooTableValidated);
        Assert.assertTrue("User 'bar' table not found in warehouse", barTableValidated);
    }
}

