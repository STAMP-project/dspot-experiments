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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ContainerConfigTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void test1_28() throws Exception {
        objectMapper.readValue(FixtureUtil.fixture("fixtures/1.28/containerConfig.json"), ContainerConfig.class);
    }

    @Test
    public void test1_29() throws Exception {
        objectMapper.readValue(FixtureUtil.fixture("fixtures/1.29/containerConfig.json"), ContainerConfig.class);
    }

    @Test
    public void test1_29_WithoutNullables() throws Exception {
        final ContainerConfig config = objectMapper.readValue(FixtureUtil.fixture("fixtures/1.29/containerConfigWithoutNullables.json"), ContainerConfig.class);
        MatcherAssert.assertThat(config.portSpecs(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.exposedPorts(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.env(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.cmd(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.entrypoint(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.onBuild(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.labels(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.healthcheck(), Matchers.is(Matchers.nullValue()));
    }
}

