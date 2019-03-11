package org.tron.core.net.node;


import BlockHeader.raw;
import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.utils.BlockUtil;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.net.message.BlockMessage;
import org.tron.core.net.peer.PeerConnection;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.BlockHeader;
import org.tron.protos.Protocol.Inventory.InventoryType;


@Slf4j
public class NodeImplTest {
    private static TronApplicationContext context;

    private static Application appT;

    private static String dbPath = "output_nodeimpl_test";

    private static NodeImpl nodeImpl;

    private static Manager dbManager;

    private static NodeDelegateImpl nodeDelegate;

    static {
        Args.setParam(new String[]{ "-d", NodeImplTest.dbPath }, TEST_CONF);
        NodeImplTest.context = new TronApplicationContext(DefaultConfig.class);
        Args.getInstance().setSolidityNode(true);
        NodeImplTest.appT = ApplicationFactory.create(NodeImplTest.context);
    }

    @Test
    public void testSyncBlockMessage() throws Exception {
        PeerConnection peer = new PeerConnection();
        BlockCapsule genesisBlockCapsule = BlockUtil.newGenesisBlockCapsule();
        ByteString witnessAddress = ByteString.copyFrom(ECKey.fromPrivate(ByteArray.fromHexString(Args.getInstance().getLocalWitnesses().getPrivateKey())).getAddress());
        raw raw = raw.newBuilder().setTimestamp(System.currentTimeMillis()).setParentHash(genesisBlockCapsule.getParentHash().getByteString()).setNumber(((genesisBlockCapsule.getNum()) + 1)).setWitnessAddress(witnessAddress).setWitnessId(1).build();
        BlockHeader blockHeader = BlockHeader.newBuilder().setRawData(raw).build();
        Block block = Block.newBuilder().setBlockHeader(blockHeader).build();
        BlockCapsule blockCapsule = new BlockCapsule(block);
        blockCapsule.sign(ByteArray.fromHexString(Args.getInstance().getLocalWitnesses().getPrivateKey()));
        blockCapsule.setMerkleRoot();
        BlockMessage blockMessage = new BlockMessage(blockCapsule);
        peer.getSyncBlockRequested().put(blockMessage.getBlockId(), System.currentTimeMillis());
        NodeImplTest.nodeImpl.onMessage(peer, blockMessage);
        Assert.assertEquals(peer.getSyncBlockRequested().size(), 0);
    }

    @Test
    public void testAdvBlockMessage() throws Exception {
        PeerConnection peer = new PeerConnection();
        BlockCapsule genesisBlockCapsule = BlockUtil.newGenesisBlockCapsule();
        ByteString witnessAddress = ByteString.copyFrom(ECKey.fromPrivate(ByteArray.fromHexString(Args.getInstance().getLocalWitnesses().getPrivateKey())).getAddress());
        raw raw = raw.newBuilder().setTimestamp(System.currentTimeMillis()).setParentHash(genesisBlockCapsule.getBlockId().getByteString()).setNumber(((genesisBlockCapsule.getNum()) + 1)).setWitnessAddress(witnessAddress).setWitnessId(1).build();
        BlockHeader blockHeader = BlockHeader.newBuilder().setRawData(raw).build();
        Block block = Block.newBuilder().setBlockHeader(blockHeader).build();
        BlockCapsule blockCapsule = new BlockCapsule(block);
        blockCapsule.setMerkleRoot();
        blockCapsule.sign(ByteArray.fromHexString(Args.getInstance().getLocalWitnesses().getPrivateKey()));
        BlockMessage blockMessage = new BlockMessage(blockCapsule);
        peer.getAdvObjWeRequested().put(new Item(blockMessage.getBlockId(), InventoryType.BLOCK), System.currentTimeMillis());
        NodeImplTest.nodeImpl.onMessage(peer, blockMessage);
        Assert.assertEquals(peer.getAdvObjWeRequested().size(), 0);
    }
}

