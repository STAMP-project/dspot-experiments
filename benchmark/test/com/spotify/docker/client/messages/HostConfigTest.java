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


import HostConfig.RestartPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.ObjectMapperProvider;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HostConfigTest {
    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void testJsonAlways() throws Exception {
        final HostConfig hostConfig = objectMapper.readValue(FixtureUtil.fixture("fixtures/hostConfig/restartPolicyAlways.json"), HostConfig.class);
        Assert.assertThat(hostConfig.restartPolicy(), CoreMatchers.is(RestartPolicy.always()));
    }

    @Test
    public void testJsonUnlessStopped() throws Exception {
        final HostConfig hostConfig = objectMapper.readValue(FixtureUtil.fixture("fixtures/hostConfig/restartPolicyUnlessStopped.json"), HostConfig.class);
        Assert.assertThat(hostConfig.restartPolicy(), CoreMatchers.is(RestartPolicy.unlessStopped()));
    }

    @Test
    public void testJsonOnFailure() throws Exception {
        final HostConfig hostConfig = objectMapper.readValue(FixtureUtil.fixture("fixtures/hostConfig/restartPolicyOnFailure.json"), HostConfig.class);
        Assert.assertThat(hostConfig.restartPolicy(), CoreMatchers.is(RestartPolicy.onFailure(5)));
    }

    @Test
    public void testReplaceBinds() {
        final List<String> initialBinds = ImmutableList.of("/one:/one", "/two:/two");
        final HostConfig hostConfig = HostConfig.builder().binds(initialBinds).binds(initialBinds).build();
        Assert.assertThat("Calling .binds() multiple times should replace the list each time", hostConfig.binds(), CoreMatchers.is(initialBinds));
    }

    @Test
    public void testAppendBinds() {
        final List<String> initialBinds = ImmutableList.of("/one:/one", "/two:/two");
        final HostConfig hostConfig = HostConfig.builder().binds(initialBinds).appendBinds("/three:/three").appendBinds("/four:/four").build();
        final List<String> expected = ImmutableList.<String>builder().addAll(initialBinds).add("/three:/three").add("/four:/four").build();
        Assert.assertThat("Calling .appendBinds should append to the list, not replace", hostConfig.binds(), CoreMatchers.is(expected));
    }

    @Test
    public void testPreventDuplicateBinds() {
        final HostConfig hostConfig = HostConfig.builder().appendBinds("/one:/one").appendBinds("/one:/one").appendBinds("/one:/one").build();
        Assert.assertThat(hostConfig.binds(), Matchers.contains("/one:/one"));
    }

    @Test
    public void testKernelMemory() throws Exception {
        final HostConfig hostConfig = objectMapper.readValue(FixtureUtil.fixture("fixtures/1.21/hostConfigKernelMemory.json"), HostConfig.class);
        Assert.assertThat(hostConfig.kernelMemory(), CoreMatchers.is(0L));
    }
}

