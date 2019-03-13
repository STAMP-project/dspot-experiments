package org.tron.core.net.node;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.overlay.discover.node.Node;
import org.tron.common.overlay.message.Message;
import org.tron.common.overlay.server.ChannelManager;
import org.tron.common.overlay.server.MessageQueue;
import org.tron.common.overlay.server.SyncPool;
import org.tron.common.utils.ReflectUtils;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.net.message.MessageTypes;
import org.tron.core.net.node.NodeImpl.PriorItem;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;
import org.tron.protos.Protocol.Inventory.InventoryType;


@Slf4j
public class BroadTest {
    private TronApplicationContext context;

    private NodeImpl nodeImpl;

    private RpcApiService rpcApiService;

    private PeerClientTest peerClient;

    private ChannelManager channelManager;

    private SyncPool pool;

    private Application appT;

    private static final String dbPath = "output-nodeImplTest-broad";

    private static final String dbDirectory = "db_Broad_test";

    private static final String indexDirectory = "index_Broad_test";

    private HandshakeHandlerTest handshakeHandlerTest;

    private Node node;

    private class Condition {
        private Sha256Hash blockId;

        private Sha256Hash transactionId;

        public Condition(Sha256Hash blockId, Sha256Hash transactionId) {
            this.blockId = blockId;
            this.transactionId = transactionId;
        }

        public Sha256Hash getBlockId() {
            return blockId;
        }

        public Sha256Hash getTransactionId() {
            return transactionId;
        }
    }

    @Test
    public void testConsumerAdvObjToFetch() throws InterruptedException {
        BroadTest.Condition condition = testConsumerAdvObjToSpread();
        Thread.sleep(1000);
        // 
        Map<Sha256Hash, PriorItem> advObjToFetch = ReflectUtils.getFieldValue(nodeImpl, "advObjToFetch");
        logger.info("advObjToFetch:{}", advObjToFetch);
        logger.info("advObjToFetchSize:{}", advObjToFetch.size());
        // Assert.assertEquals(advObjToFetch.get(condition.getBlockId()), InventoryType.BLOCK);
        // Assert.assertEquals(advObjToFetch.get(condition.getTransactionId()), InventoryType.TRX);
        // To avoid writing the database, manually stop the sending of messages.
        Collection<PeerConnection> activePeers = ReflectUtils.invokeMethod(nodeImpl, "getActivePeer");
        Thread.sleep(1000);
        if ((activePeers.size()) < 1) {
            return;
        }
        for (PeerConnection peerConnection : activePeers) {
            MessageQueue messageQueue = ReflectUtils.getFieldValue(peerConnection, "msgQueue");
            ReflectUtils.setFieldValue(messageQueue, "sendMsgFlag", false);
        }
        // 
        ReflectUtils.invokeMethod(nodeImpl, "consumerAdvObjToFetch");
        Thread.sleep(1000);
        boolean result = true;
        int count = 0;
        for (PeerConnection peerConnection : activePeers) {
            if (peerConnection.getAdvObjWeRequested().containsKey(new Item(condition.getTransactionId(), InventoryType.TRX))) {
                ++count;
            }
            if (peerConnection.getAdvObjWeRequested().containsKey(new Item(condition.getBlockId(), InventoryType.BLOCK))) {
                ++count;
            }
            MessageQueue messageQueue = ReflectUtils.getFieldValue(peerConnection, "msgQueue");
            BlockingQueue<Message> msgQueue = ReflectUtils.getFieldValue(messageQueue, "msgQueue");
            for (Message message : msgQueue) {
                if ((message.getType()) == (MessageTypes.BLOCK)) {
                    Assert.assertEquals(message.getMessageId(), condition.getBlockId());
                }
                if ((message.getType()) == (MessageTypes.TRX)) {
                    Assert.assertEquals(message.getMessageId(), condition.getTransactionId());
                }
            }
        }
        Assert.assertTrue((count >= 1));
    }

    private static boolean go = false;
}

