/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain;


import JobResult.Failed;
import JobState.Building;
import JobState.Completed;
import JobState.Scheduled;
import RunDuration.ActualDuration;
import RunDuration.IN_PROGRESS_DURATION;
import StageState.Passed;
import StageState.Unknown;
import com.thoughtworks.go.helper.StageMother;
import com.thoughtworks.go.util.TimeProvider;
import com.thoughtworks.go.utils.Timeout;
import java.sql.Timestamp;
import java.util.Date;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Test;


public class StageTest {
    DateTime time1 = new DateTime(2008, 2, 22, 12, 22, 23, 0);

    DateTime time2 = new DateTime(2008, 2, 22, 12, 22, 24, 0);

    DateTime time3 = new DateTime(2008, 2, 22, 12, 22, 25, 0);

    DateTime time4 = new DateTime(2008, 2, 22, 12, 22, 26, 0);

    private JobInstances jobInstances;

    private Stage stage;

    private JobInstance firstJob;

    private JobInstance secondJob;

    private static final Date JOB_SCHEDULE_DATE = new Date();

    private TimeProvider timeProvider;

    private long nextId = 0;

    @Test
    public void shouldUpdateCompletedByTransitionIdAndStageState() throws Exception {
        Assert.assertThat(stage.getCompletedByTransitionId(), Matchers.is(Matchers.nullValue()));
        DateTime fiveMinsForNow = new DateTime().plusMinutes(5);
        complete(firstJob, fiveMinsForNow);
        complete(secondJob, fiveMinsForNow);
        JobStateTransition lastTransition = secondJob.getTransition(Completed);
        stage.calculateResult();
        Assert.assertThat(stage.getCompletedByTransitionId(), Matchers.is(nextId));
        Assert.assertThat(stage.getState(), Matchers.is(Passed));
    }

    @Test
    public void shouldAnswerIsScheduledTrueWhenAllJobsAreInScheduleState() throws Exception {
        stage.setCounter(1);
        firstJob.setState(Scheduled);
        secondJob.setState(Scheduled);
        Assert.assertThat(stage.isScheduled(), Matchers.is(true));
    }

    @Test
    public void shouldAnswerIsScheduledFalseWhenAJobIsNotInScheduledState() {
        stage.setCounter(1);
        firstJob.setState(Scheduled);
        secondJob.setState(Completed);
        Assert.assertThat(stage.isScheduled(), Matchers.is(false));
    }

    @Test
    public void shouldAnswerIsScheduledFalseWhenAStageIsAReRun() {
        stage.setCounter(2);
        firstJob.setState(Scheduled);
        secondJob.setState(Scheduled);
        Assert.assertThat(stage.isScheduled(), Matchers.is(false));
    }

    @Test
    public void shouldAnswerIsReRunTrueWhenAllJobsAreInScheduleState() throws Exception {
        stage.setCounter(2);
        stage.setRerunOfCounter(null);
        firstJob.setState(Scheduled);
        secondJob.setState(Scheduled);
        Assert.assertThat(stage.isReRun(), Matchers.is(true));
    }

    @Test
    public void shouldAnswerIsReRunFalseWhenAJobIsNotInScheduledState() {
        stage.setCounter(2);
        stage.setRerunOfCounter(null);
        firstJob.setState(Scheduled);
        secondJob.setState(Completed);
        Assert.assertThat(stage.isReRun(), Matchers.is(false));
    }

    @Test
    public void shouldAnswerIsReRunFalseWhenStageIsScheduledFirstTime() {
        stage.setCounter(1);
        stage.setRerunOfCounter(null);
        firstJob.setState(Scheduled);
        secondJob.setState(Scheduled);
        Assert.assertThat(stage.isReRun(), Matchers.is(false));
    }

    @Test
    public void shouldAnswerIsReRunTrueWhenAllReRunJobsAreInScheduleState() {
        stage.setCounter(2);
        stage.setRerunOfCounter(1);
        firstJob.setRerun(true);
        firstJob.setState(Scheduled);
        secondJob.setRerun(false);
        secondJob.setState(Completed);
        Assert.assertThat(stage.isReRun(), Matchers.is(true));
    }

    @Test
    public void shouldAnswerIsReRunFalseWhenAReRunJobIsNotInScheduleState() {
        stage.setCounter(2);
        stage.setRerunOfCounter(1);
        firstJob.setRerun(true);
        firstJob.setState(Building);
        secondJob.setRerun(false);
        secondJob.setState(Completed);
        Assert.assertThat(stage.isReRun(), Matchers.is(false));
    }

