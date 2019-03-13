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


import JobState.SUCCEEDED;
import Service.STATE.STARTED;
import Service.STATE.STOPPED;
import TaskType.MAP;
import TaskType.REDUCE;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryEvent;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryEventHandler;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MRApp;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.event.EventHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestJobHistoryEvents {
    private static final Logger LOG = LoggerFactory.getLogger(TestJobHistoryEvents.class);

    @Test
    public void testHistoryEvents() throws Exception {
        Configuration conf = new Configuration();
        MRApp app = new TestJobHistoryEvents.MRAppWithHistory(2, 1, true, this.getClass().getName(), true);
        app.submit(conf);
        Job job = app.getContext().getAllJobs().values().iterator().next();
        JobId jobId = job.getID();
        TestJobHistoryEvents.LOG.info(("JOBID is " + (TypeConverter.fromYarn(jobId).toString())));
        app.waitForState(job, SUCCEEDED);
        // make sure all events are flushed
        app.waitForState(STOPPED);
        /* Use HistoryContext to read logged events and verify the number of 
        completed maps
         */
        HistoryContext context = new JobHistory();
        // test start and stop states
        ((JobHistory) (context)).init(conf);
        start();
        Assert.assertTrue(((context.getStartTime()) > 0));
        Assert.assertEquals(getServiceState(), STARTED);
        // get job before stopping JobHistory
        Job parsedJob = context.getJob(jobId);
        // stop JobHistory
        stop();
        Assert.assertEquals(getServiceState(), STOPPED);
        Assert.assertEquals("CompletedMaps not correct", 2, parsedJob.getCompletedMaps());
        Assert.assertEquals(System.getProperty("user.name"), parsedJob.getUserName());
        Map<TaskId, Task> tasks = parsedJob.getTasks();
        Assert.assertEquals("No of tasks not correct", 3, tasks.size());
        for (Task task : tasks.values()) {
            verifyTask(task);
        }
        Map<TaskId, Task> maps = parsedJob.getTasks(MAP);
        Assert.assertEquals("No of maps not correct", 2, maps.size());
        Map<TaskId, Task> reduces = parsedJob.getTasks(REDUCE);
        Assert.assertEquals("No of reduces not correct", 1, reduces.size());
        Assert.assertEquals("CompletedReduce not correct", 1, parsedJob.getCompletedReduces());
        Assert.assertEquals("Job state not currect", SUCCEEDED, parsedJob.getState());
    }

    /**
     * Verify that all the events are flushed on stopping the HistoryHandler
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEventsFlushOnStop() throws Exception {
        Configuration conf = new Configuration();
        MRApp app = new TestJobHistoryEvents.MRAppWithSpecialHistoryHandler(1, 0, true, this.getClass().getName(), true);
        app.submit(conf);
        Job job = app.getContext().getAllJobs().values().iterator().next();
        JobId jobId = job.getID();
        TestJobHistoryEvents.LOG.info(("JOBID is " + (TypeConverter.fromYarn(jobId).toString())));
        app.waitForState(job, SUCCEEDED);
        // make sure all events are flushed
        app.waitForState(STOPPED);
        /* Use HistoryContext to read logged events and verify the number of
        completed maps
         */
        HistoryContext context = new JobHistory();
        ((JobHistory) (context)).init(conf);
        Job parsedJob = context.getJob(jobId);
        Assert.assertEquals("CompletedMaps not correct", 1, parsedJob.getCompletedMaps());
        Map<TaskId, Task> tasks = parsedJob.getTasks();
        Assert.assertEquals("No of tasks not correct", 1, tasks.size());
        verifyTask(tasks.values().iterator().next());
        Map<TaskId, Task> maps = parsedJob.getTasks(MAP);
        Assert.assertEquals("No of maps not correct", 1, maps.size());
        Assert.assertEquals("Job state not currect", SUCCEEDED, parsedJob.getState());
    }

    @Test
    public void testJobHistoryEventHandlerIsFirstServiceToStop() {
        MRApp app = new TestJobHistoryEvents.MRAppWithSpecialHistoryHandler(1, 0, true, this.getClass().getName(), true);
        Configuration conf = new Configuration();
        app.init(conf);
        Service[] services = app.getServices().toArray(new Service[0]);
        // Verifying that it is the last to be added is same as verifying that it is
        // the first to be stopped. CompositeService related tests already validate
        // this.
        Assert.assertEquals("JobHistoryEventHandler", services[((services.length) - 1)].getName());
    }

    @Test
    public void testAssignedQueue() throws Exception {
        Configuration conf = new Configuration();
        MRApp app = new TestJobHistoryEvents.MRAppWithHistory(2, 1, true, this.getClass().getName(), true, "assignedQueue");
        app.submit(conf);
        Job job = app.getContext().getAllJobs().values().iterator().next();
        JobId jobId = job.getID();
        TestJobHistoryEvents.LOG.info(("JOBID is " + (TypeConverter.fromYarn(jobId).toString())));
        app.waitForState(job, SUCCEEDED);
        // make sure all events are flushed
        app.waitForState(STOPPED);
        /* Use HistoryContext to read logged events and verify the number of 
        completed maps
         */
        HistoryContext context = new JobHistory();
        // test start and stop states
        ((JobHistory) (context)).init(conf);
        start();
        Assert.assertTrue(((context.getStartTime()) > 0));
        Assert.assertEquals(getServiceState(), STARTED);
        // get job before stopping JobHistory
        Job parsedJob = context.getJob(jobId);
        // stop JobHistory
        stop();
        Assert.assertEquals(getServiceState(), STOPPED);
        Assert.assertEquals("QueueName not correct", "assignedQueue", parsedJob.getQueueName());
    }

    static class MRAppWithHistory extends MRApp {
        public MRAppWithHistory(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        public MRAppWithHistory(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, String assignedQueue) {
            super(maps, reduces, autoComplete, testName, cleanOnStart, assignedQueue);
        }

        @Override
        protected EventHandler<JobHistoryEvent> createJobHistoryHandler(AppContext context) {
            return new JobHistoryEventHandler(context, getStartCount());
        }
    }

    /**
     * MRapp with special HistoryEventHandler that writes events only during stop.
     * This is to simulate events that don't get written by the eventHandling
     * thread due to say a slow DFS and verify that they are flushed during stop.
     */
    private static class MRAppWithSpecialHistoryHandler extends MRApp {
        public MRAppWithSpecialHistoryHandler(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        @Override
        protected EventHandler<JobHistoryEvent> createJobHistoryHandler(AppContext context) {
            return new JobHistoryEventHandler(context, getStartCount()) {
                @Override
                protected void serviceStart() {
                    // Don't start any event draining thread.
                    super.eventHandlingThread = new Thread();
                    start();
                }
            };
        }
    }
}

