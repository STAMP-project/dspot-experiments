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
import ByteString.EMPTY;
import CloseReason.CLIENT_REQUESTED_CLOSE;
import CloseReason.NO_ACCEPTABLE_VERSION;
import CloseReason.REMOTE_SENT_ERROR;
import CloseReason.REMOTE_SENT_INVALID_MESSAGE;
import CloseReason.SERVER_REQUESTED_CLOSE;
import CloseReason.SERVER_REQUESTED_TOO_MUCH_VALUE;
import CloseReason.TIME_WINDOW_UNACCEPTABLE;
import Coin.SATOSHI;
import Coin.ZERO;
import IPaymentChannelClient.ClientChannelProperties;
import MessageType.CHANNEL_OPEN;
import MessageType.CLIENT_VERSION;
import MessageType.CLOSE;
import MessageType.ERROR;
import MessageType.INITIATE;
import MessageType.PAYMENT_ACK;
import MessageType.PROVIDE_CONTRACT;
import MessageType.PROVIDE_REFUND;
import MessageType.RETURN_REFUND;
import MessageType.SERVER_VERSION;
import MessageType.UPDATE_PAYMENT;
import Protos.ClientVersion;
import Protos.Error;
import Protos.Error.ErrorCode.BAD_TRANSACTION;
import Protos.Error.ErrorCode.CHANNEL_VALUE_TOO_LARGE;
import Protos.Error.ErrorCode.SYNTAX_ERROR;
import Protos.Error.ErrorCode.TIMEOUT;
import Protos.Initiate;
import Protos.PaymentAck;
import Protos.ProvideRefund;
import Protos.ServerVersion;
import Protos.TwoWayChannelMessage;
import Protos.TwoWayChannelMessage.Builder;
import StoredPaymentChannelClientStates.EXTENSION_ID;
import Transaction.MIN_NONDUST_OUTPUT.value;
import Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
import TransactionConfidence.Source.SELF;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bitcoin.paymentchannel.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.script.ScriptPattern;
import org.bitcoinj.testing.TestWithWallet;
import org.bitcoinj.wallet.Wallet;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Sha256Hash.ZERO_HASH;


@RunWith(Parameterized.class)
public class ChannelConnectionTest extends TestWithWallet {
    private static final int CLIENT_MAJOR_VERSION = 1;

    private Wallet serverWallet;

    private AtomicBoolean fail;

    private BlockingQueue<Transaction> broadcasts;

    private TransactionBroadcaster mockBroadcaster;

    private Semaphore broadcastTxPause;

    private static final TransactionBroadcaster failBroadcaster = new TransactionBroadcaster() {
        @Override
        public TransactionBroadcast broadcastTransaction(Transaction tx) {
            Assert.fail();
            return null;
        }
    };

    @Parameterized.Parameter
    public ClientChannelProperties clientChannelProperties;

    @Test
    public void testSimpleChannel() throws Exception {
        exectuteSimpleChannelTest(null);
    }

    @Test
    public void testEncryptedClientWallet() throws Exception {
        // Encrypt the client wallet
        String mySecretPw = "MySecret";
        wallet.encrypt(mySecretPw);
        KeyParameter userKeySetup = wallet.getKeyCrypter().deriveKey(mySecretPw);
        exectuteSimpleChannelTest(userKeySetup);
    }

