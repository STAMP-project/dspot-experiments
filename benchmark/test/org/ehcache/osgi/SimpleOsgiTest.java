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
package org.ehcache.osgi;


import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.osgi.EhcacheActivator;
import org.ehcache.core.osgi.OsgiServiceLoader;
import org.ehcache.core.spi.service.ServiceFactory;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.copy.ReadWriteCopier;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.xml.XmlConfiguration;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SimpleOsgiTest {
    @Test
    public void testEhcache3AsBundle() {
        SimpleOsgiTest.TestMethods.testEhcache3AsBundle();
    }

    @Test
    public void testEhcache3WithSerializationAndClientClass() {
        SimpleOsgiTest.TestMethods.testEhcache3WithSerializationAndClientClass();
    }

    @Test
    public void testCustomCopier() {
        SimpleOsgiTest.TestMethods.testCustomCopier();
    }

    @Test
    public void testEhcacheXMLConfig() throws Exception {
        SimpleOsgiTest.TestMethods.testEhcacheXMLConfig();
    }

    @Test
    public void testAllServicesAreAvailable() {
        SimpleOsgiTest.TestMethods.testAllServicesAreAvailable();
    }

    private static class TestMethods {
        public static void testEhcache3AsBundle() {
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build()).build(true);
            Cache<Long, String> myCache = cacheManager.getCache("myCache", Long.class, String.class);
            myCache.put(42L, "DaAnswer!");
            Assert.assertEquals("DaAnswer!", myCache.get(42L));
        }

        public static void testEhcache3WithSerializationAndClientClass() {
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Person.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCopierConfiguration<>(SerializingCopier.<Person>asCopierClass(), VALUE)).withClassLoader(SimpleOsgiTest.TestMethods.class.getClassLoader()).build()).build(true);
            Cache<Long, Person> myCache = cacheManager.getCache("myCache", Long.class, Person.class);
            myCache.put(42L, new Person("Arthur"));
            Assert.assertTrue(((myCache.get(42L)) instanceof Person));
        }

        public static void testCustomCopier() {
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCopierConfiguration<>(SimpleOsgiTest.TestMethods.StringCopier.class, VALUE)).withClassLoader(SimpleOsgiTest.TestMethods.class.getClassLoader()).build()).build(true);
            Cache<Long, String> cache = cacheManager.getCache("myCache", Long.class, String.class);
            cache.put(42L, "What's the question again?");
            cache.get(42L);
        }

        public static void testEhcacheXMLConfig() throws Exception {
            XmlConfiguration configuration = new XmlConfiguration(SimpleOsgiTest.TestMethods.class.getResource("/org/ehcache/osgi/ehcache-osgi.xml").toURI().toURL(), SimpleOsgiTest.TestMethods.class.getClassLoader());
            Assert.assertEquals(Person.class, configuration.getCacheConfigurations().get("bar").getValueType());
        }

        public static void testAllServicesAreAvailable() {
            Set<String> osgiAvailableClasses = StreamSupport.stream(Spliterators.spliterator(OsgiServiceLoader.load(ServiceFactory.class).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getClass().getName()).collect(Collectors.toSet());
            Set<String> jdkAvailableClasses = Stream.of(EhcacheActivator.getCoreBundle().getBundles()).map(( b) -> b.adapt(.class).getClassLoader()).flatMap(( cl) -> stream(spliterator(ServiceLoader.load(.class, cl).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getClass().getName())).collect(Collectors.toSet());
            Assert.assertThat(osgiAvailableClasses, IsCollectionContaining.hasItems(jdkAvailableClasses.toArray(new String[0])));
        }

        public static class StringCopier extends ReadWriteCopier<String> {
            @Override
            public String copy(String obj) {
                return new String(obj);
            }
        }
    }
}

