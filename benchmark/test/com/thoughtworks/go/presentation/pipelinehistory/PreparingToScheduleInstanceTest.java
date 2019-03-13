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


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PreparingToScheduleInstanceTest {
    @Test
    public void shouldUnderstandPipelineStatusMessage() throws Exception {
        StageInstanceModels stages = new StageInstanceModels();
        stages.addFutureStage("unit1", false);
        stages.addFutureStage("unit2", false);
        PipelineInstanceModel pipeline = PipelineInstanceModel.createPreparingToSchedule("pipeline-name", stages);
        Assert.assertThat(pipeline.getPipelineStatusMessage(), Matchers.is("Preparing to schedule (0/2)"));
    }

    @Test
    public void shouldNotReturnNullForScheduledDate() throws Exception {
        PipelineInstanceModel pipeline = PipelineInstanceModel.createPreparingToSchedule("pipeline-name", new StageInstanceModels());
        Assert.assertThat(pipeline.getScheduledDate(), Matchers.is(Matchers.not(Matchers.nullValue())));
    }
}

