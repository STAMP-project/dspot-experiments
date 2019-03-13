/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.javanica.test.common.configuration.command;


import HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;
import HystrixEventType.SUCCESS;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.domain.User;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by dmgcodevil
 */
public abstract class BasicCommandPropertiesTest extends BasicHystrixTest {
    private BasicCommandPropertiesTest.UserService userService;

    @Test
    public void testGetUser() throws IllegalAccessException, NoSuchFieldException {
        User u1 = userService.getUser("1", "name: ");
        Assert.assertEquals("name: 1", u1.getName());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("GetUserCommand", command.getCommandKey().name());
        Assert.assertEquals("UserGroupKey", command.getCommandGroup().name());
        Assert.assertEquals("Test", command.getThreadPoolKey().name());
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
        // assert properties
        Assert.assertEquals(110, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
        Assert.assertEquals(false, command.getProperties().executionIsolationThreadInterruptOnTimeout().get());
        HystrixThreadPoolProperties properties = getThreadPoolProperties(command);
        Assert.assertEquals(30, ((int) (properties.coreSize().get())));
        Assert.assertEquals(35, ((int) (properties.maximumSize().get())));
        Assert.assertEquals(true, properties.getAllowMaximumSizeToDivergeFromCoreSize().get());
        Assert.assertEquals(101, ((int) (properties.maxQueueSize().get())));
        Assert.assertEquals(2, ((int) (properties.keepAliveTimeMinutes().get())));
        Assert.assertEquals(15, ((int) (properties.queueSizeRejectionThreshold().get())));
        Assert.assertEquals(1440, ((int) (properties.metricsRollingStatisticalWindowInMilliseconds().get())));
        Assert.assertEquals(12, ((int) (properties.metricsRollingStatisticalWindowBuckets().get())));
    }

    @Test
    public void testGetUserDefaultPropertiesValues() {
        User u1 = userService.getUserDefProperties("1", "name: ");
        Assert.assertEquals("name: 1", u1.getName());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("getUserDefProperties", command.getCommandKey().name());
        Assert.assertEquals("UserService", command.getCommandGroup().name());
        Assert.assertEquals("UserService", command.getThreadPoolKey().name());
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
    }

    @Test
    public void testGetUserDefGroupKeyWithSpecificThreadPoolKey() {
        User u1 = userService.getUserDefGroupKeyWithSpecificThreadPoolKey("1", "name: ");
        Assert.assertEquals("name: 1", u1.getName());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("getUserDefGroupKeyWithSpecificThreadPoolKey", command.getCommandKey().name());
        Assert.assertEquals("UserService", command.getCommandGroup().name());
        Assert.assertEquals("CustomThreadPool", command.getThreadPoolKey().name());
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
    }

    @Test
    public void testHystrixCommandProperties() {
        User u1 = userService.getUsingAllCommandProperties("1", "name: ");
        Assert.assertEquals("name: 1", u1.getName());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
        // assert properties
        Assert.assertEquals(SEMAPHORE, command.getProperties().executionIsolationStrategy().get());
        Assert.assertEquals(500, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
        Assert.assertEquals(true, command.getProperties().executionTimeoutEnabled().get().booleanValue());
        Assert.assertEquals(false, command.getProperties().executionIsolationThreadInterruptOnTimeout().get().booleanValue());
        Assert.assertEquals(10, command.getProperties().executionIsolationSemaphoreMaxConcurrentRequests().get().intValue());
        Assert.assertEquals(15, command.getProperties().fallbackIsolationSemaphoreMaxConcurrentRequests().get().intValue());
        Assert.assertEquals(false, command.getProperties().fallbackEnabled().get().booleanValue());
        Assert.assertEquals(false, command.getProperties().circuitBreakerEnabled().get().booleanValue());
        Assert.assertEquals(30, command.getProperties().circuitBreakerRequestVolumeThreshold().get().intValue());
        Assert.assertEquals(250, command.getProperties().circuitBreakerSleepWindowInMilliseconds().get().intValue());
        Assert.assertEquals(60, command.getProperties().circuitBreakerErrorThresholdPercentage().get().intValue());
        Assert.assertEquals(false, command.getProperties().circuitBreakerForceOpen().get().booleanValue());
        Assert.assertEquals(true, command.getProperties().circuitBreakerForceClosed().get().booleanValue());
        Assert.assertEquals(false, command.getProperties().metricsRollingPercentileEnabled().get().booleanValue());
        Assert.assertEquals(400, command.getProperties().metricsRollingPercentileWindowInMilliseconds().get().intValue());
        Assert.assertEquals(5, command.getProperties().metricsRollingPercentileWindowBuckets().get().intValue());
        Assert.assertEquals(6, command.getProperties().metricsRollingPercentileBucketSize().get().intValue());
        Assert.assertEquals(10, command.getProperties().metricsRollingStatisticalWindowBuckets().get().intValue());
        Assert.assertEquals(500, command.getProperties().metricsRollingStatisticalWindowInMilliseconds().get().intValue());
        Assert.assertEquals(312, command.getProperties().metricsHealthSnapshotIntervalInMilliseconds().get().intValue());
        Assert.assertEquals(false, command.getProperties().requestCacheEnabled().get().booleanValue());
        Assert.assertEquals(true, command.getProperties().requestLogEnabled().get().booleanValue());
    }

    public static class UserService {
        @HystrixCommand(commandKey = "GetUserCommand", groupKey = "UserGroupKey", threadPoolKey = "Test", commandProperties = { @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "110"), @HystrixProperty(name = "execution.isolation.thread.interruptOnTimeout", value = "false") }, threadPoolProperties = { @HystrixProperty(name = "coreSize", value = "30"), @HystrixProperty(name = "maximumSize", value = "35"), @HystrixProperty(name = "allowMaximumSizeToDivergeFromCoreSize", value = "true"), @HystrixProperty(name = "maxQueueSize", value = "101"), @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"), @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"), @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"), @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440") })
        public User getUser(String id, String name) {
            return new User(id, (name + id));// it should be network call

        }

