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
package com.thoughtworks.go.config;


import BasicPipelineConfigs.GROUP;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BasicPipelineConfigsTest extends PipelineConfigsTestBase {
    @Test
    public void shouldReturnSelfForGetLocalWhenOriginIsNull() {
        PipelineConfigs pipelineConfigs = createEmpty();
        Assert.assertThat(pipelineConfigs.getLocal().size(), Matchers.is(0));
        TestCase.assertSame(pipelineConfigs, pipelineConfigs.getLocal());
    }

    @Test
    public void shouldReturnSelfForGetLocalPartsWhenOriginIsFile() {
        PipelineConfigs pipelineConfigs = createEmpty();
        pipelineConfigs.setOrigins(new FileConfigOrigin());
        Assert.assertThat(pipelineConfigs.getLocal().size(), Matchers.is(0));
        TestCase.assertSame(pipelineConfigs, pipelineConfigs.getLocal());
    }

    @Test
    public void shouldReturnNullGetLocalPartsWhenOriginIsRepo() {
        PipelineConfigs pipelineConfigs = createEmpty();
        pipelineConfigs.setOrigins(new RepoConfigOrigin());
        TestCase.assertNull(pipelineConfigs.getLocal());
    }

    @Test
    public void shouldSetOriginInPipelines() {
        PipelineConfig pipe = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfigs group = new BasicPipelineConfigs(pipe);
        group.setOrigins(new FileConfigOrigin());
        Assert.assertThat(pipe.getOrigin(), Matchers.is(new FileConfigOrigin()));
    }

    @Test
    public void shouldSetOriginInAuthorization() {
        PipelineConfig pipe = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfigs group = new BasicPipelineConfigs(pipe);
        group.setOrigins(new FileConfigOrigin());
        Assert.assertThat(group.getAuthorization().getOrigin(), Matchers.is(new FileConfigOrigin()));
    }

    @Test
    public void shouldUpdateName() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.setConfigAttributes(DataStructureUtils.m(GROUP, "my-new-group"));
        Assert.assertThat(group.getGroup(), Matchers.is("my-new-group"));
        group.setConfigAttributes(DataStructureUtils.m());
        Assert.assertThat(group.getGroup(), Matchers.is("my-new-group"));
        group.setConfigAttributes(null);
        Assert.assertThat(group.getGroup(), Matchers.is("my-new-group"));
        group.setConfigAttributes(DataStructureUtils.m(GROUP, null));
        Assert.assertThat(group.getGroup(), Matchers.is(Matchers.nullValue()));
    }
}

