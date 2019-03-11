package org.tron.core.db;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.exception.BadItemException;
import org.tron.core.exception.ItemNotFoundException;
import org.tron.protos.Contract.AccountCreateContract;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Contract.VoteWitnessContract;
import org.tron.protos.Contract.WitnessCreateContract;
import org.tron.protos.Protocol.AccountType;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;


@Ignore
public class TransactionStoreTest {
    private static String dbPath = "output_TransactionStore_test";

    private static String dbDirectory = "db_TransactionStore_test";

    private static String indexDirectory = "index_TransactionStore_test";

    private static TransactionStore transactionStore;

    private static TronApplicationContext context;

    private static final byte[] key1 = TransactionStoreTest.randomBytes(21);

    private static Manager dbManager;

    private static final byte[] key2 = TransactionStoreTest.randomBytes(21);

    private static final String URL = "https://tron.network";

    private static final String ACCOUNT_NAME = "ownerF";

    private static final String OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";

    private static final String TO_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";

    private static final long AMOUNT = 100;

    private static final String WITNESS_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";

    static {
        Args.setParam(new String[]{ "--output-directory", TransactionStoreTest.dbPath, "--storage-db-directory", TransactionStoreTest.dbDirectory, "--storage-index-directory", TransactionStoreTest.indexDirectory, "-w" }, TEST_CONF);
        TransactionStoreTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void GetTransactionTest() throws BadItemException, ItemNotFoundException {
        final BlockStore blockStore = TransactionStoreTest.dbManager.getBlockStore();
        final TransactionStore trxStore = TransactionStoreTest.dbManager.getTransactionStore();
        String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
        BlockCapsule blockCapsule = new BlockCapsule(1, Sha256Hash.wrap(TransactionStoreTest.dbManager.getGenesisBlockId().getByteString()), 1, ByteString.copyFrom(ECKey.fromPrivate(ByteArray.fromHexString(key)).getAddress()));
        // save in database with block number
        TransferContract tc = TransferContract.newBuilder().setAmount(10).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        TransactionCapsule trx = new TransactionCapsule(tc, ContractType.TransferContract);
        blockCapsule.addTransaction(trx);
        trx.setBlockNum(blockCapsule.getNum());
        blockStore.put(blockCapsule.getBlockId().getBytes(), blockCapsule);
        trxStore.put(trx.getTransactionId().getBytes(), trx);
        Assert.assertEquals("Get transaction is error", trxStore.get(trx.getTransactionId().getBytes()).getInstance(), trx.getInstance());
        // no found in transaction store database
        tc = TransferContract.newBuilder().setAmount(1000).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        trx = new TransactionCapsule(tc, ContractType.TransferContract);
        Assert.assertNull(trxStore.get(trx.getTransactionId().getBytes()));
        // no block number, directly save in database
        tc = TransferContract.newBuilder().setAmount(10000).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        trx = new TransactionCapsule(tc, ContractType.TransferContract);
        trxStore.put(trx.getTransactionId().getBytes(), trx);
        Assert.assertEquals("Get transaction is error", trxStore.get(trx.getTransactionId().getBytes()).getInstance(), trx.getInstance());
    }

    /**
     * put and get CreateAccountTransaction.
     */
    @Test
    public void CreateAccountTransactionStoreTest() throws BadItemException {
        AccountCreateContract accountCreateContract = getContract(TransactionStoreTest.ACCOUNT_NAME, TransactionStoreTest.OWNER_ADDRESS);
        TransactionCapsule ret = new TransactionCapsule(accountCreateContract, TransactionStoreTest.dbManager.getAccountStore());
        TransactionStoreTest.transactionStore.put(TransactionStoreTest.key1, ret);
        Assert.assertEquals("Store CreateAccountTransaction is error", TransactionStoreTest.transactionStore.get(TransactionStoreTest.key1).getInstance(), ret.getInstance());
        Assert.assertTrue(TransactionStoreTest.transactionStore.has(TransactionStoreTest.key1));
    }

    @Test
    public void GetUncheckedTransactionTest() {
        final BlockStore blockStore = TransactionStoreTest.dbManager.getBlockStore();
        final TransactionStore trxStore = TransactionStoreTest.dbManager.getTransactionStore();
        String key = "f31db24bfbd1a2ef19beddca0a0fa37632eded9ac666a05d3bd925f01dde1f62";
        BlockCapsule blockCapsule = new BlockCapsule(1, Sha256Hash.wrap(TransactionStoreTest.dbManager.getGenesisBlockId().getByteString()), 1, ByteString.copyFrom(ECKey.fromPrivate(ByteArray.fromHexString(key)).getAddress()));
        // save in database with block number
        TransferContract tc = TransferContract.newBuilder().setAmount(10).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        TransactionCapsule trx = new TransactionCapsule(tc, ContractType.TransferContract);
        blockCapsule.addTransaction(trx);
        trx.setBlockNum(blockCapsule.getNum());
        blockStore.put(blockCapsule.getBlockId().getBytes(), blockCapsule);
        trxStore.put(trx.getTransactionId().getBytes(), trx);
        Assert.assertEquals("Get transaction is error", trxStore.getUnchecked(trx.getTransactionId().getBytes()).getInstance(), trx.getInstance());
        // no found in transaction store database
        tc = TransferContract.newBuilder().setAmount(1000).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        trx = new TransactionCapsule(tc, ContractType.TransferContract);
        Assert.assertNull(trxStore.getUnchecked(trx.getTransactionId().getBytes()));
        // no block number, directly save in database
        tc = TransferContract.newBuilder().setAmount(10000).setOwnerAddress(ByteString.copyFromUtf8("aaa")).setToAddress(ByteString.copyFromUtf8("bbb")).build();
        trx = new TransactionCapsule(tc, ContractType.TransferContract);
        trxStore.put(trx.getTransactionId().getBytes(), trx);
        Assert.assertEquals("Get transaction is error", trxStore.getUnchecked(trx.getTransactionId().getBytes()).getInstance(), trx.getInstance());
    }

    /**
     * put and get CreateWitnessTransaction.
     */
    @Test
    public void CreateWitnessTransactionStoreTest() throws BadItemException {
        WitnessCreateContract witnessContract = getWitnessContract(TransactionStoreTest.OWNER_ADDRESS, TransactionStoreTest.URL);
        TransactionCapsule transactionCapsule = new TransactionCapsule(witnessContract);
        TransactionStoreTest.transactionStore.put(TransactionStoreTest.key1, transactionCapsule);
        Assert.assertEquals("Store CreateWitnessTransaction is error", TransactionStoreTest.transactionStore.get(TransactionStoreTest.key1).getInstance(), transactionCapsule.getInstance());
    }

    /**
     * put and get TransferTransaction.
     */
    @Test
    public void TransferTransactionStorenTest() throws BadItemException {
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFromUtf8(TransactionStoreTest.ACCOUNT_NAME), ByteString.copyFrom(ByteArray.fromHexString(TransactionStoreTest.OWNER_ADDRESS)), AccountType.AssetIssue, 1000000L);
        TransactionStoreTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        TransferContract transferContract = getContract(TransactionStoreTest.AMOUNT, TransactionStoreTest.OWNER_ADDRESS, TransactionStoreTest.TO_ADDRESS);
        TransactionCapsule transactionCapsule = new TransactionCapsule(transferContract, TransactionStoreTest.dbManager.getAccountStore());
        TransactionStoreTest.transactionStore.put(TransactionStoreTest.key1, transactionCapsule);
        Assert.assertEquals("Store TransferTransaction is error", TransactionStoreTest.transactionStore.get(TransactionStoreTest.key1).getInstance(), transactionCapsule.getInstance());
    }

