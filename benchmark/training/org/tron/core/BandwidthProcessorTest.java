package org.tron.core;


import Constant.TEST_CONF;
import Contract.TransferContract;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.BandwidthProcessor;
import org.tron.core.db.Manager;
import org.tron.core.db.TransactionTrace;
import org.tron.core.exception.AccountResourceInsufficientException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.TooBigTransactionResultException;
import org.tron.protos.Contract;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Contract.TransferAssetContract;
import org.tron.protos.Protocol.AccountType;

import static Constant.MAX_RESULT_SIZE_IN_TX;


@Slf4j
public class BandwidthProcessorTest {
    private static Manager dbManager;

    private static final String dbPath = "output_bandwidth_test";

    private static TronApplicationContext context;

    private static final String ASSET_NAME;

    private static final String ASSET_NAME_V2;

    private static final String OWNER_ADDRESS;

    private static final String ASSET_ADDRESS;

    private static final String ASSET_ADDRESS_V2;

    private static final String TO_ADDRESS;

    private static final long TOTAL_SUPPLY = 10000000000000L;

    private static final int TRX_NUM = 2;

    private static final int NUM = 2147483647;

    private static final int VOTE_SCORE = 2;

    private static final String DESCRIPTION = "TRX";

    private static final String URL = "https://tron.network";

    private static long START_TIME;

    private static long END_TIME;

    static {
        Args.setParam(new String[]{ "--output-directory", BandwidthProcessorTest.dbPath }, TEST_CONF);
        BandwidthProcessorTest.context = new TronApplicationContext(DefaultConfig.class);
        ASSET_NAME = "test_token";
        ASSET_NAME_V2 = "2";
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        TO_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        ASSET_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
        ASSET_ADDRESS_V2 = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a7890";
        BandwidthProcessorTest.START_TIME = DateTime.now().minusDays(1).getMillis();
        BandwidthProcessorTest.END_TIME = DateTime.now().getMillis();
    }

