/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.remote.work;


import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.remote.work.artifact.ArtifactRequestProcessor;
import com.thoughtworks.go.work.DefaultGoPublisher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


public abstract class ArtifactRequestProcessorTestBase {
    private GoApiRequest request;

    private GoPluginDescriptor descriptor;

    private DefaultGoPublisher goPublisher;

    private ArtifactRequestProcessor artifactRequestProcessorForPublish;

    private ArtifactRequestProcessor artifactRequestProcessorForFetch;

    @Test
    public void shouldFailForAVersionOutsideOfSupportedVersions() {
        Mockito.reset(request);
        Mockito.when(request.apiVersion()).thenReturn("3.0");
        Mockito.when(request.api()).thenReturn(CONSOLE_LOG.requestName());
        Mockito.when(request.requestBody()).thenReturn("{\"logLevel\":\"ERROR\",\"message\":\"Error while pushing docker image to registry: foo.\"}");
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            artifactRequestProcessorForPublish.process(descriptor, request);
        });
        Assert.assertThat(exception.getMessage(), Matchers.containsString("Unsupported 'go.processor.artifact.console-log' API version: 3.0"));
    }

    @Test
    public void shouldSendErrorLogToConsoleLogForPublish() {
        Mockito.when(request.requestBody()).thenReturn("{\"logLevel\":\"ERROR\",\"message\":\"Error while pushing docker image to registry: foo.\"}");
        artifactRequestProcessorForPublish.process(descriptor, request);
        Mockito.verify(goPublisher, Mockito.times(1)).taggedConsumeLine(PUBLISH_ERR, "[cd.go.artifact.docker] Error while pushing docker image to registry: foo.");
    }

    @Test
    public void shouldSendInfoLogToConsoleLogForPublish() {
        Mockito.when(request.requestBody()).thenReturn("{\"logLevel\":\"INFO\",\"message\":\"Pushing docker image to registry: foo.\"}");
        artifactRequestProcessorForPublish.process(descriptor, request);
        Mockito.verify(goPublisher, Mockito.times(1)).taggedConsumeLine(PUBLISH, "[cd.go.artifact.docker] Pushing docker image to registry: foo.");
    }

    @Test
    public void shouldSendErrorLogToConsoleLogForFetch() {
        Mockito.when(request.requestBody()).thenReturn("{\"logLevel\":\"ERROR\",\"message\":\"Error while pushing docker image to registry: foo.\"}");
        artifactRequestProcessorForFetch.process(descriptor, request);
        Mockito.verify(goPublisher, Mockito.times(1)).taggedConsumeLine(ERR, "[cd.go.artifact.docker] Error while pushing docker image to registry: foo.");
    }

    @Test
    public void shouldSendInfoLogToConsoleLogForFetch() {
        Mockito.when(request.requestBody()).thenReturn("{\"logLevel\":\"INFO\",\"message\":\"Pushing docker image to registry: foo.\"}");
        artifactRequestProcessorForFetch.process(descriptor, request);
        Mockito.verify(goPublisher, Mockito.times(1)).taggedConsumeLine(OUT, "[cd.go.artifact.docker] Pushing docker image to registry: foo.");
    }

    @Test
    public void shouldMaskSecretsFromEnvironmentVariables() {
        Mockito.when(request.requestBody()).thenReturn("{\"logLevel\":\"INFO\",\"message\":\"This is a secret: secret.value.\"}");
        artifactRequestProcessorForFetch.process(descriptor, request);
        Mockito.verify(goPublisher, Mockito.times(1)).taggedConsumeLine(OUT, "[cd.go.artifact.docker] This is a secret: ******.");
    }
}

