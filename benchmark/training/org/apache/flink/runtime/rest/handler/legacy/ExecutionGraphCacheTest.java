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
package org.apache.flink.runtime.rest.handler.legacy;


import JobStatus.RUNNING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.accumulators.StringifiedAccumulatorResult;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.executiongraph.AccessExecutionGraph;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.rest.handler.legacy.utils.ArchivedExecutionGraphBuilder;
import org.apache.flink.runtime.webmonitor.TestingRestfulGateway;
import org.apache.flink.util.ExecutorUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ExecutionGraphCache}.
 */
public class ExecutionGraphCacheTest extends TestLogger {
    private static ArchivedExecutionGraph expectedExecutionGraph;

    private static final JobID expectedJobId = new JobID();

    /**
     * Tests that we can cache AccessExecutionGraphs over multiple accesses.
     */
    @Test
    public void testExecutionGraphCaching() throws Exception {
        final Time timeout = Time.milliseconds(100L);
        final Time timeToLive = Time.hours(1L);
        final ExecutionGraphCacheTest.CountingRestfulGateway restfulGateway = createCountingRestfulGateway(ExecutionGraphCacheTest.expectedJobId, CompletableFuture.completedFuture(ExecutionGraphCacheTest.expectedExecutionGraph));
        try (ExecutionGraphCache executionGraphCache = new ExecutionGraphCache(timeout, timeToLive)) {
            CompletableFuture<AccessExecutionGraph> accessExecutionGraphFuture = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, accessExecutionGraphFuture.get());
            accessExecutionGraphFuture = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, accessExecutionGraphFuture.get());
            Assert.assertThat(restfulGateway.getNumRequestJobCalls(), Matchers.equalTo(1));
        }
    }

    /**
     * Tests that an AccessExecutionGraph is invalidated after its TTL expired.
     */
    @Test
    public void testExecutionGraphEntryInvalidation() throws Exception {
        final Time timeout = Time.milliseconds(100L);
        final Time timeToLive = Time.milliseconds(1L);
        final ExecutionGraphCacheTest.CountingRestfulGateway restfulGateway = createCountingRestfulGateway(ExecutionGraphCacheTest.expectedJobId, CompletableFuture.completedFuture(ExecutionGraphCacheTest.expectedExecutionGraph), CompletableFuture.completedFuture(ExecutionGraphCacheTest.expectedExecutionGraph));
        try (ExecutionGraphCache executionGraphCache = new ExecutionGraphCache(timeout, timeToLive)) {
            CompletableFuture<AccessExecutionGraph> executionGraphFuture = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, executionGraphFuture.get());
            // sleep for the TTL
            Thread.sleep(((timeToLive.toMilliseconds()) * 5L));
            CompletableFuture<AccessExecutionGraph> executionGraphFuture2 = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, executionGraphFuture2.get());
            Assert.assertThat(restfulGateway.getNumRequestJobCalls(), Matchers.equalTo(2));
        }
    }

    /**
     * Tests that a failure in requesting an AccessExecutionGraph from the gateway, will not create
     * a cache entry --> another cache request will trigger a new gateway request.
     */
    @Test
    public void testImmediateCacheInvalidationAfterFailure() throws Exception {
        final Time timeout = Time.milliseconds(100L);
        final Time timeToLive = Time.hours(1L);
        // let's first answer with a JobNotFoundException and then only with the correct result
        final ExecutionGraphCacheTest.CountingRestfulGateway restfulGateway = createCountingRestfulGateway(ExecutionGraphCacheTest.expectedJobId, FutureUtils.completedExceptionally(new org.apache.flink.runtime.messages.FlinkJobNotFoundException(ExecutionGraphCacheTest.expectedJobId)), CompletableFuture.completedFuture(ExecutionGraphCacheTest.expectedExecutionGraph));
        try (ExecutionGraphCache executionGraphCache = new ExecutionGraphCache(timeout, timeToLive)) {
            CompletableFuture<AccessExecutionGraph> executionGraphFuture = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            try {
                executionGraphFuture.get();
                Assert.fail("The execution graph future should have been completed exceptionally.");
            } catch (ExecutionException ee) {
                Assert.assertTrue(((ee.getCause()) instanceof FlinkException));
            }
            CompletableFuture<AccessExecutionGraph> executionGraphFuture2 = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, executionGraphFuture2.get());
        }
    }

    /**
     * Tests that cache entries are cleaned up when their TTL has expired upon
     * calling {@link ExecutionGraphCache#cleanup()}.
     */
    @Test
    public void testCacheEntryCleanup() throws Exception {
        final Time timeout = Time.milliseconds(100L);
        final Time timeToLive = Time.milliseconds(1L);
        final JobID expectedJobId2 = new JobID();
        final ArchivedExecutionGraph expectedExecutionGraph2 = new ArchivedExecutionGraphBuilder().build();
        final AtomicInteger requestJobCalls = new AtomicInteger(0);
        final TestingRestfulGateway restfulGateway = TestingRestfulGateway.newBuilder().setRequestJobFunction(( jobId) -> {
            requestJobCalls.incrementAndGet();
            if (jobId.equals(ExecutionGraphCacheTest.expectedJobId)) {
                return CompletableFuture.completedFuture(ExecutionGraphCacheTest.expectedExecutionGraph);
            } else
                if (jobId.equals(expectedJobId2)) {
                    return CompletableFuture.completedFuture(expectedExecutionGraph2);
                } else {
                    throw new AssertionError("Invalid job id received.");
                }

        }).build();
        try (ExecutionGraphCache executionGraphCache = new ExecutionGraphCache(timeout, timeToLive)) {
            CompletableFuture<AccessExecutionGraph> executionGraph1Future = executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway);
            CompletableFuture<AccessExecutionGraph> executionGraph2Future = executionGraphCache.getExecutionGraph(expectedJobId2, restfulGateway);
            Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, executionGraph1Future.get());
            Assert.assertEquals(expectedExecutionGraph2, executionGraph2Future.get());
            Assert.assertThat(requestJobCalls.get(), Matchers.equalTo(2));
            Thread.sleep(timeToLive.toMilliseconds());
            executionGraphCache.cleanup();
            Assert.assertTrue(((executionGraphCache.size()) == 0));
        }
    }

    /**
     * Tests that concurrent accesses only trigger a single AccessExecutionGraph request.
     */
    @Test
    public void testConcurrentAccess() throws Exception {
        final Time timeout = Time.milliseconds(100L);
        final Time timeToLive = Time.hours(1L);
        final ExecutionGraphCacheTest.CountingRestfulGateway restfulGateway = createCountingRestfulGateway(ExecutionGraphCacheTest.expectedJobId, CompletableFuture.completedFuture(ExecutionGraphCacheTest.expectedExecutionGraph));
        final int numConcurrentAccesses = 10;
        final ArrayList<CompletableFuture<AccessExecutionGraph>> executionGraphFutures = new ArrayList<>(numConcurrentAccesses);
        final ExecutorService executor = Executors.newFixedThreadPool(numConcurrentAccesses);
        try (ExecutionGraphCache executionGraphCache = new ExecutionGraphCache(timeout, timeToLive)) {
            for (int i = 0; i < numConcurrentAccesses; i++) {
                CompletableFuture<AccessExecutionGraph> executionGraphFuture = CompletableFuture.supplyAsync(() -> executionGraphCache.getExecutionGraph(ExecutionGraphCacheTest.expectedJobId, restfulGateway), executor).thenCompose(Function.identity());
                executionGraphFutures.add(executionGraphFuture);
            }
            final CompletableFuture<Collection<AccessExecutionGraph>> allExecutionGraphFutures = FutureUtils.combineAll(executionGraphFutures);
            Collection<AccessExecutionGraph> allExecutionGraphs = allExecutionGraphFutures.get();
            for (AccessExecutionGraph executionGraph : allExecutionGraphs) {
                Assert.assertEquals(ExecutionGraphCacheTest.expectedExecutionGraph, executionGraph);
            }
            Assert.assertThat(restfulGateway.getNumRequestJobCalls(), Matchers.equalTo(1));
        } finally {
            ExecutorUtils.gracefulShutdown(5000L, TimeUnit.MILLISECONDS, executor);
        }
    }

    /**
     * {@link RestfulGateway} implementation which counts the number of {@link #requestJob(JobID, Time)} calls.
     */
    private static class CountingRestfulGateway extends TestingRestfulGateway {
        private final JobID expectedJobId;

        private AtomicInteger numRequestJobCalls = new AtomicInteger(0);

        private CountingRestfulGateway(JobID expectedJobId, Function<JobID, CompletableFuture<ArchivedExecutionGraph>> requestJobFunction) {
            this.expectedJobId = Preconditions.checkNotNull(expectedJobId);
            this.requestJobFunction = Preconditions.checkNotNull(requestJobFunction);
        }

        @Override
        public CompletableFuture<ArchivedExecutionGraph> requestJob(JobID jobId, Time timeout) {
            Assert.assertThat(jobId, Matchers.equalTo(expectedJobId));
            numRequestJobCalls.incrementAndGet();
            return super.requestJob(jobId, timeout);
        }

        public int getNumRequestJobCalls() {
            return numRequestJobCalls.get();
        }
    }

    private static final class SuspendableAccessExecutionGraph extends ArchivedExecutionGraph {
        private static final long serialVersionUID = -6796543726305778101L;

        private JobStatus jobStatus;

        public SuspendableAccessExecutionGraph(JobID jobId) {
            super(jobId, "ExecutionGraphCacheTest", Collections.emptyMap(), Collections.emptyList(), new long[0], RUNNING, new org.apache.flink.runtime.executiongraph.ErrorInfo(new FlinkException("Test"), 42L), "", new StringifiedAccumulatorResult[0], Collections.emptyMap(), new org.apache.flink.api.common.ArchivedExecutionConfig(new ExecutionConfig()), false, null, null);
            jobStatus = super.getState();
        }

        @Override
        public JobStatus getState() {
            return jobStatus;
        }

        public void setJobStatus(JobStatus jobStatus) {
            this.jobStatus = jobStatus;
        }
    }
}

