/**
 * Copyright 2016 Robert Winkler and Bohdan Storozhuk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.ratelimiter.internal;


import RateLimiter.Metrics;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.TERMINATED;
import static java.lang.Thread.State.TIMED_WAITING;


public class SemaphoreBasedRateLimiterImplTest {
    private static final int LIMIT = 2;

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static final Duration REFRESH_PERIOD = Duration.ofMillis(100);

    private static final String CONFIG_MUST_NOT_BE_NULL = "RateLimiterConfig must not be null";

    private static final String NAME_MUST_NOT_BE_NULL = "Name must not be null";

    private static final Object O = new Object();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RateLimiterConfig config;

    @Test
    public void rateLimiterCreationWithProvidedScheduler() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        RateLimiterConfig configSpy = Mockito.spy(config);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", configSpy, scheduledExecutorService);
        ArgumentCaptor<Runnable> refreshLimitRunnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduledExecutorService).scheduleAtFixedRate(refreshLimitRunnableCaptor.capture(), ArgumentMatchers.eq(config.getLimitRefreshPeriod().toNanos()), ArgumentMatchers.eq(config.getLimitRefreshPeriod().toNanos()), ArgumentMatchers.eq(TimeUnit.NANOSECONDS));
        Runnable refreshLimitRunnable = refreshLimitRunnableCaptor.getValue();
        then(limit.getPermission(Duration.ZERO)).isTrue();
        then(limit.reservePermission(Duration.ZERO)).isNegative();
        then(limit.reservePermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT)).isNegative();
        then(limit.getPermission(Duration.ZERO)).isTrue();
        then(limit.reservePermission(Duration.ZERO)).isNegative();
        then(limit.reservePermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT)).isNegative();
        then(limit.getPermission(Duration.ZERO)).isFalse();
        then(limit.reservePermission(Duration.ZERO)).isNegative();
        then(limit.reservePermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT)).isNegative();
        Thread.sleep(((SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD.toMillis()) * 2));
        Mockito.verify(configSpy, Mockito.times(1)).getLimitForPeriod();
        refreshLimitRunnable.run();
        Mockito.verify(configSpy, Mockito.times(2)).getLimitForPeriod();
        then(limit.getPermission(Duration.ZERO)).isTrue();
        then(limit.getPermission(Duration.ZERO)).isTrue();
        then(limit.getPermission(Duration.ZERO)).isFalse();
    }

    @Test
    public void rateLimiterCreationWithDefaultScheduler() throws Exception {
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", config);
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(FIVE_HUNDRED_MILLISECONDS).until(() -> limit.getPermission(Duration.ZERO), CoreMatchers.equalTo(false));
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(110, TimeUnit.MILLISECONDS).until(() -> limit.getPermission(Duration.ZERO), CoreMatchers.equalTo(true));
    }

    @Test
    public void getPermissionAndMetrics() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        RateLimiterConfig configSpy = Mockito.spy(config);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", configSpy, scheduledExecutorService);
        RateLimiter.Metrics detailedMetrics = limit.getMetrics();
        SynchronousQueue<Object> synchronousQueue = new SynchronousQueue<>();
        Thread thread = new Thread(() -> {
            run(() -> {
                for (int i = 0; i < (LIMIT); i++) {
                    synchronousQueue.put(O);
                    limit.getPermission(TIMEOUT);
                }
                limit.getPermission(TIMEOUT);
            });
        });
        thread.setDaemon(true);
        thread.start();
        for (int i = 0; i < (SemaphoreBasedRateLimiterImplTest.LIMIT); i++) {
            synchronousQueue.take();
        }
        then(limit.reservePermission(Duration.ZERO)).isNegative();
        then(limit.reservePermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT)).isNegative();
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(100, TimeUnit.MILLISECONDS).until(detailedMetrics::getAvailablePermissions, CoreMatchers.equalTo(0));
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(2, TimeUnit.SECONDS).until(thread::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(detailedMetrics.getAvailablePermissions()).isEqualTo(0);
        then(limit.reservePermission(Duration.ZERO)).isNegative();
        then(limit.reservePermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT)).isNegative();
        limit.refreshLimit();
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(100, TimeUnit.MILLISECONDS).until(detailedMetrics::getAvailablePermissions, CoreMatchers.equalTo(1));
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(2, TimeUnit.SECONDS).until(thread::getState, CoreMatchers.equalTo(TERMINATED));
        then(detailedMetrics.getAvailablePermissions()).isEqualTo(1);
        limit.changeLimitForPeriod(3);
        limit.refreshLimit();
        then(detailedMetrics.getAvailablePermissions()).isEqualTo(3);
        then(limit.reservePermission(Duration.ZERO)).isNegative();
        then(limit.reservePermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT)).isNegative();
    }

    @Test
    public void changeDefaultTimeoutDuration() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        RateLimiter rateLimiter = new SemaphoreBasedRateLimiter("some", config, scheduledExecutorService);
        RateLimiterConfig rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(SemaphoreBasedRateLimiterImplTest.TIMEOUT);
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.TIMEOUT.toNanos());
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.LIMIT);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD);
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD.toNanos());
        rateLimiter.changeTimeoutDuration(Duration.ofSeconds(1));
        then((rateLimiterConfig != (rateLimiter.getRateLimiterConfig()))).isTrue();
        rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(Duration.ofSeconds(1));
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(Duration.ofSeconds(1).toNanos());
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.LIMIT);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD);
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD.toNanos());
    }

    @Test
    public void changeLimitForPeriod() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        RateLimiter rateLimiter = new SemaphoreBasedRateLimiter("some", config, scheduledExecutorService);
        RateLimiterConfig rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(SemaphoreBasedRateLimiterImplTest.TIMEOUT);
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.TIMEOUT.toNanos());
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.LIMIT);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD);
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD.toNanos());
        rateLimiter.changeLimitForPeriod(((SemaphoreBasedRateLimiterImplTest.LIMIT) * 2));
        then((rateLimiterConfig != (rateLimiter.getRateLimiterConfig()))).isTrue();
        rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(SemaphoreBasedRateLimiterImplTest.TIMEOUT);
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.TIMEOUT.toNanos());
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(((SemaphoreBasedRateLimiterImplTest.LIMIT) * 2));
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD);
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(SemaphoreBasedRateLimiterImplTest.REFRESH_PERIOD.toNanos());
    }

    @Test
    public void getPermissionInterruption() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        RateLimiterConfig configSpy = Mockito.spy(config);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", configSpy, scheduledExecutorService);
        limit.getPermission(Duration.ZERO);
        limit.getPermission(Duration.ZERO);
        Thread thread = new Thread(() -> {
            limit.getPermission(SemaphoreBasedRateLimiterImplTest.TIMEOUT);
            while (true) {
                Function.identity().apply(1);
            } 
        });
        thread.setDaemon(true);
        thread.start();
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(2, TimeUnit.SECONDS).until(thread::getState, CoreMatchers.equalTo(TIMED_WAITING));
        thread.interrupt();
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(2, TimeUnit.SECONDS).until(thread::getState, CoreMatchers.equalTo(RUNNABLE));
        SemaphoreBasedRateLimiterImplTest.awaitImpatiently().atMost(100, TimeUnit.MILLISECONDS).until(thread::isInterrupted);
    }

    @Test
    public void getName() throws Exception {
        ScheduledExecutorService scheduler = Mockito.mock(ScheduledExecutorService.class);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", config, scheduler);
        then(limit.getName()).isEqualTo("test");
    }

    @Test
    public void getMetrics() throws Exception {
        ScheduledExecutorService scheduler = Mockito.mock(ScheduledExecutorService.class);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", config, scheduler);
        RateLimiter.Metrics metrics = limit.getMetrics();
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void getRateLimiterConfig() throws Exception {
        ScheduledExecutorService scheduler = Mockito.mock(ScheduledExecutorService.class);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", config, scheduler);
        then(limit.getRateLimiterConfig()).isEqualTo(config);
    }

    @Test
    public void isUpperLimitedForPermissions() throws Exception {
        ScheduledExecutorService scheduler = Mockito.mock(ScheduledExecutorService.class);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", config, scheduler);
        RateLimiter.Metrics metrics = limit.getMetrics();
        then(metrics.getAvailablePermissions()).isEqualTo(2);
        limit.refreshLimit();
        then(metrics.getAvailablePermissions()).isEqualTo(2);
    }

    @Test
    public void getDetailedMetrics() throws Exception {
        ScheduledExecutorService scheduler = Mockito.mock(ScheduledExecutorService.class);
        SemaphoreBasedRateLimiter limit = new SemaphoreBasedRateLimiter("test", config, scheduler);
        RateLimiter.Metrics metrics = limit.getMetrics();
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        then(metrics.getAvailablePermissions()).isEqualTo(2);
    }

    @Test
    public void constructionWithNullName() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage(SemaphoreBasedRateLimiterImplTest.NAME_MUST_NOT_BE_NULL);
        new SemaphoreBasedRateLimiter(null, config, null);
    }

    @Test
    public void constructionWithNullConfig() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage(SemaphoreBasedRateLimiterImplTest.CONFIG_MUST_NOT_BE_NULL);
        new SemaphoreBasedRateLimiter("test", null, null);
    }
}

