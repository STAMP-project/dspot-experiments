package com.thinkaurelius.titan.diskstorage.cassandra;


import AbstractCassandraStoreManager.CF_COMPRESSION;
import AbstractCassandraStoreManager.CF_COMPRESSION_BLOCK_SIZE;
import AbstractCassandraStoreManager.CF_COMPRESSION_TYPE;
import com.google.common.collect.ImmutableMap;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.KeyColumnValueStoreTest;
import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import com.thinkaurelius.titan.testcategory.OrderedKeyStoreTests;
import com.thinkaurelius.titan.testcategory.UnorderedKeyStoreTests;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractCassandraStoreTest extends KeyColumnValueStoreTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractCassandraStoreTest.class);

    private static final String TEST_CF_NAME = "testcf";

    private static final String DEFAULT_COMPRESSOR_PACKAGE = "org.apache.cassandra.io.compress";

    @Test
    @Category({ UnorderedKeyStoreTests.class })
    public void testUnorderedConfiguration() {
        if (!(manager.getFeatures().hasUnorderedScan())) {
            AbstractCassandraStoreTest.log.warn(("Can't test key-unordered features on incompatible store.  " + ("This warning could indicate reduced test coverage and " + "a broken JUnit configuration.  Skipping test {}.")), name.getMethodName());
            return;
        }
        StoreFeatures features = manager.getFeatures();
        Assert.assertFalse(features.isKeyOrdered());
        Assert.assertFalse(features.hasLocalKeyPartition());
    }

    @Test
    @Category({ OrderedKeyStoreTests.class })
    public void testOrderedConfiguration() {
        if (!(manager.getFeatures().hasOrderedScan())) {
            AbstractCassandraStoreTest.log.warn(("Can't test key-ordered features on incompatible store.  " + ("This warning could indicate reduced test coverage and " + "a broken JUnit configuration.  Skipping test {}.")), name.getMethodName());
            return;
        }
        StoreFeatures features = manager.getFeatures();
        Assert.assertTrue(features.isKeyOrdered());
    }

    @Test
    public void testDefaultCFCompressor() throws BackendException {
        final String cf = (AbstractCassandraStoreTest.TEST_CF_NAME) + "_snappy";
        AbstractCassandraStoreManager mgr = openStorageManager();
        mgr.openDatabase(cf);
        Map<String, String> defaultCfCompressionOps = new ImmutableMap.Builder<String, String>().put("sstable_compression", (((AbstractCassandraStoreTest.DEFAULT_COMPRESSOR_PACKAGE) + ".") + (CF_COMPRESSION_TYPE.getDefaultValue()))).put("chunk_length_kb", "64").build();
        Assert.assertEquals(defaultCfCompressionOps, mgr.getCompressionOptions(cf));
    }

    @Test
    public void testCustomCFCompressor() throws BackendException {
        final String cname = "DeflateCompressor";
        final int ckb = 128;
        final String cf = (AbstractCassandraStoreTest.TEST_CF_NAME) + "_gzip";
        ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPRESSION_TYPE, cname);
        config.set(CF_COMPRESSION_BLOCK_SIZE, ckb);
        AbstractCassandraStoreManager mgr = openStorageManager(config);
        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);
        final Map<String, String> expected = ImmutableMap.<String, String>builder().put("sstable_compression", (((AbstractCassandraStoreTest.DEFAULT_COMPRESSOR_PACKAGE) + ".") + cname)).put("chunk_length_kb", String.valueOf(ckb)).build();
        Assert.assertEquals(expected, mgr.getCompressionOptions(cf));
    }

    @Test
    public void testDisableCFCompressor() throws BackendException {
        final String cf = (AbstractCassandraStoreTest.TEST_CF_NAME) + "_nocompress";
        ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(CF_COMPRESSION, false);
        AbstractCassandraStoreManager mgr = openStorageManager(config);
        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);
        Assert.assertEquals(Collections.emptyMap(), mgr.getCompressionOptions(cf));
    }

    @Test
    public void testTTLSupported() throws Exception {
        StoreFeatures features = manager.getFeatures();
        Assert.assertTrue(features.hasCellTTL());
    }
}

