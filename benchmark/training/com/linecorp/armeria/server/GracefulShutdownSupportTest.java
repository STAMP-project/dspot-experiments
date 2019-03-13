/**
 * Copyright 2015 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import com.google.common.base.Ticker;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class GracefulShutdownSupportTest {
    private static final long QUIET_PERIOD_NANOS = 10000;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private Ticker ticker;

    private GracefulShutdownSupport support;

    private ThreadPoolExecutor executor;

    @Test
    public void testDisabled() {
        final GracefulShutdownSupport support = GracefulShutdownSupport.createDisabled();
        assertThat(support.isShuttingDown()).isFalse();
        assertThat(support.completedQuietPeriod()).isTrue();
        assertThat(support.isShuttingDown()).isTrue();
        support.inc();
        assertThat(support.completedQuietPeriod()).isTrue();
    }

    @Test
    public void testIsShutdown() {
        // completedQuietPeriod() must make isShuttingDown() start to return true.
        assertThat(support.isShuttingDown()).isFalse();
        support.completedQuietPeriod();
        assertThat(support.isShuttingDown()).isTrue();
    }

    @Test
    public void noRequestsNotPassedQuietPeriod() {
        assertWithoutPendingTasks(false, 0, 1, ((GracefulShutdownSupportTest.QUIET_PERIOD_NANOS) - 1));
    }

    @Test
    public void noRequestsPassedQuietPeriod() {
        assertWithoutPendingTasks(true, 0, ((-(GracefulShutdownSupportTest.QUIET_PERIOD_NANOS)) * 2), GracefulShutdownSupportTest.QUIET_PERIOD_NANOS);
    }

    @Test
    public void activeRequestsNotPassedQuietPeriod() throws Exception {
        support.inc();
        support.inc();
        Mockito.verify(ticker, Mockito.never()).read();
        support.dec();
        Mockito.verify(ticker, Mockito.times(1)).read();
        assertWithPendingTasks(1, ((-(GracefulShutdownSupportTest.QUIET_PERIOD_NANOS)) * 2));
    }

    @Test
    public void activeRequestsPassedQuietPeriod() throws Exception {
        support.inc();
        support.inc();
        Mockito.verify(ticker, Mockito.never()).read();
        support.dec();
        Mockito.verify(ticker, Mockito.times(1)).read();
        assertWithPendingTasks(1, 1);
    }

    @Test
    public void noActiveRequestsNotPassedQuietPeriod() throws Exception {
        support.inc();
        support.inc();
        Mockito.verify(ticker, Mockito.never()).read();
        support.dec();
        Mockito.verify(ticker, Mockito.times(1)).read();
        support.dec();
        Mockito.verify(ticker, Mockito.times(2)).read();
        assertWithoutPendingTasks(false, 2, 1, ((GracefulShutdownSupportTest.QUIET_PERIOD_NANOS) - 1));
    }

    @Test
    public void noActiveRequestsPassedQuietPeriod() throws Exception {
        support.inc();
        support.inc();
        Mockito.verify(ticker, Mockito.never()).read();
        support.dec();
        Mockito.verify(ticker, Mockito.times(1)).read();
        support.dec();
        Mockito.verify(ticker, Mockito.times(2)).read();
        assertWithoutPendingTasks(true, 2, GracefulShutdownSupportTest.QUIET_PERIOD_NANOS, GracefulShutdownSupportTest.QUIET_PERIOD_NANOS);
    }

    @Test
    public void activeBlockingTaskNotPassedQuietPeriod() throws Exception {
        submitLongTask();
        assertWithPendingTasks(0, (-42));
    }

    @Test
    public void activeBlockingTaskPassedQuietPeriod() throws Exception {
        submitLongTask();
        assertWithPendingTasks(0, 42);
    }

    @Test
    public void testQuietPeriodExtensionOnRequest() throws Exception {
        final long deltaNanos = (GracefulShutdownSupportTest.QUIET_PERIOD_NANOS) / 2;
        long timeNanos = 0;
        Mockito.when(ticker.read()).thenReturn(timeNanos);
        Assert.assertFalse(support.completedQuietPeriod());
        Mockito.verify(ticker, Mockito.times(2)).read();
        // Handle a request during the quiet period.
        support.inc();
        Mockito.verify(ticker, Mockito.times(2)).read();
        // Even during the quiet period, pending request will fail the check.
        Assert.assertFalse(support.completedQuietPeriod());
        Mockito.verify(ticker, Mockito.times(2)).read();
        // Handle a response so that there are no pending requests left.
        timeNanos += deltaNanos;
        Mockito.when(ticker.read()).thenReturn(timeNanos);
        support.dec();
        Mockito.verify(ticker, Mockito.times(3)).read();
        // The quiet period should be extended by 'deltaNanos' due to the response handled above.
        timeNanos += deltaNanos;
        Mockito.when(ticker.read()).thenReturn(timeNanos);
        Assert.assertFalse(support.completedQuietPeriod());
        Mockito.verify(ticker, Mockito.times(4)).read();
        // The quiet period should end after 'QUIET_PERIOD_NANOS + deltaNanos'.
        timeNanos += deltaNanos;
        Mockito.when(ticker.read()).thenReturn(timeNanos);
        Assert.assertTrue(support.completedQuietPeriod());
        Mockito.verify(ticker, Mockito.times(5)).read();
    }
}

