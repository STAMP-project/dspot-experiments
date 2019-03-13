package org.tron.common.runtime;


import Constant.TEST_CONF;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.vm.DataWord;
import org.tron.common.runtime.vm.program.InternalTransaction;
import org.tron.common.storage.DepositImpl;
import org.tron.core.Wallet;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.db.TransactionTrace;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;
import org.tron.protos.Protocol.Transaction;


@Slf4j
public class ProgramResultTest {
    private static Runtime runtime;

    private static Manager dbManager;

    private static TronApplicationContext context;

    private static Application appT;

    private static DepositImpl deposit;

    private static final String dbPath = "output_InternalTransactionComplexTest";

    private static final String OWNER_ADDRESS;

    private static final String TRANSFER_TO;

    static {
        Args.setParam(new String[]{ "--output-directory", ProgramResultTest.dbPath, "--debug", "--support-constant" }, TEST_CONF);
        ProgramResultTest.context = new TronApplicationContext(DefaultConfig.class);
        ProgramResultTest.appT = ApplicationFactory.create(ProgramResultTest.context);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        TRANSFER_TO = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
    }

    /**
     * pragma solidity ^0.4.8;
     * contract B{
     *     address public calledAddress;
     *     constructor (address d) payable{calledAddress = d;}
     *     function setB() payable returns(address,address){
     *         calledContract c1 = new calledContract();
     *         calledContract c2 = new calledContract();
     *         calledAddress.call(bytes4(keccak256("getZero()")));
     *         return (address(c1),address(c2));
     *     }
     * }
     * contract A {
     *     address public calledAddress;
     *     constructor(address d) payable{
     *         calledAddress = d;
     *     }
     *
     *     address public b1;
     *     address public b2;
     *     address public b3;
     *     address public b4;
     *     address public b5;
     *     address public b6;
     *     address public b7;
     *     address public b8;
     *
     *     function create(){
     *         B b= new B(calledAddress);
     *         B bb = new B(calledAddress);
     *         b1 = address(b);
     *         b2 = address(bb);
     *         (b3,b4)=b.setB();
     *         (b5,b6)=bb.setB();
     *         (b7,b8)=bb.setB();
     *         calledAddress.call(bytes4(keccak256("getZero()")));
     *     }
     * }
     *
     * contract calledContract {
     *     function getZero() returns(uint256){
     *         return 0;
     *     }
     * }
     */
    @Test
    public void uniqueInternalTransactionHashTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] calledContractAddress = deployCalledContractAndGetItsAddress();
        byte[] contractAAddress = deployContractAAndGetItsAddress(calledContractAddress);
        /* =================================== CALL create() =================================== */
        byte[] triggerData1 = TVMTestUtils.parseABI("create()", "");
        ProgramResultTest.runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(ProgramResultTest.OWNER_ADDRESS), contractAAddress, triggerData1, 0, 100000000, ProgramResultTest.deposit, null);
        List<InternalTransaction> internalTransactionsList = getResult().getInternalTransactions();
        // 15 internalTransactions in total
        Assert.assertEquals(internalTransactionsList.size(), 15);
        List<String> hashList = new ArrayList<>();
        internalTransactionsList.forEach(( internalTransaction) -> hashList.add(Hex.toHexString(internalTransaction.getHash())));
        // No dup
        List<String> dupHash = hashList.stream().collect(Collectors.toMap(( e) -> e, ( e) -> 1, ( a, b) -> a + b)).entrySet().stream().filter(( entry) -> (entry.getValue()) > 1).map(( entry) -> entry.getKey()).collect(Collectors.toList());
        Assert.assertEquals(dupHash.size(), 0);
    }

    /**
     * pragma solidity ^0.4.24;
     *
     * contract A{
     *     uint256 public num = 0;
     *     constructor() public payable{}
     *     function transfer(address c, bool isRevert)  payable public returns(address){
     *         B b = (new B).value(10)();//1
     *         address(b).transfer(5);//2
     *         b.payC(c, isRevert);//3
     *         // b.payC(c,isRevert);
     *         return address(b);
     *     }
     *     function getBalance() returns(uint256){
     *         return this.balance;
     *     }
     * }
     * contract B{
     *     uint256 public num = 0;
     *     function f() payable returns(bool) {
     *         return true;
     *     }
     *     constructor() public payable {}
     *     function payC(address c, bool isRevert) public{
     *         c.transfer(1);//4
     *         if (isRevert) {
     *             revert();
     *         }
     *     }
     *     function getBalance() returns(uint256){
     *         return this.balance;
     *     }
     *     function () payable{}
     * }
     *
     * contract C{
     *     constructor () public payable{}
     *     function () payable{}
     * }
     *
     * @throws ContractExeException
     * 		
     * @throws ReceiptCheckErrException
     * 		
     * @throws VMIllegalException
     * 		
     * @throws ContractValidateException
     * 		
     */
    @Test
    public void successAndFailResultTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] cContract = deployC();
        byte[] aContract = deployA();
        // false
        String params = (Hex.toHexString(getData())) + "0000000000000000000000000000000000000000000000000000000000000000";
        // ======================================= Test Success =======================================
        byte[] triggerData1 = TVMTestUtils.parseABI("transfer(address,bool)", params);
        Transaction trx1 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(Hex.decode(ProgramResultTest.OWNER_ADDRESS), aContract, triggerData1, 0, 100000000);
        TransactionTrace traceSuccess = TVMTestUtils.processTransactionAndReturnTrace(trx1, ProgramResultTest.deposit, null);
        ProgramResultTest.runtime = traceSuccess.getRuntime();
        byte[] bContract = getResult().getHReturn();
        List<InternalTransaction> internalTransactionsList = getResult().getInternalTransactions();
        Assert.assertEquals(internalTransactionsList.get(0).getValue(), 10);
        Assert.assertEquals(internalTransactionsList.get(0).getSender(), aContract);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract).getLast20Bytes());
        Assert.assertEquals(internalTransactionsList.get(0).getNote(), "create");
        Assert.assertEquals(internalTransactionsList.get(0).isRejected(), false);
        Assert.assertEquals(internalTransactionsList.get(1).getValue(), 5);
        Assert.assertEquals(internalTransactionsList.get(1).getSender(), aContract);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract).getLast20Bytes());
        Assert.assertEquals(internalTransactionsList.get(1).getNote(), "call");
        Assert.assertEquals(internalTransactionsList.get(1).isRejected(), false);
        Assert.assertEquals(internalTransactionsList.get(2).getValue(), 0);
        Assert.assertEquals(internalTransactionsList.get(2).getSender(), aContract);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract).getLast20Bytes());
        Assert.assertEquals(internalTransactionsList.get(2).getNote(), "call");
        Assert.assertEquals(internalTransactionsList.get(2).isRejected(), false);
        Assert.assertEquals(internalTransactionsList.get(3).getValue(), 1);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract).getLast20Bytes());
        Assert.assertEquals(internalTransactionsList.get(3).getTransferToAddress(), cContract);
        Assert.assertEquals(internalTransactionsList.get(3).getNote(), "call");
        Assert.assertEquals(internalTransactionsList.get(3).isRejected(), false);
        checkTransactionInfo(traceSuccess, trx1, null, internalTransactionsList);
        // ======================================= Test Fail =======================================
        // set revert == true
        params = (Hex.toHexString(getData())) + "0000000000000000000000000000000000000000000000000000000000000001";
        byte[] triggerData2 = TVMTestUtils.parseABI("transfer(address,bool)", params);
        Transaction trx2 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(Hex.decode(ProgramResultTest.OWNER_ADDRESS), aContract, triggerData2, 0, 100000000);
        TransactionTrace traceFailed = TVMTestUtils.processTransactionAndReturnTrace(trx2, ProgramResultTest.deposit, null);
        ProgramResultTest.runtime = traceFailed.getRuntime();
        byte[] bContract2 = Wallet.generateContractAddress(getTransactionId().getBytes(), 0);
        List<InternalTransaction> internalTransactionsListFail = getResult().getInternalTransactions();
        Assert.assertEquals(internalTransactionsListFail.get(0).getValue(), 10);
        Assert.assertEquals(internalTransactionsListFail.get(0).getSender(), aContract);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract2).getLast20Bytes());
        Assert.assertEquals(internalTransactionsListFail.get(0).getNote(), "create");
        Assert.assertEquals(internalTransactionsListFail.get(0).isRejected(), true);
        Assert.assertEquals(internalTransactionsListFail.get(1).getValue(), 5);
        Assert.assertEquals(internalTransactionsListFail.get(1).getSender(), aContract);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract2).getLast20Bytes());
        Assert.assertEquals(internalTransactionsListFail.get(1).getNote(), "call");
        Assert.assertEquals(internalTransactionsListFail.get(1).isRejected(), true);
        Assert.assertEquals(internalTransactionsListFail.get(2).getValue(), 0);
        Assert.assertEquals(internalTransactionsListFail.get(2).getSender(), aContract);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract2).getLast20Bytes());
        Assert.assertEquals(internalTransactionsListFail.get(2).getNote(), "call");
        Assert.assertEquals(internalTransactionsListFail.get(2).isRejected(), true);
        Assert.assertEquals(internalTransactionsListFail.get(3).getValue(), 1);
        Assert.assertEquals(getLast20Bytes(), new DataWord(bContract2).getLast20Bytes());
        Assert.assertEquals(internalTransactionsListFail.get(3).getTransferToAddress(), cContract);
        Assert.assertEquals(internalTransactionsListFail.get(3).getNote(), "call");
        Assert.assertEquals(internalTransactionsListFail.get(3).isRejected(), true);
        checkTransactionInfo(traceFailed, trx2, null, internalTransactionsListFail);
    }

    /**
     * pragma solidity ^0.4.24;
     *
     * contract A{
     *     constructor () payable public{}
     *     function suicide(address toAddress) public payable{
     *         selfdestruct(toAddress);
     *     }
     *     function () payable public{}
     * }s
     */
    @Test
    public void suicideResultTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] suicideContract = deploySuicide();
        Assert.assertEquals(ProgramResultTest.deposit.getAccount(suicideContract).getBalance(), 1000);
        String params = Hex.toHexString(getData());
        // ======================================= Test Suicide =======================================
        byte[] triggerData1 = TVMTestUtils.parseABI("suicide(address)", params);
        Transaction trx = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(Hex.decode(ProgramResultTest.OWNER_ADDRESS), suicideContract, triggerData1, 0, 100000000);
        TransactionTrace trace = TVMTestUtils.processTransactionAndReturnTrace(trx, ProgramResultTest.deposit, null);
        ProgramResultTest.runtime = trace.getRuntime();
        List<InternalTransaction> internalTransactionsList = getResult().getInternalTransactions();
        Assert.assertEquals(ProgramResultTest.dbManager.getAccountStore().get(Hex.decode(ProgramResultTest.TRANSFER_TO)).getBalance(), 1000);
        Assert.assertEquals(ProgramResultTest.dbManager.getAccountStore().get(suicideContract), null);
        Assert.assertEquals(internalTransactionsList.get(0).getValue(), 1000);
        Assert.assertEquals(getLast20Bytes(), new DataWord(suicideContract).getLast20Bytes());
        Assert.assertEquals(internalTransactionsList.get(0).getTransferToAddress(), Hex.decode(ProgramResultTest.TRANSFER_TO));
        Assert.assertEquals(internalTransactionsList.get(0).getNote(), "suicide");
        Assert.assertEquals(internalTransactionsList.get(0).isRejected(), false);
        checkTransactionInfo(trace, trx, null, internalTransactionsList);
    }
}

