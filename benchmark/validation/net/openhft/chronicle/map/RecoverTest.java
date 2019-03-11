/**
 * Copyright 2012-2018 Chronicle Map Contributors
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
package net.openhft.chronicle.map;


import ChronicleHashCorruption.Listener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.hash.ChecksumEntry;
import net.openhft.chronicle.hash.ChronicleHashCorruption;
import net.openhft.chronicle.values.Values;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RecoverTest {
    Logger LOG = LoggerFactory.getLogger(RecoverTest.class);

    ReplicatedChronicleMap<Integer, Integer, ?> map;

    @Test
    public void testCorruptedEntryRecovery() throws IOException {
        File file = ChronicleMapTest.getPersistenceFile();
        try (ChronicleMap<Integer, LongValue> map = ChronicleMap.of(Integer.class, LongValue.class).entries(1).createPersistedTo(file)) {
            LongValue value = Values.newHeapInstance(LongValue.class);
            value.setValue(42);
            map.put(1, value);
            try (ExternalMapQueryContext<Integer, LongValue, ?> c = map.queryContext(1)) {
                // Update lock required for calling ChecksumEntry.checkSum()
                c.updateLock().lock();
                MapEntry<Integer, LongValue> entry = c.entry();
                Assert.assertNotNull(entry);
                ChecksumEntry checksumEntry = ((ChecksumEntry) (entry));
                Assert.assertTrue(checksumEntry.checkSum());
                // to access off-heap bytes, should call value().getUsing() with Native value
                // provided. Simple get() return Heap value by default
                LongValue nativeValue = entry.value().getUsing(Values.newNativeReference(LongValue.class));
                // This value bytes update bypass Chronicle Map internals, so checksum is not
                // updated automatically
                nativeValue.setValue(43);
                Assert.assertFalse(checksumEntry.checkSum());
            }
        }
        AtomicInteger corruptionCounter = new AtomicInteger(0);
        ChronicleHashCorruption.Listener corruptionListener = ( corruption) -> corruptionCounter.incrementAndGet();
        // noinspection EmptyTryBlock
        try (ChronicleMap<Integer, LongValue> ignore = ChronicleMap.of(Integer.class, LongValue.class).entries(1).createOrRecoverPersistedTo(file, true, corruptionListener)) {
        }
        Assert.assertTrue(((corruptionCounter.get()) > 0));
    }
}

