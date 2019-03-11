/**
 * Copyright 2019 ThoughtWorks, Inc.
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


import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class PluginProfileTest {
    @Test
    public void shouldNotAllowNullPluginIdOrProfileId() throws Exception {
        PluginProfile profile = pluginProfile(null, null);
        profile.validate(null);
        Assert.assertThat(profile.errors().size(), Matchers.is(2));
        Assert.assertThat(profile.errors().on("pluginId"), Matchers.is(String.format("%s cannot have a blank plugin id.", getObjectDescription())));
        Assert.assertThat(profile.errors().on("id"), Matchers.is(String.format("%s cannot have a blank id.", getObjectDescription())));
    }

    @Test
    public void shouldValidatePluginIdPattern() throws Exception {
        PluginProfile profile = pluginProfile("!123", "docker");
        profile.validate(null);
        Assert.assertThat(profile.errors().size(), Matchers.is(1));
        Assert.assertThat(profile.errors().on("id"), Matchers.is("Invalid id '!123'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldValidateConfigPropertyNameUniqueness() throws Exception {
        ConfigurationProperty prop1 = ConfigurationPropertyMother.create("USERNAME");
        ConfigurationProperty prop2 = ConfigurationPropertyMother.create("USERNAME");
        PluginProfile profile = pluginProfile("docker.unit-test", "cd.go.elastic-agent.docker", prop1, prop2);
        profile.validate(null);
        Assert.assertThat(profile.errors().size(), Matchers.is(0));
        Assert.assertThat(prop1.errors().size(), Matchers.is(1));
        Assert.assertThat(prop2.errors().size(), Matchers.is(1));
        Assert.assertThat(prop1.errors().on("configurationKey"), Matchers.is(String.format("Duplicate key 'USERNAME' found for %s 'docker.unit-test'", getObjectDescription())));
        Assert.assertThat(prop2.errors().on("configurationKey"), Matchers.is(String.format("Duplicate key 'USERNAME' found for %s 'docker.unit-test'", getObjectDescription())));
    }
}

