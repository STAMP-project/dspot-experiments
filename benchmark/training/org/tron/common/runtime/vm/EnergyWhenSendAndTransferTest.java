package org.tron.common.runtime.vm;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.TVMTestResult;
import org.tron.common.runtime.TVMTestUtils;
import org.tron.common.storage.DepositImpl;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;


@Slf4j
public class EnergyWhenSendAndTransferTest {
    private Manager dbManager;

    private TronApplicationContext context;

    private DepositImpl deposit;

    private String dbPath = "output_EnergyWhenSendAndTransferTest";

    private String OWNER_ADDRESS;

    private Application AppT;

    private long totalBalance = 30000000000000L;

    // solidity for callValueTest
    // pragma solidity ^0.4.0;
    // 
    // contract SubContract {
    // 
    // constructor () payable {}
    // mapping(uint256=>uint256) map;
    // 
    // function doSimple() public payable returns (uint ret) {
    // return 42;
    // }
    // 
    // function doComplex() public payable returns (uint ret) {
    // for (uint i = 0; i < 10; i++) {
    // map[i] = i;
    // }
    // }
    // 
    // }
    // 
    // contract TestForValueGasFunction {
    // 
    // SubContract subContract;
    // 
    // constructor () payable {
    // subContract = new SubContract();
    // }
    // 
    // function simpleCall() public { subContract.doSimple.value(10).gas(3)(); }
    // 
    // function complexCall() public { subContract.doComplex.value(10).gas(3)(); }
    // 
    // }
    @Test
    public void callValueTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 10000000L;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        byte[] address = Hex.decode(OWNER_ADDRESS);
        TVMTestResult result = deployCallValueTestContract(value, feeLimit, consumeUserResourcePercent);
        long expectEnergyUsageTotal = 174639;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        byte[] contractAddress = result.getContractAddress();
        Assert.assertEquals(deposit.getAccount(contractAddress).getBalance(), value);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - (expectEnergyUsageTotal * 100)));
        /* =================================== CALL simpleCall() =================================== */
        byte[] triggerData = TVMTestUtils.parseABI("simpleCall()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = 7370;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
        /* =================================== CALL complexCall() =================================== */
        triggerData = TVMTestUtils.parseABI("complexCall()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal3 = 9459;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal3);
        Assert.assertEquals(getResult().isRevert(), true);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - (((expectEnergyUsageTotal + expectEnergyUsageTotal2) + expectEnergyUsageTotal3) * 100)));
    }

    // solidity for sendTest and transferTest
    // pragma solidity ^0.4.0;
    // 
    // contract SubContract {
    // 
    // constructor () payable {}
    // mapping(uint256=>uint256) map;
    // 
    // function () payable {
    // map[1] = 1;
    // }
    // }
    // 
    // contract TestForSendAndTransfer {
    // 
    // SubContract subContract;
    // 
    // constructor () payable {
    // subContract = new SubContract();
    // }
    // 
    // 
    // function doSend() public { address(subContract).send(10000); }
    // 
    // function doTransfer() public { address(subContract).transfer(10000); }
    // 
    // function getBalance() public view returns(uint256 balance){
    // balance = address(this).balance;
    // }
    // 
    // }
    @Test
    public void sendTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 1000L;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        byte[] address = Hex.decode(OWNER_ADDRESS);
        TVMTestResult result = deploySendAndTransferTestContract(value, feeLimit, consumeUserResourcePercent);
        long expectEnergyUsageTotal = 140194;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        byte[] contractAddress = result.getContractAddress();
        Assert.assertEquals(deposit.getAccount(contractAddress).getBalance(), value);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - (expectEnergyUsageTotal * 100)));
        /* =================================== CALL doSend() =================================== */
        byte[] triggerData = TVMTestUtils.parseABI("doSend()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = 7025;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().getException(), null);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertEquals(deposit.getAccount(contractAddress).getBalance(), value);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    @Test
    public void transferTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 1000L;
        // long value = 10000000L;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        byte[] address = Hex.decode(OWNER_ADDRESS);
        TVMTestResult result = deploySendAndTransferTestContract(value, feeLimit, consumeUserResourcePercent);
        long expectEnergyUsageTotal = 140194;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        byte[] contractAddress = result.getContractAddress();
        Assert.assertEquals(deposit.getAccount(contractAddress).getBalance(), value);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - (expectEnergyUsageTotal * 100)));
        /* =================================== CALL doSend() =================================== */
        byte[] triggerData = TVMTestUtils.parseABI("doTransfer()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = 7030;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().getException(), null);
        Assert.assertEquals(getResult().isRevert(), true);
        Assert.assertEquals(deposit.getAccount(contractAddress).getBalance(), value);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), (((totalBalance) - value) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }
}

