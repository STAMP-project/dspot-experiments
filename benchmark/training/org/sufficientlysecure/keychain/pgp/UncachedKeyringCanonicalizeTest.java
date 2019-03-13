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
package org.sufficientlysecure.keychain.pgp;


import Algorithm.ECDSA;
import Constants.BOUNCY_CASTLE_PROVIDER_NAME;
import HashAlgorithmTags.SHA1;
import HashAlgorithmTags.SHA256;
import KeyFlags.CERTIFY_OTHER;
import KeyFlags.ENCRYPT_COMMS;
import KeyFlags.SIGN_DATA;
import LogType.MSG_KC_ERROR_DUP_KEY;
import LogType.MSG_KC_UID_DUP;
import PGPSignature.CERTIFICATION_REVOCATION;
import PGPSignature.DEFAULT_CERTIFICATION;
import PGPSignature.KEY_REVOCATION;
import PGPSignature.POSITIVE_CERTIFICATION;
import PGPSignature.PRIMARYKEY_BINDING;
import PGPSignature.SUBKEY_BINDING;
import PGPSignature.SUBKEY_REVOCATION;
import PacketTags.SECRET_KEY;
import PacketTags.SECRET_SUBKEY;
import PacketTags.SIGNATURE;
import PacketTags.USER_ATTRIBUTE;
import PacketTags.USER_ID;
import SaveKeyringParcel.Builder;
import SaveKeyringParcel.Curve.NIST_P256;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.Packet;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.UserIDPacket;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Strings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.operations.results.OperationResult;
import org.sufficientlysecure.keychain.operations.results.OperationResult.OperationLog;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel.SubkeyAdd;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;
import org.sufficientlysecure.keychain.support.KeyringTestingHelper;
import org.sufficientlysecure.keychain.util.Passphrase;


/**
 * Tests for the UncachedKeyring.canonicalize method.
 *
 * This is a complex and crypto-relevant method, which takes care of sanitizing keyrings.
 * Test cases are made for all its assertions.
 */
@RunWith(KeychainTestRunner.class)
public class UncachedKeyringCanonicalizeTest {
    static UncachedKeyRing staticRing;

    static int totalPackets;

    UncachedKeyRing ring;

    ArrayList<KeyringTestingHelper.RawPacket> onlyA = new ArrayList<>();

    ArrayList<KeyringTestingHelper.RawPacket> onlyB = new ArrayList<>();

    OperationLog log = new OperationResult.OperationLog();

    PGPSignatureSubpacketGenerator subHashedPacketsGen;

    PGPSecretKey secretKey;

    /**
     * Make sure the assumptions made about the generated ring packet structure are valid.
     */
    @Test
    public void testGeneratedRingStructure() throws Exception {
        Iterator<KeyringTestingHelper.RawPacket> it = KeyringTestingHelper.parseKeyring(ring.getEncoded());
        Assert.assertEquals("packet #0 should be secret key", SECRET_KEY, it.next().tag);
        Assert.assertEquals("packet #1 should be user id", USER_ID, it.next().tag);
        Assert.assertEquals("packet #2 should be signature", SIGNATURE, it.next().tag);
        Assert.assertEquals("packet #3 should be user id", USER_ID, it.next().tag);
        Assert.assertEquals("packet #4 should be signature", SIGNATURE, it.next().tag);
        Assert.assertEquals("packet #5 should be user id", USER_ATTRIBUTE, it.next().tag);
        Assert.assertEquals("packet #6 should be signature", SIGNATURE, it.next().tag);
        Assert.assertEquals("packet #7 should be secret subkey", SECRET_SUBKEY, it.next().tag);
        Assert.assertEquals("packet #8 should be signature", SIGNATURE, it.next().tag);
        Assert.assertEquals("packet #9 should be secret subkey", SECRET_SUBKEY, it.next().tag);
        Assert.assertEquals("packet #10 should be signature", SIGNATURE, it.next().tag);
        Assert.assertFalse("exactly 11 packets total", it.hasNext());
        Assert.assertArrayEquals("created keyring should be constant through canonicalization", ring.getEncoded(), ring.canonicalize(log, 0).getEncoded());
    }

