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


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.values.IntValue;
import net.openhft.chronicle.hash.impl.util.Cleaner;
import net.openhft.chronicle.hash.impl.util.CleanerUtils;
import net.openhft.chronicle.hash.serialization.impl.StringSizedReader;
import net.openhft.chronicle.hash.serialization.impl.StringUtf8DataAccess;
import net.openhft.chronicle.values.Values;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MemoryLeaksTest {
    /**
     * Accounting {@link CountedStringReader} creation and finalization. All serializers,
     * created since the map creation, should become unreachable after map.close() or collection by
     * Cleaner, it means that map contexts (referencing serializers) are collected by the GC
     */
    private final AtomicInteger serializerCount = new AtomicInteger();

    private final List<WeakReference<MemoryLeaksTest.CountedStringReader>> serializers = new ArrayList<>();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private boolean persisted;

    private ChronicleMapBuilder<IntValue, String> builder;

    private boolean closeWithinContext;

    public MemoryLeaksTest(String testType, boolean replicated, boolean persisted, boolean closeWithinContext) {
        this.persisted = persisted;
        this.closeWithinContext = closeWithinContext;
        builder = ChronicleMap.of(IntValue.class, String.class).constantKeySizeBySample(Values.newHeapInstance(IntValue.class)).valueReaderAndDataAccess(new MemoryLeaksTest.CountedStringReader(), new StringUtf8DataAccess());
        if (replicated)
            builder.replication(((byte) (1)));

        builder.entries(1).averageValueSize(10);
    }

    @Test(timeout = 60000)
    public void testChronicleMapCollectedAndDirectMemoryReleased() throws IOException, InterruptedException {
        if (!(OS.isWindows())) {
            // This test is flaky in Linux and Mac OS apparently because some native memory from
            // running previous/concurrent tests is released during this test, that infers with
            // the (*) check below. The aim of this test is to check that native memory is not
            // leaked and it is proven if it succeeds at least sometimes at least in some OSes.
            // This tests is successful always in Windows and successful in Linux and OS X when run
            // alone, rather than along all other Chronicle Map's tests.
            return;
        }
        long nativeMemoryUsedBeforeMap = nativeMemoryUsed();
        int serializersBeforeMap = serializerCount.get();
        try (ChronicleMap<IntValue, String> map = getMap()) {
            long expectedNativeMemory = nativeMemoryUsedBeforeMap + (map.offHeapMemoryUsed());
            Assert.assertEquals(expectedNativeMemory, nativeMemoryUsed());
            tryCloseFromContext(map);
            WeakReference<ChronicleMap<IntValue, String>> ref = new WeakReference(map);
            Assert.assertNotNull(ref.get());
            // map = null;
            // Wait until Map is collected by GC
            while ((ref.get()) != null) {
                System.gc();
                byte[] garbage = new byte[10000000];
                Thread.yield();
            } 
            // Wait until Cleaner is called and memory is returned to the system
            for (int i = 0; i < 6000; i++) {
                if ((nativeMemoryUsedBeforeMap == (nativeMemoryUsed()))// (*)
                 && ((serializerCount.get()) == serializersBeforeMap)) {
                    break;
                }
                System.gc();
                byte[] garbage = new byte[10000000];
                Thread.sleep(10);
            }
            Assert.assertEquals(nativeMemoryUsedBeforeMap, nativeMemoryUsed());
            Assert.assertEquals(serializersBeforeMap, serializerCount.get());
        }
    }

    private class CountedStringReader extends StringSizedReader {
        private final String creationStackTrace;

        private final Cleaner cleaner;

        CountedStringReader() {
            serializerCount.incrementAndGet();
            serializers.add(new WeakReference<>(this));
            cleaner = CleanerUtils.createCleaner(this, serializerCount::decrementAndGet);
            try (StringWriter stringWriter = new StringWriter();PrintWriter printWriter = new PrintWriter(stringWriter)) {
                new Exception().printStackTrace(printWriter);
                printWriter.flush();
                creationStackTrace = stringWriter.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public MemoryLeaksTest.CountedStringReader copy() {
            return new MemoryLeaksTest.CountedStringReader();
        }
    }
}

