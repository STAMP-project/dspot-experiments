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
package org.apache.hadoop.util;


import NodeHealthScriptRunner.NODE_HEALTH_SCRIPT_TIMED_OUT_MSG;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;

import static Shell.WINDOWS;


public class TestNodeHealthScriptRunner {
    protected static File testRootDir = new File("target", ((TestNodeHealthScriptRunner.class.getName()) + "-localDir")).getAbsoluteFile();

    private File nodeHealthscriptFile = new File(TestNodeHealthScriptRunner.testRootDir, Shell.appendScriptExtension("failingscript"));

    @Test
    public void testNodeHealthScriptShouldRun() throws IOException {
        Assert.assertFalse("Node health script should start", NodeHealthScriptRunner.shouldRun(nodeHealthscriptFile.getAbsolutePath()));
        writeNodeHealthScriptFile("", false);
        // Node health script should not start if the node health script is not
        // executable.
        Assert.assertFalse("Node health script should start", NodeHealthScriptRunner.shouldRun(nodeHealthscriptFile.getAbsolutePath()));
        writeNodeHealthScriptFile("", true);
        Assert.assertTrue("Node health script should start", NodeHealthScriptRunner.shouldRun(nodeHealthscriptFile.getAbsolutePath()));
    }

    @Test
    public void testNodeHealthScript() throws Exception {
        String errorScript = "echo ERROR\n echo \"Tracker not healthy\"";
        String normalScript = "echo \"I am all fine\"";
        String timeOutScript = (WINDOWS) ? "@echo off\nping -n 4 127.0.0.1 >nul\necho \"I am fine\"" : "sleep 4\necho \"I am fine\"";
        Configuration conf = new Configuration();
        writeNodeHealthScriptFile(normalScript, true);
        NodeHealthScriptRunner nodeHealthScriptRunner = new NodeHealthScriptRunner(nodeHealthscriptFile.getAbsolutePath(), 500, 1000, new String[]{  });
        nodeHealthScriptRunner.init(conf);
        TimerTask timerTask = nodeHealthScriptRunner.getTimerTask();
        timerTask.run();
        // Normal Script runs successfully
        Assert.assertTrue("Node health status reported unhealthy", nodeHealthScriptRunner.isHealthy());
        Assert.assertEquals("", nodeHealthScriptRunner.getHealthReport());
        // Error script.
        writeNodeHealthScriptFile(errorScript, true);
        // Run timer
        timerTask.run();
        Assert.assertFalse("Node health status reported healthy", nodeHealthScriptRunner.isHealthy());
        Assert.assertTrue(nodeHealthScriptRunner.getHealthReport().contains("ERROR"));
        // Healthy script.
        writeNodeHealthScriptFile(normalScript, true);
        timerTask.run();
        Assert.assertTrue("Node health status reported unhealthy", nodeHealthScriptRunner.isHealthy());
        Assert.assertEquals("", nodeHealthScriptRunner.getHealthReport());
        // Timeout script.
        writeNodeHealthScriptFile(timeOutScript, true);
        timerTask.run();
        Assert.assertFalse("Node health status reported healthy even after timeout", nodeHealthScriptRunner.isHealthy());
        Assert.assertEquals(NODE_HEALTH_SCRIPT_TIMED_OUT_MSG, nodeHealthScriptRunner.getHealthReport());
    }
}

