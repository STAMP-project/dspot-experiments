/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.wali;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.wali.DummyRecord;
import org.wali.DummyRecordSerde;
import org.wali.SerDeFactory;
import org.wali.UpdateType;


public class TestHashMapSnapshot {
    private final File storageDirectory = new File("target/test-hashmap-snapshot");

    private DummyRecordSerde serde;

    private SerDeFactory<DummyRecord> serdeFactory;

    @Test
    public void testSuccessfulRoundTrip() throws IOException {
        final HashMapSnapshot<DummyRecord> snapshot = new HashMapSnapshot(storageDirectory, serdeFactory);
        final Map<String, String> props = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.CREATE);
            props.put("key", String.valueOf(i));
            record.setProperties(props);
            snapshot.update(Collections.singleton(record));
        }
        for (int i = 2; i < 10; i += 2) {
            final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.DELETE);
            snapshot.update(Collections.singleton(record));
        }
        for (int i = 1; i < 10; i += 2) {
            final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.SWAP_OUT);
            record.setSwapLocation(("swapFile-" + i));
            snapshot.update(Collections.singleton(record));
        }
        final DummyRecord swapIn7 = new DummyRecord("7", UpdateType.SWAP_IN);
        swapIn7.setSwapLocation("swapFile-7");
        snapshot.update(Collections.singleton(swapIn7));
        final Set<String> swappedOutLocations = new HashSet<>();
        swappedOutLocations.add("swapFile-1");
        swappedOutLocations.add("swapFile-3");
        swappedOutLocations.add("swapFile-5");
        swappedOutLocations.add("swapFile-9");
        final SnapshotCapture<DummyRecord> capture = snapshot.prepareSnapshot(180L);
        Assert.assertEquals(180L, capture.getMaxTransactionId());
        Assert.assertEquals(swappedOutLocations, capture.getSwapLocations());
        final Map<Object, DummyRecord> records = capture.getRecords();
        Assert.assertEquals(2, records.size());
        Assert.assertTrue(records.containsKey("0"));
        Assert.assertTrue(records.containsKey("7"));
        snapshot.writeSnapshot(capture);
        final SnapshotRecovery<DummyRecord> recovery = snapshot.recover();
        Assert.assertEquals(180L, recovery.getMaxTransactionId());
        Assert.assertEquals(swappedOutLocations, recovery.getRecoveredSwapLocations());
        final Map<Object, DummyRecord> recoveredRecords = recovery.getRecords();
        Assert.assertEquals(records, recoveredRecords);
    }

    @Test
    public void testOOMEWhenWritingResultsInPreviousSnapshotStillRecoverable() throws IOException {
        final HashMapSnapshot<DummyRecord> snapshot = new HashMapSnapshot(storageDirectory, serdeFactory);
        final Map<String, String> props = new HashMap<>();
        for (int i = 0; i < 11; i++) {
            final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.CREATE);
            props.put("key", String.valueOf(i));
            record.setProperties(props);
            snapshot.update(Collections.singleton(record));
        }
        final DummyRecord swapOutRecord = new DummyRecord("10", UpdateType.SWAP_OUT);
        swapOutRecord.setSwapLocation("SwapLocation-1");
        snapshot.update(Collections.singleton(swapOutRecord));
        snapshot.writeSnapshot(snapshot.prepareSnapshot(25L));
        serde.setThrowOOMEAfterNSerializeEdits(3);
        try {
            snapshot.writeSnapshot(snapshot.prepareSnapshot(150L));
            Assert.fail("Expected OOME");
        } catch (final OutOfMemoryError oome) {
            // expected
        }
        final SnapshotRecovery<DummyRecord> recovery = snapshot.recover();
        Assert.assertEquals(25L, recovery.getMaxTransactionId());
        final Map<Object, DummyRecord> recordMap = recovery.getRecords();
        Assert.assertEquals(10, recordMap.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(recordMap.containsKey(String.valueOf(i)));
        }
        for (final Map.Entry<Object, DummyRecord> entry : recordMap.entrySet()) {
            final DummyRecord record = entry.getValue();
            final Map<String, String> properties = record.getProperties();
            Assert.assertNotNull(properties);
            Assert.assertEquals(1, properties.size());
            Assert.assertEquals(entry.getKey(), properties.get("key"));
        }
        final Set<String> swapLocations = recovery.getRecoveredSwapLocations();
        Assert.assertEquals(1, swapLocations.size());
        Assert.assertTrue(swapLocations.contains("SwapLocation-1"));
    }

    @Test
    public void testIOExceptionWhenWritingResultsInPreviousSnapshotStillRecoverable() throws IOException {
        final HashMapSnapshot<DummyRecord> snapshot = new HashMapSnapshot(storageDirectory, serdeFactory);
        final Map<String, String> props = new HashMap<>();
        for (int i = 0; i < 11; i++) {
            final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.CREATE);
            props.put("key", String.valueOf(i));
            record.setProperties(props);
            snapshot.update(Collections.singleton(record));
        }
        final DummyRecord swapOutRecord = new DummyRecord("10", UpdateType.SWAP_OUT);
        swapOutRecord.setSwapLocation("SwapLocation-1");
        snapshot.update(Collections.singleton(swapOutRecord));
        snapshot.writeSnapshot(snapshot.prepareSnapshot(25L));
        serde.setThrowIOEAfterNSerializeEdits(3);
        for (int i = 0; i < 5; i++) {
            try {
                snapshot.writeSnapshot(snapshot.prepareSnapshot(150L));
                Assert.fail("Expected IOE");
            } catch (final IOException ioe) {
                // expected
            }
        }
        final SnapshotRecovery<DummyRecord> recovery = snapshot.recover();
        Assert.assertEquals(25L, recovery.getMaxTransactionId());
        final Map<Object, DummyRecord> recordMap = recovery.getRecords();
        Assert.assertEquals(10, recordMap.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(recordMap.containsKey(String.valueOf(i)));
        }
        for (final Map.Entry<Object, DummyRecord> entry : recordMap.entrySet()) {
            final DummyRecord record = entry.getValue();
            final Map<String, String> properties = record.getProperties();
            Assert.assertNotNull(properties);
            Assert.assertEquals(1, properties.size());
            Assert.assertEquals(entry.getKey(), properties.get("key"));
        }
        final Set<String> swapLocations = recovery.getRecoveredSwapLocations();
        Assert.assertEquals(1, swapLocations.size());
        Assert.assertTrue(swapLocations.contains("SwapLocation-1"));
    }
}

