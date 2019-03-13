package org.tron.common.runtime.vm;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.TVMTestUtils;
import org.tron.common.storage.DepositImpl;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;


@Slf4j
public class InternalTransactionCallTest {
    private Runtime runtime;

    private Manager dbManager;

    private TronApplicationContext context;

    private DepositImpl deposit;

    private String dbPath = "output_InternalTransactionCallTest";

    private String OWNER_ADDRESS;

    private Application AppT;

    /**
     * contract A {
     *   uint256 public numberForB;
     *   address public senderForB;
     *   function callTest(address bAddress, uint256 _number) {
     *     bAddress.call(bytes4(sha3("setValue(uint256)")), _number); // B's storage is set, A is not modified
     *   }
     *
     *   function callcodeTest(address bAddress, uint256 _number) {
     *     bAddress.callcode(bytes4(sha3("setValue(uint256)")), _number); // A's storage is set, B is not modified
     *   }
     *
     *   function delegatecallTest(address bAddress, uint256 _number) {
     *     bAddress.delegatecall(bytes4(sha3("setValue(uint256)")), _number); // A's storage is set, B is not modified
     *   }
     * }
     *
     * contract B {
     *   uint256 public numberForB;
     *   address public senderForB;
     *
     *   function setValue(uint256 _number) {
     *     numberForB = _number;
     *     senderForB = msg.sender;
     *     // senderForB is A if invoked by A's callTest. B's storage will be updated
     *     // senderForB is A if invoked by A's callcodeTest. None of B's storage is updated
     *     // senderForB is OWNER if invoked by A's delegatecallTest. None of B's storage is updated
     *   }
     * }
     */
    /* A call B, anything belongs to A should not be changed, B should be changed.
    msg.sender for contractB should be A's address.
     */
    @Test
    public void callTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] contractBAddress = deployBContractAndGetItsAddress();
        byte[] contractAAddress = deployAContractandGetItsAddress();
        /* =================================== CALL callTest() to change B storage =================================== */
        String params = (Hex.toHexString(getData())) + "0000000000000000000000000000000000000000000000000000000000000003";
        byte[] triggerData = TVMTestUtils.parseABI("callTest(address,uint256)", params);
        TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData, 0, 1000000000, deposit, null);
        /* =================================== CALL numberForB() to check A's numberForB =================================== */
        byte[] triggerData2 = TVMTestUtils.parseABI("numberForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData2, 0, 1000000000, deposit, null);
        // A should not be changed
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000000");
        /* =================================== CALL senderForB() to check A's senderForB =================================== */
        byte[] triggerData3 = TVMTestUtils.parseABI("senderForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData3, 0, 1000000000, deposit, null);
        // A should be changed
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000000");
        /* =================================== CALL numberForB() to check B's numberForB =================================== */
        byte[] triggerData4 = TVMTestUtils.parseABI("numberForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractBAddress, triggerData4, 0, 1000000000, deposit, null);
        // B's numberForB should be changed to 3
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000003");
        /* =================================== CALL senderForB() to check B's senderForB =================================== */
        byte[] triggerData5 = TVMTestUtils.parseABI("senderForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractBAddress, triggerData5, 0, 1000000000, deposit, null);
        // B 's senderForB should be A
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), Hex.toHexString(getData()));
    }

    /* A delegatecall B, A should be changed, anything belongs to B should not be changed.
    msg.sender for contractB should be Caller(OWNER_ADDRESS), but this value will not be effected in B's senderForB since we use delegatecall.
    We store it in A's senderForB.
     */
    @Test
    public void delegateCallTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] contractBAddress = deployBContractAndGetItsAddress();
        byte[] contractAAddress = deployAContractandGetItsAddress();
        /* =================================== CALL delegatecallTest() to change B storage =================================== */
        String params = (Hex.toHexString(getData())) + "0000000000000000000000000000000000000000000000000000000000000003";
        byte[] triggerData = TVMTestUtils.parseABI("delegatecallTest(address,uint256)", params);
        TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData, 0, 1000000000, deposit, null);
        /* =================================== CALL numberForB() to check A's numberForB =================================== */
        byte[] triggerData2 = TVMTestUtils.parseABI("numberForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData2, 0, 1000000000, deposit, null);
        // A should be changed to 3
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000003");
        /* =================================== CALL senderForB() to check A's senderForB =================================== */
        byte[] triggerData3 = TVMTestUtils.parseABI("senderForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData3, 0, 1000000000, deposit, null);
        // A's senderForB should be changed to caller's contract Address (OWNER_ADDRESS)
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), Hex.toHexString(getData()));
        /* =================================== CALL numberForB() to check B's numberForB =================================== */
        byte[] triggerData4 = TVMTestUtils.parseABI("numberForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractBAddress, triggerData4, 0, 1000000000, deposit, null);
        // B's numberForB should not be changed
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000000");
        /* =================================== CALL senderForB() to check B's senderForB =================================== */
        byte[] triggerData5 = TVMTestUtils.parseABI("senderForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractBAddress, triggerData5, 0, 1000000000, deposit, null);
        // B 's senderForB should not be changed
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000000");
    }

    /* A callcode B, A should be changed, anything belongs to B should not be changed.
    msg.sender for contractB should be A, but this value will not be effected in B's senderForB since we use callcode.
    We store it in A's senderForB.
     */
    @Test
    public void callCodeTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        byte[] contractBAddress = deployBContractAndGetItsAddress();
        byte[] contractAAddress = deployAContractandGetItsAddress();
        /* =================================== CALL callcodeTest() to change B storage =================================== */
        String params = (Hex.toHexString(getData())) + "0000000000000000000000000000000000000000000000000000000000000003";
        byte[] triggerData = TVMTestUtils.parseABI("callcodeTest(address,uint256)", params);
        TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData, 0, 1000000000, deposit, null);
        /* =================================== CALL numberForB() to check A's numberForB =================================== */
        byte[] triggerData2 = TVMTestUtils.parseABI("numberForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData2, 0, 1000000000, deposit, null);
        // A should be changed to 3
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000003");
        /* =================================== CALL senderForB() to check A's senderForB =================================== */
        byte[] triggerData3 = TVMTestUtils.parseABI("senderForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractAAddress, triggerData3, 0, 1000000000, deposit, null);
        // A's senderForB should be changed to A's contract Address
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), Hex.toHexString(getData()));
        /* =================================== CALL numberForB() to check B's numberForB =================================== */
        byte[] triggerData4 = TVMTestUtils.parseABI("numberForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractBAddress, triggerData4, 0, 1000000000, deposit, null);
        // B's numberForB should not be changed
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000000");
        /* =================================== CALL senderForB() to check B's senderForB =================================== */
        byte[] triggerData5 = TVMTestUtils.parseABI("senderForB()", "");
        runtime = TVMTestUtils.triggerContractWholeProcessReturnContractAddress(Hex.decode(OWNER_ADDRESS), contractBAddress, triggerData5, 0, 1000000000, deposit, null);
        // B 's senderForB should not be changed
        Assert.assertEquals(Hex.toHexString(getResult().getHReturn()), "0000000000000000000000000000000000000000000000000000000000000000");
    }

    @Test
    public void staticCallTest() {
        // TODO: need to implement this
    }
}

