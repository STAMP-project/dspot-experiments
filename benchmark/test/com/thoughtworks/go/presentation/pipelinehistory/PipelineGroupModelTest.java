/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2017 ThoughtWorks, Inc.
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


import com.thoughtworks.go.domain.PipelinePauseInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineGroupModelTest {
    @Test
    public void shouldSayContainsPipelineIrrespectiveOfPipelineNameCase() {
        PipelineGroupModel groupModel = new PipelineGroupModel("group");
        groupModel.add(pipelineModel("pipeline"));
        Assert.assertThat(groupModel.containsPipeline("PIPELINE"), Matchers.is(true));
    }

    @Test
    public void shouldCopyAllInternalsOfPipelineModelWhenCreatingANewOneIfNeeded() throws Exception {
        PipelineGroupModel groupModel = new PipelineGroupModel("group");
        PipelineModel expectedModel = addInstanceTo(new PipelineModel("p1", true, true, PipelinePauseInfo.notPaused()));
        expectedModel.updateAdministrability(true);
        groupModel.add(expectedModel);
        PipelineModel actualModel = groupModel.getPipelineModel("p1");
        String message = String.format("\nExpected: %s\nActual:   %s", ToStringBuilder.reflectionToString(expectedModel), ToStringBuilder.reflectionToString(actualModel));
        Assert.assertThat(message, EqualsBuilder.reflectionEquals(actualModel, expectedModel), Matchers.is(true));
    }
}

