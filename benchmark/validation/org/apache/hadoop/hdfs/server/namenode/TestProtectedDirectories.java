/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import CommonConfigurationKeys.FS_PROTECTED_DIRECTORIES;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify that the dfs.namenode.protected.directories setting is respected.
 */
public class TestProtectedDirectories {
    static final Logger LOG = LoggerFactory.getLogger(TestProtectedDirectories.class);

    @Rule
    public Timeout timeout = new Timeout(300000);

    @Test
    public void testReconfigureProtectedPaths() throws Throwable {
        Configuration conf = new HdfsConfiguration();
        Collection<Path> protectedPaths = Arrays.asList(new Path("/a"), new Path("/b"), new Path("/c"));
        Collection<Path> unprotectedPaths = Arrays.asList();
        MiniDFSCluster cluster = setupTestCase(conf, protectedPaths, unprotectedPaths);
        SortedSet<String> protectedPathsNew = new TreeSet(FSDirectory.normalizePaths(Arrays.asList("/aa", "/bb", "/cc"), CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES));
        String protectedPathsStrNew = "/aa,/bb,/cc";
        NameNode nn = cluster.getNameNode();
        // change properties
        nn.reconfigureProperty(CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES, protectedPathsStrNew);
        FSDirectory fsDirectory = nn.getNamesystem().getFSDirectory();
        // verify change
        Assert.assertEquals(String.format("%s has wrong value", CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES), protectedPathsNew, fsDirectory.getProtectedDirectories());
        Assert.assertEquals(String.format("%s has wrong value", CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES), protectedPathsStrNew, nn.getConf().get(CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES));
        // revert to default
        nn.reconfigureProperty(CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES, null);
        // verify default
        Assert.assertEquals(String.format("%s has wrong value", CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES), new TreeSet<String>(), fsDirectory.getProtectedDirectories());
        Assert.assertEquals(String.format("%s has wrong value", CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES), null, nn.getConf().get(CommonConfigurationKeysPublic.FS_PROTECTED_DIRECTORIES));
    }

    @Test
    public void testAll() throws Throwable {
        for (TestProtectedDirectories.TestMatrixEntry testMatrixEntry : createTestMatrix()) {
            Configuration conf = new HdfsConfiguration();
            MiniDFSCluster cluster = setupTestCase(conf, testMatrixEntry.getProtectedPaths(), testMatrixEntry.getUnprotectedPaths());
            try {
                TestProtectedDirectories.LOG.info("Running {}", testMatrixEntry);
                FileSystem fs = cluster.getFileSystem();
                for (Path path : testMatrixEntry.getAllPathsToBeDeleted()) {
                    final long countBefore = cluster.getNamesystem().getFilesTotal();
                    Assert.assertThat((((testMatrixEntry + ": Testing whether ") + path) + " can be deleted"), deletePath(fs, path), Is.is(testMatrixEntry.canPathBeDeleted(path)));
                    final long countAfter = cluster.getNamesystem().getFilesTotal();
                    if (!(testMatrixEntry.canPathBeDeleted(path))) {
                        Assert.assertThat("Either all paths should be deleted or none", countAfter, Is.is(countBefore));
                    }
                }
            } finally {
                cluster.shutdown();
            }
        }
    }

    /**
     * Verify that configured paths are normalized by removing
     * redundant separators.
     */
    @Test
    public void testProtectedDirNormalization1() {
        Configuration conf = new HdfsConfiguration();
        conf.set(FS_PROTECTED_DIRECTORIES, "/foo//bar");
        Collection<String> paths = FSDirectory.parseProtectedDirectories(conf);
        Assert.assertThat(paths.size(), Is.is(1));
        Assert.assertThat(paths.iterator().next(), Is.is("/foo/bar"));
    }

