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
import org.junit.Test;


/**
 * Test cases around the deserialization of the docker info object.
 */
public class DockerInfoTest {
    private final ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    /**
     * Test that when we deserialize the docker info response we properly parse various fields.
     *
     * @throws Exception
     * 		when we fail to deserialize
     */
    @Test
    public void dockerInfoNetworkDesirializerTest_1_23() throws Exception {
        Info info = objectMapper.readValue(FixtureUtil.fixture("fixtures/1.23/docker_info.json"), Info.class);
        MatcherAssert.assertThat(info.plugins(), Matchers.is(Matchers.not(Matchers.nullValue())));
        MatcherAssert.assertThat(info.plugins().networks(), Matchers.is(Matchers.not(Matchers.nullValue())));
        MatcherAssert.assertThat(info.plugins().networks().size(), Matchers.is(Matchers.greaterThan(0)));
        MatcherAssert.assertThat(info.plugins().volumes(), Matchers.is(Matchers.not(Matchers.nullValue())));
        MatcherAssert.assertThat(info.plugins().volumes().size(), Matchers.is(Matchers.greaterThan(0)));
    }
}

