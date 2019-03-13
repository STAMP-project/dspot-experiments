/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.cache;


import CacheProperties.CACHE_SERVICE_CLASS;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.gchq.gaffer.cache.impl.HashMapCacheService;


public class CacheServiceLoaderTest {
    private Properties serviceLoaderProperties = new Properties();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldDoNothingOnInitialiseIfNoPropertiesAreGiven() {
        try {
            CacheServiceLoader.initialise(null);
        } catch (final NullPointerException e) {
            Assert.fail("Should not have thrown an exception");
        }
    }

    @Test
    public void shouldLoadServiceFromSystemVariable() {
        // given
        serviceLoaderProperties.setProperty(CACHE_SERVICE_CLASS, EmptyCacheService.class.getName());
        CacheServiceLoader.initialise(serviceLoaderProperties);
        // when
        ICacheService service = CacheServiceLoader.getService();
        // then
        assert service instanceof EmptyCacheService;
    }

    @Test
    public void shouldThrowAnExceptionWhenSystemVariableMisconfigured() {
        // given
        String invalidClassName = "invalid.cache.name";
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(invalidClassName);
        // when
        serviceLoaderProperties.setProperty(CACHE_SERVICE_CLASS, invalidClassName);
        CacheServiceLoader.initialise(serviceLoaderProperties);
        // then Exception is thrown
    }

    @Test
    public void shouldUseTheSameServiceAcrossDifferentComponents() {
        // given
        serviceLoaderProperties.setProperty(CACHE_SERVICE_CLASS, HashMapCacheService.class.getName());
        CacheServiceLoader.initialise(serviceLoaderProperties);
        // when
        ICacheService component1Service = CacheServiceLoader.getService();
        ICacheService component2Service = CacheServiceLoader.getService();
        // then
        Assert.assertEquals(component1Service, component2Service);
    }

    @Test
    public void shouldSetServiceToNullAfterCallingShutdown() {
        // given
        serviceLoaderProperties.setProperty(CACHE_SERVICE_CLASS, EmptyCacheService.class.getName());
        CacheServiceLoader.initialise(serviceLoaderProperties);
        // when
        CacheServiceLoader.shutdown();
        // then
        Assert.assertNull(CacheServiceLoader.getService());
    }
}

