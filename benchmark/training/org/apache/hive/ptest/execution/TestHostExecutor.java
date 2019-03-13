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


import Constants.EXIT_CODE_UNKNOWN;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import junit.framework.Assert;
import org.apache.hive.ptest.execution.conf.Host;
import org.apache.hive.ptest.execution.conf.TestBatch;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.JunitReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@UseReporter(JunitReporter.class)
public class TestHostExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(TestHostExecutor.class);

    private static final String LOCAL_DIR = "/some/local/dir";

    private static final String WORKING_DIR = "/some/working/dir";

    private static final String PRIVATE_KEY = "some.private.key";

    private static final String USER = "someuser";

    private static final String HOST = "somehost";

    private static final int INSTANCE = 13;

    private static final String INSTANCE_NAME = ((((TestHostExecutor.HOST) + "-") + (TestHostExecutor.USER)) + "-") + (TestHostExecutor.INSTANCE);

    private static final String BRANCH = "branch";

    private static final String REPOSITORY = "repository";

    private static final String REPOSITORY_NAME = "repositoryName";

    private static final String DRIVER_PARALLEL_1 = "driver-parallel-1";

    private static final String DRIVER_PARALLEL_2 = "driver-parallel-2";

    private static final String DRIVER_ISOLATED_1 = "driver-isolated-1";

    private static final String DRIVER_ISOLATED_2 = "driver-isolated-2";

    private File baseDir;

    private Host host;

    private File scratchDir;

    private File logDir;

    private File succeededLogDir;

    private File failedLogDir;

    private ListeningExecutorService executor;

    private MockLocalCommandFactory localCommandFactory;

    private LocalCommand localCommand;

    private MockSSHCommandExecutor sshCommandExecutor;

    private MockRSyncCommandExecutor rsyncCommandExecutor;

    private ImmutableMap<String, String> templateDefaults;

    private Logger logger;

    private BlockingQueue<TestBatch> parallelWorkQueue;

    private BlockingQueue<TestBatch> isolatedWorkQueue;

    private Set<TestBatch> failedTestResults;

    private TestBatch testBatchParallel1;

    private TestBatch testBatchParallel2;

    private TestBatch testBatchIsolated1;

    private TestBatch testBatchIsolated2;

    @Test
    public void testBasic() throws Exception {
        HostExecutor executor = createHostExecutor();
        parallelWorkQueue.addAll(Lists.newArrayList(testBatchParallel1, testBatchParallel2));
        parallelWorkQueue.addAll(Lists.newArrayList(testBatchIsolated1, testBatchIsolated2));
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Approvals.verify(getExecutedCommands());
    }

    @Test
    public void testParallelFailsOnExec() throws Exception {
        sshCommandExecutor.putFailure((("bash /some/local/dir/somehost-someuser-0/scratch/hiveptest-" + (testBatchParallel1.getBatchId())) + "_driver-parallel-1.sh"), EXIT_CODE_UNKNOWN);
        HostExecutor executor = createHostExecutor();
        parallelWorkQueue.addAll(Lists.newArrayList(testBatchParallel1));
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Assert.assertTrue(parallelWorkQueue.toString(), parallelWorkQueue.isEmpty());
        Assert.assertEquals(1, sshCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
    }

    @Test
    public void testIsolatedFailsOnExec() throws Exception {
        sshCommandExecutor.putFailure((("bash /some/local/dir/somehost-someuser-0/scratch/hiveptest-" + (testBatchIsolated1.getBatchId())) + "_driver-isolated-1.sh"), EXIT_CODE_UNKNOWN);
        HostExecutor executor = createHostExecutor();
        isolatedWorkQueue.addAll(Lists.newArrayList(testBatchIsolated1));
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Assert.assertTrue(isolatedWorkQueue.toString(), parallelWorkQueue.isEmpty());
        Assert.assertEquals(1, sshCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
    }

    @Test
    public void testParallelFailsOnRsync() throws Exception {
        rsyncCommandExecutor.putFailure(((((("/tmp/hive-ptest-units/TestHostExecutor/scratch/hiveptest-" + (testBatchParallel1.getBatchId())) + "_driver-parallel-1.sh ") + "/some/local/dir/somehost-someuser-0/scratch/hiveptest-") + (testBatchParallel1.getBatchId())) + "_driver-parallel-1.sh"), EXIT_CODE_UNKNOWN);
        HostExecutor executor = createHostExecutor();
        parallelWorkQueue.addAll(Lists.newArrayList(testBatchParallel1));
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Assert.assertTrue(parallelWorkQueue.toString(), parallelWorkQueue.isEmpty());
        Assert.assertEquals(1, rsyncCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
    }

    @Test
    public void testShutdownBeforeExec() throws Exception {
        rsyncCommandExecutor.putFailure(((((("/tmp/hive-ptest-units/TestHostExecutor/scratch/hiveptest-" + (testBatchParallel1.getBatchId())) + "_driver-parallel-1.sh ") + "/some/local/dir/somehost-someuser-0/scratch/hiveptest-") + (testBatchParallel1.getBatchId())) + "_driver-parallel-1.sh"), EXIT_CODE_UNKNOWN);
        HostExecutor executor = createHostExecutor();
        parallelWorkQueue.addAll(Lists.newArrayList(testBatchParallel1));
        executor.shutdownNow();
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Assert.assertEquals(parallelWorkQueue.toString(), 1, parallelWorkQueue.size());
        Approvals.verify(("EMPTY\n" + (getExecutedCommands())));
        Assert.assertEquals(0, rsyncCommandExecutor.getMatchCount());
        Assert.assertTrue(executor.isShutdown());
    }

    @Test
    public void testIsolatedFailsOnRsyncUnknown() throws Exception {
        rsyncCommandExecutor.putFailure(((((("/tmp/hive-ptest-units/TestHostExecutor/scratch/hiveptest-" + (testBatchIsolated1.getBatchId())) + "_driver-isolated-1.sh ") + "/some/local/dir/somehost-someuser-0/scratch/hiveptest-") + (testBatchIsolated1.getBatchId())) + "_driver-isolated-1.sh"), EXIT_CODE_UNKNOWN);
        HostExecutor executor = createHostExecutor();
        isolatedWorkQueue.addAll(Lists.newArrayList(testBatchIsolated1));
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Assert.assertTrue(isolatedWorkQueue.toString(), isolatedWorkQueue.isEmpty());
        Assert.assertEquals(1, rsyncCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
    }

    @Test
    public void testIsolatedFailsOnRsyncOne() throws Exception {
        rsyncCommandExecutor.putFailure(((((("/tmp/hive-ptest-units/TestHostExecutor/scratch/hiveptest-" + (testBatchIsolated1.getBatchId())) + "_driver-isolated-1.sh ") + "/some/local/dir/somehost-someuser-0/scratch/hiveptest-") + (testBatchIsolated1.getBatchId())) + "_driver-isolated-1.sh"), 1);
        HostExecutor executor = createHostExecutor();
        isolatedWorkQueue.addAll(Lists.newArrayList(testBatchIsolated1));
        executor.submitTests(parallelWorkQueue, isolatedWorkQueue, failedTestResults).get();
        Assert.assertEquals(Collections.emptySet(), failedTestResults);
        Assert.assertTrue(isolatedWorkQueue.toString(), parallelWorkQueue.isEmpty());
        Assert.assertEquals(1, rsyncCommandExecutor.getMatchCount());
        Approvals.verify(getExecutedCommands());
    }
}

