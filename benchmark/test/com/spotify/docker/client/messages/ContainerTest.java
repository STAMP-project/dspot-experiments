/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.docker.client.messages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.ObjectMapperProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ContainerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void testLoadFromFixture() throws Exception {
        final Container container = objectMapper.readValue(FixtureUtil.fixture("fixtures/container-ports-as-string.json"), Container.class);
        Assert.assertThat(container.portsAsString(), CoreMatchers.is("0.0.0.0:80->88/tcp"));
    }

    @Test
    public void testLoadFromFixtureMissingPorts() throws Exception {
        final Container container = objectMapper.readValue(FixtureUtil.fixture("fixtures/container-no-ports-or-names.json"), Container.class);
        Assert.assertThat(container.id(), CoreMatchers.is("1009"));
    }
}

