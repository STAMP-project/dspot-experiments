/**
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 * Copyright (C) 2017 Christian Hagau <ach@hagau.se>
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


import AuthenticationData.Builder;
import EdDSAEngine.ONE_SHOT_MODE;
import HashAlgorithmTags.SHA256;
import HashAlgorithmTags.SHA512;
import RuntimeEnvironment.application;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import org.bouncycastle.jcajce.provider.asymmetric.eddsa.spec.EdDSANamedCurveTable;
import org.bouncycastle.jcajce.provider.asymmetric.eddsa.spec.EdDSAParameterSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyRepository;
import org.sufficientlysecure.keychain.pgp.CanonicalizedPublicKey;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;
import org.sufficientlysecure.keychain.ssh.AuthenticationData;
import org.sufficientlysecure.keychain.ssh.AuthenticationOperation;
import org.sufficientlysecure.keychain.ssh.AuthenticationParcel;
import org.sufficientlysecure.keychain.ssh.AuthenticationResult;
import org.sufficientlysecure.keychain.util.Passphrase;


@RunWith(KeychainTestRunner.class)
public class AuthenticationOperationTest {
    private static UncachedKeyRing mStaticRingRsa;

    private static UncachedKeyRing mStaticRingEcDsa;

    private static UncachedKeyRing mStaticRingEdDsa;

    private static UncachedKeyRing mStaticRingDsa;

    private static Passphrase mKeyPhrase;

    private static PrintStream oldShadowStream;

    @Test
    public void testAuthenticateRsa() throws Exception {
        byte[] challenge = "dies ist ein challenge ?".getBytes();
        byte[] signature;
        KeyRepository keyRepository = KeyRepository.create(application);
        long masterKeyId = AuthenticationOperationTest.mStaticRingRsa.getMasterKeyId();
        Long authSubKeyId = keyRepository.getEffectiveAuthenticationKeyId(masterKeyId);
        {
            // sign challenge
            AuthenticationOperation op = new AuthenticationOperation(RuntimeEnvironment.application, keyRepository);
            AuthenticationData.Builder authData = AuthenticationData.builder();
            authData.setAuthenticationMasterKeyId(masterKeyId);
            authData.setAuthenticationSubKeyId(authSubKeyId);
            authData.setHashAlgorithm(SHA512);
            AuthenticationParcel authenticationParcel = AuthenticationParcel.createAuthenticationParcel(authData.build(), challenge);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel();
            inputParcel = inputParcel.withPassphrase(AuthenticationOperationTest.mKeyPhrase, authSubKeyId);
            AuthenticationResult result = op.execute(authData.build(), inputParcel, authenticationParcel);
            Assert.assertTrue("authentication must succeed", result.success());
            signature = result.getSignature();
        }
        {
            // verify signature
            CanonicalizedPublicKey canonicalizedPublicKey = keyRepository.getCanonicalizedPublicKeyRing(masterKeyId).getPublicKey(authSubKeyId);
            PublicKey publicKey = canonicalizedPublicKey.getJcaPublicKey();
            Signature signatureVerifier = Signature.getInstance("SHA512withRSA");
            signatureVerifier.initVerify(publicKey);
            signatureVerifier.update(challenge);
            boolean isSignatureValid = signatureVerifier.verify(signature);
            Assert.assertTrue("signature must be valid", isSignatureValid);
        }
    }

    @Test
    public void testAuthenticateEcDsa() throws Exception {
        byte[] challenge = "dies ist ein challenge ?".getBytes();
        byte[] signature;
        KeyRepository keyRepository = KeyRepository.create(application);
        long masterKeyId = AuthenticationOperationTest.mStaticRingEcDsa.getMasterKeyId();
        Long authSubKeyId = keyRepository.getEffectiveAuthenticationKeyId(masterKeyId);
        {
            // sign challenge
            AuthenticationOperation op = new AuthenticationOperation(RuntimeEnvironment.application, keyRepository);
            AuthenticationData.Builder authData = AuthenticationData.builder();
            authData.setAuthenticationMasterKeyId(masterKeyId);
            authData.setAuthenticationSubKeyId(authSubKeyId);
            authData.setHashAlgorithm(SHA512);
            AuthenticationParcel authenticationParcel = AuthenticationParcel.createAuthenticationParcel(authData.build(), challenge);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel();
            inputParcel = inputParcel.withPassphrase(AuthenticationOperationTest.mKeyPhrase, authSubKeyId);
            AuthenticationResult result = op.execute(authData.build(), inputParcel, authenticationParcel);
            Assert.assertTrue("authentication must succeed", result.success());
            signature = result.getSignature();
        }
        {
            // verify signature
            CanonicalizedPublicKey canonicalizedPublicKey = keyRepository.getCanonicalizedPublicKeyRing(masterKeyId).getPublicKey(authSubKeyId);
            PublicKey publicKey = canonicalizedPublicKey.getJcaPublicKey();
            Signature signatureVerifier = Signature.getInstance("SHA512withECDSA");
            signatureVerifier.initVerify(publicKey);
            signatureVerifier.update(challenge);
            boolean isSignatureValid = signatureVerifier.verify(signature);
            Assert.assertTrue("signature must be valid", isSignatureValid);
        }
    }

    @Test
    public void testAuthenticateEdDsa() throws Exception {
        byte[] challenge = "dies ist ein challenge ?".getBytes();
        byte[] signature;
        KeyRepository keyRepository = KeyRepository.create(application);
        long masterKeyId = AuthenticationOperationTest.mStaticRingEdDsa.getMasterKeyId();
        Long authSubKeyId = keyRepository.getEffectiveAuthenticationKeyId(masterKeyId);
        {
            // sign challenge
            AuthenticationOperation op = new AuthenticationOperation(RuntimeEnvironment.application, keyRepository);
            AuthenticationData.Builder authData = AuthenticationData.builder();
            authData.setAuthenticationMasterKeyId(masterKeyId);
            authData.setAuthenticationSubKeyId(authSubKeyId);
            authData.setHashAlgorithm(SHA512);
            AuthenticationParcel authenticationParcel = AuthenticationParcel.createAuthenticationParcel(authData.build(), challenge);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel();
            inputParcel = inputParcel.withPassphrase(AuthenticationOperationTest.mKeyPhrase, authSubKeyId);
            AuthenticationResult result = op.execute(authData.build(), inputParcel, authenticationParcel);
            Assert.assertTrue("authentication must succeed", result.success());
            signature = result.getSignature();
        }
        {
            // verify signature
            CanonicalizedPublicKey canonicalizedPublicKey = keyRepository.getCanonicalizedPublicKeyRing(masterKeyId).getPublicKey(authSubKeyId);
            PublicKey publicKey = canonicalizedPublicKey.getJcaPublicKey();
            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("Ed25519");
            Signature signatureVerifier = new org.bouncycastle.jcajce.provider.asymmetric.eddsa.EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
            signatureVerifier.setParameter(ONE_SHOT_MODE);
            signatureVerifier.initVerify(publicKey);
            signatureVerifier.update(challenge);
            boolean isSignatureValid = signatureVerifier.verify(signature);
            Assert.assertTrue("signature must be valid", isSignatureValid);
        }
    }

    @Test
    public void testAuthenticateDsa() throws Exception {
        byte[] challenge = "dies ist ein challenge ?".getBytes();
        byte[] signature;
        KeyRepository keyRepository = KeyRepository.create(application);
        long masterKeyId = AuthenticationOperationTest.mStaticRingDsa.getMasterKeyId();
        Long authSubKeyId = keyRepository.getEffectiveAuthenticationKeyId(masterKeyId);
        {
            // sign challenge
            AuthenticationOperation op = new AuthenticationOperation(RuntimeEnvironment.application, keyRepository);
            AuthenticationData.Builder authData = AuthenticationData.builder();
            authData.setAuthenticationMasterKeyId(masterKeyId);
            authData.setAuthenticationSubKeyId(authSubKeyId);
            authData.setHashAlgorithm(SHA256);
            AuthenticationParcel authenticationParcel = AuthenticationParcel.createAuthenticationParcel(authData.build(), challenge);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel();
            inputParcel = inputParcel.withPassphrase(AuthenticationOperationTest.mKeyPhrase, authSubKeyId);
            AuthenticationResult result = op.execute(authData.build(), inputParcel, authenticationParcel);
            Assert.assertTrue("authentication must succeed", result.success());
            signature = result.getSignature();
        }
        {
            // verify signature
            CanonicalizedPublicKey canonicalizedPublicKey = keyRepository.getCanonicalizedPublicKeyRing(masterKeyId).getPublicKey(authSubKeyId);
            PublicKey publicKey = canonicalizedPublicKey.getJcaPublicKey();
            Signature signatureVerifier = Signature.getInstance("SHA256withDSA");
            signatureVerifier.initVerify(publicKey);
            signatureVerifier.update(challenge);
            boolean isSignatureValid = signatureVerifier.verify(signature);
            Assert.assertTrue("signature must be valid", isSignatureValid);
        }
    }

    @Test
    public void testAccessControl() throws Exception {
        byte[] challenge = "dies ist ein challenge ?".getBytes();
        KeyRepository keyRepository = KeyRepository.create(application);
        long masterKeyId = AuthenticationOperationTest.mStaticRingEcDsa.getMasterKeyId();
        Long authSubKeyId = keyRepository.getEffectiveAuthenticationKeyId(masterKeyId);
        {
            // sign challenge - should succeed with selected key allowed
            AuthenticationOperation op = new AuthenticationOperation(RuntimeEnvironment.application, keyRepository);
            AuthenticationData.Builder authData = AuthenticationData.builder();
            authData.setAuthenticationMasterKeyId(masterKeyId);
            authData.setAuthenticationSubKeyId(authSubKeyId);
            authData.setHashAlgorithm(SHA512);
            ArrayList<Long> allowedKeyIds = new ArrayList<>(1);
            allowedKeyIds.add(AuthenticationOperationTest.mStaticRingEcDsa.getMasterKeyId());
            authData.setAllowedAuthenticationKeyIds(allowedKeyIds);
            AuthenticationParcel authenticationParcel = AuthenticationParcel.createAuthenticationParcel(authData.build(), challenge);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel();
            inputParcel = inputParcel.withPassphrase(AuthenticationOperationTest.mKeyPhrase, authSubKeyId);
            AuthenticationResult result = op.execute(authData.build(), inputParcel, authenticationParcel);
            Assert.assertTrue("authentication must succeed with selected key allowed", result.success());
        }
        {
            // sign challenge - should fail with selected key disallowed
            AuthenticationOperation op = new AuthenticationOperation(RuntimeEnvironment.application, keyRepository);
            AuthenticationData.Builder authData = AuthenticationData.builder();
            authData.setAuthenticationMasterKeyId(masterKeyId);
            authData.setAuthenticationSubKeyId(authSubKeyId);
            authData.setHashAlgorithm(SHA512);
            ArrayList<Long> allowedKeyIds = new ArrayList<>(1);
            authData.setAllowedAuthenticationKeyIds(allowedKeyIds);
            AuthenticationParcel authenticationParcel = AuthenticationParcel.createAuthenticationParcel(authData.build(), challenge);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel();
            inputParcel = inputParcel.withPassphrase(AuthenticationOperationTest.mKeyPhrase, authSubKeyId);
            AuthenticationResult result = op.execute(authData.build(), inputParcel, authenticationParcel);
            Assert.assertFalse("authentication must fail with selected key disallowed", result.success());
        }
    }
}

