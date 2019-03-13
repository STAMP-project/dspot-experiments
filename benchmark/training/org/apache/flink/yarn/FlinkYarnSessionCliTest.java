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


import HighAvailabilityOptions.HA_CLUSTER_ID;
import JobManagerOptions.JOB_MANAGER_HEAP_MEMORY;
import JobManagerOptions.JOB_MANAGER_HEAP_MEMORY_MB;
import TaskManagerOptions.NUM_TASK_SLOTS;
import TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY;
import TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY_MB;
import YarnConfigOptions.PROPERTIES_FILE_LOCATION;
import java.io.File;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.apache.flink.yarn.cli.FlinkYarnSessionCli;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link FlinkYarnSessionCli}.
 */
public class FlinkYarnSessionCliTest extends TestLogger {
    private static final ApplicationId TEST_YARN_APPLICATION_ID = ApplicationId.newInstance(System.currentTimeMillis(), 42);

    private static final ApplicationId TEST_YARN_APPLICATION_ID_2 = ApplicationId.newInstance(System.currentTimeMillis(), 43);

    private static final String TEST_YARN_JOB_MANAGER_ADDRESS = "22.33.44.55";

    private static final int TEST_YARN_JOB_MANAGER_PORT = 6655;

    private static final String validPropertiesFile = "applicationID=" + (FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID);

