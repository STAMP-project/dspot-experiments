package org.tron.core.actuator;


import ByteString.EMPTY;
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
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Account.Frozen;


@Slf4j
public class UnfreezeAssetActuatorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_unfreeze_asset_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000L;

    private static final long frozenBalance = 1000000000L;

    private static final String assetName = "testCoin";

    private static final String assetID = "123456";

    static {
        Args.setParam(new String[]{ "--output-directory", UnfreezeAssetActuatorTest.dbPath }, TEST_CONF);
        UnfreezeAssetActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    /**
     * SameTokenName close, Unfreeze assert success.
     */
    @Test
    public void SameTokenNameCloseUnfreezeAsset() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        long now = System.currentTimeMillis();
        UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        Account account = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS)).getInstance();
        Frozen newFrozen0 = Frozen.newBuilder().setFrozenBalance(UnfreezeAssetActuatorTest.frozenBalance).setExpireTime(now).build();
        Frozen newFrozen1 = Frozen.newBuilder().setFrozenBalance(((UnfreezeAssetActuatorTest.frozenBalance) + 1)).setExpireTime((now + 600000)).build();
        account = account.toBuilder().addFrozenSupply(newFrozen0).addFrozenSupply(newFrozen1).build();
        AccountCapsule accountCapsule = new AccountCapsule(account);
        UnfreezeAssetActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS));
            // V1
            Assert.assertEquals(owner.getAssetMap().get(UnfreezeAssetActuatorTest.assetName).longValue(), UnfreezeAssetActuatorTest.frozenBalance);
            // V2
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), UnfreezeAssetActuatorTest.frozenBalance);
            Assert.assertEquals(owner.getFrozenSupplyCount(), 1);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName active, Unfreeze assert success.
     */
    @Test
    public void SameTokenNameActiveUnfreezeAsset() {
        createAssertSameTokenNameActive();
        long tokenId = UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        long now = System.currentTimeMillis();
        UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        Account account = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS)).getInstance();
        Frozen newFrozen0 = Frozen.newBuilder().setFrozenBalance(UnfreezeAssetActuatorTest.frozenBalance).setExpireTime(now).build();
        Frozen newFrozen1 = Frozen.newBuilder().setFrozenBalance(((UnfreezeAssetActuatorTest.frozenBalance) + 1)).setExpireTime((now + 600000)).build();
        account = account.toBuilder().addFrozenSupply(newFrozen0).addFrozenSupply(newFrozen1).build();
        AccountCapsule accountCapsule = new AccountCapsule(account);
        UnfreezeAssetActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS));
            // V1 assert not exist
            Assert.assertNull(owner.getAssetMap().get(UnfreezeAssetActuatorTest.assetName));
            // V2
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), UnfreezeAssetActuatorTest.frozenBalance);
            Assert.assertEquals(owner.getFrozenSupplyCount(), 1);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * when init data, SameTokenName is close, then open SameTokenName, Unfreeze assert success.
     */
    @Test
    public void SameTokenNameActiveInitAndAcitveUnfreezeAsset() {
        createAssertBeforSameTokenNameActive();
        UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        long tokenId = UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        long now = System.currentTimeMillis();
        UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        Account account = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS)).getInstance();
        Frozen newFrozen0 = Frozen.newBuilder().setFrozenBalance(UnfreezeAssetActuatorTest.frozenBalance).setExpireTime(now).build();
        Frozen newFrozen1 = Frozen.newBuilder().setFrozenBalance(((UnfreezeAssetActuatorTest.frozenBalance) + 1)).setExpireTime((now + 600000)).build();
        account = account.toBuilder().addFrozenSupply(newFrozen0).addFrozenSupply(newFrozen1).build();
        AccountCapsule accountCapsule = new AccountCapsule(account);
        UnfreezeAssetActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS));
            // V1 assert not exist
            Assert.assertNull(owner.getAssetMap().get(UnfreezeAssetActuatorTest.assetName));
            // V2
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), UnfreezeAssetActuatorTest.frozenBalance);
            Assert.assertEquals(owner.getFrozenSupplyCount(), 1);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAddress() {
        createAssertBeforSameTokenNameActive();
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS_INVALID), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAccount() {
        createAssertBeforSameTokenNameActive();
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ACCOUNT_INVALID), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (UnfreezeAssetActuatorTest.OWNER_ACCOUNT_INVALID)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void notIssueAsset() {
        createAssertBeforSameTokenNameActive();
        long now = System.currentTimeMillis();
        UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        Account account = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS)).getInstance();
        Frozen newFrozen = Frozen.newBuilder().setFrozenBalance(UnfreezeAssetActuatorTest.frozenBalance).setExpireTime(now).build();
        account = account.toBuilder().addFrozenSupply(newFrozen).setAssetIssuedName(EMPTY).build();
        AccountCapsule accountCapsule = new AccountCapsule(account);
        UnfreezeAssetActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("this account did not issue any asset", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noFrozenSupply() {
        createAssertBeforSameTokenNameActive();
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("no frozen supply balance", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void notTimeToUnfreeze() {
        createAssertBeforSameTokenNameActive();
        long now = System.currentTimeMillis();
        UnfreezeAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        Account account = UnfreezeAssetActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeAssetActuatorTest.OWNER_ADDRESS)).getInstance();
        Frozen newFrozen = Frozen.newBuilder().setFrozenBalance(UnfreezeAssetActuatorTest.frozenBalance).setExpireTime((now + 60000)).build();
        account = account.toBuilder().addFrozenSupply(newFrozen).build();
        AccountCapsule accountCapsule = new AccountCapsule(account);
        UnfreezeAssetActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeAssetActuator actuator = new UnfreezeAssetActuator(getContract(UnfreezeAssetActuatorTest.OWNER_ADDRESS), UnfreezeAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("It's not time to unfreeze asset supply", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

