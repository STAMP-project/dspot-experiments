package org.tron.core.capsule;


import Constant.TEST_CONF;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.db.StorageMarket;
import org.tron.core.exception.ItemNotFoundException;


@Slf4j
public class ExchangeCapsuleTest {
    private static Manager dbManager;

    private static StorageMarket storageMarket;

    private static final String dbPath = "output_exchange_capsule_test_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", ExchangeCapsuleTest.dbPath }, TEST_CONF);
        ExchangeCapsuleTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testExchange() {
        long sellBalance = 100000000L;
        long buyBalance = 100000000L;
        byte[] key = ByteArray.fromLong(1);
        ExchangeCapsule exchangeCapsule;
        try {
            exchangeCapsule = ExchangeCapsuleTest.dbManager.getExchangeStore().get(key);
            exchangeCapsule.setBalance(sellBalance, buyBalance);
            long sellQuant = 1000000L;
            byte[] sellID = "abc".getBytes();
            long result = exchangeCapsule.transaction(sellID, sellQuant);
            Assert.assertEquals(990099L, result);
            sellBalance += sellQuant;
            Assert.assertEquals(sellBalance, exchangeCapsule.getFirstTokenBalance());
            buyBalance -= result;
            Assert.assertEquals(buyBalance, exchangeCapsule.getSecondTokenBalance());
            sellQuant = 9000000L;
            long result2 = exchangeCapsule.transaction(sellID, sellQuant);
            Assert.assertEquals(9090909L, (result + result2));
            sellBalance += sellQuant;
            Assert.assertEquals(sellBalance, exchangeCapsule.getFirstTokenBalance());
            buyBalance -= result2;
            Assert.assertEquals(buyBalance, exchangeCapsule.getSecondTokenBalance());
        } catch (ItemNotFoundException e) {
            Assert.fail();
        }
    }
}

