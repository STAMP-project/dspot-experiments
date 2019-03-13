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
import KeyFlags.SIGN_DATA;
import PacketTags.SIGNATURE;
import SaveKeyringParcel.Builder;
import SaveKeyringParcel.Curve.NIST_P256;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import org.bouncycastle.util.Strings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.operations.results.OperationResult;
import org.sufficientlysecure.keychain.operations.results.OperationResult.OperationLog;
import org.sufficientlysecure.keychain.pgp.CanonicalizedKeyRing.VerificationStatus;
import org.sufficientlysecure.keychain.pgp.PgpCertifyOperation.PgpCertifyResult;
import org.sufficientlysecure.keychain.service.CertifyActionsParcel.CertifyAction;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel.SubkeyAdd;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;
import org.sufficientlysecure.keychain.support.KeyringTestingHelper;
import org.sufficientlysecure.keychain.util.Passphrase;


/**
 * Tests for the UncachedKeyring.merge method.
 *
 * This is another complex, crypto-related method. It merges information from one keyring into
 * another, keeping information from the base (ie, called object) keyring in case of conflicts.
 * The types of keys may be Public or Secret and can be mixed, For mixed types the result type
 * will be the same as the base keyring.
 *
 * Test cases:
 *  - Merging keyrings with different masterKeyIds should fail
 *  - Merging a key with itself should be a no-operation
 *  - Merging a key with an extra revocation certificate, it should have that certificate
 *  - Merging a key with an extra user id, it should have that extra user id and its certificates
 *  - Merging a key with an extra user id certificate, it should have that certificate
 *  - Merging a key with an extra subkey, it should have that subkey
 *  - Merging a key with an extra subkey certificate, it should have that certificate
 *  - All of the above operations should work regardless of the key types. This means in particular
 *      that for new subkeys, an equivalent subkey of the proper type must be generated.
 *  - In case of two secret keys with the same id but different S2K, the key of the base keyring
 *      should be preferred (TODO or should it?)
 *
 * Note that the merge operation does not care about certificate validity, a bad certificate or
 * packet will be copied regardless. Filtering out bad packets is done with canonicalization.
 */
@RunWith(KeychainTestRunner.class)
public class UncachedKeyringMergeTest {
    static UncachedKeyRing staticRingA;

    static UncachedKeyRing staticRingB;

    UncachedKeyRing ringA;

    UncachedKeyRing ringB;

    ArrayList<KeyringTestingHelper.RawPacket> onlyA = new ArrayList<>();

    ArrayList<KeyringTestingHelper.RawPacket> onlyB = new ArrayList<>();

    OperationLog log = new OperationResult.OperationLog();

    PgpKeyOperation op;

    Builder builder;

    @Test
    public void testDifferentMasterKeyIds() throws Exception {
        Assert.assertNotEquals("generated key ids must be different", ringA.getMasterKeyId(), ringB.getMasterKeyId());
        Assert.assertNull("merging keys with differing key ids must fail", ringA.merge(ringB, log, 0));
        Assert.assertNull("merging keys with differing key ids must fail", ringB.merge(ringA, log, 0));
    }

    @Test
    public void testAddedUserId() throws Exception {
        UncachedKeyRing modifiedA;
        UncachedKeyRing modifiedB;
        {
            CanonicalizedSecretKeyRing secretRing = new CanonicalizedSecretKeyRing(ringA.getEncoded(), VerificationStatus.UNVERIFIED);
            resetBuilder();
            builder.addUserId("flim");
            modifiedA = op.modifySecretKeyRing(secretRing, CryptoInputParcel.createCryptoInputParcel(new Date(), new Passphrase()), builder.build()).getRing();
            resetBuilder();
            builder.addUserId("flam");
            modifiedB = op.modifySecretKeyRing(secretRing, CryptoInputParcel.createCryptoInputParcel(new Date(), new Passphrase()), builder.build()).getRing();
        }
        {
            // merge A into base
            UncachedKeyRing merged = mergeWithChecks(ringA, modifiedA);
            Assert.assertEquals("merged keyring must have lost no packets", 0, onlyA.size());
            Assert.assertEquals("merged keyring must have gained two packets", 2, onlyB.size());
            Assert.assertTrue("merged keyring must contain new user id", merged.getPublicKey().getUnorderedUserIds().contains("flim"));
        }
        {
            // merge A into B
            UncachedKeyRing merged = mergeWithChecks(modifiedA, modifiedB, ringA);
            Assert.assertEquals("merged keyring must have lost no packets", 0, onlyA.size());
            Assert.assertEquals("merged keyring must have gained four packets", 4, onlyB.size());
            Assert.assertTrue("merged keyring must contain first new user id", merged.getPublicKey().getUnorderedUserIds().contains("flim"));
            Assert.assertTrue("merged keyring must contain second new user id", merged.getPublicKey().getUnorderedUserIds().contains("flam"));
        }
    }

