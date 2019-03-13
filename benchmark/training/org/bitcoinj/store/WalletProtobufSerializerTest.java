/**
 * Copyright 2012 Google Inc.
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
package org.bitcoinj.store;


import Block.BLOCK_HEIGHT_GENESIS;
import Block.BLOCK_VERSION_GENESIS;
import BlockChain.NewBlockType.BEST_CHAIN;
import Coin.COIN;
import Coin.ZERO;
import ConfidenceType.DEAD;
import KeyChain.KeyPurpose.RECEIVE_FUNDS;
import Protos.Key.Type.ORIGINAL;
import Protos.Transaction.Pool.PENDING;
import Protos.Wallet.Builder;
import Purpose.RAISE_FEE;
import TransactionConfidence.Source.NETWORK;
import TransactionInput.NO_SEQUENCE;
import UnreadableWalletException.FutureVersion;
import Utils.HEX;
import Wallet.BalanceType.ESTIMATED;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.BlockTest;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.testing.FakeTxBuilder;
import org.bitcoinj.testing.FooWalletExtension;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.MarriedKeyChain;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import org.bitcoinj.wallet.WalletProtobufSerializer;
import org.bitcoinj.wallet.WalletTransaction.Pool;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.junit.Assert;
import org.junit.Test;


public class WalletProtobufSerializerTest {
    private static final NetworkParameters UNITTEST = UnitTestParams.get();

    private static final NetworkParameters MAINNET = MainNetParams.get();

    private ECKey myKey;

    private ECKey myWatchedKey;

    private Address myAddress;

    private Wallet myWallet;

    public static String WALLET_DESCRIPTION = "The quick brown fox lives in \u4f26\u6566";// Beijing in Chinese


    private long mScriptCreationTime;

    @Test
    public void empty() throws Exception {
        // Check the base case of a wallet with one key and no transactions.
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals(0, wallet1.getTransactions(true).size());
        Assert.assertEquals(ZERO, wallet1.getBalance());
        ECKey foundKey = wallet1.findKeyFromPubKeyHash(myKey.getPubKeyHash(), null);
        Assert.assertArrayEquals(myKey.getPubKey(), foundKey.getPubKey());
        Assert.assertArrayEquals(myKey.getPrivKeyBytes(), foundKey.getPrivKeyBytes());
        Assert.assertEquals(myKey.getCreationTimeSeconds(), foundKey.getCreationTimeSeconds());
        Assert.assertEquals(mScriptCreationTime, wallet1.getWatchedScripts().get(0).getCreationTimeSeconds());
        Assert.assertEquals(1, wallet1.getWatchedScripts().size());
        Assert.assertEquals(ScriptBuilder.createOutputScript(LegacyAddress.fromKey(WalletProtobufSerializerTest.UNITTEST, myWatchedKey)), wallet1.getWatchedScripts().get(0));
        Assert.assertEquals(WalletProtobufSerializerTest.WALLET_DESCRIPTION, wallet1.getDescription());
    }

    @Test
    public void oneTx() throws Exception {
        // Check basic tx serialization.
        Coin v1 = COIN;
        Transaction t1 = FakeTxBuilder.createFakeTx(WalletProtobufSerializerTest.UNITTEST, v1, myAddress);
        t1.getConfidence().markBroadcastBy(new org.bitcoinj.core.PeerAddress(WalletProtobufSerializerTest.UNITTEST, InetAddress.getByName("1.2.3.4")));
        t1.getConfidence().markBroadcastBy(new org.bitcoinj.core.PeerAddress(WalletProtobufSerializerTest.UNITTEST, InetAddress.getByName("5.6.7.8")));
        t1.getConfidence().setSource(NETWORK);
        myWallet.receivePending(t1, null);
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals(1, wallet1.getTransactions(true).size());
        Assert.assertEquals(v1, wallet1.getBalance(ESTIMATED));
        Transaction t1copy = wallet1.getTransaction(t1.getTxId());
        Assert.assertArrayEquals(t1.unsafeBitcoinSerialize(), t1copy.unsafeBitcoinSerialize());
        Assert.assertEquals(2, t1copy.getConfidence().numBroadcastPeers());
        Assert.assertNotNull(t1copy.getConfidence().getLastBroadcastedAt());
        Assert.assertEquals(NETWORK, t1copy.getConfidence().getSource());
        Protos.Wallet walletProto = new WalletProtobufSerializer().walletToProto(myWallet);
        Assert.assertEquals(ORIGINAL, walletProto.getKey(0).getType());
        Assert.assertEquals(0, walletProto.getExtensionCount());
        Assert.assertEquals(1, walletProto.getTransactionCount());
        Assert.assertEquals(6, walletProto.getKeyCount());
        Protos.Transaction t1p = walletProto.getTransaction(0);
        Assert.assertEquals(0, t1p.getBlockHashCount());
        Assert.assertArrayEquals(t1.getTxId().getBytes(), t1p.getHash().toByteArray());
        Assert.assertEquals(PENDING, t1p.getPool());
        Assert.assertFalse(t1p.hasLockTime());
        Assert.assertFalse(t1p.getTransactionInput(0).hasSequence());
        Assert.assertArrayEquals(t1.getInputs().get(0).getOutpoint().getHash().getBytes(), t1p.getTransactionInput(0).getTransactionOutPointHash().toByteArray());
        Assert.assertEquals(0, t1p.getTransactionInput(0).getTransactionOutPointIndex());
        Assert.assertEquals(t1p.getTransactionOutput(0).getValue(), v1.value);
    }

    @Test
    public void raiseFeeTx() throws Exception {
        // Check basic tx serialization.
        Coin v1 = COIN;
        Transaction t1 = FakeTxBuilder.createFakeTx(WalletProtobufSerializerTest.UNITTEST, v1, myAddress);
        t1.setPurpose(RAISE_FEE);
        myWallet.receivePending(t1, null);
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Transaction t1copy = wallet1.getTransaction(t1.getTxId());
        Assert.assertEquals(RAISE_FEE, t1copy.getPurpose());
    }

    @Test
    public void doubleSpend() throws Exception {
        // Check that we can serialize double spends correctly, as this is a slightly tricky case.
        FakeTxBuilder.DoubleSpends doubleSpends = FakeTxBuilder.createFakeDoubleSpendTxns(WalletProtobufSerializerTest.UNITTEST, myAddress);
        // t1 spends to our wallet.
        myWallet.receivePending(doubleSpends.t1, null);
        // t2 rolls back t1 and spends somewhere else.
        myWallet.receiveFromBlock(doubleSpends.t2, null, BEST_CHAIN, 0);
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals(1, wallet1.getTransactions(true).size());
        Transaction t1 = wallet1.getTransaction(doubleSpends.t1.getTxId());
        Assert.assertEquals(DEAD, t1.getConfidence().getConfidenceType());
        Assert.assertEquals(ZERO, wallet1.getBalance());
        // TODO: Wallet should store overriding transactions even if they are not wallet-relevant.
        // assertEquals(doubleSpends.t2, t1.getConfidence().getOverridingTransaction());
    }

    @Test
    public void testKeys() throws Exception {
        for (int i = 0; i < 20; i++) {
            myKey = new ECKey();
            myAddress = LegacyAddress.fromKey(WalletProtobufSerializerTest.UNITTEST, myKey);
            myWallet = new Wallet(WalletProtobufSerializerTest.UNITTEST);
            myWallet.importKey(myKey);
            Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
            ECKey foundKey = wallet1.findKeyFromPubKeyHash(myKey.getPubKeyHash(), null);
            Assert.assertArrayEquals(myKey.getPubKey(), foundKey.getPubKey());
            Assert.assertArrayEquals(myKey.getPrivKeyBytes(), foundKey.getPrivKeyBytes());
        }
    }

    @Test
    public void testLastBlockSeenHash() throws Exception {
        // Test the lastBlockSeenHash field works.
        // LastBlockSeenHash should be empty if never set.
        Wallet wallet = new Wallet(WalletProtobufSerializerTest.UNITTEST);
        Protos.Wallet walletProto = new WalletProtobufSerializer().walletToProto(wallet);
        ByteString lastSeenBlockHash = walletProto.getLastSeenBlockHash();
        Assert.assertTrue(lastSeenBlockHash.isEmpty());
        // Create a block.
        Block block = WalletProtobufSerializerTest.UNITTEST.getDefaultSerializer().makeBlock(ByteStreams.toByteArray(BlockTest.class.getResourceAsStream("block_testnet700000.dat")));
        Sha256Hash blockHash = block.getHash();
        wallet.setLastBlockSeenHash(blockHash);
        wallet.setLastBlockSeenHeight(1);
        // Roundtrip the wallet and check it has stored the blockHash.
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(wallet);
        Assert.assertEquals(blockHash, wallet1.getLastBlockSeenHash());
        Assert.assertEquals(1, wallet1.getLastBlockSeenHeight());
        // Test the Satoshi genesis block (hash of all zeroes) is roundtripped ok.
        Block genesisBlock = WalletProtobufSerializerTest.MAINNET.getGenesisBlock();
        wallet.setLastBlockSeenHash(genesisBlock.getHash());
        Wallet wallet2 = WalletProtobufSerializerTest.roundTrip(wallet);
        Assert.assertEquals(genesisBlock.getHash(), wallet2.getLastBlockSeenHash());
    }

    @Test
    public void testSequenceNumber() throws Exception {
        Wallet wallet = new Wallet(WalletProtobufSerializerTest.UNITTEST);
        Transaction tx1 = FakeTxBuilder.createFakeTx(WalletProtobufSerializerTest.UNITTEST, COIN, wallet.currentReceiveAddress());
        tx1.getInput(0).setSequenceNumber(NO_SEQUENCE);
        wallet.receivePending(tx1, null);
        Transaction tx2 = FakeTxBuilder.createFakeTx(WalletProtobufSerializerTest.UNITTEST, COIN, wallet.currentReceiveAddress());
        tx2.getInput(0).setSequenceNumber(((TransactionInput.NO_SEQUENCE) - 1));
        wallet.receivePending(tx2, null);
        Wallet walletCopy = WalletProtobufSerializerTest.roundTrip(wallet);
        Transaction tx1copy = Preconditions.checkNotNull(walletCopy.getTransaction(tx1.getTxId()));
        Assert.assertEquals(NO_SEQUENCE, tx1copy.getInput(0).getSequenceNumber());
        Transaction tx2copy = Preconditions.checkNotNull(walletCopy.getTransaction(tx2.getTxId()));
        Assert.assertEquals(((TransactionInput.NO_SEQUENCE) - 1), tx2copy.getInput(0).getSequenceNumber());
    }

    @Test
    public void testAppearedAtChainHeightDepthAndWorkDone() throws Exception {
        // Test the TransactionConfidence appearedAtChainHeight, depth and workDone field are stored.
        BlockChain chain = new BlockChain(WalletProtobufSerializerTest.UNITTEST, myWallet, new MemoryBlockStore(WalletProtobufSerializerTest.UNITTEST));
        final ArrayList<Transaction> txns = new ArrayList<>(2);
        myWallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                txns.add(tx);
            }
        });
        // Start by building two blocks on top of the genesis block.
        Block b1 = WalletProtobufSerializerTest.UNITTEST.getGenesisBlock().createNextBlock(myAddress);
        BigInteger work1 = b1.getWork();
        Assert.assertTrue(((work1.signum()) > 0));
        Block b2 = b1.createNextBlock(myAddress);
        BigInteger work2 = b2.getWork();
        Assert.assertTrue(((work2.signum()) > 0));
        Assert.assertTrue(chain.add(b1));
        Assert.assertTrue(chain.add(b2));
        // We now have the following chain:
        // genesis -> b1 -> b2
        // Check the transaction confidence levels are correct before wallet roundtrip.
        Threading.waitForUserCode();
        Assert.assertEquals(2, txns.size());
        TransactionConfidence confidence0 = txns.get(0).getConfidence();
        TransactionConfidence confidence1 = txns.get(1).getConfidence();
        Assert.assertEquals(1, confidence0.getAppearedAtChainHeight());
        Assert.assertEquals(2, confidence1.getAppearedAtChainHeight());
        Assert.assertEquals(2, confidence0.getDepthInBlocks());
        Assert.assertEquals(1, confidence1.getDepthInBlocks());
        // Roundtrip the wallet and check it has stored the depth and workDone.
        Wallet rebornWallet = WalletProtobufSerializerTest.roundTrip(myWallet);
        Set<Transaction> rebornTxns = rebornWallet.getTransactions(false);
        Assert.assertEquals(2, rebornTxns.size());
        // The transactions are not guaranteed to be in the same order so sort them to be in chain height order if required.
        Iterator<Transaction> it = rebornTxns.iterator();
        Transaction txA = it.next();
        Transaction txB = it.next();
        Transaction rebornTx0;
        Transaction rebornTx1;
        if ((txA.getConfidence().getAppearedAtChainHeight()) == 1) {
            rebornTx0 = txA;
            rebornTx1 = txB;
        } else {
            rebornTx0 = txB;
            rebornTx1 = txA;
        }
        TransactionConfidence rebornConfidence0 = rebornTx0.getConfidence();
        TransactionConfidence rebornConfidence1 = rebornTx1.getConfidence();
        Assert.assertEquals(1, rebornConfidence0.getAppearedAtChainHeight());
        Assert.assertEquals(2, rebornConfidence1.getAppearedAtChainHeight());
        Assert.assertEquals(2, rebornConfidence0.getDepthInBlocks());
        Assert.assertEquals(1, rebornConfidence1.getDepthInBlocks());
    }

    @Test
    public void testRoundTripNormalWallet() throws Exception {
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals(0, wallet1.getTransactions(true).size());
        Assert.assertEquals(ZERO, wallet1.getBalance());
        ECKey foundKey = wallet1.findKeyFromPubKeyHash(myKey.getPubKeyHash(), null);
        Assert.assertArrayEquals(myKey.getPubKey(), foundKey.getPubKey());
        Assert.assertArrayEquals(myKey.getPrivKeyBytes(), foundKey.getPrivKeyBytes());
        Assert.assertEquals(myKey.getCreationTimeSeconds(), foundKey.getCreationTimeSeconds());
    }

    @Test
    public void testRoundTripWatchingWallet() throws Exception {
        final String xpub = "tpubD9LrDvFDrB6wYNhbR2XcRRaT4yCa37TjBR3YthBQvrtEwEq6CKeEXUs3TppQd38rfxmxD1qLkC99iP3vKcKwLESSSYdFAftbrpuhSnsw6XM";
        final long creationTimeSeconds = 1457019819;
        Wallet wallet = Wallet.fromWatchingKeyB58(WalletProtobufSerializerTest.UNITTEST, xpub, creationTimeSeconds);
        Wallet wallet2 = WalletProtobufSerializerTest.roundTrip(wallet);
        Wallet wallet3 = WalletProtobufSerializerTest.roundTrip(wallet2);
        Assert.assertEquals(xpub, wallet.getWatchingKey().serializePubB58(WalletProtobufSerializerTest.UNITTEST));
        Assert.assertEquals(creationTimeSeconds, wallet.getWatchingKey().getCreationTimeSeconds());
        Assert.assertEquals(creationTimeSeconds, wallet2.getWatchingKey().getCreationTimeSeconds());
        Assert.assertEquals(creationTimeSeconds, wallet3.getWatchingKey().getCreationTimeSeconds());
        Assert.assertEquals(creationTimeSeconds, wallet.getEarliestKeyCreationTime());
        Assert.assertEquals(creationTimeSeconds, wallet2.getEarliestKeyCreationTime());
        Assert.assertEquals(creationTimeSeconds, wallet3.getEarliestKeyCreationTime());
    }

    @Test
    public void testRoundTripMarriedWallet() throws Exception {
        // create 2-of-2 married wallet
        myWallet = new Wallet(WalletProtobufSerializerTest.UNITTEST);
        final DeterministicKeyChain partnerChain = DeterministicKeyChain.builder().random(new SecureRandom()).build();
        DeterministicKey partnerKey = DeterministicKey.deserializeB58(null, partnerChain.getWatchingKey().serializePubB58(WalletProtobufSerializerTest.UNITTEST), WalletProtobufSerializerTest.UNITTEST);
        MarriedKeyChain chain = MarriedKeyChain.builder().random(new SecureRandom()).followingKeys(partnerKey).threshold(2).build();
        myWallet.addAndActivateHDChain(chain);
        myAddress = myWallet.currentAddress(RECEIVE_FUNDS);
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals(0, wallet1.getTransactions(true).size());
        Assert.assertEquals(ZERO, wallet1.getBalance());
        Assert.assertEquals(2, wallet1.getActiveKeyChain().getSigsRequiredToSpend());
        Assert.assertEquals(myAddress, wallet1.currentAddress(RECEIVE_FUNDS));
    }

    @Test
    public void roundtripVersionTwoTransaction() throws Exception {
        Transaction tx = new Transaction(WalletProtobufSerializerTest.UNITTEST, HEX.decode("0200000001d7902864af9310420c6e606b814c8f89f7902d40c130594e85df2e757a7cc301070000006b483045022100ca1757afa1af85c2bb014382d9ce411e1628d2b3d478df9d5d3e9e93cb25dcdd02206c5d272b31a23baf64e82793ee5c816e2bbef251e733a638b630ff2331fc83ba0121026ac2316508287761befbd0f7495ea794b396dbc5b556bf276639f56c0bd08911feffffff0274730700000000001976a91456da2d038a098c42390c77ef163e1cc23aedf24088ac91062300000000001976a9148ebf3467b9a8d7ae7b290da719e61142793392c188ac22e00600"));
        Assert.assertEquals(tx.getVersion(), 2);
        Assert.assertEquals(tx.getTxId().toString(), "0321b1413ed9048199815bd6bc2650cab1a9e8d543f109a42c769b1f18df4174");
        myWallet.addWalletTransaction(new org.bitcoinj.wallet.WalletTransaction(Pool.UNSPENT, tx));
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Transaction tx2 = wallet1.getTransaction(tx.getTxId());
        Assert.assertEquals(Preconditions.checkNotNull(tx2).getVersion(), 2);
    }

    @Test
    public void coinbaseTxns() throws Exception {
        // Covers issue 420 where the outpoint index of a coinbase tx input was being mis-serialized.
        Block b = WalletProtobufSerializerTest.UNITTEST.getGenesisBlock().createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, myKey.getPubKey(), FIFTY_COINS, BLOCK_HEIGHT_GENESIS);
        Transaction coinbase = b.getTransactions().get(0);
        Assert.assertTrue(coinbase.isCoinBase());
        BlockChain chain = new BlockChain(WalletProtobufSerializerTest.UNITTEST, myWallet, new MemoryBlockStore(WalletProtobufSerializerTest.UNITTEST));
        Assert.assertTrue(chain.add(b));
        // Wallet now has a coinbase tx in it.
        Assert.assertEquals(1, myWallet.getTransactions(true).size());
        Assert.assertTrue(myWallet.getTransaction(coinbase.getTxId()).isCoinBase());
        Wallet wallet2 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals(1, wallet2.getTransactions(true).size());
        Assert.assertTrue(wallet2.getTransaction(coinbase.getTxId()).isCoinBase());
    }

    @Test
    public void tags() throws Exception {
        myWallet.setTag("foo", ByteString.copyFromUtf8("bar"));
        Assert.assertEquals("bar", myWallet.getTag("foo").toStringUtf8());
        myWallet = WalletProtobufSerializerTest.roundTrip(myWallet);
        Assert.assertEquals("bar", myWallet.getTag("foo").toStringUtf8());
    }

    @Test
    public void extensions() throws Exception {
        myWallet.addExtension(new FooWalletExtension("com.whatever.required", true));
        Protos.Wallet proto = new WalletProtobufSerializer().walletToProto(myWallet);
        // Initial extension is mandatory: try to read it back into a wallet that doesn't know about it.
        try {
            new WalletProtobufSerializer().readWallet(WalletProtobufSerializerTest.UNITTEST, null, proto);
            Assert.fail();
        } catch (UnreadableWalletException e) {
            Assert.assertTrue(e.getMessage().contains("mandatory"));
        }
        Wallet wallet = new WalletProtobufSerializer().readWallet(WalletProtobufSerializerTest.UNITTEST, new WalletExtension[]{ new FooWalletExtension("com.whatever.required", true) }, proto);
        Assert.assertTrue(wallet.getExtensions().containsKey("com.whatever.required"));
        // Non-mandatory extensions are ignored if the wallet doesn't know how to read them.
        Wallet wallet2 = new Wallet(WalletProtobufSerializerTest.UNITTEST);
        wallet2.addExtension(new FooWalletExtension("com.whatever.optional", false));
        Protos.Wallet proto2 = new WalletProtobufSerializer().walletToProto(wallet2);
        Wallet wallet5 = new WalletProtobufSerializer().readWallet(WalletProtobufSerializerTest.UNITTEST, null, proto2);
        Assert.assertEquals(0, wallet5.getExtensions().size());
    }

    @Test
    public void extensionsWithError() throws Exception {
        WalletExtension extension = new WalletExtension() {
            @Override
            public String getWalletExtensionID() {
                return "test";
            }

            @Override
            public boolean isWalletExtensionMandatory() {
                return false;
            }

            @Override
            public byte[] serializeWalletExtension() {
                return new byte[0];
            }

            @Override
            public void deserializeWalletExtension(Wallet containingWallet, byte[] data) throws Exception {
                throw new NullPointerException();// Something went wrong!

            }
        };
        myWallet.addExtension(extension);
        Protos.Wallet proto = new WalletProtobufSerializer().walletToProto(myWallet);
        Wallet wallet = new WalletProtobufSerializer().readWallet(WalletProtobufSerializerTest.UNITTEST, new WalletExtension[]{ extension }, proto);
        Assert.assertEquals(0, wallet.getExtensions().size());
    }

    @Test(expected = FutureVersion.class)
    public void versions() throws Exception {
        Protos.Wallet.Builder proto = Protos.Wallet.newBuilder(new WalletProtobufSerializer().walletToProto(myWallet));
        proto.setVersion(2);
        new WalletProtobufSerializer().readWallet(WalletProtobufSerializerTest.UNITTEST, null, proto.build());
    }

    @Test
    public void storeWitnessTransactions() throws Exception {
        // 3 inputs, inputs 0 and 2 have witnesses but not input 1
        Transaction tx = new Transaction(WalletProtobufSerializerTest.UNITTEST, HEX.decode("02000000000103fc8a5bea59392369e8a1b635395e507a5cbaeffd926e6967a00d17c669aef1d3010000001716001403c80a334ed6a92cf400d8c708522ea0d6fa5593ffffffffc0166d2218a2613b5384fc2c31238b1b6fa337080a1384220734e1bfd3629d3f0100000000ffffffffc0166d2218a2613b5384fc2c31238b1b6fa337080a1384220734e1bfd3629d3f0200000000ffffffff01a086010000000000220020eb72e573a9513d982a01f0e6a6b53e92764db81a0c26d2be94c5fc5b69a0db7d02473044022048e895b7af715303ce273a2be03d6110ed69b5700679f4f036000f8ba6eddd2802205f780423fcce9b3632ed41681b0a86f5d123766b71f303558c39c1be5fe43e2601210259eb16169df80dbe5856d082a226d84a97d191c895f8046c3544df525028a874000220c0166d2218a2613b5384fc2c31238b1b6fa337080a1384220734e1bfd3629d3f20c0166d2218a2613b5384fc2c31238b1b6fa337080a1384220734e1bfd3629d3f00000000"));
        Assert.assertTrue(tx.hasWitnesses());
        Assert.assertEquals(tx.getTxId().toString(), "1c687396f4710f26206dbdd8bf07a28c76398be6750226ddfaf05a1a80d30034");
        myWallet.addWalletTransaction(new org.bitcoinj.wallet.WalletTransaction(Pool.UNSPENT, tx));
        Wallet wallet1 = WalletProtobufSerializerTest.roundTrip(myWallet);
        Transaction tx2 = wallet1.getTransaction(tx.getTxId());
        Assert.assertEquals(tx.getInput(0).getWitness(), tx2.getInput(0).getWitness());
    }
}

