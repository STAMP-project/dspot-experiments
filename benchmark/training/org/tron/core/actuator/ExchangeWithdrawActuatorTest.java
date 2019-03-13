package org.tron.core.actuator;


import Constant.TEST_CONF;
import code.SUCESS;
import com.google.protobuf.ByteString;
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
public class ExchangeWithdrawActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_ExchangeWithdraw_test";

    private static final String ACCOUNT_NAME_FIRST = "ownerF";

    private static final String OWNER_ADDRESS_FIRST;

    private static final String ACCOUNT_NAME_SECOND = "ownerS";

    private static final String OWNER_ADDRESS_SECOND;

    private static final String URL = "https://tron.network";

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ADDRESS_NOACCOUNT;

    private static final String OWNER_ADDRESS_BALANCENOTSUFFIENT;

    static {
        Args.setParam(new String[]{ "--output-directory", ExchangeWithdrawActuatorTest.dbPath }, TEST_CONF);
        ExchangeWithdrawActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
        OWNER_ADDRESS_BALANCENOTSUFFIENT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e06d4271a1ced";
    }

    /**
     * SameTokenName close, first withdraw Exchange,result is success.
     */
    @Test
    public void SameTokenNameCloseSuccessExchangeWithdraw() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(firstTokenId));
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            long id = 1;
            // V1
            ExchangeCapsule exchangeCapsule = ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(id));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule.getCreatorAddress());
            Assert.assertEquals(id, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(0L, exchangeCapsule.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(0L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(id));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule2.getCreatorAddress());
            Assert.assertEquals(id, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            Assert.assertEquals(0L, exchangeCapsule2.getFirstTokenBalance());
            Assert.assertEquals(0L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetMap = accountCapsule.getAssetMap();
            Assert.assertEquals(10000000000L, accountCapsule.getBalance());
            Assert.assertEquals(firstTokenQuant, assetMap.get(firstTokenId).longValue());
            Assert.assertEquals(secondTokenQuant, assetMap.get(secondTokenId).longValue());
            Assert.assertEquals(secondTokenQuant, ret.getExchangeWithdrawAnotherAmount());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * Init close SameTokenName,after init data,open SameTokenName
     */
    @Test
    public void oldNotUpdateSuccessExchangeWithdraw() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(firstTokenId));
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, String.valueOf(1), firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            long id = 1;
            // V1 not update
            ExchangeCapsule exchangeCapsule = ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(id));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule.getCreatorAddress());
            Assert.assertEquals(id, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertNotEquals(0L, exchangeCapsule.getFirstTokenBalance());
            Assert.assertNotEquals(0L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(id));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule2.getCreatorAddress());
            Assert.assertEquals(id, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            Assert.assertEquals(0L, exchangeCapsule2.getFirstTokenBalance());
            Assert.assertEquals(0L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetMap = accountCapsule.getAssetMapV2();
            Assert.assertEquals(10000000000L, accountCapsule.getBalance());
            Assert.assertEquals(firstTokenQuant, assetMap.get(String.valueOf(1)).longValue());
            Assert.assertEquals(secondTokenQuant, assetMap.get(String.valueOf(2)).longValue());
            Assert.assertEquals(secondTokenQuant, ret.getExchangeWithdrawAnotherAmount());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, first withdraw Exchange,result is success.
     */
    @Test
    public void SameTokenNameOpenSuccessExchangeWithdraw() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(firstTokenId));
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1,Data is no longer update
            Assert.assertFalse(ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().has(ByteArray.fromLong(exchangeId)));
            // V2
            ExchangeCapsule exchangeCapsuleV2 = ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsuleV2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsuleV2.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsuleV2.getID());
            Assert.assertEquals(1000000, exchangeCapsuleV2.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(0L, exchangeCapsuleV2.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsuleV2.getSecondTokenId()));
            Assert.assertEquals(0L, exchangeCapsuleV2.getSecondTokenBalance());
            accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetV2Map = accountCapsule.getAssetMapV2();
            Assert.assertEquals(10000000000L, accountCapsule.getBalance());
            Assert.assertEquals(firstTokenQuant, assetV2Map.get(firstTokenId).longValue());
            Assert.assertEquals(secondTokenQuant, assetV2Map.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, second withdraw Exchange,result is success.
     */
    @Test
    public void SameTokenNameCloseSuccessExchangeWithdraw2() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 1000000000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 4000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1
            ExchangeCapsule exchangeCapsule = ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(0L, exchangeCapsule.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(0L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule2.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            // Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule2.getFirstTokenId()));
            // Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule2.getFirstTokenId()));
            Assert.assertEquals(0L, exchangeCapsule2.getFirstTokenBalance());
            // Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule2.getSecondTokenId()));
            Assert.assertEquals(0L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetMap = accountCapsule.getAssetMap();
            Assert.assertEquals((firstTokenQuant + 10000000000L), accountCapsule.getBalance());
            Assert.assertEquals(10000000L, assetMap.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, second withdraw Exchange,result is success.
     */
    @Test
    public void SameTokenNameOpenSuccessExchangeWithdraw2() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 1000000000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 4000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1,Data is no longer update
            Assert.assertFalse(ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().has(ByteArray.fromLong(exchangeId)));
            // V2
            ExchangeCapsule exchangeCapsuleV2 = ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsuleV2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsuleV2.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsuleV2.getID());
            Assert.assertEquals(1000000, exchangeCapsuleV2.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(0L, exchangeCapsuleV2.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsuleV2.getSecondTokenId()));
            Assert.assertEquals(0L, exchangeCapsuleV2.getSecondTokenBalance());
            accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            assetV2Map = accountCapsule.getAssetMapV2();
            Assert.assertEquals((firstTokenQuant + 10000000000L), accountCapsule.getBalance());
            Assert.assertEquals(10000000L, assetV2Map.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void SameTokenNameCloseInvalidAddress() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(firstTokenId));
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_INVALID, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
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
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void SameTokenNameOpenInvalidAddress() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(firstTokenId));
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_INVALID, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
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
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, use AccountStore not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void SameTokenNameCloseNoAccount() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(firstTokenId));
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_NOACCOUNT, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (ExchangeWithdrawActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, use AccountStore not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void SameTokenNameOpenNoAccount() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(firstTokenId));
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_NOACCOUNT, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (ExchangeWithdrawActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, Exchange not exists
     */
    @Test
    public void SameTokenNameCloseExchangeNotExist() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 4;
        String firstTokenId = "abc";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(firstTokenId));
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("Exchange not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Exchange[4] not exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Exchange not exists
     */
    @Test
    public void SameTokenNameOpenExchangeNotExist() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 4;
        String firstTokenId = "123";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 200000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(firstTokenId));
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("Exchange not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Exchange[4] not exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, account is not creator
     */
    @Test
    public void SameTokenNameCloseAccountIsNotCreator() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(("account[a0548794500882809695a8a687866e76d4271a1abc]" + " is not creator"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, account is not creator
     */
    @Test
    public void SameTokenNameOpenAccountIsNotCreator() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), firstTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(("account[a0548794500882809695a8a687866e76d4271a1abc]" + " is not creator"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, token is not in exchange
     */
    @Test
    public void SameTokenNameCloseTokenIsNotInExchange() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "_";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
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
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, token is not in exchange
     */
    @Test
    public void SameTokenNameOpenTokenIsNotInExchange() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "_";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
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
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, Token balance in exchange is equal with 0, the exchange has been closed"
     */
    @Test
    public void SameTokenNameCloseTokenBalanceZero() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsule = ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            exchangeCapsule.setBalance(0, 0);
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().put(exchangeCapsule.createDbKey(), exchangeCapsule);
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
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Token balance in exchange is equal with 0, the exchange has been closed"
     */
    @Test
    public void SameTokenNameOpenTokenBalanceZero() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), firstTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsuleV2 = ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            exchangeCapsuleV2.setBalance(0, 0);
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().put(exchangeCapsuleV2.createDbKey(), exchangeCapsuleV2);
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
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, withdraw token quant must greater than zero
     */
    @Test
    public void SameTokenNameCloseTokenQuantLessThanZero() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = -1L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), 1000L);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("withdraw token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, withdraw token quant must greater than zero
     */
    @Test
    public void SameTokenNameOpenTokenQuantLessThanZero() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = -1L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), 1000L, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("withdraw token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, withdraw another token quant must greater than zero
     */
    @Test
    public void SameTokenNameCloseTnotherTokenQuantLessThanZero() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long quant = 1L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), 1000L);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("withdraw another token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, withdraw another token quant must greater than zero
     */
    @Test
    public void SameTokenNameOpenTnotherTokenQuantLessThanZero() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long quant = 1L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), 1000L, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeWithdrawActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeWithdrawActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("withdraw another token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, Not precise enough
     */
    @Test
    public void SameTokenNameCloseNotPreciseEnough() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long quant = 9991L;
        String secondTokenId = "def";
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Not precise enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        quant = 10001;
        actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
            Assert.assertEquals("Not precise enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Not precise enough
     */
    @Test
    public void SameTokenNameOpenNotPreciseEnough() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long quant = 9991L;
        String secondTokenId = "456";
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Not precise enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        quant = 10001;
        actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
            Assert.assertEquals("Not precise enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, Not precise enough
     */
    @Test
    public void SameTokenNameCloseNotPreciseEnough2() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 3;
        String firstTokenId = "abc";
        long quant = 1L;
        String secondTokenId = "def";
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("withdraw another token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        quant = 11;
        actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Not precise enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Not precise enough
     */
    @Test
    public void SameTokenNameOpenNotPreciseEnough2() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 3;
        String firstTokenId = "123";
        long quant = 1L;
        String secondTokenId = "456";
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("withdraw another token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        quant = 11;
        actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, quant), ExchangeWithdrawActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Not precise enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, exchange balance is not enough
     */
    @Test
    public void SameTokenNameCloseExchangeBalanceIsNotEnough() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 100000001L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(firstTokenId));
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("exchange balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, exchange balance is not enough
     */
    @Test
    public void SameTokenNameOpenExchangeBalanceIsNotEnough() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 100000001L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(firstTokenId));
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("exchange balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, exchange balance is not enough
     */
    @Test
    public void SameTokenNameCloseExchangeBalanceIsNotEnough2() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 1000000000001L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetMap = accountCapsule.getAssetMap();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetMap.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("exchange balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, exchange balance is not enough
     */
    @Test
    public void SameTokenNameOpenExchangeBalanceIsNotEnough2() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 1000000000001L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeWithdrawActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
        Assert.assertEquals(10000000000L, accountCapsule.getBalance());
        Assert.assertEquals(null, assetV2Map.get(secondTokenId));
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeWithdrawActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("exchange balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Invalid param
     * "token id is not a valid number"
     */
    @Test
    public void SameTokenNameOpenInvalidParam() {
        ExchangeWithdrawActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        TransactionResultCapsule ret = new TransactionResultCapsule();
        // token id is not a valid number
        ExchangeWithdrawActuator actuator = new ExchangeWithdrawActuator(getContract(ExchangeWithdrawActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, "abc", 1000), ExchangeWithdrawActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token id is not a valid number", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeWithdrawActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }
}

