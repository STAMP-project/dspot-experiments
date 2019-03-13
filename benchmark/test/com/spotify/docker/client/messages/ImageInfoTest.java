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
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ImageInfoTest {
    private ObjectMapper objectMapper = ObjectMapperProvider.objectMapper();

    @Test
    public void test1_24() throws Exception {
        objectMapper.readValue(FixtureUtil.fixture("fixtures/1.24/imageInfo.json"), ImageInfo.class);
    }

    /**
     * Test that an ImageInfo message from 1.22 - before RootFs was introduced - can be deserialized.
     */
    @Test
    public void test1_22() throws Exception {
        final ImageInfo imageInfo = objectMapper.readValue(FixtureUtil.fixture("fixtures/1.22/imageInfo.json"), ImageInfo.class);
        Assert.assertThat(imageInfo.rootFs(), Matchers.is(Matchers.nullValue()));
    }
}

