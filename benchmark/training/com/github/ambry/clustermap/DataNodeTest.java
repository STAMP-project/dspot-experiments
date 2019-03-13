package com.github.ambry.clustermap;


import HardwareState.AVAILABLE;
import HardwareState.UNAVAILABLE;
import PortType.PLAINTEXT;
import PortType.SSL;
import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.VerifiableProperties;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link DataNode} class.
 */
public class DataNodeTest {
    private static final int diskCount = 10;

    private static final long diskCapacityInBytes = ((1000 * 1024) * 1024) * 1024L;

    private Properties props;

    public DataNodeTest() {
        props = new Properties();
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
    }

    @Test
    public void basics() throws JSONException {
        JSONObject jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, 7666, AVAILABLE, getDisks());
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        DataNode dataNode = new TestDataNode("datacenter", jsonObject, clusterMapConfig);
        Assert.assertEquals(dataNode.getHostname(), TestUtils.getLocalHost());
        Assert.assertEquals(dataNode.getPort(), 6666);
        Assert.assertEquals(dataNode.getState(), AVAILABLE);
        Assert.assertEquals(dataNode.getDisks().size(), DataNodeTest.diskCount);
        Assert.assertEquals(dataNode.getRawCapacityInBytes(), ((DataNodeTest.diskCount) * (DataNodeTest.diskCapacityInBytes)));
        Assert.assertNull(dataNode.getRackId());
        Assert.assertEquals(TestUtils.DEFAULT_XID, dataNode.getXid());
        Assert.assertEquals(dataNode.toJSONObject().toString(), jsonObject.toString());
        Assert.assertEquals(dataNode, new TestDataNode("datacenter", dataNode.toJSONObject(), clusterMapConfig));
        // Test with defined rackId
        jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, 7666, 42, TestUtils.DEFAULT_XID, AVAILABLE, getDisks());
        dataNode = new TestDataNode("datacenter", jsonObject, clusterMapConfig);
        Assert.assertEquals("42", dataNode.getRackId());
        Assert.assertEquals(TestUtils.DEFAULT_XID, dataNode.getXid());
        Assert.assertEquals(dataNode.toJSONObject().toString(), jsonObject.toString());
        Assert.assertEquals(dataNode, new TestDataNode("datacenter", dataNode.toJSONObject(), clusterMapConfig));
    }

    @Test
    public void validation() throws JSONException {
        JSONObject jsonObject;
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        try {
            // Null DataNode
            jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, 7666, AVAILABLE, getDisks());
            new DataNode(null, jsonObject, clusterMapConfig);
            Assert.fail("Should have failed validation.");
        } catch (IllegalStateException e) {
            // Expected.
        }
        // Bad hostname
        jsonObject = TestUtils.getJsonDataNode("", 6666, 7666, AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
        // Bad hostname (http://tools.ietf.org/html/rfc6761 defines 'invalid' top level domain)
        jsonObject = TestUtils.getJsonDataNode("hostname.invalid", 6666, 7666, AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
        // Bad port (too small)
        jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), (-1), 7666, AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
        // Bad ssl port (too small)
        jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, (-1), AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
        // Bad port (too big)
        jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), (100 * 1000), 7666, AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
        // Bad ssl port (too big)
        jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, (100 * 1000), AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
        // same port number for plain text and ssl port
        jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, 6666, AVAILABLE, getDisks());
        failValidation(jsonObject, clusterMapConfig);
    }

    @Test
    public void testSoftState() throws InterruptedException, JSONException {
        JSONObject jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, 7666, AVAILABLE, getDisks());
        Properties props = new Properties();
        props.setProperty("clustermap.fixedtimeout.datanode.retry.backoff.ms", Integer.toString(2000));
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        int threshold = clusterMapConfig.clusterMapFixedTimeoutDatanodeErrorThreshold;
        long retryBackoffMs = clusterMapConfig.clusterMapFixedTimeoutDataNodeRetryBackoffMs;
        DataNode dataNode = new TestDataNode("datacenter", jsonObject, clusterMapConfig);
        for (int i = 0; i < threshold; i++) {
            ensure(dataNode, AVAILABLE);
            dataNode.onNodeTimeout();
        }
        // After threshold number of continuous errors, the resource should be unavailable
        ensure(dataNode, UNAVAILABLE);
        Thread.sleep((retryBackoffMs + 1));
        // If retryBackoffMs has passed, the resource should be available.
        ensure(dataNode, AVAILABLE);
        // A single timeout should make the node unavailable now
        dataNode.onNodeTimeout();
        ensure(dataNode, UNAVAILABLE);
        // A single response should make the node available now
        dataNode.onNodeResponse();
        ensure(dataNode, AVAILABLE);
    }

    /**
     * Validate {@link DataNodeId#getPortToConnectTo()} returns port type corresponding to the
     * SSL enabled datacenter list specified in {@link ClusterMapConfig}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void validateGetPort() throws Exception {
        ClusterMapConfig clusterMapConfig;
        Properties props = new Properties();
        props.setProperty("clustermap.ssl.enabled.datacenters", "datacenter1,datacenter2,datacenter3");
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
        clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        System.out.println(clusterMapConfig.clusterMapSslEnabledDatacenters);
        JSONObject jsonObject = TestUtils.getJsonDataNode(TestUtils.getLocalHost(), 6666, 7666, AVAILABLE, getDisks());
        DataNode dataNode = new TestDataNode("datacenter2", jsonObject, clusterMapConfig);
        Assert.assertEquals("The datacenter of the data node is in the ssl enabled datacenter list. SSL port should be returned", SSL, dataNode.getPortToConnectTo().getPortType());
        dataNode = new TestDataNode("datacenter5", jsonObject, clusterMapConfig);
        Assert.assertEquals("The datacenter of the data node is not in the ssl enabled datacenter list. Plaintext port should be returned", PLAINTEXT, dataNode.getPortToConnectTo().getPortType());
        jsonObject.remove("sslport");
        dataNode = new TestDataNode("datacenter1", jsonObject, clusterMapConfig);
        try {
            dataNode.getPortToConnectTo();
            Assert.fail("Should have thrown Exception because there is no sslPort.");
        } catch (IllegalStateException e) {
            // The datacenter of the data node is in the ssl enabled datacenter list, but the data node does not have an ssl
            // port to connect. Exception should be thrown.
        }
    }
}