    @Test
    public void shouldReturnMostRecentCompletedTransitionAsCompletedDateIfLatestTransitionIdIsNot() {
        firstJob.assign("AGENT-1", time1.toDate());
        firstJob.completing(JobResult.Passed, time2.toDate());
        firstJob.completed(time2.toDate());
        secondJob.assign("AGENT-2", time3.toDate());
        secondJob.completing(JobResult.Passed, time4.toDate());
        secondJob.completed(time4.toDate());
        secondJob.getTransitions().byState(Completed).setId(1);
        stage.calculateResult();
        Assert.assertThat(stage.completedDate(), Matchers.is(time4.toDate()));
    }

    @Test
    public void shouldReturnNullAsCompletedDateIfNeverCompleted() {
        firstJob.assign("AGENT-1", time1.toDate());
        secondJob.assign("AGENT-2", time3.toDate());
        Assert.assertNull("Completed date should be null", stage.completedDate());
    }

    @Test
    public void stageStateShouldBeUnkownIfNoJobs() {
        Stage newStage = new Stage();
        Assert.assertThat(newStage.stageState(), Matchers.is(Unknown));
    }

    @Test
    public void shouldCalculateTotalTimeFromFirstScheduledJobToLastCompletedJob() {
        final DateTime time0 = new DateTime(2008, 2, 22, 10, 21, 23, 0);
        timeProvider = new TimeProvider() {
            @Override
            public Date currentTime() {
                return time0.toDate();
            }

            public DateTime currentDateTime() {
                throw new UnsupportedOperationException("Not implemented");
            }

            public DateTime timeoutTime(Timeout timeout) {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
        firstJob = new JobInstance("first-job", timeProvider);
        secondJob = new JobInstance("second-job", timeProvider);
        jobInstances = new JobInstances(firstJob, secondJob);
        stage = StageMother.custom("test", jobInstances);
        firstJob.assign("AGENT-1", time1.toDate());
        firstJob.completing(JobResult.Passed, time2.toDate());
        firstJob.completed(time2.toDate());
        secondJob.assign("AGENT-2", time3.toDate());
        secondJob.completing(JobResult.Passed, time4.toDate());
        secondJob.completed(time4.toDate());
        stage.calculateResult();
        stage.setCreatedTime(new Timestamp(time0.toDate().getTime()));
        stage.setLastTransitionedTime(new Timestamp(time4.toDate().getTime()));
        RunDuration.ActualDuration expectedDuration = new RunDuration.ActualDuration(new org.joda.time.Duration(time0, time4));
        RunDuration.ActualDuration duration = ((RunDuration.ActualDuration) (stage.getDuration()));
        Assert.assertThat(duration, Matchers.is(expectedDuration));
        Assert.assertThat(duration.getTotalSeconds(), Matchers.is(7263L));
    }

    @Test
    public void shouldReturnZeroDurationForIncompleteStage() {
        firstJob.assign("AGENT-1", time1.toDate());
        firstJob.changeState(Building, time2.toDate());
        Assert.assertThat(stage.getDuration(), Matchers.is(IN_PROGRESS_DURATION));
    }

    @Test
    public void shouldReturnLatestTransitionDate() {
        Date date = StageTest.JOB_SCHEDULE_DATE;
        firstJob.completing(Failed, date);
        Assert.assertThat(stage.latestTransitionDate(), Matchers.is(date));
    }

    @Test
    public void shouldReturnCreatedDateWhenNoTranstions() throws Exception {
        stage = new Stage("dev", new JobInstances(), "anonymous", null, "manual", new TimeProvider());
        Assert.assertEquals(new Date(stage.getCreatedTime().getTime()), stage.latestTransitionDate());
    }

    @Test
    public void shouldCreateAStageWithAGivenConfigVersion() {
        Stage stage = new Stage("foo-stage", new JobInstances(), "admin", null, "manual", false, false, "git-sha", new TimeProvider());
        Assert.assertThat(stage.getConfigVersion(), Matchers.is("git-sha"));
        stage = new Stage("foo-stage", new JobInstances(), "admin", null, "manual", new TimeProvider());
        Assert.assertThat(stage.getConfigVersion(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldSetTheCurrentTimeAsCreationTimeForRerunOfJobs() {
        Stage stage = new Stage("foo-stage", new JobInstances(), "admin", null, "manual", false, false, "git-sha", new TimeProvider());
        Timestamp createdTimeOfRun1 = stage.getCreatedTime();
        long minuteAfter = (DateTimeUtils.currentTimeMillis()) + 60000;
        freezeTime(minuteAfter);
        stage.prepareForRerunOf(new DefaultSchedulingContext("admin"), "git-sha");
        resetTime();
        Timestamp createdTimeOfRun2 = stage.getCreatedTime();
        Assert.assertNotEquals(createdTimeOfRun1, createdTimeOfRun2);
        Assert.assertEquals(createdTimeOfRun2, new Timestamp(minuteAfter));
    }
}

