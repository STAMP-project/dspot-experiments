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
package com.thoughtworks.go.server.service;


import PipelineDependencyGraphOld.Filterer;
import StageResult.Cancelled;
import StageResult.Failed;
import StageResult.Passed;
import StageResult.Unknown;
import com.thoughtworks.go.domain.PipelineDependencyGraphOld;
import com.thoughtworks.go.domain.StageIdentifier;
import com.thoughtworks.go.domain.StageResult;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PipelineHistoryMother;
import com.thoughtworks.go.presentation.pipelinehistory.PipelineInstanceModel;
import com.thoughtworks.go.presentation.pipelinehistory.PipelineInstanceModels;
import com.thoughtworks.go.presentation.pipelinehistory.StageInstanceModels;
import java.util.Map;
import java.util.TreeSet;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PipelineDependencyGraphOldTest {
    @Test
    public void shouldFilterPIMS() throws Exception {
        PipelineDependencyGraphOld graph = new PipelineDependencyGraphOld(pim("upstream"), PipelineInstanceModels.createPipelineInstanceModels(pim("pavan"), pim("raghu")));
        PipelineDependencyGraphOld.Filterer filterer = Mockito.mock(Filterer.class);
        Mockito.when(filterer.filterPipeline("raghu")).thenReturn(false);
        Mockito.when(filterer.filterPipeline("pavan")).thenReturn(true);
        graph.filterDependencies(filterer);
        PipelineInstanceModels models = graph.dependencies();
        Assert.assertThat(models.size(), Matchers.is(1));
        Assert.assertThat(models.get(0).getName(), Matchers.is("pavan"));
    }

    @Test
    public void shouldGroupPiplineInstancesByName() throws Exception {
        PipelineInstanceModel raghu1 = pim("raghu");
        raghu1.setCounter(1);
        PipelineInstanceModel raghu2 = pim("raghu");
        raghu2.setCounter(2);
        PipelineInstanceModel phavan = pim("pavan");
        PipelineDependencyGraphOld graph = new PipelineDependencyGraphOld(pim("upstream"), PipelineInstanceModels.createPipelineInstanceModels(raghu2, phavan, raghu1));
        Map<String, TreeSet<PipelineInstanceModel>> map = graph.groupedDependencies();
        Assert.assertThat(map.size(), Matchers.is(2));
        assertOrderIsMainted(map);
        Assert.assertThat(map.get("pavan").size(), Matchers.is(1));
        Assert.assertThat(map.get("pavan"), Matchers.hasItem(phavan));
        Assert.assertThat(map.get("raghu").size(), Matchers.is(2));
        Assert.assertThat(map.get("raghu").first(), Matchers.is(raghu2));
        Assert.assertThat(map.get("raghu").last(), Matchers.is(raghu1));
    }

    @Test
    public void shouldProvideMaterialRevisionForAGivenDownStreamPipeline() throws Exception {
        StageInstanceModels stages = new StageInstanceModels();
        stages.add(new com.thoughtworks.go.presentation.pipelinehistory.StageInstanceModel("stage-0", "21", StageResult.Cancelled, new StageIdentifier("blahUpStream", 23, "stage-0", "21")));
        stages.add(new com.thoughtworks.go.presentation.pipelinehistory.StageInstanceModel("stage-1", "2", StageResult.Cancelled, new StageIdentifier("blahUpStream", 23, "stage-1", "2")));
        PipelineInstanceModel upStream = PipelineHistoryMother.singlePipeline("blahUpStream", stages);
        PipelineInstanceModel down1 = pim("blahDown1");
        down1.setMaterialConfigs(new com.thoughtworks.go.config.materials.MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("blahUpStream", "stage-0")));
        PipelineInstanceModel down2 = pim("blahDown2");
        down2.setMaterialConfigs(new com.thoughtworks.go.config.materials.MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("blahUpStream", "stage-1")));
        PipelineDependencyGraphOld graph = new PipelineDependencyGraphOld(upStream, PipelineInstanceModels.createPipelineInstanceModels(down1, down2));
        Assert.assertThat(graph.dependencyRevisionFor(down1), Matchers.is("blahUpStream/23/stage-0/21"));
        Assert.assertThat(graph.dependencyRevisionFor(down2), Matchers.is("blahUpStream/23/stage-1/2"));
    }

    @Test
    public void shouldShouldbeAbleToTellIfUpStreamMaterialIsAvailableForTrigger() throws Exception {
        StageInstanceModels stages = new StageInstanceModels();
        stages.add(new com.thoughtworks.go.presentation.pipelinehistory.StageInstanceModel("stage-0", "21", StageResult.Cancelled, new StageIdentifier("blahUpStream", 23, "stage-0", "21")));
        PipelineInstanceModel upStream = PipelineHistoryMother.singlePipeline("blahUpStream", stages);
        PipelineInstanceModel down1 = pim("blahDown1");
        down1.setMaterialConfigs(new com.thoughtworks.go.config.materials.MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("blahUpStream", "stage-1")));
        PipelineDependencyGraphOld graph = new PipelineDependencyGraphOld(upStream, PipelineInstanceModels.createPipelineInstanceModels(down1));
        Assert.assertThat(graph.hasUpStreamRevisionFor(down1), Matchers.is(false));
    }

    @Test
    public void shouldShouldbeAbleToTellIfUpStreamMaterialIsAvailableForTriggerOnlyIfTheUpstreamStageHasPassed() throws Exception {
        assertThatTriggerIsPossibleOnlyIfUpStreamPassed(Cancelled);
        assertThatTriggerIsPossibleOnlyIfUpStreamPassed(Failed);
        assertThatTriggerIsPossibleOnlyIfUpStreamPassed(Unknown);
        assertThatTriggerIsPossibleOnlyIfUpStreamPassed(Passed);
    }
}

