package org.tron.core.actuator;


import ByteString.EMPTY;
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
import org.tron.core.capsule.WitnessCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Protocol.AccountType;


@Slf4j
public class WitnessCreateActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_WitnessCreate_test";

    private static final String ACCOUNT_NAME_FIRST = "ownerF";

    private static final String OWNER_ADDRESS_FIRST;

    private static final String ACCOUNT_NAME_SECOND = "ownerS";

    private static final String OWNER_ADDRESS_SECOND;

    private static final String URL = "https://tron.network";

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ADDRESS_NOACCOUNT;

    private static final String OWNER_ADDRESS_BALANCENOTSUFFIENT;

    static {
        Args.setParam(new String[]{ "--output-directory", WitnessCreateActuatorTest.dbPath }, TEST_CONF);
        WitnessCreateActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
        OWNER_ADDRESS_BALANCENOTSUFFIENT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e06d4271a1ced";
    }

    /**
     * first createWitness,result is success.
     */
    @Test
    public void firstCreateWitness() {
        WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST, WitnessCreateActuatorTest.URL), WitnessCreateActuatorTest.dbManager);
        AccountCapsule accountCapsule = WitnessCreateActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST));
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            WitnessCapsule witnessCapsule = WitnessCreateActuatorTest.dbManager.getWitnessStore().get(ByteArray.fromHexString(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST));
            Assert.assertNotNull(witnessCapsule);
            Assert.assertEquals(witnessCapsule.getInstance().getUrl(), WitnessCreateActuatorTest.URL);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * second createWitness,result is failed,exception is "Witness has existed".
     */
    @Test
    public void secondCreateAccount() {
        WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_SECOND, WitnessCreateActuatorTest.URL), WitnessCreateActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertFalse(true);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Witness[" + (WitnessCreateActuatorTest.OWNER_ADDRESS_SECOND)) + "] has existed"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use Invalid Address createWitness,result is failed,exception is "Invalid address".
     */
    @Test
    public void InvalidAddress() {
        WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_INVALID, WitnessCreateActuatorTest.URL), WitnessCreateActuatorTest.dbManager);
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

    /**
     * use Invalid url createWitness,result is failed,exception is "Invalid url".
     */
    @Test
    public void InvalidUrlTest() {
        TransactionResultCapsule ret = new TransactionResultCapsule();
        // Url cannot empty
        try {
            WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST, EMPTY), WitnessCreateActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid url");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid url", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // 256 bytes
        String url256Bytes = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        // Url length can not greater than 256
        try {
            WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST, ByteString.copyFromUtf8((url256Bytes + "0"))), WitnessCreateActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid url");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid url", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // 1 byte url is ok.
        try {
            WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST, "0"), WitnessCreateActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            WitnessCapsule witnessCapsule = WitnessCreateActuatorTest.dbManager.getWitnessStore().get(ByteArray.fromHexString(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST));
            Assert.assertNotNull(witnessCapsule);
            Assert.assertEquals(witnessCapsule.getInstance().getUrl(), "0");
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        WitnessCreateActuatorTest.dbManager.getWitnessStore().delete(ByteArray.fromHexString(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST));
        // 256 bytes url is ok.
        try {
            WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST, url256Bytes), WitnessCreateActuatorTest.dbManager);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            WitnessCapsule witnessCapsule = WitnessCreateActuatorTest.dbManager.getWitnessStore().get(ByteArray.fromHexString(WitnessCreateActuatorTest.OWNER_ADDRESS_FIRST));
            Assert.assertNotNull(witnessCapsule);
            Assert.assertEquals(witnessCapsule.getInstance().getUrl(), url256Bytes);
            Assert.assertTrue(true);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use AccountStore not exists Address createWitness,result is failed,exception is "account not
     * exists".
     */
    @Test
    public void noAccount() {
        WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_NOACCOUNT, WitnessCreateActuatorTest.URL), WitnessCreateActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("account[" + (WitnessCreateActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use Account  ,result is failed,exception is "account not exists".
     */
    @Test
    public void balanceNotSufficient() {
        AccountCapsule balanceNotSufficientCapsule = new AccountCapsule(ByteString.copyFromUtf8("balanceNotSufficient"), ByteString.copyFrom(ByteArray.fromHexString(WitnessCreateActuatorTest.OWNER_ADDRESS_BALANCENOTSUFFIENT)), AccountType.Normal, 50L);
        WitnessCreateActuatorTest.dbManager.getAccountStore().put(balanceNotSufficientCapsule.getAddress().toByteArray(), balanceNotSufficientCapsule);
        WitnessCreateActuator actuator = new WitnessCreateActuator(getContract(WitnessCreateActuatorTest.OWNER_ADDRESS_BALANCENOTSUFFIENT, WitnessCreateActuatorTest.URL), WitnessCreateActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("witnessAccount  has balance[" + (balanceNotSufficientCapsule.getBalance())) + "] < MIN_BALANCE[100]"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("balance < AccountUpgradeCost", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