    @Test
    public void testFree() throws Exception {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        TransferAssetContract contract = getTransferAssetContract();
        TransactionCapsule trx = new TransactionCapsule(contract);
        AccountCapsule ownerCapsule = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        BandwidthProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        AccountCapsule ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        Assert.assertEquals((122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), ownerCapsuleNew.getFreeNetUsage());
        Assert.assertEquals(508882612L, ownerCapsuleNew.getLatestConsumeFreeTime());// slot

        Assert.assertEquals(1526647838000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals((122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getPublicNetUsage());
        Assert.assertEquals(508882612L, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getPublicNetTime());
        Assert.assertEquals(0L, ret.getFee());
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526691038000L);// + 12h

        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        Assert.assertEquals(((61L + 122) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), ownerCapsuleNew.getFreeNetUsage());
        Assert.assertEquals(508897012L, ownerCapsuleNew.getLatestConsumeFreeTime());// 508882612L + 28800L/2

        Assert.assertEquals(1526691038000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(((61L + 122L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getPublicNetUsage());
        Assert.assertEquals(508897012L, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getPublicNetTime());
        Assert.assertEquals(0L, ret.getFee());
    }

    @Test
    public void testConsumeAssetAccount() throws Exception {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalNetWeight(10000000L);// only assetAccount has frozen balance

        TransferAssetContract contract = getTransferAssetContract();
        TransactionCapsule trx = new TransactionCapsule(contract);
        AccountCapsule assetCapsule = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS));
        assetCapsule.setFrozen(10000000L, 0L);
        BandwidthProcessorTest.dbManager.getAccountStore().put(assetCapsule.getAddress().toByteArray(), assetCapsule);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        AccountCapsule ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        AccountCapsule assetCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS));
        Assert.assertEquals(508882612L, assetCapsuleNew.getLatestConsumeTime());
        Assert.assertEquals(1526647838000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(508882612L, ownerCapsuleNew.getLatestAssetOperationTime(BandwidthProcessorTest.ASSET_NAME));
        Assert.assertEquals((122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), ownerCapsuleNew.getFreeAssetNetUsage(BandwidthProcessorTest.ASSET_NAME));
        Assert.assertEquals((122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), ownerCapsuleNew.getFreeAssetNetUsageV2("1"));
        Assert.assertEquals((122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), assetCapsuleNew.getNetUsage());
        Assert.assertEquals(0L, ret.getFee());
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526691038000L);// + 12h

        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        assetCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS));
        Assert.assertEquals(508897012L, assetCapsuleNew.getLatestConsumeTime());
        Assert.assertEquals(1526691038000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(508897012L, ownerCapsuleNew.getLatestAssetOperationTime(BandwidthProcessorTest.ASSET_NAME));
        Assert.assertEquals(((61L + 122L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), ownerCapsuleNew.getFreeAssetNetUsage(BandwidthProcessorTest.ASSET_NAME));
        Assert.assertEquals(((61L + 122L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), ownerCapsuleNew.getFreeAssetNetUsageV2("1"));
        Assert.assertEquals(((61L + 122L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), assetCapsuleNew.getNetUsage());
        Assert.assertEquals(0L, ret.getFee());
    }

    @Test
    public void testConsumeAssetAccountV2() throws Exception {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalNetWeight(10000000L);// only assetAccount has frozen balance

        TransferAssetContract contract = getTransferAssetV2Contract();
        TransactionCapsule trx = new TransactionCapsule(contract);
        // issuer freeze balance for bandwidth
        AccountCapsule issuerCapsuleV2 = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS_V2));
        issuerCapsuleV2.setFrozen(10000000L, 0L);
        BandwidthProcessorTest.dbManager.getAccountStore().put(issuerCapsuleV2.getAddress().toByteArray(), issuerCapsuleV2);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        AccountCapsule ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        AccountCapsule issuerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS_V2));
        Assert.assertEquals(508882612L, issuerCapsuleNew.getLatestConsumeTime());
        Assert.assertEquals(1526647838000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(508882612L, ownerCapsuleNew.getLatestAssetOperationTimeV2(BandwidthProcessorTest.ASSET_NAME_V2));
        Assert.assertEquals((113L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), issuerCapsuleNew.getNetUsage());
        Assert.assertEquals((113L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), ownerCapsuleNew.getFreeAssetNetUsageV2(BandwidthProcessorTest.ASSET_NAME_V2));
        Assert.assertEquals(508882612L, ownerCapsuleNew.getLatestAssetOperationTimeV2(BandwidthProcessorTest.ASSET_NAME_V2));
        Assert.assertEquals(0L, ret.getFee());
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526691038000L);// + 12h

        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        issuerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS_V2));
        Assert.assertEquals(508897012L, issuerCapsuleNew.getLatestConsumeTime());
        Assert.assertEquals(1526691038000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(508897012L, ownerCapsuleNew.getLatestAssetOperationTimeV2(BandwidthProcessorTest.ASSET_NAME_V2));
        Assert.assertEquals(((56L + 113L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), ownerCapsuleNew.getFreeAssetNetUsageV2(BandwidthProcessorTest.ASSET_NAME_V2));
        Assert.assertEquals(((56L + 113L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), issuerCapsuleNew.getNetUsage());
        Assert.assertEquals(0L, ret.getFee());
    }

    @Test
    public void testConsumeOwner() throws Exception {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalNetWeight(10000000L);// only owner has frozen balance

        TransferAssetContract contract = getTransferAssetContract();
        TransactionCapsule trx = new TransactionCapsule(contract);
        AccountCapsule ownerCapsule = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        ownerCapsule.setFrozen(10000000L, 0L);
        BandwidthProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        AccountCapsule ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        AccountCapsule assetCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.ASSET_ADDRESS));
        Assert.assertEquals((122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)), ownerCapsuleNew.getNetUsage());
        Assert.assertEquals(1526647838000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(508882612L, ownerCapsuleNew.getLatestConsumeTime());
        Assert.assertEquals(0L, ret.getFee());
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526691038000L);// + 12h

        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        Assert.assertEquals(((61L + 122L) + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? ((MAX_RESULT_SIZE_IN_TX) / 2) * 3 : 0)), ownerCapsuleNew.getNetUsage());
        Assert.assertEquals(1526691038000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(508897012L, ownerCapsuleNew.getLatestConsumeTime());
        Assert.assertEquals(0L, ret.getFee());
    }

    @Test
    public void testUsingFee() throws Exception {
        Args.getInstance().getGenesisBlock().getAssets().forEach(( account) -> {
            AccountCapsule capsule = new AccountCapsule(ByteString.copyFromUtf8(""), ByteString.copyFrom(account.getAddress()), AccountType.AssetIssue, 100L);
            BandwidthProcessorTest.dbManager.getAccountStore().put(account.getAddress(), capsule);
        });
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveFreeNetLimit(0L);
        TransferAssetContract contract = getTransferAssetContract();
        TransactionCapsule trx = new TransactionCapsule(contract);
        AccountCapsule ownerCapsule = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        ownerCapsule.setBalance(10000000L);
        BandwidthProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        TransactionResultCapsule ret = new TransactionResultCapsule();
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        AccountCapsule ownerCapsuleNew = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
        long transactionFee = (122L + (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().supportVM() ? MAX_RESULT_SIZE_IN_TX : 0)) * (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getTransactionFee());
        Assert.assertEquals(transactionFee, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getTotalTransactionCost());
        Assert.assertEquals((10000000L - transactionFee), ownerCapsuleNew.getBalance());
        Assert.assertEquals(transactionFee, trace.getReceipt().getNetFee());
        BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS));
        BandwidthProcessorTest.dbManager.consumeBandwidth(trx, trace);
        // long createAccountFee = dbManager.getDynamicPropertiesStore().getCreateAccountFee();
        // ownerCapsuleNew = dbManager.getAccountStore()
        // .get(ByteArray.fromHexString(OWNER_ADDRESS));
        // Assert.assertEquals(dbManager.getDynamicPropertiesStore().getCreateAccountFee(),
        // dbManager.getDynamicPropertiesStore().getTotalCreateAccountCost());
        // Assert.assertEquals(
        // 10_000_000L - transactionFee - createAccountFee, ownerCapsuleNew.getBalance());
        // Assert.assertEquals(101220L, ret.getFee());
    }

    /**
     * sameTokenName close, consume success
     * assetIssueCapsule.getOwnerAddress() != fromAccount.getAddress())
     * contract.getType() = TransferAssetContract
     */
    @Test
    public void sameTokenNameCloseConsumeSuccess() {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalNetWeight(10000000L);
        long id = (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum()) + 1;
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTokenIdNum(id);
        AssetIssueContract assetIssueContract = AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS))).setName(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.ASSET_NAME))).setId(Long.toString(id)).setTotalSupply(BandwidthProcessorTest.TOTAL_SUPPLY).setTrxNum(BandwidthProcessorTest.TRX_NUM).setNum(BandwidthProcessorTest.NUM).setStartTime(BandwidthProcessorTest.START_TIME).setEndTime(BandwidthProcessorTest.END_TIME).setVoteScore(BandwidthProcessorTest.VOTE_SCORE).setDescription(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.DESCRIPTION))).setUrl(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.URL))).setPublicFreeAssetNetLimit(2000).setFreeAssetNetLimit(2000).build();
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(assetIssueContract);
        // V1
        BandwidthProcessorTest.dbManager.getAssetIssueStore().put(assetIssueCapsule.createDbKey(), assetIssueCapsule);
        // V2
        BandwidthProcessorTest.dbManager.getAssetIssueV2Store().put(assetIssueCapsule.createDbV2Key(), assetIssueCapsule);
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS)), AccountType.Normal, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
        ownerCapsule.setBalance(10000000L);
        long expireTime = (DateTime.now().getMillis()) + (6 * 86400000);
        ownerCapsule.setFrozenForBandwidth(2000000L, expireTime);
        BandwidthProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        AccountCapsule toAddressCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS)), AccountType.Normal, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
        toAddressCapsule.setBalance(10000000L);
        long expireTime2 = (DateTime.now().getMillis()) + (6 * 86400000);
        toAddressCapsule.setFrozenForBandwidth(2000000L, expireTime2);
        BandwidthProcessorTest.dbManager.getAccountStore().put(toAddressCapsule.getAddress().toByteArray(), toAddressCapsule);
        TransferAssetContract contract = Contract.TransferAssetContract.newBuilder().setAssetName(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.ASSET_NAME))).setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS))).setToAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS))).setAmount(100L).build();
        TransactionCapsule trx = new TransactionCapsule(contract);
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        long byteSize = (trx.getInstance().toBuilder().clearRet().build().getSerializedSize()) + (MAX_RESULT_SIZE_IN_TX);
        BandwidthProcessor processor = new BandwidthProcessor(BandwidthProcessorTest.dbManager);
        try {
            processor.consume(trx, trace);
            Assert.assertEquals(trace.getReceipt().getNetFee(), 0);
            Assert.assertEquals(trace.getReceipt().getNetUsage(), byteSize);
            // V1
            AssetIssueCapsule assetIssueCapsuleV1 = BandwidthProcessorTest.dbManager.getAssetIssueStore().get(assetIssueCapsule.createDbKey());
            Assert.assertNotNull(assetIssueCapsuleV1);
            Assert.assertEquals(assetIssueCapsuleV1.getPublicFreeAssetNetUsage(), byteSize);
            AccountCapsule fromAccount = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
            Assert.assertNotNull(fromAccount);
            Assert.assertEquals(fromAccount.getFreeAssetNetUsage(BandwidthProcessorTest.ASSET_NAME), byteSize);
            Assert.assertEquals(fromAccount.getFreeAssetNetUsageV2(String.valueOf(id)), byteSize);
            AccountCapsule ownerAccount = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS));
            Assert.assertNotNull(ownerAccount);
            Assert.assertEquals(ownerAccount.getNetUsage(), byteSize);
            // V2
            AssetIssueCapsule assetIssueCapsuleV2 = BandwidthProcessorTest.dbManager.getAssetIssueV2Store().get(assetIssueCapsule.createDbV2Key());
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(assetIssueCapsuleV2.getPublicFreeAssetNetUsage(), byteSize);
            Assert.assertEquals(fromAccount.getFreeAssetNetUsage(BandwidthProcessorTest.ASSET_NAME), byteSize);
            Assert.assertEquals(fromAccount.getFreeAssetNetUsageV2(String.valueOf(id)), byteSize);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (TooBigTransactionResultException e) {
            Assert.assertFalse((e instanceof TooBigTransactionResultException));
        } catch (AccountResourceInsufficientException e) {
            Assert.assertFalse((e instanceof AccountResourceInsufficientException));
        } finally {
            BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
            BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS));
            BandwidthProcessorTest.dbManager.getAssetIssueStore().delete(assetIssueCapsule.createDbKey());
            BandwidthProcessorTest.dbManager.getAssetIssueV2Store().delete(assetIssueCapsule.createDbV2Key());
        }
    }

    /**
     * sameTokenName open, consume success
     * assetIssueCapsule.getOwnerAddress() != fromAccount.getAddress())
     * contract.getType() = TransferAssetContract
     */
    @Test
    public void sameTokenNameOpenConsumeSuccess() {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(1);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalNetWeight(10000000L);
        long id = (BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getTokenIdNum()) + 1;
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTokenIdNum(id);
        AssetIssueContract assetIssueContract = AssetIssueContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS))).setName(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.ASSET_NAME))).setId(Long.toString(id)).setTotalSupply(BandwidthProcessorTest.TOTAL_SUPPLY).setTrxNum(BandwidthProcessorTest.TRX_NUM).setNum(BandwidthProcessorTest.NUM).setStartTime(BandwidthProcessorTest.START_TIME).setEndTime(BandwidthProcessorTest.END_TIME).setVoteScore(BandwidthProcessorTest.VOTE_SCORE).setDescription(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.DESCRIPTION))).setUrl(ByteString.copyFrom(ByteArray.fromString(BandwidthProcessorTest.URL))).setPublicFreeAssetNetLimit(2000).setFreeAssetNetLimit(2000).build();
        AssetIssueCapsule assetIssueCapsule = new AssetIssueCapsule(assetIssueContract);
        // V2
        BandwidthProcessorTest.dbManager.getAssetIssueV2Store().put(assetIssueCapsule.createDbV2Key(), assetIssueCapsule);
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS)), AccountType.Normal, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
        ownerCapsule.setBalance(10000000L);
        long expireTime = (DateTime.now().getMillis()) + (6 * 86400000);
        ownerCapsule.setFrozenForBandwidth(2000000L, expireTime);
        BandwidthProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        AccountCapsule toAddressCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS)), AccountType.Normal, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
        toAddressCapsule.setBalance(10000000L);
        long expireTime2 = (DateTime.now().getMillis()) + (6 * 86400000);
        toAddressCapsule.setFrozenForBandwidth(2000000L, expireTime2);
        BandwidthProcessorTest.dbManager.getAccountStore().put(toAddressCapsule.getAddress().toByteArray(), toAddressCapsule);
        TransferAssetContract contract = Contract.TransferAssetContract.newBuilder().setAssetName(ByteString.copyFrom(ByteArray.fromString(String.valueOf(id)))).setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS))).setToAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS))).setAmount(100L).build();
        TransactionCapsule trx = new TransactionCapsule(contract);
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        long byteSize = (trx.getInstance().toBuilder().clearRet().build().getSerializedSize()) + (MAX_RESULT_SIZE_IN_TX);
        BandwidthProcessor processor = new BandwidthProcessor(BandwidthProcessorTest.dbManager);
        try {
            processor.consume(trx, trace);
            Assert.assertEquals(trace.getReceipt().getNetFee(), 0);
            Assert.assertEquals(trace.getReceipt().getNetUsage(), byteSize);
            AccountCapsule ownerAccount = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS));
            Assert.assertNotNull(ownerAccount);
            Assert.assertEquals(ownerAccount.getNetUsage(), byteSize);
            // V2
            AssetIssueCapsule assetIssueCapsuleV2 = BandwidthProcessorTest.dbManager.getAssetIssueV2Store().get(assetIssueCapsule.createDbV2Key());
            Assert.assertNotNull(assetIssueCapsuleV2);
            Assert.assertEquals(assetIssueCapsuleV2.getPublicFreeAssetNetUsage(), byteSize);
            AccountCapsule fromAccount = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
            Assert.assertNotNull(fromAccount);
            Assert.assertEquals(fromAccount.getFreeAssetNetUsageV2(String.valueOf(id)), byteSize);
            Assert.assertEquals(fromAccount.getFreeAssetNetUsageV2(String.valueOf(id)), byteSize);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (TooBigTransactionResultException e) {
            Assert.assertFalse((e instanceof TooBigTransactionResultException));
        } catch (AccountResourceInsufficientException e) {
            Assert.assertFalse((e instanceof AccountResourceInsufficientException));
        } finally {
            BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
            BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS));
            BandwidthProcessorTest.dbManager.getAssetIssueStore().delete(assetIssueCapsule.createDbKey());
            BandwidthProcessorTest.dbManager.getAssetIssueV2Store().delete(assetIssueCapsule.createDbV2Key());
        }
    }

    /**
     * sameTokenName close, consume success
     * contract.getType() = TransferContract
     * toAddressAccount isn't exist.
     */
    @Test
    public void sameTokenNameCloseTransferToAccountNotExist() {
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveAllowSameTokenName(0);
        BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalNetWeight(10000000L);
        AccountCapsule ownerCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS)), AccountType.Normal, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
        ownerCapsule.setBalance(10000000L);
        long expireTime = (DateTime.now().getMillis()) + (6 * 86400000);
        ownerCapsule.setFrozenForBandwidth(2000000L, expireTime);
        BandwidthProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        AccountCapsule toAddressCapsule = new AccountCapsule(ByteString.copyFromUtf8("owner"), ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS)), AccountType.Normal, BandwidthProcessorTest.dbManager.getDynamicPropertiesStore().getAssetIssueFee());
        toAddressCapsule.setBalance(10000000L);
        long expireTime2 = (DateTime.now().getMillis()) + (6 * 86400000);
        toAddressCapsule.setFrozenForBandwidth(2000000L, expireTime2);
        BandwidthProcessorTest.dbManager.getAccountStore().delete(toAddressCapsule.getAddress().toByteArray());
        Contract.TransferContract contract = TransferContract.newBuilder().setOwnerAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS))).setToAddress(ByteString.copyFrom(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS))).setAmount(100L).build();
        TransactionCapsule trx = new TransactionCapsule(contract, BandwidthProcessorTest.dbManager.getAccountStore());
        TransactionTrace trace = new TransactionTrace(trx, BandwidthProcessorTest.dbManager);
        long byteSize = (trx.getInstance().toBuilder().clearRet().build().getSerializedSize()) + (MAX_RESULT_SIZE_IN_TX);
        BandwidthProcessor processor = new BandwidthProcessor(BandwidthProcessorTest.dbManager);
        try {
            processor.consume(trx, trace);
            Assert.assertEquals(trace.getReceipt().getNetFee(), 0);
            Assert.assertEquals(trace.getReceipt().getNetUsage(), byteSize);
            AccountCapsule fromAccount = BandwidthProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
            Assert.assertNotNull(fromAccount);
            Assert.assertEquals(fromAccount.getNetUsage(), byteSize);
        } catch (ContractValidateException e) {
            Assert.assertFalse((e instanceof ContractValidateException));
        } catch (TooBigTransactionResultException e) {
            Assert.assertFalse((e instanceof TooBigTransactionResultException));
        } catch (AccountResourceInsufficientException e) {
            Assert.assertFalse((e instanceof AccountResourceInsufficientException));
        } finally {
            BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.OWNER_ADDRESS));
            BandwidthProcessorTest.dbManager.getAccountStore().delete(ByteArray.fromHexString(BandwidthProcessorTest.TO_ADDRESS));
        }
    }
}

