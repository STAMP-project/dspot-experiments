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
package org.apache.hadoop.hdfs;


import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.BlockStoragePolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockStoragePolicySuite;
import org.junit.Assert;
import org.junit.Test;


public class TestApplyingStoragePolicy {
    private static final short REPL = 1;

    private static final int SIZE = 128;

    private static Configuration conf;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem fs;

    @Test
    public void testStoragePolicyByDefault() throws Exception {
        final Path foo = new Path("/foo");
        final Path bar = new Path(foo, "bar");
        final Path wow = new Path(bar, "wow");
        final Path fooz = new Path(bar, "/fooz");
        DFSTestUtil.createFile(TestApplyingStoragePolicy.fs, wow, TestApplyingStoragePolicy.SIZE, TestApplyingStoragePolicy.REPL, 0);
        final BlockStoragePolicySuite suite = BlockStoragePolicySuite.createDefaultSuite();
        final BlockStoragePolicy hot = suite.getPolicy("HOT");
        /* test: storage policy is HOT by default or inherited from nearest
        ancestor, if not explicitly specified for newly created dir/file.
         */
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(wow), hot);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
    }

    @Test
    public void testSetAndUnsetStoragePolicy() throws Exception {
        final Path foo = new Path("/foo");
        final Path bar = new Path(foo, "bar");
        final Path wow = new Path(bar, "wow");
        final Path fooz = new Path(bar, "/fooz");
        DFSTestUtil.createFile(TestApplyingStoragePolicy.fs, wow, TestApplyingStoragePolicy.SIZE, TestApplyingStoragePolicy.REPL, 0);
        final BlockStoragePolicySuite suite = BlockStoragePolicySuite.createDefaultSuite();
        final BlockStoragePolicy warm = suite.getPolicy("WARM");
        final BlockStoragePolicy cold = suite.getPolicy("COLD");
        final BlockStoragePolicy hot = suite.getPolicy("HOT");
        /* test: set storage policy */
        TestApplyingStoragePolicy.fs.setStoragePolicy(foo, warm.getName());
        TestApplyingStoragePolicy.fs.setStoragePolicy(bar, cold.getName());
        TestApplyingStoragePolicy.fs.setStoragePolicy(wow, hot.getName());
        try {
            TestApplyingStoragePolicy.fs.setStoragePolicy(fooz, warm.getName());
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: get storage policy after set */
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), warm);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), cold);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(wow), hot);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: unset storage policy */
        TestApplyingStoragePolicy.fs.unsetStoragePolicy(foo);
        TestApplyingStoragePolicy.fs.unsetStoragePolicy(bar);
        TestApplyingStoragePolicy.fs.unsetStoragePolicy(wow);
        try {
            TestApplyingStoragePolicy.fs.unsetStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: get storage policy after unset */
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(wow), hot);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
    }

    @Test
    public void testNestedStoragePolicy() throws Exception {
        final Path foo = new Path("/foo");
        final Path bar = new Path(foo, "bar");
        final Path wow = new Path(bar, "wow");
        final Path fooz = new Path("/foos");
        DFSTestUtil.createFile(TestApplyingStoragePolicy.fs, wow, TestApplyingStoragePolicy.SIZE, TestApplyingStoragePolicy.REPL, 0);
        final BlockStoragePolicySuite suite = BlockStoragePolicySuite.createDefaultSuite();
        final BlockStoragePolicy warm = suite.getPolicy("WARM");
        final BlockStoragePolicy cold = suite.getPolicy("COLD");
        final BlockStoragePolicy hot = suite.getPolicy("HOT");
        /* test: set storage policy */
        TestApplyingStoragePolicy.fs.setStoragePolicy(foo, warm.getName());
        TestApplyingStoragePolicy.fs.setStoragePolicy(bar, cold.getName());
        TestApplyingStoragePolicy.fs.setStoragePolicy(wow, hot.getName());
        try {
            TestApplyingStoragePolicy.fs.setStoragePolicy(fooz, warm.getName());
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: get storage policy after set */
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), warm);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), cold);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(wow), hot);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: unset storage policy in the case of being nested */
        // unset wow
        TestApplyingStoragePolicy.fs.unsetStoragePolicy(wow);
        // inherit storage policy from wow's nearest ancestor
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(wow), cold);
        // unset bar
        TestApplyingStoragePolicy.fs.unsetStoragePolicy(bar);
        // inherit storage policy from bar's nearest ancestor
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), warm);
        // unset foo
        TestApplyingStoragePolicy.fs.unsetStoragePolicy(foo);
        // default storage policy is applied, since no more available ancestors
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), hot);
        // unset fooz
        try {
            TestApplyingStoragePolicy.fs.unsetStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: default storage policy is applied, since no explicit policies from
        ancestors are available
         */
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(wow), hot);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
    }

    @Test
    public void testSetAndGetStoragePolicy() throws IOException {
        final Path foo = new Path("/foo");
        final Path bar = new Path(foo, "bar");
        final Path fooz = new Path("/fooz");
        DFSTestUtil.createFile(TestApplyingStoragePolicy.fs, bar, TestApplyingStoragePolicy.SIZE, TestApplyingStoragePolicy.REPL, 0);
        final BlockStoragePolicySuite suite = BlockStoragePolicySuite.createDefaultSuite();
        final BlockStoragePolicy warm = suite.getPolicy("WARM");
        final BlockStoragePolicy cold = suite.getPolicy("COLD");
        final BlockStoragePolicy hot = suite.getPolicy("HOT");
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), hot);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), hot);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: set storage policy */
        TestApplyingStoragePolicy.fs.setStoragePolicy(foo, warm.getName());
        TestApplyingStoragePolicy.fs.setStoragePolicy(bar, cold.getName());
        try {
            TestApplyingStoragePolicy.fs.setStoragePolicy(fooz, warm.getName());
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
        /* test: get storage policy after set */
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(foo), warm);
        Assert.assertEquals(TestApplyingStoragePolicy.fs.getStoragePolicy(bar), cold);
        try {
            TestApplyingStoragePolicy.fs.getStoragePolicy(fooz);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
    }
}

