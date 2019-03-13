package org.tron.core.db;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.capsule.WitnessCapsule;
import org.tron.core.config.args.Args;
import org.tron.core.exception.AccountResourceInsufficientException;
import org.tron.core.exception.BadBlockException;
import org.tron.core.exception.BadItemException;
import org.tron.core.exception.BadNumberBlockException;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.DupTransactionException;
import org.tron.core.exception.HeaderNotFound;
import org.tron.core.exception.ItemNotFoundException;
import org.tron.core.exception.NonCommonBlockException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.TaposException;
import org.tron.core.exception.TooBigTransactionException;
import org.tron.core.exception.TooBigTransactionResultException;
import org.tron.core.exception.TransactionExpirationException;
import org.tron.core.exception.UnLinkedBlockException;
import org.tron.core.exception.VMIllegalException;
import org.tron.core.exception.ValidateScheduleException;
import org.tron.core.exception.ValidateSignatureException;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;


@Slf4j
public class ManagerTest {
    private static Manager dbManager;

    private static TronApplicationContext context;

    private static BlockCapsule blockCapsule2;

    private static String dbPath = "output_manager_test";

    private static AtomicInteger port = new AtomicInteger(0);

    @Test
    public void setBlockReference() throws AccountResourceInsufficientException, BadBlockException, BadItemException, BadNumberBlockException, ContractExeException, ContractValidateException, DupTransactionException, ItemNotFoundException, NonCommonBlockException, ReceiptCheckErrException, TaposException, TooBigTransactionException, TooBigTransactionResultException, TransactionExpirationException, UnLinkedBlockException, VMIllegalException, ValidateScheduleException, ValidateSignatureException {
        BlockCapsule blockCapsule = new BlockCapsule(1, Sha256Hash.wrap(ManagerTest.dbManager.getGenesisBlockId().getByteString()), 1, ByteString.copyFrom(ECKey.fromPrivate(ByteArray.fromHexString(Args.getInstance().getLocalWitnesses().getPrivateKey())).getAddress()));
        blockCapsule.setMerkleRoot();
        blockCapsule.sign(ByteArray.fromHexString(Args.getInstance().getLocalWitnesses().getPrivateKey()));
        TransferContract tc = TransferContract.newBuilder().setAmount(10).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        TransactionCapsule trx = new TransactionCapsule(tc, ContractType.TransferContract);
        if ((ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) == 0) {
            ManagerTest.dbManager.pushBlock(blockCapsule);
            Assert.assertEquals(1, ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber());
            ManagerTest.dbManager.setBlockReference(trx);
            Assert.assertEquals(1, ByteArray.toInt(trx.getInstance().getRawData().getRefBlockBytes().toByteArray()));
        }
        while ((ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) > 0) {
            ManagerTest.dbManager.eraseBlock();
        } 
        ManagerTest.dbManager.pushBlock(blockCapsule);
        Assert.assertEquals(1, ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber());
        ManagerTest.dbManager.setBlockReference(trx);
        Assert.assertEquals(1, ByteArray.toInt(trx.getInstance().getRawData().getRefBlockBytes().toByteArray()));
    }

