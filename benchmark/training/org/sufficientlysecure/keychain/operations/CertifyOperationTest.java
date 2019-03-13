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


import CertifyActionsParcel.Builder;
import LogType.MSG_CRT_ERROR_MASTER_NOT_FOUND;
import LogType.MSG_CRT_ERROR_SELF;
import LogType.MSG_CRT_WARN_NOT_FOUND;
import RuntimeEnvironment.application;
import VerificationStatus.VERIFIED_SECRET;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.operations.results.CertifyResult;
import org.sufficientlysecure.keychain.pgp.CanonicalizedPublicKeyRing;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.service.CertifyActionsParcel;
import org.sufficientlysecure.keychain.service.CertifyActionsParcel.CertifyAction;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;
import org.sufficientlysecure.keychain.util.Passphrase;
import org.sufficientlysecure.keychain.util.TestingUtils;


@RunWith(KeychainTestRunner.class)
public class CertifyOperationTest {
    static UncachedKeyRing mStaticRing1;

    static UncachedKeyRing mStaticRing2;

    static Passphrase mKeyPhrase1 = TestingUtils.testPassphrase1;

    static Passphrase mKeyPhrase2 = TestingUtils.testPassphrase2;

    static PrintStream oldShadowStream;

    @Test
    public void testSelfCertifyFlag() throws Exception {
        CanonicalizedPublicKeyRing ring = KeyWritableRepository.create(application).getCanonicalizedPublicKeyRing(CertifyOperationTest.mStaticRing1.getMasterKeyId());
        // TODO this should be more correctly be VERIFIED_SELF at some point!
        Assert.assertEquals("secret key must be marked self-certified in database", VERIFIED_SECRET, ring.getVerified());
    }

    @Test
    public void testCertifyId() throws Exception {
        CertifyOperation op = new CertifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null, null);
        {
            CanonicalizedPublicKeyRing ring = KeyWritableRepository.create(application).getCanonicalizedPublicKeyRing(CertifyOperationTest.mStaticRing2.getMasterKeyId());
            Assert.assertNull("public key must not be marked verified prior to certification", ring.getVerified());
        }
        CertifyActionsParcel.Builder actions = CertifyActionsParcel.builder(CertifyOperationTest.mStaticRing1.getMasterKeyId());
        actions.addAction(CertifyAction.createForUserIds(CertifyOperationTest.mStaticRing2.getMasterKeyId(), CertifyOperationTest.mStaticRing2.getPublicKey().getUnorderedUserIds()));
        CertifyResult result = op.execute(actions.build(), CryptoInputParcel.createCryptoInputParcel(new Date(), CertifyOperationTest.mKeyPhrase1));
        Assert.assertTrue("certification must succeed", result.success());
        {
            CanonicalizedPublicKeyRing ring = KeyWritableRepository.create(application).getCanonicalizedPublicKeyRing(CertifyOperationTest.mStaticRing2.getMasterKeyId());
            Assert.assertEquals("new key must be verified now", VERIFIED_SECRET, ring.getVerified());
        }
    }

    @Test
    public void testCertifyAttribute() throws Exception {
        KeyWritableRepository keyWritableRepository = KeyWritableRepository.create(application);
        CertifyOperation op = new CertifyOperation(RuntimeEnvironment.application, keyWritableRepository, null, null);
        {
            CanonicalizedPublicKeyRing ring = keyWritableRepository.getCanonicalizedPublicKeyRing(CertifyOperationTest.mStaticRing2.getMasterKeyId());
            Assert.assertNull("public key must not be marked verified prior to certification", ring.getVerified());
        }
        CertifyActionsParcel.Builder actions = CertifyActionsParcel.builder(CertifyOperationTest.mStaticRing1.getMasterKeyId());
        actions.addAction(CertifyAction.createForUserAttributes(CertifyOperationTest.mStaticRing2.getMasterKeyId(), CertifyOperationTest.mStaticRing2.getPublicKey().getUnorderedUserAttributes()));
        CertifyResult result = op.execute(actions.build(), CryptoInputParcel.createCryptoInputParcel(new Date(), CertifyOperationTest.mKeyPhrase1));
        Assert.assertTrue("certification must succeed", result.success());
        {
            CanonicalizedPublicKeyRing ring = keyWritableRepository.getCanonicalizedPublicKeyRing(CertifyOperationTest.mStaticRing2.getMasterKeyId());
            Assert.assertEquals("new key must be verified now", VERIFIED_SECRET, ring.getVerified());
        }
    }

    @Test
    public void testCertifySelf() throws Exception {
        CertifyOperation op = new CertifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null, null);
        CertifyActionsParcel.Builder actions = CertifyActionsParcel.builder(CertifyOperationTest.mStaticRing1.getMasterKeyId());
        actions.addAction(CertifyAction.createForUserIds(CertifyOperationTest.mStaticRing1.getMasterKeyId(), CertifyOperationTest.mStaticRing2.getPublicKey().getUnorderedUserIds()));
        CertifyResult result = op.execute(actions.build(), CryptoInputParcel.createCryptoInputParcel(new Date(), CertifyOperationTest.mKeyPhrase1));
        Assert.assertFalse("certification with itself must fail!", result.success());
        Assert.assertTrue("error msg must be about self certification", result.getLog().containsType(MSG_CRT_ERROR_SELF));
    }

    @Test
    public void testCertifyNonexistent() throws Exception {
        CertifyOperation op = new CertifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null, null);
        {
            CertifyActionsParcel.Builder actions = CertifyActionsParcel.builder(CertifyOperationTest.mStaticRing1.getMasterKeyId());
            ArrayList<String> uids = new ArrayList<String>();
            uids.add("nonexistent");
            actions.addAction(CertifyAction.createForUserIds(1234L, uids));
            CertifyResult result = op.execute(actions.build(), CryptoInputParcel.createCryptoInputParcel(new Date(), CertifyOperationTest.mKeyPhrase1));
            Assert.assertFalse("certification of nonexistent key must fail", result.success());
            Assert.assertTrue("must contain error msg about not found", result.getLog().containsType(MSG_CRT_WARN_NOT_FOUND));
        }
        {
            CertifyActionsParcel.Builder actions = CertifyActionsParcel.builder(1234L);
            actions.addAction(CertifyAction.createForUserIds(CertifyOperationTest.mStaticRing1.getMasterKeyId(), CertifyOperationTest.mStaticRing2.getPublicKey().getUnorderedUserIds()));
            CertifyResult result = op.execute(actions.build(), CryptoInputParcel.createCryptoInputParcel(new Date(), CertifyOperationTest.mKeyPhrase1));
            Assert.assertFalse("certification of nonexistent key must fail", result.success());
            Assert.assertTrue("must contain error msg about not found", result.getLog().containsType(MSG_CRT_ERROR_MASTER_NOT_FOUND));
        }
    }
}

