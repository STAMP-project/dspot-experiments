package org.tron.core.actuator;


import Constant.TEST_CONF;
import code.SUCESS;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.StringUtil;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;


@Slf4j
public class CreateAccountActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_CreateAccount_test";

    private static final String OWNER_ADDRESS_FIRST;

    private static final String ACCOUNT_NAME_SECOND = "ownerS";

    private static final String OWNER_ADDRESS_SECOND;

    static {
        Args.setParam(new String[]{ "--output-directory", CreateAccountActuatorTest.dbPath }, TEST_CONF);
        CreateAccountActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
    }

    /**
     * Unit test.
     */
    @Test
    public void firstCreateAccount() {
        CreateAccountActuator actuator = new CreateAccountActuator(getContract(CreateAccountActuatorTest.OWNER_ADDRESS_SECOND, CreateAccountActuatorTest.OWNER_ADDRESS_FIRST), CreateAccountActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule accountCapsule = CreateAccountActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(CreateAccountActuatorTest.OWNER_ADDRESS_FIRST));
            Assert.assertNotNull(accountCapsule);
            Assert.assertEquals(StringUtil.createReadableString(accountCapsule.getAddress()), CreateAccountActuatorTest.OWNER_ADDRESS_FIRST);
        } catch (ContractValidateException e) {
            logger.info(e.getMessage());
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Unit test.
     */
    @Test
    public void secondCreateAccount() {
        CreateAccountActuator actuator = new CreateAccountActuator(getContract(CreateAccountActuatorTest.OWNER_ADDRESS_SECOND, CreateAccountActuatorTest.OWNER_ADDRESS_SECOND), CreateAccountActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            AccountCapsule accountCapsule = CreateAccountActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(CreateAccountActuatorTest.OWNER_ADDRESS_SECOND));
            Assert.assertNotNull(accountCapsule);
            Assert.assertEquals(accountCapsule.getAddress(), ByteString.copyFrom(ByteArray.fromHexString(CreateAccountActuatorTest.OWNER_ADDRESS_SECOND)));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

