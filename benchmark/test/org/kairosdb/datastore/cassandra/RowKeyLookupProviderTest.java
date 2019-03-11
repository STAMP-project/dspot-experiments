package org.kairosdb.datastore.cassandra;


import ClusterConnection.RowKeysTableLookup;
import ClusterConnection.TagIndexedRowKeysTableLookup;
import com.google.common.collect.ImmutableMultimap;
import java.util.EnumSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RowKeyLookupProviderTest {
    private CassandraClient m_cassandraClient;

    private EnumSet<ClusterConnection.Type> m_clusterType;

    @Test
    public void testWithWildcard() {
        ClusterConnection connection = new ClusterConnection(m_cassandraClient, m_clusterType, ImmutableMultimap.of("*", "*"));
        RowKeyLookup rowKeyLookup = connection.getRowKeyLookupForMetric("someMetric");
        Assert.assertThat(rowKeyLookup, CoreMatchers.instanceOf(TagIndexedRowKeysTableLookup.class));
    }

    @Test
    public void testWithoutEmptyStringConfig() {
        ClusterConnection connection = new ClusterConnection(m_cassandraClient, m_clusterType, ImmutableMultimap.of("", ""));
        RowKeyLookup rowKeyLookup = connection.getRowKeyLookupForMetric("someMetric");
        Assert.assertThat(rowKeyLookup, CoreMatchers.instanceOf(RowKeysTableLookup.class));
    }

    @Test
    public void testWithEmptySet() {
        ClusterConnection connection = new ClusterConnection(m_cassandraClient, m_clusterType, ImmutableMultimap.of());
        RowKeyLookup rowKeyLookup = connection.getRowKeyLookupForMetric("someMetric");
        Assert.assertThat(rowKeyLookup, CoreMatchers.instanceOf(RowKeysTableLookup.class));
    }

    @Test
    public void testWithMetricSetConfig() {
        ClusterConnection connection = new ClusterConnection(m_cassandraClient, m_clusterType, ImmutableMultimap.of("metricA", "*", "metricB", "*"));
        RowKeyLookup rowKeyLookup = connection.getRowKeyLookupForMetric("someMetric");
        Assert.assertThat(connection.getRowKeyLookupForMetric("someMetric"), CoreMatchers.instanceOf(RowKeysTableLookup.class));
        Assert.assertThat(connection.getRowKeyLookupForMetric("metricA"), CoreMatchers.instanceOf(TagIndexedRowKeysTableLookup.class));
        Assert.assertThat(connection.getRowKeyLookupForMetric("metricB"), CoreMatchers.instanceOf(TagIndexedRowKeysTableLookup.class));
    }
}

