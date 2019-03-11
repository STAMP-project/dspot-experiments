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
package org.apache.hadoop.mapred;


import ContainerLauncher.EventType;
import MRConfig.LOCAL_DIR;
import TaskType.MAP;
import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.launcher.ContainerLauncherEvent;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLocalContainerLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(TestLocalContainerLauncher.class);

    private static File testWorkDir;

    private static final String[] localDirs = new String[2];

    @SuppressWarnings("rawtypes")
    @Test(timeout = 10000)
    public void testKillJob() throws Exception {
        JobConf conf = new JobConf();
        AppContext context = Mockito.mock(AppContext.class);
        // a simple event handler solely to detect the container cleaned event
        final CountDownLatch isDone = new CountDownLatch(1);
        EventHandler<Event> handler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                TestLocalContainerLauncher.LOG.info(((("handling event " + (event.getClass())) + " with type ") + (event.getType())));
                if (event instanceof TaskAttemptEvent) {
                    if ((event.getType()) == (TaskAttemptEventType.TA_CONTAINER_CLEANED)) {
                        isDone.countDown();
                    }
                }
            }
        };
        Mockito.when(context.getEventHandler()).thenReturn(handler);
        // create and start the launcher
        LocalContainerLauncher launcher = new LocalContainerLauncher(context, Mockito.mock(TaskUmbilicalProtocol.class));
        launcher.init(conf);
        launcher.start();
        // create mocked job, task, and task attempt
        // a single-mapper job
        JobId jobId = MRBuilderUtils.newJobId(System.currentTimeMillis(), 1, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId taId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getTotalMaps()).thenReturn(1);
        Mockito.when(job.getTotalReduces()).thenReturn(0);
        Map<JobId, Job> jobs = new HashMap<JobId, Job>();
        jobs.put(jobId, job);
        // app context returns the one and only job
        Mockito.when(context.getAllJobs()).thenReturn(jobs);
        org.apache.hadoop.mapreduce.v2.app.job.Task ytask = Mockito.mock(Task.class);
        Mockito.when(ytask.getType()).thenReturn(MAP);
        Mockito.when(job.getTask(taskId)).thenReturn(ytask);
        // create a sleeping mapper that runs beyond the test timeout
        MapTask mapTask = Mockito.mock(MapTask.class);
        Mockito.when(mapTask.isMapOrReduce()).thenReturn(true);
        Mockito.when(mapTask.isMapTask()).thenReturn(true);
        TaskAttemptID taskID = TypeConverter.fromYarn(taId);
        Mockito.when(mapTask.getTaskID()).thenReturn(taskID);
        Mockito.when(mapTask.getJobID()).thenReturn(taskID.getJobID());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // sleep for a long time
                TestLocalContainerLauncher.LOG.info("sleeping for 5 minutes...");
                Thread.sleep(((5 * 60) * 1000));
                return null;
            }
        }).when(mapTask).run(ArgumentMatchers.isA(JobConf.class), ArgumentMatchers.isA(TaskUmbilicalProtocol.class));
        // pump in a task attempt launch event
        ContainerLauncherEvent launchEvent = new org.apache.hadoop.mapreduce.v2.app.launcher.ContainerRemoteLaunchEvent(taId, null, TestLocalContainerLauncher.createMockContainer(), mapTask);
        launcher.handle(launchEvent);
        Thread.sleep(200);
        // now pump in a container clean-up event
        ContainerLauncherEvent cleanupEvent = new ContainerLauncherEvent(taId, null, null, null, EventType.CONTAINER_REMOTE_CLEANUP);
        launcher.handle(cleanupEvent);
        // wait for the event to fire: this should be received promptly
        isDone.await();
        launcher.close();
    }

    @Test
    public void testRenameMapOutputForReduce() throws Exception {
        final JobConf conf = new JobConf();
        final MROutputFiles mrOutputFiles = new MROutputFiles();
        mrOutputFiles.setConf(conf);
        // make sure both dirs are distinct
        // 
        conf.set(LOCAL_DIR, TestLocalContainerLauncher.localDirs[0].toString());
        final Path mapOut = mrOutputFiles.getOutputFileForWrite(1);
        conf.set(LOCAL_DIR, TestLocalContainerLauncher.localDirs[1].toString());
        final Path mapOutIdx = mrOutputFiles.getOutputIndexFileForWrite(1);
        Assert.assertNotEquals("Paths must be different!", mapOut.getParent(), mapOutIdx.getParent());
        // make both dirs part of LOCAL_DIR
        conf.setStrings(LOCAL_DIR, TestLocalContainerLauncher.localDirs);
        final FileContext lfc = FileContext.getLocalFSFileContext(conf);
        lfc.create(mapOut, EnumSet.of(CREATE)).close();
        lfc.create(mapOutIdx, EnumSet.of(CREATE)).close();
        final JobId jobId = MRBuilderUtils.newJobId(12345L, 1, 2);
        final TaskId tid = MRBuilderUtils.newTaskId(jobId, 0, MAP);
        final TaskAttemptId taid = MRBuilderUtils.newTaskAttemptId(tid, 0);
        LocalContainerLauncher.renameMapOutputForReduce(conf, taid, mrOutputFiles);
    }
}

