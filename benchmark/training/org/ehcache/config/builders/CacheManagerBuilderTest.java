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
package org.ehcache.config.builders;


import java.util.concurrent.atomic.AtomicInteger;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.impl.config.copy.DefaultCopyProviderConfiguration;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.impl.serialization.CompactJavaSerializer;
import org.ehcache.impl.serialization.JavaSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class CacheManagerBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testIsExtensible() {
        AtomicInteger counter = new AtomicInteger(0);
        @SuppressWarnings("unchecked")
        CacheManagerConfiguration<PersistentCacheManager> managerConfiguration = ( other) -> {
            counter.getAndIncrement();
            return Mockito.mock(CacheManagerBuilder.class);
        };
        PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(managerConfiguration).build(true);
        assertThat(cacheManager).isNull();
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    public void testCanOverrideCopierInConfig() {
        @SuppressWarnings("unchecked")
        CacheManagerBuilder<CacheManager> managerBuilder = CacheManagerBuilder.newCacheManagerBuilder().withCopier(Long.class, ((Class) (IdentityCopier.class)));
        assertThat(managerBuilder.withCopier(Long.class, SerializingCopier.<Long>asCopierClass())).isNotNull();
    }

    @Test
    public void testCanOverrideSerializerConfig() {
        @SuppressWarnings("unchecked")
        Class<Serializer<String>> serializer1 = ((Class) (JavaSerializer.class));
        CacheManagerBuilder<CacheManager> managerBuilder = CacheManagerBuilder.newCacheManagerBuilder().withSerializer(String.class, serializer1);
        @SuppressWarnings("unchecked")
        Class<Serializer<String>> serializer2 = ((Class) (CompactJavaSerializer.class));
        assertThat(managerBuilder.withSerializer(String.class, serializer2)).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateServiceCreationConfigurationFails() {
        CacheManagerBuilder.newCacheManagerBuilder().using(new DefaultCopyProviderConfiguration()).using(new DefaultCopyProviderConfiguration());
    }

    @Test
    public void testDuplicateServiceCreationConfigurationOkWhenExplicit() {
        assertThat(CacheManagerBuilder.newCacheManagerBuilder().using(new DefaultCopyProviderConfiguration()).replacing(new DefaultCopyProviderConfiguration())).isNotNull();
    }

    @Test
    public void testShouldNotBeAllowedToRegisterTwoCachesWithSameAlias() {
        String cacheAlias = "cacheAliasSameName";
        CacheConfiguration<Long, String> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cache alias 'cacheAliasSameName' already exists");
        CacheManagerBuilder.newCacheManagerBuilder().withCache(cacheAlias, cacheConfig).withCache(cacheAlias, cacheConfig);
    }
}

