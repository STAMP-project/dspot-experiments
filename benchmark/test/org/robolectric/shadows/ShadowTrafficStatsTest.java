package org.robolectric.shadows;


import TrafficStats.UNSUPPORTED;
import android.net.TrafficStats;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowTrafficStatsTest {
    @Test
    public void allQueriesAreStubbed() throws Exception {
        int anything = -2;
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getThreadStatsTag());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getMobileTxPackets());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getMobileRxPackets());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getMobileTxBytes());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getMobileRxBytes());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getTotalTxPackets());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getTotalRxPackets());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getTotalTxBytes());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getTotalRxBytes());
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidTxBytes(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidRxBytes(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidTxPackets(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidRxPackets(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidTcpTxBytes(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidTcpRxBytes(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidUdpTxBytes(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidUdpRxBytes(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidTcpTxSegments(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidTcpRxSegments(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidUdpTxPackets(anything));
        Assert.assertEquals(UNSUPPORTED, TrafficStats.getUidUdpRxPackets(anything));
    }
}

