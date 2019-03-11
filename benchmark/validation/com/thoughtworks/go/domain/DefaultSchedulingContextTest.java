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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.helper.StageConfigMother;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultSchedulingContextTest {
    @Test
    public void shouldFindNoAgentsIfNoneExist() throws Exception {
        DefaultSchedulingContext context = new DefaultSchedulingContext("approved", new Agents());
        Assert.assertThat(context.findAgentsMatching(new ResourceConfigs()), Matchers.is(new Agents()));
    }

    @Test
    public void shouldFindAllAgentsIfNoResourcesAreSpecified() throws Exception {
        AgentConfig linux = agent("uuid1", "linux");
        AgentConfig windows = agent("uuid2", "windows");
        Agents matchingAgents = new Agents(linux, windows);
        DefaultSchedulingContext context = new DefaultSchedulingContext("approved", matchingAgents);
        Assert.assertThat(context.findAgentsMatching(new ResourceConfigs()), Matchers.is(matchingAgents));
    }

    @Test
    public void shouldOnlyFindAgentsThatMatchResourcesSpecified() throws Exception {
        AgentConfig linux = agent("uuid1", "linux");
        AgentConfig windows = agent("uuid2", "windows");
        Agents matchingAgents = new Agents(linux, windows);
        DefaultSchedulingContext context = new DefaultSchedulingContext("approved", matchingAgents);
        Assert.assertThat(context.findAgentsMatching(DefaultSchedulingContextTest.resources("linux")), Matchers.is(new Agents(linux)));
    }

    @Test
    public void shouldFindNoAgentsIfNoneMatch() throws Exception {
        AgentConfig linux = agent("uuid1", "linux");
        AgentConfig windows = agent("uuid2", "windows");
        Agents matchingAgents = new Agents(linux, windows);
        DefaultSchedulingContext context = new DefaultSchedulingContext("approved", matchingAgents);
        Assert.assertThat(context.findAgentsMatching(DefaultSchedulingContextTest.resources("macosx")), Matchers.is(new Agents()));
    }

    @Test
    public void shouldNotMatchDeniedAgents() throws Exception {
        AgentConfig linux = agent("uuid1", "linux");
        AgentConfig windows = agent("uuid2", "windows");
        windows.disable();
        Agents matchingAgents = new Agents(linux, windows);
        DefaultSchedulingContext context = new DefaultSchedulingContext("approved", matchingAgents);
        Assert.assertThat(context.findAgentsMatching(DefaultSchedulingContextTest.resources()), Matchers.is(new Agents(linux)));
    }

    @Test
    public void shouldSetEnvironmentVariablesOnSchedulingContext() throws Exception {
        EnvironmentVariablesConfig existing = new EnvironmentVariablesConfig();
        existing.add("firstVar", "firstVal");
        existing.add("overriddenVar", "originalVal");
        SchedulingContext schedulingContext = new DefaultSchedulingContext();
        schedulingContext = schedulingContext.overrideEnvironmentVariables(existing);
        EnvironmentVariablesConfig stageLevel = new EnvironmentVariablesConfig();
        stageLevel.add("stageVar", "stageVal");
        stageLevel.add("overriddenVar", "overriddenVal");
        StageConfig config = StageConfigMother.custom("test", Approval.automaticApproval());
        config.setVariables(stageLevel);
        ReflectionUtil.setField(schedulingContext, "rerun", true);
        SchedulingContext context = schedulingContext.overrideEnvironmentVariables(config.getVariables());
        Assert.assertThat(context.isRerun(), Matchers.is(true));
        EnvironmentVariablesConfig environmentVariablesUsed = context.getEnvironmentVariablesConfig();
        Assert.assertThat(environmentVariablesUsed.size(), Matchers.is(3));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("firstVar", "firstVal")));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("overriddenVar", "overriddenVal")));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("stageVar", "stageVal")));
    }

    @Test
    public void shouldCreatePermittedAgentContextCorrectly() throws Exception {
        AgentConfig linux = agent("uuid1", "linux");
        AgentConfig windows = agent("uuid2", "windows");
        windows.disable();
        Agents matchingAgents = new Agents(linux, windows);
        EnvironmentVariablesConfig existing = new EnvironmentVariablesConfig();
        existing.add("firstVar", "firstVal");
        existing.add("overriddenVar", "originalVal");
        SchedulingContext schedulingContext = new DefaultSchedulingContext("approver", matchingAgents);
        schedulingContext = schedulingContext.overrideEnvironmentVariables(existing);
        EnvironmentVariablesConfig stageLevel = new EnvironmentVariablesConfig();
        stageLevel.add("stageVar", "stageVal");
        stageLevel.add("overriddenVar", "overriddenVal");
        StageConfig config = StageConfigMother.custom("test", Approval.automaticApproval());
        config.setVariables(stageLevel);
        SchedulingContext context = schedulingContext.overrideEnvironmentVariables(config.getVariables());
        ReflectionUtil.setField(context, "rerun", true);
        SchedulingContext permittedAgentContext = context.permittedAgent("uuid1");
        Agents agents = ((Agents) (ReflectionUtil.getField(permittedAgentContext, "agents")));
        Assert.assertThat(agents.size(), Matchers.is(1));
        Assert.assertThat(agents.get(0).getAgentIdentifier().getUuid(), Matchers.is("uuid1"));
        Assert.assertThat(permittedAgentContext.isRerun(), Matchers.is(true));
        Assert.assertThat(permittedAgentContext.getApprovedBy(), Matchers.is("approver"));
        EnvironmentVariablesConfig environmentVariablesUsed = permittedAgentContext.getEnvironmentVariablesConfig();
        Assert.assertThat(environmentVariablesUsed.size(), Matchers.is(3));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("firstVar", "firstVal")));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("overriddenVar", "overriddenVal")));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("stageVar", "stageVal")));
    }

    @Test
    public void shouldCreateRerunSchedulingContextCorrectly() throws Exception {
        AgentConfig linux = agent("uuid1", "linux");
        AgentConfig windows = agent("uuid2", "windows");
        windows.disable();
        Agents matchingAgents = new Agents(linux, windows);
        EnvironmentVariablesConfig existing = new EnvironmentVariablesConfig();
        existing.add("firstVar", "firstVal");
        existing.add("overriddenVar", "originalVal");
        SchedulingContext schedulingContext = new DefaultSchedulingContext("approver", matchingAgents);
        schedulingContext = schedulingContext.overrideEnvironmentVariables(existing);
        EnvironmentVariablesConfig stageLevel = new EnvironmentVariablesConfig();
        stageLevel.add("stageVar", "stageVal");
        stageLevel.add("overriddenVar", "overriddenVal");
        StageConfig config = StageConfigMother.custom("test", Approval.automaticApproval());
        config.setVariables(stageLevel);
        SchedulingContext context = schedulingContext.overrideEnvironmentVariables(config.getVariables());
        SchedulingContext rerunContext = context.rerunContext();
        Assert.assertThat(rerunContext.isRerun(), Matchers.is(true));
        Assert.assertThat(rerunContext.getApprovedBy(), Matchers.is("approver"));
        Agents agents = ((Agents) (ReflectionUtil.getField(rerunContext, "agents")));
        Assert.assertThat(agents, Matchers.is(matchingAgents));
        EnvironmentVariablesConfig environmentVariablesUsed = rerunContext.getEnvironmentVariablesConfig();
        Assert.assertThat(environmentVariablesUsed.size(), Matchers.is(3));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("firstVar", "firstVal")));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("overriddenVar", "overriddenVal")));
        Assert.assertThat(environmentVariablesUsed, Matchers.hasItem(new EnvironmentVariableConfig("stageVar", "stageVal")));
    }
}

