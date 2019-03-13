package org.tron.core.net.node;


import BlockCapsule.BlockId;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
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
import org.tron.core.db.BlockStore;
import org.tron.core.db.Manager;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.net.peer.PeerConnection;
import org.tron.core.services.RpcApiService;


@Slf4j
public class GetBlockChainSummaryTest {
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

    private static final String dbPath = "output-GetBlockChainSummary";

    private static final String dbDirectory = "db_GetBlockChainSummary_test";

    private static final String indexDirectory = "index_GetBlockChainSummary_test";

    @Test
    public void testGetBlockChainSummary() {
        NodeDelegate del = ReflectUtils.getFieldValue(GetBlockChainSummaryTest.node, "del");
        Collection<PeerConnection> activePeers = ReflectUtils.invokeMethod(GetBlockChainSummaryTest.node, "getActivePeer");
        BlockStore blkstore = dbManager.getBlockStore();
        Object[] peers = activePeers.toArray();
        if ((peers == null) || ((peers.length) <= 0)) {
            return;
        }
        PeerConnection peer_he = ((PeerConnection) (peers[0]));
        Deque<BlockCapsule.BlockId> toFetch = new ConcurrentLinkedDeque<>();
        ArrayList<String> scenes = new ArrayList<>();
        scenes.add("genesis");
        scenes.add("genesis_fetch");
        scenes.add("nongenesis_fetch");
        long number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
        Map<ByteString, String> addressToProvateKeys = addTestWitnessAndAccount();
        BlockCapsule capsule = createTestBlockCapsule(1533529947843L, number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        try {
            dbManager.pushBlock(capsule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 1; i < 5; i++) {
            number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
            capsule = createTestBlockCapsule((1533529947843L + (3000L * i)), number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
            try {
                dbManager.pushBlock(capsule);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BlockCapsule.BlockId commonBlockId = null;
        // the common block is genesisblock?syncBlockToFetch is empty?
        try {
            commonBlockId = del.getGenesisBlock().getBlockId();
            peer_he.getSyncBlockToFetch().clear();
            ReflectUtils.setFieldValue(peer_he, "headBlockWeBothHave", commonBlockId);
            ReflectUtils.setFieldValue(peer_he, "headBlockTimeWeBothHave", System.currentTimeMillis());
            Deque<BlockCapsule.BlockId> retSummary = del.getBlockChainSummary(peer_he.getHeadBlockWeBothHave(), peer_he.getSyncBlockToFetch());
            Assert.assertTrue(((retSummary.size()) == 3));
        } catch (Exception e) {
            System.out.println("exception!");
        }
        // the common block is genesisblock?syncBlockToFetch is not empty?
        peer_he.getSyncBlockToFetch().addAll(toFetch);
        try {
            toFetch.clear();
            peer_he.getSyncBlockToFetch().clear();
            for (int i = 0; i < 4; i++) {
                number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
                capsule = createTestBlockCapsule((1533529947843L + (3000L * i)), number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
                toFetch.add(capsule.getBlockId());
            }
            commonBlockId = del.getGenesisBlock().getBlockId();
            peer_he.getSyncBlockToFetch().addAll(toFetch);
            ReflectUtils.setFieldValue(peer_he, "headBlockWeBothHave", commonBlockId);
            ReflectUtils.setFieldValue(peer_he, "headBlockTimeWeBothHave", System.currentTimeMillis());
            Deque<BlockCapsule.BlockId> retSummary = del.getBlockChainSummary(peer_he.getHeadBlockWeBothHave(), peer_he.getSyncBlockToFetch());
            Assert.assertTrue(((retSummary.size()) == 4));
        } catch (Exception e) {
            System.out.println("exception!");
        }
        // the common block is a normal block(not genesisblock)?syncBlockToFetc is not empty.
        try {
            toFetch.clear();
            peer_he.getSyncBlockToFetch().clear();
            number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
            BlockCapsule capsule1 = createTestBlockCapsule((1533529947843L + (3000L * 6)), number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
            dbManager.pushBlock(capsule1);
            number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
            BlockCapsule capsule2 = createTestBlockCapsule((1533529947843L + (3000L * 7)), number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
            dbManager.pushBlock(capsule2);
            for (int i = 0; i < 2; i++) {
                number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
                capsule = createTestBlockCapsule(((1533529947843L + (3000L * 8)) + (3000L * i)), number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
                toFetch.add(capsule.getBlockId());
            }
            commonBlockId = capsule2.getBlockId();
            peer_he.getSyncBlockToFetch().addAll(toFetch);
            toFetch.forEach(( block) -> blkstore.delete(block.getBytes()));
            ReflectUtils.setFieldValue(peer_he, "headBlockWeBothHave", commonBlockId);
            ReflectUtils.setFieldValue(peer_he, "headBlockTimeWeBothHave", System.currentTimeMillis());
            Deque<BlockCapsule.BlockId> retSummary = del.getBlockChainSummary(peer_he.getHeadBlockWeBothHave(), peer_he.getSyncBlockToFetch());
            Assert.assertTrue(((retSummary.size()) == 4));
        } catch (Exception e) {
            System.out.println("exception!");
        }
        logger.info("finish1");
    }

    private static boolean go = false;
}

