package water;


import H2O.SELF._heartbeat;
import UDP.udp.heartbeat;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;

import static H2O.SELF;


public class ClientJarMD5IgnoreTest extends TestUtil {
    @Test
    public void testDontCheckMD5ForClient() {
        Collection<H2OListenerExtension> listenerExtensions = ExtensionManager.getInstance().getListenerExtensions();
        NodeLocalEventCollectingListener ext = ((NodeLocalEventCollectingListener) (listenerExtensions.iterator().next()));
        ext.clear();
        HeartBeat hb = new HeartBeat();
        hb._cloud_name_hash = _heartbeat._cloud_name_hash;
        hb._client = true;
        byte[] fake_hash = new byte[1];
        fake_hash[0] = 0;
        hb._jar_md5 = fake_hash;
        // send client heartbeat with different md5
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
        ArrayList<Object[]> client_wrong_md5 = ext.getData("client_wrong_md5");
        Assert.assertEquals(1, client_wrong_md5.size());
        byte[] received_hash = ((byte[]) (client_wrong_md5.get(0)[0]));
        Assert.assertArrayEquals(fake_hash, received_hash);
    }
}

