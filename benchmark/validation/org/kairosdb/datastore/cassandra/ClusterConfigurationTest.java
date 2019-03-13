package org.kairosdb.datastore.cassandra;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import java.io.IOException;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.KairosConfig;


public class ClusterConfigurationTest {
    @Test(expected = IllegalStateException.class)
    public void test_startTimeAfterEndTime() throws ParseException {
        setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2001-01-01T12:00-0700"));
    }

    @Test
    public void test_queryBeforeClusterTime() throws ParseException {
        ClusterConfiguration cluster = setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2003-01-01T12:00-0700"));
        Assert.assertFalse(cluster.containRange(KairosConfig.DATE_TIME_FORMAT.parse("2000-01-01T12:00-0700").getTime(), KairosConfig.DATE_TIME_FORMAT.parse("2001-01-01T12:00-0700").getTime()));
    }

    @Test
    public void test_queryAfterClusterTime() throws ParseException {
        ClusterConfiguration cluster = setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2003-01-01T12:00-0700"));
        Assert.assertFalse(cluster.containRange(KairosConfig.DATE_TIME_FORMAT.parse("2004-01-01T12:00-0700").getTime(), KairosConfig.DATE_TIME_FORMAT.parse("2005-01-01T12:00-0700").getTime()));
    }

    @Test
    public void test_queryOverlapClusterStartTime() throws ParseException {
        ClusterConfiguration cluster = setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2003-01-01T12:00-0700"));
        Assert.assertTrue(cluster.containRange(KairosConfig.DATE_TIME_FORMAT.parse("2000-01-01T12:00-0700").getTime(), KairosConfig.DATE_TIME_FORMAT.parse("2002-02-01T12:00-0700").getTime()));
    }

    @Test
    public void test_queryOverlapClusterEndTime() throws ParseException {
        ClusterConfiguration cluster = setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2003-01-01T12:00-0700"));
        Assert.assertTrue(cluster.containRange(KairosConfig.DATE_TIME_FORMAT.parse("2002-12-01T12:00-0700").getTime(), KairosConfig.DATE_TIME_FORMAT.parse("2003-02-01T12:00-0700").getTime()));
    }

    @Test
    public void test_queryOverlapEntireClusterTime() throws ParseException {
        ClusterConfiguration cluster = setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2003-01-01T12:00-0700"));
        Assert.assertTrue(cluster.containRange(KairosConfig.DATE_TIME_FORMAT.parse("2000-12-01T12:00-0700").getTime(), KairosConfig.DATE_TIME_FORMAT.parse("2003-02-01T12:00-0700").getTime()));
    }

    @Test
    public void test_queryWithinClusterTime() throws ParseException {
        ClusterConfiguration cluster = setupCluster(ImmutableMap.of("start_time", "2002-01-01T12:00-0700", "end_time", "2003-01-01T12:00-0700"));
        Assert.assertTrue(cluster.containRange(KairosConfig.DATE_TIME_FORMAT.parse("2002-02-01T12:00-0700").getTime(), KairosConfig.DATE_TIME_FORMAT.parse("2002-03-01T12:00-0700").getTime()));
    }

    @Test
    public void test_tagIndex_list() throws IOException, ParseException {
        String config = "tag_indexed_row_key_lookup_metrics: [key1, key2]";
        ClusterConfiguration clusterConfiguration = setupCluster(config);
        assertThat(clusterConfiguration.getTagIndexedMetrics()).containsAllEntriesOf(ImmutableMultimap.of("key1", "*", "key2", "*"));
    }

    @Test
    public void test_discoverConfigLoading() throws IOException, ParseException {
        String config = "tag_indexed_row_key_lookup_metrics: {key1: [], key2: [value1, value2]}";
        ClusterConfiguration clusterConfiguration = setupCluster(config);
        assertThat(clusterConfiguration.getTagIndexedMetrics()).containsAllEntriesOf(ImmutableMultimap.of("key1", "*", "key2", "value1", "key2", "value2"));
    }
}

