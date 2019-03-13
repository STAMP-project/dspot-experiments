package org.tron.core.db;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.exception.BadNumberBlockException;
import org.tron.core.exception.UnLinkedBlockException;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.BlockHeader;


@Slf4j
public class KhaosDatabaseTest {
    private static final String dbPath = "output-khaosDatabase-test";

    private static KhaosDatabase khaosDatabase;

    private static TronApplicationContext context;

    static {
        Args.setParam(new String[]{ "--output-directory", KhaosDatabaseTest.dbPath }, TEST_CONF);
        KhaosDatabaseTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void testStartBlock() {
        BlockCapsule blockCapsule = new BlockCapsule(Block.newBuilder().setBlockHeader(BlockHeader.newBuilder().setRawData(raw.newBuilder().setParentHash(ByteString.copyFrom(ByteArray.fromHexString("0304f784e4e7bae517bcab94c3e0c9214fb4ac7ff9d7d5a937d1f40031f87b81"))))).build());
        KhaosDatabaseTest.khaosDatabase.start(blockCapsule);
        Assert.assertEquals(blockCapsule, KhaosDatabaseTest.khaosDatabase.getBlock(blockCapsule.getBlockId()));
    }

    @Test
    public void testPushGetBlock() {
        BlockCapsule blockCapsule = new BlockCapsule(Block.newBuilder().setBlockHeader(BlockHeader.newBuilder().setRawData(raw.newBuilder().setParentHash(ByteString.copyFrom(ByteArray.fromHexString("0304f784e4e7bae517bcab94c3e0c9214fb4ac7ff9d7d5a937d1f40031f87b81"))))).build());
        BlockCapsule blockCapsule2 = new BlockCapsule(Block.newBuilder().setBlockHeader(BlockHeader.newBuilder().setRawData(raw.newBuilder().setParentHash(ByteString.copyFrom(ByteArray.fromHexString("9938a342238077182498b464ac029222ae169360e540d1fd6aee7c2ae9575a06"))))).build());
        KhaosDatabaseTest.khaosDatabase.start(blockCapsule);
        try {
            KhaosDatabaseTest.khaosDatabase.push(blockCapsule2);
        } catch (UnLinkedBlockException | BadNumberBlockException e) {
        }
        Assert.assertEquals(blockCapsule2, KhaosDatabaseTest.khaosDatabase.getBlock(blockCapsule2.getBlockId()));
        Assert.assertTrue("contain is error", KhaosDatabaseTest.khaosDatabase.containBlock(blockCapsule2.getBlockId()));
        KhaosDatabaseTest.khaosDatabase.removeBlk(blockCapsule2.getBlockId());
        Assert.assertNull("removeBlk is error", KhaosDatabaseTest.khaosDatabase.getBlock(blockCapsule2.getBlockId()));
    }

    @Test
    public void checkWeakReference() throws BadNumberBlockException, UnLinkedBlockException {
        BlockCapsule blockCapsule = new BlockCapsule(Block.newBuilder().setBlockHeader(BlockHeader.newBuilder().setRawData(raw.newBuilder().setParentHash(ByteString.copyFrom(ByteArray.fromHexString("0304f784e4e7bae517bcab94c3e0c9214fb4ac7ff9d7d5a937d1f40031f87b82"))).setNumber(0))).build());
        BlockCapsule blockCapsule2 = new BlockCapsule(Block.newBuilder().setBlockHeader(BlockHeader.newBuilder().setRawData(raw.newBuilder().setParentHash(ByteString.copyFrom(blockCapsule.getBlockId().getBytes())).setNumber(1))).build());
        Assert.assertEquals(blockCapsule.getBlockId(), blockCapsule2.getParentHash());
        KhaosDatabaseTest.khaosDatabase.start(blockCapsule);
        KhaosDatabaseTest.khaosDatabase.push(blockCapsule2);
        KhaosDatabaseTest.khaosDatabase.removeBlk(blockCapsule.getBlockId());
        logger.info(("*** " + (KhaosDatabaseTest.khaosDatabase.getBlock(blockCapsule.getBlockId()))));
        Object object = new Object();
        Reference<Object> objectReference = new WeakReference<>(object);
        blockCapsule = null;
        object = null;
        System.gc();
        logger.info(("***** object ref:" + (objectReference.get())));
        Assert.assertNull(objectReference.get());
        Assert.assertNull(KhaosDatabaseTest.khaosDatabase.getParentBlock(blockCapsule2.getBlockId()));
    }
}

