/**
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain;


import AgentConfig.IP_ADDRESS;
import AgentConfig.UUID;
import JobConfig.RESOURCES;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.util.TestUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AgentConfigTest {
    @Test
    public void agentWithNoIpAddressShouldBeValid() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        AgentConfig agent = new AgentConfig("uuid", null, null);
        cruiseConfig.agents().add(agent);
        Assert.assertThat(cruiseConfig.validateAfterPreprocess().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldValidateIpCorrectIPv4Address() throws Exception {
        shouldBeValid("127.0.0.1");
        shouldBeValid("0.0.0.0");
        shouldBeValid("255.255.0.0");
        shouldBeValid("0:0:0:0:0:0:0:1");
    }

    @Test
    public void shouldValidateIpCorrectIPv6Address() throws Exception {
        shouldBeValid("0:0:0:0:0:0:0:1");
    }

    @Test
    public void shouldFailValidationIfIPAddressIsAString() throws Exception {
        AgentConfig agentConfig = new AgentConfig("uuid", "host", "blahinvalid");
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(IP_ADDRESS), Matchers.is("'blahinvalid' is an invalid IP address."));
        agentConfig = new AgentConfig("uuid", "host", "blah.invalid");
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(IP_ADDRESS), Matchers.is("'blah.invalid' is an invalid IP address."));
    }

    @Test
    public void shouldFailValidationIfIPAddressHasAnIncorrectValue() throws Exception {
        AgentConfig agentConfig = new AgentConfig("uuid", "host", "399.0.0.1");
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(IP_ADDRESS), Matchers.is("'399.0.0.1' is an invalid IP address."));
    }

    @Test
    public void shouldInvalidateEmptyAddress() {
        AgentConfig agentConfig = new AgentConfig("uuid", "host", "");
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(IP_ADDRESS), Matchers.is("IpAddress cannot be empty if it is present."));
    }

    @Test
    public void shouldValidateTree() {
        ResourceConfig resourceConfig = new ResourceConfig("junk%");
        AgentConfig agentConfig = new AgentConfig("uuid", "junk", "junk", new ResourceConfigs(resourceConfig));
        boolean isValid = agentConfig.validateTree(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(IP_ADDRESS), Matchers.is("'junk' is an invalid IP address."));
        Assert.assertThat(resourceConfig.errors().on(RESOURCES), TestUtils.contains("Resource name 'junk%' is not valid."));
        Assert.assertThat(isValid, Matchers.is(false));
    }

    @Test
    public void shouldPassValidationWhenUUidIsAvailable() {
        AgentConfig agentConfig = new AgentConfig("uuid");
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(UUID), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldFailValidationWhenUUidIsBlank() {
        AgentConfig agentConfig = new AgentConfig("");
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(UUID), Matchers.is("UUID cannot be empty"));
        agentConfig = new AgentConfig(null);
        agentConfig.validate(ConfigSaveValidationContext.forChain(agentConfig));
        Assert.assertThat(agentConfig.errors().on(UUID), Matchers.is("UUID cannot be empty"));
    }

    @Test
    public void shouldAllowResourcesOnNonElasticAgents() throws Exception {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("dev", "qa");
        AgentConfig agentConfig = new AgentConfig("uuid", "hostname", "10.10.10.10");
        cruiseConfig.agents().add(agentConfig);
        agentConfig.addResourceConfig(new ResourceConfig("foo"));
        Assert.assertThat(cruiseConfig.validateAfterPreprocess().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldNotAllowResourcesElasticAgents() throws Exception {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("dev", "qa");
        AgentConfig agentConfig = new AgentConfig("uuid", "hostname", "10.10.10.10");
        cruiseConfig.agents().add(agentConfig);
        agentConfig.setElasticPluginId("com.example.foo");
        agentConfig.setElasticAgentId("foobar");
        agentConfig.addResourceConfig(new ResourceConfig("foo"));
        Assert.assertThat(cruiseConfig.validateAfterPreprocess().isEmpty(), Matchers.is(false));
        Assert.assertEquals(1, agentConfig.errors().size());
        Assert.assertThat(agentConfig.errors().on("elasticAgentId"), Matchers.is("Elastic agents cannot have resources."));
    }
}

