package water;


import H2O.SELF._heartbeat;
import UDP.udp.heartbeat;
import UDP.udp.rebooted;
import UDPRebooted.MAGIC_SAFE_CLUSTER_KILL_BYTE;
import UDPRebooted.T.error;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;

import static H2O.SELF;


public class UnknownHeartbeatTest extends TestUtil {
    @Test
    public void testIgnoreUnknownHeartBeat() {
        final int clientsCountBefore = H2O.getClients().length;
        HeartBeat hb = new HeartBeat();
        hb._cloud_name_hash = 777;
        hb._client = true;
        hb._jar_md5 = _heartbeat._jar_md5;
        // Multicast the Heart Beat
        AutoBuffer ab = new AutoBuffer(SELF, heartbeat._prior);
        ab.putUdp(heartbeat, 65400);// put different port number to simulate heartbeat from fake node

        hb.write(ab);
        ab.close();
        // Give it time so the packet can arrive
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Verify that we don't have a new client
        Assert.assertEquals(clientsCountBefore, H2O.getClients().length);
    }

    @Test
    public void testIgnoreUnknownShutdownTask() {
        Collection<H2OListenerExtension> listenerExtensions = ExtensionManager.getInstance().getListenerExtensions();
        NodeLocalEventCollectingListener ext = ((NodeLocalEventCollectingListener) (listenerExtensions.iterator().next()));
        ext.clear();
        // 777 is the hashcode of the origin cloud
        new AutoBuffer(SELF, rebooted._prior).putUdp(rebooted, 65400).put1(MAGIC_SAFE_CLUSTER_KILL_BYTE).put1(error.ordinal()).putInt(777).close();
        // Give it time so the packet can arrive
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Object[]> shutdown_success = ext.getData("shutdown_fail");
        Assert.assertEquals(2, shutdown_success.size());
        Assert.assertEquals(1, shutdown_success.get(0).length);
        Assert.assertEquals(777, ((int) (shutdown_success.get(0)[0])));
    }

    @Test
    public void testIgnoreUnknownShutdownTaskOldVersion() {
        Collection<H2OListenerExtension> listenerExtensions = ExtensionManager.getInstance().getListenerExtensions();
        NodeLocalEventCollectingListener ext = ((NodeLocalEventCollectingListener) (listenerExtensions.iterator().next()));
        ext.clear();
        // Test that when request comes from the old H2O version ( older than 3.14.0.4 where the fix is implemented )
        AutoBuffer ab = new AutoBuffer(SELF, rebooted._prior);
        ab.putUdp(rebooted, 65400).put1(error.ordinal()).putInt(777);// 777 is the hashcode of the origin cloud

        ab.close();
        // Give it time so the packet can arrive
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Object[]> shutdown_success = ext.getData("shutdown_ignored");
        Assert.assertEquals(2, shutdown_success.size());
    }
}

