package org.tron.core.net.node;


import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.overlay.discover.node.Node;
import org.tron.common.overlay.server.ChannelManager;
import org.tron.common.overlay.server.SyncPool;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.ReflectUtils;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.BlockCapsule.BlockId;
import org.tron.core.db.Manager;
import org.tron.core.exception.StoreException;
import org.tron.core.net.node.override.HandshakeHandlerTest;
import org.tron.core.net.node.override.PeerClientTest;
import org.tron.core.services.RpcApiService;


@Slf4j
public class GetLostBlockIdsTest {
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

    private static final String dbPath = "output-GetLostBlockIdsTest";

    private static final String dbDirectory = "db_GetLostBlockIds_test";

    private static final String indexDirectory = "index_GetLostBlockIds_test";

    @Test
    public void testGetLostBlockIds() {
        NodeDelegate del = ReflectUtils.getFieldValue(GetLostBlockIdsTest.node, "del");
        List<BlockId> blockChainSummary;
        LinkedList<BlockId> blockIds = null;
        long number;
        Map<ByteString, String> addressToProvateKeys = addTestWitnessAndAccount();
        BlockCapsule capsule;
        for (int i = 0; i < 5; i++) {
            number = (dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1;
            capsule = createTestBlockCapsule((1533529947843L + (3000L * i)), number, dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
            try {
                dbManager.pushBlock(capsule);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // blockChainSummary is empty
        try {
            blockChainSummary = new ArrayList<BlockId>();
            blockIds = del.getLostBlockIds(blockChainSummary);
        } catch (StoreException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(((blockIds.size()) == 6));
        // blockChainSummary only have a genesis block
        try {
            blockChainSummary = new ArrayList<BlockId>();
            blockChainSummary.add(dbManager.getGenesisBlockId());
            blockIds = del.getLostBlockIds(blockChainSummary);
        } catch (StoreException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(((blockIds.size()) == 6));
        // blockChainSummary have genesis block?2nd block?3rd block
        BlockId except_first_block = null;
        try {
            blockChainSummary = new ArrayList<BlockId>();
            blockChainSummary.add(dbManager.getGenesisBlockId());
            blockChainSummary.add(dbManager.getBlockIdByNum(2));
            blockChainSummary.add(dbManager.getBlockIdByNum(3));
            except_first_block = dbManager.getBlockIdByNum(3);
            blockIds = del.getLostBlockIds(blockChainSummary);
        } catch (StoreException e) {
            e.printStackTrace();
        }
        Assert.assertTrue((((blockIds.size()) == 3) && (Arrays.equals(blockIds.peekFirst().getBytes(), except_first_block.getBytes()))));
        // blockChainSummary have 2nd block?4th block?and they are on fork chain
        try {
            BlockCapsule capsule2 = new BlockCapsule(2, Sha256Hash.wrap(ByteString.copyFrom(ByteArray.fromHexString("0000000000000002498b464ac0292229938a342238077182498b464ac0292222"))), 1234, ByteString.copyFrom("1234567".getBytes()));
            BlockCapsule capsule4 = new BlockCapsule(4, Sha256Hash.wrap(ByteString.copyFrom(ByteArray.fromHexString("00000000000000042498b464ac0292229938a342238077182498b464ac029222"))), 1234, ByteString.copyFrom("abcdefg".getBytes()));
            blockChainSummary = new ArrayList<BlockId>();
            blockChainSummary.add(capsule2.getBlockId());
            blockChainSummary.add(capsule4.getBlockId());
            blockIds = del.getLostBlockIds(blockChainSummary);
        } catch (StoreException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(((blockIds.size()) == 0));
        // blockChainSummary have 2nd block(main chain)?4th block(fork chain)
        try {
            BlockCapsule capsule4 = new BlockCapsule(4, Sha256Hash.wrap(ByteString.copyFrom(ByteArray.fromHexString("00000000000000042498b464ac0292229938a342238077182498b464ac029222"))), 1234, ByteString.copyFrom("abcdefg".getBytes()));
            blockChainSummary = new ArrayList<BlockId>();
            blockChainSummary.add(dbManager.getBlockIdByNum(2));
            blockChainSummary.add(capsule4.getBlockId());
            except_first_block = dbManager.getBlockIdByNum(2);
            blockIds = del.getLostBlockIds(blockChainSummary);
        } catch (StoreException e) {
            e.printStackTrace();
        }
        Assert.assertTrue((((blockIds.size()) == 4) && (Arrays.equals(blockIds.peekFirst().getBytes(), except_first_block.getBytes()))));
        logger.info("finish2");
    }

    private boolean go = false;
}

