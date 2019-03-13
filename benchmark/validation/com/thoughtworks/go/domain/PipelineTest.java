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


import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.helper.StageMother;
import java.sql.SQLException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class PipelineTest {
    @Test
    public void shouldReturnNextStageName() throws Exception {
        Pipeline pipeline = new Pipeline();
        Stage stage1 = StageMother.custom("stage1");
        stage1.setId(1);
        stage1.setOrderId(1);
        Stage stage2 = StageMother.custom("stage2");
        stage2.setId(2);
        stage2.setOrderId(2);
        Stage stage3 = StageMother.custom("stage3");
        stage3.setId(3);
        stage3.setOrderId(3);
        pipeline.getStages().add(stage2);
        pipeline.getStages().add(stage1);
        pipeline.getStages().add(stage3);
        Assert.assertThat(pipeline.nextStageName("stage1"), Matchers.is("stage2"));
        shouldReturnNullIfNoneNext(pipeline);
        shouldReturnNullIfStageNotExist(pipeline);
    }

    @Test
    public void shouldReturnNullForEmptyPipeline() throws Exception {
        Pipeline pipeline = new Pipeline();
        Assert.assertThat(pipeline.nextStageName("anyStage"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldUseOldBuildCauseMessageIfThereIsNoneForThisPipeline() throws SQLException {
        MaterialRevisions materialRevisions = ModificationsMother.multipleModifications();
        BuildCause buildCause = BuildCause.createWithModifications(materialRevisions, "");
        Pipeline pipeline = new Pipeline("Test", buildCause);
        Assert.assertThat(pipeline.getBuildCauseMessage(), Matchers.not(Matchers.nullValue()));
    }

    @Test
    public void shouldHaveBuildCauseMessageUnknownIfBuildCauseIsNull() throws SQLException {
        Pipeline pipeline = new Pipeline("Test", null);
        Assert.assertThat(pipeline.getBuildCauseMessage(), Matchers.is("Unknown"));
    }

    @Test
    public void shouldIncrementCounterAndUpdateLabel() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.setBuildCause(ModificationsMother.modifyNoFiles(PipelineConfigMother.pipelineConfig("mingle")));
        pipeline.updateCounter(1);
        Assert.assertThat(pipeline.getCounter(), Matchers.is(2));
        Assert.assertThat(pipeline.getLabel(), Matchers.is("2"));
    }

    @Test
    public void shouldUseOneAsFirstCounter() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.setBuildCause(ModificationsMother.modifyNoFiles(PipelineConfigMother.pipelineConfig("mingle")));
        pipeline.updateCounter(0);
        Assert.assertThat(pipeline.getCounter(), Matchers.is(1));
        Assert.assertThat(pipeline.getLabel(), Matchers.is("1"));
    }

    @Test
    public void shouldReturnIfAPipelineIsABisect() {
        Pipeline pipeline = new Pipeline();
        pipeline.setNaturalOrder(1.0);
        Assert.assertThat(pipeline.isBisect(), Matchers.is(false));
        pipeline.setNaturalOrder(5.0);
        Assert.assertThat(pipeline.isBisect(), Matchers.is(false));
        pipeline.setNaturalOrder(1.5);
        Assert.assertThat(pipeline.isBisect(), Matchers.is(true));
        pipeline.setNaturalOrder(5.0625);
        Assert.assertThat(pipeline.isBisect(), Matchers.is(true));
        pipeline.setNaturalOrder(5.000030517578125);
        Assert.assertThat(pipeline.isBisect(), Matchers.is(true));
    }
}

