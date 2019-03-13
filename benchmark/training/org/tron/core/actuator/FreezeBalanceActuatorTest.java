package org.tron.core.actuator;


import Constant.TEST_CONF;
import code.SUCESS;
import com.google.protobuf.ByteString;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.DelegatedResourceAccountIndexCapsule;
import org.tron.core.capsule.DelegatedResourceCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.Parameter.ChainConstant;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;


@Slf4j
public class FreezeBalanceActuatorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_freeze_balance_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String RECEIVER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", FreezeBalanceActuatorTest.dbPath }, TEST_CONF);
        FreezeBalanceActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        RECEIVER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049150";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testFreezeBalanceForBandwidth() {
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((FreezeBalanceActuatorTest.initBalance) - frozenBalance) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(owner.getFrozenBalance(), frozenBalance);
            Assert.assertEquals(frozenBalance, owner.getTronPower());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testFreezeBalanceForEnergy() {
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForCpu(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((FreezeBalanceActuatorTest.initBalance) - frozenBalance) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(0L, owner.getFrozenBalance());
            Assert.assertEquals(frozenBalance, owner.getEnergyFrozenBalance());
            Assert.assertEquals(frozenBalance, owner.getTronPower());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testFreezeDelegatedBalanceForBandwidth() {
        FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowDelegateResource(1);
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getDelegatedContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, FreezeBalanceActuatorTest.RECEIVER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long totalNetWeightBefore = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalNetWeight();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((FreezeBalanceActuatorTest.initBalance) - frozenBalance) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(0L, owner.getFrozenBalance());
            Assert.assertEquals(frozenBalance, owner.getDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(frozenBalance, owner.getTronPower());
            AccountCapsule receiver = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(frozenBalance, receiver.getAcquiredDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(0L, receiver.getAcquiredDelegatedFrozenBalanceForEnergy());
            Assert.assertEquals(0L, receiver.getTronPower());
            DelegatedResourceCapsule delegatedResourceCapsule = FreezeBalanceActuatorTest.dbManager.getDelegatedResourceStore().get(DelegatedResourceCapsule.createDbKey(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS), ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS)));
            Assert.assertEquals(frozenBalance, delegatedResourceCapsule.getFrozenBalanceForBandwidth());
            long totalNetWeightAfter = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalNetWeight();
            Assert.assertEquals((totalNetWeightBefore + (frozenBalance / 1000000L)), totalNetWeightAfter);
            // check DelegatedResourceAccountIndex
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndexCapsuleOwner = FreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleOwner.getFromAccountsList().size());
            Assert.assertEquals(1, delegatedResourceAccountIndexCapsuleOwner.getToAccountsList().size());
            Assert.assertEquals(true, delegatedResourceAccountIndexCapsuleOwner.getToAccountsList().contains(ByteString.copyFrom(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS))));
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndexCapsuleReceiver = FreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleReceiver.getToAccountsList().size());
            Assert.assertEquals(1, delegatedResourceAccountIndexCapsuleReceiver.getFromAccountsList().size());
            Assert.assertEquals(true, delegatedResourceAccountIndexCapsuleReceiver.getFromAccountsList().contains(ByteString.copyFrom(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testFreezeDelegatedBalanceForCpuSameNameTokenActive() {
        FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowDelegateResource(1);
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getDelegatedContractForCpu(FreezeBalanceActuatorTest.OWNER_ADDRESS, FreezeBalanceActuatorTest.RECEIVER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long totalEnergyWeightBefore = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyWeight();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((FreezeBalanceActuatorTest.initBalance) - frozenBalance) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(0L, owner.getFrozenBalance());
            Assert.assertEquals(0L, owner.getDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(frozenBalance, owner.getDelegatedFrozenBalanceForEnergy());
            Assert.assertEquals(frozenBalance, owner.getTronPower());
            AccountCapsule receiver = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(0L, receiver.getAcquiredDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(frozenBalance, receiver.getAcquiredDelegatedFrozenBalanceForEnergy());
            Assert.assertEquals(0L, receiver.getTronPower());
            DelegatedResourceCapsule delegatedResourceCapsule = FreezeBalanceActuatorTest.dbManager.getDelegatedResourceStore().get(DelegatedResourceCapsule.createDbKey(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS), ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS)));
            Assert.assertEquals(0L, delegatedResourceCapsule.getFrozenBalanceForBandwidth());
            Assert.assertEquals(frozenBalance, delegatedResourceCapsule.getFrozenBalanceForEnergy());
            long totalEnergyWeightAfter = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyWeight();
            Assert.assertEquals((totalEnergyWeightBefore + (frozenBalance / 1000000L)), totalEnergyWeightAfter);
            // check DelegatedResourceAccountIndex
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndexCapsuleOwner = FreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleOwner.getFromAccountsList().size());
            Assert.assertEquals(1, delegatedResourceAccountIndexCapsuleOwner.getToAccountsList().size());
            Assert.assertEquals(true, delegatedResourceAccountIndexCapsuleOwner.getToAccountsList().contains(ByteString.copyFrom(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS))));
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndexCapsuleReceiver = FreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleReceiver.getToAccountsList().size());
            Assert.assertEquals(1, delegatedResourceAccountIndexCapsuleReceiver.getFromAccountsList().size());
            Assert.assertEquals(true, delegatedResourceAccountIndexCapsuleReceiver.getFromAccountsList().contains(ByteString.copyFrom(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testFreezeDelegatedBalanceForCpuSameNameTokenClose() {
        FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowDelegateResource(0);
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getDelegatedContractForCpu(FreezeBalanceActuatorTest.OWNER_ADDRESS, FreezeBalanceActuatorTest.RECEIVER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long totalEnergyWeightBefore = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyWeight();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((FreezeBalanceActuatorTest.initBalance) - frozenBalance) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(0L, owner.getFrozenBalance());
            Assert.assertEquals(0L, owner.getDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(0L, owner.getDelegatedFrozenBalanceForEnergy());
            Assert.assertEquals(0L, owner.getDelegatedFrozenBalanceForEnergy());
            AccountCapsule receiver = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(0L, receiver.getAcquiredDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(0L, receiver.getAcquiredDelegatedFrozenBalanceForEnergy());
            Assert.assertEquals(0L, receiver.getTronPower());
            long totalEnergyWeightAfter = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyWeight();
            Assert.assertEquals((totalEnergyWeightBefore + (frozenBalance / 1000000L)), totalEnergyWeightAfter);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void freezeLessThanZero() {
        long frozenBalance = -1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("frozenBalance must be positive", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void freezeMoreThanBalance() {
        long frozenBalance = 11000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("frozenBalance must be less than accountBalance", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAddress() {
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS_INVALID, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAccount() {
        long frozenBalance = 1000000000L;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ACCOUNT_INVALID, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (FreezeBalanceActuatorTest.OWNER_ACCOUNT_INVALID)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void durationLessThanMin() {
        long frozenBalance = 1000000000L;
        long duration = 2;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            long minFrozenTime = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getMinFrozenTime();
            long maxFrozenTime = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getMaxFrozenTime();
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("frozenDuration must be less than " + maxFrozenTime) + " days ") + "and more than ") + minFrozenTime) + " days"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void durationMoreThanMax() {
        long frozenBalance = 1000000000L;
        long duration = 4;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            long minFrozenTime = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getMinFrozenTime();
            long maxFrozenTime = FreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getMaxFrozenTime();
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("frozenDuration must be less than " + maxFrozenTime) + " days ") + "and more than ") + minFrozenTime) + " days"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void lessThan1TrxTest() {
        long frozenBalance = 1;
        long duration = 3;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("frozenBalance must be more than 1TRX", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void frozenNumTest() {
        AccountCapsule account = FreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(FreezeBalanceActuatorTest.OWNER_ADDRESS));
        account.setFrozen(1000L, 1000000000L);
        account.setFrozen(1000000L, 1000000000L);
        FreezeBalanceActuatorTest.dbManager.getAccountStore().put(account.getAddress().toByteArray(), account);
        long frozenBalance = 20000000L;
        long duration = 3L;
        FreezeBalanceActuator actuator = new FreezeBalanceActuator(getContractForBandwidth(FreezeBalanceActuatorTest.OWNER_ADDRESS, frozenBalance, duration), FreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("frozenCount must be 0 or 1", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

