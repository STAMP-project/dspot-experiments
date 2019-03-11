package org.tron.core.actuator;


import Constant.TEST_CONF;
import Protocol.Transaction.Result.code.SUCESS;
import com.google.protobuf.ByteString;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;


@Slf4j
public class UpdateAssetActuatorTest {
    private static TronApplicationContext context;

    private static Manager dbManager;

    private static final String dbPath = "output_updateAsset_test";

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_ACCOUNT_NAME = "test_account";

    private static final String SECOND_ACCOUNT_ADDRESS;

    private static final String OWNER_ADDRESS_NOTEXIST;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String NAME = "trx-my";

    private static final long TOTAL_SUPPLY = 10000L;

    private static final String DESCRIPTION = "myCoin";

    private static final String URL = "tron-my.com";

    static {
        Args.setParam(new String[]{ "--output-directory", UpdateAssetActuatorTest.dbPath }, TEST_CONF);
        UpdateAssetActuatorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        OWNER_ADDRESS_NOTEXIST = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        SECOND_ACCOUNT_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d427122222";
    }

    @Test
    public void successUpdateAssetBeforeSameTokenNameActive() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UpdateAssetActuator actuator;
        actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1
            AssetIssueCapsule assetIssueCapsule = UpdateAssetActuatorTest.dbManager.getAssetIssueStore().get(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertEquals(UpdateAssetActuatorTest.DESCRIPTION, assetIssueCapsule.getInstance().getDescription().toStringUtf8());
            Assert.assertEquals(UpdateAssetActuatorTest.URL, assetIssueCapsule.getInstance().getUrl().toStringUtf8());
            Assert.assertEquals(assetIssueCapsule.getFreeAssetNetLimit(), 500L);
            Assert.assertEquals(assetIssueCapsule.getPublicFreeAssetNetLimit(), 8000L);
            // V2
            AssetIssueCapsule assetIssueCapsuleV2 = UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().get(ByteArray.fromString(String.valueOf(tokenId)));
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(UpdateAssetActuatorTest.DESCRIPTION, assetIssueCapsuleV2.getInstance().getDescription().toStringUtf8());
            Assert.assertEquals(UpdateAssetActuatorTest.URL, assetIssueCapsuleV2.getInstance().getUrl().toStringUtf8());
            Assert.assertEquals(assetIssueCapsuleV2.getFreeAssetNetLimit(), 500L);
            Assert.assertEquals(assetIssueCapsuleV2.getPublicFreeAssetNetLimit(), 8000L);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    /**
     * Init close SameTokenName,after init data,open SameTokenName
     */
    @Test
    public void oldNotUpdataSuccessUpdateAsset() {
        createAssertBeforSameTokenNameActive();
        UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UpdateAssetActuator actuator;
        actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1 old version exist but  not updata
            AssetIssueCapsule assetIssueCapsule = UpdateAssetActuatorTest.dbManager.getAssetIssueStore().get(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
            Assert.assertNotNull(assetIssueCapsule);
            Assert.assertNotEquals(UpdateAssetActuatorTest.DESCRIPTION, assetIssueCapsule.getInstance().getDescription().toStringUtf8());
            Assert.assertNotEquals(UpdateAssetActuatorTest.URL, assetIssueCapsule.getInstance().getUrl().toStringUtf8());
            Assert.assertNotEquals(assetIssueCapsule.getFreeAssetNetLimit(), 500L);
            Assert.assertNotEquals(assetIssueCapsule.getPublicFreeAssetNetLimit(), 8000L);
            // V2
            AssetIssueCapsule assetIssueCapsuleV2 = UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().get(ByteArray.fromString(String.valueOf(tokenId)));
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(UpdateAssetActuatorTest.DESCRIPTION, assetIssueCapsuleV2.getInstance().getDescription().toStringUtf8());
            Assert.assertEquals(UpdateAssetActuatorTest.URL, assetIssueCapsuleV2.getInstance().getUrl().toStringUtf8());
            Assert.assertEquals(assetIssueCapsuleV2.getFreeAssetNetLimit(), 500L);
            Assert.assertEquals(assetIssueCapsuleV2.getPublicFreeAssetNetLimit(), 8000L);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    @Test
    public void successUpdateAssetAfterSameTokenNameActive() {
        createAssertSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        TransactionResultCapsule ret = new TransactionResultCapsule();
        UpdateAssetActuator actuator;
        actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        try {
            actuator.validate();
            actuator.execute(ret);
            Assert.assertEquals(ret.getInstance().getRet(), SUCESS);
            // V1?Data is no longer update
            AssetIssueCapsule assetIssueCapsule = UpdateAssetActuatorTest.dbManager.getAssetIssueStore().get(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
            Assert.assertNull(assetIssueCapsule);
            // V2
            AssetIssueCapsule assetIssueCapsuleV2 = UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().get(ByteArray.fromString(String.valueOf(tokenId)));
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(UpdateAssetActuatorTest.DESCRIPTION, assetIssueCapsuleV2.getInstance().getDescription().toStringUtf8());
            Assert.assertEquals(UpdateAssetActuatorTest.URL, assetIssueCapsuleV2.getInstance().getUrl().toStringUtf8());
            Assert.assertEquals(assetIssueCapsuleV2.getFreeAssetNetLimit(), 500L);
            Assert.assertEquals(assetIssueCapsuleV2.getPublicFreeAssetNetLimit(), 8000L);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    @Test
    public void invalidAddress() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS_INVALID, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid ownerAddress");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid ownerAddress", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    @Test
    public void noExistAccount() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS_NOTEXIST, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Account has not existed");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Account has not existed", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    @Test
    public void noAsset() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.SECOND_ACCOUNT_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Account has not issue any asset");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Account has not issue any asset", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    /* empty url */
    @Test
    public void invalidAssetUrl() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        String localUrl = "";
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, localUrl, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid url");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid url", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    /* description is more than 200 character */
    @Test
    public void invalidAssetDescription() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        String localDescription = "abchefghijklmnopqrstuvwxyzabchefghijklmnopqrstuvwxyzabchefghijklmnopqrstuv" + ("wxyzabchefghijklmnopqrstuvwxyzabchefghijklmnopqrstuvwxyzabchefghijklmnopqrstuvwxyzabchefghij" + "klmnopqrstuvwxyzabchefghijklmnopqrstuvwxyzabchefghijklmnopqrstuvwxyz");
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, localDescription, UpdateAssetActuatorTest.URL, 500L, 8000L), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid description");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid description", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    /* new limit is more than 57_600_000_000 */
    @Test
    public void invalidNewLimit() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        long localNewLimit = 57600000001L;
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, localNewLimit, 8000L), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid FreeAssetNetLimit");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid FreeAssetNetLimit", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }

    @Test
    public void invalidNewPublicLimit() {
        createAssertBeforSameTokenNameActive();
        long tokenId = UpdateAssetActuatorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum();
        long localNewPublicLimit = -1L;
        UpdateAssetActuator actuator = new UpdateAssetActuator(getContract(UpdateAssetActuatorTest.OWNER_ADDRESS, UpdateAssetActuatorTest.DESCRIPTION, UpdateAssetActuatorTest.URL, 500L, localNewPublicLimit), UpdateAssetActuatorTest.dbManager);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        try {
            actuator.validate();
            actuator.execute(ret);
            TestCase.fail("Invalid PublicFreeAssetNetLimit");
        } catch (ContractValidateException e) {
            Assert.assertTrue((e instanceof ContractValidateException));
            Assert.assertEquals("Invalid PublicFreeAssetNetLimit", e.getMessage());
        } catch (ContractExeException e) {
            Assert.assertFalse((e instanceof ContractExeException));
        } finally {
            UpdateAssetActuatorTest.dbManager.getAssetIssueV2Store().delete(ByteArray.fromString(String.valueOf(tokenId)));
            UpdateAssetActuatorTest.dbManager.getAssetIssueStore().delete(ByteString.copyFromUtf8(UpdateAssetActuatorTest.NAME).toByteArray());
        }
    }
}

