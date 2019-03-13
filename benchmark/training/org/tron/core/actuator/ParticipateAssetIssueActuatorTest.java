package org.tron.core.actuator;


import Constant.TEST_CONF;
import Contract.ParticipateAssetIssueContract;
import code.SUCESS;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Contract.AssetIssueContract;


public class ParticipateAssetIssueActuatorTest {
    private static final Logger logger = LoggerFactory.getLogger("Test");

    private static Manager dbManager;

    private static final String dbPath = "output_participateAsset_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String TO_ADDRESS;

    private static final String TO_ADDRESS_2;

    private static final String THIRD_ADDRESS;

    private static final String NOT_EXIT_ADDRESS;

    private static final String ASSET_NAME = "myCoin";

    private static final long OWNER_BALANCE = 99999;

    private static final long TO_BALANCE = 100001;

    private static final long TOTAL_SUPPLY = 10000000000000L;

    private static final int TRX_NUM = 2;

    private static final int NUM = 2147483647;

    private static final int VOTE_SCORE = 2;

    private static final String DESCRIPTION = "TRX";

    private static final String URL = "https://tron.network";

    static {
        Args.setParam(new String[]{ "--output-directory", ParticipateAssetIssueActuatorTest.dbPath }, TEST_CONF);
        ParticipateAssetIssueActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1234";
        TO_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        TO_ADDRESS_2 = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e048892";
        THIRD_ADDRESS = (Wallet.getAddressPreFixString()) + "4948c2e8a756d9437037dcd8c7e0c73d560ca38d";
        NOT_EXIT_ADDRESS = (Wallet.getAddressPreFixString()) + "B56446E617E924805E4D6CA021D341FEF6E2013B";
    }

