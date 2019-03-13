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
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.ObjectMapperProvider;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DriverTest {
    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void test1_32() throws Exception {
        final Driver driver = objectMapper.readValue(FixtureUtil.fixture("fixtures/1.32/driver.json"), Driver.class);
        Assert.assertThat(driver.name(), Matchers.equalTo("my-driver"));
        Assert.assertThat(driver.options(), Matchers.equalTo(ImmutableMap.of("1", "A", "2", "B")));
    }

    @Test
    public void test1_32_WithoutNullables() throws Exception {
        final Driver driver = objectMapper.readValue("{}", Driver.class);
        Assert.assertThat(driver.name(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(driver.options(), Matchers.is(Collections.<String, String>emptyMap()));
    }
}

