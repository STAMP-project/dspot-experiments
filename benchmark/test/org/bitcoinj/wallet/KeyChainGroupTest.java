/**
 * Copyright 2014 Mike Hearn
 * Copyright 2019 Andreas Schildbach
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


import DeterministicKeyChain.ACCOUNT_ONE_PATH;
import DeterministicKeyChain.ACCOUNT_ZERO_PATH;
import DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH;
import KeyChain.KeyPurpose.CHANGE;
import KeyChainGroupStructure.DEFAULT;
import KeyPurpose.RECEIVE_FUNDS;
import Protos.Key;
import Script.ScriptType.P2PKH;
import Script.ScriptType.P2WPKH;
import ScriptType.P2SH;
import Threading.SAME_THREAD;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.BloomFilter;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.Assert;
import org.junit.Test;


public class KeyChainGroupTest {
    // Number of initial keys in this tests HD wallet, including interior keys.
    private static final int INITIAL_KEYS = 4;

    private static final int LOOKAHEAD_SIZE = 5;

    private static final NetworkParameters MAINNET = MainNetParams.get();

    private static final String XPUB = "xpub68KFnj3bqUx1s7mHejLDBPywCAKdJEu1b49uniEEn2WSbHmZ7xbLqFTjJbtx1LUcAt1DwhoqWHmo2s5WMJp6wi38CiF2hYD49qVViKVvAoi";

    private static final byte[] ENTROPY = Sha256Hash.hash("don't use a string seed like this in real life".getBytes());

    private KeyChainGroup group;

    private DeterministicKey watchingAccountKey;

    @Test
    public void createDeterministic_P2PKH() {
        KeyChainGroup kcg = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).fromRandom(P2PKH).build();
        // check default
        Address address = kcg.currentAddress(RECEIVE_FUNDS);
        Assert.assertEquals(P2PKH, address.getOutputScriptType());
    }

    @Test
    public void createDeterministic_P2WPKH() {
        KeyChainGroup kcg = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).fromRandom(P2WPKH).build();
        // check default
        Address address = kcg.currentAddress(RECEIVE_FUNDS);
        Assert.assertEquals(P2WPKH, address.getOutputScriptType());
        // check fallback (this will go away at some point)
        address = kcg.freshAddress(RECEIVE_FUNDS, P2PKH, 0);
        Assert.assertEquals(P2PKH, address.getOutputScriptType());
    }

    @Test
    public void freshCurrentKeys() throws Exception {
        /* account key + int/ext parent keys */
        int numKeys = (((((group.getLookaheadSize()) + (group.getLookaheadThreshold())) * 2)// * 2 because of internal/external
         + 1)// keys issued
         + (group.getActiveKeyChain().getAccountPath().size())) + 2;
        Assert.assertEquals(numKeys, group.numKeys());
        Assert.assertEquals((2 * numKeys), group.getBloomFilterElementCount());
        ECKey r1 = group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(numKeys, group.numKeys());
        Assert.assertEquals((2 * numKeys), group.getBloomFilterElementCount());
        ECKey i1 = new ECKey();
        group.importKeys(i1);
        numKeys++;
        Assert.assertEquals(numKeys, group.numKeys());
        Assert.assertEquals((2 * numKeys), group.getBloomFilterElementCount());
        ECKey r2 = group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(r1, r2);
        ECKey c1 = group.currentKey(CHANGE);
        Assert.assertNotEquals(r1, c1);
        ECKey r3 = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertNotEquals(r1, r3);
        ECKey c2 = group.freshKey(CHANGE);
        Assert.assertNotEquals(r3, c2);
        // Current key has not moved and will not under marked as used.
        ECKey r4 = group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(r2, r4);
        ECKey c3 = group.currentKey(CHANGE);
        Assert.assertEquals(c1, c3);
        // Mark as used. Current key is now different.
        group.markPubKeyAsUsed(r4.getPubKey());
        ECKey r5 = group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertNotEquals(r4, r5);
    }

    @Test
    public void freshCurrentKeysForMarriedKeychain() throws Exception {
        group = createMarriedKeyChainGroup();
        try {
            group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void imports() throws Exception {
        ECKey key1 = new ECKey();
        int numKeys = group.numKeys();
        Assert.assertFalse(group.removeImportedKey(key1));
        Assert.assertEquals(1, group.importKeys(ImmutableList.of(key1)));
        Assert.assertEquals((numKeys + 1), group.numKeys());// Lookahead is triggered by requesting a key, so none yet.

        group.removeImportedKey(key1);
        Assert.assertEquals(numKeys, group.numKeys());
    }

    @Test
    public void findKey() throws Exception {
        ECKey a = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        ECKey b = group.freshKey(CHANGE);
        ECKey c = new ECKey();
        ECKey d = new ECKey();// Not imported.

        group.importKeys(c);
        Assert.assertTrue(group.hasKey(a));
        Assert.assertTrue(group.hasKey(b));
        Assert.assertTrue(group.hasKey(c));
        Assert.assertFalse(group.hasKey(d));
        ECKey result = group.findKeyFromPubKey(a.getPubKey());
        Assert.assertEquals(a, result);
        result = group.findKeyFromPubKey(b.getPubKey());
        Assert.assertEquals(b, result);
        result = group.findKeyFromPubKeyHash(a.getPubKeyHash(), null);
        Assert.assertEquals(a, result);
        result = group.findKeyFromPubKeyHash(b.getPubKeyHash(), null);
        Assert.assertEquals(b, result);
        result = group.findKeyFromPubKey(c.getPubKey());
        Assert.assertEquals(c, result);
        result = group.findKeyFromPubKeyHash(c.getPubKeyHash(), null);
        Assert.assertEquals(c, result);
        Assert.assertNull(group.findKeyFromPubKey(d.getPubKey()));
        Assert.assertNull(group.findKeyFromPubKeyHash(d.getPubKeyHash(), null));
    }

    @Test
    public void currentP2SHAddress() throws Exception {
        group = createMarriedKeyChainGroup();
        Address a1 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(P2SH, a1.getOutputScriptType());
        Address a2 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(a1, a2);
        Address a3 = group.currentAddress(CHANGE);
        Assert.assertNotEquals(a2, a3);
    }

    @Test
    public void freshAddress() throws Exception {
        group = createMarriedKeyChainGroup();
        Address a1 = group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Address a2 = group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(P2SH, a1.getOutputScriptType());
        Assert.assertNotEquals(a1, a2);
        group.getBloomFilterElementCount();
        /* master, account, int, ext */
        Assert.assertEquals(((((((group.getLookaheadSize()) + (group.getLookaheadThreshold())) * 2)// * 2 because of internal/external
         + (2 - (group.getLookaheadThreshold())))// keys issued
         + (group.getActiveKeyChain().getAccountPath().size())) + 3), group.numKeys());
        Address a3 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(a2, a3);
    }

    @Test
    public void findRedeemData() throws Exception {
        group = createMarriedKeyChainGroup();
        // test script hash that we don't have
        Assert.assertNull(group.findRedeemDataFromScriptHash(new ECKey().getPubKey()));
        // test our script hash
        Address address = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        RedeemData redeemData = group.findRedeemDataFromScriptHash(address.getHash());
        Assert.assertNotNull(redeemData);
        Assert.assertNotNull(redeemData.redeemScript);
        Assert.assertEquals(2, redeemData.keys.size());
    }

    // Check encryption with and without a basic keychain.
    @Test
    public void encryptionWithoutImported() throws Exception {
        encryption(false);
    }

    @Test
    public void encryptionWithImported() throws Exception {
        encryption(true);
    }

    @Test
    public void encryptionWhilstEmpty() throws Exception {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).lookaheadSize(5).fromRandom(P2PKH).build();
        KeyCrypterScrypt scrypt = new KeyCrypterScrypt(2);
        final KeyParameter aesKey = scrypt.deriveKey("password");
        group.encrypt(scrypt, aesKey);
        Assert.assertTrue(group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS).isEncrypted());
        final ECKey key = group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        group.decrypt(aesKey);
        Assert.assertFalse(Preconditions.checkNotNull(group.findKeyFromPubKey(key.getPubKey())).isEncrypted());
    }

    @Test
    public void bloom() throws Exception {
        ECKey key1 = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        ECKey key2 = new ECKey();
        BloomFilter filter = group.getBloomFilter(group.getBloomFilterElementCount(), 0.001, ((long) ((Math.random()) * (Long.MAX_VALUE))));
        Assert.assertTrue(filter.contains(key1.getPubKeyHash()));
        Assert.assertTrue(filter.contains(key1.getPubKey()));
        Assert.assertFalse(filter.contains(key2.getPubKey()));
        // Check that the filter contains the lookahead buffer and threshold zone.
        for (int i = 0; i < ((KeyChainGroupTest.LOOKAHEAD_SIZE) + (group.getLookaheadThreshold())); i++) {
            ECKey k = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
            Assert.assertTrue(filter.contains(k.getPubKeyHash()));
        }
        // We ran ahead of the lookahead buffer.
        Assert.assertFalse(filter.contains(group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS).getPubKey()));
        group.importKeys(key2);
        filter = group.getBloomFilter(group.getBloomFilterElementCount(), 0.001, ((long) ((Math.random()) * (Long.MAX_VALUE))));
        Assert.assertTrue(filter.contains(key1.getPubKeyHash()));
        Assert.assertTrue(filter.contains(key1.getPubKey()));
        Assert.assertTrue(filter.contains(key2.getPubKey()));
    }

    @Test
    public void findRedeemScriptFromPubHash() throws Exception {
        group = createMarriedKeyChainGroup();
        Address address = group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertTrue(((group.findRedeemDataFromScriptHash(address.getHash())) != null));
        group.getBloomFilterElementCount();
        KeyChainGroup group2 = createMarriedKeyChainGroup();
        group2.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        group2.getBloomFilterElementCount();// Force lookahead.

        // test address from lookahead zone and lookahead threshold zone
        for (int i = 0; i < ((group.getLookaheadSize()) + (group.getLookaheadThreshold())); i++) {
            address = group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
            Assert.assertTrue(((group2.findRedeemDataFromScriptHash(address.getHash())) != null));
        }
        Assert.assertFalse(((group2.findRedeemDataFromScriptHash(group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS).getHash())) != null));
    }

    @Test
    public void bloomFilterForMarriedChains() throws Exception {
        group = createMarriedKeyChainGroup();
        int bufferSize = (group.getLookaheadSize()) + (group.getLookaheadThreshold());
        /* elements */
        int expected = (bufferSize * 2)/* chains */
         * 2;
        Assert.assertEquals(expected, group.getBloomFilterElementCount());
        Address address1 = group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(expected, group.getBloomFilterElementCount());
        BloomFilter filter = group.getBloomFilter((expected + 2), 0.001, ((long) ((Math.random()) * (Long.MAX_VALUE))));
        Assert.assertTrue(filter.contains(address1.getHash()));
        Address address2 = group.freshAddress(CHANGE);
        Assert.assertTrue(filter.contains(address2.getHash()));
        // Check that the filter contains the lookahead buffer.
        /* issued address */
        for (int i = 0; i < (bufferSize - 1); i++) {
            Address address = group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
            Assert.assertTrue(("key " + i), filter.contains(address.getHash()));
        }
        // We ran ahead of the lookahead buffer.
        Assert.assertFalse(filter.contains(group.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS).getHash()));
    }

    @Test
    public void earliestKeyTime() throws Exception {
        long now = Utils.currentTimeSeconds();// mock

        long yesterday = now - 86400;
        Assert.assertEquals(now, group.getEarliestKeyCreationTime());
        Utils.rollMockClock(10000);
        group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Utils.rollMockClock(10000);
        group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        // Check that all keys are assumed to be created at the same instant the seed is.
        Assert.assertEquals(now, group.getEarliestKeyCreationTime());
        ECKey key = new ECKey();
        key.setCreationTimeSeconds(yesterday);
        group.importKeys(key);
        Assert.assertEquals(yesterday, group.getEarliestKeyCreationTime());
    }

    @Test
    public void events() throws Exception {
        // Check that events are registered with the right chains and that if a chain is added, it gets the event
        // listeners attached properly even post-hoc.
        final AtomicReference<ECKey> ran = new AtomicReference<>(null);
        final KeyChainEventListener listener = new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                ran.set(keys.get(0));
            }
        };
        group.addEventListener(listener, SAME_THREAD);
        ECKey key = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(key, ran.getAndSet(null));
        ECKey key2 = new ECKey();
        group.importKeys(key2);
        Assert.assertEquals(key2, ran.getAndSet(null));
        group.removeEventListener(listener);
        ECKey key3 = new ECKey();
        group.importKeys(key3);
        Assert.assertNull(ran.get());
    }

    @Test
    public void serialization() throws Exception {
        int initialKeys = ((KeyChainGroupTest.INITIAL_KEYS) + (group.getActiveKeyChain().getAccountPath().size())) - 1;
        /* for the seed */
        Assert.assertEquals((initialKeys + 1), group.serializeToProtobuf().size());
        group = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, group.serializeToProtobuf());
        group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        DeterministicKey key1 = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        DeterministicKey key2 = group.freshKey(CHANGE);
        group.getBloomFilterElementCount();
        List<Protos.Key> protoKeys1 = group.serializeToProtobuf();
        Assert.assertEquals((((initialKeys + (((KeyChainGroupTest.LOOKAHEAD_SIZE) + 1) * 2)) + 1)/* for the seed */
         + 1), protoKeys1.size());
        group.importKeys(new ECKey());
        List<Protos.Key> protoKeys2 = group.serializeToProtobuf();
        Assert.assertEquals((((initialKeys + (((KeyChainGroupTest.LOOKAHEAD_SIZE) + 1) * 2)) + 1)/* for the seed */
         + 2), protoKeys2.size());
        group = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, protoKeys1);
        Assert.assertEquals((((initialKeys + (((KeyChainGroupTest.LOOKAHEAD_SIZE) + 1) * 2)) + 1)/* for the seed */
         + 1), protoKeys1.size());
        Assert.assertTrue(group.hasKey(key1));
        Assert.assertTrue(group.hasKey(key2));
        Assert.assertEquals(key2, group.currentKey(CHANGE));
        Assert.assertEquals(key1, group.currentKey(KeyChain.KeyPurpose.RECEIVE_FUNDS));
        group = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, protoKeys2);
        Assert.assertEquals((((initialKeys + (((KeyChainGroupTest.LOOKAHEAD_SIZE) + 1) * 2)) + 1)/* for the seed */
         + 2), protoKeys2.size());
        Assert.assertTrue(group.hasKey(key1));
        Assert.assertTrue(group.hasKey(key2));
        KeyCrypterScrypt scrypt = new KeyCrypterScrypt(2);
        final KeyParameter aesKey = scrypt.deriveKey("password");
        group.encrypt(scrypt, aesKey);
        List<Protos.Key> protoKeys3 = group.serializeToProtobuf();
        group = KeyChainGroup.fromProtobufEncrypted(KeyChainGroupTest.MAINNET, protoKeys3, scrypt);
        Assert.assertTrue(group.isEncrypted());
        Assert.assertTrue(group.checkPassword("password"));
        group.decrypt(aesKey);
        // No need for extensive contents testing here, as that's done in the keychain class tests.
    }

    @Test
    public void serializeWatching() throws Exception {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).lookaheadSize(KeyChainGroupTest.LOOKAHEAD_SIZE).addChain(DeterministicKeyChain.builder().watch(watchingAccountKey).outputScriptType(P2PKH).build()).build();
        group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        group.freshKey(CHANGE);
        group.getBloomFilterElementCount();// Force lookahead.

        List<Protos.Key> protoKeys1 = group.serializeToProtobuf();
        Assert.assertEquals((3 + ((((group.getLookaheadSize()) + (group.getLookaheadThreshold())) + 1) * 2)), protoKeys1.size());
        group = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, protoKeys1);
        Assert.assertEquals((3 + ((((group.getLookaheadSize()) + (group.getLookaheadThreshold())) + 1) * 2)), group.serializeToProtobuf().size());
    }

    @Test
    public void serializeMarried() throws Exception {
        group = createMarriedKeyChainGroup();
        Address address1 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertTrue(group.isMarried());
        Assert.assertEquals(2, group.getActiveKeyChain().getSigsRequiredToSpend());
        List<Protos.Key> protoKeys = group.serializeToProtobuf();
        KeyChainGroup group2 = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, protoKeys);
        Assert.assertTrue(group2.isMarried());
        Assert.assertEquals(2, group.getActiveKeyChain().getSigsRequiredToSpend());
        Address address2 = group2.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(address1, address2);
    }

    @Test
    public void addFollowingAccounts() throws Exception {
        Assert.assertFalse(group.isMarried());
        group.addAndActivateHDChain(createMarriedKeyChain());
        Assert.assertTrue(group.isMarried());
    }

    @Test
    public void constructFromSeed() throws Exception {
        ECKey key1 = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        final DeterministicSeed seed = Preconditions.checkNotNull(group.getActiveKeyChain().getSeed());
        KeyChainGroup group2 = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).lookaheadSize(5).addChain(DeterministicKeyChain.builder().seed(seed).outputScriptType(P2PKH).build()).build();
        ECKey key2 = group2.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(key1, key2);
    }

    @Test
    public void addAndActivateHDChain_freshCurrentAddress() {
        DeterministicSeed seed = new DeterministicSeed(KeyChainGroupTest.ENTROPY, "", 0);
        DeterministicKeyChain chain1 = DeterministicKeyChain.builder().seed(seed).accountPath(ACCOUNT_ZERO_PATH).outputScriptType(P2PKH).build();
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).addChain(chain1).build();
        Assert.assertEquals("1M5T5k9yKtGWRtWYMjQtGx3K2sshrABzCT", group.currentAddress(RECEIVE_FUNDS).toString());
        final DeterministicKeyChain chain2 = DeterministicKeyChain.builder().seed(seed).accountPath(ACCOUNT_ONE_PATH).outputScriptType(P2PKH).build();
        group.addAndActivateHDChain(chain2);
        Assert.assertEquals("1JLnjJEXcyByAaW6sqSxNvGiiSEWRhdvPb", group.currentAddress(RECEIVE_FUNDS).toString());
        final DeterministicKeyChain chain3 = DeterministicKeyChain.builder().seed(seed).accountPath(BIP44_ACCOUNT_ZERO_PATH).outputScriptType(P2WPKH).build();
        group.addAndActivateHDChain(chain3);
        Assert.assertEquals("bc1q5fa84aghxd6uzk5g2ywkppmzlut5d77vg8cd20", group.currentAddress(RECEIVE_FUNDS).toString());
    }

    @Test(expected = DeterministicUpgradeRequiredException.class)
    public void deterministicUpgradeRequired() throws Exception {
        // Check that if we try to use HD features in a KCG that only has random keys, we get an exception.
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).build();
        group.importKeys(new ECKey(), new ECKey());
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2PKH, 0));
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2WPKH, 0));
        group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);// throws

    }

    @Test
    public void deterministicUpgradeUnencrypted() throws Exception {
        // Check that a group that contains only random keys has its HD chain created using the private key bytes of
        // the oldest random key, so upgrading the same wallet twice gives the same outcome.
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).lookaheadSize(KeyChainGroupTest.LOOKAHEAD_SIZE).build();
        ECKey key1 = new ECKey();
        Utils.rollMockClock(86400);
        ECKey key2 = new ECKey();
        group.importKeys(key2, key1);
        List<Protos.Key> protobufs = group.serializeToProtobuf();
        group.upgradeToDeterministic(P2PKH, DEFAULT, 0, null);
        Assert.assertFalse(group.isEncrypted());
        Assert.assertFalse(group.isDeterministicUpgradeRequired(P2PKH, 0));
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2WPKH, 0));
        DeterministicKey dkey1 = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        DeterministicSeed seed1 = group.getActiveKeyChain().getSeed();
        Assert.assertNotNull(seed1);
        group = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, protobufs);
        group.upgradeToDeterministic(P2PKH, DEFAULT, 0, null);// Should give same result as last time.

        Assert.assertFalse(group.isEncrypted());
        Assert.assertFalse(group.isDeterministicUpgradeRequired(P2PKH, 0));
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2WPKH, 0));
        DeterministicKey dkey2 = group.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        DeterministicSeed seed2 = group.getActiveKeyChain().getSeed();
        Assert.assertEquals(seed1, seed2);
        Assert.assertEquals(dkey1, dkey2);
        // Check we used the right (oldest) key despite backwards import order.
        byte[] truncatedBytes = Arrays.copyOfRange(key1.getSecretBytes(), 0, 16);
        Assert.assertArrayEquals(seed1.getEntropyBytes(), truncatedBytes);
    }

    @Test
    public void deterministicUpgradeRotating() throws Exception {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).lookaheadSize(KeyChainGroupTest.LOOKAHEAD_SIZE).build();
        long now = Utils.currentTimeSeconds();
        ECKey key1 = new ECKey();
        Utils.rollMockClock(86400);
        ECKey key2 = new ECKey();
        Utils.rollMockClock(86400);
        ECKey key3 = new ECKey();
        group.importKeys(key2, key1, key3);
        group.upgradeToDeterministic(P2PKH, DEFAULT, (now + 10), null);
        DeterministicSeed seed = group.getActiveKeyChain().getSeed();
        Assert.assertNotNull(seed);
        // Check we used the right key: oldest non rotating.
        byte[] truncatedBytes = Arrays.copyOfRange(key2.getSecretBytes(), 0, 16);
        Assert.assertArrayEquals(seed.getEntropyBytes(), truncatedBytes);
    }

    @Test
    public void deterministicUpgradeEncrypted() throws Exception {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).build();
        final ECKey key = new ECKey();
        group.importKeys(key);
        final KeyCrypterScrypt crypter = new KeyCrypterScrypt();
        final KeyParameter aesKey = crypter.deriveKey("abc");
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2PKH, 0));
        group.encrypt(crypter, aesKey);
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2PKH, 0));
        try {
            group.upgradeToDeterministic(P2PKH, DEFAULT, 0, null);
            Assert.fail();
        } catch (DeterministicUpgradeRequiresPassword e) {
            // Expected.
        }
        group.upgradeToDeterministic(P2PKH, DEFAULT, 0, aesKey);
        Assert.assertTrue(group.isEncrypted());
        Assert.assertFalse(group.isDeterministicUpgradeRequired(P2PKH, 0));
        Assert.assertTrue(group.isDeterministicUpgradeRequired(P2WPKH, 0));
        final DeterministicSeed deterministicSeed = group.getActiveKeyChain().getSeed();
        Assert.assertNotNull(deterministicSeed);
        Assert.assertTrue(deterministicSeed.isEncrypted());
        byte[] entropy = Preconditions.checkNotNull(group.getActiveKeyChain().toDecrypted(aesKey).getSeed()).getEntropyBytes();
        // Check we used the right key: oldest non rotating.
        byte[] truncatedBytes = Arrays.copyOfRange(key.getSecretBytes(), 0, 16);
        Assert.assertArrayEquals(entropy, truncatedBytes);
    }

    @Test
    public void markAsUsed() throws Exception {
        Address addr1 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Address addr2 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertEquals(addr1, addr2);
        group.markPubKeyHashAsUsed(addr1.getHash());
        Address addr3 = group.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Assert.assertNotEquals(addr2, addr3);
    }

    @Test
    public void isNotWatching() {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).fromRandom(P2PKH).build();
        final ECKey key = ECKey.fromPrivate(BigInteger.TEN);
        group.importKeys(key);
        Assert.assertFalse(group.isWatching());
    }

    @Test
    public void isWatching() {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).addChain(DeterministicKeyChain.builder().watch(DeterministicKey.deserializeB58("xpub69bjfJ91ikC5ghsqsVDHNq2dRGaV2HHVx7Y9LXi27LN9BWWAXPTQr4u8U3wAtap8bLdHdkqPpAcZmhMS5SnrMQC4ccaoBccFhh315P4UYzo", KeyChainGroupTest.MAINNET)).outputScriptType(P2PKH).build()).build();
        final ECKey watchingKey = ECKey.fromPublicOnly(new ECKey().getPubKeyPoint());
        group.importKeys(watchingKey);
        Assert.assertTrue(group.isWatching());
    }

    @Test(expected = IllegalStateException.class)
    public void isWatchingNoKeys() {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).build();
        group.isWatching();
    }

    @Test(expected = IllegalStateException.class)
    public void isWatchingMixedKeys() {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).addChain(DeterministicKeyChain.builder().watch(DeterministicKey.deserializeB58("xpub69bjfJ91ikC5ghsqsVDHNq2dRGaV2HHVx7Y9LXi27LN9BWWAXPTQr4u8U3wAtap8bLdHdkqPpAcZmhMS5SnrMQC4ccaoBccFhh315P4UYzo", KeyChainGroupTest.MAINNET)).outputScriptType(P2PKH).build()).build();
        final ECKey key = ECKey.fromPrivate(BigInteger.TEN);
        group.importKeys(key);
        group.isWatching();
    }

    @Test
    public void segwitKeyChainGroup() throws Exception {
        group = KeyChainGroup.builder(KeyChainGroupTest.MAINNET).lookaheadSize(KeyChainGroupTest.LOOKAHEAD_SIZE).addChain(DeterministicKeyChain.builder().entropy(KeyChainGroupTest.ENTROPY, 0).outputScriptType(P2WPKH).accountPath(ACCOUNT_ONE_PATH).build()).build();
        Assert.assertEquals(P2WPKH, group.getActiveKeyChain().getOutputScriptType());
        Assert.assertEquals("bc1qhcurdec849thpjjp3e27atvya43gy2snrechd9", group.currentAddress(RECEIVE_FUNDS).toString());
        Assert.assertEquals("bc1qw8sf3mwuwn74qnhj83gjg0cwkk78fun2pxl9t2", group.currentAddress(KeyPurpose.CHANGE).toString());
        // round-trip through protobuf
        group = KeyChainGroup.fromProtobufUnencrypted(KeyChainGroupTest.MAINNET, group.serializeToProtobuf());
        Assert.assertEquals(P2WPKH, group.getActiveKeyChain().getOutputScriptType());
        Assert.assertEquals("bc1qhcurdec849thpjjp3e27atvya43gy2snrechd9", group.currentAddress(RECEIVE_FUNDS).toString());
        Assert.assertEquals("bc1qw8sf3mwuwn74qnhj83gjg0cwkk78fun2pxl9t2", group.currentAddress(KeyPurpose.CHANGE).toString());
        // encryption
        KeyCrypterScrypt scrypt = new KeyCrypterScrypt(2);
        KeyParameter aesKey = scrypt.deriveKey("password");
        group.encrypt(scrypt, aesKey);
        Assert.assertEquals(P2WPKH, group.getActiveKeyChain().getOutputScriptType());
        Assert.assertEquals("bc1qhcurdec849thpjjp3e27atvya43gy2snrechd9", group.currentAddress(RECEIVE_FUNDS).toString());
        Assert.assertEquals("bc1qw8sf3mwuwn74qnhj83gjg0cwkk78fun2pxl9t2", group.currentAddress(KeyPurpose.CHANGE).toString());
        // round-trip encrypted again, then dectypt
        group = KeyChainGroup.fromProtobufEncrypted(KeyChainGroupTest.MAINNET, group.serializeToProtobuf(), scrypt);
        group.decrypt(aesKey);
        Assert.assertEquals(P2WPKH, group.getActiveKeyChain().getOutputScriptType());
        Assert.assertEquals("bc1qhcurdec849thpjjp3e27atvya43gy2snrechd9", group.currentAddress(RECEIVE_FUNDS).toString());
        Assert.assertEquals("bc1qw8sf3mwuwn74qnhj83gjg0cwkk78fun2pxl9t2", group.currentAddress(KeyPurpose.CHANGE).toString());
    }
}

