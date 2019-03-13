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
package org.apache.hadoop.registry.client.impl;


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathIsNotEmptyDirectoryException;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.hadoop.registry.client.exceptions.InvalidPathnameException;
import org.apache.hadoop.registry.client.types.ServiceRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 * FSRegistryOperationsService test, using the local filesystem.
 */
public class TestFSRegistryOperationsService {
    private static FSRegistryOperationsService registry = new FSRegistryOperationsService();

    private static FileSystem fs;

    @Test
    public void testMkNodeNonRecursive() throws IOException, PathNotFoundException, InvalidPathnameException {
        boolean result = false;
        System.out.println("Make node with parent already made, nonrecursive");
        result = TestFSRegistryOperationsService.registry.mknode("test/registryTestNode", false);
        Assert.assertTrue(result);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode")));
        // Expected to fail
        try {
            System.out.println("Try to make node with no parent, nonrecursive");
            TestFSRegistryOperationsService.registry.mknode("test/parent/registryTestNode", false);
            Assert.fail("Should not have created node");
        } catch (IOException e) {
        }
        Assert.assertFalse(TestFSRegistryOperationsService.fs.exists(new Path("test/parent/registryTestNode")));
    }

    @Test
    public void testMkNodeRecursive() throws IOException {
        boolean result = false;
        System.out.println("Make node with parent already made, recursive");
        result = TestFSRegistryOperationsService.registry.mknode("test/registryTestNode", true);
        Assert.assertTrue(result);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode")));
        result = false;
        System.out.println("Try to make node with no parent, recursive");
        result = TestFSRegistryOperationsService.registry.mknode("test/parent/registryTestNode", true);
        Assert.assertTrue(result);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/parent/registryTestNode")));
    }

    @Test
    public void testMkNodeAlreadyExists() throws IOException {
        System.out.println("pre-create test path");
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode"));
        System.out.println("Try to mknode existing path -- should be noop and return false");
        Assert.assertFalse(TestFSRegistryOperationsService.registry.mknode("test/registryTestNode", true));
        Assert.assertFalse(TestFSRegistryOperationsService.registry.mknode("test/registryTestNode", false));
    }

    @Test
    public void testBindParentPath() throws IOException, FileAlreadyExistsException, PathNotFoundException, InvalidPathnameException {
        ServiceRecord record = createRecord("0");
        System.out.println("pre-create test path");
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/parent1/registryTestNode"));
        TestFSRegistryOperationsService.registry.bind("test/parent1/registryTestNode", record, 1);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/parent1/registryTestNode/_record")));
        // Test without pre-creating path
        TestFSRegistryOperationsService.registry.bind("test/parent2/registryTestNode", record, 1);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/parent2/registryTestNode")));
    }

    @Test
    public void testBindAlreadyExists() throws IOException {
        ServiceRecord record1 = createRecord("1");
        ServiceRecord record2 = createRecord("2");
        System.out.println("Bind record1");
        TestFSRegistryOperationsService.registry.bind("test/registryTestNode", record1, 1);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/_record")));
        System.out.println("Bind record2, overwrite = 1");
        TestFSRegistryOperationsService.registry.bind("test/registryTestNode", record2, 1);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/_record")));
        // The record should have been overwritten
        ServiceRecord readRecord = TestFSRegistryOperationsService.registry.resolve("test/registryTestNode");
        Assert.assertTrue(readRecord.equals(record2));
        System.out.println("Bind record3, overwrite = 0");
        try {
            TestFSRegistryOperationsService.registry.bind("test/registryTestNode", record1, 0);
            Assert.fail("Should not overwrite record");
        } catch (IOException e) {
        }
        // The record should not be overwritten
        readRecord = TestFSRegistryOperationsService.registry.resolve("test/registryTestNode");
        Assert.assertTrue(readRecord.equals(record2));
    }

    @Test
    public void testResolve() throws IOException {
        ServiceRecord record = createRecord("0");
        TestFSRegistryOperationsService.registry.bind("test/registryTestNode", record, 1);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/_record")));
        System.out.println("Read record that exists");
        ServiceRecord readRecord = TestFSRegistryOperationsService.registry.resolve("test/registryTestNode");
        Assert.assertNotNull(readRecord);
        Assert.assertTrue(record.equals(readRecord));
        System.out.println("Try to read record that does not exist");
        try {
            readRecord = TestFSRegistryOperationsService.registry.resolve("test/nonExistentNode");
            Assert.fail("Should throw an error, record does not exist");
        } catch (IOException e) {
        }
    }

