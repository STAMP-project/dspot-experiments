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
package com.thoughtworks.go.presentation.pipelinehistory;


import MaterialRevisions.EMPTY;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.domain.PipelinePauseInfo;
import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.helper.ModificationsMother;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineModelTest {
    @Test
    public void shouldUnderstandIfHasNewRevisions() throws Exception {
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipelineName", (-1), "1", BuildCause.createManualForced(), new StageInstanceModels());
        MaterialRevisions latest = ModificationsMother.createHgMaterialRevisions();
        instanceModel.setMaterialConfigs(new com.thoughtworks.go.config.materials.MaterialConfigs(latest.getMaterialRevision(0).getMaterial().config()));
        instanceModel.setLatestRevisions(latest);
        PipelineModel pipelineModel = new PipelineModel(instanceModel.getName(), true, true, PipelinePauseInfo.notPaused());
        pipelineModel.addPipelineInstance(instanceModel);
        instanceModel.setMaterialRevisionsOnBuildCause(EMPTY);
        Assert.assertThat(pipelineModel.hasNewRevisions(), Matchers.is(true));
        instanceModel.setMaterialRevisionsOnBuildCause(latest);
        Assert.assertThat(pipelineModel.hasNewRevisions(), Matchers.is(false));
    }

    @Test
    public void shouldNotBeAbleToscheduleIfTheLatestPipelineIsPreparingToSchedule() throws Exception {
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPreparingToSchedule("pipelineName", new StageInstanceModels());
        PipelineModel pipelineModel = new PipelineModel(instanceModel.getName(), true, true, PipelinePauseInfo.notPaused());
        pipelineModel.addPipelineInstance(instanceModel);
        Assert.assertThat(pipelineModel.canForce(), Matchers.is(false));
    }

    @Test
    public void shouldUnderstandCanOperateAndCanForce() {
        PipelineModel foo = new PipelineModel("foo", true, true, PipelinePauseInfo.notPaused());
        foo.addPipelineInstance(pipelineNamed("foo"));
        PipelineModel bar = new PipelineModel("bar", false, false, PipelinePauseInfo.notPaused());
        bar.addPipelineInstance(pipelineNamed("bar"));
        PipelineModel baz = new PipelineModel("baz", false, true, PipelinePauseInfo.notPaused());
        baz.addPipelineInstance(pipelineNamed("baz"));
        Assert.assertThat(foo.canOperate(), Matchers.is(true));
        Assert.assertThat(foo.canForce(), Matchers.is(true));
        Assert.assertThat(bar.canOperate(), Matchers.is(false));
        Assert.assertThat(bar.canForce(), Matchers.is(false));
        Assert.assertThat(baz.canOperate(), Matchers.is(true));
        Assert.assertThat(baz.canForce(), Matchers.is(false));
    }
}

