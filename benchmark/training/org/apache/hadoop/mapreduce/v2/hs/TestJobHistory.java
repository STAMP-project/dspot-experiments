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
package org.apache.hadoop.mapreduce.v2.hs;


import JHAdminConfig.MR_HISTORY_CLEANER_INTERVAL_MS;
import JHAdminConfig.MR_HISTORY_LOADED_JOB_CACHE_SIZE;
import JHAdminConfig.MR_HISTORY_LOADED_TASKS_CACHE_SIZE;
import JHAdminConfig.MR_HISTORY_MAX_AGE_MS;
import com.google.common.cache.Cache;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobState;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.HistoryFileInfo;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.JobListCache;
import org.apache.hadoop.mapreduce.v2.hs.webapp.dao.JobsInfo;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestJobHistory {
    JobHistory jobHistory = null;

    @Test
    public void testRefreshLoadedJobCache() throws Exception {
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        Configuration conf = new Configuration();
        // Set the cache size to 2
        conf.setInt(MR_HISTORY_LOADED_JOB_CACHE_SIZE, 2);
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertFalse(storage.getUseLoadedTasksCache());
        Job[] jobs = new Job[3];
        JobId[] jobIds = new JobId[3];
        for (int i = 0; i < 3; i++) {
            jobs[i] = Mockito.mock(Job.class);
            jobIds[i] = Mockito.mock(JobId.class);
            Mockito.when(jobs[i].getID()).thenReturn(jobIds[i]);
        }
        HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(historyManager.getFileInfo(ArgumentMatchers.any(JobId.class))).thenReturn(fileInfo);
        Mockito.when(fileInfo.loadJob()).thenReturn(jobs[0]).thenReturn(jobs[1]).thenReturn(jobs[2]);
        // getFullJob will put the job in the cache if it isn't there
        for (int i = 0; i < 3; i++) {
            storage.getFullJob(jobs[i].getID());
        }
        Cache<JobId, Job> jobCache = storage.getLoadedJobCache();
        // Verify some jobs are stored in the cache.  Hard to predict eviction
        // in Guava version.
        Assert.assertTrue(((jobCache.size()) > 0));
        // Setting cache size to 3
        conf.setInt(MR_HISTORY_LOADED_JOB_CACHE_SIZE, 3);
        Mockito.doReturn(conf).when(storage).createConf();
        Mockito.when(fileInfo.loadJob()).thenReturn(jobs[0]).thenReturn(jobs[1]).thenReturn(jobs[2]);
        jobHistory.refreshLoadedJobCache();
        for (int i = 0; i < 3; i++) {
            storage.getFullJob(jobs[i].getID());
        }
        jobCache = storage.getLoadedJobCache();
        // Verify some jobs are stored in the cache.  Hard to predict eviction
        // in Guava version.
        Assert.assertTrue(((jobCache.size()) > 0));
    }

    @Test
    public void testTasksCacheLimit() throws Exception {
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        Configuration conf = new Configuration();
        // Set the cache threshold to 50 tasks
        conf.setInt(MR_HISTORY_LOADED_TASKS_CACHE_SIZE, 50);
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertTrue(storage.getUseLoadedTasksCache());
        TestCase.assertEquals(storage.getLoadedTasksCacheSize(), 50);
        // Create a bunch of smaller jobs (<< 50 tasks)
        Job[] jobs = new Job[10];
        JobId[] jobIds = new JobId[10];
        for (int i = 0; i < (jobs.length); i++) {
            jobs[i] = Mockito.mock(Job.class);
            jobIds[i] = Mockito.mock(JobId.class);
            Mockito.when(jobs[i].getID()).thenReturn(jobIds[i]);
            Mockito.when(jobs[i].getTotalMaps()).thenReturn(10);
            Mockito.when(jobs[i].getTotalReduces()).thenReturn(2);
        }
        // Create some large jobs that forces task-based cache flushing
        Job[] lgJobs = new Job[3];
        JobId[] lgJobIds = new JobId[3];
        for (int i = 0; i < (lgJobs.length); i++) {
            lgJobs[i] = Mockito.mock(Job.class);
            lgJobIds[i] = Mockito.mock(JobId.class);
            Mockito.when(lgJobs[i].getID()).thenReturn(lgJobIds[i]);
            Mockito.when(lgJobs[i].getTotalMaps()).thenReturn(2000);
            Mockito.when(lgJobs[i].getTotalReduces()).thenReturn(10);
        }
        HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(historyManager.getFileInfo(ArgumentMatchers.any(JobId.class))).thenReturn(fileInfo);
        Mockito.when(fileInfo.loadJob()).thenReturn(jobs[0]).thenReturn(jobs[1]).thenReturn(jobs[2]).thenReturn(jobs[3]).thenReturn(jobs[4]).thenReturn(jobs[5]).thenReturn(jobs[6]).thenReturn(jobs[7]).thenReturn(jobs[8]).thenReturn(jobs[9]).thenReturn(lgJobs[0]).thenReturn(lgJobs[1]).thenReturn(lgJobs[2]);
        // getFullJob will put the job in the cache if it isn't there
        Cache<JobId, Job> jobCache = storage.getLoadedJobCache();
        for (int i = 0; i < (jobs.length); i++) {
            storage.getFullJob(jobs[i].getID());
        }
        long prevSize = jobCache.size();
        // Fill the cache with some larger jobs and verify the cache
        // gets reduced in size.
        for (int i = 0; i < (lgJobs.length); i++) {
            storage.getFullJob(lgJobs[i].getID());
        }
        Assert.assertTrue(((jobCache.size()) < prevSize));
    }

    @Test
    public void testJobCacheLimitLargerThanMax() throws Exception {
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        JobHistory jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        Configuration conf = new Configuration();
        // Set the cache threshold to 50 tasks
        conf.setInt(MR_HISTORY_LOADED_TASKS_CACHE_SIZE, 500);
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertTrue(storage.getUseLoadedTasksCache());
        TestCase.assertEquals(storage.getLoadedTasksCacheSize(), 500);
        // Create a bunch of large jobs (>> 50 tasks)
        Job[] lgJobs = new Job[10];
        JobId[] lgJobIds = new JobId[10];
        for (int i = 0; i < (lgJobs.length); i++) {
            lgJobs[i] = Mockito.mock(Job.class);
            lgJobIds[i] = Mockito.mock(JobId.class);
            Mockito.when(lgJobs[i].getID()).thenReturn(lgJobIds[i]);
            Mockito.when(lgJobs[i].getTotalMaps()).thenReturn(700);
            Mockito.when(lgJobs[i].getTotalReduces()).thenReturn(50);
        }
        HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(historyManager.getFileInfo(ArgumentMatchers.any(JobId.class))).thenReturn(fileInfo);
        Mockito.when(fileInfo.loadJob()).thenReturn(lgJobs[0]).thenReturn(lgJobs[1]).thenReturn(lgJobs[2]).thenReturn(lgJobs[3]).thenReturn(lgJobs[4]).thenReturn(lgJobs[5]).thenReturn(lgJobs[6]).thenReturn(lgJobs[7]).thenReturn(lgJobs[8]).thenReturn(lgJobs[9]);
        // getFullJob will put the job in the cache if it isn't there
        Cache<JobId, Job> jobCache = storage.getLoadedJobCache();
        long[] cacheSize = new long[10];
        for (int i = 0; i < (lgJobs.length); i++) {
            storage.getFullJob(lgJobs[i].getID());
            Assert.assertTrue(((jobCache.size()) > 0));
        }
    }

    @Test
    public void testLoadedTasksEmptyConfiguration() {
        Configuration conf = new Configuration();
        conf.set(MR_HISTORY_LOADED_TASKS_CACHE_SIZE, "");
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        JobHistory jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertFalse(storage.getUseLoadedTasksCache());
    }

    @Test
    public void testLoadedTasksZeroConfiguration() {
        Configuration conf = new Configuration();
        conf.setInt(MR_HISTORY_LOADED_TASKS_CACHE_SIZE, 0);
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        JobHistory jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertTrue(storage.getUseLoadedTasksCache());
        TestCase.assertEquals(storage.getLoadedTasksCacheSize(), 1);
    }

    @Test
    public void testLoadedTasksNegativeConfiguration() {
        Configuration conf = new Configuration();
        conf.setInt(MR_HISTORY_LOADED_TASKS_CACHE_SIZE, (-1));
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        JobHistory jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertTrue(storage.getUseLoadedTasksCache());
        TestCase.assertEquals(storage.getLoadedTasksCacheSize(), 1);
    }

    @Test
    public void testLoadJobErrorCases() throws IOException {
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        Configuration conf = new Configuration();
        // Set the cache threshold to 50 tasks
        conf.setInt(MR_HISTORY_LOADED_TASKS_CACHE_SIZE, 50);
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Assert.assertTrue(storage.getUseLoadedTasksCache());
        TestCase.assertEquals(storage.getLoadedTasksCacheSize(), 50);
        // Create jobs for bad fileInfo results
        Job[] jobs = new Job[4];
        JobId[] jobIds = new JobId[4];
        for (int i = 0; i < (jobs.length); i++) {
            jobs[i] = Mockito.mock(Job.class);
            jobIds[i] = Mockito.mock(JobId.class);
            Mockito.when(jobs[i].getID()).thenReturn(jobIds[i]);
            Mockito.when(jobs[i].getTotalMaps()).thenReturn(10);
            Mockito.when(jobs[i].getTotalReduces()).thenReturn(2);
        }
        HistoryFileInfo loadJobException = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(loadJobException.loadJob()).thenThrow(new IOException("History file not found"));
        Mockito.when(historyManager.getFileInfo(jobIds[0])).thenThrow(new IOException(""));
        Mockito.when(historyManager.getFileInfo(jobIds[1])).thenReturn(null);
        Mockito.when(historyManager.getFileInfo(jobIds[2])).thenReturn(loadJobException);
        try {
            storage.getFullJob(jobIds[0]);
            Assert.fail("Did not get expected YarnRuntimeException for getFileInfo() throwing IOException");
        } catch (YarnRuntimeException e) {
            // Expected
        }
        // fileInfo==null should return null
        Job job = storage.getFullJob(jobIds[1]);
        Assert.assertNull(job);
        try {
            storage.getFullJob(jobIds[2]);
            Assert.fail("Did not get expected YarnRuntimeException for fileInfo.loadJob() throwing IOException");
        } catch (YarnRuntimeException e) {
            // Expected
        }
    }

    @Test
    public void testRefreshJobRetentionSettings() throws IOException, InterruptedException {
        String root = "mockfs://foo/";
        String historyDoneDir = root + "mapred/history/done";
        long now = System.currentTimeMillis();
        long someTimeYesterday = now - ((25L * 3600) * 1000);
        long timeBefore200Secs = now - (200L * 1000);
        // Get yesterday's date in YY/MM/DD format
        String timestampComponent = JobHistoryUtils.timestampDirectoryComponent(someTimeYesterday);
        // Create a folder under yesterday's done dir
        Path donePathYesterday = new Path(historyDoneDir, ((timestampComponent + "/") + "000000"));
        FileStatus dirCreatedYesterdayStatus = new FileStatus(0, true, 0, 0, someTimeYesterday, donePathYesterday);
        // Get today's date in YY/MM/DD format
        timestampComponent = JobHistoryUtils.timestampDirectoryComponent(timeBefore200Secs);
        // Create a folder under today's done dir
        Path donePathToday = new Path(historyDoneDir, ((timestampComponent + "/") + "000000"));
        FileStatus dirCreatedTodayStatus = new FileStatus(0, true, 0, 0, timeBefore200Secs, donePathToday);
        // Create a jhist file with yesterday's timestamp under yesterday's done dir
        Path fileUnderYesterdayDir = new Path(donePathYesterday.toString(), (((("job_1372363578825_0015-" + someTimeYesterday) + "-user-Sleep+job-") + someTimeYesterday) + "-1-1-SUCCEEDED-default.jhist"));
        FileStatus fileUnderYesterdayDirStatus = new FileStatus(10, false, 0, 0, someTimeYesterday, fileUnderYesterdayDir);
        // Create a jhist file with today's timestamp under today's done dir
        Path fileUnderTodayDir = new Path(donePathYesterday.toString(), (((("job_1372363578825_0016-" + timeBefore200Secs) + "-user-Sleep+job-") + timeBefore200Secs) + "-1-1-SUCCEEDED-default.jhist"));
        FileStatus fileUnderTodayDirStatus = new FileStatus(10, false, 0, 0, timeBefore200Secs, fileUnderTodayDir);
        HistoryFileManager historyManager = Mockito.spy(new HistoryFileManager());
        jobHistory = Mockito.spy(new JobHistory());
        List<FileStatus> fileStatusList = new LinkedList<FileStatus>();
        fileStatusList.add(dirCreatedYesterdayStatus);
        fileStatusList.add(dirCreatedTodayStatus);
        // Make the initial delay of history job cleaner as 4 secs
        Mockito.doReturn(4).when(jobHistory).getInitDelaySecs();
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        List<FileStatus> list1 = new LinkedList<FileStatus>();
        list1.add(fileUnderYesterdayDirStatus);
        Mockito.doReturn(list1).when(historyManager).scanDirectoryForHistoryFiles(ArgumentMatchers.eq(donePathYesterday), ArgumentMatchers.any(FileContext.class));
        List<FileStatus> list2 = new LinkedList<FileStatus>();
        list2.add(fileUnderTodayDirStatus);
        Mockito.doReturn(list2).when(historyManager).scanDirectoryForHistoryFiles(ArgumentMatchers.eq(donePathToday), ArgumentMatchers.any(FileContext.class));
        Mockito.doReturn(fileStatusList).when(historyManager).getHistoryDirsForCleaning(Mockito.anyLong());
        Mockito.doReturn(true).when(historyManager).deleteDir(ArgumentMatchers.any(FileStatus.class));
        JobListCache jobListCache = Mockito.mock(JobListCache.class);
        HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
        Mockito.doReturn(jobListCache).when(historyManager).createJobListCache();
        Mockito.when(jobListCache.get(ArgumentMatchers.any(JobId.class))).thenReturn(fileInfo);
        Mockito.doNothing().when(fileInfo).delete();
        // Set job retention time to 24 hrs and cleaner interval to 2 secs
        Configuration conf = new Configuration();
        conf.setLong(MR_HISTORY_MAX_AGE_MS, ((24L * 3600) * 1000));
        conf.setLong(MR_HISTORY_CLEANER_INTERVAL_MS, (2 * 1000));
        jobHistory.init(conf);
        jobHistory.start();
        TestCase.assertEquals((2 * 1000L), jobHistory.getCleanerInterval());
        // Only yesterday's jhist file should get deleted
        Mockito.verify(fileInfo, Mockito.timeout(20000).times(1)).delete();
        fileStatusList.remove(dirCreatedYesterdayStatus);
        // Now reset job retention time to 10 secs
        conf.setLong(MR_HISTORY_MAX_AGE_MS, (10 * 1000));
        // Set cleaner interval to 1 sec
        conf.setLong(MR_HISTORY_CLEANER_INTERVAL_MS, (1 * 1000));
        Mockito.doReturn(conf).when(jobHistory).createConf();
        // Do refresh job retention settings
        jobHistory.refreshJobRetentionSettings();
        // Cleaner interval should be updated
        TestCase.assertEquals((1 * 1000L), jobHistory.getCleanerInterval());
        // Today's jhist file will also be deleted now since it falls below the
        // retention threshold
        Mockito.verify(fileInfo, Mockito.timeout(20000).times(2)).delete();
    }

    @Test
    public void testCachedStorageWaitsForFileMove() throws IOException {
        HistoryFileManager historyManager = Mockito.mock(HistoryFileManager.class);
        jobHistory = Mockito.spy(new JobHistory());
        Mockito.doReturn(historyManager).when(jobHistory).createHistoryFileManager();
        Configuration conf = new Configuration();
        jobHistory.init(conf);
        jobHistory.start();
        CachedHistoryStorage storage = Mockito.spy(((CachedHistoryStorage) (jobHistory.getHistoryStorage())));
        Job job = Mockito.mock(Job.class);
        JobId jobId = Mockito.mock(JobId.class);
        Mockito.when(job.getID()).thenReturn(jobId);
        Mockito.when(job.getTotalMaps()).thenReturn(10);
        Mockito.when(job.getTotalReduces()).thenReturn(2);
        HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(historyManager.getFileInfo(ArgumentMatchers.eq(jobId))).thenReturn(fileInfo);
        Mockito.when(fileInfo.loadJob()).thenReturn(job);
        storage.getFullJob(jobId);
        Mockito.verify(fileInfo).waitUntilMoved();
    }

    @Test
    public void testRefreshLoadedJobCacheUnSupportedOperation() {
        jobHistory = Mockito.spy(new JobHistory());
        HistoryStorage storage = new HistoryStorage() {
            @Override
            public void setHistoryFileManager(HistoryFileManager hsManager) {
                // TODO Auto-generated method stub
            }

            @Override
            public JobsInfo getPartialJobs(Long offset, Long count, String user, String queue, Long sBegin, Long sEnd, Long fBegin, Long fEnd, JobState jobState) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Job getFullJob(JobId jobId) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<JobId, Job> getAllPartialJobs() {
                // TODO Auto-generated method stub
                return null;
            }
        };
        Mockito.doReturn(storage).when(jobHistory).createHistoryStorage();
        jobHistory.init(new Configuration());
        jobHistory.start();
        Throwable th = null;
        try {
            jobHistory.refreshLoadedJobCache();
        } catch (Exception e) {
            th = e;
        }
        Assert.assertTrue((th instanceof UnsupportedOperationException));
    }
}