    @Test
    public void testServerErrorHandling_badTransaction() throws Exception {
        if (!(useRefunds())) {
            // This test only applies to versions with refunds
            return;
        }
        // Gives the server crap and checks proper error responses are sent.
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        PaymentChannelServer server = pair.server;
        server.connectionOpen();
        client.connectionOpen();
        // Make sure we get back a BAD_TRANSACTION if we send a bogus refund transaction.
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(INITIATE));
        Protos.TwoWayChannelMessage msg = pair.clientRecorder.checkNextMsg(PROVIDE_REFUND);
        server.receiveMessage(TwoWayChannelMessage.newBuilder().setType(PROVIDE_REFUND).setProvideRefund(ProvideRefund.newBuilder(msg.getProvideRefund()).setMultisigKey(EMPTY).setTx(EMPTY)).build());
        final Protos.TwoWayChannelMessage errorMsg = pair.serverRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(BAD_TRANSACTION, errorMsg.getError().getCode());
    }

    @Test
    public void testServerErrorHandling_killSocketOnClose() throws Exception {
        // Make sure the server closes the socket on CLOSE
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        PaymentChannelServer server = pair.server;
        server.connectionOpen();
        client.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.settle();
        client.receiveMessage(pair.serverRecorder.checkNextMsg(INITIATE));
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLOSE));
        Assert.assertEquals(CLIENT_REQUESTED_CLOSE, pair.serverRecorder.q.take());
    }

    @Test
    public void testServerErrorHandling_killSocketOnError() throws Exception {
        // Make sure the server closes the socket on ERROR
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        PaymentChannelServer server = pair.server;
        server.connectionOpen();
        client.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(INITIATE));
        server.receiveMessage(TwoWayChannelMessage.newBuilder().setType(ERROR).setError(Error.newBuilder().setCode(TIMEOUT)).build());
        Assert.assertEquals(REMOTE_SENT_ERROR, pair.serverRecorder.q.take());
    }

    @Test
    public void testClientErrorHandlingIncreasePaymentError() throws Exception {
        // Tests various aspects of channel resuming.
        Utils.setMockClock();
        final Sha256Hash someServerId = Sha256Hash.of(new byte[]{  });
        // Open up a normal channel.
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        pair.server.connectionOpen();
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        PaymentChannelServer server = pair.server;
        client.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        final Protos.TwoWayChannelMessage initiateMsg = pair.serverRecorder.checkNextMsg(INITIATE);
        Coin.Coin minPayment = Coin.Coin.valueOf(initiateMsg.getInitiate().getMinPayment());
        client.receiveMessage(initiateMsg);
        if (useRefunds()) {
            server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_REFUND));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(RETURN_REFUND));
        }
        broadcastTxPause.release();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_CONTRACT));
        broadcasts.take();
        pair.serverRecorder.checkTotalPayment(REFERENCE_DEFAULT_MIN_TX_FEE);
        client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
        Sha256Hash contractHash = ((Sha256Hash) (pair.serverRecorder.q.take()));
        pair.clientRecorder.checkInitiated();
        Assert.assertNull(pair.serverRecorder.q.poll());
        Assert.assertNull(pair.clientRecorder.q.poll());
        Assert.assertEquals(minPayment, client.state().getValueSpent());
        // Send a bitcent.
        Coin.Coin amount = minPayment.add(CENT);
        ListenableFuture<PaymentIncrementAck> ackFuture = client.incrementPayment(CENT);
        // We never pass this message to the server
        // Instead we pretend the server didn't like our increase
        client.receiveMessage(// some random error
        TwoWayChannelMessage.newBuilder().setType(ERROR).setError(Error.newBuilder().setCode(CHANNEL_VALUE_TOO_LARGE)).build());
        // Now we need the client to actually close the future and report this error
        try {
            ackFuture.get(1L, TimeUnit.SECONDS);
            Assert.fail("This should not work");
        } catch (ExecutionException ee) {
            PaymentChannelCloseException ce = ((PaymentChannelCloseException) (ee.getCause()));
            Assert.assertEquals(REMOTE_SENT_ERROR, ce.getCloseReason());
        } catch (TimeoutException e) {
            Assert.fail("Should not time out");
        }
    }

    @Test
    public void testChannelResume() throws Exception {
        // Tests various aspects of channel resuming.
        Utils.setMockClock();
        final Sha256Hash someServerId = Sha256Hash.of(new byte[]{  });
        // Open up a normal channel.
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        pair.server.connectionOpen();
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        PaymentChannelServer server = pair.server;
        client.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        final Protos.TwoWayChannelMessage initiateMsg = pair.serverRecorder.checkNextMsg(INITIATE);
        Coin.Coin minPayment = Coin.Coin.valueOf(initiateMsg.getInitiate().getMinPayment());
        client.receiveMessage(initiateMsg);
        if (useRefunds()) {
            server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_REFUND));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(RETURN_REFUND));
        }
        broadcastTxPause.release();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_CONTRACT));
        broadcasts.take();
        pair.serverRecorder.checkTotalPayment(REFERENCE_DEFAULT_MIN_TX_FEE);
        client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
        Sha256Hash contractHash = ((Sha256Hash) (pair.serverRecorder.q.take()));
        pair.clientRecorder.checkInitiated();
        Assert.assertNull(pair.serverRecorder.q.poll());
        Assert.assertNull(pair.clientRecorder.q.poll());
        Assert.assertEquals(minPayment, client.state().getValueSpent());
        // Send a bitcent.
        Coin.Coin amount = minPayment.add(CENT);
        client.incrementPayment(CENT);
        server.receiveMessage(pair.clientRecorder.checkNextMsg(UPDATE_PAYMENT));
        Assert.assertEquals(amount, ((ChannelTestUtils.UpdatePair) (pair.serverRecorder.q.take())).amount);
        server.close();
        server.connectionClosed();
        client.receiveMessage(pair.serverRecorder.checkNextMsg(PAYMENT_ACK));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(CLOSE));
        client.connectionClosed();
        Assert.assertFalse(client.connectionOpen);
        // There is now an inactive open channel worth COIN-CENT + minPayment with id Sha256.create(new byte[] {})
        StoredPaymentChannelClientStates clientStoredChannels = ((StoredPaymentChannelClientStates) (wallet.getExtensions().get(EXTENSION_ID)));
        Assert.assertEquals(1, clientStoredChannels.mapChannels.size());
        Assert.assertFalse(clientStoredChannels.mapChannels.values().iterator().next().active);
        // Check that server-side won't attempt to reopen a nonexistent channel (it will tell the client to re-initiate
        // instead).
        pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        pair.server.connectionOpen();
        pair.server.receiveMessage(TwoWayChannelMessage.newBuilder().setType(CLIENT_VERSION).setClientVersion(ClientVersion.newBuilder().setPreviousChannelContractHash(ByteString.copyFrom(Sha256Hash.hash(new byte[]{ 3 }))).setMajor(ChannelConnectionTest.CLIENT_MAJOR_VERSION).setMinor(42)).build());
        pair.serverRecorder.checkNextMsg(SERVER_VERSION);
        pair.serverRecorder.checkNextMsg(INITIATE);
        // Now reopen/resume the channel after round-tripping the wallets.
        wallet = ChannelConnectionTest.roundTripClientWallet(wallet);
        serverWallet = ChannelConnectionTest.roundTripServerWallet(serverWallet);
        clientStoredChannels = ((StoredPaymentChannelClientStates) (wallet.getExtensions().get(EXTENSION_ID)));
        pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        server = pair.server;
        client.connectionOpen();
        server.connectionOpen();
        // Check the contract hash is sent on the wire correctly.
        final Protos.TwoWayChannelMessage clientVersionMsg = pair.clientRecorder.checkNextMsg(CLIENT_VERSION);
        Assert.assertTrue(clientVersionMsg.getClientVersion().hasPreviousChannelContractHash());
        Assert.assertEquals(contractHash, Sha256Hash.wrap(clientVersionMsg.getClientVersion().getPreviousChannelContractHash().toByteArray()));
        server.receiveMessage(clientVersionMsg);
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
        Assert.assertEquals(contractHash, pair.serverRecorder.q.take());
        pair.clientRecorder.checkOpened();
        Assert.assertNull(pair.serverRecorder.q.poll());
        Assert.assertNull(pair.clientRecorder.q.poll());
        // Send another bitcent and check 2 were received in total.
        client.incrementPayment(CENT);
        amount = amount.add(CENT);
        server.receiveMessage(pair.clientRecorder.checkNextMsg(UPDATE_PAYMENT));
        pair.serverRecorder.checkTotalPayment(amount);
        client.receiveMessage(pair.serverRecorder.checkNextMsg(PAYMENT_ACK));
        PaymentChannelClient openClient = client;
        ChannelTestUtils.RecordingPair openPair = pair;
        // Now open up a new client with the same id and make sure the server disconnects the previous client.
        pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        server = pair.server;
        client.connectionOpen();
        server.connectionOpen();
        // Check that no prev contract hash is sent on the wire the client notices it's already in use by another
        // client attached to the same wallet and refuses to resume.
        {
            Protos.TwoWayChannelMessage msg = pair.clientRecorder.checkNextMsg(CLIENT_VERSION);
            Assert.assertFalse(msg.getClientVersion().hasPreviousChannelContractHash());
        }
        // Make sure the server allows two simultaneous opens. It will close the first and allow resumption of the second.
        pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        server = pair.server;
        client.connectionOpen();
        server.connectionOpen();
        // Swap out the clients version message for a custom one that tries to resume ...
        pair.clientRecorder.getNextMsg();
        server.receiveMessage(TwoWayChannelMessage.newBuilder().setType(CLIENT_VERSION).setClientVersion(ClientVersion.newBuilder().setPreviousChannelContractHash(ByteString.copyFrom(contractHash.getBytes())).setMajor(ChannelConnectionTest.CLIENT_MAJOR_VERSION).setMinor(42)).build());
        // We get the usual resume sequence.
        pair.serverRecorder.checkNextMsg(SERVER_VERSION);
        pair.serverRecorder.checkNextMsg(CHANNEL_OPEN);
        // Verify the previous one was closed.
        openPair.serverRecorder.checkNextMsg(CLOSE);
        Assert.assertTrue(clientStoredChannels.getChannel(someServerId, contractHash).active);
        // And finally close the first channel too.
        openClient.connectionClosed();
        Assert.assertFalse(clientStoredChannels.getChannel(someServerId, contractHash).active);
        // Now roll the mock clock and recreate the client object so that it removes the channels and announces refunds.
        Assert.assertEquals(86640, clientStoredChannels.getSecondsUntilExpiry(someServerId));
        Utils.rollMockClock((((60 * 60) * 24) + (60 * 5)));// Client announces refund 5 minutes after expire time

        StoredPaymentChannelClientStates newClientStates = new StoredPaymentChannelClientStates(wallet, mockBroadcaster);
        newClientStates.deserializeWalletExtension(wallet, clientStoredChannels.serializeWalletExtension());
        broadcastTxPause.release();
        if (isMultiSigContract()) {
            Assert.assertTrue(ScriptPattern.isSentToMultisig(broadcasts.take().getOutput(0).getScriptPubKey()));
        } else {
            Assert.assertTrue(ScriptPattern.isP2SH(broadcasts.take().getOutput(0).getScriptPubKey()));
        }
        broadcastTxPause.release();
        Assert.assertEquals(SELF, broadcasts.take().getConfidence().getSource());
        Assert.assertTrue(broadcasts.isEmpty());
        Assert.assertTrue(newClientStates.mapChannels.isEmpty());
        // Server also knows it's too late.
        StoredPaymentChannelServerStates serverStoredChannels = new StoredPaymentChannelServerStates(serverWallet, mockBroadcaster);
        Thread.sleep(2000);// TODO: Fix this stupid hack.

        Assert.assertTrue(serverStoredChannels.mapChannels.isEmpty());
    }

    @Test
    public void testBadResumeHash() throws InterruptedException {
        // Check that server-side will reject incorrectly formatted hashes. If anything goes wrong with session resume,
        // then the server will start the opening of a new channel automatically, so we expect to see INITIATE here.
        ChannelTestUtils.RecordingPair srv = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        srv.server.connectionOpen();
        srv.server.receiveMessage(TwoWayChannelMessage.newBuilder().setType(CLIENT_VERSION).setClientVersion(ClientVersion.newBuilder().setPreviousChannelContractHash(ByteString.copyFrom(new byte[]{ 0, 1 })).setMajor(ChannelConnectionTest.CLIENT_MAJOR_VERSION).setMinor(42)).build());
        srv.serverRecorder.checkNextMsg(SERVER_VERSION);
        srv.serverRecorder.checkNextMsg(INITIATE);
        Assert.assertTrue(srv.serverRecorder.q.isEmpty());
    }

    @Test
    public void testClientUnknownVersion() throws Exception {
        // Tests client rejects unknown version
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        pair.clientRecorder.checkNextMsg(CLIENT_VERSION);
        client.receiveMessage(TwoWayChannelMessage.newBuilder().setServerVersion(ServerVersion.newBuilder().setMajor((-1))).setType(SERVER_VERSION).build());
        pair.clientRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(NO_ACCEPTABLE_VERSION, pair.clientRecorder.q.take());
        // Double-check that we cant do anything that requires an open channel
        try {
            client.incrementPayment(SATOSHI);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testClientTimeWindowUnacceptable() throws Exception {
        // Tests that clients reject too large time windows
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster, 100);
        PaymentChannelServer server = pair.server;
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        server.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(TwoWayChannelMessage.newBuilder().setInitiate(Initiate.newBuilder().setExpireTimeSecs(((Utils.currentTimeSeconds()) + ((60 * 60) * 48))).setMinAcceptedChannelSize(100).setMultisigKey(ByteString.copyFrom(new ECKey().getPubKey())).setMinPayment(value)).setType(INITIATE).build());
        pair.clientRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(TIME_WINDOW_UNACCEPTABLE, pair.clientRecorder.q.take());
        // Double-check that we cant do anything that requires an open channel
        try {
            client.incrementPayment(SATOSHI);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testValuesAreRespected() throws Exception {
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelServer server = pair.server;
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        server.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(TwoWayChannelMessage.newBuilder().setInitiate(Initiate.newBuilder().setExpireTimeSecs(Utils.currentTimeSeconds()).setMinAcceptedChannelSize(COIN.add(SATOSHI).value).setMultisigKey(ByteString.copyFrom(new ECKey().getPubKey())).setMinPayment(value)).setType(INITIATE).build());
        pair.clientRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(SERVER_REQUESTED_TOO_MUCH_VALUE, pair.clientRecorder.q.take());
        // Double-check that we cant do anything that requires an open channel
        try {
            client.incrementPayment(SATOSHI);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        // Now check that if the server has a lower min size than what we are willing to spend, we do actually open
        // a channel of that size.
        sendMoneyToWallet(BEST_CHAIN, COIN.multiply(10));
        pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        server = pair.server;
        final Coin.Coin myValue = COIN.multiply(10);
        client = new PaymentChannelClient(wallet, myKey, myValue, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        server.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(TwoWayChannelMessage.newBuilder().setInitiate(Initiate.newBuilder().setExpireTimeSecs(Utils.currentTimeSeconds()).setMinAcceptedChannelSize(COIN.add(SATOSHI).value).setMultisigKey(ByteString.copyFrom(new ECKey().getPubKey())).setMinPayment(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE.value)).setType(INITIATE).build());
        if (useRefunds()) {
            final Protos.TwoWayChannelMessage provideRefund = pair.clientRecorder.checkNextMsg(PROVIDE_REFUND);
            Transaction refund = new Transaction(TestWithWallet.UNITTEST, provideRefund.getProvideRefund().getTx().toByteArray());
            Assert.assertEquals(myValue, refund.getOutput(0).getValue());
        } else {
            Assert.assertEquals(2, client.state().getMajorVersion());
            PaymentChannelV2ClientState state = ((PaymentChannelV2ClientState) (client.state()));
            Assert.assertEquals(myValue, state.refundTx.getOutput(0).getValue());
        }
    }

    @Test
    public void testEmptyWallet() throws Exception {
        Wallet emptyWallet = new Wallet(TestWithWallet.UNITTEST);
        emptyWallet.freshReceiveKey();
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelServer server = pair.server;
        PaymentChannelClient client = new PaymentChannelClient(emptyWallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        server.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        try {
            client.receiveMessage(TwoWayChannelMessage.newBuilder().setInitiate(Initiate.newBuilder().setExpireTimeSecs(Utils.currentTimeSeconds()).setMinAcceptedChannelSize(CENT.value).setMultisigKey(ByteString.copyFrom(new ECKey().getPubKey())).setMinPayment(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE.value)).setType(INITIATE).build());
            Assert.fail();
        } catch (InsufficientMoneyException expected) {
            // This should be thrown.
        }
    }

    @Test
    public void testClientRefusesNonCanonicalKey() throws Exception {
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelServer server = pair.server;
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        server.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        Protos.TwoWayChannelMessage.Builder initiateMsg = TwoWayChannelMessage.newBuilder(pair.serverRecorder.checkNextMsg(INITIATE));
        ByteString brokenKey = initiateMsg.getInitiate().getMultisigKey();
        brokenKey = ByteString.copyFrom(Arrays.copyOf(brokenKey.toByteArray(), ((brokenKey.size()) + 1)));
        initiateMsg.getInitiateBuilder().setMultisigKey(brokenKey);
        client.receiveMessage(initiateMsg.build());
        pair.clientRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(REMOTE_SENT_INVALID_MESSAGE, pair.clientRecorder.q.take());
    }

    @Test
    public void testClientResumeNothing() throws Exception {
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelServer server = pair.server;
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        server.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(TwoWayChannelMessage.newBuilder().setType(CHANNEL_OPEN).build());
        pair.clientRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(REMOTE_SENT_INVALID_MESSAGE, pair.clientRecorder.q.take());
    }

    @Test
    public void testClientRandomMessage() throws Exception {
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, ZERO_HASH, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        pair.clientRecorder.checkNextMsg(CLIENT_VERSION);
        // Send a CLIENT_VERSION back to the client - ?!?!!
        client.receiveMessage(TwoWayChannelMessage.newBuilder().setType(CLIENT_VERSION).build());
        Protos.TwoWayChannelMessage error = pair.clientRecorder.checkNextMsg(ERROR);
        Assert.assertEquals(SYNTAX_ERROR, error.getError().getCode());
        Assert.assertEquals(REMOTE_SENT_INVALID_MESSAGE, pair.clientRecorder.q.take());
    }

    @Test
    public void testDontResumeEmptyChannels() throws Exception {
        // Check that if the client has an empty channel that's being kept around in case we need to broadcast the
        // refund, we don't accidentally try to resume it).
        // Open up a normal channel.
        Sha256Hash someServerId = ZERO_HASH;
        ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
        pair.server.connectionOpen();
        PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        PaymentChannelServer server = pair.server;
        client.connectionOpen();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(INITIATE));
        if (useRefunds()) {
            server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_REFUND));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(RETURN_REFUND));
        }
        broadcastTxPause.release();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_CONTRACT));
        broadcasts.take();
        pair.serverRecorder.checkTotalPayment(REFERENCE_DEFAULT_MIN_TX_FEE);
        client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
        Sha256Hash contractHash = ((Sha256Hash) (pair.serverRecorder.q.take()));
        pair.clientRecorder.checkInitiated();
        Assert.assertNull(pair.serverRecorder.q.poll());
        Assert.assertNull(pair.clientRecorder.q.poll());
        // Send the whole channel at once. The server will broadcast the final contract and settle the channel for us.
        client.incrementPayment(client.state().getValueRefunded());
        broadcastTxPause.release();
        server.receiveMessage(pair.clientRecorder.checkNextMsg(UPDATE_PAYMENT));
        broadcasts.take();
        // The channel is now empty.
        Assert.assertEquals(ZERO, client.state().getValueRefunded());
        pair.serverRecorder.q.take();// Take the Coin.

        client.receiveMessage(pair.serverRecorder.checkNextMsg(PAYMENT_ACK));
        client.receiveMessage(pair.serverRecorder.checkNextMsg(CLOSE));
        Assert.assertEquals(SERVER_REQUESTED_CLOSE, pair.clientRecorder.q.take());
        client.connectionClosed();
        // Now try opening a new channel with the same server ID and verify the client asks for a new channel.
        client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
        client.connectionOpen();
        Protos.TwoWayChannelMessage msg = pair.clientRecorder.checkNextMsg(CLIENT_VERSION);
        Assert.assertFalse(msg.getClientVersion().hasPreviousChannelContractHash());
    }

    @Test
    public void repeatedChannels() throws Exception {
        // Ensures we're selecting channels correctly. Covers a bug in which we'd always try and fail to resume
        // the first channel due to lack of proper closing behaviour.
        // Open up a normal channel, but don't spend all of it, then settle it.
        {
            Sha256Hash someServerId = ZERO_HASH;
            ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
            pair.server.connectionOpen();
            PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
            PaymentChannelServer server = pair.server;
            client.connectionOpen();
            server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(INITIATE));
            if (useRefunds()) {
                server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_REFUND));
                client.receiveMessage(pair.serverRecorder.checkNextMsg(RETURN_REFUND));
            }
            broadcastTxPause.release();
            server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_CONTRACT));
            broadcasts.take();
            pair.serverRecorder.checkTotalPayment(REFERENCE_DEFAULT_MIN_TX_FEE);
            client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
            Sha256Hash contractHash = ((Sha256Hash) (pair.serverRecorder.q.take()));
            pair.clientRecorder.checkInitiated();
            Assert.assertNull(pair.serverRecorder.q.poll());
            Assert.assertNull(pair.clientRecorder.q.poll());
            for (int i = 0; i < 3; i++) {
                ListenableFuture<PaymentIncrementAck> future = client.incrementPayment(CENT);
                server.receiveMessage(pair.clientRecorder.checkNextMsg(UPDATE_PAYMENT));
                pair.serverRecorder.q.take();
                final Protos.TwoWayChannelMessage msg = pair.serverRecorder.checkNextMsg(PAYMENT_ACK);
                final Protos.PaymentAck paymentAck = msg.getPaymentAck();
                Assert.assertTrue("No PaymentAck.Info", paymentAck.hasInfo());
                Assert.assertEquals("Wrong PaymentAck info", ByteString.copyFromUtf8(CENT.toPlainString()), paymentAck.getInfo());
                client.receiveMessage(msg);
                Assert.assertTrue(future.isDone());
                final PaymentIncrementAck paymentIncrementAck = future.get();
                Assert.assertEquals("Wrong value returned from increasePayment", CENT, paymentIncrementAck.getValue());
                Assert.assertEquals("Wrong info returned from increasePayment", ByteString.copyFromUtf8(CENT.toPlainString()), paymentIncrementAck.getInfo());
            }
            // Settle it and verify it's considered to be settled.
            broadcastTxPause.release();
            client.settle();
            server.receiveMessage(pair.clientRecorder.checkNextMsg(CLOSE));
            Transaction settlement1 = broadcasts.take();
            // Server sends back the settle TX it just broadcast.
            final Protos.TwoWayChannelMessage closeMsg = pair.serverRecorder.checkNextMsg(CLOSE);
            final Transaction settlement2 = new Transaction(TestWithWallet.UNITTEST, closeMsg.getSettlement().getTx().toByteArray());
            Assert.assertEquals(settlement1, settlement2);
            client.receiveMessage(closeMsg);
            Assert.assertNotNull(wallet.getTransaction(settlement2.getTxId()));// Close TX entered the wallet.

            sendMoneyToWallet(BEST_CHAIN, settlement1);
            client.connectionClosed();
            server.connectionClosed();
        }
        // Now open a second channel and don't spend all of it/don't settle it.
        {
            Sha256Hash someServerId = ZERO_HASH;
            ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
            pair.server.connectionOpen();
            PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
            PaymentChannelServer server = pair.server;
            client.connectionOpen();
            final Protos.TwoWayChannelMessage msg = pair.clientRecorder.checkNextMsg(CLIENT_VERSION);
            Assert.assertFalse(msg.getClientVersion().hasPreviousChannelContractHash());
            server.receiveMessage(msg);
            client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(INITIATE));
            if (useRefunds()) {
                server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_REFUND));
                client.receiveMessage(pair.serverRecorder.checkNextMsg(RETURN_REFUND));
            }
            broadcastTxPause.release();
            server.receiveMessage(pair.clientRecorder.checkNextMsg(PROVIDE_CONTRACT));
            broadcasts.take();
            pair.serverRecorder.checkTotalPayment(REFERENCE_DEFAULT_MIN_TX_FEE);
            client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
            Sha256Hash contractHash = ((Sha256Hash) (pair.serverRecorder.q.take()));
            pair.clientRecorder.checkInitiated();
            Assert.assertNull(pair.serverRecorder.q.poll());
            Assert.assertNull(pair.clientRecorder.q.poll());
            client.incrementPayment(CENT);
            server.receiveMessage(pair.clientRecorder.checkNextMsg(UPDATE_PAYMENT));
            client.connectionClosed();
            server.connectionClosed();
        }
        // Now connect again and check we resume the second channel.
        {
            Sha256Hash someServerId = ZERO_HASH;
            ChannelTestUtils.RecordingPair pair = ChannelTestUtils.makeRecorders(serverWallet, mockBroadcaster);
            pair.server.connectionOpen();
            PaymentChannelClient client = new PaymentChannelClient(wallet, myKey, COIN, someServerId, null, clientChannelProperties, pair.clientRecorder);
            PaymentChannelServer server = pair.server;
            client.connectionOpen();
            server.receiveMessage(pair.clientRecorder.checkNextMsg(CLIENT_VERSION));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(SERVER_VERSION));
            client.receiveMessage(pair.serverRecorder.checkNextMsg(CHANNEL_OPEN));
        }
        Assert.assertEquals(2, StoredPaymentChannelClientStates.getFromWallet(wallet).mapChannels.size());
    }
}

