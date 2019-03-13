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


import java.io.File;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.osgi.EhcacheActivator;
import org.ehcache.core.osgi.OsgiServiceLoader;
import org.ehcache.core.spi.service.ServiceFactory;
import org.ehcache.xml.XmlConfiguration;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ClusteredOsgiTest {
    @Rule
    public TemporaryFolder serverLocation = new TemporaryFolder();

    @Test
    public void testProgrammaticClusteredCache() throws Throwable {
        try (OsgiTestUtils.Cluster cluster = OsgiTestUtils.startServer(serverLocation.newFolder().toPath())) {
            ClusteredOsgiTest.TestMethods.testProgrammaticClusteredCache(cluster);
        }
    }

    @Test
    public void testXmlClusteredCache() throws Throwable {
        try (OsgiTestUtils.Cluster cluster = OsgiTestUtils.startServer(serverLocation.newFolder().toPath())) {
            ClusteredOsgiTest.TestMethods.testXmlClusteredCache(cluster);
        }
    }

    @Test
    public void testAllServicesAreAvailable() {
        ClusteredOsgiTest.TestMethods.testAllServicesAreAvailable();
    }

    private static class TestMethods {
        public static void testProgrammaticClusteredCache(OsgiTestUtils.Cluster cluster) throws Throwable {
            try (PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(cluster(cluster.getConnectionUri()).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(clusteredDedicated("main", 2, MemoryUnit.MB)))).build(true)) {
                final Cache<Long, String> cache = cacheManager.getCache("clustered-cache", Long.class, String.class);
                cache.put(1L, "value");
                Assert.assertThat(cache.get(1L), Is.is("value"));
            }
        }

        public static void testXmlClusteredCache(OsgiTestUtils.Cluster cluster) throws Exception {
            File config = cluster.getWorkingArea().resolve("ehcache.xml").toFile();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ClusteredOsgiTest.TestMethods.class.getResourceAsStream("ehcache-clustered-osgi.xml"));
            XPath xpath = XPathFactory.newInstance().newXPath();
            Node clusterUriAttribute = ((Node) (xpath.evaluate("//config/service/cluster/connection/@url", doc, XPathConstants.NODE)));
            clusterUriAttribute.setTextContent(((cluster.getConnectionUri().toString()) + "/cache-manager"));
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(new DOMSource(doc), new StreamResult(config));
            try (PersistentCacheManager cacheManager = ((PersistentCacheManager) (CacheManagerBuilder.newCacheManager(new XmlConfiguration(config.toURI().toURL(), ClusteredOsgiTest.TestMethods.class.getClassLoader()))))) {
                cacheManager.init();
                final Cache<Long, Person> cache = cacheManager.getCache("clustered-cache", Long.class, Person.class);
                cache.put(1L, new Person("Brian"));
                Assert.assertThat(cache.get(1L).name, Is.is("Brian"));
            }
        }

        public static void testAllServicesAreAvailable() {
            Set<String> osgiAvailableClasses = StreamSupport.stream(Spliterators.spliterator(OsgiServiceLoader.load(ServiceFactory.class).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getClass().getName()).collect(Collectors.toSet());
            Set<String> jdkAvailableClasses = Stream.of(EhcacheActivator.getCoreBundle().getBundles()).map(( b) -> b.adapt(.class).getClassLoader()).flatMap(( cl) -> stream(spliterator(ServiceLoader.load(.class, cl).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getClass().getName())).collect(Collectors.toSet());
            Assert.assertThat(osgiAvailableClasses, IsCollectionContaining.hasItems(jdkAvailableClasses.toArray(new String[0])));
        }
    }
}

