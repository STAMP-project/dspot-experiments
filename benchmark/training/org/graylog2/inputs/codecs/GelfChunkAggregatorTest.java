/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.codecs;


import CodecAggregator.Result;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import io.netty.buffer.ByteBuf;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.graylog2.plugin.InstantMillisProvider;
import org.graylog2.plugin.inputs.codecs.CodecAggregator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class GelfChunkAggregatorTest {
    private static final byte[] CHUNK_MAGIC_BYTES = new byte[]{ 30, 15 };

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private ScheduledThreadPoolExecutor poolExecutor;

    private GelfChunkAggregator aggregator;

    private MetricRegistry metricRegistry;

    @Test
    public void addSingleChunk() {
        final ByteBuf[] singleChunk = createChunkedMessage(512, 1024);
        final CodecAggregator.Result result = aggregator.addChunk(singleChunk[0]);
        Assert.assertNotNull("message should be complete", result.getMessage());
        Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
        Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
    }

    @Test
    public void manyChunks() {
        final ByteBuf[] chunks = createChunkedMessage((4096 + 512), 1024);// creates 5 chunks

        int i = 0;
        for (final ByteBuf chunk : chunks) {
            i++;
            final CodecAggregator.Result result = aggregator.addChunk(chunk);
            Assert.assertTrue(result.isValid());
            if (i == 5) {
                Assert.assertNotNull("message should've been assembled from chunks", result.getMessage());
                Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
                Assert.assertEquals(5, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
            } else {
                Assert.assertNull("chunks not complete", result.getMessage());
                Assert.assertEquals("message not complete yet", 0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
                Assert.assertEquals(i, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
                Assert.assertEquals("one message waiting", 1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
                Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
            }
        }
    }

    @Test
    public void tooManyChunks() {
        final ByteBuf[] chunks = createChunkedMessage((129 * 1024), 1024);
        int i = 1;
        for (final ByteBuf chunk : chunks) {
            final CodecAggregator.Result result = aggregator.addChunk(chunk);
            if (i == 129) {
                Assert.assertFalse((("Message invalidated (chunk #" + i) + ")"), result.isValid());
                Assert.assertNull((("Message discarded (chunk #" + i) + ")"), result.getMessage());
            } else {
                Assert.assertTrue((("Incomplete message valid (chunk #" + i) + ")"), result.isValid());
                Assert.assertNull((("Message not complete (chunk #" + i) + ")"), result.getMessage());
            }
            i++;
        }
    }

    @Test
    public void missingChunk() {
        final DateTime initialTime = new DateTime(2014, 1, 1, 1, 59, 59, 0, DateTimeZone.UTC);
        final InstantMillisProvider clock = new InstantMillisProvider(initialTime);
        DateTimeUtils.setCurrentMillisProvider(clock);
        // we don't want the clean up task to run automatically
        poolExecutor = Mockito.mock(ScheduledThreadPoolExecutor.class);
        final MetricRegistry metricRegistry = new MetricRegistry();
        aggregator = new GelfChunkAggregator(poolExecutor, metricRegistry);
        final GelfChunkAggregator.ChunkEvictionTask evictionTask = aggregator.new ChunkEvictionTask();
        final ByteBuf[] chunks = createChunkedMessage((4096 + 512), 1024);// creates 5 chunks

        int i = 0;
        for (final ByteBuf chunk : chunks) {
            final CodecAggregator.Result result;
            // skip first chunk
            if ((i++) == 0) {
                continue;
            }
            result = aggregator.addChunk(chunk);
            Assert.assertTrue(result.isValid());
            Assert.assertNull("chunks not complete", result.getMessage());
        }
        // move clock forward enough to evict all of the chunks
        clock.tick(Period.seconds(10));
        evictionTask.run();
        final CodecAggregator.Result result = aggregator.addChunk(chunks[0]);
        Assert.assertNull("message should not be complete because chunks were evicted already", result.getMessage());
        Assert.assertTrue(result.isValid());
        // we send all chunks but the last one comes too late
        Assert.assertEquals("no message is complete", 0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
        Assert.assertEquals("received 5 chunks", 5, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
        Assert.assertEquals("last chunk creates another waiting message", 1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
        Assert.assertEquals("4 chunks expired", 4, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
        Assert.assertEquals("one message expired", 1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
        Assert.assertEquals("no duplicate chunks", 0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
        // reset clock for other tests
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void outOfOrderChunks() {
        final ByteBuf[] chunks = createChunkedMessage((4096 + 512), 1024);// creates 5 chunks

        CodecAggregator.Result result = null;
        for (int i = (chunks.length) - 1; i >= 0; i--) {
            result = aggregator.addChunk(chunks[i]);
            if (i != 0) {
                Assert.assertNull("message still incomplete", result.getMessage());
            }
        }
        Assert.assertNotNull(result);
        Assert.assertNotNull("first chunk should've completed the message", result.getMessage());
        Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
        Assert.assertEquals(5, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
    }

    @Test
    public void differentIdsDoNotInterfere() {
        final ByteBuf[] msg1 = createChunkedMessage((4096 + 1), 1024, generateMessageId(1));// 5 chunks;

        final ByteBuf[] msg2 = createChunkedMessage((4096 + 1), 1024, generateMessageId(2));// 5 chunks;

        CodecAggregator.Result result1 = null;
        CodecAggregator.Result result2 = null;
        for (int i = 0; i < (msg1.length); i++) {
            result1 = aggregator.addChunk(msg1[i]);
            if (i > 0) {
                result2 = aggregator.addChunk(msg2[i]);
            }
        }
        Assert.assertNotNull(result1);
        Assert.assertNotNull(result2);
        Assert.assertNotNull("message 1 should be complete", result1.getMessage());
        Assert.assertNull("message 2 should not be complete", result2.getMessage());
        // only one is complete, we sent 9 chunks
        Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
        Assert.assertEquals(9, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
        Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
    }

    @Test
    public void duplicateChunk() {
        final byte[] messageId1 = generateMessageId(1);
        final byte[] messageId2 = generateMessageId(2);
        final ByteBuf chunk1 = createChunk(messageId1, ((byte) (0)), ((byte) (2)), new byte[16]);
        final ByteBuf chunk2 = createChunk(messageId1, ((byte) (0)), ((byte) (2)), new byte[16]);
        final ByteBuf chunk3 = createChunk(messageId2, ((byte) (0)), ((byte) (2)), new byte[16]);
        final ByteBuf chunk4 = createChunk(messageId1, ((byte) (1)), ((byte) (2)), new byte[16]);
        final ByteBuf chunk5 = createChunk(messageId2, ((byte) (1)), ((byte) (2)), new byte[16]);
        Assert.assertNull("message should not be complete", aggregator.addChunk(chunk1).getMessage());
        Assert.assertNull("message should not be complete", aggregator.addChunk(chunk2).getMessage());
        Assert.assertNull("message should not be complete", aggregator.addChunk(chunk3).getMessage());
        Assert.assertNotNull("message 1 should be complete", aggregator.addChunk(chunk4).getMessage());
        Assert.assertNotNull("message 2 should be complete", aggregator.addChunk(chunk5).getMessage());
        Assert.assertEquals(2, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.COMPLETE_MESSAGES));
        Assert.assertEquals(5, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.CHUNK_COUNTER));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.WAITING_MESSAGES));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_CHUNKS));
        Assert.assertEquals(0, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.EXPIRED_MESSAGES));
        Assert.assertEquals(1, GelfChunkAggregatorTest.counterValueNamed(metricRegistry, GelfChunkAggregator.DUPLICATE_CHUNKS));
    }

    @Test
    public void testChunkEntryCompareTo() throws Exception {
        // Test if the ChunkEntry#compareTo() method can handle ChunkEntry objects which have the same timestamp.
        // See: https://github.com/Graylog2/graylog2-server/issues/1462
        final ConcurrentSkipListSet<GelfChunkAggregator.ChunkEntry> sortedEvictionSet = new ConcurrentSkipListSet<>();
        final long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            sortedEvictionSet.add(new GelfChunkAggregator.ChunkEntry(1, currentTime, ("a" + i)));
        }
        final int size = sortedEvictionSet.size();
        for (int i = 0; i < size; i++) {
            sortedEvictionSet.remove(sortedEvictionSet.first());
        }
        Assert.assertTrue("eviction set should be empty", sortedEvictionSet.isEmpty());
    }

    @Test
    public void testChunkEntryEquals() throws Exception {
        final GelfChunkAggregator.ChunkEntry entry = new GelfChunkAggregator.ChunkEntry(1, 0L, "id");
        assertThat(entry).isEqualTo(new GelfChunkAggregator.ChunkEntry(1, 0L, "id"));
        assertThat(entry).isEqualTo(new GelfChunkAggregator.ChunkEntry(2, 0L, "id"));
        assertThat(entry).isNotEqualTo(new GelfChunkAggregator.ChunkEntry(1, 1L, "id"));
        assertThat(entry).isNotEqualTo(new GelfChunkAggregator.ChunkEntry(1, 0L, "foo"));
    }

    @Test
    public void testChunkEntryHashCode() throws Exception {
        final GelfChunkAggregator.ChunkEntry entry = new GelfChunkAggregator.ChunkEntry(1, 0L, "id");
        assertThat(entry.hashCode()).isEqualTo(new GelfChunkAggregator.ChunkEntry(1, 0L, "id").hashCode());
        assertThat(entry.hashCode()).isEqualTo(new GelfChunkAggregator.ChunkEntry(2, 0L, "id").hashCode());
        assertThat(entry.hashCode()).isNotEqualTo(new GelfChunkAggregator.ChunkEntry(1, 1L, "id").hashCode());
        assertThat(entry.hashCode()).isNotEqualTo(new GelfChunkAggregator.ChunkEntry(1, 0L, "foo").hashCode());
    }

    private static class SingleNameMatcher implements MetricFilter {
        private final String metricName;

        public SingleNameMatcher(String metricName) {
            this.metricName = metricName;
        }

        @Override
        public boolean matches(String name, Metric metric) {
            return metricName.equals(name);
        }
    }
}

