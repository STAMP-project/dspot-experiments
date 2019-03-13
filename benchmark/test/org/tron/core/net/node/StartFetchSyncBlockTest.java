package org.tron.core.net.node;


import Protocol.Block;
import com.google.common.cache.Cache;
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
import org.tron.common.utils.Sha256Hash;
import org.tron.core.net.message.BlockMessage;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;
import org.tron.protos.Protocol;


@Slf4j
public class StartFetchSyncBlockTest {
    private static TronApplicationContext context;

    private NodeImpl node;

    private RpcApiService rpcApiService;

    private static PeerClientTest peerClient;

    private ChannelManager channelManager;

    private SyncPool pool;

    private static Application appT;

    private Node nodeEntity;

    private static HandshakeHandlerTest handshakeHandlerTest;

    private static final String dbPath = "output-nodeImplTest-startFetchSyncBlockTest";

    private static final String dbDirectory = "db_StartFetchSyncBlock_test";

    private static final String indexDirectory = "index_StartFetchSyncBlock_test";

    private class Condition {
        private Sha256Hash blockId;

        public Condition(Sha256Hash blockId) {
            this.blockId = blockId;
        }

        public Sha256Hash getBlockId() {
            return blockId;
        }
    }

    @Test
    public void testStartFetchSyncBlock() throws InterruptedException {
        testConsumerAdvObjToSpread();
        Collection<PeerConnection> activePeers = ReflectUtils.invokeMethod(node, "getActivePeer");
        Thread.sleep(5000);
        if ((activePeers.size()) < 1) {
            return;
        }
        ReflectUtils.setFieldValue(activePeers.iterator().next(), "needSyncFromPeer", true);
        // construct a block
        Protocol.Block block = Block.getDefaultInstance();
        BlockMessage blockMessage = new BlockMessage(new org.tron.core.capsule.BlockCapsule(block));
        // push the block to syncBlockToFetch
        activePeers.iterator().next().getSyncBlockToFetch().push(blockMessage.getBlockId());
        // invoke testing method
        addTheBlock(blockMessage);
        ReflectUtils.invokeMethod(node, "startFetchSyncBlock");
        Cache syncBlockIdWeRequested = ReflectUtils.getFieldValue(node, "syncBlockIdWeRequested");
        Assert.assertTrue(((syncBlockIdWeRequested.size()) == 1));
    }

    private static boolean go = false;
}

