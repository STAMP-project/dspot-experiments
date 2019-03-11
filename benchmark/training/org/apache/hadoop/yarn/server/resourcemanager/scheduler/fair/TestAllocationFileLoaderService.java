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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import DominantResourceFairnessPolicy.NAME;
import FSQueueType.LEAF;
import FSQueueType.PARENT;
import FairSchedulerConfiguration.ALLOCATION_FILE;
import FairSchedulerConfiguration.ALLOW_UNDECLARED_POOLS;
import FairSchedulerConfiguration.USER_AS_DEFAULT_QUEUE;
import MiniDFSCluster.Builder;
import MiniDFSCluster.HDFS_MINIDFS_BASEDIR;
import QueuePlacementRule.Default;
import QueuePlacementRule.PrimaryGroup;
import QueuePlacementRule.Specified;
import ReservationSchedulerConfiguration.DEFAULT_RESERVATION_ENFORCEMENT_WINDOW;
import ReservationSchedulerConfiguration.DEFAULT_RESERVATION_PLANNER_NAME;
import ReservationSchedulerConfiguration.DEFAULT_RESERVATION_WINDOW;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.QueuePlacementRule.NestedUserQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.allocationfile.AllocationFileWriter;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.resource.CustomResourceTypesConfigurationProvider;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;

import static AllocationFileLoaderService.ALLOC_RELOAD_WAIT_MS;


public class TestAllocationFileLoaderService {
    private static final String A_CUSTOM_RESOURCE = "a-custom-resource";

    static final String TEST_DIR = new File(System.getProperty("test.build.data", "/tmp")).getAbsolutePath();

    static final String ALLOC_FILE = new File(TestAllocationFileLoaderService.TEST_DIR, "test-queues").getAbsolutePath();

    private static final String TEST_FAIRSCHED_XML = "test-fair-scheduler.xml";

    @Test
    public void testGetAllocationFileFromFileSystem() throws IOException, URISyntaxException {
        Configuration conf = new YarnConfiguration();
        File baseDir = new File((((TestAllocationFileLoaderService.TEST_DIR) + (Path.SEPARATOR)) + "getAllocHDFS")).getAbsoluteFile();
        FileUtil.fullyDelete(baseDir);
        conf.set(HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf);
        MiniDFSCluster hdfsCluster = builder.build();
        String fsAllocPath = (("hdfs://localhost:" + (hdfsCluster.getNameNodePort())) + (Path.SEPARATOR)) + (TestAllocationFileLoaderService.TEST_FAIRSCHED_XML);
        URL fschedURL = Thread.currentThread().getContextClassLoader().getResource(TestAllocationFileLoaderService.TEST_FAIRSCHED_XML);
        FileSystem fs = FileSystem.get(conf);
        fs.copyFromLocalFile(new Path(fschedURL.toURI()), new Path(fsAllocPath));
        conf.set(ALLOCATION_FILE, fsAllocPath);
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        Path allocationFile = allocLoader.getAllocationFile(conf);
        Assert.assertEquals(fsAllocPath, allocationFile.toString());
        Assert.assertTrue(fs.exists(allocationFile));
        hdfsCluster.shutdown(true);
    }

    @Test(expected = UnsupportedFileSystemException.class)
    public void testDenyGetAllocationFileFromUnsupportedFileSystem() throws UnsupportedFileSystemException {
        Configuration conf = new YarnConfiguration();
        conf.set(ALLOCATION_FILE, "badfs:///badfile");
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.getAllocationFile(conf);
    }

