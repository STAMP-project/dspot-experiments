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
package org.apache.hadoop.yarn.server.nodemanager;


import NodeHealthCheckerService.SEPARATOR;
import NodeHealthScriptRunner.NODE_HEALTH_SCRIPT_TIMED_OUT_MSG;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.NodeHealthScriptRunner;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNodeHealthService {
    private static volatile Logger LOG = LoggerFactory.getLogger(TestNodeHealthService.class);

    protected static File testRootDir = new File("target", ((TestNodeHealthService.class.getName()) + "-localDir")).getAbsoluteFile();

    static final File nodeHealthConfigFile = new File(TestNodeHealthService.testRootDir, "modified-mapred-site.xml");

    private File nodeHealthscriptFile = new File(TestNodeHealthService.testRootDir, Shell.appendScriptExtension("failingscript"));

    @Test
    public void testNodeHealthService() throws Exception {
        RecordFactory factory = RecordFactoryProvider.getRecordFactory(null);
        NodeHealthStatus healthStatus = factory.newRecordInstance(NodeHealthStatus.class);
        Configuration conf = getConfForNodeHealthScript();
        conf.writeXml(new FileOutputStream(TestNodeHealthService.nodeHealthConfigFile));
        conf.addResource(TestNodeHealthService.nodeHealthConfigFile.getName());
        writeNodeHealthScriptFile("", true);
        LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
        NodeHealthScriptRunner nodeHealthScriptRunner = Mockito.spy(NodeManager.getNodeHealthScriptRunner(conf));
        NodeHealthCheckerService nodeHealthChecker = new NodeHealthCheckerService(nodeHealthScriptRunner, dirsHandler);
        nodeHealthChecker.init(conf);
        Mockito.doReturn(true).when(nodeHealthScriptRunner).isHealthy();
        Mockito.doReturn("").when(nodeHealthScriptRunner).getHealthReport();
        setHealthStatus(healthStatus, nodeHealthChecker.isHealthy(), nodeHealthChecker.getHealthReport(), nodeHealthChecker.getLastHealthReportTime());
        TestNodeHealthService.LOG.info("Checking initial healthy condition");
        // Check proper report conditions.
        Assert.assertTrue("Node health status reported unhealthy", healthStatus.getIsNodeHealthy());
        Assert.assertTrue("Node health status reported unhealthy", healthStatus.getHealthReport().equals(nodeHealthChecker.getHealthReport()));
        Mockito.doReturn(false).when(nodeHealthScriptRunner).isHealthy();
        // update health status
        setHealthStatus(healthStatus, nodeHealthChecker.isHealthy(), nodeHealthChecker.getHealthReport(), nodeHealthChecker.getLastHealthReportTime());
        TestNodeHealthService.LOG.info("Checking Healthy--->Unhealthy");
        Assert.assertFalse("Node health status reported healthy", healthStatus.getIsNodeHealthy());
        Assert.assertTrue("Node health status reported healthy", healthStatus.getHealthReport().equals(nodeHealthChecker.getHealthReport()));
        Mockito.doReturn(true).when(nodeHealthScriptRunner).isHealthy();
        setHealthStatus(healthStatus, nodeHealthChecker.isHealthy(), nodeHealthChecker.getHealthReport(), nodeHealthChecker.getLastHealthReportTime());
        TestNodeHealthService.LOG.info("Checking UnHealthy--->healthy");
        // Check proper report conditions.
        Assert.assertTrue("Node health status reported unhealthy", healthStatus.getIsNodeHealthy());
        Assert.assertTrue("Node health status reported unhealthy", healthStatus.getHealthReport().equals(nodeHealthChecker.getHealthReport()));
        // Healthy to timeout transition.
        Mockito.doReturn(false).when(nodeHealthScriptRunner).isHealthy();
        Mockito.doReturn(NODE_HEALTH_SCRIPT_TIMED_OUT_MSG).when(nodeHealthScriptRunner).getHealthReport();
        setHealthStatus(healthStatus, nodeHealthChecker.isHealthy(), nodeHealthChecker.getHealthReport(), nodeHealthChecker.getLastHealthReportTime());
        TestNodeHealthService.LOG.info("Checking Healthy--->timeout");
        Assert.assertFalse("Node health status reported healthy even after timeout", healthStatus.getIsNodeHealthy());
        Assert.assertTrue("Node script time out message not propagated", healthStatus.getHealthReport().equals(Joiner.on(SEPARATOR).skipNulls().join(NODE_HEALTH_SCRIPT_TIMED_OUT_MSG, Strings.emptyToNull(nodeHealthChecker.getDiskHandler().getDisksHealthReport(false)))));
    }
}

