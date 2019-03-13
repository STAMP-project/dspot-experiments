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
package org.apache.flink.runtime.webmonitor;


import ConfigConstants.DEFAULT_CHARSET;
import ConfigConstants.LOCAL_START_WEBSERVER;
import HttpResponseStatus.ACCEPTED;
import HttpResponseStatus.OK;
import WebMonitorUtils.LogFileLocation;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobgraph.tasks.StoppableTask;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.runtime.webmonitor.testutils.HttpTestClient;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.FiniteDuration;


/**
 * Tests for the WebFrontend.
 */
public class WebFrontendITCase extends TestLogger {
    private static final int NUM_TASK_MANAGERS = 2;

    private static final int NUM_SLOTS = 4;

    private static final Configuration CLUSTER_CONFIGURATION = WebFrontendITCase.getClusterConfiguration();

    @ClassRule
    public static final MiniClusterWithClientResource CLUSTER = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().setConfiguration(WebFrontendITCase.CLUSTER_CONFIGURATION).setNumberTaskManagers(WebFrontendITCase.NUM_TASK_MANAGERS).setNumberSlotsPerTaskManager(WebFrontendITCase.NUM_SLOTS).build());

    @Test
    public void getFrontPage() {
        try {
            String fromHTTP = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/index.html"));
            String text = "Apache Flink Dashboard";
            Assert.assertTrue(("Startpage should contain " + text), fromHTTP.contains(text));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testResponseHeaders() throws Exception {
        // check headers for successful json response
        URL taskManagersUrl = new URL((("http://localhost:" + (getRestPort())) + "/taskmanagers"));
        HttpURLConnection taskManagerConnection = ((HttpURLConnection) (taskManagersUrl.openConnection()));
        taskManagerConnection.setConnectTimeout(100000);
        taskManagerConnection.connect();
        if ((taskManagerConnection.getResponseCode()) >= 400) {
            // error!
            InputStream is = taskManagerConnection.getErrorStream();
            String errorMessage = IOUtils.toString(is, DEFAULT_CHARSET);
            throw new RuntimeException(errorMessage);
        }
        // we don't set the content-encoding header
        Assert.assertNull(taskManagerConnection.getContentEncoding());
        Assert.assertEquals("application/json; charset=UTF-8", taskManagerConnection.getContentType());
        // check headers in case of an error
        URL notFoundJobUrl = new URL((("http://localhost:" + (getRestPort())) + "/jobs/dontexist"));
        HttpURLConnection notFoundJobConnection = ((HttpURLConnection) (notFoundJobUrl.openConnection()));
        notFoundJobConnection.setConnectTimeout(100000);
        notFoundJobConnection.connect();
        if ((notFoundJobConnection.getResponseCode()) >= 400) {
            // we don't set the content-encoding header
            Assert.assertNull(notFoundJobConnection.getContentEncoding());
            Assert.assertEquals("application/json; charset=UTF-8", notFoundJobConnection.getContentType());
        } else {
            throw new RuntimeException("Request for non-existing job did not return an error.");
        }
    }

    @Test
    public void getNumberOfTaskManagers() {
        try {
            String json = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/taskmanagers/"));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(json);
            ArrayNode taskManagers = ((ArrayNode) (response.get("taskmanagers")));
            Assert.assertNotNull(taskManagers);
            Assert.assertEquals(WebFrontendITCase.NUM_TASK_MANAGERS, taskManagers.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getTaskmanagers() throws Exception {
        String json = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/taskmanagers/"));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode parsed = mapper.readTree(json);
        ArrayNode taskManagers = ((ArrayNode) (parsed.get("taskmanagers")));
        Assert.assertNotNull(taskManagers);
        Assert.assertEquals(WebFrontendITCase.NUM_TASK_MANAGERS, taskManagers.size());
        JsonNode taskManager = taskManagers.get(0);
        Assert.assertNotNull(taskManager);
        Assert.assertEquals(WebFrontendITCase.NUM_SLOTS, taskManager.get("slotsNumber").asInt());
        Assert.assertTrue(((taskManager.get("freeSlots").asInt()) <= (WebFrontendITCase.NUM_SLOTS)));
    }

    @Test
    public void getLogAndStdoutFiles() throws Exception {
        WebMonitorUtils.LogFileLocation logFiles = LogFileLocation.find(WebFrontendITCase.CLUSTER_CONFIGURATION);
        FileUtils.writeStringToFile(logFiles.logFile, "job manager log");
        String logs = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/jobmanager/log"));
        Assert.assertTrue(logs.contains("job manager log"));
        FileUtils.writeStringToFile(logFiles.stdOutFile, "job manager out");
        logs = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/jobmanager/stdout"));
        Assert.assertTrue(logs.contains("job manager out"));
    }

    @Test
    public void getTaskManagerLogAndStdoutFiles() {
        try {
            String json = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/taskmanagers/"));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode parsed = mapper.readTree(json);
            ArrayNode taskManagers = ((ArrayNode) (parsed.get("taskmanagers")));
            JsonNode taskManager = taskManagers.get(0);
            String id = taskManager.get("id").asText();
            WebMonitorUtils.LogFileLocation logFiles = LogFileLocation.find(WebFrontendITCase.CLUSTER_CONFIGURATION);
            // we check for job manager log files, since no separate taskmanager logs exist
            FileUtils.writeStringToFile(logFiles.logFile, "job manager log");
            String logs = TestBaseUtils.getFromHTTP((((("http://localhost:" + (getRestPort())) + "/taskmanagers/") + id) + "/log"));
            Assert.assertTrue(logs.contains("job manager log"));
            FileUtils.writeStringToFile(logFiles.stdOutFile, "job manager out");
            logs = TestBaseUtils.getFromHTTP((((("http://localhost:" + (getRestPort())) + "/taskmanagers/") + id) + "/stdout"));
            Assert.assertTrue(logs.contains("job manager out"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getConfiguration() {
        try {
            String config = TestBaseUtils.getFromHTTP((("http://localhost:" + (getRestPort())) + "/jobmanager/config"));
            Map<String, String> conf = WebMonitorUtils.fromKeyValueJsonArray(config);
            Assert.assertEquals(WebFrontendITCase.CLUSTER_CONFIGURATION.getString(LOCAL_START_WEBSERVER, null), conf.get(LOCAL_START_WEBSERVER));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testStop() throws Exception {
        // this only works if there is no active job at this point
        Assert.assertTrue(WebFrontendITCase.getRunningJobs(WebFrontendITCase.CLUSTER.getClusterClient()).isEmpty());
        // Create a task
        final JobVertex sender = new JobVertex("Sender");
        sender.setParallelism(2);
        sender.setInvokableClass(WebFrontendITCase.BlockingInvokable.class);
        final JobGraph jobGraph = new JobGraph("Stoppable streaming test job", sender);
        final JobID jid = jobGraph.getJobID();
        ClusterClient<?> clusterClient = WebFrontendITCase.CLUSTER.getClusterClient();
        clusterClient.setDetached(true);
        clusterClient.submitJob(jobGraph, WebFrontendITCase.class.getClassLoader());
        // wait for job to show up
        while (WebFrontendITCase.getRunningJobs(WebFrontendITCase.CLUSTER.getClusterClient()).isEmpty()) {
            Thread.sleep(10);
        } 
        // wait for tasks to be properly running
        WebFrontendITCase.BlockingInvokable.latch.await();
        final FiniteDuration testTimeout = new FiniteDuration(2, TimeUnit.MINUTES);
        final Deadline deadline = testTimeout.fromNow();
        try (HttpTestClient client = new HttpTestClient("localhost", getRestPort())) {
            // stop the job
            client.sendPatchRequest((("/jobs/" + jid) + "/?mode=stop"), deadline.timeLeft());
            HttpTestClient.SimpleHttpResponse response = client.getNextResponse(deadline.timeLeft());
            Assert.assertEquals(ACCEPTED, response.getStatus());
            Assert.assertEquals("application/json; charset=UTF-8", response.getType());
            Assert.assertEquals("{}", response.getContent());
        }
        // wait for cancellation to finish
        while (!(WebFrontendITCase.getRunningJobs(WebFrontendITCase.CLUSTER.getClusterClient()).isEmpty())) {
            Thread.sleep(20);
        } 
        // ensure we can access job details when its finished (FLINK-4011)
        try (HttpTestClient client = new HttpTestClient("localhost", getRestPort())) {
            FiniteDuration timeout = new FiniteDuration(30, TimeUnit.SECONDS);
            client.sendGetRequest((("/jobs/" + jid) + "/config"), timeout);
            HttpTestClient.SimpleHttpResponse response = client.getNextResponse(timeout);
            Assert.assertEquals(OK, response.getStatus());
            Assert.assertEquals("application/json; charset=UTF-8", response.getType());
            Assert.assertEquals((((("{\"jid\":\"" + jid) + "\",\"name\":\"Stoppable streaming test job\",") + "\"execution-config\":{\"execution-mode\":\"PIPELINED\",\"restart-strategy\":\"Cluster level default restart strategy\",") + "\"job-parallelism\":-1,\"object-reuse-mode\":false,\"user-config\":{}}}"), response.getContent());
        }
        WebFrontendITCase.BlockingInvokable.reset();
    }

    @Test
    public void testStopYarn() throws Exception {
        // this only works if there is no active job at this point
        Assert.assertTrue(WebFrontendITCase.getRunningJobs(WebFrontendITCase.CLUSTER.getClusterClient()).isEmpty());
        // Create a task
        final JobVertex sender = new JobVertex("Sender");
        sender.setParallelism(2);
        sender.setInvokableClass(WebFrontendITCase.BlockingInvokable.class);
        final JobGraph jobGraph = new JobGraph("Stoppable streaming test job", sender);
        final JobID jid = jobGraph.getJobID();
        ClusterClient<?> clusterClient = WebFrontendITCase.CLUSTER.getClusterClient();
        clusterClient.setDetached(true);
        clusterClient.submitJob(jobGraph, WebFrontendITCase.class.getClassLoader());
        // wait for job to show up
        while (WebFrontendITCase.getRunningJobs(WebFrontendITCase.CLUSTER.getClusterClient()).isEmpty()) {
            Thread.sleep(10);
        } 
        // wait for tasks to be properly running
        WebFrontendITCase.BlockingInvokable.latch.await();
        final FiniteDuration testTimeout = new FiniteDuration(2, TimeUnit.MINUTES);
        final Deadline deadline = testTimeout.fromNow();
        try (HttpTestClient client = new HttpTestClient("localhost", getRestPort())) {
            // Request the file from the web server
            client.sendGetRequest((("/jobs/" + jid) + "/yarn-stop"), deadline.timeLeft());
            HttpTestClient.SimpleHttpResponse response = client.getNextResponse(deadline.timeLeft());
            Assert.assertEquals(ACCEPTED, response.getStatus());
            Assert.assertEquals("application/json; charset=UTF-8", response.getType());
            Assert.assertEquals("{}", response.getContent());
        }
        // wait for cancellation to finish
        while (!(WebFrontendITCase.getRunningJobs(WebFrontendITCase.CLUSTER.getClusterClient()).isEmpty())) {
            Thread.sleep(20);
        } 
        WebFrontendITCase.BlockingInvokable.reset();
    }

    /**
     * Test invokable that is stoppable and allows waiting for all subtasks to be running.
     */
    public static class BlockingInvokable extends AbstractInvokable implements StoppableTask {
        private static CountDownLatch latch = new CountDownLatch(2);

        private volatile boolean isRunning = true;

        public BlockingInvokable(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            WebFrontendITCase.BlockingInvokable.latch.countDown();
            while (isRunning) {
                Thread.sleep(100);
            } 
        }

        @Override
        public void stop() {
            this.isRunning = false;
        }

        public static void reset() {
            WebFrontendITCase.BlockingInvokable.latch = new CountDownLatch(2);
        }
    }
}

