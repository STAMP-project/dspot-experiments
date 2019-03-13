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
package org.apache.hadoop.mapreduce.v2.hs.webapp;


import AMParams.JOB_ID;
import HtmlBlock.Block;
import JHAdminConfig.MR_HS_LOADED_JOBS_TASKS_MAX;
import JobState.ERROR;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.JobIdPBImpl;
import org.apache.hadoop.mapreduce.v2.hs.CompletedJob;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.HistoryFileInfo;
import org.apache.hadoop.mapreduce.v2.hs.JobHistory;
import org.apache.hadoop.mapreduce.v2.hs.UnparsedJob;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.ResponseInfo;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test the HsJobBlock generated for oversized jobs in JHS.
 */
public class TestHsJobBlock {
    @Test
    public void testHsJobBlockForOversizeJobShouldDisplayWarningMessage() {
        int maxAllowedTaskNum = 100;
        Configuration config = new Configuration();
        config.setInt(MR_HS_LOADED_JOBS_TASKS_MAX, maxAllowedTaskNum);
        JobHistory jobHistory = new TestHsJobBlock.JobHistoryStubWithAllOversizeJobs(maxAllowedTaskNum);
        jobHistory.init(config);
        HsJobBlock jobBlock = new HsJobBlock(jobHistory) {
            // override this so that job block can fetch a job id.
            @Override
            public Map<String, String> moreParams() {
                Map<String, String> map = new HashMap<>();
                map.put(JOB_ID, "job_0000_0001");
                return map;
            }
        };
        // set up the test block to render HsJobBLock to
        OutputStream outputStream = new ByteArrayOutputStream();
        HtmlBlock.Block block = TestHsJobBlock.createBlockToCreateTo(outputStream);
        jobBlock.render(block);
        block.getWriter().flush();
        String out = outputStream.toString();
        Assert.assertTrue(("Should display warning message for jobs that have too " + "many tasks"), out.contains((("Any job larger than " + maxAllowedTaskNum) + " will not be loaded")));
    }

    @Test
    public void testHsJobBlockForNormalSizeJobShouldNotDisplayWarningMessage() {
        Configuration config = new Configuration();
        config.setInt(MR_HS_LOADED_JOBS_TASKS_MAX, (-1));
        JobHistory jobHistory = new TestHsJobBlock.JobHitoryStubWithAllNormalSizeJobs();
        jobHistory.init(config);
        HsJobBlock jobBlock = new HsJobBlock(jobHistory) {
            // override this so that the job block can fetch a job id.
            @Override
            public Map<String, String> moreParams() {
                Map<String, String> map = new HashMap<>();
                map.put(JOB_ID, "job_0000_0001");
                return map;
            }

            // override this to avoid view context lookup in render()
            @Override
            public ResponseInfo info(String about) {
                return new ResponseInfo().about(about);
            }

            // override this to avoid view context lookup in render()
            @Override
            public String url(String... parts) {
                return StringHelper.ujoin("", parts);
            }
        };
        // set up the test block to render HsJobBLock to
        OutputStream outputStream = new ByteArrayOutputStream();
        HtmlBlock.Block block = TestHsJobBlock.createBlockToCreateTo(outputStream);
        jobBlock.render(block);
        block.getWriter().flush();
        String out = outputStream.toString();
        Assert.assertTrue("Should display job overview for the job.", out.contains("ApplicationMaster"));
    }

    /**
     * A JobHistory stub that treat all jobs as oversized and therefore will
     * not parse their job history files but return a UnparseJob instance.
     */
    static class JobHistoryStubWithAllOversizeJobs extends JobHistory {
        private final int maxAllowedTaskNum;

        public JobHistoryStubWithAllOversizeJobs(int maxAllowedTaskNum) {
            this.maxAllowedTaskNum = maxAllowedTaskNum;
        }

        @Override
        protected HistoryFileManager createHistoryFileManager() {
            HistoryFileManager historyFileManager;
            try {
                HistoryFileInfo historyFileInfo = TestHsJobBlock.JobHistoryStubWithAllOversizeJobs.createUnparsedJobHistoryFileInfo(maxAllowedTaskNum);
                historyFileManager = Mockito.mock(HistoryFileManager.class);
                Mockito.when(historyFileManager.getFileInfo(ArgumentMatchers.any(JobId.class))).thenReturn(historyFileInfo);
            } catch (IOException ex) {
                // this should never happen
                historyFileManager = super.createHistoryFileManager();
            }
            return historyFileManager;
        }

