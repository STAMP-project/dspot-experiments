package org.tron.core.db.api;


import java.util.List;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.NonUniqueObjectException;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Witness;


public class StoreAPITest {
    public static final String ACCOUNT_ADDRESS_ONE = "121212a9cf";

    public static final String ACCOUNT_ADDRESS_TWO = "232323a9cf";

    public static final String ACCOUNT_ADDRESS_THREE = "343434a9cf";

    public static final String ACCOUNT_ADDRESS_FOUR = "454545a9cf";

    public static final String ACCOUNT_NAME_ONE = "account12";

    public static final String ACCOUNT_NAME_TWO = "account23";

    public static final long BLOCK_NUM_ONE = 10;

    public static final long BLOCK_NUM_TWO = 11;

    public static final long BLOCK_NUM_THREE = 12;

    public static final long BLOCK_TIMESTAMP_ONE = DateTime.now().minusDays(2).getMillis();

    public static final long BLOCK_TIMESTAMP_TWO = DateTime.now().minusDays(1).getMillis();

    public static final long BLOCK_TIMESTAMP_THREE = DateTime.now().getMillis();

    public static final long BLOCK_WITNESS_ONE = 12;

    public static final long BLOCK_WITNESS_TWO = 13;

    public static final long BLOCK_WITNESS_THREE = 14;

    public static final long TRANSACTION_TIMESTAMP_ONE = DateTime.now().minusDays(2).getMillis();

    public static final long TRANSACTION_TIMESTAMP_TWO = DateTime.now().minusDays(1).getMillis();

    public static final long TRANSACTION_TIMESTAMP_THREE = DateTime.now().getMillis();

    public static final String WITNESS_PUB_K_ONE = "989898a9cf";

    public static final String WITNESS_PUB_K_TWO = "878787a9cf";

    public static final String WITNESS_PUB_K_THREE = "767676a9cf";

    public static final String WITNESS_PUB_K_FOUR = "656565a9cf";

    public static final String WITNESS_URL_ONE = "www.tron.cn";

    public static final String WITNESS_URL_TWO = "www.tron-super.cn";

    public static final String WITNESS_URL_THREE = "www.tron-plus.cn";

    public static final String WITNESS_URL_FOUR = "www.tron-hk.cn";

    public static final long WITNESS_COUNT_ONE = 100;

    public static final long WITNESS_COUNT_TWO = 200;

    public static final long WITNESS_COUNT_THREE = 300;

    public static final long WITNESS_COUNT_FOUR = 400;

    public static final String ASSETISSUE_NAME_ONE = "www.tron.cn";

    public static final String ASSETISSUE_NAME_TWO = "www.tron-super.cn";

    public static final String ASSETISSUE_NAME_THREE = "www.tron-plus.cn";

    public static final String ASSETISSUE_NAME_FOUR = "www.tron-hk.cn";

    public static final long ASSETISSUE_START_ONE = DateTime.now().minusDays(2).getMillis();

    public static final long ASSETISSUE_END_ONE = DateTime.now().minusDays(1).getMillis();

    public static final long ASSETISSUE_START_TWO = DateTime.now().minusDays(1).getMillis();

    public static final long ASSETISSUE_END_TWO = DateTime.now().plusDays(1).getMillis();

    public static final long ASSETISSUE_START_THREE = DateTime.now().getMillis();

    public static final long ASSETISSUE_END_THREE = DateTime.now().plusDays(1).getMillis();

    public static final long ASSETISSUE_START_FOUR = DateTime.now().plusDays(1).getMillis();

    public static final long ASSETISSUE_END_FOUR = DateTime.now().plusDays(2).getMillis();

    public static Account account1;

    public static Account account2;

    public static Block block1;

    public static Block block2;

    public static Block block3;

    public static Witness witness1;

    public static Witness witness2;

    public static Witness witness3;

    public static Witness witness4;

    public static Transaction transaction1;

    public static Transaction transaction2;

    public static Transaction transaction3;

    public static Transaction transaction4;

    public static Transaction transaction5;

    public static Transaction transaction6;

    public static AssetIssueContract assetIssue1;

    public static AssetIssueContract assetIssue2;

    public static AssetIssueContract assetIssue3;

