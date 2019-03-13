/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.execution;


import ExecutionPhase.TOTAL_RSYNC_TIME;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.apache.hive.ptest.execution.conf.TestBatch;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.JunitReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Assert;
import org.junit.Test;

import static ExecutionPhase.TOTAL_RSYNC_TIME;


@UseReporter(JunitReporter.class)
public class TestExecutionPhase extends AbstractTestPhase {
    private static final String DRIVER = "driver";

    private static final String QFILENAME = "sometest";

    private ExecutionPhase phase;

    private File testDir;

    private Set<String> executedTests;

    private Set<String> failedTests;

    private List<TestBatch> testBatches;

    private TestBatch testBatch;

    @Test
    public void testPassingQFileTest() throws Throwable {
        setupQFile(true);
        copyTestOutput("SomeTest-success.xml", succeededLogDir, testBatch.getName());
        getPhase().execute();
        Approvals.verify(getExecutedCommands());
        Assert.assertEquals(Sets.newHashSet(("SomeTest." + (TestExecutionPhase.QFILENAME))), executedTests);
        Assert.assertEquals(Sets.newHashSet(), failedTests);
    }

    @Test
    public void testFailingQFile() throws Throwable {
        setupQFile(true);
        sshCommandExecutor.putFailure(((((((((((("bash " + (AbstractTestPhase.LOCAL_DIR)) + "/") + (AbstractTestPhase.HOST)) + "-") + (AbstractTestPhase.USER)) + "-0/scratch/hiveptest-") + "1-") + (TestExecutionPhase.DRIVER)) + "-") + (TestExecutionPhase.QFILENAME)) + ".sh"), 1);
        copyTestOutput("SomeTest-failure.xml", failedLogDir, testBatch.getName());
        getPhase().execute();
        Assert.assertEquals(1, sshCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
        Assert.assertEquals(Sets.newHashSet(("SomeTest." + (TestExecutionPhase.QFILENAME))), executedTests);
        Assert.assertEquals(Sets.newHashSet((("SomeTest." + (TestExecutionPhase.QFILENAME)) + " (batchId=1)")), failedTests);
    }

    @Test
    public void testPassingUnitTest() throws Throwable {
        setupUnitTest();
        copyTestOutput("SomeTest-success.xml", succeededLogDir, testBatch.getName());
        getPhase().execute();
        Approvals.verify(getExecutedCommands());
        Assert.assertEquals(Sets.newHashSet(("SomeTest." + (TestExecutionPhase.QFILENAME))), executedTests);
        Assert.assertEquals(Sets.newHashSet(), failedTests);
    }

    @Test
    public void testFailingUnitTest() throws Throwable {
        setupUnitTest();
        sshCommandExecutor.putFailure((((((((((("bash " + (AbstractTestPhase.LOCAL_DIR)) + "/") + (AbstractTestPhase.HOST)) + "-") + (AbstractTestPhase.USER)) + "-0/scratch/hiveptest-") + (testBatch.getBatchId())) + "_") + (TestExecutionPhase.DRIVER)) + ".sh"), 1);
        copyTestOutput("SomeTest-failure.xml", failedLogDir, testBatch.getName());
        getPhase().execute();
        Assert.assertEquals(1, sshCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
        Assert.assertEquals(Sets.newHashSet(("SomeTest." + (TestExecutionPhase.QFILENAME))), executedTests);
        Assert.assertEquals(Sets.newHashSet((("SomeTest." + (TestExecutionPhase.QFILENAME)) + " (batchId=1)")), failedTests);
    }

    @Test
    public void testPerfMetrics() throws Throwable {
        // when test is successful
        setupUnitTest();
        copyTestOutput("SomeTest-success.xml", succeededLogDir, testBatch.getName());
        Phase phase = getPhase();
        phase.execute();
        Assert.assertNotNull("Perf metrics should have been initialized", phase.getPerfMetrics());
        Assert.assertNotNull(((TOTAL_RSYNC_TIME) + " should have been initialized"), phase.getPerfMetrics().get(TOTAL_RSYNC_TIME));
        Assert.assertTrue("Total Rsync Elapsed time should have been greater than 0", ((phase.getPerfMetrics().get(TOTAL_RSYNC_TIME)) > 0));
        // when test fails
        setupUnitTest();
        sshCommandExecutor.putFailure((((((((((("bash " + (AbstractTestPhase.LOCAL_DIR)) + "/") + (AbstractTestPhase.HOST)) + "-") + (AbstractTestPhase.USER)) + "-0/scratch/hiveptest-") + (testBatch.getBatchId())) + "_") + (TestExecutionPhase.DRIVER)) + ".sh"), 1);
        copyTestOutput("SomeTest-failure.xml", failedLogDir, testBatch.getName());
        phase = getPhase();
        phase.execute();
        Assert.assertNotNull("Perf metrics should have been initialized", phase.getPerfMetrics());
        Assert.assertNotNull(((TOTAL_RSYNC_TIME) + " should have been initialized"), phase.getPerfMetrics().get(TOTAL_RSYNC_TIME));
        Assert.assertTrue("Total Rsync Elapsed time should have been greater than 0", ((phase.getPerfMetrics().get(TOTAL_RSYNC_TIME)) > 0));
    }

    @Test(timeout = 20000)
    public void testTimedOutUnitTest() throws Throwable {
        setupUnitTest(3);
        copyTestOutput("SomeTest-success.xml", succeededLogDir, testBatch.getName(), "TEST-TestClass-0.xml");
        copyTestOutput("SomeTest-success.xml", succeededLogDir, testBatch.getName(), "TEST-TestClass-1.xml");
        getPhase().execute();
        Approvals.verify(getExecutedCommands());
        Assert.assertEquals(1, failedTests.size());
        Assert.assertEquals("TestClass-2 - did not produce a TEST-*.xml file (likely timed out) (batchId=1)", failedTests.iterator().next());
    }
}

