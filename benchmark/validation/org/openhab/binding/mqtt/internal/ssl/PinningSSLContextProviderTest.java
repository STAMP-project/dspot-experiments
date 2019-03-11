/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mqtt.internal.ssl;


import PinType.CERTIFICATE_TYPE;
import PinType.PUBLIC_KEY_TYPE;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.util.HexUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests cases for {@link PinningSSLContextProvider}.
 *
 * @author David Graeff - Initial contribution
 */
public class PinningSSLContextProviderTest {
    @Test
    public void getDigestDataFor() throws FileNotFoundException, NoSuchAlgorithmException, CertificateException {
        // Load test certificate
        InputStream inputCert = getClass().getResourceAsStream("cert.pem");
        X509Certificate certificate = ((X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(inputCert)));
        PinTrustManager pinTrustManager = new PinTrustManager();
        PinMessageDigest pinMessageDigest = pinTrustManager.getMessageDigestForSigAlg(certificate.getSigAlgName());
        String hashForCert = HexUtils.bytesToHex(pinMessageDigest.digest(pinTrustManager.getEncoded(CERTIFICATE_TYPE, certificate)));
        String expectedHash = "41fa6d40d1e8f53ac81a395ac13b1efa10917718f1ebe3ac278925716d630b72".toUpperCase();
        Assert.assertThat(hashForCert, CoreMatchers.is(expectedHash));
        String hashForPublicKey = HexUtils.bytesToHex(pinMessageDigest.digest(pinTrustManager.getEncoded(PUBLIC_KEY_TYPE, certificate)));
        String expectedPubKeyHash = "9a6f30e67ae9723579da2575c35daf7da3b370b04ac0bde031f5e1f5e4617eb8".toUpperCase();
        Assert.assertThat(hashForPublicKey, CoreMatchers.is(expectedPubKeyHash));
    }

    // Test if X509Certificate.getEncoded() is called if it is a certificate pin and
    // X509Certificate.getPublicKey().getEncoded() is called if it is a public key pinning.
    @Test
    public void certPinCallsX509CertificateGetEncoded() throws NoSuchAlgorithmException, CertificateException {
        PinTrustManager pinTrustManager = new PinTrustManager();
        pinTrustManager.addPinning(Pin.LearningPin(CERTIFICATE_TYPE));
        // Mock a certificate
        X509Certificate certificate = Mockito.mock(X509Certificate.class);
        Mockito.when(certificate.getEncoded()).thenReturn(new byte[0]);
        Mockito.when(certificate.getSigAlgName()).thenReturn("SHA256withRSA");
        pinTrustManager.checkServerTrusted(new X509Certificate[]{ certificate }, null);
        Mockito.verify(certificate).getEncoded();
    }

    // Test if X509Certificate.getEncoded() is called if it is a certificate pin and
    // X509Certificate.getPublicKey().getEncoded() is called if it is a public key pinning.
    @Test
    public void pubKeyPinCallsX509CertificateGetPublicKey() throws NoSuchAlgorithmException, CertificateException {
        PinTrustManager pinTrustManager = new PinTrustManager();
        pinTrustManager.addPinning(Pin.LearningPin(PUBLIC_KEY_TYPE));
        // Mock a certificate
        PublicKey publicKey = Mockito.mock(PublicKey.class);
        Mockito.when(publicKey.getEncoded()).thenReturn(new byte[0]);
        X509Certificate certificate = Mockito.mock(X509Certificate.class);
        Mockito.when(certificate.getSigAlgName()).thenReturn("SHA256withRSA");
        Mockito.when(certificate.getPublicKey()).thenReturn(publicKey);
        pinTrustManager.checkServerTrusted(new X509Certificate[]{ certificate }, null);
        Mockito.verify(publicKey).getEncoded();
    }

    /**
     * Overwrite {@link #getMessageDigestForSigAlg(String)} method and return a pre-defined {@link PinMessageDigest}.
     */
    public static class PinTrustManagerEx extends PinTrustManager {
        private final PinMessageDigest pinMessageDigest;

