package org.tron.core.db;


import Constant.TEST_CONF;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;


public class AccountStoreTest {
    private static String dbPath = "output_AccountStore_test";

    private static String dbDirectory = "db_AccountStore_test";

    private static String indexDirectory = "index_AccountStore_test";

    private static TronApplicationContext context;

    private static AccountStore accountStore;

    private static final byte[] data = TransactionStoreTest.randomBytes(32);

    private static byte[] address = TransactionStoreTest.randomBytes(32);

    private static byte[] accountName = TransactionStoreTest.randomBytes(32);

    static {
        Args.setParam(new String[]{ "--output-directory", AccountStoreTest.dbPath, "--storage-db-directory", AccountStoreTest.dbDirectory, "--storage-index-directory", AccountStoreTest.indexDirectory }, TEST_CONF);
        AccountStoreTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void get() {
        // test get and has Method
        Assert.assertEquals(ByteArray.toHexString(AccountStoreTest.address), ByteArray.toHexString(AccountStoreTest.accountStore.get(AccountStoreTest.data).getInstance().getAddress().toByteArray()));
        Assert.assertEquals(ByteArray.toHexString(AccountStoreTest.accountName), ByteArray.toHexString(AccountStoreTest.accountStore.get(AccountStoreTest.data).getInstance().getAccountName().toByteArray()));
        Assert.assertTrue(AccountStoreTest.accountStore.has(AccountStoreTest.data));
    }
}

