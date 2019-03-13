/**
 * -
 * -\-\-
 * Helios Services
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
package com.spotify.helios.agent;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerRequestException;
import java.net.URI;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DockerDaemonHealthCheckerTest {
    private final DockerClient dockerClient = Mockito.mock(DockerClient.class);

    private final DockerDaemonHealthChecker sut = new DockerDaemonHealthChecker(dockerClient);

    @Test
    public void testHealthy() throws Exception {
        final HealthCheck.Result result = sut.check();
        Assert.assertThat(result.isHealthy(), Matchers.is(true));
    }

    @Test
    public void testUnhealthy() throws Exception {
        Mockito.when(dockerClient.ping()).thenThrow(new DockerRequestException("GET", new URI("/ping"), 500, null, null));
        final HealthCheck.Result result = sut.check();
        Assert.assertThat(result.isHealthy(), Matchers.is(false));
    }
}

