/**
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.protocols.payments;


import Coin.ZERO;
import PaymentProtocolException.Expired;
import PaymentProtocolException.InvalidNetwork;
import Protos.Output.Builder;
import Protos.Payment;
import Protos.PaymentDetails;
import Protos.PaymentRequest;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Assert;
import org.junit.Test;


public class PaymentSessionTest {
    private static final NetworkParameters TESTNET = TestNet3Params.get();

    private static final NetworkParameters MAINNET = MainNetParams.get();

    private static final String simplePaymentUrl = "http://a.simple.url.com/";

    private static final String paymentRequestMemo = "send coinz noa plz kthx";

    private static final String paymentMemo = "take ze coinz";

    private static final ByteString merchantData = ByteString.copyFromUtf8("merchant data");

    private static final long time = (System.currentTimeMillis()) / 1000L;

    private ECKey serverKey;

    private Transaction tx;

    private TransactionOutput outputToMe;

    private Coin coin = Coin.COIN;

    @Test
    public void testSimplePayment() throws Exception {
        // Create a PaymentRequest and make sure the correct values are parsed by the PaymentSession.
        PaymentSessionTest.MockPaymentSession paymentSession = new PaymentSessionTest.MockPaymentSession(newSimplePaymentRequest("test"));
        Assert.assertEquals(PaymentSessionTest.paymentRequestMemo, getMemo());
        Assert.assertEquals(coin, getValue());
        Assert.assertEquals(PaymentSessionTest.simplePaymentUrl, getPaymentUrl());
        Assert.assertTrue(new Date(((PaymentSessionTest.time) * 1000L)).equals(getDate()));
        Assert.assertTrue(getSendRequest().tx.equals(tx));
        Assert.assertFalse(isExpired());
        // Send the payment and verify that the correct information is sent.
        // Add a dummy input to tx so it is considered valid.
        tx.addInput(new org.bitcoinj.core.TransactionInput(PaymentSessionTest.TESTNET, tx, outputToMe.getScriptBytes()));
        ArrayList<Transaction> txns = new ArrayList<>();
        txns.add(tx);
        Address refundAddr = LegacyAddress.fromKey(PaymentSessionTest.TESTNET, serverKey);
        paymentSession.sendPayment(txns, refundAddr, PaymentSessionTest.paymentMemo);
        Assert.assertEquals(1, paymentSession.getPaymentLog().size());
        Assert.assertEquals(PaymentSessionTest.simplePaymentUrl, paymentSession.getPaymentLog().get(0).getUrl().toString());
        Protos.Payment payment = paymentSession.getPaymentLog().get(0).getPayment();
        Assert.assertEquals(PaymentSessionTest.paymentMemo, payment.getMemo());
        Assert.assertEquals(PaymentSessionTest.merchantData, payment.getMerchantData());
        Assert.assertEquals(1, payment.getRefundToCount());
        Assert.assertEquals(coin.value, payment.getRefundTo(0).getAmount());
        TransactionOutput refundOutput = new TransactionOutput(PaymentSessionTest.TESTNET, null, coin, refundAddr);
        ByteString refundScript = ByteString.copyFrom(refundOutput.getScriptBytes());
        Assert.assertTrue(refundScript.equals(payment.getRefundTo(0).getScript()));
    }

    @Test
    public void testDefaults() throws Exception {
        Protos.Output.Builder outputBuilder = Protos.Output.newBuilder().setScript(ByteString.copyFrom(outputToMe.getScriptBytes()));
        Protos.PaymentDetails paymentDetails = PaymentDetails.newBuilder().setTime(PaymentSessionTest.time).addOutputs(outputBuilder).build();
        Protos.PaymentRequest paymentRequest = PaymentRequest.newBuilder().setSerializedPaymentDetails(paymentDetails.toByteString()).build();
        PaymentSessionTest.MockPaymentSession paymentSession = new PaymentSessionTest.MockPaymentSession(paymentRequest);
        Assert.assertEquals(ZERO, getValue());
        Assert.assertNull(getPaymentUrl());
        Assert.assertNull(getMemo());
    }

    @Test
    public void testExpiredPaymentRequest() throws Exception {
        PaymentSessionTest.MockPaymentSession paymentSession = new PaymentSessionTest.MockPaymentSession(newExpiredPaymentRequest());
        Assert.assertTrue(isExpired());
        // Send the payment and verify that an exception is thrown.
        // Add a dummy input to tx so it is considered valid.
        tx.addInput(new org.bitcoinj.core.TransactionInput(PaymentSessionTest.TESTNET, tx, outputToMe.getScriptBytes()));
        ArrayList<Transaction> txns = new ArrayList<>();
        txns.add(tx);
        try {
            paymentSession.sendPayment(txns, null, null);
        } catch (PaymentProtocolException e) {
            Assert.assertEquals(0, paymentSession.getPaymentLog().size());
            Assert.assertEquals(e.getMessage(), "PaymentRequest is expired");
            return;
        }
        Assert.fail("Expected exception due to expired PaymentRequest");
    }

    @Test(expected = InvalidNetwork.class)
    public void testWrongNetwork() throws Exception {
        // Create a PaymentRequest and make sure the correct values are parsed by the PaymentSession.
        PaymentSessionTest.MockPaymentSession paymentSession = new PaymentSessionTest.MockPaymentSession(newSimplePaymentRequest("main"));
        Assert.assertEquals(PaymentSessionTest.MAINNET, getNetworkParameters());
        // Send the payment and verify that the correct information is sent.
        // Add a dummy input to tx so it is considered valid.
        tx.addInput(new org.bitcoinj.core.TransactionInput(PaymentSessionTest.TESTNET, tx, outputToMe.getScriptBytes()));
        ArrayList<Transaction> txns = new ArrayList<>();
        txns.add(tx);
        Address refundAddr = LegacyAddress.fromKey(PaymentSessionTest.TESTNET, serverKey);
        paymentSession.sendPayment(txns, refundAddr, PaymentSessionTest.paymentMemo);
        Assert.assertEquals(1, paymentSession.getPaymentLog().size());
    }

    private class MockPaymentSession extends PaymentSession {
        private ArrayList<PaymentSessionTest.MockPaymentSession.PaymentLogItem> paymentLog = new ArrayList<>();

        public MockPaymentSession(Protos.PaymentRequest request) throws PaymentProtocolException {
            super(request);
        }

        public ArrayList<PaymentSessionTest.MockPaymentSession.PaymentLogItem> getPaymentLog() {
            return paymentLog;
        }

        @Override
        protected ListenableFuture<PaymentProtocol.Ack> sendPayment(final URL url, final Protos.Payment payment) {
            paymentLog.add(new PaymentSessionTest.MockPaymentSession.PaymentLogItem(url, payment));
            return null;
        }

        public class PaymentLogItem {
            private final URL url;

            private final Payment payment;

            PaymentLogItem(final URL url, final Protos.Payment payment) {
                this.url = url;
                this.payment = payment;
            }

            public URL getUrl() {
                return url;
            }

            public Payment getPayment() {
                return payment;
            }
        }
    }
}

