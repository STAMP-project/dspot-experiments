/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.merge;


import BasicEnvironmentConfig.VARIABLES_FIELD;
import MergeEnvironmentConfig.CONSISTENT_KV;
import com.thoughtworks.go.config.BasicEnvironmentConfig;
import com.thoughtworks.go.config.EnvironmentConfig;
import com.thoughtworks.go.config.EnvironmentConfigTestBase;
import com.thoughtworks.go.config.MergeEnvironmentConfig;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MergeEnvironmentConfigTest extends EnvironmentConfigTestBase {
    public MergeEnvironmentConfig singleEnvironmentConfig;

    public MergeEnvironmentConfig pairEnvironmentConfig;

    private static final String AGENT_UUID = "uuid";

    private EnvironmentConfig localUatEnv1;

    private EnvironmentConfig uatLocalPart2;

    private BasicEnvironmentConfig uatRemotePart;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowPartsWithDifferentNames() {
        new MergeEnvironmentConfig(new BasicEnvironmentConfig(new CaseInsensitiveString("UAT")), new BasicEnvironmentConfig(new CaseInsensitiveString("Two")));
    }

    @Test
    public void ShouldContainSameNameAsOfPartialEnvironments() throws Exception {
        BasicEnvironmentConfig local = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        BasicEnvironmentConfig remote = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        MergeEnvironmentConfig mergeEnv = new MergeEnvironmentConfig(local, remote);
        Assert.assertThat(mergeEnv.name(), Matchers.is(local.name()));
    }

    @Test
    public void getRemotePipelines_shouldReturnEmptyWhenOnlyLocalPartHasPipelines() {
        uatLocalPart2.addPipeline(new CaseInsensitiveString("pipe"));
        Assert.assertThat(pairEnvironmentConfig.getRemotePipelines().isEmpty(), Matchers.is(true));
    }

    @Test
    public void getRemotePipelines_shouldReturnPipelinesFromRemotePartWhenRemoteHasPipesAssigned() {
        uatRemotePart.addPipeline(new CaseInsensitiveString("pipe"));
        Assert.assertThat(environmentConfig.getRemotePipelines().isEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseThatLocal() {
        Assert.assertThat(environmentConfig.isLocal(), Matchers.is(false));
    }

    @Test
    public void shouldGetLocalPartWhenOriginFile() {
        Assert.assertThat(environmentConfig.getLocal(), Matchers.is(uatLocalPart2));
    }

    @Test
    public void hasSamePipelinesAs_shouldReturnTrueWhenAnyPipelineNameIsEqualToOther() {
        pairEnvironmentConfig.get(0).addPipeline(new CaseInsensitiveString("pipe1"));
        pairEnvironmentConfig.get(1).addPipeline(new CaseInsensitiveString("pipe2"));
        BasicEnvironmentConfig config = new BasicEnvironmentConfig();
        config.addPipeline(new CaseInsensitiveString("pipe2"));
        Assert.assertThat(pairEnvironmentConfig.hasSamePipelinesAs(config), Matchers.is(true));
    }

    @Test
    public void hasSamePipelinesAs_shouldReturnFalseWhenNoneOfOtherPipelinesIsEqualToOther() {
        pairEnvironmentConfig.get(0).addPipeline(new CaseInsensitiveString("pipe1"));
        pairEnvironmentConfig.get(1).addPipeline(new CaseInsensitiveString("pipe2"));
        BasicEnvironmentConfig config = new BasicEnvironmentConfig();
        config.addPipeline(new CaseInsensitiveString("pipe3"));
        Assert.assertThat(pairEnvironmentConfig.hasSamePipelinesAs(config), Matchers.is(false));
    }

    // merges
    @Test
    public void shouldReturnPipelineNamesFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addPipeline(new CaseInsensitiveString("deployment"));
        pairEnvironmentConfig.get(1).addPipeline(new CaseInsensitiveString("testing"));
        List<CaseInsensitiveString> pipelineNames = pairEnvironmentConfig.getPipelineNames();
        Assert.assertThat(pipelineNames.size(), Matchers.is(2));
        Assert.assertThat(pipelineNames, Matchers.hasItem(new CaseInsensitiveString("deployment")));
        Assert.assertThat(pipelineNames, Matchers.hasItem(new CaseInsensitiveString("testing")));
    }

    @Test
    public void shouldNotRepeatPipelineNamesFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addPipeline(new CaseInsensitiveString("deployment"));
        pairEnvironmentConfig.get(1).addPipeline(new CaseInsensitiveString("deployment"));
        List<CaseInsensitiveString> pipelineNames = pairEnvironmentConfig.getPipelineNames();
        Assert.assertThat(pipelineNames, Matchers.hasItem(new CaseInsensitiveString("deployment")));
    }

    @Test
    public void shouldDeduplicateRepeatedPipelinesFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addPipeline(new CaseInsensitiveString("deployment"));
        pairEnvironmentConfig.get(1).addPipeline(new CaseInsensitiveString("deployment"));
        List<CaseInsensitiveString> pipelineNames = pairEnvironmentConfig.getPipelineNames();
        Assert.assertThat(pipelineNames.size(), Matchers.is(1));
        Assert.assertTrue(pairEnvironmentConfig.containsPipeline(new CaseInsensitiveString("deployment")));
    }

    @Test
    public void shouldHaveAgentsFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addAgent("123");
        pairEnvironmentConfig.get(1).addAgent("345");
        EnvironmentAgentsConfig agents = pairEnvironmentConfig.getAgents();
        Assert.assertTrue(pairEnvironmentConfig.hasAgent("123"));
        Assert.assertTrue(pairEnvironmentConfig.hasAgent("345"));
    }

    @Test
    public void shouldReturnAgentsUuidsFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addAgent("123");
        pairEnvironmentConfig.get(1).addAgent("345");
        EnvironmentAgentsConfig agents = pairEnvironmentConfig.getAgents();
        Assert.assertThat(agents.size(), Matchers.is(2));
        Assert.assertThat(agents.getUuids(), Matchers.hasItem("123"));
        Assert.assertThat(agents.getUuids(), Matchers.hasItem("345"));
    }

    @Test
    public void shouldDeduplicateRepeatedAgentsFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addAgent("123");
        pairEnvironmentConfig.get(1).addAgent("123");
        EnvironmentAgentsConfig agents = pairEnvironmentConfig.getAgents();
        Assert.assertThat(agents.size(), Matchers.is(1));
        Assert.assertThat(agents.getUuids(), Matchers.hasItem("123"));
    }

    @Test
    public void shouldHaveVariablesFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addEnvironmentVariable("variable-name1", "variable-value1");
        pairEnvironmentConfig.get(1).addEnvironmentVariable("variable-name2", "variable-value2");
        Assert.assertTrue(pairEnvironmentConfig.hasVariable("variable-name1"));
        Assert.assertTrue(pairEnvironmentConfig.hasVariable("variable-name2"));
    }

    @Test
    public void shouldAddEnvironmentVariablesToEnvironmentVariableContextFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addEnvironmentVariable("variable-name1", "variable-value1");
        pairEnvironmentConfig.get(1).addEnvironmentVariable("variable-name2", "variable-value2");
        EnvironmentVariableContext context = pairEnvironmentConfig.createEnvironmentContext();
        Assert.assertThat(context.getProperty("variable-name1"), Matchers.is("variable-value1"));
        Assert.assertThat(context.getProperty("variable-name2"), Matchers.is("variable-value2"));
    }

    @Test
    public void shouldAddDeduplicatedEnvironmentVariablesToEnvironmentVariableContextFrom2Parts() throws Exception {
        pairEnvironmentConfig.get(0).addEnvironmentVariable("variable-name1", "variable-value1");
        pairEnvironmentConfig.get(1).addEnvironmentVariable("variable-name1", "variable-value1");
        Assert.assertThat(pairEnvironmentConfig.getVariables().size(), Matchers.is(1));
        EnvironmentVariableContext context = pairEnvironmentConfig.createEnvironmentContext();
        Assert.assertThat(context.getProperty("variable-name1"), Matchers.is("variable-value1"));
    }

    @Test
    public void shouldCreateErrorsForInconsistentEnvironmentVariables() throws Exception {
        pairEnvironmentConfig.get(0).addEnvironmentVariable("variable-name1", "variable-value1");
        pairEnvironmentConfig.get(1).addEnvironmentVariable("variable-name1", "variable-value2");
        pairEnvironmentConfig.validate(ConfigSaveValidationContext.forChain(pairEnvironmentConfig));
        Assert.assertThat(pairEnvironmentConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pairEnvironmentConfig.errors().on(CONSISTENT_KV), Matchers.is("Environment variable 'variable-name1' is defined more than once with different values"));
    }

    @Test
    public void shouldValidateDuplicatePipelines() throws Exception {
        pairEnvironmentConfig.get(0).addPipeline(new CaseInsensitiveString("up42"));
        pairEnvironmentConfig.get(1).addPipeline(new CaseInsensitiveString("up42"));
        pairEnvironmentConfig.validate(ConfigSaveValidationContext.forChain(pairEnvironmentConfig));
        Assert.assertThat(pairEnvironmentConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pairEnvironmentConfig.errors().firstError(), Matchers.is("Environment pipeline 'up42' is defined more than once."));
    }

    @Test
    public void shouldValidateDuplicateAgents() throws Exception {
        pairEnvironmentConfig.get(0).addAgent("random-uuid");
        pairEnvironmentConfig.get(1).addAgent("random-uuid");
        pairEnvironmentConfig.validate(ConfigSaveValidationContext.forChain(pairEnvironmentConfig));
        Assert.assertThat(pairEnvironmentConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pairEnvironmentConfig.errors().firstError(), Matchers.is("Environment agent 'random-uuid' is defined more than once."));
    }

    @Test
    public void shouldReturnTrueWhenOnlyPartIsLocal() {
        BasicEnvironmentConfig uatLocalPart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatLocalPart.setOrigins(new FileConfigOrigin());
        environmentConfig = new MergeEnvironmentConfig(uatLocalPart);
        Assert.assertThat(environmentConfig.isLocal(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseWhenPartIsRemote() {
        BasicEnvironmentConfig uatLocalPart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatLocalPart.setOrigins(new FileConfigOrigin());
        BasicEnvironmentConfig uatRemotePart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatRemotePart.setOrigins(new RepoConfigOrigin());
        environmentConfig = new MergeEnvironmentConfig(uatLocalPart, uatRemotePart);
        Assert.assertThat(environmentConfig.isLocal(), Matchers.is(false));
    }

    @Test
    public void shouldUpdateEnvironmentVariablesWhenSourceIsEditable() {
        BasicEnvironmentConfig uatLocalPart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatLocalPart.setOrigins(new FileConfigOrigin());
        BasicEnvironmentConfig uatRemotePart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatRemotePart.setOrigins(new RepoConfigOrigin());
        uatLocalPart.addEnvironmentVariable("hello", "world");
        environmentConfig = new MergeEnvironmentConfig(uatLocalPart, uatRemotePart);
        environmentConfig.setConfigAttributes(Collections.singletonMap(VARIABLES_FIELD, Arrays.asList(EnvironmentConfigTestBase.envVar("foo", "bar"), EnvironmentConfigTestBase.envVar("baz", "quux"), EnvironmentConfigTestBase.envVar("hello", "you"))));
        Assert.assertThat(environmentConfig.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("hello", "you")));
        Assert.assertThat(environmentConfig.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("foo", "bar")));
        Assert.assertThat(environmentConfig.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("baz", "quux")));
        Assert.assertThat(environmentConfig.getVariables().size(), Matchers.is(3));
        Assert.assertThat("ChangesShouldBeInLocalConfig", uatLocalPart.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("hello", "you")));
        Assert.assertThat("ChangesShouldBeInLocalConfig", uatLocalPart.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("foo", "bar")));
        Assert.assertThat("ChangesShouldBeInLocalConfig", uatLocalPart.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("baz", "quux")));
        Assert.assertThat("ChangesShouldBeInLocalConfig", uatLocalPart.getVariables().size(), Matchers.is(3));
    }

    @Test
    public void shouldReturnCorrectOriginOfDefinedPipeline() throws Exception {
        BasicEnvironmentConfig uatLocalPart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatLocalPart.setOrigins(new FileConfigOrigin());
        String localPipeline = "local-pipeline";
        uatLocalPart.addPipeline(new CaseInsensitiveString(localPipeline));
        BasicEnvironmentConfig uatRemotePart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatRemotePart.setOrigins(new RepoConfigOrigin());
        String remotePipeline = "remote-pipeline";
        uatRemotePart.addPipeline(new CaseInsensitiveString(remotePipeline));
        MergeEnvironmentConfig environmentConfig = new MergeEnvironmentConfig(uatLocalPart, uatRemotePart);
        Assert.assertThat(environmentConfig.getOriginForPipeline(new CaseInsensitiveString(localPipeline)), Matchers.is(new FileConfigOrigin()));
        Assert.assertThat(environmentConfig.getOriginForPipeline(new CaseInsensitiveString(remotePipeline)), Matchers.is(new RepoConfigOrigin()));
    }

    @Test
    public void shouldReturnCorrectOriginOfDefinedAgent() throws Exception {
        BasicEnvironmentConfig uatLocalPart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatLocalPart.setOrigins(new FileConfigOrigin());
        String localAgent = "local-agent";
        uatLocalPart.addAgent(localAgent);
        BasicEnvironmentConfig uatRemotePart = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uatRemotePart.setOrigins(new RepoConfigOrigin());
        String remoteAgent = "remote-agent";
        uatRemotePart.addAgent(remoteAgent);
        MergeEnvironmentConfig environmentConfig = new MergeEnvironmentConfig(uatLocalPart, uatRemotePart);
        Assert.assertThat(environmentConfig.getOriginForAgent(localAgent), Matchers.is(new FileConfigOrigin()));
        Assert.assertThat(environmentConfig.getOriginForAgent(remoteAgent), Matchers.is(new RepoConfigOrigin()));
    }
}

