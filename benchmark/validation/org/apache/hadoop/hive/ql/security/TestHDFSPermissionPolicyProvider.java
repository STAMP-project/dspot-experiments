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
package org.apache.hadoop.hive.ql.security;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.ql.security.authorization.HDFSPermissionPolicyProvider;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject.HivePrivilegeObjectType;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveResourceACLs;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for privilege synchronizer for storage based authorizer
 */
public class TestHDFSPermissionPolicyProvider {
    private static MiniDFSCluster mDfs;

    private static IMetaStoreClient client;

    private static Configuration conf;

    private static String defaultTbl1Loc;

    private static String defaultTbl2Loc;

    private static String db1Loc;

    private static String db1Tbl1Loc;

    @Test
    public void testPolicyProvider() throws Exception {
        HDFSPermissionPolicyProvider policyProvider = new HDFSPermissionPolicyProvider(TestHDFSPermissionPolicyProvider.conf);
        FileSystem fs = FileSystem.get(TestHDFSPermissionPolicyProvider.conf);
        fs.setOwner(new Path(TestHDFSPermissionPolicyProvider.defaultTbl1Loc), "user1", "group1");
        fs.setOwner(new Path(TestHDFSPermissionPolicyProvider.defaultTbl2Loc), "user1", "group1");
        fs.setOwner(new Path(TestHDFSPermissionPolicyProvider.db1Loc), "user1", "group1");
        fs.setOwner(new Path(TestHDFSPermissionPolicyProvider.db1Tbl1Loc), "user1", "group1");
        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.defaultTbl1Loc), new FsPermission("444"));// r--r--r--

        HiveResourceACLs acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "default", "tbl1"));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertTrue(acls.getUserPermissions().keySet().contains("user1"));
        Assert.assertEquals(acls.getGroupPermissions().size(), 2);
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("group1"));
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("public"));
        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.defaultTbl1Loc), new FsPermission("440"));// r--r-----

        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "default", "tbl1"));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertEquals(acls.getUserPermissions().keySet().iterator().next(), "user1");
        Assert.assertEquals(acls.getGroupPermissions().size(), 1);
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("group1"));
        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.defaultTbl1Loc), new FsPermission("404"));// r-----r--

        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "default", "tbl1"));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertTrue(acls.getUserPermissions().keySet().contains("user1"));
        Assert.assertEquals(acls.getGroupPermissions().size(), 1);
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("public"));
        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.defaultTbl1Loc), new FsPermission("400"));// r--------

        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "default", "tbl1"));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertTrue(acls.getUserPermissions().keySet().contains("user1"));
        Assert.assertEquals(acls.getGroupPermissions().size(), 0);
        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.defaultTbl1Loc), new FsPermission("004"));// ------r--

        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.defaultTbl2Loc), new FsPermission("777"));// rwxrwxrwx

        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "default", "tbl1"));
        Assert.assertEquals(acls.getUserPermissions().size(), 0);
        Assert.assertEquals(acls.getGroupPermissions().size(), 1);
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("public"));
        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "default", "tbl2"));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertTrue(acls.getUserPermissions().keySet().contains("user1"));
        Assert.assertEquals(acls.getGroupPermissions().size(), 2);
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("group1"));
        Assert.assertTrue(acls.getGroupPermissions().keySet().contains("public"));
        fs.setPermission(new Path(TestHDFSPermissionPolicyProvider.db1Loc), new FsPermission("400"));// ------r--

        fs.delete(new Path(TestHDFSPermissionPolicyProvider.db1Tbl1Loc), true);
        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.DATABASE, "db1", null));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertTrue(acls.getUserPermissions().keySet().contains("user1"));
        Assert.assertEquals(acls.getGroupPermissions().size(), 0);
        acls = policyProvider.getResourceACLs(new org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject(HivePrivilegeObjectType.TABLE_OR_VIEW, "db1", "tbl1"));
        Assert.assertEquals(acls.getUserPermissions().size(), 1);
        Assert.assertTrue(acls.getUserPermissions().keySet().contains("user1"));
        Assert.assertEquals(acls.getGroupPermissions().size(), 0);
    }
}

