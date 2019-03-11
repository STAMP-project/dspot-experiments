/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.overlord.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.druid.guice.JsonConfigurator;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;


public class RemoteTaskRunnerConfigTest {
    private static final ObjectMapper mapper = new DefaultObjectMapper();

    private static final Period DEFAULT_TIMEOUT = Period.ZERO;

    private static final String DEFAULT_VERSION = "";

    private static final long DEFAULT_MAX_ZNODE = 10 * 1024;

    private static final int DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS = 5;

    private static final int DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST = 5;

    private static final Period DEFAULT_TASK_BACKOFF = new Period("PT10M");

    private static final Period DEFAULT_BLACKLIST_CLEANUP_PERIOD = new Period("PT5M");

    @Test
    public void testIsJsonConfiguratable() {
        JsonConfigurator.verifyClazzIsConfigurable(RemoteTaskRunnerConfigTest.mapper, RemoteTaskRunnerConfig.class, null);
    }

    @Test
    public void testGetTaskAssignmentTimeout() throws Exception {
        final Period timeout = Period.hours(1);
        Assert.assertEquals(timeout, reflect(generateRemoteTaskRunnerConfig(timeout, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getTaskAssignmentTimeout());
    }

    @Test
    public void testGetPendingTasksRunnerNumThreads() throws Exception {
        final int pendingTasksRunnerNumThreads = 20;
        Assert.assertEquals(pendingTasksRunnerNumThreads, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, pendingTasksRunnerNumThreads, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getPendingTasksRunnerNumThreads());
    }

    @Test
    public void testGetMinWorkerVersion() throws Exception {
        final String version = "some version";
        Assert.assertEquals(version, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, version, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getMinWorkerVersion());
    }

    @Test
    public void testGetMaxZnodeBytes() throws Exception {
        final long max = 20 * 1024;
        Assert.assertEquals(max, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, max, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getMaxZnodeBytes());
    }

    @Test
    public void testGetTaskShutdownLinkTimeout() throws Exception {
        final Period timeout = Period.hours(1);
        Assert.assertEquals(timeout, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, timeout, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getTaskShutdownLinkTimeout());
    }

    @Test
    public void testGetTaskCleanupTimeout() throws Exception {
        final Period timeout = Period.hours(1);
        Assert.assertEquals(timeout, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, timeout, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getTaskCleanupTimeout());
    }

    @Test
    public void testGetMaxRetriesBeforeBlacklist() throws Exception {
        final int maxRetriesBeforeBlacklist = 2;
        Assert.assertEquals(maxRetriesBeforeBlacklist, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, maxRetriesBeforeBlacklist, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getMaxRetriesBeforeBlacklist());
    }

    @Test
    public void testGetWorkerBlackListBackoffTime() throws Exception {
        final Period taskBlackListBackoffTime = new Period("PT1M");
        Assert.assertEquals(taskBlackListBackoffTime, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, taskBlackListBackoffTime, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).getWorkerBlackListBackoffTime());
    }

    @Test
    public void testGetTaskBlackListCleanupPeriod() throws Exception {
        final Period taskBlackListCleanupPeriod = Period.years(100);
        Assert.assertEquals(taskBlackListCleanupPeriod, reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, taskBlackListCleanupPeriod)).getWorkerBlackListCleanupPeriod());
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)), reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)));
        final Period timeout = Period.years(999);
        final String version = "someVersion";
        final long max = 20 * 1024;
        final int pendingTasksRunnerNumThreads = 20;
        final int maxRetriesBeforeBlacklist = 1;
        final Period taskBlackListBackoffTime = new Period("PT1M");
        final Period taskBlackListCleanupPeriod = Period.years(10);
        Assert.assertEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, taskBlackListBackoffTime, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, taskBlackListCleanupPeriod)));
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)));
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).hashCode(), reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).hashCode());
        final Period timeout = Period.years(999);
        final String version = "someVersion";
        final long max = 20 * 1024;
        final int pendingTasksRunnerNumThreads = 20;
        final int maxRetriesBeforeBlacklist = 80;
        final Period taskBlackListBackoffTime = new Period("PT1M");
        final Period taskBlackListCleanupPeriod = Period.years(10);
        Assert.assertEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, RemoteTaskRunnerConfigTest.DEFAULT_VERSION, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, RemoteTaskRunnerConfigTest.DEFAULT_MAX_ZNODE, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, RemoteTaskRunnerConfigTest.DEFAULT_TIMEOUT, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, RemoteTaskRunnerConfigTest.DEFAULT_PENDING_TASKS_RUNNER_NUM_THREADS, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, RemoteTaskRunnerConfigTest.DEFAULT_MAX_RETRIES_BEFORE_BLACKLIST, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, RemoteTaskRunnerConfigTest.DEFAULT_TASK_BACKOFF, taskBlackListCleanupPeriod)).hashCode());
        Assert.assertNotEquals(reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, taskBlackListCleanupPeriod)).hashCode(), reflect(generateRemoteTaskRunnerConfig(timeout, timeout, version, max, timeout, pendingTasksRunnerNumThreads, maxRetriesBeforeBlacklist, taskBlackListBackoffTime, RemoteTaskRunnerConfigTest.DEFAULT_BLACKLIST_CLEANUP_PERIOD)).hashCode());
    }
}

