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
package org.apache.flink.runtime.jobmaster;


import FlinkUserCodeClassLoaders.ResolveOrder;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.blob.VoidPermanentBlobService;
import org.apache.flink.runtime.execution.librarycache.BlobLibraryCacheManager;
import org.apache.flink.runtime.execution.librarycache.LibraryCacheManager;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmaster.factories.JobMasterService;
import org.apache.flink.runtime.jobmaster.factories.JobMasterServiceFactory;
import org.apache.flink.runtime.jobmaster.factories.TestingJobMasterServiceFactory;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link JobManagerRunner}.
 */
public class JobManagerRunnerTest extends TestLogger {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static JobGraph jobGraph;

    private static ArchivedExecutionGraph archivedExecutionGraph;

    private static LibraryCacheManager libraryCacheManager;

    private static JobMasterServiceFactory defaultJobMasterServiceFactory;

    private TestingHighAvailabilityServices haServices;

    private TestingLeaderElectionService leaderElectionService;

    private TestingFatalErrorHandler fatalErrorHandler;

    @Test
    public void testJobCompletion() throws Exception {
        final JobManagerRunner jobManagerRunner = createJobManagerRunner();
        try {
            jobManagerRunner.start();
            final CompletableFuture<ArchivedExecutionGraph> resultFuture = jobManagerRunner.getResultFuture();
            Assert.assertThat(resultFuture.isDone(), Matchers.is(false));
            jobManagerRunner.jobReachedGloballyTerminalState(JobManagerRunnerTest.archivedExecutionGraph);
            Assert.assertThat(resultFuture.get(), Matchers.is(JobManagerRunnerTest.archivedExecutionGraph));
        } finally {
            jobManagerRunner.close();
        }
    }

    @Test
    public void testJobFinishedByOther() throws Exception {
        final JobManagerRunner jobManagerRunner = createJobManagerRunner();
        try {
            jobManagerRunner.start();
            final CompletableFuture<ArchivedExecutionGraph> resultFuture = jobManagerRunner.getResultFuture();
            Assert.assertThat(resultFuture.isDone(), Matchers.is(false));
            jobManagerRunner.jobFinishedByOther();
            try {
                resultFuture.get();
                Assert.fail("Should have failed.");
            } catch (ExecutionException ee) {
                Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.instanceOf(JobNotFinishedException.class));
            }
        } finally {
            jobManagerRunner.close();
        }
    }

    @Test
    public void testShutDown() throws Exception {
        final JobManagerRunner jobManagerRunner = createJobManagerRunner();
        try {
            jobManagerRunner.start();
            final CompletableFuture<ArchivedExecutionGraph> resultFuture = jobManagerRunner.getResultFuture();
            Assert.assertThat(resultFuture.isDone(), Matchers.is(false));
            jobManagerRunner.closeAsync();
            try {
                resultFuture.get();
                Assert.fail("Should have failed.");
            } catch (ExecutionException ee) {
                Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.instanceOf(JobNotFinishedException.class));
            }
        } finally {
            jobManagerRunner.close();
        }
    }

    @Test
    public void testLibraryCacheManagerRegistration() throws Exception {
        final BlobLibraryCacheManager libraryCacheManager = new BlobLibraryCacheManager(VoidPermanentBlobService.INSTANCE, ResolveOrder.CHILD_FIRST, new String[]{  });
        final JobManagerRunner jobManagerRunner = createJobManagerRunner(libraryCacheManager);
        try {
            jobManagerRunner.start();
            final JobID jobID = JobManagerRunnerTest.jobGraph.getJobID();
            Assert.assertThat(libraryCacheManager.hasClassLoader(jobID), Matchers.is(true));
            jobManagerRunner.close();
            Assert.assertThat(libraryCacheManager.hasClassLoader(jobID), Matchers.is(false));
        } finally {
            jobManagerRunner.close();
        }
    }

    /**
     * Tests that the {@link JobManagerRunner} always waits for the previous leadership operation
     * (granting or revoking leadership) to finish before starting a new leadership operation.
     */
    @Test
    public void testConcurrentLeadershipOperationsBlockingSuspend() throws Exception {
        final CompletableFuture<Acknowledge> suspendedFuture = new CompletableFuture<>();
        TestingJobMasterServiceFactory jobMasterServiceFactory = new TestingJobMasterServiceFactory(() -> new TestingJobMasterService("localhost", ( e) -> suspendedFuture));
        JobManagerRunner jobManagerRunner = createJobManagerRunner(jobMasterServiceFactory);
        jobManagerRunner.start();
        leaderElectionService.isLeader(UUID.randomUUID()).get();
        leaderElectionService.notLeader();
        final CompletableFuture<UUID> leaderFuture = leaderElectionService.isLeader(UUID.randomUUID());
        // the new leadership should wait first for the suspension to happen
        Assert.assertThat(leaderFuture.isDone(), Matchers.is(false));
        try {
            leaderFuture.get(1L, TimeUnit.MILLISECONDS);
            Assert.fail("Granted leadership even though the JobMaster has not been suspended.");
        } catch (TimeoutException expected) {
            // expected
        }
        suspendedFuture.complete(Acknowledge.get());
        leaderFuture.get();
    }

    /**
     * Tests that the {@link JobManagerRunner} always waits for the previous leadership operation
     * (granting or revoking leadership) to finish before starting a new leadership operation.
     */
    @Test
    public void testConcurrentLeadershipOperationsBlockingGainLeadership() throws Exception {
        final CompletableFuture<Exception> suspendFuture = new CompletableFuture<>();
        final CompletableFuture<Acknowledge> startFuture = new CompletableFuture<>();
        TestingJobMasterServiceFactory jobMasterServiceFactory = new TestingJobMasterServiceFactory(() -> new TestingJobMasterService("localhost", ( e) -> {
            suspendFuture.complete(e);
            return CompletableFuture.completedFuture(Acknowledge.get());
        }, ( ignored) -> startFuture));
        JobManagerRunner jobManagerRunner = createJobManagerRunner(jobMasterServiceFactory);
        jobManagerRunner.start();
        leaderElectionService.isLeader(UUID.randomUUID());
        leaderElectionService.notLeader();
        // suspending should wait for the start to happen first
        Assert.assertThat(suspendFuture.isDone(), Matchers.is(false));
        try {
            suspendFuture.get(1L, TimeUnit.MILLISECONDS);
            Assert.fail("Suspended leadership even though the JobMaster has not been started.");
        } catch (TimeoutException expected) {
            // expected
        }
        startFuture.complete(Acknowledge.get());
        suspendFuture.get();
    }
}

