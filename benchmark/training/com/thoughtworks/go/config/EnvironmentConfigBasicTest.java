/**
 * Copyright 2015 ThoughtWorks, Inc.
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
 */
package com.thoughtworks.go.config;


import BasicEnvironmentConfig.NAME_FIELD;
import EnvironmentPipelineConfig.ORIGIN;
import com.thoughtworks.go.config.remote.ConfigOrigin;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.config.remote.UIConfigOrigin;
import com.thoughtworks.go.helper.GoConfigMother;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EnvironmentConfigBasicTest extends EnvironmentConfigTestBase {
    @Test
    public void shouldReturnEmptyForRemotePipelinesWhenIsLocal() {
        environmentConfig.addPipeline(new CaseInsensitiveString("pipe"));
        Assert.assertThat(environmentConfig.getRemotePipelines().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnAllPipelinesForRemotePipelinesWhenIsRemote() {
        environmentConfig.setOrigins(new RepoConfigOrigin());
        environmentConfig.addPipeline(new CaseInsensitiveString("pipe"));
        Assert.assertThat(environmentConfig.getRemotePipelines().isEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueThatLocalWhenOriginIsNotSet() {
        environmentConfig.setOrigins(null);
        Assert.assertThat(environmentConfig.isLocal(), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueThatLocalWhenOriginIsFile() {
        environmentConfig.setOrigins(new FileConfigOrigin());
        Assert.assertThat(environmentConfig.isLocal(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseThatLocalWhenOriginIsConfigRepo() {
        environmentConfig.setOrigins(new RepoConfigOrigin());
        Assert.assertThat(environmentConfig.isLocal(), Matchers.is(false));
    }

    @Test
    public void shouldReturnSelfAsLocalPartWhenOriginIsFile() {
        environmentConfig.setOrigins(new FileConfigOrigin());
        Assert.assertSame(environmentConfig, environmentConfig.getLocal());
    }

    @Test
    public void shouldReturnSelfAsLocalPartWhenOriginIsUI() {
        environmentConfig.setOrigins(new UIConfigOrigin());
        Assert.assertSame(environmentConfig, environmentConfig.getLocal());
    }

    @Test
    public void shouldReturnNullAsLocalPartWhenOriginIsConfigRepo() {
        environmentConfig.setOrigins(new RepoConfigOrigin());
        Assert.assertNull(environmentConfig.getLocal());
    }

    @Test
    public void shouldUpdateName() {
        environmentConfig.setConfigAttributes(Collections.singletonMap(NAME_FIELD, "PROD"));
        Assert.assertThat(environmentConfig.name(), Matchers.is(new CaseInsensitiveString("PROD")));
    }

    @Test
    public void validate_shouldNotAllowToReferencePipelineDefinedInConfigRepo_WhenEnvironmentDefinedInFile() {
        ConfigOrigin pipelineOrigin = new RepoConfigOrigin();
        ConfigOrigin envOrigin = new FileConfigOrigin();
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("pipe1");
        cruiseConfig.getPipelineConfigByName(new CaseInsensitiveString("pipe1")).setOrigin(pipelineOrigin);
        BasicEnvironmentConfig environmentConfig = ((BasicEnvironmentConfig) (this.environmentConfig));
        environmentConfig.setOrigins(envOrigin);
        environmentConfig.addPipeline(new CaseInsensitiveString("pipe1"));
        cruiseConfig.addEnvironment(environmentConfig);
        environmentConfig.validate(ConfigSaveValidationContext.forChain(cruiseConfig, environmentConfig));
        EnvironmentPipelineConfig reference = environmentConfig.getPipelines().first();
        Assert.assertThat(reference.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(reference.errors().on(ORIGIN), Matchers.startsWith("Environment defined in"));
    }

    @Test
    public void validate_shouldAllowToReferencePipelineDefinedInConfigRepo_WhenEnvironmentDefinedInConfigRepo() {
        ConfigOrigin pipelineOrigin = new RepoConfigOrigin();
        ConfigOrigin envOrigin = new RepoConfigOrigin();
        passReferenceValidationHelper(pipelineOrigin, envOrigin);
    }

    @Test
    public void validate_shouldAllowToReferencePipelineDefinedInFile_WhenEnvironmentDefinedInFile() {
        ConfigOrigin pipelineOrigin = new FileConfigOrigin();
        ConfigOrigin envOrigin = new FileConfigOrigin();
        passReferenceValidationHelper(pipelineOrigin, envOrigin);
    }

    @Test
    public void validate_shouldAllowToReferencePipelineDefinedInFile_WhenEnvironmentDefinedInConfigRepo() {
        ConfigOrigin pipelineOrigin = new FileConfigOrigin();
        ConfigOrigin envOrigin = new RepoConfigOrigin();
        passReferenceValidationHelper(pipelineOrigin, envOrigin);
    }

    @Test
    public void shouldValidateWhenPipelineNotFound() {
        ConfigOrigin pipelineOrigin = new RepoConfigOrigin();
        ConfigOrigin envOrigin = new FileConfigOrigin();
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("pipe1");
        cruiseConfig.getPipelineConfigByName(new CaseInsensitiveString("pipe1")).setOrigin(pipelineOrigin);
        BasicEnvironmentConfig environmentConfig = ((BasicEnvironmentConfig) (this.environmentConfig));
        environmentConfig.setOrigins(envOrigin);
        environmentConfig.addPipeline(new CaseInsensitiveString("unknown"));
        cruiseConfig.addEnvironment(environmentConfig);
        environmentConfig.validate(ConfigSaveValidationContext.forChain(cruiseConfig, environmentConfig));
        EnvironmentPipelineConfig reference = environmentConfig.getPipelines().first();
        Assert.assertThat(reference.errors().isEmpty(), Matchers.is(true));
    }
}

