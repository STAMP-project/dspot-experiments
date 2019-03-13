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
package org.apache.hadoop.yarn.applications.distributedshell;


import ContainerState.COMPLETE;
import ExecutionType.OPPORTUNISTIC;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.JarFinder;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.impl.TestTimelineClient;
import org.apache.hadoop.yarn.client.api.impl.TimelineClientImpl;
import org.apache.hadoop.yarn.client.api.impl.TimelineWriter;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.ResourceNotFoundException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.timeline.TimelineVersion;
import org.apache.hadoop.yarn.server.timeline.TimelineVersionWatcher;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDistributedShell {
    private static final Logger LOG = LoggerFactory.getLogger(TestDistributedShell.class);

    protected MiniYARNCluster yarnCluster = null;

    protected MiniDFSCluster hdfsCluster = null;

    private FileSystem fs = null;

    private TimelineWriter spyTimelineWriter;

    protected YarnConfiguration conf = null;

    // location of the filesystem timeline writer for timeline service v.2
    private String timelineV2StorageDir = null;

    private static final int NUM_NMS = 1;

    private static final float DEFAULT_TIMELINE_VERSION = 1.0F;

    private static final String TIMELINE_AUX_SERVICE_NAME = "timeline_collector";

    private static final int MIN_ALLOCATION_MB = 128;

    protected static final String APPMASTER_JAR = JarFinder.getJar(ApplicationMaster.class);

    @Rule
    public TimelineVersionWatcher timelineVersionWatcher = new TimelineVersionWatcher();

    @Rule
    public Timeout globalTimeout = new Timeout(90000);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testDSShellWithDomain() throws Exception {
        testDSShell(true);
    }

    @Test
    public void testDSShellWithoutDomain() throws Exception {
        testDSShell(false);
    }

    @Test
    @TimelineVersion(1.5F)
    public void testDSShellWithoutDomainV1_5() throws Exception {
        testDSShell(false);
    }

    @Test
    @TimelineVersion(1.5F)
    public void testDSShellWithDomainV1_5() throws Exception {
        testDSShell(true);
    }

    @Test
    @TimelineVersion(2.0F)
    public void testDSShellWithoutDomainV2() throws Exception {
        testDSShell(false);
    }

    @Test
    @TimelineVersion(2.0F)
    public void testDSShellWithoutDomainV2DefaultFlow() throws Exception {
        testDSShell(false, true);
    }

    @Test
    @TimelineVersion(2.0F)
    public void testDSShellWithoutDomainV2CustomizedFlow() throws Exception {
        testDSShell(false, false);
    }

    @Test
    public void testDSRestartWithPreviousRunningContainers() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", getSleepCommand(8), "--master_memory", "512", "--container_memory", "128", "--keep_containers_across_application_attempts" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        Client client = new Client(TestDSFailedAppMaster.class.getName(), new Configuration(yarnCluster.getConfig()));
        client.init(args);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        // application should succeed
        Assert.assertTrue(result);
    }

    /* The sleeping period in TestDSSleepingAppMaster is set as 5 seconds.
    Set attempt_failures_validity_interval as 2.5 seconds. It will check
    how many attempt failures for previous 2.5 seconds.
    The application is expected to be successful.
     */
    @Test
    public void testDSAttemptFailuresValidityIntervalSucess() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", getSleepCommand(8), "--master_memory", "512", "--container_memory", "128", "--attempt_failures_validity_interval", "2500" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        Configuration conf = yarnCluster.getConfig();
        conf.setInt(RM_AM_MAX_ATTEMPTS, 2);
        Client client = new Client(TestDSSleepingAppMaster.class.getName(), new Configuration(conf));
        client.init(args);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        // application should succeed
        Assert.assertTrue(result);
    }

    /* The sleeping period in TestDSSleepingAppMaster is set as 5 seconds.
    Set attempt_failures_validity_interval as 15 seconds. It will check
    how many attempt failure for previous 15 seconds.
    The application is expected to be fail.
     */
    @Test
    public void testDSAttemptFailuresValidityIntervalFailed() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", getSleepCommand(8), "--master_memory", "512", "--container_memory", "128", "--attempt_failures_validity_interval", "15000" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        Configuration conf = yarnCluster.getConfig();
        conf.setInt(RM_AM_MAX_ATTEMPTS, 2);
        Client client = new Client(TestDSSleepingAppMaster.class.getName(), new Configuration(conf));
        client.init(args);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        // application should be failed
        Assert.assertFalse(result);
    }

    @Test
    public void testDSShellWithCustomLogPropertyFile() throws Exception {
        final File basedir = new File("target", TestDistributedShell.class.getName());
        final File tmpDir = new File(basedir, "tmpDir");
        tmpDir.mkdirs();
        final File customLogProperty = new File(tmpDir, "custom_log4j.properties");
        if (customLogProperty.exists()) {
            customLogProperty.delete();
        }
        if (!(customLogProperty.createNewFile())) {
            Assert.fail("Can not create custom log4j property file.");
        }
        PrintWriter fileWriter = new PrintWriter(customLogProperty);
        // set the output to DEBUG level
        fileWriter.write("log4j.rootLogger=debug,stdout");
        fileWriter.close();
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "3", "--shell_command", "echo", "--shell_args", "HADOOP", "--log_properties", customLogProperty.getAbsolutePath(), "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1" };
        // Before run the DS, the default the log level is INFO
        final Logger LOG_Client = LoggerFactory.getLogger(Client.class);
        Assert.assertTrue(LOG_Client.isInfoEnabled());
        Assert.assertFalse(LOG_Client.isDebugEnabled());
        final Logger LOG_AM = LoggerFactory.getLogger(ApplicationMaster.class);
        Assert.assertTrue(LOG_AM.isInfoEnabled());
        Assert.assertFalse(LOG_AM.isDebugEnabled());
        TestDistributedShell.LOG.info("Initializing DS Client");
        final Client client = new Client(new Configuration(yarnCluster.getConfig()));
        boolean initSuccess = client.init(args);
        Assert.assertTrue(initSuccess);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        Assert.assertTrue(((verifyContainerLog(3, null, true, "DEBUG")) > 10));
        // After DS is finished, the log level should be DEBUG
        Assert.assertTrue(LOG_Client.isInfoEnabled());
        Assert.assertTrue(LOG_Client.isDebugEnabled());
        Assert.assertTrue(LOG_AM.isInfoEnabled());
        Assert.assertTrue(LOG_AM.isDebugEnabled());
    }

    @Test
    public void testDSShellWithMultipleArgs() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "4", "--shell_command", "echo", "--shell_args", "HADOOP YARN MAPREDUCE HDFS", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        final Client client = new Client(new Configuration(yarnCluster.getConfig()));
        boolean initSuccess = client.init(args);
        Assert.assertTrue(initSuccess);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        List<String> expectedContent = new ArrayList<String>();
        expectedContent.add("HADOOP YARN MAPREDUCE HDFS");
        verifyContainerLog(4, expectedContent, false, "");
    }

    @Test
    public void testDSShellWithShellScript() throws Exception {
        final File basedir = new File("target", TestDistributedShell.class.getName());
        final File tmpDir = new File(basedir, "tmpDir");
        tmpDir.mkdirs();
        final File customShellScript = new File(tmpDir, "custom_script.sh");
        if (customShellScript.exists()) {
            customShellScript.delete();
        }
        if (!(customShellScript.createNewFile())) {
            Assert.fail("Can not create custom shell script file.");
        }
        PrintWriter fileWriter = new PrintWriter(customShellScript);
        // set the output to DEBUG level
        fileWriter.write("echo testDSShellWithShellScript");
        fileWriter.close();
        System.out.println(customShellScript.getAbsolutePath());
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_script", customShellScript.getAbsolutePath(), "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        final Client client = new Client(new Configuration(yarnCluster.getConfig()));
        boolean initSuccess = client.init(args);
        Assert.assertTrue(initSuccess);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        List<String> expectedContent = new ArrayList<String>();
        expectedContent.add("testDSShellWithShellScript");
        verifyContainerLog(1, expectedContent, false, "");
    }

    @Test
    public void testDSShellWithInvalidArgs() throws Exception {
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        TestDistributedShell.LOG.info("Initializing DS Client with no args");
        try {
            client.init(new String[]{  });
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains("No args"));
        }
        TestDistributedShell.LOG.info("Initializing DS Client with no jar file");
        try {
            String[] args = new String[]{ "--num_containers", "2", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_memory", "512", "--container_memory", "128" };
            client.init(args);
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains("No jar"));
        }
        TestDistributedShell.LOG.info("Initializing DS Client with no shell command");
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--master_memory", "512", "--container_memory", "128" };
            client.init(args);
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains("No shell command"));
        }
        TestDistributedShell.LOG.info("Initializing DS Client with invalid no. of containers");
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "-1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_memory", "512", "--container_memory", "128" };
            client.init(args);
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains("Invalid no. of containers"));
        }
        TestDistributedShell.LOG.info("Initializing DS Client with invalid no. of vcores");
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_memory", "512", "--master_vcores", "-2", "--container_memory", "128", "--container_vcores", "1" };
            client.init(args);
            client.run();
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains("Invalid virtual cores specified"));
        }
        TestDistributedShell.LOG.info("Initializing DS Client with --shell_command and --shell_script");
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1", "--shell_script", "test.sh" };
            client.init(args);
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains(("Can not specify shell_command option " + "and shell_script option at the same time")));
        }
        TestDistributedShell.LOG.info("Initializing DS Client without --shell_command and --shell_script");
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1" };
            client.init(args);
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains(("No shell command or shell script specified " + "to be executed by application master")));
        }
        TestDistributedShell.LOG.info("Initializing DS Client with invalid container_type argument");
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1", "--shell_command", "date", "--container_type", "UNSUPPORTED_TYPE" };
            client.init(args);
            Assert.fail("Exception is expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("The throw exception is not expected", e.getMessage().contains("Invalid container_type: UNSUPPORTED_TYPE"));
        }
    }

    @Test
    public void testDSTimelineClientWithConnectionRefuse() throws Exception {
        ApplicationMaster am = new ApplicationMaster();
        TimelineClientImpl client = new TimelineClientImpl() {
            @Override
            protected TimelineWriter createTimelineWriter(Configuration conf, UserGroupInformation authUgi, com.sun.jersey.api.client.Client client, URI resURI) throws IOException {
                TimelineWriter timelineWriter = new org.apache.hadoop.yarn.client.api.impl.DirectTimelineWriter(authUgi, client, resURI);
                spyTimelineWriter = Mockito.spy(timelineWriter);
                return spyTimelineWriter;
            }
        };
        client.init(conf);
        client.start();
        TestTimelineClient.mockEntityClientResponse(spyTimelineWriter, null, false, true);
        try {
            UserGroupInformation ugi = Mockito.mock(UserGroupInformation.class);
            Mockito.when(ugi.getShortUserName()).thenReturn("user1");
            // verify no ClientHandlerException get thrown out.
            am.publishContainerEndEvent(client, ContainerStatus.newInstance(BuilderUtils.newContainerId(1, 1, 1, 1), COMPLETE, "", 1), "domainId", ugi);
        } finally {
            client.stop();
        }
    }

    @Test
    public void testContainerLaunchFailureHandling() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_memory", "512", "--container_memory", "128" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        Client client = new Client(ContainerLaunchFailAppMaster.class.getName(), new Configuration(yarnCluster.getConfig()));
        boolean initSuccess = client.init(args);
        Assert.assertTrue(initSuccess);
        TestDistributedShell.LOG.info("Running DS Client");
        boolean result = client.run();
        TestDistributedShell.LOG.info(("Client run completed. Result=" + result));
        Assert.assertFalse(result);
    }

    @Test
    public void testDebugFlag() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1", "--debug" };
        TestDistributedShell.LOG.info("Initializing DS Client");
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        Assert.assertTrue(client.init(args));
        TestDistributedShell.LOG.info("Running DS Client");
        Assert.assertTrue(client.run());
    }

    @Test
    public void testDistributedShellResourceProfiles() throws Exception {
        String[][] args = new String[][]{ new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--container_resource_profile", "maximum" }, new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_resource_profile", "default" }, new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_resource_profile", "default", "--container_resource_profile", "maximum" } };
        for (int i = 0; i < (args.length); ++i) {
            TestDistributedShell.LOG.info("Initializing DS Client");
            Client client = new Client(new Configuration(yarnCluster.getConfig()));
            Assert.assertTrue(client.init(args[i]));
            TestDistributedShell.LOG.info("Running DS Client");
            try {
                client.run();
                Assert.fail("Client run should throw error");
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Test
    public void testDSShellWithOpportunisticContainers() throws Exception {
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1", "--shell_command", "date", "--container_type", "OPPORTUNISTIC" };
            client.init(args);
            client.run();
        } catch (Exception e) {
            Assert.fail("Job execution with opportunistic containers failed.");
        }
    }

    @Test
    @TimelineVersion(2.0F)
    public void testDSShellWithEnforceExecutionType() throws Exception {
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        try {
            String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "2", "--master_memory", "512", "--master_vcores", "2", "--container_memory", "128", "--container_vcores", "1", "--shell_command", "date", "--container_type", "OPPORTUNISTIC", "--enforce_execution_type" };
            client.init(args);
            final AtomicBoolean result = new AtomicBoolean(false);
            Thread t = new Thread() {
                public void run() {
                    try {
                        result.set(client.run());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            t.start();
            YarnClient yarnClient = YarnClient.createYarnClient();
            yarnClient.init(new Configuration(yarnCluster.getConfig()));
            yarnClient.start();
            waitForContainersLaunch(yarnClient, 2);
            List<ApplicationReport> apps = yarnClient.getApplications();
            ApplicationReport appReport = apps.get(0);
            ApplicationId appId = appReport.getApplicationId();
            List<ApplicationAttemptReport> appAttempts = yarnClient.getApplicationAttempts(appId);
            ApplicationAttemptReport appAttemptReport = appAttempts.get(0);
            ApplicationAttemptId appAttemptId = appAttemptReport.getApplicationAttemptId();
            List<ContainerReport> containers = yarnClient.getContainers(appAttemptId);
            // we should get two containers.
            Assert.assertEquals(2, containers.size());
            ContainerId amContainerId = appAttemptReport.getAMContainerId();
            for (ContainerReport container : containers) {
                if (!(container.getContainerId().equals(amContainerId))) {
                    Assert.assertEquals(container.getExecutionType(), OPPORTUNISTIC);
                }
            }
        } catch (Exception e) {
            Assert.fail("Job execution with enforce execution type failed.");
        }
    }

    @Test
    @TimelineVersion(2.0F)
    public void testDistributedShellWithResources() throws Exception {
        doTestDistributedShellWithResources(false);
    }

    @Test
    @TimelineVersion(2.0F)
    public void testDistributedShellWithResourcesWithLargeContainers() throws Exception {
        doTestDistributedShellWithResources(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDistributedShellAMResourcesWithIllegalArguments() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_resources", "memory-mb=invalid" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
    }

    @Test(expected = MissingArgumentException.class)
    public void testDistributedShellAMResourcesWithMissingArgumentValue() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_resources" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDistributedShellAMResourcesWithUnknownResource() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--master_resources", "unknown-resource=5" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
        client.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDistributedShellNonExistentQueue() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "dir" : "ls", "--queue", "non-existent-queue" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
        client.run();
    }

    @Test
    public void testDistributedShellWithSingleFileLocalization() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "type" : "cat", "--localize_files", "./src/test/resources/a.txt", "--shell_args", "a.txt" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
        Assert.assertTrue("Client exited with an error", client.run());
    }

    @Test
    public void testDistributedShellWithMultiFileLocalization() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "type" : "cat", "--localize_files", "./src/test/resources/a.txt,./src/test/resources/b.txt", "--shell_args", "a.txt b.txt" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
        Assert.assertTrue("Client exited with an error", client.run());
    }

    @Test(expected = UncheckedIOException.class)
    public void testDistributedShellWithNonExistentFileLocalization() throws Exception {
        String[] args = new String[]{ "--jar", TestDistributedShell.APPMASTER_JAR, "--num_containers", "1", "--shell_command", Shell.WINDOWS ? "type" : "cat", "--localize_files", "/non/existing/path/file.txt", "--shell_args", "file.txt" };
        Client client = new Client(new Configuration(yarnCluster.getConfig()));
        client.init(args);
        client.run();
    }
}

