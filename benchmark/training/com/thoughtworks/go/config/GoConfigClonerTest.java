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


import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.helper.PipelineTemplateConfigMother;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoConfigClonerTest {
    @Test
    public void shouldNotCloneAllPipelineConfigs() {
        BasicCruiseConfig config = GoConfigMother.configWithPipelines("p1", "p2");
        // to prime cache
        config.getAllPipelineConfigs();
        // change state
        config.findGroup("defaultGroup").remove(0);
        BasicCruiseConfig cloned = new GoConfigCloner().deepClone(config);
        Assert.assertThat(ReflectionUtil.getField(config, "allPipelineConfigs"), Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(ReflectionUtil.getField(cloned, "allPipelineConfigs"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(cloned.getAllPipelineConfigs().size(), Matchers.is(1));
    }

    @Test
    public void shouldNotCloneAllTemplatesWithAssociatedPipelines() {
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template-1");
        BasicCruiseConfig config = GoConfigMother.defaultCruiseConfig();
        config.addTemplate(template);
        config.addPipelineWithoutValidation("g1", PipelineConfigMother.pipelineConfigWithTemplate("p1", template.name().toString()));
        // to prime cache
        config.templatesWithAssociatedPipelines();
        // change state
        config.findGroup("g1").remove(0);
        config.getTemplates().removeTemplateNamed(template.name());
        BasicCruiseConfig cloned = new GoConfigCloner().deepClone(config);
        Assert.assertThat(ReflectionUtil.getField(config, "allTemplatesWithAssociatedPipelines"), Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(ReflectionUtil.getField(cloned, "allTemplatesWithAssociatedPipelines"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(cloned.templatesWithAssociatedPipelines().size(), Matchers.is(0));
    }

    @Test
    public void shouldNotClonePipelineNameToConfigMap() {
        BasicCruiseConfig config = GoConfigMother.configWithPipelines("p1", "p2");
        // to prime cache
        config.pipelineConfigByName(new CaseInsensitiveString("p1"));
        // change state
        config.findGroup("defaultGroup").remove(0);
        BasicCruiseConfig cloned = new GoConfigCloner().deepClone(config);
        Assert.assertThat(ReflectionUtil.getField(config, "pipelineNameToConfigMap"), Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(ReflectionUtil.getField(cloned, "pipelineNameToConfigMap"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(cloned.pipelineConfigsAsMap().size(), Matchers.is(1));
    }

    @Test
    public void shouldDeepCloneObject() {
        BasicCruiseConfig config = GoConfigMother.configWithPipelines("p1", "p2");
        BasicCruiseConfig cloned = new GoConfigCloner().deepClone(config);
        Assert.assertThat(cloned.getGroups().size(), Matchers.is(1));
        Assert.assertThat(cloned.getGroups().get(0).getPipelines().size(), Matchers.is(2));
    }
}

