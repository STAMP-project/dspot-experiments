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
package com.spotify.docker.client.messages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.ObjectMapperProvider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class IpamTest {
    private static final ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void testDeserialize() throws Exception {
        final Ipam ipam = IpamTest.objectMapper.readValue(FixtureUtil.fixture("fixtures/1.29/ipam.json"), Ipam.class);
        MatcherAssert.assertThat(ipam.driver(), Matchers.equalTo("default"));
        MatcherAssert.assertThat(ipam.config(), Matchers.contains(IpamConfig.create("172.17.0.0/16", null, null)));
        MatcherAssert.assertThat(ipam.options(), Matchers.equalTo(ImmutableMap.of("foo", "bar")));
    }

    @Test
    public void testDeserialize_nullConfig() throws Exception {
        final Ipam ipam = IpamTest.objectMapper.readValue(FixtureUtil.fixture("fixtures/1.29/ipam-null-config.json"), Ipam.class);
        MatcherAssert.assertThat(ipam.driver(), Matchers.equalTo("default"));
        MatcherAssert.assertThat(ipam.config(), Matchers.is(Matchers.nullValue()));
    }
}

