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


import AtomicRateLimiter.AtomicRateLimiterMetrics;
import RateLimiter.Metrics;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.lang.Thread.State.TIMED_WAITING;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AtomicRateLimiter.class)
public class AtomicRateLimiterTest {
    private static final String LIMITER_NAME = "test";

    private static final long CYCLE_IN_NANOS = 500000000L;

    private static final long POLL_INTERVAL_IN_NANOS = 2000000L;

    private static final int PERMISSIONS_RER_CYCLE = 1;

    private RateLimiterConfig rateLimiterConfig;

    private AtomicRateLimiter rateLimiter;

    private AtomicRateLimiterMetrics metrics;

    @Test
    public void notSpyRawTest() {
        AtomicRateLimiter rawLimiter = new AtomicRateLimiter("rawLimiter", rateLimiterConfig);
        AtomicRateLimiter.AtomicRateLimiterMetrics rawDetailedMetrics = rawLimiter.getDetailedMetrics();
        long firstCycle = rawDetailedMetrics.getCycle();
        while (firstCycle == (rawDetailedMetrics.getCycle())) {
            System.out.print('.');// wait for current cycle to pass

        } 
        boolean firstPermission = rawLimiter.getPermission(Duration.ZERO);
        long nanosToWait = rawDetailedMetrics.getNanosToWait();
        long startTime = System.nanoTime();
        while (((System.nanoTime()) - startTime) < nanosToWait) {
            System.out.print('*');// wait for permission renewal

        } 
        boolean secondPermission = rawLimiter.getPermission(Duration.ZERO);
        boolean firstNoPermission = rawLimiter.getPermission(Duration.ZERO);
        long secondCycle = rawDetailedMetrics.getCycle();
        rawLimiter.changeLimitForPeriod(((AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE) * 2));
        nanosToWait = rawDetailedMetrics.getNanosToWait();
        startTime = System.nanoTime();
        while (((System.nanoTime()) - startTime) < nanosToWait) {
            System.out.print('^');// wait for permission renewal

        } 
        boolean thirdPermission = rawLimiter.getPermission(Duration.ZERO);
        boolean fourthPermission = rawLimiter.getPermission(Duration.ZERO);
        boolean secondNoPermission = rawLimiter.getPermission(Duration.ZERO);
        long thirdCycle = rawDetailedMetrics.getCycle();
        then((secondCycle - firstCycle)).isEqualTo(2);
        then((thirdCycle - secondCycle)).isEqualTo(1);
        then(firstPermission).isTrue();
        then(secondPermission).isTrue();
        then(thirdPermission).isTrue();
        then(fourthPermission).isTrue();
        then(firstNoPermission).isFalse();
        then(secondNoPermission).isFalse();
    }

    @Test
    public void notSpyRawNonBlockingTest() {
        AtomicRateLimiter rawLimiter = new AtomicRateLimiter("rawLimiter", rateLimiterConfig);
        AtomicRateLimiter.AtomicRateLimiterMetrics rawDetailedMetrics = rawLimiter.getDetailedMetrics();
        long firstCycle = rawDetailedMetrics.getCycle();
        while (firstCycle == (rawDetailedMetrics.getCycle())) {
            System.out.print('.');// wait for current cycle to pass

        } 
        long firstPermission = rawLimiter.reservePermission(Duration.ZERO);
        long nanosToWait = rawDetailedMetrics.getNanosToWait();
        long startTime = System.nanoTime();
        while (((System.nanoTime()) - startTime) < nanosToWait) {
            System.out.print('*');// wait for permission renewal

        } 
        long secondPermission = rawLimiter.reservePermission(Duration.ZERO);
        long firstNoPermission = rawLimiter.reservePermission(Duration.ZERO);
        long secondCycle = rawDetailedMetrics.getCycle();
        rawLimiter.changeLimitForPeriod(((AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE) * 2));
        nanosToWait = rawDetailedMetrics.getNanosToWait();
        startTime = System.nanoTime();
        while (((System.nanoTime()) - startTime) < nanosToWait) {
            System.out.print('^');// wait for permission renewal

        } 
        long thirdPermission = rawLimiter.reservePermission(Duration.ZERO);
        long fourthPermission = rawLimiter.reservePermission(Duration.ZERO);
        long secondNoPermission = rawLimiter.reservePermission(Duration.ZERO);
        long thirdCycle = rawDetailedMetrics.getCycle();
        then((secondCycle - firstCycle)).isEqualTo(2);
        then((thirdCycle - secondCycle)).isEqualTo(1);
        then(firstPermission).isZero();
        then(secondPermission).isZero();
        then(thirdPermission).isZero();
        then(fourthPermission).isZero();
        then(firstNoPermission).isNegative();
        then(secondNoPermission).isNegative();
    }

