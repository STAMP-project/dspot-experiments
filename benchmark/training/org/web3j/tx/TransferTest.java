package org.web3j.tx;


import Convert.Unit.ETHER;
import Convert.Unit.WEI;
import SampleKeys.CREDENTIALS;
import java.math.BigDecimal;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;


public class TransferTest extends ManagedTransactionTester {
    protected TransactionReceipt transactionReceipt;

    @Test
    public void testSendFunds() throws Exception {
        Assert.assertThat(sendFunds(CREDENTIALS, ManagedTransactionTester.ADDRESS, BigDecimal.TEN, ETHER), CoreMatchers.is(transactionReceipt));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTransferInvalidValue() throws Exception {
        sendFunds(CREDENTIALS, ManagedTransactionTester.ADDRESS, new BigDecimal(0.1), WEI);
    }
}

