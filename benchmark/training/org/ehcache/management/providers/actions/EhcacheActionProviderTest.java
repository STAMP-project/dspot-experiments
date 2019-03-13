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
package org.ehcache.management.providers.actions;


import CapabilityContext.Attribute;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.core.Ehcache;
import org.ehcache.management.ManagementRegistryServiceConfiguration;
import org.ehcache.management.providers.CacheBinding;
import org.ehcache.management.registry.DefaultManagementRegistryConfiguration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terracotta.management.model.call.Parameter;
import org.terracotta.management.model.capabilities.context.CapabilityContext;
import org.terracotta.management.model.capabilities.descriptors.CallDescriptor;
import org.terracotta.management.model.capabilities.descriptors.Descriptor;
import org.terracotta.management.model.context.Context;


public class EhcacheActionProviderTest {
    Context cmContext = Context.create("cacheManagerName", "myCacheManagerName");

    Context cmContext_0 = Context.create("cacheManagerName", "cache-manager-0");

    ManagementRegistryServiceConfiguration cmConfig = new DefaultManagementRegistryConfiguration().setContext(cmContext);

    ManagementRegistryServiceConfiguration cmConfig_0 = new DefaultManagementRegistryConfiguration().setContext(cmContext_0);

    @Test
    @SuppressWarnings("unchecked")
    public void testDescriptions() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig);
        ehcacheActionProvider.register(new CacheBinding("myCacheName1", EhcacheActionProviderTest.mock(Ehcache.class)));
        ehcacheActionProvider.register(new CacheBinding("myCacheName2", EhcacheActionProviderTest.mock(Ehcache.class)));
        Collection<? extends Descriptor> descriptions = ehcacheActionProvider.getDescriptors();
        MatcherAssert.assertThat(descriptions.size(), Matchers.is(4));
        MatcherAssert.assertThat(descriptions, Matchers.containsInAnyOrder(new CallDescriptor("remove", "void", Collections.singletonList(new CallDescriptor.Parameter("key", "java.lang.Object"))), new CallDescriptor("get", "java.lang.Object", Collections.singletonList(new CallDescriptor.Parameter("key", "java.lang.Object"))), new CallDescriptor("put", "void", Arrays.asList(new CallDescriptor.Parameter("key", "java.lang.Object"), new CallDescriptor.Parameter("value", "java.lang.Object"))), new CallDescriptor("clear", "void", Collections.emptyList())));
    }

    @Test
    public void testCapabilityContext() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig);
        ehcacheActionProvider.register(new CacheBinding("myCacheName1", EhcacheActionProviderTest.mock(Ehcache.class)));
        ehcacheActionProvider.register(new CacheBinding("myCacheName2", EhcacheActionProviderTest.mock(Ehcache.class)));
        CapabilityContext capabilityContext = ehcacheActionProvider.getCapabilityContext();
        MatcherAssert.assertThat(capabilityContext.getAttributes().size(), Matchers.is(2));
        Iterator<CapabilityContext.Attribute> iterator = capabilityContext.getAttributes().iterator();
        CapabilityContext.Attribute next = iterator.next();
        MatcherAssert.assertThat(next.getName(), Matchers.equalTo("cacheManagerName"));
        MatcherAssert.assertThat(next.isRequired(), Matchers.is(true));
        next = iterator.next();
        MatcherAssert.assertThat(next.getName(), Matchers.equalTo("cacheName"));
        MatcherAssert.assertThat(next.isRequired(), Matchers.is(true));
    }

    @Test
    public void testCollectStatistics() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig);
        try {
            ehcacheActionProvider.collectStatistics(null, null, 0);
            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }

    @Test
    public void testCallAction_happyPathNoParam() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig_0);
        Ehcache<Object, Object> ehcache = EhcacheActionProviderTest.mock(Ehcache.class);
        CacheRuntimeConfiguration<Object, Object> cacheRuntimeConfiguration = EhcacheActionProviderTest.mock(CacheRuntimeConfiguration.class);
        Mockito.when(cacheRuntimeConfiguration.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
        Mockito.when(ehcache.getRuntimeConfiguration()).thenReturn(cacheRuntimeConfiguration);
        ehcacheActionProvider.register(new CacheBinding("cache-0", ehcache));
        Context context = cmContext_0.with("cacheName", "cache-0");
        ehcacheActionProvider.callAction(context, "clear", Void.class);
        Mockito.verify(ehcache, Mockito.times(1)).clear();
    }

    @Test
    public void testCallAction_happyPathWithParams() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig_0);
        @SuppressWarnings("unchecked")
        Ehcache<Long, String> ehcache = EhcacheActionProviderTest.mock(Ehcache.class);
        @SuppressWarnings("unchecked")
        CacheRuntimeConfiguration<Long, String> cacheRuntimeConfiguration = EhcacheActionProviderTest.mock(CacheRuntimeConfiguration.class);
        Mockito.when(cacheRuntimeConfiguration.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
        Mockito.when(cacheRuntimeConfiguration.getKeyType()).thenReturn(Long.class);
        Mockito.when(ehcache.getRuntimeConfiguration()).thenReturn(cacheRuntimeConfiguration);
        ehcacheActionProvider.register(new CacheBinding("cache-0", ehcache));
        Context context = cmContext_0.with("cacheName", "cache-0");
        ehcacheActionProvider.callAction(context, "get", Object.class, new Parameter("1", Object.class.getName()));
        Mockito.verify(ehcache, Mockito.times(1)).get(ArgumentMatchers.eq(1L));
    }

    @Test
    public void testCallAction_noSuchCache() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig_0);
        Ehcache<?, ?> ehcache = EhcacheActionProviderTest.mock(Ehcache.class);
        ehcacheActionProvider.register(new CacheBinding("cache-0", ehcache));
        Context context = cmContext_0.with("cacheName", "cache-1");
        try {
            ehcacheActionProvider.callAction(context, "clear", Void.class);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        Mockito.verify(ehcache, Mockito.times(0)).clear();
    }

    @Test
    public void testCallAction_noSuchCacheManager() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig_0);
        Ehcache<?, ?> ehcache = EhcacheActionProviderTest.mock(Ehcache.class);
        ehcacheActionProvider.register(new CacheBinding("cache-0", ehcache));
        Context context = Context.empty().with("cacheManagerName", "cache-manager-1").with("cacheName", "cache-0");
        try {
            ehcacheActionProvider.callAction(context, "clear", Void.class);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        Mockito.verify(ehcache, Mockito.times(0)).clear();
    }

    @Test
    public void testCallAction_noSuchMethodName() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig_0);
        Ehcache<Object, Object> ehcache = EhcacheActionProviderTest.mock(Ehcache.class);
        CacheRuntimeConfiguration<Object, Object> cacheRuntimeConfiguration = EhcacheActionProviderTest.mock(CacheRuntimeConfiguration.class);
        Mockito.when(cacheRuntimeConfiguration.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
        Mockito.when(ehcache.getRuntimeConfiguration()).thenReturn(cacheRuntimeConfiguration);
        ehcacheActionProvider.register(new CacheBinding("cache-0", ehcache));
        Context context = cmContext_0.with("cacheName", "cache-0");
        try {
            ehcacheActionProvider.callAction(context, "clearer", Void.class);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test
    public void testCallAction_noSuchMethod() throws Exception {
        EhcacheActionProvider ehcacheActionProvider = new EhcacheActionProvider(cmConfig_0);
        @SuppressWarnings("unchecked")
        Ehcache<Long, String> ehcache = EhcacheActionProviderTest.mock(Ehcache.class);
        @SuppressWarnings("unchecked")
        CacheRuntimeConfiguration<Long, String> cacheRuntimeConfiguration = EhcacheActionProviderTest.mock(CacheRuntimeConfiguration.class);
        Mockito.when(cacheRuntimeConfiguration.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
        Mockito.when(ehcache.getRuntimeConfiguration()).thenReturn(cacheRuntimeConfiguration);
        ehcacheActionProvider.register(new CacheBinding("cache-0", ehcache));
        Context context = Context.empty().with("cacheManagerName", "cache-manager-1").with("cacheName", "cache-0");
        try {
            ehcacheActionProvider.callAction(context, "get", Object.class, new Parameter(0L, Long.class.getName()));
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        Mockito.verify(ehcache, Mockito.times(0)).get(null);
    }
}

