/**
 * -
 * -\-\-
 * Helios Testing Library
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
package com.spotify.helios.testing;


import TemporaryJobs.Builder;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TemporaryJobsBuilderTest {
    /**
     * Ensure that the TemporaryJobs.Builder can be constructed ok when DOCKER_HOST looks like a unix
     * socket path.
     */
    @Test
    public void testDockerHostIsUnixSocket() {
        final Map<String, String> env = ImmutableMap.of("DOCKER_HOST", "unix:///var/run/docker.sock");
        final TemporaryJobs.Builder builder = TemporaryJobs.builder(env);
        Assert.assertNotNull(builder);
    }
}

