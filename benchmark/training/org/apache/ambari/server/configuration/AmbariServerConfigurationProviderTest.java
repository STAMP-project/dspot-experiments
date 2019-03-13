/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.configuration;


import com.google.inject.Injector;
import com.google.inject.persist.jpa.AmbariJpaPersistService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.events.JpaInitializedEvent;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class AmbariServerConfigurationProviderTest extends EasyMockSupport {
    private static final AmbariServerConfigurationCategory TEST_CONFIGURATION = AmbariServerConfigurationCategory.TPROXY_CONFIGURATION;

    @Test
    public void testGetAndLoadDataForVariousEvents() {
        Injector injector = getInjector();
        AmbariServerConfiguration emptyTestConfiguration = createMock(AmbariServerConfiguration.class);
        AmbariServerConfiguration filledTestConfiguration1 = createMock(AmbariServerConfiguration.class);
        AmbariServerConfiguration filledTestConfiguration2 = createMock(AmbariServerConfiguration.class);
        AmbariEventPublisher publisher = injector.getInstance(AmbariEventPublisher.class);
        AmbariJpaPersistService persistService = injector.getInstance(AmbariJpaPersistService.class);
        AmbariServerConfigurationProvider provider = createMockBuilder(AmbariServerConfigurationProvider.class).addMockedMethod("loadInstance", Collection.class).withConstructor(AmbariServerConfigurationProviderTest.TEST_CONFIGURATION, publisher, persistService).createMock();
        expect(provider.loadInstance(Collections.emptyList())).andReturn(emptyTestConfiguration).once();
        expect(provider.loadInstance(null)).andReturn(filledTestConfiguration1).once();
        expect(provider.loadInstance(null)).andReturn(filledTestConfiguration2).once();
        replayAll();
        injector.injectMembers(provider);
        AmbariServerConfiguration configuration = provider.get();
        Assert.assertSame(emptyTestConfiguration, configuration);
        // Push a configuration change event...
        provider.ambariConfigurationChanged(new org.apache.ambari.server.events.AmbariConfigurationChangedEvent(AmbariServerConfigurationProviderTest.TEST_CONFIGURATION.getCategoryName()));
        AmbariServerConfiguration configuration2 = provider.get();
        // This should return the same instance as before since loadInstance should not have done anything
        Assert.assertSame(configuration, configuration2);
        // Push an initializing JPA event...
        provider.jpaInitialized(new JpaInitializedEvent());
        AmbariServerConfiguration configuration3 = provider.get();
        Assert.assertSame(filledTestConfiguration1, configuration3);
        // Push a configuration change event...
        provider.ambariConfigurationChanged(new org.apache.ambari.server.events.AmbariConfigurationChangedEvent(AmbariServerConfigurationProviderTest.TEST_CONFIGURATION.getCategoryName()));
        AmbariServerConfiguration configuration4 = provider.get();
        // This should return a different instance since loadInstance should have done some work
        Assert.assertNotSame(configuration3, configuration4);
        verifyAll();
    }

    @Test
    public void testToProperties() {
        Injector injector = getInjector();
        AmbariEventPublisher publisher = injector.getInstance(AmbariEventPublisher.class);
        AmbariJpaPersistService persistService = injector.getInstance(AmbariJpaPersistService.class);
        AmbariServerConfigurationProvider provider = createMockBuilder(AmbariServerConfigurationProvider.class).withConstructor(AmbariServerConfigurationProviderTest.TEST_CONFIGURATION, publisher, persistService).createMock();
        replayAll();
        Map actualProperties;
        actualProperties = provider.toProperties(null);
        Assert.assertNotNull(actualProperties);
        Assert.assertEquals(Collections.emptyMap(), actualProperties);
        actualProperties = provider.toProperties(Collections.emptyList());
        Assert.assertNotNull(actualProperties);
        Assert.assertEquals(Collections.emptyMap(), actualProperties);
        Map<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put("one", "1");
        expectedProperties.put("two", "2");
        expectedProperties.put("three", "3");
        actualProperties = provider.toProperties(createAmbariConfigurationEntities(expectedProperties));
        Assert.assertNotNull(actualProperties);
        Assert.assertNotSame(expectedProperties, actualProperties);
        Assert.assertEquals(expectedProperties, actualProperties);
        verifyAll();
    }
}

