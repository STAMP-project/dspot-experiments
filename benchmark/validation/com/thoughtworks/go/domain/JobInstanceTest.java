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


import JobInstance.NULL;
import JobResult.Cancelled;
import JobResult.Failed;
import JobResult.Passed;
import JobResult.Unknown;
import JobState.Assigned;
import JobState.Building;
import JobState.Completed;
import JobState.Completing;
import JobState.Preparing;
import JobState.Scheduled;
import RunDuration.IN_PROGRESS_DURATION;
import com.thoughtworks.go.helper.JobInstanceMother;
import com.thoughtworks.go.util.TimeProvider;
import java.text.MessageFormat;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static JobState.Building;
import static JobState.Completing;
import static JobState.Scheduled;


public class JobInstanceTest {
    public TimeProvider timeProvider;

    @Test
    public void shouldDetermineMostRecentPassedBeforeWithNullBuildInstances() {
        JobInstance mostRecent = JobInstanceMother.building("mostRecent");
        mostRecent.completing(Passed, new Date());
        mostRecent.completed(new Date());
        Assert.assertEquals(mostRecent, mostRecent.mostRecentPassed(NULL));
        Assert.assertEquals(mostRecent, NULL.mostRecentPassed(mostRecent));
    }

    @Test
    public void shouldDetermineMostRecentPassed() {
        JobInstance oldestPassed = JobInstanceMother.building("oldestPassed");
        oldestPassed.completing(Passed, DateUtils.addHours(new Date(), (-1)));
        oldestPassed.completed(DateUtils.addHours(new Date(), (-1)));
        JobInstance newestPassed = JobInstanceMother.building("newestPassed");
        newestPassed.completing(Passed, new Date());
        newestPassed.completed(new Date());
        Assert.assertEquals(newestPassed, newestPassed.mostRecentPassed(oldestPassed));
        Assert.assertEquals(newestPassed, oldestPassed.mostRecentPassed(newestPassed));
        JobInstance newestFailed = JobInstanceMother.building("newestFailed");
        newestFailed.completing(Failed, DateUtils.addHours(new Date(), (+1)));
        newestFailed.completed(DateUtils.addHours(new Date(), (+1)));
        Assert.assertEquals(newestPassed, newestPassed.mostRecentPassed(newestFailed));
    }

    @Test
    public void shouldGetDisplayStatusFailedIfStateIsCompletedAndResultIsFailed() {
        JobInstance job = JobInstanceMother.completed("test", Failed);
        Assert.assertThat(job.displayStatusWithResult(), Matchers.is("failed"));
    }

    @Test
    public void shouldGetDisplayStatusScheduledIfStateIsScheduled() {
        JobInstance job = JobInstanceMother.scheduled("test");
        Assert.assertThat(job.displayStatusWithResult(), Matchers.is("scheduled"));
    }

    @Test
    public void shouldChangeStatus() throws Exception {
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        instance.assign("1234", timeProvider.currentTime());
        Assert.assertThat(instance.getState(), Matchers.is(Assigned));
        Assert.assertThat(instance.getTransitions().byState(Assigned), Matchers.not(nullValue()));
    }

    @Test
    public void shouldSetCompletingTimeAndResult() throws Exception {
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        final Date completionDate = new Date();
        instance.completing(Passed, completionDate);
        Assert.assertThat(instance.getResult(), Matchers.is(Passed));
        Assert.assertThat(instance.getState(), Matchers.is(Completing));
    }

    @Test
    public void shouldSetCompletedTimeOnComplete() throws Exception {
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        final Date completionDate = new Date();
        instance.completing(Passed, completionDate);
        instance.completed(completionDate);
        Assert.assertThat(instance.getResult(), Matchers.is(Passed));
        Assert.assertThat(instance.getStartedDateFor(Completed), Matchers.is(completionDate));
        Assert.assertThat(instance.getState(), Matchers.is(Completed));
    }

    @Test
    public void shouldIncreaseElapsedTimeWhileBuilding() throws Exception {
        JobInstance instance = JobInstanceMother.building("jobConfig1");
        instance.setClock(timeProvider);
        Mockito.when(timeProvider.currentTime()).thenReturn(new Date(1000000));
        long before = Long.parseLong(instance.getCurrentBuildDuration());
        Mockito.when(timeProvider.currentTime()).thenReturn(new Date(5000000));
        long after = Long.parseLong(instance.getCurrentBuildDuration());
        Assert.assertTrue(((("after " + after) + " should bigger than ") + before), (after > before));
    }

    @Test
    public void shouldReturnTotalDurationOfBuild() throws Exception {
        JobInstance instance = JobInstanceMother.completed("jobConfig1");
        Assert.assertThat(instance.getCurrentBuildDuration(), Matchers.is(instance.durationOfCompletedBuildInSeconds().toString()));
    }

    @Test
    public void shouldReturnBuildLocatorAsTitle() throws Exception {
        JobInstance instance = JobInstanceMother.completed("jobConfig1");
        Assert.assertThat(instance.getTitle(), Matchers.is("pipeline/label-1/stage/1/jobConfig1"));
    }

    @Test
    public void shouldCreateATransitionOnStateChange() throws Exception {
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        instance.completing(Passed);
        final JobStateTransition scheduledState = new JobStateTransition(Scheduled, new Date());
        final JobStateTransition completedState = new JobStateTransition(Completing, new Date());
        Assert.assertThat(instance.getTransitions(), Matchers.hasItem(scheduledState));
        Assert.assertThat(instance.getTransitions(), Matchers.hasItem(completedState));
        Assert.assertThat(instance.getTransitions().first(), Matchers.not(JobInstanceTest.isTransitionWithState(Preparing)));
    }

