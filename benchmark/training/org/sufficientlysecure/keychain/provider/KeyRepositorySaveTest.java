/**
 * Copyright (C) 2014 Dominik Sch?rmann <dominik@dominikschuermann.de>
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sufficientlysecure.keychain.provider;


import KeyFlags.AUTHENTICATION;
import RuntimeEnvironment.application;
import SecretKeyType.DIVERT_TO_CARD;
import java.util.Arrays;
import java.util.Iterator;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.model.SubKey.UnifiedKeyInfo;
import org.sufficientlysecure.keychain.operations.results.OperationResult.OperationLog;
import org.sufficientlysecure.keychain.operations.results.SaveKeyringResult;
import org.sufficientlysecure.keychain.pgp.CanonicalizedPublicKeyRing;
import org.sufficientlysecure.keychain.pgp.CanonicalizedSecretKey;
import org.sufficientlysecure.keychain.pgp.CanonicalizedSecretKeyRing;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;


@RunWith(KeychainTestRunner.class)
public class KeyRepositorySaveTest {
    KeyWritableRepository mDatabaseInteractor = KeyWritableRepository.create(application);

    @Test
    public void testImportCooperPairOne() throws Exception {
        // insert two keys with same long key id, make sure the second one gets rejected either way!
        UncachedKeyRing first = readRingFromResource("/test-keys/cooperpair/9E669861368BCA0BE42DAF7DDDA252EBB8EBE1AF.asc");
        UncachedKeyRing second = readRingFromResource("/test-keys/cooperpair/A55120427374F3F7AA5F1166DDA252EBB8EBE1AF.asc");
        SaveKeyringResult result;
        // insert both keys, second should fail
        result = KeyWritableRepository.create(application).savePublicKeyRing(first);
        Assert.assertTrue("first keyring import should succeed", result.success());
        result = KeyWritableRepository.create(application).savePublicKeyRing(second);
        Assert.assertFalse("second keyring import should fail", result.success());
    }

    @Test
    public void testImportCooperPairTwo() throws Exception {
        // insert two keys with same long key id, make sure the second one gets rejected either way!
        UncachedKeyRing first = readRingFromResource("/test-keys/cooperpair/9E669861368BCA0BE42DAF7DDDA252EBB8EBE1AF.asc");
        UncachedKeyRing second = readRingFromResource("/test-keys/cooperpair/A55120427374F3F7AA5F1166DDA252EBB8EBE1AF.asc");
        SaveKeyringResult result;
        result = KeyWritableRepository.create(application).savePublicKeyRing(second);
        Assert.assertTrue("first keyring import should succeed", result.success());
        result = KeyWritableRepository.create(application).savePublicKeyRing(first);
        Assert.assertFalse("second keyring import should fail", result.success());
    }

    @Test
    public void testImportSymantec() throws Exception {
        // symantec pgp desktop exports secret keys without self certificates. we don't support
        // those on their own, but if they are imported together with their public key (or if
        // the public key is already known), the self certs info will be merged in as a special
        // case.
        UncachedKeyRing seckey = readRingFromResource("/test-keys/symantec_secret.asc");
        UncachedKeyRing pubkey = readRingFromResource("/test-keys/symantec_public.asc");
        SaveKeyringResult result;
        // insert secret, this should fail because of missing self-cert
        result = KeyWritableRepository.create(application).saveSecretKeyRing(seckey);
        Assert.assertFalse("secret keyring import before pubring import should fail", result.success());
        // insert pubkey, then seckey - both should succeed
        result = KeyWritableRepository.create(application).savePublicKeyRing(pubkey);
        Assert.assertTrue("public keyring import should succeed", result.success());
        result = KeyWritableRepository.create(application).saveSecretKeyRing(seckey);
        Assert.assertTrue("secret keyring import after pubring import should succeed", result.success());
    }

    @Test
    public void testImportNoFlagKey() throws Exception {
        UncachedKeyRing pub = readRingFromResource("/test-keys/mailvelope_07_no_key_flags.asc");
        long keyId = pub.getMasterKeyId();
        Assert.assertEquals("key flags should be zero", 0, ((long) (pub.canonicalize(new OperationLog(), 0).getPublicKey().getKeyUsage())));
        mDatabaseInteractor.savePublicKeyRing(pub);
        UnifiedKeyInfo unifiedKeyInfo = mDatabaseInteractor.getUnifiedKeyInfo(keyId);
        CanonicalizedPublicKeyRing pubRing = mDatabaseInteractor.getCanonicalizedPublicKeyRing(keyId);
        Assert.assertEquals("master key should be encryption key", keyId, pubRing.getEncryptId());
        Assert.assertEquals("master key should be encryption key (cached)", keyId, unifiedKeyInfo.has_encrypt_key_int());
        Assert.assertEquals("canonicalized key flags should be zero", 0, ((long) (pubRing.getPublicKey().getKeyUsage())));
        Assert.assertTrue("master key should be able to certify", pubRing.getPublicKey().canCertify());
        Assert.assertTrue("master key should be allowed to sign", pubRing.getPublicKey().canSign());
        Assert.assertTrue("master key should be able to encrypt", pubRing.getPublicKey().canEncrypt());
    }

    @Test
    public void testImportDivertToCard() throws Exception {
        UncachedKeyRing sec = readRingFromResource("/test-keys/divert_to_card_sec.asc");
        long keyId = sec.getMasterKeyId();
        SaveKeyringResult result;
        result = mDatabaseInteractor.saveSecretKeyRing(sec);
        Assert.assertTrue("import of secret keyring should succeed", result.success());
        // make sure both the CanonicalizedSecretKeyRing as well as the CachedPublicKeyRing correctly
        // indicate the secret key type
        CanonicalizedSecretKeyRing secRing = mDatabaseInteractor.getCanonicalizedSecretKeyRing(keyId);
        Iterator<CanonicalizedSecretKey> it = secRing.secretKeyIterator().iterator();
        {
            // first subkey
            Assert.assertTrue("keyring should have 3 subkeys (1)", it.hasNext());
            CanonicalizedSecretKey key = it.next();
            Assert.assertEquals("first subkey should be of type sign+certify", ((KeyFlags.CERTIFY_OTHER) | (KeyFlags.SIGN_DATA)), ((int) (key.getKeyUsage())));
            Assert.assertEquals("first subkey should be divert-to-card", DIVERT_TO_CARD, key.getSecretKeyTypeSuperExpensive());
            Assert.assertTrue("canCertify() should be true", key.canCertify());
            Assert.assertTrue("canSign() should be true", key.canSign());
            Assert.assertEquals("all subkeys from CachedPublicKeyRing should be divert-to-key", DIVERT_TO_CARD, mDatabaseInteractor.getSecretKeyType(key.getKeyId()));
        }
        {
            // second subkey
            Assert.assertTrue("keyring should have 3 subkeys (2)", it.hasNext());
            CanonicalizedSecretKey key = it.next();
            Assert.assertEquals("second subkey should be of type authenticate", AUTHENTICATION, ((int) (key.getKeyUsage())));
            Assert.assertEquals("second subkey should be divert-to-card", DIVERT_TO_CARD, key.getSecretKeyTypeSuperExpensive());
            Assert.assertTrue("canAuthenticate() should be true", key.canAuthenticate());
            // cached
            Assert.assertEquals("all subkeys from CachedPublicKeyRing should be divert-to-key", DIVERT_TO_CARD, mDatabaseInteractor.getSecretKeyType(key.getKeyId()));
        }
        {
            // third subkey
            Assert.assertTrue("keyring should have 3 subkeys (3)", it.hasNext());
            CanonicalizedSecretKey key = it.next();
            Assert.assertEquals("first subkey should be of type encrypt (both types)", ((KeyFlags.ENCRYPT_COMMS) | (KeyFlags.ENCRYPT_STORAGE)), ((int) (key.getKeyUsage())));
            Assert.assertEquals("third subkey should be divert-to-card", DIVERT_TO_CARD, key.getSecretKeyTypeSuperExpensive());
            Assert.assertTrue("canEncrypt() should be true", key.canEncrypt());
            // cached
            Assert.assertEquals("all subkeys from CachedPublicKeyRing should be divert-to-key", DIVERT_TO_CARD, mDatabaseInteractor.getSecretKeyType(key.getKeyId()));
        }
        Assert.assertFalse("keyring should have 3 subkeys (4)", it.hasNext());
    }

    @Test
    public void testImportBadEncodedUserId() throws Exception {
        UncachedKeyRing key = readRingFromResource("/test-keys/bad_user_id_encoding.asc");
        long keyId = key.getMasterKeyId();
        SaveKeyringResult result;
        result = mDatabaseInteractor.savePublicKeyRing(key, null);
        Assert.assertTrue("import of keyring should succeed", result.success());
        CanonicalizedPublicKeyRing ring = mDatabaseInteractor.getCanonicalizedPublicKeyRing(keyId);
        boolean found = false;
        byte[] badUserId = Hex.decode("436c61757320467261656e6b656c203c436c6175732e4672e46e6b656c4068616c696661782e727774682d61616368656e2e64653e");
        for (byte[] rawUserId : new org.sufficientlysecure.keychain.util.IterableIterator<byte[]>(ring.getUnorderedRawUserIds().iterator())) {
            if (Arrays.equals(rawUserId, badUserId)) {
                found = true;
            }
        }
        Assert.assertTrue("import of the badly encoded user id should succeed", found);
    }

    /**
     * Tests a master key which may sign, but is stripped. In this case, if there is a different
     * subkey available which can sign, that one should be selected.
     */
    @Test
    public void testImportStrippedFlags() throws Exception {
        UncachedKeyRing key = readRingFromResource("/test-keys/stripped_flags.asc");
        long masterKeyId = key.getMasterKeyId();
        SaveKeyringResult result;
        result = mDatabaseInteractor.saveSecretKeyRing(key);
        Assert.assertTrue("import of keyring should succeed", result.success());
        long signId;
        {
            CanonicalizedSecretKeyRing ring = mDatabaseInteractor.getCanonicalizedSecretKeyRing(masterKeyId);
            Assert.assertTrue("master key should have sign flag", ring.getPublicKey().canSign());
            Assert.assertTrue("master key should have encrypt flag", ring.getPublicKey().canEncrypt());
            signId = mDatabaseInteractor.getSecretSignId(masterKeyId);
            Assert.assertNotEquals("encrypt id should not be 0", 0, signId);
            Assert.assertNotEquals("signing key should be different from master key", masterKeyId, signId);
        }
        {
            Assert.assertEquals("signing key should be same id cached as uncached", signId, mDatabaseInteractor.getSecretSignId(masterKeyId));
        }
    }
}

