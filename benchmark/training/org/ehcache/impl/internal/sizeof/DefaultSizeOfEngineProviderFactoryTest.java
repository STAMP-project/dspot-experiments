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
package org.ehcache.impl.internal.sizeof;


import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.store.heap.SizeOfEngine;
import org.ehcache.core.spi.store.heap.SizeOfEngineProvider;
import org.ehcache.spi.service.ServiceConfiguration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Abhilash
 */
public class DefaultSizeOfEngineProviderFactoryTest {
    @Test
    public void testNullConfiguration() {
        DefaultSizeOfEngineProviderFactory factory = new DefaultSizeOfEngineProviderFactory();
        SizeOfEngineProvider sizeOfEngineProvider = factory.create(null);
        SizeOfEngine sizeOfEngine = sizeOfEngineProvider.createSizeOfEngine(MemoryUnit.B, Mockito.mock(ServiceConfiguration.class));
        MatcherAssert.assertThat(sizeOfEngineProvider, Matchers.notNullValue());
        MatcherAssert.assertThat(sizeOfEngine, Matchers.notNullValue());
        MatcherAssert.assertThat(sizeOfEngine, Matchers.instanceOf(DefaultSizeOfEngine.class));
    }

    @Test
    public void testNoopSizeOfEngineConfig() {
        DefaultSizeOfEngineProviderFactory factory = new DefaultSizeOfEngineProviderFactory();
        SizeOfEngineProvider sizeOfEngineProvider = factory.create(null);
        SizeOfEngine sizeOfEngine = sizeOfEngineProvider.createSizeOfEngine(null, Mockito.mock(ServiceConfiguration.class));
        MatcherAssert.assertThat(sizeOfEngineProvider, Matchers.notNullValue());
        MatcherAssert.assertThat(sizeOfEngine, Matchers.notNullValue());
        MatcherAssert.assertThat(sizeOfEngine, Matchers.instanceOf(NoopSizeOfEngine.class));
    }
}

