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
package org.bitcoinj.wallet;


import KeyChain.KeyPurpose.CHANGE;
import KeyChain.KeyPurpose.RECEIVE_FUNDS;
import Protos.Key;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.bitcoinj.core.BloomFilter;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.junit.Assert;
import org.junit.Test;


public class BasicKeyChainTest {
    private BasicKeyChain chain;

    private AtomicReference<List<ECKey>> onKeysAdded;

    private AtomicBoolean onKeysAddedRan;

    @Test
    public void importKeys() {
        long now = Utils.currentTimeSeconds();
        Utils.setMockClock(now);
        final ECKey key1 = new ECKey();
        Utils.rollMockClock(86400);
        final ECKey key2 = new ECKey();
        final ArrayList<ECKey> keys = Lists.newArrayList(key1, key2);
        // Import two keys, check the event is correct.
        Assert.assertEquals(2, chain.importKeys(keys));
        Assert.assertEquals(2, chain.numKeys());
        Assert.assertTrue(onKeysAddedRan.getAndSet(false));
        Assert.assertArrayEquals(keys.toArray(), onKeysAdded.get().toArray());
        Assert.assertEquals(now, chain.getEarliestKeyCreationTime());
        // Check we ignore duplicates.
        final ECKey newKey = new ECKey();
        keys.add(newKey);
        Assert.assertEquals(1, chain.importKeys(keys));
        Assert.assertTrue(onKeysAddedRan.getAndSet(false));
        Assert.assertEquals(newKey, onKeysAdded.getAndSet(null).get(0));
        Assert.assertEquals(0, chain.importKeys(keys));
        Assert.assertFalse(onKeysAddedRan.getAndSet(false));
        Assert.assertNull(onKeysAdded.get());
        Assert.assertTrue(chain.hasKey(key1));
        Assert.assertTrue(chain.hasKey(key2));
        Assert.assertEquals(key1, chain.findKeyFromPubHash(key1.getPubKeyHash()));
        Assert.assertEquals(key2, chain.findKeyFromPubKey(key2.getPubKey()));
        Assert.assertNull(chain.findKeyFromPubKey(key2.getPubKeyHash()));
    }

    @Test
    public void removeKey() {
        ECKey key = new ECKey();
        chain.importKeys(key);
        Assert.assertEquals(1, chain.numKeys());
        Assert.assertTrue(chain.removeKey(key));
        Assert.assertEquals(0, chain.numKeys());
        Assert.assertFalse(chain.removeKey(key));
    }

    @Test
    public void getKey() {
        ECKey key1 = chain.getKey(RECEIVE_FUNDS);
        Assert.assertTrue(onKeysAddedRan.getAndSet(false));
        Assert.assertEquals(key1, onKeysAdded.getAndSet(null).get(0));
        ECKey key2 = chain.getKey(CHANGE);
        Assert.assertFalse(onKeysAddedRan.getAndSet(false));
        Assert.assertEquals(key2, key1);
    }

    @Test(expected = IllegalStateException.class)
    public void checkPasswordNoKeys() {
        chain.checkPassword("test");
    }

    @Test(expected = IllegalStateException.class)
    public void checkPasswordNotEncrypted() {
        final ArrayList<ECKey> keys = Lists.newArrayList(new ECKey(), new ECKey());
        chain.importKeys(keys);
        chain.checkPassword("test");
    }

    @Test(expected = IllegalStateException.class)
    public void doubleEncryptFails() {
        final ArrayList<ECKey> keys = Lists.newArrayList(new ECKey(), new ECKey());
        chain.importKeys(keys);
        chain = chain.toEncrypted("foo");
        chain.toEncrypted("foo");
    }

    @Test
    public void encryptDecrypt() {
        final ECKey key1 = new ECKey();
        chain.importKeys(key1, new ECKey());
        final String PASSWORD = "foobar";
        chain = chain.toEncrypted(PASSWORD);
        final KeyCrypter keyCrypter = chain.getKeyCrypter();
        Assert.assertNotNull(keyCrypter);
        Assert.assertTrue((keyCrypter instanceof KeyCrypterScrypt));
        Assert.assertTrue(chain.checkPassword(PASSWORD));
        Assert.assertFalse(chain.checkPassword("wrong"));
        ECKey key = chain.findKeyFromPubKey(key1.getPubKey());
        Assert.assertTrue(key.isEncrypted());
        Assert.assertTrue(key.isPubKeyOnly());
        Assert.assertFalse(key.isWatching());
        Assert.assertNull(key.getSecretBytes());
        try {
            // Don't allow import of an unencrypted key.
            chain.importKeys(new ECKey());
            Assert.fail();
        } catch (KeyCrypterException e) {
        }
        try {
            chain.toDecrypted(keyCrypter.deriveKey("wrong"));
            Assert.fail();
        } catch (KeyCrypterException e) {
        }
        chain = chain.toDecrypted(PASSWORD);
        key = chain.findKeyFromPubKey(key1.getPubKey());
        Assert.assertFalse(key.isEncrypted());
        Assert.assertFalse(key.isPubKeyOnly());
        Assert.assertFalse(key.isWatching());
        key.getPrivKeyBytes();
    }

    @Test(expected = KeyCrypterException.class)
    public void cannotImportEncryptedKey() {
        final ECKey key1 = new ECKey();
        chain.importKeys(ImmutableList.of(key1));
        chain = chain.toEncrypted("foobar");
        ECKey encryptedKey = chain.getKey(RECEIVE_FUNDS);
        Assert.assertTrue(encryptedKey.isEncrypted());
        BasicKeyChain chain2 = new BasicKeyChain();
        chain2.importKeys(ImmutableList.of(encryptedKey));
    }

