/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import RocksDBStore.RocksDBBatchingRestoreCallback;
import StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.ProcessorStateException;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.StreamsTestUtils;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.Options;


public class RocksDBStoreTest {
    private static boolean enableBloomFilters = false;

    static final String DB_NAME = "db-name";

    private File dir;

    private final Serializer<String> stringSerializer = new StringSerializer();

    private final Deserializer<String> stringDeserializer = new StringDeserializer();

    InternalMockProcessorContext context;

    RocksDBStore rocksDBStore;

    @Test
    public void shouldRespectBulkloadOptionsDuringInit() {
        rocksDBStore.init(context, rocksDBStore);
        final StateRestoreListener restoreListener = context.getRestoreListener(rocksDBStore.name());
        restoreListener.onRestoreStart(null, rocksDBStore.name(), 0L, 0L);
        MatcherAssert.assertThat(rocksDBStore.getOptions().level0FileNumCompactionTrigger(), CoreMatchers.equalTo((1 << 30)));
        MatcherAssert.assertThat(rocksDBStore.getOptions().level0SlowdownWritesTrigger(), CoreMatchers.equalTo((1 << 30)));
        MatcherAssert.assertThat(rocksDBStore.getOptions().level0StopWritesTrigger(), CoreMatchers.equalTo((1 << 30)));
        restoreListener.onRestoreEnd(null, rocksDBStore.name(), 0L);
        MatcherAssert.assertThat(rocksDBStore.getOptions().level0FileNumCompactionTrigger(), CoreMatchers.equalTo(10));
        MatcherAssert.assertThat(rocksDBStore.getOptions().level0SlowdownWritesTrigger(), CoreMatchers.equalTo(20));
        MatcherAssert.assertThat(rocksDBStore.getOptions().level0StopWritesTrigger(), CoreMatchers.equalTo(36));
    }

