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
package org.apache.hadoop.mapreduce;


import MRJobConfig.SHARED_CACHE_MODE;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.client.api.SharedCacheClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static MRJobConfig.DEFAULT_MR_AM_STAGING_DIR;


/**
 * Tests the JobResourceUploader class with the shared cache.
 */
public class TestJobResourceUploaderWithSharedCache {
    protected static final Logger LOG = LoggerFactory.getLogger(TestJobResourceUploaderWithSharedCache.class);

    private static MiniDFSCluster dfs;

    private static FileSystem localFs;

    private static FileSystem remoteFs;

    private static Configuration conf = new Configuration();

    private static Path testRootDir;

    private static Path remoteStagingDir = new Path(DEFAULT_MR_AM_STAGING_DIR);

    private String input = "roses.are.red\nviolets.are.blue\nbunnies.are.pink\n";

    private class MyFileUploader extends JobResourceUploader {
        // The mocked SharedCacheClient that will be fed into the FileUploader
        private SharedCacheClient mockscClient = Mockito.mock(SharedCacheClient.class);

        // A real client for checksum calculation
        private SharedCacheClient scClient = SharedCacheClient.createSharedCacheClient();

        MyFileUploader(FileSystem submitFs, Configuration conf) throws IOException {
            super(submitFs, false);
            // Initialize the real client, but don't start it. We don't need or want
            // to create an actual proxy because we only use this for mocking out the
            // getFileChecksum method.
            scClient.init(conf);
            Mockito.when(mockscClient.getFileChecksum(ArgumentMatchers.any(Path.class))).thenAnswer(new Answer<String>() {
                @Override
                public String answer(InvocationOnMock invocation) throws Throwable {
                    Path file = ((Path) (invocation.getArguments()[0]));
                    // Use the real scClient to generate the checksum. We use an
                    // answer/mock combination to avoid having to spy on a real
                    // SharedCacheClient object.
                    return scClient.getFileChecksum(file);
                }
            });
        }

        // This method is to prime the mock client with the correct checksum, so it
        // looks like a given resource is present in the shared cache.
        public void mockFileInSharedCache(Path localFile, URL remoteFile) throws IOException, YarnException {
            // when the resource is referenced, simply return the remote path to the
            // caller
            Mockito.when(mockscClient.use(ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.eq(scClient.getFileChecksum(localFile)))).thenReturn(remoteFile);
        }

        @Override
        protected SharedCacheClient createSharedCacheClient(Configuration c) {
            // Feed the mocked SharedCacheClient into the FileUploader logic
            return mockscClient;
        }
    }

    @Test
    public void testSharedCacheDisabled() throws Exception {
        JobConf jobConf = createJobConf();
        Job job = new Job(jobConf);
        job.setJobID(new JobID("567789", 1));
        // shared cache is disabled by default
        uploadFilesToRemoteFS(job, jobConf, 0, 0, 0, false);
    }

    @Test
    public void testSharedCacheEnabled() throws Exception {
        JobConf jobConf = createJobConf();
        jobConf.set(SHARED_CACHE_MODE, "enabled");
        Job job = new Job(jobConf);
        job.setJobID(new JobID("567789", 1));
        // shared cache is enabled for every file type
        // the # of times SharedCacheClient.use is called should ==
        // total # of files/libjars/archive/jobjar
        uploadFilesToRemoteFS(job, jobConf, 8, 3, 2, false);
    }

    @Test
    public void testSharedCacheEnabledWithJobJarInSharedCache() throws Exception {
        JobConf jobConf = createJobConf();
        jobConf.set(SHARED_CACHE_MODE, "enabled");
        Job job = new Job(jobConf);
        job.setJobID(new JobID("567789", 1));
        // shared cache is enabled for every file type
        // the # of times SharedCacheClient.use is called should ==
        // total # of files/libjars/archive/jobjar
        uploadFilesToRemoteFS(job, jobConf, 8, 3, 2, true);
    }

    @Test
    public void testSharedCacheArchivesAndLibjarsEnabled() throws Exception {
        JobConf jobConf = createJobConf();
        jobConf.set(SHARED_CACHE_MODE, "archives,libjars");
        Job job = new Job(jobConf);
        job.setJobID(new JobID("567789", 1));
        // shared cache is enabled for archives and libjars type
        // the # of times SharedCacheClient.use is called should ==
        // total # of libjars and archives
        uploadFilesToRemoteFS(job, jobConf, 5, 1, 2, true);
    }
}

