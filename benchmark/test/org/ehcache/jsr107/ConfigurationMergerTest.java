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
package org.ehcache.jsr107;


import java.io.Closeable;
import java.time.Duration;
import java.util.Collection;
import java.util.function.Supplier;
import javax.cache.CacheException;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.config.copy.DefaultCopyProviderConfiguration;
import org.ehcache.impl.config.loaderwriter.DefaultCacheLoaderWriterConfiguration;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.jsr107.config.Jsr107Configuration;
import org.ehcache.jsr107.internal.DefaultJsr107Service;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.xml.XmlConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.creation.MockSettingsImpl;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.KEY;
import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


/**
 * ConfigurationMergerTest
 */
@SuppressWarnings("unchecked")
public class ConfigurationMergerTest {
    private ConfigurationMerger merger;

    private XmlConfiguration xmlConfiguration;

    private Jsr107Service jsr107Service;

    private Eh107CacheLoaderWriterProvider cacheLoaderWriterFactory;

    @Test
    public void mergeConfigNoTemplateNoLoaderWriter() {
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        ConfigurationMerger.ConfigHolder<Object, Object> configHolder = merger.mergeConfigurations("cache", configuration);
        Assert.assertThat(configHolder.cacheResources.getExpiryPolicy().getExpiryForCreation(42L, "Yay!"), Matchers.is(ExpiryPolicy.INFINITE));
        Assert.assertThat(configHolder.cacheResources.getCacheLoaderWriter(), Matchers.nullValue());
        Assert.assertThat(configHolder.useEhcacheLoaderWriter, Matchers.is(false));
        boolean storeByValue = false;
        Collection<ServiceConfiguration<?>> serviceConfigurations = configHolder.cacheConfiguration.getServiceConfigurations();
        for (ServiceConfiguration<?> serviceConfiguration : serviceConfigurations) {
            if (serviceConfiguration instanceof DefaultCopierConfiguration) {
                storeByValue = true;
                break;
            }
        }
        Assert.assertThat(storeByValue, Matchers.is(true));
    }

    @Test
    public void jsr107ExpiryGetsRegistered() {
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        ConfigurationMergerTest.RecordingFactory<CreatedExpiryPolicy> factory = factoryOf(new CreatedExpiryPolicy(Duration.FIVE_MINUTES));
        configuration.setExpiryPolicyFactory(factory);
        ConfigurationMerger.ConfigHolder<Object, Object> configHolder = merger.mergeConfigurations("Cache", configuration);
        Assert.assertThat(factory.called, Matchers.is(true));
        ExpiryPolicy<Object, Object> resourcesExpiry = configHolder.cacheResources.getExpiryPolicy();
        ExpiryPolicy<Object, Object> configExpiry = configHolder.cacheConfiguration.getExpiryPolicy();
        Assert.assertThat(configExpiry, Matchers.sameInstance(resourcesExpiry));
    }

    @Test
    public void jsr107LoaderGetsRegistered() {
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        CacheLoader<Object, Object> mock = Mockito.mock(CacheLoader.class);
        ConfigurationMergerTest.RecordingFactory<CacheLoader<Object, Object>> factory = factoryOf(mock);
        configuration.setReadThrough(true).setCacheLoaderFactory(factory);
        merger.mergeConfigurations("cache", configuration);
        Assert.assertThat(factory.called, Matchers.is(true));
        Mockito.verify(cacheLoaderWriterFactory).registerJsr107Loader(ArgumentMatchers.eq("cache"), ArgumentMatchers.<CacheLoaderWriter<Object, Object>>isNotNull());
    }

    @Test
    public void jsr107WriterGetsRegistered() {
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        CacheWriter<Object, Object> mock = Mockito.mock(CacheWriter.class);
        ConfigurationMergerTest.RecordingFactory<CacheWriter<Object, Object>> factory = factoryOf(mock);
        configuration.setWriteThrough(true).setCacheWriterFactory(factory);
        merger.mergeConfigurations("cache", configuration);
        Assert.assertThat(factory.called, Matchers.is(true));
        Mockito.verify(cacheLoaderWriterFactory).registerJsr107Loader(ArgumentMatchers.eq("cache"), ArgumentMatchers.<CacheLoaderWriter<Object, Object>>isNotNull());
    }

    @Test
    public void looksUpTemplateName() {
        merger.mergeConfigurations("cache", new MutableConfiguration());
        Mockito.verify(jsr107Service).getTemplateNameForCache("cache");
    }

    @Test
    public void loadsTemplateWhenNameFound() throws Exception {
        Mockito.when(jsr107Service.getTemplateNameForCache("cache")).thenReturn("cacheTemplate");
        merger.mergeConfigurations("cache", new MutableConfiguration());
        Mockito.verify(xmlConfiguration).newCacheConfigurationBuilderFromTemplate("cacheTemplate", Object.class, Object.class);
    }

