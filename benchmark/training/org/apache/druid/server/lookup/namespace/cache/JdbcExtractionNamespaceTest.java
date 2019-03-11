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
package org.apache.druid.server.lookup.namespace.cache;


import CacheScheduler.Entry;
import ServerTestHelper.MAPPER;
import TestDerbyConnector.DerbyConnectorRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.query.lookup.namespace.ExtractionNamespace;
import org.apache.druid.query.lookup.namespace.JdbcExtractionNamespace;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.skife.jdbi.v2.Handle;


/**
 *
 */
@RunWith(Parameterized.class)
public class JdbcExtractionNamespaceTest {
    @Rule
    public final DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private static final Logger log = new Logger(JdbcExtractionNamespaceTest.class);

    private static final String tableName = "abstractDbRenameTest";

    private static final String keyName = "keyName";

    private static final String valName = "valName";

    private static final String tsColumn_ = "tsColumn";

    private static final String filterColumn = "filterColumn";

    private static final Map<String, String[]> renames = ImmutableMap.of("foo", new String[]{ "bar", "1" }, "bad", new String[]{ "bar", "1" }, "how about that", new String[]{ "foo", "0" }, "empty string", new String[]{ "empty string", "0" });

    public JdbcExtractionNamespaceTest(String tsColumn) {
        this.tsColumn = tsColumn;
    }

    private final String tsColumn;

    private CacheScheduler scheduler;

    private Lifecycle lifecycle;

    private AtomicLong updates;

    private Lock updateLock;

    private Closer closer;

    private ListeningExecutorService setupTeardownService;

    private Handle handleRef = null;

    @Test(timeout = 60000L)
    public void testMappingWithoutFilter() throws InterruptedException {
        final JdbcExtractionNamespace extractionNamespace = new JdbcExtractionNamespace(derbyConnectorRule.getMetadataConnectorConfig(), JdbcExtractionNamespaceTest.tableName, JdbcExtractionNamespaceTest.keyName, JdbcExtractionNamespaceTest.valName, tsColumn, null, new Period(0));
        try (CacheScheduler.Entry entry = scheduler.schedule(extractionNamespace)) {
            CacheSchedulerTest.waitFor(entry);
            final Map<String, String> map = entry.getCache();
            for (Map.Entry<String, String[]> e : JdbcExtractionNamespaceTest.renames.entrySet()) {
                String key = e.getKey();
                String[] val = e.getValue();
                String field = val[0];
                Assert.assertEquals("non-null check", NullHandling.emptyToNullIfNeeded(field), NullHandling.emptyToNullIfNeeded(map.get(key)));
            }
            Assert.assertEquals("null check", null, map.get("baz"));
        }
    }

    @Test(timeout = 60000L)
    public void testMappingWithFilter() throws InterruptedException {
        final JdbcExtractionNamespace extractionNamespace = new JdbcExtractionNamespace(derbyConnectorRule.getMetadataConnectorConfig(), JdbcExtractionNamespaceTest.tableName, JdbcExtractionNamespaceTest.keyName, JdbcExtractionNamespaceTest.valName, tsColumn, ((JdbcExtractionNamespaceTest.filterColumn) + "='1'"), new Period(0));
        try (CacheScheduler.Entry entry = scheduler.schedule(extractionNamespace)) {
            CacheSchedulerTest.waitFor(entry);
            final Map<String, String> map = entry.getCache();
            for (Map.Entry<String, String[]> e : JdbcExtractionNamespaceTest.renames.entrySet()) {
                String key = e.getKey();
                String[] val = e.getValue();
                String field = val[0];
                String filterVal = val[1];
                if ("1".equals(filterVal)) {
                    Assert.assertEquals("non-null check", NullHandling.emptyToNullIfNeeded(field), NullHandling.emptyToNullIfNeeded(map.get(key)));
                } else {
                    Assert.assertEquals("non-null check", null, NullHandling.emptyToNullIfNeeded(map.get(key)));
                }
            }
        }
    }

    @Test(timeout = 60000L)
    public void testSkipOld() throws InterruptedException {
        try (final CacheScheduler.Entry entry = ensureEntry()) {
            assertUpdated(entry, "foo", "bar");
            if ((tsColumn) != null) {
                insertValues(handleRef, "foo", "baz", null, "1900-01-01 00:00:00");
            }
            assertUpdated(entry, "foo", "bar");
        }
    }

    @Test(timeout = 60000L)
    public void testFindNew() throws InterruptedException {
        try (final CacheScheduler.Entry entry = ensureEntry()) {
            assertUpdated(entry, "foo", "bar");
            insertValues(handleRef, "foo", "baz", null, "2900-01-01 00:00:00");
            assertUpdated(entry, "foo", "baz");
        }
    }

    @Test(timeout = 60000L)
    public void testIgnoresNullValues() throws InterruptedException {
        try (final CacheScheduler.Entry entry = ensureEntry()) {
            insertValues(handleRef, "fooz", null, null, "2900-01-01 00:00:00");
            waitForUpdates(1000L, 2L);
            Thread.sleep(100);
            Set set = entry.getCache().keySet();
            Assert.assertFalse(set.contains("fooz"));
        }
    }

    @Test
    public void testSerde() throws IOException {
        final JdbcExtractionNamespace extractionNamespace = new JdbcExtractionNamespace(derbyConnectorRule.getMetadataConnectorConfig(), JdbcExtractionNamespaceTest.tableName, JdbcExtractionNamespaceTest.keyName, JdbcExtractionNamespaceTest.valName, tsColumn, "some filter", new Period(10));
        final ExtractionNamespace extractionNamespace2 = MAPPER.readValue(MAPPER.writeValueAsBytes(extractionNamespace), ExtractionNamespace.class);
        Assert.assertEquals(extractionNamespace, extractionNamespace2);
    }
}

