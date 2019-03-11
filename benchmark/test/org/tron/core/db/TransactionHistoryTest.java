package org.tron.core.db;


import Constant.TEST_CONF;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.TransactionInfoCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.exception.BadItemException;


public class TransactionHistoryTest {
    private static String dbPath = "output_TransactionHistoryStore_test";

    private static String dbDirectory = "db_TransactionHistoryStore_test";

    private static String indexDirectory = "index_TransactionHistoryStore_test";

    private static TronApplicationContext context;

    private static TransactionHistoryStore transactionHistoryStore;

    private static final byte[] transactionId = TransactionStoreTest.randomBytes(32);

    static {
        Args.setParam(new String[]{ "--output-directory", TransactionHistoryTest.dbPath, "--storage-db-directory", TransactionHistoryTest.dbDirectory, "--storage-index-directory", TransactionHistoryTest.indexDirectory }, TEST_CONF);
        TransactionHistoryTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void get() throws BadItemException {
        // test get and has Method
        TransactionInfoCapsule resultCapsule = TransactionHistoryTest.transactionHistoryStore.get(TransactionHistoryTest.transactionId);
        Assert.assertEquals(1000L, resultCapsule.getFee());
        Assert.assertEquals(100L, resultCapsule.getBlockNumber());
        Assert.assertEquals(200L, resultCapsule.getBlockTimeStamp());
        Assert.assertEquals(ByteArray.toHexString(TransactionHistoryTest.transactionId), ByteArray.toHexString(resultCapsule.getId()));
    }
}

