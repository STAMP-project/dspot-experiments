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
package org.apache.hadoop.mapreduce.v2;


import RMAppState.FAILED;
import RMAppState.FINISHED;
import RMAppState.KILLED;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.avro.AvroRemoteException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.SleepJob;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.HSClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetJobReportRequest;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRJobsWithHistoryService {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRJobsWithHistoryService.class);

    private static final EnumSet<RMAppState> TERMINAL_RM_APP_STATES = EnumSet.of(FINISHED, FAILED, KILLED);

    private static MiniMRYarnCluster mrCluster;

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    static {
        try {
            TestMRJobsWithHistoryService.localFs = FileSystem.getLocal(TestMRJobsWithHistoryService.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static Path TEST_ROOT_DIR = TestMRJobsWithHistoryService.localFs.makeQualified(new Path("target", ((TestMRJobs.class.getName()) + "-tmpDir")));

    static Path APP_JAR = new Path(TestMRJobsWithHistoryService.TEST_ROOT_DIR, "MRAppJar.jar");

    @Test(timeout = 90000)
    public void testJobHistoryData() throws IOException, ClassNotFoundException, InterruptedException, AvroRemoteException {
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobsWithHistoryService.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        SleepJob sleepJob = new SleepJob();
        sleepJob.setConf(getConfig());
        // Job with 3 maps and 2 reduces
        Job job = sleepJob.createJob(3, 2, 1000, 1, 500, 1);
        job.setJarByClass(SleepJob.class);
        job.addFileToClassPath(TestMRJobsWithHistoryService.APP_JAR);// The AppMaster jar itself.

        job.waitForCompletion(true);
        Counters counterMR = job.getCounters();
        JobId jobId = TypeConverter.toYarn(job.getJobID());
        ApplicationId appID = jobId.getAppId();
        int pollElapsed = 0;
        while (true) {
            Thread.sleep(1000);
            pollElapsed += 1000;
            if (TestMRJobsWithHistoryService.TERMINAL_RM_APP_STATES.contains(getResourceManager().getRMContext().getRMApps().get(appID).getState())) {
                break;
            }
            if (pollElapsed >= 60000) {
                TestMRJobsWithHistoryService.LOG.warn("application did not reach terminal state within 60 seconds");
                break;
            }
        } 
        Assert.assertEquals(FINISHED, getResourceManager().getRMContext().getRMApps().get(appID).getState());
        Counters counterHS = job.getCounters();
        // TODO the Assert below worked. need to check
        // Should we compare each field or convert to V2 counter and compare
        TestMRJobsWithHistoryService.LOG.info(("CounterHS " + counterHS));
        TestMRJobsWithHistoryService.LOG.info(("CounterMR " + counterMR));
        Assert.assertEquals(counterHS, counterMR);
        HSClientProtocol historyClient = instantiateHistoryProxy();
        GetJobReportRequest gjReq = Records.newRecord(GetJobReportRequest.class);
        gjReq.setJobId(jobId);
        JobReport jobReport = historyClient.getJobReport(gjReq).getJobReport();
        verifyJobReport(jobReport, jobId);
    }
}

