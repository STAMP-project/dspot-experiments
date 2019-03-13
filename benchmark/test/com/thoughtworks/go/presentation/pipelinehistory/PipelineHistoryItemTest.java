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


import com.thoughtworks.go.helper.PipelineHistoryItemMother;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineHistoryItemTest {
    @Test
    public void shouldReturnFalseForEmptyPipelineHistory() throws Exception {
        PipelineInstanceModel emptyOne = PipelineInstanceModel.createEmptyModel();
        Assert.assertThat(emptyOne.hasPreviousStageBeenScheduled("stage1"), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueForFirstStage() throws Exception {
        Assert.assertThat(PipelineHistoryItemMother.custom("stage1").hasPreviousStageBeenScheduled("stage1"), Matchers.is(true));
        Assert.assertThat(PipelineHistoryItemMother.custom("stage1", "stage2").hasPreviousStageBeenScheduled("stage1"), Matchers.is(true));
    }

    @Test
    public void shouldCheckIfPreviousStageInstanceExist() throws Exception {
        PipelineInstanceModel twoStages = PipelineHistoryItemMother.custom("stage1", "stage2");
        Assert.assertThat(twoStages.hasPreviousStageBeenScheduled("stage2"), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfPreviousStageHasNotBeenScheduled() throws Exception {
        PipelineInstanceModel twoStages = PipelineHistoryItemMother.custom(new NullStageHistoryItem("stage1"), new StageInstanceModel("stage2", "1", new JobHistory()));
        Assert.assertThat(twoStages.hasPreviousStageBeenScheduled("stage2"), Matchers.is(false));
        PipelineInstanceModel threeStages = PipelineHistoryItemMother.custom(new NullStageHistoryItem("stage1"), new NullStageHistoryItem("stage2"), new StageInstanceModel("stage3", "1", new JobHistory()));
        Assert.assertThat(threeStages.hasPreviousStageBeenScheduled("stage3"), Matchers.is(false));
    }
}

