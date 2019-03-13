/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.periodical;


import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.graylog2.plugin.periodical.Periodical;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;


public class PeriodicalsTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ScheduledExecutorService scheduler;

    @Mock
    private ScheduledExecutorService daemonScheduler;

    @Mock
    private Periodical periodical;

    private ScheduledFuture<Object> future;

    private Periodicals periodicals;

    @Test
    public void testRegisterAndStartWithRunsForeverPeriodical() throws Exception {
        // Starts the periodical in a separate thread.
        Mockito.when(periodical.runsForever()).thenReturn(true);
        periodicals.registerAndStart(periodical);
        Mockito.verify(daemonScheduler, Mockito.never()).scheduleAtFixedRate(periodical, periodical.getInitialDelaySeconds(), periodical.getPeriodSeconds(), TimeUnit.SECONDS);
        Mockito.verify(scheduler, Mockito.never()).scheduleAtFixedRate(periodical, periodical.getInitialDelaySeconds(), periodical.getPeriodSeconds(), TimeUnit.SECONDS);
        // TODO This does not work because the verify() runs before the run() method on the periodical has been called.
        // Fixable by using an injectable ThreadFactoryBuilder so we can properly mock?
        // verify(periodical).run();
        Assert.assertFalse("Periodical should not be in the futures Map", periodicals.getFutures().containsKey(periodical));
    }

    @Test
    public void testRegisterAndStartInvokeDaemonScheduler() throws Exception {
        // Uses the daemon scheduler for daemon periodicals.
        Mockito.when(periodical.isDaemon()).thenReturn(true);
        Mockito.when(periodical.runsForever()).thenReturn(false);
        periodicals.registerAndStart(periodical);
        Mockito.verify(daemonScheduler).scheduleAtFixedRate(periodical, periodical.getInitialDelaySeconds(), periodical.getPeriodSeconds(), TimeUnit.SECONDS);
        Mockito.verify(periodical, Mockito.never()).run();
        Assert.assertTrue("Periodical was not added to the futures Map", periodicals.getFutures().containsKey(periodical));
    }

    @Test
    public void testRegisterAndStartInvokeScheduler() throws Exception {
        // Uses the regular scheduler for non-daemon periodicals.
        Mockito.when(periodical.isDaemon()).thenReturn(false);
        Mockito.when(periodical.runsForever()).thenReturn(false);
        periodicals.registerAndStart(periodical);
        Mockito.verify(scheduler).scheduleAtFixedRate(periodical, periodical.getInitialDelaySeconds(), periodical.getPeriodSeconds(), TimeUnit.SECONDS);
        Mockito.verify(periodical, Mockito.never()).run();
        Assert.assertEquals("Future for the periodical was not added to the futures Map", future, periodicals.getFutures().get(periodical));
    }

    @Test
    public void testGetAll() throws Exception {
        periodicals.registerAndStart(periodical);
        Assert.assertEquals("getAll() did not return all periodicals", Lists.newArrayList(periodical), periodicals.getAll());
    }

    @Test
    public void testGetAllReturnsACopyOfThePeriodicalsList() throws Exception {
        periodicals.registerAndStart(periodical);
        periodicals.getAll().add(periodical);
        Assert.assertEquals("getAll() did not return a copy of the periodicals List", 1, periodicals.getAll().size());
    }

    @Test
    public void testGetAllStoppedOnGracefulShutdown() throws Exception {
        final Periodical periodical2 = Mockito.mock(Periodical.class);
        Mockito.when(periodical2.stopOnGracefulShutdown()).thenReturn(true);
        periodicals.registerAndStart(periodical);
        periodicals.registerAndStart(periodical2);
        List<Periodical> allStoppedOnGracefulShutdown = periodicals.getAllStoppedOnGracefulShutdown();
        Assert.assertFalse("periodical without graceful shutdown is in the list", allStoppedOnGracefulShutdown.contains(periodical));
        Assert.assertTrue("graceful shutdown periodical is not in the list", allStoppedOnGracefulShutdown.contains(periodical2));
        Assert.assertEquals("more graceful shutdown periodicals in the list", 1, allStoppedOnGracefulShutdown.size());
    }

    @Test
    public void testGetFutures() throws Exception {
        periodicals.registerAndStart(periodical);
        Assert.assertTrue("missing periodical in future Map", periodicals.getFutures().containsKey(periodical));
        Assert.assertEquals(1, periodicals.getFutures().size());
    }

    @Test
    public void testGetFuturesReturnsACopyOfTheMap() throws Exception {
        final Periodical periodical2 = Mockito.mock(Periodical.class);
        periodicals.registerAndStart(periodical);
        periodicals.getFutures().put(periodical2, null);
        Assert.assertFalse("getFutures() did not return a copy of the Map", periodicals.getFutures().containsKey(periodical2));
        Assert.assertEquals(1, periodicals.getFutures().size());
    }

    @Test
    public void testExceptionIsNotUncaught() {
        final Logger logger = Mockito.mock(Logger.class);
        final Periodical periodical1 = new Periodical() {
            @Override
            public boolean runsForever() {
                return false;
            }

            @Override
            public boolean stopOnGracefulShutdown() {
                return false;
            }

            @Override
            public boolean masterOnly() {
                return false;
            }

            @Override
            public boolean startOnThisNode() {
                return true;
            }

            @Override
            public boolean isDaemon() {
                return false;
            }

            @Override
            public int getInitialDelaySeconds() {
                return 0;
            }

            @Override
            public int getPeriodSeconds() {
                return 1;
            }

            @Override
            protected Logger getLogger() {
                return logger;
            }

            @Override
            public void doRun() {
                throw new NullPointerException();
            }
        };
        periodical1.run();
        // the uncaught exception from doRun should have been logged
        Mockito.verify(logger, Mockito.atLeastOnce()).error(ArgumentMatchers.anyString(), ArgumentMatchers.any(Throwable.class));
    }
}