    @Test
    public void shouldNotThrowExceptionOnRestoreWhenThereIsPreExistingRocksDbFiles() {
        rocksDBStore.init(context, rocksDBStore);
        final String message = "how can a 4 ounce bird carry a 2lb coconut";
        int intKey = 1;
        for (int i = 0; i < 2000000; i++) {
            rocksDBStore.put(new Bytes(stringSerializer.serialize(null, ("theKeyIs" + (intKey++)))), stringSerializer.serialize(null, message));
        }
        final List<KeyValue<byte[], byte[]>> restoreBytes = new ArrayList<>();
        final byte[] restoredKey = "restoredKey".getBytes(StandardCharsets.UTF_8);
        final byte[] restoredValue = "restoredValue".getBytes(StandardCharsets.UTF_8);
        restoreBytes.add(KeyValue.pair(restoredKey, restoredValue));
        context.restore(RocksDBStoreTest.DB_NAME, restoreBytes);
        MatcherAssert.assertThat(stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "restoredKey")))), CoreMatchers.equalTo("restoredValue"));
    }

    @Test
    public void shouldCallRocksDbConfigSetter() {
        RocksDBStoreTest.MockRocksDbConfigSetter.called = false;
        rocksDBStore.openDB(context);
        Assert.assertTrue(RocksDBStoreTest.MockRocksDbConfigSetter.called);
    }

    @Test
    public void shouldThrowProcessorStateExceptionOnOpeningReadOnlyDir() {
        final File tmpDir = TestUtils.tempDirectory();
        final InternalMockProcessorContext tmpContext = new InternalMockProcessorContext(tmpDir, new StreamsConfig(StreamsTestUtils.getStreamsConfig()));
        Assert.assertTrue(tmpDir.setReadOnly());
        try {
            rocksDBStore.openDB(tmpContext);
            Assert.fail("Should have thrown ProcessorStateException");
        } catch (final ProcessorStateException e) {
            // this is good, do nothing
        }
    }

    @Test
    public void shouldPutAll() {
        final List<KeyValue<Bytes, byte[]>> entries = new ArrayList<>();
        entries.add(new KeyValue(new Bytes(stringSerializer.serialize(null, "1")), stringSerializer.serialize(null, "a")));
        entries.add(new KeyValue(new Bytes(stringSerializer.serialize(null, "2")), stringSerializer.serialize(null, "b")));
        entries.add(new KeyValue(new Bytes(stringSerializer.serialize(null, "3")), stringSerializer.serialize(null, "c")));
        rocksDBStore.init(context, rocksDBStore);
        rocksDBStore.putAll(entries);
        rocksDBStore.flush();
        Assert.assertEquals("a", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "1")))));
        Assert.assertEquals("b", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "2")))));
        Assert.assertEquals("c", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "3")))));
    }

    @Test
    public void shouldTogglePrepareForBulkloadSetting() {
        rocksDBStore.init(context, rocksDBStore);
        final RocksDBStore.RocksDBBatchingRestoreCallback restoreListener = ((RocksDBStore.RocksDBBatchingRestoreCallback) (rocksDBStore.batchingStateRestoreCallback));
        restoreListener.onRestoreStart(null, null, 0, 0);
        Assert.assertTrue("Should have set bulk loading to true", rocksDBStore.isPrepareForBulkload());
        restoreListener.onRestoreEnd(null, null, 0);
        Assert.assertFalse("Should have set bulk loading to false", rocksDBStore.isPrepareForBulkload());
    }

    @Test
    public void shouldTogglePrepareForBulkloadSettingWhenPrexistingSstFiles() {
        final List<KeyValue<byte[], byte[]>> entries = getKeyValueEntries();
        rocksDBStore.init(context, rocksDBStore);
        context.restore(rocksDBStore.name(), entries);
        final RocksDBStore.RocksDBBatchingRestoreCallback restoreListener = ((RocksDBStore.RocksDBBatchingRestoreCallback) (rocksDBStore.batchingStateRestoreCallback));
        restoreListener.onRestoreStart(null, null, 0, 0);
        Assert.assertTrue("Should have not set bulk loading to true", rocksDBStore.isPrepareForBulkload());
        restoreListener.onRestoreEnd(null, null, 0);
        Assert.assertFalse("Should have set bulk loading to false", rocksDBStore.isPrepareForBulkload());
    }

    @Test
    public void shouldRestoreAll() {
        final List<KeyValue<byte[], byte[]>> entries = getKeyValueEntries();
        rocksDBStore.init(context, rocksDBStore);
        context.restore(rocksDBStore.name(), entries);
        Assert.assertEquals("a", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "1")))));
        Assert.assertEquals("b", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "2")))));
        Assert.assertEquals("c", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "3")))));
    }

    @Test
    public void shouldPutOnlyIfAbsentValue() {
        rocksDBStore.init(context, rocksDBStore);
        final Bytes keyBytes = new Bytes(stringSerializer.serialize(null, "one"));
        final byte[] valueBytes = stringSerializer.serialize(null, "A");
        final byte[] valueBytesUpdate = stringSerializer.serialize(null, "B");
        rocksDBStore.putIfAbsent(keyBytes, valueBytes);
        rocksDBStore.putIfAbsent(keyBytes, valueBytesUpdate);
        final String retrievedValue = stringDeserializer.deserialize(null, rocksDBStore.get(keyBytes));
        Assert.assertEquals("A", retrievedValue);
    }

    @Test
    public void shouldHandleDeletesOnRestoreAll() {
        final List<KeyValue<byte[], byte[]>> entries = getKeyValueEntries();
        entries.add(new KeyValue("1".getBytes(StandardCharsets.UTF_8), null));
        rocksDBStore.init(context, rocksDBStore);
        context.restore(rocksDBStore.name(), entries);
        final KeyValueIterator<Bytes, byte[]> iterator = rocksDBStore.all();
        final Set<String> keys = new HashSet<>();
        while (iterator.hasNext()) {
            keys.add(stringDeserializer.deserialize(null, iterator.next().key.get()));
        } 
        MatcherAssert.assertThat(keys, CoreMatchers.equalTo(Utils.mkSet("2", "3")));
    }

    @Test
    public void shouldHandleDeletesAndPutbackOnRestoreAll() {
        final List<KeyValue<byte[], byte[]>> entries = new ArrayList<>();
        entries.add(new KeyValue("1".getBytes(StandardCharsets.UTF_8), "a".getBytes(StandardCharsets.UTF_8)));
        entries.add(new KeyValue("2".getBytes(StandardCharsets.UTF_8), "b".getBytes(StandardCharsets.UTF_8)));
        // this will be deleted
        entries.add(new KeyValue("1".getBytes(StandardCharsets.UTF_8), null));
        entries.add(new KeyValue("3".getBytes(StandardCharsets.UTF_8), "c".getBytes(StandardCharsets.UTF_8)));
        // this will restore key "1" as WriteBatch applies updates in order
        entries.add(new KeyValue("1".getBytes(StandardCharsets.UTF_8), "restored".getBytes(StandardCharsets.UTF_8)));
        rocksDBStore.init(context, rocksDBStore);
        context.restore(rocksDBStore.name(), entries);
        final KeyValueIterator<Bytes, byte[]> iterator = rocksDBStore.all();
        final Set<String> keys = new HashSet<>();
        while (iterator.hasNext()) {
            keys.add(stringDeserializer.deserialize(null, iterator.next().key.get()));
        } 
        MatcherAssert.assertThat(keys, CoreMatchers.equalTo(Utils.mkSet("1", "2", "3")));
        Assert.assertEquals("restored", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "1")))));
        Assert.assertEquals("b", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "2")))));
        Assert.assertEquals("c", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "3")))));
    }

    @Test
    public void shouldRestoreThenDeleteOnRestoreAll() {
        final List<KeyValue<byte[], byte[]>> entries = getKeyValueEntries();
        rocksDBStore.init(context, rocksDBStore);
        context.restore(rocksDBStore.name(), entries);
        Assert.assertEquals("a", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "1")))));
        Assert.assertEquals("b", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "2")))));
        Assert.assertEquals("c", stringDeserializer.deserialize(null, rocksDBStore.get(new Bytes(stringSerializer.serialize(null, "3")))));
        entries.clear();
        entries.add(new KeyValue("2".getBytes(StandardCharsets.UTF_8), "b".getBytes(StandardCharsets.UTF_8)));
        entries.add(new KeyValue("3".getBytes(StandardCharsets.UTF_8), "c".getBytes(StandardCharsets.UTF_8)));
        entries.add(new KeyValue("1".getBytes(StandardCharsets.UTF_8), null));
        context.restore(rocksDBStore.name(), entries);
        final KeyValueIterator<Bytes, byte[]> iterator = rocksDBStore.all();
        final Set<String> keys = new HashSet<>();
        while (iterator.hasNext()) {
            keys.add(stringDeserializer.deserialize(null, iterator.next().key.get()));
        } 
        MatcherAssert.assertThat(keys, CoreMatchers.equalTo(Utils.mkSet("2", "3")));
    }

    @Test
    public void shouldThrowNullPointerExceptionOnNullPut() {
        rocksDBStore.init(context, rocksDBStore);
        try {
            rocksDBStore.put(null, stringSerializer.serialize(null, "someVal"));
            Assert.fail("Should have thrown NullPointerException on null put()");
        } catch (final NullPointerException e) {
            // this is good
        }
    }

    @Test
    public void shouldThrowNullPointerExceptionOnNullPutAll() {
        rocksDBStore.init(context, rocksDBStore);
        try {
            rocksDBStore.put(null, stringSerializer.serialize(null, "someVal"));
            Assert.fail("Should have thrown NullPointerException on null put()");
        } catch (final NullPointerException e) {
            // this is good
        }
    }

    @Test
    public void shouldThrowNullPointerExceptionOnNullGet() {
        rocksDBStore.init(context, rocksDBStore);
        try {
            rocksDBStore.get(null);
            Assert.fail("Should have thrown NullPointerException on null get()");
        } catch (final NullPointerException e) {
            // this is good
        }
    }

    @Test
    public void shouldThrowNullPointerExceptionOnDelete() {
        rocksDBStore.init(context, rocksDBStore);
        try {
            rocksDBStore.delete(null);
            Assert.fail("Should have thrown NullPointerException on deleting null key");
        } catch (final NullPointerException e) {
            // this is good
        }
    }

    @Test
    public void shouldThrowNullPointerExceptionOnRange() {
        rocksDBStore.init(context, rocksDBStore);
        try {
            rocksDBStore.range(null, new Bytes(stringSerializer.serialize(null, "2")));
            Assert.fail("Should have thrown NullPointerException on deleting null key");
        } catch (final NullPointerException e) {
            // this is good
        }
    }

    @Test(expected = ProcessorStateException.class)
    public void shouldThrowProcessorStateExceptionOnPutDeletedDir() throws IOException {
        rocksDBStore.init(context, rocksDBStore);
        Utils.delete(dir);
        rocksDBStore.put(new Bytes(stringSerializer.serialize(null, "anyKey")), stringSerializer.serialize(null, "anyValue"));
        rocksDBStore.flush();
    }

    @Test
    public void shouldHandleToggleOfEnablingBloomFilters() {
        final Properties props = StreamsTestUtils.getStreamsConfig();
        props.put(ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, RocksDBStoreTest.TestingBloomFilterRocksDBConfigSetter.class);
        rocksDBStore = getRocksDBStore();
        dir = TestUtils.tempDirectory();
        context = new InternalMockProcessorContext(dir, Serdes.String(), Serdes.String(), new StreamsConfig(props));
        RocksDBStoreTest.enableBloomFilters = false;
        rocksDBStore.init(context, rocksDBStore);
        final List<String> expectedValues = new ArrayList<>();
        expectedValues.add("a");
        expectedValues.add("b");
        expectedValues.add("c");
        final List<KeyValue<byte[], byte[]>> keyValues = getKeyValueEntries();
        for (final KeyValue<byte[], byte[]> keyValue : keyValues) {
            rocksDBStore.put(new Bytes(keyValue.key), keyValue.value);
        }
        int expectedIndex = 0;
        for (final KeyValue<byte[], byte[]> keyValue : keyValues) {
            final byte[] valBytes = rocksDBStore.get(new Bytes(keyValue.key));
            MatcherAssert.assertThat(new String(valBytes, StandardCharsets.UTF_8), CoreMatchers.is(expectedValues.get((expectedIndex++))));
        }
        Assert.assertFalse(RocksDBStoreTest.TestingBloomFilterRocksDBConfigSetter.bloomFiltersSet);
        rocksDBStore.close();
        expectedIndex = 0;
        // reopen with Bloom Filters enabled
        // should open fine without errors
        RocksDBStoreTest.enableBloomFilters = true;
        rocksDBStore.init(context, rocksDBStore);
        for (final KeyValue<byte[], byte[]> keyValue : keyValues) {
            final byte[] valBytes = rocksDBStore.get(new Bytes(keyValue.key));
            MatcherAssert.assertThat(new String(valBytes, StandardCharsets.UTF_8), CoreMatchers.is(expectedValues.get((expectedIndex++))));
        }
        Assert.assertTrue(RocksDBStoreTest.TestingBloomFilterRocksDBConfigSetter.bloomFiltersSet);
    }

    public static class MockRocksDbConfigSetter implements RocksDBConfigSetter {
        static boolean called;

        @Override
        public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
            RocksDBStoreTest.MockRocksDbConfigSetter.called = true;
            options.setLevel0FileNumCompactionTrigger(10);
        }
    }

    public static class TestingBloomFilterRocksDBConfigSetter implements RocksDBConfigSetter {
        static boolean bloomFiltersSet;

        @Override
        public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
            final BlockBasedTableConfig tableConfig = new BlockBasedTableConfig();
            tableConfig.setBlockCacheSize(((50 * 1024) * 1024L));
            tableConfig.setBlockSize(4096L);
            if (RocksDBStoreTest.enableBloomFilters) {
                tableConfig.setFilter(new BloomFilter());
                options.optimizeFiltersForHits();
                RocksDBStoreTest.TestingBloomFilterRocksDBConfigSetter.bloomFiltersSet = true;
            } else {
                options.setOptimizeFiltersForHits(false);
                RocksDBStoreTest.TestingBloomFilterRocksDBConfigSetter.bloomFiltersSet = false;
            }
            options.setTableFormatConfig(tableConfig);
        }
    }
}

