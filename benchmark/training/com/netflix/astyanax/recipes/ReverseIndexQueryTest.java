package com.netflix.astyanax.recipes;


import com.google.common.base.Function;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Cluster;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.annotations.Component;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReverseIndexQueryTest {
    private static Logger LOG = LoggerFactory.getLogger(ReverseIndexQueryTest.class);

    private static AstyanaxContext<Cluster> clusterContext;

    private static final String TEST_CLUSTER_NAME = "TestCluster";

    private static final String TEST_KEYSPACE_NAME = "ReverseIndexTest";

    private static final String TEST_DATA_CF = "Data";

    private static final String TEST_INDEX_CF = "Index";

    private static final boolean TEST_INIT_KEYSPACE = true;

    private static final long ROW_COUNT = 1000;

    private static final int SHARD_COUNT = 11;

    public static final String SEEDS = "localhost:7102";

    private static ColumnFamily<Long, String> CF_DATA = ColumnFamily.newColumnFamily(ReverseIndexQueryTest.TEST_DATA_CF, LongSerializer.get(), StringSerializer.get());

    private static class IndexEntry {
        @Component(ordinal = 0)
        Long value;

        @Component(ordinal = 1)
        Long key;

        public IndexEntry(Long value, Long key) {
            this.value = value;
            this.key = key;
        }
    }

    private static Serializer<ReverseIndexQueryTest.IndexEntry> indexEntitySerializer = new com.netflix.astyanax.serializers.AnnotatedCompositeSerializer<ReverseIndexQueryTest.IndexEntry>(ReverseIndexQueryTest.IndexEntry.class);

    private static ColumnFamily<String, ReverseIndexQueryTest.IndexEntry> CF_INDEX = ColumnFamily.newColumnFamily(ReverseIndexQueryTest.TEST_INDEX_CF, StringSerializer.get(), ReverseIndexQueryTest.indexEntitySerializer);

    @Test
    public void testReverseIndex() throws Exception {
        ReverseIndexQueryTest.LOG.info("Starting");
        final AtomicLong counter = new AtomicLong();
        Keyspace keyspace = ReverseIndexQueryTest.clusterContext.getEntity().getKeyspace(ReverseIndexQueryTest.TEST_KEYSPACE_NAME);
        ReverseIndexQuery.newQuery(keyspace, ReverseIndexQueryTest.CF_DATA, ReverseIndexQueryTest.CF_INDEX.getName(), LongSerializer.get()).fromIndexValue(100L).toIndexValue(10000L).withIndexShards(new Shards.StringShardBuilder().setPrefix("B_").setShardCount(ReverseIndexQueryTest.SHARD_COUNT).build()).withColumnSlice(Arrays.asList("A")).forEach(new Function<Row<Long, String>, Void>() {
            @Override
            public Void apply(Row<Long, String> row) {
                StringBuilder sb = new StringBuilder();
                for (Column<String> column : row.getColumns()) {
                    sb.append(column.getName()).append(", ");
                }
                counter.incrementAndGet();
                ReverseIndexQueryTest.LOG.info(((("Row: " + (row.getKey())) + " Columns: ") + (sb.toString())));
                return null;
            }
        }).forEachIndexEntry(new com.netflix.astyanax.recipes.ReverseIndexQuery.IndexEntryCallback<Long, Long>() {
            @Override
            public boolean handleEntry(Long key, Long value, ByteBuffer meta) {
                ReverseIndexQueryTest.LOG.info(((((("Row : " + key) + " IndexValue: ") + value) + " Meta: ") + (LongSerializer.get().fromByteBuffer(meta))));
                if ((key % 2) == 1)
                    return false;

                return true;
            }
        }).execute();
        ReverseIndexQueryTest.LOG.info((("Read " + (counter.get())) + " rows"));
    }
}

