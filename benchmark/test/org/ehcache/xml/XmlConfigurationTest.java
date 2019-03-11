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
package org.ehcache.xml;


import com.pany.ehcache.MyExpiry;
import com.pany.ehcache.copier.AnotherPersonCopier;
import com.pany.ehcache.copier.Description;
import com.pany.ehcache.copier.DescriptionCopier;
import com.pany.ehcache.copier.Person;
import com.pany.ehcache.copier.PersonCopier;
import com.pany.ehcache.serializer.TestSerializer;
import com.pany.ehcache.serializer.TestSerializer2;
import com.pany.ehcache.serializer.TestSerializer3;
import com.pany.ehcache.serializer.TestSerializer4;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourceType;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.core.util.ClassLoading;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.config.copy.DefaultCopyProviderConfiguration;
import org.ehcache.impl.config.event.DefaultCacheEventListenerConfiguration;
import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.impl.config.resilience.DefaultResilienceStrategyConfiguration;
import org.ehcache.impl.config.serializer.DefaultSerializationProviderConfiguration;
import org.ehcache.impl.config.serializer.DefaultSerializerConfiguration;
import org.ehcache.impl.config.store.disk.OffHeapDiskStoreConfiguration;
import org.ehcache.impl.config.store.heap.DefaultSizeOfEngineConfiguration;
import org.ehcache.impl.config.store.heap.DefaultSizeOfEngineProviderConfiguration;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.spi.copy.Copier;
import org.ehcache.spi.loaderwriter.WriteBehindConfiguration;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;
import org.xmlunit.diff.ElementSelectors;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;
import static org.ehcache.impl.config.serializer.DefaultSerializerConfiguration.Type.KEY;
import static org.ehcache.impl.config.serializer.DefaultSerializerConfiguration.Type.VALUE;


/**
 *
 *
 * @author Chris Dennis
 */
