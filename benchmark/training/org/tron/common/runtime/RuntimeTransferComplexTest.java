package org.tron.common.runtime;


import Constant.TEST_CONF;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.storage.DepositImpl;
import org.tron.core.Wallet;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;
import org.tron.protos.Protocol.Transaction;
import stest.tron.wallet.common.client.utils.DataWord;


@Slf4j
public class RuntimeTransferComplexTest {
    private static Runtime runtime;

    private static Manager dbManager;

    private static TronApplicationContext context;

    private static Application appT;

    private static DepositImpl deposit;

    private static final String dbPath = "output_RuntimeTransferComplexTest";

    private static final String OWNER_ADDRESS;

    private static final String TRANSFER_TO;

    static {
        Args.setParam(new String[]{ "--output-directory", RuntimeTransferComplexTest.dbPath, "--debug" }, TEST_CONF);
        RuntimeTransferComplexTest.context = new TronApplicationContext(DefaultConfig.class);
        RuntimeTransferComplexTest.appT = ApplicationFactory.create(RuntimeTransferComplexTest.context);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        TRANSFER_TO = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
    }

    /**
     * Test constructor Transfer pragma solidity ^0.4.16; contract transferWhenDeploy { constructor ()
     * payable{} }
     */
    @Test
    public void TransferTrxToContractAccountWhenDeployAContract() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        String contractName = "TransferWhenDeployContract";
        byte[] address = Hex.decode(RuntimeTransferComplexTest.OWNER_ADDRESS);
        String ABI = "[{\"inputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"constructor\"}]";
        String code = "608060405260358060116000396000f3006080604052600080fd00a165627a7a72305820d3b0de5bdc00ebe85619d50b72b29d30bd00dd233e8849402671979de0e9e73b0029";
        long value = 100;
        long fee = 100000000;
        long consumeUserResourcePercent = 0;
        Transaction trx = TVMTestUtils.generateDeploySmartContractAndGetTransaction(contractName, address, ABI, code, value, fee, consumeUserResourcePercent, null);
        byte[] contractAddress = Wallet.generateContractAddress(trx);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(trx, RuntimeTransferComplexTest.deposit, null);
        Assert.assertNull(getRuntimeError());
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(contractAddress).getBalance(), 100);
        recoverDeposit();
    }

    /**
     * Test constructor Transfer pragma solidity ^0.4.16; contract transferWhenDeploy { constructor ()
     * {} }
     */
    @Test
    public void TransferTrxToContractAccountFailIfNotPayable() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        String contractName = "TransferWhenDeployContract";
        byte[] address = Hex.decode(RuntimeTransferComplexTest.OWNER_ADDRESS);
        String ABI = "[{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
        String code = "6080604052348015600f57600080fd5b50603580601d6000396000f3006080604052600080fd00a165627a7a72305820f" + "5dc348e1c7dc90f9996a05c69dc9d060b6d356a1ed570ce3cd89570dc4ce6440029";
        long value = 100;
        long fee = 100000000;
        long consumeUserResourcePercent = 0;
        Transaction trx = TVMTestUtils.generateDeploySmartContractAndGetTransaction(contractName, address, ABI, code, value, fee, consumeUserResourcePercent, null);
        byte[] contractAddress = Wallet.generateContractAddress(trx);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(trx, RuntimeTransferComplexTest.deposit, null);
        Assert.assertNotNull(getRuntimeError().contains("REVERT"));
        Assert.assertNull(RuntimeTransferComplexTest.dbManager.getAccountStore().get(contractAddress));
        recoverDeposit();
    }

    /**
     * pragma solidity ^0.4.16; contract transferWhenTriggerContract { constructor () {} function
     * transferTo(address toAddress) public payable{ toAddress.transfer(5); } }
     */
    @Test
    public void TransferTrxToContractAccountWhenTriggerAContract() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        String contractName = "TransferWhenDeployContract";
        byte[] address = Hex.decode(RuntimeTransferComplexTest.OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"toAddress\",\"type\":\"address\"}]," + ("\"name\":\"transferTo\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"}," + "{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]");
        String code = "608060405234801561001057600080fd5b5060ee8061001f6000396000f300608060405260043610603f576000357c01" + ((("00000000000000000000000000000000000000000000000000000000900463ffffffff168063a03fa7e3146044575b600080fd5b607" + "6600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506078565b005b8073") + "ffffffffffffffffffffffffffffffffffffffff166108fc60059081150290604051600060405180830381858888f1935050505015801560be573d") + "6000803e3d6000fd5b50505600a165627a7a723058209b248b5be19bae77660cdc92b0a141f279dc4746d858d9d7d270a22d014eb97a0029");
        long value = 0;
        long feeLimit = 100000000;
        long consumeUserResourcePercent = 0;
        long transferToInitBalance = RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance();
        byte[] contractAddress = TVMTestUtils.deployContractWholeProcessReturnContractAddress(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, null, RuntimeTransferComplexTest.deposit, null);
        String selectorStr = "transferTo(address)";
        String params = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData = TVMTestUtils.parseABI(selectorStr, params);
        long triggerCallValue = 100;
        Transaction transaction = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(address, contractAddress, triggerData, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction, RuntimeTransferComplexTest.deposit, null);
        Assert.assertNull(getRuntimeError());
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(contractAddress).getBalance(), (100 - 5));
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), (transferToInitBalance + 5));
        recoverDeposit();
    }

    /**
     * contract callerContract { calledContract CALLED_INSTANCE; constructor(address _addr) public
     * payable { CALLED_INSTANCE = calledContract(_addr); } // expect calledContract -5, toAddress +5
     * function testCallTransferToInCalledContract(address toAddress){ CALLED_INSTANCE.transferTo(toAddress);
     * }
     *
     * // expect calledContract -0, toAddress +0 function testRevertForCall(address toAddress){
     * CALLED_INSTANCE.transferTo(toAddress); revert(); } function testExceptionForCall(address
     * toAddress){ CALLED_INSTANCE.transferTo(toAddress); assert(1==2); } // expect c +100 -5,
     * toAddress +0 function testTransferToInCreatedContract(address toAddress) payable
     * returns(address){ createdContract c = (new createdContract).value(100)();
     * c.transferTo(toAddress); return address(c); }
     *
     * // expect c +100 -5, toAddress not exist function testRevertForCreate(address toAddress)
     * payable returns(address){ createdContract c = (new createdContract).value(100)();
     * c.transferTo(toAddress); revert(); return address(c); }
     *
     * // expect c +100 -5, toAddress not exist function testExceptionForCreate(address toAddress)
     * payable returns(address){ createdContract c = (new createdContract).value(100)();
     * c.transferTo(toAddress); assert(1==2); return address(c); }
     *
     * function getBalance() public view returns(uint256){ return this.balance; } }
     *
     * contract calledContract { constructor() payable {} function transferTo(address toAddress)
     * payable{ toAddress.transfer(5); }
     *
     * function getBalance() public view returns(uint256){ return this.balance; }
     *
     * }
     *
     * contract createdContract { constructor() payable {} function transferTo(address toAddress){
     * toAddress.transfer(5); }
     *
     * function getBalance() public view returns(uint256){ return this.balance; } }
     */
    @Test
    public void TransferCallValueTestWhenUsingCallAndCreate() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] msgSenderAddress = Hex.decode(RuntimeTransferComplexTest.OWNER_ADDRESS);
        byte[] calledAddress = deployCalledContract();
        byte[] callerAddress = deployCallerContract(calledAddress);
        long triggerCallValue = 0;
        long feeLimit = 100000000;
        // ==================================0. check initial status ================================================
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(calledAddress).getBalance(), 1000);
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), 1000);
        long transferToInitBalance = RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance();
        // ==================================1. testCallTransferToInCalledContract====================================
        String selectorStr1 = "testCallTransferToInCalledContract(address)";
        String params1 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData1 = TVMTestUtils.parseABI(selectorStr1, params1);
        Transaction transaction1 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(msgSenderAddress, callerAddress, triggerData1, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction1, RuntimeTransferComplexTest.deposit, null);
        Assert.assertNull(getRuntimeError());
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), 1000);// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(calledAddress).getBalance(), (1000 - 5));// Transfer 5 sun to TransferTo

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), (transferToInitBalance + 5));// get 5 sun from calledAddress

        recoverDeposit();
        // ==================================2. testRevertForCall =================================================
        String selectorStr2 = "testRevertForCall(address)";
        String params2 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData2 = TVMTestUtils.parseABI(selectorStr2, params2);
        Transaction transaction2 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(msgSenderAddress, callerAddress, triggerData2, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction2, RuntimeTransferComplexTest.deposit, null);
        Assert.assertTrue(getRuntimeError().contains("REVERT"));
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), 1000);// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(calledAddress).getBalance(), 995);// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), (transferToInitBalance + 5));// Not changed

        recoverDeposit();
        // ==================================3. testExceptionForCall =================================================
        String selectorStr3 = "testExceptionForCall(address)";
        String params3 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData3 = TVMTestUtils.parseABI(selectorStr3, params3);
        Transaction transaction3 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(msgSenderAddress, callerAddress, triggerData3, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction3, RuntimeTransferComplexTest.deposit, null);
        Assert.assertTrue(getRuntimeError().contains("Invalid operation code: opCode[fe];"));
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), 1000);// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(calledAddress).getBalance(), 995);// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), (transferToInitBalance + 5));// Not changed

        recoverDeposit();
        // ==================================4. testTransferToInCreatedContract =================================================
        String selectorStr4 = "testTransferToInCreatedContract(address)";
        String params4 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData4 = TVMTestUtils.parseABI(selectorStr4, params4);
        Transaction transaction4 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(msgSenderAddress, callerAddress, triggerData4, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction4, RuntimeTransferComplexTest.deposit, null);
        byte[] createdAddress = convertToTronAddress(new DataWord(getResult().getHReturn()).getLast20Bytes());
        Assert.assertNull(getRuntimeError());
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), (1000 - 100));// Transfer to createdAddress

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(createdAddress).getBalance(), (100 - 5));// Transfer to transfer_to

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), ((transferToInitBalance + 5) + 5));// get 5 from created Address

        recoverDeposit();
        // ==================================5. testRevertForCreate =================================================
        String selectorStr5 = "testRevertForCreate(address)";
        String params5 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData5 = TVMTestUtils.parseABI(selectorStr5, params5);
        Transaction transaction5 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(msgSenderAddress, callerAddress, triggerData5, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction5, RuntimeTransferComplexTest.deposit, null);
        byte[] createdAddress2 = convertToTronAddress(new DataWord(getResult().getHReturn()).getLast20Bytes());
        Assert.assertTrue(Hex.toHexString(new DataWord(createdAddress2).getLast20Bytes()).equalsIgnoreCase("0000000000000000000000000000000000000000"));
        Assert.assertTrue(getRuntimeError().contains("REVERT"));
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), (1000 - 100));// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(createdAddress).getBalance(), (100 - 5));// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), ((transferToInitBalance + 5) + 5));// Not changed

        recoverDeposit();
        // ==================================5. testExceptionForCreate =================================================
        String selectorStr6 = "testExceptionForCreate(address)";
        String params6 = "000000000000000000000000548794500882809695a8a687866e76d4271a1abc";// TRANSFER_TO

        byte[] triggerData6 = TVMTestUtils.parseABI(selectorStr6, params6);
        Transaction transaction6 = TVMTestUtils.generateTriggerSmartContractAndGetTransaction(msgSenderAddress, callerAddress, triggerData6, triggerCallValue, feeLimit);
        RuntimeTransferComplexTest.runtime = TVMTestUtils.processTransactionAndReturnRuntime(transaction6, RuntimeTransferComplexTest.deposit, null);
        byte[] createdAddress3 = convertToTronAddress(new DataWord(getResult().getHReturn()).getLast20Bytes());
        Assert.assertTrue(Hex.toHexString(new DataWord(createdAddress2).getLast20Bytes()).equalsIgnoreCase("0000000000000000000000000000000000000000"));
        Assert.assertTrue(getRuntimeError().contains("Invalid operation code: opCode[fe];"));
        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(callerAddress).getBalance(), (1000 - 100));// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(createdAddress).getBalance(), (100 - 5));// Not changed

        Assert.assertEquals(RuntimeTransferComplexTest.dbManager.getAccountStore().get(Hex.decode(RuntimeTransferComplexTest.TRANSFER_TO)).getBalance(), ((transferToInitBalance + 5) + 5));// Not changed

        recoverDeposit();
    }
}

