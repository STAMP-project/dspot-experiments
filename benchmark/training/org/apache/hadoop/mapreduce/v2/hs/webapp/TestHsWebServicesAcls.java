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


import JobACL.VIEW_JOB;
import Status.UNAUTHORIZED;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobACLsManager;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobACL;
import org.apache.hadoop.mapreduce.v2.api.records.AMInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.mapreduce.v2.api.records.JobState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptCompletionEvent;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.hs.HistoryContext;
import org.apache.hadoop.security.GroupMappingServiceProvider;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.Priority;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestHsWebServicesAcls {
    private static String FRIENDLY_USER = "friendly";

    private static String ENEMY_USER = "enemy";

    private JobConf conf;

    private HistoryContext ctx;

    private String jobIdStr;

    private String taskIdStr;

    private String taskAttemptIdStr;

    private HsWebServices hsWebServices;

    @Test
    public void testGetJobAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJob(hsr, jobIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJob(hsr, jobIdStr);
    }

    @Test
    public void testGetJobCountersAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobCounters(hsr, jobIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobCounters(hsr, jobIdStr);
    }

    @Test
    public void testGetJobConfAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobConf(hsr, jobIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobConf(hsr, jobIdStr);
    }

    @Test
    public void testGetJobTasksAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobTasks(hsr, jobIdStr, "m");
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobTasks(hsr, jobIdStr, "m");
    }

    @Test
    public void testGetJobTaskAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobTask(hsr, jobIdStr, this.taskIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobTask(hsr, this.jobIdStr, this.taskIdStr);
    }

    @Test
    public void testGetSingleTaskCountersAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getSingleTaskCounters(hsr, this.jobIdStr, this.taskIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getSingleTaskCounters(hsr, this.jobIdStr, this.taskIdStr);
    }

    @Test
    public void testGetJobTaskAttemptsAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobTaskAttempts(hsr, this.jobIdStr, this.taskIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobTaskAttempts(hsr, this.jobIdStr, this.taskIdStr);
    }

    @Test
    public void testGetJobTaskAttemptIdAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobTaskAttemptId(hsr, this.jobIdStr, this.taskIdStr, this.taskAttemptIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobTaskAttemptId(hsr, this.jobIdStr, this.taskIdStr, this.taskAttemptIdStr);
    }

    @Test
    public void testGetJobTaskAttemptIdCountersAcls() {
        HttpServletRequest hsr = Mockito.mock(HttpServletRequest.class);
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.ENEMY_USER);
        try {
            hsWebServices.getJobTaskAttemptIdCounters(hsr, this.jobIdStr, this.taskIdStr, this.taskAttemptIdStr);
            Assert.fail("enemy can access job");
        } catch (WebApplicationException e) {
            Assert.assertEquals(UNAUTHORIZED, Status.fromStatusCode(e.getResponse().getStatus()));
        }
        Mockito.when(hsr.getRemoteUser()).thenReturn(TestHsWebServicesAcls.FRIENDLY_USER);
        hsWebServices.getJobTaskAttemptIdCounters(hsr, this.jobIdStr, this.taskIdStr, this.taskAttemptIdStr);
    }

    private static class NullGroupsProvider implements GroupMappingServiceProvider {
        @Override
        public List<String> getGroups(String user) throws IOException {
            return Collections.emptyList();
        }

        @Override
        public void cacheGroupsRefresh() throws IOException {
        }

        @Override
        public void cacheGroupsAdd(List<String> groups) throws IOException {
        }
    }

    private static class MockJobForAcls implements Job {
        private Job mockJob;

        private Configuration conf;

        private Map<JobACL, AccessControlList> jobAcls;

        private JobACLsManager aclsMgr;

        public MockJobForAcls(Job mockJob, Configuration conf) {
            this.mockJob = mockJob;
            this.conf = conf;
            AccessControlList viewAcl = new AccessControlList(TestHsWebServicesAcls.FRIENDLY_USER);
            this.jobAcls = new HashMap<JobACL, AccessControlList>();
            this.jobAcls.put(VIEW_JOB, viewAcl);
            this.aclsMgr = new JobACLsManager(conf);
        }

        @Override
        public JobId getID() {
            return mockJob.getID();
        }

        @Override
        public String getName() {
            return mockJob.getName();
        }

        @Override
        public JobState getState() {
            return mockJob.getState();
        }

        @Override
        public JobReport getReport() {
            return mockJob.getReport();
        }

        @Override
        public Counters getAllCounters() {
            return mockJob.getAllCounters();
        }

        @Override
        public Map<TaskId, Task> getTasks() {
            return mockJob.getTasks();
        }

        @Override
        public Map<TaskId, Task> getTasks(TaskType taskType) {
            return mockJob.getTasks(taskType);
        }

        @Override
        public Task getTask(TaskId taskID) {
            return mockJob.getTask(taskID);
        }

        @Override
        public List<String> getDiagnostics() {
            return mockJob.getDiagnostics();
        }

        @Override
        public int getTotalMaps() {
            return mockJob.getTotalMaps();
        }

        @Override
        public int getTotalReduces() {
            return mockJob.getTotalReduces();
        }

        @Override
        public int getCompletedMaps() {
            return mockJob.getCompletedMaps();
        }

        @Override
        public int getCompletedReduces() {
            return mockJob.getCompletedReduces();
        }

        @Override
        public float getProgress() {
            return mockJob.getProgress();
        }

        @Override
        public boolean isUber() {
            return mockJob.isUber();
        }

        @Override
        public String getUserName() {
            return mockJob.getUserName();
        }

        @Override
        public String getQueueName() {
            return mockJob.getQueueName();
        }

        @Override
        public Path getConfFile() {
            return new Path("/some/path/to/conf");
        }

        @Override
        public Configuration loadConfFile() throws IOException {
            return conf;
        }

        @Override
        public Map<JobACL, AccessControlList> getJobACLs() {
            return jobAcls;
        }

        @Override
        public TaskAttemptCompletionEvent[] getTaskAttemptCompletionEvents(int fromEventId, int maxEvents) {
            return mockJob.getTaskAttemptCompletionEvents(fromEventId, maxEvents);
        }

        @Override
        public TaskCompletionEvent[] getMapAttemptCompletionEvents(int startIndex, int maxEvents) {
            return mockJob.getMapAttemptCompletionEvents(startIndex, maxEvents);
        }

        @Override
        public List<AMInfo> getAMInfos() {
            return mockJob.getAMInfos();
        }

        @Override
        public boolean checkAccess(UserGroupInformation callerUGI, JobACL jobOperation) {
            return aclsMgr.checkAccess(callerUGI, jobOperation, this.getUserName(), jobAcls.get(jobOperation));
        }

        @Override
        public void setQueueName(String queueName) {
        }

        @Override
        public void setJobPriority(Priority priority) {
        }

        @Override
        public int getFailedMaps() {
            return mockJob.getFailedMaps();
        }

        @Override
        public int getFailedReduces() {
            return mockJob.getFailedReduces();
        }

        @Override
        public int getKilledMaps() {
            return mockJob.getKilledMaps();
        }

        @Override
        public int getKilledReduces() {
            return mockJob.getKilledReduces();
        }
    }
}