    public static AssetIssueContract assetIssue4;

    private static Manager dbManager;

    private static StoreAPI storeAPI;

    private static TronApplicationContext context;

    private static String dbPath = "output_StoreAPI_test";

    private static Application AppT;

    static {
        Args.setParam(new String[]{ "-d", StoreAPITest.dbPath, "-w" }, "config-test-index.conf");
        Args.getInstance().setSolidityNode(true);
        StoreAPITest.context = new TronApplicationContext(DefaultConfig.class);
        StoreAPITest.AppT = ApplicationFactory.create(StoreAPITest.context);
    }

    @Test
    public void addAssetIssueToStoreV2() {
        byte[] id = ByteArray.fromString("100000");
        StoreAPITest.addAssetIssueToStoreV2(StoreAPITest.assetIssue1, id);
        AssetIssueCapsule assetIssueCapsule = StoreAPITest.dbManager.getAssetIssueV2Store().get(id);
        Assert.assertEquals(true, assetIssueCapsule.getName().equals(StoreAPITest.assetIssue1.getName()));
    }

    @Test
    public void UpdateAssetV2() {
        long tokenIdNum = StoreAPITest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        for (AssetIssueCapsule assetIssueCapsule : StoreAPITest.dbManager.getAssetIssueStore().getAllAssetIssues()) {
            StoreAPITest.dbManager.getAssetIssueV2Store().put(ByteArray.fromLong(tokenIdNum), assetIssueCapsule);
            assetIssueCapsule.setId(String.valueOf(tokenIdNum));
            tokenIdNum++;
        }
        Assert.assertEquals(2, StoreAPITest.dbManager.getAssetIssueV2Store().getAllAssetIssues().size());
    }

    @Test
    public void getTransactionsFromThis() {
        List<Transaction> transactionList = StoreAPITest.storeAPI.getTransactionsFromThis(StoreAPITest.ACCOUNT_ADDRESS_ONE, 0, 1000);
        Assert.assertEquals("TransactionsFromThis1", StoreAPITest.transaction1, transactionList.get(0));
        transactionList = StoreAPITest.storeAPI.getTransactionsFromThis(StoreAPITest.ACCOUNT_ADDRESS_TWO, 0, 1000);
        Assert.assertEquals("TransactionsFromThis2", StoreAPITest.transaction2, transactionList.get(0));
        transactionList = StoreAPITest.storeAPI.getTransactionsFromThis(null, 0, 1000);
        Assert.assertEquals("TransactionsFromThis3", 0, transactionList.size());
        transactionList = StoreAPITest.storeAPI.getTransactionsFromThis("", 0, 1000);
        Assert.assertEquals("TransactionsFromThis4", 0, transactionList.size());
    }

    @Test
    public void getTransactionsToThis() {
        List<Transaction> transactionList = StoreAPITest.storeAPI.getTransactionsToThis(StoreAPITest.ACCOUNT_ADDRESS_TWO, 0, 1000);
        Assert.assertEquals("TransactionsToThis1", StoreAPITest.transaction1, transactionList.get(0));
        transactionList = StoreAPITest.storeAPI.getTransactionsToThis(StoreAPITest.ACCOUNT_ADDRESS_THREE, 0, 1000);
        Assert.assertEquals("TransactionsToThis2", StoreAPITest.transaction2, transactionList.get(0));
        transactionList = StoreAPITest.storeAPI.getTransactionsToThis(null, 0, 1000);
        Assert.assertEquals("TransactionsToThis3", 0, transactionList.size());
        transactionList = StoreAPITest.storeAPI.getTransactionsToThis("", 0, 1000);
        Assert.assertEquals("TransactionsToThis4", 0, transactionList.size());
    }

    @Test
    public void getTransactionById() {
        try {
            Transaction transaction = StoreAPITest.storeAPI.getTransactionById(getTransactionId().toString());
            Assert.assertEquals("TransactionById1", StoreAPITest.transaction1, transaction);
            transaction = StoreAPITest.storeAPI.getTransactionById(getTransactionId().toString());
            Assert.assertEquals("TransactionById2", StoreAPITest.transaction2, transaction);
        } catch (NonUniqueObjectException e) {
            Assert.fail(("Exception " + e));
        }
    }
}

