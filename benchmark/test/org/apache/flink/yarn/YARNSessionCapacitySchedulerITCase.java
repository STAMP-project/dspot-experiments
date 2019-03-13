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
package org.apache.flink.yarn;


import JobManagerOptions.ADDRESS;
import ResourceManagerOptions.CONTAINERIZED_HEAP_CUTOFF_MIN;
import YarnConfigOptions.VCORES;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import junit.framework.TestCase;
import org.apache.flink.client.cli.CliFrontend;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rest.RestClient;
import org.apache.flink.runtime.taskexecutor.TaskManagerServices;
import org.apache.flink.shaded.guava18.com.google.common.net.HostAndPort;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.yarn.util.YarnTestUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.log4j.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.yarn.YarnTestBase.RunTypes.CLI_FRONTEND;
import static org.apache.flink.yarn.YarnTestBase.RunTypes.YARN_SESSION;


/**
 * This test starts a MiniYARNCluster with a CapacityScheduler.
 * Is has, by default a queue called "default". The configuration here adds another queue: "qa-team".
 */
public class YARNSessionCapacitySchedulerITCase extends YarnTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(YARNSessionCapacitySchedulerITCase.class);

    /**
     * RestClient to query Flink cluster.
     */
    private static RestClient restClient;

    /**
     * ExecutorService for {@link RestClient}.
     *
     * @see #restClient
     */
    private static ExecutorService restClientExecutor;

    /**
     * Toggles checking for prohibited strings in logs after the test has run.
     */
    private boolean checkForProhibitedLogContents = true;

    /**
     * Tests that a session cluster, that uses the resources from the <i>qa-team</i> queue,
     * can be started from the command line.
     */
    @Test
    public void testStartYarnSessionClusterInQaTeamQueue() throws IOException {
        runWithArgs(new String[]{ "-j", YarnTestBase.flinkUberjar.getAbsolutePath(), "-t", YarnTestBase.flinkLibFolder.getAbsolutePath(), "-t", YarnTestBase.flinkShadedHadoopDir.getAbsolutePath(), "-jm", "768m", "-tm", "1024m", "-qu", "qa-team" }, "Flink JobManager is now running on ", null, YARN_SESSION, 0);
    }

    /**
     * Test per-job yarn cluster
     *
     * <p>This also tests the prefixed CliFrontend options for the YARN case
     * We also test if the requested parallelism of 2 is passed through.
     * The parallelism is requested at the YARN client (-ys).
     */
    @Test
    public void perJobYarnCluster() throws IOException {
        YARNSessionCapacitySchedulerITCase.LOG.info("Starting perJobYarnCluster()");
        UtilsTest.addTestAppender(CliFrontend.class, Level.INFO);
        File exampleJarLocation = YarnTestUtils.getTestJarPath("BatchWordCount.jar");
        /* test succeeded after this string */
        /* prohibited strings: (to verify the parallelism) */
        // (we should see "DataSink (...) (1/2)" and "DataSink (...) (2/2)" instead)
        runWithArgs(new String[]{ "run", "-m", "yarn-cluster", "-yj", YarnTestBase.flinkUberjar.getAbsolutePath(), "-yt", YarnTestBase.flinkLibFolder.getAbsolutePath(), "-yt", YarnTestBase.flinkShadedHadoopDir.getAbsolutePath(), "-yn", "1", "-ys", "2"// test that the job is executed with a DOP of 2
        , "-yjm", "768m", "-ytm", "1024m", exampleJarLocation.getAbsolutePath() }, "Program execution finished", new String[]{ "DataSink \\(.*\\) \\(1/1\\) switched to FINISHED" }, CLI_FRONTEND, 0, true);
        YARNSessionCapacitySchedulerITCase.LOG.info("Finished perJobYarnCluster()");
    }

    /**
     * Test per-job yarn cluster and memory calculations for off-heap use (see FLINK-7400) with the
     * same job as {@link #perJobYarnCluster()}.
     *
     * <p>This ensures that with (any) pre-allocated off-heap memory by us, there is some off-heap
     * memory remaining for Flink's libraries. Creating task managers will thus fail if no off-heap
     * memory remains.
     */
    @Test
    public void perJobYarnClusterOffHeap() throws IOException {
        YARNSessionCapacitySchedulerITCase.LOG.info("Starting perJobYarnCluster()");
        UtilsTest.addTestAppender(CliFrontend.class, Level.INFO);
        File exampleJarLocation = YarnTestUtils.getTestJarPath("BatchWordCount.jar");
        // set memory constraints (otherwise this is the same test as perJobYarnCluster() above)
        final long taskManagerMemoryMB = 1024;
        // noinspection NumericOverflow if the calculation of the total Java memory size overflows, default configuration parameters are wrong in the first place, so we can ignore this inspection
        final long networkBuffersMB = (TaskManagerServices.calculateNetworkBufferMemory(((taskManagerMemoryMB - (CONTAINERIZED_HEAP_CUTOFF_MIN.defaultValue())) << 20), new Configuration())) >> 20;
        final long offHeapMemory = ((taskManagerMemoryMB - (CONTAINERIZED_HEAP_CUTOFF_MIN.defaultValue())) - // cutoff memory (will be added automatically)
        networkBuffersMB)// amount of memory used for network buffers
         - 100;// reserve something for the Java heap space

        /* test succeeded after this string */
        /* prohibited strings: (to verify the parallelism) */
        // (we should see "DataSink (...) (1/2)" and "DataSink (...) (2/2)" instead)
        runWithArgs(new String[]{ "run", "-m", "yarn-cluster", "-yj", YarnTestBase.flinkUberjar.getAbsolutePath(), "-yt", YarnTestBase.flinkLibFolder.getAbsolutePath(), "-yt", YarnTestBase.flinkShadedHadoopDir.getAbsolutePath(), "-yn", "1", "-ys", "2"// test that the job is executed with a DOP of 2
        , "-yjm", "768m", "-ytm", taskManagerMemoryMB + "m", "-yD", "taskmanager.memory.off-heap=true", "-yD", ("taskmanager.memory.size=" + offHeapMemory) + "m", "-yD", "taskmanager.memory.preallocate=true", exampleJarLocation.getAbsolutePath() }, "Program execution finished", new String[]{ "DataSink \\(.*\\) \\(1/1\\) switched to FINISHED" }, CLI_FRONTEND, 0, true);
        YARNSessionCapacitySchedulerITCase.LOG.info("Finished perJobYarnCluster()");
    }

    /**
     * Starts a session cluster on YARN, and submits a streaming job.
     *
     * <p>Tests
     * <ul>
     * <li>if a custom YARN application name can be set from the command line,
     * <li>if the number of TaskManager slots can be set from the command line,
     * <li>if dynamic properties from the command line are set,
     * <li>if the vcores are set correctly (FLINK-2213),
     * <li>if jobmanager hostname/port are shown in web interface (FLINK-1902)
     * </ul>
     *
     * <p><b>Hint: </b> If you think it is a good idea to add more assertions to this test, think again!
     */
    @Test(timeout = 100000)
    public void testVCoresAreSetCorrectlyAndJobManagerHostnameAreShownInWebInterfaceAndDynamicPropertiesAndYarnApplicationNameAndTaskManagerSlots() throws Exception {
        checkForProhibitedLogContents = false;
        final YarnTestBase.Runner yarnSessionClusterRunner = startWithArgs(new String[]{ "-j", YarnTestBase.flinkUberjar.getAbsolutePath(), "-t", YarnTestBase.flinkLibFolder.getAbsolutePath(), "-t", YarnTestBase.flinkShadedHadoopDir.getAbsolutePath(), "-jm", "768m", "-tm", "1024m", "-s", "3"// set the slots 3 to check if the vCores are set properly!
        , "-nm", "customName", "-Dfancy-configuration-value=veryFancy", "-Dyarn.maximum-failed-containers=3", ("-D" + (VCORES.key())) + "=2" }, "Flink JobManager is now running on ", YARN_SESSION);
        final String logs = YarnTestBase.outContent.toString();
        final HostAndPort hostAndPort = YARNSessionCapacitySchedulerITCase.parseJobManagerHostname(logs);
        final String host = hostAndPort.getHostText();
        final int port = hostAndPort.getPort();
        YARNSessionCapacitySchedulerITCase.LOG.info("Extracted hostname:port: {}", host, port);
        submitJob("WindowJoin.jar");
        // 
        // Assert that custom YARN application name "customName" is set
        // 
        final ApplicationReport applicationReport = getOnlyApplicationReport();
        Assert.assertEquals("customName", applicationReport.getName());
        // 
        // Assert the number of TaskManager slots are set
        // 
        YARNSessionCapacitySchedulerITCase.waitForTaskManagerRegistration(host, port, Duration.ofMillis(30000));
        YARNSessionCapacitySchedulerITCase.assertNumberOfSlotsPerTask(host, port, 3);
        final Map<String, String> flinkConfig = YARNSessionCapacitySchedulerITCase.getFlinkConfig(host, port);
        // 
        // Assert dynamic properties
        // 
        Assert.assertThat(flinkConfig, Matchers.hasEntry("fancy-configuration-value", "veryFancy"));
        Assert.assertThat(flinkConfig, Matchers.hasEntry("yarn.maximum-failed-containers", "3"));
        // 
        // FLINK-2213: assert that vcores are set
        // 
        Assert.assertThat(flinkConfig, Matchers.hasEntry(VCORES.key(), "2"));
        // 
        // FLINK-1902: check if jobmanager hostname is shown in web interface
        // 
        Assert.assertThat(flinkConfig, Matchers.hasEntry(ADDRESS.key(), host));
        yarnSessionClusterRunner.sendStop();
        yarnSessionClusterRunner.join();
    }

    /**
     * Test deployment to non-existing queue & ensure that the system logs a WARN message
     * for the user. (Users had unexpected behavior of Flink on YARN because they mistyped the
     * target queue. With an error message, we can help users identifying the issue)
     */
    @Test
    public void testNonexistingQueueWARNmessage() throws IOException {
        YARNSessionCapacitySchedulerITCase.LOG.info("Starting testNonexistingQueueWARNmessage()");
        UtilsTest.addTestAppender(AbstractYarnClusterDescriptor.class, Level.WARN);
        try {
            runWithArgs(new String[]{ "-j", YarnTestBase.flinkUberjar.getAbsolutePath(), "-t", YarnTestBase.flinkLibFolder.getAbsolutePath(), "-t", YarnTestBase.flinkShadedHadoopDir.getAbsolutePath(), "-n", "1", "-jm", "768m", "-tm", "1024m", "-qu", "doesntExist" }, "to unknown queue: doesntExist", null, YARN_SESSION, 1);
        } catch (Exception e) {
            TestCase.assertTrue(ExceptionUtils.findThrowableWithMessage(e, "to unknown queue: doesntExist").isPresent());
        }
        UtilsTest.checkForLogString("The specified queue 'doesntExist' does not exist. Available queues");
        YARNSessionCapacitySchedulerITCase.LOG.info("Finished testNonexistingQueueWARNmessage()");
    }

    /**
     * Test per-job yarn cluster with the parallelism set at the CliFrontend instead of the YARN client.
     */
    @Test
    public void perJobYarnClusterWithParallelism() throws IOException {
        YARNSessionCapacitySchedulerITCase.LOG.info("Starting perJobYarnClusterWithParallelism()");
        // write log messages to stdout as well, so that the runWithArgs() method
        // is catching the log output
        UtilsTest.addTestAppender(CliFrontend.class, Level.INFO);
        File exampleJarLocation = YarnTestUtils.getTestJarPath("BatchWordCount.jar");
        /* test succeeded after this string */
        /* prohibited strings: (we want to see "DataSink (...) (2/2) switched to FINISHED") */
        runWithArgs(new String[]{ "run", "-p", "2"// test that the job is executed with a DOP of 2
        , "-m", "yarn-cluster", "-yj", YarnTestBase.flinkUberjar.getAbsolutePath(), "-yt", YarnTestBase.flinkLibFolder.getAbsolutePath(), "-yt", YarnTestBase.flinkShadedHadoopDir.getAbsolutePath(), "-yn", "1", "-ys", "2", "-yjm", "768m", "-ytm", "1024m", exampleJarLocation.getAbsolutePath() }, "Program execution finished", new String[]{ "DataSink \\(.*\\) \\(1/1\\) switched to FINISHED" }, CLI_FRONTEND, 0, true);
        YARNSessionCapacitySchedulerITCase.LOG.info("Finished perJobYarnClusterWithParallelism()");
    }

    /**
     * Test a fire-and-forget job submission to a YARN cluster.
     */
    @Test(timeout = 60000)
    public void testDetachedPerJobYarnCluster() throws Exception {
        YARNSessionCapacitySchedulerITCase.LOG.info("Starting testDetachedPerJobYarnCluster()");
        File exampleJarLocation = YarnTestUtils.getTestJarPath("BatchWordCount.jar");
        testDetachedPerJobYarnClusterInternal(exampleJarLocation.getAbsolutePath());
        YARNSessionCapacitySchedulerITCase.LOG.info("Finished testDetachedPerJobYarnCluster()");
    }

    /**
     * Test a fire-and-forget job submission to a YARN cluster.
     */
    @Test(timeout = 60000)
    public void testDetachedPerJobYarnClusterWithStreamingJob() throws Exception {
        YARNSessionCapacitySchedulerITCase.LOG.info("Starting testDetachedPerJobYarnClusterWithStreamingJob()");
        File exampleJarLocation = YarnTestUtils.getTestJarPath("StreamingWordCount.jar");
        testDetachedPerJobYarnClusterInternal(exampleJarLocation.getAbsolutePath());
        YARNSessionCapacitySchedulerITCase.LOG.info("Finished testDetachedPerJobYarnClusterWithStreamingJob()");
    }
}

