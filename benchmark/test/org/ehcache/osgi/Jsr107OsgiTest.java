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
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.ehcache.core.osgi.EhcacheActivator;
import org.ehcache.core.osgi.OsgiServiceLoader;
import org.ehcache.core.spi.service.ServiceFactory;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;


/**
 * Jsr107OsgiTest
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class Jsr107OsgiTest {
    @Test
    public void testJsr107EhcacheOsgi() throws Exception {
        Jsr107OsgiTest.TestMethods.testJsr107EhcacheOsgi();
    }

    @Test
    public void testAllServicesAreAvailable() {
        Jsr107OsgiTest.TestMethods.testAllServicesAreAvailable();
    }

    private static class TestMethods {
        @SuppressWarnings("unchecked")
        public static void testJsr107EhcacheOsgi() throws Exception {
            CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider", Jsr107OsgiTest.TestMethods.class.getClassLoader());
            CacheManager cacheManager = cachingProvider.getCacheManager(Jsr107OsgiTest.TestMethods.class.getResource("/org/ehcache/osgi/ehcache-107-osgi.xml").toURI(), Jsr107OsgiTest.TestMethods.class.getClassLoader());
            Cache<Long, Person> personCache = cacheManager.getCache("personCache", Long.class, Person.class);
            Assert.assertEquals(Person.class, personCache.getConfiguration(Configuration.class).getValueType());
        }

        public static void testAllServicesAreAvailable() {
            Set<String> osgiAvailableClasses = StreamSupport.stream(Spliterators.spliterator(OsgiServiceLoader.load(ServiceFactory.class).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getClass().getName()).collect(Collectors.toSet());
            Set<String> jdkAvailableClasses = Stream.of(EhcacheActivator.getCoreBundle().getBundles()).map(( b) -> b.adapt(.class).getClassLoader()).flatMap(( cl) -> stream(spliterator(ServiceLoader.load(.class, cl).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getClass().getName())).collect(Collectors.toSet());
            Assert.assertThat(osgiAvailableClasses, IsCollectionContaining.hasItems(jdkAvailableClasses.toArray(new String[0])));
        }
    }
}

