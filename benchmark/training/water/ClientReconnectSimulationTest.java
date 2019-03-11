package water;


import H2O.SELF._heartbeat;
import java.net.InetAddress;
import org.junit.Assert;
import org.junit.Test;


public class ClientReconnectSimulationTest extends TestUtil {
    private InetAddress FAKE_NODE_ADDRESS;

    @Test
    public void testClientReconnect() {
        HeartBeat hb = new HeartBeat();
        hb._cloud_name_hash = _heartbeat._cloud_name_hash;
        hb._client = true;
        hb._jar_md5 = _heartbeat._jar_md5;
        H2ONode node = H2ONode.intern(FAKE_NODE_ADDRESS, 65456, ((short) (-100)));
        node._heartbeat = hb;
        Assert.assertSame(node, H2ONode.intern(FAKE_NODE_ADDRESS, 65456, ((short) (-101))));
        // Verify number of clients
        Assert.assertEquals(1, ClientReconnectSimulationTest.getClients().length);
    }

    @Test
    public void testClientTimeout() {
        // connect the client, wait for the timeout period and check that afterwards, the client is disconnected
        HeartBeat hb = new HeartBeat();
        hb._cloud_name_hash = _heartbeat._cloud_name_hash;
        hb._client = true;
        hb._jar_md5 = _heartbeat._jar_md5;
        H2ONode.intern(FAKE_NODE_ADDRESS, 65456, ((short) (-100)))._heartbeat = hb;
        // Verify number of clients
        Assert.assertEquals(1, ClientReconnectSimulationTest.getClients().length);
        waitForClientsDisconnect();
        Assert.assertEquals(0, ClientReconnectSimulationTest.getClients().length);
    }

    @Test
    public void testTwoClientsReconnect() {
        HeartBeat hb = new HeartBeat();
        hb._cloud_name_hash = _heartbeat._cloud_name_hash;
        hb._client = true;
        hb._jar_md5 = _heartbeat._jar_md5;
        H2ONode n1 = H2ONode.intern(FAKE_NODE_ADDRESS, 65456, ((short) (-100)));
        n1._heartbeat = hb;
        H2ONode n2 = H2ONode.intern(FAKE_NODE_ADDRESS, 65456, ((short) (-101)));
        Assert.assertSame(n1, n2);
        // make sure H2O sees only one client node
        Assert.assertEquals(1, ClientReconnectSimulationTest.getClients().length);
        // second client from another node re-connects
        H2ONode n3 = H2ONode.intern(FAKE_NODE_ADDRESS, 65458, ((short) (-100)));
        n3._heartbeat = hb;
        H2ONode n4 = H2ONode.intern(FAKE_NODE_ADDRESS, 65458, ((short) (-101)));
        Assert.assertSame(n3, n4);
        // we should see 2 clients nodes now
        Assert.assertEquals(2, ClientReconnectSimulationTest.getClients().length);
    }
}

