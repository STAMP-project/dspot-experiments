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


import AMParams.ATTEMPT_STATE;
import AMParams.JOB_ID;
import AMParams.TASK_ID;
import AMParams.TASK_TYPE;
import Params.TITLE;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobACL;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.TaskAttemptIdPBImpl;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.webapp.App;
import org.apache.hadoop.mapreduce.v2.app.webapp.AppForTest;
import org.apache.hadoop.mapreduce.v2.hs.webapp.HsTaskPage.AttemptsBlock;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.webapp.Controller;
import org.apache.hadoop.yarn.webapp.Controller.RequestContext;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.webapp.View.ViewContext;
import org.apache.hadoop.yarn.webapp.log.AggregatedLogsPage;
import org.apache.hadoop.yarn.webapp.view.BlockForTest;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock.Block;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test some HtmlBlock classes
 */
public class TestBlocks {
    private ByteArrayOutputStream data = new ByteArrayOutputStream();

    @Test
    public void testPullTaskLink() {
        Task task = getTask(0);
        String taskId = task.getID().toString();
        Assert.assertEquals("pull links doesn't work correctly", (((("Task failed <a href=\"/jobhistory/task/" + taskId) + "\">") + taskId) + "</a>"), HsJobBlock.addTaskLinks(("Task failed " + taskId)));
        Assert.assertEquals("pull links doesn't work correctly", (((("Task failed <a href=\"/jobhistory/task/" + taskId) + "\">") + taskId) + "</a>\n Job failed as tasks failed. failedMaps:1 failedReduces:0"), HsJobBlock.addTaskLinks(((("Task failed " + taskId) + "\n ") + "Job failed as tasks failed. failedMaps:1 failedReduces:0")));
    }

