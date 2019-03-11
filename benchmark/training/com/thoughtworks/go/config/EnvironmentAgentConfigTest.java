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


import EnvironmentAgentConfig.UUID;
import java.util.HashSet;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EnvironmentAgentConfigTest {
    @Test
    public void shouldFailValidationIfUUIDDoesNotMapToAnAgent() {
        EnvironmentAgentConfig config = new EnvironmentAgentConfig("uuid1");
        HashSet<String> uuids = new HashSet<>();
        uuids.add("uuid2");
        uuids.add("uuid3");
        boolean isValid = config.validateUuidPresent(new CaseInsensitiveString("foo"), uuids);
        Assert.assertThat(isValid, Matchers.is(false));
        Assert.assertThat(config.errors().on(UUID), Matchers.is("Environment 'foo' has an invalid agent uuid 'uuid1'"));
    }

    @Test
    public void shouldPassValidationIfUUIDMapsToAnAgent() {
        EnvironmentAgentConfig config = new EnvironmentAgentConfig("uuid1");
        HashSet<String> uuids = new HashSet<>();
        uuids.add("uuid1");
        uuids.add("uuid2");
        boolean isValid = config.validateUuidPresent(new CaseInsensitiveString("foo"), uuids);
        Assert.assertThat(isValid, Matchers.is(true));
        Assert.assertThat(config.errors().isEmpty(), Matchers.is(true));
    }
}

