package org.tron.core.actuator;


import Constant.TEST_CONF;
import State.CANCELED;
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
public class ProposalApproveActuatorTest {
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
        Args.setParam(new String[]{ "--output-directory", ProposalApproveActuatorTest.dbPath }, TEST_CONF);
        ProposalApproveActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS_FIRST = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_SECOND = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
    }

    /**
     * first approveProposal, result is success.
     */
    @Test
    public void successProposalApprove() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, id, true), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            try {
                proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
            } catch (ItemNotFoundException e) {
                Assert.assertFalse((e instanceof ItemNotFoundException));
                return;
            }
            Assert.assertEquals(proposalCapsule.getApprovals().size(), 1);
            Assert.assertEquals(proposalCapsule.getApprovals().get(0), ByteString.copyFrom(ByteArray.fromHexString(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST)));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // isAddApproval == false
        ProposalApproveActuator actuator2 = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, 1, false), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret2 = new TransactionResultCapsule();
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 1);
        try {
            actuator2.validate();
            actuator2.execute(ret2);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            try {
                proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
            } catch (ItemNotFoundException e) {
                Assert.assertFalse((e instanceof ItemNotFoundException));
                return;
            }
            Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
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
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_INVALID, id, true), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
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
     * use AccountStore not exists, result is failed, exception is "account not exists".
     */
    @Test
    public void noAccount() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_NOACCOUNT, id, true), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("account[+OWNER_ADDRESS_NOACCOUNT+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (ProposalApproveActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use WitnessStore not exists Address,result is failed,exception is "witness not exists".
     */
    @Test
    public void noWitness() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_SECOND, id, true), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("witness[+OWNER_ADDRESS_NOWITNESS+] not exists");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Witness[" + (ProposalApproveActuatorTest.OWNER_ADDRESS_SECOND)) + "] not exists"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use Proposal not exists, result is failed, exception is "Proposal not exists".
     */
    @Test
    public void noProposal() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 2;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, id, true), ProposalApproveActuatorTest.dbManager);
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
     * duplicate approval, result is failed, exception is "Proposal not exists".
     */
    @Test
    public void duplicateApproval() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000100);
        long id = 1;
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, id, true), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        proposalCapsule.addApproval(ByteString.copyFrom(ByteArray.fromHexString(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST)));
        ProposalApproveActuatorTest.dbManager.getProposalStore().put(proposalCapsule.createDbKey(), proposalCapsule);
        String readableOwnerAddress = StringUtil.createReadableString(ByteString.copyFrom(ByteArray.fromHexString(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST)));
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((((("witness [" + readableOwnerAddress) + "]has approved proposal[") + id) + "] before"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("Witness[" + readableOwnerAddress) + "]has approved ") + "proposal[") + id) + "] before"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Proposal expired, result is failed, exception is "Proposal expired".
     */
    @Test
    public void proposalExpired() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(261200100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, id, true), ProposalApproveActuatorTest.dbManager);
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
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(100100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, id, true), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
            proposalCapsule.setState(CANCELED);
            ProposalApproveActuatorTest.dbManager.getProposalStore().put(proposalCapsule.createDbKey(), proposalCapsule);
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

    /**
     * if !isAddApproval, and proposal not approved before, result is failed, exception is "Proposal
     * expired".
     */
    @Test
    public void proposalNotApproved() {
        ProposalApproveActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(100100);
        long id = 1;
        // isAddApproval == true
        ProposalApproveActuator actuator = new ProposalApproveActuator(getContract(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST, id, false), ProposalApproveActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        String readableOwnerAddress = StringUtil.createReadableString(ByteString.copyFrom(ByteArray.fromHexString(ProposalApproveActuatorTest.OWNER_ADDRESS_FIRST)));
        ProposalCapsule proposalCapsule;
        try {
            proposalCapsule = ProposalApproveActuatorTest.dbManager.getProposalStore().get(ByteArray.fromLong(id));
            ProposalApproveActuatorTest.dbManager.getProposalStore().put(proposalCapsule.createDbKey(), proposalCapsule);
        } catch (ItemNotFoundException e) {
            Assert.assertFalse((e instanceof ItemNotFoundException));
            return;
        }
        Assert.assertEquals(proposalCapsule.getApprovals().size(), 0);
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((((("witness [" + readableOwnerAddress) + "]has not approved proposal[") + id) + "] before"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(((((("Witness[" + readableOwnerAddress) + "]has not approved ") + "proposal[") + id) + "] before"), e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

