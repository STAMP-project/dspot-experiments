package org.tron.core.actuator;


import Constant.TEST_CONF;
import State.CANCELED;
import State.PENDING;
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
import org.tron.core.capsule.ProposalCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ItemNotFoundException;


@Slf4j
public class ProposalDeleteActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_ProposalApprove_test";

    private static final String ACCOUNT_NAME_FIRST = "ownerF";

    private static final String OWNER_ADDRESS_FIRST;

    private static final String ACCOUNT_NAME_SECOND = "ownerS";

    private static final String OWNER_ADDRESS_SECOND;

    private static final String URL = "https://tron.network";

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ADDRESS_NOACCOUNT;

    static {
        Args.setParam(new String[]{ "--output-directory", ProposalDeleteActuatorTest.dbPath }, TEST_CONF);
        ProposalDeleteActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
    }

    /**
     * first deleteProposal, result is success.
     */
    @Test
    public void successDeleteApprove() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_FIRST, id), ProposalDeleteActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalDeleteActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getState(), PENDING);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            try {
                proposalCapsule = ProposalDeleteActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
            } catch (ItemNotFoundException e) {
                Assert.assertFalse((e instanceof ItemNotFoundException));
                return;
            }
            Assert.assertEquals(proposalCapsule.getState(), CANCELED);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use Invalid Address, result is failed, exception is "Invalid address".
     */
    @Test
    public void invalidAddress() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_INVALID, id), ProposalDeleteActuatorTest.dbManager);
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
     * use Account not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void noAccount() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_NOACCOUNT, id), ProposalDeleteActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (ProposalDeleteActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Proposal is not proposed by witness, result is failed,exception is "witness not exists".
     */
    @Test
    public void notProposed() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_SECOND, id), ProposalDeleteActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("witness[+OWNER_ADDRESS_NOWITNESS+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((((("Proposal[" + id) + "] ") + "is not proposed by ") + (StringUtil.createReadableString(ByteString.copyFrom(ByteArray.fromHexString(ProposalDeleteActuatorTest.OWNER_ADDRESS_SECOND))))), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use Proposal not exists, result is failed, exception is "Proposal not exists".
     */
    @Test
    public void noProposal() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 2;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_FIRST, id), ProposalDeleteActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Proposal[" + id) + "] not exists"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Proposal[" + id) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Proposal expired, result is failed, exception is "Proposal expired".
     */
    @Test
    public void proposalExpired() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(261200100);
        long id = 1;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_FIRST, id), ProposalDeleteActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Proposal[" + id) + "] expired"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Proposal[" + id) + "] expired"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Proposal canceled, result is failed, exception is "Proposal expired".
     */
    @Test
    public void proposalCanceled() {
        ProposalDeleteActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(100100);
        long id = 1;
        ProposalDeleteActuator actuator = new ProposalDeleteActuator(getContract(ProposalDeleteActuatorTest.OWNER_ADDRESS_FIRST, id), ProposalDeleteActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalDeleteActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
            proposalCapsule.setState(CANCELED);
            ProposalDeleteActuatorTest.dbManager.getProposalStore().put(proposalCapsule.createDbKey(), proposalCapsule);
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Proposal[" + id) + "] canceled"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Proposal[" + id) + "] canceled"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