    /**
     * Verify that configured paths are normalized by removing
     * trailing separators.
     */
    @Test
    public void testProtectedDirNormalization2() {
        Configuration conf = new HdfsConfiguration();
        conf.set(FS_PROTECTED_DIRECTORIES, "/a/b/,/c,/d/e/f/");
        Collection<String> paths = FSDirectory.parseProtectedDirectories(conf);
        for (String path : paths) {
            Assert.assertFalse(path.endsWith("/"));
        }
    }

    /**
     * Verify that configured paths are canonicalized.
     */
    @Test
    public void testProtectedDirIsCanonicalized() {
        Configuration conf = new HdfsConfiguration();
        conf.set(FS_PROTECTED_DIRECTORIES, "/foo/../bar/");
        Collection<String> paths = FSDirectory.parseProtectedDirectories(conf);
        Assert.assertThat(paths.size(), Is.is(1));
        Assert.assertThat(paths.iterator().next(), Is.is("/bar"));
    }

    /**
     * Verify that the root directory in the configuration is correctly handled.
     */
    @Test
    public void testProtectedRootDirectory() {
        Configuration conf = new HdfsConfiguration();
        conf.set(FS_PROTECTED_DIRECTORIES, "/");
        Collection<String> paths = FSDirectory.parseProtectedDirectories(conf);
        Assert.assertThat(paths.size(), Is.is(1));
        Assert.assertThat(paths.iterator().next(), Is.is("/"));
    }

    /**
     * Verify that invalid paths in the configuration are filtered out.
     * (Path with scheme, reserved path).
     */
    @Test
    public void testBadPathsInConfig() {
        Configuration conf = new HdfsConfiguration();
        conf.set(FS_PROTECTED_DIRECTORIES, "hdfs://foo/,/.reserved/foo");
        Collection<String> paths = FSDirectory.parseProtectedDirectories(conf);
        Assert.assertThat(("Unexpected directories " + paths), paths.size(), Is.is(0));
    }

    private static class TestMatrixEntry {
        // true if the path can be deleted.
        final Map<Path, Boolean> protectedPaths = Maps.newHashMap();

        final Map<Path, Boolean> unProtectedPaths = Maps.newHashMap();

        private TestMatrixEntry() {
        }

        public static TestProtectedDirectories.TestMatrixEntry get() {
            return new TestProtectedDirectories.TestMatrixEntry();
        }

        public Collection<Path> getProtectedPaths() {
            return protectedPaths.keySet();
        }

        public Collection<Path> getUnprotectedPaths() {
            return unProtectedPaths.keySet();
        }

        /**
         * Get all paths to be deleted in sorted order.
         *
         * @return sorted collection of paths to be deleted.
         */
        // Path implements Comparable incorrectly
        @SuppressWarnings("unchecked")
        public Iterable<Path> getAllPathsToBeDeleted() {
            // Sorting ensures deletion of parents is attempted first.
            ArrayList<Path> combined = new ArrayList<>();
            combined.addAll(protectedPaths.keySet());
            combined.addAll(unProtectedPaths.keySet());
            Collections.sort(combined);
            return combined;
        }

        public boolean canPathBeDeleted(Path path) {
            return protectedPaths.containsKey(path) ? protectedPaths.get(path) : unProtectedPaths.get(path);
        }

        public TestProtectedDirectories.TestMatrixEntry addProtectedDir(String dir, boolean canBeDeleted) {
            protectedPaths.put(new Path(dir), canBeDeleted);
            return this;
        }

        public TestProtectedDirectories.TestMatrixEntry addUnprotectedDir(String dir, boolean canBeDeleted) {
            unProtectedPaths.put(new Path(dir), canBeDeleted);
            return this;
        }

        @Override
        public String toString() {
            return ((("TestMatrixEntry - ProtectedPaths=[" + (Joiner.on(", ").join(protectedPaths.keySet()))) + "]; UnprotectedPaths=[") + (Joiner.on(", ").join(unProtectedPaths.keySet()))) + "]";
        }
    }
}

