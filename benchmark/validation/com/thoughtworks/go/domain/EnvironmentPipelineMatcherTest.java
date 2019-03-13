/**
 * Copyright 2016 ThoughtWorks, Inc.
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


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.EnvironmentPipelinesConfig;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EnvironmentPipelineMatcherTest {
    @Test
    public void shouldMatchPipelineNameIgnoringCase() {
        EnvironmentPipelineMatcher matcher = new EnvironmentPipelineMatcher(new CaseInsensitiveString("env"), Arrays.asList("uuid1", "uuid2"), new EnvironmentPipelinesConfig() {
            {
                add(new com.thoughtworks.go.config.EnvironmentPipelineConfig(new CaseInsensitiveString("pipeline1")));
                add(new com.thoughtworks.go.config.EnvironmentPipelineConfig(new CaseInsensitiveString("pipeline2")));
            }
        });
        Assert.assertThat(matcher.hasPipeline(jobPlan("PipeLine1").getPipelineName()), Matchers.is(true));
        Assert.assertThat(matcher.hasPipeline(jobPlan("PIPELINE1").getPipelineName()), Matchers.is(true));
        Assert.assertThat(matcher.hasPipeline(jobPlan("PIPELine1").getPipelineName()), Matchers.is(true));
        Assert.assertThat(matcher.hasPipeline(jobPlan("PipeLine2").getPipelineName()), Matchers.is(true));
    }
}

