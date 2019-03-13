package org.web3j.protocol.scenarios;


import java.math.BigInteger;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;


/**
 * Create, sign and send a raw transaction.
 */
public class CreateRawTransactionIT extends Scenario {
    @Test
    public void testTransferEther() throws Exception {
        BigInteger nonce = getNonce(Scenario.ALICE.getAddress());
        RawTransaction rawTransaction = CreateRawTransactionIT.createEtherTransaction(nonce, Scenario.BOB.getAddress());
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Scenario.ALICE);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        TestCase.assertFalse(transactionHash.isEmpty());
        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);
        Assert.assertThat(transactionReceipt.getTransactionHash(), Is.is(transactionHash));
    }

    @Test
    public void testDeploySmartContract() throws Exception {
        BigInteger nonce = getNonce(Scenario.ALICE.getAddress());
        RawTransaction rawTransaction = CreateRawTransactionIT.createSmartContractTransaction(nonce);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Scenario.ALICE);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        TestCase.assertFalse(transactionHash.isEmpty());
        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);
        Assert.assertThat(transactionReceipt.getTransactionHash(), Is.is(transactionHash));
        TestCase.assertFalse("Contract execution ran out of gas", rawTransaction.getGasLimit().equals(transactionReceipt.getGasUsed()));
    }
}

