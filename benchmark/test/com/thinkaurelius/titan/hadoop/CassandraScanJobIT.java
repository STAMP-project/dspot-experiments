package com.thinkaurelius.titan.hadoop;


import TimestampProviders.MICRO;
import TitanHadoopConfiguration.FILTER_PARTITIONED_VERTICES;
import TitanHadoopConfiguration.GRAPH_CONFIG_KEYS;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.scan.ScanJob;
import com.thinkaurelius.titan.diskstorage.util.StandardBaseTransactionConfig;
import com.thinkaurelius.titan.graphdb.TitanGraphBaseTest;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.thinkaurelius.titan.hadoop.scan.CassandraHadoopScanRunner;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// public static class NoopScanJob implements ScanJob {
// 
// @Override
// public void process(StaticBuffer key, Map<SliceQuery, EntryList> entries, ScanMetrics metrics) {
// // do nothing
// }
// 
// @Override
// public List<SliceQuery> getQueries() {
// int len = 4;
// return ImmutableList.of(new SliceQuery(BufferUtil.zeroBuffer(len), BufferUtil.oneBuffer(len)));
// }
// }
public class CassandraScanJobIT extends TitanGraphBaseTest {
    private static final Logger log = LoggerFactory.getLogger(CassandraScanJobIT.class);

    @Test
    public void testSimpleScan() throws BackendException, IOException, InterruptedException, ExecutionException {
        int keys = 1000;
        int cols = 40;
        String[][] values = KeyValueStoreUtil.generateData(keys, cols);
        // Make it only half the number of columns for every 2nd key
        for (int i = 0; i < (values.length); i++) {
            if ((i % 2) == 0)
                values[i] = Arrays.copyOf(values[i], (cols / 2));

        }
        CassandraScanJobIT.log.debug(((("Loading values: " + keys) + "x") + cols));
        KeyColumnValueStoreManager mgr = new com.thinkaurelius.titan.diskstorage.cassandra.thrift.CassandraThriftStoreManager(GraphDatabaseConfiguration.buildGraphConfiguration());
        KeyColumnValueStore store = mgr.openDatabase("edgestore");
        StoreTransaction tx = mgr.beginTransaction(StandardBaseTransactionConfig.of(MICRO));
        KeyColumnValueStoreUtil.loadValues(store, tx, values);
        tx.commit();// noop on Cassandra, but harmless

        SimpleScanJobRunner runner = (ScanJob job,Configuration jobConf,String rootNSName) -> {
            try {
                return new CassandraHadoopScanRunner(job).scanJobConf(jobConf).scanJobConfRoot(rootNSName).partitionerOverride("org.apache.cassandra.dht.Murmur3Partitioner").run();
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        };
        SimpleScanJob.runBasicTests(keys, cols, runner);
    }

    @Test
    public void testPartitionedVertexScan() throws Exception {
        tearDown();
        clearGraph(getConfiguration());
        WriteConfiguration partConf = getConfiguration();
        open(partConf);
        mgmt.makeVertexLabel("part").partition().make();
        finishSchema();
        TitanVertex supernode = graph.addVertex("part");
        for (int i = 0; i < 128; i++) {
            TitanVertex v = graph.addVertex("part");
            v.addEdge("default", supernode);
            if ((0 < i) && (0 == (i % 4)))
                graph.tx().commit();

        }
        graph.tx().commit();
        org.apache.hadoop.conf.Configuration c = new org.apache.hadoop.conf.Configuration();
        c.set((((ConfigElement.getPath(GRAPH_CONFIG_KEYS, true)) + ".") + "storage.cassandra.keyspace"), getClass().getSimpleName());
        c.set((((ConfigElement.getPath(GRAPH_CONFIG_KEYS, true)) + ".") + "storage.backend"), "cassandrathrift");
        c.set("cassandra.input.partitioner.class", "org.apache.cassandra.dht.Murmur3Partitioner");
        Job job = getVertexJobWithDefaultMapper(c);
        // Should throw an exception since filter-partitioned-vertices wasn't enabled
        Assert.assertFalse(job.waitForCompletion(true));
    }

    @Test
    public void testPartitionedVertexFilteredScan() throws Exception {
        tearDown();
        clearGraph(getConfiguration());
        WriteConfiguration partConf = getConfiguration();
        open(partConf);
        mgmt.makeVertexLabel("part").partition().make();
        finishSchema();
        TitanVertex supernode = graph.addVertex("part");
        for (int i = 0; i < 128; i++) {
            TitanVertex v = graph.addVertex("part");
            v.addEdge("default", supernode);
            if ((0 < i) && (0 == (i % 4)))
                graph.tx().commit();

        }
        graph.tx().commit();
        org.apache.hadoop.conf.Configuration c = new org.apache.hadoop.conf.Configuration();
        c.set((((ConfigElement.getPath(GRAPH_CONFIG_KEYS, true)) + ".") + "storage.cassandra.keyspace"), getClass().getSimpleName());
        c.set((((ConfigElement.getPath(GRAPH_CONFIG_KEYS, true)) + ".") + "storage.backend"), "cassandrathrift");
        c.set(ConfigElement.getPath(FILTER_PARTITIONED_VERTICES), "true");
        c.set("cassandra.input.partitioner.class", "org.apache.cassandra.dht.Murmur3Partitioner");
        Job job = getVertexJobWithDefaultMapper(c);
        // Should succeed
        Assert.assertTrue(job.waitForCompletion(true));
    }
}

