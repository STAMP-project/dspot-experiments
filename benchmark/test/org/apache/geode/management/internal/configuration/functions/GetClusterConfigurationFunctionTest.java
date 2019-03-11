/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.configuration.functions;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.distributed.internal.InternalLocator;
import org.apache.geode.management.internal.configuration.messages.ConfigurationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@PowerMockIgnore("*.UnitTest")
@RunWith(PowerMockRunner.class)
@PrepareForTest(InternalLocator.class)
public class GetClusterConfigurationFunctionTest {
    private InternalLocator mockedLocator;

    private FunctionContext mockedFunctionContext;

    private ResultSender<Object> mockedResultSender;

    private InternalConfigurationPersistenceService mockedConfigurationService;

    private GetClusterConfigurationFunction getClusterConfigurationFunction;

    @Test
    public void executeShouldReturnIllegalStateExceptionWhenClusterConfigurationServiceIsDisabled() {
        Mockito.when(mockedLocator.isSharedConfigurationEnabled()).thenReturn(false);
        ArgumentCaptor<Exception> argumentCaptor = ArgumentCaptor.forClass(Exception.class);
        assertThatCode(() -> getClusterConfigurationFunction.execute(mockedFunctionContext)).doesNotThrowAnyException();
        Mockito.verify(mockedResultSender).lastResult(argumentCaptor.capture());
        Exception exceptionThrown = argumentCaptor.getValue();
        assertThat(exceptionThrown).isInstanceOf(IllegalStateException.class).hasMessage("The cluster configuration service is not enabled on this member.");
    }

    @Test
    public void executeShouldReturnExceptionWhenClusterConfigurationServiceIsEnabledButFailuresOccurWhileRetrievingIt() {
        Mockito.when(mockedConfigurationService.createConfigurationResponse(ArgumentMatchers.any())).thenThrow(new RuntimeException("Mocked Exception."));
        Mockito.when(mockedLocator.getConfigurationPersistenceService()).thenReturn(mockedConfigurationService);
        ArgumentCaptor<Exception> argumentCaptor = ArgumentCaptor.forClass(Exception.class);
        assertThatCode(() -> getClusterConfigurationFunction.execute(mockedFunctionContext)).doesNotThrowAnyException();
        Mockito.verify(mockedResultSender).lastResult(argumentCaptor.capture());
        Exception exceptionThrown = argumentCaptor.getValue();
        assertThat(exceptionThrown).isInstanceOf(RuntimeException.class).hasMessage("Mocked Exception.");
    }

    @Test
    public void executeShouldReturnNullWhenClusterConfigurationServiceIsEnabledButNotRunning() {
        Mockito.when(mockedLocator.isSharedConfigurationRunning()).thenReturn(false);
        assertThatCode(() -> getClusterConfigurationFunction.execute(mockedFunctionContext)).doesNotThrowAnyException();
        Mockito.verify(mockedResultSender, Mockito.times(1)).lastResult(null);
    }

    @Test
    public void executeShouldReturnTheRequestConfigurationWhenClusterConfigurationServiceIsEnabled() {
        Set<String> requestedGroups = new HashSet<>(Arrays.asList("group1", "group2"));
        Mockito.when(mockedFunctionContext.getArguments()).thenReturn(requestedGroups);
        Mockito.when(mockedLocator.getConfigurationPersistenceService()).thenReturn(mockedConfigurationService);
        ConfigurationResponse mockedResponse = new ConfigurationResponse();
        Mockito.when(mockedConfigurationService.createConfigurationResponse(ArgumentMatchers.any())).thenReturn(mockedResponse);
        ArgumentCaptor<ConfigurationResponse> argumentCaptor = ArgumentCaptor.forClass(ConfigurationResponse.class);
        assertThatCode(() -> getClusterConfigurationFunction.execute(mockedFunctionContext)).doesNotThrowAnyException();
        Mockito.verify(mockedResultSender).lastResult(argumentCaptor.capture());
        Mockito.verify(mockedConfigurationService, Mockito.times(1)).createConfigurationResponse(requestedGroups);
        Mockito.verify(mockedResultSender, Mockito.times(1)).lastResult(mockedResponse);
    }
}

