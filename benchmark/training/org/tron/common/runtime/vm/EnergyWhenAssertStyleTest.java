package org.tron.common.runtime.vm;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.TVMTestResult;
import org.tron.common.runtime.TVMTestUtils;
import org.tron.common.runtime.vm.program.Program.IllegalOperationException;
import org.tron.common.runtime.vm.program.Program.OutOfMemoryException;
import org.tron.common.storage.DepositImpl;
import org.tron.core.db.Manager;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;


@Slf4j
public class EnergyWhenAssertStyleTest {
    private Manager dbManager;

    private TronApplicationContext context;

    private DepositImpl deposit;

    private String dbPath = "output_EnergyWhenAssertStyleTest";

    private String OWNER_ADDRESS;

    private Application AppT;

    private long totalBalance = 30000000000000L;

    // An assert-style exception is generated in the following situations:
    // If you access an array at a too large or negative index (i.e. x[i] where i >= x.length or i < 0).
    // If you access a fixed-length bytesN at a too large or negative index.
    // If you divide or modulo by zero (e.g. 5 / 0 or 23 % 0).
    // If you shift by a negative amount.
    // If you convert a value too big or negative into an enum type.
    // If you call a zero-initialized variable of internal function type.
    // If you call assert with an argument that evaluates to false.
    // If you call a system precompile contract and fail.
    // If you out of memory
    // If you overflow
    // pragma solidity ^0.4.0;
    // 
    // contract TestOutOfIndexContract{
    // 
    // function testOutOfIndex() public {
    // uint256[] memory a = new uint256[](10);
    // a[10] = 10;
    // }
    // 
    // }
    @Test
    public void outOfIndexTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testOutOfIndex\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "608060405234801561001057600080fd5b5060c58061001f6000396000f300608060405260043610603e5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416639a4e1fa081146043575b600080fd5b348015604e57600080fd5b5060556057565b005b60408051600a80825261016082019092526060916020820161014080388339019050509050600a81600a815181101515608c57fe5b60209081029091010152505600a165627a7a723058201aaf6626083e32afa834a13d3365784c509d10f57ce1024f88c697cf0718795e0029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 39487;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testOutOfIndex()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestbytesNContract{
    // 
    // function testbytesN() public {
    // bytes16 a = 0x12345;
    // uint c = 20;
    // uint b = uint256(a[c]);
    // }
    // }
    @Test
    public void bytesNTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testbytesN\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "6080604052348015600f57600080fd5b50609f8061001e6000396000f300608060405260043610603e5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416631e76e10781146043575b600080fd5b348015604e57600080fd5b5060556057565b005b7201234500000000000000000000000000000000601460008282fe00a165627a7a72305820a1c7c81d642cc0aa11c43d63614a5b3c018e4af84700af4bfde5f2efb18b55130029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 31875;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testbytesN()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestDivZeroContract{
    // 
    // function testDivZero() public {
    // uint256 a = 0;
    // uint256 b = 10 / a;
    // }
    // }
    @Test
    public void divZeroTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testDivZero\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "6080604052348015600f57600080fd5b50608b8061001e6000396000f300608060405260043610603e5763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663b87d948d81146043575b600080fd5b348015604e57600080fd5b5060556057565b005b60008080600afe00a165627a7a7230582084ed35f2e244d6721bb5f5fcaf53d237ea050b3de84d5cc7fee74584fd2ff31f0029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 27875;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testDivZero()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestShiftByNegativeContract{
    // 
    // function testShiftByNegative() public {
    // int256 shift = -10;
    // int256 a = 1024 >> shift;
    // }
    // 
    // }
    @Test
    public void shiftByNegativeTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testShiftByNegative\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "6080604052348015600f57600080fd5b50608e8061001e6000396000f300608060405260043610603e5763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663e88e362a81146043575b600080fd5b348015604e57600080fd5b5060556057565b005b600919600081610400fe00a165627a7a7230582086c99cfe65e26909bb0fb3a2bdaf2385ad8dfff72680adab954063a4fe1d549b0029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 28475;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testShiftByNegative()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestEnumTypeContract {
    // 
    // enum fortest {one, second, third}
    // 
    // function testEnumType() public {
    // fortest a = fortest(10);
    // 
    // }
    // }
    @Test
    public void enumTypeTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testEnumType\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "6080604052348015600f57600080fd5b5060898061001e6000396000f300608060405260043610603e5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416635a43cddc81146043575b600080fd5b348015604e57600080fd5b5060556057565b005b6000600afe00a165627a7a72305820b24a4d459b753723d300f56c408c6120d5ef0c7ddb166d66ccf4277a76ad83ed0029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 27475;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testEnumType()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestFunctionPointerContract {
    // 
    // function testFunctionPointer() public {
    // function (int) internal pure returns (int) funcPtr;
    // funcPtr(1);
    // }
    // }
    @Test
    public void functionPointerTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testFunctionPointer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "6080604052348015600f57600080fd5b5060988061001e6000396000f300608060405260043610603e5763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663e9ad8ee781146043575b600080fd5b348015604e57600080fd5b5060556057565b005b606a606660018263ffffffff16565b5050565bfe00a165627a7a723058201c8982fa288ec7aad86b1d1992ecc5d08c4b22e4fe037981f91aff8bcbd900680029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 30475;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testFunctionPointer()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestAssertContract{
    // 
    // function testAssert(){
    // assert(1==2);
    // }
    // 
    // }
    @Test
    public void assertTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[],\"name\":\"testAssert\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "6080604052348015600f57600080fd5b5060858061001e6000396000f300608060405260043610603e5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416632b813bc081146043575b600080fd5b348015604e57600080fd5b5060556057565b005bfe00a165627a7a723058208ce7511bd3a946a22baaba2b4521cbf29d2481ad52887c5567e422cd89726eda0029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 26675;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        byte[] triggerData = TVMTestUtils.parseABI("testAssert()", null);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof IllegalOperationException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }

    // pragma solidity ^0.4.0;
    // 
    // contract TestMemContract{
    // 
    // function testMem(uint256 end) public {
    // for (uint256 i = 0; i < end; i++) {
    // uint256[] memory theArray = new uint256[](1024 * 1024 * 3 + 1024);
    // }
    // }
    // }
    @Test
    public void outOfMemTest() throws ContractExeException, ContractValidateException, ReceiptCheckErrException, VMIllegalException {
        long value = 0;
        long feeLimit = 1000000000L;// sun

        long consumeUserResourcePercent = 100;
        String contractName = "test";
        byte[] address = Hex.decode(OWNER_ADDRESS);
        String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"end\",\"type\":\"uint256\"}],\"name\":\"testMem\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        String code = "608060405234801561001057600080fd5b5060ca8061001f6000396000f300608060405260043610603e5763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663e31fcf3c81146043575b600080fd5b348015604e57600080fd5b506058600435605a565b005b600060605b828210156099576040805162300400808252630600802082019092529060208201630600800080388339019050506001909201919050605f565b5050505600a165627a7a723058209e5d294a7bf5133b304bc6851c749cd5e1f4748230405755e6bd2e31549ae1d00029";
        String libraryAddressPair = null;
        TVMTestResult result = TVMTestUtils.deployContractAndReturnTVMTestResult(contractName, address, ABI, code, value, feeLimit, consumeUserResourcePercent, libraryAddressPair, dbManager, null);
        long expectEnergyUsageTotal = 40487;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal);
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - (expectEnergyUsageTotal * 100)));
        byte[] contractAddress = result.getContractAddress();
        String params = "0000000000000000000000000000000000000000000000000000000000000001";
        byte[] triggerData = TVMTestUtils.parseABI("testMem(uint256)", params);
        result = TVMTestUtils.triggerContractAndReturnTVMTestResult(Hex.decode(OWNER_ADDRESS), contractAddress, triggerData, 0, feeLimit, dbManager, null);
        long expectEnergyUsageTotal2 = feeLimit / 100;
        Assert.assertEquals(result.getReceipt().getEnergyUsageTotal(), expectEnergyUsageTotal2);
        Assert.assertEquals(getResult().isRevert(), false);
        Assert.assertTrue(((getResult().getException()) instanceof OutOfMemoryException));
        Assert.assertEquals(dbManager.getAccountStore().get(address).getBalance(), ((totalBalance) - ((expectEnergyUsageTotal + expectEnergyUsageTotal2) * 100)));
    }
}