    /**
     * test HsTasksBlock's rendering.
     */
    @Test
    public void testHsTasksBlock() {
        Task task = getTask(0);
        Map<TaskId, Task> tasks = new HashMap<TaskId, Task>();
        tasks.put(task.getID(), task);
        AppContext ctx = Mockito.mock(AppContext.class);
        AppForTest app = new AppForTest(ctx);
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getTasks()).thenReturn(tasks);
        app.setJob(job);
        TestBlocks.HsTasksBlockForTest block = new TestBlocks.HsTasksBlockForTest(app);
        block.addParameter(TASK_TYPE, "r");
        PrintWriter pWriter = new PrintWriter(data);
        Block html = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriter, 0, false);
        block.render(html);
        pWriter.flush();
        // should be printed information about task
        Assert.assertTrue(data.toString().contains("task_0_0001_r_000000"));
        Assert.assertTrue(data.toString().contains("SUCCEEDED"));
        Assert.assertTrue(data.toString().contains("100001"));
        Assert.assertTrue(data.toString().contains("100011"));
        Assert.assertTrue(data.toString().contains(""));
    }

    /**
     * test AttemptsBlock's rendering.
     */
    @Test
    public void testAttemptsBlock() {
        AppContext ctx = Mockito.mock(AppContext.class);
        AppForTest app = new AppForTest(ctx);
        Task task = getTask(0);
        Map<TaskAttemptId, TaskAttempt> attempts = new HashMap<TaskAttemptId, TaskAttempt>();
        TaskAttempt attempt = Mockito.mock(TaskAttempt.class);
        TaskAttemptId taId = new TaskAttemptIdPBImpl();
        taId.setId(0);
        taId.setTaskId(task.getID());
        Mockito.when(attempt.getID()).thenReturn(taId);
        Mockito.when(attempt.getNodeHttpAddress()).thenReturn("Node address");
        ApplicationId appId = ApplicationIdPBImpl.newInstance(0, 5);
        ApplicationAttemptId appAttemptId = ApplicationAttemptIdPBImpl.newInstance(appId, 1);
        ContainerId containerId = ContainerIdPBImpl.newContainerId(appAttemptId, 1);
        Mockito.when(attempt.getAssignedContainerID()).thenReturn(containerId);
        Mockito.when(attempt.getAssignedContainerMgrAddress()).thenReturn("assignedContainerMgrAddress");
        Mockito.when(attempt.getNodeRackName()).thenReturn("nodeRackName");
        final long taStartTime = 100002L;
        final long taFinishTime = 100012L;
        final long taShuffleFinishTime = 100010L;
        final long taSortFinishTime = 100011L;
        final TaskAttemptState taState = TaskAttemptState.SUCCEEDED;
        Mockito.when(attempt.getLaunchTime()).thenReturn(taStartTime);
        Mockito.when(attempt.getFinishTime()).thenReturn(taFinishTime);
        Mockito.when(attempt.getShuffleFinishTime()).thenReturn(taShuffleFinishTime);
        Mockito.when(attempt.getSortFinishTime()).thenReturn(taSortFinishTime);
        Mockito.when(attempt.getState()).thenReturn(taState);
        TaskAttemptReport taReport = Mockito.mock(TaskAttemptReport.class);
        Mockito.when(taReport.getStartTime()).thenReturn(taStartTime);
        Mockito.when(taReport.getFinishTime()).thenReturn(taFinishTime);
        Mockito.when(taReport.getShuffleFinishTime()).thenReturn(taShuffleFinishTime);
        Mockito.when(taReport.getSortFinishTime()).thenReturn(taSortFinishTime);
        Mockito.when(taReport.getContainerId()).thenReturn(containerId);
        Mockito.when(taReport.getProgress()).thenReturn(1.0F);
        Mockito.when(taReport.getStateString()).thenReturn("Processed 128/128 records <p> \n");
        Mockito.when(taReport.getTaskAttemptState()).thenReturn(taState);
        Mockito.when(taReport.getDiagnosticInfo()).thenReturn("");
        Mockito.when(attempt.getReport()).thenReturn(taReport);
        attempts.put(taId, attempt);
        Mockito.when(task.getAttempts()).thenReturn(attempts);
        app.setTask(task);
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getUserName()).thenReturn("User");
        app.setJob(job);
        TestBlocks.AttemptsBlockForTest block = new TestBlocks.AttemptsBlockForTest(app);
        block.addParameter(TASK_TYPE, "r");
        PrintWriter pWriter = new PrintWriter(data);
        Block html = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriter, 0, false);
        block.render(html);
        pWriter.flush();
        // should be printed information about attempts
        Assert.assertTrue(data.toString().contains("attempt_0_0001_r_000000_0"));
        Assert.assertTrue(data.toString().contains("SUCCEEDED"));
        Assert.assertFalse(data.toString().contains("Processed 128/128 records <p> \n"));
        Assert.assertTrue(data.toString().contains("Processed 128\\/128 records &lt;p&gt; \\n"));
        Assert.assertTrue(data.toString().contains("_0005_01_000001:attempt_0_0001_r_000000_0:User:"));
        Assert.assertTrue(data.toString().contains("100002"));
        Assert.assertTrue(data.toString().contains("100010"));
        Assert.assertTrue(data.toString().contains("100011"));
        Assert.assertTrue(data.toString().contains("100012"));
    }

    /**
     * test HsJobsBlock's rendering.
     */
    @Test
    public void testHsJobsBlock() {
        AppContext ctx = Mockito.mock(AppContext.class);
        Map<JobId, Job> jobs = new HashMap<JobId, Job>();
        Job job = getJob();
        jobs.put(job.getID(), job);
        Mockito.when(ctx.getAllJobs()).thenReturn(jobs);
        Controller.RequestContext rc = Mockito.mock(RequestContext.class);
        ViewContext view = Mockito.mock(ViewContext.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(rc.getRequest()).thenReturn(req);
        Mockito.when(view.requestContext()).thenReturn(rc);
        Configuration conf = new Configuration();
        HsJobsBlock block = new TestBlocks.HsJobsBlockForTest(conf, ctx, view);
        PrintWriter pWriter = new PrintWriter(data);
        Block html = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriter, 0, false);
        block.render(html);
        pWriter.flush();
        Assert.assertTrue(data.toString().contains("JobName"));
        Assert.assertTrue(data.toString().contains("UserName"));
        Assert.assertTrue(data.toString().contains("QueueName"));
        Assert.assertTrue(data.toString().contains("SUCCEEDED"));
    }

    /**
     * test HsController
     */
    @Test
    public void testHsController() throws Exception {
        AppContext ctx = Mockito.mock(AppContext.class);
        ApplicationId appId = ApplicationIdPBImpl.newInstance(0, 5);
        Mockito.when(ctx.getApplicationID()).thenReturn(appId);
        AppForTest app = new AppForTest(ctx);
        Configuration config = new Configuration();
        RequestContext requestCtx = Mockito.mock(RequestContext.class);
        TestBlocks.HsControllerForTest controller = new TestBlocks.HsControllerForTest(app, config, requestCtx);
        index();
        Assert.assertEquals("JobHistory", controller.get(TITLE, ""));
        Assert.assertEquals(HsJobPage.class, jobPage());
        Assert.assertEquals(HsCountersPage.class, countersPage());
        Assert.assertEquals(HsTasksPage.class, tasksPage());
        Assert.assertEquals(HsTaskPage.class, taskPage());
        Assert.assertEquals(HsAttemptsPage.class, attemptsPage());
        controller.set(JOB_ID, "job_01_01");
        controller.set(TASK_ID, "task_01_01_m_01");
        controller.set(TASK_TYPE, "m");
        controller.set(ATTEMPT_STATE, "State");
        Job job = Mockito.mock(Job.class);
        Task task = Mockito.mock(Task.class);
        Mockito.when(job.getTask(ArgumentMatchers.any(TaskId.class))).thenReturn(task);
        JobId jobID = MRApps.toJobID("job_01_01");
        Mockito.when(ctx.getJob(jobID)).thenReturn(job);
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(true);
        job();
        Assert.assertEquals(HsJobPage.class, controller.getClazz());
        jobCounters();
        Assert.assertEquals(HsCountersPage.class, controller.getClazz());
        taskCounters();
        Assert.assertEquals(HsCountersPage.class, controller.getClazz());
        tasks();
        Assert.assertEquals(HsTasksPage.class, controller.getClazz());
        task();
        Assert.assertEquals(HsTaskPage.class, controller.getClazz());
        attempts();
        Assert.assertEquals(HsAttemptsPage.class, controller.getClazz());
        Assert.assertEquals(HsConfPage.class, confPage());
        Assert.assertEquals(HsAboutPage.class, aboutPage());
        about();
        Assert.assertEquals(HsAboutPage.class, controller.getClazz());
        logs();
        Assert.assertEquals(HsLogsPage.class, controller.getClazz());
        nmlogs();
        Assert.assertEquals(AggregatedLogsPage.class, controller.getClazz());
        Assert.assertEquals(HsSingleCounterPage.class, singleCounterPage());
        singleJobCounter();
        Assert.assertEquals(HsSingleCounterPage.class, controller.getClazz());
        singleTaskCounter();
        Assert.assertEquals(HsSingleCounterPage.class, controller.getClazz());
    }

    private static class HsControllerForTest extends HsController {
        private static Map<String, String> params = new HashMap<String, String>();

        private Class<?> clazz;

        ByteArrayOutputStream data = new ByteArrayOutputStream();

        public void set(String name, String value) {
            TestBlocks.HsControllerForTest.params.put(name, value);
        }

        public String get(String key, String defaultValue) {
            String value = TestBlocks.HsControllerForTest.params.get(key);
            return value == null ? defaultValue : value;
        }

        HsControllerForTest(App app, Configuration configuration, RequestContext ctx) {
            super(app, configuration, ctx);
        }

        @Override
        public HttpServletRequest request() {
            HttpServletRequest result = Mockito.mock(HttpServletRequest.class);
            Mockito.when(result.getRemoteUser()).thenReturn("User");
            return result;
        }

        public HttpServletResponse response() {
            HttpServletResponse result = Mockito.mock(HttpServletResponse.class);
            try {
                Mockito.when(result.getWriter()).thenReturn(new PrintWriter(data));
            } catch (IOException ignored) {
            }
            return result;
        }

        protected void render(Class<? extends View> cls) {
            clazz = cls;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }

    private class HsJobsBlockForTest extends HsJobsBlock {
        HsJobsBlockForTest(Configuration conf, AppContext appCtx, ViewContext view) {
            super(conf, appCtx, view);
        }

        @Override
        public String url(String... parts) {
            String result = "url://";
            for (String string : parts) {
                result += string + ":";
            }
            return result;
        }
    }

    private class AttemptsBlockForTest extends AttemptsBlock {
        private final Map<String, String> params = new HashMap<String, String>();

        public void addParameter(String name, String value) {
            params.put(name, value);
        }

        public String $(String key, String defaultValue) {
            String value = params.get(key);
            return value == null ? defaultValue : value;
        }

        public AttemptsBlockForTest(App ctx) {
            super(ctx);
        }

        @Override
        public String url(String... parts) {
            String result = "url://";
            for (String string : parts) {
                result += string + ":";
            }
            return result;
        }
    }

    private class HsTasksBlockForTest extends HsTasksBlock {
        private final Map<String, String> params = new HashMap<String, String>();

        public void addParameter(String name, String value) {
            params.put(name, value);
        }

        public String $(String key, String defaultValue) {
            String value = params.get(key);
            return value == null ? defaultValue : value;
        }

        @Override
        public String url(String... parts) {
            String result = "url://";
            for (String string : parts) {
                result += string + ":";
            }
            return result;
        }

        public HsTasksBlockForTest(App app) {
            super(app);
        }
    }

    private class HtmlBlockForTest extends HtmlBlock {
        @Override
        protected void render(Block html) {
        }
    }
}