    @Test
    public void permissionsInFirstCycle() throws Exception {
        setTimeOnNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) - 10));
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        int availablePermissions = metrics.getAvailablePermissions();
        then(availablePermissions).isEqualTo(AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE);
    }

    @Test
    public void acquireAndRefreshWithEventPublishing() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean permission = rateLimiter.getPermission(Duration.ZERO);
        then(permission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean secondPermission = rateLimiter.getPermission(Duration.ZERO);
        then(secondPermission).isFalse();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        setTimeOnNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2));
        boolean thirdPermission = rateLimiter.getPermission(Duration.ZERO);
        then(thirdPermission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean fourthPermission = rateLimiter.getPermission(Duration.ZERO);
        then(fourthPermission).isFalse();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
    }

    @Test
    public void reserveAndRefresh() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean permission = rateLimiter.getPermission(Duration.ZERO);
        then(permission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        AtomicReference<Boolean> reservedPermission = new AtomicReference<>(null);
        Thread caller = new Thread(() -> reservedPermission.set(rateLimiter.getPermission(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS))));
        caller.setDaemon(true);
        caller.start();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(caller::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(metrics.getAvailablePermissions()).isEqualTo((-1));
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) + (AtomicRateLimiterTest.CYCLE_IN_NANOS)));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(1);
        setTimeOnNanos((((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2) + 10));
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(reservedPermission::get, CoreMatchers.equalTo(true));
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) - 10));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void reserveFewThenSkipCyclesBeforeRefreshNonBlocking() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        long permission = rateLimiter.reservePermission(Duration.ZERO);
        then(permission).isZero();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        long reservation = rateLimiter.reservePermission(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS));
        then(reservation).isPositive();
        then(reservation).isLessThanOrEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getAvailablePermissions()).isEqualTo((-1));
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        long additionalReservation = rateLimiter.reservePermission(Duration.ofNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2)));
        then(additionalReservation).isPositive();
        then(additionalReservation).isGreaterThan(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(additionalReservation).isLessThanOrEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2));
        then(metrics.getAvailablePermissions()).isEqualTo((-2));
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 3));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        setTimeOnNanos((((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 6) + 10));
        then(metrics.getAvailablePermissions()).isEqualTo(1);
        then(metrics.getNanosToWait()).isEqualTo(0L);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void reserveFewThenSkipCyclesBeforeRefresh() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean permission = rateLimiter.getPermission(Duration.ZERO);
        then(permission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        AtomicReference<Boolean> firstReservedPermission = new AtomicReference<>(null);
        Thread firstCaller = new Thread(() -> firstReservedPermission.set(rateLimiter.getPermission(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS))));
        firstCaller.setDaemon(true);
        firstCaller.start();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(firstCaller::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(metrics.getAvailablePermissions()).isEqualTo((-1));
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(1);
        AtomicReference<Boolean> secondReservedPermission = new AtomicReference<>(null);
        Thread secondCaller = new Thread(() -> secondReservedPermission.set(rateLimiter.getPermission(Duration.ofNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2)))));
        secondCaller.setDaemon(true);
        secondCaller.start();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(secondCaller::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(metrics.getAvailablePermissions()).isEqualTo((-2));
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 3));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(2);
        setTimeOnNanos((((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 6) + 10));
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(firstReservedPermission::get, CoreMatchers.equalTo(true));
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(secondReservedPermission::get, CoreMatchers.equalTo(true));
        then(metrics.getAvailablePermissions()).isEqualTo(1);
        then(metrics.getNanosToWait()).isEqualTo(0L);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void rejectedByTimeout() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean permission = rateLimiter.getPermission(Duration.ZERO);
        then(permission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        AtomicReference<Boolean> declinedPermission = new AtomicReference<>(null);
        Thread caller = new Thread(() -> declinedPermission.set(rateLimiter.getPermission(Duration.ofNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) - 1)))));
        caller.setDaemon(true);
        caller.start();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(caller::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(1);
        setTimeOnNanos((((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2) - 1));
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(declinedPermission::get, CoreMatchers.equalTo(false));
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(1L);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void rejectedByTimeoutNonBlocking() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        long permission = rateLimiter.reservePermission(Duration.ZERO);
        then(permission).isZero();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        long failedPermission = rateLimiter.reservePermission(Duration.ofNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) - 1)));
        then(failedPermission).isNegative();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        setTimeOnNanos((((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2) - 1));
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(1L);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void waitingThreadIsInterrupted() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean permission = rateLimiter.getPermission(Duration.ZERO);
        then(permission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        AtomicReference<Boolean> declinedPermission = new AtomicReference<>(null);
        AtomicBoolean wasInterrupted = new AtomicBoolean(false);
        Thread caller = new Thread(() -> {
            declinedPermission.set(rateLimiter.getPermission(Duration.ofNanos(((AtomicRateLimiterTest.CYCLE_IN_NANOS) - 1))));
            wasInterrupted.set(Thread.currentThread().isInterrupted());
        });
        caller.setDaemon(true);
        caller.start();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(caller::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(1);
        caller.interrupt();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(declinedPermission::get, CoreMatchers.equalTo(false));
        then(wasInterrupted.get()).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void changePermissionsLimitBetweenCycles() throws Exception {
        setTimeOnNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        boolean permission = rateLimiter.getPermission(Duration.ZERO);
        then(permission).isTrue();
        then(metrics.getAvailablePermissions()).isEqualTo(0);
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        AtomicReference<Boolean> reservedPermission = new AtomicReference<>(null);
        Thread caller = new Thread(() -> reservedPermission.set(rateLimiter.getPermission(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS))));
        caller.setDaemon(true);
        caller.start();
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(caller::getState, CoreMatchers.equalTo(TIMED_WAITING));
        then(metrics.getAvailablePermissions()).isEqualTo((-1));
        then(metrics.getNanosToWait()).isEqualTo(((AtomicRateLimiterTest.CYCLE_IN_NANOS) + (AtomicRateLimiterTest.CYCLE_IN_NANOS)));
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(1);
        rateLimiter.changeLimitForPeriod(((AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE) * 2));
        then(rateLimiter.getRateLimiterConfig().getLimitForPeriod()).isEqualTo(((AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE) * 2));
        then(metrics.getAvailablePermissions()).isEqualTo((-1));
        then(metrics.getNanosToWait()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(1);
        setTimeOnNanos((((AtomicRateLimiterTest.CYCLE_IN_NANOS) * 2) + 10));
        AtomicRateLimiterTest.awaitImpatiently().atMost(5, TimeUnit.SECONDS).until(reservedPermission::get, CoreMatchers.equalTo(true));
        then(metrics.getAvailablePermissions()).isEqualTo(1);
        then(metrics.getNanosToWait()).isEqualTo(0);
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
    }

    @Test
    public void changeDefaultTimeoutDuration() throws Exception {
        RateLimiterConfig rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(Duration.ZERO);
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(0L);
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS));
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        rateLimiter.changeTimeoutDuration(Duration.ofSeconds(1));
        then((rateLimiterConfig != (rateLimiter.getRateLimiterConfig()))).isTrue();
        rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(Duration.ofSeconds(1));
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(Duration.ofSeconds(1).toNanos());
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS));
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
    }

    @Test
    public void changeLimitForPeriod() throws Exception {
        RateLimiterConfig rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(Duration.ZERO);
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(0L);
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(AtomicRateLimiterTest.PERMISSIONS_RER_CYCLE);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS));
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
        rateLimiter.changeLimitForPeriod(35);
        then((rateLimiterConfig != (rateLimiter.getRateLimiterConfig()))).isTrue();
        rateLimiterConfig = rateLimiter.getRateLimiterConfig();
        then(rateLimiterConfig.getTimeoutDuration()).isEqualTo(Duration.ZERO);
        then(rateLimiterConfig.getTimeoutDurationInNanos()).isEqualTo(0L);
        then(rateLimiterConfig.getLimitForPeriod()).isEqualTo(35);
        then(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(Duration.ofNanos(AtomicRateLimiterTest.CYCLE_IN_NANOS));
        then(rateLimiterConfig.getLimitRefreshPeriodInNanos()).isEqualTo(AtomicRateLimiterTest.CYCLE_IN_NANOS);
    }

    @Test
    public void metricsTest() {
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        then(metrics.getNumberOfWaitingThreads()).isEqualTo(0);
        then(metrics.getAvailablePermissions()).isEqualTo(1);
        AtomicRateLimiter.AtomicRateLimiterMetrics detailedMetrics = rateLimiter.getDetailedMetrics();
        then(detailedMetrics.getNumberOfWaitingThreads()).isEqualTo(0);
        then(detailedMetrics.getAvailablePermissions()).isEqualTo(1);
        then(detailedMetrics.getNanosToWait()).isEqualTo(0);
        then(detailedMetrics.getCycle()).isGreaterThan(0);
    }

    @Test
    public void namePropagation() {
        then(rateLimiter.getName()).isEqualTo(AtomicRateLimiterTest.LIMITER_NAME);
    }

    @Test
    public void configPropagation() {
        then(rateLimiter.getRateLimiterConfig()).isEqualTo(rateLimiterConfig);
    }

    @Test
    public void metrics() {
        then(rateLimiter.getMetrics().getNumberOfWaitingThreads()).isEqualTo(0);
    }
}