        PinTrustManagerEx(PinMessageDigest pinMessageDigest) {
            this.pinMessageDigest = pinMessageDigest;
        }

        @Override
        @NonNull
        PinMessageDigest getMessageDigestForSigAlg(@NonNull
        String sigAlg) throws CertificateException {
            return pinMessageDigest;
        }
    }

    @Test
    public void learningMode() throws NoSuchAlgorithmException, CertificateException {
        PinMessageDigest pinMessageDigest = new PinMessageDigest("SHA-256");
        PinTrustManager pinTrustManager = new PinningSSLContextProviderTest.PinTrustManagerEx(pinMessageDigest);
        byte[] testCert = new byte[]{ 1, 2, 3 };
        byte[] digestOfTestCert = pinMessageDigest.digest(testCert);
        // Add a certificate pin in learning mode to a trust manager
        Pin pin = Pin.LearningPin(CERTIFICATE_TYPE);
        pinTrustManager.addPinning(pin);
        Assert.assertThat(pinTrustManager.pins.size(), CoreMatchers.is(1));
        // Mock a callback
        PinnedCallback callback = Mockito.mock(PinnedCallback.class);
        pinTrustManager.setCallback(callback);
        // Mock a certificate
        X509Certificate certificate = Mockito.mock(X509Certificate.class);
        Mockito.when(certificate.getEncoded()).thenReturn(testCert);
        Mockito.when(certificate.getSigAlgName()).thenReturn("SHA256withRSA");
        // Perform an SSL certificate check
        pinTrustManager.checkServerTrusted(new X509Certificate[]{ certificate }, null);
        // After a first connect learning mode should turn into check mode. It should have learned the hash data and
        // message digest, returned by PinTrustManager.getMessageDigestForSigAlg().
        Assert.assertThat(pin.learning, CoreMatchers.is(false));
        Assert.assertThat(pin.pinData, CoreMatchers.is(digestOfTestCert));
        Assert.assertThat(pin.hashDigest, CoreMatchers.is(pinMessageDigest));
        // We expect callbacks
        Mockito.verify(callback).pinnedLearnedHash(ArgumentMatchers.eq(pin));
        Mockito.verify(callback).pinnedConnectionAccepted();
    }

    @Test
    public void checkMode() throws NoSuchAlgorithmException, CertificateException {
        PinTrustManager pinTrustManager = new PinTrustManager();
        PinMessageDigest pinMessageDigest = new PinMessageDigest("SHA-256");
        byte[] testCert = new byte[]{ 1, 2, 3 };
        byte[] digestOfTestCert = pinMessageDigest.digest(testCert);
        // Add a certificate pin in checking mode to a trust manager
        Pin pin = Pin.CheckingPin(CERTIFICATE_TYPE, pinMessageDigest, digestOfTestCert);
        pinTrustManager.addPinning(pin);
        Assert.assertThat(pinTrustManager.pins.size(), CoreMatchers.is(1));
        // Mock a callback
        PinnedCallback callback = Mockito.mock(PinnedCallback.class);
        pinTrustManager.setCallback(callback);
        // Mock a certificate
        X509Certificate certificate = Mockito.mock(X509Certificate.class);
        Mockito.when(certificate.getEncoded()).thenReturn(testCert);
        Mockito.when(certificate.getSigAlgName()).thenReturn("SHA256withRSA");
        // Perform an SSL certificate check
        pinTrustManager.checkServerTrusted(new X509Certificate[]{ certificate }, null);
        // After a first connect learning mode should turn into check mode
        Assert.assertThat(pin.learning, CoreMatchers.is(false));
        Assert.assertThat(pin.pinData, CoreMatchers.is(digestOfTestCert));
        Assert.assertThat(pin.hashDigest, CoreMatchers.is(pinMessageDigest));
        // We expect callbacks
        Mockito.verify(callback, Mockito.times(0)).pinnedLearnedHash(ArgumentMatchers.eq(pin));
        Mockito.verify(callback).pinnedConnectionAccepted();
    }
}

