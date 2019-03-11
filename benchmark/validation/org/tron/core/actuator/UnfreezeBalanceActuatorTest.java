package org.tron.core.actuator;


import Constant.TEST_CONF;
import code.SUCESS;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
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
import org.tron.core.capsule.VotesCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Protocol.Vote;


// @Test
// public void InvalidTotalNetWeight(){
// long now = System.currentTimeMillis();
// dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
// dbManager.getDynamicPropertiesStore().saveTotalNetWeight(smallTatalResource);
// 
// AccountCapsule accountCapsule = dbManager.getAccountStore()
// .get(ByteArray.fromHexString(OWNER_ADDRESS));
// accountCapsule.setFrozen(frozenBalance, now);
// dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
// 
// Assert.assertTrue(frozenBalance/1000_000L > smallTatalResource );
// UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(
// getContract(OWNER_ADDRESS), dbManager);
// TransactionResultCapsule ret = new TransactionResultCapsule();
// try {
// actuator.validate();
// actuator.execute(ret);
// 
// Assert.assertTrue(dbManager.getDynamicPropertiesStore().getTotalNetWeight() >= 0);
// } catch (ContractValidateException e) {
// Assert.assertTrue(e instanceof ContractValidateException);
// } catch (ContractExeException e) {
// Assert.assertTrue(e instanceof ContractExeException);
// }
// }
// 
// @Test
// public void InvalidTotalEnergyWeight(){
// long now = System.currentTimeMillis();
// dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
// dbManager.getDynamicPropertiesStore().saveTotalEnergyWeight(smallTatalResource);
// 
// AccountCapsule accountCapsule = dbManager.getAccountStore()
// .get(ByteArray.fromHexString(OWNER_ADDRESS));
// accountCapsule.setFrozenForEnergy(frozenBalance, now);
// dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
// 
// Assert.assertTrue(frozenBalance/1000_000L > smallTatalResource );
// UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(
// getContract(OWNER_ADDRESS, Contract.ResourceCode.ENERGY), dbManager);
// TransactionResultCapsule ret = new TransactionResultCapsule();
// try {
// actuator.validate();
// actuator.execute(ret);
// 
// Assert.assertTrue(dbManager.getDynamicPropertiesStore().getTotalEnergyWeight() >= 0);
// } catch (ContractValidateException e) {
// Assert.assertTrue(e instanceof ContractValidateException);
// } catch (ContractExeException e) {
// Assert.assertTrue(e instanceof ContractExeException);
// }
// }
@Slf4j
public class UnfreezeBalanceActuatorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_unfreeze_balance_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String RECEIVER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000L;

    private static final long frozenBalance = 1000000000L;

    private static final long smallTatalResource = 100L;

    static {
        Args.setParam(new String[]{ "--output-directory", UnfreezeBalanceActuatorTest.dbPath }, TEST_CONF);
        UnfreezeBalanceActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        RECEIVER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049150";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testUnfreezeBalanceForBandwidth() {
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule accountCapsule = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        accountCapsule.setFrozen(UnfreezeBalanceActuatorTest.frozenBalance, now);
        Assert.assertEquals(accountCapsule.getFrozenBalance(), UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(accountCapsule.getTronPower(), UnfreezeBalanceActuatorTest.frozenBalance);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long totalNetWeightBefore = UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalNetWeight();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((UnfreezeBalanceActuatorTest.initBalance) + (UnfreezeBalanceActuatorTest.frozenBalance)));
            Assert.assertEquals(owner.getFrozenBalance(), 0);
            Assert.assertEquals(owner.getTronPower(), 0L);
            long totalNetWeightAfter = UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalNetWeight();
            Assert.assertEquals(totalNetWeightBefore, (totalNetWeightAfter + ((UnfreezeBalanceActuatorTest.frozenBalance) / 1000000L)));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testUnfreezeBalanceForEnergy() {
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule accountCapsule = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        accountCapsule.setFrozenForEnergy(UnfreezeBalanceActuatorTest.frozenBalance, now);
        Assert.assertEquals(accountCapsule.getAllFrozenBalanceForEnergy(), UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(accountCapsule.getTronPower(), UnfreezeBalanceActuatorTest.frozenBalance);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForCpu(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        long totalEnergyWeightBefore = UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyWeight();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((UnfreezeBalanceActuatorTest.initBalance) + (UnfreezeBalanceActuatorTest.frozenBalance)));
            Assert.assertEquals(owner.getEnergyFrozenBalance(), 0);
            Assert.assertEquals(owner.getTronPower(), 0L);
            long totalEnergyWeightAfter = UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyWeight();
            Assert.assertEquals(totalEnergyWeightBefore, (totalEnergyWeightAfter + ((UnfreezeBalanceActuatorTest.frozenBalance) / 1000000L)));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testUnfreezeDelegatedBalanceForBandwidth() {
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowDelegateResource(1);
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule owner = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        owner.setDelegatedFrozenBalanceForBandwidth(UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(UnfreezeBalanceActuatorTest.frozenBalance, owner.getTronPower());
        AccountCapsule receiver = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS));
        receiver.setAcquiredDelegatedFrozenBalanceForBandwidth(UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(0L, receiver.getTronPower());
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(owner.createDbKey(), owner);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(receiver.createDbKey(), receiver);
        // init DelegatedResourceCapsule
        DelegatedResourceCapsule delegatedResourceCapsule = new DelegatedResourceCapsule(owner.getAddress(), receiver.getAddress());
        delegatedResourceCapsule.setFrozenBalanceForBandwidth(UnfreezeBalanceActuatorTest.frozenBalance, (now - 100L));
        UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceStore().put(DelegatedResourceCapsule.createDbKey(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS)), delegatedResourceCapsule);
        // init DelegatedResourceAccountIndex
        {
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndex = new DelegatedResourceAccountIndexCapsule(owner.getAddress());
            delegatedResourceAccountIndex.addToAccount(ByteString.copyFrom(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS)));
            UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().put(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), delegatedResourceAccountIndex);
        }
        {
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndex = new DelegatedResourceAccountIndexCapsule(receiver.getAddress());
            delegatedResourceAccountIndex.addFromAccount(ByteString.copyFrom(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS)));
            UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().put(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS), delegatedResourceAccountIndex);
        }
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getDelegatedContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS, UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule ownerResult = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
            AccountCapsule receiverResult = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(((UnfreezeBalanceActuatorTest.initBalance) + (UnfreezeBalanceActuatorTest.frozenBalance)), ownerResult.getBalance());
            Assert.assertEquals(0L, ownerResult.getTronPower());
            Assert.assertEquals(0L, ownerResult.getDelegatedFrozenBalanceForBandwidth());
            Assert.assertEquals(0L, receiverResult.getAllFrozenBalanceForBandwidth());
            // check DelegatedResourceAccountIndex
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndexCapsuleOwner = UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleOwner.getFromAccountsList().size());
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleOwner.getToAccountsList().size());
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndexCapsuleReceiver = UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleReceiver.getToAccountsList().size());
            Assert.assertEquals(0, delegatedResourceAccountIndexCapsuleReceiver.getFromAccountsList().size());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * when SameTokenName close,delegate balance frozen, unfreoze show error
     */
    @Test
    public void testUnfreezeDelegatedBalanceForBandwidthSameTokenNameClose() {
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowDelegateResource(0);
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule owner = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        owner.setDelegatedFrozenBalanceForBandwidth(UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(UnfreezeBalanceActuatorTest.frozenBalance, owner.getTronPower());
        AccountCapsule receiver = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS));
        receiver.setAcquiredDelegatedFrozenBalanceForBandwidth(UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(0L, receiver.getTronPower());
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(owner.createDbKey(), owner);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(receiver.createDbKey(), receiver);
        // init DelegatedResourceCapsule
        DelegatedResourceCapsule delegatedResourceCapsule = new DelegatedResourceCapsule(owner.getAddress(), receiver.getAddress());
        delegatedResourceCapsule.setFrozenBalanceForBandwidth(UnfreezeBalanceActuatorTest.frozenBalance, (now - 100L));
        UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceStore().put(DelegatedResourceCapsule.createDbKey(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS)), delegatedResourceCapsule);
        // init DelegatedResourceAccountIndex
        {
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndex = new DelegatedResourceAccountIndexCapsule(owner.getAddress());
            delegatedResourceAccountIndex.addToAccount(ByteString.copyFrom(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS)));
            UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().put(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), delegatedResourceAccountIndex);
        }
        {
            DelegatedResourceAccountIndexCapsule delegatedResourceAccountIndex = new DelegatedResourceAccountIndexCapsule(receiver.getAddress());
            delegatedResourceAccountIndex.addFromAccount(ByteString.copyFrom(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS)));
            UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceAccountIndexStore().put(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS), delegatedResourceAccountIndex);
        }
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getDelegatedContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS, UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("no frozenBalance(BANDWIDTH)", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
        }
    }

    @Test
    public void testUnfreezeDelegatedBalanceForCpu() {
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowDelegateResource(1);
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule owner = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        owner.addDelegatedFrozenBalanceForEnergy(UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(UnfreezeBalanceActuatorTest.frozenBalance, owner.getTronPower());
        AccountCapsule receiver = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS));
        receiver.addAcquiredDelegatedFrozenBalanceForEnergy(UnfreezeBalanceActuatorTest.frozenBalance);
        Assert.assertEquals(0L, receiver.getTronPower());
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(owner.createDbKey(), owner);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(receiver.createDbKey(), receiver);
        DelegatedResourceCapsule delegatedResourceCapsule = new DelegatedResourceCapsule(owner.getAddress(), receiver.getAddress());
        delegatedResourceCapsule.setFrozenBalanceForEnergy(UnfreezeBalanceActuatorTest.frozenBalance, (now - 100L));
        UnfreezeBalanceActuatorTest.dbManager.getDelegatedResourceStore().put(DelegatedResourceCapsule.createDbKey(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS)), delegatedResourceCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getDelegatedContractForCpu(UnfreezeBalanceActuatorTest.OWNER_ADDRESS, UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule ownerResult = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
            AccountCapsule receiverResult = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.RECEIVER_ADDRESS));
            Assert.assertEquals(((UnfreezeBalanceActuatorTest.initBalance) + (UnfreezeBalanceActuatorTest.frozenBalance)), ownerResult.getBalance());
            Assert.assertEquals(0L, ownerResult.getTronPower());
            Assert.assertEquals(0L, ownerResult.getDelegatedFrozenBalanceForEnergy());
            Assert.assertEquals(0L, receiverResult.getAllFrozenBalanceForEnergy());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAddress() {
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule accountCapsule = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        accountCapsule.setFrozen(1000000000L, now);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS_INVALID), UnfreezeBalanceActuatorTest.dbManager);
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
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule accountCapsule = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        accountCapsule.setFrozen(1000000000L, now);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ACCOUNT_INVALID), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (UnfreezeBalanceActuatorTest.OWNER_ACCOUNT_INVALID)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noFrozenBalance() {
        // long now = System.currentTimeMillis();
        // AccountCapsule accountCapsule = dbManager.getAccountStore()
        // .get(ByteArray.fromHexString(OWNER_ADDRESS));
        // accountCapsule.setFrozen(1_000_000_000L, now);
        // dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("no frozenBalance(BANDWIDTH)", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void notTimeToUnfreeze() {
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule accountCapsule = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS));
        accountCapsule.setFrozen(1000000000L, (now + 60000));
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("It's not time to unfreeze(BANDWIDTH).", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void testClearVotes() {
        byte[] ownerAddressBytes = ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS);
        ByteString ownerAddress = ByteString.copyFrom(ownerAddressBytes);
        long now = System.currentTimeMillis();
        UnfreezeBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        AccountCapsule accountCapsule = UnfreezeBalanceActuatorTest.dbManager.getAccountStore().get(ownerAddressBytes);
        accountCapsule.setFrozen(1000000000L, now);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        UnfreezeBalanceActuator actuator = new UnfreezeBalanceActuator(getContractForBandwidth(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), UnfreezeBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UnfreezeBalanceActuatorTest.dbManager.getVotesStore().reset();
        Assert.assertNull(UnfreezeBalanceActuatorTest.dbManager.getVotesStore().get(ownerAddressBytes));
        try {
            actuator.validate();
            actuator.execute(ret);
            VotesCapsule votesCapsule = UnfreezeBalanceActuatorTest.dbManager.getVotesStore().get(ownerAddressBytes);
            Assert.assertNotNull(votesCapsule);
            Assert.assertEquals(0, votesCapsule.getNewVotes().size());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // if had votes
        List<Vote> oldVotes = new ArrayList<Vote>();
        VotesCapsule votesCapsule = new VotesCapsule(ByteString.copyFrom(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS)), oldVotes);
        votesCapsule.addNewVotes(ByteString.copyFrom(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS)), 100);
        UnfreezeBalanceActuatorTest.dbManager.getVotesStore().put(ByteArray.fromHexString(UnfreezeBalanceActuatorTest.OWNER_ADDRESS), votesCapsule);
        accountCapsule.setFrozen(1000000000L, now);
        UnfreezeBalanceActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            actuator.validate();
            actuator.execute(ret);
            votesCapsule = UnfreezeBalanceActuatorTest.dbManager.getVotesStore().get(ownerAddressBytes);
            Assert.assertNotNull(votesCapsule);
            Assert.assertEquals(0, votesCapsule.getNewVotes().size());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

