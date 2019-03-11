package org.web3j.tx.response;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;


public class PollingTransactionReceiptProcessorTest {
    private static final String TRANSACTION_HASH = "0x00";

    private Web3j web3j;

    private long sleepDuration;

    private int attempts;

    private PollingTransactionReceiptProcessor processor;

    @Test
    public void returnsTransactionReceiptWhenItIsAvailableInstantly() throws Exception {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        Mockito.doReturn(PollingTransactionReceiptProcessorTest.requestReturning(PollingTransactionReceiptProcessorTest.response(transactionReceipt))).when(web3j).ethGetTransactionReceipt(PollingTransactionReceiptProcessorTest.TRANSACTION_HASH);
        TransactionReceipt receipt = processor.waitForTransactionReceipt(PollingTransactionReceiptProcessorTest.TRANSACTION_HASH);
        Assert.assertThat(receipt, CoreMatchers.sameInstance(transactionReceipt));
    }

    @Test
    public void throwsTransactionExceptionWhenReceiptIsNotAvailableInTime() throws Exception {
        Mockito.doReturn(PollingTransactionReceiptProcessorTest.requestReturning(PollingTransactionReceiptProcessorTest.response(null))).when(web3j).ethGetTransactionReceipt(PollingTransactionReceiptProcessorTest.TRANSACTION_HASH);
        try {
            processor.waitForTransactionReceipt(PollingTransactionReceiptProcessorTest.TRANSACTION_HASH);
            Assert.fail("call should fail with TransactionException");
        } catch (TransactionException e) {
            Assert.assertTrue(e.getTransactionHash().isPresent());
            Assert.assertEquals(e.getTransactionHash().get(), PollingTransactionReceiptProcessorTest.TRANSACTION_HASH);
        }
    }
}

