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
package org.graylog2.system.jobs;


import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.TimeUnit;
import org.graylog2.system.activities.SystemMessageActivityWriter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SystemJobManagerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SystemMessageActivityWriter systemMessageActivityWriter;

    @Test
    public void testGetRunningJobs() throws Exception {
        SystemJobManager manager = new SystemJobManager(systemMessageActivityWriter, new MetricRegistry());
        SystemJobManagerTest.LongRunningJob job1 = new SystemJobManagerTest.LongRunningJob(1);
        SystemJobManagerTest.LongRunningJob job2 = new SystemJobManagerTest.LongRunningJob(1);
        String jobID1 = manager.submit(job1);
        String jobID2 = manager.submit(job2);
        Assert.assertEquals(2, manager.getRunningJobs().size());
        Assert.assertTrue(manager.getRunningJobs().containsValue(job1));
        Assert.assertTrue(manager.getRunningJobs().containsValue(job2));
        Assert.assertEquals(jobID1, manager.getRunningJobs().get(jobID1).getId());
        Assert.assertEquals(jobID2, manager.getRunningJobs().get(jobID2).getId());
    }

    @Test
    public void testConcurrentJobs() throws Exception {
        SystemJobManager manager = new SystemJobManager(systemMessageActivityWriter, new MetricRegistry());
        SystemJob job1 = new SystemJobManagerTest.LongRunningJob(3);
        SystemJob job2 = new SystemJobManagerTest.LongRunningJob(3);
        SystemJob job3 = new SystemJobManagerTest.AnotherLongRunningJob(3);
        manager.submit(job1);
        manager.submit(job2);
        manager.submit(job3);
        Assert.assertEquals(3, manager.getRunningJobs().size());
        Assert.assertEquals(2, manager.concurrentJobs(job1.getClass()));
    }

    @Test
    public void testSubmitThrowsExceptionIfMaxConcurrencyLevelReached() throws Exception {
        SystemJobManager manager = new SystemJobManager(systemMessageActivityWriter, new MetricRegistry());
        SystemJobManagerTest.LongRunningJob job1 = new SystemJobManagerTest.LongRunningJob(3);
        SystemJobManagerTest.LongRunningJob job2 = new SystemJobManagerTest.LongRunningJob(3);
        SystemJob job3 = new SystemJobManagerTest.AnotherLongRunningJob(3);
        // We have to set it for both instances in tests because the stubs are dynamic and no static max level can be set.
        job1.setMaxConcurrency(1);
        job2.setMaxConcurrency(1);
        manager.submit(job1);
        boolean exceptionThrown = false;
        try {
            manager.submit(job2);
        } catch (SystemJobConcurrencyException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
        manager.submit(job3);
        Assert.assertEquals(2, manager.getRunningJobs().size());
        Assert.assertEquals(1, manager.concurrentJobs(job1.getClass()));
    }

    private static class LongRunningJob extends SystemJob {
        private int seconds;

        private int maxConcurrency = 9001;

        public LongRunningJob(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void execute() {
            Uninterruptibles.sleepUninterruptibly(seconds, TimeUnit.SECONDS);
        }

        void setMaxConcurrency(int maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }

        @Override
        public void requestCancel() {
        }

        @Override
        public int getProgress() {
            return 0;
        }

        @Override
        public int maxConcurrency() {
            return maxConcurrency;
        }

        @Override
        public boolean providesProgress() {
            return false;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }

        @Override
        public String getDescription() {
            return "Test Job. You better not use this anywhere else, bro.";
        }

        @Override
        public String getClassName() {
            return getClass().getCanonicalName();
        }
    }

    private static class AnotherLongRunningJob extends SystemJob {
        private int seconds;

        public AnotherLongRunningJob(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void execute() {
            Uninterruptibles.sleepUninterruptibly(seconds, TimeUnit.SECONDS);
        }

        @Override
        public void requestCancel() {
        }

        @Override
        public int getProgress() {
            return 0;
        }

        @Override
        public int maxConcurrency() {
            return 9001;
        }

        @Override
        public boolean providesProgress() {
            return false;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }

        @Override
        public String getDescription() {
            return "Another Test Job. You better not use this anywhere else, bro.";
        }

        @Override
        public String getClassName() {
            return getClass().getCanonicalName();
        }
    }
}

