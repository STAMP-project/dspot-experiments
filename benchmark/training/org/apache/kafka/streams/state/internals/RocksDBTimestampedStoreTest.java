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


import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.processor.internals.testutil.LogCaptureAppender;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Test;


public class RocksDBTimestampedStoreTest extends RocksDBStoreTest {
    @Test
    public void shouldMigrateDataFromDefaultToTimestampColumnFamily() throws Exception {
        prepareOldStore();
        LogCaptureAppender.setClassLoggerToDebug(RocksDBTimestampedStore.class);
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        rocksDBStore.init(context, rocksDBStore);
        MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem((("Opening store " + (RocksDBStoreTest.DB_NAME)) + " in upgrade mode")));
        LogCaptureAppender.unregister(appender);
        // approx: 7 entries on old CF, 0 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(7L));
        // get()
        // should be no-op on both CF
        MatcherAssert.assertThat(rocksDBStore.get(new Bytes("unknown".getBytes())), new IsNull());
        // approx: 7 entries on old CF, 0 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(7L));
        // should migrate key1 from old to new CF
        // must return timestamp plus value, ie, it's not 1 byte but 9 bytes
        MatcherAssert.assertThat(rocksDBStore.get(new Bytes("key1".getBytes())).length, CoreMatchers.is((8 + 1)));
        // one delete on old CF, one put on new CF
        // approx: 6 entries on old CF, 1 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(7L));
        // put()
        // should migrate key2 from old to new CF with new value
        rocksDBStore.put(new Bytes("key2".getBytes()), "timestamp+22".getBytes());
        // one delete on old CF, one put on new CF
        // approx: 5 entries on old CF, 2 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(7L));
        // should delete key3 from old and new CF
        rocksDBStore.put(new Bytes("key3".getBytes()), null);
        // count is off by one, due to two delete operations (even if one does not delete anything)
        // approx: 4 entries on old CF, 1 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(5L));
        // should add new key8 to new CF
        rocksDBStore.put(new Bytes("key8".getBytes()), "timestamp+88888888".getBytes());
        // one delete on old CF, one put on new CF
        // approx: 3 entries on old CF, 2 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(5L));
        // putIfAbsent()
        // should migrate key4 from old to new CF with old value
        MatcherAssert.assertThat(rocksDBStore.putIfAbsent(new Bytes("key4".getBytes()), "timestamp+4444".getBytes()).length, CoreMatchers.is((8 + 4)));
        // one delete on old CF, one put on new CF
        // approx: 2 entries on old CF, 3 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(5L));
        // should add new key11 to new CF
        MatcherAssert.assertThat(rocksDBStore.putIfAbsent(new Bytes("key11".getBytes()), "timestamp+11111111111".getBytes()), new IsNull());
        // one delete on old CF, one put on new CF
        // approx: 1 entries on old CF, 4 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(5L));
        // should not delete key5 but migrate to new CF
        MatcherAssert.assertThat(rocksDBStore.putIfAbsent(new Bytes("key5".getBytes()), null).length, CoreMatchers.is((8 + 5)));
        // one delete on old CF, one put on new CF
        // approx: 0 entries on old CF, 5 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(5L));
        // should be no-op on both CF
        MatcherAssert.assertThat(rocksDBStore.putIfAbsent(new Bytes("key12".getBytes()), null), new IsNull());
        // two delete operation, however, only one is counted because old CF count was zero before already
        // approx: 0 entries on old CF, 4 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(4L));
        // delete()
        // should delete key6 from old and new CF
        MatcherAssert.assertThat(rocksDBStore.delete(new Bytes("key6".getBytes())).length, CoreMatchers.is((8 + 6)));
        // two delete operation, however, only one is counted because old CF count was zero before already
        // approx: 0 entries on old CF, 3 in new CF
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(3L));
        iteratorsShouldNotMigrateData();
        MatcherAssert.assertThat(rocksDBStore.approximateNumEntries(), CoreMatchers.is(3L));
        rocksDBStore.close();
        verifyOldAndNewColumnFamily();
    }
}

