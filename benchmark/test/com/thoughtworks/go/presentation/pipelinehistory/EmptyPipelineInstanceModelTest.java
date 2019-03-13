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


import PipelineInstanceModel.UNKNOWN_REVISION;
import com.thoughtworks.go.domain.PipelineIdentifier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EmptyPipelineInstanceModelTest {
    private EmptyPipelineInstanceModel instanceModel;

    @Test
    public void shouldAdvertiseAsUnrealPipeline() {
        Assert.assertThat(instanceModel.hasHistoricalData(), Matchers.is(false));
    }

    @Test
    public void shouldReturnUnknownModificationAsCurrent() {
        Assert.assertThat(instanceModel.getCurrentRevision("foo"), Matchers.is(UNKNOWN_REVISION));
    }

    @Test
    public void shouldBeCapableOfGeneratingPipelineIdentifier() {
        Assert.assertThat(instanceModel.getPipelineIdentifier(), Matchers.is(new PipelineIdentifier("pipeline", 0, "unknown")));
    }

    @Test
    public void shouldHaveNegetivePipelineId() {
        Assert.assertThat(instanceModel.getId(), Matchers.is((-1L)));
    }
}

