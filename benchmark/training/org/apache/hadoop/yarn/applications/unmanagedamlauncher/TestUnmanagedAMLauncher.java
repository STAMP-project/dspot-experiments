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
package org.apache.hadoop.yarn.applications.unmanagedamlauncher;


import YarnApplicationAttemptState.LAUNCHED;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestUnmanagedAMLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(TestUnmanagedAMLauncher.class);

    protected static MiniYARNCluster yarnCluster = null;

    protected static Configuration conf = new YarnConfiguration();

    @Test(timeout = 30000)
    public void testUMALauncher() throws Exception {
        String classpath = TestUnmanagedAMLauncher.getTestRuntimeClasspath();
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            TestUnmanagedAMLauncher.LOG.error("JAVA_HOME not defined. Test not running.");
            return;
        }
        String[] args = new String[]{ "--classpath", classpath, "--queue", "default", "--cmd", ((javaHome + "/bin/java -Xmx512m ") + (TestUnmanagedAMLauncher.class.getCanonicalName())) + " success" };
        TestUnmanagedAMLauncher.LOG.info("Initializing Launcher");
        UnmanagedAMLauncher launcher = new UnmanagedAMLauncher(new Configuration(TestUnmanagedAMLauncher.yarnCluster.getConfig())) {
            public void launchAM(ApplicationAttemptId attemptId) throws IOException, YarnException {
                YarnApplicationAttemptState attemptState = rmClient.getApplicationAttemptReport(attemptId).getYarnApplicationAttemptState();
                Assert.assertTrue(attemptState.equals(LAUNCHED));
                super.launchAM(attemptId);
            }
        };
        boolean initSuccess = launcher.init(args);
        Assert.assertTrue(initSuccess);
        TestUnmanagedAMLauncher.LOG.info("Running Launcher");
        boolean result = launcher.run();
        TestUnmanagedAMLauncher.LOG.info(("Launcher run completed. Result=" + result));
        Assert.assertTrue(result);
    }

    @Test(timeout = 30000)
    public void testUMALauncherError() throws Exception {
        String classpath = TestUnmanagedAMLauncher.getTestRuntimeClasspath();
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            TestUnmanagedAMLauncher.LOG.error("JAVA_HOME not defined. Test not running.");
            return;
        }
        String[] args = new String[]{ "--classpath", classpath, "--queue", "default", "--cmd", ((javaHome + "/bin/java -Xmx512m ") + (TestUnmanagedAMLauncher.class.getCanonicalName())) + " failure" };
        TestUnmanagedAMLauncher.LOG.info("Initializing Launcher");
        UnmanagedAMLauncher launcher = new UnmanagedAMLauncher(new Configuration(TestUnmanagedAMLauncher.yarnCluster.getConfig()));
        boolean initSuccess = launcher.init(args);
        Assert.assertTrue(initSuccess);
        TestUnmanagedAMLauncher.LOG.info("Running Launcher");
        try {
            launcher.run();
            Assert.fail("Expected an exception to occur as launch should have failed");
        } catch (RuntimeException e) {
            // Expected
        }
    }
}