    @Test
    public void testAddedSubkeyId() throws Exception {
        UncachedKeyRing modifiedA;
        UncachedKeyRing modifiedB;
        long subKeyIdA;
        long subKeyIdB;
        {
            CanonicalizedSecretKeyRing secretRing = new CanonicalizedSecretKeyRing(ringA.getEncoded(), VerificationStatus.UNVERIFIED);
            resetBuilder();
            builder.addSubkeyAdd(SubkeyAdd.createSubkeyAdd(ECDSA, 0, NIST_P256, SIGN_DATA, 0L));
            modifiedA = op.modifySecretKeyRing(secretRing, CryptoInputParcel.createCryptoInputParcel(new Date(), new Passphrase()), builder.build()).getRing();
            modifiedB = op.modifySecretKeyRing(secretRing, CryptoInputParcel.createCryptoInputParcel(new Date(), new Passphrase()), builder.build()).getRing();
            subKeyIdA = KeyringTestingHelper.getSubkeyId(modifiedA, 2);
            subKeyIdB = KeyringTestingHelper.getSubkeyId(modifiedB, 2);
        }
        {
            UncachedKeyRing merged = mergeWithChecks(ringA, modifiedA);
            Assert.assertEquals("merged keyring must have lost no packets", 0, onlyA.size());
            Assert.assertEquals("merged keyring must have gained two packets", 2, onlyB.size());
            long mergedKeyId = KeyringTestingHelper.getSubkeyId(merged, 2);
            Assert.assertEquals("merged keyring must contain the new subkey", subKeyIdA, mergedKeyId);
        }
        {
            UncachedKeyRing merged = mergeWithChecks(modifiedA, modifiedB, ringA);
            Assert.assertEquals("merged keyring must have lost no packets", 0, onlyA.size());
            Assert.assertEquals("merged keyring must have gained four packets", 4, onlyB.size());
            Iterator<UncachedPublicKey> it = merged.getPublicKeys();
            it.next();
            it.next();
            Assert.assertEquals("merged keyring must contain the new subkey", subKeyIdA, it.next().getKeyId());
            Assert.assertEquals("merged keyring must contain both new subkeys", subKeyIdB, it.next().getKeyId());
        }
    }

    @Test
    public void testAddedKeySignature() throws Exception {
        final UncachedKeyRing modified;
        {
            resetBuilder();
            builder.addRevokeSubkey(KeyringTestingHelper.getSubkeyId(ringA, 1));
            CanonicalizedSecretKeyRing secretRing = new CanonicalizedSecretKeyRing(ringA.getEncoded(), VerificationStatus.UNVERIFIED);
            modified = op.modifySecretKeyRing(secretRing, CryptoInputParcel.createCryptoInputParcel(new Date(), new Passphrase()), builder.build()).getRing();
        }
        {
            UncachedKeyRing merged = ringA.merge(modified, log, 0);
            Assert.assertNotNull("merge must succeed", merged);
            Assert.assertFalse("merging keyring with extra signatures into its base should yield that same keyring", KeyringTestingHelper.diffKeyrings(merged.getEncoded(), modified.getEncoded(), onlyA, onlyB));
        }
    }

