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
package org.apache.hadoop.fs.permission;


import AclEntryScope.ACCESS;
import AclEntryScope.DEFAULT;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests covering basic functionality of the ACL objects.
 */
public class TestAcl {
    private static AclEntry ENTRY1;

    private static AclEntry ENTRY2;

    private static AclEntry ENTRY3;

    private static AclEntry ENTRY4;

    private static AclEntry ENTRY5;

    private static AclEntry ENTRY6;

    private static AclEntry ENTRY7;

    private static AclEntry ENTRY8;

    private static AclEntry ENTRY9;

    private static AclEntry ENTRY10;

    private static AclEntry ENTRY11;

    private static AclEntry ENTRY12;

    private static AclEntry ENTRY13;

    private static AclStatus STATUS1;

    private static AclStatus STATUS2;

    private static AclStatus STATUS3;

    private static AclStatus STATUS4;

    @Test
    public void testEntryEquals() {
        Assert.assertNotSame(TestAcl.ENTRY1, TestAcl.ENTRY2);
        Assert.assertNotSame(TestAcl.ENTRY1, TestAcl.ENTRY3);
        Assert.assertNotSame(TestAcl.ENTRY1, TestAcl.ENTRY4);
        Assert.assertNotSame(TestAcl.ENTRY2, TestAcl.ENTRY3);
        Assert.assertNotSame(TestAcl.ENTRY2, TestAcl.ENTRY4);
        Assert.assertNotSame(TestAcl.ENTRY3, TestAcl.ENTRY4);
        Assert.assertEquals(TestAcl.ENTRY1, TestAcl.ENTRY1);
        Assert.assertEquals(TestAcl.ENTRY2, TestAcl.ENTRY2);
        Assert.assertEquals(TestAcl.ENTRY1, TestAcl.ENTRY2);
        Assert.assertEquals(TestAcl.ENTRY2, TestAcl.ENTRY1);
        Assert.assertFalse(TestAcl.ENTRY1.equals(TestAcl.ENTRY3));
        Assert.assertFalse(TestAcl.ENTRY1.equals(TestAcl.ENTRY4));
        Assert.assertFalse(TestAcl.ENTRY3.equals(TestAcl.ENTRY4));
        Assert.assertFalse(TestAcl.ENTRY1.equals(null));
        Assert.assertFalse(TestAcl.ENTRY1.equals(new Object()));
    }

    @Test
    public void testEntryHashCode() {
        Assert.assertEquals(TestAcl.ENTRY1.hashCode(), TestAcl.ENTRY2.hashCode());
        Assert.assertFalse(((TestAcl.ENTRY1.hashCode()) == (TestAcl.ENTRY3.hashCode())));
        Assert.assertFalse(((TestAcl.ENTRY1.hashCode()) == (TestAcl.ENTRY4.hashCode())));
        Assert.assertFalse(((TestAcl.ENTRY3.hashCode()) == (TestAcl.ENTRY4.hashCode())));
    }

    @Test
    public void testEntryScopeIsAccessIfUnspecified() {
        Assert.assertEquals(ACCESS, TestAcl.ENTRY1.getScope());
        Assert.assertEquals(ACCESS, TestAcl.ENTRY2.getScope());
        Assert.assertEquals(ACCESS, TestAcl.ENTRY3.getScope());
        Assert.assertEquals(DEFAULT, TestAcl.ENTRY4.getScope());
    }

    @Test
    public void testStatusEquals() {
        Assert.assertNotSame(TestAcl.STATUS1, TestAcl.STATUS2);
        Assert.assertNotSame(TestAcl.STATUS1, TestAcl.STATUS3);
        Assert.assertNotSame(TestAcl.STATUS2, TestAcl.STATUS3);
        Assert.assertEquals(TestAcl.STATUS1, TestAcl.STATUS1);
        Assert.assertEquals(TestAcl.STATUS2, TestAcl.STATUS2);
        Assert.assertEquals(TestAcl.STATUS1, TestAcl.STATUS2);
        Assert.assertEquals(TestAcl.STATUS2, TestAcl.STATUS1);
        Assert.assertFalse(TestAcl.STATUS1.equals(TestAcl.STATUS3));
        Assert.assertFalse(TestAcl.STATUS2.equals(TestAcl.STATUS3));
        Assert.assertFalse(TestAcl.STATUS1.equals(null));
        Assert.assertFalse(TestAcl.STATUS1.equals(new Object()));
    }

    @Test
    public void testStatusHashCode() {
        Assert.assertEquals(TestAcl.STATUS1.hashCode(), TestAcl.STATUS2.hashCode());
        Assert.assertFalse(((TestAcl.STATUS1.hashCode()) == (TestAcl.STATUS3.hashCode())));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("user:user1:rwx", TestAcl.ENTRY1.toString());
        Assert.assertEquals("user:user1:rwx", TestAcl.ENTRY2.toString());
        Assert.assertEquals("group:group2:rw-", TestAcl.ENTRY3.toString());
        Assert.assertEquals("default:other::---", TestAcl.ENTRY4.toString());
        Assert.assertEquals("owner: owner1, group: group1, acl: {entries: [user:user1:rwx, group:group2:rw-, default:other::---], stickyBit: false}", TestAcl.STATUS1.toString());
        Assert.assertEquals("owner: owner1, group: group1, acl: {entries: [user:user1:rwx, group:group2:rw-, default:other::---], stickyBit: false}", TestAcl.STATUS2.toString());
        Assert.assertEquals("owner: owner2, group: group2, acl: {entries: [], stickyBit: true}", TestAcl.STATUS3.toString());
    }
}

