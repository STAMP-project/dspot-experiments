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


import JobState.Assigned;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JobInstancesTest {
    @Test
    public void shouldFilterByStatus() {
        final JobInstance instance1 = new JobInstance("test");
        final JobInstance instance2 = new JobInstance("test2");
        instance2.setState(Assigned);
        JobInstances instances = new JobInstances(instance1, instance2);
        JobInstances actual = instances.filterByState(Assigned);
        Assert.assertThat(actual.size(), Matchers.is(1));
        Assert.assertThat(actual.get(0).getState(), Matchers.is(Assigned));
    }

    @Test
    public void shouldGetMostRecentCompletedBuild() {
        JobInstances jobInstances = mixedBuilds();
        JobInstance mostRecentCompleted = jobInstances.mostRecentCompleted();
        Assert.assertThat(mostRecentCompleted, Matchers.is(jobInstances.get(2)));
    }

    @Test
    public void shouldGetMostRecentPassedBuild() {
        JobInstances jobInstances = mixedBuilds();
        JobInstance mostRecent = jobInstances.mostRecentPassed();
        Assert.assertThat(mostRecent, Matchers.is(jobInstances.get(1)));
    }

    @Test
    public void shouldGetMostRecentPassedWhenBuilding() {
        JobInstances jobInstances = new JobInstances(passed("passed"), building("unit"));
        JobInstance mostRecent = jobInstances.mostRecentPassed();
        Assert.assertThat(mostRecent.getName(), Matchers.is("passed"));
    }

    @Test
    public void shouldGetMostRecentPassedBuildIfThereAreFailedBuilds() {
        JobInstances jobInstances = new JobInstances(failed("foo"), passed("foo"));
        JobInstance mostRecent = jobInstances.mostRecentPassed();
        Assert.assertThat(mostRecent, Matchers.is(jobInstances.get(1)));
    }

    @Test
    public void shouldReturnNullObjectWhenNoMostRecentPassedInstance() {
        JobInstance actual = new JobInstances().mostRecentPassed();
        Assert.assertThat(actual.isNull(), Matchers.is(true));
    }

    @Test
    public void shouldReturnStatusBuildingWhenAnyBuildsAreBuilding() {
        JobInstances builds = new JobInstances();
        builds.add(completed("passports", JobResult.Passed));
        builds.add(completed("visas", JobResult.Cancelled));
        builds.add(scheduled("flights"));
        Assert.assertThat(builds.stageState(), Matchers.is(Building));
    }

    @Test
    public void jobShouldBeCancelledWhenNoActiveBuildAndHaveAtLeastOneCancelledJob() {
        JobInstances builds = new JobInstances();
        builds.add(completed("passports", JobResult.Passed));
        builds.add(completed("passports-failed", JobResult.Failed));
        builds.add(completed("visas", JobResult.Cancelled));
        builds.add(completed("flights", JobResult.Cancelled));
        Assert.assertThat(builds.stageState(), Matchers.is(Cancelled));
    }

    @Test
    public void shouldReturnStatusFailingWhenAnyPlansHaveFailedAndNotAllAreCompleted() {
        JobInstances builds = new JobInstances();
        builds.add(completed("passports", JobResult.Failed));
        builds.add(completed("visas", JobResult.Passed));
        builds.add(scheduled("flights"));
        Assert.assertThat(builds.stageState(), Matchers.is(Failing));
    }

    @Test
    public void shouldReturnLatestTransitionDate() {
        Date expectedLatest = date(3908, 10, 12);
        Date actualLatest = latestTransitionDate();
        Assert.assertThat(actualLatest, Matchers.is(expectedLatest));
    }
}

