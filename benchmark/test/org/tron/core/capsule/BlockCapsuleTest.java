package org.tron.core.capsule;


import Sha256Hash.ZERO_HASH;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.Constant;
import org.tron.core.Wallet;
import org.tron.core.exception.BadItemException;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;


@Slf4j
public class BlockCapsuleTest {
    private static BlockCapsule blockCapsule0 = new BlockCapsule(1, Sha256Hash.wrap(ByteString.copyFrom(ByteArray.fromHexString("9938a342238077182498b464ac0292229938a342238077182498b464ac029222"))), 1234, ByteString.copyFrom("1234567".getBytes()));

    private static String dbPath = "output_bloackcapsule_test";

    @Test
    public void testCalcMerkleRoot() throws Exception {
        BlockCapsuleTest.blockCapsule0.setMerkleRoot();
        Assert.assertEquals(Sha256Hash.wrap(ZERO_HASH.getByteString()).toString(), BlockCapsuleTest.blockCapsule0.getMerkleRoot().toString());
        logger.info("Transaction[X] Merkle Root : {}", BlockCapsuleTest.blockCapsule0.getMerkleRoot().toString());
        TransferContract transferContract1 = TransferContract.newBuilder().setAmount(1L).setOwnerAddress(ByteString.copyFrom("0x0000000000000000000".getBytes())).setToAddress(ByteString.copyFrom(ByteArray.fromHexString(((Wallet.getAddressPreFixString()) + "A389132D6639FBDA4FBC8B659264E6B7C90DB086")))).build();
        TransferContract transferContract2 = TransferContract.newBuilder().setAmount(2L).setOwnerAddress(ByteString.copyFrom("0x0000000000000000000".getBytes())).setToAddress(ByteString.copyFrom(ByteArray.fromHexString(((Wallet.getAddressPreFixString()) + "ED738B3A0FE390EAA71B768B6D02CDBD18FB207B")))).build();
        BlockCapsuleTest.blockCapsule0.addTransaction(new TransactionCapsule(transferContract1, ContractType.TransferContract));
        BlockCapsuleTest.blockCapsule0.addTransaction(new TransactionCapsule(transferContract2, ContractType.TransferContract));
        BlockCapsuleTest.blockCapsule0.setMerkleRoot();
        if ((Constant.ADD_PRE_FIX_BYTE_TESTNET) == (Wallet.getAddressPreFixByte())) {
            Assert.assertEquals("53421c1f1bcbbba67a4184cc3dbc1a59f90af7e2b0644dcfc8dc738fe30deffc", BlockCapsuleTest.blockCapsule0.getMerkleRoot().toString());
        } else {
            Assert.assertEquals("5bc862243292e6aa1d5e21a60bb6a673e4c2544709f6363d4a2f85ec29bcfe00", BlockCapsuleTest.blockCapsule0.getMerkleRoot().toString());
        }
        logger.info("Transaction[O] Merkle Root : {}", BlockCapsuleTest.blockCapsule0.getMerkleRoot().toString());
    }

    /* @Test
    public void testAddTransaction() {
    TransactionCapsule transactionCapsule = new TransactionCapsule("123", 1L);
    blockCapsule0.addTransaction(transactionCapsule);
    Assert.assertArrayEquals(blockCapsule0.getTransactions().get(0).getHash().getBytes(),
    transactionCapsule.getHash().getBytes());
    Assert.assertEquals(transactionCapsule.getInstance().getRawData().getVout(0).getValue(),
    blockCapsule0.getTransactions().get(0).getInstance().getRawData().getVout(0).getValue());
    }
     */
    @Test
    public void testGetData() {
        BlockCapsuleTest.blockCapsule0.getData();
        byte[] b = BlockCapsuleTest.blockCapsule0.getData();
        BlockCapsule blockCapsule1 = null;
        try {
            blockCapsule1 = new BlockCapsule(b);
            Assert.assertEquals(BlockCapsuleTest.blockCapsule0.getBlockId(), blockCapsule1.getBlockId());
        } catch (BadItemException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetInsHash() {
        Assert.assertEquals(1, BlockCapsuleTest.blockCapsule0.getInstance().getBlockHeader().getRawData().getNumber());
        Assert.assertEquals(BlockCapsuleTest.blockCapsule0.getParentHash(), Sha256Hash.wrap(BlockCapsuleTest.blockCapsule0.getParentHashStr()));
    }

    @Test
    public void testGetTimeStamp() {
        Assert.assertEquals(1234L, BlockCapsuleTest.blockCapsule0.getTimeStamp());
    }
}