    /**
     * SameTokenName close, success participate assert
     */
    @Test
    public void sameTokenNameCloseRightAssetIssue() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((ParticipateAssetIssueActuatorTest.OWNER_BALANCE) - 1000));
            Assert.assertEquals(toAccount.getBalance(), ((ParticipateAssetIssueActuatorTest.TO_BALANCE) + 1000));
            // V1
            Assert.assertEquals(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM))));
            // V2
            long tokenId = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM)));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * Init close SameTokenName,after init data,open SameTokenName
     */
    @Test
    public void OldNotUpdateSuccessAssetIssue() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((ParticipateAssetIssueActuatorTest.OWNER_BALANCE) - 1000));
            Assert.assertEquals(toAccount.getBalance(), ((ParticipateAssetIssueActuatorTest.TO_BALANCE) + 1000));
            // V1 data not update
            Assert.assertNull(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
            // V2
            long tokenId = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM)));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(tokenId)).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, success participate assert
     */
    @Test
    public void sameTokenNameOpenRightAssetIssue() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((ParticipateAssetIssueActuatorTest.OWNER_BALANCE) - 1000));
            Assert.assertEquals(toAccount.getBalance(), ((ParticipateAssetIssueActuatorTest.TO_BALANCE) + 1000));
            // V1, data is not exist
            Assert.assertNull(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME));
            Assert.assertNull(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME));
            // V2
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(id)).longValue(), ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM)));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, check asset start time and end time
     */
    @Test
    public void sameTokenNameCloseAssetIssueTimeRight() {
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("No longer valid period!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, check asset start time and end time
     */
    @Test
    public void sameTokenNameOpenAssetIssueTimeRight() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("No longer valid period!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, check asset left time
     */
    @Test
    public void sameTokenNameCloseAssetIssueTimeLeft() {
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("No longer valid period!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, check asset left time
     */
    @Test
    public void sameTokenNameOpenAssetIssueTimeLeft() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("No longer valid period!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, exchange devisible
     */
    @Test
    public void sameTokenNameCloseExchangeDevisibleTest() {
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(999L), ParticipateAssetIssueActuatorTest.dbManager);// no problem

        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ((999L * (ParticipateAssetIssueActuatorTest.NUM)) / (ParticipateAssetIssueActuatorTest.TRX_NUM)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((999L * (ParticipateAssetIssueActuatorTest.NUM)) / (ParticipateAssetIssueActuatorTest.TRX_NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, exchange devisible
     */
    @Test
    public void sameTokenNameOpenExchangeDevisibleTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(999L), ParticipateAssetIssueActuatorTest.dbManager);// no problem

        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(id)).longValue(), ((999L * (ParticipateAssetIssueActuatorTest.NUM)) / (ParticipateAssetIssueActuatorTest.TRX_NUM)));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((999L * (ParticipateAssetIssueActuatorTest.NUM)) / (ParticipateAssetIssueActuatorTest.TRX_NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, nagative amount
     */
    @Test
    public void sameTokenNameCloseNegativeAmountTest() {
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract((-999L)), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Amount must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, nagative amount
     */
    @Test
    public void sameTokenNameOpenNegativeAmountTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract((-999L)), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Amount must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, zere amount
     */
    @Test
    public void sameTokenNameCloseZeroAmountTest() {
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(0), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Amount must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, zere amount
     */
    @Test
    public void sameTokenNameOpenZeroAmountTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(0), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Amount must greater than 0!".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, Owner account is not exit
     */
    @Test
    public void sameTokenNameCloseNoExitOwnerTest() {
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithOwner(101, ParticipateAssetIssueActuatorTest.NOT_EXIT_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Account does not exist!", e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, Owner account is not exit
     */
    @Test
    public void sameTokenNameOpenNoExitOwnerTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithOwner(101, ParticipateAssetIssueActuatorTest.NOT_EXIT_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Account does not exist!", e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, To account is not exit.
     */
    @Test
    public void sameTokenNameCloseNoExitToTest() {
        initAssetIssueWithOwner(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000), ParticipateAssetIssueActuatorTest.NOT_EXIT_ADDRESS);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(101, ParticipateAssetIssueActuatorTest.NOT_EXIT_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("To account does not exist!", e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, To account is not exit.
     */
    @Test
    public void sameTokenNameOpenNoExitToTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssueWithOwner(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000), ParticipateAssetIssueActuatorTest.NOT_EXIT_ADDRESS);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(101, ParticipateAssetIssueActuatorTest.NOT_EXIT_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("To account does not exist!", e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, Participate to self, will throw exception.
     */
    @Test
    public void sameTokenNameCloseParticipateAssetSelf() {
        initAssetIssueWithOwner(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000), ParticipateAssetIssueActuatorTest.OWNER_ADDRESS);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(101, ParticipateAssetIssueActuatorTest.OWNER_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Cannot participate asset Issue yourself !", e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, Participate to self, will throw exception.
     */
    @Test
    public void sameTokenNameOpenParticipateAssetSelf() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssueWithOwner(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000), ParticipateAssetIssueActuatorTest.OWNER_ADDRESS);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(101, ParticipateAssetIssueActuatorTest.OWNER_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Cannot participate asset Issue yourself !", e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, Participate to the third party that not the issuer, will throw exception.
     */
    @Test
    public void sameTokenNameCloseParticipateAssetToThird() {
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(101, ParticipateAssetIssueActuatorTest.THIRD_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(("The asset is not issued by " + (ParticipateAssetIssueActuatorTest.THIRD_ADDRESS)), e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, Participate to the third party that not the issuer, will throw exception.
     */
    @Test
    public void sameTokenNameOpenParticipateAssetToThird() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(101, ParticipateAssetIssueActuatorTest.THIRD_ADDRESS), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals(("The asset is not issued by " + (ParticipateAssetIssueActuatorTest.THIRD_ADDRESS)), e.getMessage());
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /* Asset name length must between 1 to 32 and can not contain space and other unreadable character, and can not contain chinese characters. */
    // asset name validation which is unnecessary has been removed!
    @Test
    public void assetNameTest() {
        // Empty name, throw exception
        ByteString emptyName = ByteString.EMPTY;
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1000L, emptyName), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("No asset named null", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        String assetName = "testname0123456789abcdefghijgklmo";
        // 32 byte readable character just ok.
        assetName = "testname0123456789abcdefghijgklm";
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000), assetName);
        actuator = new ParticipateAssetIssueActuator(getContract(1000L, assetName), ParticipateAssetIssueActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((ParticipateAssetIssueActuatorTest.OWNER_BALANCE) - 1000));
            Assert.assertEquals(toAccount.getBalance(), ((ParticipateAssetIssueActuatorTest.TO_BALANCE) + 1000));
            Assert.assertEquals(owner.getAssetMap().get(assetName).longValue(), ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM)));
            Assert.assertEquals(toAccount.getAssetMap().get(assetName).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
        // 1 byte readable character ok.
        assetName = "t";
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000), assetName);
        actuator = new ParticipateAssetIssueActuator(getContract(1000L, assetName), ParticipateAssetIssueActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ((ParticipateAssetIssueActuatorTest.OWNER_BALANCE) - 2000));
            Assert.assertEquals(toAccount.getBalance(), ((ParticipateAssetIssueActuatorTest.TO_BALANCE) + 2000));
            Assert.assertEquals(owner.getAssetMap().get(assetName).longValue(), ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM)));
            Assert.assertEquals(toAccount.getAssetMap().get(assetName).longValue(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - ((1000L / (ParticipateAssetIssueActuatorTest.TRX_NUM)) * (ParticipateAssetIssueActuatorTest.NUM))));
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, not enough trx
     */
    @Test
    public void sameTokenNameCloseNotEnoughTrxTest() {
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, reduce the owner trx balance. Else can't complete this test case.
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(100);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(101), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("No enough balance !".equals(e.getMessage()));
            owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 100);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, not enough trx
     */
    @Test
    public void sameTokenNameOpenNotEnoughTrxTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, reduce the owner trx balance. Else can't complete this test case.
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(100);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(101), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("No enough balance !".equals(e.getMessage()));
            owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 100);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, not enough asset
     */
    @Test
    public void sameTokenNameCloseNotEnoughAssetTest() {
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, reduce to account asset balance. Else can't complete this test case.
        AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
        toAccount.reduceAssetAmount(ByteString.copyFromUtf8(ParticipateAssetIssueActuatorTest.ASSET_NAME).toByteArray(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - 10000));
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(toAccount.getAddress().toByteArray(), toAccount);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Asset balance is not enough !".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), 10000);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, not enough asset
     */
    @Test
    public void sameTokenNameOpenNotEnoughAssetTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, reduce to account asset balance. Else can't complete this test case.
        AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
        long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        toAccount.reduceAssetAmountV2(ByteString.copyFromUtf8(String.valueOf(id)).toByteArray(), ((ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY) - 10000), ParticipateAssetIssueActuatorTest.dbManager);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(toAccount.getAddress().toByteArray(), toAccount);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Asset balance is not enough !".equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), 10000);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, nont exist asset
     */
    @Test
    public void sameTokenNameCloseNoneExistAssetTest() {
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1, "TTTTTTTTTTTT"), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue(("No asset named " + "TTTTTTTTTTTT").equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, nont exist asset
     */
    @Test
    public void sameTokenNameOpenNoneExistAssetTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        DateTime now = DateTime.now();
        initAssetIssue(now.minusDays(1).getMillis(), now.plusDays(1).getMillis());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1, "TTTTTTTTTTTT"), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue(("No asset named " + "TTTTTTTTTTTT").equals(e.getMessage()));
            AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, add over flow
     */
    @Test
    public void sameTokenNameCloseAddOverflowTest() {
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, increase the owner asset balance. Else can't complete this test case.
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.addAsset(ParticipateAssetIssueActuatorTest.ASSET_NAME.getBytes(), Long.MAX_VALUE);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1L), ParticipateAssetIssueActuatorTest.dbManager);
        // NUM = 2147483647;
        // ASSET_BLANCE = Long.MAX_VALUE + 2147483647/2
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
            Assert.assertTrue("long overflow".equals(e.getMessage()));
            owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertEquals(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), Long.MAX_VALUE);
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        }
    }

    /**
     * SameTokenName open, add over flow
     */
    @Test
    public void sameTokenNameOpenAddOverflowTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, increase the owner asset balance. Else can't complete this test case.
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        owner.addAssetV2(ByteString.copyFromUtf8(String.valueOf(id)).toByteArray(), Long.MAX_VALUE);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1L), ParticipateAssetIssueActuatorTest.dbManager);
        // NUM = 2147483647;
        // ASSET_BLANCE = Long.MAX_VALUE + 2147483647/2
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertTrue((e instanceof ContractExeException));
            Assert.assertTrue("long overflow".equals(e.getMessage()));
            owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), ParticipateAssetIssueActuatorTest.OWNER_BALANCE);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertEquals(owner.getAssetMapV2().get(String.valueOf(id)).longValue(), Long.MAX_VALUE);
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        }
    }

    /**
     * SameTokenName close, multiply over flow
     */
    @Test
    public void sameTokenNameCloseMultiplyOverflowTest() {
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, increase the owner trx balance. Else can't complete this test case.
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(100000000000000L);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(8589934597L), ParticipateAssetIssueActuatorTest.dbManager);
        // NUM = 2147483647;
        // LONG_MAX = 9223372036854775807L = 0x7fffffffffffffff
        // 4294967298 * 2147483647 = 9223372036854775806 = 0x7ffffffffffffffe
        // 8589934596 * 2147483647 = 4294967298 * 2147483647 *2 = 0xfffffffffffffffc = -4
        // 8589934597 * 2147483647 = 8589934596 * 2147483647 + 2147483647 = -4 + 2147483647 = 2147483643  vs 9223372036854775806*2 + 2147483647
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("long overflow".equals(e.getMessage()));
            owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 100000000000000L);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            Assert.assertTrue(isNullOrZero(owner.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME)));
            Assert.assertEquals(toAccount.getAssetMap().get(ParticipateAssetIssueActuatorTest.ASSET_NAME).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, multiply over flow
     */
    @Test
    public void sameTokenNameOpenMultiplyOverflowTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        initAssetIssue(((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) - 1000), ((ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getLatestBlockHeaderTimestamp()) + 1000));
        // First, increase the owner trx balance. Else can't complete this test case.
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(100000000000000L);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(8589934597L), ParticipateAssetIssueActuatorTest.dbManager);
        // NUM = 2147483647;
        // LONG_MAX = 9223372036854775807L = 0x7fffffffffffffff
        // 4294967298 * 2147483647 = 9223372036854775806 = 0x7ffffffffffffffe
        // 8589934596 * 2147483647 = 4294967298 * 2147483647 *2 = 0xfffffffffffffffc = -4
        // 8589934597 * 2147483647 = 8589934596 * 2147483647 + 2147483647 = -4 + 2147483647 = 2147483643  vs 9223372036854775806*2 + 2147483647
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("long overflow".equals(e.getMessage()));
            owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
            AccountCapsule toAccount = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
            Assert.assertEquals(owner.getBalance(), 100000000000000L);
            Assert.assertEquals(toAccount.getBalance(), ParticipateAssetIssueActuatorTest.TO_BALANCE);
            long id = ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
            Assert.assertTrue(isNullOrZero(owner.getAssetMapV2().get(String.valueOf(id))));
            Assert.assertEquals(toAccount.getAssetMapV2().get(String.valueOf(id)).longValue(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, exchangeAmount <= 0 trx, throw exception
     */
    @Test
    public void sameTokenNameCloseExchangeAmountTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000000);
        AssetIssueContract assetIssueContract = AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS))).setName(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.ASSET_NAME))).setTotalSupply(ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(100).setNum(1).setStartTime(((ParticipateAssetIssueActuatorTest.dbManager.getHeadBlockTimeStamp()) - 10000)).setEndTime(((ParticipateAssetIssueActuatorTest.dbManager.getHeadBlockTimeStamp()) + 11000000)).setVoteScore(ParticipateAssetIssueActuatorTest.VOTE_SCORE).setDescription(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.DESCRIPTION))).setUrl(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.URL))).build();
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(assetIssueContract);
        ParticipateAssetIssueActuatorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule.createDbKey(), assetIssueCapsule);
        AccountCapsule toAccountCapsule = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS));
        toAccountCapsule.addAsset(ParticipateAssetIssueActuatorTest.ASSET_NAME.getBytes(), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(toAccountCapsule.getAddress().toByteArray(), toAccountCapsule);
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(100000000000000L);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContract(1), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertTrue("Can not process the exchange!".equals(e.getMessage()));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName open, exchangeAmount <= 0 trx, throw exception
     */
    @Test
    public void sameTokenNameOpenExchangeAmountTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1000000);
        long tokenId = (ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum()) + 1;
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveTokenIdNum(tokenId);
        AssetIssueContract assetIssueContract = AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS_2))).setName(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.ASSET_NAME))).setTotalSupply(ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY).setTrxNum(100).setId(String.valueOf(tokenId)).setNum(1).setStartTime(((ParticipateAssetIssueActuatorTest.dbManager.getHeadBlockTimeStamp()) - 10000)).setEndTime(((ParticipateAssetIssueActuatorTest.dbManager.getHeadBlockTimeStamp()) + 11000000)).setVoteScore(ParticipateAssetIssueActuatorTest.VOTE_SCORE).setDescription(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.DESCRIPTION))).setUrl(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.URL))).build();
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(assetIssueContract);
        ParticipateAssetIssueActuatorTest.dbManager.getAssetIssueV2Store().put(assetIssueCapsule.createDbV2Key(), assetIssueCapsule);
        AccountCapsule toAccountCapsule = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS_2));
        toAccountCapsule.addAssetV2(ByteArray.fromString(String.valueOf(tokenId)), ParticipateAssetIssueActuatorTest.TOTAL_SUPPLY);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(toAccountCapsule.getAddress().toByteArray(), toAccountCapsule);
        AccountCapsule owner = ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS));
        owner.setBalance(100000000000000L);
        ParticipateAssetIssueActuatorTest.dbManager.getAccountStore().put(owner.getAddress().toByteArray(), owner);
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(getContractWithTo(1, ParticipateAssetIssueActuatorTest.TO_ADDRESS_2), ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            System.out.println(("e:" + (e.getMessage())));
            Assert.assertTrue("Can not process the exchange!".equals(e.getMessage()));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, invalid oweraddress
     */
    @Test
    public void sameTokenNameCloseInvalidOwerAddressTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        Any any = Any.pack(ParticipateAssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString("12131312"))).setToAddress(ByteString.copyFrom(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.TO_ADDRESS))).setAssetName(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.ASSET_NAME))).setAmount(1000).build());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(any, ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            System.out.println(("e:" + (e.getMessage())));
            Assert.assertTrue("Invalid ownerAddress".equals(e.getMessage()));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }

    /**
     * SameTokenName close, invalid Invalid toAddress
     */
    @Test
    public void sameTokenNameCloseInvalidToAddressTest() {
        ParticipateAssetIssueActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        Any any = Any.pack(ParticipateAssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(ParticipateAssetIssueActuatorTest.OWNER_ADDRESS))).setToAddress(ByteString.copyFrom(ByteArray.fromHexString("12313123"))).setAssetName(ByteString.copyFrom(ByteArray.fromString(ParticipateAssetIssueActuatorTest.ASSET_NAME))).setAmount(1000).build());
        ParticipateAssetIssueActuator actuator = new ParticipateAssetIssueActuator(any, ParticipateAssetIssueActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertTrue(false);
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            System.out.println(("e:" + (e.getMessage())));
            Assert.assertTrue("Invalid toAddress".equals(e.getMessage()));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        }
    }
}

