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
import org.tron.common.utils.StringUtil;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.capsule.WitnessCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.config.args.Witness;
import org.tron.core.db.Manager;
import org.tron.core.exception.BalanceInsufficientException;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Protocol.AccountType;


@Slf4j
public class WithdrawBalanceActuatorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_withdraw_balance_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000L;

    private static final long allowance = 32000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", WithdrawBalanceActuatorTest.dbPath }, TEST_CONF);
        WithdrawBalanceActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testWithdrawBalance() {
        long now = System.currentTimeMillis();
        WithdrawBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        byte[] address = ByteArray.fromHexString(WithdrawBalanceActuatorTest.OWNER_ADDRESS);
        try {
            WithdrawBalanceActuatorTest.dbManager.adjustAllowance(address, WithdrawBalanceActuatorTest.allowance);
        } catch (BalanceInsufficientException e) {
            TestCase.fail("BalanceInsufficientException");
        }
        AccountCapsule accountCapsule = WithdrawBalanceActuatorTest.dbManager.getAccountStore().get(address);
        Assert.assertEquals(accountCapsule.getAllowance(), WithdrawBalanceActuatorTest.allowance);
        Assert.assertEquals(accountCapsule.getLatestWithdrawTime(), 0);
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address), 100, "http://baidu.com");
        WithdrawBalanceActuatorTest.dbManager.getWitnessStore().put(address, witnessCapsule);
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(WithdrawBalanceActuatorTest.OWNER_ADDRESS), WithdrawBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = WithdrawBalanceActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(WithdrawBalanceActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((WithdrawBalanceActuatorTest.initBalance) + (WithdrawBalanceActuatorTest.allowance)));
            Assert.assertEquals(owner.getAllowance(), 0);
            Assert.assertNotEquals(owner.getLatestWithdrawTime(), 0);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidOwnerAddress() {
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(WithdrawBalanceActuatorTest.OWNER_ADDRESS_INVALID), WithdrawBalanceActuatorTest.dbManager);
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
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(WithdrawBalanceActuatorTest.OWNER_ACCOUNT_INVALID), WithdrawBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (WithdrawBalanceActuatorTest.OWNER_ACCOUNT_INVALID)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void notWitness() {
        // long now = System.currentTimeMillis();
        // AccountCapsule accountCapsule = dbManager.getAccountStore()
        // .get(ByteArray.fromHexString(OWNER_ADDRESS));
        // accountCapsule.setFrozen(1_000_000_000L, now);
        // dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(WithdrawBalanceActuatorTest.OWNER_ADDRESS), WithdrawBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (WithdrawBalanceActuatorTest.OWNER_ADDRESS)) + "] is not a witnessAccount"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noAllowance() {
        long now = System.currentTimeMillis();
        WithdrawBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        byte[] address = ByteArray.fromHexString(WithdrawBalanceActuatorTest.OWNER_ADDRESS);
        AccountCapsule accountCapsule = WithdrawBalanceActuatorTest.dbManager.getAccountStore().get(address);
        Assert.assertEquals(accountCapsule.getAllowance(), 0);
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address), 100, "http://baidu.com");
        WithdrawBalanceActuatorTest.dbManager.getWitnessStore().put(address, witnessCapsule);
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(WithdrawBalanceActuatorTest.OWNER_ADDRESS), WithdrawBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("witnessAccount does not have any allowance", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void isGR() {
        Witness w = Args.getInstance().getGenesisBlock().getWitnesses().get(0);
        byte[] address = w.getAddress();
        AccountCapsule grCapsule = new AccountCapsule(ByteString.copyFromUtf8("gr"), ByteString.copyFrom(address), AccountType.Normal, WithdrawBalanceActuatorTest.initBalance);
        WithdrawBalanceActuatorTest.dbManager.getAccountStore().put(grCapsule.createDbKey(), grCapsule);
        long now = System.currentTimeMillis();
        WithdrawBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        try {
            WithdrawBalanceActuatorTest.dbManager.adjustAllowance(address, WithdrawBalanceActuatorTest.allowance);
        } catch (BalanceInsufficientException e) {
            TestCase.fail("BalanceInsufficientException");
        }
        AccountCapsule accountCapsule = WithdrawBalanceActuatorTest.dbManager.getAccountStore().get(address);
        Assert.assertEquals(accountCapsule.getAllowance(), WithdrawBalanceActuatorTest.allowance);
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address), 100, "http://google.com");
        WithdrawBalanceActuatorTest.dbManager.getAccountStore().put(address, accountCapsule);
        WithdrawBalanceActuatorTest.dbManager.getWitnessStore().put(address, witnessCapsule);
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(ByteArray.toHexString(address)), WithdrawBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        Assert.assertTrue(WithdrawBalanceActuatorTest.dbManager.getWitnessStore().has(address));
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            String readableOwnerAddress = StringUtil.createReadableString(address);
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + readableOwnerAddress) + "] is a guard representative and is not allowed to withdraw Balance"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void notTimeToWithdraw() {
        long now = System.currentTimeMillis();
        WithdrawBalanceActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(now);
        byte[] address = ByteArray.fromHexString(WithdrawBalanceActuatorTest.OWNER_ADDRESS);
        try {
            WithdrawBalanceActuatorTest.dbManager.adjustAllowance(address, WithdrawBalanceActuatorTest.allowance);
        } catch (BalanceInsufficientException e) {
            TestCase.fail("BalanceInsufficientException");
        }
        AccountCapsule accountCapsule = WithdrawBalanceActuatorTest.dbManager.getAccountStore().get(address);
        accountCapsule.setLatestWithdrawTime(now);
        Assert.assertEquals(accountCapsule.getAllowance(), WithdrawBalanceActuatorTest.allowance);
        Assert.assertEquals(accountCapsule.getLatestWithdrawTime(), now);
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFrom(address), 100, "http://baidu.com");
        WithdrawBalanceActuatorTest.dbManager.getAccountStore().put(address, accountCapsule);
        WithdrawBalanceActuatorTest.dbManager.getWitnessStore().put(address, witnessCapsule);
        WithdrawBalanceActuator actuator = new WithdrawBalanceActuator(getContract(WithdrawBalanceActuatorTest.OWNER_ADDRESS), WithdrawBalanceActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("cannot run here.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("The last withdraw time is " + now) + ",less than 24 hours"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

