package org.tron.core;


import Constant.TEST_CONF;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.Parameter.ChainConstant;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.db.StorageMarket;


@Slf4j
public class StorageMarketTest {
    private static Manager dbManager;

    private static StorageMarket storageMarket;

    private static final String dbPath = "output_storage_market_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", StorageMarketTest.dbPath }, TEST_CONF);
        StorageMarketTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testBuyStorage() {
        long currentPool = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        AccountCapsule owner = StorageMarketTest.dbManager.getAccountStore().get(ByteArray.fromHexString(StorageMarketTest.OWNER_ADDRESS));
        long quant = 2000000000000L;// 2 million trx

        StorageMarketTest.storageMarket.buyStorage(owner, quant);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(2694881440L, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - 2694881440L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + quant), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    }

    @Test
    public void testBuyStorage2() {
        long currentPool = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        AccountCapsule owner = StorageMarketTest.dbManager.getAccountStore().get(ByteArray.fromHexString(StorageMarketTest.OWNER_ADDRESS));
        long quant = 1000000000000L;// 1 million trx

        StorageMarketTest.storageMarket.buyStorage(owner, quant);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(1360781717L, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - 1360781717L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + quant), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        StorageMarketTest.storageMarket.buyStorage(owner, quant);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - (2 * quant)) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(2694881439L, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - 2694881439L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + (2 * quant)), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    }

    @Test
    public void testBuyStorageBytes() {
        long currentPool = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        AccountCapsule owner = StorageMarketTest.dbManager.getAccountStore().get(ByteArray.fromHexString(StorageMarketTest.OWNER_ADDRESS));
        long bytes = 2694881440L;// 2 million trx

        StorageMarketTest.storageMarket.buyStorageBytes(owner, bytes);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - 2000000000000L) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(bytes, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - bytes), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + 2000000000000L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    }

    @Test
    public void testBuyStorageBytes2() {
        long currentPool = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        AccountCapsule owner = StorageMarketTest.dbManager.getAccountStore().get(ByteArray.fromHexString(StorageMarketTest.OWNER_ADDRESS));
        long bytes1 = 1360781717L;
        StorageMarketTest.storageMarket.buyStorageBytes(owner, bytes1);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - 1000000000000L) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(bytes1, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - bytes1), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + 1000000000000L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        long bytes2 = 1334099723L;
        StorageMarketTest.storageMarket.buyStorageBytes(owner, bytes2);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - (2 * 1000000000000L)) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals((bytes1 + bytes2), owner.getStorageLimit());
        Assert.assertEquals((currentReserved - (bytes1 + bytes2)), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + (2 * 1000000000000L)), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    }

    @Test
    public void testSellStorage() {
        long currentPool = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        AccountCapsule owner = StorageMarketTest.dbManager.getAccountStore().get(ByteArray.fromHexString(StorageMarketTest.OWNER_ADDRESS));
        long quant = 2000000000000L;// 2 million trx

        StorageMarketTest.storageMarket.buyStorage(owner, quant);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(2694881440L, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - 2694881440L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + quant), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        long bytes = 2694881440L;
        StorageMarketTest.storageMarket.sellStorage(owner, bytes);
        Assert.assertEquals(owner.getBalance(), StorageMarketTest.initBalance);
        Assert.assertEquals(0, owner.getStorageLimit());
        Assert.assertEquals(currentReserved, StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals(100000000000000L, StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    }

    @Test
    public void testSellStorage2() {
        long currentPool = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        AccountCapsule owner = StorageMarketTest.dbManager.getAccountStore().get(ByteArray.fromHexString(StorageMarketTest.OWNER_ADDRESS));
        long quant = 2000000000000L;// 2 million trx

        StorageMarketTest.storageMarket.buyStorage(owner, quant);
        Assert.assertEquals(owner.getBalance(), (((StorageMarketTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
        Assert.assertEquals(2694881440L, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - 2694881440L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + quant), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        long bytes1 = 2694881440L - 1360781717L;// 1 million trx

        long bytes2 = 1360781717L;// 1 million trx

        StorageMarketTest.storageMarket.sellStorage(owner, bytes1);
        Assert.assertEquals(owner.getBalance(), ((StorageMarketTest.initBalance) - 1000000000000L));
        Assert.assertEquals(1360781717L, owner.getStorageLimit());
        Assert.assertEquals((currentReserved - 1360781717L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals((currentPool + 1000000000000L), StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        StorageMarketTest.storageMarket.sellStorage(owner, bytes2);
        Assert.assertEquals(owner.getBalance(), StorageMarketTest.initBalance);
        Assert.assertEquals(0, owner.getStorageLimit());
        Assert.assertEquals(currentReserved, StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
        Assert.assertEquals(currentPool, StorageMarketTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    }
}

