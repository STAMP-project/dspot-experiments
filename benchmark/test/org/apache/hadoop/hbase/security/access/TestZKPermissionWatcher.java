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


import Permission.Action;
import Permission.Action.READ;
import Permission.Action.WRITE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hbase.thirdparty.com.google.common.collect.ArrayListMultimap;
import org.apache.hbase.thirdparty.com.google.common.collect.ListMultimap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TablePermission.Action.READ;


/**
 * Test the reading and writing of access permissions to and from zookeeper.
 */
@Category({ SecurityTests.class, LargeTests.class })
public class TestZKPermissionWatcher {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKPermissionWatcher.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKPermissionWatcher.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static AuthManager AUTH_A;

    private static AuthManager AUTH_B;

    private static final Abortable ABORTABLE = new Abortable() {
        private final AtomicBoolean abort = new AtomicBoolean(false);

        @Override
        public void abort(String why, Throwable e) {
            TestZKPermissionWatcher.LOG.info(why, e);
            abort.set(true);
        }

        @Override
        public boolean isAborted() {
            return abort.get();
        }
    };

    private static TableName TEST_TABLE = TableName.valueOf("perms_test");

    @Test
    public void testPermissionsWatcher() throws Exception {
        Configuration conf = TestZKPermissionWatcher.UTIL.getConfiguration();
        User george = User.createUserForTesting(conf, "george", new String[]{  });
        User hubert = User.createUserForTesting(conf, "hubert", new String[]{  });
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        // update ACL: george RW
        List<UserPermission> acl = new ArrayList<>(1);
        acl.add(new UserPermission(george.getShortName(), TestZKPermissionWatcher.TEST_TABLE, Action.READ, Action.WRITE));
        ListMultimap<String, UserPermission> multimap = ArrayListMultimap.create();
        multimap.putAll(george.getShortName(), acl);
        byte[] serialized = AccessControlLists.writePermissionsAsBytes(multimap, conf);
        TestZKPermissionWatcher.AUTH_A.getZKPermissionWatcher().writeToZookeeper(TestZKPermissionWatcher.TEST_TABLE.getName(), serialized);
        final long mtimeB = TestZKPermissionWatcher.AUTH_B.getMTime();
        // Wait for the update to propagate
        TestZKPermissionWatcher.UTIL.waitFor(10000, 100, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestZKPermissionWatcher.AUTH_B.getMTime()) > mtimeB;
            }
        });
        Thread.sleep(1000);
        // check it
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        // update ACL: hubert R
        List<UserPermission> acl2 = new ArrayList<>(1);
        acl2.add(new UserPermission(hubert.getShortName(), TestZKPermissionWatcher.TEST_TABLE, READ));
        final long mtimeA = TestZKPermissionWatcher.AUTH_A.getMTime();
        multimap.putAll(hubert.getShortName(), acl2);
        byte[] serialized2 = AccessControlLists.writePermissionsAsBytes(multimap, conf);
        TestZKPermissionWatcher.AUTH_B.getZKPermissionWatcher().writeToZookeeper(TestZKPermissionWatcher.TEST_TABLE.getName(), serialized2);
        // Wait for the update to propagate
        waitFor(10000, 100, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestZKPermissionWatcher.AUTH_A.getMTime()) > mtimeA;
            }
        });
        Thread.sleep(1000);
        // check it
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(george, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_A.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, WRITE));
        Assert.assertTrue(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, READ));
        Assert.assertFalse(TestZKPermissionWatcher.AUTH_B.authorizeUserTable(hubert, TestZKPermissionWatcher.TEST_TABLE, WRITE));
    }
}