        private static HistoryFileInfo createUnparsedJobHistoryFileInfo(int maxAllowedTaskNum) throws IOException {
            HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
            // create an instance of UnparsedJob for a large job
            UnparsedJob unparsedJob = Mockito.mock(UnparsedJob.class);
            Mockito.when(unparsedJob.getMaxTasksAllowed()).thenReturn(maxAllowedTaskNum);
            Mockito.when(unparsedJob.getTotalMaps()).thenReturn(maxAllowedTaskNum);
            Mockito.when(unparsedJob.getTotalReduces()).thenReturn(maxAllowedTaskNum);
            Mockito.when(fileInfo.loadJob()).thenReturn(unparsedJob);
            return fileInfo;
        }
    }

    /**
     * A JobHistory stub that treats all jobs as normal size and therefore will
     * return a CompletedJob on HistoryFileInfo.loadJob().
     */
    static class JobHitoryStubWithAllNormalSizeJobs extends JobHistory {
        @Override
        public HistoryFileManager createHistoryFileManager() {
            HistoryFileManager historyFileManager;
            try {
                HistoryFileInfo historyFileInfo = TestHsJobBlock.JobHitoryStubWithAllNormalSizeJobs.createParsedJobHistoryFileInfo();
                historyFileManager = Mockito.mock(HistoryFileManager.class);
                Mockito.when(historyFileManager.getFileInfo(ArgumentMatchers.any(JobId.class))).thenReturn(historyFileInfo);
            } catch (IOException ex) {
                // this should never happen
                historyFileManager = super.createHistoryFileManager();
            }
            return historyFileManager;
        }

        private static HistoryFileInfo createParsedJobHistoryFileInfo() throws IOException {
            HistoryFileInfo fileInfo = Mockito.mock(HistoryFileInfo.class);
            CompletedJob job = TestHsJobBlock.JobHitoryStubWithAllNormalSizeJobs.createFakeCompletedJob();
            Mockito.when(fileInfo.loadJob()).thenReturn(job);
            return fileInfo;
        }

        private static CompletedJob createFakeCompletedJob() {
            CompletedJob job = Mockito.mock(CompletedJob.class);
            Mockito.when(job.getTotalMaps()).thenReturn(0);
            Mockito.when(job.getCompletedMaps()).thenReturn(0);
            Mockito.when(job.getTotalReduces()).thenReturn(0);
            Mockito.when(job.getCompletedReduces()).thenReturn(0);
            JobId jobId = TestHsJobBlock.JobHitoryStubWithAllNormalSizeJobs.createFakeJobId();
            Mockito.when(job.getID()).thenReturn(jobId);
            JobReport jobReport = Mockito.mock(JobReport.class);
            Mockito.when(jobReport.getSubmitTime()).thenReturn((-1L));
            Mockito.when(jobReport.getStartTime()).thenReturn((-1L));
            Mockito.when(jobReport.getFinishTime()).thenReturn((-1L));
            Mockito.when(job.getReport()).thenReturn(jobReport);
            Mockito.when(job.getAMInfos()).thenReturn(new ArrayList<AMInfo>());
            Mockito.when(job.getDiagnostics()).thenReturn(new ArrayList<String>());
            Mockito.when(job.getName()).thenReturn("fake completed job");
            Mockito.when(job.getQueueName()).thenReturn("default");
            Mockito.when(job.getUserName()).thenReturn("junit");
            Mockito.when(job.getState()).thenReturn(ERROR);
            Mockito.when(job.getAllCounters()).thenReturn(new Counters());
            Mockito.when(job.getTasks()).thenReturn(new HashMap<TaskId, org.apache.hadoop.mapreduce.v2.app.job.Task>());
            return job;
        }

        private static JobId createFakeJobId() {
            JobId jobId = new JobIdPBImpl();
            jobId.setId(0);
            ApplicationId appId = Mockito.mock(ApplicationId.class);
            Mockito.when(appId.getClusterTimestamp()).thenReturn(0L);
            Mockito.when(appId.getId()).thenReturn(0);
            jobId.setAppId(appId);
            return jobId;
        }
    }
}

