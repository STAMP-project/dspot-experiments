package org.tron.common.runtime.vm;


import AccountType.Normal;
import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.TVMTestUtils;
import org.tron.common.storage.DepositImpl;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;
import org.tron.protos.Protocol.AccountType;
import org.tron.protos.Protocol.Transaction;


@Slf4j
public class TransferTokenTest {
    private static Runtime runtime;

    private static Manager dbManager;

    private static TronApplicationContext context;

    private static Application appT;

    private static DepositImpl deposit;

    private static final String dbPath = "output_TransferTokenTest";

    private static final String OWNER_ADDRESS;

    private static final String TRANSFER_TO;

    private static final long TOTAL_SUPPLY = 1000000000L;

    private static final int TRX_NUM = 10;

    private static final int NUM = 1;

    private static final long START_TIME = 1;

    private static final long END_TIME = 2;

    private static final int VOTE_SCORE = 2;

    private static final String DESCRIPTION = "TRX";

    private static final String URL = "https://tron.network";

    private static AccountCapsule ownerCapsule;

    static {
        Args.setParam(new String[]{ "--output-directory", TransferTokenTest.dbPath, "--debug" }, TEST_CONF);
        TransferTokenTest.context = new TronApplicationContext(DefaultConfig.class);
        TransferTokenTest.appT = ApplicationFactory.create(TransferTokenTest.context);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        TRANSFER_TO = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        TransferTokenTest.dbManager = TransferTokenTest.context.getBean(Manager.class);
        TransferTokenTest.deposit = DepositImpl.createRoot(TransferTokenTest.dbManager);
        TransferTokenTest.deposit.createAccount(Hex.decode(TransferTokenTest.TRANSFER_TO), Normal);
        TransferTokenTest.deposit.addBalance(Hex.decode(TransferTokenTest.TRANSFER_TO), 10);
        TransferTokenTest.deposit.commit();
        TransferTokenTest.ownerCapsule = new AccountCapsule(ByteString.copyFrom(ByteArray.fromHexString(TransferTokenTest.OWNER_ADDRESS)), ByteString.copyFromUtf8("owner"), AccountType.AssetIssue);
        TransferTokenTest.ownerCapsule.setBalance(100010001000L);
    }

    /**
     * pragma solidity ^0.4.24;
     *
     *    contract tokenTest{
     *        constructor() public payable{}
     *        // positive case
     *        function TransferTokenTo(address toAddress, trcToken id,uint256 amount) public payable{
     *            toAddress.transferToken(amount,id);
     *        }
     *        function suicide(address toAddress) payable public{
     *            selfdestruct(toAddress);
     *        }
     *        function get(trcToken trc) public payable returns(uint256){
     *            return address(this).tokenBalance(trc);
     *        }
     *    }
     *
     *  1. deploy
     *  2. trigger and internal transaction
     *  3. suicide (all token)
     */
    @Test
    public void TransferTokenTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        /* 1. Test deploy with tokenValue and tokenId */
        long id = createAsset("testToken1");
        byte[] contractAddress = deployTransferTokenContract(id);
        TransferTokenTest.deposit.commit();
        Assert.assertEquals(100, TransferTokenTest.dbManager.getAccountStore().get(contractAddress).getAssetMapV2().get(String.valueOf(id)).longValue());
        Assert.assertEquals(1000, TransferTokenTest.dbManager.getAccountStore().get(contractAddress).getBalance());
        String selectorStr = "TransferTokenTo(address,trcToken,uint256)";
        String params = ("000000000000000000000000548794500882809695a8a687866e76d4271a1abc" + (Hex.toHexString(new DataWord(id).getData()))) + "0000000000000000000000000000000000000000000000000000000000000009";// TRANSFER_TO, 100001, 9

        byte[] triggerData = TVMTestUtils.parseABI(selectorStr, params);
        /* 2. Test trigger with tokenValue and tokenId, also test internal transaction transferToken function */
        long triggerCallValue = 100;
        long feeLimit = 100000000;
        long tokenValue = 8;
        Transaction transaction = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(Hex.decode(TransferTokenTest.OWNER_ADDRESS), contractAddress, triggerData, triggerCallValue, feeLimit, tokenValue, id);
        TransferTokenTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction, TransferTokenTest.dbManager, null);
        org.testng.Assert.assertNull(getRuntimeError());
        Assert.assertEquals(((100 + tokenValue) - 9), TransferTokenTest.dbManager.getAccountStore().get(contractAddress).getAssetMapV2().get(String.valueOf(id)).longValue());
        Assert.assertEquals(9, TransferTokenTest.dbManager.getAccountStore().get(Hex.decode(TransferTokenTest.TRANSFER_TO)).getAssetMapV2().get(String.valueOf(id)).longValue());
        /* suicide test */
        // create new token: testToken2
        long id2 = createAsset("testToken2");
        // add token balance for last created contract
        AccountCapsule changeAccountCapsule = TransferTokenTest.dbManager.getAccountStore().get(contractAddress);
        changeAccountCapsule.addAssetAmountV2(String.valueOf(id2).getBytes(), 99, TransferTokenTest.dbManager);
        TransferTokenTest.dbManager.getAccountStore().put(contractAddress, changeAccountCapsule);
        String selectorStr2 = "suicide(address)";
        String params2 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData2 = TVMTestUtils.parseABI(selectorStr2, params2);
        Transaction transaction2 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(Hex.decode(TransferTokenTest.OWNER_ADDRESS), contractAddress, triggerData2, triggerCallValue, feeLimit, 0, id);
        TransferTokenTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction2, TransferTokenTest.dbManager, null);
        org.testng.Assert.assertNull(getRuntimeError());
        Assert.assertEquals((((100 + tokenValue) - 9) + 9), TransferTokenTest.dbManager.getAccountStore().get(Hex.decode(TransferTokenTest.TRANSFER_TO)).getAssetMapV2().get(String.valueOf(id)).longValue());
        Assert.assertEquals(99, TransferTokenTest.dbManager.getAccountStore().get(Hex.decode(TransferTokenTest.TRANSFER_TO)).getAssetMapV2().get(String.valueOf(id2)).longValue());
    }

    /**
     * contract tokenPerformanceTest{
     *           uint256 public counter = 0;
     *           constructor() public payable{}
     *           // positive case
     *           function TransferTokenTo(address toAddress, trcToken id,uint256 amount) public payable{
     *                while(true){
     *                   counter++;
     *                   toAddress.transferToken(amount,id);
     *                }
     *           }
     *       }
     */
    @Test
    public void TransferTokenSingleInstructionTimeTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long id = createAsset("testPerformanceToken");
        byte[] contractAddress = deployTransferTokenPerformanceContract(id);
        long triggerCallValue = 100;
        long feeLimit = 1000000000;
        long tokenValue = 0;
        String selectorStr = "trans(address,trcToken,uint256)";
        String params = ("000000000000000000000000548794500882809695a8a687866e76d4271a1abc" + (Hex.toHexString(new DataWord(id).getData()))) + "0000000000000000000000000000000000000000000000000000000000000002";// TRANSFER_TO, 100001, 9

        byte[] triggerData = TVMTestUtils.parseABI(selectorStr, params);
        Transaction transaction = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(Hex.decode(TransferTokenTest.OWNER_ADDRESS), contractAddress, triggerData, triggerCallValue, feeLimit, tokenValue, id);
        long start = (System.nanoTime()) / 1000;
        TransferTokenTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction, TransferTokenTest.dbManager, null);
        long end = (System.nanoTime()) / 1000;
        System.err.println(("running time:" + (end - start)));
        Assert.assertTrue(((end - start) < 500000));
    }
}

