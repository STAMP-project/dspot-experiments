package org.web3j.protocol.scenarios;


import java.math.BigInteger;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;


/**
 * Integration test demonstrating the full contract deployment workflow.
 */
public class DeployContractIT extends Scenario {
    @Test
    public void testContractCreation() throws Exception {
        boolean accountUnlocked = unlockAccount();
        Assert.assertTrue(accountUnlocked);
        String transactionHash = sendTransaction();
        TestCase.assertFalse(transactionHash.isEmpty());
        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);
        Assert.assertThat(transactionReceipt.getTransactionHash(), Is.is(transactionHash));
        TestCase.assertFalse("Contract execution ran out of gas", transactionReceipt.getGasUsed().equals(Scenario.GAS_LIMIT));
        String contractAddress = transactionReceipt.getContractAddress();
        Assert.assertNotNull(contractAddress);
        Function function = createFibonacciFunction();
        String responseValue = callSmartContractFunction(function, contractAddress);
        TestCase.assertFalse(responseValue.isEmpty());
        List<Type> uint = FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        Assert.assertThat(uint.size(), Is.is(1));
        Assert.assertThat(uint.get(0).getValue(), IsEqual.equalTo(BigInteger.valueOf(13)));
    }
}

