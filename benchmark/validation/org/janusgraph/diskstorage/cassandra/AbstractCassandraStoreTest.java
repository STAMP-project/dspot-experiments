/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.cassandra;


import AbstractCassandraStoreManager.CF_COMPRESSION;
import AbstractCassandraStoreManager.CF_COMPRESSION_BLOCK_SIZE;
import AbstractCassandraStoreManager.CF_COMPRESSION_TYPE;
import GraphDatabaseConfiguration.GRAPH_NAME;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.KeyColumnValueStoreTest;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.keycolumnvalue.StoreFeatures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractCassandraStoreTest extends KeyColumnValueStoreTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractCassandraStoreTest.class);

    private static final String TEST_CF_NAME = "testcf";

    private static final String DEFAULT_COMPRESSOR_PACKAGE = "org.apache.cassandra.io.compress";

    @Test
    public void testDefaultCFCompressor() throws BackendException {
        final String cf = (AbstractCassandraStoreTest.TEST_CF_NAME) + "_snappy";
        AbstractCassandraStoreManager mgr = openStorageManager();
        mgr.openDatabase(cf);
        Map<String, String> defaultCfCompressionOps = new ImmutableMap.Builder<String, String>().put("sstable_compression", (((AbstractCassandraStoreTest.DEFAULT_COMPRESSOR_PACKAGE) + ".") + (CF_COMPRESSION_TYPE.getDefaultValue()))).put("chunk_length_kb", "64").build();
        Assertions.assertEquals(defaultCfCompressionOps, mgr.getCompressionOptions(cf));
    }

    @Test
    public void testCustomCFCompressor() throws BackendException {
        final String compressor = "DeflateCompressor";
        final int ckb = 128;
        final String cf = (AbstractCassandraStoreTest.TEST_CF_NAME) + "_gzip";
        ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPRESSION_TYPE, compressor);
        config.set(CF_COMPRESSION_BLOCK_SIZE, ckb);
        AbstractCassandraStoreManager mgr = openStorageManager(config);
        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);
        final Map<String, String> expected = ImmutableMap.<String, String>builder().put("sstable_compression", (((AbstractCassandraStoreTest.DEFAULT_COMPRESSOR_PACKAGE) + ".") + compressor)).put("chunk_length_kb", String.valueOf(ckb)).build();
        Assertions.assertEquals(expected, mgr.getCompressionOptions(cf));
    }

    @Test
    public void testDisableCFCompressor() throws BackendException {
        final String cf = (AbstractCassandraStoreTest.TEST_CF_NAME) + "_nocompress";
        ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPRESSION, false);
        AbstractCassandraStoreManager mgr = openStorageManager(config);
        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);
        Assertions.assertEquals(Collections.emptyMap(), mgr.getCompressionOptions(cf));
    }

    @Test
    public void testTTLSupported() {
        StoreFeatures features = manager.getFeatures();
        Assertions.assertTrue(features.hasCellTTL());
    }

    @Test
    public void keyspaceShouldBeEquivalentToProvidedOne() throws BackendException {
        final ModifiableConfiguration config = getBaseStorageConfiguration("randomNewKeyspace");
        final AbstractCassandraStoreManager mgr = openStorageManager(config);
        Assertions.assertEquals("randomNewKeyspace", mgr.keySpaceName);
    }

    @Test
    public void keyspaceShouldBeEquivalentToGraphName() throws BackendException {
        final ModifiableConfiguration config = getBaseStorageConfiguration(null);
        config.set(GRAPH_NAME, "randomNewGraphName");
        final AbstractCassandraStoreManager mgr = openStorageManager(config);
        Assertions.assertEquals("randomNewGraphName", mgr.keySpaceName);
    }
}

