package org.tron.core.net.node;


import BlockCapsule.BlockId;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
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
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.net.message.BlockMessage;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;


@Slf4j
public class HandleSyncBlockTest {
    private static TronApplicationContext context;

    private static NodeImpl node;

    private RpcApiService rpcApiService;

    private static PeerClientTest peerClient;

    private ChannelManager channelManager;

    private SyncPool pool;

    private static Application appT;

    private Node nodeEntity;

    private static HandshakeHandlerTest handshakeHandlerTest;

    private static final String dbPath = "output-nodeImplTest-handleSyncBlock";

    private static final String dbDirectory = "db_HandleSyncBlock_test";

    private static final String indexDirectory = "index_HandleSyncBlock_test";

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
    public void testHandleSyncBlock() throws Exception {
        testConsumerAdvObjToSpread();
        // build block Message
        BlockMessage blockMessage = buildBlockMessage();
        // build blockJustReceived
        Map<BlockMessage, PeerConnection> blockJustReceived = new ConcurrentHashMap<>();
        blockJustReceived.put(blockMessage, new PeerConnection());
        ReflectUtils.setFieldValue(HandleSyncBlockTest.node, "blockJustReceived", blockJustReceived);
        Thread.sleep(1000);
        // retrieve the first active peer
        Collection<PeerConnection> activePeers = ReflectUtils.invokeMethod(HandleSyncBlockTest.node, "getActivePeer");
        activePeers.iterator().next().getSyncBlockToFetch().push(blockMessage.getBlockId());
        // clean up freshBlockId
        Queue<BlockCapsule.BlockId> freshBlockId = ReflectUtils.getFieldValue(HandleSyncBlockTest.node, "freshBlockId");
        freshBlockId.poll();
        // trigger handlesyncBlock method
        ReflectUtils.invokeMethod(HandleSyncBlockTest.node, "handleSyncBlock");
        Assert.assertTrue(freshBlockId.contains(blockMessage.getBlockId()));
    }

    private static boolean go = false;
}