    private static final String invalidPropertiesFile = (("jasfobManager=" + (FlinkYarnSessionCliTest.TEST_YARN_JOB_MANAGER_ADDRESS)) + ":asf") + (FlinkYarnSessionCliTest.TEST_YARN_JOB_MANAGER_PORT);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testDynamicProperties() throws Exception {
        FlinkYarnSessionCli cli = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "", "", false);
        Options options = new Options();
        cli.addGeneralOptions(options);
        cli.addRunOptions(options);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[]{ "run", "-j", "fake.jar", "-n", "15", "-D", "akka.ask.timeout=5 min", "-D", "env.java.opts=-DappName=foobar" });
        AbstractYarnClusterDescriptor flinkYarnDescriptor = cli.createClusterDescriptor(cmd);
        Assert.assertNotNull(flinkYarnDescriptor);
        Map<String, String> dynProperties = FlinkYarnSessionCli.getDynamicProperties(flinkYarnDescriptor.getDynamicPropertiesEncoded());
        Assert.assertEquals(2, dynProperties.size());
        Assert.assertEquals("5 min", dynProperties.get("akka.ask.timeout"));
        Assert.assertEquals("-DappName=foobar", dynProperties.get("env.java.opts"));
    }

    @Test
    public void testCorrectSettingOfMaxSlots() throws Exception {
        String[] params = new String[]{ "-yn", "2", "-ys", "3" };
        FlinkYarnSessionCli yarnCLI = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = yarnCLI.parseCommandLineOptions(params, true);
        AbstractYarnClusterDescriptor descriptor = yarnCLI.createClusterDescriptor(commandLine);
        final ClusterSpecification clusterSpecification = yarnCLI.getClusterSpecification(commandLine);
        // each task manager has 3 slots but the parallelism is 7. Thus the slots should be increased.
        Assert.assertEquals(3, clusterSpecification.getSlotsPerTaskManager());
        Assert.assertEquals(2, clusterSpecification.getNumberTaskManagers());
    }

    @Test
    public void testCorrectSettingOfDetachedMode() throws Exception {
        String[] params = new String[]{ "-yd" };
        FlinkYarnSessionCli yarnCLI = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = yarnCLI.parseCommandLineOptions(params, true);
        AbstractYarnClusterDescriptor descriptor = yarnCLI.createClusterDescriptor(commandLine);
        // each task manager has 3 slots but the parallelism is 7. Thus the slots should be increased.
        Assert.assertTrue(descriptor.isDetachedMode());
    }

    @Test
    public void testZookeeperNamespaceProperty() throws Exception {
        String zkNamespaceCliInput = "flink_test_namespace";
        String[] params = new String[]{ "-yn", "2", "-yz", zkNamespaceCliInput };
        FlinkYarnSessionCli yarnCLI = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        CommandLine commandLine = yarnCLI.parseCommandLineOptions(params, true);
        AbstractYarnClusterDescriptor descriptor = yarnCLI.createClusterDescriptor(commandLine);
        Assert.assertEquals(zkNamespaceCliInput, descriptor.getZookeeperNamespace());
    }

    @Test
    public void testNodeLabelProperty() throws Exception {
        String nodeLabelCliInput = "flink_test_nodelabel";
        String[] params = new String[]{ "-yn", "2", "-ynl", nodeLabelCliInput };
        FlinkYarnSessionCli yarnCLI = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        CommandLine commandLine = yarnCLI.parseCommandLineOptions(params, true);
        AbstractYarnClusterDescriptor descriptor = yarnCLI.createClusterDescriptor(commandLine);
        Assert.assertEquals(nodeLabelCliInput, descriptor.getNodeLabel());
    }

    /**
     * Test that the CliFrontend is able to pick up the .yarn-properties file from a specified location.
     */
    @Test
    public void testResumeFromYarnPropertiesFile() throws Exception {
        File directoryPath = writeYarnPropertiesFile(FlinkYarnSessionCliTest.validPropertiesFile);
        final Configuration configuration = new Configuration();
        configuration.setString(PROPERTIES_FILE_LOCATION, directoryPath.getAbsolutePath());
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[]{  }, true);
        final ApplicationId clusterId = flinkYarnSessionCli.getClusterId(commandLine);
        Assert.assertEquals(FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID, clusterId);
    }

    /**
     * Tests that we fail when reading an invalid yarn properties file when retrieving
     * the cluster id.
     */
    @Test(expected = FlinkException.class)
    public void testInvalidYarnPropertiesFile() throws Exception {
        File directoryPath = writeYarnPropertiesFile(FlinkYarnSessionCliTest.invalidPropertiesFile);
        final Configuration configuration = new Configuration();
        configuration.setString(PROPERTIES_FILE_LOCATION, directoryPath.getAbsolutePath());
        new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
    }

    @Test
    public void testResumeFromYarnID() throws Exception {
        final Configuration configuration = new Configuration();
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[]{ "-yid", FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID.toString() }, true);
        final ApplicationId clusterId = flinkYarnSessionCli.getClusterId(commandLine);
        Assert.assertEquals(FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID, clusterId);
    }

    @Test
    public void testResumeFromYarnIDZookeeperNamespace() throws Exception {
        final Configuration configuration = new Configuration();
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[]{ "-yid", FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID.toString() }, true);
        final AbstractYarnClusterDescriptor clusterDescriptor = flinkYarnSessionCli.createClusterDescriptor(commandLine);
        final Configuration clusterDescriptorConfiguration = clusterDescriptor.getFlinkConfiguration();
        String zkNs = clusterDescriptorConfiguration.getValue(HA_CLUSTER_ID);
        Assert.assertTrue(zkNs.matches("application_\\d+_0042"));
    }

    @Test
    public void testResumeFromYarnIDZookeeperNamespaceOverride() throws Exception {
        final Configuration configuration = new Configuration();
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final String overrideZkNamespace = "my_cluster";
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[]{ "-yid", FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID.toString(), "-yz", overrideZkNamespace }, true);
        final AbstractYarnClusterDescriptor clusterDescriptor = flinkYarnSessionCli.createClusterDescriptor(commandLine);
        final Configuration clusterDescriptorConfiguration = clusterDescriptor.getFlinkConfiguration();
        final String clusterId = clusterDescriptorConfiguration.getValue(HA_CLUSTER_ID);
        Assert.assertEquals(overrideZkNamespace, clusterId);
    }

    @Test
    public void testYarnIDOverridesPropertiesFile() throws Exception {
        File directoryPath = writeYarnPropertiesFile(FlinkYarnSessionCliTest.validPropertiesFile);
        final Configuration configuration = new Configuration();
        configuration.setString(PROPERTIES_FILE_LOCATION, directoryPath.getAbsolutePath());
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[]{ "-yid", FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID_2.toString() }, true);
        final ApplicationId clusterId = flinkYarnSessionCli.getClusterId(commandLine);
        Assert.assertEquals(FlinkYarnSessionCliTest.TEST_YARN_APPLICATION_ID_2, clusterId);
    }

    /**
     * Tests that the command line arguments override the configuration settings
     * when the {@link ClusterSpecification} is created.
     */
    @Test
    public void testCommandLineClusterSpecification() throws Exception {
        final Configuration configuration = new Configuration();
        final int jobManagerMemory = 1337;
        final int taskManagerMemory = 7331;
        final int slotsPerTaskManager = 30;
        configuration.setString(JOB_MANAGER_HEAP_MEMORY, (jobManagerMemory + "m"));
        configuration.setString(TASK_MANAGER_HEAP_MEMORY, (taskManagerMemory + "m"));
        configuration.setInteger(NUM_TASK_SLOTS, slotsPerTaskManager);
        final String[] args = new String[]{ "-yjm", (String.valueOf(jobManagerMemory)) + "m", "-ytm", (String.valueOf(taskManagerMemory)) + "m", "-ys", String.valueOf(slotsPerTaskManager) };
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(args, false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(jobManagerMemory));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(taskManagerMemory));
        Assert.assertThat(clusterSpecification.getSlotsPerTaskManager(), Matchers.is(slotsPerTaskManager));
    }

    /**
     * Tests that the configuration settings are used to create the
     * {@link ClusterSpecification}.
     */
    @Test
    public void testConfigurationClusterSpecification() throws Exception {
        final Configuration configuration = new Configuration();
        final int jobManagerMemory = 1337;
        configuration.setString(JOB_MANAGER_HEAP_MEMORY, (jobManagerMemory + "m"));
        final int taskManagerMemory = 7331;
        configuration.setString(TASK_MANAGER_HEAP_MEMORY, (taskManagerMemory + "m"));
        final int slotsPerTaskManager = 42;
        configuration.setInteger(NUM_TASK_SLOTS, slotsPerTaskManager);
        final String[] args = new String[]{  };
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(args, false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(jobManagerMemory));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(taskManagerMemory));
        Assert.assertThat(clusterSpecification.getSlotsPerTaskManager(), Matchers.is(slotsPerTaskManager));
    }

    /**
     * Tests the specifying heap memory without unit for job manager and task manager.
     */
    @Test
    public void testHeapMemoryPropertyWithoutUnit() throws Exception {
        final String[] args = new String[]{ "-yn", "2", "-yjm", "1024", "-ytm", "2048" };
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(args, false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(1024));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(2048));
    }

    /**
     * Tests the specifying heap memory with unit (MB) for job manager and task manager.
     */
    @Test
    public void testHeapMemoryPropertyWithUnitMB() throws Exception {
        final String[] args = new String[]{ "-yn", "2", "-yjm", "1024m", "-ytm", "2048m" };
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(args, false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(1024));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(2048));
    }

    /**
     * Tests the specifying heap memory with arbitrary unit for job manager and task manager.
     */
    @Test
    public void testHeapMemoryPropertyWithArbitraryUnit() throws Exception {
        final String[] args = new String[]{ "-yn", "2", "-yjm", "1g", "-ytm", "2g" };
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(args, false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(1024));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(2048));
    }

    /**
     * Tests the specifying heap memory with old config key for job manager and task manager.
     */
    @Test
    public void testHeapMemoryPropertyWithOldConfigKey() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setInteger(JOB_MANAGER_HEAP_MEMORY_MB, 2048);
        configuration.setInteger(TASK_MANAGER_HEAP_MEMORY_MB, 4096);
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(configuration, tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[0], false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(2048));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(4096));
    }

    /**
     * Tests the specifying heap memory with config default value for job manager and task manager.
     */
    @Test
    public void testHeapMemoryPropertyWithConfigDefaultValue() throws Exception {
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(new String[0], false);
        final ClusterSpecification clusterSpecification = flinkYarnSessionCli.getClusterSpecification(commandLine);
        Assert.assertThat(clusterSpecification.getMasterMemoryMB(), Matchers.is(1024));
        Assert.assertThat(clusterSpecification.getTaskManagerMemoryMB(), Matchers.is(1024));
    }

    @Test
    public void testMultipleYarnShipOptions() throws Exception {
        final String[] args = new String[]{ "run", "--yarnship", tmp.newFolder().getAbsolutePath(), "--yarnship", tmp.newFolder().getAbsolutePath() };
        final FlinkYarnSessionCli flinkYarnSessionCli = new FlinkYarnSessionCli(new Configuration(), tmp.getRoot().getAbsolutePath(), "y", "yarn");
        final CommandLine commandLine = flinkYarnSessionCli.parseCommandLineOptions(args, false);
        AbstractYarnClusterDescriptor flinkYarnDescriptor = flinkYarnSessionCli.createClusterDescriptor(commandLine);
        Assert.assertEquals(2, flinkYarnDescriptor.shipFiles.size());
    }
}

