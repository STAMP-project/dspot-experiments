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


@Slf4j
public class InternalTransactionComplexTest {
    private static Runtime runtime;

    private static Manager dbManager;

    private static TronApplicationContext context;

    private static Application appT;

    private static DepositImpl deposit;

    private static final String dbPath = "output_InternalTransactionComplexTest";

    private static final String OWNER_ADDRESS;

    static {
        Args.setParam(new String[]{ "--output-directory", InternalTransactionComplexTest.dbPath, "--debug", "--support-constant" }, TEST_CONF);
        InternalTransactionComplexTest.context = new TronApplicationContext(DefaultConfig.class);
        InternalTransactionComplexTest.appT = ApplicationFactory.create(InternalTransactionComplexTest.context);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
    }

    /**
     * pragma solidity 0.4.24;
     *
     * // this is to test wither the TVM is returning vars from one contract calling another //
     * contract's functions.
     *
     * contract callerContract { // lets set up our instance of the new contract calledContract
     * CALLED_INSTANCE; // lets set the contract instance address in the constructor
     * constructor(address _addr) public { CALLED_INSTANCE = calledContract(_addr); } // lets create a
     * few public vars to store results so we know if we are // getting the callback return struct
     * SomeStruct { bool someBool; uint256 someUint; bytes32 someBytes32; } SomeStruct public
     * testCallbackReturns_; // create the function call to external contract. store return in vars
     * created // above. function makeTheCall() public { // lets call the contract and store returns
     * in to temp vars (bool _bool, uint256 _uint, bytes32 _bytes32) = CALLED_INSTANCE.testReturns();
     * // lets write those temp vars to state testCallbackReturns_.someBool = _bool;
     * testCallbackReturns_.someUint = _uint; testCallbackReturns_.someBytes32 = _bytes32; } }
     *
     * contract calledContract { function testReturns() external pure returns(bool, uint256, bytes32)
     * { return(true, 314159, 0x123456); } }
     */
    @Test
    public void internalTransactionAsInstanceTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] calledContractAddress = deployCalledContractandGetItsAddress();
        byte[] callerContractAddress = deployCallerContractAndGetItsAddress(calledContractAddress);
        /* =================================== CALL makeTheCall =================================== */
        byte[] triggerData1 = TVMTestUtils.parseABI("makeTheCall()", "");
        InternalTransactionComplexTest.runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(InternalTransactionComplexTest.OWNER_ADDRESS), callerContractAddress, triggerData1, 0, 100000000, InternalTransactionComplexTest.deposit, null);
        /* =================================== CALL testCallbackReturns_ to check data =================================== */
        byte[] triggerData2 = TVMTestUtils.parseABI("testCallbackReturns_()", "");
        InternalTransactionComplexTest.runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(InternalTransactionComplexTest.OWNER_ADDRESS), callerContractAddress, triggerData2, 0, 100000000, InternalTransactionComplexTest.deposit, null);
        // bool true => 0000000000000000000000000000000000000000000000000000000000000001,
        // uint256 314159 =>000000000000000000000000000000000000000000000000000000000004cb2f,
        // byte32 0x123456 =>  0000000000000000000000000000000000000000000000000000000000123456
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), ("0000000000000000000000000000000000000000000000000000000000000001" + ("000000000000000000000000000000000000000000000000000000000004cb2f" + "0000000000000000000000000000000000000000000000000000000000123456")));
    }
}

