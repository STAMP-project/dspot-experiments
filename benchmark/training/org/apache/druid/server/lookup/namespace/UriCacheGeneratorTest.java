/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.lookup.namespace;


import CacheScheduler.Entry;
import CacheScheduler.VersionedCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.druid.data.SearchableVersionedDataFinder;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.IAE;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.query.lookup.namespace.UriExtractionNamespace;
import org.apache.druid.query.lookup.namespace.UriExtractionNamespaceTest;
import org.apache.druid.segment.loading.LocalFileTimestampVersionFinder;
import org.apache.druid.server.lookup.namespace.cache.CacheScheduler;
import org.apache.druid.server.lookup.namespace.cache.CacheSchedulerTest;
import org.apache.druid.server.lookup.namespace.cache.NamespaceExtractionCacheManager;
import org.apache.druid.server.metrics.NoopServiceEmitter;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class UriCacheGeneratorTest {
    private static final String FAKE_SCHEME = "wabblywoo";

    private static final Map<String, SearchableVersionedDataFinder> FINDERS = ImmutableMap.of("file", new LocalFileTimestampVersionFinder(), UriCacheGeneratorTest.FAKE_SCHEME, new LocalFileTimestampVersionFinder() {
        URI fixURI(URI uri) {
            final URI newURI;
            try {
                newURI = new URI("file", uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException e) {
                throw Throwables.propagate(e);
            }
            return newURI;
        }

        @Override
        public String getVersion(URI uri) {
            return super.getVersion(fixURI(uri));
        }

        @Override
        public InputStream getInputStream(URI uri) throws IOException {
            return super.getInputStream(fixURI(uri));
        }
    });

    public UriCacheGeneratorTest(String suffix, Function<File, OutputStream> outStreamSupplier, Function<Lifecycle, NamespaceExtractionCacheManager> cacheManagerCreator) {
        this.suffix = suffix;
        this.outStreamSupplier = outStreamSupplier;
        this.lifecycle = new Lifecycle();
        this.scheduler = new CacheScheduler(new NoopServiceEmitter(), ImmutableMap.of(UriExtractionNamespace.class, new UriCacheGenerator(UriCacheGeneratorTest.FINDERS)), cacheManagerCreator.apply(lifecycle));
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final String suffix;

    private final Function<File, OutputStream> outStreamSupplier;

    private Lifecycle lifecycle;

    private CacheScheduler scheduler;

    private File tmpFile;

    private File tmpFileParent;

    private UriCacheGenerator generator;

    private UriExtractionNamespace namespace;

    @Test
    public void simpleTest() throws InterruptedException {
        Assert.assertEquals(0, scheduler.getActiveEntries());
        CacheScheduler.Entry entry = scheduler.schedule(namespace);
        CacheSchedulerTest.waitFor(entry);
        Map<String, String> map = entry.getCache();
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals(null, map.get("baz"));
    }

    @Test
    public void simpleTestRegex() throws InterruptedException {
        final UriExtractionNamespace namespace = new UriExtractionNamespace(null, Paths.get(this.namespace.getUri()).getParent().toUri(), Pattern.quote(Paths.get(this.namespace.getUri()).getFileName().toString()), this.namespace.getNamespaceParseSpec(), Period.millis(((int) (this.namespace.getPollMs()))), null);
        CacheScheduler.Entry entry = scheduler.schedule(namespace);
        CacheSchedulerTest.waitFor(entry);
        Map<String, String> map = entry.getCache();
        Assert.assertNotNull(map);
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals(null, map.get("baz"));
    }

    @Test
    public void simplePileONamespacesTest() throws InterruptedException {
        final int size = 128;
        List<CacheScheduler.Entry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            UriExtractionNamespace namespace = new UriExtractionNamespace(tmpFile.toURI(), null, null, new UriExtractionNamespace.ObjectMapperFlatDataParser(UriExtractionNamespaceTest.registerTypes(new ObjectMapper())), new Period(0), null);
            CacheScheduler.Entry entry = scheduler.schedule(namespace);
            entries.add(entry);
            CacheSchedulerTest.waitFor(entry);
        }
        for (CacheScheduler.Entry entry : entries) {
            final Map<String, String> map = entry.getCache();
            Assert.assertEquals("bar", map.get("foo"));
            Assert.assertEquals(null, map.get("baz"));
            entry.close();
        }
        Assert.assertEquals(0, scheduler.getActiveEntries());
    }

    @Test
    public void testLoadOnlyOnce() throws Exception {
        Assert.assertEquals(0, scheduler.getActiveEntries());
        CacheScheduler.VersionedCache versionedCache = generator.generateCache(namespace, null, null, scheduler);
        Assert.assertNotNull(versionedCache);
        Map<String, String> map = versionedCache.getCache();
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals(null, map.get("baz"));
        String version = versionedCache.getVersion();
        Assert.assertNotNull(version);
        Assert.assertNull(generator.generateCache(namespace, null, version, scheduler));
    }

    @Test(expected = FileNotFoundException.class)
    public void testMissing() throws Exception {
        UriExtractionNamespace badNamespace = new UriExtractionNamespace(namespace.getUri(), null, null, namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), null);
        Assert.assertTrue(new File(namespace.getUri()).delete());
        generator.generateCache(badNamespace, null, null, scheduler);
    }

    @Test(expected = FileNotFoundException.class)
    public void testMissingRegex() throws Exception {
        UriExtractionNamespace badNamespace = new UriExtractionNamespace(null, Paths.get(namespace.getUri()).getParent().toUri(), Pattern.quote(Paths.get(namespace.getUri()).getFileName().toString()), namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), null);
        Assert.assertTrue(new File(namespace.getUri()).delete());
        generator.generateCache(badNamespace, null, null, scheduler);
    }

    @Test(expected = IAE.class)
    public void testExceptionalCreationDoubleURI() {
        new UriExtractionNamespace(namespace.getUri(), namespace.getUri(), null, namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), null);
    }

    @Test(expected = IAE.class)
    public void testExceptionalCreationURIWithPattern() {
        new UriExtractionNamespace(namespace.getUri(), null, "", namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), null);
    }

    @Test(expected = IAE.class)
    public void testExceptionalCreationURIWithLegacyPattern() {
        new UriExtractionNamespace(namespace.getUri(), null, null, namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), "");
    }

    @Test(expected = IAE.class)
    public void testLegacyMix() {
        new UriExtractionNamespace(null, namespace.getUri(), "", namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), "");
    }

    @Test(expected = IAE.class)
    public void testBadPattern() {
        new UriExtractionNamespace(null, namespace.getUri(), "[", namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), null);
    }

    @Test
    public void testWeirdSchemaOnExactURI() throws Exception {
        final UriExtractionNamespace extractionNamespace = new UriExtractionNamespace(new URI(UriCacheGeneratorTest.FAKE_SCHEME, namespace.getUri().getUserInfo(), namespace.getUri().getHost(), namespace.getUri().getPort(), namespace.getUri().getPath(), namespace.getUri().getQuery(), namespace.getUri().getFragment()), null, null, namespace.getNamespaceParseSpec(), Period.millis(((int) (namespace.getPollMs()))), null);
        Assert.assertNotNull(generator.generateCache(extractionNamespace, null, null, scheduler));
    }

    @Test(timeout = 60000L)
    public void testDeleteOnScheduleFail() throws Exception {
        Assert.assertNull(scheduler.scheduleAndWait(new UriExtractionNamespace(new URI(("file://tmp/I_DONT_REALLY_EXIST" + (UUID.randomUUID()))), null, null, new UriExtractionNamespace.JSONFlatDataParser(new DefaultObjectMapper(), "key", "val"), Period.millis(10000), null), 500));
        Assert.assertEquals(0, scheduler.getActiveEntries());
    }
}

