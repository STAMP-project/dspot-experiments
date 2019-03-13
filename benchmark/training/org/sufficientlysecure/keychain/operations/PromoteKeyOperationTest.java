/**
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
package org.sufficientlysecure.keychain.operations;


import RuntimeEnvironment.application;
import SecretKeyType.DIVERT_TO_CARD;
import SecretKeyType.GNU_DUMMY;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.model.SubKey.UnifiedKeyInfo;
import org.sufficientlysecure.keychain.operations.results.PromoteKeyResult;
import org.sufficientlysecure.keychain.pgp.CanonicalizedSecretKey;
import org.sufficientlysecure.keychain.pgp.CanonicalizedSecretKeyRing;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.pgp.UncachedPublicKey;
import org.sufficientlysecure.keychain.service.PromoteKeyringParcel;
import org.sufficientlysecure.keychain.support.KeyringTestingHelper;
import org.sufficientlysecure.keychain.util.Passphrase;
import org.sufficientlysecure.keychain.util.TestingUtils;


@RunWith(KeychainTestRunner.class)
public class PromoteKeyOperationTest {
    static UncachedKeyRing mStaticRing;

    static Passphrase mKeyPhrase1 = TestingUtils.testPassphrase1;

    static PrintStream oldShadowStream;

    @Test
    public void testPromote() throws Exception {
        KeyWritableRepository keyRepository = KeyWritableRepository.create(application);
        PromoteKeyOperation op = new PromoteKeyOperation(RuntimeEnvironment.application, keyRepository, null, null);
        PromoteKeyResult result = op.execute(PromoteKeyringParcel.createPromoteKeyringParcel(PromoteKeyOperationTest.mStaticRing.getMasterKeyId(), null, null), null);
        Assert.assertTrue("promotion must succeed", result.success());
        {
            UnifiedKeyInfo unifiedKeyInfo = keyRepository.getUnifiedKeyInfo(PromoteKeyOperationTest.mStaticRing.getMasterKeyId());
            Assert.assertTrue("key must have a secret now", unifiedKeyInfo.has_any_secret());
            Iterator<UncachedPublicKey> it = PromoteKeyOperationTest.mStaticRing.getPublicKeys();
            while (it.hasNext()) {
                long keyId = it.next().getKeyId();
                Assert.assertEquals("all subkeys must be gnu dummy", GNU_DUMMY, keyRepository.getSecretKeyType(keyId));
            } 
        }
    }

    @Test
    public void testPromoteDivert() throws Exception {
        PromoteKeyOperation op = new PromoteKeyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null, null);
        byte[] aid = Hex.decode("D2760001240102000000012345670000");
        PromoteKeyResult result = op.execute(PromoteKeyringParcel.createPromoteKeyringParcel(PromoteKeyOperationTest.mStaticRing.getMasterKeyId(), aid, null), null);
        Assert.assertTrue("promotion must succeed", result.success());
        {
            CanonicalizedSecretKeyRing ring = KeyWritableRepository.create(application).getCanonicalizedSecretKeyRing(PromoteKeyOperationTest.mStaticRing.getMasterKeyId());
            for (CanonicalizedSecretKey key : ring.secretKeyIterator()) {
                Assert.assertEquals("all subkeys must be divert-to-card", DIVERT_TO_CARD, key.getSecretKeyTypeSuperExpensive());
                Assert.assertArrayEquals("all subkeys must have correct iv", aid, key.getIv());
            }
        }
    }

    @Test
    public void testPromoteDivertSpecific() throws Exception {
        PromoteKeyOperation op = new PromoteKeyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null, null);
        byte[] aid = Hex.decode("D2760001240102000000012345670000");
        // only promote the first, rest stays dummy
        long keyId = KeyringTestingHelper.getSubkeyId(PromoteKeyOperationTest.mStaticRing, 1);
        PromoteKeyResult result = op.execute(PromoteKeyringParcel.createPromoteKeyringParcel(PromoteKeyOperationTest.mStaticRing.getMasterKeyId(), aid, Arrays.asList(PromoteKeyOperationTest.mStaticRing.getPublicKey(keyId).getFingerprint())), null);
        Assert.assertTrue("promotion must succeed", result.success());
        {
            CanonicalizedSecretKeyRing ring = KeyWritableRepository.create(application).getCanonicalizedSecretKeyRing(PromoteKeyOperationTest.mStaticRing.getMasterKeyId());
            for (CanonicalizedSecretKey key : ring.secretKeyIterator()) {
                if ((key.getKeyId()) == keyId) {
                    Assert.assertEquals("subkey must be divert-to-card", DIVERT_TO_CARD, key.getSecretKeyTypeSuperExpensive());
                    Assert.assertArrayEquals("subkey must have correct iv", aid, key.getIv());
                } else {
                    Assert.assertEquals("some subkeys must be gnu dummy", GNU_DUMMY, key.getSecretKeyTypeSuperExpensive());
                }
            }
        }
    }
}