public class XmlConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDefaultTypesConfig() throws Exception {
        URL resource = XmlConfigurationTest.class.getResource("/configs/defaultTypes-cache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(resource);
        Assert.assertThat(xmlConfig.getCacheConfigurations().keySet(), IsCollectionContaining.hasItem("foo"));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("foo").getKeyType(), IsSame.sameInstance(((Class) (Object.class))));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("foo").getValueType(), IsSame.sameInstance(((Class) (Object.class))));
        Assert.assertThat(xmlConfig.getCacheConfigurations().keySet(), IsCollectionContaining.hasItem("bar"));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("bar").getKeyType(), IsSame.sameInstance(((Class) (Number.class))));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("bar").getValueType(), IsSame.sameInstance(((Class) (Object.class))));
        Assert.assertThat(xmlConfig.newCacheConfigurationBuilderFromTemplate("example", Object.class, Object.class, ResourcePoolsBuilder.heap(10)), CoreMatchers.notNullValue());
        // Allow the key/value to be assignable for xml configuration in case of type definition in template class
        Assert.assertThat(xmlConfig.newCacheConfigurationBuilderFromTemplate("example", Number.class, Object.class, ResourcePoolsBuilder.heap(10)), CoreMatchers.notNullValue());
    }

    @Test
    public void testNonExistentAdvisorClassInCacheThrowsException() throws Exception {
        try {
            new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/nonExistentAdvisor-cache.xml"));
            Assert.fail();
        } catch (XmlConfigurationException xce) {
            Assert.assertThat(xce.getCause(), IsInstanceOf.instanceOf(ClassNotFoundException.class));
        }
    }

    @Test
    public void testNonExistentAdvisorClassInTemplateThrowsException() throws Exception {
        try {
            new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/nonExistentAdvisor-template.xml"));
            Assert.fail();
        } catch (XmlConfigurationException xce) {
            Assert.assertThat(xce.getCause(), IsInstanceOf.instanceOf(ClassNotFoundException.class));
        }
    }

    @Test
    public void testOneServiceConfig() throws Exception {
        URL resource = XmlConfigurationTest.class.getResource("/configs/one-service.xml");
        Configuration config = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(config.getServiceCreationConfigurations(), IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(BarConfiguration.class)));
        Assert.assertThat(config.getCacheConfigurations().keySet(), hasSize(0));
    }

    @Test
    public void testOneCacheConfig() throws Exception {
        URL resource = XmlConfigurationTest.class.getResource("/configs/one-cache.xml");
        Configuration config = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(config.getServiceCreationConfigurations(), hasSize(0));
        Assert.assertThat(config.getCacheConfigurations().keySet(), IsCollectionContaining.hasItem("bar"));
        Assert.assertThat(config.getCacheConfigurations().get("bar").getServiceConfigurations(), IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(FooConfiguration.class)));
    }

    @Test
    public void testAllExtensions() {
        URL resource = XmlConfigurationTest.class.getResource("/configs/all-extensions.xml");
        Configuration config = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(config.getServiceCreationConfigurations(), IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(BarConfiguration.class)));
        CacheConfiguration<?, ?> cacheConfiguration = config.getCacheConfigurations().get("fancy");
        Assert.assertThat(cacheConfiguration.getServiceConfigurations(), IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(FooConfiguration.class)));
        Assert.assertThat(cacheConfiguration.getResourcePools().getResourceTypeSet(), IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(BazResource.Type.class)));
    }

    @Test
    public void testOneCacheConfigWithTemplate() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/template-cache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(resource);
        Assert.assertThat(xmlConfig.getServiceCreationConfigurations(), hasSize(0));
        Assert.assertThat(xmlConfig.getCacheConfigurations().keySet(), IsCollectionContaining.hasItem("bar"));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("bar").getServiceConfigurations(), IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(FooConfiguration.class)));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("bar").getKeyType(), IsSame.sameInstance(((Class) (Number.class))));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("bar").getValueType(), IsSame.sameInstance(((Class) (String.class))));
        final CacheConfigurationBuilder<String, String> example = xmlConfig.newCacheConfigurationBuilderFromTemplate("example", String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(5, EntryUnit.ENTRIES));
        Assert.assertThat(example.build().getExpiryPolicy(), CoreMatchers.equalTo(((ExpiryPolicy) (ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(30))))));
        try {
            xmlConfig.newCacheConfigurationBuilderFromTemplate("example", String.class, Number.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("CacheTemplate 'example' declares value type of java.lang.String. Provided: class java.lang.Number"));
        }
        try {
            xmlConfig.newCacheConfigurationBuilderFromTemplate("example", Number.class, String.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("CacheTemplate 'example' declares key type of java.lang.String. Provided: class java.lang.Number"));
        }
        Assert.assertThat(xmlConfig.newCacheConfigurationBuilderFromTemplate("bar", Object.class, Object.class), CoreMatchers.nullValue());
    }

    @Test
    public void testExpiryIsParsed() throws Exception {
        URL resource = XmlConfigurationTest.class.getResource("/configs/expiry-caches.xml");
        final XmlConfiguration xmlConfiguration = new XmlConfiguration(resource);
        ExpiryPolicy<?, ?> expiry = xmlConfiguration.getCacheConfigurations().get("none").getExpiryPolicy();
        ExpiryPolicy<?, ?> value = ExpiryPolicyBuilder.noExpiration();
        Assert.assertThat(expiry, Is.is(value));
        expiry = xmlConfiguration.getCacheConfigurations().get("notSet").getExpiryPolicy();
        value = ExpiryPolicyBuilder.noExpiration();
        Assert.assertThat(expiry, Is.is(value));
        expiry = xmlConfiguration.getCacheConfigurations().get("class").getExpiryPolicy();
        Assert.assertThat(expiry, CoreMatchers.instanceOf(MyExpiry.class));
        expiry = xmlConfiguration.getCacheConfigurations().get("deprecatedClass").getExpiryPolicy();
        Assert.assertThat(expiry.getExpiryForCreation(null, null), Is.is(Duration.ofSeconds(42)));
        Assert.assertThat(expiry.getExpiryForAccess(null, () -> null), Is.is(Duration.ofSeconds(42)));
        Assert.assertThat(expiry.getExpiryForUpdate(null, () -> null, null), Is.is(Duration.ofSeconds(42)));
        expiry = xmlConfiguration.getCacheConfigurations().get("tti").getExpiryPolicy();
        value = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(500));
        Assert.assertThat(expiry, CoreMatchers.equalTo(value));
        expiry = xmlConfiguration.getCacheConfigurations().get("ttl").getExpiryPolicy();
        value = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(30));
        Assert.assertThat(expiry, CoreMatchers.equalTo(value));
    }

    @Test
    public void testInvalidCoreConfiguration() throws Exception {
        try {
            new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/invalid-core.xml"));
            Assert.fail();
        } catch (XmlConfigurationException xce) {
            SAXParseException e = ((SAXParseException) (xce.getCause()));
            Assert.assertThat(e.getLineNumber(), Is.is(5));
            Assert.assertThat(e.getColumnNumber(), Is.is(29));
        }
    }

    @Test
    public void testInvalidServiceConfiguration() throws Exception {
        try {
            new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/invalid-service.xml"));
            Assert.fail();
        } catch (XmlConfigurationException xce) {
            SAXParseException e = ((SAXParseException) (xce.getCause()));
            Assert.assertThat(e.getLineNumber(), Is.is(6));
            Assert.assertThat(e.getColumnNumber(), Is.is(15));
        }
    }

    @Test
    public void testTwoCachesWithSameAlias() {
        try {
            new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/invalid-two-caches.xml"));
            Assert.fail("Two caches with the same alias should not be allowed");
        } catch (XmlConfigurationException e) {
            Assert.assertThat(e.getMessage(), Is.is("Two caches defined with the same alias: foo"));
        }
    }

    @Test
    public void testExposesProperURL() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/one-cache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(resource);
        Assert.assertThat(xmlConfig.getURL(), CoreMatchers.equalTo(resource));
    }

    @Test
    public void testResourcesCaches() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/resources-caches.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        CacheConfiguration<?, ?> tieredCacheConfig = xmlConfig.getCacheConfigurations().get("tiered");
        Assert.assertThat(tieredCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(10L));
        Assert.assertThat(tieredCacheConfig.getResourcePools().getPoolForResource(DISK).getSize(), CoreMatchers.equalTo(100L));
        Assert.assertThat(tieredCacheConfig.getResourcePools().getPoolForResource(DISK).isPersistent(), Is.is(false));
        CacheConfiguration<?, ?> tieredPersistentCacheConfig = xmlConfig.getCacheConfigurations().get("tieredPersistent");
        Assert.assertThat(tieredPersistentCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(10L));
        Assert.assertThat(tieredPersistentCacheConfig.getResourcePools().getPoolForResource(DISK).getSize(), CoreMatchers.equalTo(100L));
        Assert.assertThat(tieredPersistentCacheConfig.getResourcePools().getPoolForResource(DISK).isPersistent(), Is.is(true));
        CacheConfiguration<?, ?> tieredOffHeapCacheConfig = xmlConfig.getCacheConfigurations().get("tieredOffHeap");
        Assert.assertThat(tieredOffHeapCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(10L));
        Assert.assertThat(tieredOffHeapCacheConfig.getResourcePools().getPoolForResource(OFFHEAP).getSize(), CoreMatchers.equalTo(10L));
        Assert.assertThat(tieredOffHeapCacheConfig.getResourcePools().getPoolForResource(OFFHEAP).getUnit(), CoreMatchers.equalTo(MemoryUnit.MB));
        CacheConfiguration<?, ?> explicitHeapOnlyCacheConfig = xmlConfig.getCacheConfigurations().get("explicitHeapOnly");
        Assert.assertThat(explicitHeapOnlyCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(15L));
        Assert.assertThat(explicitHeapOnlyCacheConfig.getResourcePools().getPoolForResource(DISK), Is.is(CoreMatchers.nullValue()));
        CacheConfiguration<?, ?> implicitHeapOnlyCacheConfig = xmlConfig.getCacheConfigurations().get("directHeapOnly");
        Assert.assertThat(implicitHeapOnlyCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(25L));
        Assert.assertThat(implicitHeapOnlyCacheConfig.getResourcePools().getPoolForResource(DISK), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testResourcesTemplates() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/resources-templates.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(resource);
        CacheConfigurationBuilder<String, String> tieredResourceTemplate = xmlConfig.newCacheConfigurationBuilderFromTemplate("tieredResourceTemplate", String.class, String.class);
        Assert.assertThat(tieredResourceTemplate.build().getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(5L));
        Assert.assertThat(tieredResourceTemplate.build().getResourcePools().getPoolForResource(DISK).getSize(), CoreMatchers.equalTo(50L));
        Assert.assertThat(tieredResourceTemplate.build().getResourcePools().getPoolForResource(DISK).isPersistent(), Is.is(false));
        CacheConfigurationBuilder<String, String> persistentTieredResourceTemplate = xmlConfig.newCacheConfigurationBuilderFromTemplate("persistentTieredResourceTemplate", String.class, String.class);
        Assert.assertThat(persistentTieredResourceTemplate.build().getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(5L));
        Assert.assertThat(persistentTieredResourceTemplate.build().getResourcePools().getPoolForResource(DISK).getSize(), CoreMatchers.equalTo(50L));
        Assert.assertThat(persistentTieredResourceTemplate.build().getResourcePools().getPoolForResource(DISK).isPersistent(), Is.is(true));
        CacheConfigurationBuilder<String, String> tieredOffHeapResourceTemplate = xmlConfig.newCacheConfigurationBuilderFromTemplate("tieredOffHeapResourceTemplate", String.class, String.class);
        Assert.assertThat(tieredOffHeapResourceTemplate.build().getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(5L));
        Assert.assertThat(tieredOffHeapResourceTemplate.build().getResourcePools().getPoolForResource(OFFHEAP).getSize(), CoreMatchers.equalTo(50L));
        Assert.assertThat(tieredOffHeapResourceTemplate.build().getResourcePools().getPoolForResource(OFFHEAP).getUnit(), CoreMatchers.equalTo(MemoryUnit.MB));
        CacheConfigurationBuilder<String, String> explicitHeapResourceTemplate = xmlConfig.newCacheConfigurationBuilderFromTemplate("explicitHeapResourceTemplate", String.class, String.class);
        Assert.assertThat(explicitHeapResourceTemplate.build().getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(15L));
        Assert.assertThat(explicitHeapResourceTemplate.build().getResourcePools().getPoolForResource(DISK), Is.is(CoreMatchers.nullValue()));
        CacheConfiguration<?, ?> tieredCacheConfig = xmlConfig.getCacheConfigurations().get("templatedTieredResource");
        Assert.assertThat(tieredCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(5L));
        Assert.assertThat(tieredCacheConfig.getResourcePools().getPoolForResource(DISK).getSize(), CoreMatchers.equalTo(50L));
        CacheConfiguration<?, ?> explicitHeapOnlyCacheConfig = xmlConfig.getCacheConfigurations().get("templatedExplicitHeapResource");
        Assert.assertThat(explicitHeapOnlyCacheConfig.getResourcePools().getPoolForResource(HEAP).getSize(), CoreMatchers.equalTo(15L));
        Assert.assertThat(explicitHeapOnlyCacheConfig.getResourcePools().getPoolForResource(DISK), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testNoClassLoaderSpecified() throws Exception {
        URL resource = XmlConfigurationTest.class.getResource("/configs/one-cache.xml");
        XmlConfiguration config = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertSame(config.getClassLoader(), ClassLoading.getDefaultClassLoader());
        Assert.assertNull(config.getCacheConfigurations().get("bar").getClassLoader());
    }

    @Test
    public void testClassLoaderSpecified() throws Exception {
        ClassLoader cl = new ClassLoader() {};
        URL resource = XmlConfigurationTest.class.getResource("/configs/one-cache.xml");
        XmlConfiguration config = new XmlConfiguration(new XmlConfiguration(resource, cl));
        Assert.assertSame(cl, config.getClassLoader());
        Assert.assertNull(config.getCacheConfigurations().get("bar").getClassLoader());
    }

    @Test
    public void testCacheClassLoaderSpecified() throws Exception {
        ClassLoader cl = new ClassLoader() {};
        ClassLoader cl2 = new ClassLoader() {};
        Assert.assertNotSame(cl, cl2);
        Map<String, ClassLoader> loaders = new HashMap<>();
        loaders.put("bar", cl2);
        URL resource = XmlConfigurationTest.class.getResource("/configs/one-cache.xml");
        XmlConfiguration config = new XmlConfiguration(new XmlConfiguration(resource, cl, loaders));
        Assert.assertSame(cl, config.getClassLoader());
        Assert.assertSame(cl2, config.getCacheConfigurations().get("bar").getClassLoader());
    }

    @Test
    public void testDefaultSerializerConfiguration() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/default-serializer.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(xmlConfig.getServiceCreationConfigurations().size(), Is.is(1));
        ServiceCreationConfiguration<?> configuration = xmlConfig.getServiceCreationConfigurations().iterator().next();
        Assert.assertThat(configuration, IsInstanceOf.instanceOf(DefaultSerializationProviderConfiguration.class));
        DefaultSerializationProviderConfiguration factoryConfiguration = ((DefaultSerializationProviderConfiguration) (configuration));
        Assert.assertThat(factoryConfiguration.getDefaultSerializers().size(), Is.is(4));
        Assert.assertThat(factoryConfiguration.getDefaultSerializers().get(CharSequence.class), Matchers.equalTo(TestSerializer.class));
        Assert.assertThat(factoryConfiguration.getDefaultSerializers().get(Number.class), Matchers.equalTo(TestSerializer2.class));
        Assert.assertThat(factoryConfiguration.getDefaultSerializers().get(Long.class), Matchers.equalTo(TestSerializer3.class));
        Assert.assertThat(factoryConfiguration.getDefaultSerializers().get(Integer.class), Matchers.equalTo(TestSerializer4.class));
        List<ServiceConfiguration<?>> orderedServiceConfigurations = new ArrayList<>(xmlConfig.getCacheConfigurations().get("baz").getServiceConfigurations());
        // order services by class name so the test can rely on some sort of ordering
        orderedServiceConfigurations.sort(Comparator.comparing(( o) -> o.getClass().getName()));
        Iterator<ServiceConfiguration<?>> it = orderedServiceConfigurations.iterator();
        DefaultSerializerConfiguration<?> keySerializationProviderConfiguration = ((DefaultSerializerConfiguration<?>) (it.next()));
        Assert.assertThat(keySerializationProviderConfiguration.getType(), Matchers.isIn(new DefaultSerializerConfiguration.Type[]{ KEY, VALUE }));
    }

    @Test
    public void testThreadPoolsConfiguration() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/thread-pools.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(xmlConfig.getServiceCreationConfigurations(), contains(IsInstanceOf.instanceOf(PooledExecutionServiceConfiguration.class)));
        PooledExecutionServiceConfiguration configuration = ((PooledExecutionServiceConfiguration) (xmlConfig.getServiceCreationConfigurations().iterator().next()));
        Assert.assertThat(configuration.getPoolConfigurations().keySet(), Matchers.containsInAnyOrder("big", "small"));
        PooledExecutionServiceConfiguration.PoolConfiguration small = configuration.getPoolConfigurations().get("small");
        Assert.assertThat(small.minSize(), Is.is(1));
        Assert.assertThat(small.maxSize(), Is.is(1));
        PooledExecutionServiceConfiguration.PoolConfiguration big = configuration.getPoolConfigurations().get("big");
        Assert.assertThat(big.minSize(), Is.is(4));
        Assert.assertThat(big.maxSize(), Is.is(32));
        Assert.assertThat(configuration.getDefaultPoolAlias(), Is.is("big"));
    }

    @Test
    public void testCacheCopierConfiguration() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/cache-copiers.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(xmlConfig.getServiceCreationConfigurations().size(), Is.is(1));
        ServiceCreationConfiguration<?> configuration = xmlConfig.getServiceCreationConfigurations().iterator().next();
        Assert.assertThat(configuration, IsInstanceOf.instanceOf(DefaultCopyProviderConfiguration.class));
        DefaultCopyProviderConfiguration factoryConfiguration = ((DefaultCopyProviderConfiguration) (configuration));
        Assert.assertThat(factoryConfiguration.getDefaults().size(), Is.is(2));
        Assert.assertThat(factoryConfiguration.getDefaults().get(Description.class).getClazz(), Matchers.<Class<? extends Copier<?>>>equalTo(DescriptionCopier.class));
        Assert.assertThat(factoryConfiguration.getDefaults().get(Person.class).getClazz(), Matchers.<Class<? extends Copier<?>>>equalTo(PersonCopier.class));
        Collection<ServiceConfiguration<?>> configs = xmlConfig.getCacheConfigurations().get("baz").getServiceConfigurations();
        for (ServiceConfiguration<?> config : configs) {
            if (config instanceof DefaultCopierConfiguration) {
                DefaultCopierConfiguration<?> copierConfig = ((DefaultCopierConfiguration<?>) (config));
                if ((copierConfig.getType()) == (DefaultCopierConfiguration.Type.KEY)) {
                    Assert.assertEquals(SerializingCopier.class, copierConfig.getClazz());
                } else {
                    Assert.assertEquals(AnotherPersonCopier.class, copierConfig.getClazz());
                }
            }
        }
        configs = xmlConfig.getCacheConfigurations().get("bak").getServiceConfigurations();
        for (ServiceConfiguration<?> config : configs) {
            if (config instanceof DefaultCopierConfiguration) {
                DefaultCopierConfiguration<?> copierConfig = ((DefaultCopierConfiguration<?>) (config));
                if ((copierConfig.getType()) == (DefaultCopierConfiguration.Type.KEY)) {
                    Assert.assertEquals(SerializingCopier.class, copierConfig.getClazz());
                } else {
                    Assert.assertEquals(AnotherPersonCopier.class, copierConfig.getClazz());
                }
            }
        }
    }

    @Test
    public void testPersistenceConfig() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/persistence-config.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        ServiceCreationConfiguration<?> serviceConfig = xmlConfig.getServiceCreationConfigurations().iterator().next();
        Assert.assertThat(serviceConfig, IsInstanceOf.instanceOf(DefaultPersistenceConfiguration.class));
        DefaultPersistenceConfiguration persistenceConfiguration = ((DefaultPersistenceConfiguration) (serviceConfig));
        Assert.assertThat(persistenceConfiguration.getRootDirectory(), Is.is(new File("   \n\t/my/caching/persistence  directory\r\n      ")));
    }

    @Test
    public void testPersistenceConfigXmlPersistencePathHasWhitespaces() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/persistence-config.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(resource.toURI()));
        Element persistence = ((Element) (doc.getElementsByTagName("ehcache:persistence").item(0)));
        String directoryValue = persistence.getAttribute("directory");
        Assert.assertThat(directoryValue, StringContains.containsString(" "));
        Assert.assertThat(directoryValue, StringContains.containsString("\r"));
        Assert.assertThat(directoryValue, StringContains.containsString("\n"));
        Assert.assertThat(directoryValue, StringContains.containsString("\t"));
    }

    @Test
    public void testWriteBehind() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/writebehind-cache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(resource);
        Collection<ServiceConfiguration<?>> serviceConfiguration = xmlConfig.getCacheConfigurations().get("bar").getServiceConfigurations();
        Assert.assertThat(serviceConfiguration, IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(WriteBehindConfiguration.class)));
        serviceConfiguration = xmlConfig.newCacheConfigurationBuilderFromTemplate("example", Number.class, String.class).build().getServiceConfigurations();
        Assert.assertThat(serviceConfiguration, IsCollectionContaining.hasItem(IsInstanceOf.instanceOf(WriteBehindConfiguration.class)));
        for (ServiceConfiguration<?> configuration : serviceConfiguration) {
            if (configuration instanceof WriteBehindConfiguration) {
                WriteBehindConfiguration.BatchingConfiguration batchingConfig = ((WriteBehindConfiguration) (configuration)).getBatchingConfiguration();
                Assert.assertThat(batchingConfig.getMaxDelay(), Is.is(10L));
                Assert.assertThat(batchingConfig.getMaxDelayUnit(), Is.is(TimeUnit.SECONDS));
                Assert.assertThat(batchingConfig.isCoalescing(), Is.is(false));
                Assert.assertThat(batchingConfig.getBatchSize(), Is.is(2));
                Assert.assertThat(((WriteBehindConfiguration) (configuration)).getConcurrency(), Is.is(1));
                Assert.assertThat(((WriteBehindConfiguration) (configuration)).getMaxQueueSize(), Is.is(10));
                break;
            }
        }
    }

    @Test
    public void testCacheEventListener() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/ehcache-cacheEventListener.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        Assert.assertThat(xmlConfig.getCacheConfigurations().size(), Is.is(2));
        Collection<?> configuration = xmlConfig.getCacheConfigurations().get("bar").getServiceConfigurations();
        checkListenerConfigurationExists(configuration);
    }

    @Test
    public void testCacheEventListenerThroughTemplate() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/ehcache-cacheEventListener.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(resource);
        CacheConfiguration<?, ?> cacheConfig = xmlConfig.getCacheConfigurations().get("template1");
        checkListenerConfigurationExists(cacheConfig.getServiceConfigurations());
        CacheConfigurationBuilder<Number, String> templateConfig = xmlConfig.newCacheConfigurationBuilderFromTemplate("example", Number.class, String.class);
        Assert.assertThat(templateConfig.getExistingServiceConfiguration(DefaultCacheEventListenerConfiguration.class), CoreMatchers.notNullValue());
    }

    @Test
    public void testDefaulSerializerXmlsSerializersValueHasWhitespaces() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/default-serializer.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(resource.toURI()));
        NodeList nList = doc.getElementsByTagName("ehcache:serializer");
        Assert.assertThat(nList.item(2).getFirstChild().getNodeValue(), StringContains.containsString(" "));
        Assert.assertThat(nList.item(2).getFirstChild().getNodeValue(), StringContains.containsString("\n"));
        Assert.assertThat(nList.item(3).getFirstChild().getNodeValue(), StringContains.containsString(" "));
        Assert.assertThat(nList.item(3).getFirstChild().getNodeValue(), StringContains.containsString("\n"));
        nList = doc.getElementsByTagName("ehcache:key-type");
        Assert.assertThat(nList.item(0).getFirstChild().getNodeValue(), StringContains.containsString(" "));
        Assert.assertThat(nList.item(0).getFirstChild().getNodeValue(), StringContains.containsString("\n"));
        Assert.assertThat(nList.item(1).getFirstChild().getNodeValue(), StringContains.containsString(" "));
        Assert.assertThat(nList.item(1).getFirstChild().getNodeValue(), StringContains.containsString("\n"));
        nList = doc.getElementsByTagName("ehcache:value-type");
        Assert.assertThat(nList.item(0).getFirstChild().getNodeValue(), StringContains.containsString(" "));
        Assert.assertThat(nList.item(0).getFirstChild().getNodeValue(), StringContains.containsString("\n"));
        Assert.assertThat(nList.item(1).getFirstChild().getNodeValue(), StringContains.containsString(" "));
        Assert.assertThat(nList.item(1).getFirstChild().getNodeValue(), StringContains.containsString("\n"));
    }

    @Test
    public void testDiskStoreSettings() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/resources-caches.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        CacheConfiguration<?, ?> cacheConfig = xmlConfig.getCacheConfigurations().get("tiered");
        OffHeapDiskStoreConfiguration diskConfig = ServiceUtils.findSingletonAmongst(OffHeapDiskStoreConfiguration.class, cacheConfig.getServiceConfigurations());
        Assert.assertThat(diskConfig.getThreadPoolAlias(), Is.is("some-pool"));
        Assert.assertThat(diskConfig.getWriterConcurrency(), Is.is(2));
        Assert.assertThat(diskConfig.getDiskSegments(), Is.is(4));
    }

    @Test
    public void testNullUrlInConstructorThrowsNPE() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The url can not be null");
        new XmlConfiguration(((URL) (null)), Mockito.mock(ClassLoader.class), getClassLoaderMapMock());
    }

    @Test
    public void testNullClassLoaderInConstructorThrowsNPE() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The classLoader can not be null");
        new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/one-cache.xml"), null, getClassLoaderMapMock());
    }

    @Test
    public void testNullCacheClassLoaderMapInConstructorThrowsNPE() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The cacheClassLoaders map can not be null");
        new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/one-cache.xml"), Mockito.mock(ClassLoader.class), null);
    }

    @Test
    public void testSizeOfEngineLimits() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/sizeof-engine.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        DefaultSizeOfEngineProviderConfiguration sizeOfEngineProviderConfig = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineProviderConfiguration.class, xmlConfig.getServiceCreationConfigurations());
        Assert.assertThat(sizeOfEngineProviderConfig, CoreMatchers.notNullValue());
        Assert.assertEquals(sizeOfEngineProviderConfig.getMaxObjectGraphSize(), 200);
        Assert.assertEquals(sizeOfEngineProviderConfig.getMaxObjectSize(), 100000);
        CacheConfiguration<?, ?> cacheConfig = xmlConfig.getCacheConfigurations().get("usesDefaultSizeOfEngine");
        DefaultSizeOfEngineConfiguration sizeOfEngineConfig = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineConfiguration.class, cacheConfig.getServiceConfigurations());
        Assert.assertThat(sizeOfEngineConfig, CoreMatchers.nullValue());
        CacheConfiguration<?, ?> cacheConfig1 = xmlConfig.getCacheConfigurations().get("usesConfiguredInCache");
        DefaultSizeOfEngineConfiguration sizeOfEngineConfig1 = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineConfiguration.class, cacheConfig1.getServiceConfigurations());
        Assert.assertThat(sizeOfEngineConfig1, CoreMatchers.notNullValue());
        Assert.assertEquals(sizeOfEngineConfig1.getMaxObjectGraphSize(), 500);
        Assert.assertEquals(sizeOfEngineConfig1.getMaxObjectSize(), 200000);
        CacheConfiguration<?, ?> cacheConfig2 = xmlConfig.getCacheConfigurations().get("usesPartialOneConfiguredInCache");
        DefaultSizeOfEngineConfiguration sizeOfEngineConfig2 = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineConfiguration.class, cacheConfig2.getServiceConfigurations());
        Assert.assertThat(sizeOfEngineConfig2, CoreMatchers.notNullValue());
        Assert.assertThat(sizeOfEngineConfig2.getMaxObjectGraphSize(), Is.is(500L));
        Assert.assertThat(sizeOfEngineConfig2.getMaxObjectSize(), Is.is(Long.MAX_VALUE));
        CacheConfiguration<?, ?> cacheConfig3 = xmlConfig.getCacheConfigurations().get("usesPartialTwoConfiguredInCache");
        DefaultSizeOfEngineConfiguration sizeOfEngineConfig3 = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineConfiguration.class, cacheConfig3.getServiceConfigurations());
        Assert.assertThat(sizeOfEngineConfig3, CoreMatchers.notNullValue());
        Assert.assertThat(sizeOfEngineConfig3.getMaxObjectGraphSize(), Is.is(1000L));
        Assert.assertThat(sizeOfEngineConfig3.getMaxObjectSize(), Is.is(200000L));
    }

    @Test
    public void testCacheManagerDefaultObjectGraphSize() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/sizeof-engine-cm-defaults-one.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        DefaultSizeOfEngineProviderConfiguration sizeOfEngineProviderConfig = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineProviderConfiguration.class, xmlConfig.getServiceCreationConfigurations());
        Assert.assertThat(sizeOfEngineProviderConfig, CoreMatchers.notNullValue());
        Assert.assertThat(sizeOfEngineProviderConfig.getMaxObjectGraphSize(), Is.is(1000L));
        Assert.assertThat(sizeOfEngineProviderConfig.getMaxObjectSize(), Is.is(100000L));
    }

    @Test
    public void testCacheManagerDefaultObjectSize() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/sizeof-engine-cm-defaults-two.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        DefaultSizeOfEngineProviderConfiguration sizeOfEngineProviderConfig = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineProviderConfiguration.class, xmlConfig.getServiceCreationConfigurations());
        Assert.assertThat(sizeOfEngineProviderConfig, CoreMatchers.notNullValue());
        Assert.assertThat(sizeOfEngineProviderConfig.getMaxObjectGraphSize(), Is.is(200L));
        Assert.assertThat(sizeOfEngineProviderConfig.getMaxObjectSize(), Is.is(Long.MAX_VALUE));
    }

    @Test
    public void testCustomResource() throws Exception {
        try {
            new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/custom-resource.xml"));
            Assert.fail();
        } catch (XmlConfigurationException xce) {
            Assert.assertThat(xce.getMessage(), StringContains.containsString("Can't find parser for element"));
        }
    }

    @Test
    public void testResilienceStrategy() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/resilience-config.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        CacheConfiguration<?, ?> cacheConfig = xmlConfig.getCacheConfigurations().get("ni");
        DefaultResilienceStrategyConfiguration resilienceStrategyConfiguration = ServiceUtils.findSingletonAmongst(DefaultResilienceStrategyConfiguration.class, cacheConfig.getServiceConfigurations());
        Assert.assertThat(resilienceStrategyConfiguration.getClazz(), IsSame.sameInstance(NiResilience.class));
    }

    @Test
    public void testResilienceStrategyFromTemplate() throws Exception {
        final URL resource = XmlConfigurationTest.class.getResource("/configs/resilience-config.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(new XmlConfiguration(resource));
        CacheConfiguration<?, ?> cacheConfig = xmlConfig.getCacheConfigurations().get("shrubbery");
        DefaultResilienceStrategyConfiguration resilienceStrategyConfiguration = ServiceUtils.findSingletonAmongst(DefaultResilienceStrategyConfiguration.class, cacheConfig.getServiceConfigurations());
        Assert.assertThat(resilienceStrategyConfiguration.getClazz(), IsSame.sameInstance(ShrubberyResilience.class));
    }

    @Test
    public void testSysPropReplace() {
        System.getProperties().setProperty("ehcache.match", Number.class.getName());
        XmlConfiguration xmlConfig = new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/systemprops.xml"));
        Assert.assertThat(xmlConfig.getCacheConfigurations().get("bar").getKeyType(), IsSame.sameInstance(((Class) (Number.class))));
        DefaultPersistenceConfiguration persistenceConfiguration = ((DefaultPersistenceConfiguration) (xmlConfig.getServiceCreationConfigurations().iterator().next()));
        Assert.assertThat(persistenceConfiguration.getRootDirectory(), Is.is(new File(((System.getProperty("user.home")) + "/ehcache"))));
    }

    @Test
    public void testSysPropReplaceRegExp() {
        Assert.assertThat(ConfigurationParser.replaceProperties("foo${file.separator}"), CoreMatchers.equalTo(("foo" + (File.separator))));
        Assert.assertThat(ConfigurationParser.replaceProperties("${file.separator}foo${file.separator}"), CoreMatchers.equalTo((((File.separator) + "foo") + (File.separator))));
        try {
            ConfigurationParser.replaceProperties("${bar}foo");
            Assert.fail("Should have thrown!");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains("${bar}"), Is.is(true));
        }
        Assert.assertThat(ConfigurationParser.replaceProperties("foo"), CoreMatchers.nullValue());
    }

    @Test
    public void testMultithreadedXmlParsing() throws InterruptedException, ExecutionException {
        Callable<Configuration> parserTask = () -> new XmlConfiguration(XmlConfigurationTest.class.getResource("/configs/one-cache.xml"));
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            for (Future<Configuration> c : service.invokeAll(Collections.nCopies(10, parserTask))) {
                Assert.assertThat(c.get(), IsNull.notNullValue());
            }
        } finally {
            service.shutdown();
        }
    }

    @Test
    public void testCompleteXmlToString() {
        URL resource = XmlConfigurationTest.class.getResource("/configs/ehcache-complete.xml");
        Configuration config = new XmlConfiguration(resource);
        XmlConfiguration xmlConfig = new XmlConfiguration(config);
        Assert.assertThat(xmlConfig.toString(), isSimilarTo(resource).ignoreComments().ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testPrettyTypeNames() {
        URL resource = XmlConfigurationTest.class.getResource("/configs/pretty-typed-caches.xml");
        Configuration config = new XmlConfiguration(new XmlConfiguration(resource));
        CacheConfiguration<?, ?> byteArray = config.getCacheConfigurations().get("byte-array");
        Assert.assertThat(byteArray.getValueType(), CoreMatchers.equalTo(byte[].class));
        CacheConfiguration<?, ?> stringArray = config.getCacheConfigurations().get("string-array");
        Assert.assertThat(stringArray.getValueType(), CoreMatchers.equalTo(String[].class));
        CacheConfiguration<?, ?> string2dArray = config.getCacheConfigurations().get("string-2d-array");
        Assert.assertThat(string2dArray.getValueType(), CoreMatchers.equalTo(String[][].class));
        CacheConfiguration<?, ?> mapEntry = config.getCacheConfigurations().get("map-entry");
        Assert.assertThat(mapEntry.getValueType(), CoreMatchers.equalTo(Map.Entry.class));
    }

    @Test
    public void testPrimitiveNameConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("boolean", getDefaultClassLoader()), IsEqual.equalTo(Boolean.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("byte", getDefaultClassLoader()), IsEqual.equalTo(Byte.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("short", getDefaultClassLoader()), IsEqual.equalTo(Short.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("int", getDefaultClassLoader()), IsEqual.equalTo(Integer.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("long", getDefaultClassLoader()), IsEqual.equalTo(Long.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("char", getDefaultClassLoader()), IsEqual.equalTo(Character.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("float", getDefaultClassLoader()), IsEqual.equalTo(Float.TYPE));
        Assert.assertThat(XmlConfiguration.getClassForName("double", getDefaultClassLoader()), IsEqual.equalTo(Double.TYPE));
    }

    @Test
    public void testPrimitiveArrayClassNameConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("boolean[]", getDefaultClassLoader()), IsEqual.equalTo(boolean[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("byte[]", getDefaultClassLoader()), IsEqual.equalTo(byte[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("short[]", getDefaultClassLoader()), IsEqual.equalTo(short[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("int[]", getDefaultClassLoader()), IsEqual.equalTo(int[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("long[]", getDefaultClassLoader()), IsEqual.equalTo(long[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("char[]", getDefaultClassLoader()), IsEqual.equalTo(char[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("float[]", getDefaultClassLoader()), IsEqual.equalTo(float[].class));
        Assert.assertThat(XmlConfiguration.getClassForName("double[]", getDefaultClassLoader()), IsEqual.equalTo(double[].class));
    }

    @Test
    public void testMultiDimensionPrimitiveArrayClassNameConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("byte[][][][]", getDefaultClassLoader()), IsEqual.equalTo(byte[][][][].class));
        Assert.assertThat(XmlConfiguration.getClassForName("short[][][][]", getDefaultClassLoader()), IsEqual.equalTo(short[][][][].class));
        Assert.assertThat(XmlConfiguration.getClassForName("int[][][][]", getDefaultClassLoader()), IsEqual.equalTo(int[][][][].class));
        Assert.assertThat(XmlConfiguration.getClassForName("long[][][][]", getDefaultClassLoader()), IsEqual.equalTo(long[][][][].class));
        Assert.assertThat(XmlConfiguration.getClassForName("char[][][][]", getDefaultClassLoader()), IsEqual.equalTo(char[][][][].class));
        Assert.assertThat(XmlConfiguration.getClassForName("float[][][][]", getDefaultClassLoader()), IsEqual.equalTo(float[][][][].class));
        Assert.assertThat(XmlConfiguration.getClassForName("double[][][][]", getDefaultClassLoader()), IsEqual.equalTo(double[][][][].class));
    }

    @Test
    public void testArrayClassNameConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("java.lang.String[]", getDefaultClassLoader()), IsEqual.equalTo(String[].class));
    }

    @Test
    public void testMultiDimensionArrayClassNameConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("java.lang.String[][][][]", getDefaultClassLoader()), IsEqual.equalTo(String[][][][].class));
    }

    @Test
    public void testInnerClassNameConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("java.util.Map.Entry", getDefaultClassLoader()), IsEqual.equalTo(Map.Entry.class));
    }

    @Test
    public void testInnerClassNameArrayConversion() throws ClassNotFoundException {
        Assert.assertThat(XmlConfiguration.getClassForName("java.util.Map.Entry[]", getDefaultClassLoader()), IsEqual.equalTo(Map.Entry[].class));
    }
}

