package org.tron.core.actuator;


import Constant.TEST_CONF;
import code.SUCESS;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.Parameter.ChainConstant;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;


@Slf4j
public class SellStorageActuatorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_sell_storage_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", SellStorageActuatorTest.dbPath }, TEST_CONF);
        SellStorageActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testSellStorage() {
        long currentPool = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        long quant = 2000000000000L;// 2 million trx

        BuyStorageActuator buyStorageactuator = new BuyStorageActuator(getBuyContract(SellStorageActuatorTest.OWNER_ADDRESS, quant), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            buyStorageactuator.validate();
            buyStorageactuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((SellStorageActuatorTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(2694881440L, owner.getStorageLimit());
            Assert.assertEquals((currentReserved - 2694881440L), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals((currentPool + quant), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        long bytes = 2694881440L;
        SellStorageActuator sellStorageActuator = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS, bytes), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret2 = new TransactionResultCapsule();
        try {
            sellStorageActuator.validate();
            sellStorageActuator.execute(ret);
            Assert.assertEquals(ret2.getInstance().getRet(), SUCESS);
            AccountCapsule owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            // TODO: more precise
            Assert.assertEquals(owner.getBalance(), SellStorageActuatorTest.initBalance);
            Assert.assertEquals(0, owner.getStorageLimit());
            Assert.assertEquals(currentReserved, SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals(100000000000000L, SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testSellStorage2() {
        long currentPool = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        long quant = 2000000000000L;// 2 million trx

        BuyStorageActuator buyStorageactuator = new BuyStorageActuator(getBuyContract(SellStorageActuatorTest.OWNER_ADDRESS, quant), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule buyRet = new TransactionResultCapsule();
        try {
            buyStorageactuator.validate();
            buyStorageactuator.execute(buyRet);
            Assert.assertEquals(buyRet.getInstance().getRet(), SUCESS);
            AccountCapsule owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((SellStorageActuatorTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(2694881440L, owner.getStorageLimit());
            Assert.assertEquals((currentReserved - 2694881440L), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals((currentPool + quant), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        long bytes1 = 2694881440L - 1360781717L;// 1 million trx

        long bytes2 = 1360781717L;// 1 million trx

        SellStorageActuator sellStorageActuator1 = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS, bytes1), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret1 = new TransactionResultCapsule();
        SellStorageActuator sellStorageActuator2 = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS, bytes2), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret2 = new TransactionResultCapsule();
        try {
            sellStorageActuator1.validate();
            sellStorageActuator1.execute(ret1);
            Assert.assertEquals(ret1.getInstance().getRet(), SUCESS);
            AccountCapsule owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((SellStorageActuatorTest.initBalance) - 1000000000000L));
            Assert.assertEquals(1360781717L, owner.getStorageLimit());
            Assert.assertEquals((currentReserved - 1360781717L), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals(101000000000000L, SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
            sellStorageActuator2.validate();
            sellStorageActuator2.execute(ret2);
            Assert.assertEquals(ret2.getInstance().getRet(), SUCESS);
            owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), SellStorageActuatorTest.initBalance);
            Assert.assertEquals(0, owner.getStorageLimit());
            long tax = 0L;
            Assert.assertEquals(tax, SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageTax());
            Assert.assertEquals(currentReserved, SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals(100000000000000L, SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    // @Test
    // public void testSellStorageTax() {
    // long currentPool = dbManager.getDynamicPropertiesStore().getTotalStoragePool();
    // long currentReserved = dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
    // Assert.assertEquals(currentPool, 100_000_000_000000L);
    // Assert.assertEquals(currentReserved, 128L * 1024 * 1024 * 1024);
    // 
    // long quant = 2_000_000_000_000L; // 2 million trx
    // BuyStorageActuator buyStorageactuator = new BuyStorageActuator(
    // getBuyContract(OWNER_ADDRESS, quant), dbManager);
    // TransactionResultCapsule ret = new TransactionResultCapsule();
    // try {
    // buyStorageactuator.validate();
    // buyStorageactuator.execute(ret);
    // Assert.assertEquals(ret.getInstance().getRet(), code.SUCESS);
    // AccountCapsule owner =
    // dbManager.getAccountStore().get(ByteArray.fromHexString(OWNER_ADDRESS));
    // 
    // Assert.assertEquals(owner.getBalance(), initBalance - quant
    // - ChainConstant.TRANSFER_FEE);
    // Assert.assertEquals(2694881440L, owner.getStorageLimit());
    // Assert.assertEquals(currentReserved - 2694881440L,
    // dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
    // Assert.assertEquals(currentPool + quant,
    // dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    // } catch (ContractValidateException e) {
    // Assert.assertFalse(e instanceof ContractValidateException);
    // } catch (ContractExeException e) {
    // Assert.assertFalse(e instanceof ContractExeException);
    // }
    // 
    // dbManager.getDynamicPropertiesStore()
    // .saveLatestBlockHeaderTimestamp(365 * 24 * 3600 * 1000L);
    // long bytes = 2694881440L - 269488144L;
    // SellStorageActuator sellStorageActuator = new SellStorageActuator(
    // getContract(OWNER_ADDRESS, bytes), dbManager);
    // TransactionResultCapsule ret2 = new TransactionResultCapsule();
    // try {
    // sellStorageActuator.validate();
    // sellStorageActuator.execute(ret);
    // Assert.assertEquals(ret2.getInstance().getRet(), code.SUCESS);
    // AccountCapsule owner =
    // dbManager.getAccountStore().get(ByteArray.fromHexString(OWNER_ADDRESS));
    // 
    // Assert.assertEquals(owner.getBalance(), 9999796407185160L);
    // Assert.assertEquals(0, owner.getStorageLimit());
    // long tax = 10_000_000_000_000_000L + 100_000_000_000_000L
    // - 9999796407185160L - 100000000000550L; // == 203592814290L
    // Assert.assertEquals(currentReserved,
    // dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
    // Assert.assertEquals(100000000000550L,
    // dbManager.getDynamicPropertiesStore().getTotalStoragePool());
    // Assert.assertEquals(tax,
    // dbManager.getDynamicPropertiesStore().getTotalStorageTax());
    // } catch (ContractValidateException e) {
    // Assert.assertFalse(e instanceof ContractValidateException);
    // } catch (ContractExeException e) {
    // Assert.assertFalse(e instanceof ContractExeException);
    // }
    // }
    @Test
    public void sellLessThanZero() {
        long bytes = -1000000000L;
        SellStorageActuator actuator = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS, bytes), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("bytes must be positive", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void sellLessThan1Trx() {
        long currentPool = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        long quant = 2000000000000L;// 2 million trx

        BuyStorageActuator buyStorageactuator = new BuyStorageActuator(getBuyContract(SellStorageActuatorTest.OWNER_ADDRESS, quant), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            buyStorageactuator.validate();
            buyStorageactuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((SellStorageActuatorTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(2694881440L, owner.getStorageLimit());
            Assert.assertEquals((currentReserved - 2694881440L), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals((currentPool + quant), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        long bytes = 1200L;
        SellStorageActuator sellStorageActuator = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS, bytes), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret2 = new TransactionResultCapsule();
        try {
            sellStorageActuator.validate();
            sellStorageActuator.execute(ret2);
            Assert.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("quantity must be larger than 1TRX,current quantity[900000]", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void sellMoreThanLimit() {
        long currentPool = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool();
        long currentReserved = SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved();
        Assert.assertEquals(currentPool, 100000000000000L);
        Assert.assertEquals(currentReserved, (((128L * 1024) * 1024) * 1024));
        long quant = 2000000000000L;// 2 million trx

        BuyStorageActuator buyStorageactuator = new BuyStorageActuator(getBuyContract(SellStorageActuatorTest.OWNER_ADDRESS, quant), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            buyStorageactuator.validate();
            buyStorageactuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = SellStorageActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SellStorageActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((SellStorageActuatorTest.initBalance) - quant) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(2694881440L, owner.getStorageLimit());
            Assert.assertEquals((currentReserved - 2694881440L), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStorageReserved());
            Assert.assertEquals((currentPool + quant), SellStorageActuatorTest.dbManager.getDynamicPropertiesStore().getTotalStoragePool());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        long bytes = 2694881441L;
        SellStorageActuator sellStorageActuator = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS, bytes), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret2 = new TransactionResultCapsule();
        try {
            sellStorageActuator.validate();
            sellStorageActuator.execute(ret2);
            Assert.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("bytes must be less than currentUnusedStorage[2694881440]", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAddress() {
        long bytes = 2694881440L;
        SellStorageActuator actuator = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ADDRESS_INVALID, bytes), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAccount() {
        long bytes = 2694881440L;
        SellStorageActuator actuator = new SellStorageActuator(getContract(SellStorageActuatorTest.OWNER_ACCOUNT_INVALID, bytes), SellStorageActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (SellStorageActuatorTest.OWNER_ACCOUNT_INVALID)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

