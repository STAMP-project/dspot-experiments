package org.tron.core.actuator;


import Constant.TEST_CONF;
import Protocol.Transaction.Result.code.SUCESS;
import com.google.protobuf.InvalidProtocolBufferException;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.TronException;


@Slf4j
@Ignore
public class UpdateEnergyLimitContractActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_updateEnergyLimitContractActuator_test";

    private static String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_ACCOUNT_NAME = "test_account";

    private static String SECOND_ACCOUNT_ADDRESS;

    private static String OWNER_ADDRESS_NOTEXIST;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String SMART_CONTRACT_NAME = "smart_contarct";

    private static final String CONTRACT_ADDRESS = "111111";

    private static final String NO_EXIST_CONTRACT_ADDRESS = "2222222";

    private static final long SOURCE_ENERGY_LIMIT = 10L;

    private static final long TARGET_ENERGY_LIMIT = 30L;

    private static final long INVALID_ENERGY_LIMIT = -200L;

    static {
        Args.setParam(new String[]{ "--output-directory", UpdateEnergyLimitContractActuatorTest.dbPath }, TEST_CONF);
        UpdateEnergyLimitContractActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void successUpdateEnergyLimitContract() throws InvalidProtocolBufferException {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            // assert result state and energy_limit
            Assert.assertEquals(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, ByteArray.toHexString(actuator.getOwnerAddress().toByteArray()));
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(UpdateEnergyLimitContractActuatorTest.dbManager.getContractStore().get(ByteArray.fromHexString(UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS)).getOriginEnergyLimit(), UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT);
        } catch (ContractValidateException | ContractExeException e) {
            Assert.fail(getMessage());
        }
    }

    @Test
    public void invalidAddress() {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS_INVALID, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid address");
        } catch (TronException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
        }
    }

    @Test
    public void noExistAccount() {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS_NOTEXIST, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Account[" + (UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS_NOTEXIST)) + "] not exists"));
        } catch (TronException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS_NOTEXIST)) + "] not exists"), e.getMessage());
        }
    }

    @Test
    public void invalidResourceEnergyLimit() {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.INVALID_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("origin energy limit less than 0");
        } catch (TronException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("origin energy limit must > 0", e.getMessage());
        }
    }

    @Test
    public void noExistContract() {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, UpdateEnergyLimitContractActuatorTest.NO_EXIST_CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Contract not exists");
        } catch (TronException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Contract not exists", e.getMessage());
        }
    }

    @Test
    public void callerNotContractOwner() {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.SECOND_ACCOUNT_ADDRESS, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Account[" + (UpdateEnergyLimitContractActuatorTest.SECOND_ACCOUNT_ADDRESS)) + "] is not the owner of the contract"));
        } catch (TronException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (UpdateEnergyLimitContractActuatorTest.SECOND_ACCOUNT_ADDRESS)) + "] is not the owner of the contract"), e.getMessage());
        }
    }

    @Test
    public void twiceUpdateEnergyLimitContract() throws InvalidProtocolBufferException {
        UpdateEnergyLimitContractActuator actuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT), UpdateEnergyLimitContractActuatorTest.dbManager);
        UpdateEnergyLimitContractActuator secondActuator = new UpdateEnergyLimitContractActuator(getContract(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS, 90L), UpdateEnergyLimitContractActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            // first
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(UpdateEnergyLimitContractActuatorTest.OWNER_ADDRESS, ByteArray.toHexString(actuator.getOwnerAddress().toByteArray()));
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(UpdateEnergyLimitContractActuatorTest.dbManager.getContractStore().get(ByteArray.fromHexString(UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS)).getOriginEnergyLimit(), UpdateEnergyLimitContractActuatorTest.TARGET_ENERGY_LIMIT);
            // second
            secondActuator.validate();
            secondActuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            Assert.assertEquals(UpdateEnergyLimitContractActuatorTest.dbManager.getContractStore().get(ByteArray.fromHexString(UpdateEnergyLimitContractActuatorTest.CONTRACT_ADDRESS)).getOriginEnergyLimit(), 90L);
        } catch (ContractValidateException | ContractExeException e) {
            Assert.fail(getMessage());
        }
    }
}

