package org.tron.core.db;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.protos.Protocol.AccountType;


public class AccountIdIndexStoreTest {
    private static String dbPath = "output_AccountIndexStore_test";

    private static TronApplicationContext context;

    private static AccountIdIndexStore accountIdIndexStore;

    private static final byte[] ACCOUNT_ADDRESS_ONE = AccountIdIndexStoreTest.randomBytes(16);

    private static final byte[] ACCOUNT_ADDRESS_TWO = AccountIdIndexStoreTest.randomBytes(16);

    private static final byte[] ACCOUNT_ADDRESS_THREE = AccountIdIndexStoreTest.randomBytes(16);

    private static final byte[] ACCOUNT_ADDRESS_FOUR = AccountIdIndexStoreTest.randomBytes(16);

    private static final byte[] ACCOUNT_NAME_ONE = AccountIdIndexStoreTest.randomBytes(6);

    private static final byte[] ACCOUNT_NAME_TWO = AccountIdIndexStoreTest.randomBytes(6);

    private static final byte[] ACCOUNT_NAME_THREE = AccountIdIndexStoreTest.randomBytes(6);

    private static final byte[] ACCOUNT_NAME_FOUR = AccountIdIndexStoreTest.randomBytes(6);

    private static final byte[] ACCOUNT_NAME_FIVE = AccountIdIndexStoreTest.randomBytes(6);

    private static AccountCapsule accountCapsule1;

    private static AccountCapsule accountCapsule2;

    private static AccountCapsule accountCapsule3;

    private static AccountCapsule accountCapsule4;

    static {
        Args.setParam(new String[]{ "--output-directory", AccountIdIndexStoreTest.dbPath }, TEST_CONF);
        AccountIdIndexStoreTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void putAndGet() {
        byte[] address = AccountIdIndexStoreTest.accountIdIndexStore.get(ByteString.copyFrom(AccountIdIndexStoreTest.ACCOUNT_NAME_ONE));
        Assert.assertArrayEquals("putAndGet1", address, AccountIdIndexStoreTest.ACCOUNT_ADDRESS_ONE);
        address = AccountIdIndexStoreTest.accountIdIndexStore.get(ByteString.copyFrom(AccountIdIndexStoreTest.ACCOUNT_NAME_TWO));
        Assert.assertArrayEquals("putAndGet2", address, AccountIdIndexStoreTest.ACCOUNT_ADDRESS_TWO);
        address = AccountIdIndexStoreTest.accountIdIndexStore.get(ByteString.copyFrom(AccountIdIndexStoreTest.ACCOUNT_NAME_THREE));
        Assert.assertArrayEquals("putAndGet3", address, AccountIdIndexStoreTest.ACCOUNT_ADDRESS_THREE);
        address = AccountIdIndexStoreTest.accountIdIndexStore.get(ByteString.copyFrom(AccountIdIndexStoreTest.ACCOUNT_NAME_FOUR));
        Assert.assertArrayEquals("putAndGet4", address, AccountIdIndexStoreTest.ACCOUNT_ADDRESS_FOUR);
        address = AccountIdIndexStoreTest.accountIdIndexStore.get(ByteString.copyFrom(AccountIdIndexStoreTest.ACCOUNT_NAME_FIVE));
        Assert.assertNull("putAndGet4", address);
    }

    @Test
    public void putAndHas() {
        Boolean result = AccountIdIndexStoreTest.accountIdIndexStore.has(AccountIdIndexStoreTest.ACCOUNT_NAME_ONE);
        Assert.assertTrue("putAndGet1", result);
        result = AccountIdIndexStoreTest.accountIdIndexStore.has(AccountIdIndexStoreTest.ACCOUNT_NAME_TWO);
        Assert.assertTrue("putAndGet2", result);
        result = AccountIdIndexStoreTest.accountIdIndexStore.has(AccountIdIndexStoreTest.ACCOUNT_NAME_THREE);
        Assert.assertTrue("putAndGet3", result);
        result = AccountIdIndexStoreTest.accountIdIndexStore.has(AccountIdIndexStoreTest.ACCOUNT_NAME_FOUR);
        Assert.assertTrue("putAndGet4", result);
        result = AccountIdIndexStoreTest.accountIdIndexStore.has(AccountIdIndexStoreTest.ACCOUNT_NAME_FIVE);
        Assert.assertFalse("putAndGet4", result);
    }

    @Test
    public void testCaseInsensitive() {
        byte[] ACCOUNT_NAME = "aABbCcDd_ssd1234".getBytes();
        byte[] ACCOUNT_ADDRESS = AccountIdIndexStoreTest.randomBytes(16);
        AccountCapsule accountCapsule = new AccountCapsule(ByteString.copyFrom(ACCOUNT_ADDRESS), ByteString.copyFrom(ACCOUNT_NAME), AccountType.Normal);
        accountCapsule.setAccountId(ByteString.copyFrom(ACCOUNT_NAME).toByteArray());
        AccountIdIndexStoreTest.accountIdIndexStore.put(accountCapsule);
        Boolean result = AccountIdIndexStoreTest.accountIdIndexStore.has(ACCOUNT_NAME);
        Assert.assertTrue("fail", result);
        byte[] lowerCase = ByteString.copyFromUtf8(ByteString.copyFrom(ACCOUNT_NAME).toStringUtf8().toLowerCase()).toByteArray();
        result = AccountIdIndexStoreTest.accountIdIndexStore.has(lowerCase);
        Assert.assertTrue("lowerCase fail", result);
        byte[] upperCase = ByteString.copyFromUtf8(ByteString.copyFrom(ACCOUNT_NAME).toStringUtf8().toUpperCase()).toByteArray();
        result = AccountIdIndexStoreTest.accountIdIndexStore.has(upperCase);
        Assert.assertTrue("upperCase fail", result);
        Assert.assertNotNull("getLowerCase fail", AccountIdIndexStoreTest.accountIdIndexStore.get(upperCase));
    }
}