    @Test
    public void shouldNotCreateATransitionWhenPreviousStateIsTheSame() throws Exception {
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        instance.changeState(Scheduled);
        final JobStateTransition scheduledState = new JobStateTransition(Scheduled, new Date());
        Assert.assertThat(instance.getTransitions(), Matchers.hasItem(scheduledState));
        Assert.assertThat(instance.getTransitions(), iterableWithSize(1));
        Assert.assertThat(instance.getTransitions().first(), Matchers.not(JobInstanceTest.isTransitionWithState(Preparing)));
    }

    @Test
    public void shouldReturnBuildingTransitionTimeAsStartBuildingDate() {
        final Date date = new Date();
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        JobStateTransitions transitions = new JobStateTransitions(new JobStateTransition(Building, date));
        instance.setTransitions(transitions);
        Assert.assertThat(instance.getStartedDateFor(Building), Matchers.is(date));
    }

    @Test
    public void shouldCancelBuild() {
        final JobInstance instance = JobInstanceMother.scheduled("plan1");
        instance.cancel();
        Assert.assertThat(instance.getState(), Matchers.is(Completed));
        Assert.assertThat(instance.getResult(), Matchers.is(Cancelled));
    }

    @Test
    public void shouldNotCancelCompletedBuild() {
        final JobInstance instance = JobInstanceMother.completed("plan1", Passed);
        instance.cancel();
        Assert.assertThat(instance.getResult(), Matchers.is(Passed));
    }

    @Test
    public void shouldDetermineDurationOfCompletedBuild() throws Exception {
        JobInstance testJob = JobInstanceMother.completed("testJob");
        Long duration = testJob.durationOfCompletedBuildInSeconds();
        Assert.assertThat(duration, Matchers.is(120L));
    }

    @Test
    public void durationShouldBeZeroForIncompleteBuild() throws Exception {
        JobInstance building = JobInstanceMother.scheduled("building");
        Long duration = building.durationOfCompletedBuildInSeconds();
        Assert.assertThat(duration, Matchers.is(0L));
    }

    @Test
    public void shouldCleanAgentIdAndResultAfterRescheduled() throws Exception {
        JobInstance instance = JobInstanceMother.assignedWithAgentId("testBuild", "uuid");
        instance.completing(Failed);
        instance.reschedule();
        Assert.assertThat(instance.getState(), Matchers.is(Scheduled));
        Assert.assertThat(instance.getAgentUuid(), Matchers.is(nullValue()));
        Assert.assertThat(instance.getResult(), Matchers.is(Unknown));
    }

    @Test
    public void shouldReturnDateForLatestTransition() throws Exception {
        JobInstance instance = JobInstanceMother.scheduled("jobConfig1");
        instance.setClock(timeProvider);
        Mockito.when(timeProvider.currentTime()).thenReturn(new DateTime().plusDays(1).toDate());
        instance.completing(Passed);
        Assert.assertThat(instance.latestTransitionDate(), Matchers.is(greaterThan(instance.getScheduledDate())));
    }

    @Test
    public void shouldConsiderJobARerunWhenHasOriginalId() {
        JobInstance instance = new JobInstance();
        Assert.assertThat(instance.isCopy(), Matchers.is(false));
        instance.setOriginalJobId(10L);
        Assert.assertThat(instance.isCopy(), Matchers.is(true));
    }

    @Test
    public void shouldReturnJobDurationForACompletedJob() {
        int fiveSeconds = 5000;
        JobInstance instance = JobInstanceMother.passed("first", new Date(), fiveSeconds);
        Assert.assertThat(instance.getDuration(), Matchers.is(new RunDuration.ActualDuration(new Duration((5 * fiveSeconds)))));
    }

    @Test
    public void shouldReturnJobDurationForABuildingJob() {
        int fiveSeconds = 5000;
        JobInstance instance = JobInstanceMother.building("first", new Date(), fiveSeconds);
        Assert.assertThat(instance.getDuration(), Matchers.is(IN_PROGRESS_DURATION));
    }

    @Test
    public void shouldReturnJobTypeCorrectly() {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setRunOnAllAgents(true);
        Assert.assertThat(jobInstance.jobType(), instanceOf(RunOnAllAgents.class));
        jobInstance = new JobInstance();
        jobInstance.setRunMultipleInstance(true);
        Assert.assertThat(jobInstance.jobType(), instanceOf(RunMultipleInstance.class));
        jobInstance = new JobInstance();
        Assert.assertThat(jobInstance.jobType(), instanceOf(SingleJobInstance.class));
    }

    private static class JobStateTransitionMatcher extends BaseMatcher<JobStateTransition> {
        private JobState actualState;

        private final JobState expectedState;

        public JobStateTransitionMatcher(JobState expectedState) {
            this.expectedState = expectedState;
        }

        public boolean matches(Object o) {
            JobStateTransition transition = ((JobStateTransition) (o));
            actualState = transition.getCurrentState();
            return (actualState) == (expectedState);
        }

        public void describeTo(Description description) {
            description.appendText(MessageFormat.format("Expect to get a state {0} but was {1}", expectedState, actualState));
        }
    }
}