    @Test
    public void pushBlock() {
        boolean isUnlinked = false;
        try {
            ManagerTest.dbManager.pushBlock(ManagerTest.blockCapsule2);
        } catch (UnLinkedBlockException e) {
            isUnlinked = true;
        } catch (Exception e) {
            Assert.assertTrue("pushBlock is error", false);
        }
        // Assert.assertTrue(
        // "containBlock is error",
        // dbManager.containBlock(
        // Sha256Hash.wrap(ByteArray.fromHexString(blockCapsule2.getBlockId().toString()))));
        if (isUnlinked) {
            Assert.assertEquals("getBlockIdByNum is error", ManagerTest.dbManager.getHeadBlockNum(), 0);
        } else {
            try {
                Assert.assertEquals("getBlockIdByNum is error", ManagerTest.blockCapsule2.getBlockId().toString(), ManagerTest.dbManager.getBlockIdByNum(1).toString());
            } catch (ItemNotFoundException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue("hasBlocks is error", ManagerTest.dbManager.hasBlocks());
    }

    @Test
    public void fork() throws AccountResourceInsufficientException, BadBlockException, BadItemException, BadNumberBlockException, ContractExeException, ContractValidateException, DupTransactionException, HeaderNotFound, ItemNotFoundException, NonCommonBlockException, ReceiptCheckErrException, TaposException, TooBigTransactionException, TooBigTransactionResultException, TransactionExpirationException, UnLinkedBlockException, VMIllegalException, ValidateScheduleException, ValidateSignatureException {
        Args.setParam(new String[]{ "--witness" }, TEST_CONF);
        long size = ManagerTest.dbManager.getBlockStore().size();
        System.out.print((("block store size:" + size) + "\n"));
        String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
        byte[] privateKey = ByteArray.fromHexString(key);
        final ECKey ecKey = ECKey.fromPrivate(privateKey);
        byte[] address = ecKey.getAddress();
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address));
        ManagerTest.dbManager.addWitness(ByteString.copyFrom(address));
        ManagerTest.dbManager.generateBlock(witnessCapsule, 1533529947843L, privateKey, false, false);
        Map<ByteString, String> addressToProvateKeys = addTestWitnessAndAccount();
        long num = ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber();
        BlockCapsule blockCapsule0 = createTestBlockCapsule((1533529947843L + 3000), (num + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        BlockCapsule blockCapsule1 = createTestBlockCapsule((1533529947843L + 3000), (num + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        ManagerTest.dbManager.pushBlock(blockCapsule0);
        ManagerTest.dbManager.pushBlock(blockCapsule1);
        BlockCapsule blockCapsule2 = createTestBlockCapsule((1533529947843L + 6000), (num + 2), blockCapsule1.getBlockId().getByteString(), addressToProvateKeys);
        ManagerTest.dbManager.pushBlock(blockCapsule2);
        Assert.assertNotNull(ManagerTest.dbManager.getBlockStore().get(blockCapsule1.getBlockId().getBytes()));
        Assert.assertNotNull(ManagerTest.dbManager.getBlockStore().get(blockCapsule2.getBlockId().getBytes()));
        Assert.assertEquals(ManagerTest.dbManager.getBlockStore().get(blockCapsule2.getBlockId().getBytes()).getParentHash(), blockCapsule1.getBlockId());
        Assert.assertEquals(ManagerTest.dbManager.getBlockStore().size(), (size + 3));
        Assert.assertEquals(ManagerTest.dbManager.getBlockIdByNum(((ManagerTest.dbManager.getHead().getNum()) - 1)), blockCapsule1.getBlockId());
        Assert.assertEquals(ManagerTest.dbManager.getBlockIdByNum(((ManagerTest.dbManager.getHead().getNum()) - 2)), blockCapsule1.getParentHash());
        Assert.assertEquals(blockCapsule2.getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
        Assert.assertEquals(ManagerTest.dbManager.getHead().getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
    }

    @Test
    public void doNotSwitch() throws AccountResourceInsufficientException, BadBlockException, BadItemException, BadNumberBlockException, ContractExeException, ContractValidateException, DupTransactionException, HeaderNotFound, ItemNotFoundException, NonCommonBlockException, ReceiptCheckErrException, TaposException, TooBigTransactionException, TooBigTransactionResultException, TransactionExpirationException, UnLinkedBlockException, VMIllegalException, ValidateScheduleException, ValidateSignatureException {
        Args.setParam(new String[]{ "--witness" }, TEST_CONF);
        long size = ManagerTest.dbManager.getBlockStore().size();
        System.out.print((("block store size:" + size) + "\n"));
        String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
        byte[] privateKey = ByteArray.fromHexString(key);
        final ECKey ecKey = ECKey.fromPrivate(privateKey);
        byte[] address = ecKey.getAddress();
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address));
        ManagerTest.dbManager.addWitness(ByteString.copyFrom(address));
        ManagerTest.dbManager.generateBlock(witnessCapsule, 1533529947843L, privateKey, false, false);
        Map<ByteString, String> addressToProvateKeys = addTestWitnessAndAccount();
        long num = ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber();
        BlockCapsule blockCapsule0 = createTestBlockCapsule((1533529947843L + 3000), (num + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        BlockCapsule blockCapsule1 = createTestBlockCapsule((1533529947843L + 3001), (num + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        logger.info(("******block0:" + blockCapsule0));
        logger.info(("******block1:" + blockCapsule1));
        ManagerTest.dbManager.pushBlock(blockCapsule0);
        ManagerTest.dbManager.pushBlock(blockCapsule1);
        ManagerTest.context.getBean(KhaosDatabase.class).removeBlk(ManagerTest.dbManager.getBlockIdByNum(num));
        Exception exception = null;
        BlockCapsule blockCapsule2 = createTestBlockCapsule((1533529947843L + 6000), (num + 2), blockCapsule1.getBlockId().getByteString(), addressToProvateKeys);
        logger.info(("******block2:" + blockCapsule2));
        try {
            ManagerTest.dbManager.pushBlock(blockCapsule2);
        } catch (NonCommonBlockException e) {
            logger.info("do not switch fork");
            Assert.assertNotNull(ManagerTest.dbManager.getBlockStore().get(blockCapsule0.getBlockId().getBytes()));
            Assert.assertEquals(blockCapsule0.getBlockId(), ManagerTest.dbManager.getBlockStore().get(blockCapsule0.getBlockId().getBytes()).getBlockId());
            Assert.assertEquals(blockCapsule0.getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
            exception = e;
        }
        if (exception == null) {
            throw new IllegalStateException();
        }
        BlockCapsule blockCapsule3 = createTestBlockCapsule((1533529947843L + 9000), ((ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        logger.info(("******block3:" + blockCapsule3));
        ManagerTest.dbManager.pushBlock(blockCapsule3);
        Assert.assertEquals(blockCapsule3.getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
        Assert.assertEquals(blockCapsule3.getBlockId(), ManagerTest.dbManager.getBlockStore().get(ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getBytes()).getBlockId());
        BlockCapsule blockCapsule4 = createTestBlockCapsule((1533529947843L + 12000), ((ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1), blockCapsule3.getBlockId().getByteString(), addressToProvateKeys);
        logger.info(("******block4:" + blockCapsule4));
        ManagerTest.dbManager.pushBlock(blockCapsule4);
        Assert.assertEquals(blockCapsule4.getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
        Assert.assertEquals(blockCapsule4.getBlockId(), ManagerTest.dbManager.getBlockStore().get(ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getBytes()).getBlockId());
    }

    @Test
    public void testLastHeadBlockIsMaintenance() throws AccountResourceInsufficientException, BadBlockException, BadItemException, BadNumberBlockException, ContractExeException, ContractValidateException, DupTransactionException, HeaderNotFound, ItemNotFoundException, NonCommonBlockException, ReceiptCheckErrException, TaposException, TooBigTransactionException, TooBigTransactionResultException, TransactionExpirationException, UnLinkedBlockException, VMIllegalException, ValidateScheduleException, ValidateSignatureException {
        Args.setParam(new String[]{ "--witness" }, TEST_CONF);
        long size = ManagerTest.dbManager.getBlockStore().size();
        System.out.print((("block store size:" + size) + "\n"));
        String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
        byte[] privateKey = ByteArray.fromHexString(key);
        final ECKey ecKey = ECKey.fromPrivate(privateKey);
        byte[] address = ecKey.getAddress();
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address));
        ManagerTest.dbManager.addWitness(ByteString.copyFrom(address));
        BlockCapsule blockCapsule = ManagerTest.dbManager.generateBlock(witnessCapsule, 1533529947843L, privateKey, true, false);
        // has processed the first block of the maintenance period before starting the block
        ManagerTest.dbManager.getWitnessStore().reset();
        ManagerTest.dbManager.getDynamicPropertiesStore().saveStateFlag(0);
        blockCapsule = ManagerTest.dbManager.generateBlock(witnessCapsule, 1533529947843L, privateKey, true, false);
        Assert.assertTrue((blockCapsule == null));
    }

    @Test
    public void switchBack() throws AccountResourceInsufficientException, BadBlockException, BadItemException, BadNumberBlockException, ContractExeException, ContractValidateException, DupTransactionException, HeaderNotFound, ItemNotFoundException, NonCommonBlockException, ReceiptCheckErrException, TaposException, TooBigTransactionException, TooBigTransactionResultException, TransactionExpirationException, UnLinkedBlockException, VMIllegalException, ValidateScheduleException, ValidateSignatureException {
        Args.setParam(new String[]{ "--witness" }, TEST_CONF);
        long size = ManagerTest.dbManager.getBlockStore().size();
        System.out.print((("block store size:" + size) + "\n"));
        String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
        byte[] privateKey = ByteArray.fromHexString(key);
        final ECKey ecKey = ECKey.fromPrivate(privateKey);
        byte[] address = ecKey.getAddress();
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address));
        ManagerTest.dbManager.addWitness(ByteString.copyFrom(address));
        ManagerTest.dbManager.generateBlock(witnessCapsule, 1533529947843L, privateKey, false, false);
        Map<ByteString, String> addressToProvateKeys = addTestWitnessAndAccount();
        long num = ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber();
        BlockCapsule blockCapsule0 = createTestBlockCapsule((1533529947843L + 3000), (num + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        BlockCapsule blockCapsule1 = createTestBlockCapsule((1533529947843L + 3000), (num + 1), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getByteString(), addressToProvateKeys);
        ManagerTest.dbManager.pushBlock(blockCapsule0);
        ManagerTest.dbManager.pushBlock(blockCapsule1);
        try {
            BlockCapsule blockCapsule2 = createTestBlockCapsuleError((1533529947843L + 6000), (num + 2), blockCapsule1.getBlockId().getByteString(), addressToProvateKeys);
            ManagerTest.dbManager.pushBlock(blockCapsule2);
        } catch (ValidateScheduleException e) {
            logger.info("the fork chain has error block");
        }
        Assert.assertNotNull(ManagerTest.dbManager.getBlockStore().get(blockCapsule0.getBlockId().getBytes()));
        Assert.assertEquals(blockCapsule0.getBlockId(), ManagerTest.dbManager.getBlockStore().get(blockCapsule0.getBlockId().getBytes()).getBlockId());
        BlockCapsule blockCapsule3 = createTestBlockCapsule((1533529947843L + 9000), ((ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1), blockCapsule0.getBlockId().getByteString(), addressToProvateKeys);
        ManagerTest.dbManager.pushBlock(blockCapsule3);
        Assert.assertEquals(blockCapsule3.getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
        Assert.assertEquals(blockCapsule3.getBlockId(), ManagerTest.dbManager.getBlockStore().get(ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getBytes()).getBlockId());
        BlockCapsule blockCapsule4 = createTestBlockCapsule((1533529947843L + 12000), ((ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) + 1), blockCapsule3.getBlockId().getByteString(), addressToProvateKeys);
        ManagerTest.dbManager.pushBlock(blockCapsule4);
        Assert.assertEquals(blockCapsule4.getBlockId(), ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash());
        Assert.assertEquals(blockCapsule4.getBlockId(), ManagerTest.dbManager.getBlockStore().get(ManagerTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderHash().getBytes()).getBlockId());
    }
}

