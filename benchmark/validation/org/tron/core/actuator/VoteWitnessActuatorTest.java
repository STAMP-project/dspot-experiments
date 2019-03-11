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
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.witness.WitnessController;
import org.tron.protos.Protocol.AccountType;


@Slf4j
public class VoteWitnessActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static WitnessController witnessController;

    private static final String dbPath = "output_VoteWitness_test";

    private static final String ACCOUNT_NAME = "account";

    private static final String OWNER_ADDRESS;

    private static final String WITNESS_NAME = "witness";

    private static final String WITNESS_ADDRESS;

    private static final String URL = "https://tron.network";

    private static final String ADDRESS_INVALID = "aaaa";

    private static final String WITNESS_ADDRESS_NOACCOUNT;

    private static final String OWNER_ADDRESS_NOACCOUNT;

    private static final String OWNER_ADDRESS_BALANCENOTSUFFICIENT;

    static {
        Args.setParam(new String[]{ "--output-directory", VoteWitnessActuatorTest.dbPath }, TEST_CONF);
        VoteWitnessActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        WITNESS_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        WITNESS_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aed";
        OWNER_ADDRESS_NOACCOUNT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1aae";
        OWNER_ADDRESS_BALANCENOTSUFFICIENT = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e06d4271a1ced";
    }

    /**
     * voteWitness,result is success.
     */
    @Test
    public void voteWitness() {
        long frozenBalance = 1000000000000L;
        long duration = 3;
        FreezeBalanceActuator freezeBalanceActuator = new FreezeBalanceActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, frozenBalance, duration), VoteWitnessActuatorTest.dbManager);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            freezeBalanceActuator.validate();
            freezeBalanceActuator.execute(ret);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(1, VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().get(0).getVoteCount());
            Assert.assertArrayEquals(ByteArray.fromHexString(VoteWitnessActuatorTest.WITNESS_ADDRESS), VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().get(0).getVoteAddress().toByteArray());
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals((10 + 1), witnessCapsule.getVoteCount());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use Invalid ownerAddress voteWitness,result is failed,exception is "Invalid address".
     */
    @Test
    public void InvalidAddress() {
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.ADDRESS_INVALID, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid address");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid address", e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use AccountStore not exists witness Address VoteWitness,result is failed,exception is "account
     * not exists".
     */
    @Test
    public void noAccount() {
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Account[" + (VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT)) + "] not exists"));
        } catch (ContractValidateException e) {
            Assert.assertEquals(0, VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().size());
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use WitnessStore not exists Address VoteWitness,result is failed,exception is "Witness not
     * exists".
     */
    @Test
    public void noWitness() {
        AccountCapsule accountSecondCapsule = new AccountCapsule(ByteString.copyFromUtf8(VoteWitnessActuatorTest.WITNESS_NAME), StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT), AccountType.Normal, 300L);
        VoteWitnessActuatorTest.dbManager.getAccountStore().put(accountSecondCapsule.getAddress().toByteArray(), accountSecondCapsule);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Witness[" + (VoteWitnessActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"));
        } catch (ContractValidateException e) {
            Assert.assertEquals(0, VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().size());
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Witness[" + (VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * invalideVoteAddress
     */
    @Test
    public void invalideVoteAddress() {
        AccountCapsule accountSecondCapsule = new AccountCapsule(ByteString.copyFromUtf8(VoteWitnessActuatorTest.WITNESS_NAME), StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS_NOACCOUNT), AccountType.Normal, 300L);
        VoteWitnessActuatorTest.dbManager.getAccountStore().put(accountSecondCapsule.getAddress().toByteArray(), accountSecondCapsule);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.ADDRESS_INVALID, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertEquals(0, VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().size());
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid vote address!", e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Every vote count must greater than 0.
     */
    @Test
    public void voteCountTest() {
        long frozenBalance = 1000000000000L;
        long duration = 3;
        FreezeBalanceActuator freezeBalanceActuator = new FreezeBalanceActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, frozenBalance, duration), VoteWitnessActuatorTest.dbManager);
        // 0 votes
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 0L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            freezeBalanceActuator.validate();
            freezeBalanceActuator.execute(ret);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("vote count must be greater than 0", e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // -1 votes
        actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, (-1L)), VoteWitnessActuatorTest.dbManager);
        ret = new TransactionResultCapsule();
        try {
            freezeBalanceActuator.validate();
            freezeBalanceActuator.execute(ret);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("vote count must be greater than 0", e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * User can vote to 1 - 30 witnesses.
     */
    @Test
    public void voteCountsTest() {
        long frozenBalance = 1000000000000L;
        long duration = 3;
        FreezeBalanceActuator freezeBalanceActuator = new FreezeBalanceActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, frozenBalance, duration), VoteWitnessActuatorTest.dbManager);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getRepeateContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L, 0), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            freezeBalanceActuator.validate();
            freezeBalanceActuator.execute(ret);
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("VoteNumber must more than 0", e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        actuator = new VoteWitnessActuator(getRepeateContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L, 31), VoteWitnessActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("VoteNumber more than maxVoteNumber 30", e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Vote 1 witness one more times.
     */
    @Test
    public void vote1WitnssOneMoreTiems() {
        long frozenBalance = 1000000000000L;
        long duration = 3;
        FreezeBalanceActuator freezeBalanceActuator = new FreezeBalanceActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, frozenBalance, duration), VoteWitnessActuatorTest.dbManager);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getRepeateContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L, 30), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            freezeBalanceActuator.validate();
            freezeBalanceActuator.execute(ret);
            actuator.validate();
            actuator.execute(ret);
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals((10 + 30), witnessCapsule.getVoteCount());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * use AccountStore not exists ownerAddress VoteWitness,result is failed,exception is "account not
     * exists".
     */
    @Test
    public void noOwnerAccount() {
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS_NOACCOUNT, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((("Account[" + (VoteWitnessActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"));
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((("Account[" + (VoteWitnessActuatorTest.OWNER_ADDRESS_NOACCOUNT)) + "] not exists"), e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * witnessAccount not freeze Balance, result is failed ,exception is "The total number of votes
     * 1000000 is greater than 0.
     */
    @Test
    public void balanceNotSufficient() {
        AccountCapsule balanceNotSufficientCapsule = new AccountCapsule(ByteString.copyFromUtf8("balanceNotSufficient"), StringUtil.hexString2ByteString(VoteWitnessActuatorTest.OWNER_ADDRESS_BALANCENOTSUFFICIENT), AccountType.Normal, 500L);
        VoteWitnessActuatorTest.dbManager.getAccountStore().put(balanceNotSufficientCapsule.getAddress().toByteArray(), balanceNotSufficientCapsule);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS_BALANCENOTSUFFICIENT, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail((((("The total number of votes[" + 1000000) + "] is greater than the tronPower[") + (balanceNotSufficientCapsule.getTronPower())) + "]"));
        } catch (ContractValidateException e) {
            Assert.assertEquals(0, VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS_BALANCENOTSUFFICIENT)).getVotesList().size());
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals((((("The total number of votes[" + 1000000) + "] is greater than the tronPower[") + (balanceNotSufficientCapsule.getTronPower())) + "]"), e.getMessage());
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(10, witnessCapsule.getVoteCount());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Twice voteWitness,result is the last voteWitness.
     */
    @Test
    public void voteWitnessTwice() {
        long frozenBalance = 7000000000000L;
        long duration = 3;
        FreezeBalanceActuator freezeBalanceActuator = new FreezeBalanceActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, frozenBalance, duration), VoteWitnessActuatorTest.dbManager);
        VoteWitnessActuator actuator = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 1L), VoteWitnessActuatorTest.dbManager);
        VoteWitnessActuator actuatorTwice = new VoteWitnessActuator(getContract(VoteWitnessActuatorTest.OWNER_ADDRESS, VoteWitnessActuatorTest.WITNESS_ADDRESS, 3L), VoteWitnessActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            freezeBalanceActuator.validate();
            freezeBalanceActuator.execute(ret);
            actuator.validate();
            actuator.execute(ret);
            actuatorTwice.validate();
            actuatorTwice.execute(ret);
            Assert.assertEquals(3, VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().get(0).getVoteCount());
            Assert.assertArrayEquals(ByteArray.fromHexString(VoteWitnessActuatorTest.WITNESS_ADDRESS), VoteWitnessActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(VoteWitnessActuatorTest.OWNER_ADDRESS)).getVotesList().get(0).getVoteAddress().toByteArray());
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            VoteWitnessActuatorTest.witnessController.updateWitness();
            WitnessCapsule witnessCapsule = VoteWitnessActuatorTest.witnessController.getWitnesseByAddress(StringUtil.hexString2ByteString(VoteWitnessActuatorTest.WITNESS_ADDRESS));
            Assert.assertEquals(13, witnessCapsule.getVoteCount());
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

