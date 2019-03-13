package org.tron.core.actuator;


import Constant.TEST_CONF;
import code.SUCESS;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.ExchangeCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ItemNotFoundException;


@Slf4j
public class ExchangeTransactionActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_ExchangeTransaction_test";

    private static final String ACCOUNT_NAME_FIRST = "ownerF";

    private static final String OWNER_ADDRESS_FIRST;

    private static final String ACCOUNT_NAME_SECOND = "ownerS";

    private static final String OWNER_ADDRESS_SECOND;

    private static final String URL = "https://tron.network";

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ADDRESS_NOACCOUNT;

    private static final String OWNER_ADDRESS_BALANCENOTSUFFIENT;

    static {
        Args.setParam(new String[]{ "--output-directory", ExchangeTransactionActuatorTest.dbPath }, TEST_CONF);
        ExchangeTransactionActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
        OWNER_ADDRESS_BALANCENOTSUFFIENT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e06d4271a1ced";
    }

    /**
     * SameTokenName close,first transaction Exchange,result is success.
     */
    @Test
    public void SameTokenNameCloseSuccessExchangeTransaction() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "_";
        long quant = 100000000L;// use 100 TRX to buy abc

        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get("def"));
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            long firstTokenBalance = exchangeCapsule.getFirstTokenBalance();
            long secondTokenBalance = exchangeCapsule.getSecondTokenBalance();
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(1000000000000L, firstTokenBalance);
            Assert.assertEquals("abc", ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(10000000L, secondTokenBalance);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1
            exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(tokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals((firstTokenBalance + quant), exchangeCapsule.getFirstTokenBalance());
            Assert.assertEquals("abc", ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(9999001L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(exchangeId, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            Assert.assertEquals((firstTokenBalance + quant), exchangeCapsule2.getFirstTokenBalance());
            Assert.assertEquals(9999001L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetMap = accountCapsule.getAssetMap();
            Assert.assertEquals((20000000000L - quant), accountCapsule.getBalance());
            Assert.assertEquals(999L, assetMap.get("abc").longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * Init close SameTokenName,after init data,open SameTokenName
     */
    @Test
    public void oldNotUpdateSuccessExchangeTransaction() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "_";
        long quant = 100000000L;// use 100 TRX to buy abc

        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get("def"));
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        try {
            ExchangeCapsule exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            long firstTokenBalance = exchangeCapsule.getFirstTokenBalance();
            long secondTokenBalance = exchangeCapsule.getSecondTokenBalance();
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(1000000000000L, firstTokenBalance);
            Assert.assertEquals("abc", ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(10000000L, secondTokenBalance);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1 not update
            exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(tokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals("abc", ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertNotEquals((firstTokenBalance + quant), exchangeCapsule.getFirstTokenBalance());
            Assert.assertNotEquals(9999001L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(exchangeId, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            Assert.assertEquals((firstTokenBalance + quant), exchangeCapsule2.getFirstTokenBalance());
            Assert.assertEquals(9999001L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetMap = accountCapsule.getAssetMapV2();
            Assert.assertEquals((20000000000L - quant), accountCapsule.getBalance());
            Assert.assertEquals(999L, assetMap.get("1").longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,first transaction Exchange,result is success.
     */
    @Test
    public void SameTokenNameOpenSuccessExchangeTransaction() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "_";
        long quant = 100000000L;// use 100 TRX to buy abc

        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get("456"));
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeV2Capsule = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeV2Capsule);
            long firstTokenBalance = exchangeV2Capsule.getFirstTokenBalance();
            long secondTokenBalance = exchangeV2Capsule.getSecondTokenBalance();
            Assert.assertEquals(exchangeId, exchangeV2Capsule.getID());
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeV2Capsule.getFirstTokenId()));
            Assert.assertEquals(1000000000000L, firstTokenBalance);
            Assert.assertEquals("123", ByteArray.toStr(exchangeV2Capsule.getSecondTokenId()));
            Assert.assertEquals(10000000L, secondTokenBalance);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1,Data is no longer update
            Assert.assertFalse(ExchangeTransactionActuatorTest.dbManager.getExchangeStore().has(ByteArray.fromLong(exchangeId)));
            // V2
            exchangeV2Capsule = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeV2Capsule);
            Assert.assertEquals(exchangeId, exchangeV2Capsule.getID());
            Assert.assertEquals(1000000, exchangeV2Capsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(tokenId.getBytes(), exchangeV2Capsule.getFirstTokenId()));
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeV2Capsule.getFirstTokenId()));
            Assert.assertEquals((firstTokenBalance + quant), exchangeV2Capsule.getFirstTokenBalance());
            Assert.assertEquals("123", ByteArray.toStr(exchangeV2Capsule.getSecondTokenId()));
            Assert.assertEquals(9999001L, exchangeV2Capsule.getSecondTokenBalance());
            accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetV2Map = accountCapsule.getAssetMapV2();
            Assert.assertEquals((20000000000L - quant), accountCapsule.getBalance());
            Assert.assertEquals(999L, assetV2Map.get("123").longValue());
            Assert.assertEquals(999L, ret.getExchangeReceivedAmount());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,second transaction Exchange,result is success.
     */
    @Test
    public void SameTokenNameCloseSuccessExchangeTransaction2() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            long firstTokenBalance = exchangeCapsule.getFirstTokenBalance();
            long secondTokenBalance = exchangeCapsule.getSecondTokenBalance();
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(100000000L, firstTokenBalance);
            Assert.assertEquals("def", ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(200000000L, secondTokenBalance);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1
            exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(tokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals((firstTokenBalance + quant), exchangeCapsule.getFirstTokenBalance());
            Assert.assertEquals("def", ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(199998001L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(exchangeId, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            // Assert.assertTrue(Arrays.equals(tokenId.getBytes(), exchangeCapsule2.getFirstTokenId()));
            // Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsule2.getFirstTokenId()));
            Assert.assertEquals((firstTokenBalance + quant), exchangeCapsule2.getFirstTokenBalance());
            // Assert.assertEquals("def", ByteArray.toStr(exchangeCapsule2.getSecondTokenId()));
            Assert.assertEquals(199998001L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetMap = accountCapsule.getAssetMap();
            Assert.assertEquals(9000L, assetMap.get("abc").longValue());
            Assert.assertEquals(1999L, assetMap.get("def").longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,second transaction Exchange,result is success.
     */
    @Test
    public void SameTokenNameOpenSuccessExchangeTransaction2() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsuleV2 = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsuleV2);
            long firstTokenBalance = exchangeCapsuleV2.getFirstTokenBalance();
            long secondTokenBalance = exchangeCapsuleV2.getSecondTokenBalance();
            Assert.assertEquals(exchangeId, exchangeCapsuleV2.getID());
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(100000000L, firstTokenBalance);
            Assert.assertEquals("456", ByteArray.toStr(exchangeCapsuleV2.getSecondTokenId()));
            Assert.assertEquals(200000000L, secondTokenBalance);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1,Data is no longer update
            Assert.assertFalse(ExchangeTransactionActuatorTest.dbManager.getExchangeStore().has(ByteArray.fromLong(exchangeId)));
            // V2
            exchangeCapsuleV2 = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsuleV2);
            Assert.assertEquals(exchangeId, exchangeCapsuleV2.getID());
            Assert.assertEquals(1000000, exchangeCapsuleV2.getCreateTime());
            Assert.assertTrue(Arrays.equals(tokenId.getBytes(), exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(tokenId, ByteArray.toStr(exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals((firstTokenBalance + quant), exchangeCapsuleV2.getFirstTokenBalance());
            Assert.assertEquals("456", ByteArray.toStr(exchangeCapsuleV2.getSecondTokenId()));
            Assert.assertEquals(199998001L, exchangeCapsuleV2.getSecondTokenBalance());
            accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetV2Map = accountCapsule.getAssetMapV2();
            Assert.assertEquals(9000L, assetV2Map.get("123").longValue());
            Assert.assertEquals(1999L, assetV2Map.get("456").longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void SameTokenNameCloseInvalidAddress() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_INVALID, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("Invalid address");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void SameTokenNameOpenInvalidAddress() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_INVALID, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("Invalid address");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,use AccountStore not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void SameTokenNameCloseNoAccount() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_NOACCOUNT, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (ExchangeTransactionActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,use AccountStore not exists, result is failed, exception is "account not
     * exists".
     */
    @Test
    public void SameTokenNameOpenNoAccount() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_NOACCOUNT, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (ExchangeTransactionActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,Exchange not exists
     */
    @Test
    public void SameTokenNameCloseExchangeNotExist() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 3;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("Exchange not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Exchange[3] not exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,Exchange not exists
     */
    @Test
    public void SameTokenNameOpenExchangeNotExist() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 3;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("Exchange not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Exchange[3] not exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,token is not in exchange
     */
    @Test
    public void SameTokenNameCloseTokenIsNotInExchange() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "ddd";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token is not in exchange", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,token is not in exchange
     */
    @Test
    public void SameTokenNameOpenTokenIsNotInExchange() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "999";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token is not in exchange", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,Token balance in exchange is equal with 0, the exchange has been closed"
     */
    @Test
    public void SameTokenNameCloseTokenBalanceZero() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            exchangeCapsule.setBalance(0, 0);
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().put(exchangeCapsule.createDbKey(), exchangeCapsule);
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(("Token balance in exchange is equal with 0," + "the exchange has been closed"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,Token balance in exchange is equal with 0, the exchange has been closed"
     */
    @Test
    public void SameTokenNameOpenTokenBalanceZero() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsuleV2 = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            exchangeCapsuleV2.setBalance(0, 0);
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().put(exchangeCapsuleV2.createDbKey(), exchangeCapsuleV2);
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(("Token balance in exchange is equal with 0," + "the exchange has been closed"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,token quant must greater than zero
     */
    @Test
    public void SameTokenNameCloseTokenQuantLessThanZero() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = -1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,token quant must greater than zero
     */
    @Test
    public void SameTokenNameOpenTokenQuantLessThanZero() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = -1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,token balance must less than balanceLimit
     */
    @Test
    public void SameTokenNameCloseTokenBalanceGreaterThanBalanceLimit() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000000000000001L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), 10000);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token balance must less than 1000000000000000", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,token balance must less than balanceLimit
     */
    @Test
    public void SameTokenNameOpenTokenBalanceGreaterThanBalanceLimit() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000000000000001L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), 10000, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token balance must less than 1000000000000000", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,balance is not enough
     */
    @Test
    public void SameTokenNameCloseBalanceNotEnough() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "_";
        long quant = 100000000L;
        String buyTokenId = "abc";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        accountCapsule.setBalance((quant - 1));
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,balance is not enough
     */
    @Test
    public void SameTokenNameOpenBalanceNotEnough() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String tokenId = "_";
        long quant = 100000000L;
        String buyTokenId = "123";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        accountCapsule.setBalance((quant - 1));
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,token balance is not enough
     */
    @Test
    public void SameTokenNameCloseTokenBalanceNotEnough() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), (quant - 1));
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,token balance is not enough
     */
    @Test
    public void SameTokenNameOpenTokenBalanceNotEnough() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), (quant - 1), ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, 1), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close,token required must greater than expected
     */
    @Test
    public void SameTokenNameCloseTokenRequiredNotEnough() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "abc";
        long quant = 1000L;
        String buyTokenId = "def";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(tokenId.getBytes(), quant);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        long expected = 0;
        try {
            ExchangeCapsule exchangeCapsule = ExchangeTransactionActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            expected = exchangeCapsule.transaction(tokenId.getBytes(), quant);
        } catch (ItemNotFoundException e) {
            fail();
        }
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, (expected + 1)), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("should not run here");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token required must greater than expected", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,token required must greater than expected
     */
    @Test
    public void SameTokenNameOpenTokenRequiredNotEnough() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String tokenId = "123";
        long quant = 1000L;
        String buyTokenId = "456";
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeTransactionActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(tokenId.getBytes(), quant, ExchangeTransactionActuatorTest.dbManager);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(20000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(buyTokenId));
        ExchangeTransactionActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        long expected = 0;
        try {
            ExchangeCapsule exchangeCapsuleV2 = ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            expected = exchangeCapsuleV2.transaction(tokenId.getBytes(), quant);
        } catch (ItemNotFoundException e) {
            fail();
        }
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, tokenId, quant, (expected + 1)), ExchangeTransactionActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("should not run here");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token required must greater than expected", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open,invalid param
     * "token id is not a valid number"
     * "token expected must greater than zero"
     */
    @Test
    public void SameTokenNameOpenInvalidParam() {
        ExchangeTransactionActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        long quant = 100000000L;// use 100 TRX to buy abc

        TransactionResultCapsule ret = new TransactionResultCapsule();
        // token id is not a valid number
        ExchangeTransactionActuator actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, "abc", quant, 1), ExchangeTransactionActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("should not run here");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token id is not a valid number", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // token expected must greater than zero
        actuator = new ExchangeTransactionActuator(getContract(ExchangeTransactionActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, "_", quant, 0), ExchangeTransactionActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("should not run here");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token expected must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeTransactionActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }
}

