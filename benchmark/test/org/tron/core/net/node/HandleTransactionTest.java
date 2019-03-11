package org.tron.core.net.node;


import Protocol.Transaction;
import com.google.protobuf.ByteString;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.testng.collections.Lists;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.overlay.discover.node.Node;
import org.tron.common.overlay.server.ChannelManager;
import org.tron.common.overlay.server.SyncPool;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.db.Manager;
import org.tron.core.net.message.TransactionMessage;
import org.tron.core.net.message.TransactionsMessage;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Inventory.InventoryType;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;


@Slf4j
public class HandleTransactionTest {
    private static TronApplicationContext context;

    private static NodeImpl node;

    private RpcApiService rpcApiService;

    private static PeerClientTest peerClient;

    private ChannelManager channelManager;

    private SyncPool pool;

    private static Application appT;

    private static Manager dbManager;

    private Node nodeEntity;

    private static HandshakeHandlerTest handshakeHandlerTest;

    private static final String dbPath = "output-HandleTransactionTest";

    private static final String dbDirectory = "db_HandleTransaction_test";

    private static final String indexDirectory = "index_HandleTransaction_test";

    @Test
    public void testHandleTransactionMessage() throws Exception {
        TransferContract tc = TransferContract.newBuilder().setAmount(10).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        TransactionCapsule trx = new TransactionCapsule(tc, ContractType.TransferContract);
        Protocol.Transaction transaction = trx.getInstance();
        TransactionMessage transactionMessage = new TransactionMessage(transaction);
        List list = Lists.newArrayList();
        list.add(transaction);
        TransactionsMessage transactionsMessage = new TransactionsMessage(list);
        PeerConnection peer = new PeerConnection();
        // ???peer?????????
        peer.getAdvObjWeRequested().clear();
        peer.setSyncFlag(true);
        // nodeImpl.onMessage(peer, transactionMessage);
        // Assert.assertEquals(peer.getSyncFlag(), false);
        // ?peer?????????
        peer.getAdvObjWeRequested().put(new Item(transactionMessage.getMessageId(), InventoryType.TRX), System.currentTimeMillis());
        peer.setSyncFlag(true);
        HandleTransactionTest.node.onMessage(peer, transactionsMessage);
        // Assert.assertEquals(peer.getAdvObjWeRequested().isEmpty(), true);
        // ConcurrentHashMap<Sha256Hash, InventoryType> advObjToSpread = ReflectUtils.getFieldValue(nodeImpl, "advObjToSpread");
        // Assert.assertEquals(advObjToSpread.contains(transactionMessage.getMessageId()), true);
    }

    private static boolean go = false;
}

