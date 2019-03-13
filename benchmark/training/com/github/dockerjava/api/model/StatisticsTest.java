package com.github.dockerjava.api.model;


import RemoteApiVersion.VERSION_1_27;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


/**
 *
 *
 * @author Yuting Liu
 */
public class StatisticsTest {
    @Test
    public void serderJson1() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Statistics.class);
        final Statistics statistics = JSONSamples.testRoundTrip(VERSION_1_27, "containers/container/stats/stats1.json", type);
        MatcherAssert.assertThat(statistics.getRead(), IsEqual.equalTo("2017-12-06T00:42:03.8352972Z"));
        final StatisticNetworksConfig network = statistics.getNetworks().get("eth0");
        MatcherAssert.assertThat(network.getRxBytes(), CoreMatchers.is(1230L));
        MatcherAssert.assertThat(network.getRxPackets(), CoreMatchers.is(19L));
        MatcherAssert.assertThat(network.getRxErrors(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(network.getRxDropped(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(network.getTxBytes(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(network.getTxPackets(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(network.getTxErrors(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(network.getTxDropped(), CoreMatchers.is(0L));
        final MemoryStatsConfig memoryStats = statistics.getMemoryStats();
        MatcherAssert.assertThat(memoryStats.getUsage(), CoreMatchers.is(647168L));
        MatcherAssert.assertThat(memoryStats.getMaxUsage(), CoreMatchers.is(1703936L));
        final StatsConfig stats = memoryStats.getStats();
        MatcherAssert.assertThat(stats.getActiveAnon(), CoreMatchers.is(102400L));
        MatcherAssert.assertThat(stats.getActiveFile(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getCache(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getDirty(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getHierarchicalMemoryLimit(), CoreMatchers.is(9223372036854771712L));
        MatcherAssert.assertThat(stats.getHierarchicalMemswLimit(), CoreMatchers.is(9223372036854771712L));
        MatcherAssert.assertThat(stats.getInactiveAnon(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getInactiveFile(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getMappedFile(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getPgfault(), CoreMatchers.is(9656L));
        MatcherAssert.assertThat(stats.getPgmajfault(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getPgpgin(), CoreMatchers.is(3425L));
        MatcherAssert.assertThat(stats.getPgpgout(), CoreMatchers.is(3400L));
        MatcherAssert.assertThat(stats.getRss(), CoreMatchers.is(102400L));
        MatcherAssert.assertThat(stats.getRssHuge(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getSwap(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalActiveAnon(), CoreMatchers.is(102400L));
        MatcherAssert.assertThat(stats.getTotalActiveFile(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalCache(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalDirty(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalInactiveAnon(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalInactiveFile(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalMappedFile(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalPgfault(), CoreMatchers.is(9656L));
        MatcherAssert.assertThat(stats.getTotalPgmajfault(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalPgpgin(), CoreMatchers.is(3425L));
        MatcherAssert.assertThat(stats.getTotalPgpgout(), CoreMatchers.is(3400L));
        MatcherAssert.assertThat(stats.getTotalRss(), CoreMatchers.is(102400L));
        MatcherAssert.assertThat(stats.getTotalRssHuge(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalSwap(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalUnevictable(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getTotalWriteback(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getUnevictable(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(stats.getWriteback(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(memoryStats.getLimit(), CoreMatchers.is(2095874048L));
        MatcherAssert.assertThat(memoryStats.getFailcnt(), CoreMatchers.is(0L));
        final BlkioStatsConfig blkioStats = statistics.getBlkioStats();
        MatcherAssert.assertThat(blkioStats.getIoServiceBytesRecursive(), Matchers.<BlkioStatEntry>hasSize(5));
        MatcherAssert.assertThat(blkioStats.getIoServiceBytesRecursive(), IsEqual.equalTo(Arrays.asList(new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Read").withValue(823296L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Write").withValue(122880L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Sync").withValue(835584L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Async").withValue(110592L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Total").withValue(946176L))));
        MatcherAssert.assertThat(blkioStats.getIoServicedRecursive(), Matchers.<BlkioStatEntry>hasSize(5));
        MatcherAssert.assertThat(blkioStats.getIoServicedRecursive(), IsEqual.equalTo(Arrays.asList(new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Read").withValue(145L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Write").withValue(4L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Sync").withValue(148L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Async").withValue(1L), new BlkioStatEntry().withMajor(259L).withMinor(0L).withOp("Total").withValue(149L))));
        MatcherAssert.assertThat(blkioStats.getIoQueueRecursive(), CoreMatchers.is(Matchers.empty()));
        MatcherAssert.assertThat(blkioStats.getIoServiceTimeRecursive(), CoreMatchers.is(Matchers.empty()));
        MatcherAssert.assertThat(blkioStats.getIoWaitTimeRecursive(), CoreMatchers.is(Matchers.empty()));
        MatcherAssert.assertThat(blkioStats.getIoMergedRecursive(), CoreMatchers.is(Matchers.empty()));
        MatcherAssert.assertThat(blkioStats.getIoTimeRecursive(), CoreMatchers.is(Matchers.empty()));
        MatcherAssert.assertThat(blkioStats.getSectorsRecursive(), CoreMatchers.is(Matchers.empty()));
        final CpuStatsConfig cpuStats = statistics.getCpuStats();
        final CpuUsageConfig cpuUsage = cpuStats.getCpuUsage();
        MatcherAssert.assertThat(cpuUsage.getTotalUsage(), CoreMatchers.is(212198028L));
        MatcherAssert.assertThat(cpuUsage.getPercpuUsage(), IsEqual.equalTo(Arrays.asList(71592953L, 42494761L, 59298344L, 38811970L)));
        MatcherAssert.assertThat(cpuUsage.getUsageInKernelmode(), CoreMatchers.is(170000000L));
        MatcherAssert.assertThat(cpuUsage.getUsageInUsermode(), CoreMatchers.is(20000000L));
        MatcherAssert.assertThat(cpuStats.getSystemCpuUsage(), CoreMatchers.is(545941980000000L));
        MatcherAssert.assertThat(cpuStats.getOnlineCpus(), CoreMatchers.is(4L));
        final ThrottlingDataConfig throttlingData = cpuStats.getThrottlingData();
        MatcherAssert.assertThat(throttlingData.getPeriods(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(throttlingData.getThrottledPeriods(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(throttlingData.getThrottledTime(), CoreMatchers.is(0L));
        final CpuStatsConfig preCpuStats = statistics.getPreCpuStats();
        final CpuUsageConfig preCpuUsage = preCpuStats.getCpuUsage();
        MatcherAssert.assertThat(preCpuUsage.getTotalUsage(), CoreMatchers.is(211307214L));
        MatcherAssert.assertThat(preCpuUsage.getPercpuUsage(), IsEqual.equalTo(Arrays.asList(71451389L, 42097782L, 59298344L, 38459699L)));
        MatcherAssert.assertThat(preCpuUsage.getUsageInKernelmode(), CoreMatchers.is(170000000L));
        MatcherAssert.assertThat(preCpuUsage.getUsageInUsermode(), CoreMatchers.is(20000000L));
        MatcherAssert.assertThat(preCpuStats.getSystemCpuUsage(), CoreMatchers.is(545937990000000L));
        MatcherAssert.assertThat(preCpuStats.getOnlineCpus(), CoreMatchers.is(4L));
        final ThrottlingDataConfig preThrottlingData = preCpuStats.getThrottlingData();
        MatcherAssert.assertThat(preThrottlingData.getPeriods(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(preThrottlingData.getThrottledPeriods(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(preThrottlingData.getThrottledTime(), CoreMatchers.is(0L));
        final PidsStatsConfig pidsStats = statistics.getPidsStats();
        MatcherAssert.assertThat(pidsStats.getCurrent(), CoreMatchers.is(2L));
    }
}

