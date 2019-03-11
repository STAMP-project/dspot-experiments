package org.tron.core.actuator;


import ByteString.EMPTY;
import Constant.TEST_CONF;
import code.SUCESS;
import com.google.protobuf.ByteString;
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
import org.tron.protos.Protocol.AccountType;


@Slf4j
public class SetAccountIdActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_setaccountid_test";

    private static final String ACCOUNT_NAME = "ownertest";

    private static final String ACCOUNT_NAME_1 = "ownertest1";

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_1;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    static {
        Args.setParam(new String[]{ "--output-directory", SetAccountIdActuatorTest.dbPath }, TEST_CONF);
        SetAccountIdActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_1 = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
    }

    /**
     * Unit test.
     */
    /**
     * set account id when all right.
     */
    @Test
    public void rightSetAccountId() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME, SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(SetAccountIdActuatorTest.ACCOUNT_NAME, accountCapsule.getAccountId().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidAddress() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME, SetAccountIdActuatorTest.OWNER_ADDRESS_INVALID), SetAccountIdActuatorTest.dbManager);
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
    public void noExistAccount() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME, SetAccountIdActuatorTest.OWNER_ADDRESS_1), SetAccountIdActuatorTest.dbManager);
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

    @Test
    public void twiceUpdateAccount() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME, SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
        SetAccountIdActuator actuator1 = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME_1, SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(SetAccountIdActuatorTest.ACCOUNT_NAME, accountCapsule.getAccountId().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        try {
            actuator1.validate();
            actuator1.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("This account id already set", e.getMessage());
            AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(SetAccountIdActuatorTest.ACCOUNT_NAME, accountCapsule.getAccountId().toStringUtf8());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void nameAlreadyUsed() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME, SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
        SetAccountIdActuator actuator1 = new SetAccountIdActuator(getContract(SetAccountIdActuatorTest.ACCOUNT_NAME, SetAccountIdActuatorTest.OWNER_ADDRESS_1), SetAccountIdActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(SetAccountIdActuatorTest.ACCOUNT_NAME, accountCapsule.getAccountId().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFrom(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS_1)), ByteString.EMPTY, AccountType.Normal);
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        try {
            actuator1.validate();
            actuator1.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("This id has existed", e.getMessage());
            AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals(SetAccountIdActuatorTest.ACCOUNT_NAME, accountCapsule.getAccountId().toStringUtf8());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /* Account name need 8 - 32 bytes. */
    @Test
    public void invalidName() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        // Just OK 32 bytes is OK
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract("testname0123456789abcdefghijgklm", SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals("testname0123456789abcdefghijgklm", accountCapsule.getAccountId().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // 8 bytes is OK
        AccountCapsule accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountId(EMPTY.toByteArray());
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract("test1111", SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
            Assert.assertEquals("test1111", accountCapsule.getAccountId().toStringUtf8());
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Empty name
        accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountId(EMPTY.toByteArray());
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(EMPTY, SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountId", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Too long name 33 bytes
        accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountId(EMPTY.toByteArray());
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(("testname0123456789abcdefghijgklmo0123456789abcdefghijgk" + (("lmo0123456789abcdefghijgklmo0123456789abcdefghijgklmo0123456789abcdefghijgklmo" + "0123456789abcdefghijgklmo0123456789abcdefghijgklmo0123456789abcdefghijgklmo") + "0123456789abcdefghijgklmo0123456789abcdefghijgklmo")), SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountId", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Too short name 7 bytes
        accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountId(EMPTY.toByteArray());
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract("testnam", SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountId", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Can't contain space
        accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountId(EMPTY.toByteArray());
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract("t e", SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountId", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // Can't contain chinese characters
        accountCapsule = SetAccountIdActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(SetAccountIdActuatorTest.OWNER_ADDRESS));
        accountCapsule.setAccountId(EMPTY.toByteArray());
        SetAccountIdActuatorTest.dbManager.getAccountStore().put(accountCapsule.createDbKey(), accountCapsule);
        try {
            SetAccountIdActuator actuator = new SetAccountIdActuator(getContract(ByteString.copyFrom(ByteArray.fromHexString("E6B58BE8AF95")), SetAccountIdActuatorTest.OWNER_ADDRESS), SetAccountIdActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid accountId", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

