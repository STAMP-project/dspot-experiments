package com.github.ambry.clustermap;


import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.VerifiableProperties;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Datacenter} class.
 */
public class DatacenterTest {
    private static final int diskCount = 10;

    private static final long diskCapacityInBytes = ((1000 * 1024) * 1024) * 1024L;

    private static final int dataNodeCount = 6;

    private Properties props;

    public DatacenterTest() {
        props = new Properties();
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
    }

    @Test
    public void basics() throws JSONException {
        JSONObject jsonObject = TestUtils.getJsonDatacenter("XYZ1", ((byte) (1)), getDataNodes());
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        Datacenter datacenter = new TestDatacenter(jsonObject, clusterMapConfig);
        Assert.assertEquals(datacenter.getName(), "XYZ1");
        Assert.assertEquals(datacenter.getId(), 1);
        Assert.assertEquals(datacenter.getDataNodes().size(), DatacenterTest.dataNodeCount);
        Assert.assertEquals(datacenter.getRawCapacityInBytes(), (((DatacenterTest.dataNodeCount) * (DatacenterTest.diskCount)) * (DatacenterTest.diskCapacityInBytes)));
        Assert.assertFalse(datacenter.isRackAware());
        Assert.assertEquals(datacenter.toJSONObject().toString(), jsonObject.toString());
        Assert.assertEquals(datacenter, new TestDatacenter(datacenter.toJSONObject(), clusterMapConfig));
        jsonObject = TestUtils.getJsonDatacenter("XYZ1", ((byte) (1)), getDataNodesRackAware());
        datacenter = new TestDatacenter(jsonObject, clusterMapConfig);
        Assert.assertTrue(datacenter.isRackAware());
        Assert.assertEquals(datacenter.toJSONObject().toString(), jsonObject.toString());
        Assert.assertEquals(datacenter, new TestDatacenter(datacenter.toJSONObject(), clusterMapConfig));
    }

    @Test
    public void validation() throws JSONException {
        JSONObject jsonObject;
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        try {
            // Null HardwareLayout
            jsonObject = TestUtils.getJsonDatacenter("XYZ1", ((byte) (1)), getDataNodes());
            new Datacenter(null, jsonObject, clusterMapConfig);
            Assert.fail("Should have failed validation.");
        } catch (IllegalStateException e) {
            // Expected.
        }
        // Bad datacenter name
        jsonObject = TestUtils.getJsonDatacenter("", ((byte) (1)), getDataNodes());
        failValidation(jsonObject, clusterMapConfig);
        // Missing rack IDs
        jsonObject = TestUtils.getJsonDatacenter("XYZ1", ((byte) (1)), getDataNodesPartiallyRackAware());
        failValidation(jsonObject, clusterMapConfig);
    }
}

