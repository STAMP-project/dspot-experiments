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


import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ExecState;
import com.spotify.docker.client.messages.Version;
import com.spotify.helios.agent.HealthCheckerFactory.ExecHealthChecker;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ExecHealthCheckerTest {
    private static final String CONTAINER_ID = "abc123def";

    private static final String EXEC_ID = "5678";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private DockerClient docker;

    private ExecHealthChecker checker;

    @Test
    public void testHealthCheckSuccess() {
        Assert.assertThat(checker.check(ExecHealthCheckerTest.CONTAINER_ID), Is.is(true));
    }

    @Test
    public void testHealthCheckFailure() throws Exception {
        final ExecState execState = Mockito.mock(ExecState.class);
        Mockito.when(execState.exitCode()).thenReturn(2L);
        Mockito.when(docker.execInspect(ExecHealthCheckerTest.EXEC_ID)).thenReturn(execState);
        Assert.assertThat(checker.check(ExecHealthCheckerTest.CONTAINER_ID), Is.is(false));
    }

    @Test
    public void testIncompatibleVersion() throws Exception {
        final Version version = Mockito.mock(Version.class);
        Mockito.when(version.apiVersion()).thenReturn("1.15");
        Mockito.when(docker.version()).thenReturn(version);
        exception.expect(UnsupportedOperationException.class);
        checker.check(ExecHealthCheckerTest.CONTAINER_ID);
    }
}

