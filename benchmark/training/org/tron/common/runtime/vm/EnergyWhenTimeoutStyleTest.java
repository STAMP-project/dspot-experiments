package org.tron.common.runtime.vm;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.TVMTestResult;
import org.tron.common.runtime.TVMTestUtils;
import org.tron.common.runtime.vm.program.Program.OutOfEnergyException;
import org.tron.common.runtime.vm.program.Program.OutOfTimeException;
import org.tron.common.storage.DepositImpl;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;


@Slf4j
public class EnergyWhenTimeoutStyleTest {
    private Manager dbManager;

    private TronApplicationContext context;

    private DepositImpl deposit;

    private String dbPath = "output_CPUTimeTest";

    private String OWNER_ADDRESS;

    private Application AppT;

    private long totalBalance = 30000000000000L;

    // solidity for endlessLoopTest
    // pragma solidity ^0.4.0;
    // 
    // contract TestForEndlessLoop {
    // 
    // uint256 vote;
    // constructor () public {
    // vote = 0;
    // }
    // 
    // function getVote() public constant returns (uint256 _vote) {
    // _vote = vote;
    // }
    // 
    // function setVote(uint256 _vote) public {
    // vote = _vote;
    // while(true)
    // {
    // vote += 1;
    // }
    // }
    // }
    @Test
    public void endlessLoopTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;
        byte[] address = Hex.decode(OWNER_ADDRESS);
        long consumeUserResourcePercent = 0;
        TVMTestResult result = deployEndlessLoopContract(value, feeLimit, consumeUserResourcePercent);
        if (null != (getResult().getException())) {
            long expectEnergyUsageTotal = feeLimit / 100;
            Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
            Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
            return;
        }
        long expectEnergyUsageTotal = 55107;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        /* =================================== CALL setVote(uint256) =================================== */
        String params = "0000000000000000000000000000000000000000000000000000000000000003";
        byte[] triggerData = TVMTestUtils.parseABI("setVote(uint256)", params);
        boolean haveException = false;
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, value, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Exception exception = getResult().getException();
        Assert.assertTrue(((exception instanceof OutOfTimeException) || (exception instanceof OutOfEnergyException)));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }
}

