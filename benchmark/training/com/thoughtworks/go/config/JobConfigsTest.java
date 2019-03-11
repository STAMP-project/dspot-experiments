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


import JobConfig.NAME;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class JobConfigsTest {
    @Test
    public void shouldAddJobsGivenInTheAttributesMapAfterClearingExistingJobs() throws Exception {
        JobConfigs jobs = new JobConfigs();
        jobs.add(new JobConfig("quux"));
        jobs.setConfigAttributes(DataStructureUtils.a(DataStructureUtils.m(NAME, "foo"), DataStructureUtils.m(NAME, "bar")));
        Assert.assertThat(jobs.get(0).name(), Matchers.is(new CaseInsensitiveString("foo")));
        Assert.assertThat(jobs.get(1).name(), Matchers.is(new CaseInsensitiveString("bar")));
        Assert.assertThat(jobs.size(), Matchers.is(2));
    }

    @Test
    public void shouldNotFailForRepeatedJobNames_shouldInsteedSetErrorsOnValidation() throws Exception {
        CruiseConfig config = new BasicCruiseConfig();
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        config.addPipeline("grp", pipelineConfig);
        JobConfigs jobs = pipelineConfig.get(0).getJobs();
        jobs.add(new JobConfig("quux"));
        jobs.setConfigAttributes(DataStructureUtils.a(DataStructureUtils.m(NAME, "foo"), DataStructureUtils.m(NAME, "foo")));
        Assert.assertThat(jobs.size(), Matchers.is(2));
        JobConfig firstFoo = jobs.get(0);
        JobConfig secondFoo = jobs.get(1);
        Assert.assertThat(firstFoo.name(), Matchers.is(new CaseInsensitiveString("foo")));
        Assert.assertThat(secondFoo.name(), Matchers.is(new CaseInsensitiveString("foo")));
        Assert.assertThat(firstFoo.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(secondFoo.errors().isEmpty(), Matchers.is(true));
        jobs.validate(ConfigSaveValidationContext.forChain(config, config.getGroups(), config.getGroups().get(0), pipelineConfig, pipelineConfig.get(0), jobs));
        Assert.assertThat(firstFoo.errors().on(NAME), Matchers.is("You have defined multiple jobs called 'foo'. Job names are case-insensitive and must be unique."));
        Assert.assertThat(secondFoo.errors().on(NAME), Matchers.is("You have defined multiple jobs called 'foo'. Job names are case-insensitive and must be unique."));
    }

    @Test
    public void shouldValidateTree() throws Exception {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        JobConfigs jobs = pipelineConfig.get(0).getJobs();
        jobs.add(new JobConfig("quux"));
        jobs.setConfigAttributes(DataStructureUtils.a(DataStructureUtils.m(NAME, "foo"), DataStructureUtils.m(NAME, "foo")));
        Assert.assertThat(jobs.size(), Matchers.is(2));
        JobConfig firstFoo = jobs.get(0);
        JobConfig secondFoo = jobs.get(1);
        Assert.assertThat(firstFoo.name(), Matchers.is(new CaseInsensitiveString("foo")));
        Assert.assertThat(secondFoo.name(), Matchers.is(new CaseInsensitiveString("foo")));
        Assert.assertThat(firstFoo.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(secondFoo.errors().isEmpty(), Matchers.is(true));
        jobs.validate(PipelineConfigSaveValidationContext.forChain(true, "group", pipelineConfig, pipelineConfig.get(0), jobs));
        Assert.assertThat(firstFoo.errors().on(NAME), Matchers.is("You have defined multiple jobs called 'foo'. Job names are case-insensitive and must be unique."));
        Assert.assertThat(secondFoo.errors().on(NAME), Matchers.is("You have defined multiple jobs called 'foo'. Job names are case-insensitive and must be unique."));
    }

    @Test
    public void shouldReturnTrueIfAllDescendentsAreValid() {
        JobConfig jobConfig = Mockito.mock(JobConfig.class);
        Mockito.when(jobConfig.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        JobConfigs jobConfigs = new JobConfigs(jobConfig);
        boolean isValid = jobConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()));
        assertTrue(isValid);
        Mockito.verify(jobConfig).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
    }

    @Test
    public void shouldReturnFalseIfAnyDescendentIsInvalid() {
        JobConfig jobConfig = Mockito.mock(JobConfig.class);
        Mockito.when(jobConfig.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        JobConfigs jobConfigs = new JobConfigs(jobConfig);
        boolean isValid = jobConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()));
        assertFalse(isValid);
        Mockito.verify(jobConfig).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
    }

    @Test
    public void shouldClearExistingJobsWhenNullGivenAsAttributeMap() throws Exception {
        JobConfigs jobs = new JobConfigs();
        jobs.add(new JobConfig("quux"));
        jobs.setConfigAttributes(null);
        Assert.assertThat(jobs.size(), Matchers.is(0));
    }

    @Test
    public void shouldGetJobConfigByJobName() {
        JobConfigs configs = new JobConfigs();
        JobConfig expected = new JobConfig("job-1");
        configs.add(expected);
        configs.add(new JobConfig("job-2"));
        JobConfig actual = configs.getJob(new CaseInsensitiveString("job-1"));
        Assert.assertThat(actual, Matchers.is(expected));
        Assert.assertThat(configs.getJob(new CaseInsensitiveString("some-junk")), Matchers.is(Matchers.nullValue()));
    }
}

