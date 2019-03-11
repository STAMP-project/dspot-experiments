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


import StageEvent.Breaks;
import StageEvent.Fails;
import StageEvent.Fixed;
import StageEvent.Passes;
import StageResult.Cancelled;
import StageResult.Failed;
import StageResult.Passed;
import StageResult.Unknown;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StageResultTest {
    @Test
    public void shouldGenerateEventByResults() {
        Assert.assertThat(Cancelled.describeChangeEvent(Passed), Matchers.is(StageEvent.Cancelled));
        Assert.assertThat(Cancelled.describeChangeEvent(Failed), Matchers.is(StageEvent.Cancelled));
        Assert.assertThat(Cancelled.describeChangeEvent(Cancelled), Matchers.is(StageEvent.Cancelled));
        Assert.assertThat(Cancelled.describeChangeEvent(Unknown), Matchers.is(StageEvent.Cancelled));
        Assert.assertThat(Passed.describeChangeEvent(Failed), Matchers.is(Fixed));
        Assert.assertThat(Passed.describeChangeEvent(Cancelled), Matchers.is(Passes));
        Assert.assertThat(Passed.describeChangeEvent(Passed), Matchers.is(Passes));
        Assert.assertThat(Passed.describeChangeEvent(Unknown), Matchers.is(Passes));
        Assert.assertThat(Failed.describeChangeEvent(Passed), Matchers.is(Breaks));
        Assert.assertThat(Failed.describeChangeEvent(Failed), Matchers.is(Fails));
        Assert.assertThat(Failed.describeChangeEvent(Cancelled), Matchers.is(Fails));
        Assert.assertThat(Failed.describeChangeEvent(Unknown), Matchers.is(Fails));
    }

    @Test
    public void shouldThrowExceptionIfNewStatusIsUnknown() {
        try {
            Unknown.describeChangeEvent(Passed);
            Assert.fail("shouldThrowExceptionIfNewStatusIsUnknown");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Current result can not be Unknown"));
        }
    }

    @Test
    public void shouldMakeSureThatAnyChangeToThisEnumIsReflectedInQueriesInStagesXML() throws Exception {
        Assert.assertThat(StageResult.values().length, Matchers.is(4));
        List<StageResult> actualStageResults = Arrays.asList(StageResult.values());
        List<String> names = new ArrayList<>();
        for (StageResult stageResult : actualStageResults) {
            names.add(stageResult.name());
        }
        Assert.assertThat(("If this test fails, it means that either a stage result has been added/removed or renamed.\n" + " This might cause Stage queries in Stages.xml, especially, allPassedStageAsDMRsAfter, which uses something like: stages.result <> 'Failed' AND stages.result <> 'Unknown' AND stages.result <> 'Cancelled'"), names, Matchers.hasItems("Passed", "Cancelled", "Failed", "Unknown"));
    }
}

