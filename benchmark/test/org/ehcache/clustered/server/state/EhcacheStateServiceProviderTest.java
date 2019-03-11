/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.server.state;


import java.util.Collections;
import org.ehcache.clustered.common.ServerSideConfiguration;
import org.ehcache.clustered.common.internal.ClusterTierManagerConfiguration;
import org.ehcache.clustered.server.KeySegmentMapper;
import org.ehcache.clustered.server.state.config.EhcacheStateServiceConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.entity.PlatformConfiguration;
import org.terracotta.entity.ServiceProviderCleanupException;
import org.terracotta.entity.ServiceProviderConfiguration;


public class EhcacheStateServiceProviderTest {
    private static final KeySegmentMapper DEFAULT_MAPPER = new KeySegmentMapper(16);

    private PlatformConfiguration platformConfiguration;

    private ServiceProviderConfiguration serviceProviderConfiguration;

    private ClusterTierManagerConfiguration tierManagerConfiguration;

    @Test
    public void testInitialize() {
        EhcacheStateServiceProvider serviceProvider = new EhcacheStateServiceProvider();
        Assert.assertTrue(serviceProvider.initialize(serviceProviderConfiguration, platformConfiguration));
    }

    @Test
    public void testGetService() {
        EhcacheStateServiceProvider serviceProvider = new EhcacheStateServiceProvider();
        serviceProvider.initialize(serviceProviderConfiguration, platformConfiguration);
        EhcacheStateService ehcacheStateService = serviceProvider.getService(1L, new EhcacheStateServiceConfig(tierManagerConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        Assert.assertNotNull(ehcacheStateService);
        EhcacheStateService sameStateService = serviceProvider.getService(1L, new EhcacheStateServiceConfig(tierManagerConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        Assert.assertSame(ehcacheStateService, sameStateService);
        ClusterTierManagerConfiguration otherConfiguration = new ClusterTierManagerConfiguration("otherIdentifier", new ServerSideConfiguration(Collections.emptyMap()));
        EhcacheStateService anotherStateService = serviceProvider.getService(2L, new EhcacheStateServiceConfig(otherConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        Assert.assertNotNull(anotherStateService);
        Assert.assertNotSame(ehcacheStateService, anotherStateService);
    }

    @Test
    public void testDestroyService() throws Exception {
        EhcacheStateServiceProvider serviceProvider = new EhcacheStateServiceProvider();
        serviceProvider.initialize(serviceProviderConfiguration, platformConfiguration);
        EhcacheStateServiceConfig configuration = new EhcacheStateServiceConfig(tierManagerConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER);
        EhcacheStateService ehcacheStateService = serviceProvider.getService(1L, configuration);
        ehcacheStateService.destroy();
        Assert.assertThat(serviceProvider.getService(1L, configuration), Matchers.not(Matchers.sameInstance(ehcacheStateService)));
    }

    @Test
    public void testPrepareForSynchronization() throws ServiceProviderCleanupException {
        EhcacheStateServiceProvider serviceProvider = new EhcacheStateServiceProvider();
        serviceProvider.initialize(serviceProviderConfiguration, platformConfiguration);
        ClusterTierManagerConfiguration otherConfiguration = new ClusterTierManagerConfiguration("otherIdentifier", new ServerSideConfiguration(Collections.emptyMap()));
        EhcacheStateService ehcacheStateService = serviceProvider.getService(1L, new EhcacheStateServiceConfig(tierManagerConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        EhcacheStateService anotherStateService = serviceProvider.getService(2L, new EhcacheStateServiceConfig(otherConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        serviceProvider.prepareForSynchronization();
        EhcacheStateService ehcacheStateServiceAfterClear = serviceProvider.getService(1L, new EhcacheStateServiceConfig(tierManagerConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        EhcacheStateService anotherStateServiceAfterClear = serviceProvider.getService(2L, new EhcacheStateServiceConfig(otherConfiguration, null, EhcacheStateServiceProviderTest.DEFAULT_MAPPER));
        Assert.assertNotSame(ehcacheStateService, ehcacheStateServiceAfterClear);
        Assert.assertNotSame(anotherStateService, anotherStateServiceAfterClear);
    }
}

