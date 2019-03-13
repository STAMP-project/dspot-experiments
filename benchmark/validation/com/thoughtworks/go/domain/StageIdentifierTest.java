/**
 * Copyright 2018 ThoughtWorks, Inc.
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


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StageIdentifierTest {
    @Test
    public void shouldContainCounterIfStageHasRerun() {
        StageIdentifier identifier = new StageIdentifier("cruise", null, "label", "dev", "2");
        Assert.assertThat(identifier.ccTrayLastBuildLabel(), Matchers.is("label :: 2"));
    }

    @Test
    public void shouldNotContainCounterForFirstRun() {
        StageIdentifier identifier = new StageIdentifier("cruise", null, "label", "dev", "1");
        Assert.assertThat(identifier.ccTrayLastBuildLabel(), Matchers.is("label"));
    }

    @Test
    public void shouldConstructFromStageLocator() {
        StageIdentifier identifier = new StageIdentifier("pipeline-name/10/stage-name/7");
        Assert.assertThat(identifier.getPipelineName(), Matchers.is("pipeline-name"));
        Assert.assertThat(identifier.getStageName(), Matchers.is("stage-name"));
        Assert.assertThat(identifier.getPipelineCounter(), Matchers.is(10));
        Assert.assertThat(identifier.getStageCounter(), Matchers.is("7"));
    }

    @Test
    public void shouldThrowExceptionIfStageCounterIsNotNumber() {
        StageIdentifier identifier = new StageIdentifier("cruise", null, "label", "dev", "");
        try {
            identifier.ccTrayLastBuildLabel();
            Assert.fail("should throw exception if stage counter is not number");
        } catch (Exception e) {
            Assert.assertThat(e, Matchers.instanceOf(NumberFormatException.class));
        }
    }

    @Test
    public void pipelineStagesWithSameCountersAndDifferentlabelShouldBeEqual() {
        StageIdentifier stage1 = new StageIdentifier("blahPipeline", 1, "blahLabel", "blahStage", "1");
        StageIdentifier stage2 = new StageIdentifier("blahPipeline", 1, "fooLabel", "blahStage", "1");
        StageIdentifier stage3 = new StageIdentifier("blahPipeline", 1, "blahStage", "1");
        StageIdentifier stage4 = new StageIdentifier("blahPipeline", 1, "blahStage", "2");
        Assert.assertThat(stage1, Matchers.is(stage2));
        Assert.assertThat(stage1, Matchers.is(stage3));
        Assert.assertThat(stage2, Matchers.is(stage3));
        Assert.assertThat(stage2, Matchers.is(Matchers.not(stage4)));
    }

    @Test
    public void shouldReturnURN() throws Exception {
        StageIdentifier id = new StageIdentifier("cruise", 1, "dev", "1");
        Assert.assertThat(id.asURN(), Matchers.is("urn:x-go.studios.thoughtworks.com:stage-id:cruise:1:dev:1"));
    }
}

