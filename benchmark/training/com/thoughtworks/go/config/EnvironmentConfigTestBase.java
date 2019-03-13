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
package com.thoughtworks.go.config;


import BasicEnvironmentConfig.AGENTS_FIELD;
import BasicEnvironmentConfig.PIPELINES_FIELD;
import BasicEnvironmentConfig.VARIABLES_FIELD;
import EnvironmentVariableContext.GO_ENVIRONMENT_NAME;
import com.rits.cloning.Cloner;
import com.thoughtworks.go.domain.EnvironmentPipelineMatcher;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class EnvironmentConfigTestBase {
    public EnvironmentConfig environmentConfig;

    private static final String AGENT_UUID = "uuid";

    @Test
    public void shouldReturnTrueWhenIsEmpty() {
        Assert.assertThat(environmentConfig.isEnvironmentEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseThatNotEmptyWhenHasPipeline() {
        environmentConfig.addPipeline(new CaseInsensitiveString("pipe"));
        Assert.assertThat(environmentConfig.isEnvironmentEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseThatNotEmptyWhenHasAgent() {
        environmentConfig.addAgent("agent");
        Assert.assertThat(environmentConfig.isEnvironmentEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseThatNotEmptyWhenHasVariable() {
        environmentConfig.addEnvironmentVariable("k", "v");
        Assert.assertThat(environmentConfig.isEnvironmentEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldCreateMatcherWhenNoPipelines() throws Exception {
        EnvironmentPipelineMatcher pipelineMatcher = environmentConfig.createMatcher();
        Assert.assertThat(pipelineMatcher.match("pipeline", EnvironmentConfigTestBase.AGENT_UUID), Matchers.is(false));
    }

    @Test
    public void shouldCreateMatcherWhenPipelinesGiven() throws Exception {
        environmentConfig.addPipeline(new CaseInsensitiveString("pipeline"));
        environmentConfig.addAgent(EnvironmentConfigTestBase.AGENT_UUID);
        EnvironmentPipelineMatcher pipelineMatcher = environmentConfig.createMatcher();
        Assert.assertThat(pipelineMatcher.match("pipeline", EnvironmentConfigTestBase.AGENT_UUID), Matchers.is(true));
    }

    @Test
    public void shouldRemoveAgentFromEnvironment() throws Exception {
        environmentConfig.addAgent("uuid1");
        environmentConfig.addAgent("uuid2");
        Assert.assertThat(environmentConfig.getAgents().size(), Matchers.is(2));
        Assert.assertThat(environmentConfig.hasAgent("uuid1"), Matchers.is(true));
        Assert.assertThat(environmentConfig.hasAgent("uuid2"), Matchers.is(true));
        environmentConfig.removeAgent("uuid1");
        Assert.assertThat(environmentConfig.getAgents().size(), Matchers.is(1));
        Assert.assertThat(environmentConfig.hasAgent("uuid1"), Matchers.is(false));
        Assert.assertThat(environmentConfig.hasAgent("uuid2"), Matchers.is(true));
    }

    @Test
    public void shouldAddAgentToEnvironmentIfNotPresent() throws Exception {
        environmentConfig.addAgent("uuid");
        environmentConfig.addAgentIfNew("uuid");
        environmentConfig.addAgentIfNew("uuid1");
        Assert.assertThat(environmentConfig.getAgents().size(), Matchers.is(2));
        Assert.assertThat(environmentConfig.hasAgent("uuid"), Matchers.is(true));
        Assert.assertThat(environmentConfig.hasAgent("uuid1"), Matchers.is(true));
    }

    @Test
    public void twoEnvironmentConfigsShouldBeEqualIfNameIsEqual() throws Exception {
        EnvironmentConfig another = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        Assert.assertThat(another, Matchers.is(environmentConfig));
    }

    @Test
    public void twoEnvironmentConfigsShouldNotBeEqualIfnameNotEqual() throws Exception {
        EnvironmentConfig another = new BasicEnvironmentConfig(new CaseInsensitiveString("other"));
        Assert.assertThat(another, Matchers.is(Matchers.not(environmentConfig)));
    }

    @Test
    public void shouldAddEnvironmentVariablesToEnvironmentVariableContext() throws Exception {
        EnvironmentConfig another = new BasicEnvironmentConfig(new CaseInsensitiveString("other"));
        another.addEnvironmentVariable("variable-name", "variable-value");
        EnvironmentVariableContext context = another.createEnvironmentContext();
        Assert.assertThat(context.getProperty("variable-name"), Matchers.is("variable-value"));
    }

    @Test
    public void shouldAddEnvironmentNameToEnvironmentVariableContext() throws Exception {
        EnvironmentConfig another = new BasicEnvironmentConfig(new CaseInsensitiveString("other"));
        EnvironmentVariableContext context = another.createEnvironmentContext();
        Assert.assertThat(context.getProperty(GO_ENVIRONMENT_NAME), Matchers.is("other"));
    }

    @Test
    public void shouldReturnPipelineNamesContainedInIt() throws Exception {
        environmentConfig.addPipeline(new CaseInsensitiveString("deployment"));
        environmentConfig.addPipeline(new CaseInsensitiveString("testing"));
        List<CaseInsensitiveString> pipelineNames = environmentConfig.getPipelineNames();
        Assert.assertThat(pipelineNames.size(), Matchers.is(2));
        Assert.assertThat(pipelineNames, Matchers.hasItem(new CaseInsensitiveString("deployment")));
        Assert.assertThat(pipelineNames, Matchers.hasItem(new CaseInsensitiveString("testing")));
    }

    @Test
    public void shouldUpdatePipelines() {
        environmentConfig.addPipeline(new CaseInsensitiveString("baz"));
        environmentConfig.setConfigAttributes(Collections.singletonMap(PIPELINES_FIELD, Arrays.asList(Collections.singletonMap("name", "foo"), Collections.singletonMap("name", "bar"))));
        Assert.assertThat(environmentConfig.getPipelineNames(), Matchers.is(Arrays.asList(new CaseInsensitiveString("foo"), new CaseInsensitiveString("bar"))));
    }

    @Test
    public void shouldUpdateAgents() {
        environmentConfig.addAgent("uuid-1");
        environmentConfig.setConfigAttributes(Collections.singletonMap(AGENTS_FIELD, Arrays.asList(Collections.singletonMap("uuid", "uuid-2"), Collections.singletonMap("uuid", "uuid-3"))));
        EnvironmentAgentsConfig expectedAgents = new EnvironmentAgentsConfig();
        expectedAgents.add(new EnvironmentAgentConfig("uuid-2"));
        expectedAgents.add(new EnvironmentAgentConfig("uuid-3"));
        Assert.assertThat(environmentConfig.getAgents(), Matchers.is(expectedAgents));
    }

    @Test
    public void shouldUpdateEnvironmentVariables() {
        environmentConfig.addEnvironmentVariable("hello", "world");
        environmentConfig.setConfigAttributes(Collections.singletonMap(VARIABLES_FIELD, Arrays.asList(EnvironmentConfigTestBase.envVar("foo", "bar"), EnvironmentConfigTestBase.envVar("baz", "quux"))));
        Assert.assertThat(environmentConfig.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("foo", "bar")));
        Assert.assertThat(environmentConfig.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("baz", "quux")));
        Assert.assertThat(environmentConfig.getVariables().size(), Matchers.is(2));
    }

    @Test
    public void shouldNotSetEnvironmentVariableFromConfigAttributesIfNameAndValueIsEmpty() {
        environmentConfig.setConfigAttributes(Collections.singletonMap(VARIABLES_FIELD, Arrays.asList(EnvironmentConfigTestBase.envVar("", "anything"), EnvironmentConfigTestBase.envVar("", ""))));
        Assert.assertThat(environmentConfig.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(environmentConfig.getVariables(), Matchers.hasItem(new EnvironmentVariableConfig("", "anything")));
        Assert.assertThat(environmentConfig.getVariables().size(), Matchers.is(1));
    }

    @Test
    public void shouldNotUpdateAnythingForNullAttributes() {
        EnvironmentConfig beforeUpdate = new Cloner().deepClone(environmentConfig);
        environmentConfig.setConfigAttributes(null);
        Assert.assertThat(environmentConfig, Matchers.is(beforeUpdate));
    }
}

