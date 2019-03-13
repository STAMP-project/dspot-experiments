/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain;


import StageEvent.All;
import StageEvent.Breaks;
import StageEvent.Fixed;
import com.thoughtworks.go.util.GoConstants;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static StageEvent.All;
import static StageEvent.Breaks;
import static StageEvent.Fixed;


public class NotificationFilterTest {
    @Test
    public void shouldMatchFixedStage() {
        NotificationFilter filter = new NotificationFilter("cruise", "dev", Fixed, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), Fixed), Matchers.is(true));
    }

    @Test
    public void shouldMatchBrokenStage() {
        NotificationFilter filter = new NotificationFilter("cruise", "dev", Breaks, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), Breaks), Matchers.is(true));
    }

    @Test
    public void allEventShouldMatchAnyEvents() {
        NotificationFilter filter = new NotificationFilter("cruise", "dev", All, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), Breaks), Matchers.is(true));
    }

    @Test
    public void shouldNotMatchStageWithDifferentPipeline() {
        NotificationFilter filter = new NotificationFilter("xyz", "dev", All, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), All), Matchers.is(false));
    }

    @Test
    public void shouldNotMatchStageWithDifferentName() {
        NotificationFilter filter = new NotificationFilter("cruise", "xyz", All, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), All), Matchers.is(false));
    }

    @Test
    public void filterWithAllEventShouldIncludeOthers() {
        Assert.assertThat(include(new NotificationFilter("cruise", "dev", Fixed, false)), Matchers.is(true));
    }

    @Test
    public void filterWithSameEventShouldIncludeOthers() {
        Assert.assertThat(include(new NotificationFilter("cruise", "dev", Fixed, true)), Matchers.is(true));
    }

    @Test
    public void anyPipelineShouldAlwaysMatch() {
        NotificationFilter filter = new NotificationFilter(GoConstants.ANY_PIPELINE, GoConstants.ANY_STAGE, Breaks, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), Breaks), Matchers.is(true));
    }

    @Test
    public void anyStageShouldAlwaysMatchWithinSamePipeline() {
        NotificationFilter filter = new NotificationFilter("cruise", GoConstants.ANY_STAGE, Breaks, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise", "dev"), Breaks), Matchers.is(true));
    }

    @Test
    public void anyStageShouldNotMatchWithinADifferentPipeline() {
        NotificationFilter filter = new NotificationFilter("cruise", GoConstants.ANY_STAGE, Breaks, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise2", "dev"), Breaks), Matchers.is(false));
    }

    @Test
    public void specificStageShouldMatchWithinAnyPipeline() {
        NotificationFilter filter = new NotificationFilter(GoConstants.ANY_PIPELINE, "dev", Breaks, false);
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise1", "dev"), Breaks), Matchers.is(true));
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise2", "dev"), Breaks), Matchers.is(true));
        Assert.assertThat(filter.matchStage(new StageConfigIdentifier("cruise2", "not-dev"), Breaks), Matchers.is(false));
    }

    @Test
    public void anyPipelineAndAnyStageShouldAlwaysApply() {
        NotificationFilter filter = new NotificationFilter(GoConstants.ANY_PIPELINE, GoConstants.ANY_STAGE, Breaks, false);
        Assert.assertThat(filter.appliesTo("cruise2", "dev"), Matchers.is(true));
    }

    @Test
    public void anyStageShouldAlwaysApply() {
        NotificationFilter filter = new NotificationFilter("cruise2", GoConstants.ANY_STAGE, Breaks, false);
        Assert.assertThat(filter.appliesTo("cruise2", "dev"), Matchers.is(true));
    }

    @Test
    public void shouldNotApplyIfPipelineDiffers() {
        NotificationFilter filter = new NotificationFilter("cruise1", GoConstants.ANY_STAGE, Breaks, false);
        Assert.assertThat(filter.appliesTo("cruise2", "dev"), Matchers.is(false));
    }

    @Test
    public void shouldNotApplyIfStageDiffers() {
        NotificationFilter filter = new NotificationFilter("cruise2", "devo", Breaks, false);
        Assert.assertThat(filter.appliesTo("cruise2", "dev"), Matchers.is(false));
    }

    @Test
    public void specificStageShouldApplyToAnyPipeline() {
        NotificationFilter filter = new NotificationFilter(GoConstants.ANY_PIPELINE, "dev", Breaks, false);
        Assert.assertThat(filter.appliesTo("cruise1", "dev"), Matchers.is(true));
        Assert.assertThat(filter.appliesTo("cruise2", "dev"), Matchers.is(true));
        Assert.assertThat(filter.appliesTo("cruise2", "not-dev"), Matchers.is(false));
    }
}

