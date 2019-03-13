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
package org.apache.hadoop.hbase.security.access;


import Action.ADMIN;
import Action.CREATE;
import Action.EXEC;
import Action.READ;
import Action.WRITE;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.security.access.Permission.Action;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTests.class, SmallTests.class })
public class TestPermissionBuilder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPermissionBuilder.class);

    @Test
    public void testBuildGlobalPermission() {
        // check global permission with empty action
        Permission permission = Permission.newBuilder().build();
        Assert.assertTrue((permission instanceof GlobalPermission));
        Assert.assertEquals(0, permission.getActions().length);
        // check global permission with ADMIN action
        permission = Permission.newBuilder().withActions(ADMIN).build();
        Assert.assertTrue((permission instanceof GlobalPermission));
        Assert.assertEquals(1, permission.getActions().length);
        Assert.assertTrue(((permission.getActions()[0]) == (Action.ADMIN)));
        byte[] qualifier = Bytes.toBytes("q");
        try {
            permission = Permission.newBuilder().withQualifier(qualifier).withActions(CREATE, READ).build();
            Assert.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // catch NPE because set family but table name is null
        }
    }

    @Test
    public void testBuildNamespacePermission() {
        String namespace = "ns";
        // check namespace permission with CREATE and READ actions
        Permission permission = Permission.newBuilder(namespace).withActions(CREATE, READ).build();
        Assert.assertTrue((permission instanceof NamespacePermission));
        NamespacePermission namespacePermission = ((NamespacePermission) (permission));
        Assert.assertEquals(namespace, namespacePermission.getNamespace());
        Assert.assertEquals(2, permission.getActions().length);
        Assert.assertEquals(READ, permission.getActions()[0]);
        Assert.assertEquals(CREATE, permission.getActions()[1]);
        byte[] family = Bytes.toBytes("f");
        try {
            permission = Permission.newBuilder(namespace).withFamily(family).withActions(CREATE, READ).build();
            Assert.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // catch NPE because set family but table name is null
        }
    }

    @Test
    public void testBuildTablePermission() {
        TableName tableName = TableName.valueOf("ns", "table");
        byte[] family = Bytes.toBytes("f");
        byte[] qualifier = Bytes.toBytes("q");
        // check table permission without family or qualifier
        Permission permission = Permission.newBuilder(tableName).withActions(WRITE, READ).build();
        Assert.assertTrue((permission instanceof TablePermission));
        Assert.assertEquals(2, permission.getActions().length);
        Assert.assertEquals(READ, permission.getActions()[0]);
        Assert.assertEquals(WRITE, permission.getActions()[1]);
        TablePermission tPerm = ((TablePermission) (permission));
        Assert.assertEquals(tableName, tPerm.getTableName());
        Assert.assertEquals(null, tPerm.getFamily());
        Assert.assertEquals(null, tPerm.getQualifier());
        // check table permission with family
        permission = Permission.newBuilder(tableName).withFamily(family).withActions(EXEC).build();
        Assert.assertTrue((permission instanceof TablePermission));
        Assert.assertEquals(1, permission.getActions().length);
        Assert.assertEquals(EXEC, permission.getActions()[0]);
        tPerm = ((TablePermission) (permission));
        Assert.assertEquals(tableName, tPerm.getTableName());
        Assert.assertTrue(Bytes.equals(family, tPerm.getFamily()));
        Assert.assertTrue(Bytes.equals(null, tPerm.getQualifier()));
        // check table permission with family and qualifier
        permission = Permission.newBuilder(tableName).withFamily(family).withQualifier(qualifier).build();
        Assert.assertTrue((permission instanceof TablePermission));
        Assert.assertEquals(0, permission.getActions().length);
        tPerm = ((TablePermission) (permission));
        Assert.assertEquals(tableName, tPerm.getTableName());
        Assert.assertTrue(Bytes.equals(family, tPerm.getFamily()));
        Assert.assertTrue(Bytes.equals(qualifier, tPerm.getQualifier()));
    }
}

