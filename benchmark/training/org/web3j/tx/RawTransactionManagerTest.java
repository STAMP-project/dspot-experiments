package org.web3j.tx;


import Convert.Unit.ETHER;
import java.math.BigDecimal;
import org.junit.Test;
import org.web3j.crypto.SampleKeys;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.exceptions.TxHashMismatchException;


public class RawTransactionManagerTest extends ManagedTransactionTester {
    @Test(expected = TxHashMismatchException.class)
    public void testTxHashMismatch() throws Exception {
        TransactionReceipt transactionReceipt = prepareTransfer();
        prepareTransaction(transactionReceipt);
        TransactionManager transactionManager = new RawTransactionManager(web3j, SampleKeys.CREDENTIALS);
        Transfer transfer = new Transfer(web3j, transactionManager);
        transfer.sendFunds(ManagedTransactionTester.ADDRESS, BigDecimal.ONE, ETHER).send();
    }
}

