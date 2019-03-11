package org.tron.core.net.node;


import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.overlay.discover.node.Node;
import org.tron.common.overlay.server.ChannelManager;
import org.tron.common.overlay.server.SyncPool;
import org.tron.common.utils.ReflectUtils;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.db.Manager;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;


@Slf4j
public class FinishProcessSyncBlockTest {
    private static TronApplicationContext context;

    private static NodeImpl node;

    private RpcApiService rpcApiService;

    private static PeerClientTest peerClient;

    private ChannelManager channelManager;

    private SyncPool pool;

    private static Application appT;

    private Manager dbManager;

    private Node nodeEntity;

    private static HandshakeHandlerTest handshakeHandlerTest;

    private static final String dbPath = "output-FinishProcessSyncBlockTest";

    private static final String dbDirectory = "db_FinishProcessSyncBlock_test";

    private static final String indexDirectory = "index_FinishProcessSyncBlock_test";

    @Test
    public void testFinishProcessSyncBlock() throws Exception {
        Collection<PeerConnection> activePeers = ReflectUtils.invokeMethod(FinishProcessSyncBlockTest.node, "getActivePeer");
        Thread.sleep(5000);
        if ((activePeers.size()) < 1) {
            return;
        }
        PeerConnection peer = ((PeerConnection) (activePeers.toArray()[1]));
        BlockCapsule headBlockCapsule = dbManager.getHead();
        BlockCapsule blockCapsule = generateOneBlockCapsule(headBlockCapsule);
        Class[] cls = new Class[]{ BlockCapsule.class };
        Object[] params = new Object[]{ blockCapsule };
        peer.getBlockInProc().add(blockCapsule.getBlockId());
        ReflectUtils.invokeMethod(FinishProcessSyncBlockTest.node, "finishProcessSyncBlock", cls, params);
        Assert.assertEquals(peer.getBlockInProc().isEmpty(), true);
    }

    private static boolean go = false;
}

