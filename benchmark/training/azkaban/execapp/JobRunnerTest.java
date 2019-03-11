/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.execapp;


import EventType.JOB_FINISHED;
import EventType.JOB_STARTED;
import EventType.JOB_STATUS_CHANGED;
import JobProperties.JOB_LOG_LAYOUT;
import JobProperties.USER_TO_PROXY;
import Status.DISABLED;
import Status.KILLED;
import Status.READY;
import Status.RUNNING;
import azkaban.event.Event;
import azkaban.executor.ExecutableNode;
import azkaban.executor.MockExecutorLoader;
import azkaban.executor.Status;
import azkaban.jobtype.JobTypeManager;
import azkaban.test.TestUtils;
import azkaban.utils.Props;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class JobRunnerTest {
    public static final String SUBMIT_USER = "testUser";

    private final Logger logger = Logger.getLogger("JobRunnerTest");

    private File workingDir;

    private JobTypeManager jobtypeManager;

    public JobRunnerTest() {
    }

    @Test
    public void testBasicRun() throws IOException {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(1, "testJob", 0, false, loader, eventCollector);
        final ExecutableNode node = runner.getNode();
        eventCollector.handleEvent(Event.create(null, JOB_STARTED, new azkaban.event.EventData(node)));
        Assert.assertTrue((((runner.getStatus()) != (Status.SUCCEEDED)) && ((runner.getStatus()) != (Status.FAILED))));
        runner.run();
        eventCollector.handleEvent(Event.create(null, JOB_FINISHED, new azkaban.event.EventData(node)));
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(("Node status is " + (node.getStatus())), ((node.getStatus()) == (Status.SUCCEEDED)));
        Assert.assertTrue((((node.getStartTime()) >= 0) && ((node.getEndTime()) >= 0)));
        Assert.assertTrue((((node.getEndTime()) - (node.getStartTime())) >= 0));
        final File logFile = new File(runner.getLogFilePath());
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertTrue((outputProps != null));
        checkRequiredJobProperties(runner, logFile);
        try (final BufferedReader br = getLogReader(logFile)) {
            final String firstLine = br.readLine();
            Assert.assertTrue("Unexpected default layout", firstLine.startsWith(new SimpleDateFormat("dd-MM-yyyy").format(new Date())));
        }
        // Verify that user.to.proxy is default to submit user.
        Assert.assertEquals(JobRunnerTest.SUBMIT_USER, runner.getProps().get(USER_TO_PROXY));
        Assert.assertTrue(((loader.getNodeUpdateCount(node.getId())) == 3));
        eventCollector.assertEvents(JOB_STARTED, JOB_STATUS_CHANGED, JOB_FINISHED);
    }

    @Test
    public void testFailedRun() {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(1, "testJob", 0, true, loader, eventCollector);
        final ExecutableNode node = runner.getNode();
        Assert.assertTrue((((runner.getStatus()) != (Status.SUCCEEDED)) || ((runner.getStatus()) != (Status.FAILED))));
        runner.run();
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(((node.getStatus()) == (Status.FAILED)));
        Assert.assertTrue((((node.getStartTime()) > 0) && ((node.getEndTime()) >= 0)));
        final File logFile = new File(runner.getLogFilePath());
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertEquals(0, outputProps.size());
        Assert.assertTrue(logFile.exists());
        Assert.assertTrue(eventCollector.checkOrdering());
        Assert.assertTrue((!(runner.isKilled())));
        Assert.assertTrue(((loader.getNodeUpdateCount(node.getId())) == 3));
        eventCollector.assertEvents(JOB_STARTED, JOB_STATUS_CHANGED, JOB_FINISHED);
    }

    @Test
    public void testDisabledRun() {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(1, "testJob", 0, false, loader, eventCollector);
        final ExecutableNode node = runner.getNode();
        node.setStatus(DISABLED);
        // Should be disabled.
        Assert.assertTrue(((runner.getStatus()) == (Status.DISABLED)));
        runner.run();
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(((node.getStatus()) == (Status.SKIPPED)));
        Assert.assertTrue((((node.getStartTime()) > 0) && ((node.getEndTime()) > 0)));
        // Give it 2000 ms to fail.
        Assert.assertTrue((((node.getEndTime()) - (node.getStartTime())) < 2000));
        // Log file and output files should not exist.
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertTrue((outputProps == null));
        Assert.assertTrue(((runner.getLogFilePath()) == null));
        Assert.assertTrue(eventCollector.checkOrdering());
        Assert.assertTrue(((loader.getNodeUpdateCount(node.getId())) == null));
        eventCollector.assertEvents(JOB_STARTED, JOB_FINISHED);
    }

    @Test
    public void testPreKilledRun() {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(1, "testJob", 0, false, loader, eventCollector);
        final ExecutableNode node = runner.getNode();
        node.setStatus(KILLED);
        // Should be killed.
        Assert.assertTrue(((runner.getStatus()) == (Status.KILLED)));
        runner.run();
        // Should just skip the run and not change
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(((node.getStatus()) == (Status.KILLED)));
        Assert.assertTrue((((node.getStartTime()) > 0) && ((node.getEndTime()) > 0)));
        // Give it 2000 ms to fail.
        Assert.assertTrue((((node.getEndTime()) - (node.getStartTime())) < 2000));
        Assert.assertTrue(((loader.getNodeUpdateCount(node.getId())) == null));
        // Log file and output files should not exist.
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertTrue((outputProps == null));
        Assert.assertTrue(((runner.getLogFilePath()) == null));
        Assert.assertTrue((!(runner.isKilled())));
        eventCollector.assertEvents(JOB_STARTED, JOB_FINISHED);
    }

    @Test
    public void testCancelRun() throws Exception {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(13, "testJob", 10, false, loader, eventCollector);
        final ExecutableNode node = runner.getNode();
        Assert.assertTrue((((runner.getStatus()) != (Status.SUCCEEDED)) || ((runner.getStatus()) != (Status.FAILED))));
        final Thread thread = startThread(runner);
        StatusTestUtils.waitForStatus(node, RUNNING);
        runner.kill();
        assertThreadIsNotAlive(thread);
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(("Status is " + (node.getStatus())), ((node.getStatus()) == (Status.KILLED)));
        Assert.assertTrue((((node.getStartTime()) > 0) && ((node.getEndTime()) > 0)));
        // Give it some time to fail.
        Assert.assertTrue((((node.getEndTime()) - (node.getStartTime())) < 3000));
        Assert.assertTrue(((loader.getNodeUpdateCount(node.getId())) == 3));
        // Log file and output files should not exist.
        final File logFile = new File(runner.getLogFilePath());
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertEquals(0, outputProps.size());
        Assert.assertTrue(logFile.exists());
        Assert.assertTrue(eventCollector.checkOrdering());
        Assert.assertTrue(runner.isKilled());
        eventCollector.assertEvents(JOB_STARTED, JOB_STATUS_CHANGED, JOB_FINISHED);
    }

    @Test
    public void testDelayedExecutionJob() throws Exception {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(1, "testJob", 0, false, loader, eventCollector);
        runner.setDelayStart(10000);
        final long startTime = System.currentTimeMillis();
        final ExecutableNode node = runner.getNode();
        eventCollector.handleEvent(Event.create(null, JOB_STARTED, new azkaban.event.EventData(node)));
        Assert.assertTrue(((runner.getStatus()) != (Status.SUCCEEDED)));
        final Thread thread = startThread(runner);
        // wait for job to get into delayExecution() -> wait()
        assertThreadIsWaiting(thread);
        // Wake up delayExecution() -> wait()
        notifyWaiting(runner);
        assertThreadIsNotAlive(thread);
        eventCollector.handleEvent(Event.create(null, JOB_FINISHED, new azkaban.event.EventData(node)));
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(("Node status is " + (node.getStatus())), ((node.getStatus()) == (Status.SUCCEEDED)));
        Assert.assertTrue((((node.getStartTime()) > 0) && ((node.getEndTime()) > 0)));
        Assert.assertTrue((((node.getEndTime()) - (node.getStartTime())) >= 0));
        Assert.assertTrue((((node.getStartTime()) - startTime) >= 0));
        final File logFile = new File(runner.getLogFilePath());
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertTrue((outputProps != null));
        Assert.assertTrue(logFile.exists());
        Assert.assertFalse(runner.isKilled());
        Assert.assertTrue(((loader.getNodeUpdateCount(node.getId())) == 3));
        Assert.assertTrue(eventCollector.checkOrdering());
        eventCollector.assertEvents(JOB_STARTED, JOB_STATUS_CHANGED, JOB_FINISHED);
    }

    @Test
    public void testDelayedExecutionCancelledJob() throws Exception {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final JobRunner runner = createJobRunner(1, "testJob", 0, false, loader, eventCollector);
        runner.setDelayStart(10000);
        final long startTime = System.currentTimeMillis();
        final ExecutableNode node = runner.getNode();
        eventCollector.handleEvent(Event.create(null, JOB_STARTED, new azkaban.event.EventData(node)));
        Assert.assertTrue(((runner.getStatus()) != (Status.SUCCEEDED)));
        final Thread thread = startThread(runner);
        StatusTestUtils.waitForStatus(node, READY);
        // wait for job to get into delayExecution() -> wait()
        assertThreadIsWaiting(thread);
        runner.kill();
        StatusTestUtils.waitForStatus(node, KILLED);
        eventCollector.handleEvent(Event.create(null, JOB_FINISHED, new azkaban.event.EventData(node)));
        Assert.assertTrue(((runner.getStatus()) == (node.getStatus())));
        Assert.assertTrue(("Node status is " + (node.getStatus())), ((node.getStatus()) == (Status.KILLED)));
        Assert.assertTrue((((node.getStartTime()) > 0) && ((node.getEndTime()) > 0)));
        Assert.assertTrue((((node.getEndTime()) - (node.getStartTime())) < 1000));
        Assert.assertTrue((((node.getStartTime()) - startTime) >= 0));
        Assert.assertTrue((((node.getStartTime()) - startTime) <= 5000));
        Assert.assertTrue(runner.isKilled());
        final File logFile = new File(runner.getLogFilePath());
        final Props outputProps = runner.getNode().getOutputProps();
        Assert.assertTrue((outputProps == null));
        Assert.assertTrue(logFile.exists());
        // wait so that there's time to make the "DB update" for KILLED status
        TestUtils.await().untilAsserted(() -> assertThat(loader.getNodeUpdateCount("testJob")).isEqualTo(2));
        eventCollector.assertEvents(JOB_FINISHED);
    }

    @Test
    public void testCustomLogLayout() throws IOException {
        final MockExecutorLoader loader = new MockExecutorLoader();
        final EventCollectorListener eventCollector = new EventCollectorListener();
        final Props azkabanProps = new Props();
        azkabanProps.put(JOB_LOG_LAYOUT, "TEST %c{1} %p - %m\n");
        final JobRunner runner = createJobRunner(1, "testJob", 0, false, loader, eventCollector, azkabanProps);
        runner.run();
        try (final BufferedReader br = getLogReader(runner.getLogFile())) {
            final String firstLine = br.readLine();
            Assert.assertTrue("Unexpected default layout", firstLine.startsWith("TEST"));
        }
    }
}

