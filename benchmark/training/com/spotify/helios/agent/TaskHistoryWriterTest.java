/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.agent;


import State.CREATING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.spotify.helios.Polling;
import com.spotify.helios.ZooKeeperTestManager;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.TaskStatus;
import com.spotify.helios.common.descriptors.TaskStatusEvent;
import com.spotify.helios.master.ZooKeeperMasterModel;
import com.spotify.helios.servicescommon.coordination.DefaultZooKeeperClient;
import com.spotify.helios.servicescommon.coordination.Paths;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static TaskHistoryWriter.MAX_NUMBER_STATUS_EVENTS_TO_RETAIN;


public class TaskHistoryWriterTest {
    private static final long TIMESTAMP = 8675309L;

    private static final String HOSTNAME = "hostname";

    private static final Job JOB = Job.newBuilder().setCommand(ImmutableList.of()).setImage("image").setName("foo").setVersion("version").build();

    private static final JobId JOB_ID = TaskHistoryWriterTest.JOB.getId();

    private static final TaskStatus TASK_STATUS = TaskStatus.newBuilder().setState(CREATING).setJob(TaskHistoryWriterTest.JOB).setGoal(START).setContainerId("containerId").build();

    private ZooKeeperTestManager zk;

    private DefaultZooKeeperClient client;

    private TaskHistoryWriter writer;

    private ZooKeeperMasterModel masterModel;

    private Path agentStateDirs;

    @Test
    public void testZooKeeperErrorDoesntLoseItemsReally() throws Exception {
        final ZooKeeperClient mockClient = Mockito.mock(ZooKeeperClient.class, AdditionalAnswers.delegatesTo(client));
        final String path = Paths.historyJobHostEventsTimestamp(TaskHistoryWriterTest.JOB_ID, TaskHistoryWriterTest.HOSTNAME, TaskHistoryWriterTest.TIMESTAMP);
        // make save operations fail
        final AtomicBoolean throwExceptionOnCreateAndSet = new AtomicBoolean(true);
        final KeeperException exc = new ConnectionLossException();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (throwExceptionOnCreateAndSet.get()) {
                    throw exc;
                } else {
                    client.createAndSetData(((String) (invocation.getArguments()[0])), ((byte[]) (invocation.getArguments()[1])));
                    return null;
                }
            }
        }).when(mockClient).createAndSetData(path, TaskHistoryWriterTest.TASK_STATUS.toJsonBytes());
        makeWriter(mockClient);
        writer.saveHistoryItem(TaskHistoryWriterTest.TASK_STATUS, TaskHistoryWriterTest.TIMESTAMP);
        // wait up to 10s for it to fail twice -- and make sure I mocked it correctly.
        Mockito.verify(mockClient, Mockito.timeout(10000).atLeast(2)).createAndSetData(path, TaskHistoryWriterTest.TASK_STATUS.toJsonBytes());
        // now make the client work
        throwExceptionOnCreateAndSet.set(false);
        awaitHistoryItems();
    }

    @Test
    public void testSimpleWorkage() throws Exception {
        writer.saveHistoryItem(TaskHistoryWriterTest.TASK_STATUS, TaskHistoryWriterTest.TIMESTAMP);
        final TaskStatusEvent historyItem = Iterables.getOnlyElement(awaitHistoryItems());
        Assert.assertEquals(TaskHistoryWriterTest.JOB_ID, historyItem.getStatus().getJob().getId());
    }

    @Test
    public void testWriteWithZooKeeperDown() throws Exception {
        zk.stop();
        writer.saveHistoryItem(TaskHistoryWriterTest.TASK_STATUS, TaskHistoryWriterTest.TIMESTAMP);
        zk.start();
        final TaskStatusEvent historyItem = Iterables.getOnlyElement(awaitHistoryItems());
        Assert.assertEquals(TaskHistoryWriterTest.JOB_ID, historyItem.getStatus().getJob().getId());
    }

    @Test
    public void testWriteWithZooKeeperDownAndInterveningCrash() throws Exception {
        zk.stop();
        writer.saveHistoryItem(TaskHistoryWriterTest.TASK_STATUS, TaskHistoryWriterTest.TIMESTAMP);
        // simulate a crash by recreating the writer
        writer.stopAsync().awaitTerminated();
        makeWriter(client);
        zk.start();
        final TaskStatusEvent historyItem = Iterables.getOnlyElement(awaitHistoryItems());
        Assert.assertEquals(TaskHistoryWriterTest.JOB_ID, historyItem.getStatus().getJob().getId());
    }

    @Test
    public void testKeepsNoMoreThanMaxHistoryItems() throws Exception {
        // And that it keeps the correct items!
        // Save a superflouous number of events
        for (int i = 0; i < ((MAX_NUMBER_STATUS_EVENTS_TO_RETAIN) + 20); i++) {
            writer.saveHistoryItem(TaskHistoryWriterTest.TASK_STATUS, ((TaskHistoryWriterTest.TIMESTAMP) + i));
            Thread.sleep(50);// just to allow other stuff a chance to run in the background

        }
        // Should converge to 30 items
        List<TaskStatusEvent> events = Polling.await(1, TimeUnit.MINUTES, new Callable<List<TaskStatusEvent>>() {
            @Override
            public List<TaskStatusEvent> call() throws Exception {
                if (!(writer.isEmpty())) {
                    return null;
                }
                final List<TaskStatusEvent> events = masterModel.getJobHistory(TaskHistoryWriterTest.JOB_ID);
                if ((events.size()) == (TaskHistoryWriter.MAX_NUMBER_STATUS_EVENTS_TO_RETAIN)) {
                    return events;
                }
                return null;
            }
        });
        Assert.assertEquals((((TaskHistoryWriterTest.TIMESTAMP) + (MAX_NUMBER_STATUS_EVENTS_TO_RETAIN)) + 19), Iterables.getLast(events).getTimestamp());
        Assert.assertEquals(((TaskHistoryWriterTest.TIMESTAMP) + 20), Iterables.get(events, 0).getTimestamp());
    }
}

