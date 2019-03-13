/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.statistics.rdb;


import StatisticInterval.DAY;
import StatisticInterval.HOUR;
import StatisticInterval.MINUTE;
import com.google.common.base.Optional;
import io.elasticjob.lite.statistics.StatisticInterval;
import io.elasticjob.lite.statistics.type.job.JobRegisterStatistics;
import io.elasticjob.lite.statistics.type.job.JobRunningStatistics;
import io.elasticjob.lite.statistics.type.task.TaskResultStatistics;
import io.elasticjob.lite.statistics.type.task.TaskRunningStatistics;
import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StatisticRdbRepositoryTest {
    private StatisticRdbRepository repository;

    @Test
    public void assertAddTaskResultStatistics() {
        for (StatisticInterval each : StatisticInterval.values()) {
            Assert.assertTrue(repository.add(new TaskResultStatistics(100, 0, each, new Date())));
        }
    }

    @Test
    public void assertAddTaskRunningStatistics() {
        Assert.assertTrue(repository.add(new TaskRunningStatistics(100, new Date())));
    }

    @Test
    public void assertAddJobRunningStatistics() {
        Assert.assertTrue(repository.add(new TaskRunningStatistics(100, new Date())));
    }

    @Test
    public void assertAddJobRegisterStatistics() {
        Assert.assertTrue(repository.add(new JobRegisterStatistics(100, new Date())));
    }

    @Test
    public void assertFindTaskResultStatisticsWhenTableIsEmpty() {
        Assert.assertThat(repository.findTaskResultStatistics(new Date(), MINUTE).size(), CoreMatchers.is(0));
        Assert.assertThat(repository.findTaskResultStatistics(new Date(), HOUR).size(), CoreMatchers.is(0));
        Assert.assertThat(repository.findTaskResultStatistics(new Date(), DAY).size(), CoreMatchers.is(0));
    }

    @Test
    public void assertFindTaskResultStatisticsWithDifferentFromDate() {
        Date now = new Date();
        Date yesterday = getYesterday();
        for (StatisticInterval each : StatisticInterval.values()) {
            Assert.assertTrue(repository.add(new TaskResultStatistics(100, 0, each, yesterday)));
            Assert.assertTrue(repository.add(new TaskResultStatistics(100, 0, each, now)));
            Assert.assertThat(repository.findTaskResultStatistics(yesterday, each).size(), CoreMatchers.is(2));
            Assert.assertThat(repository.findTaskResultStatistics(now, each).size(), CoreMatchers.is(1));
        }
    }

    @Test
    public void assertGetSummedTaskResultStatisticsWhenTableIsEmpty() {
        for (StatisticInterval each : StatisticInterval.values()) {
            TaskResultStatistics po = repository.getSummedTaskResultStatistics(new Date(), each);
            Assert.assertThat(po.getSuccessCount(), CoreMatchers.is(0));
            Assert.assertThat(po.getFailedCount(), CoreMatchers.is(0));
        }
    }

    @Test
    public void assertGetSummedTaskResultStatistics() {
        for (StatisticInterval each : StatisticInterval.values()) {
            Date date = new Date();
            repository.add(new TaskResultStatistics(100, 2, each, date));
            repository.add(new TaskResultStatistics(200, 5, each, date));
            TaskResultStatistics po = repository.getSummedTaskResultStatistics(date, each);
            Assert.assertThat(po.getSuccessCount(), CoreMatchers.is(300));
            Assert.assertThat(po.getFailedCount(), CoreMatchers.is(7));
        }
    }

    @Test
    public void assertFindLatestTaskResultStatisticsWhenTableIsEmpty() {
        for (StatisticInterval each : StatisticInterval.values()) {
            Assert.assertFalse(repository.findLatestTaskResultStatistics(each).isPresent());
        }
    }

    @Test
    public void assertFindLatestTaskResultStatistics() {
        for (StatisticInterval each : StatisticInterval.values()) {
            repository.add(new TaskResultStatistics(100, 2, each, new Date()));
            repository.add(new TaskResultStatistics(200, 5, each, new Date()));
            Optional<TaskResultStatistics> po = repository.findLatestTaskResultStatistics(each);
            Assert.assertThat(po.get().getSuccessCount(), CoreMatchers.is(200));
            Assert.assertThat(po.get().getFailedCount(), CoreMatchers.is(5));
        }
    }

    @Test
    public void assertFindTaskRunningStatisticsWhenTableIsEmpty() {
        Assert.assertThat(repository.findTaskRunningStatistics(new Date()).size(), CoreMatchers.is(0));
    }

    @Test
    public void assertFindTaskRunningStatisticsWithDifferentFromDate() {
        Date now = new Date();
        Date yesterday = getYesterday();
        Assert.assertTrue(repository.add(new TaskRunningStatistics(100, yesterday)));
        Assert.assertTrue(repository.add(new TaskRunningStatistics(100, now)));
        Assert.assertThat(repository.findTaskRunningStatistics(yesterday).size(), CoreMatchers.is(2));
        Assert.assertThat(repository.findTaskRunningStatistics(now).size(), CoreMatchers.is(1));
    }

    @Test
    public void assertFindLatestTaskRunningStatisticsWhenTableIsEmpty() {
        Assert.assertFalse(repository.findLatestTaskRunningStatistics().isPresent());
    }

    @Test
    public void assertFindLatestTaskRunningStatistics() {
        repository.add(new TaskRunningStatistics(100, new Date()));
        repository.add(new TaskRunningStatistics(200, new Date()));
        Optional<TaskRunningStatistics> po = repository.findLatestTaskRunningStatistics();
        Assert.assertThat(po.get().getRunningCount(), CoreMatchers.is(200));
    }

    @Test
    public void assertFindJobRunningStatisticsWhenTableIsEmpty() {
        Assert.assertThat(repository.findJobRunningStatistics(new Date()).size(), CoreMatchers.is(0));
    }

    @Test
    public void assertFindJobRunningStatisticsWithDifferentFromDate() {
        Date now = new Date();
        Date yesterday = getYesterday();
        Assert.assertTrue(repository.add(new JobRunningStatistics(100, yesterday)));
        Assert.assertTrue(repository.add(new JobRunningStatistics(100, now)));
        Assert.assertThat(repository.findJobRunningStatistics(yesterday).size(), CoreMatchers.is(2));
        Assert.assertThat(repository.findJobRunningStatistics(now).size(), CoreMatchers.is(1));
    }

    @Test
    public void assertFindLatestJobRunningStatisticsWhenTableIsEmpty() {
        Assert.assertFalse(repository.findLatestJobRunningStatistics().isPresent());
    }

    @Test
    public void assertFindLatestJobRunningStatistics() {
        repository.add(new JobRunningStatistics(100, new Date()));
        repository.add(new JobRunningStatistics(200, new Date()));
        Optional<JobRunningStatistics> po = repository.findLatestJobRunningStatistics();
        Assert.assertThat(po.get().getRunningCount(), CoreMatchers.is(200));
    }

    @Test
    public void assertFindJobRegisterStatisticsWhenTableIsEmpty() {
        Assert.assertThat(repository.findJobRegisterStatistics(new Date()).size(), CoreMatchers.is(0));
    }

    @Test
    public void assertFindJobRegisterStatisticsWithDifferentFromDate() {
        Date now = new Date();
        Date yesterday = getYesterday();
        Assert.assertTrue(repository.add(new JobRegisterStatistics(100, yesterday)));
        Assert.assertTrue(repository.add(new JobRegisterStatistics(100, now)));
        Assert.assertThat(repository.findJobRegisterStatistics(yesterday).size(), CoreMatchers.is(2));
        Assert.assertThat(repository.findJobRegisterStatistics(now).size(), CoreMatchers.is(1));
    }

    @Test
    public void assertFindLatestJobRegisterStatisticsWhenTableIsEmpty() {
        Assert.assertFalse(repository.findLatestJobRegisterStatistics().isPresent());
    }

    @Test
    public void assertFindLatestJobRegisterStatistics() {
        repository.add(new JobRegisterStatistics(100, new Date()));
        repository.add(new JobRegisterStatistics(200, new Date()));
        Optional<JobRegisterStatistics> po = repository.findLatestJobRegisterStatistics();
        Assert.assertThat(po.get().getRegisteredCount(), CoreMatchers.is(200));
    }
}

