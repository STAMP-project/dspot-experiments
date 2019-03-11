/**
 * Copyright 2013 Google Inc.
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
package org.bitcoinj.protocols.channels;


import AbstractBlockChain.NewBlockType.BEST_CHAIN;
import Coin.SATOSHI;
import Coin.ZERO;
import PaymentChannelClient.VersionSelector;
import PaymentChannelClientState.IncrementedPayment;
import PaymentChannelClientState.State.NEW;
import PaymentChannelClientState.State.PROVIDE_MULTISIG_CONTRACT_TO_SERVER;
import PaymentChannelClientState.State.READY;
import PaymentChannelClientState.State.SAVE_STATE_IN_WALLET;
import PaymentChannelServerState.State.CLOSED;
import PaymentChannelServerState.State.CLOSING;
import PaymentChannelServerState.State.ERROR;
import PaymentChannelServerState.State.WAITING_FOR_MULTISIG_ACCEPTANCE;
import PaymentChannelServerState.State.WAITING_FOR_MULTISIG_CONTRACT;
import Transaction.MIN_NONDUST_OUTPUT;
import Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
import Transaction.SigHash.ANYONECANPAY_NONE;
import Transaction.SigHash.ANYONECANPAY_SINGLE;
import Transaction.SigHash.NONE;
import TransactionInput.NO_SEQUENCE;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bitcoinj.testing.FakeTxBuilder;
import org.bitcoinj.testing.TestWithWallet;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.bitcoinj.core.Coin.Coin.ZERO;


@RunWith(Parameterized.class)
public class PaymentChannelStateTest extends TestWithWallet {
    private ECKey serverKey;

    private Wallet serverWallet;

    private PaymentChannelServerState serverState;

    private PaymentChannelClientState clientState;

    private TransactionBroadcaster mockBroadcaster;

    private BlockingQueue<PaymentChannelStateTest.TxFuturePair> broadcasts;

    private static final Coin.Coin HALF_COIN = Coin.Coin.valueOf(0, 50);

    @Parameterized.Parameter
    public VersionSelector versionSelector;

    private static class TxFuturePair {
        Transaction tx;

        SettableFuture<Transaction> future;

        public TxFuturePair(Transaction tx, SettableFuture<Transaction> future) {
            this.tx = tx;
            this.future = future;
        }
    }

    @Test
    public void stateErrors() throws Exception {
        PaymentChannelClientState channelState = makeClientState(wallet, myKey, serverKey, COIN.multiply(10), 20);
        Assert.assertEquals(NEW, channelState.getState());
        try {
            channelState.getContract();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        try {
            channelState.initiate();
            Assert.fail();
        } catch (InsufficientMoneyException e) {
        }
    }

    @Test
    public void basic() throws Exception {
        // Check it all works when things are normal (no attacks, no problems).
        Utils.setMockClock();// Use mock clock

        final long EXPIRE_TIME = (Utils.currentTimeSeconds()) + ((60 * 60) * 24);
        serverState = makeServerState(mockBroadcaster, serverWallet, serverKey, EXPIRE_TIME);
        Assert.assertEquals(getInitialServerState(), serverState.getState());
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), PaymentChannelStateTest.HALF_COIN, EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        clientState.initiate();
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        // Send the refund tx from client to server and get back the signature.
        Transaction refund;
        if (useRefunds()) {
            refund = new Transaction(TestWithWallet.UNITTEST, clientV1State().getIncompleteRefundTransaction().bitcoinSerialize());
            byte[] refundSig = serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
            Assert.assertEquals(WAITING_FOR_MULTISIG_CONTRACT, serverState.getState());
            // This verifies that the refund can spend the multi-sig output when run.
            clientV1State().provideRefundSignature(refundSig, null);
        } else {
            refund = clientV2State().getRefundTransaction();
        }
        Assert.assertEquals(SAVE_STATE_IN_WALLET, clientState.getState());
        clientState.fakeSave();
        Assert.assertEquals(PROVIDE_MULTISIG_CONTRACT_TO_SERVER, clientState.getState());
        // Validate the multisig contract looks right.
        Transaction multisigContract = new Transaction(TestWithWallet.UNITTEST, clientState.getContract().bitcoinSerialize());
        Assert.assertEquals(READY, clientState.getState());
        Assert.assertEquals(2, multisigContract.getOutputs().size());// One multi-sig, one change.

        Script script = multisigContract.getOutput(0).getScriptPubKey();
        if ((versionSelector) == (VersionSelector.VERSION_1)) {
            Assert.assertTrue(ScriptPattern.isSentToMultisig(script));
        } else {
            Assert.assertTrue(ScriptPattern.isP2SH(script));
        }
        script = multisigContract.getOutput(1).getScriptPubKey();
        Assert.assertTrue(ScriptPattern.isP2PKH(script));
        Assert.assertTrue(wallet.getPendingTransactions().contains(multisigContract));
        // Provide the server with the multisig contract and simulate successful propagation/acceptance.
        if (!(useRefunds())) {
            serverV2State().provideClientKey(clientState.myKey.getPubKey());
        }
        serverState.provideContract(multisigContract);
        Assert.assertEquals(WAITING_FOR_MULTISIG_ACCEPTANCE, serverState.getState());
        final PaymentChannelStateTest.TxFuturePair pair = broadcasts.take();
        pair.future.set(pair.tx);
        Assert.assertEquals(PaymentChannelServerState.State.READY, serverState.getState());
        // Make sure the refund transaction is not in the wallet and multisig contract's output is not connected to it
        Assert.assertEquals(2, wallet.getTransactions(false).size());
        Iterator<Transaction> walletTransactionIterator = wallet.getTransactions(false).iterator();
        Transaction clientWalletMultisigContract = walletTransactionIterator.next();
        Assert.assertFalse(clientWalletMultisigContract.getTxId().equals(clientState.getRefundTransaction().getTxId()));
        if (!(clientWalletMultisigContract.getTxId().equals(multisigContract.getTxId()))) {
            clientWalletMultisigContract = walletTransactionIterator.next();
            Assert.assertFalse(clientWalletMultisigContract.getTxId().equals(clientState.getRefundTransaction().getTxId()));
        } else
            Assert.assertFalse(walletTransactionIterator.next().getTxId().equals(clientState.getRefundTransaction().getTxId()));

        Assert.assertEquals(multisigContract.getTxId(), clientWalletMultisigContract.getTxId());
        Assert.assertFalse(clientWalletMultisigContract.getInput(0).getConnectedOutput().getSpentBy().getParentTransaction().getTxId().equals(refund.getTxId()));
        // Both client and server are now in the ready state. Simulate a few micropayments of 0.005 bitcoins.
        Coin.Coin size = PaymentChannelStateTest.HALF_COIN.divide(100);
        Coin.Coin totalPayment = ZERO;
        for (int i = 0; i < 4; i++) {
            byte[] signature = clientState.incrementPaymentBy(size, null).signature.encodeToBitcoin();
            totalPayment = totalPayment.add(size);
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signature);
        }
        // Now confirm the contract transaction and make sure payments still work
        chain.add(FakeTxBuilder.makeSolvedTestBlock(blockStore.getChainHead().getHeader(), multisigContract));
        byte[] signature = clientState.incrementPaymentBy(size, null).signature.encodeToBitcoin();
        totalPayment = totalPayment.add(size);
        serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signature);
        // And settle the channel.
        serverState.close();
        Assert.assertEquals(CLOSING, serverState.getState());
        final PaymentChannelStateTest.TxFuturePair pair2 = broadcasts.take();
        Transaction closeTx = pair2.tx;
        pair2.future.set(closeTx);
        final Transaction reserializedCloseTx = new Transaction(TestWithWallet.UNITTEST, closeTx.bitcoinSerialize());
        Assert.assertEquals(CLOSED, serverState.getState());
        // ... and on the client side.
        wallet.receivePending(reserializedCloseTx, null);
        Assert.assertEquals(PaymentChannelClientState.State.CLOSED, clientState.getState());
        // Create a block with the payment transaction in it and give it to both wallets
        chain.add(FakeTxBuilder.makeSolvedTestBlock(blockStore.getChainHead().getHeader(), reserializedCloseTx));
        Assert.assertEquals(size.multiply(5), serverWallet.getBalance());
        Assert.assertEquals(0, serverWallet.getPendingTransactions().size());
        Assert.assertEquals(COIN.subtract(size.multiply(5)), wallet.getBalance());
        Assert.assertEquals(0, wallet.getPendingTransactions().size());
        Assert.assertEquals(3, wallet.getTransactions(false).size());
        walletTransactionIterator = wallet.getTransactions(false).iterator();
        Transaction clientWalletCloseTransaction = walletTransactionIterator.next();
        if (!(clientWalletCloseTransaction.getTxId().equals(closeTx.getTxId())))
            clientWalletCloseTransaction = walletTransactionIterator.next();

        if (!(clientWalletCloseTransaction.getTxId().equals(closeTx.getTxId())))
            clientWalletCloseTransaction = walletTransactionIterator.next();

        Assert.assertEquals(closeTx.getTxId(), clientWalletCloseTransaction.getTxId());
        Assert.assertNotNull(clientWalletCloseTransaction.getInput(0).getConnectedOutput());
    }

    @Test
    public void setupDoS() throws Exception {
        // Check that if the other side stops after we have provided a signed multisig contract, that after a timeout
        // we can broadcast the refund and get our balance back.
        // Spend the client wallet's one coin
        Transaction spendCoinTx = wallet.sendCoinsOffline(SendRequest.to(LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey()), COIN));
        Assert.assertEquals(ZERO, wallet.getBalance());
        chain.add(FakeTxBuilder.makeSolvedTestBlock(blockStore.getChainHead().getHeader(), spendCoinTx, FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, myAddress)));
        Assert.assertEquals(CENT, wallet.getBalance());
        // Set the wallet's stored states to use our real test PeerGroup
        StoredPaymentChannelClientStates stateStorage = new StoredPaymentChannelClientStates(wallet, mockBroadcaster);
        wallet.addOrUpdateExtension(stateStorage);
        Utils.setMockClock();// Use mock clock

        final long EXPIRE_TIME = ((Utils.currentTimeMillis()) / 1000) + ((60 * 60) * 24);
        serverState = makeServerState(mockBroadcaster, serverWallet, serverKey, EXPIRE_TIME);
        Assert.assertEquals(getInitialServerState(), serverState.getState());
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), CENT.divide(2), EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        Assert.assertEquals(CENT.divide(2), clientState.getTotalValue());
        clientState.initiate();
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        if (useRefunds()) {
            // Send the refund tx from client to server and get back the signature.
            Transaction refund = new Transaction(TestWithWallet.UNITTEST, clientV1State().getIncompleteRefundTransaction().bitcoinSerialize());
            byte[] refundSig = serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
            Assert.assertEquals(WAITING_FOR_MULTISIG_CONTRACT, serverState.getState());
            // This verifies that the refund can spend the multi-sig output when run.
            clientV1State().provideRefundSignature(refundSig, null);
        }
        Assert.assertEquals(SAVE_STATE_IN_WALLET, clientState.getState());
        clientState.fakeSave();
        Assert.assertEquals(PROVIDE_MULTISIG_CONTRACT_TO_SERVER, clientState.getState());
        // Validate the multisig contract looks right.
        Transaction multisigContract = new Transaction(TestWithWallet.UNITTEST, clientState.getContract().bitcoinSerialize());
        Assert.assertEquals(READY, clientState.getState());
        Assert.assertEquals(2, multisigContract.getOutputs().size());// One multi-sig, one change.

        Script script = multisigContract.getOutput(0).getScriptPubKey();
        if ((versionSelector) == (VersionSelector.VERSION_1)) {
            Assert.assertTrue(ScriptPattern.isSentToMultisig(script));
        } else {
            Assert.assertTrue(ScriptPattern.isP2SH(script));
        }
        script = multisigContract.getOutput(1).getScriptPubKey();
        Assert.assertTrue(ScriptPattern.isP2PKH(script));
        Assert.assertTrue(wallet.getPendingTransactions().contains(multisigContract));
        // Provide the server with the multisig contract and simulate successful propagation/acceptance.
        if (!(useRefunds())) {
            serverV2State().provideClientKey(clientState.myKey.getPubKey());
        }
        serverState.provideContract(multisigContract);
        Assert.assertEquals(WAITING_FOR_MULTISIG_ACCEPTANCE, serverState.getState());
        final PaymentChannelStateTest.TxFuturePair pop = broadcasts.take();
        pop.future.set(pop.tx);
        Assert.assertEquals(PaymentChannelServerState.State.READY, serverState.getState());
        // Pay a tiny bit
        serverState.incrementPayment(CENT.divide(2).subtract(CENT.divide(10)), clientState.incrementPaymentBy(CENT.divide(10), null).signature.encodeToBitcoin());
        // Advance time until our we get close enough to lock time that server should rebroadcast
        Utils.rollMockClock(((60 * 60) * 22));
        // ... and store server to get it to broadcast payment transaction
        serverState.storeChannelInWallet(null);
        PaymentChannelStateTest.TxFuturePair broadcastPaymentPair = broadcasts.take();
        Exception paymentException = new RuntimeException("I'm sorry, but the network really just doesn't like you");
        broadcastPaymentPair.future.setException(paymentException);
        try {
            serverState.close().get();
        } catch (ExecutionException e) {
            Assert.assertSame(e.getCause(), paymentException);
        }
        Assert.assertEquals(ERROR, serverState.getState());
        // Now advance until client should rebroadcast
        Utils.rollMockClock((((60 * 60) * 2) + (60 * 5)));
        // Now store the client state in a stored state object which handles the rebroadcasting
        clientState.doStoreChannelInWallet(Sha256Hash.of(new byte[]{  }));
        PaymentChannelStateTest.TxFuturePair clientBroadcastedMultiSig = broadcasts.take();
        PaymentChannelStateTest.TxFuturePair broadcastRefund = broadcasts.take();
        Assert.assertEquals(clientBroadcastedMultiSig.tx.getTxId(), multisigContract.getTxId());
        for (TransactionInput input : clientBroadcastedMultiSig.tx.getInputs())
            input.verify();

        clientBroadcastedMultiSig.future.set(clientBroadcastedMultiSig.tx);
        Transaction clientBroadcastedRefund = broadcastRefund.tx;
        Assert.assertEquals(clientBroadcastedRefund.getTxId(), clientState.getRefundTransaction().getTxId());
        for (TransactionInput input : clientBroadcastedRefund.getInputs()) {
            // If the multisig output is connected, the wallet will fail to deserialize
            if (input.getOutpoint().getHash().equals(clientBroadcastedMultiSig.tx.getTxId()))
                Assert.assertNull(input.getConnectedOutput().getSpentBy());

            input.verify(clientBroadcastedMultiSig.tx.getOutput(0));
        }
        broadcastRefund.future.set(clientBroadcastedRefund);
        // Create a block with multisig contract and refund transaction in it and give it to both wallets,
        // making getBalance() include the transactions
        chain.add(FakeTxBuilder.makeSolvedTestBlock(blockStore.getChainHead().getHeader(), multisigContract, clientBroadcastedRefund));
        // Make sure we actually had to pay what initialize() told us we would
        Assert.assertEquals(CENT, wallet.getBalance());
        try {
            // After its expired, we cant still increment payment
            clientState.incrementPaymentBy(CENT, null);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void checkBadData() throws Exception {
        // Check that if signatures/transactions/etc are corrupted, the protocol rejects them correctly.
        // We'll broadcast only one tx: multisig contract
        Utils.setMockClock();// Use mock clock

        final long EXPIRE_TIME = (Utils.currentTimeSeconds()) + ((60 * 60) * 24);
        serverState = makeServerState(mockBroadcaster, serverWallet, serverKey, EXPIRE_TIME);
        Assert.assertEquals(getInitialServerState(), serverState.getState());
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), PaymentChannelStateTest.HALF_COIN, EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        clientState.initiate();
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        if (useRefunds()) {
            // Test refund transaction with any number of issues
            byte[] refundTxBytes = clientV1State().getIncompleteRefundTransaction().bitcoinSerialize();
            Transaction refund = new Transaction(TestWithWallet.UNITTEST, refundTxBytes);
            refund.addOutput(ZERO, LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey()));
            try {
                serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
                Assert.fail();
            } catch (VerificationException e) {
            }
            refund = new Transaction(TestWithWallet.UNITTEST, refundTxBytes);
            refund.addInput(new TransactionInput(TestWithWallet.UNITTEST, refund, new byte[]{  }, new TransactionOutPoint(TestWithWallet.UNITTEST, 42, refund.getTxId())));
            try {
                serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
                Assert.fail();
            } catch (VerificationException e) {
            }
            refund = new Transaction(TestWithWallet.UNITTEST, refundTxBytes);
            refund.setLockTime(0);
            try {
                serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
                Assert.fail();
            } catch (VerificationException e) {
            }
            refund = new Transaction(TestWithWallet.UNITTEST, refundTxBytes);
            refund.getInput(0).setSequenceNumber(NO_SEQUENCE);
            try {
                serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
                Assert.fail();
            } catch (VerificationException e) {
            }
            refund = new Transaction(TestWithWallet.UNITTEST, refundTxBytes);
            byte[] refundSig = serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
            try {
                serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
                Assert.fail();
            } catch (IllegalStateException e) {
            }
            Assert.assertEquals(WAITING_FOR_MULTISIG_CONTRACT, serverState.getState());
            byte[] refundSigCopy = Arrays.copyOf(refundSig, refundSig.length);
            refundSigCopy[((refundSigCopy.length) - 1)] = NONE.byteValue();
            try {
                clientV1State().provideRefundSignature(refundSigCopy, null);
                Assert.fail();
            } catch (VerificationException e) {
                Assert.assertTrue(e.getMessage().contains("SIGHASH_NONE"));
            }
            refundSigCopy = Arrays.copyOf(refundSig, refundSig.length);
            refundSigCopy[3] ^= 66;// Make the signature fail standard checks

            try {
                clientV1State().provideRefundSignature(refundSigCopy, null);
                Assert.fail();
            } catch (VerificationException e) {
                Assert.assertTrue(e.getMessage().contains("not canonical"));
            }
            refundSigCopy = Arrays.copyOf(refundSig, refundSig.length);
            refundSigCopy[10] ^= 66;// Flip some random bits in the signature (to make it invalid, not just nonstandard)

            try {
                clientV1State().provideRefundSignature(refundSigCopy, null);
                Assert.fail();
            } catch (VerificationException e) {
                Assert.assertFalse(e.getMessage().contains("not canonical"));
            }
            refundSigCopy = Arrays.copyOf(refundSig, refundSig.length);
            try {
                clientV1State().getCompletedRefundTransaction();
                Assert.fail();
            } catch (IllegalStateException e) {
            }
            clientV1State().provideRefundSignature(refundSigCopy, null);
            try {
                clientV1State().provideRefundSignature(refundSigCopy, null);
                Assert.fail();
            } catch (IllegalStateException e) {
            }
        }
        Assert.assertEquals(SAVE_STATE_IN_WALLET, clientState.getState());
        clientState.fakeSave();
        Assert.assertEquals(PROVIDE_MULTISIG_CONTRACT_TO_SERVER, clientState.getState());
        if (!(useRefunds())) {
            serverV2State().provideClientKey(myKey.getPubKey());
        }
        try {
            clientState.incrementPaymentBy(SATOSHI, null);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        byte[] multisigContractSerialized = clientState.getContract().bitcoinSerialize();
        Transaction multisigContract = new Transaction(TestWithWallet.UNITTEST, multisigContractSerialized);
        multisigContract.clearOutputs();
        // Swap order of client and server keys to check correct failure
        if ((versionSelector) == (VersionSelector.VERSION_1)) {
            multisigContract.addOutput(PaymentChannelStateTest.HALF_COIN, ScriptBuilder.createMultiSigOutputScript(2, Lists.newArrayList(serverKey, myKey)));
        } else {
            multisigContract.addOutput(PaymentChannelStateTest.HALF_COIN, ScriptBuilder.createP2SHOutputScript(ScriptBuilder.createCLTVPaymentChannelOutput(BigInteger.valueOf(serverState.getExpiryTime()), serverKey, myKey)));
        }
        try {
            serverState.provideContract(multisigContract);
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertTrue(e.getMessage().contains("client and server in that order"));
        }
        multisigContract = new Transaction(TestWithWallet.UNITTEST, multisigContractSerialized);
        multisigContract.clearOutputs();
        if ((versionSelector) == (VersionSelector.VERSION_1)) {
            multisigContract.addOutput(ZERO, ScriptBuilder.createMultiSigOutputScript(2, Lists.newArrayList(myKey, serverKey)));
        } else {
            multisigContract.addOutput(ZERO, ScriptBuilder.createP2SHOutputScript(ScriptBuilder.createCLTVPaymentChannelOutput(BigInteger.valueOf(serverState.getExpiryTime()), myKey, serverKey)));
        }
        try {
            serverState.provideContract(multisigContract);
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertTrue(e.getMessage().contains("zero value"));
        }
        multisigContract = new Transaction(TestWithWallet.UNITTEST, multisigContractSerialized);
        multisigContract.clearOutputs();
        multisigContract.addOutput(new TransactionOutput(TestWithWallet.UNITTEST, multisigContract, PaymentChannelStateTest.HALF_COIN, new byte[]{ 1 }));
        try {
            serverState.provideContract(multisigContract);
            Assert.fail();
        } catch (VerificationException e) {
        }
        multisigContract = new Transaction(TestWithWallet.UNITTEST, multisigContractSerialized);
        ListenableFuture<PaymentChannelServerState> multisigStateFuture = serverState.provideContract(multisigContract);
        try {
            serverState.provideContract(multisigContract);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        Assert.assertEquals(WAITING_FOR_MULTISIG_ACCEPTANCE, serverState.getState());
        Assert.assertFalse(multisigStateFuture.isDone());
        final PaymentChannelStateTest.TxFuturePair pair = broadcasts.take();
        pair.future.set(pair.tx);
        Assert.assertEquals(multisigStateFuture.get(), serverState);
        Assert.assertEquals(PaymentChannelServerState.State.READY, serverState.getState());
        // Both client and server are now in the ready state. Simulate a few micropayments of 0.005 bitcoins.
        Coin.Coin size = PaymentChannelStateTest.HALF_COIN.divide(100);
        Coin.Coin totalPayment = ZERO;
        try {
            clientState.incrementPaymentBy(COIN, null);
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        byte[] signature = clientState.incrementPaymentBy(size, null).signature.encodeToBitcoin();
        totalPayment = totalPayment.add(size);
        byte[] signatureCopy = Arrays.copyOf(signature, signature.length);
        signatureCopy[((signatureCopy.length) - 1)] = ANYONECANPAY_NONE.byteValue();
        try {
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signatureCopy);
            Assert.fail();
        } catch (VerificationException e) {
        }
        signatureCopy = Arrays.copyOf(signature, signature.length);
        signatureCopy[2] ^= 66;// Make the signature fail standard checks

        try {
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signatureCopy);
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertTrue(e.getMessage().contains("not canonical"));
        }
        signatureCopy = Arrays.copyOf(signature, signature.length);
        signatureCopy[10] ^= 66;// Flip some random bits in the signature (to make it invalid, not just nonstandard)

        try {
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signatureCopy);
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertFalse(e.getMessage().contains("not canonical"));
        }
        serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signature);
        // Pay the rest (signed with SIGHASH_NONE|SIGHASH_ANYONECANPAY)
        byte[] signature2 = clientState.incrementPaymentBy(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), null).signature.encodeToBitcoin();
        totalPayment = totalPayment.add(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment));
        Assert.assertEquals(totalPayment, PaymentChannelStateTest.HALF_COIN);
        signatureCopy = Arrays.copyOf(signature, signature.length);
        signatureCopy[((signatureCopy.length) - 1)] = ANYONECANPAY_SINGLE.byteValue();
        try {
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signatureCopy);
            Assert.fail();
        } catch (VerificationException e) {
        }
        serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signature2);
        // Trying to take reduce the refund size fails.
        try {
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment.subtract(size)), signature);
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        Assert.assertEquals(serverState.getBestValueToMe(), totalPayment);
        try {
            clientState.incrementPaymentBy(SATOSHI.negate(), null);
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        try {
            clientState.incrementPaymentBy(PaymentChannelStateTest.HALF_COIN.subtract(size).add(SATOSHI), null);
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
    }

    @Test
    public void feesTest() throws Exception {
        // Test that transactions are getting the necessary fees
        Context.propagate(new Context(TestWithWallet.UNITTEST, 100, ZERO, true));
        // Spend the client wallet's one coin
        final SendRequest request = SendRequest.to(LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey()), COIN);
        request.ensureMinRequiredFee = false;
        wallet.sendCoinsOffline(request);
        Assert.assertEquals(ZERO, wallet.getBalance());
        chain.add(FakeTxBuilder.makeSolvedTestBlock(blockStore.getChainHead().getHeader(), FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT.add(REFERENCE_DEFAULT_MIN_TX_FEE), myAddress)));
        Assert.assertEquals(CENT.add(REFERENCE_DEFAULT_MIN_TX_FEE), wallet.getBalance());
        Utils.setMockClock();// Use mock clock

        final long EXPIRE_TIME = ((Utils.currentTimeMillis()) / 1000) + ((60 * 60) * 24);
        serverState = makeServerState(mockBroadcaster, serverWallet, serverKey, EXPIRE_TIME);
        Assert.assertEquals(getInitialServerState(), serverState.getState());
        // Clearly SATOSHI is far too small to be useful
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), SATOSHI, EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        try {
            clientState.initiate();
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), MIN_NONDUST_OUTPUT.subtract(SATOSHI).add(REFERENCE_DEFAULT_MIN_TX_FEE), EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        try {
            clientState.initiate();
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        // Verify that MIN_NONDUST_OUTPUT + MIN_TX_FEE is accepted
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), MIN_NONDUST_OUTPUT.add(REFERENCE_DEFAULT_MIN_TX_FEE), EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        // We'll have to pay REFERENCE_DEFAULT_MIN_TX_FEE twice (multisig+refund), and we'll end up getting back nearly nothing...
        clientState.initiate();
        // Hardcoded tx length because actual length may vary depending on actual signature length
        // The value is close to clientState.getContractInternal().unsafeBitcoinSerialize().length;
        int contractSize = ((versionSelector) == (VersionSelector.VERSION_1)) ? 273 : 225;
        Coin.Coin expectedFees = REFERENCE_DEFAULT_MIN_TX_FEE.multiply(contractSize).divide(1000).add(REFERENCE_DEFAULT_MIN_TX_FEE);
        Assert.assertEquals(expectedFees, clientState.getRefundTxFees());
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        // Now actually use a more useful CENT
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), CENT, EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        clientState.initiate();
        Assert.assertEquals(expectedFees, clientState.getRefundTxFees());
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        if (useRefunds()) {
            // Send the refund tx from client to server and get back the signature.
            Transaction refund = new Transaction(TestWithWallet.UNITTEST, clientV1State().getIncompleteRefundTransaction().bitcoinSerialize());
            byte[] refundSig = serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
            Assert.assertEquals(WAITING_FOR_MULTISIG_CONTRACT, serverState.getState());
            // This verifies that the refund can spend the multi-sig output when run.
            clientV1State().provideRefundSignature(refundSig, null);
        }
        Assert.assertEquals(SAVE_STATE_IN_WALLET, clientState.getState());
        clientState.fakeSave();
        Assert.assertEquals(PROVIDE_MULTISIG_CONTRACT_TO_SERVER, clientState.getState());
        // Get the multisig contract
        Transaction multisigContract = new Transaction(TestWithWallet.UNITTEST, clientState.getContract().bitcoinSerialize());
        Assert.assertEquals(READY, clientState.getState());
        // Provide the server with the multisig contract and simulate successful propagation/acceptance.
        if (!(useRefunds())) {
            serverV2State().provideClientKey(clientState.myKey.getPubKey());
        }
        serverState.provideContract(multisigContract);
        Assert.assertEquals(WAITING_FOR_MULTISIG_ACCEPTANCE, serverState.getState());
        PaymentChannelStateTest.TxFuturePair pair = broadcasts.take();
        pair.future.set(pair.tx);
        Assert.assertEquals(PaymentChannelServerState.State.READY, serverState.getState());
        // Both client and server are now in the ready state. Simulate a few micropayments
        Coin.Coin totalPayment = ZERO;
        // We can send as little as we want - its up to the server to get the fees right
        byte[] signature = clientState.incrementPaymentBy(SATOSHI, null).signature.encodeToBitcoin();
        totalPayment = totalPayment.add(SATOSHI);
        serverState.incrementPayment(CENT.subtract(totalPayment), signature);
        // We can't refund more than the contract is worth...
        try {
            serverState.incrementPayment(CENT.add(SATOSHI), signature);
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        // We cannot send just under the total value - our refund would make it unspendable. So the client
        // will correct it for us to be larger than the requested amount, to make the change output zero.
        PaymentChannelClientState.IncrementedPayment payment = clientState.incrementPaymentBy(CENT.subtract(MIN_NONDUST_OUTPUT), null);
        Assert.assertEquals(CENT.subtract(SATOSHI), payment.amount);
        totalPayment = totalPayment.add(payment.amount);
        // The server also won't accept it if we do that.
        try {
            serverState.incrementPayment(MIN_NONDUST_OUTPUT.subtract(SATOSHI), signature);
            Assert.fail();
        } catch (ValueOutOfRangeException e) {
        }
        serverState.incrementPayment(CENT.subtract(totalPayment), payment.signature.encodeToBitcoin());
        // And settle the channel.
        serverState.close();
        Assert.assertEquals(CLOSING, serverState.getState());
        pair = broadcasts.take();// settle

        pair.future.set(pair.tx);
        Assert.assertEquals(CLOSED, serverState.getState());
        serverState.close();
        Assert.assertEquals(CLOSED, serverState.getState());
    }

    @Test
    public void serverAddsFeeTest() throws Exception {
        // Test that the server properly adds the necessary fee at the end (or just drops the payment if its not worth it)
        Context.propagate(new Context(TestWithWallet.UNITTEST, 100, ZERO, true));
        Utils.setMockClock();// Use mock clock

        final long EXPIRE_TIME = ((Utils.currentTimeMillis()) / 1000) + ((60 * 60) * 24);
        serverState = makeServerState(mockBroadcaster, serverWallet, serverKey, EXPIRE_TIME);
        Assert.assertEquals(getInitialServerState(), serverState.getState());
        switch (versionSelector) {
            case VERSION_1 :
                clientState = new PaymentChannelV1ClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), CENT, EXPIRE_TIME);
                break;
            case VERSION_2_ALLOW_1 :
            case VERSION_2 :
                clientState = new PaymentChannelV2ClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), CENT, EXPIRE_TIME);
                break;
        }
        Assert.assertEquals(NEW, clientState.getState());
        clientState.initiate(null, new PaymentChannelClient.DefaultClientChannelProperties() {
            @Override
            public SendRequest modifyContractSendRequest(SendRequest sendRequest) {
                sendRequest.coinSelector = wallet.getCoinSelector();
                return sendRequest;
            }
        });
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        if (useRefunds()) {
            // Send the refund tx from client to server and get back the signature.
            Transaction refund = new Transaction(TestWithWallet.UNITTEST, clientV1State().getIncompleteRefundTransaction().bitcoinSerialize());
            byte[] refundSig = serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
            Assert.assertEquals(WAITING_FOR_MULTISIG_CONTRACT, serverState.getState());
            // This verifies that the refund can spend the multi-sig output when run.
            clientV1State().provideRefundSignature(refundSig, null);
        }
        Assert.assertEquals(SAVE_STATE_IN_WALLET, clientState.getState());
        clientState.fakeSave();
        Assert.assertEquals(PROVIDE_MULTISIG_CONTRACT_TO_SERVER, clientState.getState());
        // Validate the multisig contract looks right.
        Transaction multisigContract = new Transaction(TestWithWallet.UNITTEST, clientState.getContract().bitcoinSerialize());
        Assert.assertEquals(PaymentChannelV1ClientState.State.READY, clientState.getState());
        Assert.assertEquals(2, multisigContract.getOutputs().size());// One multi-sig, one change.

        Script script = multisigContract.getOutput(0).getScriptPubKey();
        if ((versionSelector) == (VersionSelector.VERSION_1)) {
            Assert.assertTrue(ScriptPattern.isSentToMultisig(script));
        } else {
            Assert.assertTrue(ScriptPattern.isP2SH(script));
        }
        script = multisigContract.getOutput(1).getScriptPubKey();
        Assert.assertTrue(ScriptPattern.isP2PKH(script));
        Assert.assertTrue(wallet.getPendingTransactions().contains(multisigContract));
        // Provide the server with the multisig contract and simulate successful propagation/acceptance.
        if (!(useRefunds())) {
            serverV2State().provideClientKey(clientState.myKey.getPubKey());
        }
        serverState.provideContract(multisigContract);
        Assert.assertEquals(WAITING_FOR_MULTISIG_ACCEPTANCE, serverState.getState());
        PaymentChannelStateTest.TxFuturePair pair = broadcasts.take();
        pair.future.set(pair.tx);
        Assert.assertEquals(PaymentChannelServerState.State.READY, serverState.getState());
        int expectedSize = ((versionSelector) == (VersionSelector.VERSION_1)) ? 271 : 355;
        Coin.Coin expectedFee = REFERENCE_DEFAULT_MIN_TX_FEE.multiply(expectedSize).divide(1000);
        // Both client and server are now in the ready state, split the channel in half
        byte[] signature = clientState.incrementPaymentBy(expectedFee.subtract(SATOSHI), null).signature.encodeToBitcoin();
        Coin.Coin totalRefund = CENT.subtract(expectedFee.subtract(SATOSHI));
        serverState.incrementPayment(totalRefund, signature);
        // We need to pay MIN_TX_FEE, but we only have MIN_NONDUST_OUTPUT
        try {
            serverState.close();
            Assert.fail();
        } catch (InsufficientMoneyException e) {
            Assert.assertTrue(e.getMessage().contains("Insufficient money,  missing "));
        }
        // Now give the server enough coins to pay the fee
        sendMoneyToWallet(serverWallet, BEST_CHAIN, COIN, LegacyAddress.fromKey(TestWithWallet.UNITTEST, serverKey));
        // The contract is still not worth redeeming - its worth less than we pay in fee
        try {
            serverState.close();
            Assert.fail();
        } catch (InsufficientMoneyException e) {
            Assert.assertTrue(e.getMessage().contains("more in fees"));
        }
        signature = clientState.incrementPaymentBy(SATOSHI.multiply(20), null).signature.encodeToBitcoin();
        totalRefund = totalRefund.subtract(SATOSHI.multiply(20));
        serverState.incrementPayment(totalRefund, signature);
        // And settle the channel.
        serverState.close();
        Assert.assertEquals(CLOSING, serverState.getState());
        pair = broadcasts.take();
        pair.future.set(pair.tx);
        Assert.assertEquals(CLOSED, serverState.getState());
    }

    @Test
    public void doubleSpendContractTest() throws Exception {
        // Tests that if the client double-spends the multisig contract after it is sent, no more payments are accepted
        // Start with a copy of basic()....
        Utils.setMockClock();// Use mock clock

        final long EXPIRE_TIME = (Utils.currentTimeSeconds()) + ((60 * 60) * 24);
        serverState = makeServerState(mockBroadcaster, serverWallet, serverKey, EXPIRE_TIME);
        Assert.assertEquals(getInitialServerState(), serverState.getState());
        clientState = makeClientState(wallet, myKey, ECKey.fromPublicOnly(serverKey.getPubKey()), PaymentChannelStateTest.HALF_COIN, EXPIRE_TIME);
        Assert.assertEquals(NEW, clientState.getState());
        clientState.initiate();
        Assert.assertEquals(getInitialClientState(), clientState.getState());
        Transaction refund;
        if (useRefunds()) {
            refund = new Transaction(TestWithWallet.UNITTEST, clientV1State().getIncompleteRefundTransaction().bitcoinSerialize());
            // Send the refund tx from client to server and get back the signature.
            byte[] refundSig = serverV1State().provideRefundTransaction(refund, myKey.getPubKey());
            Assert.assertEquals(PaymentChannelV1ServerState.State.WAITING_FOR_MULTISIG_CONTRACT, serverState.getState());
            // This verifies that the refund can spend the multi-sig output when run.
            clientV1State().provideRefundSignature(refundSig, null);
        } else {
            refund = clientV2State().getRefundTransaction();
        }
        Assert.assertEquals(SAVE_STATE_IN_WALLET, clientState.getState());
        clientState.fakeSave();
        Assert.assertEquals(PROVIDE_MULTISIG_CONTRACT_TO_SERVER, clientState.getState());
        // Validate the multisig contract looks right.
        Transaction multisigContract = new Transaction(TestWithWallet.UNITTEST, clientState.getContract().bitcoinSerialize());
        Assert.assertEquals(READY, clientState.getState());
        Assert.assertEquals(2, multisigContract.getOutputs().size());// One multi-sig, one change.

        Script script = multisigContract.getOutput(0).getScriptPubKey();
        if ((versionSelector) == (VersionSelector.VERSION_1)) {
            Assert.assertTrue(ScriptPattern.isSentToMultisig(script));
        } else {
            Assert.assertTrue(ScriptPattern.isP2SH(script));
        }
        script = multisigContract.getOutput(1).getScriptPubKey();
        Assert.assertTrue(ScriptPattern.isP2PKH(script));
        Assert.assertTrue(wallet.getPendingTransactions().contains(multisigContract));
        // Provide the server with the multisig contract and simulate successful propagation/acceptance.
        if (!(useRefunds())) {
            serverV2State().provideClientKey(clientState.myKey.getPubKey());
        }
        serverState.provideContract(multisigContract);
        Assert.assertEquals(WAITING_FOR_MULTISIG_ACCEPTANCE, serverState.getState());
        final PaymentChannelStateTest.TxFuturePair pair = broadcasts.take();
        pair.future.set(pair.tx);
        Assert.assertEquals(PaymentChannelServerState.State.READY, serverState.getState());
        // Make sure the refund transaction is not in the wallet and multisig contract's output is not connected to it
        Assert.assertEquals(2, wallet.getTransactions(false).size());
        Iterator<Transaction> walletTransactionIterator = wallet.getTransactions(false).iterator();
        Transaction clientWalletMultisigContract = walletTransactionIterator.next();
        Assert.assertFalse(clientWalletMultisigContract.getTxId().equals(clientState.getRefundTransaction().getTxId()));
        if (!(clientWalletMultisigContract.getTxId().equals(multisigContract.getTxId()))) {
            clientWalletMultisigContract = walletTransactionIterator.next();
            Assert.assertFalse(clientWalletMultisigContract.getTxId().equals(clientState.getRefundTransaction().getTxId()));
        } else
            Assert.assertFalse(walletTransactionIterator.next().getTxId().equals(clientState.getRefundTransaction().getTxId()));

        Assert.assertEquals(multisigContract.getTxId(), clientWalletMultisigContract.getTxId());
        Assert.assertFalse(clientWalletMultisigContract.getInput(0).getConnectedOutput().getSpentBy().getParentTransaction().getTxId().equals(refund.getTxId()));
        // Both client and server are now in the ready state. Simulate a few micropayments of 0.005 bitcoins.
        Coin.Coin size = PaymentChannelStateTest.HALF_COIN.divide(100);
        Coin.Coin totalPayment = ZERO;
        for (int i = 0; i < 5; i++) {
            byte[] signature = clientState.incrementPaymentBy(size, null).signature.encodeToBitcoin();
            totalPayment = totalPayment.add(size);
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signature);
        }
        // Now create a double-spend and send it to the server
        Transaction doubleSpendContract = new Transaction(TestWithWallet.UNITTEST);
        doubleSpendContract.addInput(new TransactionInput(TestWithWallet.UNITTEST, doubleSpendContract, new byte[0], multisigContract.getInput(0).getOutpoint()));
        doubleSpendContract.addOutput(PaymentChannelStateTest.HALF_COIN, myKey);
        doubleSpendContract = new Transaction(TestWithWallet.UNITTEST, doubleSpendContract.bitcoinSerialize());
        StoredBlock block = new StoredBlock(TestWithWallet.UNITTEST.getGenesisBlock().createNextBlock(LegacyAddress.fromKey(TestWithWallet.UNITTEST, myKey)), BigInteger.TEN, 1);
        serverWallet.receiveFromBlock(doubleSpendContract, block, BEST_CHAIN, 0);
        // Now if we try to spend again the server will reject it since it saw a double-spend
        try {
            byte[] signature = clientState.incrementPaymentBy(size, null).signature.encodeToBitcoin();
            totalPayment = totalPayment.add(size);
            serverState.incrementPayment(PaymentChannelStateTest.HALF_COIN.subtract(totalPayment), signature);
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertTrue(e.getMessage().contains("double-spent"));
        }
    }
}

