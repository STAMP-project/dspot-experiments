package com.github.ambry.clustermap;


import HardwareState.AVAILABLE;
import HardwareState.UNAVAILABLE;
import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.VerifiableProperties;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Disk} class.
 */
public class DiskTest {
    private Properties props;

    public DiskTest() {
        props = new Properties();
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
    }

    @Test
    public void basics() throws JSONException {
        JSONObject jsonObject = TestUtils.getJsonDisk("/mnt1", AVAILABLE, (((100 * 1024) * 1024) * 1024L));
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        Disk testDisk = new TestDisk(jsonObject, clusterMapConfig);
        Assert.assertEquals(testDisk.getMountPath(), "/mnt1");
        Assert.assertEquals(testDisk.getHardState(), AVAILABLE);
        Assert.assertEquals(testDisk.getRawCapacityInBytes(), (((100 * 1024) * 1024) * 1024L));
        Assert.assertEquals(testDisk.toJSONObject().toString(), jsonObject.toString());
        Assert.assertEquals(testDisk, new TestDisk(testDisk.toJSONObject(), clusterMapConfig));
    }

    @Test
    public void validation() throws JSONException {
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        try {
            // Null DataNode
            new Disk(null, TestUtils.getJsonDisk("/mnt1", AVAILABLE, (((100 * 1024) * 1024) * 1024L)), clusterMapConfig);
            Assert.fail("Construction of Disk should have failed validation.");
        } catch (IllegalStateException e) {
            // Expected.
        }
        // Bad mount path (empty)
        failValidation(TestUtils.getJsonDisk("", AVAILABLE, (((100 * 1024) * 1024) * 1024L)), clusterMapConfig);
        // Bad mount path (relative path)
        failValidation(TestUtils.getJsonDisk("mnt1", AVAILABLE, (((100 * 1024) * 1024) * 1024L)), clusterMapConfig);
        // Bad capacity (too small)
        failValidation(TestUtils.getJsonDisk("/mnt1", UNAVAILABLE, 0), clusterMapConfig);
        // Bad capacity (too big)
        failValidation(TestUtils.getJsonDisk("/mnt1", UNAVAILABLE, (((((((1024 * 1024) * 1024) * 1024) * 1024) * 1024) * 1024) * 1024L)), clusterMapConfig);
    }

    @Test
    public void testDiskSoftState() throws InterruptedException, JSONException {
        JSONObject jsonObject = TestUtils.getJsonDisk("/mnt1", AVAILABLE, (((100 * 1024) * 1024) * 1024L));
        Properties props = new Properties();
        props.setProperty("clustermap.fixedtimeout.disk.retry.backoff.ms", Integer.toString(2000));
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        int threshold = clusterMapConfig.clusterMapFixedTimeoutDiskErrorThreshold;
        long retryBackoffMs = clusterMapConfig.clusterMapFixedTimeoutDiskRetryBackoffMs;
        Disk testDisk = new TestDisk(jsonObject, clusterMapConfig);
        for (int i = 0; i < threshold; i++) {
            Assert.assertEquals(testDisk.getState(), AVAILABLE);
            testDisk.onDiskError();
        }
        Assert.assertEquals(testDisk.getState(), UNAVAILABLE);
        Thread.sleep((retryBackoffMs + 1));
        Assert.assertEquals(testDisk.getState(), AVAILABLE);
        // A single error should make it unavailable
        testDisk.onDiskError();
        Assert.assertEquals(testDisk.getState(), UNAVAILABLE);
        testDisk.onDiskOk();
        Assert.assertEquals(testDisk.getState(), AVAILABLE);
    }
}