    @Test
    public void testAddedUserIdSignature() throws Exception {
        final UncachedKeyRing pubRing = ringA.extractPublicKeyRing();
        final UncachedKeyRing modified;
        {
            CanonicalizedPublicKeyRing publicRing = new CanonicalizedPublicKeyRing(pubRing.getEncoded(), VerificationStatus.UNVERIFIED);
            CanonicalizedSecretKey secretKey = getSecretKey();
            secretKey.unlock(new Passphrase());
            PgpCertifyOperation op = new PgpCertifyOperation();
            CertifyAction action = CertifyAction.createForUserIds(pubRing.getMasterKeyId(), publicRing.getPublicKey().getUnorderedUserIds());
            // sign all user ids
            PgpCertifyResult result = op.certify(secretKey, publicRing, new OperationLog(), 0, action, null, new Date());
            Assert.assertTrue("certification must succeed", result.success());
            Assert.assertNotNull("certification must yield result", result.getCertifiedRing());
            modified = result.getCertifiedRing();
        }
        {
            UncachedKeyRing merged = ringA.merge(modified, log, 0);
            Assert.assertNotNull("merge must succeed", merged);
            Assert.assertArrayEquals("foreign signatures should not be merged into secret key", ringA.getEncoded(), merged.getEncoded());
        }
        {
            byte[] sig = KeyringTestingHelper.getNth(modified.getPublicKey().getSignaturesForRawId(Strings.toUTF8ByteArray("twi")), 1).getEncoded();
            // inject the (foreign!) signature into subkey signature position
            UncachedKeyRing moreModified = KeyringTestingHelper.injectPacket(modified, sig, 1);
            UncachedKeyRing merged = ringA.merge(moreModified, log, 0);
            Assert.assertNotNull("merge must succeed", merged);
            Assert.assertArrayEquals("foreign signatures should not be merged into secret key", ringA.getEncoded(), merged.getEncoded());
            merged = pubRing.merge(moreModified, log, 0);
            Assert.assertNotNull("merge must succeed", merged);
            Assert.assertTrue("merged keyring should contain new signature", KeyringTestingHelper.diffKeyrings(pubRing.getEncoded(), merged.getEncoded(), onlyA, onlyB));
            Assert.assertEquals("merged keyring should be missing no packets", 0, onlyA.size());
            Assert.assertEquals("merged keyring should contain exactly two more packets", 2, onlyB.size());
            Assert.assertEquals("first added packet should be a signature", SIGNATURE, onlyB.get(0).tag);
            Assert.assertEquals("first added packet should be in the position we injected it at", 1, onlyB.get(0).position);
            Assert.assertEquals("second added packet should be a signature", SIGNATURE, onlyB.get(1).tag);
        }
        {
            UncachedKeyRing merged = pubRing.merge(modified, log, 0);
            Assert.assertNotNull("merge must succeed", merged);
            Assert.assertFalse("merging keyring with extra signatures into its base should yield that same keyring", KeyringTestingHelper.diffKeyrings(merged.getEncoded(), modified.getEncoded(), onlyA, onlyB));
        }
    }

    @Test
    public void testAddedUserAttributeSignature() throws Exception {
        final UncachedKeyRing modified;
        {
            resetBuilder();
            Random r = new Random();
            int type = (r.nextInt(110)) + 1;
            byte[] data = new byte[r.nextInt(2000)];
            new Random().nextBytes(data);
            WrappedUserAttribute uat = WrappedUserAttribute.fromSubpacket(type, data);
            builder.addUserAttribute(uat);
            CanonicalizedSecretKeyRing secretRing = new CanonicalizedSecretKeyRing(ringA.getEncoded(), VerificationStatus.UNVERIFIED);
            modified = op.modifySecretKeyRing(secretRing, CryptoInputParcel.createCryptoInputParcel(new Date(), new Passphrase()), builder.build()).getRing();
        }
        {
            UncachedKeyRing merged = ringA.merge(modified, log, 0);
            Assert.assertNotNull("merge must succeed", merged);
            Assert.assertFalse("merging keyring with extra user attribute into its base should yield that same keyring", KeyringTestingHelper.diffKeyrings(merged.getEncoded(), modified.getEncoded(), onlyA, onlyB));
        }
    }
}