    @Test
    public void testExists() throws IOException {
        System.out.println("pre-create test path");
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode"));
        System.out.println("Check for existing node");
        boolean exists = TestFSRegistryOperationsService.registry.exists("test/registryTestNode");
        Assert.assertTrue(exists);
        System.out.println("Check for  non-existing node");
        exists = TestFSRegistryOperationsService.registry.exists("test/nonExistentNode");
        Assert.assertFalse(exists);
    }

    @Test
    public void testDeleteDirsOnly() throws IOException {
        System.out.println("pre-create test path with children");
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode"));
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode/child1"));
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode/child2"));
        try {
            TestFSRegistryOperationsService.registry.delete("test/registryTestNode", false);
            Assert.fail("Deleted dir wich children, nonrecursive flag set");
        } catch (IOException e) {
        }
        // Make sure nothing was deleted
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode")));
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child1")));
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child2")));
        System.out.println("Delete leaf path 'test/registryTestNode/child2'");
        TestFSRegistryOperationsService.registry.delete("test/registryTestNode/child2", false);
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode")));
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child1")));
        Assert.assertFalse(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child2")));
        System.out.println("Recursively delete non-leaf path 'test/registryTestNode'");
        TestFSRegistryOperationsService.registry.delete("test/registryTestNode", true);
        Assert.assertFalse(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode")));
    }

    @Test
    public void testDeleteWithRecords() throws IOException {
        System.out.println("pre-create test path with children and mocked records");
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode"));
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode/child1"));
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode/child2"));
        // Create and close stream immediately so they aren't blocking
        TestFSRegistryOperationsService.fs.create(new Path("test/registryTestNode/_record")).close();
        TestFSRegistryOperationsService.fs.create(new Path("test/registryTestNode/child1/_record")).close();
        System.out.println("Delete dir with child nodes and record file");
        try {
            TestFSRegistryOperationsService.registry.delete("test/registryTestNode", false);
            Assert.fail("Nonrecursive delete of non-empty dir");
        } catch (PathIsNotEmptyDirectoryException e) {
        }
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/_record")));
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child1/_record")));
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child2")));
        System.out.println("Delete dir with record file and no child dirs");
        TestFSRegistryOperationsService.registry.delete("test/registryTestNode/child1", false);
        Assert.assertFalse(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child1")));
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child2")));
        System.out.println("Delete dir with child dir and no record file");
        try {
            TestFSRegistryOperationsService.registry.delete("test/registryTestNode", false);
            Assert.fail("Nonrecursive delete of non-empty dir");
        } catch (PathIsNotEmptyDirectoryException e) {
        }
        Assert.assertTrue(TestFSRegistryOperationsService.fs.exists(new Path("test/registryTestNode/child2")));
    }

    @Test
    public void testList() throws IOException {
        System.out.println("pre-create test path with children and mocked records");
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode"));
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode/child1"));
        TestFSRegistryOperationsService.fs.mkdirs(new Path("test/registryTestNode/child2"));
        // Create and close stream immediately so they aren't blocking
        TestFSRegistryOperationsService.fs.create(new Path("test/registryTestNode/_record")).close();
        TestFSRegistryOperationsService.fs.create(new Path("test/registryTestNode/child1/_record")).close();
        List<String> ls = null;
        ls = TestFSRegistryOperationsService.registry.list("test/registryTestNode");
        Assert.assertNotNull(ls);
        Assert.assertEquals(2, ls.size());
        System.out.println(ls);
        Assert.assertTrue(ls.contains("child1"));
        Assert.assertTrue(ls.contains("child2"));
        ls = null;
        ls = TestFSRegistryOperationsService.registry.list("test/registryTestNode/child1");
        Assert.assertNotNull(ls);
        Assert.assertTrue(ls.isEmpty());
        ls = null;
        ls = TestFSRegistryOperationsService.registry.list("test/registryTestNode/child2");
        Assert.assertNotNull(ls);
        Assert.assertTrue(ls.isEmpty());
    }
}

