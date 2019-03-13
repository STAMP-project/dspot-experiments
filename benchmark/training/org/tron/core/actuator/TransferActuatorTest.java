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
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.Parameter.ChainConstant;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Protocol.AccountType;


@Slf4j
public class TransferActuatorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_transfer_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String TO_ADDRESS;

    private static final long AMOUNT = 100;

    private static final long OWNER_BALANCE = 9999999;

    private static final long TO_BALANCE = 100001;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String TO_ADDRESS_INVALID = "bbb";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final String OWNER_NO_BALANCE;

    private static final String To_ACCOUNT_INVALID;

    static {
        Args.setParam(new String[]{ "--output-directory", TransferActuatorTest.dbPath }, TEST_CONF);
        TransferActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        TO_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
        OWNER_NO_BALANCE = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3433";
        To_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3422";
    }

    @Test
    public void rightTransfer() {
        TransferActuator actuator = new TransferActuator(getContract(TransferActuatorTest.AMOUNT), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), (((TransferActuatorTest.OWNER_BALANCE) - (TransferActuatorTest.AMOUNT)) - (ChainConstant.TRANSFER_FEE)));
            Assert.assertEquals(toAccount.getBalance(), ((TransferActuatorTest.TO_BALANCE) + (TransferActuatorTest.AMOUNT)));
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void perfectTransfer() {
        TransferActuator actuator = new TransferActuator(getContract(((TransferActuatorTest.OWNER_BALANCE) - (ChainConstant.TRANSFER_FEE))), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 0);
            Assert.assertEquals(toAccount.getBalance(), ((TransferActuatorTest.TO_BALANCE) + (TransferActuatorTest.OWNER_BALANCE)));
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void moreTransfer() {
        TransferActuator actuator = new TransferActuator(getContract(((TransferActuatorTest.OWNER_BALANCE) + 1)), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            logger.info(e.getMessage());
            Assert.assertTrue("Validate TransferContract error, balance is not sufficient.".equals(e.getMessage()));
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void iniviateOwnerAddress() {
        TransferActuator actuator = new TransferActuator(getContract(10000L, TransferActuatorTest.OWNER_ADDRESS_INVALID, TransferActuatorTest.TO_ADDRESS), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid ownerAddress");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid ownerAddress", e.getMessage());
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
        }
    }

    @Test
    public void iniviateToAddress() {
        TransferActuator actuator = new TransferActuator(getContract(10000L, TransferActuatorTest.OWNER_ADDRESS, TransferActuatorTest.TO_ADDRESS_INVALID), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid toAddress");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid toAddress", e.getMessage());
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void iniviateTrx() {
        TransferActuator actuator = new TransferActuator(getContract(100L, TransferActuatorTest.OWNER_ADDRESS, TransferActuatorTest.OWNER_ADDRESS), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Cannot transfer trx to yourself.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Cannot transfer trx to yourself.", e.getMessage());
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noExitOwnerAccount() {
        TransferActuator actuator = new TransferActuator(getContract(100L, TransferActuatorTest.OWNER_ACCOUNT_INVALID, TransferActuatorTest.TO_ADDRESS), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Validate TransferContract error, no OwnerAccount.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Validate TransferContract error, no OwnerAccount.", e.getMessage());
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * If to account not exit, create it.
     */
    @Test
    public void noExitToAccount() {
        TransferActuator actuator = new TransferActuator(getContract(1000000L, TransferActuatorTest.OWNER_ADDRESS, TransferActuatorTest.To_ACCOUNT_INVALID), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            AccountCapsule noExitAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.To_ACCOUNT_INVALID));
            Assert.assertTrue((null == noExitAccount));
            actuator.validate();
            actuator.execute(ret);
            noExitAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.To_ACCOUNT_INVALID));
            Assert.assertFalse((null == noExitAccount));// Had created.

            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((TransferActuatorTest.OWNER_BALANCE) - 1000000L));
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
            noExitAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.To_ACCOUNT_INVALID));
            Assert.assertEquals(noExitAccount.getBalance(), 1000000L);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            TransferActuatorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(TransferActuatorTest.To_ACCOUNT_INVALID));
        }
    }

    @Test
    public void zeroAmountTest() {
        TransferActuator actuator = new TransferActuator(getContract(0), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Amount must greater than 0.".equals(e.getMessage()));
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void negativeAmountTest() {
        TransferActuator actuator = new TransferActuator(getContract((-(TransferActuatorTest.AMOUNT))), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Amount must greater than 0.".equals(e.getMessage()));
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void addOverflowTest() {
        // First, increase the to balance. Else can't complete this test case.
        AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
        toAccount.setBalance(Long.MAX_VALUE);
        TransferActuatorTest.dbManager.getAccountStore().put(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS), toAccount);
        TransferActuator actuator = new TransferActuator(getContract(1), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("long overflow".equals(e.getMessage()));
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), Long.MAX_VALUE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void insufficientFee() {
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(TransferActuatorTest.OWNER_NO_BALANCE)), AccountType.Normal, (-10000L));
        AccountCapsule toAccountCapsule = new AccountCapsule(ByteString.copyFromUtf8("toAccount"), ByteString.copyFrom(ByteArray.fromHexString(TransferActuatorTest.To_ACCOUNT_INVALID)), AccountType.Normal, 100L);
        TransferActuatorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        TransferActuatorTest.dbManager.getAccountStore().put(toAccountCapsule.getAddress().toByteArray(), toAccountCapsule);
        TransferActuator actuator = new TransferActuator(getContract(TransferActuatorTest.AMOUNT, TransferActuatorTest.OWNER_NO_BALANCE, TransferActuatorTest.To_ACCOUNT_INVALID), TransferActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Validate TransferContract error, insufficient fee.");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Validate TransferContract error, balance is not sufficient.", e.getMessage());
            AccountCapsule owner = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = TransferActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(TransferActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), TransferActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), TransferActuatorTest.TO_BALANCE);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            TransferActuatorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(TransferActuatorTest.To_ACCOUNT_INVALID));
        }
    }
}

