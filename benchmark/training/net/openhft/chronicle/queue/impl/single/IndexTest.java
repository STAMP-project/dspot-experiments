/**
 * Copyright 2016 higherfrequencytrading.com
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
package net.openhft.chronicle.queue.impl.single;


import java.io.IOException;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ChronicleQueueTestBase;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.WireType;
import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static StoreTailer.INDEXING_LINEAR_SCAN_THRESHOLD;


/**
 *
 *
 * @author Rob Austin.
 */
@RunWith(Parameterized.class)
public class IndexTest extends ChronicleQueueTestBase {
    @NotNull
    private final WireType wireType;

    private ThreadDump threadDump;

    /**
     *
     *
     * @param wireType
     * 		the type of the wire
     */
    public IndexTest(@NotNull
    WireType wireType) {
        this.wireType = wireType;
    }

    @Test
    public void test() throws IOException {
        try (final RollingChronicleQueue queue = SingleChronicleQueueBuilder.binary(getTmpDir()).testBlockSize().wireType(this.wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            for (int i = 0; i < 5; i++) {
                final int n = i;
                appender.writeDocument(( w) -> w.write(TestKey.test).int32(n));
                final int cycle = queue.lastCycle();
                long index0 = queue.rollCycle().toIndex(cycle, n);
                long indexA = appender.lastIndexAppended();
                accessHexEquals(index0, indexA);
            }
        }
    }

    @Test
    public void shouldShortCircuitIndexLookupWhenNewIndexIsCloseToPreviousIndex() throws Exception {
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(getTmpDir()).testBlockSize().wireType(this.wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            final int messageCount = (INDEXING_LINEAR_SCAN_THRESHOLD) + 5;
            final long[] indices = new long[messageCount];
            for (int i = 0; i < messageCount; i++) {
                try (final DocumentContext ctx = appender.writingDocument()) {
                    ctx.wire().write("event").int32(i);
                    indices[i] = ctx.index();
                }
            }
            final SingleChronicleQueueExcerpts.StoreTailer tailer = ((SingleChronicleQueueExcerpts.StoreTailer) (queue.createTailer()));
            tailer.moveToIndex(indices[0]);
            Assert.assertThat(tailer.index(), CoreMatchers.is(indices[0]));
            Assert.assertThat(tailer.getIndexMoveCount(), CoreMatchers.is(1));
            tailer.moveToIndex(indices[0]);
            Assert.assertThat(tailer.index(), CoreMatchers.is(indices[0]));
            Assert.assertThat(tailer.getIndexMoveCount(), CoreMatchers.is(1));
            tailer.moveToIndex(indices[2]);
            Assert.assertThat(tailer.index(), CoreMatchers.is(indices[2]));
            Assert.assertThat(tailer.getIndexMoveCount(), CoreMatchers.is(1));
            tailer.moveToIndex(indices[((INDEXING_LINEAR_SCAN_THRESHOLD) + 2)]);
            Assert.assertThat(tailer.index(), CoreMatchers.is(indices[((INDEXING_LINEAR_SCAN_THRESHOLD) + 2)]));
            Assert.assertThat(tailer.getIndexMoveCount(), CoreMatchers.is(2));
            // document that moving backwards requires an index scan
            tailer.moveToIndex(indices[((INDEXING_LINEAR_SCAN_THRESHOLD) - 1)]);
            Assert.assertThat(tailer.index(), CoreMatchers.is(indices[((INDEXING_LINEAR_SCAN_THRESHOLD) - 1)]));
            Assert.assertThat(tailer.getIndexMoveCount(), CoreMatchers.is(3));
        }
    }
}