    @Test
    public void testUidSignature() throws Exception {
        UncachedPublicKey masterKey = ring.getPublicKey();
        final WrappedSignature sig = masterKey.getSignaturesForRawId(Strings.toUTF8ByteArray("twi")).next();
        byte[] raw = sig.getEncoded();
        // destroy the signature
        raw[((raw.length) - 5)] += 1;
        final WrappedSignature brokenSig = WrappedSignature.fromBytes(raw);
        {
            // bad certificates get stripped
            UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, brokenSig.getEncoded(), 3);
            CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
            Assert.assertTrue("canonicalized keyring with invalid extra sig must be same as original one", (!(KeyringTestingHelper.diffKeyrings(ring.getEncoded(), canonicalized.getEncoded(), onlyA, onlyB))));
        }
        // remove user id certificate for one user
        final UncachedKeyRing base = KeyringTestingHelper.removePacket(ring, 2);
        {
            // user id without certificate should be removed
            CanonicalizedKeyRing modified = base.canonicalize(log, 0);
            Assert.assertTrue("canonicalized keyring must differ", KeyringTestingHelper.diffKeyrings(ring.getEncoded(), modified.getEncoded(), onlyA, onlyB));
            Assert.assertEquals("two packets should be stripped after canonicalization", 2, onlyA.size());
            Assert.assertEquals("no new packets after canonicalization", 0, onlyB.size());
            Packet p = new BCPGInputStream(new ByteArrayInputStream(onlyA.get(0).buf)).readPacket();
            Assert.assertTrue("first stripped packet must be user id", (p instanceof UserIDPacket));
            Assert.assertEquals("missing user id must be the expected one", "twi", getID());
            Assert.assertArrayEquals("second stripped packet must be signature we removed", sig.getEncoded(), onlyA.get(1).buf);
        }
        {
            // add error to signature
            UncachedKeyRing modified = KeyringTestingHelper.injectPacket(base, brokenSig.getEncoded(), 3);
            CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
            Assert.assertTrue("canonicalized keyring must differ", KeyringTestingHelper.diffKeyrings(ring.getEncoded(), canonicalized.getEncoded(), onlyA, onlyB));
            Assert.assertEquals("two packets should be missing after canonicalization", 2, onlyA.size());
            Assert.assertEquals("no new packets after canonicalization", 0, onlyB.size());
            Packet p = new BCPGInputStream(new ByteArrayInputStream(onlyA.get(0).buf)).readPacket();
            Assert.assertTrue("first stripped packet must be user id", (p instanceof UserIDPacket));
            Assert.assertEquals("missing user id must be the expected one", "twi", getID());
            Assert.assertArrayEquals("second stripped packet must be signature we removed", sig.getEncoded(), onlyA.get(1).buf);
        }
    }

    @Test
    public void testUidDestroy() throws Exception {
        // signature for "twi"
        ring = KeyringTestingHelper.removePacket(ring, 2);
        // signature for "pink"
        ring = KeyringTestingHelper.removePacket(ring, 3);
        // canonicalization should fail, because there are no valid uids left
        CanonicalizedKeyRing canonicalized = ring.canonicalize(log, 0);
        Assert.assertNull("canonicalization of keyring with no valid uids should fail", canonicalized);
    }

    @Test
    public void testRevocationRedundant() throws Exception {
        PGPSignature revocation = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, KEY_REVOCATION, subHashedPacketsGen, secretKey.getPublicKey());
        UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, revocation.getEncoded(), 1);
        // try to add the same packet again, it should be rejected in all positions
        UncachedKeyringCanonicalizeTest.injectEverywhere(modified, revocation.getEncoded());
        // an older (but different!) revocation should be rejected as well
        subHashedPacketsGen.setSignatureCreationTime(false, new Date(((new Date().getTime()) - (1000 * 1000))));
        revocation = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, KEY_REVOCATION, subHashedPacketsGen, secretKey.getPublicKey());
        UncachedKeyringCanonicalizeTest.injectEverywhere(modified, revocation.getEncoded());
    }

    @Test
    public void testUidRedundant() throws Exception {
        // an older uid certificate should be rejected
        subHashedPacketsGen.setSignatureCreationTime(false, new Date(((new Date().getTime()) - (1000 * 1000))));
        PGPSignature revocation = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, DEFAULT_CERTIFICATION, subHashedPacketsGen, "twi", secretKey.getPublicKey());
        UncachedKeyringCanonicalizeTest.injectEverywhere(ring, revocation.getEncoded());
    }

    @Test
    public void testUidRevocationOutdated() throws Exception {
        // an older uid revocation cert should be rejected
        subHashedPacketsGen.setSignatureCreationTime(false, new Date(((new Date().getTime()) - (1000 * 1000))));
        PGPSignature revocation = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, CERTIFICATION_REVOCATION, subHashedPacketsGen, "twi", secretKey.getPublicKey());
        UncachedKeyringCanonicalizeTest.injectEverywhere(ring, revocation.getEncoded());
    }

    @Test
    public void testUidRevocationRedundant() throws Exception {
        PGPSignature revocation = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, CERTIFICATION_REVOCATION, subHashedPacketsGen, "twi", secretKey.getPublicKey());
        // add that revocation to the base, and check if the redundant one will be rejected as well
        UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, revocation.getEncoded(), 2);
        UncachedKeyringCanonicalizeTest.injectEverywhere(modified, revocation.getEncoded());
        // an older (but different!) uid revocation should be rejected as well
        subHashedPacketsGen.setSignatureCreationTime(false, new Date(((new Date().getTime()) - (1000 * 1000))));
        revocation = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, CERTIFICATION_REVOCATION, subHashedPacketsGen, "twi", secretKey.getPublicKey());
        UncachedKeyringCanonicalizeTest.injectEverywhere(modified, revocation.getEncoded());
    }

    @Test
    public void testDuplicateUid() throws Exception {
        // get subkey packets
        Iterator<KeyringTestingHelper.RawPacket> it = KeyringTestingHelper.parseKeyring(ring.getEncoded());
        KeyringTestingHelper.RawPacket uidPacket = KeyringTestingHelper.getNth(it, 3);
        KeyringTestingHelper.RawPacket uidSig = it.next();
        // inject at a second position
        UncachedKeyRing modified = ring;
        modified = KeyringTestingHelper.injectPacket(modified, uidPacket.buf, 5);
        modified = KeyringTestingHelper.injectPacket(modified, uidSig.buf, 6);
        // canonicalize, and check if we lose the bad signature
        OperationLog log = new OperationLog();
        CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
        Assert.assertNotNull("canonicalization with duplicate user id should succeed", canonicalized);
        Assert.assertTrue("log should contain uid_dup event", log.containsType(MSG_KC_UID_DUP));
        /* TODO actually test ths, and fix behavior
        Assert.assertTrue("duplicate user id packets should be gone after canonicalization",
        KeyringTestingHelper.diffKeyrings(modified.getEncoded(), canonicalized.getEncoded(),
        onlyA, onlyB)
        );
        Assert.assertEquals("canonicalized keyring should have lost the two duplicate packets",
        2, onlyA.size());
        Assert.assertTrue("canonicalized keyring should still contain the user id",
        canonicalized.getUnorderedUserIds().contains(new UserIDPacket(uidPacket.buf).getID()));
         */
    }

    @Test
    public void testSignatureBroken() throws Exception {
        UncachedKeyringCanonicalizeTest.injectEverytype(secretKey, ring, subHashedPacketsGen, true);
    }

    @Test
    public void testForeignSignature() throws Exception {
        SaveKeyringParcel.Builder builder = SaveKeyringParcel.buildNewKeyringParcel();
        builder.addSubkeyAdd(SubkeyAdd.createSubkeyAdd(ECDSA, 0, NIST_P256, CERTIFY_OTHER, 0L));
        builder.addUserId("trix");
        PgpKeyOperation op = new PgpKeyOperation(null);
        OperationResult.OperationLog log = new OperationResult.OperationLog();
        UncachedKeyRing foreign = op.createSecretKeyRing(builder.build()).getRing();
        Assert.assertNotNull("initial test key creation must succeed", foreign);
        PGPSecretKey foreignSecretKey = getSecretKey();
        UncachedKeyringCanonicalizeTest.injectEverytype(foreignSecretKey, ring, subHashedPacketsGen);
    }

    @Test
    public void testSignatureFuture() throws Exception {
        // generate future timestamp (we allow up to one day future timestamps)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 2);
        subHashedPacketsGen.setSignatureCreationTime(false, cal.getTime());
        UncachedKeyringCanonicalizeTest.injectEverytype(secretKey, ring, subHashedPacketsGen);
    }

    @Test
    public void testSignatureLocal() throws Exception {
        // make key local only
        subHashedPacketsGen.setExportable(false, false);
        UncachedKeyringCanonicalizeTest.injectEverytype(secretKey, ring, subHashedPacketsGen);
    }

    @Test
    public void testSubkeyDestroy() throws Exception {
        // signature for second key (first subkey)
        UncachedKeyRing modified = KeyringTestingHelper.removePacket(ring, 8);
        // canonicalization should fail, because there are no valid uids left
        CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
        Assert.assertTrue("keyring with missing subkey binding sig should differ from intact one after canonicalization", KeyringTestingHelper.diffKeyrings(ring.getEncoded(), canonicalized.getEncoded(), onlyA, onlyB));
        Assert.assertEquals("canonicalized keyring should have two extra packets", 2, onlyA.size());
        Assert.assertEquals("canonicalized keyring should have no extra packets", 0, onlyB.size());
        Assert.assertEquals("first missing packet should be the subkey", SECRET_SUBKEY, onlyA.get(0).tag);
        Assert.assertEquals("second missing packet should be subkey's signature", SIGNATURE, onlyA.get(1).tag);
        Assert.assertEquals("second missing packet should be next to subkey", ((onlyA.get(0).position) + 1), onlyA.get(1).position);
    }

    @Test
    public void testSubkeyBindingNoPKB() throws Exception {
        UncachedPublicKey pKey = KeyringTestingHelper.getNth(ring.getPublicKeys(), 1);
        PGPSignature sig;
        subHashedPacketsGen.setKeyFlags(false, SIGN_DATA);
        {
            // forge a (newer) signature, which has the sign flag but no primary key binding sig
            PGPSignatureSubpacketGenerator unhashedSubs = new PGPSignatureSubpacketGenerator();
            // just add any random signature, because why not
            unhashedSubs.setEmbeddedSignature(false, UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, POSITIVE_CERTIFICATION, subHashedPacketsGen, secretKey.getPublicKey()));
            sig = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, SUBKEY_BINDING, subHashedPacketsGen, unhashedSubs, secretKey.getPublicKey(), pKey.getPublicKey());
            // inject in the right position
            UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, sig.getEncoded(), 8);
            // canonicalize, and check if we lose the bad signature
            CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
            Assert.assertFalse("subkey binding signature should be gone after canonicalization", KeyringTestingHelper.diffKeyrings(ring.getEncoded(), canonicalized.getEncoded(), onlyA, onlyB));
        }
        {
            // now try one with a /bad/ primary key binding signature
            PGPSignatureSubpacketGenerator unhashedSubs = new PGPSignatureSubpacketGenerator();
            // this one is signed by the primary key itself, not the subkey - but it IS primary binding
            unhashedSubs.setEmbeddedSignature(false, UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, PRIMARYKEY_BINDING, subHashedPacketsGen, secretKey.getPublicKey(), pKey.getPublicKey()));
            sig = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, SUBKEY_BINDING, subHashedPacketsGen, unhashedSubs, secretKey.getPublicKey(), pKey.getPublicKey());
            // inject in the right position
            UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, sig.getEncoded(), 8);
            // canonicalize, and check if we lose the bad signature
            CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
            Assert.assertFalse("subkey binding signature should be gone after canonicalization", KeyringTestingHelper.diffKeyrings(ring.getEncoded(), canonicalized.getEncoded(), onlyA, onlyB));
        }
    }

    @Test
    public void testSubkeyBindingRedundant() throws Exception {
        UncachedPublicKey pKey = KeyringTestingHelper.getNth(ring.getPublicKeys(), 2);
        subHashedPacketsGen.setKeyFlags(false, ENCRYPT_COMMS);
        PGPSignature sig2 = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, SUBKEY_BINDING, subHashedPacketsGen, secretKey.getPublicKey(), pKey.getPublicKey());
        subHashedPacketsGen.setSignatureCreationTime(false, new Date(((new Date().getTime()) - (1000 * 1000))));
        PGPSignature sig1 = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, SUBKEY_REVOCATION, subHashedPacketsGen, secretKey.getPublicKey(), pKey.getPublicKey());
        subHashedPacketsGen = new PGPSignatureSubpacketGenerator();
        subHashedPacketsGen.setSignatureCreationTime(false, new Date(((new Date().getTime()) - (100 * 1000))));
        PGPSignature sig3 = UncachedKeyringCanonicalizeTest.forgeSignature(secretKey, SUBKEY_BINDING, subHashedPacketsGen, secretKey.getPublicKey(), pKey.getPublicKey());
        UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, sig1.getEncoded(), 10);
        modified = KeyringTestingHelper.injectPacket(modified, sig2.getEncoded(), 11);
        modified = KeyringTestingHelper.injectPacket(modified, sig1.getEncoded(), 12);
        modified = KeyringTestingHelper.injectPacket(modified, sig3.getEncoded(), 13);
        // canonicalize, and check if we lose the bad signature
        CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
        Assert.assertTrue("subkey binding signature should be gone after canonicalization", KeyringTestingHelper.diffKeyrings(modified.getEncoded(), canonicalized.getEncoded(), onlyA, onlyB));
        Assert.assertEquals("canonicalized keyring should have lost two packets", 3, onlyA.size());
        Assert.assertEquals("canonicalized keyring should have no extra packets", 0, onlyB.size());
        Assert.assertEquals("first missing packet should be the subkey", SIGNATURE, onlyA.get(0).tag);
        Assert.assertEquals("second missing packet should be a signature", SIGNATURE, onlyA.get(1).tag);
        Assert.assertEquals("second missing packet should be a signature", SIGNATURE, onlyA.get(2).tag);
    }

    @Test
    public void testDuplicateSubkey() throws Exception {
        {
            // duplicate subkey
            // get subkey packets
            Iterator<KeyringTestingHelper.RawPacket> it = KeyringTestingHelper.parseKeyring(ring.getEncoded());
            KeyringTestingHelper.RawPacket subKey = KeyringTestingHelper.getNth(it, 7);
            KeyringTestingHelper.RawPacket subSig = it.next();
            // inject at a second position
            UncachedKeyRing modified = ring;
            modified = KeyringTestingHelper.injectPacket(modified, subKey.buf, 9);
            modified = KeyringTestingHelper.injectPacket(modified, subSig.buf, 10);
            // canonicalize, and check if we lose the bad signature
            OperationLog log = new OperationLog();
            CanonicalizedKeyRing canonicalized = modified.canonicalize(log, 0);
            Assert.assertNull("canonicalization with duplicate subkey should fail", canonicalized);
            Assert.assertTrue("log should contain dup_key event", log.containsType(MSG_KC_ERROR_DUP_KEY));
        }
        {
            // duplicate subkey, which is the same as the master key
            // We actually encountered one of these in the wild:
            // https://www.sparkasse-holstein.de/firmenkunden/electronic_banking/secure-e-mail/pdf/Spk_Holstein_PGP_Domain-Zertifikat.asc
            CanonicalizedSecretKeyRing canonicalized = ((CanonicalizedSecretKeyRing) (ring.canonicalize(log, 0)));
            CanonicalizedSecretKey masterSecretKey = canonicalized.getSecretKey();
            masterSecretKey.unlock(new Passphrase());
            PGPPublicKey masterPublicKey = masterSecretKey.getPublicKey();
            CryptoInputParcel cryptoInput = CryptoInputParcel.createCryptoInputParcel(new Date());
            PGPSignature cert = PgpKeyOperation.generateSubkeyBindingSignature(PgpKeyOperation.getSignatureGenerator(masterSecretKey.getSecretKey(), cryptoInput), cryptoInput.getSignatureTime(), masterPublicKey, masterSecretKey.getPrivateKey(), PgpKeyOperation.getSignatureGenerator(masterSecretKey.getSecretKey(), null), masterSecretKey.getPrivateKey(), masterPublicKey, masterSecretKey.getKeyUsage(), 0);
            PGPPublicKey subPubKey = PGPPublicKey.addSubkeyBindingCertification(masterPublicKey, cert);
            PGPSecretKey sKey;
            {
                // Build key encrypter and decrypter based on passphrase
                PGPDigestCalculator encryptorHashCalc = new JcaPGPDigestCalculatorProviderBuilder().build().get(SHA256);
                PBESecretKeyEncryptor keyEncryptor = new org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256, encryptorHashCalc, 10).setProvider(BOUNCY_CASTLE_PROVIDER_NAME).build("".toCharArray());
                // NOTE: only SHA1 is supported for key checksum calculations.
                PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(SHA1);
                sKey = new PGPSecretKey(masterSecretKey.getPrivateKey(), subPubKey, sha1Calc, false, keyEncryptor);
            }
            UncachedKeyRing modified = KeyringTestingHelper.injectPacket(ring, sKey.getEncoded(), 7);
            // canonicalize, and check if we lose the bad signature
            OperationLog log = new OperationLog();
            CanonicalizedKeyRing result = modified.canonicalize(log, 0);
            Assert.assertNull("canonicalization with duplicate subkey (from master) should fail", result);
            Assert.assertTrue("log should contain dup_key event", log.containsType(MSG_KC_ERROR_DUP_KEY));
        }
    }

    private static final int[] sigtypes_direct = new int[]{ PGPSignature.KEY_REVOCATION, PGPSignature.DIRECT_KEY };

    private static final int[] sigtypes_uid = new int[]{ PGPSignature.DEFAULT_CERTIFICATION, PGPSignature.NO_CERTIFICATION, PGPSignature.CASUAL_CERTIFICATION, PGPSignature.POSITIVE_CERTIFICATION, PGPSignature.CERTIFICATION_REVOCATION };

    private static final int[] sigtypes_subkey = new int[]{ PGPSignature.SUBKEY_BINDING, PGPSignature.PRIMARYKEY_BINDING, PGPSignature.SUBKEY_REVOCATION };
}

