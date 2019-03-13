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
package org.apache.hadoop.hbase;


import HealthCheckerExitStatus.FAILED;
import HealthCheckerExitStatus.SUCCESS;
import HealthCheckerExitStatus.TIMED_OUT;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestNodeHealthCheckChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNodeHealthCheckChore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestNodeHealthCheckChore.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final int SCRIPT_TIMEOUT = 5000;

    private File healthScriptFile;

    private String eol = System.getProperty("line.separator");

    @Test
    public void testHealthCheckerSuccess() throws Exception {
        String normalScript = "echo \"I am all fine\"";
        healthCheckerTest(normalScript, SUCCESS);
    }

    @Test
    public void testHealthCheckerFail() throws Exception {
        String errorScript = ("echo ERROR" + (eol)) + "echo \"Node not healthy\"";
        healthCheckerTest(errorScript, FAILED);
    }

    @Test
    public void testHealthCheckerTimeout() throws Exception {
        String timeOutScript = ("sleep 10" + (eol)) + "echo \"I am fine\"";
        healthCheckerTest(timeOutScript, TIMED_OUT);
    }

    @Test
    public void testRSHealthChore() throws Exception {
        Stoppable stop = new TestNodeHealthCheckChore.StoppableImplementation();
        Configuration conf = getConfForNodeHealthScript();
        String errorScript = ("echo ERROR" + (eol)) + " echo \"Server not healthy\"";
        createScript(errorScript, true);
        HealthCheckChore rsChore = new HealthCheckChore(100, stop, conf);
        try {
            // Default threshold is three.
            rsChore.chore();
            rsChore.chore();
            Assert.assertFalse("Stoppable must not be stopped.", stop.isStopped());
            rsChore.chore();
            Assert.assertTrue("Stoppable must have been stopped.", stop.isStopped());
        } finally {
            stop.stop("Finished w/ test");
        }
    }

    /**
     * Simple helper class that just keeps track of whether or not its stopped.
     */
    private static class StoppableImplementation implements Stoppable {
        private volatile boolean stop = false;

        @Override
        public void stop(String why) {
            this.stop = true;
        }

        @Override
        public boolean isStopped() {
            return this.stop;
        }
    }
}

