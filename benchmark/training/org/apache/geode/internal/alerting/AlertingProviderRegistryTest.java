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
package org.apache.geode.internal.alerting;


import org.apache.geode.test.junit.categories.AlertingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AlertingProviderRegistry}.
 */
@Category(AlertingTest.class)
public class AlertingProviderRegistryTest {
    private AlertingProvider provider;

    private AlertingProviderRegistry alertingProviderRegistry;

    @Test
    public void getAlertingProviderIsNullProviderBeforeRegister() {
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(AlertingProviderRegistry.getNullAlertingProvider());
    }

    @Test
    public void getAlertingProviderIsSameAsRegisteredProvider() {
        alertingProviderRegistry.registerAlertingProvider(provider);
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(provider);
    }

    @Test
    public void unregisterDoesNothingIfNotRegistered() {
        alertingProviderRegistry.unregisterAlertingProvider(provider);
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(AlertingProviderRegistry.getNullAlertingProvider());
    }

    @Test
    public void unregisterWrongProviderDoesNothing() {
        alertingProviderRegistry.registerAlertingProvider(provider);
        alertingProviderRegistry.unregisterAlertingProvider(Mockito.mock(AlertingProvider.class));
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(provider);
    }

    @Test
    public void unregisterDoesNothingIfNullProvider() {
        alertingProviderRegistry.unregisterAlertingProvider(AlertingProviderRegistry.getNullAlertingProvider());
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(AlertingProviderRegistry.getNullAlertingProvider());
    }

    @Test
    public void unregisterRemovesRegisteredProvider() {
        alertingProviderRegistry.registerAlertingProvider(provider);
        alertingProviderRegistry.unregisterAlertingProvider(provider);
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(AlertingProviderRegistry.getNullAlertingProvider());
    }

    @Test
    public void registerAddsAlertingProvider() {
        alertingProviderRegistry.registerAlertingProvider(provider);
        assertThat(alertingProviderRegistry.getAlertingProvider()).isSameAs(provider);
    }
}