    @Test
    public void testGetAllocationFileFromClasspath() {
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(conf);
            conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.TEST_FAIRSCHED_XML);
            AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
            Path allocationFile = allocLoader.getAllocationFile(conf);
            Assert.assertEquals(TestAllocationFileLoaderService.TEST_FAIRSCHED_XML, allocationFile.getName());
            Assert.assertTrue(fs.exists(allocationFile));
        } catch (IOException e) {
            Assert.fail(("Unable to access allocation file from classpath: " + e));
        }
    }

    @Test(timeout = 10000)
    public void testReload() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("  <queue name=\"queueA\">");
        out.println("    <maxRunningApps>1</maxRunningApps>");
        out.println("  </queue>");
        out.println("  <queue name=\"queueB\" />");
        out.println("  <queuePlacementPolicy>");
        out.println("    <rule name='default' />");
        out.println("  </queuePlacementPolicy>");
        out.println("</allocations>");
        out.close();
        ControlledClock clock = new ControlledClock();
        clock.setTime(0);
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService(clock);
        allocLoader.reloadIntervalMs = 5;
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
        AllocationConfiguration allocConf = confHolder.allocConf;
        // Verify conf
        QueuePlacementPolicy policy = allocConf.getPlacementPolicy();
        List<QueuePlacementRule> rules = policy.getRules();
        Assert.assertEquals(1, rules.size());
        Assert.assertEquals(Default.class, rules.get(0).getClass());
        Assert.assertEquals(1, allocConf.getQueueMaxApps("root.queueA"));
        Assert.assertEquals(2, allocConf.getConfiguredQueues().get(LEAF).size());
        Assert.assertTrue(allocConf.getConfiguredQueues().get(LEAF).contains("root.queueA"));
        Assert.assertTrue(allocConf.getConfiguredQueues().get(LEAF).contains("root.queueB"));
        confHolder.allocConf = null;
        // Modify file and advance the clock
        out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("  <queue name=\"queueB\">");
        out.println("    <maxRunningApps>3</maxRunningApps>");
        out.println("  </queue>");
        out.println("  <queuePlacementPolicy>");
        out.println("    <rule name='specified' />");
        out.println("    <rule name='nestedUserQueue' >");
        out.println("         <rule name='primaryGroup' />");
        out.println("    </rule>");
        out.println("    <rule name='default' />");
        out.println("  </queuePlacementPolicy>");
        out.println("</allocations>");
        out.close();
        clock.tickMsec((((System.currentTimeMillis()) + (ALLOC_RELOAD_WAIT_MS)) + 10000));
        allocLoader.start();
        while ((confHolder.allocConf) == null) {
            Thread.sleep(20);
        } 
        // Verify conf
        allocConf = confHolder.allocConf;
        policy = allocConf.getPlacementPolicy();
        rules = policy.getRules();
        Assert.assertEquals(3, rules.size());
        Assert.assertEquals(Specified.class, rules.get(0).getClass());
        Assert.assertEquals(NestedUserQueue.class, rules.get(1).getClass());
        Assert.assertEquals(PrimaryGroup.class, ((NestedUserQueue) (rules.get(1))).nestedRule.getClass());
        Assert.assertEquals(Default.class, rules.get(2).getClass());
        Assert.assertEquals(3, allocConf.getQueueMaxApps("root.queueB"));
        Assert.assertEquals(1, allocConf.getConfiguredQueues().get(LEAF).size());
        Assert.assertTrue(allocConf.getConfiguredQueues().get(LEAF).contains("root.queueB"));
    }

    @Test
    public void testAllocationFileParsing() throws Exception {
        Configuration conf = new YarnConfiguration();
        CustomResourceTypesConfigurationProvider.initResourceTypes(TestAllocationFileLoaderService.A_CUSTOM_RESOURCE);
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        // Give user1 a limit of 10 jobs
        // Set default scheduling policy to DRF
        // Set default fair share preemption threshold to 0.4
        // Set default fair share preemption timeout to 5 minutes
        // Set default min share preemption timeout to 2 minutes
        // Set default limit of AMResourceShare to 0.5f
        // Set default limit of apps per user to 5
        // Set default limit of max resource per queue to 4G and 100 cores
        // Set default limit of apps per queue to 15
        // Create hierarchical queues G,H, with different min/fair
        // share preemption timeouts and preemption thresholds.
        // Also add a child default to make sure it doesn't impact queue H.
        // Make queue F a parent queue without configured leaf queues
        // using the 'type' attribute
        // Give queue E a preemption timeout of one minute
        // Give queue D a limit of 3 running apps and 0.4f maxAMShare
        // Give queue C no minimum
        // Give queue B a minimum of 2048 M
        // Give queue A a minimum of 1024 M
        AllocationFileWriter.create().queue("queueA").minResources("1024mb,0vcores").maxResources("2048mb,10vcores").buildQueue().queue("queueB").minResources("2048mb,0vcores").maxResources("5120mb,110vcores").aclAdministerApps("alice,bob admins").schedulingPolicy("fair").buildQueue().queue("queueC").minResources("5120mb,0vcores").aclSubmitApps("alice,bob admins").buildQueue().queue("queueD").maxRunningApps(3).maxAMShare(0.4).buildQueue().queue("queueE").minSharePreemptionTimeout(60).buildQueue().queue("queueF").parent(true).maxChildResources("2048mb,64vcores").buildQueue().queue("queueG").maxChildResources("2048mb,64vcores").fairSharePreemptionTimeout(120).minSharePreemptionTimeout(50).fairSharePreemptionThreshold(0.6).maxContainerAllocation((("vcores=16, memory-mb=512, " + (TestAllocationFileLoaderService.A_CUSTOM_RESOURCE)) + "=10")).subQueue("queueH").fairSharePreemptionTimeout(180).minSharePreemptionTimeout(40).fairSharePreemptionThreshold(0.7).maxContainerAllocation("1024mb,8vcores").buildSubQueue().buildQueue().queueMaxAppsDefault(15).queueMaxResourcesDefault("4096mb,100vcores").userMaxAppsDefault(5).queueMaxAMShareDefault(0.5).defaultMinSharePreemptionTimeout(120).defaultFairSharePreemptionTimeout(300).defaultFairSharePreemptionThreshold(0.4).defaultQueueSchedulingPolicy("drf").userSettings("user1").maxRunningApps(10).build().writeToFile(TestAllocationFileLoaderService.ALLOC_FILE);
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
        AllocationConfiguration queueConf = confHolder.allocConf;
        Assert.assertEquals(6, queueConf.getConfiguredQueues().get(LEAF).size());
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals(Resources.createResource(2048, 10), queueConf.getMaxResources("root.queueA").getResource());
        Assert.assertEquals(Resources.createResource(5120, 110), queueConf.getMaxResources("root.queueB").getResource());
        Assert.assertEquals(Resources.createResource(4096, 100), queueConf.getMaxResources("root.queueC").getResource());
        Assert.assertEquals(Resources.createResource(4096, 100), queueConf.getMaxResources("root.queueD").getResource());
        Assert.assertEquals(Resources.createResource(4096, 100), queueConf.getMaxResources("root.queueE").getResource());
        Assert.assertEquals(Resources.createResource(4096, 100), queueConf.getMaxResources("root.queueF").getResource());
        Assert.assertEquals(Resources.createResource(4096, 100), queueConf.getMaxResources("root.queueG").getResource());
        Assert.assertEquals(Resources.createResource(4096, 100), queueConf.getMaxResources("root.queueG.queueH").getResource());
        Assert.assertEquals(Resources.createResource(1024, 0), queueConf.getMinResources("root.queueA"));
        Assert.assertEquals(Resources.createResource(2048, 0), queueConf.getMinResources("root.queueB"));
        Assert.assertEquals(Resources.createResource(5120, 0), queueConf.getMinResources("root.queueC"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueD"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueE"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueF"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueG"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueG.queueH"));
        Assert.assertNull("Max child resources unexpectedly set for queue root.queueA", queueConf.getMaxChildResources("root.queueA"));
        Assert.assertNull("Max child resources unexpectedly set for queue root.queueB", queueConf.getMaxChildResources("root.queueB"));
        Assert.assertNull("Max child resources unexpectedly set for queue root.queueC", queueConf.getMaxChildResources("root.queueC"));
        Assert.assertNull("Max child resources unexpectedly set for queue root.queueD", queueConf.getMaxChildResources("root.queueD"));
        Assert.assertNull("Max child resources unexpectedly set for queue root.queueE", queueConf.getMaxChildResources("root.queueE"));
        Assert.assertEquals(Resources.createResource(2048, 64), queueConf.getMaxChildResources("root.queueF").getResource());
        Assert.assertEquals(Resources.createResource(2048, 64), queueConf.getMaxChildResources("root.queueG").getResource());
        Assert.assertNull(("Max child resources unexpectedly set for " + "queue root.queueG.queueH"), queueConf.getMaxChildResources("root.queueG.queueH"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueA"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueB"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueC"));
        Assert.assertEquals(3, queueConf.getQueueMaxApps("root.queueD"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueE"));
        Assert.assertEquals(10, queueConf.getUserMaxApps("user1"));
        Assert.assertEquals(5, queueConf.getUserMaxApps("user2"));
        Assert.assertEquals(0.5F, queueConf.getQueueMaxAMShare(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))), 0.01);
        Assert.assertEquals(0.5F, queueConf.getQueueMaxAMShare("root.queueA"), 0.01);
        Assert.assertEquals(0.5F, queueConf.getQueueMaxAMShare("root.queueB"), 0.01);
        Assert.assertEquals(0.5F, queueConf.getQueueMaxAMShare("root.queueC"), 0.01);
        Assert.assertEquals(0.4F, queueConf.getQueueMaxAMShare("root.queueD"), 0.01);
        Assert.assertEquals(0.5F, queueConf.getQueueMaxAMShare("root.queueE"), 0.01);
        Resource expectedResourceWithCustomType = Resources.createResource(512, 16);
        expectedResourceWithCustomType.setResourceValue(TestAllocationFileLoaderService.A_CUSTOM_RESOURCE, 10);
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation("root.queueA"));
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation("root.queueB"));
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation("root.queueC"));
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation("root.queueD"));
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation("root.queueE"));
        Assert.assertEquals(Resources.unbounded(), queueConf.getQueueMaxContainerAllocation("root.queueF"));
        Assert.assertEquals(expectedResourceWithCustomType, queueConf.getQueueMaxContainerAllocation("root.queueG"));
        Assert.assertEquals(Resources.createResource(1024, 8), queueConf.getQueueMaxContainerAllocation("root.queueG.queueH"));
        Assert.assertEquals(120000, queueConf.getMinSharePreemptionTimeout("root"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueA"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueB"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueC"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueD"));
        Assert.assertEquals(60000, queueConf.getMinSharePreemptionTimeout("root.queueE"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueF"));
        Assert.assertEquals(50000, queueConf.getMinSharePreemptionTimeout("root.queueG"));
        Assert.assertEquals(40000, queueConf.getMinSharePreemptionTimeout("root.queueG.queueH"));
        Assert.assertEquals(300000, queueConf.getFairSharePreemptionTimeout("root"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueA"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueB"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueC"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueD"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueE"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueF"));
        Assert.assertEquals(120000, queueConf.getFairSharePreemptionTimeout("root.queueG"));
        Assert.assertEquals(180000, queueConf.getFairSharePreemptionTimeout("root.queueG.queueH"));
        Assert.assertEquals(0.4F, queueConf.getFairSharePreemptionThreshold("root"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueA"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueB"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueC"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueD"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueE"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueF"), 0.01);
        Assert.assertEquals(0.6F, queueConf.getFairSharePreemptionThreshold("root.queueG"), 0.01);
        Assert.assertEquals(0.7F, queueConf.getFairSharePreemptionThreshold("root.queueG.queueH"), 0.01);
        Assert.assertTrue(queueConf.getConfiguredQueues().get(PARENT).contains("root.queueF"));
        Assert.assertTrue(queueConf.getConfiguredQueues().get(PARENT).contains("root.queueG"));
        Assert.assertTrue(queueConf.getConfiguredQueues().get(LEAF).contains("root.queueG.queueH"));
        // Verify existing queues have default scheduling policy
        Assert.assertEquals(NAME, queueConf.getSchedulingPolicy("root").getName());
        Assert.assertEquals(NAME, queueConf.getSchedulingPolicy("root.queueA").getName());
        // Verify default is overriden if specified explicitly
        Assert.assertEquals(FairSharePolicy.NAME, queueConf.getSchedulingPolicy("root.queueB").getName());
        // Verify new queue gets default scheduling policy
        Assert.assertEquals(NAME, queueConf.getSchedulingPolicy("root.newqueue").getName());
    }

    @Test
    public void testBackwardsCompatibleAllocationFileParsing() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        // Give queue A a minimum of 1024 M
        out.println("<pool name=\"queueA\">");
        out.println("<minResources>1024mb,0vcores</minResources>");
        out.println("</pool>");
        // Give queue B a minimum of 2048 M
        out.println("<pool name=\"queueB\">");
        out.println("<minResources>2048mb,0vcores</minResources>");
        out.println("<aclAdministerApps>alice,bob admins</aclAdministerApps>");
        out.println("</pool>");
        // Give queue C no minimum
        out.println("<pool name=\"queueC\">");
        out.println("<aclSubmitApps>alice,bob admins</aclSubmitApps>");
        out.println("</pool>");
        // Give queue D a limit of 3 running apps
        out.println("<pool name=\"queueD\">");
        out.println("<maxRunningApps>3</maxRunningApps>");
        out.println("</pool>");
        // Give queue E a preemption timeout of one minute and 0.3f threshold
        out.println("<pool name=\"queueE\">");
        out.println("<minSharePreemptionTimeout>60</minSharePreemptionTimeout>");
        out.println("<fairSharePreemptionThreshold>0.3</fairSharePreemptionThreshold>");
        out.println("</pool>");
        // Set default limit of apps per queue to 15
        out.println("<queueMaxAppsDefault>15</queueMaxAppsDefault>");
        // Set default limit of apps per user to 5
        out.println("<userMaxAppsDefault>5</userMaxAppsDefault>");
        // Give user1 a limit of 10 jobs
        out.println("<user name=\"user1\">");
        out.println("<maxRunningApps>10</maxRunningApps>");
        out.println("</user>");
        // Set default min share preemption timeout to 2 minutes
        out.println(("<defaultMinSharePreemptionTimeout>120" + "</defaultMinSharePreemptionTimeout>"));
        // Set fair share preemption timeout to 5 minutes
        out.println("<fairSharePreemptionTimeout>300</fairSharePreemptionTimeout>");
        // Set default fair share preemption threshold to 0.6f
        out.println("<defaultFairSharePreemptionThreshold>0.6</defaultFairSharePreemptionThreshold>");
        out.println("</allocations>");
        out.close();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
        AllocationConfiguration queueConf = confHolder.allocConf;
        Assert.assertEquals(5, queueConf.getConfiguredQueues().get(LEAF).size());
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals(Resources.createResource(1024, 0), queueConf.getMinResources("root.queueA"));
        Assert.assertEquals(Resources.createResource(2048, 0), queueConf.getMinResources("root.queueB"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueC"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueD"));
        Assert.assertEquals(Resources.createResource(0), queueConf.getMinResources("root.queueE"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueA"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueB"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueC"));
        Assert.assertEquals(3, queueConf.getQueueMaxApps("root.queueD"));
        Assert.assertEquals(15, queueConf.getQueueMaxApps("root.queueE"));
        Assert.assertEquals(10, queueConf.getUserMaxApps("user1"));
        Assert.assertEquals(5, queueConf.getUserMaxApps("user2"));
        Assert.assertEquals(120000, queueConf.getMinSharePreemptionTimeout("root"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueA"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueB"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueC"));
        Assert.assertEquals((-1), queueConf.getMinSharePreemptionTimeout("root.queueD"));
        Assert.assertEquals(60000, queueConf.getMinSharePreemptionTimeout("root.queueE"));
        Assert.assertEquals(300000, queueConf.getFairSharePreemptionTimeout("root"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueA"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueB"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueC"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueD"));
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionTimeout("root.queueE"));
        Assert.assertEquals(0.6F, queueConf.getFairSharePreemptionThreshold("root"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold(("root." + (YarnConfiguration.DEFAULT_QUEUE_NAME))), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueA"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueB"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueC"), 0.01);
        Assert.assertEquals((-1), queueConf.getFairSharePreemptionThreshold("root.queueD"), 0.01);
        Assert.assertEquals(0.3F, queueConf.getFairSharePreemptionThreshold("root.queueE"), 0.01);
    }

    @Test
    public void testSimplePlacementPolicyFromConf() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        conf.setBoolean(ALLOW_UNDECLARED_POOLS, false);
        conf.setBoolean(USER_AS_DEFAULT_QUEUE, false);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
        AllocationConfiguration allocConf = confHolder.allocConf;
        QueuePlacementPolicy placementPolicy = allocConf.getPlacementPolicy();
        List<QueuePlacementRule> rules = placementPolicy.getRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals(Specified.class, rules.get(0).getClass());
        Assert.assertEquals(false, rules.get(0).create);
        Assert.assertEquals(Default.class, rules.get(1).getClass());
    }

    /**
     * Verify that you can't place queues at the same level as the root queue in
     * the allocations file.
     */
    @Test(expected = AllocationConfigurationException.class)
    public void testQueueAlongsideRoot() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"root\">");
        out.println("</queue>");
        out.println("<queue name=\"other\">");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
    }

    /**
     * Verify that you can't include periods as the queue name in the allocations
     * file.
     */
    @Test(expected = AllocationConfigurationException.class)
    public void testQueueNameContainingPeriods() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"parent1.child1\">");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
    }

    /**
     * Verify that you can't have the queue name with whitespace only in the
     * allocations file.
     */
    @Test(expected = AllocationConfigurationException.class)
    public void testQueueNameContainingOnlyWhitespace() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"      \">");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
    }

    @Test
    public void testParentTagWithReservation() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"parent\" type=\"parent\">");
        out.println("<reservation>");
        out.println("</reservation>");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        try {
            allocLoader.reloadAllocations();
        } catch (AllocationConfigurationException ex) {
            Assert.assertEquals(ex.getMessage(), ("The configuration settings for root.parent" + ((" are invalid. A queue element that contains child queue elements" + " or that has the type='parent' attribute cannot also include a") + " reservation element.")));
        }
    }

    @Test
    public void testParentWithReservation() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"parent\">");
        out.println("<reservation>");
        out.println("</reservation>");
        out.println(" <queue name=\"child\">");
        out.println(" </queue>");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        try {
            allocLoader.reloadAllocations();
        } catch (AllocationConfigurationException ex) {
            Assert.assertEquals(ex.getMessage(), ("The configuration settings for root.parent" + ((" are invalid. A queue element that contains child queue elements" + " or that has the type='parent' attribute cannot also include a") + " reservation element.")));
        }
    }

    @Test
    public void testParentTagWithChild() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"parent\" type=\"parent\">");
        out.println(" <queue name=\"child\">");
        out.println(" </queue>");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
        AllocationConfiguration queueConf = confHolder.allocConf;
        // Check whether queue 'parent' and 'child' are loaded successfully
        Assert.assertTrue(queueConf.getConfiguredQueues().get(PARENT).contains("root.parent"));
        Assert.assertTrue(queueConf.getConfiguredQueues().get(LEAF).contains("root.parent.child"));
    }

    /**
     * Verify that you can't have the queue name with just a non breaking
     * whitespace in the allocations file.
     */
    @Test(expected = AllocationConfigurationException.class)
    public void testQueueNameContainingNBWhitespace() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TestAllocationFileLoaderService.ALLOC_FILE), StandardCharsets.UTF_8));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"\u00a0\">");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
    }

    /**
     * Verify that defaultQueueSchedulingMode can't accept FIFO as a value.
     */
    @Test(expected = AllocationConfigurationException.class)
    public void testDefaultQueueSchedulingModeIsFIFO() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<defaultQueueSchedulingPolicy>fifo</defaultQueueSchedulingPolicy>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
    }

    @Test
    public void testReservableQueue() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"reservable\">");
        out.println("<reservation>");
        out.println("</reservation>");
        out.println("</queue>");
        out.println("<queue name=\"other\">");
        out.println("</queue>");
        out.println("<reservation-agent>DummyAgentName</reservation-agent>");
        out.println("<reservation-policy>AnyAdmissionPolicy</reservation-policy>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
        AllocationConfiguration allocConf = confHolder.allocConf;
        String reservableQueueName = "root.reservable";
        String nonreservableQueueName = "root.other";
        Assert.assertFalse(allocConf.isReservable(nonreservableQueueName));
        Assert.assertTrue(allocConf.isReservable(reservableQueueName));
        Map<FSQueueType, Set<String>> configuredQueues = allocConf.getConfiguredQueues();
        Assert.assertTrue("reservable queue is expected be to a parent queue", configuredQueues.get(PARENT).contains(reservableQueueName));
        Assert.assertFalse("reservable queue should not be a leaf queue", configuredQueues.get(LEAF).contains(reservableQueueName));
        Assert.assertTrue(allocConf.getMoveOnExpiry(reservableQueueName));
        Assert.assertEquals(DEFAULT_RESERVATION_WINDOW, allocConf.getReservationWindow(reservableQueueName));
        Assert.assertEquals(100, allocConf.getInstantaneousMaxCapacity(reservableQueueName), 1.0E-4);
        Assert.assertEquals("DummyAgentName", allocConf.getReservationAgent(reservableQueueName));
        Assert.assertEquals(100, allocConf.getAverageCapacity(reservableQueueName), 0.001);
        Assert.assertFalse(allocConf.getShowReservationAsQueues(reservableQueueName));
        Assert.assertEquals("AnyAdmissionPolicy", allocConf.getReservationAdmissionPolicy(reservableQueueName));
        Assert.assertEquals(DEFAULT_RESERVATION_PLANNER_NAME, allocConf.getReplanner(reservableQueueName));
        Assert.assertEquals(DEFAULT_RESERVATION_ENFORCEMENT_WINDOW, allocConf.getEnforcementWindow(reservableQueueName));
    }

    /**
     * Verify that you can't have dynamic user queue and reservable queue on
     * the same queue
     */
    @Test(expected = AllocationConfigurationException.class)
    public void testReservableCannotBeCombinedWithDynamicUserQueue() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ALLOCATION_FILE, TestAllocationFileLoaderService.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAllocationFileLoaderService.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"notboth\" type=\"parent\" >");
        out.println("<reservation>");
        out.println("</reservation>");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        AllocationFileLoaderService allocLoader = new AllocationFileLoaderService();
        allocLoader.init(conf);
        TestAllocationFileLoaderService.ReloadListener confHolder = new TestAllocationFileLoaderService.ReloadListener();
        allocLoader.setReloadListener(confHolder);
        allocLoader.reloadAllocations();
    }

    private class ReloadListener implements AllocationFileLoaderService.Listener {
        public AllocationConfiguration allocConf;

        @Override
        public void onReload(AllocationConfiguration info) {
            allocConf = info;
        }

        @Override
        public void onCheck() {
        }
    }
}