    @Test(expected = KeyCrypterException.class)
    public void cannotMixParams() throws Exception {
        chain = chain.toEncrypted("foobar");
        KeyCrypterScrypt scrypter = new KeyCrypterScrypt(2);// Some bogus params.

        ECKey key1 = new ECKey().encrypt(scrypter, scrypter.deriveKey("other stuff"));
        chain.importKeys(key1);
    }

    @Test
    public void serializationUnencrypted() throws UnreadableWalletException {
        Utils.setMockClock();
        Date now = Utils.now();
        final ECKey key1 = new ECKey();
        Utils.rollMockClock(5000);
        final ECKey key2 = new ECKey();
        chain.importKeys(ImmutableList.of(key1, key2));
        List<Protos.Key> keys = chain.serializeToProtobuf();
        Assert.assertEquals(2, keys.size());
        Assert.assertArrayEquals(key1.getPubKey(), keys.get(0).getPublicKey().toByteArray());
        Assert.assertArrayEquals(key2.getPubKey(), keys.get(1).getPublicKey().toByteArray());
        Assert.assertArrayEquals(key1.getPrivKeyBytes(), keys.get(0).getSecretBytes().toByteArray());
        Assert.assertArrayEquals(key2.getPrivKeyBytes(), keys.get(1).getSecretBytes().toByteArray());
        long normTime = ((long) ((Math.floor(((now.getTime()) / 1000))) * 1000));
        Assert.assertEquals(normTime, keys.get(0).getCreationTimestamp());
        Assert.assertEquals((normTime + (5000 * 1000)), keys.get(1).getCreationTimestamp());
        chain = BasicKeyChain.fromProtobufUnencrypted(keys);
        Assert.assertEquals(2, chain.getKeys().size());
        Assert.assertEquals(key1, chain.getKeys().get(0));
        Assert.assertEquals(key2, chain.getKeys().get(1));
    }

    @Test
    public void serializationEncrypted() throws UnreadableWalletException {
        ECKey key1 = new ECKey();
        chain.importKeys(key1);
        chain = chain.toEncrypted("foo bar");
        key1 = chain.getKeys().get(0);
        List<Protos.Key> keys = chain.serializeToProtobuf();
        Assert.assertEquals(1, keys.size());
        Assert.assertArrayEquals(key1.getPubKey(), keys.get(0).getPublicKey().toByteArray());
        Assert.assertFalse(keys.get(0).hasSecretBytes());
        Assert.assertTrue(keys.get(0).hasEncryptedData());
        chain = BasicKeyChain.fromProtobufEncrypted(keys, Preconditions.checkNotNull(chain.getKeyCrypter()));
        Assert.assertEquals(key1.getEncryptedPrivateKey(), chain.getKeys().get(0).getEncryptedPrivateKey());
        Assert.assertTrue(chain.checkPassword("foo bar"));
    }

    @Test
    public void watching() throws UnreadableWalletException {
        ECKey key1 = new ECKey();
        ECKey pub = ECKey.fromPublicOnly(key1.getPubKeyPoint());
        chain.importKeys(pub);
        Assert.assertEquals(1, chain.numKeys());
        List<Protos.Key> keys = chain.serializeToProtobuf();
        Assert.assertEquals(1, keys.size());
        Assert.assertTrue(keys.get(0).hasPublicKey());
        Assert.assertFalse(keys.get(0).hasSecretBytes());
        chain = BasicKeyChain.fromProtobufUnencrypted(keys);
        Assert.assertEquals(1, chain.numKeys());
        Assert.assertFalse(chain.findKeyFromPubKey(pub.getPubKey()).hasPrivKey());
    }

    @Test
    public void bloom() throws Exception {
        ECKey key1 = new ECKey();
        ECKey key2 = new ECKey();
        chain.importKeys(key1, key2);
        Assert.assertEquals(2, chain.numKeys());
        Assert.assertEquals(4, chain.numBloomFilterEntries());
        BloomFilter filter = chain.getFilter(4, 0.001, 100);
        Assert.assertTrue(filter.contains(key1.getPubKey()));
        Assert.assertTrue(filter.contains(key1.getPubKeyHash()));
        Assert.assertTrue(filter.contains(key2.getPubKey()));
        Assert.assertTrue(filter.contains(key2.getPubKeyHash()));
        ECKey key3 = new ECKey();
        Assert.assertFalse(filter.contains(key3.getPubKey()));
    }

    @Test
    public void keysBeforeAndAfter() throws Exception {
        Utils.setMockClock();
        long now = Utils.currentTimeSeconds();
        final ECKey key1 = new ECKey();
        Utils.rollMockClock(86400);
        final ECKey key2 = new ECKey();
        final List<ECKey> keys = Lists.newArrayList(key1, key2);
        Assert.assertEquals(2, chain.importKeys(keys));
        Assert.assertNull(chain.findOldestKeyAfter((now + (86400 * 2))));
        Assert.assertEquals(key1, chain.findOldestKeyAfter((now - 1)));
        Assert.assertEquals(key2, chain.findOldestKeyAfter(((now + 86400) - 1)));
        Assert.assertEquals(2, chain.findKeysBefore((now + (86400 * 2))).size());
        Assert.assertEquals(1, chain.findKeysBefore((now + 1)).size());
        Assert.assertEquals(0, chain.findKeysBefore((now - 1)).size());
    }
}

