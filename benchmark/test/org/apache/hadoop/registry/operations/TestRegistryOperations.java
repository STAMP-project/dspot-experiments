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
package org.apache.hadoop.registry.operations;


import BindFlags.CREATE;
import BindFlags.OVERWRITE;
import PersistencePolicies.APPLICATION;
import PersistencePolicies.CONTAINER;
import PersistencePolicies.PERMANENT;
import YarnRegistryAttributes.YARN_ID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.PathIsNotEmptyDirectoryException;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.hadoop.registry.AbstractRegistryTest;
import org.apache.hadoop.registry.RegistryTestHelper;
import org.apache.hadoop.registry.client.binding.RegistryPathUtils;
import org.apache.hadoop.registry.client.binding.RegistryTypeUtils;
import org.apache.hadoop.registry.client.binding.RegistryUtils;
import org.apache.hadoop.registry.client.exceptions.NoRecordException;
import org.apache.hadoop.registry.client.types.RegistryPathStatus;
import org.apache.hadoop.registry.client.types.ServiceRecord;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRegistryOperations extends AbstractRegistryTest {
    protected static final Logger LOG = LoggerFactory.getLogger(TestRegistryOperations.class);

    @Test
    public void testPutGetServiceEntry() throws Throwable {
        ServiceRecord written = putExampleServiceEntry(RegistryTestHelper.ENTRY_PATH, 0, APPLICATION);
        ServiceRecord resolved = operations.resolve(RegistryTestHelper.ENTRY_PATH);
        RegistryTestHelper.validateEntry(resolved);
        RegistryTestHelper.assertMatches(written, resolved);
    }

    @Test
    public void testDeleteServiceEntry() throws Throwable {
        putExampleServiceEntry(RegistryTestHelper.ENTRY_PATH, 0);
        operations.delete(RegistryTestHelper.ENTRY_PATH, false);
    }

    @Test
    public void testDeleteNonexistentEntry() throws Throwable {
        operations.delete(RegistryTestHelper.ENTRY_PATH, false);
        operations.delete(RegistryTestHelper.ENTRY_PATH, true);
    }

    @Test
    public void testStat() throws Throwable {
        putExampleServiceEntry(RegistryTestHelper.ENTRY_PATH, 0);
        RegistryPathStatus stat = operations.stat(RegistryTestHelper.ENTRY_PATH);
        Assert.assertTrue(((stat.size) > 0));
        Assert.assertTrue(((stat.time) > 0));
        Assert.assertEquals(RegistryTestHelper.NAME, stat.path);
    }

    @Test
    public void testLsParent() throws Throwable {
        ServiceRecord written = putExampleServiceEntry(RegistryTestHelper.ENTRY_PATH, 0);
        RegistryPathStatus stat = operations.stat(RegistryTestHelper.ENTRY_PATH);
        List<String> children = operations.list(RegistryTestHelper.PARENT_PATH);
        Assert.assertEquals(1, children.size());
        Assert.assertEquals(RegistryTestHelper.NAME, children.get(0));
        Map<String, RegistryPathStatus> childStats = RegistryUtils.statChildren(operations, RegistryTestHelper.PARENT_PATH);
        Assert.assertEquals(1, childStats.size());
        Assert.assertEquals(stat, childStats.get(RegistryTestHelper.NAME));
        Map<String, ServiceRecord> records = RegistryUtils.extractServiceRecords(operations, RegistryTestHelper.PARENT_PATH, childStats.values());
        Assert.assertEquals(1, records.size());
        ServiceRecord record = records.get(RegistryTestHelper.ENTRY_PATH);
        RegistryTypeUtils.validateServiceRecord(RegistryTestHelper.ENTRY_PATH, record);
        RegistryTestHelper.assertMatches(written, record);
    }

    @Test
    public void testDeleteNonEmpty() throws Throwable {
        putExampleServiceEntry(RegistryTestHelper.ENTRY_PATH, 0);
        try {
            operations.delete(RegistryTestHelper.PARENT_PATH, false);
            Assert.fail("Expected a failure");
        } catch (PathIsNotEmptyDirectoryException expected) {
            // expected; ignore
        }
        operations.delete(RegistryTestHelper.PARENT_PATH, true);
    }

    @Test(expected = PathNotFoundException.class)
    public void testStatEmptyPath() throws Throwable {
        operations.stat(RegistryTestHelper.ENTRY_PATH);
    }

    @Test(expected = PathNotFoundException.class)
    public void testLsEmptyPath() throws Throwable {
        operations.list(RegistryTestHelper.PARENT_PATH);
    }

    @Test(expected = PathNotFoundException.class)
    public void testResolveEmptyPath() throws Throwable {
        operations.resolve(RegistryTestHelper.ENTRY_PATH);
    }

    @Test
    public void testMkdirNoParent() throws Throwable {
        String path = (RegistryTestHelper.ENTRY_PATH) + "/missing";
        try {
            operations.mknode(path, false);
            RegistryPathStatus stat = operations.stat(path);
            Assert.fail(("Got a status " + stat));
        } catch (PathNotFoundException expected) {
            // expected
        }
    }

    @Test
    public void testDoubleMkdir() throws Throwable {
        operations.mknode(RegistryTestHelper.USERPATH, false);
        String path = (RegistryTestHelper.USERPATH) + "newentry";
        Assert.assertTrue(operations.mknode(path, false));
        operations.stat(path);
        Assert.assertFalse(operations.mknode(path, false));
    }

    @Test
    public void testPutNoParent() throws Throwable {
        ServiceRecord record = new ServiceRecord();
        record.set(YARN_ID, "testPutNoParent");
        String path = "/path/without/parent";
        try {
            operations.bind(path, record, 0);
            // didn't get a failure
            // trouble
            RegistryPathStatus stat = operations.stat(path);
            Assert.fail(("Got a status " + stat));
        } catch (PathNotFoundException expected) {
            // expected
        }
    }

    @Test
    public void testPutMinimalRecord() throws Throwable {
        String path = "/path/with/minimal";
        operations.mknode(path, true);
        ServiceRecord record = new ServiceRecord();
        operations.bind(path, record, OVERWRITE);
        ServiceRecord resolve = operations.resolve(path);
        RegistryTestHelper.assertMatches(record, resolve);
    }

    @Test(expected = PathNotFoundException.class)
    public void testPutNoParent2() throws Throwable {
        ServiceRecord record = new ServiceRecord();
        record.set(YARN_ID, "testPutNoParent");
        String path = "/path/without/parent";
        operations.bind(path, record, 0);
    }

    @Test
    public void testStatDirectory() throws Throwable {
        String empty = "/empty";
        operations.mknode(empty, false);
        operations.stat(empty);
    }

    @Test
    public void testStatRootPath() throws Throwable {
        operations.mknode("/", false);
        operations.stat("/");
        operations.list("/");
        operations.list("/");
    }

    @Test
    public void testStatOneLevelDown() throws Throwable {
        operations.mknode("/subdir", true);
        operations.stat("/subdir");
    }

    @Test
    public void testLsRootPath() throws Throwable {
        String empty = "/";
        operations.mknode(empty, false);
        operations.stat(empty);
    }

    @Test
    public void testResolvePathThatHasNoEntry() throws Throwable {
        String empty = "/empty2";
        operations.mknode(empty, false);
        try {
            ServiceRecord record = operations.resolve(empty);
            Assert.fail(("expected an exception, got " + record));
        } catch (NoRecordException expected) {
            // expected
        }
    }

    @Test
    public void testOverwrite() throws Throwable {
        ServiceRecord written = putExampleServiceEntry(RegistryTestHelper.ENTRY_PATH, 0);
        ServiceRecord resolved1 = operations.resolve(RegistryTestHelper.ENTRY_PATH);
        resolved1.description = "resolved1";
        try {
            operations.bind(RegistryTestHelper.ENTRY_PATH, resolved1, 0);
            Assert.fail("overwrite succeeded when it should have failed");
        } catch (FileAlreadyExistsException expected) {
            // expected
        }
        // verify there's no changed
        ServiceRecord resolved2 = operations.resolve(RegistryTestHelper.ENTRY_PATH);
        RegistryTestHelper.assertMatches(written, resolved2);
        operations.bind(RegistryTestHelper.ENTRY_PATH, resolved1, OVERWRITE);
        ServiceRecord resolved3 = operations.resolve(RegistryTestHelper.ENTRY_PATH);
        RegistryTestHelper.assertMatches(resolved1, resolved3);
    }

    @Test
    public void testPutGetContainerPersistenceServiceEntry() throws Throwable {
        String path = RegistryTestHelper.ENTRY_PATH;
        ServiceRecord written = RegistryTestHelper.buildExampleServiceEntry(CONTAINER);
        operations.mknode(RegistryPathUtils.parentOf(path), true);
        operations.bind(path, written, CREATE);
        ServiceRecord resolved = operations.resolve(path);
        RegistryTestHelper.validateEntry(resolved);
        RegistryTestHelper.assertMatches(written, resolved);
    }

    @Test
    public void testAddingWriteAccessIsNoOpEntry() throws Throwable {
        Assert.assertFalse(operations.addWriteAccessor("id", "pass"));
        operations.clearWriteAccessors();
    }

    @Test
    public void testListListFully() throws Throwable {
        ServiceRecord r1 = new ServiceRecord();
        ServiceRecord r2 = RegistryTestHelper.createRecord("i", PERMANENT, "r2");
        String path = ((RegistryTestHelper.USERPATH) + (RegistryTestHelper.SC_HADOOP)) + "/listing";
        operations.mknode(path, true);
        String r1path = path + "/r1";
        operations.bind(r1path, r1, 0);
        String r2path = path + "/r2";
        operations.bind(r2path, r2, 0);
        RegistryPathStatus r1stat = operations.stat(r1path);
        Assert.assertEquals("r1", r1stat.path);
        RegistryPathStatus r2stat = operations.stat(r2path);
        Assert.assertEquals("r2", r2stat.path);
        Assert.assertNotEquals(r1stat, r2stat);
        // listings now
        List<String> list = operations.list(path);
        Assert.assertEquals("Wrong no. of children", 2, list.size());
        // there's no order here, so create one
        Map<String, String> names = new HashMap<String, String>();
        String entries = "";
        for (String child : list) {
            names.put(child, child);
            entries += child + " ";
        }
        Assert.assertTrue(("No 'r1' in " + entries), names.containsKey("r1"));
        Assert.assertTrue(("No 'r2' in " + entries), names.containsKey("r2"));
        Map<String, RegistryPathStatus> stats = RegistryUtils.statChildren(operations, path);
        Assert.assertEquals("Wrong no. of children", 2, stats.size());
        Assert.assertEquals(r1stat, stats.get("r1"));
        Assert.assertEquals(r2stat, stats.get("r2"));
    }

    @Test
    public void testComplexUsernames() throws Throwable {
        operations.mknode("/users/user with spaces", true);
        operations.mknode("/users/user-with_underscores", true);
        operations.mknode("/users/000000", true);
        operations.mknode("/users/-storm", true);
        operations.mknode("/users/windows\\ user", true);
        String home = RegistryUtils.homePathForUser("\u0413PA\u0414_3");
        operations.mknode(home, true);
        operations.mknode(RegistryUtils.servicePath(home, "service.class", "service 4_5"), true);
        operations.mknode(RegistryUtils.homePathForUser("hbase@HADOOP.APACHE.ORG"), true);
        operations.mknode(RegistryUtils.homePathForUser("hbase/localhost@HADOOP.APACHE.ORG"), true);
        home = RegistryUtils.homePathForUser("ADMINISTRATOR/127.0.0.1");
        Assert.assertTrue(("No 'administrator' in " + home), home.contains("administrator"));
        operations.mknode(home, true);
    }
}

