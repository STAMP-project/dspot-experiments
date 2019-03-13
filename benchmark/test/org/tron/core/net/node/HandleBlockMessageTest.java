package org.tron.core.net.node;


import java.util.List;
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
import org.tron.core.net.message.BlockMessage;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;
import org.tron.protos.Protocol.Inventory.InventoryType;


@Slf4j
public class HandleBlockMessageTest {
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

    private static final String dbPath = "output-HandleBlockMessageTest";

    private static final String dbDirectory = "db_HandleBlockMessage_test";

    private static final String indexDirectory = "index_HandleBlockMessage_test";

    @Test
    public void testHandleBlockMessage() throws Exception {
        List<PeerConnection> activePeers = ReflectUtils.getFieldValue(pool, "activePeers");
        Thread.sleep(5000);
        if ((activePeers.size()) < 1) {
            return;
        }
        PeerConnection peer = activePeers.get(0);
        // receive a sync block
        BlockCapsule headBlockCapsule = dbManager.getHead();
        BlockCapsule syncblockCapsule = generateOneBlockCapsule(headBlockCapsule);
        BlockMessage blockMessage = new BlockMessage(syncblockCapsule);
        peer.getSyncBlockRequested().put(blockMessage.getBlockId(), System.currentTimeMillis());
        HandleBlockMessageTest.node.onMessage(peer, blockMessage);
        Assert.assertEquals(peer.getSyncBlockRequested().isEmpty(), true);
        // receive a advertise block
        BlockCapsule advblockCapsule = generateOneBlockCapsule(headBlockCapsule);
        BlockMessage advblockMessage = new BlockMessage(advblockCapsule);
        peer.getAdvObjWeRequested().put(new Item(advblockMessage.getBlockId(), InventoryType.BLOCK), System.currentTimeMillis());
        HandleBlockMessageTest.node.onMessage(peer, advblockMessage);
        Assert.assertEquals(peer.getAdvObjWeRequested().size(), 0);
        // receive a sync block but not requested
        BlockCapsule blockCapsule = generateOneBlockCapsule(headBlockCapsule);
        blockMessage = new BlockMessage(blockCapsule);
        BlockCapsule blockCapsuleOther = generateOneBlockCapsule(blockCapsule);
        BlockMessage blockMessageOther = new BlockMessage(blockCapsuleOther);
        peer.getSyncBlockRequested().put(blockMessage.getBlockId(), System.currentTimeMillis());
        HandleBlockMessageTest.node.onMessage(peer, blockMessageOther);
        Assert.assertEquals(peer.getSyncBlockRequested().isEmpty(), false);
    }

    private static boolean go = false;
}

