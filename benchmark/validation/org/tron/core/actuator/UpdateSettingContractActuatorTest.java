package org.tron.core.actuator;


import Constant.TEST_CONF;
import Protocol.Transaction.Result.code.SUCESS;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;


@Slf4j
public class UpdateSettingContractActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_updatesettingcontract_test";

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_ACCOUNT_NAME = "test_account";

    private static final String SECOND_ACCOUNT_ADDRESS;

    private static final String OWNER_ADDRESS_NOTEXIST;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String SMART_CONTRACT_NAME = "smart_contarct";

    private static final String CONTRACT_ADDRESS = "111111";

    private static final String NO_EXIST_CONTRACT_ADDRESS = "2222222";

    private static final long SOURCE_PERCENT = 10L;

    private static final long TARGET_PERCENT = 30L;

    private static final long INVALID_PERCENT = 200L;

    static {
        Args.setParam(new String[]{ "--output-directory", UpdateSettingContractActuatorTest.dbPath }, TEST_CONF);
        UpdateSettingContractActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_NOTEXIST = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        SECOND_ACCOUNT_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d427122222";
    }

    @Test
    public void successUpdateSettingContract() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.TARGET_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            // assert result state and consume_user_resource_percent
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(UpdateSettingContractActuatorTest.dbManager.getContractStore().get(ByteArray.fromHexString(UpdateSettingContractActuatorTest.CONTRACT_ADDRESS)).getConsumeUserResourcePercent(), UpdateSettingContractActuatorTest.TARGET_PERCENT);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidAddress() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS_INVALID, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.TARGET_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid address");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noExistAccount() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS_NOTEXIST, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.TARGET_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Account[" + (UpdateSettingContractActuatorTest.OWNER_ADDRESS_NOTEXIST)) + "] not exists"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (UpdateSettingContractActuatorTest.OWNER_ADDRESS_NOTEXIST)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void invalidResourcePercent() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.INVALID_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("percent not in [0, 100]");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("percent not in [0, 100]", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void noExistContract() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS, UpdateSettingContractActuatorTest.NO_EXIST_CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.TARGET_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Contract not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Contract not exists", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void callerNotContractOwner() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.SECOND_ACCOUNT_ADDRESS, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.TARGET_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Account[" + (UpdateSettingContractActuatorTest.SECOND_ACCOUNT_ADDRESS)) + "] is not the owner of the contract"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (UpdateSettingContractActuatorTest.SECOND_ACCOUNT_ADDRESS)) + "] is not the owner of the contract"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    @Test
    public void twiceUpdateSettingContract() {
        UpdateSettingContractActuator actuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, UpdateSettingContractActuatorTest.TARGET_PERCENT), UpdateSettingContractActuatorTest.dbManager);
        UpdateSettingContractActuator secondActuator = new UpdateSettingContractActuator(getContract(UpdateSettingContractActuatorTest.OWNER_ADDRESS, UpdateSettingContractActuatorTest.CONTRACT_ADDRESS, 90L), UpdateSettingContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            // first
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(UpdateSettingContractActuatorTest.dbManager.getContractStore().get(ByteArray.fromHexString(UpdateSettingContractActuatorTest.CONTRACT_ADDRESS)).getConsumeUserResourcePercent(), UpdateSettingContractActuatorTest.TARGET_PERCENT);
            // second
            secondActuator.validate();
            secondActuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(UpdateSettingContractActuatorTest.dbManager.getContractStore().get(ByteArray.fromHexString(UpdateSettingContractActuatorTest.CONTRACT_ADDRESS)).getConsumeUserResourcePercent(), 90L);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