    /**
     * put and get VoteWitnessTransaction.
     */
    @Test
    public void voteWitnessTransactionTest() throws BadItemException {
        AccountCapsule ownerAccountFirstCapsule = new AccountCapsule(ByteString.copyFromUtf8(TransactionStoreTest.ACCOUNT_NAME), ByteString.copyFrom(ByteArray.fromHexString(TransactionStoreTest.OWNER_ADDRESS)), AccountType.Normal, 1000000000000L);
        long frozenBalance = 1000000000000L;
        long duration = 3;
        ownerAccountFirstCapsule.setFrozen(frozenBalance, duration);
        TransactionStoreTest.dbManager.getAccountStore().put(ownerAccountFirstCapsule.getAddress().toByteArray(), ownerAccountFirstCapsule);
        VoteWitnessContract actuator = getVoteWitnessContract(TransactionStoreTest.OWNER_ADDRESS, TransactionStoreTest.WITNESS_ADDRESS, 1L);
        TransactionCapsule transactionCapsule = new TransactionCapsule(actuator);
        TransactionStoreTest.transactionStore.put(TransactionStoreTest.key1, transactionCapsule);
        Assert.assertEquals("Store VoteWitnessTransaction is error", TransactionStoreTest.transactionStore.get(TransactionStoreTest.key1).getInstance(), transactionCapsule.getInstance());
    }

    /**
     * put value is null and get it.
     */
    @Test
    public void TransactionValueNullTest() throws BadItemException {
        TransactionCapsule transactionCapsule = null;
        TransactionStoreTest.transactionStore.put(TransactionStoreTest.key2, transactionCapsule);
        Assert.assertNull("put value is null", TransactionStoreTest.transactionStore.get(TransactionStoreTest.key2));
    }

    /**
     * put key is null and get it.
     */
    @Test
    public void TransactionKeyNullTest() throws BadItemException {
        AccountCreateContract accountCreateContract = getContract(TransactionStoreTest.ACCOUNT_NAME, TransactionStoreTest.OWNER_ADDRESS);
        TransactionCapsule ret = new TransactionCapsule(accountCreateContract, TransactionStoreTest.dbManager.getAccountStore());
        byte[] key = null;
        TransactionStoreTest.transactionStore.put(key, ret);
        try {
            TransactionStoreTest.transactionStore.get(key);
        } catch (RuntimeException e) {
            Assert.assertEquals("The key argument cannot be null", e.getMessage());
        }
    }
}

