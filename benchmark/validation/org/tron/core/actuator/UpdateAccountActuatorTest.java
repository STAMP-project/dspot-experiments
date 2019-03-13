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


@Slf4j
public class UpdateAccountActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_updateaccount_test";

    private static final String ACCOUNT_NAME = "ownerTest";

    private static final String ACCOUNT_NAME_1 = "ownerTest1";

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_1;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    static {
        Args.setParam(new String[]{ "--output-directory", UpdateAccountActuatorTest.dbPath }, TEST_CONF);
        UpdateAccountActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_1 = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
    }

    /**
     * Unit test.
     */
    /**
     * Update account when all right.
     */
    @Test
    public void rightUpdateAccount() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UpdateAccountActuator actuator = new UpdateAccountActuator(getContract(UpdateAccountActuatorTest.ACCOUNT_NAME, UpdateAccountActuatorTest.OWNER_ADDRESS), UpdateAccountActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = UpdateAccountActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UpdateAccountActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(UpdateAccountActuatorTest.ACCOUNT_NAME, accountCapsule.getAccountName().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidAddress() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UpdateAccountActuator actuator = new UpdateAccountActuator(getContract(UpdateAccountActuatorTest.ACCOUNT_NAME, UpdateAccountActuatorTest.OWNER_ADDRESS_INVALID), UpdateAccountActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid ownerAddress", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noExitAccount() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UpdateAccountActuator actuator = new UpdateAccountActuator(getContract(UpdateAccountActuatorTest.ACCOUNT_NAME, UpdateAccountActuatorTest.OWNER_ADDRESS_1), UpdateAccountActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Account has not existed", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /* Account name need 8 - 32 bytes. */
    @Test
    public void invalidName() {
        UpdateAccountActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowUpdateAccountName(1);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        // Just OK 32 bytes is OK
        try {
            UpdateAccountActuator actuator = new UpdateAccountActuator(getContract("testname0123456789abcdefghijgklm", UpdateAccountActuatorTest.OWNER_ADDRESS), UpdateAccountActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = UpdateAccountActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UpdateAccountActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals("testname0123456789abcdefghijgklm", accountCapsule.getAccountName().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // 8 bytes is OK
        AccountCapsule accountCapsule = UpdateAccountActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UpdateAccountActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountName(EMPTY.toByteArray());
        UpdateAccountActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            UpdateAccountActuator actuator = new UpdateAccountActuator(getContract("testname", UpdateAccountActuatorTest.OWNER_ADDRESS), UpdateAccountActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            accountCapsule = UpdateAccountActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(UpdateAccountActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals("testname", accountCapsule.getAccountName().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Empty name
        try {
            UpdateAccountActuator actuator = new UpdateAccountActuator(getContract(EMPTY, UpdateAccountActuatorTest.OWNER_ADDRESS), UpdateAccountActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountName", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Too long name 33 bytes
        try {
            UpdateAccountActuator actuator = new UpdateAccountActuator(getContract(("testname0123456789abcdefghijgklmo0123456789abcdefghijgk" + (("lmo0123456789abcdefghijgklmo0123456789abcdefghijgklmo0123456789abcdefghijgklmo" + "0123456789abcdefghijgklmo0123456789abcdefghijgklmo0123456789abcdefghijgklmo") + "0123456789abcdefghijgklmo0123456789abcdefghijgklmo")), UpdateAccountActuatorTest.OWNER_ADDRESS), UpdateAccountActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountName", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // //Too short name 7 bytes
        // try {
        // UpdateAccountActuator actuator = new UpdateAccountActuator(
        // getContract("testnam", OWNER_ADDRESS), dbManager);
        // actuator.validate();
        // actuator.execute(ret);
        // Assert.assertFalse(true);
        // } catch (ContractValidateException e) {
        // Assert.assertTrue(e instanceof ContractValidateException);
        // Assert.assertEquals("Invalid accountName", e.getMessage());
        // } catch (ContractExeException e) {
        // Assert.assertFalse(e instanceof ContractExeException);
        // }
        // 
        // //Can't contain space
        // try {
        // UpdateAccountActuator actuator = new UpdateAccountActuator(
        // getContract("t e", OWNER_ADDRESS), dbManager);
        // actuator.validate();
        // actuator.execute(ret);
        // Assert.assertFalse(true);
        // } catch (ContractValidateException e) {
        // Assert.assertTrue(e instanceof ContractValidateException);
        // Assert.assertEquals("Invalid accountName", e.getMessage());
        // } catch (ContractExeException e) {
        // Assert.assertFalse(e instanceof ContractExeException);
        // }
        // //Can't contain chinese characters
        // try {
        // UpdateAccountActuator actuator = new UpdateAccountActuator(
        // getContract(ByteString.copyFrom(ByteArray.fromHexString("E6B58BE8AF95"))
        // , OWNER_ADDRESS), dbManager);
        // actuator.validate();
        // actuator.execute(ret);
        // Assert.assertFalse(true);
        // } catch (ContractValidateException e) {
        // Assert.assertTrue(e instanceof ContractValidateException);
        // Assert.assertEquals("Invalid accountName", e.getMessage());
        // } catch (ContractExeException e) {
        // Assert.assertFalse(e instanceof ContractExeException);
        // }
    }
}

