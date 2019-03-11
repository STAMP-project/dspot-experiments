/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.config;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.axonframework.messaging.ScopeAware;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.saga.AbstractSagaManager;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests {@link ConfigurationScopeAwareProvider}.
 *
 * @author Rob van der Linden Vooren
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationScopeAwareProviderTest {
    @Mock
    private Configuration configuration;

    @Mock
    private AggregateConfiguration aggregateConfiguration;

    @Mock
    private Repository aggregateRepository;

    @Mock
    private SagaConfiguration sagaConfiguration;

    @Mock
    private AbstractSagaManager sagaManager;

    @Mock
    private EventProcessingModule eventProcessingConfiguration;

    private ConfigurationScopeAwareProvider scopeAwareProvider;

    @Test
    public void providesScopeAwareAggregatesFromModuleConfiguration() {
        Mockito.when(configuration.findModules(AggregateConfiguration.class)).thenCallRealMethod();
        Mockito.when(configuration.getModules()).thenReturn(Collections.singletonList(new ConfigurationScopeAwareProviderTest.WrappingModuleConfiguration(aggregateConfiguration)));
        Mockito.when(aggregateConfiguration.repository()).thenReturn(aggregateRepository);
        List<ScopeAware> components = scopeAwareProvider.provideScopeAwareStream(ConfigurationScopeAwareProviderTest.anyScopeDescriptor()).collect(Collectors.toList());
        MatcherAssert.assertThat(components, CoreMatchers.equalTo(Collections.singletonList(aggregateRepository)));
    }

    @Test
    public void providesScopeAwareSagasFromModuleConfiguration() {
        Mockito.when(eventProcessingConfiguration.sagaConfigurations()).thenReturn(Collections.singletonList(sagaConfiguration));
        Mockito.when(sagaConfiguration.manager()).thenReturn(sagaManager);
        List<ScopeAware> components = scopeAwareProvider.provideScopeAwareStream(ConfigurationScopeAwareProviderTest.anyScopeDescriptor()).collect(Collectors.toList());
        MatcherAssert.assertThat(components, CoreMatchers.equalTo(Collections.singletonList(sagaManager)));
    }

    @Test
    public void lazilyInitializes() {
        new ConfigurationScopeAwareProvider(configuration);
        Mockito.verifyZeroInteractions(configuration);
    }

    @Test
    public void cachesScopeAwareComponentsOnceProvisioned() {
        Mockito.when(configuration.findModules(AggregateConfiguration.class)).thenCallRealMethod();
        Mockito.when(configuration.getModules()).thenReturn(Collections.singletonList(new ConfigurationScopeAwareProviderTest.WrappingModuleConfiguration(aggregateConfiguration)));
        Mockito.when(aggregateConfiguration.repository()).thenReturn(aggregateRepository);
        // provision once
        List<ScopeAware> first = scopeAwareProvider.provideScopeAwareStream(ConfigurationScopeAwareProviderTest.anyScopeDescriptor()).collect(Collectors.toList());
        Mockito.reset(configuration, aggregateConfiguration);
        // provision twice
        List<ScopeAware> second = scopeAwareProvider.provideScopeAwareStream(ConfigurationScopeAwareProviderTest.anyScopeDescriptor()).collect(Collectors.toList());
        Mockito.verifyZeroInteractions(configuration);
        Mockito.verifyZeroInteractions(aggregateConfiguration);
        MatcherAssert.assertThat(second, CoreMatchers.equalTo(first));
    }

    /**
     * Test variant of a {@link #unwrap() wrapping} configuration.
     */
    private static class WrappingModuleConfiguration implements ModuleConfiguration {
        private final ModuleConfiguration delegate;

        WrappingModuleConfiguration(ModuleConfiguration delegate) {
            this.delegate = delegate;
        }

        @Override
        public void initialize(Configuration config) {
            // No-op, only ipmlemented for test case
        }

        @Override
        public void start() {
            // No-op, only ipmlemented for test case
        }

        @Override
        public void shutdown() {
            // No-op, only ipmlemented for test case
        }

        @Override
        public ModuleConfiguration unwrap() {
            return (delegate) == null ? this : delegate;
        }

        @Override
        public boolean isType(Class<?> type) {
            return type.isAssignableFrom(unwrap().getClass());
        }
    }
}

