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
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.capsule.ExchangeCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ItemNotFoundException;
import org.tron.protos.Contract.AssetIssueContract;


@Slf4j
public class ExchangeInjectActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_ExchangeInject_test";

    private static final String ACCOUNT_NAME_FIRST = "ownerF";

    private static final String OWNER_ADDRESS_FIRST;

    private static final String ACCOUNT_NAME_SECOND = "ownerS";

    private static final String OWNER_ADDRESS_SECOND;

    private static final String URL = "https://tron.network";

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ADDRESS_NOACCOUNT;

    private static final String OWNER_ADDRESS_BALANCENOTSUFFIENT;

    static {
        Args.setParam(new String[]{ "--output-directory", ExchangeInjectActuatorTest.dbPath }, TEST_CONF);
        ExchangeInjectActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
        OWNER_ADDRESS_BALANCENOTSUFFIENT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e06d4271a1ced";
    }

    /**
     * SameTokenName close, first inject Exchange,result is success.
     */
    @Test
    public void SameTokenNameCloseSuccessExchangeInject() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        AssetIssueCapsule assetIssueCapsule1 = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(firstTokenId.getBytes())).build());
        assetIssueCapsule1.setId(String.valueOf(1L));
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule1.getName().toByteArray(), assetIssueCapsule1);
        AssetIssueCapsule assetIssueCapsule2 = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(secondTokenId.getBytes())).build());
        assetIssueCapsule2.setId(String.valueOf(2L));
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule2.getName().toByteArray(), assetIssueCapsule2);
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(ret.getExchangeInjectAnotherAmount(), secondTokenQuant);
            // V1
            ExchangeCapsule exchangeCapsule = ExchangeInjectActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(300000000L, exchangeCapsule.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(600000000L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsuleV2 = ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsuleV2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsuleV2.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsuleV2.getID());
            Assert.assertEquals(1000000, exchangeCapsuleV2.getCreateTime());
            Assert.assertEquals(300000000L, exchangeCapsuleV2.getFirstTokenBalance());
            Assert.assertEquals(600000000L, exchangeCapsuleV2.getSecondTokenBalance());
            accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            Map<String, Long> assetMap = accountCapsule.getAssetMap();
            Assert.assertEquals(10000000000L, accountCapsule.getBalance());
            Assert.assertEquals(0L, assetMap.get(firstTokenId).longValue());
            Assert.assertEquals(0L, assetMap.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * Init close SameTokenName,after init data,open SameTokenName
     */
    @Test
    public void OldNotUpdateSuccessExchangeInject() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        AssetIssueCapsule assetIssueCapsule1 = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(firstTokenId.getBytes())).setId(String.valueOf(1L)).build());
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule1.createDbKey(), assetIssueCapsule1);
        ExchangeInjectActuatorTest.dbManager.getAssetIssueV2Store().put(assetIssueCapsule1.createDbV2Key(), assetIssueCapsule1);
        AssetIssueCapsule assetIssueCapsule2 = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(secondTokenId.getBytes())).setId(String.valueOf(2L)).build());
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule2.createDbKey(), assetIssueCapsule2);
        ExchangeInjectActuatorTest.dbManager.getAssetIssueV2Store().put(assetIssueCapsule2.createDbV2Key(), assetIssueCapsule2);
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAsset(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAsset(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.addAssetV2(String.valueOf(1L).getBytes(), firstTokenQuant);
        accountCapsule.addAssetV2(String.valueOf(2L).getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, String.valueOf(1L), firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(ret.getExchangeInjectAnotherAmount(), secondTokenQuant);
            // V1
            ExchangeCapsule exchangeCapsule = ExchangeInjectActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertNotEquals(300000000L, exchangeCapsule.getFirstTokenBalance());
            Assert.assertNotEquals(600000000L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsuleV2 = ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsuleV2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsuleV2.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsuleV2.getID());
            Assert.assertEquals(1000000, exchangeCapsuleV2.getCreateTime());
            Assert.assertEquals(300000000L, exchangeCapsuleV2.getFirstTokenBalance());
            Assert.assertEquals(600000000L, exchangeCapsuleV2.getSecondTokenBalance());
            accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            Map<String, Long> assetMap = accountCapsule.getAssetMapV2();
            Assert.assertEquals(10000000000L, accountCapsule.getBalance());
            Assert.assertEquals(0L, assetMap.get(String.valueOf(1)).longValue());
            Assert.assertEquals(0L, assetMap.get(String.valueOf(2)).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, first inject Exchange,result is success.
     */
    @Test
    public void SameTokenNameOpenSuccessExchangeInject() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        AssetIssueCapsule assetIssueCapsule1 = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(firstTokenId.getBytes())).build());
        assetIssueCapsule1.setId(String.valueOf(1L));
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule1.getName().toByteArray(), assetIssueCapsule1);
        AssetIssueCapsule assetIssueCapsule2 = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(secondTokenId.getBytes())).build());
        assetIssueCapsule2.setId(String.valueOf(2L));
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule2.getName().toByteArray(), assetIssueCapsule2);
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), firstTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            long id = 1;
            // V1,Data is no longer update
            Assert.assertFalse(ExchangeInjectActuatorTest.dbManager.getExchangeStore().has(ByteArray.fromLong(id)));
            // V2
            ExchangeCapsule exchangeCapsuleV2 = ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(id));
            Assert.assertNotNull(exchangeCapsuleV2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsuleV2.getCreatorAddress());
            Assert.assertEquals(id, exchangeCapsuleV2.getID());
            Assert.assertEquals(1000000, exchangeCapsuleV2.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsuleV2.getFirstTokenId()));
            Assert.assertEquals(300000000L, exchangeCapsuleV2.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsuleV2.getSecondTokenId()));
            Assert.assertEquals(600000000L, exchangeCapsuleV2.getSecondTokenBalance());
            accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
            Assert.assertEquals(10000000000L, accountCapsule.getBalance());
            Assert.assertEquals(0L, assetV2Map.get(firstTokenId).longValue());
            Assert.assertEquals(0L, assetV2Map.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, second inject Exchange,result is success.
     */
    @Test
    public void SameTokenNameCloseSuccessExchangeInject2() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 100000000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 4000000L;
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(secondTokenId.getBytes())).build());
        assetIssueCapsule.setId(String.valueOf(2L));
        ExchangeInjectActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule.getName().toByteArray(), assetIssueCapsule);
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1
            ExchangeCapsule exchangeCapsule = ExchangeInjectActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsule.getID());
            Assert.assertEquals(1000000, exchangeCapsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeCapsule.getFirstTokenId()));
            Assert.assertEquals(1100000000000L, exchangeCapsule.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeCapsule.getSecondTokenId()));
            Assert.assertEquals(11000000L, exchangeCapsule.getSecondTokenBalance());
            // V2
            ExchangeCapsule exchangeCapsule2 = ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeCapsule2);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeCapsule2.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeCapsule2.getID());
            Assert.assertEquals(1000000, exchangeCapsule2.getCreateTime());
            Assert.assertEquals(1100000000000L, exchangeCapsule2.getFirstTokenBalance());
            Assert.assertEquals(11000000L, exchangeCapsule2.getSecondTokenBalance());
            accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            Map<String, Long> assetMap = accountCapsule.getAssetMap();
            Assert.assertEquals(0L, accountCapsule.getBalance());
            Assert.assertEquals(3000000L, assetMap.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, second inject Exchange,result is success.
     */
    @Test
    public void SameTokenNameOpenSuccessExchangeInject2() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 100000000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 4000000L;
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(AssetIssueContract.newBuilder().setName(ByteString.copyFrom(secondTokenId.getBytes())).build());
        assetIssueCapsule.setId(String.valueOf(2L));
        ExchangeInjectActuatorTest.dbManager.getAssetIssueV2Store().put(assetIssueCapsule.getName().toByteArray(), assetIssueCapsule);
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1,Data is no longer update
            Assert.assertFalse(ExchangeInjectActuatorTest.dbManager.getExchangeStore().has(ByteArray.fromLong(exchangeId)));
            // V2
            ExchangeCapsule exchangeV2Capsule = ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            Assert.assertNotNull(exchangeV2Capsule);
            Assert.assertEquals(ByteString.copyFrom(ownerAddress), exchangeV2Capsule.getCreatorAddress());
            Assert.assertEquals(exchangeId, exchangeV2Capsule.getID());
            Assert.assertEquals(1000000, exchangeV2Capsule.getCreateTime());
            Assert.assertTrue(Arrays.equals(firstTokenId.getBytes(), exchangeV2Capsule.getFirstTokenId()));
            Assert.assertEquals(firstTokenId, ByteArray.toStr(exchangeV2Capsule.getFirstTokenId()));
            Assert.assertEquals(1100000000000L, exchangeV2Capsule.getFirstTokenBalance());
            Assert.assertEquals(secondTokenId, ByteArray.toStr(exchangeV2Capsule.getSecondTokenId()));
            Assert.assertEquals(11000000L, exchangeV2Capsule.getSecondTokenBalance());
            accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
            Map<String, Long> assetV2Map = accountCapsule.getAssetMapV2();
            Assert.assertEquals(0L, accountCapsule.getBalance());
            Assert.assertEquals(3000000L, assetV2Map.get(secondTokenId).longValue());
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void SameTokenNameCloseInvalidAddress() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_INVALID, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void SameTokenNameOpenInvalidAddress() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_INVALID, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, use AccountStore not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void SameTokenNameCloseNoAccount() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_NOACCOUNT, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (ExchangeInjectActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, use AccountStore not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void SameTokenNameOpenNoAccount() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_NOACCOUNT, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (ExchangeInjectActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, Exchange not exists
     */
    @Test
    public void SameTokenNameCloseExchangeNotExist() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 3;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Exchange not exists
     */
    @Test
    public void SameTokenNameOpenExchangeNotExist() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 3;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), firstTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, account[" + readableOwnerAddress + "] is not creator
     */
    @Test
    public void SameTokenNameCloseAccountIsNotCreator() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, account[" + readableOwnerAddress + "] is not creator
     */
    @Test
    public void SameTokenNameOpenAccountIsNotCreator() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_SECOND);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), firstTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_SECOND, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, token is not in exchange
     */
    @Test
    public void SameTokenNameCloseTokenIsNotInExchange() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "_";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token id is not in exchange", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, token is not in exchange
     */
    @Test
    public void SameTokenNameOpenTokenIsNotInExchange() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "_";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("token id is not in exchange", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, Token balance in exchange is equal with 0, the exchange has been closed"
     */
    @Test
    public void SameTokenNameCloseTokenBalanceZero() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), firstTokenQuant);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsule = ExchangeInjectActuatorTest.dbManager.getExchangeStore().get(ByteArray.fromLong(exchangeId));
            exchangeCapsule.setBalance(0, 0);
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().put(exchangeCapsule.createDbKey(), exchangeCapsule);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, Token balance in exchange is equal with 0, the exchange has been closed"
     */
    @Test
    public void SameTokenNameOpenTokenBalanceZero() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), firstTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            ExchangeCapsule exchangeCapsuleV2 = ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().get(ByteArray.fromLong(exchangeId));
            exchangeCapsuleV2.setBalance(0, 0);
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().put(exchangeCapsuleV2.createDbKey(), exchangeCapsuleV2);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, injected token quant must greater than zero
     */
    @Test
    public void SameTokenNameCloseTokenQuantLessThanZero() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = -1L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), 1000L);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("injected token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, injected token quant must greater than zero
     */
    @Test
    public void SameTokenNameOpenTokenQuantLessThanZero() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = -1L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), 1000L, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("injected token quant must greater than zero", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, "the calculated token quant  must be greater than 0"
     */
    @Test
    public void SameTokenNameCloseCalculatedTokenQuantLessThanZero() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 100L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("the calculated token quant  must be greater than 0", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, "the calculated token quant  must be greater than 0"
     */
    @Test
    public void SameTokenNameOpenCalculatedTokenQuantLessThanZero() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 100L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("the calculated token quant  must be greater than 0", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, token balance must less than balanceLimit
     */
    @Test
    public void SameTokenNameCloseTokenBalanceGreaterThanBalanceLimit() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 1000000000000001L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, token balance must less than balanceLimit
     */
    @Test
    public void SameTokenNameOpenTokenBalanceGreaterThanBalanceLimit() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 1000000000000001L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(firstTokenQuant);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, balance is not enough
     */
    @Test
    public void SameTokenNameCloseBalanceNotEnough() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance((firstTokenQuant - 1));
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, balance is not enough
     */
    @Test
    public void SameTokenNameOpenBalanceNotEnough() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String firstTokenId = "_";
        long firstTokenQuant = 100000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance((firstTokenQuant - 1));
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, first token balance is not enough
     */
    @Test
    public void SameTokenNameCloseTokenBalanceNotEnough() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), (firstTokenQuant - 1));
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, first token balance is not enough
     */
    @Test
    public void SameTokenNameOpenTokenBalanceNotEnough() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), (firstTokenQuant - 1), ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, firstTokenId, firstTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, balance is not enough2
     */
    @Test
    public void SameTokenNameCloseBalanceNotEnough2() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 2;
        String secondTokenId = "def";
        long secondTokenQuant = 4000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(399000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, secondTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, balance is not enough2
     */
    @Test
    public void SameTokenNameOpenBalanceNotEnough2() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 2;
        String secondTokenId = "456";
        long secondTokenQuant = 4000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(399000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, secondTokenQuant), ExchangeInjectActuatorTest.dbManager);
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName close, first token balance is not enough
     */
    @Test
    public void SameTokenNameCloseAnotherTokenBalanceNotEnough() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        InitExchangeBeforeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "abc";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "def";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmount(firstTokenId.getBytes(), (firstTokenQuant - 1));
        accountCapsule.addAssetAmount(secondTokenId.getBytes(), secondTokenQuant);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, secondTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("another token balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, first token balance is not enough
     */
    @Test
    public void SameTokenNameOpenAnotherTokenBalanceNotEnough() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        String firstTokenId = "123";
        long firstTokenQuant = 200000000L;
        String secondTokenId = "456";
        long secondTokenQuant = 400000000L;
        byte[] ownerAddress = ByteArray.fromHexString(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST);
        AccountCapsule accountCapsule = ExchangeInjectActuatorTest.dbManager.getAccountStore().get(ownerAddress);
        accountCapsule.addAssetAmountV2(firstTokenId.getBytes(), (firstTokenQuant - 1), ExchangeInjectActuatorTest.dbManager);
        accountCapsule.addAssetAmountV2(secondTokenId.getBytes(), secondTokenQuant, ExchangeInjectActuatorTest.dbManager);
        accountCapsule.setBalance(10000000000L);
        ExchangeInjectActuatorTest.dbManager.getAccountStore().put(ownerAddress, accountCapsule);
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, secondTokenId, secondTokenQuant), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            fail();
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("another token balance is not enough", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }

    /**
     * SameTokenName open, invalid param
     * "token id is not a valid number"
     */
    @Test
    public void SameTokenNameOpenInvalidParam() {
        ExchangeInjectActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        InitExchangeSameTokenNameActive();
        long exchangeId = 1;
        ExchangeInjectActuator actuator = new ExchangeInjectActuator(getContract(ExchangeInjectActuatorTest.OWNER_ADDRESS_FIRST, exchangeId, "abc", 1000), ExchangeInjectActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
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
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeStore().delete(ByteArray.fromLong(2L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(1L));
            ExchangeInjectActuatorTest.dbManager.getExchangeV2Store().delete(ByteArray.fromLong(2L));
        }
    }
}