    @Test
    public void jsr107ExpiryGetsOverriddenByTemplate() throws Exception {
        Mockito.when(jsr107Service.getTemplateNameForCache("cache")).thenReturn("cacheTemplate");
        Mockito.when(xmlConfiguration.newCacheConfigurationBuilderFromTemplate("cacheTemplate", Object.class, Object.class)).thenReturn(CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(5))));
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        ConfigurationMergerTest.RecordingFactory<CreatedExpiryPolicy> factory = factoryOf(new CreatedExpiryPolicy(Duration.FIVE_MINUTES));
        configuration.setExpiryPolicyFactory(factory);
        ConfigurationMerger.ConfigHolder<Object, Object> configHolder = merger.mergeConfigurations("cache", configuration);
        Assert.assertThat(factory.called, Matchers.is(false));
        Eh107Expiry<Object, Object> expiryPolicy = configHolder.cacheResources.getExpiryPolicy();
        ExpiryPolicy<? super Object, ? super Object> expiry = configHolder.cacheConfiguration.getExpiryPolicy();
        Assert.assertThat(expiryPolicy.getExpiryForAccess(42, () -> "Yay"), Matchers.is(expiry.getExpiryForAccess(42, () -> "Yay")));
        Assert.assertThat(expiryPolicy.getExpiryForUpdate(42, () -> "Yay", "Lala"), Matchers.is(expiry.getExpiryForUpdate(42, () -> "Yay", "Lala")));
        Assert.assertThat(expiryPolicy.getExpiryForCreation(42, "Yay"), Matchers.is(expiry.getExpiryForCreation(42, "Yay")));
    }

    @Test
    public void jsr107LoaderGetsOverriddenByTemplate() throws Exception {
        Mockito.when(jsr107Service.getTemplateNameForCache("cache")).thenReturn("cacheTemplate");
        Mockito.when(xmlConfiguration.newCacheConfigurationBuilderFromTemplate("cacheTemplate", Object.class, Object.class)).thenReturn(CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCacheLoaderWriterConfiguration(((Class) (null)))));
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        CacheLoader<Object, Object> mock = Mockito.mock(CacheLoader.class);
        ConfigurationMergerTest.RecordingFactory<CacheLoader<Object, Object>> factory = factoryOf(mock);
        configuration.setReadThrough(true).setCacheLoaderFactory(factory);
        ConfigurationMerger.ConfigHolder<Object, Object> configHolder = merger.mergeConfigurations("cache", configuration);
        Assert.assertThat(factory.called, Matchers.is(false));
        Assert.assertThat(configHolder.cacheResources.getCacheLoaderWriter(), Matchers.nullValue());
    }

    @Test
    public void jsr107StoreByValueGetsOverriddenByTemplate() throws Exception {
        CacheConfigurationBuilder<Object, Object> builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCopierConfiguration<Object>(((Class) (IdentityCopier.class)), KEY)).add(new DefaultCopierConfiguration<Object>(((Class) (IdentityCopier.class)), VALUE));
        Mockito.when(jsr107Service.getTemplateNameForCache("cache")).thenReturn("cacheTemplate");
        Mockito.when(xmlConfiguration.newCacheConfigurationBuilderFromTemplate("cacheTemplate", Object.class, Object.class)).thenReturn(builder);
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();// store-by-value by default

        ConfigurationMerger.ConfigHolder<Object, Object> configHolder = merger.mergeConfigurations("cache", configuration);
        boolean storeByValue = true;
        Collection<ServiceConfiguration<?>> serviceConfigurations = configHolder.cacheConfiguration.getServiceConfigurations();
        for (ServiceConfiguration<?> serviceConfiguration : serviceConfigurations) {
            if (serviceConfiguration instanceof DefaultCopierConfiguration) {
                DefaultCopierConfiguration<Object> copierConfig = ((DefaultCopierConfiguration<Object>) (serviceConfiguration));
                if (copierConfig.getClazz().isAssignableFrom(IdentityCopier.class))
                    storeByValue = false;

                break;
            }
        }
        Assert.assertThat(storeByValue, Matchers.is(false));
    }

    @Test
    public void jsr107LoaderInitFailureClosesExpiry() throws Exception {
        javax.cache.expiry.ExpiryPolicy expiryPolicy = Mockito.mock(javax.cache.expiry.ExpiryPolicy.class, new MockSettingsImpl<>().extraInterfaces(Closeable.class));
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        Factory<CacheLoader<Object, Object>> factory = throwingFactory();
        configuration.setExpiryPolicyFactory(factoryOf(expiryPolicy)).setReadThrough(true).setCacheLoaderFactory(factory);
        try {
            merger.mergeConfigurations("cache", configuration);
            Assert.fail("Loader factory should have thrown");
        } catch (CacheException mce) {
            Mockito.verify(((Closeable) (expiryPolicy))).close();
        }
    }

    @Test
    public void jsr107ListenerFactoryInitFailureClosesExpiryLoader() throws Exception {
        javax.cache.expiry.ExpiryPolicy expiryPolicy = Mockito.mock(javax.cache.expiry.ExpiryPolicy.class, new MockSettingsImpl<>().extraInterfaces(Closeable.class));
        CacheLoader<Object, Object> loader = Mockito.mock(CacheLoader.class, new MockSettingsImpl<>().extraInterfaces(Closeable.class));
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        configuration.setExpiryPolicyFactory(factoryOf(expiryPolicy)).setReadThrough(true).setCacheLoaderFactory(factoryOf(loader)).addCacheEntryListenerConfiguration(new ConfigurationMergerTest.ThrowingCacheEntryListenerConfiguration());
        try {
            merger.mergeConfigurations("cache", configuration);
            Assert.fail("Loader factory should have thrown");
        } catch (CacheException mce) {
            Mockito.verify(((Closeable) (expiryPolicy))).close();
            Mockito.verify(((Closeable) (loader))).close();
        }
    }

    @Test
    public void jsr107LoaderInitAlways() {
        CacheLoader<Object, Object> loader = Mockito.mock(CacheLoader.class);
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration();
        ConfigurationMergerTest.RecordingFactory<CacheLoader<Object, Object>> factory = factoryOf(loader);
        configuration.setCacheLoaderFactory(factory);
        ConfigurationMerger.ConfigHolder<Object, Object> configHolder = merger.mergeConfigurations("cache", configuration);
        Assert.assertThat(factory.called, Matchers.is(true));
        Assert.assertThat(configHolder.cacheResources.getCacheLoaderWriter(), Matchers.notNullValue());
        Assert.assertThat(configHolder.useEhcacheLoaderWriter, Matchers.is(false));
    }

    @Test
    public void setReadThroughWithoutLoaderFails() {
        MutableConfiguration<Long, String> config = new MutableConfiguration();
        config.setTypes(Long.class, String.class);
        config.setReadThrough(true);
        try {
            merger.mergeConfigurations("cache", config);
            Assert.fail("Expected exception as no CacheLoader factory is configured and read-through is enabled.");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("read-through"));
        }
    }

    @Test
    public void setWriteThroughWithoutWriterFails() {
        MutableConfiguration<Long, String> config = new MutableConfiguration();
        config.setTypes(Long.class, String.class);
        config.setWriteThrough(true);
        try {
            merger.mergeConfigurations("cache", config);
            Assert.fail("Expected exception as no CacheLoader factory is configured and read-through is enabled.");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("write-through"));
        }
    }

    @Test
    public void jsr107DefaultEh107IdentityCopierForImmutableTypes() {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(getClass().getResource("/ehcache-107-copiers-immutable-types.xml"));
        DefaultJsr107Service jsr107Service = new DefaultJsr107Service(ServiceUtils.findSingletonAmongst(Jsr107Configuration.class, xmlConfiguration.getServiceCreationConfigurations()));
        merger = new ConfigurationMerger(xmlConfiguration, jsr107Service, Mockito.mock(Eh107CacheLoaderWriterProvider.class));
        MutableConfiguration<Long, String> stringCacheConfiguration = new MutableConfiguration();
        stringCacheConfiguration.setTypes(Long.class, String.class);
        ConfigurationMerger.ConfigHolder<Long, String> configHolder1 = merger.mergeConfigurations("stringCache", stringCacheConfiguration);
        ConfigurationMergerTest.assertDefaultCopier(configHolder1.cacheConfiguration.getServiceConfigurations());
        MutableConfiguration<Long, Double> doubleCacheConfiguration = new MutableConfiguration();
        doubleCacheConfiguration.setTypes(Long.class, Double.class);
        ConfigurationMerger.ConfigHolder<Long, Double> configHolder2 = merger.mergeConfigurations("doubleCache", doubleCacheConfiguration);
        ConfigurationMergerTest.assertDefaultCopier(configHolder2.cacheConfiguration.getServiceConfigurations());
        MutableConfiguration<Long, Character> charCacheConfiguration = new MutableConfiguration();
        charCacheConfiguration.setTypes(Long.class, Character.class);
        ConfigurationMerger.ConfigHolder<Long, Character> configHolder3 = merger.mergeConfigurations("charCache", charCacheConfiguration);
        ConfigurationMergerTest.assertDefaultCopier(configHolder3.cacheConfiguration.getServiceConfigurations());
        MutableConfiguration<Long, Float> floatCacheConfiguration = new MutableConfiguration();
        floatCacheConfiguration.setTypes(Long.class, Float.class);
        ConfigurationMerger.ConfigHolder<Long, Float> configHolder4 = merger.mergeConfigurations("floatCache", floatCacheConfiguration);
        ConfigurationMergerTest.assertDefaultCopier(configHolder4.cacheConfiguration.getServiceConfigurations());
        MutableConfiguration<Long, Integer> integerCacheConfiguration = new MutableConfiguration();
        integerCacheConfiguration.setTypes(Long.class, Integer.class);
        ConfigurationMerger.ConfigHolder<Long, Integer> configHolder5 = merger.mergeConfigurations("integerCache", integerCacheConfiguration);
        ConfigurationMergerTest.assertDefaultCopier(configHolder5.cacheConfiguration.getServiceConfigurations());
    }

    @Test
    public void jsr107DefaultEh107IdentityCopierForImmutableTypesWithCMLevelDefaults() {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(getClass().getResource("/ehcache-107-immutable-types-cm-level-copiers.xml"));
        DefaultJsr107Service jsr107Service = new DefaultJsr107Service(ServiceUtils.findSingletonAmongst(Jsr107Configuration.class, xmlConfiguration.getServiceCreationConfigurations()));
        merger = new ConfigurationMerger(xmlConfiguration, jsr107Service, Mockito.mock(Eh107CacheLoaderWriterProvider.class));
        MutableConfiguration<Long, String> stringCacheConfiguration = new MutableConfiguration();
        stringCacheConfiguration.setTypes(Long.class, String.class);
        ConfigurationMerger.ConfigHolder<Long, String> configHolder1 = merger.mergeConfigurations("stringCache", stringCacheConfiguration);
        Assert.assertThat(configHolder1.cacheConfiguration.getServiceConfigurations().isEmpty(), Matchers.is(true));
        for (ServiceCreationConfiguration<?> serviceCreationConfiguration : xmlConfiguration.getServiceCreationConfigurations()) {
            if (serviceCreationConfiguration instanceof DefaultCopyProviderConfiguration) {
                DefaultCopyProviderConfiguration copierConfig = ((DefaultCopyProviderConfiguration) (serviceCreationConfiguration));
                Assert.assertThat(copierConfig.getDefaults().size(), Matchers.is(6));
                Assert.assertThat(copierConfig.getDefaults().get(Long.class).getClazz().isAssignableFrom(IdentityCopier.class), Matchers.is(true));
                Assert.assertThat(copierConfig.getDefaults().get(String.class).getClazz().isAssignableFrom(Eh107IdentityCopier.class), Matchers.is(true));
                Assert.assertThat(copierConfig.getDefaults().get(Float.class).getClazz().isAssignableFrom(Eh107IdentityCopier.class), Matchers.is(true));
                Assert.assertThat(copierConfig.getDefaults().get(Double.class).getClazz().isAssignableFrom(Eh107IdentityCopier.class), Matchers.is(true));
                Assert.assertThat(copierConfig.getDefaults().get(Character.class).getClazz().isAssignableFrom(Eh107IdentityCopier.class), Matchers.is(true));
                Assert.assertThat(copierConfig.getDefaults().get(Integer.class).getClazz().isAssignableFrom(Eh107IdentityCopier.class), Matchers.is(true));
            }
        }
    }

    @Test
    public void jsr107DefaultEh107IdentityCopierForImmutableTypesWithoutTemplates() {
        MutableConfiguration<Long, String> stringCacheConfiguration = new MutableConfiguration();
        stringCacheConfiguration.setTypes(Long.class, String.class);
        ConfigurationMerger.ConfigHolder<Long, String> configHolder1 = merger.mergeConfigurations("stringCache", stringCacheConfiguration);
        ConfigurationMergerTest.assertDefaultCopier(configHolder1.cacheConfiguration.getServiceConfigurations());
    }

    private static class RecordingFactory<T> implements Factory<T> {
        private static final long serialVersionUID = 1L;

        private final T instance;

        boolean called;

        RecordingFactory(T instance) {
            this.instance = instance;
        }

        @Override
        public T create() {
            called = true;
            return instance;
        }
    }

    private static class ThrowingCacheEntryListenerConfiguration implements CacheEntryListenerConfiguration<Object, Object> {
        private static final long serialVersionUID = 1L;

        @Override
        public Factory<CacheEntryListener<? super Object, ? super Object>> getCacheEntryListenerFactory() {
            throw new UnsupportedOperationException("BOOM");
        }

        @Override
        public boolean isOldValueRequired() {
            throw new UnsupportedOperationException("BOOM");
        }

        @Override
        public Factory<CacheEntryEventFilter<? super Object, ? super Object>> getCacheEntryEventFilterFactory() {
            throw new UnsupportedOperationException("BOOM");
        }

        @Override
        public boolean isSynchronous() {
            throw new UnsupportedOperationException("BOOM");
        }
    }
}

