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
package org.apache.hadoop.mapreduce.filecache;


import MRJobConfig.CACHE_FILE_TIMESTAMPS;
import MRJobConfig.CACHE_FILE_VISIBILITIES;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestClientDistributedCacheManager {
    private static final Logger LOG = LoggerFactory.getLogger(TestClientDistributedCacheManager.class);

    private static final Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestClientDistributedCacheManager.class.getSimpleName());

    private static final Path TEST_VISIBILITY_PARENT_DIR = new Path(TestClientDistributedCacheManager.TEST_ROOT_DIR, "TestCacheVisibility_Parent");

    private static final Path TEST_VISIBILITY_CHILD_DIR = new Path(TestClientDistributedCacheManager.TEST_VISIBILITY_PARENT_DIR, "TestCacheVisibility_Child");

    private static final String FIRST_CACHE_FILE = "firstcachefile";

    private static final String SECOND_CACHE_FILE = "secondcachefile";

    private FileSystem fs;

    private Path firstCacheFile;

    private Path secondCacheFile;

    private Configuration conf;

    @Test
    public void testDetermineTimestamps() throws IOException {
        Job job = Job.getInstance(conf);
        job.addCacheFile(firstCacheFile.toUri());
        job.addCacheFile(secondCacheFile.toUri());
        Configuration jobConf = job.getConfiguration();
        Map<URI, FileStatus> statCache = new HashMap<>();
        ClientDistributedCacheManager.determineTimestamps(jobConf, statCache);
        FileStatus firstStatus = statCache.get(firstCacheFile.toUri());
        FileStatus secondStatus = statCache.get(secondCacheFile.toUri());
        Assert.assertNotNull(((firstCacheFile) + " was not found in the stats cache"), firstStatus);
        Assert.assertNotNull(((secondCacheFile) + " was not found in the stats cache"), secondStatus);
        Assert.assertEquals("Missing/extra entries found in the stats cache", 2, statCache.size());
        String expected = ((firstStatus.getModificationTime()) + ",") + (secondStatus.getModificationTime());
        Assert.assertEquals(expected, jobConf.get(CACHE_FILE_TIMESTAMPS));
        job = Job.getInstance(conf);
        job.addCacheFile(toUri());
        jobConf = job.getConfiguration();
        statCache.clear();
        ClientDistributedCacheManager.determineTimestamps(jobConf, statCache);
        FileStatus thirdStatus = statCache.get(TestClientDistributedCacheManager.TEST_VISIBILITY_CHILD_DIR.toUri());
        Assert.assertEquals("Missing/extra entries found in the stats cache", 1, statCache.size());
        Assert.assertNotNull(((TestClientDistributedCacheManager.TEST_VISIBILITY_CHILD_DIR) + " was not found in the stats cache"), thirdStatus);
        expected = Long.toString(thirdStatus.getModificationTime());
        Assert.assertEquals(("Incorrect timestamp for " + (TestClientDistributedCacheManager.TEST_VISIBILITY_CHILD_DIR)), expected, jobConf.get(CACHE_FILE_TIMESTAMPS));
    }

    @Test
    public void testDetermineCacheVisibilities() throws IOException {
        fs.setPermission(TestClientDistributedCacheManager.TEST_VISIBILITY_PARENT_DIR, new FsPermission(((short) (511))));
        fs.setPermission(TestClientDistributedCacheManager.TEST_VISIBILITY_CHILD_DIR, new FsPermission(((short) (511))));
        fs.setWorkingDirectory(TestClientDistributedCacheManager.TEST_VISIBILITY_CHILD_DIR);
        Job job = Job.getInstance(conf);
        Path relativePath = new Path(TestClientDistributedCacheManager.SECOND_CACHE_FILE);
        Path wildcardPath = new Path("*");
        Map<URI, FileStatus> statCache = new HashMap<>();
        Configuration jobConf;
        job.addCacheFile(firstCacheFile.toUri());
        job.addCacheFile(relativePath.toUri());
        jobConf = job.getConfiguration();
        // skip test if scratch dir is not PUBLIC
        Assume.assumeTrue(((TestClientDistributedCacheManager.TEST_VISIBILITY_PARENT_DIR) + " is not public"), ClientDistributedCacheManager.isPublic(jobConf, TestClientDistributedCacheManager.TEST_VISIBILITY_PARENT_DIR.toUri(), statCache));
        ClientDistributedCacheManager.determineCacheVisibilities(jobConf, statCache);
        // We use get() instead of getBoolean() so we can tell the difference
        // between wrong and missing
        Assert.assertEquals(("The file paths were not found to be publicly visible " + "even though the full path is publicly accessible"), "true,true", jobConf.get(CACHE_FILE_VISIBILITIES));
        checkCacheEntries(statCache, null, firstCacheFile, relativePath);
        job = Job.getInstance(conf);
        job.addCacheFile(wildcardPath.toUri());
        jobConf = job.getConfiguration();
        statCache.clear();
        ClientDistributedCacheManager.determineCacheVisibilities(jobConf, statCache);
        // We use get() instead of getBoolean() so we can tell the difference
        // between wrong and missing
        Assert.assertEquals(("The file path was not found to be publicly visible " + "even though the full path is publicly accessible"), "true", jobConf.get(CACHE_FILE_VISIBILITIES));
        checkCacheEntries(statCache, null, wildcardPath.getParent());
        Path qualifiedParent = fs.makeQualified(TestClientDistributedCacheManager.TEST_VISIBILITY_PARENT_DIR);
        fs.setPermission(TestClientDistributedCacheManager.TEST_VISIBILITY_PARENT_DIR, new FsPermission(((short) (448))));
        job = Job.getInstance(conf);
        job.addCacheFile(firstCacheFile.toUri());
        job.addCacheFile(relativePath.toUri());
        jobConf = job.getConfiguration();
        statCache.clear();
        ClientDistributedCacheManager.determineCacheVisibilities(jobConf, statCache);
        // We use get() instead of getBoolean() so we can tell the difference
        // between wrong and missing
        Assert.assertEquals(("The file paths were found to be publicly visible " + "even though the parent directory is not publicly accessible"), "false,false", jobConf.get(CACHE_FILE_VISIBILITIES));
        checkCacheEntries(statCache, qualifiedParent, firstCacheFile, relativePath);
        job = Job.getInstance(conf);
        job.addCacheFile(wildcardPath.toUri());
        jobConf = job.getConfiguration();
        statCache.clear();
        ClientDistributedCacheManager.determineCacheVisibilities(jobConf, statCache);
        // We use get() instead of getBoolean() so we can tell the difference
        // between wrong and missing
        Assert.assertEquals(("The file path was found to be publicly visible " + "even though the parent directory is not publicly accessible"), "false", jobConf.get(CACHE_FILE_VISIBILITIES));
        checkCacheEntries(statCache, qualifiedParent, wildcardPath.getParent());
    }
}

