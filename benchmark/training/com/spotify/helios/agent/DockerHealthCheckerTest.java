/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.agent;


import com.spotify.helios.servicescommon.statistics.MeterRates;
import com.spotify.helios.servicescommon.statistics.SupervisorMetrics;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DockerHealthCheckerTest {
    private static final MeterRates OK_RATE = DockerHealthCheckerTest.makeMeter(((DockerHealthChecker.FAILURE_LOW_WATERMARK) - 0.01));

    private static final MeterRates BETWEEN_RATE = DockerHealthCheckerTest.makeMeter(((DockerHealthChecker.FAILURE_HIGH_WATERMARK) - 0.01));

    private static final MeterRates RUN_RATE = DockerHealthCheckerTest.makeMeter(1);

    private static final MeterRates ZERO_RATE = DockerHealthCheckerTest.makeMeter(0);

    private static final MeterRates BAD_RATE = DockerHealthCheckerTest.makeMeter(((DockerHealthChecker.FAILURE_HIGH_WATERMARK) + 0.01));

    private SupervisorMetrics metrics;

    private DockerHealthChecker checker;

    @Test
    public void testTimeouts() throws Exception {
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.BAD_RATE);// start out as many timeouts

        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.ZERO_RATE);
        Mockito.when(metrics.getSupervisorRunRates()).thenReturn(DockerHealthCheckerTest.RUN_RATE);
        checker.start();
        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.BETWEEN_RATE);
        Thread.sleep(2);
        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.OK_RATE);
        Assert.assertTrue(checker.check().isHealthy());
    }

    @Test
    public void testExceptions() throws Exception {
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.ZERO_RATE);
        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.BAD_RATE);
        Mockito.when(metrics.getSupervisorRunRates()).thenReturn(DockerHealthCheckerTest.RUN_RATE);
        checker.start();
        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.BETWEEN_RATE);
        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.OK_RATE);
        Assert.assertTrue(checker.check().isHealthy());
    }

    @Test
    public void testBoth() throws Exception {
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.ZERO_RATE);
        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.BAD_RATE);
        Mockito.when(metrics.getSupervisorRunRates()).thenReturn(DockerHealthCheckerTest.RUN_RATE);
        checker.start();
        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.BETWEEN_RATE);
        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getContainersThrewExceptionRates()).thenReturn(DockerHealthCheckerTest.OK_RATE);
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.BETWEEN_RATE);// between maintains unhealthy

        Assert.assertFalse(checker.check().isHealthy());
        Mockito.when(metrics.getDockerTimeoutRates()).thenReturn(DockerHealthCheckerTest.OK_RATE);
        Assert.assertTrue(checker.check().isHealthy());
    }
}

