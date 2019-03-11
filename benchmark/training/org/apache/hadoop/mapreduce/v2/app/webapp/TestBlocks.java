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
package org.apache.hadoop.mapreduce.v2.app.webapp;


import AMParams.ATTEMPT_STATE;
import AMParams.JOB_ID;
import AMParams.TASK_ID;
import AMParams.TASK_TYPE;
import MRJobConfUtil.REDACTION_REPLACEMENT_VAL;
import MRJobConfig.MR_JOB_REDACTED_PROPERTIES;
import TaskState.SUCCEEDED;
import TaskType.MAP;
import TaskType.REDUCE;
import View.ViewContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskReport;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.JobIdPBImpl;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.TaskAttemptIdPBImpl;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.TaskIdPBImpl;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.webapp.AttemptsPage.FewAttemptsBlock;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.webapp.view.BlockForTest;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock.Block;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestBlocks {
    private ByteArrayOutputStream data = new ByteArrayOutputStream();

    /**
     * Test rendering for ConfBlock
     */
    @Test
    public void testConfigurationBlock() throws Exception {
        AppContext ctx = Mockito.mock(AppContext.class);
        Job job = Mockito.mock(Job.class);
        Path path = new Path("conf");
        Configuration configuration = new Configuration();
        configuration.set("Key for test", "Value for test");
        final String redactedProp = "Key for redaction";
        configuration.set(MR_JOB_REDACTED_PROPERTIES, redactedProp);
        Mockito.when(job.getConfFile()).thenReturn(path);
        Mockito.when(job.loadConfFile()).thenReturn(configuration);
        Mockito.when(ctx.getJob(ArgumentMatchers.any(JobId.class))).thenReturn(job);
        TestBlocks.ConfBlockForTest configurationBlock = new TestBlocks.ConfBlockForTest(ctx);
        PrintWriter pWriter = new PrintWriter(data);
        Block html = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriter, 0, false);
        configurationBlock.render(html);
        pWriter.flush();
        Assert.assertTrue(data.toString().contains("Sorry, can't do anything without a JobID"));
        configurationBlock.addParameter(JOB_ID, "job_01_01");
        data.reset();
        configurationBlock.render(html);
        pWriter.flush();
        Assert.assertTrue(data.toString().contains("Key for test"));
        Assert.assertTrue(data.toString().contains("Value for test"));
        Assert.assertTrue(data.toString().contains(redactedProp));
        Assert.assertTrue(data.toString().contains(REDACTION_REPLACEMENT_VAL));
    }

    /**
     * Test rendering for TasksBlock
     */
    @Test
    public void testTasksBlock() throws Exception {
        ApplicationId appId = ApplicationIdPBImpl.newInstance(0, 1);
        JobId jobId = new JobIdPBImpl();
        jobId.setId(0);
        jobId.setAppId(appId);
        TaskId taskId = new TaskIdPBImpl();
        taskId.setId(0);
        taskId.setTaskType(MAP);
        taskId.setJobId(jobId);
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getID()).thenReturn(taskId);
        TaskReport report = Mockito.mock(TaskReport.class);
        Mockito.when(report.getProgress()).thenReturn(0.7F);
        Mockito.when(report.getTaskState()).thenReturn(SUCCEEDED);
        Mockito.when(report.getStartTime()).thenReturn(100001L);
        Mockito.when(report.getFinishTime()).thenReturn(100011L);
        Mockito.when(report.getStatus()).thenReturn("Dummy Status \n*");
        Mockito.when(task.getReport()).thenReturn(report);
        Mockito.when(task.getType()).thenReturn(MAP);
        Map<TaskId, Task> tasks = new HashMap<TaskId, Task>();
        tasks.put(taskId, task);
        AppContext ctx = Mockito.mock(AppContext.class);
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getTasks()).thenReturn(tasks);
        App app = new App(ctx);
        app.setJob(job);
        TasksBlockForTest taskBlock = new TasksBlockForTest(app);
        taskBlock.addParameter(TASK_TYPE, "m");
        PrintWriter pWriter = new PrintWriter(data);
        Block html = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriter, 0, false);
        taskBlock.render(html);
        pWriter.flush();
        Assert.assertTrue(data.toString().contains("task_0_0001_m_000000"));
        Assert.assertTrue(data.toString().contains("70.00"));
        Assert.assertTrue(data.toString().contains("SUCCEEDED"));
        Assert.assertTrue(data.toString().contains("100001"));
        Assert.assertTrue(data.toString().contains("100011"));
        Assert.assertFalse(data.toString().contains("Dummy Status \n*"));
        Assert.assertTrue(data.toString().contains("Dummy Status \\n*"));
    }

    /**
     * test AttemptsBlock's rendering.
     */
    @Test
    public void testAttemptsBlock() {
        AppContext ctx = Mockito.mock(AppContext.class);
        AppForTest app = new AppForTest(ctx);
        JobId jobId = new JobIdPBImpl();
        jobId.setId(0);
        jobId.setAppId(ApplicationIdPBImpl.newInstance(0, 1));
        TaskId taskId = new TaskIdPBImpl();
        taskId.setId(0);
        taskId.setTaskType(REDUCE);
        taskId.setJobId(jobId);
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getID()).thenReturn(taskId);
        TaskReport report = Mockito.mock(TaskReport.class);
        Mockito.when(task.getReport()).thenReturn(report);
        Mockito.when(task.getType()).thenReturn(REDUCE);
        Map<TaskId, Task> tasks = new HashMap<TaskId, Task>();
        Map<TaskAttemptId, TaskAttempt> attempts = new HashMap<TaskAttemptId, TaskAttempt>();
        TaskAttempt attempt = Mockito.mock(TaskAttempt.class);
        TaskAttemptId taId = new TaskAttemptIdPBImpl();
        taId.setId(0);
        taId.setTaskId(task.getID());
        Mockito.when(attempt.getID()).thenReturn(taId);
        final TaskAttemptState taState = TaskAttemptState.SUCCEEDED;
        Mockito.when(attempt.getState()).thenReturn(taState);
        TaskAttemptReport taReport = Mockito.mock(TaskAttemptReport.class);
        Mockito.when(taReport.getTaskAttemptState()).thenReturn(taState);
        Mockito.when(attempt.getReport()).thenReturn(taReport);
        attempts.put(taId, attempt);
        tasks.put(taskId, task);
        Mockito.when(task.getAttempts()).thenReturn(attempts);
        app.setTask(task);
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getTasks(REDUCE)).thenReturn(tasks);
        app.setJob(job);
        TestBlocks.AttemptsBlockForTest block = new TestBlocks.AttemptsBlockForTest(app, new Configuration());
        block.addParameter(TASK_TYPE, "r");
        block.addParameter(ATTEMPT_STATE, "SUCCESSFUL");
        PrintWriter pWriter = new PrintWriter(data);
        Block html = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriter, 0, false);
        block.render(html);
        pWriter.flush();
        Assert.assertTrue(data.toString().contains(((("<a href='" + (block.url("task", task.getID().toString()))) + "'>") + "attempt_0_0001_r_000000_0</a>")));
    }

    @Test
    public void testSingleCounterBlock() {
        AppContext appCtx = Mockito.mock(AppContext.class);
        View.ViewContext ctx = Mockito.mock(ViewContext.class);
        JobId jobId = new JobIdPBImpl();
        jobId.setId(0);
        jobId.setAppId(ApplicationIdPBImpl.newInstance(0, 1));
        TaskId mapTaskId = new TaskIdPBImpl();
        mapTaskId.setId(0);
        mapTaskId.setTaskType(MAP);
        mapTaskId.setJobId(jobId);
        Task mapTask = Mockito.mock(Task.class);
        Mockito.when(mapTask.getID()).thenReturn(mapTaskId);
        TaskReport mapReport = Mockito.mock(TaskReport.class);
        Mockito.when(mapTask.getReport()).thenReturn(mapReport);
        Mockito.when(mapTask.getType()).thenReturn(MAP);
        TaskId reduceTaskId = new TaskIdPBImpl();
        reduceTaskId.setId(0);
        reduceTaskId.setTaskType(REDUCE);
        reduceTaskId.setJobId(jobId);
        Task reduceTask = Mockito.mock(Task.class);
        Mockito.when(reduceTask.getID()).thenReturn(reduceTaskId);
        TaskReport reduceReport = Mockito.mock(TaskReport.class);
        Mockito.when(reduceTask.getReport()).thenReturn(reduceReport);
        Mockito.when(reduceTask.getType()).thenReturn(REDUCE);
        Map<TaskId, Task> tasks = new HashMap<TaskId, Task>();
        tasks.put(mapTaskId, mapTask);
        tasks.put(reduceTaskId, reduceTask);
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getTasks()).thenReturn(tasks);
        Mockito.when(appCtx.getJob(ArgumentMatchers.any(JobId.class))).thenReturn(job);
        // SingleCounter for map task
        TestBlocks.SingleCounterBlockForMapTest blockForMapTest = Mockito.spy(new TestBlocks.SingleCounterBlockForMapTest(appCtx, ctx));
        PrintWriter pWriterForMapTest = new PrintWriter(data);
        Block htmlForMapTest = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriterForMapTest, 0, false);
        blockForMapTest.render(htmlForMapTest);
        pWriterForMapTest.flush();
        Assert.assertTrue(data.toString().contains("task_0_0001_m_000000"));
        Assert.assertFalse(data.toString().contains("task_0_0001_r_000000"));
        data.reset();
        // SingleCounter for reduce task
        TestBlocks.SingleCounterBlockForReduceTest blockForReduceTest = Mockito.spy(new TestBlocks.SingleCounterBlockForReduceTest(appCtx, ctx));
        PrintWriter pWriterForReduceTest = new PrintWriter(data);
        Block htmlForReduceTest = new BlockForTest(new TestBlocks.HtmlBlockForTest(), pWriterForReduceTest, 0, false);
        blockForReduceTest.render(htmlForReduceTest);
        pWriterForReduceTest.flush();
        System.out.println(data.toString());
        Assert.assertFalse(data.toString().contains("task_0_0001_m_000000"));
        Assert.assertTrue(data.toString().contains("task_0_0001_r_000000"));
    }

    private class ConfBlockForTest extends ConfBlock {
        private final Map<String, String> params = new HashMap<String, String>();

        public void addParameter(String name, String value) {
            params.put(name, value);
        }

        @Override
        public String $(String key, String defaultValue) {
            String value = params.get(key);
            return value == null ? defaultValue : value;
        }

        ConfBlockForTest(AppContext appCtx) {
            super(appCtx);
        }
    }

    private class HtmlBlockForTest extends HtmlBlock {
        @Override
        protected void render(Block html) {
        }
    }

    private class AttemptsBlockForTest extends FewAttemptsBlock {
        private final Map<String, String> params = new HashMap<String, String>();

        public void addParameter(String name, String value) {
            params.put(name, value);
        }

        public String $(String key, String defaultValue) {
            String value = params.get(key);
            return value == null ? defaultValue : value;
        }

        public AttemptsBlockForTest(App ctx, Configuration conf) {
            super(ctx, conf);
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

    private class SingleCounterBlockForMapTest extends SingleCounterBlock {
        public SingleCounterBlockForMapTest(AppContext appCtx, ViewContext ctx) {
            super(appCtx, ctx);
        }

        public String $(String key, String defaultValue) {
            if (key.equals(TITLE)) {
                return "org.apache.hadoop.mapreduce.JobCounter DATA_LOCAL_MAPS for " + "job_12345_0001";
            } else
                if (key.equals(JOB_ID)) {
                    return "job_12345_0001";
                } else
                    if (key.equals(TASK_ID)) {
                        return "";
                    }


            return "";
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

    private class SingleCounterBlockForReduceTest extends SingleCounterBlock {
        public SingleCounterBlockForReduceTest(AppContext appCtx, ViewContext ctx) {
            super(appCtx, ctx);
        }

        public String $(String key, String defaultValue) {
            if (key.equals(TITLE)) {
                return "org.apache.hadoop.mapreduce.JobCounter DATA_LOCAL_REDUCES " + "for job_12345_0001";
            } else
                if (key.equals(JOB_ID)) {
                    return "job_12345_0001";
                } else
                    if (key.equals(TASK_ID)) {
                        return "";
                    }


            return "";
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
}

