/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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
package com.spotify.docker.client.messages.swarm;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.ObjectMapperProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DnsConfigTest {
    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void test1_32() throws Exception {
        final DnsConfig config = objectMapper.readValue(FixtureUtil.fixture("fixtures/1.32/dnsConfig.json"), DnsConfig.class);
        Assert.assertThat(config.nameServers(), Matchers.contains("8.8.8.8"));
        Assert.assertThat(config.search(), Matchers.contains("example.org"));
        Assert.assertThat(config.options(), Matchers.contains("timeout:3"));
    }

    @Test
    public void test1_32_WithoutNullables() throws Exception {
        final DnsConfig config = objectMapper.readValue("{}", DnsConfig.class);
        Assert.assertThat(config.nameServers(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(config.search(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(config.options(), Matchers.is(Matchers.nullValue()));
    }
}