        @HystrixCommand
        public User getUserDefProperties(String id, String name) {
            return new User(id, (name + id));// it should be network call

        }

        @HystrixCommand(threadPoolKey = "CustomThreadPool")
        public User getUserDefGroupKeyWithSpecificThreadPoolKey(String id, String name) {
            return new User(id, (name + id));// it should be network call

        }

        @HystrixCommand(commandProperties = { @HystrixProperty(name = EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"), @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "500"), @HystrixProperty(name = EXECUTION_TIMEOUT_ENABLED, value = "true"), @HystrixProperty(name = EXECUTION_ISOLATION_THREAD_INTERRUPT_ON_TIMEOUT, value = "false"), @HystrixProperty(name = EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "10"), @HystrixProperty(name = FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "15"), @HystrixProperty(name = FALLBACK_ENABLED, value = "false"), @HystrixProperty(name = CIRCUIT_BREAKER_ENABLED, value = "false"), @HystrixProperty(name = CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "30"), @HystrixProperty(name = CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "250"), @HystrixProperty(name = CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "60"), @HystrixProperty(name = CIRCUIT_BREAKER_FORCE_OPEN, value = "false"), @HystrixProperty(name = CIRCUIT_BREAKER_FORCE_CLOSED, value = "true"), @HystrixProperty(name = METRICS_ROLLING_PERCENTILE_ENABLED, value = "false"), @HystrixProperty(name = METRICS_ROLLING_PERCENTILE_TIME_IN_MILLISECONDS, value = "400"), @HystrixProperty(name = METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "500"), @HystrixProperty(name = METRICS_ROLLING_STATS_NUM_BUCKETS, value = "10"), @HystrixProperty(name = METRICS_ROLLING_PERCENTILE_NUM_BUCKETS, value = "5"), @HystrixProperty(name = METRICS_ROLLING_PERCENTILE_BUCKET_SIZE, value = "6"), @HystrixProperty(name = METRICS_HEALTH_SNAPSHOT_INTERVAL_IN_MILLISECONDS, value = "312"), @HystrixProperty(name = REQUEST_CACHE_ENABLED, value = "false"), @HystrixProperty(name = REQUEST_LOG_ENABLED, value = "true") })
        public User getUsingAllCommandProperties(String id, String name) {
            return new User(id, (name + id));// it should be network call

        }
    }
}

