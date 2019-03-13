package org.tron.core.actuator;


import ByteString.EMPTY;
import Constant.TEST_CONF;
import Contract.AssetIssueContract;
import Parameter.ForkBlockVersionConsts.ENERGY_LIMIT;
import code.SUCESS;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Contract;
import org.tron.protos.Contract.AssetIssueContract.FrozenSupply;
import org.tron.protos.Protocol.AccountType;


@Slf4j
public class AssetIssueActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_assetIssue_test";

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_SECOND;

    private static final String NAME = "trx-my";

    private static final long TOTAL_SUPPLY = 10000L;

    private static final int TRX_NUM = 10000;

    private static final int NUM = 100000;

    private static final String DESCRIPTION = "myCoin";

    private static final String URL = "tron-my.com";

    private static final String ASSET_NAME_SECOND = "asset_name2";

    private static long now = 0;

    private static long startTime = 0;

    private static long endTime = 0;

    static {
        Args.setParam(new String[]{ "--output-directory", AssetIssueActuatorTest.dbPath }, TEST_CONF);
        AssetIssueActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049150";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
    }

    /**
     * SameTokenName close, asset issue success
     */
    @Test
    public void SameTokenNameCloseAssetIssueSuccess() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        AssetIssueActuator actuator = new AssetIssueActuator(getContract(), AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        Long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            // check V1
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME).toByteArray());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(6, assetIssueCapsule.getPrecision());
            Assert.assertEquals(AssetIssueActuatorTest.NUM, assetIssueCapsule.getNum());
            Assert.assertEquals(AssetIssueActuatorTest.TRX_NUM, assetIssueCapsule.getTrxNum());
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
            // check V2
            long tokenIdNum = AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            AssetIssueCapsule assetIssueCapsuleV2 = AssetIssueActuatorTest.dbManager.getAssetIssueV2Store().get(ByteArray.fromString(String.valueOf(tokenIdNum)));
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(0, assetIssueCapsuleV2.getPrecision());
            Assert.assertEquals(AssetIssueActuatorTest.NUM, assetIssueCapsuleV2.getNum());
            Assert.assertEquals(AssetIssueActuatorTest.TRX_NUM, assetIssueCapsuleV2.getTrxNum());
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenIdNum)).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * Init close SameTokenName,after init data,open SameTokenName
     */
    @Test
    public void oldNotUpdateAssetIssueSuccess() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        AssetIssueActuator actuator = new AssetIssueActuator(getContract(), AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        Long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            // V1,Data is no longer update
            Assert.assertFalse(AssetIssueActuatorTest.dbManager.getAssetIssueStore().has(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME).toByteArray()));
            // check V2
            long tokenIdNum = AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            AssetIssueCapsule assetIssueCapsuleV2 = AssetIssueActuatorTest.dbManager.getAssetIssueV2Store().get(ByteArray.fromString(String.valueOf(tokenIdNum)));
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(6, assetIssueCapsuleV2.getPrecision());
            Assert.assertEquals(AssetIssueActuatorTest.NUM, assetIssueCapsuleV2.getNum());
            Assert.assertEquals(AssetIssueActuatorTest.TRX_NUM, assetIssueCapsuleV2.getTrxNum());
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenIdNum)).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * SameTokenName open, asset issue success
     */
    @Test
    public void SameTokenNameOpenAssetIssueSuccess() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        AssetIssueActuator actuator = new AssetIssueActuator(getContract(), AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        Long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            // V1,Data is no longer update
            Assert.assertFalse(AssetIssueActuatorTest.dbManager.getAssetIssueStore().has(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME).toByteArray()));
            // V2
            long tokenIdNum = AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            byte[] assertKey = ByteArray.fromString(String.valueOf(tokenIdNum));
            AssetIssueCapsule assetIssueCapsuleV2 = AssetIssueActuatorTest.dbManager.getAssetIssueV2Store().get(assertKey);
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(6, assetIssueCapsuleV2.getPrecision());
            Assert.assertEquals(AssetIssueActuatorTest.NUM, assetIssueCapsuleV2.getNum());
            Assert.assertEquals(AssetIssueActuatorTest.TRX_NUM, assetIssueCapsuleV2.getTrxNum());
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenIdNum)).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * Total supply must greater than zero.Else can't asset issue and balance do not change.
     */
    @Test
    public void negativeTotalSupplyTest() {
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply((-(AssetIssueActuatorTest.TOTAL_SUPPLY))).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("TotalSupply must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * Total supply must greater than zero.Else can't asset issue and balance do not change.
     */
    @Test
    public void zeroTotalSupplyTest() {
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(0).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("TotalSupply must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Trx num must greater than zero.Else can't asset issue and balance do not change. */
    @Test
    public void negativeTrxNumTest() {
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum((-(AssetIssueActuatorTest.TRX_NUM))).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("TrxNum must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Trx num must greater than zero.Else can't asset issue and balance do not change. */
    @Test
    public void zeroTrxNumTest() {
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(0).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("TrxNum must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Num must greater than zero.Else can't asset issue and balance do not change. */
    @Test
    public void negativeNumTest() {
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum((-(AssetIssueActuatorTest.NUM))).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Num must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Trx num must greater than zero.Else can't asset issue and balance do not change. */
    @Test
    public void zeroNumTest() {
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(0).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Num must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Asset name length must between 1 to 32 and can not contain space and other unreadable character, and can not contain chinese characters. */
    @Test
    public void assetNameTest() {
        long nowTime = new Date().getTime();
        // Empty name, throw exception
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(EMPTY).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid assetName", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // Too long name, throw exception. Max long is 32.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8("testname0123456789abcdefghijgklmo")).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid assetName", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // Contain space, throw exception. Every character need readable .
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8("t e")).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid assetName", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // Contain chinese character, throw exception.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFrom(ByteArray.fromHexString("E6B58BE8AF95"))).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid assetName", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // 32 byte readable character just ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8("testname0123456789abcdefghijgklm")).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get("testname0123456789abcdefghijgklm".getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get("testname0123456789abcdefghijgklm").longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        createCapsule();
        // 1 byte readable character ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8("0")).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get("0".getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get("0").longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Url length must between 1 to 256. */
    @Test
    public void urlTest() {
        long nowTime = new Date().getTime();
        // Empty url, throw exception
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(EMPTY).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid url", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        String url256Bytes = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        // Too long url, throw exception. Max long is 256.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8((url256Bytes + "0"))).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid url", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // 256 byte readable character just ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(url256Bytes)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(AssetIssueActuatorTest.NAME.getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        createCapsule();
        // 1 byte url.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8("0")).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(AssetIssueActuatorTest.NAME.getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        createCapsule();
        // 1 byte space ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(" ")).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(AssetIssueActuatorTest.NAME.getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Description length must less than 200. */
    @Test
    public void descriptionTest() {
        long nowTime = new Date().getTime();
        String description200Bytes = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef01234567";
        // Too long description, throw exception. Max long is 200.
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8((description200Bytes + "0"))).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid description", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // 200 bytes character just ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(description200Bytes)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(AssetIssueActuatorTest.NAME.getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        createCapsule();
        // Empty description is ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(EMPTY).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(AssetIssueActuatorTest.NAME.getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        createCapsule();
        // 1 byte space ok.
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(" ")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(AssetIssueActuatorTest.NAME.getBytes());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMap().get(AssetIssueActuatorTest.NAME).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /* Test FrozenSupply, 1. frozen_amount must greater than zero. */
    @Test
    public void frozenTest() {
        // frozen_amount = 0 throw exception.
        FrozenSupply frozenSupply = FrozenSupply.newBuilder().setFrozenDays(1).setFrozenAmount(0).build();
        long nowTime = new Date().getTime();
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addFrozenSupply(frozenSupply).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Frozen supply must be greater than 0!", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // frozen_amount < 0 throw exception.
        frozenSupply = FrozenSupply.newBuilder().setFrozenDays(1).setFrozenAmount((-1)).build();
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addFrozenSupply(frozenSupply).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Frozen supply must be greater than 0!", e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        long minFrozenSupplyTime = AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getMinFrozenSupplyTime();
        long maxFrozenSupplyTime = AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getMaxFrozenSupplyTime();
        // FrozenDays = 0 throw exception.
        frozenSupply = FrozenSupply.newBuilder().setFrozenDays(0).setFrozenAmount(1).build();
        nowTime = new Date().getTime();
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addFrozenSupply(frozenSupply).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("frozenDuration must be less than " + maxFrozenSupplyTime) + " days ") + "and more than ") + minFrozenSupplyTime) + " days"), e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // FrozenDays < 0 throw exception.
        frozenSupply = FrozenSupply.newBuilder().setFrozenDays((-1)).setFrozenAmount(1).build();
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addFrozenSupply(frozenSupply).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("frozenDuration must be less than " + maxFrozenSupplyTime) + " days ") + "and more than ") + minFrozenSupplyTime) + " days"), e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // FrozenDays >  maxFrozenSupplyTime throw exception.
        frozenSupply = FrozenSupply.newBuilder().setFrozenDays((maxFrozenSupplyTime + 1)).setFrozenAmount(1).build();
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addFrozenSupply(frozenSupply).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("frozenDuration must be less than " + maxFrozenSupplyTime) + " days ") + "and more than ") + minFrozenSupplyTime) + " days"), e.getMessage());
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            AssetIssueCapsule assetIssueCapsule = AssetIssueActuatorTest.dbManager.getAssetIssueStore().get(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            Assert.assertEquals(owner.getBalance(), AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), blackholeBalance);
            Assert.assertNull(assetIssueCapsule);
            Assert.assertNull(owner.getInstance().getAssetMap().get(AssetIssueActuatorTest.NAME));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // frozen_amount = 1 and  frozenDays = 1 is OK
        frozenSupply = FrozenSupply.newBuilder().setFrozenDays(1).setFrozenAmount(1).build();
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addFrozenSupply(frozenSupply).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        try {
            actuator.validate();
            actuator.execute(ret);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * 1. start time should not be null 2. end time should not be null 3. start time >=
     * getHeadBlockTimeStamp 4. start time < end time
     */
    @Test
    public void issueTimeTest() {
        // empty start time will throw exception
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Start time should be not empty", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // empty end time will throw exception
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("End time should be not empty", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // startTime == now, throw exception
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.now).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Start time should be greater than HeadBlockTime", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // startTime < now, throw exception
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(((AssetIssueActuatorTest.now) - 1)).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Start time should be greater than HeadBlockTime", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // endTime == startTime, throw exception
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.startTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("End time should be greater than start time", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // endTime < startTime, throw exception
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.endTime).setEndTime(AssetIssueActuatorTest.startTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("End time should be greater than start time", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // right issue, will not throw exception
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            AccountCapsule account = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(account.getAssetIssuedName().toStringUtf8(), AssetIssueActuatorTest.NAME);
            Assert.assertEquals(account.getAssetMap().size(), 1);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * an account should issue asset only once
     */
    @Test
    public void assetIssueNameTest() {
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.ASSET_NAME_SECOND)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("An account can only issue one asset", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.ASSET_NAME_SECOND));
        }
    }

    @Test
    public void assetIssueTRXNameTest() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8("TRX")).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("assetName can't be trx", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.ASSET_NAME_SECOND));
        }
    }

    @Test
    public void frozenListSizeTest() {
        this.dbManager.getDynamicPropertiesStore().saveMaxFrozenSupplyNumber(3);
        List<FrozenSupply> frozenList = new ArrayList();
        for (int i = 0; i < ((this.dbManager.getDynamicPropertiesStore().getMaxFrozenSupplyNumber()) + 2); i++) {
            frozenList.add(FrozenSupply.newBuilder().setFrozenAmount(10).setFrozenDays(3).build());
        }
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addAllFrozenSupply(frozenList).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Frozen supply list length is too long", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    @Test
    public void frozenSupplyMoreThanTotalSupplyTest() {
        this.dbManager.getDynamicPropertiesStore().saveMaxFrozenSupplyNumber(3);
        List<FrozenSupply> frozenList = new ArrayList();
        frozenList.add(FrozenSupply.newBuilder().setFrozenAmount(((AssetIssueActuatorTest.TOTAL_SUPPLY) + 1)).setFrozenDays(3).build());
        Any contract = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(AssetIssueActuatorTest.startTime).setEndTime(AssetIssueActuatorTest.endTime).setDescription(ByteString.copyFromUtf8("description")).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).addAllFrozenSupply(frozenList).build());
        AssetIssueActuator actuator = new AssetIssueActuator(contract, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Frozen supply cannot exceed total supply", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * SameTokenName close, Invalid ownerAddress
     */
    @Test
    public void SameTokenNameCloseInvalidOwnerAddress() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        long nowTime = new Date().getTime();
        Any any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString("12312315345345"))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).build());
        AssetIssueActuator actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid ownerAddress", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * SameTokenName open, check invalid precision
     */
    @Test
    public void SameTokenNameCloseInvalidPrecision() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        long nowTime = new Date().getTime();
        Any any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setPrecision(7).build());
        AssetIssueActuator actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        byte[] stats = new byte[27];
        Arrays.fill(stats, ((byte) (1)));
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().statsByVersion(ENERGY_LIMIT, stats);
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("precision cannot exceed 6", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * SameTokenName close, Invalid abbreviation for token
     */
    @Test
    public void SameTokenNameCloseInvalidAddr() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        long nowTime = new Date().getTime();
        Any any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setAbbr(ByteString.copyFrom(ByteArray.fromHexString("a0299f3db80a24123b20a254b89ce639d59132f157f13"))).setPrecision(4).build());
        AssetIssueActuator actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        byte[] stats = new byte[27];
        Arrays.fill(stats, ((byte) (1)));
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().statsByVersion(ENERGY_LIMIT, stats);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid abbreviation for token", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * repeat issue assert name,
     */
    @Test
    public void IssueSameTokenNameAssert() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        String ownerAddress = "a08beaa1a8e2d45367af7bae7c49009876a4fa4301";
        long id = (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum()) + 1;
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveTokenIdNum(id);
        Contract.AssetIssueContract assetIssueContract = AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(ownerAddress))).setName(ByteString.copyFrom(ByteArray.fromString(AssetIssueActuatorTest.NAME))).setId(Long.toString(id)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(1).setEndTime(100).setVoteScore(2).setDescription(ByteString.copyFrom(ByteArray.fromString(AssetIssueActuatorTest.DESCRIPTION))).setUrl(ByteString.copyFrom(ByteArray.fromString(AssetIssueActuatorTest.URL))).build();
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(assetIssueContract);
        AssetIssueActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule.createDbKey(), assetIssueCapsule);
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFrom(ByteArray.fromHexString(ownerAddress)), ByteString.copyFromUtf8("owner11"), AccountType.AssetIssue);
        ownerCapsule.addAsset(AssetIssueActuatorTest.NAME.getBytes(), 1000L);
        AssetIssueActuatorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        AssetIssueActuator actuator = new AssetIssueActuator(getContract(), AssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        Long blackholeBalance = AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance();
        // SameTokenName not active, same assert name, should failure
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Token exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // SameTokenName active, same assert name,should success
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
            long tokenIdNum = AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            AssetIssueCapsule assetIssueCapsuleV2 = AssetIssueActuatorTest.dbManager.getAssetIssueV2Store().get(ByteArray.fromString(String.valueOf(tokenIdNum)));
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(owner.getBalance(), 0L);
            Assert.assertEquals(AssetIssueActuatorTest.dbManager.getAccountStore().getBlackhole().getBalance(), (blackholeBalance + (AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee())));
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenIdNum)).longValue(), AssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * SameTokenName close, check invalid param
     * "PublicFreeAssetNetUsage must be 0!"
     * "Invalid FreeAssetNetLimit"
     * "Invalid PublicFreeAssetNetLimit"
     * "Account not exists"
     * "No enough balance for fee!"
     */
    @Test
    public void SameTokenNameCloseInvalidparam() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        long nowTime = new Date().getTime();
        byte[] stats = new byte[27];
        Arrays.fill(stats, ((byte) (1)));
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().statsByVersion(ENERGY_LIMIT, stats);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        // PublicFreeAssetNetUsage must be 0!
        Any any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setPrecision(3).setPublicFreeAssetNetUsage(100).build());
        AssetIssueActuator actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("PublicFreeAssetNetUsage must be 0!", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // Invalid FreeAssetNetLimit
        any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setPrecision(3).setFreeAssetNetLimit((-10)).build());
        actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid FreeAssetNetLimit", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // Invalid PublicFreeAssetNetLimit
        any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setPrecision(3).setPublicFreeAssetNetLimit((-10)).build());
        actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid PublicFreeAssetNetLimit", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }

    /**
     * SameTokenName close, account not good
     * "Account not exists"
     * "No enough balance for fee!"
     */
    @Test
    public void SameTokenNameCloseInvalidAccount() {
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        long nowTime = new Date().getTime();
        byte[] stats = new byte[27];
        Arrays.fill(stats, ((byte) (1)));
        AssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().statsByVersion(ENERGY_LIMIT, stats);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        // No enough balance for fee!
        Any any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setPrecision(3).build());
        AssetIssueActuator actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        AccountCapsule owner = AssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(1000);
        AssetIssueActuatorTest.dbManager.getAccountStore().put(owner.createDbKey(), owner);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("No enough balance for fee!", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
        // Account not exists
        AssetIssueActuatorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS));
        any = Any.pack(AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(AssetIssueActuatorTest.OWNER_ADDRESS))).setName(ByteString.copyFromUtf8(AssetIssueActuatorTest.NAME)).setTotalSupply(AssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(AssetIssueActuatorTest.TRX_NUM).setNum(AssetIssueActuatorTest.NUM).setStartTime(nowTime).setEndTime((nowTime + ((24 * 3600) * 1000))).setDescription(ByteString.copyFromUtf8(AssetIssueActuatorTest.DESCRIPTION)).setUrl(ByteString.copyFromUtf8(AssetIssueActuatorTest.URL)).setPrecision(3).build());
        actuator = new AssetIssueActuator(any, AssetIssueActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Account not exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            AssetIssueActuatorTest.dbManager.getAssetIssueStore().delete(ByteArray.fromString(AssetIssueActuatorTest.NAME));
        }
    }
}

