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
package org.apache.hadoop.mapred;


import DistributedCache.CACHE_ARCHIVES;
import DistributedCache.CACHE_ARCHIVES_TIMESTAMPS;
import DistributedCache.CACHE_FILES;
import DistributedCache.CACHE_FILES_TIMESTAMPS;
import DistributedCache.CACHE_LOCALARCHIVES;
import DistributedCache.CACHE_LOCALFILES;
import DistributedCache.CACHE_SYMLINK;
import JTConfig.JT_IPC_ADDRESS;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the use of the
 * {@link org.apache.hadoop.mapreduce.filecache.DistributedCache} within the
 * full MR flow as well as the LocalJobRunner. This ought to be part of the
 * filecache package, but that package is not currently in mapred, so cannot
 * depend on MR for testing.
 *
 * We use the distributed.* namespace for temporary files.
 *
 * See {@link TestMiniMRLocalFS}, {@link TestMiniMRDFSCaching}, and
 * {@link MRCaching} for other tests that test the distributed cache.
 *
 * This test is not fast: it uses MiniMRCluster.
 */
@SuppressWarnings("deprecation")
public class TestMRWithDistributedCache {
    private static Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "/tmp"));

    private static File symlinkFile = new File("distributed.first.symlink");

    private static File expectedAbsentSymlinkFile = new File("distributed.second.jar");

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    static {
        try {
            TestMRWithDistributedCache.localFs = FileSystem.getLocal(TestMRWithDistributedCache.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestMRWithDistributedCache.class);

    private static class DistributedCacheChecker {
        public void setup(TaskInputOutputContext<?, ?, ?, ?> context) throws IOException {
            Configuration conf = context.getConfiguration();
            Path[] localFiles = context.getLocalCacheFiles();
            URI[] files = context.getCacheFiles();
            Path[] localArchives = context.getLocalCacheArchives();
            URI[] archives = context.getCacheArchives();
            FileSystem fs = LocalFileSystem.get(conf);
            // Check that 2 files and 2 archives are present
            Assert.assertEquals(2, localFiles.length);
            Assert.assertEquals(2, localArchives.length);
            Assert.assertEquals(2, files.length);
            Assert.assertEquals(2, archives.length);
            // Check the file name
            Assert.assertTrue(files[0].getPath().endsWith("distributed.first"));
            Assert.assertTrue(files[1].getPath().endsWith("distributed.second.jar"));
            // Check lengths of the files
            Assert.assertEquals(1, fs.getFileStatus(localFiles[0]).getLen());
            Assert.assertTrue(((fs.getFileStatus(localFiles[1]).getLen()) > 1));
            // Check extraction of the archive
            Assert.assertTrue(fs.exists(new Path(localArchives[0], "distributed.jar.inside3")));
            Assert.assertTrue(fs.exists(new Path(localArchives[1], "distributed.jar.inside4")));
            // Check the class loaders
            TestMRWithDistributedCache.LOG.info(("Java Classpath: " + (System.getProperty("java.class.path"))));
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            // Both the file and the archive were added to classpath, so both
            // should be reachable via the class loader.
            Assert.assertNotNull(cl.getResource("distributed.jar.inside2"));
            Assert.assertNotNull(cl.getResource("distributed.jar.inside3"));
            Assert.assertNull(cl.getResource("distributed.jar.inside4"));
            // Check that the symlink for the renaming was created in the cwd;
            Assert.assertTrue("symlink distributed.first.symlink doesn't exist", TestMRWithDistributedCache.symlinkFile.exists());
            Assert.assertEquals("symlink distributed.first.symlink length not 1", 1, TestMRWithDistributedCache.symlinkFile.length());
            // This last one is a difference between MRv2 and MRv1
            Assert.assertTrue("second file should be symlinked too", TestMRWithDistributedCache.expectedAbsentSymlinkFile.exists());
        }
    }

    public static class DistributedCacheCheckerMapper extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            new TestMRWithDistributedCache.DistributedCacheChecker().setup(context);
        }
    }

    public static class DistributedCacheCheckerReducer extends Reducer<LongWritable, Text, NullWritable, NullWritable> {
        @Override
        public void setup(Context context) throws IOException {
            new TestMRWithDistributedCache.DistributedCacheChecker().setup(context);
        }
    }

    /**
     * Tests using the local job runner.
     */
    @Test
    public void testLocalJobRunner() throws Exception {
        TestMRWithDistributedCache.symlinkFile.delete();// ensure symlink is not present (e.g. if test is

        // killed part way through)
        Configuration c = new Configuration();
        c.set(JT_IPC_ADDRESS, "local");
        c.set("fs.defaultFS", "file:///");
        testWithConf(c);
        // Symlink target will have gone so can't use File.exists()
        Assert.assertFalse("Symlink not removed by local job runner", Arrays.asList(new File(".").list()).contains(TestMRWithDistributedCache.symlinkFile.getName()));
    }

    @Test(timeout = 10000)
    public void testDeprecatedFunctions() throws Exception {
        DistributedCache.addLocalArchives(TestMRWithDistributedCache.conf, "Test Local Archives 1");
        Assert.assertEquals("Test Local Archives 1", TestMRWithDistributedCache.conf.get(CACHE_LOCALARCHIVES));
        Assert.assertEquals(1, DistributedCache.getLocalCacheArchives(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals("Test Local Archives 1", DistributedCache.getLocalCacheArchives(TestMRWithDistributedCache.conf)[0].getName());
        DistributedCache.addLocalArchives(TestMRWithDistributedCache.conf, "Test Local Archives 2");
        Assert.assertEquals("Test Local Archives 1,Test Local Archives 2", TestMRWithDistributedCache.conf.get(CACHE_LOCALARCHIVES));
        Assert.assertEquals(2, DistributedCache.getLocalCacheArchives(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals("Test Local Archives 2", DistributedCache.getLocalCacheArchives(TestMRWithDistributedCache.conf)[1].getName());
        DistributedCache.setLocalArchives(TestMRWithDistributedCache.conf, "Test Local Archives 3");
        Assert.assertEquals("Test Local Archives 3", TestMRWithDistributedCache.conf.get(CACHE_LOCALARCHIVES));
        Assert.assertEquals(1, DistributedCache.getLocalCacheArchives(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals("Test Local Archives 3", DistributedCache.getLocalCacheArchives(TestMRWithDistributedCache.conf)[0].getName());
        DistributedCache.addLocalFiles(TestMRWithDistributedCache.conf, "Test Local Files 1");
        Assert.assertEquals("Test Local Files 1", TestMRWithDistributedCache.conf.get(CACHE_LOCALFILES));
        Assert.assertEquals(1, DistributedCache.getLocalCacheFiles(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals("Test Local Files 1", DistributedCache.getLocalCacheFiles(TestMRWithDistributedCache.conf)[0].getName());
        DistributedCache.addLocalFiles(TestMRWithDistributedCache.conf, "Test Local Files 2");
        Assert.assertEquals("Test Local Files 1,Test Local Files 2", TestMRWithDistributedCache.conf.get(CACHE_LOCALFILES));
        Assert.assertEquals(2, DistributedCache.getLocalCacheFiles(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals("Test Local Files 2", DistributedCache.getLocalCacheFiles(TestMRWithDistributedCache.conf)[1].getName());
        DistributedCache.setLocalFiles(TestMRWithDistributedCache.conf, "Test Local Files 3");
        Assert.assertEquals("Test Local Files 3", TestMRWithDistributedCache.conf.get(CACHE_LOCALFILES));
        Assert.assertEquals(1, DistributedCache.getLocalCacheFiles(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals("Test Local Files 3", DistributedCache.getLocalCacheFiles(TestMRWithDistributedCache.conf)[0].getName());
        DistributedCache.setArchiveTimestamps(TestMRWithDistributedCache.conf, "1234567890");
        Assert.assertEquals(1234567890, TestMRWithDistributedCache.conf.getLong(CACHE_ARCHIVES_TIMESTAMPS, 0));
        Assert.assertEquals(1, DistributedCache.getArchiveTimestamps(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals(1234567890, DistributedCache.getArchiveTimestamps(TestMRWithDistributedCache.conf)[0]);
        DistributedCache.setFileTimestamps(TestMRWithDistributedCache.conf, "1234567890");
        Assert.assertEquals(1234567890, TestMRWithDistributedCache.conf.getLong(CACHE_FILES_TIMESTAMPS, 0));
        Assert.assertEquals(1, DistributedCache.getFileTimestamps(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals(1234567890, DistributedCache.getFileTimestamps(TestMRWithDistributedCache.conf)[0]);
        DistributedCache.createAllSymlink(TestMRWithDistributedCache.conf, new File("Test Job Cache Dir"), new File("Test Work Dir"));
        Assert.assertNull(TestMRWithDistributedCache.conf.get(CACHE_SYMLINK));
        Assert.assertTrue(DistributedCache.getSymlink(TestMRWithDistributedCache.conf));
        Assert.assertTrue(TestMRWithDistributedCache.symlinkFile.createNewFile());
        FileStatus fileStatus = DistributedCache.getFileStatus(TestMRWithDistributedCache.conf, TestMRWithDistributedCache.symlinkFile.toURI());
        Assert.assertNotNull(fileStatus);
        Assert.assertEquals(fileStatus.getModificationTime(), DistributedCache.getTimestamp(TestMRWithDistributedCache.conf, TestMRWithDistributedCache.symlinkFile.toURI()));
        Assert.assertTrue(TestMRWithDistributedCache.symlinkFile.delete());
        DistributedCache.addCacheArchive(TestMRWithDistributedCache.symlinkFile.toURI(), TestMRWithDistributedCache.conf);
        Assert.assertEquals(TestMRWithDistributedCache.symlinkFile.toURI().toString(), TestMRWithDistributedCache.conf.get(CACHE_ARCHIVES));
        Assert.assertEquals(1, DistributedCache.getCacheArchives(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals(TestMRWithDistributedCache.symlinkFile.toURI(), DistributedCache.getCacheArchives(TestMRWithDistributedCache.conf)[0]);
        DistributedCache.addCacheFile(TestMRWithDistributedCache.symlinkFile.toURI(), TestMRWithDistributedCache.conf);
        Assert.assertEquals(TestMRWithDistributedCache.symlinkFile.toURI().toString(), TestMRWithDistributedCache.conf.get(CACHE_FILES));
        Assert.assertEquals(1, DistributedCache.getCacheFiles(TestMRWithDistributedCache.conf).length);
        Assert.assertEquals(TestMRWithDistributedCache.symlinkFile.toURI(), DistributedCache.getCacheFiles(TestMRWithDistributedCache.conf)[0]);
    }
}

