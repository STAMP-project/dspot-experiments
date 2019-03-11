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
package org.apache.hadoop.mapred.gridmix;


import DistributedCacheEmulator.GRIDMIX_EMULATE_DISTRIBUTEDCACHE;
import JobCreator.LOADJOB;
import MRJobConfig.CACHE_FILES;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;

import static DistributedCacheEmulator.GRIDMIX_EMULATE_DISTRIBUTEDCACHE;


/**
 * Validate emulation of distributed cache load in gridmix simulated jobs.
 */
public class TestDistCacheEmulation {
    private DistributedCacheEmulator dce = null;

    /**
     * Validate GenerateDistCacheData job if it creates dist cache files properly.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 200000)
    public void testGenerateDistCacheData() throws Exception {
        long[] sortedFileSizes = new long[5];
        Configuration jobConf = runSetupGenerateDistCacheData(true, sortedFileSizes);
        GridmixJob gridmixJob = new GenerateDistCacheData(jobConf);
        Job job = gridmixJob.call();
        Assert.assertEquals("Number of reduce tasks in GenerateDistCacheData is not 0.", 0, job.getNumReduceTasks());
        Assert.assertTrue("GenerateDistCacheData job failed.", job.waitForCompletion(false));
        validateDistCacheData(jobConf, sortedFileSizes);
    }

    /**
     * Test if DistributedCacheEmulator's setup of GenerateDistCacheData is
     * working as expected.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 20000)
    public void testSetupGenerateDistCacheData() throws IOException, InterruptedException {
        long[] sortedFileSizes = new long[5];
        Configuration jobConf = runSetupGenerateDistCacheData(true, sortedFileSizes);
        validateSetupGenDC(jobConf, sortedFileSizes);
        // Verify if correct exit code is seen when -generate option is missing and
        // distributed cache files are missing in the expected path.
        runSetupGenerateDistCacheData(false, sortedFileSizes);
    }

    /**
     * Test the configuration property for disabling/enabling emulation of
     * distributed cache load.
     */
    @Test(timeout = 2000)
    public void testDistCacheEmulationConfigurability() throws IOException {
        Configuration jobConf = GridmixTestUtils.mrvl.getConfig();
        Path ioPath = new Path("testDistCacheEmulationConfigurability").makeQualified(GridmixTestUtils.dfs.getUri(), GridmixTestUtils.dfs.getWorkingDirectory());
        FileSystem fs = FileSystem.get(jobConf);
        FileSystem.mkdirs(fs, ioPath, new FsPermission(((short) (511))));
        // default config
        dce = createDistributedCacheEmulator(jobConf, ioPath, false);
        Assert.assertTrue((("Default configuration of " + (GRIDMIX_EMULATE_DISTRIBUTEDCACHE)) + " is wrong."), dce.shouldEmulateDistCacheLoad());
        // config property set to false
        jobConf.setBoolean(GRIDMIX_EMULATE_DISTRIBUTEDCACHE, false);
        dce = createDistributedCacheEmulator(jobConf, ioPath, false);
        Assert.assertFalse((("Disabling of emulation of distributed cache load by setting " + (GRIDMIX_EMULATE_DISTRIBUTEDCACHE)) + " to false is not working."), dce.shouldEmulateDistCacheLoad());
    }

    /**
     * test method configureDistCacheFiles
     */
    @Test(timeout = 2000)
    public void testDistCacheEmulator() throws Exception {
        Configuration conf = new Configuration();
        configureDummyDistCacheFiles(conf);
        File ws = new File((("target" + (File.separator)) + (this.getClass().getName())));
        Path ioPath = new Path(ws.getAbsolutePath());
        DistributedCacheEmulator dce = new DistributedCacheEmulator(conf, ioPath);
        JobConf jobConf = new JobConf(conf);
        jobConf.setUser(UserGroupInformation.getCurrentUser().getShortUserName());
        File fin = new File((((((((("src" + (File.separator)) + "test") + (File.separator)) + "resources") + (File.separator)) + "data") + (File.separator)) + "wordcount.json"));
        dce.init(fin.getAbsolutePath(), LOADJOB, true);
        dce.configureDistCacheFiles(conf, jobConf);
        String[] caches = conf.getStrings(CACHE_FILES);
        String[] tmpfiles = conf.getStrings("tmpfiles");
        // this method should fill caches AND tmpfiles  from MRJobConfig.CACHE_FILES property
        Assert.assertEquals(6, ((caches == null ? 0 : caches.length) + (tmpfiles == null ? 0 : tmpfiles.length)));
    }
}

