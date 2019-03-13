/**
 * Copyright 2011 Google Inc.
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
package org.bitcoinj.wallet;


import AbstractBlockChain.NewBlockType.BEST_CHAIN;
import AbstractBlockChain.NewBlockType.SIDE_CHAIN;
import BalanceType.ESTIMATED_SPENDABLE;
import Block.BLOCK_HEIGHT_GENESIS;
import COIN.value;
import Coin.COIN;
import Coin.MICROCOIN;
import DeterministicKeyChain.ACCOUNT_ZERO_PATH;
import DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH;
import DeterministicKeyChain.EXTERNAL_SUBPATH;
import ECKey.MissingPrivateKeyException;
import EncryptionType.ENCRYPTED_SCRYPT_AES;
import KeyChain.KeyPurpose.CHANGE;
import KeyChain.KeyPurpose.RECEIVE_FUNDS;
import Protos.ScryptParameters.Builder;
import Script.ScriptType.P2PKH;
import Script.ScriptType.P2WPKH;
import Sha256Hash.ZERO_HASH;
import Threading.SAME_THREAD;
import Transaction.DEFAULT_TX_FEE;
import Transaction.MIN_NONDUST_OUTPUT;
import Transaction.Purpose.KEY_ROTATION;
import Transaction.Purpose.RAISE_FEE;
import Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
import TransactionConfidence.ConfidenceType.BUILDING;
import TransactionConfidence.ConfidenceType.DEAD;
import TransactionConfidence.ConfidenceType.UNKNOWN;
import TransactionConfidence.Listener;
import TransactionSigner.MissingSignatureException;
import Utils.HEX;
import Wallet.BalanceType.AVAILABLE;
import Wallet.BalanceType.AVAILABLE_SPENDABLE;
import Wallet.BalanceType.ESTIMATED;
import Wallet.DustySendRequested;
import Wallet.ExceededMaxTransactionSize;
import Wallet.MissingSigsMode;
import Wallet.MissingSigsMode.THROW;
import Wallet.MissingSigsMode.USE_DUMMY_SIG;
import Wallet.MissingSigsMode.USE_OP_ZERO;
import Wallet.MultipleOpReturnRequested;
import WalletTransaction.Pool.PENDING;
import WalletTransaction.Pool.SPENT;
import WalletTransaction.Pool.UNSPENT;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionConfidence.ConfidenceType;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptPattern;
import org.bitcoinj.signers.TransactionSigner;
import org.bitcoinj.testing.FakeTxBuilder;
import org.bitcoinj.testing.MockTransactionBroadcaster;
import org.bitcoinj.testing.NopTransactionSigner;
import org.bitcoinj.testing.TestWithWallet;
import org.bitcoinj.utils.Fiat;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.WalletTransaction.Pool;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import org.bouncycastle.crypto.params.KeyParameter;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ChangeReason.TYPE;
import static Result.NON_FINAL;
import static Result.OK;
import static RiskAnalysis.Result.NON_STANDARD;


public class WalletTest extends TestWithWallet {
    private static final Logger log = LoggerFactory.getLogger(WalletTest.class);

    private static final CharSequence PASSWORD1 = "my helicopter contains eels";

    private static final CharSequence WRONG_PASSWORD = "nothing noone nobody nowhere";

    private final Address OTHER_ADDRESS = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());

    @Test
    public void createBasic() {
        Wallet wallet = Wallet.createBasic(TestWithWallet.UNITTEST);
        Assert.assertEquals(0, wallet.getKeyChainGroupSize());
        wallet.importKey(new ECKey());
        Assert.assertEquals(1, wallet.getKeyChainGroupSize());
    }

    @Test(expected = IllegalStateException.class)
    public void createBasic_noDerivation() {
        Wallet wallet = Wallet.createBasic(TestWithWallet.UNITTEST);
        wallet.currentReceiveAddress();
    }

    @Test
    public void getSeedAsWords1() {
        // Can't verify much here as the wallet is random each time. We could fix the RNG for the unit tests and solve.
        Assert.assertEquals(12, wallet.getKeyChainSeed().getMnemonicCode().size());
    }

    @Test
    public void checkSeed() throws MnemonicException {
        wallet.getKeyChainSeed().check();
    }

    @Test
    public void basicSpending() throws Exception {
        basicSpendingCommon(wallet, myAddress, OTHER_ADDRESS, null);
    }

    @Test
    public void basicSpendingToP2SH() throws Exception {
        Address destination = LegacyAddress.fromScriptHash(TestWithWallet.UNITTEST, Utils.HEX.decode("4a22c3c4cbb31e4d03b15550636762bda0baf85a"));
        basicSpendingCommon(wallet, myAddress, destination, null);
    }

    @Test
    public void basicSpendingWithEncryptedWallet() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        Address myEncryptedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, encryptedWallet.freshReceiveKey());
        basicSpendingCommon(encryptedWallet, myEncryptedAddress, OTHER_ADDRESS, encryptedWallet);
    }

    @Test
    public void encryptDecryptWalletWithArbitraryPathAndScriptType() throws Exception {
        final byte[] ENTROPY = Sha256Hash.hash("don't use a string seed like this in real life".getBytes());
        KeyChainGroup keyChainGroup = KeyChainGroup.builder(TestWithWallet.UNITTEST).addChain(DeterministicKeyChain.builder().seed(new DeterministicSeed(ENTROPY, "", 1389353062L)).outputScriptType(P2WPKH).accountPath(BIP44_ACCOUNT_ZERO_PATH).build()).build();
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST, keyChainGroup);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        encryptedWallet.decrypt(WalletTest.PASSWORD1);
    }

    @Test
    public void basicSpendingFromP2SH() throws Exception {
        createMarriedWallet(2, 2);
        myAddress = wallet.currentAddress(RECEIVE_FUNDS);
        basicSpendingCommon(wallet, myAddress, OTHER_ADDRESS, null);
        createMarriedWallet(2, 3);
        myAddress = wallet.currentAddress(RECEIVE_FUNDS);
        basicSpendingCommon(wallet, myAddress, OTHER_ADDRESS, null);
        createMarriedWallet(3, 3);
        myAddress = wallet.currentAddress(RECEIVE_FUNDS);
        basicSpendingCommon(wallet, myAddress, OTHER_ADDRESS, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void thresholdShouldNotExceedNumberOfKeys() throws Exception {
        createMarriedWallet(3, 2);
    }

    @Test
    public void spendingWithIncompatibleSigners() throws Exception {
        wallet.addTransactionSigner(new NopTransactionSigner(true));
        basicSpendingCommon(wallet, myAddress, OTHER_ADDRESS, null);
    }

    static class TestRiskAnalysis implements RiskAnalysis {
        private final boolean risky;

        public TestRiskAnalysis(boolean risky) {
            this.risky = risky;
        }

        @Override
        public Result analyze() {
            return risky ? NON_FINAL : OK;
        }

        public static class Analyzer implements RiskAnalysis.Analyzer {
            private final Transaction riskyTx;

            Analyzer(Transaction riskyTx) {
                this.riskyTx = riskyTx;
            }

            @Override
            public RiskAnalysis create(Wallet wallet, Transaction tx, List<Transaction> dependencies) {
                return new WalletTest.TestRiskAnalysis((tx == (riskyTx)));
            }
        }
    }

    static class TestCoinSelector extends DefaultCoinSelector {
        @Override
        protected boolean shouldSelect(Transaction tx) {
            return true;
        }
    }

    @Test
    public void cleanup() throws Exception {
        Transaction t = cleanupCommon(OTHER_ADDRESS);
        // Consider the new pending as risky and remove it from the wallet
        wallet.setRiskAnalyzer(new WalletTest.TestRiskAnalysis.Analyzer(t));
        wallet.cleanup();
        Assert.assertTrue(wallet.isConsistent());
        Assert.assertEquals("Wrong number of PENDING", 1, wallet.getPoolSize(PENDING));
        Assert.assertEquals("Wrong number of UNSPENT", 0, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals("Wrong number of ALL", 2, wallet.getTransactions(true).size());
        Assert.assertEquals(valueOf(0, 50), wallet.getBalance(ESTIMATED));
    }

    @Test
    public void cleanupFailsDueToSpend() throws Exception {
        Transaction t = cleanupCommon(OTHER_ADDRESS);
        // Now we have another incoming pending.  Spend everything.
        Coin v3 = valueOf(0, 60);
        SendRequest req = SendRequest.to(OTHER_ADDRESS, v3);
        // Force selection of the incoming coin so that we can spend it
        req.coinSelector = new WalletTest.TestCoinSelector();
        wallet.completeTx(req);
        wallet.commitTx(req.tx);
        Assert.assertEquals("Wrong number of PENDING", 3, wallet.getPoolSize(PENDING));
        Assert.assertEquals("Wrong number of UNSPENT", 0, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals("Wrong number of ALL", 4, wallet.getTransactions(true).size());
        // Consider the new pending as risky and try to remove it from the wallet
        wallet.setRiskAnalyzer(new WalletTest.TestRiskAnalysis.Analyzer(t));
        wallet.cleanup();
        Assert.assertTrue(wallet.isConsistent());
        // The removal should have failed
        Assert.assertEquals("Wrong number of PENDING", 3, wallet.getPoolSize(PENDING));
        Assert.assertEquals("Wrong number of UNSPENT", 0, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals("Wrong number of ALL", 4, wallet.getTransactions(true).size());
        Assert.assertEquals(ZERO, wallet.getBalance(ESTIMATED));
    }

    @Test
    public void customTransactionSpending() throws Exception {
        // We'll set up a wallet that receives a coin, then sends a coin of lesser value and keeps the change.
        Coin v1 = valueOf(3, 0);
        sendMoneyToWallet(BEST_CHAIN, v1);
        Assert.assertEquals(v1, wallet.getBalance());
        Assert.assertEquals(1, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(1, wallet.getTransactions(true).size());
        Coin v2 = valueOf(0, 50);
        Coin v3 = valueOf(0, 75);
        Coin v4 = valueOf(1, 25);
        Transaction t2 = new Transaction(TestWithWallet.UNITTEST);
        t2.addOutput(v2, OTHER_ADDRESS);
        t2.addOutput(v3, OTHER_ADDRESS);
        t2.addOutput(v4, OTHER_ADDRESS);
        SendRequest req = SendRequest.forTx(t2);
        wallet.completeTx(req);
        // Do some basic sanity checks.
        Assert.assertEquals(1, t2.getInputs().size());
        List<ScriptChunk> scriptSigChunks = t2.getInput(0).getScriptSig().getChunks();
        // check 'from address' -- in a unit test this is fine
        Assert.assertEquals(2, scriptSigChunks.size());
        Assert.assertEquals(myAddress, LegacyAddress.fromPubKeyHash(TestWithWallet.UNITTEST, Utils.sha256hash160(scriptSigChunks.get(1).data)));
        Assert.assertEquals(UNKNOWN, t2.getConfidence().getConfidenceType());
        // We have NOT proven that the signature is correct!
        wallet.commitTx(t2);
        Assert.assertEquals(1, wallet.getPoolSize(PENDING));
        Assert.assertEquals(1, wallet.getPoolSize(SPENT));
        Assert.assertEquals(2, wallet.getTransactions(true).size());
    }

    @Test
    public void sideChain() throws Exception {
        // The wallet receives a coin on the best chain, then on a side chain. Balance is equal to both added together
        // as we assume the side chain tx is pending and will be included shortly.
        Coin v1 = COIN;
        sendMoneyToWallet(BEST_CHAIN, v1);
        Assert.assertEquals(v1, wallet.getBalance());
        Assert.assertEquals(1, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(1, wallet.getTransactions(true).size());
        Coin v2 = valueOf(0, 50);
        sendMoneyToWallet(SIDE_CHAIN, v2);
        Assert.assertEquals(2, wallet.getTransactions(true).size());
        Assert.assertEquals(v1, wallet.getBalance());
        Assert.assertEquals(v1.add(v2), wallet.getBalance(ESTIMATED));
    }

    @Test
    public void balance() throws Exception {
        // Receive 5 coins then half a coin.
        Coin v1 = valueOf(5, 0);
        Coin v2 = valueOf(0, 50);
        Coin expected = valueOf(5, 50);
        Assert.assertEquals(0, wallet.getTransactions(true).size());
        sendMoneyToWallet(BEST_CHAIN, v1);
        Assert.assertEquals(1, wallet.getPoolSize(UNSPENT));
        sendMoneyToWallet(BEST_CHAIN, v2);
        Assert.assertEquals(2, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(expected, wallet.getBalance());
        // Now spend one coin.
        Coin v3 = COIN;
        Transaction spend = wallet.createSend(OTHER_ADDRESS, v3);
        wallet.commitTx(spend);
        Assert.assertEquals(1, wallet.getPoolSize(PENDING));
        // Available and estimated balances should not be the same. We don't check the exact available balance here
        // because it depends on the coin selection algorithm.
        Assert.assertEquals(valueOf(4, 50), wallet.getBalance(ESTIMATED));
        Assert.assertFalse(wallet.getBalance(AVAILABLE).equals(wallet.getBalance(ESTIMATED)));
        // Now confirm the transaction by including it into a block.
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, spend);
        // Change is confirmed. We started with 5.50 so we should have 4.50 left.
        Coin v4 = valueOf(4, 50);
        Assert.assertEquals(v4, wallet.getBalance(AVAILABLE));
    }

    @Test
    public void balanceWithIdenticalOutputs() {
        Assert.assertEquals(Coin.ZERO, wallet.getBalance(BalanceType.ESTIMATED));
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        tx.addOutput(COIN, myAddress);
        tx.addOutput(COIN, myAddress);// identical to the above

        wallet.addWalletTransaction(new WalletTransaction(Pool.UNSPENT, tx));
        Assert.assertEquals(COIN.plus(COIN), wallet.getBalance(BalanceType.ESTIMATED));
    }

    // Intuitively you'd expect to be able to create a transaction with identical inputs and outputs and get an
    // identical result to Bitcoin Core. However the signatures are not deterministic - signing the same data
    // with the same key twice gives two different outputs. So we cannot prove bit-for-bit compatibility in this test
    // suite.
    @Test
    public void blockChainCatchup() throws Exception {
        // Test that we correctly process transactions arriving from the chain, with callbacks for inbound and outbound.
        final Coin[] bigints = new Coin[4];
        final Transaction[] txn = new Transaction[2];
        final LinkedList<Transaction> confTxns = new LinkedList<>();
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                bigints[0] = prevBalance;
                bigints[1] = newBalance;
                txn[0] = tx;
            }
        });
        wallet.addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                bigints[2] = prevBalance;
                bigints[3] = newBalance;
                txn[1] = tx;
            }
        });
        wallet.addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                confTxns.add(tx);
            }
        });
        // Receive some money.
        Coin oneCoin = COIN;
        Transaction tx1 = sendMoneyToWallet(BEST_CHAIN, oneCoin);
        Threading.waitForUserCode();
        Assert.assertEquals(null, txn[1]);// onCoinsSent not called.

        Assert.assertEquals(tx1, confTxns.getFirst());// onTransactionConfidenceChanged called

        Assert.assertEquals(txn[0].getTxId(), tx1.getTxId());
        Assert.assertEquals(ZERO, bigints[0]);
        Assert.assertEquals(oneCoin, bigints[1]);
        Assert.assertEquals(BUILDING, tx1.getConfidence().getConfidenceType());
        Assert.assertEquals(1, tx1.getConfidence().getAppearedAtChainHeight());
        // Send 0.10 to somebody else.
        Transaction send1 = wallet.createSend(OTHER_ADDRESS, valueOf(0, 10));
        // Pretend it makes it into the block chain, our wallet state is cleared but we still have the keys, and we
        // want to get back to our previous state. We can do this by just not confirming the transaction as
        // createSend is stateless.
        txn[0] = txn[1] = null;
        confTxns.clear();
        sendMoneyToWallet(BEST_CHAIN, send1);
        Threading.waitForUserCode();
        Assert.assertEquals(Coin.valueOf(0, 90), wallet.getBalance());
        Assert.assertEquals(null, txn[0]);
        Assert.assertEquals(2, confTxns.size());
        Assert.assertEquals(txn[1].getTxId(), send1.getTxId());
        Assert.assertEquals(COIN, bigints[2]);
        Assert.assertEquals(Coin.valueOf(0, 90), bigints[3]);
        // And we do it again after the catchup.
        Transaction send2 = wallet.createSend(OTHER_ADDRESS, valueOf(0, 10));
        // What we'd really like to do is prove Bitcoin Core would accept it .... no such luck unfortunately.
        wallet.commitTx(send2);
        sendMoneyToWallet(BEST_CHAIN, send2);
        Assert.assertEquals(Coin.valueOf(0, 80), wallet.getBalance());
        Threading.waitForUserCode();
        FakeTxBuilder.BlockPair b4 = FakeTxBuilder.createFakeBlock(blockStore, BLOCK_HEIGHT_GENESIS);
        confTxns.clear();
        wallet.notifyNewBestBlock(b4.storedBlock);
        Threading.waitForUserCode();
        Assert.assertEquals(3, confTxns.size());
    }

    @Test
    public void balances() throws Exception {
        Coin nanos = COIN;
        Transaction tx1 = sendMoneyToWallet(BEST_CHAIN, nanos);
        Assert.assertEquals(nanos, tx1.getValueSentToMe(wallet));
        Assert.assertTrue(((tx1.getWalletOutputs(wallet).size()) >= 1));
        // Send 0.10 to somebody else.
        Transaction send1 = wallet.createSend(OTHER_ADDRESS, valueOf(0, 10));
        // Reserialize.
        Transaction send2 = TestWithWallet.UNITTEST.getDefaultSerializer().makeTransaction(send1.bitcoinSerialize());
        Assert.assertEquals(nanos, send2.getValueSentFromMe(wallet));
        Assert.assertEquals(ZERO.subtract(valueOf(0, 10)), send2.getValue(wallet));
    }

    @Test
    public void isConsistent_duplicates() throws Exception {
        // This test ensures that isConsistent catches duplicate transactions, eg, because we submitted the same block
        // twice (this is not allowed).
        Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        TransactionOutput output = new TransactionOutput(TestWithWallet.UNITTEST, tx, valueOf(0, 5), OTHER_ADDRESS);
        tx.addOutput(output);
        wallet.receiveFromBlock(tx, null, BlockChain.NewBlockType.BEST_CHAIN, 0);
        Assert.assertTrue(wallet.isConsistent());
        Transaction txClone = TestWithWallet.UNITTEST.getDefaultSerializer().makeTransaction(tx.bitcoinSerialize());
        try {
            wallet.receiveFromBlock(txClone, null, BlockChain.NewBlockType.BEST_CHAIN, 0);
            Assert.fail("Illegal argument not thrown when it should have been.");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void isConsistent_pools() throws Exception {
        // This test ensures that isConsistent catches transactions that are in incompatible pools.
        Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        TransactionOutput output = new TransactionOutput(TestWithWallet.UNITTEST, tx, valueOf(0, 5), OTHER_ADDRESS);
        tx.addOutput(output);
        wallet.receiveFromBlock(tx, null, BlockChain.NewBlockType.BEST_CHAIN, 0);
        Assert.assertTrue(wallet.isConsistent());
        wallet.addWalletTransaction(new WalletTransaction(Pool.PENDING, tx));
        Assert.assertFalse(wallet.isConsistent());
    }

    @Test
    public void isConsistent_spent() throws Exception {
        // This test ensures that isConsistent catches transactions that are marked spent when
        // they aren't.
        Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        TransactionOutput output = new TransactionOutput(TestWithWallet.UNITTEST, tx, valueOf(0, 5), OTHER_ADDRESS);
        tx.addOutput(output);
        Assert.assertTrue(wallet.isConsistent());
        wallet.addWalletTransaction(new WalletTransaction(Pool.SPENT, tx));
        Assert.assertFalse(wallet.isConsistent());
    }

    @Test
    public void isTxConsistentReturnsFalseAsExpected() {
        Wallet wallet = new Wallet(TestWithWallet.UNITTEST);
        TransactionOutput to = createMock(TransactionOutput.class);
        EasyMock.expect(to.isAvailableForSpending()).andReturn(true);
        EasyMock.expect(to.isMineOrWatched(wallet)).andReturn(true);
        EasyMock.expect(to.getSpentBy()).andReturn(new TransactionInput(TestWithWallet.UNITTEST, null, new byte[0]));
        Transaction tx = FakeTxBuilder.createFakeTxWithoutChange(TestWithWallet.UNITTEST, to);
        replay(to);
        boolean isConsistent = wallet.isTxConsistent(tx, false);
        Assert.assertFalse(isConsistent);
    }

    @Test
    public void isTxConsistentReturnsFalseAsExpected_WhenAvailableForSpendingEqualsFalse() {
        Wallet wallet = new Wallet(TestWithWallet.UNITTEST);
        TransactionOutput to = createMock(TransactionOutput.class);
        EasyMock.expect(to.isAvailableForSpending()).andReturn(false);
        EasyMock.expect(to.getSpentBy()).andReturn(null);
        Transaction tx = FakeTxBuilder.createFakeTxWithoutChange(TestWithWallet.UNITTEST, to);
        replay(to);
        boolean isConsistent = wallet.isTxConsistent(tx, false);
        Assert.assertFalse(isConsistent);
    }

    @Test
    public void transactions() throws Exception {
        // This test covers a bug in which Transaction.getValueSentFromMe was calculating incorrectly.
        Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        // Now add another output (ie, change) that goes to some other address.
        TransactionOutput output = new TransactionOutput(TestWithWallet.UNITTEST, tx, valueOf(0, 5), OTHER_ADDRESS);
        tx.addOutput(output);
        // Note that tx is no longer valid: it spends more than it imports. However checking transactions balance
        // correctly isn't possible in SPV mode because value is a property of outputs not inputs. Without all
        // transactions you can't check they add up.
        sendMoneyToWallet(BEST_CHAIN, tx);
        // Now the other guy creates a transaction which spends that change.
        Transaction tx2 = new Transaction(TestWithWallet.UNITTEST);
        tx2.addInput(output);
        tx2.addOutput(new TransactionOutput(TestWithWallet.UNITTEST, tx2, valueOf(0, 5), myAddress));
        // tx2 doesn't send any coins from us, even though the output is in the wallet.
        Assert.assertEquals(ZERO, tx2.getValueSentFromMe(wallet));
    }

    @Test
    public void bounce() throws Exception {
        // This test covers bug 64 (False double spends). Check that if we create a spend and it's immediately sent
        // back to us, this isn't considered as a double spend.
        Coin coin1 = COIN;
        sendMoneyToWallet(BEST_CHAIN, coin1);
        // Send half to some other guy. Sending only half then waiting for a confirm is important to ensure the tx is
        // in the unspent pool, not pending or spent.
        Coin coinHalf = valueOf(0, 50);
        Assert.assertEquals(1, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(1, wallet.getTransactions(true).size());
        Transaction outbound1 = wallet.createSend(OTHER_ADDRESS, coinHalf);
        wallet.commitTx(outbound1);
        sendMoneyToWallet(BEST_CHAIN, outbound1);
        Assert.assertTrue(((outbound1.getWalletOutputs(wallet).size()) <= 1));// the change address at most

        // That other guy gives us the coins right back.
        Transaction inbound2 = new Transaction(TestWithWallet.UNITTEST);
        inbound2.addOutput(new TransactionOutput(TestWithWallet.UNITTEST, inbound2, coinHalf, myAddress));
        Assert.assertTrue(((outbound1.getWalletOutputs(wallet).size()) >= 1));
        inbound2.addInput(outbound1.getOutputs().get(0));
        sendMoneyToWallet(BEST_CHAIN, inbound2);
        Assert.assertEquals(coin1, wallet.getBalance());
    }

    @Test
    public void doubleSpendUnspendsOtherInputs() throws Exception {
        // Test another Finney attack, but this time the killed transaction was also spending some other outputs in
        // our wallet which were not themselves double spent. This test ensures the death of the pending transaction
        // frees up the other outputs and makes them spendable again.
        // Receive 1 coin and then 2 coins in separate transactions.
        sendMoneyToWallet(BEST_CHAIN, COIN);
        sendMoneyToWallet(BEST_CHAIN, valueOf(2, 0));
        // Create a send to a merchant of all our coins.
        Transaction send1 = wallet.createSend(OTHER_ADDRESS, valueOf(2, 90));
        // Create a double spend of just the first one.
        Address BAD_GUY = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        Transaction send2 = wallet.createSend(BAD_GUY, COIN);
        send2 = TestWithWallet.UNITTEST.getDefaultSerializer().makeTransaction(send2.bitcoinSerialize());
        // Broadcast send1, it's now pending.
        wallet.commitTx(send1);
        Assert.assertEquals(ZERO, wallet.getBalance());// change of 10 cents is not yet mined so not included in the balance.

        // Receive a block that overrides the send1 using send2.
        sendMoneyToWallet(BEST_CHAIN, send2);
        // send1 got rolled back and replaced with a smaller send that only used one of our received coins, thus ...
        Assert.assertEquals(valueOf(2, 0), wallet.getBalance());
        Assert.assertTrue(wallet.isConsistent());
    }

    @Test
    public void doubleSpends() throws Exception {
        // Test the case where two semantically identical but bitwise different transactions double spend each other.
        // We call the second transaction a "mutant" of the first.
        // 
        // This can (and has!) happened when a wallet is cloned between devices, and both devices decide to make the
        // same spend simultaneously - for example due a re-keying operation. It can also happen if there are malicious
        // nodes in the P2P network that are mutating transactions on the fly as occurred during Feb 2014.
        final Coin value = COIN;
        final Coin value2 = valueOf(2, 0);
        // Give us three coins and make sure we have some change.
        sendMoneyToWallet(BEST_CHAIN, value.add(value2));
        Transaction send1 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, value2));
        Transaction send2 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, value2));
        byte[] buf = send1.bitcoinSerialize();
        buf[43] = 0;// Break the signature: bitcoinj won't check in SPV mode and this is easier than other mutations.

        send1 = TestWithWallet.UNITTEST.getDefaultSerializer().makeTransaction(buf);
        wallet.commitTx(send2);
        wallet.allowSpendingUnconfirmedTransactions();
        Assert.assertEquals(value, wallet.getBalance(ESTIMATED));
        // Now spend the change. This transaction should die permanently when the mutant appears in the chain.
        Transaction send3 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, value));
        wallet.commitTx(send3);
        Assert.assertEquals(ZERO, wallet.getBalance());
        final LinkedList<TransactionConfidence> dead = new LinkedList<>();
        final TransactionConfidence.Listener listener = new TransactionConfidence.Listener() {
            @Override
            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                final TransactionConfidence.ConfidenceType type = confidence.getConfidenceType();
                if ((reason == (TYPE)) && (type == (ConfidenceType.DEAD)))
                    dead.add(confidence);

            }
        };
        send2.getConfidence().addEventListener(SAME_THREAD, listener);
        send3.getConfidence().addEventListener(SAME_THREAD, listener);
        // Double spend!
        sendMoneyToWallet(BEST_CHAIN, send1);
        // Back to having one coin.
        Assert.assertEquals(value, wallet.getBalance());
        Assert.assertEquals(send2.getTxId(), dead.poll().getTransactionHash());
        Assert.assertEquals(send3.getTxId(), dead.poll().getTransactionHash());
    }

    @Test
    public void doubleSpendFinneyAttack() throws Exception {
        // A Finney attack is where a miner includes a transaction spending coins to themselves but does not
        // broadcast it. When they find a solved block, they hold it back temporarily whilst they buy something with
        // those same coins. After purchasing, they broadcast the block thus reversing the transaction. It can be
        // done by any miner for products that can be bought at a chosen time and very quickly (as every second you
        // withold your block means somebody else might find it first, invalidating your work).
        // 
        // Test that we handle the attack correctly: a double spend on the chain moves transactions from pending to dead.
        // This needs to work both for transactions we create, and that we receive from others.
        final Transaction[] eventDead = new Transaction[1];
        final Transaction[] eventReplacement = new Transaction[1];
        final int[] eventWalletChanged = new int[1];
        wallet.addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                if ((tx.getConfidence().getConfidenceType()) == (ConfidenceType.DEAD)) {
                    eventDead[0] = tx;
                    eventReplacement[0] = tx.getConfidence().getOverridingTransaction();
                }
            }
        });
        wallet.addChangeEventListener(new WalletChangeEventListener() {
            @Override
            public void onWalletChanged(Wallet wallet) {
                (eventWalletChanged[0])++;
            }
        });
        // Receive 1 BTC.
        Coin nanos = COIN;
        sendMoneyToWallet(BEST_CHAIN, nanos);
        Transaction received = wallet.getTransactions(false).iterator().next();
        // Create a send to a merchant.
        Transaction send1 = wallet.createSend(OTHER_ADDRESS, valueOf(0, 50));
        // Create a double spend.
        Address BAD_GUY = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        Transaction send2 = wallet.createSend(BAD_GUY, valueOf(0, 50));
        send2 = TestWithWallet.UNITTEST.getDefaultSerializer().makeTransaction(send2.bitcoinSerialize());
        // Broadcast send1.
        wallet.commitTx(send1);
        Assert.assertEquals(send1, received.getOutput(0).getSpentBy().getParentTransaction());
        // Receive a block that overrides it.
        sendMoneyToWallet(BEST_CHAIN, send2);
        Threading.waitForUserCode();
        Assert.assertEquals(send1, eventDead[0]);
        Assert.assertEquals(send2, eventReplacement[0]);
        Assert.assertEquals(DEAD, send1.getConfidence().getConfidenceType());
        Assert.assertEquals(send2, received.getOutput(0).getSpentBy().getParentTransaction());
        FakeTxBuilder.DoubleSpends doubleSpends = FakeTxBuilder.createFakeDoubleSpendTxns(TestWithWallet.UNITTEST, myAddress);
        // t1 spends to our wallet. t2 double spends somewhere else.
        wallet.receivePending(doubleSpends.t1, null);
        Assert.assertEquals(TransactionConfidence.ConfidenceType.PENDING, doubleSpends.t1.getConfidence().getConfidenceType());
        sendMoneyToWallet(BEST_CHAIN, doubleSpends.t2);
        Threading.waitForUserCode();
        Assert.assertEquals(DEAD, doubleSpends.t1.getConfidence().getConfidenceType());
        Assert.assertEquals(doubleSpends.t2, doubleSpends.t1.getConfidence().getOverridingTransaction());
        Assert.assertEquals(5, eventWalletChanged[0]);
    }

    @Test
    public void doubleSpendWeCreate() throws Exception {
        // Test we keep pending double spends in IN_CONFLICT until one of them is included in a block
        // and we handle reorgs and dependency chains properly.
        // The following graph shows the txns we use in this test and how they are related
        // (Eg txA1 spends txARoot outputs, txC1 spends txA1 and txB1 outputs, etc).
        // txARoot (10)  -> txA1 (1)  -+
        // |--> txC1 (0.10) -> txD1 (0.01)
        // txBRoot (100) -> txB1 (11) -+
        // 
        // txARoot (10)  -> txA2 (2)  -+
        // |--> txC2 (0.20) -> txD2 (0.02)
        // txBRoot (100) -> txB2 (22) -+
        // 
        // txARoot (10)  -> txA3 (3)
        // 
        // txA1 is in conflict with txA2 and txA3. txB1 is in conflict with txB2.
        CoinSelector originalCoinSelector = wallet.getCoinSelector();
        try {
            wallet.allowSpendingUnconfirmedTransactions();
            Transaction txARoot = sendMoneyToWallet(BEST_CHAIN, valueOf(10, 0));
            SendRequest a1Req = SendRequest.to(OTHER_ADDRESS, valueOf(1, 0));
            a1Req.tx.addInput(txARoot.getOutput(0));
            a1Req.shuffleOutputs = false;
            wallet.completeTx(a1Req);
            Transaction txA1 = a1Req.tx;
            SendRequest a2Req = SendRequest.to(OTHER_ADDRESS, valueOf(2, 0));
            a2Req.tx.addInput(txARoot.getOutput(0));
            a2Req.shuffleOutputs = false;
            wallet.completeTx(a2Req);
            Transaction txA2 = a2Req.tx;
            SendRequest a3Req = SendRequest.to(OTHER_ADDRESS, valueOf(3, 0));
            a3Req.tx.addInput(txARoot.getOutput(0));
            a3Req.shuffleOutputs = false;
            wallet.completeTx(a3Req);
            Transaction txA3 = a3Req.tx;
            wallet.commitTx(txA1);
            wallet.commitTx(txA2);
            wallet.commitTx(txA3);
            Transaction txBRoot = sendMoneyToWallet(BEST_CHAIN, valueOf(100, 0));
            SendRequest b1Req = SendRequest.to(OTHER_ADDRESS, valueOf(11, 0));
            b1Req.tx.addInput(txBRoot.getOutput(0));
            b1Req.shuffleOutputs = false;
            wallet.completeTx(b1Req);
            Transaction txB1 = b1Req.tx;
            SendRequest b2Req = SendRequest.to(OTHER_ADDRESS, valueOf(22, 0));
            b2Req.tx.addInput(txBRoot.getOutput(0));
            b2Req.shuffleOutputs = false;
            wallet.completeTx(b2Req);
            Transaction txB2 = b2Req.tx;
            wallet.commitTx(txB1);
            wallet.commitTx(txB2);
            SendRequest c1Req = SendRequest.to(OTHER_ADDRESS, valueOf(0, 10));
            c1Req.tx.addInput(txA1.getOutput(1));
            c1Req.tx.addInput(txB1.getOutput(1));
            c1Req.shuffleOutputs = false;
            wallet.completeTx(c1Req);
            Transaction txC1 = c1Req.tx;
            SendRequest c2Req = SendRequest.to(OTHER_ADDRESS, valueOf(0, 20));
            c2Req.tx.addInput(txA2.getOutput(1));
            c2Req.tx.addInput(txB2.getOutput(1));
            c2Req.shuffleOutputs = false;
            wallet.completeTx(c2Req);
            Transaction txC2 = c2Req.tx;
            wallet.commitTx(txC1);
            wallet.commitTx(txC2);
            SendRequest d1Req = SendRequest.to(OTHER_ADDRESS, valueOf(0, 1));
            d1Req.tx.addInput(txC1.getOutput(1));
            d1Req.shuffleOutputs = false;
            wallet.completeTx(d1Req);
            Transaction txD1 = d1Req.tx;
            SendRequest d2Req = SendRequest.to(OTHER_ADDRESS, valueOf(0, 2));
            d2Req.tx.addInput(txC2.getOutput(1));
            d2Req.shuffleOutputs = false;
            wallet.completeTx(d2Req);
            Transaction txD2 = d2Req.tx;
            wallet.commitTx(txD1);
            wallet.commitTx(txD2);
            assertInConflict(txA1);
            assertInConflict(txA2);
            assertInConflict(txA3);
            assertInConflict(txB1);
            assertInConflict(txB2);
            assertInConflict(txC1);
            assertInConflict(txC2);
            assertInConflict(txD1);
            assertInConflict(txD2);
            // Add a block to the block store. The rest of the blocks in this test will be on top of this one.
            FakeTxBuilder.BlockPair blockPair0 = FakeTxBuilder.createFakeBlock(blockStore, 1);
            // A block was mined including txA1
            FakeTxBuilder.BlockPair blockPair1 = FakeTxBuilder.createFakeBlock(blockStore, 2, txA1);
            wallet.receiveFromBlock(txA1, blockPair1.storedBlock, BEST_CHAIN, 0);
            wallet.notifyNewBestBlock(blockPair1.storedBlock);
            assertSpent(txA1);
            assertDead(txA2);
            assertDead(txA3);
            assertInConflict(txB1);
            assertInConflict(txB2);
            assertInConflict(txC1);
            assertDead(txC2);
            assertInConflict(txD1);
            assertDead(txD2);
            // A reorg: previous block "replaced" by new block containing txA1 and txB1
            FakeTxBuilder.BlockPair blockPair2 = FakeTxBuilder.createFakeBlock(blockStore, blockPair0.storedBlock, 2, txA1, txB1);
            wallet.receiveFromBlock(txA1, blockPair2.storedBlock, SIDE_CHAIN, 0);
            wallet.receiveFromBlock(txB1, blockPair2.storedBlock, SIDE_CHAIN, 1);
            wallet.reorganize(blockPair0.storedBlock, Lists.newArrayList(blockPair1.storedBlock), Lists.newArrayList(blockPair2.storedBlock));
            assertSpent(txA1);
            assertDead(txA2);
            assertDead(txA3);
            assertSpent(txB1);
            assertDead(txB2);
            assertPending(txC1);
            assertDead(txC2);
            assertPending(txD1);
            assertDead(txD2);
            // A reorg: previous block "replaced" by new block containing txA1, txB1 and txC1
            FakeTxBuilder.BlockPair blockPair3 = FakeTxBuilder.createFakeBlock(blockStore, blockPair0.storedBlock, 2, txA1, txB1, txC1);
            wallet.receiveFromBlock(txA1, blockPair3.storedBlock, SIDE_CHAIN, 0);
            wallet.receiveFromBlock(txB1, blockPair3.storedBlock, SIDE_CHAIN, 1);
            wallet.receiveFromBlock(txC1, blockPair3.storedBlock, SIDE_CHAIN, 2);
            wallet.reorganize(blockPair0.storedBlock, Lists.newArrayList(blockPair2.storedBlock), Lists.newArrayList(blockPair3.storedBlock));
            assertSpent(txA1);
            assertDead(txA2);
            assertDead(txA3);
            assertSpent(txB1);
            assertDead(txB2);
            assertSpent(txC1);
            assertDead(txC2);
            assertPending(txD1);
            assertDead(txD2);
            // A reorg: previous block "replaced" by new block containing txB1
            FakeTxBuilder.BlockPair blockPair4 = FakeTxBuilder.createFakeBlock(blockStore, blockPair0.storedBlock, 2, txB1);
            wallet.receiveFromBlock(txB1, blockPair4.storedBlock, SIDE_CHAIN, 0);
            wallet.reorganize(blockPair0.storedBlock, Lists.newArrayList(blockPair3.storedBlock), Lists.newArrayList(blockPair4.storedBlock));
            assertPending(txA1);
            assertDead(txA2);
            assertDead(txA3);
            assertSpent(txB1);
            assertDead(txB2);
            assertPending(txC1);
            assertDead(txC2);
            assertPending(txD1);
            assertDead(txD2);
            // A reorg: previous block "replaced" by new block containing txA2
            FakeTxBuilder.BlockPair blockPair5 = FakeTxBuilder.createFakeBlock(blockStore, blockPair0.storedBlock, 2, txA2);
            wallet.receiveFromBlock(txA2, blockPair5.storedBlock, SIDE_CHAIN, 0);
            wallet.reorganize(blockPair0.storedBlock, Lists.newArrayList(blockPair4.storedBlock), Lists.newArrayList(blockPair5.storedBlock));
            assertDead(txA1);
            assertUnspent(txA2);
            assertDead(txA3);
            assertPending(txB1);
            assertDead(txB2);
            assertDead(txC1);
            assertDead(txC2);
            assertDead(txD1);
            assertDead(txD2);
            // A reorg: previous block "replaced" by new empty block
            FakeTxBuilder.BlockPair blockPair6 = FakeTxBuilder.createFakeBlock(blockStore, blockPair0.storedBlock, 2);
            wallet.reorganize(blockPair0.storedBlock, Lists.newArrayList(blockPair5.storedBlock), Lists.newArrayList(blockPair6.storedBlock));
            assertDead(txA1);
            assertPending(txA2);
            assertDead(txA3);
            assertPending(txB1);
            assertDead(txB2);
            assertDead(txC1);
            assertDead(txC2);
            assertDead(txD1);
            assertDead(txD2);
        } finally {
            wallet.setCoinSelector(originalCoinSelector);
        }
    }

    @Test
    public void doubleSpendWeReceive() throws Exception {
        FakeTxBuilder.DoubleSpends doubleSpends = FakeTxBuilder.createFakeDoubleSpendTxns(TestWithWallet.UNITTEST, myAddress);
        // doubleSpends.t1 spends to our wallet. doubleSpends.t2 double spends somewhere else.
        Transaction t1b = new Transaction(TestWithWallet.UNITTEST);
        TransactionOutput t1bo = new TransactionOutput(TestWithWallet.UNITTEST, t1b, valueOf(0, 50), OTHER_ADDRESS);
        t1b.addOutput(t1bo);
        t1b.addInput(doubleSpends.t1.getOutput(0));
        wallet.receivePending(doubleSpends.t1, null);
        wallet.receivePending(doubleSpends.t2, null);
        wallet.receivePending(t1b, null);
        assertInConflict(doubleSpends.t1);
        assertInConflict(doubleSpends.t1);
        assertInConflict(t1b);
        // Add a block to the block store. The rest of the blocks in this test will be on top of this one.
        FakeTxBuilder.BlockPair blockPair0 = FakeTxBuilder.createFakeBlock(blockStore, 1);
        // A block was mined including doubleSpends.t1
        FakeTxBuilder.BlockPair blockPair1 = FakeTxBuilder.createFakeBlock(blockStore, 2, doubleSpends.t1);
        wallet.receiveFromBlock(doubleSpends.t1, blockPair1.storedBlock, BEST_CHAIN, 0);
        wallet.notifyNewBestBlock(blockPair1.storedBlock);
        assertSpent(doubleSpends.t1);
        assertDead(doubleSpends.t2);
        assertPending(t1b);
        // A reorg: previous block "replaced" by new block containing doubleSpends.t2
        FakeTxBuilder.BlockPair blockPair2 = FakeTxBuilder.createFakeBlock(blockStore, blockPair0.storedBlock, 2, doubleSpends.t2);
        wallet.receiveFromBlock(doubleSpends.t2, blockPair2.storedBlock, SIDE_CHAIN, 0);
        wallet.reorganize(blockPair0.storedBlock, Lists.newArrayList(blockPair1.storedBlock), Lists.newArrayList(blockPair2.storedBlock));
        assertDead(doubleSpends.t1);
        assertSpent(doubleSpends.t2);
        assertDead(t1b);
    }

    @Test
    public void doubleSpendForBuildingTx() throws Exception {
        CoinSelector originalCoinSelector = wallet.getCoinSelector();
        try {
            wallet.allowSpendingUnconfirmedTransactions();
            sendMoneyToWallet(BEST_CHAIN, valueOf(2, 0));
            Transaction send1 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 0)));
            Transaction send2 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 20)));
            sendMoneyToWallet(BEST_CHAIN, send1);
            assertUnspent(send1);
            wallet.receivePending(send2, null);
            assertUnspent(send1);
            assertDead(send2);
        } finally {
            wallet.setCoinSelector(originalCoinSelector);
        }
    }

    @Test
    public void txSpendingDeadTx() throws Exception {
        CoinSelector originalCoinSelector = wallet.getCoinSelector();
        try {
            wallet.allowSpendingUnconfirmedTransactions();
            sendMoneyToWallet(BEST_CHAIN, valueOf(2, 0));
            Transaction send1 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 0)));
            Transaction send2 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 20)));
            wallet.commitTx(send1);
            assertPending(send1);
            Transaction send1b = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 50)));
            sendMoneyToWallet(BEST_CHAIN, send2);
            assertDead(send1);
            assertUnspent(send2);
            wallet.receivePending(send1b, null);
            assertDead(send1);
            assertUnspent(send2);
            assertDead(send1b);
        } finally {
            wallet.setCoinSelector(originalCoinSelector);
        }
    }

    @Test
    public void testAddTransactionsDependingOn() throws Exception {
        CoinSelector originalCoinSelector = wallet.getCoinSelector();
        try {
            wallet.allowSpendingUnconfirmedTransactions();
            sendMoneyToWallet(BEST_CHAIN, valueOf(2, 0));
            Transaction send1 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 0)));
            Transaction send2 = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 20)));
            wallet.commitTx(send1);
            Transaction send1b = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 50)));
            wallet.commitTx(send1b);
            Transaction send1c = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 25)));
            wallet.commitTx(send1c);
            wallet.commitTx(send2);
            Set<Transaction> txns = new HashSet<>();
            txns.add(send1);
            wallet.addTransactionsDependingOn(txns, wallet.getTransactions(true));
            Assert.assertEquals(3, txns.size());
            Assert.assertTrue(txns.contains(send1));
            Assert.assertTrue(txns.contains(send1b));
            Assert.assertTrue(txns.contains(send1c));
        } finally {
            wallet.setCoinSelector(originalCoinSelector);
        }
    }

    @Test
    public void sortTxnsByDependency() throws Exception {
        CoinSelector originalCoinSelector = wallet.getCoinSelector();
        try {
            wallet.allowSpendingUnconfirmedTransactions();
            Transaction send1 = sendMoneyToWallet(BEST_CHAIN, valueOf(2, 0));
            Transaction send1a = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(1, 0)));
            wallet.commitTx(send1a);
            Transaction send1b = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 50)));
            wallet.commitTx(send1b);
            Transaction send1c = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 25)));
            wallet.commitTx(send1c);
            Transaction send1d = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 12)));
            wallet.commitTx(send1d);
            Transaction send1e = Preconditions.checkNotNull(wallet.createSend(OTHER_ADDRESS, valueOf(0, 6)));
            wallet.commitTx(send1e);
            Transaction send2 = sendMoneyToWallet(BEST_CHAIN, valueOf(200, 0));
            SendRequest req2a = SendRequest.to(OTHER_ADDRESS, valueOf(100, 0));
            req2a.tx.addInput(send2.getOutput(0));
            req2a.shuffleOutputs = false;
            wallet.completeTx(req2a);
            Transaction send2a = req2a.tx;
            SendRequest req2b = SendRequest.to(OTHER_ADDRESS, valueOf(50, 0));
            req2b.tx.addInput(send2a.getOutput(1));
            req2b.shuffleOutputs = false;
            wallet.completeTx(req2b);
            Transaction send2b = req2b.tx;
            SendRequest req2c = SendRequest.to(OTHER_ADDRESS, valueOf(25, 0));
            req2c.tx.addInput(send2b.getOutput(1));
            req2c.shuffleOutputs = false;
            wallet.completeTx(req2c);
            Transaction send2c = req2c.tx;
            Set<Transaction> unsortedTxns = new HashSet<>();
            unsortedTxns.add(send1a);
            unsortedTxns.add(send1b);
            unsortedTxns.add(send1c);
            unsortedTxns.add(send1d);
            unsortedTxns.add(send1e);
            unsortedTxns.add(send2a);
            unsortedTxns.add(send2b);
            unsortedTxns.add(send2c);
            List<Transaction> sortedTxns = wallet.sortTxnsByDependency(unsortedTxns);
            Assert.assertEquals(8, sortedTxns.size());
            Assert.assertTrue(((sortedTxns.indexOf(send1a)) < (sortedTxns.indexOf(send1b))));
            Assert.assertTrue(((sortedTxns.indexOf(send1b)) < (sortedTxns.indexOf(send1c))));
            Assert.assertTrue(((sortedTxns.indexOf(send1c)) < (sortedTxns.indexOf(send1d))));
            Assert.assertTrue(((sortedTxns.indexOf(send1d)) < (sortedTxns.indexOf(send1e))));
            Assert.assertTrue(((sortedTxns.indexOf(send2a)) < (sortedTxns.indexOf(send2b))));
            Assert.assertTrue(((sortedTxns.indexOf(send2b)) < (sortedTxns.indexOf(send2c))));
        } finally {
            wallet.setCoinSelector(originalCoinSelector);
        }
    }

    @Test
    public void pending1() throws Exception {
        // Check that if we receive a pending transaction that is then confirmed, we are notified as appropriate.
        final Coin nanos = COIN;
        final Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, nanos, myAddress);
        // First one is "called" second is "pending".
        final boolean[] flags = new boolean[2];
        final Transaction[] notifiedTx = new Transaction[1];
        final int[] walletChanged = new int[1];
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                // Check we got the expected transaction.
                Assert.assertEquals(tx, t1);
                // Check that it's considered to be pending inclusion in the block chain.
                Assert.assertEquals(prevBalance, ZERO);
                Assert.assertEquals(newBalance, nanos);
                flags[0] = true;
                flags[1] = tx.isPending();
                notifiedTx[0] = tx;
            }
        });
        wallet.addChangeEventListener(new WalletChangeEventListener() {
            @Override
            public void onWalletChanged(Wallet wallet) {
                (walletChanged[0])++;
            }
        });
        if (wallet.isPendingTransactionRelevant(t1))
            wallet.receivePending(t1, null);

        Threading.waitForUserCode();
        Assert.assertTrue(flags[0]);
        Assert.assertTrue(flags[1]);// is pending

        flags[0] = false;
        // Check we don't get notified if we receive it again.
        Assert.assertFalse(wallet.isPendingTransactionRelevant(t1));
        Assert.assertFalse(flags[0]);
        // Now check again, that we should NOT be notified when we receive it via a block (we were already notified).
        // However the confidence should be updated.
        // Make a fresh copy of the tx to ensure we're testing realistically.
        flags[0] = flags[1] = false;
        final TransactionConfidence[] reasons = new TransactionConfidence.Listener.ChangeReason[1];
        notifiedTx[0].getConfidence().addEventListener(new TransactionConfidence.Listener() {
            @Override
            public void onConfidenceChanged(TransactionConfidence confidence, TransactionConfidence.Listener.ChangeReason reason) {
                flags[1] = true;
                reasons[0] = reason;
            }
        });
        Assert.assertEquals(TransactionConfidence.ConfidenceType.PENDING, notifiedTx[0].getConfidence().getConfidenceType());
        // Send a block with nothing interesting. Verify we don't get a callback.
        sendMoneyToWallet(BEST_CHAIN);
        Threading.waitForUserCode();
        Assert.assertNull(reasons[0]);
        final Transaction t1Copy = TestWithWallet.UNITTEST.getDefaultSerializer().makeTransaction(t1.bitcoinSerialize());
        sendMoneyToWallet(BEST_CHAIN, t1Copy);
        Threading.waitForUserCode();
        Assert.assertFalse(flags[0]);
        Assert.assertTrue(flags[1]);
        Assert.assertEquals(BUILDING, notifiedTx[0].getConfidence().getConfidenceType());
        // Check we don't get notified about an irrelevant transaction.
        flags[0] = false;
        flags[1] = false;
        Transaction irrelevant = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, nanos, OTHER_ADDRESS);
        if (wallet.isPendingTransactionRelevant(irrelevant))
            wallet.receivePending(irrelevant, null);

        Threading.waitForUserCode();
        Assert.assertFalse(flags[0]);
        Assert.assertEquals(3, walletChanged[0]);
    }

    @Test
    public void pending2() throws Exception {
        // Check that if we receive a pending tx we did not send, it updates our spent flags correctly.
        final Transaction[] txn = new Transaction[1];
        final Coin[] bigints = new Coin[2];
        wallet.addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                txn[0] = tx;
                bigints[0] = prevBalance;
                bigints[1] = newBalance;
            }
        });
        // Receive some coins.
        Coin nanos = COIN;
        sendMoneyToWallet(BEST_CHAIN, nanos);
        // Create a spend with them, but don't commit it (ie it's from somewhere else but using our keys). This TX
        // will have change as we don't spend our entire balance.
        Coin halfNanos = valueOf(0, 50);
        Transaction t2 = wallet.createSend(OTHER_ADDRESS, halfNanos);
        // Now receive it as pending.
        if (wallet.isPendingTransactionRelevant(t2))
            wallet.receivePending(t2, null);

        // We received an onCoinsSent() callback.
        Threading.waitForUserCode();
        Assert.assertEquals(t2, txn[0]);
        Assert.assertEquals(nanos, bigints[0]);
        Assert.assertEquals(halfNanos, bigints[1]);
        // Our balance is now 0.50 BTC
        Assert.assertEquals(halfNanos, wallet.getBalance(ESTIMATED));
    }

    @Test
    public void pending3() throws Exception {
        // Check that if we receive a pending tx, and it's overridden by a double spend from the best chain, we
        // are notified that it's dead. This should work even if the pending tx inputs are NOT ours, ie, they don't
        // connect to anything.
        Coin nanos = COIN;
        // Create two transactions that share the same input tx.
        Address badGuy = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        Transaction doubleSpentTx = new Transaction(TestWithWallet.UNITTEST);
        TransactionOutput doubleSpentOut = new TransactionOutput(TestWithWallet.UNITTEST, doubleSpentTx, nanos, badGuy);
        doubleSpentTx.addOutput(doubleSpentOut);
        Transaction t1 = new Transaction(TestWithWallet.UNITTEST);
        TransactionOutput o1 = new TransactionOutput(TestWithWallet.UNITTEST, t1, nanos, myAddress);
        t1.addOutput(o1);
        t1.addInput(doubleSpentOut);
        Transaction t2 = new Transaction(TestWithWallet.UNITTEST);
        TransactionOutput o2 = new TransactionOutput(TestWithWallet.UNITTEST, t2, nanos, badGuy);
        t2.addOutput(o2);
        t2.addInput(doubleSpentOut);
        final Transaction[] called = new Transaction[2];
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                called[0] = tx;
            }
        });
        wallet.addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                if ((tx.getConfidence().getConfidenceType()) == (ConfidenceType.DEAD)) {
                    called[0] = tx;
                    called[1] = tx.getConfidence().getOverridingTransaction();
                }
            }
        });
        Assert.assertEquals(ZERO, wallet.getBalance());
        if (wallet.isPendingTransactionRelevant(t1))
            wallet.receivePending(t1, null);

        Threading.waitForUserCode();
        Assert.assertEquals(t1, called[0]);
        Assert.assertEquals(nanos, wallet.getBalance(ESTIMATED));
        // Now receive a double spend on the best chain.
        called[0] = called[1] = null;
        sendMoneyToWallet(BEST_CHAIN, t2);
        Threading.waitForUserCode();
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(t1, called[0]);// dead

        Assert.assertEquals(t2, called[1]);// replacement

    }

    @Test
    public void transactionsList() throws Exception {
        // Check the wallet can give us an ordered list of all received transactions.
        Utils.setMockClock();
        Transaction tx1 = sendMoneyToWallet(BEST_CHAIN, COIN);
        Utils.rollMockClock((60 * 10));
        Transaction tx2 = sendMoneyToWallet(BEST_CHAIN, valueOf(0, 5));
        // Check we got them back in order.
        List<Transaction> transactions = wallet.getTransactionsByTime();
        Assert.assertEquals(tx2, transactions.get(0));
        Assert.assertEquals(tx1, transactions.get(1));
        Assert.assertEquals(2, transactions.size());
        // Check we get only the last transaction if we request a subrange.
        transactions = wallet.getRecentTransactions(1, false);
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(tx2, transactions.get(0));
        // Create a spend five minutes later.
        Utils.rollMockClock((60 * 5));
        Transaction tx3 = wallet.createSend(OTHER_ADDRESS, valueOf(0, 5));
        // Does not appear in list yet.
        Assert.assertEquals(2, wallet.getTransactionsByTime().size());
        wallet.commitTx(tx3);
        // Now it does.
        transactions = wallet.getTransactionsByTime();
        Assert.assertEquals(3, transactions.size());
        Assert.assertEquals(tx3, transactions.get(0));
        // Verify we can handle the case of older wallets in which the timestamp is null (guessed from the
        // block appearances list).
        tx1.setUpdateTime(null);
        tx3.setUpdateTime(null);
        // Check we got them back in order.
        transactions = wallet.getTransactionsByTime();
        Assert.assertEquals(tx2, transactions.get(0));
        Assert.assertEquals(3, transactions.size());
    }

    @Test
    public void keyCreationTime() throws Exception {
        Utils.setMockClock();
        long now = Utils.currentTimeSeconds();
        wallet = new Wallet(TestWithWallet.UNITTEST);
        Assert.assertEquals(now, wallet.getEarliestKeyCreationTime());
        Utils.rollMockClock(60);
        wallet.freshReceiveKey();
        Assert.assertEquals(now, wallet.getEarliestKeyCreationTime());
    }

    @Test
    public void scriptCreationTime() throws Exception {
        Utils.setMockClock();
        long now = Utils.currentTimeSeconds();
        wallet = new Wallet(TestWithWallet.UNITTEST);
        Assert.assertEquals(now, wallet.getEarliestKeyCreationTime());
        Utils.rollMockClock((-120));
        wallet.addWatchedAddress(OTHER_ADDRESS);
        wallet.freshReceiveKey();
        Assert.assertEquals((now - 120), wallet.getEarliestKeyCreationTime());
    }

    @Test
    public void spendToSameWallet() throws Exception {
        // Test that a spend to the same wallet is dealt with correctly.
        // It should appear in the wallet and confirm.
        // This is a bit of a silly thing to do in the real world as all it does is burn a fee but it is perfectly valid.
        Coin coin1 = COIN;
        Coin coinHalf = valueOf(0, 50);
        // Start by giving us 1 coin.
        sendMoneyToWallet(BEST_CHAIN, coin1);
        // Send half to ourselves. We should then have a balance available to spend of zero.
        Assert.assertEquals(1, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(1, wallet.getTransactions(true).size());
        Transaction outbound1 = wallet.createSend(myAddress, coinHalf);
        wallet.commitTx(outbound1);
        // We should have a zero available balance before the next block.
        Assert.assertEquals(ZERO, wallet.getBalance());
        sendMoneyToWallet(BEST_CHAIN, outbound1);
        // We should have a balance of 1 BTC after the block is received.
        Assert.assertEquals(coin1, wallet.getBalance());
    }

    @Test
    public void lastBlockSeen() throws Exception {
        Coin v1 = valueOf(5, 0);
        Coin v2 = valueOf(0, 50);
        Coin v3 = valueOf(0, 25);
        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, v1, myAddress);
        Transaction t2 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, v2, myAddress);
        Transaction t3 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, v3, myAddress);
        Block genesis = blockStore.getChainHead().getHeader();
        Block b10 = FakeTxBuilder.makeSolvedTestBlock(genesis, t1);
        Block b11 = FakeTxBuilder.makeSolvedTestBlock(genesis, t2);
        Block b2 = FakeTxBuilder.makeSolvedTestBlock(b10, t3);
        Block b3 = FakeTxBuilder.makeSolvedTestBlock(b2);
        // Receive a block on the best chain - this should set the last block seen hash.
        chain.add(b10);
        Assert.assertEquals(b10.getHash(), wallet.getLastBlockSeenHash());
        Assert.assertEquals(b10.getTimeSeconds(), wallet.getLastBlockSeenTimeSecs());
        Assert.assertEquals(1, wallet.getLastBlockSeenHeight());
        // Receive a block on the side chain - this should not change the last block seen hash.
        chain.add(b11);
        Assert.assertEquals(b10.getHash(), wallet.getLastBlockSeenHash());
        // Receive block 2 on the best chain - this should change the last block seen hash.
        chain.add(b2);
        Assert.assertEquals(b2.getHash(), wallet.getLastBlockSeenHash());
        // Receive block 3 on the best chain - this should change the last block seen hash despite having no txns.
        chain.add(b3);
        Assert.assertEquals(b3.getHash(), wallet.getLastBlockSeenHash());
    }

    @Test
    public void pubkeyOnlyScripts() throws Exception {
        // Verify that we support outputs like OP_PUBKEY and the corresponding inputs.
        ECKey key1 = wallet.freshReceiveKey();
        Coin value = valueOf(5, 0);
        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, value, key1);
        if (wallet.isPendingTransactionRelevant(t1))
            wallet.receivePending(t1, null);

        // TX should have been seen as relevant.
        Assert.assertEquals(value, wallet.getBalance(ESTIMATED));
        Assert.assertEquals(ZERO, wallet.getBalance(AVAILABLE));
        sendMoneyToWallet(BEST_CHAIN, t1);
        // TX should have been seen as relevant, extracted and processed.
        Assert.assertEquals(value, wallet.getBalance(AVAILABLE));
        // Spend it and ensure we can spend the <key> OP_CHECKSIG output correctly.
        Transaction t2 = wallet.createSend(OTHER_ADDRESS, value);
        Assert.assertNotNull(t2);
        // TODO: This code is messy, improve the Script class and fixinate!
        Assert.assertEquals(t2.toString(), 1, t2.getInputs().get(0).getScriptSig().getChunks().size());
        Assert.assertTrue(((t2.getInputs().get(0).getScriptSig().getChunks().get(0).data.length) > 50));
    }

    @Test
    public void isWatching() {
        Assert.assertFalse(wallet.isWatching());
        Wallet watchingWallet = Wallet.fromWatchingKey(TestWithWallet.UNITTEST, wallet.getWatchingKey().dropPrivateBytes().dropParent(), P2PKH);
        Assert.assertTrue(watchingWallet.isWatching());
        wallet.encrypt(WalletTest.PASSWORD1);
        Assert.assertFalse(wallet.isWatching());
    }

    @Test
    public void watchingWallet() throws Exception {
        DeterministicKey watchKey = wallet.getWatchingKey();
        String serialized = watchKey.serializePubB58(TestWithWallet.UNITTEST);
        // Construct watching wallet.
        Wallet watchingWallet = Wallet.fromWatchingKey(TestWithWallet.UNITTEST, DeterministicKey.deserializeB58(null, serialized, TestWithWallet.UNITTEST), P2PKH);
        DeterministicKey key2 = watchingWallet.freshReceiveKey();
        Assert.assertEquals(myKey, key2);
        ECKey key = wallet.freshKey(CHANGE);
        key2 = watchingWallet.freshKey(CHANGE);
        Assert.assertEquals(key, key2);
        key.sign(ZERO_HASH);
        try {
            key2.sign(ZERO_HASH);
            Assert.fail();
        } catch (ECKey e) {
            // Expected
        }
        receiveATransaction(watchingWallet, LegacyAddress.fromKey(TestWithWallet.UNITTEST, myKey));
        Assert.assertEquals(COIN, watchingWallet.getBalance());
        Assert.assertEquals(COIN, watchingWallet.getBalance(AVAILABLE));
        Assert.assertEquals(ZERO, watchingWallet.getBalance(AVAILABLE_SPENDABLE));
    }

    @Test(expected = MissingPrivateKeyException.class)
    public void watchingWalletWithCreationTime() throws Exception {
        DeterministicKey watchKey = wallet.getWatchingKey();
        String serialized = watchKey.serializePubB58(TestWithWallet.UNITTEST);
        Wallet watchingWallet = Wallet.fromWatchingKeyB58(TestWithWallet.UNITTEST, serialized, 1415282801);
        DeterministicKey key2 = watchingWallet.freshReceiveKey();
        Assert.assertEquals(myKey, key2);
        ECKey key = wallet.freshKey(CHANGE);
        key2 = watchingWallet.freshKey(CHANGE);
        Assert.assertEquals(key, key2);
        key.sign(ZERO_HASH);
        key2.sign(ZERO_HASH);
    }

    @Test
    public void watchingScripts() throws Exception {
        // Verify that pending transactions to watched addresses are relevant
        Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        wallet.addWatchedAddress(watchedAddress);
        Coin value = valueOf(5, 0);
        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, value, watchedAddress);
        Assert.assertTrue(((t1.getWalletOutputs(wallet).size()) >= 1));
        Assert.assertTrue(wallet.isPendingTransactionRelevant(t1));
    }

    @Test(expected = InsufficientMoneyException.class)
    public void watchingScriptsConfirmed() throws Exception {
        Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        wallet.addWatchedAddress(watchedAddress);
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, CENT, watchedAddress);
        Assert.assertEquals(CENT, wallet.getBalance());
        // We can't spend watched balances
        wallet.createSend(OTHER_ADDRESS, CENT);
    }

    @Test
    public void watchingScriptsSentFrom() throws Exception {
        int baseElements = wallet.getBloomFilterElementCount();
        Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        wallet.addWatchedAddress(watchedAddress);
        Assert.assertEquals((baseElements + 1), wallet.getBloomFilterElementCount());
        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, watchedAddress);
        Transaction t2 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, OTHER_ADDRESS);
        sendMoneyToWallet(BEST_CHAIN, t1);
        Assert.assertEquals((baseElements + 2), wallet.getBloomFilterElementCount());
        Transaction st2 = new Transaction(TestWithWallet.UNITTEST);
        st2.addOutput(CENT, OTHER_ADDRESS);
        st2.addOutput(COIN, OTHER_ADDRESS);
        st2.addInput(t1.getOutput(0));
        st2.addInput(t2.getOutput(0));
        sendMoneyToWallet(BEST_CHAIN, st2);
        Assert.assertEquals((baseElements + 2), wallet.getBloomFilterElementCount());
        Assert.assertEquals(CENT, st2.getValueSentFromMe(wallet));
    }

    @Test
    public void watchingScriptsBloomFilter() throws Exception {
        Assert.assertFalse(wallet.isRequiringUpdateAllBloomFilter());
        Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, watchedAddress);
        TransactionOutPoint outPoint = new TransactionOutPoint(TestWithWallet.UNITTEST, 0, t1);
        wallet.addWatchedAddress(watchedAddress);
        Assert.assertTrue(wallet.isRequiringUpdateAllBloomFilter());
        // Note that this has a 1e-12 chance of failing this unit test due to a false positive
        Assert.assertFalse(wallet.getBloomFilter(1.0E-12).contains(outPoint.unsafeBitcoinSerialize()));
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, t1);
        Assert.assertTrue(wallet.getBloomFilter(1.0E-12).contains(outPoint.unsafeBitcoinSerialize()));
    }

    @Test
    public void getWatchedAddresses() throws Exception {
        Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        wallet.addWatchedAddress(watchedAddress);
        List<Address> watchedAddresses = wallet.getWatchedAddresses();
        Assert.assertEquals(1, watchedAddresses.size());
        Assert.assertEquals(watchedAddress, watchedAddresses.get(0));
    }

    @Test
    public void removeWatchedAddresses() {
        List<Address> addressesForRemoval = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
            addressesForRemoval.add(watchedAddress);
            wallet.addWatchedAddress(watchedAddress);
        }
        wallet.removeWatchedAddresses(addressesForRemoval);
        for (Address addr : addressesForRemoval)
            Assert.assertFalse(wallet.isAddressWatched(addr));

        Assert.assertFalse(wallet.isRequiringUpdateAllBloomFilter());
    }

    @Test
    public void removeWatchedAddress() {
        Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
        wallet.addWatchedAddress(watchedAddress);
        wallet.removeWatchedAddress(watchedAddress);
        Assert.assertFalse(wallet.isAddressWatched(watchedAddress));
        Assert.assertFalse(wallet.isRequiringUpdateAllBloomFilter());
    }

    @Test
    public void removeScriptsBloomFilter() throws Exception {
        List<Address> addressesForRemoval = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Address watchedAddress = LegacyAddress.fromKey(TestWithWallet.UNITTEST, new ECKey());
            addressesForRemoval.add(watchedAddress);
            wallet.addWatchedAddress(watchedAddress);
        }
        wallet.removeWatchedAddresses(addressesForRemoval);
        for (Address addr : addressesForRemoval) {
            Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, addr);
            TransactionOutPoint outPoint = new TransactionOutPoint(TestWithWallet.UNITTEST, 0, t1);
            // Note that this has a 1e-12 chance of failing this unit test due to a false positive
            Assert.assertFalse(wallet.getBloomFilter(1.0E-12).contains(outPoint.unsafeBitcoinSerialize()));
            sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, t1);
            Assert.assertFalse(wallet.getBloomFilter(1.0E-12).contains(outPoint.unsafeBitcoinSerialize()));
        }
    }

    @Test
    public void marriedKeychainBloomFilter() throws Exception {
        createMarriedWallet(2, 2);
        Address address = wallet.currentReceiveAddress();
        Assert.assertTrue(wallet.getBloomFilter(0.001).contains(address.getHash()));
        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, address);
        TransactionOutPoint outPoint = new TransactionOutPoint(TestWithWallet.UNITTEST, 0, t1);
        Assert.assertFalse(wallet.getBloomFilter(0.001).contains(outPoint.unsafeBitcoinSerialize()));
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, t1);
        Assert.assertTrue(wallet.getBloomFilter(0.001).contains(outPoint.unsafeBitcoinSerialize()));
    }

    @Test
    public void autosaveImmediate() throws Exception {
        // Test that the wallet will save itself automatically when it changes.
        File f = File.createTempFile("bitcoinj-unit-test", null);
        Sha256Hash hash1 = Sha256Hash.of(f);
        // Start with zero delay and ensure the wallet file changes after adding a key.
        wallet.autosaveToFile(f, 0, TimeUnit.SECONDS, null);
        ECKey key = wallet.freshReceiveKey();
        Sha256Hash hash2 = Sha256Hash.of(f);
        Assert.assertFalse("Wallet not saved after generating fresh key", hash1.equals(hash2));// File has changed.

        Transaction t1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, valueOf(5, 0), key);
        if (wallet.isPendingTransactionRelevant(t1))
            wallet.receivePending(t1, null);

        Sha256Hash hash3 = Sha256Hash.of(f);
        Assert.assertFalse("Wallet not saved after receivePending", hash2.equals(hash3));// File has changed again.

    }

    @Test
    public void autosaveDelayed() throws Exception {
        // Test that the wallet will save itself automatically when it changes, but not immediately and near-by
        // updates are coalesced together. This test is a bit racy, it assumes we can complete the unit test within
        // an auto-save cycle of 1 second.
        final File[] results = new File[2];
        final CountDownLatch latch = new CountDownLatch(3);
        File f = File.createTempFile("bitcoinj-unit-test", null);
        Sha256Hash hash1 = Sha256Hash.of(f);
        wallet.autosaveToFile(f, 1, TimeUnit.SECONDS, new WalletFiles.Listener() {
            @Override
            public void onBeforeAutoSave(File tempFile) {
                results[0] = tempFile;
            }

            @Override
            public void onAfterAutoSave(File newlySavedFile) {
                results[1] = newlySavedFile;
                latch.countDown();
            }
        });
        ECKey key = wallet.freshReceiveKey();
        Sha256Hash hash2 = Sha256Hash.of(f);
        Assert.assertFalse(hash1.equals(hash2));// File has changed immediately despite the delay, as keys are important.

        Assert.assertNotNull(results[0]);
        Assert.assertEquals(f, results[1]);
        results[0] = results[1] = null;
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN);
        Sha256Hash hash3 = Sha256Hash.of(f);
        Assert.assertEquals(hash2, hash3);// File has NOT changed yet. Just new blocks with no txns - delayed.

        Assert.assertNull(results[0]);
        Assert.assertNull(results[1]);
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, valueOf(5, 0), key);
        Sha256Hash hash4 = Sha256Hash.of(f);
        Assert.assertFalse(hash3.equals(hash4));// File HAS changed.

        results[0] = results[1] = null;
        // A block that contains some random tx we don't care about.
        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, COIN, OTHER_ADDRESS);
        Assert.assertEquals(hash4, Sha256Hash.of(f));// File has NOT changed.

        Assert.assertNull(results[0]);
        Assert.assertNull(results[1]);
        // Wait for an auto-save to occur.
        latch.await();
        Sha256Hash hash5 = Sha256Hash.of(f);
        Assert.assertFalse(hash4.equals(hash5));// File has now changed.

        Assert.assertNotNull(results[0]);
        Assert.assertEquals(f, results[1]);
        // Now we shutdown auto-saving and expect wallet changes to remain unsaved, even "important" changes.
        wallet.shutdownAutosaveAndWait();
        results[0] = results[1] = null;
        ECKey key2 = new ECKey();
        wallet.importKey(key2);
        Assert.assertEquals(hash5, Sha256Hash.of(f));// File has NOT changed.

        sendMoneyToWallet(BlockChain.NewBlockType.BEST_CHAIN, valueOf(5, 0), key2);
        Thread.sleep(2000);// Wait longer than autosave delay. TODO Fix the racyness.

        Assert.assertEquals(hash5, Sha256Hash.of(f));// File has still NOT changed.

        Assert.assertNull(results[0]);
        Assert.assertNull(results[1]);
    }

    @Test
    public void spendOutputFromPendingTransaction() throws Exception {
        // We'll set up a wallet that receives a coin, then sends a coin of lesser value and keeps the change.
        Coin v1 = COIN;
        sendMoneyToWallet(BEST_CHAIN, v1);
        // First create our current transaction
        ECKey k2 = wallet.freshReceiveKey();
        Coin v2 = valueOf(0, 50);
        Transaction t2 = new Transaction(TestWithWallet.UNITTEST);
        TransactionOutput o2 = new TransactionOutput(TestWithWallet.UNITTEST, t2, v2, LegacyAddress.fromKey(TestWithWallet.UNITTEST, k2));
        t2.addOutput(o2);
        SendRequest req = SendRequest.forTx(t2);
        wallet.completeTx(req);
        // Commit t2, so it is placed in the pending pool
        wallet.commitTx(t2);
        Assert.assertEquals(0, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(1, wallet.getPoolSize(PENDING));
        Assert.assertEquals(2, wallet.getTransactions(true).size());
        // Now try to the spend the output.
        ECKey k3 = new ECKey();
        Coin v3 = valueOf(0, 25);
        Transaction t3 = new Transaction(TestWithWallet.UNITTEST);
        t3.addOutput(v3, LegacyAddress.fromKey(TestWithWallet.UNITTEST, k3));
        t3.addInput(o2);
        wallet.signTransaction(SendRequest.forTx(t3));
        // Commit t3, so the coins from the pending t2 are spent
        wallet.commitTx(t3);
        Assert.assertEquals(0, wallet.getPoolSize(UNSPENT));
        Assert.assertEquals(2, wallet.getPoolSize(PENDING));
        Assert.assertEquals(3, wallet.getTransactions(true).size());
        // Now the output of t2 must not be available for spending
        Assert.assertFalse(o2.isAvailableForSpending());
    }

    @Test
    public void replayWhilstPending() throws Exception {
        // Check that if a pending transaction spends outputs of chain-included transactions, we mark them as spent.
        // See bug 345. This can happen if there is a pending transaction floating around and then you replay the
        // chain without emptying the memory pool (or refilling it from a peer).
        Coin value = COIN;
        Transaction tx1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, value, myAddress);
        Transaction tx2 = new Transaction(TestWithWallet.UNITTEST);
        tx2.addInput(tx1.getOutput(0));
        tx2.addOutput(valueOf(0, 9), OTHER_ADDRESS);
        // Add a change address to ensure this tx is relevant.
        tx2.addOutput(CENT, wallet.currentChangeAddress());
        wallet.receivePending(tx2, null);
        sendMoneyToWallet(BEST_CHAIN, tx1);
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(1, wallet.getPoolSize(Pool.SPENT));
        Assert.assertEquals(1, wallet.getPoolSize(Pool.PENDING));
        Assert.assertEquals(0, wallet.getPoolSize(Pool.UNSPENT));
    }

    @Test
    public void outOfOrderPendingTxns() throws Exception {
        // Check that if there are two pending transactions which we receive out of order, they are marked as spent
        // correctly. For instance, we are watching a wallet, someone pays us (A) and we then pay someone else (B)
        // with a change address but the network delivers the transactions to us in order B then A.
        Coin value = COIN;
        Transaction a = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, value, myAddress);
        Transaction b = new Transaction(TestWithWallet.UNITTEST);
        b.addInput(a.getOutput(0));
        b.addOutput(CENT, OTHER_ADDRESS);
        Coin v = COIN.subtract(CENT);
        b.addOutput(v, wallet.currentChangeAddress());
        a = FakeTxBuilder.roundTripTransaction(TestWithWallet.UNITTEST, a);
        b = FakeTxBuilder.roundTripTransaction(TestWithWallet.UNITTEST, b);
        wallet.receivePending(b, null);
        Assert.assertEquals(v, wallet.getBalance(ESTIMATED));
        wallet.receivePending(a, null);
        Assert.assertEquals(v, wallet.getBalance(ESTIMATED));
    }

    @Test
    public void encryptionDecryptionAESBasic() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        KeyCrypter keyCrypter = encryptedWallet.getKeyCrypter();
        KeyParameter aesKey = keyCrypter.deriveKey(WalletTest.PASSWORD1);
        Assert.assertEquals(ENCRYPTED_SCRYPT_AES, encryptedWallet.getEncryptionType());
        Assert.assertTrue(encryptedWallet.checkPassword(WalletTest.PASSWORD1));
        Assert.assertTrue(encryptedWallet.checkAESKey(aesKey));
        Assert.assertFalse(encryptedWallet.checkPassword(WalletTest.WRONG_PASSWORD));
        Assert.assertNotNull("The keyCrypter is missing but should not be", keyCrypter);
        encryptedWallet.decrypt(aesKey);
        // Wallet should now be unencrypted.
        Assert.assertNull("Wallet is not an unencrypted wallet", encryptedWallet.getKeyCrypter());
        try {
            encryptedWallet.checkPassword(WalletTest.PASSWORD1);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void encryptionDecryptionPasswordBasic() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        Assert.assertTrue(encryptedWallet.isEncrypted());
        encryptedWallet.decrypt(WalletTest.PASSWORD1);
        Assert.assertFalse(encryptedWallet.isEncrypted());
        // Wallet should now be unencrypted.
        Assert.assertNull("Wallet is not an unencrypted wallet", encryptedWallet.getKeyCrypter());
        try {
            encryptedWallet.checkPassword(WalletTest.PASSWORD1);
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void encryptionDecryptionBadPassword() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        KeyCrypter keyCrypter = encryptedWallet.getKeyCrypter();
        KeyParameter wrongAesKey = keyCrypter.deriveKey(WalletTest.WRONG_PASSWORD);
        // Check the wallet is currently encrypted
        Assert.assertEquals("Wallet is not an encrypted wallet", ENCRYPTED_SCRYPT_AES, encryptedWallet.getEncryptionType());
        Assert.assertFalse(encryptedWallet.checkAESKey(wrongAesKey));
        // Check that the wrong password does not decrypt the wallet.
        try {
            encryptedWallet.decrypt(wrongAesKey);
            Assert.fail("Incorrectly decoded wallet with wrong password");
        } catch (KeyCrypterException ede) {
            // Expected.
        }
    }

    @Test
    public void changePasswordTest() {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        CharSequence newPassword = "My name is Tom";
        encryptedWallet.changeEncryptionPassword(WalletTest.PASSWORD1, newPassword);
        Assert.assertTrue(encryptedWallet.checkPassword(newPassword));
        Assert.assertFalse(encryptedWallet.checkPassword(WalletTest.WRONG_PASSWORD));
    }

    @Test
    public void changeAesKeyTest() {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        KeyCrypter keyCrypter = encryptedWallet.getKeyCrypter();
        KeyParameter aesKey = keyCrypter.deriveKey(WalletTest.PASSWORD1);
        CharSequence newPassword = "My name is Tom";
        KeyParameter newAesKey = keyCrypter.deriveKey(newPassword);
        encryptedWallet.changeEncryptionKey(keyCrypter, aesKey, newAesKey);
        Assert.assertTrue(encryptedWallet.checkAESKey(newAesKey));
        Assert.assertFalse(encryptedWallet.checkAESKey(aesKey));
    }

    @Test
    public void encryptionDecryptionCheckExceptions() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        KeyCrypter keyCrypter = encryptedWallet.getKeyCrypter();
        KeyParameter aesKey = keyCrypter.deriveKey(WalletTest.PASSWORD1);
        // Check the wallet is currently encrypted
        Assert.assertEquals("Wallet is not an encrypted wallet", ENCRYPTED_SCRYPT_AES, encryptedWallet.getEncryptionType());
        // Decrypt wallet.
        Assert.assertNotNull("The keyCrypter is missing but should not be", keyCrypter);
        encryptedWallet.decrypt(aesKey);
        // Try decrypting it again
        try {
            Assert.assertNotNull("The keyCrypter is missing but should not be", keyCrypter);
            encryptedWallet.decrypt(aesKey);
            Assert.fail("Should not be able to decrypt a decrypted wallet");
        } catch (IllegalStateException e) {
            // expected
        }
        Assert.assertNull("Wallet is not an unencrypted wallet", encryptedWallet.getKeyCrypter());
        // Encrypt wallet.
        encryptedWallet.encrypt(keyCrypter, aesKey);
        Assert.assertEquals("Wallet is not an encrypted wallet", ENCRYPTED_SCRYPT_AES, encryptedWallet.getEncryptionType());
        // Try encrypting it again
        try {
            encryptedWallet.encrypt(keyCrypter, aesKey);
            Assert.fail("Should not be able to encrypt an encrypted wallet");
        } catch (IllegalStateException e) {
            // expected
        }
        Assert.assertEquals("Wallet is not an encrypted wallet", ENCRYPTED_SCRYPT_AES, encryptedWallet.getEncryptionType());
    }

    @Test(expected = KeyCrypterException.class)
    public void addUnencryptedKeyToEncryptedWallet() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        ECKey key1 = new ECKey();
        encryptedWallet.importKey(key1);
    }

    @Test(expected = KeyCrypterException.class)
    public void addEncryptedKeyToUnencryptedWallet() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        KeyCrypter keyCrypter = encryptedWallet.getKeyCrypter();
        ECKey key1 = new ECKey();
        key1 = key1.encrypt(keyCrypter, keyCrypter.deriveKey("PASSWORD!"));
        wallet.importKey(key1);
    }

    @Test(expected = KeyCrypterException.class)
    public void mismatchedCrypter() throws Exception {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        KeyCrypter keyCrypter = encryptedWallet.getKeyCrypter();
        KeyParameter aesKey = keyCrypter.deriveKey(WalletTest.PASSWORD1);
        // Try added an ECKey that was encrypted with a differenct ScryptParameters (i.e. a non-homogenous key).
        // This is not allowed as the ScryptParameters is stored at the Wallet level.
        Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(KeyCrypterScrypt.randomSalt()));
        Protos.ScryptParameters scryptParameters = scryptParametersBuilder.build();
        KeyCrypter keyCrypterDifferent = new KeyCrypterScrypt(scryptParameters);
        ECKey ecKeyDifferent = new ECKey();
        ecKeyDifferent = ecKeyDifferent.encrypt(keyCrypterDifferent, aesKey);
        encryptedWallet.importKey(ecKeyDifferent);
    }

    @Test
    public void importAndEncrypt() throws InsufficientMoneyException {
        Wallet encryptedWallet = new Wallet(TestWithWallet.UNITTEST);
        encryptedWallet.encrypt(WalletTest.PASSWORD1);
        final ECKey key = new ECKey();
        encryptedWallet.importKeysAndEncrypt(ImmutableList.of(key), WalletTest.PASSWORD1);
        Assert.assertEquals(1, encryptedWallet.getImportedKeys().size());
        Assert.assertEquals(key.getPubKeyPoint(), encryptedWallet.getImportedKeys().get(0).getPubKeyPoint());
        sendMoneyToWallet(encryptedWallet, BEST_CHAIN, COIN, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key));
        Assert.assertEquals(COIN, encryptedWallet.getBalance());
        SendRequest req = SendRequest.emptyWallet(OTHER_ADDRESS);
        req.aesKey = Preconditions.checkNotNull(encryptedWallet.getKeyCrypter()).deriveKey(WalletTest.PASSWORD1);
        encryptedWallet.sendCoinsOffline(req);
    }

    @Test
    public void ageMattersDuringSelection() throws Exception {
        // Test that we prefer older coins to newer coins when building spends. This reduces required fees and improves
        // time to confirmation as the transaction will appear less spammy.
        final int ITERATIONS = 10;
        Transaction[] txns = new Transaction[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            txns[i] = sendMoneyToWallet(BEST_CHAIN, COIN);
        }
        // Check that we spend transactions in order of reception.
        for (int i = 0; i < ITERATIONS; i++) {
            Transaction spend = wallet.createSend(OTHER_ADDRESS, COIN);
            Assert.assertEquals(spend.getInputs().size(), 1);
            Assert.assertEquals(("Failed on iteration " + i), spend.getInput(0).getOutpoint().getHash(), txns[i].getTxId());
            wallet.commitTx(spend);
        }
    }

    @Test(expected = ExceededMaxTransactionSize.class)
    public void respectMaxStandardSize() throws Exception {
        // Check that we won't create txns > 100kb. Average tx size is ~220 bytes so this would have to be enormous.
        sendMoneyToWallet(BEST_CHAIN, valueOf(100, 0));
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        byte[] bits = new byte[20];
        new Random().nextBytes(bits);
        Coin v = CENT;
        // 3100 outputs to a random address.
        for (int i = 0; i < 3100; i++) {
            tx.addOutput(v, LegacyAddress.fromPubKeyHash(TestWithWallet.UNITTEST, bits));
        }
        SendRequest req = SendRequest.forTx(tx);
        wallet.completeTx(req);
    }

    @Test
    public void opReturnOneOutputTest() throws Exception {
        // Tests basic send of transaction with one output that doesn't transfer any value but just writes OP_RETURN.
        receiveATransaction(wallet, myAddress);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        Coin messagePrice = Coin.ZERO;
        Script script = ScriptBuilder.createOpReturnScript("hello world!".getBytes());
        tx.addOutput(messagePrice, script);
        SendRequest request = SendRequest.forTx(tx);
        request.ensureMinRequiredFee = true;
        wallet.completeTx(request);
    }

    @Test
    public void opReturnMaxBytes() throws Exception {
        receiveATransaction(wallet, myAddress);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        Script script = ScriptBuilder.createOpReturnScript(new byte[80]);
        tx.addOutput(Coin.ZERO, script);
        SendRequest request = SendRequest.forTx(tx);
        request.ensureMinRequiredFee = true;
        wallet.completeTx(request);
    }

    @Test
    public void opReturnOneOutputWithValueTest() throws Exception {
        // Tests basic send of transaction with one output that destroys coins and has an OP_RETURN.
        receiveATransaction(wallet, myAddress);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        Coin messagePrice = CENT;
        Script script = ScriptBuilder.createOpReturnScript("hello world!".getBytes());
        tx.addOutput(messagePrice, script);
        SendRequest request = SendRequest.forTx(tx);
        wallet.completeTx(request);
    }

    @Test
    public void opReturnTwoOutputsTest() throws Exception {
        // Tests sending transaction where one output transfers BTC, the other one writes OP_RETURN.
        receiveATransaction(wallet, myAddress);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        Coin messagePrice = Coin.ZERO;
        Script script = ScriptBuilder.createOpReturnScript("hello world!".getBytes());
        tx.addOutput(CENT, OTHER_ADDRESS);
        tx.addOutput(messagePrice, script);
        SendRequest request = SendRequest.forTx(tx);
        wallet.completeTx(request);
    }

    @Test(expected = MultipleOpReturnRequested.class)
    public void twoOpReturnsPerTransactionTest() throws Exception {
        // Tests sending transaction where there are 2 attempts to write OP_RETURN scripts - this should fail and throw MultipleOpReturnRequested.
        receiveATransaction(wallet, myAddress);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        Coin messagePrice = Coin.ZERO;
        Script script1 = ScriptBuilder.createOpReturnScript("hello world 1!".getBytes());
        Script script2 = ScriptBuilder.createOpReturnScript("hello world 2!".getBytes());
        tx.addOutput(messagePrice, script1);
        tx.addOutput(messagePrice, script2);
        SendRequest request = SendRequest.forTx(tx);
        request.ensureMinRequiredFee = true;
        wallet.completeTx(request);
    }

    @Test(expected = DustySendRequested.class)
    public void sendDustTest() throws InsufficientMoneyException {
        // Tests sending dust, should throw DustySendRequested.
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        tx.addOutput(MIN_NONDUST_OUTPUT.subtract(SATOSHI), OTHER_ADDRESS);
        SendRequest request = SendRequest.forTx(tx);
        request.ensureMinRequiredFee = true;
        wallet.completeTx(request);
    }

    @Test
    public void sendMultipleCentsTest() throws Exception {
        receiveATransactionAmount(wallet, myAddress, COIN);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        Coin c = CENT.subtract(SATOSHI);
        tx.addOutput(c, OTHER_ADDRESS);
        tx.addOutput(c, OTHER_ADDRESS);
        tx.addOutput(c, OTHER_ADDRESS);
        tx.addOutput(c, OTHER_ADDRESS);
        SendRequest request = SendRequest.forTx(tx);
        wallet.completeTx(request);
    }

    @Test(expected = DustySendRequested.class)
    public void sendDustAndOpReturnWithoutValueTest() throws Exception {
        // Tests sending dust and OP_RETURN without value, should throw DustySendRequested because sending sending dust is not allowed in any case.
        receiveATransactionAmount(wallet, myAddress, COIN);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        tx.addOutput(Coin.ZERO, ScriptBuilder.createOpReturnScript("hello world!".getBytes()));
        tx.addOutput(Coin.SATOSHI, OTHER_ADDRESS);
        SendRequest request = SendRequest.forTx(tx);
        request.ensureMinRequiredFee = true;
        wallet.completeTx(request);
    }

    @Test(expected = DustySendRequested.class)
    public void sendDustAndMessageWithValueTest() throws Exception {
        // Tests sending dust and OP_RETURN with value, should throw DustySendRequested
        receiveATransaction(wallet, myAddress);
        Transaction tx = new Transaction(TestWithWallet.UNITTEST);
        tx.addOutput(Coin.CENT, ScriptBuilder.createOpReturnScript("hello world!".getBytes()));
        tx.addOutput(MIN_NONDUST_OUTPUT.subtract(SATOSHI), OTHER_ADDRESS);
        SendRequest request = SendRequest.forTx(tx);
        request.ensureMinRequiredFee = true;
        wallet.completeTx(request);
    }

    @Test
    public void sendRequestP2PKTest() {
        ECKey key = new ECKey();
        SendRequest req = SendRequest.to(TestWithWallet.UNITTEST, key, SATOSHI.multiply(12));
        Assert.assertArrayEquals(key.getPubKey(), ScriptPattern.extractKeyFromP2PK(req.tx.getOutputs().get(0).getScriptPubKey()));
    }

    @Test
    public void sendRequestP2PKHTest() {
        SendRequest req = SendRequest.to(OTHER_ADDRESS, SATOSHI.multiply(12));
        Assert.assertEquals(OTHER_ADDRESS, req.tx.getOutputs().get(0).getScriptPubKey().getToAddress(TestWithWallet.UNITTEST));
    }

    @Test
    public void feeSolverAndCoinSelectionTest_dustySendRequested() throws Exception {
        // Generate a few outputs to us that are far too small to spend reasonably
        Transaction tx1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, SATOSHI, myAddress);
        Transaction tx2 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, SATOSHI, myAddress);
        Assert.assertNotEquals(tx1.getTxId(), tx2.getTxId());
        Transaction tx3 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, SATOSHI.multiply(10), myAddress);
        sendMoneyToWallet(BEST_CHAIN, tx1, tx2, tx3);
        // Not allowed to send dust.
        try {
            SendRequest request = SendRequest.to(OTHER_ADDRESS, SATOSHI);
            request.ensureMinRequiredFee = true;
            wallet.completeTx(request);
            Assert.fail();
        } catch (Wallet e) {
            // Expected.
        }
        // Spend it all without fee enforcement
        SendRequest req = SendRequest.to(OTHER_ADDRESS, SATOSHI.multiply(12));
        Assert.assertNotNull(wallet.sendCoinsOffline(req));
        Assert.assertEquals(ZERO, wallet.getBalance());
    }

    @Test
    public void coinSelection_coinTimesDepth() throws Exception {
        Transaction txCent = sendMoneyToWallet(BEST_CHAIN, CENT);
        for (int i = 0; i < 197; i++)
            sendMoneyToWallet(BEST_CHAIN);

        Transaction txCoin = sendMoneyToWallet(BEST_CHAIN, COIN);
        Assert.assertEquals(COIN.add(CENT), wallet.getBalance());
        Assert.assertTrue(txCent.getOutput(0).isMine(wallet));
        Assert.assertTrue(txCent.getOutput(0).isAvailableForSpending());
        Assert.assertEquals(199, txCent.getConfidence().getDepthInBlocks());
        Assert.assertTrue(txCoin.getOutput(0).isMine(wallet));
        Assert.assertTrue(txCoin.getOutput(0).isAvailableForSpending());
        Assert.assertEquals(1, txCoin.getConfidence().getDepthInBlocks());
        // txCent has higher coin*depth than txCoin...
        Assert.assertTrue(txCent.getOutput(0).getValue().multiply(txCent.getConfidence().getDepthInBlocks()).isGreaterThan(txCoin.getOutput(0).getValue().multiply(txCoin.getConfidence().getDepthInBlocks())));
        // ...so txCent should be selected
        Transaction spend1 = wallet.createSend(OTHER_ADDRESS, CENT);
        Assert.assertEquals(1, spend1.getInputs().size());
        Assert.assertEquals(CENT, spend1.getInput(0).getValue());
        sendMoneyToWallet(BEST_CHAIN);
        Assert.assertTrue(txCent.getOutput(0).isMine(wallet));
        Assert.assertTrue(txCent.getOutput(0).isAvailableForSpending());
        Assert.assertEquals(200, txCent.getConfidence().getDepthInBlocks());
        Assert.assertTrue(txCoin.getOutput(0).isMine(wallet));
        Assert.assertTrue(txCoin.getOutput(0).isAvailableForSpending());
        Assert.assertEquals(2, txCoin.getConfidence().getDepthInBlocks());
        // Now txCent and txCoin have exactly the same coin*depth...
        Assert.assertEquals(txCent.getOutput(0).getValue().multiply(txCent.getConfidence().getDepthInBlocks()), txCoin.getOutput(0).getValue().multiply(txCoin.getConfidence().getDepthInBlocks()));
        // ...so the larger txCoin should be selected
        Transaction spend2 = wallet.createSend(OTHER_ADDRESS, COIN);
        Assert.assertEquals(1, spend2.getInputs().size());
        Assert.assertEquals(COIN, spend2.getInput(0).getValue());
        sendMoneyToWallet(BEST_CHAIN);
        Assert.assertTrue(txCent.getOutput(0).isMine(wallet));
        Assert.assertTrue(txCent.getOutput(0).isAvailableForSpending());
        Assert.assertEquals(201, txCent.getConfidence().getDepthInBlocks());
        Assert.assertTrue(txCoin.getOutput(0).isMine(wallet));
        Assert.assertTrue(txCoin.getOutput(0).isAvailableForSpending());
        Assert.assertEquals(3, txCoin.getConfidence().getDepthInBlocks());
        // Now txCent has lower coin*depth than txCoin...
        Assert.assertTrue(txCent.getOutput(0).getValue().multiply(txCent.getConfidence().getDepthInBlocks()).isLessThan(txCoin.getOutput(0).getValue().multiply(txCoin.getConfidence().getDepthInBlocks())));
        // ...so txCoin should be selected
        Transaction spend3 = wallet.createSend(OTHER_ADDRESS, COIN);
        Assert.assertEquals(1, spend3.getInputs().size());
        Assert.assertEquals(COIN, spend3.getInput(0).getValue());
    }

    @Test
    public void feeSolverAndCoinSelectionTests2() throws Exception {
        Transaction tx5 = sendMoneyToWallet(BEST_CHAIN, CENT);
        sendMoneyToWallet(BEST_CHAIN, COIN);
        // Now test feePerKb
        SendRequest request15 = SendRequest.to(OTHER_ADDRESS, CENT);
        for (int i = 0; i < 29; i++)
            request15.tx.addOutput(CENT, OTHER_ADDRESS);

        Assert.assertTrue(((request15.tx.unsafeBitcoinSerialize().length) > 1000));
        request15.feePerKb = Transaction.DEFAULT_TX_FEE;
        request15.ensureMinRequiredFee = true;
        wallet.completeTx(request15);
        Assert.assertEquals(Coin.valueOf(121300), request15.tx.getFee());
        Transaction spend15 = request15.tx;
        Assert.assertEquals(31, spend15.getOutputs().size());
        // We optimize for priority, so the output selected should be the largest one
        Assert.assertEquals(1, spend15.getInputs().size());
        Assert.assertEquals(COIN, spend15.getInput(0).getValue());
        // Test ensureMinRequiredFee
        SendRequest request16 = SendRequest.to(OTHER_ADDRESS, CENT);
        request16.feePerKb = ZERO;
        request16.ensureMinRequiredFee = true;
        for (int i = 0; i < 29; i++)
            request16.tx.addOutput(CENT, OTHER_ADDRESS);

        Assert.assertTrue(((request16.tx.unsafeBitcoinSerialize().length) > 1000));
        wallet.completeTx(request16);
        // Just the reference fee should be added if feePerKb == 0
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Assert.assertEquals(REFERENCE_DEFAULT_MIN_TX_FEE.multiply(1213).divide(1000), request16.tx.getFee());
        Transaction spend16 = request16.tx;
        Assert.assertEquals(31, spend16.getOutputs().size());
        // We optimize for priority, so the output selected should be the largest one
        Assert.assertEquals(1, spend16.getInputs().size());
        Assert.assertEquals(COIN, spend16.getInput(0).getValue());
        // Create a transaction whose max size could be up to 999 (if signatures were maximum size)
        SendRequest request17 = SendRequest.to(OTHER_ADDRESS, CENT);
        for (int i = 0; i < 22; i++)
            request17.tx.addOutput(CENT, OTHER_ADDRESS);

        request17.tx.addOutput(new TransactionOutput(TestWithWallet.UNITTEST, request17.tx, CENT, new byte[15]));
        request17.feePerKb = Transaction.DEFAULT_TX_FEE;
        request17.ensureMinRequiredFee = true;
        wallet.completeTx(request17);
        Assert.assertEquals(Coin.valueOf(99900), request17.tx.getFee());
        Assert.assertEquals(1, request17.tx.getInputs().size());
        // Calculate its max length to make sure it is indeed 999
        int theoreticalMaxLength17 = ((request17.tx.unsafeBitcoinSerialize().length) + (myKey.getPubKey().length)) + 75;
        for (TransactionInput in : request17.tx.getInputs())
            theoreticalMaxLength17 -= in.getScriptBytes().length;

        Assert.assertEquals(999, theoreticalMaxLength17);
        Transaction spend17 = request17.tx;
        {
            // Its actual size must be between 996 and 999 (inclusive) as signatures have a 3-byte size range (almost always)
            final int length = spend17.unsafeBitcoinSerialize().length;
            Assert.assertTrue(Integer.toString(length), ((length >= 996) && (length <= 999)));
        }
        // Now check that it got a fee of 1 since its max size is 999 (1kb).
        Assert.assertEquals(25, spend17.getOutputs().size());
        // We optimize for priority, so the output selected should be the largest one
        Assert.assertEquals(1, spend17.getInputs().size());
        Assert.assertEquals(COIN, spend17.getInput(0).getValue());
        // Create a transaction who's max size could be up to 1001 (if signatures were maximum size)
        SendRequest request18 = SendRequest.to(OTHER_ADDRESS, CENT);
        for (int i = 0; i < 22; i++)
            request18.tx.addOutput(CENT, OTHER_ADDRESS);

        request18.tx.addOutput(new TransactionOutput(TestWithWallet.UNITTEST, request18.tx, CENT, new byte[17]));
        request18.feePerKb = Transaction.DEFAULT_TX_FEE;
        request18.ensureMinRequiredFee = true;
        wallet.completeTx(request18);
        Assert.assertEquals(Coin.valueOf(100100), request18.tx.getFee());
        Assert.assertEquals(1, request18.tx.getInputs().size());
        // Calculate its max length to make sure it is indeed 1001
        Transaction spend18 = request18.tx;
        int theoreticalMaxLength18 = ((spend18.unsafeBitcoinSerialize().length) + (myKey.getPubKey().length)) + 75;
        for (TransactionInput in : spend18.getInputs())
            theoreticalMaxLength18 -= in.getScriptBytes().length;

        Assert.assertEquals(1001, theoreticalMaxLength18);
        // Its actual size must be between 998 and 1000 (inclusive) as signatures have a 3-byte size range (almost always)
        Assert.assertTrue(((spend18.unsafeBitcoinSerialize().length) >= 998));
        Assert.assertTrue(((spend18.unsafeBitcoinSerialize().length) <= 1001));
        // Now check that it did get a fee since its max size is 1000
        Assert.assertEquals(25, spend18.getOutputs().size());
        // We optimize for priority, so the output selected should be the largest one
        Assert.assertEquals(1, spend18.getInputs().size());
        Assert.assertEquals(COIN, spend18.getInput(0).getValue());
        // Now create a transaction that will spend COIN + fee, which makes it require both inputs
        Assert.assertEquals(wallet.getBalance(), CENT.add(COIN));
        SendRequest request19 = SendRequest.to(OTHER_ADDRESS, CENT);
        request19.feePerKb = ZERO;
        for (int i = 0; i < 99; i++)
            request19.tx.addOutput(CENT, OTHER_ADDRESS);

        // If we send now, we should only have to spend our COIN
        wallet.completeTx(request19);
        Assert.assertEquals(Coin.ZERO, request19.tx.getFee());
        Assert.assertEquals(1, request19.tx.getInputs().size());
        Assert.assertEquals(100, request19.tx.getOutputs().size());
        // Now reset request19 and give it a fee per kb
        request19.tx.clearInputs();
        request19 = SendRequest.forTx(request19.tx);
        request19.feePerKb = Transaction.DEFAULT_TX_FEE;
        request19.shuffleOutputs = false;
        wallet.completeTx(request19);
        Assert.assertEquals(Coin.valueOf(374200), request19.tx.getFee());
        Assert.assertEquals(2, request19.tx.getInputs().size());
        Assert.assertEquals(COIN, request19.tx.getInput(0).getValue());
        Assert.assertEquals(CENT, request19.tx.getInput(1).getValue());
        // Create another transaction that will spend COIN + fee, which makes it require both inputs
        SendRequest request20 = SendRequest.to(OTHER_ADDRESS, CENT);
        request20.feePerKb = ZERO;
        for (int i = 0; i < 99; i++)
            request20.tx.addOutput(CENT, OTHER_ADDRESS);

        // If we send now, we shouldn't have a fee and should only have to spend our COIN
        wallet.completeTx(request20);
        Assert.assertEquals(ZERO, request20.tx.getFee());
        Assert.assertEquals(1, request20.tx.getInputs().size());
        Assert.assertEquals(100, request20.tx.getOutputs().size());
        // Now reset request19 and give it a fee per kb
        request20.tx.clearInputs();
        request20 = SendRequest.forTx(request20.tx);
        request20.feePerKb = Transaction.DEFAULT_TX_FEE;
        wallet.completeTx(request20);
        // 4kb tx.
        Assert.assertEquals(Coin.valueOf(374200), request20.tx.getFee());
        Assert.assertEquals(2, request20.tx.getInputs().size());
        Assert.assertEquals(COIN, request20.tx.getInput(0).getValue());
        Assert.assertEquals(CENT, request20.tx.getInput(1).getValue());
        // Same as request 19, but make the change 0 (so it doesn't force fee) and make us require min fee
        SendRequest request21 = SendRequest.to(OTHER_ADDRESS, CENT);
        request21.feePerKb = ZERO;
        request21.ensureMinRequiredFee = true;
        for (int i = 0; i < 99; i++)
            request21.tx.addOutput(CENT, OTHER_ADDRESS);

        // request21.tx.addOutput(CENT.subtract(Coin.valueOf(18880-10)), OTHER_ADDRESS); //fails because tx size is calculated with a change output
        request21.tx.addOutput(CENT.subtract(Coin.valueOf(18880)), OTHER_ADDRESS);// 3739 bytes, fee 5048 sat/kb

        // request21.tx.addOutput(CENT.subtract(Coin.valueOf(500000)), OTHER_ADDRESS); //3774 bytes, fee 5003 sat/kb
        // If we send without a feePerKb, we should still require REFERENCE_DEFAULT_MIN_TX_FEE because we have an output < 0.01
        wallet.completeTx(request21);
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Assert.assertEquals(REFERENCE_DEFAULT_MIN_TX_FEE.multiply(3776).divide(1000), request21.tx.getFee());
        Assert.assertEquals(2, request21.tx.getInputs().size());
        Assert.assertEquals(COIN, request21.tx.getInput(0).getValue());
        Assert.assertEquals(CENT, request21.tx.getInput(1).getValue());
        // Test feePerKb when we aren't using ensureMinRequiredFee
        SendRequest request25 = SendRequest.to(OTHER_ADDRESS, CENT);
        request25.feePerKb = ZERO;
        for (int i = 0; i < 70; i++)
            request25.tx.addOutput(CENT, OTHER_ADDRESS);

        // If we send now, we shouldn't need a fee and should only have to spend our COIN
        wallet.completeTx(request25);
        Assert.assertEquals(ZERO, request25.tx.getFee());
        Assert.assertEquals(1, request25.tx.getInputs().size());
        Assert.assertEquals(72, request25.tx.getOutputs().size());
        // Now reset request25 and give it a fee per kb
        request25.tx.clearInputs();
        request25 = SendRequest.forTx(request25.tx);
        request25.feePerKb = Transaction.DEFAULT_TX_FEE;
        request25.shuffleOutputs = false;
        wallet.completeTx(request25);
        Assert.assertEquals(Coin.valueOf(279000), request25.tx.getFee());
        Assert.assertEquals(2, request25.tx.getInputs().size());
        Assert.assertEquals(COIN, request25.tx.getInput(0).getValue());
        Assert.assertEquals(CENT, request25.tx.getInput(1).getValue());
        // Spend our CENT output.
        Transaction spendTx5 = new Transaction(TestWithWallet.UNITTEST);
        spendTx5.addOutput(CENT, OTHER_ADDRESS);
        spendTx5.addInput(tx5.getOutput(0));
        wallet.signTransaction(SendRequest.forTx(spendTx5));
        sendMoneyToWallet(BEST_CHAIN, spendTx5);
        Assert.assertEquals(COIN, wallet.getBalance());
        // Ensure change is discarded if it is dust
        SendRequest request26 = SendRequest.to(OTHER_ADDRESS, CENT);
        for (int i = 0; i < 98; i++)
            request26.tx.addOutput(CENT, OTHER_ADDRESS);

        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee = REFERENCE_DEFAULT_MIN_TX_FEE.multiply(3560).divide(1000);
        Coin dustMinusOne = MIN_NONDUST_OUTPUT.subtract(SATOSHI);
        request26.tx.addOutput(CENT.subtract(fee.add(dustMinusOne)), OTHER_ADDRESS);
        Assert.assertTrue(((request26.tx.unsafeBitcoinSerialize().length) > 1000));
        request26.feePerKb = SATOSHI;
        request26.ensureMinRequiredFee = true;
        wallet.completeTx(request26);
        Assert.assertEquals(fee.add(dustMinusOne), request26.tx.getFee());
        Transaction spend26 = request26.tx;
        Assert.assertEquals(100, spend26.getOutputs().size());
        // We optimize for priority, so the output selected should be the largest one
        Assert.assertEquals(1, spend26.getInputs().size());
        Assert.assertEquals(COIN, spend26.getInput(0).getValue());
    }

    @Test
    public void recipientPaysFees() throws Exception {
        sendMoneyToWallet(BEST_CHAIN, COIN);
        // Simplest recipientPaysFees use case
        Coin valueToSend = CENT.divide(2);
        SendRequest request = SendRequest.to(OTHER_ADDRESS, valueToSend);
        request.feePerKb = Transaction.DEFAULT_TX_FEE;
        request.ensureMinRequiredFee = true;
        request.recipientsPayFees = true;
        request.shuffleOutputs = false;
        wallet.completeTx(request);
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee = request.feePerKb.multiply(227).divide(1000);
        Assert.assertEquals(fee, request.tx.getFee());
        Transaction spend = request.tx;
        Assert.assertEquals(2, spend.getOutputs().size());
        Assert.assertEquals(valueToSend.subtract(fee), spend.getOutput(0).getValue());
        Assert.assertEquals(COIN.subtract(valueToSend), spend.getOutput(1).getValue());
        Assert.assertEquals(1, spend.getInputs().size());
        Assert.assertEquals(COIN, spend.getInput(0).getValue());
        // Fee is split between the 2 outputs
        SendRequest request2 = SendRequest.to(OTHER_ADDRESS, valueToSend);
        request2.tx.addOutput(valueToSend, OTHER_ADDRESS);
        request2.feePerKb = Transaction.DEFAULT_TX_FEE;
        request2.ensureMinRequiredFee = true;
        request2.recipientsPayFees = true;
        request2.shuffleOutputs = false;
        wallet.completeTx(request2);
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee2 = request2.feePerKb.multiply(261).divide(1000);
        Assert.assertEquals(fee2, request2.tx.getFee());
        Transaction spend2 = request2.tx;
        Assert.assertEquals(3, spend2.getOutputs().size());
        Assert.assertEquals(valueToSend.subtract(fee2.divide(2)), spend2.getOutput(0).getValue());
        Assert.assertEquals(valueToSend.subtract(fee2.divide(2)), spend2.getOutput(1).getValue());
        Assert.assertEquals(COIN.subtract(valueToSend.multiply(2)), spend2.getOutput(2).getValue());
        Assert.assertEquals(1, spend2.getInputs().size());
        Assert.assertEquals(COIN, spend2.getInput(0).getValue());
        // Fee is split between the 3 outputs. Division has a remainder which is taken from the first output
        SendRequest request3 = SendRequest.to(OTHER_ADDRESS, valueToSend);
        request3.tx.addOutput(valueToSend, OTHER_ADDRESS);
        request3.tx.addOutput(valueToSend, OTHER_ADDRESS);
        request3.feePerKb = Transaction.DEFAULT_TX_FEE;
        request3.ensureMinRequiredFee = true;
        request3.recipientsPayFees = true;
        request3.shuffleOutputs = false;
        wallet.completeTx(request3);
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee3 = request3.feePerKb.multiply(295).divide(1000);
        Assert.assertEquals(fee3, request3.tx.getFee());
        Transaction spend3 = request3.tx;
        Assert.assertEquals(4, spend3.getOutputs().size());
        // 1st output pays the fee division remainder
        Assert.assertEquals(valueToSend.subtract(fee3.divideAndRemainder(3)[0]).subtract(fee3.divideAndRemainder(3)[1]), spend3.getOutput(0).getValue());
        Assert.assertEquals(valueToSend.subtract(fee3.divide(3)), spend3.getOutput(1).getValue());
        Assert.assertEquals(valueToSend.subtract(fee3.divide(3)), spend3.getOutput(2).getValue());
        Assert.assertEquals(COIN.subtract(valueToSend.multiply(3)), spend3.getOutput(3).getValue());
        Assert.assertEquals(1, spend3.getInputs().size());
        Assert.assertEquals(COIN, spend3.getInput(0).getValue());
        // Output when subtracted fee is dust
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee4 = DEFAULT_TX_FEE.multiply(227).divide(1000);
        valueToSend = fee4.add(MIN_NONDUST_OUTPUT).subtract(SATOSHI);
        SendRequest request4 = SendRequest.to(OTHER_ADDRESS, valueToSend);
        request4.feePerKb = Transaction.DEFAULT_TX_FEE;
        request4.ensureMinRequiredFee = true;
        request4.recipientsPayFees = true;
        request4.shuffleOutputs = false;
        try {
            wallet.completeTx(request4);
            Assert.fail("Expected CouldNotAdjustDownwards exception");
        } catch (Wallet e) {
        }
        // Change is dust, so it is incremented to min non dust value. First output value is reduced to compensate.
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee5 = DEFAULT_TX_FEE.multiply(261).divide(1000);
        valueToSend = COIN.divide(2).subtract(MICROCOIN);
        SendRequest request5 = SendRequest.to(OTHER_ADDRESS, valueToSend);
        request5.tx.addOutput(valueToSend, OTHER_ADDRESS);
        request5.feePerKb = Transaction.DEFAULT_TX_FEE;
        request5.ensureMinRequiredFee = true;
        request5.recipientsPayFees = true;
        request5.shuffleOutputs = false;
        wallet.completeTx(request5);
        Assert.assertEquals(fee5, request5.tx.getFee());
        Transaction spend5 = request5.tx;
        Assert.assertEquals(3, spend5.getOutputs().size());
        Coin valueSubtractedFromFirstOutput = MIN_NONDUST_OUTPUT.subtract(COIN.subtract(valueToSend.multiply(2)));
        Assert.assertEquals(valueToSend.subtract(fee5.divide(2)).subtract(valueSubtractedFromFirstOutput), spend5.getOutput(0).getValue());
        Assert.assertEquals(valueToSend.subtract(fee5.divide(2)), spend5.getOutput(1).getValue());
        Assert.assertEquals(MIN_NONDUST_OUTPUT, spend5.getOutput(2).getValue());
        Assert.assertEquals(1, spend5.getInputs().size());
        Assert.assertEquals(COIN, spend5.getInput(0).getValue());
        // Change is dust, so it is incremented to min non dust value. First output value is about to be reduced to
        // compensate, but after subtracting some satoshis, first output is dust.
        // Hardcoded tx length because actual length may vary depending on actual signature length
        Coin fee6 = DEFAULT_TX_FEE.multiply(261).divide(1000);
        Coin valueToSend1 = fee6.divide(2).add(MIN_NONDUST_OUTPUT).add(MICROCOIN);
        Coin valueToSend2 = COIN.subtract(valueToSend1).subtract(MICROCOIN.multiply(2));
        SendRequest request6 = SendRequest.to(OTHER_ADDRESS, valueToSend1);
        request6.tx.addOutput(valueToSend2, OTHER_ADDRESS);
        request6.feePerKb = Transaction.DEFAULT_TX_FEE;
        request6.ensureMinRequiredFee = true;
        request6.recipientsPayFees = true;
        request6.shuffleOutputs = false;
        try {
            wallet.completeTx(request6);
            Assert.fail("Expected CouldNotAdjustDownwards exception");
        } catch (Wallet e) {
        }
    }

    @Test
    public void transactionGetFeeTest() throws Exception {
        // Prepare wallet to spend
        StoredBlock block = new StoredBlock(FakeTxBuilder.makeSolvedTestBlock(blockStore, OTHER_ADDRESS), BigInteger.ONE, 1);
        Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        wallet.receiveFromBlock(tx, block, BEST_CHAIN, 0);
        // Create a transaction
        SendRequest request = SendRequest.to(OTHER_ADDRESS, CENT);
        request.feePerKb = Transaction.DEFAULT_TX_FEE;
        wallet.completeTx(request);
        Assert.assertEquals(Coin.valueOf(22700), request.tx.getFee());
    }

    @Test
    public void lowerThanDefaultFee() throws InsufficientMoneyException {
        int feeFactor = 50;
        Coin fee = DEFAULT_TX_FEE.divide(feeFactor);
        receiveATransactionAmount(wallet, myAddress, COIN);
        SendRequest req = SendRequest.to(myAddress, Coin.CENT);
        req.feePerKb = fee;
        wallet.completeTx(req);
        Assert.assertEquals(Coin.valueOf(22700).divide(feeFactor), req.tx.getFee());
        wallet.commitTx(req.tx);
        SendRequest emptyReq = SendRequest.emptyWallet(myAddress);
        emptyReq.feePerKb = fee;
        emptyReq.ensureMinRequiredFee = true;
        emptyReq.emptyWallet = true;
        emptyReq.coinSelector = AllowUnconfirmedCoinSelector.get();
        wallet.completeTx(emptyReq);
        Assert.assertEquals(REFERENCE_DEFAULT_MIN_TX_FEE, emptyReq.tx.getFee());
        wallet.commitTx(emptyReq.tx);
    }

    @Test
    public void higherThanDefaultFee() throws InsufficientMoneyException {
        int feeFactor = 10;
        Coin fee = DEFAULT_TX_FEE.multiply(feeFactor);
        receiveATransactionAmount(wallet, myAddress, COIN);
        SendRequest req = SendRequest.to(myAddress, Coin.CENT);
        req.feePerKb = fee;
        wallet.completeTx(req);
        Assert.assertEquals(Coin.valueOf(22700).multiply(feeFactor), req.tx.getFee());
        wallet.commitTx(req.tx);
        SendRequest emptyReq = SendRequest.emptyWallet(myAddress);
        emptyReq.feePerKb = fee;
        emptyReq.emptyWallet = true;
        emptyReq.coinSelector = AllowUnconfirmedCoinSelector.get();
        wallet.completeTx(emptyReq);
        Assert.assertEquals(Coin.valueOf(342000), emptyReq.tx.getFee());
        wallet.commitTx(emptyReq.tx);
    }

    @Test
    public void testCompleteTxWithExistingInputs() throws Exception {
        // Tests calling completeTx with a SendRequest that already has a few inputs in it
        // Generate a few outputs to us
        StoredBlock block = new StoredBlock(FakeTxBuilder.makeSolvedTestBlock(blockStore, OTHER_ADDRESS), BigInteger.ONE, 1);
        Transaction tx1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        wallet.receiveFromBlock(tx1, block, BEST_CHAIN, 0);
        Transaction tx2 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        Assert.assertNotEquals(tx1.getTxId(), tx2.getTxId());
        wallet.receiveFromBlock(tx2, block, BEST_CHAIN, 1);
        Transaction tx3 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, myAddress);
        wallet.receiveFromBlock(tx3, block, BEST_CHAIN, 2);
        SendRequest request1 = SendRequest.to(OTHER_ADDRESS, CENT);
        // If we just complete as-is, we will use one of the COIN outputs to get higher priority,
        // resulting in a change output
        request1.shuffleOutputs = false;
        wallet.completeTx(request1);
        Assert.assertEquals(1, request1.tx.getInputs().size());
        Assert.assertEquals(2, request1.tx.getOutputs().size());
        Assert.assertEquals(CENT, request1.tx.getOutput(0).getValue());
        Assert.assertEquals(COIN.subtract(CENT), request1.tx.getOutput(1).getValue());
        // Now create an identical request2 and add an unsigned spend of the CENT output
        SendRequest request2 = SendRequest.to(OTHER_ADDRESS, CENT);
        request2.tx.addInput(tx3.getOutput(0));
        // Now completeTx will result in one input, one output
        wallet.completeTx(request2);
        Assert.assertEquals(1, request2.tx.getInputs().size());
        Assert.assertEquals(1, request2.tx.getOutputs().size());
        Assert.assertEquals(CENT, request2.tx.getOutput(0).getValue());
        // Make sure it was properly signed
        request2.tx.getInput(0).getScriptSig().correctlySpends(request2.tx, 0, tx3.getOutput(0).getScriptPubKey());
        // However, if there is no connected output, we will grab a COIN output anyway and add the CENT to fee
        SendRequest request3 = SendRequest.to(OTHER_ADDRESS, CENT);
        request3.tx.addInput(new TransactionInput(TestWithWallet.UNITTEST, request3.tx, new byte[]{  }, new TransactionOutPoint(TestWithWallet.UNITTEST, 0, tx3.getTxId())));
        // Now completeTx will result in two inputs, two outputs and a fee of a CENT
        // Note that it is simply assumed that the inputs are correctly signed, though in fact the first is not
        request3.shuffleOutputs = false;
        wallet.completeTx(request3);
        Assert.assertEquals(2, request3.tx.getInputs().size());
        Assert.assertEquals(2, request3.tx.getOutputs().size());
        Assert.assertEquals(CENT, request3.tx.getOutput(0).getValue());
        Assert.assertEquals(COIN.subtract(CENT), request3.tx.getOutput(1).getValue());
        SendRequest request4 = SendRequest.to(OTHER_ADDRESS, CENT);
        request4.tx.addInput(tx3.getOutput(0));
        // Now if we manually sign it, completeTx will not replace our signature
        wallet.signTransaction(request4);
        byte[] scriptSig = request4.tx.getInput(0).getScriptBytes();
        wallet.completeTx(request4);
        Assert.assertEquals(1, request4.tx.getInputs().size());
        Assert.assertEquals(1, request4.tx.getOutputs().size());
        Assert.assertEquals(CENT, request4.tx.getOutput(0).getValue());
        Assert.assertArrayEquals(scriptSig, request4.tx.getInput(0).getScriptBytes());
    }

    // There is a test for spending a coinbase transaction as it matures in BlockChainTest#coinbaseTransactionAvailability
    // Support for offline spending is tested in PeerGroupTest
    @Test
    public void exceptionsDoNotBlockAllListeners() throws Exception {
        // Check that if a wallet listener throws an exception, the others still run.
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                WalletTest.log.info("onCoinsReceived 1");
                throw new RuntimeException("barf");
            }
        });
        final AtomicInteger flag = new AtomicInteger();
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                WalletTest.log.info("onCoinsReceived 2");
                flag.incrementAndGet();
            }
        });
        sendMoneyToWallet(BEST_CHAIN, COIN);
        WalletTest.log.info("Wait for user thread");
        Threading.waitForUserCode();
        WalletTest.log.info("... and test flag.");
        Assert.assertEquals(1, flag.get());
    }

    @Test
    public void testEmptyRandomWallet() throws Exception {
        // Add a random set of outputs
        StoredBlock block = new StoredBlock(FakeTxBuilder.makeSolvedTestBlock(blockStore, OTHER_ADDRESS), BigInteger.ONE, 1);
        Random rng = new Random();
        for (int i = 0; i < ((rng.nextInt(100)) + 1); i++) {
            Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, Coin.valueOf(rng.nextInt(((int) (value)))), myAddress);
            wallet.receiveFromBlock(tx, block, BEST_CHAIN, i);
        }
        SendRequest request = SendRequest.emptyWallet(OTHER_ADDRESS);
        wallet.completeTx(request);
        wallet.commitTx(request.tx);
        Assert.assertEquals(ZERO, wallet.getBalance());
    }

    @Test
    public void testEmptyWallet() throws Exception {
        // Add exactly 0.01
        StoredBlock block = new StoredBlock(FakeTxBuilder.makeSolvedTestBlock(blockStore, OTHER_ADDRESS), BigInteger.ONE, 1);
        Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, myAddress);
        wallet.receiveFromBlock(tx, block, BEST_CHAIN, 0);
        SendRequest request = SendRequest.emptyWallet(OTHER_ADDRESS);
        wallet.completeTx(request);
        Assert.assertEquals(ZERO, request.tx.getFee());
        wallet.commitTx(request.tx);
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(CENT, request.tx.getOutput(0).getValue());
        // Add 1 confirmed cent and 1 unconfirmed cent. Verify only one cent is emptied because of the coin selection
        // policies that are in use by default.
        block = new StoredBlock(FakeTxBuilder.makeSolvedTestBlock(blockStore, OTHER_ADDRESS), BigInteger.ONE, 2);
        tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, myAddress);
        wallet.receiveFromBlock(tx, block, BEST_CHAIN, 0);
        tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, CENT, myAddress);
        wallet.receivePending(tx, null);
        request = SendRequest.emptyWallet(OTHER_ADDRESS);
        wallet.completeTx(request);
        Assert.assertEquals(ZERO, request.tx.getFee());
        wallet.commitTx(request.tx);
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(CENT, request.tx.getOutput(0).getValue());
        // Add an unsendable value
        block = new StoredBlock(block.getHeader().createNextBlock(OTHER_ADDRESS), BigInteger.ONE, 3);
        Coin outputValue = MIN_NONDUST_OUTPUT.subtract(SATOSHI);
        tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, outputValue, myAddress);
        wallet.receiveFromBlock(tx, block, BEST_CHAIN, 0);
        try {
            request = SendRequest.emptyWallet(OTHER_ADDRESS);
            wallet.completeTx(request);
            Assert.assertEquals(ZERO, request.tx.getFee());
            Assert.fail();
        } catch (Wallet e) {
        }
    }

    @Test
    public void childPaysForParent() throws Exception {
        // Receive confirmed balance to play with.
        Transaction toMe = FakeTxBuilder.createFakeTxWithoutChangeAddress(TestWithWallet.UNITTEST, COIN, myAddress);
        sendMoneyToWallet(BEST_CHAIN, toMe);
        Assert.assertEquals(COIN, wallet.getBalance(ESTIMATED_SPENDABLE));
        Assert.assertEquals(COIN, wallet.getBalance(BalanceType.AVAILABLE_SPENDABLE));
        // Receive unconfirmed coin without fee.
        Transaction toMeWithoutFee = FakeTxBuilder.createFakeTxWithoutChangeAddress(TestWithWallet.UNITTEST, COIN, myAddress);
        wallet.receivePending(toMeWithoutFee, null);
        Assert.assertEquals(COIN.multiply(2), wallet.getBalance(ESTIMATED_SPENDABLE));
        Assert.assertEquals(COIN, wallet.getBalance(BalanceType.AVAILABLE_SPENDABLE));
        // Craft a child-pays-for-parent transaction.
        final Coin feeRaise = MILLICOIN;
        final SendRequest sendRequest = SendRequest.childPaysForParent(wallet, toMeWithoutFee, feeRaise);
        wallet.signTransaction(sendRequest);
        wallet.commitTx(sendRequest.tx);
        Assert.assertEquals(RAISE_FEE, sendRequest.tx.getPurpose());
        Assert.assertEquals(COIN.multiply(2).subtract(feeRaise), wallet.getBalance(ESTIMATED_SPENDABLE));
        Assert.assertEquals(COIN, wallet.getBalance(BalanceType.AVAILABLE_SPENDABLE));
    }

    @Test
    public void keyRotationRandom() throws Exception {
        Utils.setMockClock();
        // Start with an empty wallet (no HD chain).
        wallet = new Wallet(TestWithWallet.UNITTEST);
        // Watch out for wallet-initiated broadcasts.
        MockTransactionBroadcaster broadcaster = new MockTransactionBroadcaster(wallet);
        // Send three cents to two different random keys, then add a key and mark the initial keys as compromised.
        ECKey key1 = new ECKey();
        key1.setCreationTimeSeconds(((Utils.currentTimeSeconds()) - (86400 * 2)));
        ECKey key2 = new ECKey();
        key2.setCreationTimeSeconds(((Utils.currentTimeSeconds()) - 86400));
        wallet.importKey(key1);
        wallet.importKey(key2);
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key1));
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key2));
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key2));
        Date compromiseTime = Utils.now();
        Assert.assertEquals(0, broadcaster.size());
        Assert.assertFalse(wallet.isKeyRotating(key1));
        // We got compromised!
        Utils.rollMockClock(1);
        wallet.setKeyRotationTime(compromiseTime);
        Assert.assertTrue(wallet.isKeyRotating(key1));
        wallet.doMaintenance(null, true);
        Transaction tx = broadcaster.waitForTransactionAndSucceed();
        final Coin THREE_CENTS = CENT.add(CENT).add(CENT);
        Assert.assertEquals(Coin.valueOf(49100), tx.getFee());
        Assert.assertEquals(THREE_CENTS, tx.getValueSentFromMe(wallet));
        Assert.assertEquals(THREE_CENTS.subtract(tx.getFee()), tx.getValueSentToMe(wallet));
        // TX sends to one of our addresses (for now we ignore married wallets).
        final Address toAddress = tx.getOutput(0).getScriptPubKey().getToAddress(TestWithWallet.UNITTEST);
        final ECKey rotatingToKey = wallet.findKeyFromPubKeyHash(toAddress.getHash(), toAddress.getOutputScriptType());
        Assert.assertNotNull(rotatingToKey);
        Assert.assertFalse(wallet.isKeyRotating(rotatingToKey));
        Assert.assertEquals(3, tx.getInputs().size());
        // It confirms.
        sendMoneyToWallet(BEST_CHAIN, tx);
        // Now receive some more money to the newly derived address via a new block and check that nothing happens.
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, toAddress);
        Assert.assertTrue(wallet.doMaintenance(null, true).get().isEmpty());
        Assert.assertEquals(0, broadcaster.size());
        // Receive money via a new block on key1 and ensure it shows up as a maintenance task.
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key1));
        wallet.doMaintenance(null, true);
        tx = broadcaster.waitForTransactionAndSucceed();
        Assert.assertNotNull(wallet.findKeyFromPubKeyHash(tx.getOutput(0).getScriptPubKey().getPubKeyHash(), toAddress.getOutputScriptType()));
        WalletTest.log.info("Unexpected thing: {}", tx);
        Assert.assertEquals(Coin.valueOf(19300), tx.getFee());
        Assert.assertEquals(1, tx.getInputs().size());
        Assert.assertEquals(1, tx.getOutputs().size());
        Assert.assertEquals(CENT, tx.getValueSentFromMe(wallet));
        Assert.assertEquals(CENT.subtract(tx.getFee()), tx.getValueSentToMe(wallet));
        Assert.assertEquals(KEY_ROTATION, tx.getPurpose());
        // We don't attempt to race an attacker against unconfirmed transactions.
        // Now round-trip the wallet and check the protobufs are storing the data correctly.
        wallet = roundTrip(wallet);
        tx = wallet.getTransaction(tx.getTxId());
        Preconditions.checkNotNull(tx);
        Assert.assertEquals(KEY_ROTATION, tx.getPurpose());
        // Have to divide here to avoid mismatch due to second-level precision in serialisation.
        Assert.assertEquals(((compromiseTime.getTime()) / 1000), ((wallet.getKeyRotationTime().getTime()) / 1000));
        // Make a normal spend and check it's all ok.
        wallet.sendCoins(broadcaster, OTHER_ADDRESS, wallet.getBalance());
        tx = broadcaster.waitForTransaction();
        Assert.assertArrayEquals(OTHER_ADDRESS.getHash(), tx.getOutput(0).getScriptPubKey().getPubKeyHash());
    }

    @Test
    public void keyRotationHD() throws Exception {
        // Test that if we rotate an HD chain, a new one is created and all arrivals on the old keys are moved.
        Utils.setMockClock();
        wallet = new Wallet(TestWithWallet.UNITTEST);
        ECKey key1 = wallet.freshReceiveKey();
        ECKey key2 = wallet.freshReceiveKey();
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key1));
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, key2));
        DeterministicKey watchKey1 = wallet.getWatchingKey();
        // A day later, we get compromised.
        Utils.rollMockClock(86400);
        wallet.setKeyRotationTime(Utils.currentTimeSeconds());
        List<Transaction> txns = wallet.doMaintenance(null, false).get();
        Assert.assertEquals(1, txns.size());
        DeterministicKey watchKey2 = wallet.getWatchingKey();
        Assert.assertNotEquals(watchKey1, watchKey2);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void keyRotationHD2() throws Exception {
        // Check we handle the following scenario: a weak random key is created, then some good random keys are created
        // but the weakness of the first isn't known yet. The wallet is upgraded to HD based on the weak key. Later, we
        // find out about the weakness and set the rotation time to after the bad key's creation date. A new HD chain
        // should be created based on the oldest known good key and the old chain + bad random key should rotate to it.
        // We fix the private keys just to make the test deterministic (last byte differs).
        Utils.setMockClock();
        ECKey badKey = ECKey.fromPrivate(HEX.decode("00905b93f990267f4104f316261fc10f9f983551f9ef160854f40102eb71cffdbb"));
        badKey.setCreationTimeSeconds(Utils.currentTimeSeconds());
        Utils.rollMockClock(86400);
        ECKey goodKey = ECKey.fromPrivate(HEX.decode("00905b93f990267f4104f316261fc10f9f983551f9ef160854f40102eb71cffdcc"));
        goodKey.setCreationTimeSeconds(Utils.currentTimeSeconds());
        // Do an upgrade based on the bad key.
        KeyChainGroup kcg = KeyChainGroup.builder(TestWithWallet.UNITTEST).build();
        kcg.importKeys(badKey, goodKey);
        Utils.rollMockClock(86400);
        wallet = new Wallet(TestWithWallet.UNITTEST, kcg);// This avoids the automatic HD initialisation

        Assert.assertTrue(kcg.getDeterministicKeyChains().isEmpty());
        wallet.upgradeToDeterministic(P2PKH, null);
        DeterministicKey badWatchingKey = wallet.getWatchingKey();
        Assert.assertEquals(badKey.getCreationTimeSeconds(), badWatchingKey.getCreationTimeSeconds());
        sendMoneyToWallet(wallet, BEST_CHAIN, CENT, LegacyAddress.fromKey(TestWithWallet.UNITTEST, badWatchingKey));
        // Now we set the rotation time to the time we started making good keys. This should create a new HD chain.
        wallet.setKeyRotationTime(goodKey.getCreationTimeSeconds());
        List<Transaction> txns = wallet.doMaintenance(null, false).get();
        Assert.assertEquals(1, txns.size());
        Address output = txns.get(0).getOutput(0).getScriptPubKey().getToAddress(TestWithWallet.UNITTEST);
        ECKey usedKey = wallet.findKeyFromPubKeyHash(output.getHash(), output.getOutputScriptType());
        Assert.assertEquals(goodKey.getCreationTimeSeconds(), usedKey.getCreationTimeSeconds());
        Assert.assertEquals(goodKey.getCreationTimeSeconds(), wallet.freshReceiveKey().getCreationTimeSeconds());
        Assert.assertEquals("mrM3TpCnav5YQuVA1xLercCGJH4DXujMtv", LegacyAddress.fromKey(TestWithWallet.UNITTEST, usedKey).toString());
        DeterministicKeyChain c = kcg.getDeterministicKeyChains().get(1);
        Assert.assertEquals(c.getEarliestKeyCreationTime(), goodKey.getCreationTimeSeconds());
        Assert.assertEquals(2, kcg.getDeterministicKeyChains().size());
        // Commit the maint txns.
        wallet.commitTx(txns.get(0));
        // Check next maintenance does nothing.
        Assert.assertTrue(wallet.doMaintenance(null, false).get().isEmpty());
        Assert.assertEquals(c, kcg.getDeterministicKeyChains().get(1));
        Assert.assertEquals(2, kcg.getDeterministicKeyChains().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void importOfHDKeyForbidden() throws Exception {
        wallet.importKey(wallet.freshReceiveKey());
    }

    private static final byte[] EMPTY_SIG = new byte[]{  };

    @Test
    public void completeTxPartiallySignedWithDummySigs() throws Exception {
        byte[] dummySig = TransactionSignature.dummy().encodeToBitcoin();
        completeTxPartiallySigned(USE_DUMMY_SIG, dummySig);
    }

    @Test
    public void completeTxPartiallySignedWithEmptySig() throws Exception {
        completeTxPartiallySigned(USE_OP_ZERO, WalletTest.EMPTY_SIG);
    }

    @Test(expected = MissingPrivateKeyException.class)
    public void completeTxPartiallySignedThrows() throws Exception {
        sendMoneyToWallet(BEST_CHAIN, CENT, wallet.currentReceiveKey());
        SendRequest req = SendRequest.emptyWallet(OTHER_ADDRESS);
        wallet.completeTx(req);
        // Delete the sigs
        for (TransactionInput input : req.tx.getInputs())
            input.clearScriptBytes();

        Wallet watching = Wallet.fromWatchingKey(TestWithWallet.UNITTEST, wallet.getWatchingKey().dropParent().dropPrivateBytes(), P2PKH);
        watching.currentReceiveKey();
        watching.completeTx(SendRequest.forTx(req.tx));
    }

    @Test
    public void completeTxPartiallySignedMarriedWithDummySigs() throws Exception {
        byte[] dummySig = TransactionSignature.dummy().encodeToBitcoin();
        completeTxPartiallySignedMarried(USE_DUMMY_SIG, dummySig);
    }

    @Test
    public void completeTxPartiallySignedMarriedWithEmptySig() throws Exception {
        completeTxPartiallySignedMarried(USE_OP_ZERO, WalletTest.EMPTY_SIG);
    }

    @Test(expected = MissingSignatureException.class)
    public void completeTxPartiallySignedMarriedThrows() throws Exception {
        completeTxPartiallySignedMarried(THROW, WalletTest.EMPTY_SIG);
    }

    @Test(expected = MissingSignatureException.class)
    public void completeTxPartiallySignedMarriedThrowsByDefault() throws Exception {
        createMarriedWallet(2, 2, false);
        myAddress = wallet.currentAddress(RECEIVE_FUNDS);
        sendMoneyToWallet(BEST_CHAIN, COIN, myAddress);
        SendRequest req = SendRequest.emptyWallet(OTHER_ADDRESS);
        wallet.completeTx(req);
    }

    @Test
    public void riskAnalysis() throws Exception {
        // Send a tx that is considered risky to the wallet, verify it doesn't show up in the balances.
        final Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        final AtomicBoolean bool = new AtomicBoolean();
        wallet.setRiskAnalyzer(new RiskAnalysis.Analyzer() {
            @Override
            public RiskAnalysis create(Wallet wallet, Transaction wtx, List<Transaction> dependencies) {
                RiskAnalysis.Result result = RiskAnalysis.Result.OK;
                if (wtx.getTxId().equals(tx.getTxId()))
                    result = NON_STANDARD;

                final RiskAnalysis.Result finalResult = result;
                return new RiskAnalysis() {
                    @Override
                    public Result analyze() {
                        bool.set(true);
                        return finalResult;
                    }
                };
            }
        });
        Assert.assertTrue(wallet.isPendingTransactionRelevant(tx));
        Assert.assertEquals(Coin.ZERO, wallet.getBalance());
        Assert.assertEquals(Coin.ZERO, wallet.getBalance(ESTIMATED));
        wallet.receivePending(tx, null);
        Assert.assertEquals(Coin.ZERO, wallet.getBalance());
        Assert.assertEquals(Coin.ZERO, wallet.getBalance(ESTIMATED));
        Assert.assertTrue(bool.get());
        // Confirm it in the same manner as how Bloom filtered blocks do. Verify it shows up.
        sendMoneyToWallet(BEST_CHAIN, tx);
        Assert.assertEquals(COIN, wallet.getBalance());
    }

    @Test
    public void transactionInBlockNotification() {
        final Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        StoredBlock block = FakeTxBuilder.createFakeBlock(blockStore, BLOCK_HEIGHT_GENESIS, tx).storedBlock;
        wallet.receivePending(tx, null);
        boolean notification = wallet.notifyTransactionIsInBlock(tx.getTxId(), block, BEST_CHAIN, 1);
        Assert.assertTrue(notification);
        final Transaction tx2 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, OTHER_ADDRESS);
        wallet.receivePending(tx2, null);
        StoredBlock block2 = FakeTxBuilder.createFakeBlock(blockStore, ((Block.BLOCK_HEIGHT_GENESIS) + 1), tx2).storedBlock;
        boolean notification2 = wallet.notifyTransactionIsInBlock(tx2.getTxId(), block2, BEST_CHAIN, 1);
        Assert.assertFalse(notification2);
    }

    @Test
    public void duplicatedBlock() {
        final Transaction tx = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST, COIN, myAddress);
        StoredBlock block = FakeTxBuilder.createFakeBlock(blockStore, BLOCK_HEIGHT_GENESIS, tx).storedBlock;
        wallet.notifyNewBestBlock(block);
        wallet.notifyNewBestBlock(block);
    }

    @Test
    public void keyEvents() throws Exception {
        // Check that we can register an event listener, generate some keys and the callbacks are invoked properly.
        wallet = new Wallet(TestWithWallet.UNITTEST, KeyChainGroup.builder(TestWithWallet.UNITTEST).fromRandom(P2PKH).build());
        final List<ECKey> keys = Lists.newLinkedList();
        wallet.addKeyChainEventListener(SAME_THREAD, new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> k) {
                keys.addAll(k);
            }
        });
        wallet.freshReceiveKey();
        Assert.assertEquals(1, keys.size());
    }

    @Test
    public void upgradeToDeterministic_basic_to_P2PKH_unencrypted() throws Exception {
        wallet = new Wallet(TestWithWallet.UNITTEST, KeyChainGroup.builder(TestWithWallet.UNITTEST).build());
        wallet.importKeys(Arrays.asList(new ECKey(), new ECKey()));
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        try {
            wallet.freshReceiveKey();
            Assert.fail();
        } catch (DeterministicUpgradeRequiredException e) {
            // Expected.
        }
        wallet.upgradeToDeterministic(P2PKH, null);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2PKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2PKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test
    public void upgradeToDeterministic_basic_to_P2PKH_encrypted() throws Exception {
        wallet = new Wallet(TestWithWallet.UNITTEST, KeyChainGroup.builder(TestWithWallet.UNITTEST).build());
        wallet.importKeys(Arrays.asList(new ECKey(), new ECKey()));
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        KeyParameter aesKey = new KeyCrypterScrypt().deriveKey("abc");
        wallet.encrypt(new KeyCrypterScrypt(), aesKey);
        Assert.assertTrue(wallet.isEncrypted());
        try {
            wallet.freshReceiveKey();
            Assert.fail();
        } catch (DeterministicUpgradeRequiredException e) {
            // Expected.
        }
        try {
            wallet.upgradeToDeterministic(P2PKH, null);
            Assert.fail();
        } catch (DeterministicUpgradeRequiresPassword e) {
            // Expected.
        }
        wallet.upgradeToDeterministic(P2PKH, aesKey);
        Assert.assertTrue(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2PKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2PKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test
    public void upgradeToDeterministic_basic_to_P2WPKH_unencrypted() throws Exception {
        wallet = new Wallet(TestWithWallet.UNITTEST, KeyChainGroup.builder(TestWithWallet.UNITTEST).build());
        wallet.importKeys(Arrays.asList(new ECKey(), new ECKey()));
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        try {
            wallet.freshReceiveKey();
            Assert.fail();
        } catch (DeterministicUpgradeRequiredException e) {
            // Expected.
        }
        wallet.upgradeToDeterministic(P2WPKH, null);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2WPKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2WPKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test
    public void upgradeToDeterministic_basic_to_P2WPKH_encrypted() throws Exception {
        wallet = new Wallet(TestWithWallet.UNITTEST, KeyChainGroup.builder(TestWithWallet.UNITTEST).build());
        wallet.importKeys(Arrays.asList(new ECKey(), new ECKey()));
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        KeyParameter aesKey = new KeyCrypterScrypt().deriveKey("abc");
        wallet.encrypt(new KeyCrypterScrypt(), aesKey);
        Assert.assertTrue(wallet.isEncrypted());
        try {
            wallet.upgradeToDeterministic(P2WPKH, null);
            Assert.fail();
        } catch (DeterministicUpgradeRequiresPassword e) {
            // Expected.
        }
        wallet.upgradeToDeterministic(P2WPKH, aesKey);
        Assert.assertTrue(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2WPKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2WPKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test
    public void upgradeToDeterministic_P2PKH_to_P2WPKH_unencrypted() throws Exception {
        wallet = Wallet.createDeterministic(TestWithWallet.UNITTEST, P2PKH);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2PKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2PKH, wallet.freshReceiveAddress().getOutputScriptType());
        wallet.upgradeToDeterministic(P2WPKH, null);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2WPKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2WPKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test
    public void upgradeToDeterministic_P2PKH_to_P2WPKH_encrypted() throws Exception {
        wallet = Wallet.createDeterministic(TestWithWallet.UNITTEST, P2PKH);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertTrue(wallet.isDeterministicUpgradeRequired(P2WPKH));
        KeyParameter aesKey = new KeyCrypterScrypt().deriveKey("abc");
        wallet.encrypt(new KeyCrypterScrypt(), aesKey);
        Assert.assertTrue(wallet.isEncrypted());
        Assert.assertEquals(P2PKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2PKH, wallet.freshReceiveAddress().getOutputScriptType());
        try {
            wallet.upgradeToDeterministic(P2WPKH, null);
            Assert.fail();
        } catch (DeterministicUpgradeRequiresPassword e) {
            // Expected.
        }
        wallet.upgradeToDeterministic(P2WPKH, aesKey);
        Assert.assertTrue(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2WPKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2WPKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test
    public void upgradeToDeterministic_noDowngrade_unencrypted() throws Exception {
        wallet = Wallet.createDeterministic(TestWithWallet.UNITTEST, P2WPKH);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2WPKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2WPKH, wallet.freshReceiveAddress().getOutputScriptType());
        wallet.upgradeToDeterministic(P2PKH, null);
        Assert.assertFalse(wallet.isEncrypted());
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2PKH));
        Assert.assertFalse(wallet.isDeterministicUpgradeRequired(P2WPKH));
        Assert.assertEquals(P2WPKH, wallet.currentReceiveAddress().getOutputScriptType());
        Assert.assertEquals(P2WPKH, wallet.freshReceiveAddress().getOutputScriptType());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAddTransactionSignerThatIsNotReady() throws Exception {
        wallet.addTransactionSigner(new NopTransactionSigner(false));
    }

    @Test
    public void watchingMarriedWallet() throws Exception {
        DeterministicKey watchKey = wallet.getWatchingKey();
        String serialized = watchKey.serializePubB58(TestWithWallet.UNITTEST);
        Wallet wallet = Wallet.fromWatchingKeyB58(TestWithWallet.UNITTEST, serialized, 0);
        blockStore = new org.bitcoinj.store.MemoryBlockStore(TestWithWallet.UNITTEST);
        chain = new org.bitcoinj.core.BlockChain(TestWithWallet.UNITTEST, wallet, blockStore);
        final DeterministicKeyChain keyChain = DeterministicKeyChain.builder().random(new SecureRandom()).build();
        DeterministicKey partnerKey = DeterministicKey.deserializeB58(null, keyChain.getWatchingKey().serializePubB58(TestWithWallet.UNITTEST), TestWithWallet.UNITTEST);
        TransactionSigner signer = new TransactionSigner() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public boolean signInputs(ProposedTransaction propTx, KeyBag keyBag) {
                Assert.assertEquals(propTx.partialTx.getInputs().size(), propTx.keyPaths.size());
                List<ChildNumber> externalZeroLeaf = ImmutableList.<ChildNumber>builder().addAll(ACCOUNT_ZERO_PATH).addAll(EXTERNAL_SUBPATH).add(ChildNumber.ZERO).build();
                for (TransactionInput input : propTx.partialTx.getInputs()) {
                    List<ChildNumber> keypath = propTx.keyPaths.get(input.getConnectedOutput().getScriptPubKey());
                    Assert.assertNotNull(keypath);
                    Assert.assertEquals(externalZeroLeaf, keypath);
                }
                return true;
            }
        };
        wallet.addTransactionSigner(signer);
        MarriedKeyChain chain = MarriedKeyChain.builder().random(new SecureRandom()).followingKeys(partnerKey).build();
        wallet.addAndActivateHDChain(chain);
        Address myAddress = wallet.currentAddress(RECEIVE_FUNDS);
        sendMoneyToWallet(wallet, BEST_CHAIN, COIN, myAddress);
        SendRequest req = SendRequest.emptyWallet(OTHER_ADDRESS);
        req.missingSigsMode = MissingSigsMode.USE_DUMMY_SIG;
        wallet.completeTx(req);
    }

    @Test
    public void sendRequestExchangeRate() throws Exception {
        receiveATransaction(wallet, myAddress);
        SendRequest sendRequest = SendRequest.to(myAddress, COIN);
        sendRequest.exchangeRate = new org.bitcoinj.utils.ExchangeRate(Fiat.parseFiat("EUR", "500"));
        wallet.completeTx(sendRequest);
        Assert.assertEquals(sendRequest.exchangeRate, sendRequest.tx.getExchangeRate());
    }

    @Test
    public void sendRequestMemo() throws Exception {
        receiveATransaction(wallet, myAddress);
        SendRequest sendRequest = SendRequest.to(myAddress, COIN);
        sendRequest.memo = "memo";
        wallet.completeTx(sendRequest);
        Assert.assertEquals(sendRequest.memo, sendRequest.tx.getMemo());
    }

    @Test(expected = IllegalStateException.class)
    public void sendCoinsNoBroadcasterTest() throws InsufficientMoneyException {
        ECKey key = ECKey.fromPrivate(BigInteger.TEN);
        SendRequest req = SendRequest.to(OTHER_ADDRESS.getParameters(), key, SATOSHI.multiply(12));
        wallet.sendCoins(req);
    }

    @Test
    public void sendCoinsWithBroadcasterTest() throws InsufficientMoneyException {
        ECKey key = ECKey.fromPrivate(BigInteger.TEN);
        receiveATransactionAmount(wallet, myAddress, COIN);
        MockTransactionBroadcaster broadcaster = new MockTransactionBroadcaster(wallet);
        wallet.setTransactionBroadcaster(broadcaster);
        SendRequest req = SendRequest.to(OTHER_ADDRESS.getParameters(), key, Coin.CENT);
        wallet.sendCoins(req);
    }

    @Test
    public void fromKeys() {
        ECKey key = ECKey.fromPrivate(HEX.decode("00905b93f990267f4104f316261fc10f9f983551f9ef160854f40102eb71cffdcc"));
        Wallet wallet = Wallet.fromKeys(TestWithWallet.UNITTEST, Arrays.asList(key));
        Assert.assertEquals(1, wallet.getImportedKeys().size());
        Assert.assertEquals(key, wallet.getImportedKeys().get(0));
        wallet.upgradeToDeterministic(P2PKH, null);
        String seed = wallet.getKeyChainSeed().toHexString();
        Assert.assertEquals("5ca8cd6c01aa004d3c5396c628b78a4a89462f412f460a845b594ac42eceaa264b0e14dcd4fe73d4ed08ce06f4c28facfa85042d26d784ab2798a870bb7af556", seed);
    }

    @Test
    public void reset() {
        sendMoneyToWallet(BEST_CHAIN, COIN, myAddress);
        Assert.assertNotEquals(Coin.ZERO, wallet.getBalance(ESTIMATED));
        Assert.assertNotEquals(0, wallet.getTransactions(false).size());
        Assert.assertNotEquals(0, wallet.getUnspents().size());
        wallet.reset();
        Assert.assertEquals(Coin.ZERO, wallet.getBalance(ESTIMATED));
        Assert.assertEquals(0, wallet.getTransactions(false).size());
        Assert.assertEquals(0, wallet.getUnspents().size());
    }

    @Test
    public void totalReceivedSent() throws Exception {
        // Receive 4 BTC in 2 separate transactions
        Transaction toMe1 = FakeTxBuilder.createFakeTxWithoutChangeAddress(TestWithWallet.UNITTEST, COIN.multiply(2), myAddress);
        Transaction toMe2 = FakeTxBuilder.createFakeTxWithoutChangeAddress(TestWithWallet.UNITTEST, COIN.multiply(2), myAddress);
        sendMoneyToWallet(BEST_CHAIN, toMe1, toMe2);
        // Check we calculate the total received correctly
        Assert.assertEquals(COIN.multiply(4), wallet.getTotalReceived());
        // Send 3 BTC in a single transaction
        SendRequest req = SendRequest.to(OTHER_ADDRESS, COIN.multiply(3));
        wallet.completeTx(req);
        sendMoneyToWallet(BEST_CHAIN, req.tx);
        // Check that we still have the same totalReceived, since the above tx will have sent us change back
        Assert.assertEquals(COIN.multiply(4), wallet.getTotalReceived());
        Assert.assertEquals(COIN.multiply(3), wallet.getTotalSent());
        // TODO: test shared wallet calculation here
    }

    @Test
    public void testIrrelevantDoubleSpend() throws Exception {
        Transaction tx0 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST);
        Transaction tx1 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST);
        Transaction tx2 = new Transaction(TestWithWallet.UNITTEST);
        tx2.addInput(tx0.getOutput(0));
        tx2.addOutput(COIN, myAddress);
        tx2.addOutput(COIN, OTHER_ADDRESS);
        sendMoneyToWallet(BEST_CHAIN, tx2, tx1, tx0);
        // tx3 and tx4 double spend each other
        Transaction tx3 = new Transaction(TestWithWallet.UNITTEST);
        tx3.addInput(tx1.getOutput(0));
        tx3.addOutput(COIN, myAddress);
        tx3.addOutput(COIN, OTHER_ADDRESS);
        wallet.receivePending(tx3, null);
        // tx4 also spends irrelevant output from tx2
        Transaction tx4 = new Transaction(TestWithWallet.UNITTEST);
        tx4.addInput(tx1.getOutput(0));// spends same output

        tx4.addInput(tx2.getOutput(1));
        tx4.addOutput(COIN, OTHER_ADDRESS);
        // tx4 does not actually get added to wallet here since it by itself is irrelevant
        sendMoneyToWallet(BEST_CHAIN, tx4);
        // since tx4 is not saved, tx2 output 1 will have bad spentBy
        wallet = roundTrip(wallet);
        Assert.assertTrue(wallet.isConsistent());
    }

    @Test
    public void overridingDeadTxTest() throws Exception {
        Transaction tx0 = FakeTxBuilder.createFakeTx(TestWithWallet.UNITTEST);
        Transaction tx1 = new Transaction(TestWithWallet.UNITTEST);
        tx1.addInput(tx0.getOutput(0));
        tx1.addOutput(COIN, OTHER_ADDRESS);
        tx1.addOutput(COIN, OTHER_ADDRESS);
        tx1.addOutput(COIN, myAddress);// to save this in wallet

        sendMoneyToWallet(BEST_CHAIN, tx0, tx1);
        // tx2, tx3 and tx4 double spend each other
        Transaction tx2 = new Transaction(TestWithWallet.UNITTEST);
        tx2.addInput(tx1.getOutput(0));
        tx2.addInput(tx1.getOutput(1));
        tx2.addOutput(COIN, myAddress);
        tx2.addOutput(COIN, OTHER_ADDRESS);
        wallet.receivePending(tx2, null);
        // irrelevant to the wallet
        Transaction tx3 = new Transaction(TestWithWallet.UNITTEST);
        tx3.addInput(tx1.getOutput(0));// spends same output as tx2

        tx3.addOutput(COIN, OTHER_ADDRESS);
        // irrelevant to the wallet
        Transaction tx4 = new Transaction(TestWithWallet.UNITTEST);
        tx4.addInput(tx1.getOutput(1));// spends different output, but also in tx2

        tx4.addOutput(COIN, OTHER_ADDRESS);
        assertUnspent(tx1);
        assertPending(tx2);
        sendMoneyToWallet(BEST_CHAIN, tx3);
        assertUnspent(tx1);
        assertDead(tx2);
        Assert.assertEquals(2, wallet.transactions.size());// tx3 not saved

        sendMoneyToWallet(BEST_CHAIN, tx4);
        assertUnspent(tx1);
        assertDead(tx2);
        Assert.assertEquals(2, wallet.transactions.size());// tx4 not saved

        // this will fail if tx4 does not get disconnected from tx1
        wallet = roundTrip(wallet);
        Assert.assertTrue(wallet.isConsistent());
    }

    @Test
    public void scriptTypeKeyChainRestrictions() {
        // Set up chains: basic chain, P2PKH deterministric chain, P2WPKH deterministic chain.
        DeterministicKeyChain p2pkhChain = DeterministicKeyChain.builder().random(new SecureRandom()).outputScriptType(P2PKH).build();
        DeterministicKeyChain p2wpkhChain = DeterministicKeyChain.builder().random(new SecureRandom()).outputScriptType(P2WPKH).build();
        KeyChainGroup kcg = KeyChainGroup.builder(TestWithWallet.UNITTEST).addChain(p2pkhChain).addChain(p2wpkhChain).build();
        Wallet wallet = new Wallet(TestWithWallet.UNITTEST, kcg);
        // Set up one key from each chain.
        ECKey importedKey = new ECKey();
        wallet.importKey(importedKey);
        ECKey p2pkhKey = p2pkhChain.getKey(KeyPurpose.RECEIVE_FUNDS);
        ECKey p2wpkhKey = p2wpkhChain.getKey(KeyPurpose.RECEIVE_FUNDS);
        // Test imported key: it's not limited to script type.
        Assert.assertTrue(wallet.isAddressMine(LegacyAddress.fromKey(TestWithWallet.UNITTEST, importedKey)));
        Assert.assertTrue(wallet.isAddressMine(SegwitAddress.fromKey(TestWithWallet.UNITTEST, importedKey)));
        Assert.assertEquals(importedKey, wallet.findKeyFromAddress(LegacyAddress.fromKey(TestWithWallet.UNITTEST, importedKey)));
        Assert.assertEquals(importedKey, wallet.findKeyFromAddress(SegwitAddress.fromKey(TestWithWallet.UNITTEST, importedKey)));
        // Test key from P2PKH chain: it's limited to P2PKH addresses
        Assert.assertTrue(wallet.isAddressMine(LegacyAddress.fromKey(TestWithWallet.UNITTEST, p2pkhKey)));
        Assert.assertFalse(wallet.isAddressMine(SegwitAddress.fromKey(TestWithWallet.UNITTEST, p2pkhKey)));
        Assert.assertEquals(p2pkhKey, wallet.findKeyFromAddress(LegacyAddress.fromKey(TestWithWallet.UNITTEST, p2pkhKey)));
        Assert.assertNull(wallet.findKeyFromAddress(SegwitAddress.fromKey(TestWithWallet.UNITTEST, p2pkhKey)));
        // Test key from P2WPKH chain: it's limited to P2WPKH addresses
        Assert.assertFalse(wallet.isAddressMine(LegacyAddress.fromKey(TestWithWallet.UNITTEST, p2wpkhKey)));
        Assert.assertTrue(wallet.isAddressMine(SegwitAddress.fromKey(TestWithWallet.UNITTEST, p2wpkhKey)));
        Assert.assertNull(wallet.findKeyFromAddress(LegacyAddress.fromKey(TestWithWallet.UNITTEST, p2wpkhKey)));
        Assert.assertEquals(p2wpkhKey, wallet.findKeyFromAddress(SegwitAddress.fromKey(TestWithWallet.UNITTEST, p2wpkhKey)));
    }

    @Test
    public void roundtripViaMnemonicCode() {
        Wallet wallet = Wallet.createDeterministic(TestWithWallet.UNITTEST, P2WPKH);
        List<String> mnemonicCode = wallet.getKeyChainSeed().getMnemonicCode();
        final DeterministicSeed clonedSeed = new DeterministicSeed(mnemonicCode, null, "", wallet.getEarliestKeyCreationTime());
        Wallet clone = Wallet.fromSeed(TestWithWallet.UNITTEST, clonedSeed, P2WPKH);
        Assert.assertEquals(wallet.currentReceiveKey(), clone.currentReceiveKey());
        Assert.assertEquals(wallet.freshReceiveAddress(P2PKH), clone.freshReceiveAddress(P2PKH));
    }
}

