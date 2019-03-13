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


import ContainerState.Health;
import ContainerState.HealthLog;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.ObjectMapperProvider;
import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ContainerStateTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void testLoadFromRandomFixture() throws Exception {
        final ContainerState containerState = objectMapper.readValue(FixtureUtil.fixture("fixtures/container-state-random.json"), ContainerState.class);
        Assert.assertThat(containerState.paused(), CoreMatchers.is(false));
        Assert.assertThat(containerState.restarting(), CoreMatchers.is(false));
        Assert.assertThat(containerState.running(), CoreMatchers.is(true));
        Assert.assertThat(containerState.exitCode(), CoreMatchers.is(0L));
        Assert.assertThat(containerState.pid(), CoreMatchers.is(27629));
        Assert.assertThat(containerState.startedAt(), CoreMatchers.is(new Date(1412236798929L)));
        Assert.assertThat(containerState.finishedAt(), CoreMatchers.is(new Date((-62135769600000L))));
        Assert.assertThat(containerState.error(), CoreMatchers.is("this is an error"));
        Assert.assertThat(containerState.oomKilled(), CoreMatchers.is(false));
        Assert.assertThat(containerState.status(), CoreMatchers.is("running"));
        ContainerState.Health health = containerState.health();
        Assert.assertThat(health.failingStreak(), CoreMatchers.is(1));
        Assert.assertThat(health.status(), CoreMatchers.is("starting"));
        Assert.assertThat(health.log().size(), CoreMatchers.is(1));
        ContainerState.HealthLog log = health.log().get(0);
        Assert.assertThat(log.start(), CoreMatchers.is(new Date(1412236801547L)));
        Assert.assertThat(log.end(), CoreMatchers.is(new Date(1412236802697L)));
        Assert.assertThat(log.exitCode(), CoreMatchers.is(1L));
        Assert.assertThat(log.output(), CoreMatchers.is("output"));
    }

    @Test
    public void testLoadFromRandomFixtureMissingProperty() throws Exception {
        objectMapper.readValue(FixtureUtil.fixture("fixtures/container-state-missing-property.json"), ContainerState.class);
    }

    @Test
    public void testLoadInvalidConatainerStateJson() throws Exception {
        expectedException.expect(JsonMappingException.class);
        objectMapper.readValue(FixtureUtil.fixture("fixtures/container-state-invalid.json"), ContainerState.class);
    }

    @Test
    public void testLoadInvalidJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        objectMapper.readValue(FixtureUtil.fixture("fixtures/invalid.json"), ContainerState.class);
    }
}

